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
import hashlib
import cbor
import json
import os

from sawtooth_sdk.processor.handler import TransactionHandler
from sawtooth_sdk.processor.exceptions import InvalidTransaction
from sawtooth_sdk.processor.exceptions import InternalError
from smart_bgt.processor.utils  import FAMILY_NAME,FAMILY_VER,make_smart_bgt_address,SMART_BGT_ADDRESS_PREFIX
from smart_bgt.processor.utils  import TRANSFER_FEE, SMART_BGT_META_ADDRESS,ALLOWANCE_INFO
from smart_bgt.processor.services import BGXlistener, AllowanceRow
from smart_bgt.processor.services import BGXwallet, BGXmeta, StorageCell
from smart_bgt.processor.crypto import BGXCrypto
from smart_bgt.processor.token import Token
from smart_bgt.processor.token import MetaToken
from smart_bgt.processor.emission import EmissionMechanism
from sawtooth_signing import create_context


LOGGER = logging.getLogger(__name__)
BGX_EPSILON = 0.000000000001


class SmartBgtTransactionHandler(TransactionHandler):
    @property
    def family_name(self):
        return FAMILY_NAME

    @property
    def family_versions(self):
        return [FAMILY_VER]

    @property
    def namespaces(self):
        return [SMART_BGT_ADDRESS_PREFIX]

    def apply(self, transaction, context):
        verb, args = _unpack_transaction(transaction)
        LOGGER.info('SmartBgtTransactionHandler verb=%s args %s', verb, args)
        try:
            if verb == 'generate_key':
                state = ''
            elif verb == 'balance_of':
                state = _get_state_data([args['addr']], context)
            elif verb == 'total_supply':
                state = _get_state_data([args['token_name']], context)
            elif verb == 'init':
                #private_key = args['private_key']
                #digital_signature = BGXCrypto.DigitalSignature(private_key)
                validator_digital_signature = BGXCrypto.get_validator_signature()
                validator_address = validator_digital_signature.get_verifying_key()
                #state = _get_state_data([args['Name'], open_key, SMART_BGT_META_ADDRESS], context)
                state = _get_state_data([validator_address, SMART_BGT_META_ADDRESS], context)
            elif verb == 'transfer':
                validator_digital_signature = BGXCrypto.get_validator_signature()
                validator_address = validator_digital_signature.get_verifying_key()
                state = _get_state_data([args['from_addr'], args['to_addr'], validator_address], context)
            elif verb == 'transfer_bgx':
                validator_digital_signature = BGXCrypto.get_validator_signature()
                validator_address = validator_digital_signature.get_verifying_key()
                state = _get_state_data([args['addr'], validator_address, SMART_BGT_META_ADDRESS], context)
            else:
                state = _get_state_data([ALLOWANCE_INFO], context)

            updated_state = _do_smart_bgt(verb, args, state)

            if not (verb == 'generate_key' or verb == 'balance_of' or verb == 'total_supply' or verb == 'allowance'):
                _set_state_data(updated_state, context)
        except AttributeError:
            raise InvalidTransaction('Args are required')


def _unpack_transaction(transaction):
    return  _decode_transaction(transaction)


def _decode_transaction(transaction):
    try:
        content = cbor.loads(transaction.payload)
    except:
        raise InvalidTransaction('Invalid payload serialization')
    try:
        verb = content['Verb']
    except AttributeError:
        raise InvalidTransaction('Verb is required')

    return verb, content


def _get_state_data(names, context):
    alist = []
    for name in names:
        address = make_smart_bgt_address(name)
        alist.append(address)
    state_entries = context.get_state(alist)

    LOGGER.debug('_get_state_data state_entries=%s', state_entries)
    try:
        states = {}
        for entry in state_entries:
            state = cbor.loads(entry.data)
            for key, val in state.items():
                LOGGER.debug('_get_state_data add=%s', key)
                states[key] = val
        return states
    except IndexError:
        return {}
    except:
        raise InternalError('Failed to load state data')


def _set_state_data(state, context):
    if state:
        new_states = {}
        for key,val in state.items():
            LOGGER.debug('_set_state_data  [%s]=%s', key, val)
            address = make_smart_bgt_address(key)
            encoded = cbor.dumps({key:val})
            new_states[address] = encoded

        addresses = context.set_state(new_states)

        if not addresses:
            LOGGER.debug('_set_state_data  State error')
            raise InternalError('State error')
        LOGGER.debug('_set_state_data  DONE address=%s', address)


