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

import time
#import services
#import inspect
import logging

from smart_bgt.processor.services import BGXlistener
from smart_bgt.processor.crypto import BGXCrypto
from smart_bgt.processor.token import Token, MetaToken


LOGGER = logging.getLogger(__name__)


# Prototype for a EmissionMechanism class
# without check of ethereum account

class EmissionMechanism:

    # checks possibility of new emission for node with <wallet_address>
    @classmethod
    def check_ethereum(cls, bgt_amount, wallet_address, bgt_price, dec_price):
        dec_amount = BGXlistener.balanceOf(wallet_address)
        return int(bgt_amount) * bgt_price <= dec_amount * dec_price

    # Methods for a control of executable code
    # TODO: implement
    #def get_proved_hash_of_class(self):
        #return True

    # TODO: implement
    #def check_hash_of_class(self):
        #lines = inspect.getsource(EmissionMechanism)
        #hash = BGXCrypto.intHash(lines)
        #return True

    @classmethod
    def release_tokens(cls, name, symbol, company_id, ethereum_address, num_bgt, description, \
                      bgt_price = 1, dec_price = 1):

        if not EmissionMechanism.check_ethereum(num_bgt, ethereum_address, bgt_price, dec_price):
            return None, None

        # TODO: use specific complex imprint
        imprint = name
        group_code = BGXCrypto.get_string_hash(imprint)
        digital_signature = BGXCrypto.get_validator_signature()

        meta = MetaToken(name, symbol, company_id, group_code, num_bgt, description, bgt_price, digital_signature)
        token = Token(group_code, num_bgt, digital_signature)
        return token, meta

    # additioanl emission (if MetaToken already exists) 
    @classmethod
    def release_extra_tokens(cls, token, meta_token, ethereum_address, num_bgt, bgt_price, dec_price):

        if not isinstance(meta_token, MetaToken):
            return None, None

        if not EmissionMechanism.check_ethereum(num_bgt, ethereum_address, bgt_price, dec_price):
            return None, None

        digital_signature = BGXCrypto.get_validator_signature()

        if not isinstance(token, Token):
            group_code = meta_token.get_group_code()
            token = Token(group_code, num_bgt, digital_signature)
        else:
            token.add(num_bgt)

        meta_token.add(num_bgt)
        return token, meta_token
