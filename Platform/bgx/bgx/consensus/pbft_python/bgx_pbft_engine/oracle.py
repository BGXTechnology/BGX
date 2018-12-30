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
# -----------------------------------------------------------------------------

import logging
import os
import binascii

import sawtooth_signing as signing
from sawtooth_signing import CryptoFactory
from sawtooth_signing.secp256k1 import Secp256k1PrivateKey

from sawtooth_sdk.consensus.exceptions import UnknownBlock
from sawtooth_sdk.messaging.stream import Stream
from sawtooth_sdk.protobuf.batch_pb2 import Batch
from sawtooth_sdk.protobuf.batch_pb2 import BatchHeader
from sawtooth_sdk.protobuf.client_batch_submit_pb2 import ClientBatchSubmitRequest
from sawtooth_sdk.protobuf.client_batch_submit_pb2 import ClientBatchSubmitResponse
from sawtooth_sdk.protobuf.client_block_pb2 import ClientBlockGetByTransactionIdRequest
from sawtooth_sdk.protobuf.client_block_pb2 import ClientBlockGetResponse
from sawtooth_sdk.protobuf.block_pb2 import BlockHeader
from sawtooth_sdk.protobuf.consensus_pb2 import ConsensusBlock
from sawtooth_sdk.protobuf.validator_pb2 import Message

from bgx_pbft.consensus.pbft_block_publisher import PbftBlockPublisher
#from bgx_pbft.consensus.pbft_block_verifier import BgtBlockVerifier
#from bgx_pbft.consensus.pbft_fork_resolver import BgtForkResolver
from bgx_pbft.consensus.consensus_state import ConsensusState
from bgx_pbft.consensus.consensus_state_store import ConsensusStateStore


from bgx_pbft_common.protobuf.pbft_consensus_pb2 import PbftMessage,PbftMessageInfo,PbftBlockMessage

LOGGER = logging.getLogger(__name__)

