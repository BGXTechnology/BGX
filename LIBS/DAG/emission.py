
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


import ecdsa
import json
import time
import services


# Prototype for a Token class.
# Note: must be JSON-serializable

class Token:

    def __init__(self, name, symbol, companyID, groupCode, unique, signingKey, seed = 0):
        services.BGXlog.logInfo('Producing token')
        self.name = name
        self.symbol = symbol
        self.companyID = companyID
        self.GroupCode = groupCode
        self.granularity = 1
        self.decimals = 18
        self.unique = unique

        imprint = name + symbol + companyID + groupCode

        if self.unique == True:
            self.TokenId = services.BGXCrypto.intHash(imprint + str(seed))
            imprint += str(self.TokenId)

        self.imprint = imprint
        self.sign = str(signingKey.sign(imprint.encode('utf-8')))

    def __str__(self):
        return self.toJSON()

    def getSign(self):
        return self.sign

    def getImprint(self):
        return self.imprint

    def toJSON(self):
        return json.dumps(self.__dict__)

    # TODO: реализовать

    def fromJSON(self, data):
        return True


# Prototype for a EmissionMechanism class
# without check of ethereum account

class EmissionMechanism:

    # TODO: реализовать

    def checkEthereum():
        return True

    def releaseTokens(name, symbol, companyID, unique, signingKey, tokensAmount):
        services.BGXlog.logInfo('Emission in progress')
        seed = str(time.time())
        imprint = name + symbol + companyID + seed
        groupCode = str(services.BGXCrypto.intHash(imprint))

        if not EmissionMechanism.checkEthereum():
            services.BGXlog.logError('Fail! Not enough tokens: ' + companyID)
            # raise something
            return False

        tokens = []
        for tokenNumber in range(tokensAmount):
            token = Token(name, symbol, companyID, groupCode, unique, signingKey, tokenNumber)
            tokens.append(token)

        return tokens




print('-----------------------------------------------------------------------')
sk = ecdsa.SigningKey.generate(curve=ecdsa.SECP256k1)
vk = sk.get_verifying_key()
t = Token("BGX Token", "BGT", "id", "code", 1, sk)
tokens = EmissionMechanism.releaseTokens("BGX Token", "BGT", "id", 1, sk, 22)

for token in tokens:
    print(token)
print('-----------------------------------------------------------------------')

