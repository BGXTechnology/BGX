
 # Copyright 2018 NTRlab (https://ntrlab.ru)
 #
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 #      http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 #
 # Author: Mikhail Kashchenko


import time
import services
import inspect


# Prototype for a Token class.
# Note: must be JSON-serializable

class Token:

    def __init__(self, name, symbol, company_id, group_code, unique, digital_signature, seed = 0):
        services.BGXlog.logInfo('Producing token')
        self.name = name
        self.symbol = symbol
        self.company_id = company_id
        self.group_code = group_code
        self.granularity = 1
        self.decimals = 18
        self.unique = unique

        if not isinstance(digital_signature, services.BGXCrypto.DigitalSignature):
            services.BGXlog.logError('Wrong digital signature class as input in token creation')
            # raise something
            return False

        imprint = name + symbol + company_id + group_code

        if self.unique:
            self.TokenId = services.BGXCrypto.intHash(imprint + str(seed))

        self.sign = digital_signature.sign(self.getImprint())

    def __str__(self):
        return self.toJSON()

    def verifyToken(self, digital_signature):
        return digital_signature.verify(self.sign, self.getImprint())

    def getSign(self):
        return self.sign

    def getImprint(self):
        imprint = self.name + self.symbol + self.company_id + self.group_code
        if self.unique:
            imprint += str(self.TokenId)
        return imprint

    # TODO: implement

    #def toJSON(self):
        #return json.dumps(self.__dict__)

    # TODO: implement

    def fromJSON(self, data):
        return True


# Prototype for a EmissionMechanism class
# without check of ethereum account

class EmissionMechanism:

    # TODO: implement

    def checkEthereum():
        return True

    # TODO: implement

    def getProvedHashOfClass():
        return True

    # TODO: implement

    def checkHashOfClass():
        lines = inspect.getsource(EmissionMechanism)
        hash = services.BGXCrypto.intHash(lines)
        return True

    def releaseTokens(name, symbol, company_id, unique, signing_key, tokens_amount):
        services.BGXlog.logInfo('Emission in progress')
        seed = str(time.time())
        imprint = name + symbol + company_id + seed
        group_code = str(services.BGXCrypto.intHash(imprint))

        if not EmissionMechanism.checkEthereum():
            services.BGXlog.logError('Fail! Not enough tokens: ' + company_id)
            # raise something
            return False

        tokens = []
        for tokenNumber in range(tokens_amount):
            token = Token(name, symbol, company_id, group_code, unique, signing_key, tokenNumber)
            tokens.append(token)

        return tokens