class PbftOracle:
    """
    This is a wrapper around the PBFT structures (publisher,verifier, fork resolver) and their attendant proxies.
    """
    CONSENSUS_MSG = ['PrePrepare','Prepare','Commit','CheckPoint']

    def __init__(self, service, component_endpoint,
                 config_dir, data_dir, key_dir):
        
        self._config_dir = config_dir
        self._data_dir = data_dir
        self._service = service
        LOGGER.debug('PbftOracle: Stream key_dir=%s',key_dir)
        self._signer = _load_identity_signer(key_dir, 'validator')
        self._validator_id = self._signer.get_public_key().as_hex()

        LOGGER.debug('PbftOracle: Stream component_endpoint=%s ',component_endpoint)
        stream = Stream(component_endpoint)

        self._block_cache = _BlockCacheProxy(service, stream)
        self._state_view_factory = _StateViewFactoryProxy(service)

        self._batch_publisher = _BatchPublisherProxy(stream, self._signer)
        self._publisher = None
        self._consensus_state_store = ConsensusStateStore(data_dir=self._data_dir,validator_id=self._validator_id)
        LOGGER.debug('PbftOracle: _validator_id=%s init DONE',self._validator_id)

    def get_validator_id(self):
        return self._validator_id 

    def initialize_block(self, previous_block,node):
        block_header = NewBlockHeader(
            previous_block,
            self._signer.get_public_key().as_hex()
            )

        self._publisher = PbftBlockPublisher(
            block_cache=self._block_cache,
            state_view_factory=self._state_view_factory,
            batch_publisher=self._batch_publisher,
            data_dir=self._data_dir,
            config_dir=self._config_dir,
            validator_id=self._validator_id,
            node = node
            )

        return self._publisher.initialize_block(block_header)

    def check_publish_block(self, block):
        #LOGGER.debug('PbftOracle:check_publish_block...')
        return self._publisher.check_publish_block(block)

    def finalize_block(self, block):
        LOGGER.debug('PbftOracle:finalize_block...')
        return self._publisher.finalize_block(block)

    def verify_block(self, block):
        LOGGER.debug('PbftOracle:verify_block...')
        
        """
        verifier = PbfBlockVerifier(
            block_cache=self._block_cache,
            state_view_factory=self._state_view_factory,
            data_dir=self._data_dir,
            config_dir=self._config_dir,
            validator_id=self._validator_id)

        return verifier.verify_block(block)
        """

    def switch_forks(self, cur_fork_head, new_fork_head):
        '''"compare_forks" is not an intuitive name.'''
        LOGGER.debug('PbftOracle: switch_forks %s',cur_fork_head)
        if new_fork_head.block_num > cur_fork_head.block_num or (new_fork_head.block_num == cur_fork_head.block_num and new_fork_head.block_id > cur_fork_head.block_id) :
            LOGGER.debug('PbftOracle: switch_forks new-num=%s cur-num=%s new-id=%s cur-id=%s',new_fork_head.block_num,cur_fork_head.block_num,new_fork_head.block_id,cur_fork_head.block_id)
            return True
        elif new_fork_head.block_num < cur_fork_head.block_num :
            #
            chain_block = cur_fork_head
            LOGGER.debug('PbftOracle: new_fork_head.block_num=%s < cur_fork_head.block_num=%s',new_fork_head.block_num,cur_fork_head.block_num)
            num = 0
            while(True): 
                chain_block = PbftBlock(self._service.get_blocks([chain_block.previous_id])[chain_block.previous_id]) 
                LOGGER.debug('PbftOracle: while chain_block.block_num=%s == new_fork_head.block_num=%s',chain_block.block_num,new_fork_head.block_num) 
                if chain_block.block_num == new_fork_head.block_num or num > 20:
                    LOGGER.debug('PbftOracle: found block')
                    break
                num += 1 
            if new_fork_head.block_id > chain_block.block_id:
                LOGGER.debug('PbftOracle: switch to new forks')
                return True    

        return False

    def get_consensus_state_for_block_id(self,block):
        block_id = block.block_id.hex()
        LOGGER.debug("PbftOracle: get_consensus_state for block_id='%s'",block_id)
        consensus_state = ConsensusState.consensus_state_for_block_id(
                block_id=block_id, #block_header.previous_block_id,
                block_cache=self._block_cache,
                state_view_factory=self._state_view_factory,
                consensus_state_store=self._consensus_state_store
                )
        return consensus_state

    def start_consensus(self,node,block):
        """
        Have got NewBlock message
        """
        #block_str = int2hex(block.block_num)
        block_id = block.block_id.hex()
        state = self.get_consensus_state_for_block_id(block)
        if state is not None:
            if state.is_step_NotStarted:
                LOGGER.debug('PbftOracle: START CONSENSUS for block_id=%s step=%s mode=%s',block_id,state.step,state.mode)
                state.next_step()
                self._consensus_state_store[block_id] = state # save new state
                if node == 'leader':
                    # leader node - send prePrepare
                    self._send_pre_prepare(state,block)
                elif  node == 'plink' :
                    # just change step of consensus
                    LOGGER.debug('PbftOracle: PLINK node CONSENSUS step=%s',state.step)
                return True
            else:
                LOGGER.debug('PbftOracle: cant START CONSENSUS for block_id=%s (incorrect state) ',block_id)
        else:
            LOGGER.debug('PbftOracle: there is no CONSENSUS_STATE for block_id=%s',block_id)
            return False

    def peer_message(self,node,msg):
        """
        consensuse message: PrePrepare, Prepare, Commit, Checkpoint 
        """
       # LOGGER.debug('PbftOracle: peer_message p2p=(%s),(%s)',type(msg[0]),msg[1])
        p2p_mesg = msg[0]
        payload = PbftMessage()
        payload.ParseFromString(p2p_mesg.content)
        info,block = payload.info,payload.block
        mgs_type = PbftOracle.CONSENSUS_MSG[info.msg_type]
        block_id = block.block_id.hex()
        state = self.get_consensus_state_for_block_id(block)
        LOGGER.debug("PbftOracle: Received PEER_MESSAGE '%s' for block='%s'",mgs_type,block_id)
        def is_pre_prepare_valid():
            return True

        if state is None:
            """
            Undefined state for block 
            """
            LOGGER.debug('PbftOracle: PEER_MESSAGE undefined state for block=%s IGNORE message',block_id)
            return
        
        LOGGER.debug('PbftOracle: CONSENSUS_STATE=%s for block_id=%s',state,block_id) 
        if node == 'plink':
            if state.is_step_PrePreparing :
                if info.msg_type == PbftMessageInfo.PRE_PREPARE_MSG:
                    LOGGER.debug('PbftOracle: check PRE_PREPARE_MSG for node=%s',node)
                    """
                    check PRE_PREPARE_MSG:
                        1) signer_id and summary of block inside PrePrepare match the corresponding fields of the original BlockNew block 
                        2) View in PrePrepare message corresponds to this server's current view &&
                        3) This message hasn't been accepted already with a different summary &&
                        4) Sequence number is within the sequential bounds of the log (low and high water marks)
                    """
                    if is_pre_prepare_valid():
                        # if correct change step of consensus and send Prepare 
                        state.next_step()
                        self._consensus_state_store[block_id] = state
                        self._send_prepare(state,block)
                    else:
                        # If the PrePrepare is determined to be invalid, then start a view change
                        pass
                else:
                    LOGGER.debug('PbftOracle: PRE_PREPARE_MSG in incorrect consensus state=%s',state.step)
            elif state.is_step_Preparing:
                # PREPARING STATE
                if info.msg_type == PbftMessageInfo.PRE_PREPARE_MSG:
                    LOGGER.debug('PbftOracle: PRE_PREPARE_MSG node=%s',node)
                    state.next_step()
                    self._consensus_state_store[block_id] = state
                    self._send_prepare(state,block)
                else:
                    LOGGER.debug('PbftOracle: PRE_PREPARE_MSG in incorrect consensus state=%s',state.step)
            else:
                LOGGER.debug('PbftOracle: PRE_PREPARE_MSG in incorrect consensus state=%s',state.step)

        elif node == 'leader' :
            #if state.is_step_PrePreparing :
            if info.msg_type == PbftMessageInfo.PREPARE_MSG :
                LOGGER.debug('PbftOracle: PREPARE_MSG node=%s state=%s',node,state.step)
                if state.is_step_PrePreparing :
                    # reply from plink node
                    LOGGER.debug('PbftOracle: have got PREPARE_MSG in PrePreparing check_blocks=%d',block_id)
                    self._service.check_blocks([block.block_id])



    def _send_pre_prepare(self,state,block):
        # send PRE_PREPARE message 
        messageInfo = PbftMessageInfo(
                    msg_type = PbftMessageInfo.PRE_PREPARE_MSG,
                    view     = 0,
                    seq_num  = state.sequence_number,
                    signer_id = self.get_validator_id().encode()
            ) 
        blockMessage = PbftBlockMessage(
                    block_id  = block.block_id,
                    signer_id =  block.signer_id,
                    block_num = block.block_num,
                    summary   = block.summary 
            )
        self._broadcast(messageInfo,blockMessage) 

    def _send_prepare(self,state,block):
        # send PREPARE message 
        messageInfo = PbftMessageInfo(
                    msg_type = PbftMessageInfo.PREPARE_MSG,
                    view     = 0,
                    seq_num  = state.sequence_number,
                    signer_id = self.get_validator_id().encode()
            ) 
        blockMessage = PbftBlockMessage(
                    block_id  = block.block_id,
                    signer_id =  block.signer_id,
                    block_num = block.block_num,
                    summary   = block.summary 
            )
        self._broadcast(messageInfo,blockMessage) 


    def _broadcast(self,info,block):
        # broadcast 
        payload = PbftMessage(
                    info  = info,
                    block = block 
            )
        mgs_type = PbftOracle.CONSENSUS_MSG[info.msg_type]
        LOGGER.debug("broadcast CONSENSUS_MSG '%s' for(%s)",mgs_type,block.block_id.hex())
        self._service.broadcast(mgs_type,payload.SerializeToString()) 



