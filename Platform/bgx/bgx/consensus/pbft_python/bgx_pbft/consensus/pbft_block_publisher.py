# Copyright 2018 NTRlab
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

from base64 import b64encode
import logging
import hashlib
import time
import json
import random
import base64
try:
    import sawtooth_sdk.protobuf.transaction_pb2 as txn_pb
except TypeError:
    import sawtooth_validator.protobuf.transaction_pb2 as txn_pb

from bgx_pbft.journal.block_wrapper import BlockWrapper
from bgx_pbft.journal.consensus.consensus import BlockPublisherInterface
from bgx_pbft.state.settings_view import SettingsView
from bgx_pbft.consensus import pbft_enclave_factory as factory
from bgx_pbft.consensus.consensus_state import ConsensusState
from bgx_pbft.consensus.consensus_state_store import ConsensusStateStore
from bgx_pbft.consensus.pbft_settings_view import PbftSettingsView
from bgx_pbft.consensus.signup_info import SignupInfo
from bgx_pbft.consensus.pbft_key_state_store import PbftKeyState
from bgx_pbft.consensus.pbft_key_state_store import PbftKeyStateStore

#import bgx_pbft.enclave.pbft_enclave as PBFT_ENCLAVE_MODULE
#from bgx_pbft.consensus.wait_timer import WaitTimer
#from bgx_pbft.consensus.wait_certificate import WaitCertificate
from bgx_pbft.consensus import utils
from bgx_pbft.enclave.utils import json2dict
from bgx_pbft.enclave.utils import dict2json

import bgx_pbft_common.protobuf.bgx_validator_registry_pb2 as vr_pb
from bgx_pbft_common.validator_registry_view.validator_registry_view import ValidatorRegistryView

from  sawtooth_settings.protobuf.settings_pb2 import SettingProposal
from  sawtooth_settings.protobuf.settings_pb2 import SettingsPayload

LOGGER = logging.getLogger(__name__)

SETTINGS_NAMESPACE = '000000'

_MIN_PRINT_WIDTH = 15
_MAX_KEY_PARTS = 4
_ADDRESS_PART_SIZE = 16

def _short_hash(in_str):
    return hashlib.sha256(in_str.encode()).hexdigest()[:_ADDRESS_PART_SIZE]

def _key_to_address(key):
    """Creates the state address for a given setting key.
    """
    key_parts = key.split('.', maxsplit=_MAX_KEY_PARTS - 1)
    key_parts.extend([''] * (_MAX_KEY_PARTS - len(key_parts)))

    return SETTINGS_NAMESPACE + ''.join(_short_hash(x) for x in key_parts)

def _config_inputs(key):
    """Creates the list of inputs for a sawtooth_settings transaction, for a
    given setting key.
    """
    return [
        _key_to_address('sawtooth.settings.vote.proposals'),
        _key_to_address('sawtooth.settings.vote.authorized_keys'),
        _key_to_address('sawtooth.settings.vote.approval_threshold'),
        _key_to_address(key)
    ]


def _config_outputs(key):
    """Creates the list of outputs for a sawtooth_settings transaction, for a
    given setting key.
    """
    return [
        _key_to_address('sawtooth.settings.vote.proposals'),
        _key_to_address(key)
    ]


