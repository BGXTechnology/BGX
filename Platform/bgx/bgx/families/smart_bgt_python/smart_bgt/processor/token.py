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

from smart_bgt.processor.crypto import BGXCrypto
import math
import json
import logging

from smart_bgt.processor.utils  import SMART_BGT_CREATOR_KEY,TRANSFER_FEE
from sawtooth_sdk.processor.exceptions import InternalError

LOGGER = logging.getLogger(__name__)

BASIC_DECIMALS = 18

# Prototype for a MetaToken class. Contains general information
# about tokens with specific <group_code>.

class MetaToken:

    def __init__(self, name=None, symbol=None, company_id=None, group_code=None, total_supply=0, description=None, \
                 internal_token_price=1, digital_signature=None, json_string=None):

        if json_string is not None:
            self.from_json(json_string)
        else:
            if not self.__check_values(total_supply, internal_token_price, digital_signature):
                LOGGER.error("Init metatoken - wrong args")
                raise InternalError('Failed to init metatoken')

            self.name = name
            self.symbol = symbol
            self.company_id = company_id
            self.group_code = group_code
            self.total_supply = total_supply
            self.granularity = 1
            self.decimals = BASIC_DECIMALS
            self.description = description
            self.currency_code = 1
            self.internal_token_price = internal_token_price
            self.bgx_conversion = False
            self.internal_conversion = False
            self.ethereum_conversion = False
            self.owner_key = digital_signature.get_verifying_key()

    # checks parameters values
    def __check_values(self, total_supply, internal_token_price, digital_signature = None):

        if not isinstance(total_supply, int) or total_supply < 0:
            LOGGER.debug('Bad integer : total_supply')
            return False

        if not isinstance(internal_token_price, int) or internal_token_price <= 0:
            LOGGER.debug('Bad integer : internal_token_price')
            return False

        if digital_signature is not None and not isinstance(digital_signature, BGXCrypto.DigitalSignature):
            LOGGER.debug('Bad digital signature')
            return False
        return True

    def to_json(self):
        data = {'name': self.name, 'symbol': self.symbol, 'company_id': self.company_id, 'group_code': self.group_code,\
                'total_supply': str(self.total_supply), 'granularity': str(self.granularity), \
                'decimals': str(self.decimals), 'description': self.description, 'currency_code': \
                str(self.currency_code), 'internal_token_price': str(self.internal_token_price), 'bgx_conversion': \
                str(self.bgx_conversion), 'internal_conversion': str(self.internal_conversion), \
                'ethereum_conversion': str(self.ethereum_conversion),  SMART_BGT_CREATOR_KEY: self.owner_key}
        return json.dumps(data)

    def from_json(self, json_string):
        try:
            data = json.loads(json_string)
        except:
            LOGGER.error('Cant read json with metatoken: %s', sys.exc_info()[0])
            raise InternalError('Failed to load metatoken')

        try:
            name = data['name']
            symbol = data['symbol']
            company_id = data['company_id']
            group_code = data['group_code']
            total_supply = int(data['total_supply'])
            granularity = int(data['granularity'])
            decimals = int(data['decimals'])
            description = data['description']
            currency_code = int(data['currency_code'])
            internal_token_price = int(data['internal_token_price'])
            bgx_conversion = data['bgx_conversion']
            internal_conversion = data['internal_conversion']
            ethereum_conversion = data['ethereum_conversion']
            owner_key = data[SMART_BGT_CREATOR_KEY]
        except KeyError:
            LOGGER.error("json with metatoken has not all arg")
            raise InternalError('Failed to load metatoken')

        if not self.__check_values(total_supply, internal_token_price):
            LOGGER.error("Init metatoken - wrong args")
            raise InternalError('Failed to init metatoken')

        self.name = name
        self.symbol = symbol
        self.company_id = company_id
        self.group_code = group_code
        self.total_supply = total_supply
        self.granularity = granularity
        self.decimals = decimals
        self.description = description
        self.currency_code = currency_code
        self.internal_token_price = internal_token_price
        self.bgx_conversion = bgx_conversion
        self.internal_conversion = internal_conversion
        self.ethereum_conversion = ethereum_conversion
        self.owner_key = owner_key

    def get_total_supply(self):
        return self.total_supply

    def get_internal_token_price(self):
        return self.internal_token_price

    def get_owner_key(self):
        return self.owner_key

    def get_group_code(self):
        return self.group_code

    def add(self, amount):
        if (not isinstance(amount, float) and not isinstance(amount, int)) or amount <= 0 or \
                pow(10, BASIC_DECIMALS) * amount < 1:
            LOGGER.debug("Add extra tokens - wrong args")
            return False

        self.total_supply += amount
        return True

    def get_id(self):
        return self.group_code

# Prototype for a Token class.
# Note: must be JSON-serializable