class PbftBlock:
    def __init__(self, block):
        # fields that come with consensus blocks
        
        self.block_id = block.block_id
        self.previous_id = block.previous_id
        self.signer_id = block.signer_id
        self.block_num = block.block_num
        self.payload = block.payload
        self.summary = block.summary
        LOGGER.debug('PbftBlock: init block_id=%s prev_id=%s',self.block_id.hex(),self.previous_id.hex())
        # fields that bgt requires
        identifier = block.block_id.hex()
        previous_block_id = block.previous_id.hex()
        signer_public_key = block.signer_id.hex()

        self.identifier = identifier
        self.header_signature = identifier
        self.previous_block_id = previous_block_id
        self.signer_public_key = signer_public_key

        self.header = _DummyHeader(
            consensus=block.payload,
            signer_public_key=signer_public_key,
            previous_block_id=previous_block_id)

        # this is a trick
        self.state_root_hash = block.block_id

    def __str__(self):
        return (
            "Block("
            + ", ".join([
                "block_num: {}".format(self.block_num),
                "block_id: {}".format(self.block_id.hex()),
                "previous_id: {}".format(self.previous_id.hex()),
                "signer_id: {}".format(self.signer_id.hex()),
                "payload: {}".format(self.payload),
                "summary: {}".format(self.summary.hex()),
            ])
            + ")"
        )


class NewBlockHeader:
    '''The header for the block that is to be initialized.'''
    def __init__(self, previous_block, signer_public_key):
        self.consensus = None
        self.signer_public_key = signer_public_key
        self.previous_block_id = previous_block.identifier
        self.block_num = previous_block.block_num + 1


class _DummyHeader:
    def __init__(self, consensus, signer_public_key, previous_block_id):
        self.consensus = consensus
        self.signer_public_key = signer_public_key
        self.previous_block_id = previous_block_id


class _BlockCacheProxy:
    """
    interface to BlockCache
    """
    def __init__(self, service, stream):
        self.block_store = _BlockStoreProxy(service, stream)  # public
        self._service = service

    def __getitem__(self, block_id):
        block_id = bytes.fromhex(block_id)

        try:
            return PbftBlock(self._service.get_blocks([block_id])[block_id])
        except UnknownBlock:
            return None


