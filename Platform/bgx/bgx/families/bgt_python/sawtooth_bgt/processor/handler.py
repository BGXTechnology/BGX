# Copyright 2018 NTRLab
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

import logging
import hashlib
import base64
import cbor


from sawtooth_sdk.processor.handler import TransactionHandler
from sawtooth_sdk.processor.exceptions import InvalidTransaction
from sawtooth_sdk.processor.exceptions import InternalError
from bgt_common.protobuf.smart_bgt_token_pb2 import BgtTokenInfo
from sawtooth_signing.secp256k1 import Secp256k1PrivateKey, Secp256k1PublicKey, Secp256k1Context
from sawtooth_signing import CryptoFactory,create_context

LOGGER = logging.getLogger(__name__)


VALID_VERBS = 'set', 'inc', 'dec'

MIN_VALUE = 0
MAX_VALUE = 4294967295
MAX_NAME_LENGTH = 20

FAMILY_NAME = 'bgt'

BGT_ADDRESS_PREFIX = hashlib.sha512(FAMILY_NAME.encode('utf-8')).hexdigest()[0:6]


def make_bgt_address(name):
    return BGT_ADDRESS_PREFIX + hashlib.sha512(name.encode('utf-8')).hexdigest()[-64:]


class BgtTransactionHandler(TransactionHandler):
    def __init__(self):
        self._context = create_context('secp256k1') 
        LOGGER.debug('_do_set: context')
        self._private_key = Secp256k1PrivateKey.new_random()
        LOGGER.debug('_do_set: context private_key=%s',self._private_key.as_hex())
        self._public_key = self._context.get_public_key(self._private_key)
        crypto_factory = CryptoFactory(self._context)
        self._signer = crypto_factory.new_signer(self._private_key)
        #self._signer = CryptoFactory(self._context).new_signer(self.private_key)
        LOGGER.debug('_do_set: public_key=%s  ',self._public_key.as_hex())
        LOGGER.info('BgtTransactionHandler init DONE')

    @property
    def family_name(self):
        return FAMILY_NAME

    @property
    def family_versions(self):
        return ['1.0']

    @property
    def namespaces(self):
        return [BGT_ADDRESS_PREFIX]

    def apply(self, transaction, context):
        verb, name, value = _unpack_transaction(transaction)

        state = _get_state_data(name, context)

        updated_state = self._do_bgt(verb, name, value, state)

        _set_state_data(name, updated_state, context)

    def _do_bgt(self,verb, name, value, state):
        verbs = {
            'set': self._do_set,
            'inc': self._do_inc,
            'dec': self._do_dec,
        }
        LOGGER.debug('_do_bgt request....')
        try:
            return verbs[verb](name, value, state)
        except KeyError:
            # This would be a programming error.
            raise InternalError('Unhandled verb: {}'.format(verb))


    def _do_set(self,name, value, state):
        msg = 'Setting "{n}" to {v}'.format(n=name, v=value)
        LOGGER.debug(msg)
        

        if name in state:
            raise InvalidTransaction('Verb is "set", but already exists: Name: {n}, Value {v}'.format(n=name,v=state[name]))

        updated = {k: v for k, v in state.items()}
        #owner_key = self._context.sign('BGT_token'.encode(),self._private_key)
        token = BgtTokenInfo(group_code = 'BGT_token',
                             owner_key = self._signer.sign('BGT_token'.encode()), #owner_key,
                             sign = self._public_key.as_hex(),
                             decimals = int(value)
                )
        updated[name] = token.SerializeToString()
        LOGGER.debug('_do_set updated=%s',updated)
        return updated


    def _do_inc(self,name, value, state):
        msg = 'Incrementing "{n}" by {v}'.format(n=name, v=value)
        LOGGER.debug(msg)

        if name not in state:
            raise InvalidTransaction(
                'Verb is "inc" but name "{}" not in state'.format(name))

        curr = state[name]
        token = BgtTokenInfo()
        token.ParseFromString(curr)
        LOGGER.debug('_do_inc token[%s]=%s',token.group_code,token.decimals,value)
        incd = token.decimals + value

        if incd > MAX_VALUE:
            raise InvalidTransaction(
                'Verb is "inc", but result would be greater than {}'.format(
                    MAX_VALUE))

        updated = {k: v for k, v in state.items()}
        token.decimals = incd
        updated[name] = token.SerializeToString() 

        return updated


    def _do_dec(self,name, value, state):
        msg = 'Decrementing "{n}" by {v}'.format(n=name, v=value)
        LOGGER.debug(msg)

        if name not in state:
            raise InvalidTransaction(
                'Verb is "dec" but name "{}" not in state'.format(name))

        curr = state[name]
        token = BgtTokenInfo()
        token.ParseFromString(curr)
        LOGGER.debug('_do_dec token[%s]=%s',token.group_code,token.decimals,value)
        decd = token.decimals - value

        if decd < MIN_VALUE:
            raise InvalidTransaction(
                'Verb is "dec", but result would be less than {}'.format(
                    MIN_VALUE))

        updated = {k: v for k, v in state.items()}
        token.decimals = decd
        updated[name] = token.SerializeToString()

        return updated
        

def _unpack_transaction(transaction):
    verb, name, value = _decode_transaction(transaction)

    _validate_verb(verb)
    _validate_name(name)
    _validate_value(value)

    return verb, name, value


def _decode_transaction(transaction):
    try:
        content = cbor.loads(transaction.payload)
    except:
        raise InvalidTransaction('Invalid payload serialization')

    try:
        verb = content['Verb']
    except AttributeError:
        raise InvalidTransaction('Verb is required')

    try:
        name = content['Name']
    except AttributeError:
        raise InvalidTransaction('Name is required')

    try:
        value = content['Value']
    except AttributeError:
        raise InvalidTransaction('Value is required')

    return verb, name, value


def _validate_verb(verb):
    if verb not in VALID_VERBS:
        raise InvalidTransaction('Verb must be "set", "inc", or "dec"')


def _validate_name(name):
    if not isinstance(name, str) or len(name) > MAX_NAME_LENGTH:
        raise InvalidTransaction(
            'Name must be a string of no more than {} characters'.format(
                MAX_NAME_LENGTH))


def _validate_value(value):
    if not isinstance(value, int) or value < 0 or value > MAX_VALUE:
        raise InvalidTransaction(
            'Value must be an integer '
            'no less than {i} and no greater than {a}'.format(
                i=MIN_VALUE,
                a=MAX_VALUE))


def _get_state_data(name, context):
    address = make_bgt_address(name)

    state_entries = context.get_state([address])

    try:
        #value = BgtTokenInfo()
        #value.ParseFromString(state_entries[0].data)
        return cbor.loads(state_entries[0].data)
    except IndexError:
        return {}
    except:
        raise InternalError('Failed to load state data')


def _set_state_data(name, state, context):
    address = make_bgt_address(name)

    encoded = cbor.dumps(state)

    addresses = context.set_state({address: encoded})

    if not addresses:
        raise InternalError('State error')