class PbftBlockPublisher(BlockPublisherInterface):
    """Consensus objects provide the following services to the Journal:
    1) Build candidate blocks ( this temporary until the block types are
    combined into a single block type)
    2) Check if it is time to claim the current candidate blocks.
    3) Provide the data a signatures required for a block to be validated by
    other consensus algorithms
    """

    _previous_block_id = None

    _validator_registry_namespace = hashlib.sha256('validator_registry'.encode()).hexdigest()[0:6]
    _validator_map_address = _validator_registry_namespace + hashlib.sha256('validator_map'.encode()).hexdigest()

    def __init__(self,
                 block_cache,
                 state_view_factory,
                 batch_publisher,
                 data_dir,
                 config_dir,
                 validator_id,
                 node):
        """Initialize the object, is passed (read-only) state access objects.
            Args:
                block_cache (BlockCache): Dict interface to the block cache.
                    Any predecessor block to blocks handed to this object will
                    be present in this dict.
                state_view_factory (StateViewFactory): A factory that can be
                    used to create read-only views of state for a particular
                    merkle root, in particular the state as it existed when a
                    particular block was the chain head.
                batch_publisher (BatchPublisher): An interface implementing
                    send(txn_list) which wrap the transactions in a batch and
                    broadcast that batch to the network.
                data_dir (str): path to location where persistent data for the
                    consensus module can be stored.
                config_dir (str): path to location where configuration for the
                    consensus module can be found.
                validator_id (str): A unique ID for this validator
            Returns:
                none.
        """
        super().__init__(
            block_cache,
            state_view_factory,
            batch_publisher,
            data_dir,
            config_dir,
            validator_id)

        self._block_cache = block_cache
        self._state_view_factory = state_view_factory
        self._batch_publisher = batch_publisher
        self._data_dir = data_dir
        self._config_dir = config_dir
        self._validator_id = validator_id
        self._node = node
        LOGGER.debug('PbftBlockPublisher:: ConsensusStateStore')
        self._consensus_state_store = ConsensusStateStore(data_dir=self._data_dir,validator_id=self._validator_id)
        self._pbft_key_state_store = PbftKeyStateStore(data_dir=self._data_dir,validator_id=self._validator_id)
        self._wait_timer = None

    def _create_proposal(self, block_header, pbft_enclave_module):
        """
        proposal request
        """
        public_key_hash = hashlib.sha256(block_header.signer_public_key.encode()).hexdigest()
        nonce = SignupInfo.block_id_to_nonce(block_header.previous_block_id)

        setting = 'sawtooth.consensus.pbft.max_log_size'
        if False:
            # try to set pbft params
            
            proposal = SettingProposal(
                 setting=setting,
                 value='1003',
                 nonce=nonce)
            payload = SettingsPayload(data=proposal.SerializeToString(),action=SettingsPayload.PROPOSE)
            serialized = payload.SerializeToString()
            input_addresses = _config_inputs(setting) 
            output_addresses = _config_outputs(setting)

            header = txn_pb.TransactionHeader(
                signer_public_key=block_header.signer_public_key,
                family_name='sawtooth_settings',
                family_version='1.0',
                inputs=input_addresses,
                outputs=output_addresses,
                dependencies=[],
                payload_sha512=hashlib.sha512(serialized).hexdigest(),
                batcher_public_key=block_header.signer_public_key,
                nonce=hex(random.randint(0, 2**64))).SerializeToString()

            signature = self._batch_publisher.identity_signer.sign(header)

            transaction = txn_pb.Transaction(
                    header=header,
                    payload=serialized,
                    header_signature=signature)

            LOGGER.info('payload action=%s nonce=%s',
                payload.action,
                nonce)

            self._batch_publisher.send([transaction])
        else:
            # get setting
            pass


    def _register_signup_information(self, block_header, pbft_enclave_module=None):
        # Create signup information for this validator, putting the block ID
        # of the block previous to the block referenced by block_header in the
        # nonce.  Block ID is better than wait certificate ID for testing
        # freshness as we need to account for non-BGT blocks.
        LOGGER.debug('_register_signup_information: TRY to REGISTER')
        public_key_hash = hashlib.sha256(block_header.signer_public_key.encode()).hexdigest()
        nonce = SignupInfo.block_id_to_nonce(block_header.previous_block_id)
        pbft_public_key = self._validator_id
        anti_sybil_id = hashlib.sha256(pbft_public_key.encode()).hexdigest()
        signup_data = {
                'pbft_public_key': pbft_public_key,
            }
        sealed_signup_data = base64.b64encode(dict2json(signup_data).encode()).decode('utf-8')
        """
        signup_info = SignupInfo.create_signup_info(
                pbft_enclave_module=pbft_enclave_module,
                originator_public_key_hash=public_key_hash,
                nonce=nonce)
        """
        # Create the validator registry payload
        payload = vr_pb.BgxValidatorRegistryPayload(
                verb='register',
                name='validator-{}'.format(block_header.signer_public_key[:8]),
                id=block_header.signer_public_key,
                node = self._node,
                signup_info=vr_pb.BgxSignUpInfo(
                    pbft_public_key=pbft_public_key, # signup_info.pbft_public_key,
                    anti_sybil_id= anti_sybil_id, # signup_info.anti_sybil_id,
                    nonce=nonce),
            )
        serialized = payload.SerializeToString()

        # Create the address that will be used to look up this validator
        # registry transaction.  Seems like a potential for refactoring..
        validator_entry_address = PbftBlockPublisher._validator_registry_namespace + hashlib.sha256(block_header.signer_public_key.encode()).hexdigest()

        # Create a transaction header and transaction for the validator
        # registry update amd then hand it off to the batch publisher to
        # send out.
        output_addresses = [validator_entry_address,PbftBlockPublisher._validator_map_address]
        input_addresses = output_addresses + \
            [SettingsView.setting_address('sawtooth.bgt.report_public_key_pem'),
             SettingsView.setting_address('sawtooth.bgt.valid_enclave_measurements'),
             SettingsView.setting_address('sawtooth.bgt.valid_enclave_basenames')
            ]

        header = txn_pb.TransactionHeader(
                signer_public_key=block_header.signer_public_key,
                family_name='bgx_validator_registry',
                family_version='1.0',
                inputs=input_addresses,
                outputs=output_addresses,
                dependencies=[],
                payload_sha512=hashlib.sha512(serialized).hexdigest(),
                batcher_public_key=block_header.signer_public_key,
                nonce=hex(random.randint(0, 2**64))).SerializeToString()

        signature = self._batch_publisher.identity_signer.sign(header)

        transaction = txn_pb.Transaction(
                header=header,
                payload=serialized,
                header_signature=signature)

        LOGGER.info(
            'Register Validator Name=%s, ID=%s...%s,Nonce=%s',
            payload.name,
            payload.id[:8],
            payload.id[-8:],
            nonce)

        self._batch_publisher.send([transaction])

        # Store the key state so that we can look it up later if need be and
        # set the new key as our active key
        self._pbft_key_state_store[pbft_public_key] = PbftKeyState(
                sealed_signup_data=sealed_signup_data,
                has_been_refreshed=False,
                signup_nonce=nonce)
        self._pbft_key_state_store.active_key = pbft_public_key
        LOGGER.debug('_register_signup_information: REGISTER DONE')

    def _handle_registration_timeout(self, block_header, pbft_enclave_module,
                                     state_view, signup_nonce,
                                     pbft_public_key):
        # See if a registration attempt has timed out. Assumes the caller has
        # checked for a committed registration and did not find it.
        # If it has timed out then this method will re-register.
        LOGGER.debug("_handle_registration_timeout:ADD CONSENSUS_STATE for block_id=%s",block_header.previous_block_id)
        consensus_state = ConsensusState.consensus_state_for_block_id(
                block_id=block_header.previous_block_id,
                block_cache=self._block_cache,
                state_view_factory=self._state_view_factory,
                consensus_state_store=self._consensus_state_store,
                pbft_enclave_module=None #pbft_enclave_module
            )
        """
        # for getting PBFT settings from chain
        pbft_settings_view = PbftSettingsView(state_view)

        if consensus_state.signup_attempt_timed_out(signup_nonce, pbft_settings_view, self._block_cache):
            LOGGER.error('My pbft registration using PPK %s has not committed by block %s. Create new registration',
                         pbft_public_key,
                         block_header.previous_block_id)

            del self._pbft_key_state_store[pbft_public_key]
            self._register_signup_information(
                block_header=block_header,
                pbft_enclave_module=pbft_enclave_module
                )
        """

    def initialize_block(self, block_header):
        """Do initialization necessary for the consensus to claim a block,
        this may include initiating voting activities, starting proof of work
        hash generation, or create a PBFT wait timer.

        Args:
            block_header (BlockHeader): The BlockHeader to initialize.
        Returns:
            Boolean: True if the candidate block should be built. False if
            no candidate should be built.
        """
        # If the previous block ID matches our cached one, that means that we
        # have already determined that even if we initialize the requested
        # block we would not be able to claim it.  So, instead of wasting time
        # doing all of the checking again, simply short-circuit the failure so
        # that the validator can go do something more useful.
        if block_header.previous_block_id == PbftBlockPublisher._previous_block_id:
            LOGGER.debug("block_header.previous_block_id == PbftBlockPublisher._previous_block_id TRUE")
            return False
        PbftBlockPublisher._previous_block_id = block_header.previous_block_id
        # Using the current chain head, we need to create a state view so we
        # can create a PBFT enclave.
        state_view = BlockWrapper.state_view_for_block(
                block_wrapper=self._block_cache.block_store.chain_head,
                state_view_factory=self._state_view_factory)
        
        pbft_enclave_module = factory.PbftEnclaveFactory.get_pbft_enclave_module(state_view=state_view,config_dir=self._config_dir,data_dir=self._data_dir)
        LOGGER.debug("pbft_enclave_module=%s previous_block_id=%s",pbft_enclave_module,type(block_header.previous_block_id))
        # Get our validator registry entry to see what PBFT public key
        # other validators think we are using.
        validator_registry_view = ValidatorRegistryView(state_view)
        validator_info = None

        try:
            validator_id = block_header.signer_public_key
            validator_info = validator_registry_view.get_validator_info(validator_id=validator_id)
        except KeyError:
            pass

        # If we don't have a validator registry entry, then check the active
        # key.  If we don't have one, then we need to sign up.  If we do have
        # one, then our validator registry entry has not percolated through the
        # system, so nothing to to but wait.
        active_pbft_public_key = self._pbft_key_state_store.active_key
        if validator_info is None:
            if active_pbft_public_key is None:
                LOGGER.debug('No public key found, so going to register new signup information')
                self._register_signup_information(block_header=block_header)

            else:  # Check if we need to give up on this registration attempt
                try:
                    nonce = self._pbft_key_state_store[active_pbft_public_key].signup_nonce
                except (ValueError, AttributeError):
                    self._pbft_key_state_store.active_key = None
                    LOGGER.warning('Pbft Key State Store had inaccessible or '
                                   'corrupt active key [%s] clearing '
                                   'key.', active_pbft_public_key)
                    return False
                LOGGER.debug('Check if we need to give up on this registration attempt')
                self._handle_registration_timeout(
                    block_header=block_header,
                    pbft_enclave_module=pbft_enclave_module,
                    state_view=state_view,
                    signup_nonce=nonce,
                    pbft_public_key=active_pbft_public_key
                )
            LOGGER.debug("validator_info NONE")
            return True #False

        # Retrieve the key state corresponding to the PBFT public key in our
        # validator registry entry.
        pbft_key_state = None
        try:
            pbft_key_state = self._pbft_key_state_store[
                    validator_info.signup_info.pbft_public_key]
        except (ValueError, KeyError):
            pass

        # If there is no key state associated with the PBFT public key that
        # other validators think we should be using, then we need to create
        # new signup information as we have no way whatsoever to publish
        # blocks that other validators will accept.
        LOGGER.debug("check pbft_key_state=%s",pbft_key_state)
        if pbft_key_state is None:
            LOGGER.debug(
                'PBFT public key %s...%s in validator registry not found in key state store.  Sign up again',
                validator_info.signup_info.pbft_public_key[:8],
                validator_info.signup_info.pbft_public_key[-8:])
            self._register_signup_information(block_header=block_header)

            # We need to put fake information in the key state store for the
            # PBFT public key the other validators think we are using so that
            # we don't try to keep signing up.  However, we are going to mark
            # that key state store entry as being refreshed so that we will
            # never actually try to use it.
            dummy_data = b64encode(b'No sealed signup data').decode('utf-8')
            self._pbft_key_state_store[validator_info.signup_info.pbft_public_key] = PbftKeyState(
                    sealed_signup_data=dummy_data,
                    has_been_refreshed=True,
                    signup_nonce='unknown')

            return False

        # Check the key state.  If it is marked as being refreshed, then we are
        # waiting until our PBFT public key is updated in the validator
        # registry and therefore we cannot publish any blocks.
        if pbft_key_state.has_been_refreshed:
            LOGGER.debug(
                'PBFT public key %s...%s has been refreshed.  Wait for new '
                'key to show up in validator registry.',
                validator_info.signup_info.pbft_public_key[:8],
                validator_info.signup_info.pbft_public_key[-8:])

            # Check if we need to give up on this registration attempt
            self._handle_registration_timeout(
                block_header=block_header,
                pbft_enclave_module=pbft_enclave_module,
                state_view=state_view,
                signup_nonce=pbft_key_state.signup_nonce,
                pbft_public_key=active_pbft_public_key
            )
            return False

        # If the PBFT public key in the validator registry is not the active
        # one, then we need to switch the active key in the key state store.
        if validator_info.signup_info.pbft_public_key != active_pbft_public_key:
            active_pbft_public_key = validator_info.signup_info.pbft_public_key
            self._pbft_key_state_store.active_key = active_pbft_public_key

        # Ensure that the enclave is using the appropriate keys
        try:
            unsealed_pbft_public_key = SignupInfo.unseal_signup_data(
                    pbft_enclave_module=pbft_enclave_module,
                    sealed_signup_data=pbft_key_state.sealed_signup_data)
        except SystemError:
            # Signup data is unuseable
            LOGGER.error(
                'Could not unseal signup data associated with PPK: %s..%s',
                active_pbft_public_key[:8],
                active_pbft_public_key[-8:])
            self._pbft_key_state_store.active_key = None
            return False
        LOGGER.debug("unsealed_pbft_public_key=%s ~ %s",unsealed_pbft_public_key,active_pbft_public_key)
        assert active_pbft_public_key == unsealed_pbft_public_key

        LOGGER.debug('Using PBFT public key: %s...%s',active_pbft_public_key[:8],active_pbft_public_key[-8:])
        LOGGER.debug('Unseal signup data: %s...%s',pbft_key_state.sealed_signup_data[:8],pbft_key_state.sealed_signup_data[-8:])
        LOGGER.debug("initialize_block:ADD CONSENSUS_STATE for block_id=%s",block_header.previous_block_id)
        consensus_state = ConsensusState.consensus_state_for_block_id(
                block_id=block_header.previous_block_id,
                block_cache=self._block_cache,
                state_view_factory=self._state_view_factory,
                consensus_state_store=self._consensus_state_store,
                pbft_enclave_module=None,#pbft_enclave_module=pbft_enclave_module
                )
        pbft_settings_view = PbftSettingsView(state_view)

        # If our signup information does not pass the freshness test, then we
        # know that other validators will reject any blocks we try to claim so
        # we need to try to sign up again.
        if consensus_state.validator_signup_was_committed_too_late(
                validator_info=validator_info,
                pbft_settings_view=pbft_settings_view,
                block_cache=self._block_cache):
            LOGGER.info(
                'Reject building on block %s: Validator signup information '
                'not committed in a timely manner.',
                block_header.previous_block_id[:8])
            self._register_signup_information(block_header=block_header)
            return False

        # Using the consensus state for the block upon which we want to
        # build, check to see how many blocks we have claimed on this chain
        # with this PBFT key.  If we have hit the key block claim limit, then
        # we need to check if the key has been refreshed.
        if consensus_state.validator_has_claimed_block_limit(
                validator_info=validator_info,
                pbft_settings_view=pbft_settings_view):
            # Because we have hit the limit, check to see if we have already
            # submitted a validator registry transaction with new signup
            # information, and therefore a new PBFT public key.  If not, then
            # mark this PBFT public key in the store as having been refreshed
            # and register new signup information.  Regardless, since we have
            # hit the key block claim limit, we won't even bother initializing
            # a block on this chain as it will be rejected by other
            # validators.

            LOGGER.debug("initialize_block:ADD validator_has_claimed_block_limit ")
            pbft_key_state = self._pbft_key_state_store[active_pbft_public_key]
            if not pbft_key_state.has_been_refreshed:
                LOGGER.info(
                    'Reached block claim limit for key: %s...%s',
                    active_pbft_public_key[:8],
                    active_pbft_public_key[-8:])

                sealed_signup_data = pbft_key_state.sealed_signup_data
                signup_nonce = pbft_key_state.signup_nonce
                self._pbft_key_state_store[active_pbft_public_key] = PbftKeyState(
                                                                        sealed_signup_data=sealed_signup_data,
                                                                        has_been_refreshed=True,
                                                                        signup_nonce=signup_nonce)

                # Release enclave resources for this identity
                # This signup will be invalid on all forks that use it,
                # even if there is a rollback to a point it should be valid.
                # A more sophisticated policy would be to release signups
                # only at a block depth where finality probability
                # is high.
                SignupInfo.release_signup_data(
                    pbft_enclave_module=None,#pbft_enclave_module=pbft_enclave_module,
                    sealed_signup_data=sealed_signup_data)

                self._register_signup_information(block_header=block_header)

            LOGGER.info(
                'Reject building on block %s: Validator has reached maximum '
                'number of blocks with key pair.',
                block_header.previous_block_id[:8])
            return False

        # Verify that we are abiding by the block claim delay (i.e., waiting a
        # certain number of blocks since our validator registry was added/
        # updated).
        if consensus_state.validator_is_claiming_too_early(
                validator_info=validator_info,
                block_number=block_header.block_num,
                validator_registry_view=validator_registry_view,
                pbft_settings_view=pbft_settings_view,
                block_store=self._block_cache.block_store):
            LOGGER.info(
                'Reject building on block %s: Validator has not waited long '
                'enough since registering validator information.',
                block_header.previous_block_id[:8])
            return False

        # We need to create a wait timer for the block...this is what we
        # will check when we are asked if it is time to publish the block
        pbft_key_state = self._pbft_key_state_store[active_pbft_public_key]
        sealed_signup_data = pbft_key_state.sealed_signup_data
        previous_certificate_id = \
            utils.get_previous_certificate_id(
                block_header=block_header,
                block_cache=self._block_cache,
                pbft_enclave_module=pbft_enclave_module
                )
        """
        wait_timer = \
            WaitTimer.create_wait_timer(
                pbft_enclave_module=pbft_enclave_module,
                sealed_signup_data=sealed_signup_data,
                validator_address=block_header.signer_public_key,
                previous_certificate_id=previous_certificate_id,
                consensus_state=consensus_state,
                pbft_settings_view=pbft_settings_view)
        """
        wait_timer =None

        # NOTE - we do the zTest after we create the wait timer because we
        # need its population estimate to see if this block would be accepted
        # by other validators based upon the zTest.

        # Check to see if by chance we were to be able to claim this block
        # if it would result in us winning more frequently than statistically
        # expected.  If so, then refuse to initialize the block because other
        # validators will not accept anyway.
        """
        if consensus_state.validator_is_claiming_too_frequently(
                validator_info=validator_info,
                previous_block_id=block_header.previous_block_id,
                pbft_settings_view=pbft_settings_view,
                population_estimate=wait_timer.population_estimate(pbft_settings_view=pbft_settings_view),
                block_cache=self._block_cache,
                pbft_enclave_module=pbft_enclave_module):
            LOGGER.info(
                'Reject building on block %s: '
                'Validator (signing public key: %s) is claiming blocks '
                'too frequently.',
                block_header.previous_block_id[:8],
                block_header.signer_public_key)
            return False
        """
        # At this point, we know that if we are able to claim the block we are
        # initializing, we will not be prevented from doing so because of PBFT
        # policies.

        self._wait_timer = wait_timer
        PbftBlockPublisher._previous_block_id = None

        LOGGER.debug('Created wait timer: %s', self._wait_timer)

        return True

    def check_publish_block(self, block_header):
        """Check if a candidate block is ready to be claimed.

        Args:
            block_header (BlockHeader): The block header for the candidate
                block that is checked for readiness for publishing.
        Returns:
            Boolean: True if the candidate block should be claimed. False if
            the block is not ready to be claimed.
        """

        # Only claim readiness if the wait timer has expired
        return True #self._wait_timer.has_expired(now=time.time())

    def finalize_block(self, block_header):
        """Finalize a block to be claimed. Provide any signatures and
        data updates that need to be applied to the block before it is
        signed and broadcast to the network.

        Args:
            block_header (BlockHeader): The block header for the candidate
                block that needs to be finalized.
        Returns:
            Boolean: True if the candidate block good and should be generated.
            False if the block should be abandoned.
        """
        if isinstance(block_header, bytes):
            # Using the current chain head, we need to create a state
            # view so we can create a PBFT enclave.
            state_view = BlockWrapper.state_view_for_block(
                    block_wrapper=self._block_cache.block_store.chain_head,
                    state_view_factory=self._state_view_factory)
            
            pbft_enclave_module = factory.PbftEnclaveFactory.get_pbft_enclave_module(
                    state_view=state_view,
                    config_dir=self._config_dir,
                    data_dir=self._data_dir)
            # We need to create a wait certificate for the block and
            # then serialize that into the block header consensus field.
            active_key = self._pbft_key_state_store.active_key
            pbft_key_state = self._pbft_key_state_store[active_key]
            sealed_signup_data = pbft_key_state.sealed_signup_data
            """
            try:
                wait_certificate = WaitCertificate.create_wait_certificate(
                        pbft_enclave_module=pbft_enclave_module,
                        sealed_signup_data=sealed_signup_data,
                        wait_timer=self._wait_timer,
                        block_hash=block_header)
                consensus = json.dumps(wait_certificate.dump()).encode()
            except ValueError as ve:
                LOGGER.error('Failed to create wait certificate: %s', ve)
                return None

            LOGGER.debug('Created wait certificate: %s', wait_certificate)
            """
            consensus = b'pbft' 
            return consensus

        # To compute the block hash, we are going to perform a hash using the
        # previous block ID and the batch IDs contained in the block
        hasher = hashlib.sha256(block_header.previous_block_id.encode())
        for batch_id in block_header.batch_ids:
            hasher.update(batch_id.encode())
        block_hash = hasher.hexdigest()

        # Using the current chain head, we need to create a state view so we
        # can create a PBFT enclave.
        state_view = BlockWrapper.state_view_for_block(
                block_wrapper=self._block_cache.block_store.chain_head,
                state_view_factory=self._state_view_factory)
         
        pbft_enclave_module = factory.PbftEnclaveFactory.get_pbft_enclave_module(
                state_view=state_view,
                config_dir=self._config_dir,
                data_dir=self._data_dir)
        
        # We need to create a wait certificate for the block and then serialize
        # that into the block header consensus field.
        active_key = self._pbft_key_state_store.active_key
        pbft_key_state = self._pbft_key_state_store[active_key]
        sealed_signup_data = pbft_key_state.sealed_signup_data
        try:
            wait_certificate = WaitCertificate.create_wait_certificate(
                    pbft_enclave_module=pbft_enclave_module,
                    sealed_signup_data=sealed_signup_data,
                    wait_timer=self._wait_timer,
                    block_hash=block_hash)
            block_header.consensus = json.dumps(wait_certificate.dump()).encode()
        except ValueError as ve:
            LOGGER.error('Failed to create wait certificate: %s', ve)
            return False

        LOGGER.debug('Created wait certificate: %s', wait_certificate)

        return True
