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

from sawtooth_signing.secp256k1 import Secp256k1PrivateKey, Secp256k1PublicKey, Secp256k1Context
from sawtooth_signing import ParseError

import binascii
import hashlib
import logging
import os


LOGGER = logging.getLogger(__name__)


# Namespace for cryptofunction
# Currently we use sawtooth_signing.secp256k1 as basis

class BGXCrypto:

    @classmethod
    def get_string_hash(cls, str):
        return hashlib.sha256(str.encode('utf-8')).hexdigest()

    @classmethod
    def get_integer_hash(cls, str):
        return int(BGXCrypto.get_string_hash(str), 16)

    class DigitalSignature:

        def __init__(self, str_signing_key=None):
            if str_signing_key is None:
                LOGGER.debug('DigitalSignature - generate new keys..')
                self._context = Secp256k1Context()
                self._signing_key = self._context.new_random_private_key()
                self._verifying_key = self._context.get_public_key(self._signing_key)
            else:
                LOGGER.debug('DigitalSignature - load key from string %s', str_signing_key)
                self._context = Secp256k1Context()
                hexed_string = str_signing_key.encode()
                self._signing_key = Secp256k1PrivateKey.from_hex(hexed_string)
                self._verifying_key = self._context.get_public_key(self._signing_key)

        def sign(self, message):
            return self._context.sign(str(message).encode('utf-8'), self._signing_key)

        def verify(self, sign, message):
            return self._context.verify(sign, str(message).encode('utf-8'), self._verifying_key)

        def get_verifying_key(self):
            return str(self._verifying_key.as_hex())

        def get_signing_key(self):
            return str(self._signing_key.as_hex())

    # return signature of validator node
    @classmethod
    def get_validator_signature(cls):
        path_to_sawtooth = os.getenv("SAWTOOTH_HOME")
        if path_to_sawtooth is None:
            path_to_keys = '/etc/sawtooth/keys/validator.priv'
        else:
            path_to_keys = path_to_sawtooth + '/keys/validator.priv'

        try:
            with open(path_to_keys, 'r') as infile:
                signing_key = infile.read().strip()
            digital_signature = BGXCrypto.DigitalSignature(signing_key)
        except ParseError as pe:
            raise InternalError(str(pe))
        except IOError as ioe:
            LOGGER.debug(str(ioe))
            digital_signature = BGXCrypto.DigitalSignature()
        return digital_signature

