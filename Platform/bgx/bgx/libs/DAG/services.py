
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


import logging
import hashlib
import ecdsa


# Namespace for logger function

class BGXlog:

    def logInfo(str):
        logging.info(str)

    def logError(str):
        logging.error(str)


# Namespace for cryptofunction

class BGXCrypto:

    def strHash(str):
        return hashlib.sha256(str.encode('utf-8')).hexdigest()

    def intHash(str):
        return int(BGXCrypto.strHash(str), 16)

    class DigitalSignature:

        def __init__(self, verifying_key=None):
            if verifying_key is None:
                self._signing_key = ecdsa.SigningKey.generate(curve=ecdsa.SECP256k1)
                self._verifying_key = self._signing_key.get_verifying_key()
            else:
                self._signing_key = None
                self._verifying_key = verifying_key

        def sign(self, message):
            if self._signing_key is None:
                BGXlog.logError('Fail! Can not sign this')
                # raise something
                return False
            return self._signing_key.sign(str(message).encode('utf-8'))

        def verify(self, sign, message):
            return self._verifying_key.verify(sign, str(message).encode('utf-8'))

        def getVerifyingKey(self):
            return self._verifying_key

# Namespace for general configuration

class BGXConf:

    DEFAULT_STORAGE_PATH = './'
    MAX_RETRY_CREATE_DB = 10