class _BlockStoreProxy:
    def __init__(self, service, stream):
        self._service = service
        self._stream = stream

    @property
    def chain_head(self):
        return PbftBlock(self._service.get_chain_head())

    def get_block_by_transaction_id(self, transaction_id):
        future = self._stream.send(
            message_type=Message.CLIENT_BLOCK_GET_BY_TRANSACTION_ID_REQUEST,
            content=ClientBlockGetByTransactionIdRequest(
                transaction_id=transaction_id).SerializeToString())

        content = future.result().content

        response = ClientBlockGetResponse()
        response.ParseFromString(content)

        if response.status == ClientBlockGetResponse.NO_RESOURCE:
            raise ValueError("The transaction supplied is not in a block")

        block = response.block

        header = BlockHeader()
        header.ParseFromString(block.header)

        consensus_block = ConsensusBlock(
            block_id=bytes.fromhex(block.header_signature),
            previous_id=bytes.fromhex(header.previous_block_id),
            signer_id=bytes.fromhex(header.signer_public_key),
            block_num=header.block_num,
            payload=header.consensus,
            summary=b'')

        bgt_block = PbftBlock(consensus_block)

        return bgt_block

    def get_block_iter(self, reverse):
        # Ignore the reverse flag, since we can only get blocks
        # starting from the head.

        chain_head = self.chain_head

        yield chain_head

        curr = chain_head

        while curr.previous_id:
            try:
                previous_block = PbftBlock(
                    self._service.get_blocks(
                        [curr.previous_id]
                    )[curr.previous_id])
            except UnknownBlock:
                return

            yield previous_block

            curr = previous_block


class _StateViewFactoryProxy:
    def __init__(self, service):
        self._service = service

    def create_view(self, state_root_hash=None):
        '''The "state_root_hash" is really the block_id.'''

        block_id = state_root_hash

        return _StateViewProxy(self._service, block_id)


class _StateViewProxy:
    def __init__(self, service, block_id):
        self._service = service
        self._block_id = block_id

    def get(self, address):
        result = self._service.get_state(
            block_id=self._block_id,
            addresses=[address])

        return result[address]

    def leaves(self, prefix):
        result = self._service.get_state(
            block_id=self._block_id,
            addresses=[prefix])

        return [
            (address, data)
            for address, data in result.items()
        ]


class _BatchPublisherProxy:
    def __init__(self, stream, signer):
        self.identity_signer = signer  # public
        self._stream = stream

    def send(self, transactions):
        txn_signatures = [txn.header_signature for txn in transactions]

        header = BatchHeader(
            signer_public_key=self.identity_signer.get_public_key().as_hex(),
            transaction_ids=txn_signatures
        ).SerializeToString()

        signature = self.identity_signer.sign(header)

        batch = Batch(
            header=header,
            transactions=transactions,
            header_signature=signature)

        future = self._stream.send(
            message_type=Message.CLIENT_BATCH_SUBMIT_REQUEST,
            content=ClientBatchSubmitRequest(
                batches=[batch]).SerializeToString())
        LOGGER.debug('_BatchPublisherProxy: future.result ...')
        result = future.result()
        LOGGER.debug('_BatchPublisherProxy: future.result DONE')
        assert result.message_type == Message.CLIENT_BATCH_SUBMIT_RESPONSE
        response = ClientBatchSubmitResponse()
        response.ParseFromString(result.content)
        if response.status != ClientBatchSubmitResponse.OK:
            LOGGER.warning("Submitting batch failed with status %s", response)


def _load_identity_signer(key_dir, key_name):
    """Loads a private key from the key directory, based on a validator's
    identity.

    Args:
        key_dir (str): The path to the key directory.
        key_name (str): The name of the key to load.

    Returns:
        Signer: the cryptographic signer for the key
    """
    key_path = os.path.join(key_dir, '{}.priv'.format(key_name))

    if not os.path.exists(key_path):
        raise Exception(
            "No such signing key file: {}".format(key_path))
    if not os.access(key_path, os.R_OK):
        raise Exception(
            "Key file is not readable: {}".format(key_path))

    LOGGER.info('Loading signing key: %s', key_path)
    try:
        with open(key_path, 'r') as key_file:
            private_key_str = key_file.read().strip()
    except IOError as e:
        raise Exception(
            "Could not load key file: {}".format(str(e)))

    try:
        private_key = Secp256k1PrivateKey.from_hex(private_key_str)
    except signing.ParseError as e:
        raise Exception(
            "Invalid key in file {}: {}".format(key_path, str(e)))

    context = signing.create_context('secp256k1')
    crypto_factory = CryptoFactory(context)
    return crypto_factory.new_signer(private_key)