class Token:

    def __init__(self, group_code=None, balance=0, digital_signature=None, granularity=1, decimals=18, \
                 json_string=None):

        if json_string is not None:
            self.from_json(json_string)
        else:
            if group_code is None:
                self.active_flag = False
                self.group_code = 'None'
                self.balance = 0
                self.granularity = granularity
                self.decimals = decimals
                self.owner_key = 'None'
                self.sign = 'None'
            else:
                if not self.__check_values(balance, granularity, decimals, digital_signature):
                    LOGGER.error("Init token - wrong args")
                    raise InternalError('Failed to init token')

                self.active_flag = True
                self.group_code = str(group_code)
                self.balance = balance
                self.granularity = granularity
                self.decimals = decimals
                self.owner_key = str(digital_signature.get_verifying_key())
                self.sign = str(digital_signature.sign(self.get_imprint()))

    def __str__(self):
        return self.get_imprint()

    def __eq__(self, other):
        return self.get_imprint() == other.get_imprint()

    def get_id(self):
        return self.group_code

    def copy_identity(self, token):
        self.active_flag = True
        self.group_code = token.get_id()
        #self.owner_key = owner_key

    # checks parameters values
    def __check_values(self, balance, granularity, decimals, digital_signature=None):

        if not isinstance(balance, int) or balance < 0:
            LOGGER.debug('Bad integer : balance')
            return False

        if not isinstance(granularity, int) or granularity < 0:
            LOGGER.debug('Bad integer : granularity')
            return False

        if not isinstance(decimals, int) or decimals < 0:
            LOGGER.debug('Bad integer : decimals')
            return False

        if digital_signature is not None and not isinstance(digital_signature, BGXCrypto.DigitalSignature):
            LOGGER.debug('Bad digital signature')
            return False
        return True

    # checks sign of current token
    def verify_token(self, digital_signature):
        return digital_signature.verify(self.sign, self.get_imprint())

    # returns unique general information about token 
    def get_imprint(self):
        imprint = self.group_code + str(self.balance) + str(self.granularity) + \
                  str(self.decimals) + self.owner_key
        return imprint

    def to_json(self):
        data = {'group_code': str(self.group_code), 'granularity': str(self.granularity), 'balance': str(self.balance),\
                'decimals': str(self.decimals), 'owner_key': str(self.owner_key), 'sign': str(self.sign)}
        return json.dumps(data)

    def from_json(self, json_string):
        try:
            data = json.loads(json_string)
        except:
            LOGGER.error('Cant read json with token: %s', sys.exc_info()[0])
            raise InternalError('Failed to load token')

        try:
            group_code = data['group_code']
            balance = int(data['balance'])
            granularity = int(data['granularity'])
            decimals = int(data['decimals'])
            owner_key = data['owner_key']
            sign = data['sign']
        except KeyError:
            LOGGER.error("json with token has not all arg")
            raise InternalError('Failed to load token')

        if not self.__check_values(balance, granularity, decimals):
            LOGGER.error("Loading token from JSON - wrong args")
            raise InternalError('Failed to load token')

        self.active_flag = True
        self.group_code = group_code
        self.balance = balance
        self.granularity = granularity
        self.decimals = decimals
        self.owner_key = owner_key
        self.sign = sign

    def getSign(self):
        return self.sign

    def getOwnerKey(self):
        return self.owner_key

    def get_balance(self):
        return self.balance

    def get_amount(self):
        return self.balance * pow(10, self.decimals - BASIC_DECIMALS)

    def __set_balance(self, balance):
        self.balance = balance

    def get_decimals(self):
        return self.decimals

    def __set_decimals(self, decimals):
        self.decimals = decimals

    # converts int value to (decimals, amount) pair
    def __int_to_iternal_format(self, amount):
        if amount <= 0:
            return BASIC_DECIMALS, 0

        decimals = 0
        flag = amount / 10
        while int(flag) == flag:
            amount /= 10
            flag /= 10
            decimals += 1
        return decimals, int(amount)

    # removes amount from current token
    def get_amount(self, amount=0):
        if (not isinstance(amount, float) and not isinstance(amount, int)) or amount <= 0 or pow(10, BASIC_DECIMALS) * \
                amount < 1:
            LOGGER.debug("Get amount from token - wrong args")
            return False

        token_decimals = self.get_decimals()
        token_balance = self.get_balance()

        token_amount = token_balance * pow(10, token_decimals)
        transfer_amount = int(amount * pow(10, BASIC_DECIMALS))

        if token_amount < transfer_amount:
            LOGGER.debug("Get amount from token - not enough money")
            return False

        token_amount -= transfer_amount

        token_decimals, token_balance = self.__int_to_iternal_format(token_amount)
        self.__set_decimals(token_decimals)
        self.__set_balance(token_balance)
        return True

    # adds amount to current token
    def add_amount(self, amount=0):
        if (not isinstance(amount, float) and not isinstance(amount, int)) or amount <= 0 or pow(10, BASIC_DECIMALS) * \
                amount < 1:
            LOGGER.debug("Add amount to token - wrong args")
            return False

        token_decimals = self.get_decimals()
        token_balance = self.get_balance()

        token_amount = token_balance * pow(10, token_decimals)
        transfer_amount = int(amount * pow(10, BASIC_DECIMALS))
        token_amount += transfer_amount

        token_decimals, token_balance = self.__int_to_iternal_format(token_amount)
        self.__set_decimals(token_decimals)
        self.__set_balance(token_balance)
        return True