def _do_smart_bgt(verb, args, state):
    LOGGER.debug('_do_smart_bgt request verb=%s', verb)

    try:
        if verb == 'init':
            return _do_init(args, state)
        elif verb == 'generate_key':
            return _do_generate_key(state)
        elif verb == 'transfer':
            return _do_transfer(args, state)
        elif verb == 'allowance':
            return _do_allowance(args, state)
        elif verb == 'approve':
            return _do_approve(args, state)
        elif verb == 'balance_of':
            return _get_balance_of(args, state)
        elif verb == 'total_supply':
            return _get_total_supply(args, state)
        elif verb == 'transfer_bgx':
            return _do_transfer_bgx(args, state)
    except KeyError:
        # This would be a programming error.
        raise InternalError('Unhandled verb: {}'.format(verb))

# generates new private key
def _do_generate_key(state):
    digital_signature = BGXCrypto.DigitalSignature()
    private_key = digital_signature.getSigningKey()
    LOGGER.debug("New private key generated: " + str(private_key))
    return state

# release new tokens
def _do_init(args, state):
    LOGGER.debug("_do_init ...")
    try:
        token_name = str(args['token_name'])
        ethereum_address = str(args['ethereum_address'])
        num_bgt = int(args['num_bgt'])
        bgt_price = int(args['bgt_price'])
        dec_price = int(args['dec_price'])
    except KeyError:
        msg = "_do_init not all args"
        LOGGER.debug(msg)
        raise InternalError(msg)

    updated = {k: v for k, v in state.items()}
    digital_signature = BGXCrypto.get_validator_signature()
    open_key = digital_signature.get_verifying_key()

    global_meta_tokens = StorageCell()
    if SMART_BGT_META_ADDRESS in state:
        global_meta_tokens_json = state[SMART_BGT_META_ADDRESS]
        global_meta_tokens.from_json(global_meta_tokens_json)

    group_code = BGXCrypto.get_string_hash(token_name)
    meta_token = global_meta_tokens.get_meta_token(group_code)

    cur_tokens = StorageCell()
    if open_key in state:
        cur_tokens_json = state[open_key]
        cur_tokens.from_json(cur_tokens_json)

    if meta_token is not None:
        LOGGER.debug("This type of tokens already exists. Updating..")

        old_price = meta_token.get_internal_token_price()
        owner = meta_token.get_owner_key()

        if abs(old_price - bgt_price) > BGX_EPSILON or owner != open_key:
            LOGGER.debug("Old price and new one are different OR wrong public key")
            return updated

        token = cur_tokens.get_token(group_code)
        token, meta_token = EmissionMechanism.release_extra_tokens(token, meta_token, ethereum_address, num_bgt, \
                                                                   bgt_price, dec_price)
    else:
        LOGGER.debug("Creating new type of tokens..")
        symbol = 'BGT'
        company_id = 'company_id'
        description = 'BGT token'

        token, meta_token = EmissionMechanism.release_tokens(token_name, symbol, company_id, ethereum_address, \
                                                             num_bgt, description, bgt_price, dec_price)

    if token is None or meta_token is None:
        LOGGER.debug("Emission failed: not enough money")
        return updated

    cur_tokens.append(token)
    global_meta_tokens.append(meta_token)

    updated[open_key] = str(cur_tokens.to_json())
    updated[SMART_BGT_META_ADDRESS] = str(global_meta_tokens.to_json())
    LOGGER.debug("Init - ready! updated=%s", updated)
    return updated

# transfer <num_bgt> tokens <group_id> from account <from_addr> to account <to_addr> 
# TODO: add check and usage of allowance settings
def _do_transfer(args, state):
    LOGGER.debug("_do_transfer ...")
    try:
        from_addr = str(args['from_addr'])
        to_addr = str(args['to_addr'])
        transfer_amount = float(args['num_bgt'])
        token_name = str(args['group_id'])
    except KeyError:
        msg = "_do_transfer not all args"
        LOGGER.debug(msg)
        raise InternalError(msg)

    updated = {k: v for k, v in state.items()}
    group_code = BGXCrypto.get_string_hash(token_name)

    if from_addr == to_addr:
        LOGGER.debug("Sending tokens - bad args")
        return updated

    if from_addr not in state:
        LOGGER.debug("Sending tokens - address %s not registered", from_addr)
        return updated

    senders_tokens_json = state[from_addr]
    senders_tokens = StorageCell(senders_tokens_json)
    senders_token = senders_tokens.get_token(group_code)

    if senders_token is None:
        LOGGER.debug("Sending tokens - sender has no tokens with id=%s", group_code)
        return updated

    digital_signature = BGXCrypto.get_validator_signature()
    beneficiary_addr = digital_signature.get_verifying_key()

    if beneficiary_addr in state:
        beneficiary_tokens_json = state[beneficiary_addr]
        beneficiary_tokens = StorageCell(beneficiary_tokens_json)
        beneficiary_token = beneficiary_tokens.get_token(group_code)

        if beneficiary_token is None:
            beneficiary_token = Token()
    else:
        beneficiary_tokens = StorageCell()
        beneficiary_token = Token()

    if to_addr in state:
        receivers_tokens_json = state[to_addr]
        receivers_tokens = StorageCell(receivers_tokens_json)
        receivers_token = receivers_tokens.get_token(group_code)

        if receivers_token is None:
            receivers_token = Token()
    else:
        receivers_tokens = StorageCell()
        receivers_token = Token()

    beneficiary_token.copy_identity(senders_token)
    receivers_token.copy_identity(senders_token)

    beneficiary_part = transfer_amount * TRANSFER_FEE
    receivers_part = transfer_amount - beneficiary_part

    if beneficiary_part < 0 or receivers_part < 0:
        LOGGER.debug("Sending token - wrong transfer fee")
        return updated

    if from_addr != beneficiary_addr and to_addr != beneficiary_addr:
        result = senders_token.get_amount(transfer_amount)
        receivers_token.add_amount(receivers_part)
        beneficiary_token.add_amount(beneficiary_part)

        if result:
            beneficiary_tokens.append(beneficiary_token)
            updated[beneficiary_addr] = str(beneficiary_tokens.to_json())
    else:
        if from_addr == beneficiary_addr:
            result = senders_token.get_amount(receivers_part)
            receivers_token.add_amount(receivers_part)
        else:
            result = senders_token.get_amount(transfer_amount)
            receivers_token.add_amount(transfer_amount)

    if not result:
        LOGGER.debug("Sending tokens - not enough money")
    else:
        senders_tokens.append(senders_token)
        receivers_tokens.append(receivers_token)
        updated[from_addr] = str(senders_tokens.to_json())
        updated[to_addr] = str(receivers_tokens.to_json())

    LOGGER.debug("Transfer - ready! updated=%s", updated)
    return updated

# internal exchange of tokens with different type of group_id 
def _do_transfer_bgx(args, state):
    LOGGER.debug("_do_transfer_bgx ...")
    try:
        addr = str(args['addr'])
        from_group = str(args['from_group'])
        to_group = str(args['to_group'])
        send_tokens = float(args['num_bgt'])
    except KeyError:
        msg = "_do_transfer_bgx not all args"
        LOGGER.debug(msg)
        raise InternalError(msg)

    updated = {k: v for k, v in state.items()}

    if from_group == to_group:
        LOGGER.debug("Internal transfer of tokens - bad args")
        return updated

    from_group_code = BGXCrypto.get_string_hash(from_group)
    to_group_code = BGXCrypto.get_string_hash(to_group)
    digital_signature = BGXCrypto.get_validator_signature()
    validators_addr = digital_signature.get_verifying_key()

    if validators_addr == addr:
        LOGGER.debug("Internal transfer of tokens - it is better to use new init (CANCEL)")
        return updated

    if addr not in state or validators_addr not in state:
        LOGGER.debug("Internal transfer of tokens - bad addresses")
        return updated

    user_tokens = StorageCell(state[addr])
    validator_tokens = StorageCell(state[validators_addr])

    from_user_token = user_tokens.get_token(from_group_code)
    from_validator_token = validator_tokens.get_token(to_group_code)

    if from_user_token is None or from_validator_token is None:
        LOGGER.debug("Internal transfer of tokens - participants have no proper tokens")
        return updated

    to_user_token = user_tokens.get_token(to_group_code, strictly=True)
    to_user_token.copy_identity(from_validator_token)

    to_validator_token = validator_tokens.get_token(from_group_code, strictly=True)
    to_validator_token.copy_identity(from_user_token)

    global_meta_tokens = StorageCell()
    if SMART_BGT_META_ADDRESS in state:
        global_meta_tokens_json = state[SMART_BGT_META_ADDRESS]
        global_meta_tokens.from_json(global_meta_tokens_json)

    from_meta_token = global_meta_tokens.get_meta_token(from_group_code)
    to_meta_token = global_meta_tokens.get_meta_token(to_group_code)

    if from_meta_token is None or to_meta_token is None:
        LOGGER.debug("_do_transfer_bgx: bad token types")
        return updated

    from_price = from_meta_token.get_internal_token_price()
    to_price = to_meta_token.get_internal_token_price()

    if from_price <= 0 or to_price <=0:
        LOGGER.debug("_do_transfer_bgx: bad token price was used")
        return updated

    get_tokens = from_price * send_tokens / to_price
    first_step_result = from_user_token.get_amount(send_tokens)
    to_user_token.add_amount(get_tokens)
    second_step_result = from_validator_token.get_amount(get_tokens)
    to_validator_token.add_amount(send_tokens)

    if not first_step_result:
        LOGGER.debug("Internal transfer - user has not enough tokens")
    elif not second_step_result:
        LOGGER.debug("Internal transfer - validator has not enough tokens")
    else:
        user_tokens.append()
        user_tokens.append()
        updated[addr] = str(user_tokens.to_json())

        validator_tokens.append()
        validator_tokens.append()
        updated[validators_addr] = str(validator_tokens.to_json())

    LOGGER.debug("Internal transfer - ready! updated=%s", updated)
    return updated

# allow <spender_addr> to withdraw tokens <group_id> from account <user_addr>
def _do_approve(args, state):
    LOGGER.debug("_do_approve ...")
    try:
        owner_addr = str(args['user_addr'])
        spender_addr = str(args['spender_addr'])
        value = float(args['value'])
        group_id = str(args['group_id'])
    except KeyError:
        msg = "_do_approve not all args"
        LOGGER.debug(msg)
        raise InternalError(msg)

    updated = {k: v for k, v in state.items()}
    group_code = BGXCrypto.get_string_hash(group_id)

    if owner_addr == spender_addr:
        LOGGER.debug("_do_approve - owner addr = spender addr (CANCEL)")
        return updated

    allowance_storage = StorageCell()
    if ALLOWANCE_INFO in state:
        allowance_storage_json = state[ALLOWANCE_INFO]
        allowance_storage.from_json(allowance_storage_json)

    spender_allowance = allowance_storage.get(spender_addr)
    spender_allowance.append(owner_addr, group_code, value)
    allowance_storage.append(spender_allowance)
    updated[ALLOWANCE_INFO] = str(allowance_storage.to_json())
    return updated

# returns the amount of tokens <group_id> which <spender_addr> is still allowed to withdraw from <user_addr>
def _do_allowance(args, state):
    LOGGER.debug("_do_allowance ...")
    try:
        owner_addr = str(args['user_addr'])
        spender_addr = str(args['spender_addr'])
        group_id = str(args['group_id'])
    except KeyError:
        msg = "_do_allowance not all arg"
        LOGGER.debug(msg)
        raise InternalError(msg)

    group_code = BGXCrypto.get_string_hash(group_id)

    if owner_addr == spender_addr:
        LOGGER.debug("_do_allowance - owner addr = spender addr (CANCEL)")
        return state

    allowance_storage = StorageCell()
    if ALLOWANCE_INFO in state:
        allowance_storage_json = state[ALLOWANCE_INFO]
        allowance_storage.from_json(allowance_storage_json)

    spender_allowance = allowance_storage.get(spender_addr)
    amount = spender_allowance.get(owner_addr, group_code)
    LOGGER.debug("_do_allowance - approved amount = %s", str(amount))
    return state

# returns balance of <addr>
def _get_balance_of(args, state):
    LOGGER.debug("_get_balance_of ...")
    try:
        addr = str(args['addr'])
    except KeyError:
        msg = "_get_balance_of not all arg"
        LOGGER.debug(msg)
        raise InternalError(msg)

    if addr not in state:
        LOGGER.debug("_get_balance_of - address %s not registered", addr)
        return state

    wallet_str = state[addr]
    wallet = BGXwallet()
    wallet.fromJSON(wallet_str)
    balance = wallet.get_balance()
    LOGGER.debug("_get_balance_of - address %s balance = %s", addr, str(balance))
    return state

# returns total supply of tokens <token_name>
def _get_total_supply(args, state):
    LOGGER.debug("_get_total_supply ...")
    try:
        addr = str(args['token_name'])
    except KeyError:
        msg = "_get_total_supply not all arg"
        LOGGER.debug(msg)
        raise InternalError(msg)

    if addr not in state:
        LOGGER.debug("_get_total_supply - metatoken %s not registered", addr)
        return state

    meta_token_str = state[addr]
    meta_token = MetaToken()
    meta_token.fromJSON(meta_token_str)
    balance = meta_token.get_total_supply()
    LOGGER.debug("_get_total_supply - total supply of %s = %s", addr, str(balance))
    return state
