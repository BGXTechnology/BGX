# Copyright 2016, 2017 Intel Corporation
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

import asyncio
import re
import logging
import json
import base64
from aiohttp import web

# pylint: disable=no-name-in-module,import-error
# needed for the google.protobuf imports to pass pylint
from google.protobuf.json_format import MessageToDict
from google.protobuf.message import DecodeError

from sawtooth_rest_api.protobuf.validator_pb2 import Message

import sawtooth_rest_api.exceptions as errors
from sawtooth_rest_api import error_handlers
from sawtooth_rest_api.messaging import DisconnectError
from sawtooth_rest_api.messaging import SendBackoffTimeoutError
from sawtooth_rest_api.protobuf import client_transaction_pb2
from sawtooth_rest_api.protobuf import client_list_control_pb2
from sawtooth_rest_api.protobuf import client_batch_submit_pb2
from sawtooth_rest_api.protobuf import client_state_pb2
from sawtooth_rest_api.protobuf import client_block_pb2
from sawtooth_rest_api.protobuf import client_batch_pb2
from sawtooth_rest_api.protobuf import client_receipt_pb2
from sawtooth_rest_api.protobuf import client_peers_pb2
from sawtooth_rest_api.protobuf import client_status_pb2
from sawtooth_rest_api.protobuf.block_pb2 import BlockHeader
from sawtooth_rest_api.protobuf.batch_pb2 import BatchList
from sawtooth_rest_api.protobuf.batch_pb2 import BatchHeader
from sawtooth_rest_api.protobuf.transaction_pb2 import TransactionHeader

from datetime import datetime
import chilkat
# pylint: disable=too-many-lines

DEFAULT_TIMEOUT = 300
LOGGER = logging.getLogger(__name__)

# BGX
# Transaction scheme
# TRANSACTION_SCHEME = {
#     'timestamp': time
#     'status': true/false
#     'tx_payload': number
#     'currency': string ('dec', 'bgt')
#     'key_from': base64 encoded string public key
#     'key_to': base64 encoded string of public key
#     'extra': string
# }

# Unlock chilkat library
chilkat.CkGlobal().UnlockBundle("Anything for 30-day trial")

NODE_PUBLIC_KEY = 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABAECfXHrRBEo50pR/r88eBXlfREDUU95tAubx7JoDtXA+wvU+xtFLSRfceUd0JWgjOeysS/zq8ktlUwk7jxNBdY='

USERS_KEYS = [
    {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCAaLUY/S9zYzSc0RxxPd3Aj47sZkQoTjo5rst2U4Ylw+aAKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABNOT8Oz31n93/eGsooJvZhnwwQr7tqRYOdotxJ9Ku9Fsq/OgK/pVKILjsuVRy9pJL525ns7/0k/ephS4nlkS3xw='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCBrl/omw6xWvjvmqocvAIuGMl78UBci3Hl7e7MOwj6+rqAKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABB9mxRPAFV0Uf5ZCRmkhDgf9IF+9MX5NW3JaOjc1fqmUJflVSA301OkjJsRmRPg/627514JK0nmdB20kFT/7y1g='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCBivZHDbEgJzHt4ZkjNbdExzLQAbDAA/ieCmnD3MfSM96AKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABNPXzQdoyc/BqtDx8aOZAeDNaIPDwXhBEpCe1ysOT7Cc4Fo+fG0DTcJRTJz9xiJ0TsKuGJockHMOzNQOm8nlyl0='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCAq/0z6PUQEY7COfo+UPH2SX8mzvg0E547NxS+LrwnTn6AKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABFyxmP00ixe/LWqdP08mzaLk81kV++0+Ympm2aRW4WBkD6Nkp7JLAHTXlEY4zJm3xnBXwS5ShafWGIq1qK6smLA='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCAQE/3N40yAGQNHAqWD686CVnTyYYDHlhd3W8xcqLhWH6AKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABM7aeFWclnSQ8XIepC+59guIp7yPiRMoQT92E9p6aYHLY0MQ3AQj4p7VLZfAdv/Ag+N9t009sDK0cIEAZQ2rW8Q='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCBsqGWFDGSpRsa29nb8YksP7I/+Lbt9AvJvzviyb/mSn6AKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABMSFylbwbIk0eBB0hpnn22kSuFjJn38AlYDHBvymY/LIeTu4iFbB0uJh/iyf3jX9Oc6/llm8s8GcMFxH0SewCMQ='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCA1v095NsknXVtckrQKfbRITrQFFQTYz4lDi5op5Mm5UKAKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABDV3sRXm1io8gshJWATV8lDM0KJRg0tRyvFJACtcMpqsrn5I1G7SS0uJ1NQ6orJI2NPzsEJdyyz7ogzs5p4UVnI='
    }
]
USERS_PUBLIC_KEYS = [keys['public_key'] for keys in USERS_KEYS]
USERS_PRIVATE_KEYS = [keys['private_key'] for keys in USERS_KEYS]

USERS_ETHER_KEYS = [
    {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCBJ654i4eRACJT7K+Ey5FEGX8fQ/cTBvWc4xEpO6HdytaAKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABAGIektFnctcrKcJGWbHDX3NkL5PItfTTUkvY3VRbTAHFcBYx7uydzGmbmE5y4QDWNd3zuz1sd05i5SUZ58tTyw='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCCcavJYmrupMiuriWcCZ+kAmXHNKChyffuetYEcyVEZSqAKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABIW7SD5V0XummRyb3Jn5PH4eBnyFWlqUD/JNeH51jKAlMwLvSaqDw70MjxRqrVy+y3+4d4jUCl76YK8jWMXmdAg='
    }, {
      'private_key': 'ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCD4JFqnl3uZjG23UD++4tscN0s41q6C+CjcOWRete3376AKBggqhkjOPQMBBw==',
      'public_key': 'MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABO0pWlAs2ohboeZNLcYVXo86i0b3FJckG7C4MoGGxSyrm2wxPMPv6gGDJKANbPqK8Ox9HUvJ7JHNsJ77kJNZR3U='
    }
]
USERS_ETHER_PUBLIC_KEYS = [keys['public_key'] for keys in USERS_ETHER_KEYS]
USERS_ETHER_PRIVATE_KEYS = [keys['private_key'] for keys in USERS_ETHER_KEYS]

class crypt_SHA256_base64url():
    def __init__(self):
        self.crypt = chilkat.CkCrypt2()
        self.crypt.put_HashAlgorithm("SHA256")
        self.crypt.put_Charset("utf-8")
        self.crypt.put_EncodingMode("base64url")

    def get_key_hashed(self, public_key):
        return self.crypt.hashStringENC(public_key)

crypt_SHA256_base64url = crypt_SHA256_base64url()
KEYS_HASH_TABLE = {}
for key in USERS_PUBLIC_KEYS + USERS_ETHER_PUBLIC_KEYS:
    KEYS_HASH_TABLE[crypt_SHA256_base64url.get_key_hashed(key)] = key


### generate 10 keys pairs
# keys = []
# fortuna = chilkat.CkPrng()
# entropy = fortuna.getEntropy(32,"base64")
# fortuna.AddEntropy(entropy,"base64")
# ecc = chilkat.CkEcc()
# for i in range(10):
#     privKey = ecc.GenEccKey("secp256r1", fortuna)
#     pubKey = privKey.GetPublicKey()
#     keys.append({
#         'private_key': privKey.getPkcs8ENC('base64'),
#         'public_key': pubKey.getPkcs8ENC('base64')
#     })
#
### private key from string
# b = chilkat.CkByteData()
# b.appendEncoded(privateKeyStr, 'base64')
# v = chilkat.CkPrivateKey()
# v.LoadPkcs8(b)
#
### public key from string
# v = chilkat.CkPublicKey()
# v.LoadFromString(pubKeyStr)
#
### sign with private key
# ecdsa = chilkat.CkEcc()
# crypt = chilkat.CkCrypt2()
# crypt.put_HashAlgorithm("SHA256")
# crypt.put_Charset("utf-8")
# crypt.put_EncodingMode("base64")
#
# #  Hash a payload_from string
# sha256_hash = crypt.hashStringENC(json.dumps({
#     "public_key": "HKJEk_ov0wgRsmi6_o8pL9pSEBbIHvgmLGto7LaT3fM",
#     "tx_payload": 10,
#     "coin_code": "dec"
# }, sort_keys=True))
# prng = chilkat.CkPrng()
# ecdsaSigBase64 = ecdsa.signHashENC(sha256_hash,"base64",privKey,prng)
###

GLOBAL_STATE = {
    'wallets': {
        # node's wallet works in bgt and dec(ether)
        NODE_PUBLIC_KEY: {
            'dec': 100,
            'bgt': 100000
        },
        # users' wallets
        USERS_ETHER_PUBLIC_KEYS[0]: {'dec': 20},
        USERS_ETHER_PUBLIC_KEYS[1]: {'dec': 50},
        USERS_ETHER_PUBLIC_KEYS[2]: {'dec': 80},
        USERS_PUBLIC_KEYS[0]: {'bgt': 100},
        USERS_PUBLIC_KEYS[1]: {'bgt': 200},
        USERS_PUBLIC_KEYS[2]: {'bgt': 300},
        USERS_PUBLIC_KEYS[3]: {'bgt': 400},
        USERS_PUBLIC_KEYS[4]: {'bgt': 500},
        USERS_PUBLIC_KEYS[5]: {'bgt': 600},
        USERS_PUBLIC_KEYS[6]: {'bgt': 700},
    },
    'transactions': [],
}

def append_transaction(key_from, key_to, tx_payload, currency, extra):
    if (not GLOBAL_STATE['wallets'][key_from][currency]) \
            or\
       (GLOBAL_STATE['wallets'][key_from][currency] < tx_payload):
        return False
    if key_to not in GLOBAL_STATE['wallets']:
        GLOBAL_STATE['wallets'][key_to] = {}
    if currency not in GLOBAL_STATE['wallets'][key_to]:
        GLOBAL_STATE['wallets'][key_to][currency] = 0
    GLOBAL_STATE['wallets'][key_from][currency] -= tx_payload
    GLOBAL_STATE['wallets'][key_to][currency] += tx_payload
    GLOBAL_STATE['transactions'].append({
        'timestamp': datetime.now().__str__(),
        'status': True,
        'tx_payload': tx_payload,
        'currency': currency,
        'key_from': key_from,
        'key_to': key_to,
        'extra': extra
    })
    return True


class CounterWrapper():
    def __init__(self, counter=None):
        self._counter = counter

    def inc(self):
        if self._counter:
            self._counter.inc()


class NoopTimerContext():
    def __enter__(self):
        pass

    def __exit__(self, exception_type, exception_value, traceback):
        pass

    def stop(self):
        pass


class TimerWrapper():
    def __init__(self, timer=None):
        self._timer = timer
        self._noop = NoopTimerContext()

    def time(self):
        if self._timer:
            return self._timer.time()
        return self._noop


class RouteHandler:
    """Contains a number of aiohttp handlers for endpoints in the Rest Api.

    Each handler takes an aiohttp Request object, and uses the data in
    that request to send Protobuf message to a validator. The Protobuf response
    is then parsed, and finally an aiohttp Response object is sent back
    to the client with JSON formatted data and metadata.

    If something goes wrong, an aiohttp HTTP exception is raised or returned
    instead.

    Args:
        connection (:obj: messaging.Connection): The object that communicates
            with the validator.
        timeout (int, optional): The time in seconds before the Api should
            cancel a request and report that the validator is unavailable.
    """

    def __init__(
            self, loop, connection,
            timeout=DEFAULT_TIMEOUT, metrics_registry=None):
        self._loop = loop
        self._connection = connection
        self._timeout = timeout
        if metrics_registry:
            self._post_batches_count = CounterWrapper(
                metrics_registry.counter('post_batches_count'))
            self._post_batches_error = CounterWrapper(
                metrics_registry.counter('post_batches_error'))
            self._post_batches_total_time = TimerWrapper(
                metrics_registry.timer('post_batches_total_time'))
            self._post_batches_validator_time = TimerWrapper(
                metrics_registry.timer('post_batches_validator_time'))
        else:
            self._post_batches_count = CounterWrapper()
            self._post_batches_error = CounterWrapper()
            self._post_batches_total_time = TimerWrapper()
            self._post_batches_validator_time = TimerWrapper()

    async def submit_batches(self, request):
        """Accepts a binary encoded BatchList and submits it to the validator.

        Request:
            body: octet-stream BatchList of one or more Batches
        Response:
            status:
                 - 202: Batches submitted and pending
            link: /batches or /batch_statuses link for submitted batches

        """
        timer_ctx = self._post_batches_total_time.time()
        self._post_batches_count.inc()

        # Parse request
        if request.headers['Content-Type'] != 'application/octet-stream':
            LOGGER.debug(
                'Submission headers had wrong Content-Type: %s',
                request.headers['Content-Type'])
            self._post_batches_error.inc()
            raise errors.SubmissionWrongContentType()

        body = await request.read()
        if not body:
            LOGGER.debug('Submission contained an empty body')
            self._post_batches_error.inc()
            raise errors.NoBatchesSubmitted()

        try:
            batch_list = BatchList()
            batch_list.ParseFromString(body)
        except DecodeError:
            LOGGER.debug('Submission body could not be decoded: %s', body)
            self._post_batches_error.inc()
            raise errors.BadProtobufSubmitted()

        # Query validator
        error_traps = [error_handlers.BatchInvalidTrap,
                       error_handlers.BatchQueueFullTrap]
        validator_query = client_batch_submit_pb2.ClientBatchSubmitRequest(
            batches=batch_list.batches)

        with self._post_batches_validator_time.time():
            await self._query_validator(
                Message.CLIENT_BATCH_SUBMIT_REQUEST,
                client_batch_submit_pb2.ClientBatchSubmitResponse,
                validator_query,
                error_traps)

        # Build response envelope
        id_string = ','.join(b.header_signature for b in batch_list.batches)

        status = 202
        link = self._build_url(request, path='/batch_statuses', id=id_string)

        retval = self._wrap_response(
            request,
            metadata={'link': link},
            status=status)

        timer_ctx.stop()
        return retval

    async def list_statuses(self, request):
        """Fetches the committed status of batches by either a POST or GET.

        Request:
            body: A JSON array of one or more id strings (if POST)
            query:
                - id: A comma separated list of up to 15 ids (if GET)
                - wait: Request should not return until all batches committed

        Response:
            data: A JSON object, with batch ids as keys, and statuses as values
            link: The /batch_statuses link queried (if GET)
        """
        error_traps = [error_handlers.StatusResponseMissing]

        # Parse batch ids from POST body, or query paramaters
        if request.method == 'POST':
            if request.headers['Content-Type'] != 'application/json':
                LOGGER.debug(
                    'Request headers had wrong Content-Type: %s',
                    request.headers['Content-Type'])
                raise errors.StatusWrongContentType()

            ids = await request.json()

            if (not ids
                    or not isinstance(ids, list)
                    or not all(isinstance(i, str) for i in ids)):
                LOGGER.debug('Request body was invalid: %s', ids)
                raise errors.StatusBodyInvalid()
            for i in ids:
                self._validate_id(i)

        else:
            ids = self._get_filter_ids(request)
            if not ids:
                LOGGER.debug('Request for statuses missing id query')
                raise errors.StatusIdQueryInvalid()

        # Query validator
        validator_query = \
            client_batch_submit_pb2.ClientBatchStatusRequest(
                batch_ids=ids)
        self._set_wait(request, validator_query)

        response = await self._query_validator(
            Message.CLIENT_BATCH_STATUS_REQUEST,
            client_batch_submit_pb2.ClientBatchStatusResponse,
            validator_query,
            error_traps)

        # Send response
        if request.method != 'POST':
            metadata = self._get_metadata(request, response)
        else:
            metadata = None

        data = self._drop_id_prefixes(
            self._drop_empty_props(response['batch_statuses']))

        return self._wrap_response(request, data=data, metadata=metadata)

    async def list_state(self, request):
        """Fetches list of data entries, optionally filtered by address prefix.

        Request:
            query:
                - head: The id of the block to use as the head of the chain
                - address: Return entries whose addresses begin with this
                prefix

        Response:
            data: An array of leaf objects with address and data keys
            head: The head used for this query (most recent if unspecified)
            link: The link to this exact query, including head block
            paging: Paging info and nav, like total resources and a next link
        """
        paging_controls = self._get_paging_controls(request)

        head, root = await self._head_to_root(request.url.query.get(
            'head', None))
        validator_query = client_state_pb2.ClientStateListRequest(
            state_root=root,
            address=request.url.query.get('address', None),
            sorting=self._get_sorting_message(request, "default"),
            paging=self._make_paging_message(paging_controls))

        response = await self._query_validator(
            Message.CLIENT_STATE_LIST_REQUEST,
            client_state_pb2.ClientStateListResponse,
            validator_query)

        return self._wrap_paginated_response(
            request=request,
            response=response,
            controls=paging_controls,
            data=response.get('entries', []),
            head=head)

    async def fetch_state(self, request):
        """Fetches data from a specific address in the validator's state tree.

        Request:
            query:
                - head: The id of the block to use as the head of the chain
                - address: The 70 character address of the data to be fetched

        Response:
            data: The base64 encoded binary data stored at that address
            head: The head used for this query (most recent if unspecified)
            link: The link to this exact query, including head block
        """
        error_traps = [
            error_handlers.InvalidAddressTrap,
            error_handlers.StateNotFoundTrap]

        address = request.match_info.get('address', '')
        head = request.url.query.get('head', None)

        head, root = await self._head_to_root(head)
        response = await self._query_validator(
            Message.CLIENT_STATE_GET_REQUEST,
            client_state_pb2.ClientStateGetResponse,
            client_state_pb2.ClientStateGetRequest(
                state_root=root, address=address),
            error_traps)

        return self._wrap_response(
            request,
            data=response['value'],
            metadata=self._get_metadata(request, response, head=head))

    async def list_blocks(self, request):
        """Fetches list of blocks from validator, optionally filtered by id.

        Request:
            query:
                - head: The id of the block to use as the head of the chain
                - id: Comma separated list of block ids to include in results

        Response:
            data: JSON array of fully expanded Block objects
            head: The head used for this query (most recent if unspecified)
            link: The link to this exact query, including head block
            paging: Paging info and nav, like total resources and a next link
        """
        paging_controls = self._get_paging_controls(request)
        validator_query = client_block_pb2.ClientBlockListRequest(
            head_id=self._get_head_id(request),
            block_ids=self._get_filter_ids(request),
            sorting=self._get_sorting_message(request, "block_num"),
            paging=self._make_paging_message(paging_controls))

        response = await self._query_validator(
            Message.CLIENT_BLOCK_LIST_REQUEST,
            client_block_pb2.ClientBlockListResponse,
            validator_query)

        return self._wrap_paginated_response(
            request=request,
            response=response,
            controls=paging_controls,
            data=[self._expand_block(b) for b in response['blocks']])

    async def fetch_block(self, request):
        """Fetches a specific block from the validator, specified by id.
        Request:
            path:
                - block_id: The 128-character id of the block to be fetched

        Response:
            data: A JSON object with the data from the fully expanded Block
            link: The link to this exact query
        """
        error_traps = [error_handlers.BlockNotFoundTrap]

        block_id = request.match_info.get('block_id', '')
        self._validate_id(block_id)

        response = await self._query_validator(
            Message.CLIENT_BLOCK_GET_BY_ID_REQUEST,
            client_block_pb2.ClientBlockGetResponse,
            client_block_pb2.ClientBlockGetByIdRequest(block_id=block_id),
            error_traps)

        return self._wrap_response(
            request,
            data=self._expand_block(response['block']),
            metadata=self._get_metadata(request, response))

    async def list_batches(self, request):
        """Fetches list of batches from validator, optionally filtered by id.

        Request:
            query:
                - head: The id of the block to use as the head of the chain
                - id: Comma separated list of batch ids to include in results

        Response:
            data: JSON array of fully expanded Batch objects
            head: The head used for this query (most recent if unspecified)
            link: The link to this exact query, including head block
            paging: Paging info and nav, like total resources and a next link
        """
        paging_controls = self._get_paging_controls(request)
        validator_query = client_batch_pb2.ClientBatchListRequest(
            head_id=self._get_head_id(request),
            batch_ids=self._get_filter_ids(request),
            sorting=self._get_sorting_message(request, "default"),
            paging=self._make_paging_message(paging_controls))

        response = await self._query_validator(
            Message.CLIENT_BATCH_LIST_REQUEST,
            client_batch_pb2.ClientBatchListResponse,
            validator_query)

        return self._wrap_paginated_response(
            request=request,
            response=response,
            controls=paging_controls,
            data=[self._expand_batch(b) for b in response['batches']])

    async def fetch_batch(self, request):
        """Fetches a specific batch from the validator, specified by id.

        Request:
            path:
                - batch_id: The 128-character id of the batch to be fetched

        Response:
            data: A JSON object with the data from the fully expanded Batch
            link: The link to this exact query
        """
        error_traps = [error_handlers.BatchNotFoundTrap]

        batch_id = request.match_info.get('batch_id', '')
        self._validate_id(batch_id)

        response = await self._query_validator(
            Message.CLIENT_BATCH_GET_REQUEST,
            client_batch_pb2.ClientBatchGetResponse,
            client_batch_pb2.ClientBatchGetRequest(batch_id=batch_id),
            error_traps)

        return self._wrap_response(
            request,
            data=self._expand_batch(response['batch']),
            metadata=self._get_metadata(request, response))

    # First iteration of implementation
    async def post_wallet(self, request):
        if 'public_key' not in request.headers:
            LOGGER.debug(
                'Submission header public_key is mandatory'
            )
            raise errors.NoMandatoryHeader()
        public_key = request.headers['public_key']
        public_key_hashed = request.match_info.get('public_key_hashed', '')

        if crypt_SHA256_base64url.get_key_hashed(public_key) != public_key_hashed:
            raise errors.InvalidPublicKey()

        if public_key in GLOBAL_STATE['wallets']:
            return self._wrap_response(
                request,
                metadata={
                    'wallet': {
                        public_key: GLOBAL_STATE['wallets'][public_key]
                    }
                },
                status=200)

        append_transaction(NODE_PUBLIC_KEY, public_key, 33.0, 'bgt', 'Wallet creation gift')

        KEYS_HASH_TABLE[public_key_hashed] = public_key

        return self._wrap_response(
            request,
            metadata={
                'wallet': {
                    public_key: {
                        'bgt': 33.0
                    }
                }
            },
            status=200)

    # Convertation from dec to coin_code makes as two transaction
    # Transaction of DEC from user's wallet to node's wallet
    # If first transaction was approved:
    #   Transaction of coin_code from node's wallet to user's wallet
    async def post_transaction_convert(self, request):
        body = await request.json()

        if 'payload' not in body:
            raise errors.NoTransactionPayload()

        payload = body['payload']
        try:
            payload_from = payload['payload_from']
            payload_to = payload['payload_to']
            signed_payload_from = payload['signed_payload_from']
            signed_payload_to = payload['signed_payload_to']
        except KeyError:
            raise errors.BadTransactionPayload()

        if payload_from['public_key_hashed'] not in KEYS_HASH_TABLE:
            raise errors.PublicKeyNotFound()
        else:
            from_public_key = KEYS_HASH_TABLE[payload_from['public_key_hashed']]

        if payload_to['public_key_hashed'] not in KEYS_HASH_TABLE:
            raise errors.PublicKeyNotFound()
        else:
            to_public_key = KEYS_HASH_TABLE[payload_to['public_key_hashed']]

        if from_public_key not in GLOBAL_STATE['wallets']:
            LOGGER.debug(
                'Wallet not found',
                from_public_key,
            )
            raise errors.WalletNotFound()
        elif payload_from['coin_code'] not in GLOBAL_STATE['wallets'][from_public_key]:
            LOGGER.debug(
                "Not enough funds in user's wallet",
                from_public_key,
            )
            raise errors.NotEnoughFunds()
        elif GLOBAL_STATE['wallets'][from_public_key][payload_from['coin_code']] < payload_from['tx_payload']:
            LOGGER.debug(
                "Not enough funds in user's wallet",
                from_public_key,
            )
            raise errors.NotEnoughFunds()

        if to_public_key not in GLOBAL_STATE['wallets']:
            LOGGER.debug(
                'Wallet not found',
                from_public_key,
            )
            raise errors.WalletNotFound()

        # Verification of signed hashes
        ecdsa = chilkat.CkEcc()
        crypt = chilkat.CkCrypt2()
        crypt.put_HashAlgorithm("SHA256")
        crypt.put_Charset("utf-8")
        crypt.put_EncodingMode("base64")

        #  Hash a payload_from string
        sha256_hash = crypt.hashStringENC(json.dumps(payload_from, sort_keys=True))

        ck_public_key = chilkat.CkPublicKey()
        success = ck_public_key.LoadFromString(from_public_key)
        if not success:
            print(ck_public_key.lastErrorText())
            raise errors.InvalidPublicKey()

        result = ecdsa.VerifyHashENC(sha256_hash, signed_payload_from, "base64", ck_public_key)
        if result != 1:
            raise errors.InvalidSignature()

        #  Hash a payload_to string
        sha256_hash = crypt.hashStringENC(json.dumps(payload_to, sort_keys=True))

        success = ck_public_key.LoadFromString(to_public_key)
        if not success:
            print(ck_public_key.lastErrorText())
            raise errors.InvalidPublicKey()

        result = ecdsa.VerifyHashENC(sha256_hash, signed_payload_to, "base64", ck_public_key)
        if result != 1:
            raise errors.InvalidSignature()

        # Execute transaction
        append_transaction(
            from_public_key,
            NODE_PUBLIC_KEY,
            payload_from['tx_payload'],
            payload_from['coin_code'],
            'First step of convertation'
        )

        append_transaction(
            NODE_PUBLIC_KEY,
            to_public_key,
            payload_from['tx_payload'] * 100,
            payload_to['coin_code'],
            'Second step of convertation'
        )

        return self._wrap_response(
            request,
            metadata={
                'wallet_from': GLOBAL_STATE['wallets'][from_public_key],
                'wallet_to': GLOBAL_STATE['wallets'][to_public_key]
            },
            status=200)


    async def post_transaction(self, request):
        body = await request.json()

        if 'payload' not in body:
            raise errors.NoTransactionPayload()

        payload = body['payload']
        try:
            signed_payload = payload['signed_payload']
            payload = payload['payload']
        except KeyError:
            raise errors.BadTransactionPayload()

        if payload['public_key_hashed'] not in KEYS_HASH_TABLE:
            raise errors.PublicKeyNotFound()
        else:
            public_key = KEYS_HASH_TABLE[payload['public_key_hashed']]

        if public_key not in GLOBAL_STATE['wallets']:
            LOGGER.debug(
                'Wallet not found',
                public_key,
            )
            raise errors.WalletNotFound()
        elif payload['coin_code'] not in GLOBAL_STATE['wallets'][public_key]:
            GLOBAL_STATE['wallets'][public_key][payload['coin_code']] = 0
            LOGGER.debug(
                "Not enough funds in user's wallet",
                public_key,
            )
            raise errors.NotEnoughFunds()
        elif GLOBAL_STATE['wallets'][public_key][payload['coin_code']] < payload['tx_payload']:
            LOGGER.debug(
                "Not enough funds in user's wallet",
                public_key,
            )
            raise errors.NotEnoughFunds()

        # Verification of signed hashes
        ecdsa = chilkat.CkEcc()
        crypt = chilkat.CkCrypt2()
        crypt.put_HashAlgorithm("SHA256")
        crypt.put_Charset("utf-8")
        crypt.put_EncodingMode("base64")

        #  Hash a payload_from string
        sha256_hash = crypt.hashStringENC(json.dumps(payload, sort_keys=True))

        ck_public_key = chilkat.CkPublicKey()
        success = ck_public_key.LoadFromString(public_key)
        if not success:
            print(ck_public_key.lastErrorText())
            raise errors.InvalidPublicKey()

        result = ecdsa.VerifyHashENC(sha256_hash, signed_payload, "base64", ck_public_key)
        if result != 1:
            raise errors.InvalidSignature()

        # Execute transaction
        append_transaction(
            public_key,
            NODE_PUBLIC_KEY,
            payload['tx_payload'],
            payload['coin_code'],
            'Regular transaction'
        )

        retval = self._wrap_response(
            request,
            metadata=GLOBAL_STATE['transactions'][-1],
            status=200)
        return retval

    async def get_wallet(self, request):
        public_key_hashed = request.match_info.get('public_key_hashed', '')
        if public_key_hashed not in KEYS_HASH_TABLE:
            raise errors.PublicKeyNotFound()
        else:
            public_key = KEYS_HASH_TABLE[public_key_hashed]

        if public_key not in GLOBAL_STATE['wallets']:
            LOGGER.debug(
                'Wallet not found',
                public_key,
            )
            raise errors.WalletNotFound()

        retval = self._wrap_response(
            request,
            metadata={
                'wallet': GLOBAL_STATE['wallets'][public_key]
            },
            status=200)
        return retval

    async def get_global_wallet(self, request):
        return self._wrap_response(
            request,
            metadata={
                'wallets': GLOBAL_STATE['wallets']
            },
            status=200)

    async def get_global_transactions(self, request):
        return self._wrap_response(
            request,
            metadata={
                'transactions': GLOBAL_STATE['transactions']
            },
            status=200)

    async def list_transactions(self, request):
        """Fetches list of txns from validator, optionally filtered by id.

        Request:
            query:
                - head: The id of the block to use as the head of the chain
                - id: Comma separated list of txn ids to include in results

        Response:
            data: JSON array of Transaction objects with expanded headers
            head: The head used for this query (most recent if unspecified)
            link: The link to this exact query, including head block
            paging: Paging info and nav, like total resources and a next link
        """
        paging_controls = self._get_paging_controls(request)
        validator_query = client_transaction_pb2.ClientTransactionListRequest(
            head_id=self._get_head_id(request),
            transaction_ids=self._get_filter_ids(request),
            sorting=self._get_sorting_message(request, "default"),
            paging=self._make_paging_message(paging_controls))

        response = await self._query_validator(
            Message.CLIENT_TRANSACTION_LIST_REQUEST,
            client_transaction_pb2.ClientTransactionListResponse,
            validator_query)

        data = [self._expand_transaction(t) for t in response['transactions']]

        return self._wrap_paginated_response(
            request=request,
            response=response,
            controls=paging_controls,
            data=data)

    async def fetch_transaction(self, request):
        """Fetches a specific transaction from the validator, specified by id.

        Request:
            path:
                - transaction_id: The 128-character id of the txn to be fetched

        Response:
            data: A JSON object with the data from the expanded Transaction
            link: The link to this exact query
        """
        error_traps = [error_handlers.TransactionNotFoundTrap]

        txn_id = request.match_info.get('transaction_id', '')
        self._validate_id(txn_id)

        response = await self._query_validator(
            Message.CLIENT_TRANSACTION_GET_REQUEST,
            client_transaction_pb2.ClientTransactionGetResponse,
            client_transaction_pb2.ClientTransactionGetRequest(
                transaction_id=txn_id),
            error_traps)

        return self._wrap_response(
            request,
            data=self._expand_transaction(response['transaction']),
            metadata=self._get_metadata(request, response))

    async def list_receipts(self, request):
        """Fetches the receipts for transaction by either a POST or GET.

        Request:
            body: A JSON array of one or more transaction id strings (if POST)
            query:
                - id: A comma separated list of up to 15 ids (if GET)
                - wait: Request should return as soon as some receipts are
                    available

        Response:
            data: A JSON object, with transaction ids as keys, and receipts as
                values
            link: The /receipts link queried (if GET)
        """
        error_traps = [error_handlers.ReceiptNotFoundTrap]

        # Parse transaction ids from POST body, or query paramaters
        if request.method == 'POST':
            if request.headers['Content-Type'] != 'application/json':
                LOGGER.debug(
                    'Request headers had wrong Content-Type: %s',
                    request.headers['Content-Type'])
                raise errors.ReceiptWrongContentType()

            ids = await request.json()

            if (not ids
                    or not isinstance(ids, list)
                    or not all(isinstance(i, str) for i in ids)):
                LOGGER.debug('Request body was invalid: %s', ids)
                raise errors.ReceiptBodyInvalid()
            for i in ids:
                self._validate_id(i)

        else:
            ids = self._get_filter_ids(request)
            if not ids:
                LOGGER.debug('Request for receipts missing id query')
                raise errors.ReceiptIdQueryInvalid()

        # Query validator
        validator_query = \
            client_receipt_pb2.ClientReceiptGetRequest(
                transaction_ids=ids)
        self._set_wait(request, validator_query)

        response = await self._query_validator(
            Message.CLIENT_RECEIPT_GET_REQUEST,
            client_receipt_pb2.ClientReceiptGetResponse,
            validator_query,
            error_traps)

        # Send response
        if request.method != 'POST':
            metadata = self._get_metadata(request, response)
        else:
            metadata = None

        data = self._drop_id_prefixes(
            self._drop_empty_props(response['receipts']))

        return self._wrap_response(request, data=data, metadata=metadata)

    async def fetch_peers(self, request):
        """Fetches the peers from the validator.
        Request:

        Response:
            data: JSON array of peer endpoints
            link: The link to this exact query
        """

        response = await self._query_validator(
            Message.CLIENT_PEERS_GET_REQUEST,
            client_peers_pb2.ClientPeersGetResponse,
            client_peers_pb2.ClientPeersGetRequest())

        return self._wrap_response(
            request,
            data=response['peers'],
            metadata=self._get_metadata(request, response))

    async def fetch_status(self, request):
        '''Fetches information pertaining to the valiator's status.'''

        response = await self._query_validator(
            Message.CLIENT_STATUS_GET_REQUEST,
            client_status_pb2.ClientStatusGetResponse,
            client_status_pb2.ClientStatusGetRequest())

        return self._wrap_response(
            request,
            data={
                'peers': response['peers'],
                'endpoint': response['endpoint']
            },
            metadata=self._get_metadata(request, response))

    async def _query_validator(self, request_type, response_proto,
                               payload, error_traps=None):
        """Sends a request to the validator and parses the response.
        """
        LOGGER.debug(
            'Sending %s request to validator',
            self._get_type_name(request_type))

        payload_bytes = payload.SerializeToString()
        response = await self._send_request(request_type, payload_bytes)
        content = self._parse_response(response_proto, response)

        LOGGER.debug(
            'Received %s response from validator with status %s',
            self._get_type_name(response.message_type),
            self._get_status_name(response_proto, content.status))

        self._check_status_errors(response_proto, content, error_traps)
        return self._message_to_dict(content)

    async def _send_request(self, request_type, payload):
        """Uses an executor to send an asynchronous ZMQ request to the
        validator with the handler's Connection
        """
        try:
            return await self._connection.send(
                message_type=request_type,
                message_content=payload,
                timeout=self._timeout)
        except DisconnectError:
            LOGGER.warning('Validator disconnected while waiting for response')
            raise errors.ValidatorDisconnected()
        except asyncio.TimeoutError:
            LOGGER.warning('Timed out while waiting for validator response')
            raise errors.ValidatorTimedOut()
        except SendBackoffTimeoutError:
            LOGGER.warning('Failed sending message - Backoff timed out')
            raise errors.SendBackoffTimeout()

    async def _head_to_root(self, block_id):
        error_traps = [error_handlers.BlockNotFoundTrap]
        if block_id:
            response = await self._query_validator(
                Message.CLIENT_BLOCK_GET_BY_ID_REQUEST,
                client_block_pb2.ClientBlockGetResponse,
                client_block_pb2.ClientBlockGetByIdRequest(block_id=block_id),
                error_traps)
            block = self._expand_block(response['block'])
        else:
            response = await self._query_validator(
                Message.CLIENT_BLOCK_LIST_REQUEST,
                client_block_pb2.ClientBlockListResponse,
                client_block_pb2.ClientBlockListRequest(
                    paging=client_list_control_pb2.ClientPagingControls(
                        limit=1)),
                error_traps)
            block = self._expand_block(response['blocks'][0])
        return (
            block['header_signature'],
            block['header']['state_root_hash'],
        )

    @staticmethod
    def _parse_response(proto, response):
        """Parses the content from a validator response Message.
        """
        try:
            content = proto()
            content.ParseFromString(response.content)
            return content
        except (DecodeError, AttributeError):
            LOGGER.error('Validator response was not parsable: %s', response)
            raise errors.ValidatorResponseInvalid()

    @staticmethod
    def _check_status_errors(proto, content, error_traps=None):
        """Raises HTTPErrors based on error statuses sent from validator.
        Checks for common statuses and runs route specific error traps.
        """
        if content.status == proto.OK:
            return

        try:
            if content.status == proto.INTERNAL_ERROR:
                raise errors.UnknownValidatorError()
        except AttributeError:
            # Not every protobuf has every status enum, so pass AttributeErrors
            pass

        try:
            if content.status == proto.NOT_READY:
                raise errors.ValidatorNotReady()
        except AttributeError:
            pass

        try:
            if content.status == proto.NO_ROOT:
                raise errors.HeadNotFound()
        except AttributeError:
            pass

        try:
            if content.status == proto.INVALID_PAGING:
                raise errors.PagingInvalid()
        except AttributeError:
            pass

        try:
            if content.status == proto.INVALID_SORT:
                raise errors.SortInvalid()
        except AttributeError:
            pass

        # Check custom error traps from the particular route message
        if error_traps is not None:
            for trap in error_traps:
                trap.check(content.status)

    @staticmethod
    def _wrap_response(request, data=None, metadata=None, status=200):
        """Creates the JSON response envelope to be sent back to the client.
        """
        envelope = metadata or {}

        if data is not None:
            envelope['data'] = data

        return web.Response(
            status=status,
            content_type='application/json',
            text=json.dumps(
                envelope,
                indent=2,
                separators=(',', ': '),
                sort_keys=True))

    @classmethod
    def _wrap_paginated_response(cls, request, response, controls, data,
                                 head=None):
        """Builds the metadata for a pagingated response and wraps everying in
        a JSON encoded web.Response
        """
        paging_response = response['paging']
        if head is None:
            head = response['head_id']
        link = cls._build_url(
            request,
            head=head,
            start=paging_response['start'],
            limit=paging_response['limit'])

        paging = {}
        limit = controls.get('limit')
        start = controls.get("start")
        paging["limit"] = limit
        paging["start"] = start
        # If there are no resources, there should be nothing else in paging
        if paging_response.get("next") == "":
            return cls._wrap_response(
                request,
                data=data,
                metadata={
                    'head': head,
                    'link': link,
                    'paging': paging
                })

        next_id = paging_response['next']
        paging['next_position'] = next_id

        # Builds paging urls specific to this response
        def build_pg_url(start=None):
            return cls._build_url(request, head=head, limit=limit, start=start)

        paging['next'] = build_pg_url(paging_response['next'])

        return cls._wrap_response(
            request,
            data=data,
            metadata={
                'head': head,
                'link': link,
                'paging': paging
            })

    @classmethod
    def _get_metadata(cls, request, response, head=None):
        """Parses out the head and link properties based on the HTTP Request
        from the client, and the Protobuf response from the validator.
        """
        head = response.get('head_id', head)
        metadata = {'link': cls._build_url(request, head=head)}

        if head is not None:
            metadata['head'] = head
        return metadata

    @classmethod
    def _build_url(cls, request, path=None, **changes):
        """Builds a response URL by overriding the original queries with
        specified change queries. Change queries set to None will not be used.
        Setting a change query to False will remove it even if there is an
        original query with a value.
        """
        changes = {k: v for k, v in changes.items() if v is not None}
        queries = {**request.url.query, **changes}
        queries = {k: v for k, v in queries.items() if v is not False}
        query_strings = []

        def add_query(key):
            query_strings.append('{}={}'.format(key, queries[key])
                                 if queries[key] != '' else key)

        def del_query(key):
            queries.pop(key, None)

        if 'head' in queries:
            add_query('head')
            del_query('head')

        if 'start' in changes:
            add_query('start')
        elif 'start' in queries:
            add_query('start')

        del_query('start')

        if 'limit' in queries:
            add_query('limit')
            del_query('limit')

        for key in sorted(queries):
            add_query(key)

        scheme = cls._get_forwarded(request, 'proto') or request.url.scheme
        host = cls._get_forwarded(request, 'host') or request.host
        forwarded_path = cls._get_forwarded(request, 'path')
        path = path if path is not None else request.path
        query = '?' + '&'.join(query_strings) if query_strings else ''

        url = '{}://{}{}{}{}'.format(scheme, host, forwarded_path, path, query)
        return url

    @staticmethod
    def _get_forwarded(request, key):
        """Gets a forwarded value from the `Forwarded` header if present, or
        the equivalent `X-Forwarded-` header if not. If neither is present,
        returns an empty string.
        """
        forwarded = request.headers.get('Forwarded', '')
        match = re.search(
            r'(?<={}=).+?(?=[\s,;]|$)'.format(key),
            forwarded,
            re.IGNORECASE)

        if match is not None:
            header = match.group(0)

            if header[0] == '"' and header[-1] == '"':
                return header[1:-1]

            return header

        return request.headers.get('X-Forwarded-{}'.format(key.title()), '')

    @classmethod
    def _expand_block(cls, block):
        """Deserializes a Block's header, and the header of its Batches.
        """
        cls._parse_header(BlockHeader, block)
        if 'batches' in block:
            block['batches'] = [cls._expand_batch(b) for b in block['batches']]
        return block

    @classmethod
    def _expand_batch(cls, batch):
        """Deserializes a Batch's header, and the header of its Transactions.
        """
        cls._parse_header(BatchHeader, batch)
        if 'transactions' in batch:
            batch['transactions'] = [
                cls._expand_transaction(t) for t in batch['transactions']]
        return batch

    @classmethod
    def _expand_transaction(cls, transaction):
        """Deserializes a Transaction's header.
        """
        return cls._parse_header(TransactionHeader, transaction)

    @classmethod
    def _parse_header(cls, header_proto, resource):
        """Deserializes a resource's base64 encoded Protobuf header.
        """
        header = header_proto()
        try:
            header_bytes = base64.b64decode(resource['header'])
            header.ParseFromString(header_bytes)
        except (KeyError, TypeError, ValueError, DecodeError):
            header = resource.get('header', None)
            LOGGER.error(
                'The validator sent a resource with %s %s',
                'a missing header' if header is None else 'an invalid header:',
                header or '')
            raise errors.ResourceHeaderInvalid()

        resource['header'] = cls._message_to_dict(header)
        return resource

    @staticmethod
    def _get_paging_controls(request):
        """Parses start and/or limit queries into a paging controls dict.
        """
        start = request.url.query.get('start', None)
        limit = request.url.query.get('limit', None)
        controls = {}

        if limit is not None:
            try:
                controls['limit'] = int(limit)
            except ValueError:
                LOGGER.debug('Request query had an invalid limit: %s', limit)
                raise errors.CountInvalid()

            if controls['limit'] <= 0:
                LOGGER.debug('Request query had an invalid limit: %s', limit)
                raise errors.CountInvalid()

        if start is not None:
            controls['start'] = start

        return controls

    @staticmethod
    def _make_paging_message(controls):
        """Turns a raw paging controls dict into Protobuf ClientPagingControls.
        """

        return client_list_control_pb2.ClientPagingControls(
            start=controls.get('start', None),
            limit=controls.get('limit', None))

    @staticmethod
    def _get_sorting_message(request, key):
        """Parses the reverse query into a list of ClientSortControls protobuf
        messages.
        """
        control_list = []
        reverse = request.url.query.get('reverse', None)
        if reverse is None:
            return control_list

        if reverse.lower() == "":
            control_list.append(client_list_control_pb2.ClientSortControls(
                reverse=True,
                keys=key.split(",")
            ))
        elif reverse.lower() != 'false':
            control_list.append(client_list_control_pb2.ClientSortControls(
                reverse=True,
                keys=reverse.split(",")
            ))

        return control_list

    def _set_wait(self, request, validator_query):
        """Parses the `wait` query parameter, and sets the corresponding
        `wait` and `timeout` properties in the validator query.
        """
        wait = request.url.query.get('wait', 'false')
        if wait.lower() != 'false':
            validator_query.wait = True
            try:
                validator_query.timeout = int(wait)
            except ValueError:
                # By default, waits for 95% of REST API's configured timeout
                validator_query.timeout = int(self._timeout * 0.95)

    def _drop_empty_props(self, item):
        """Remove properties with empty strings from nested dicts.
        """
        if isinstance(item, list):
            return [self._drop_empty_props(i) for i in item]
        if isinstance(item, dict):
            return {
                k: self._drop_empty_props(v)
                for k, v in item.items() if v != ''
            }
        return item

    def _drop_id_prefixes(self, item):
        """Rename keys ending in 'id', to just be 'id' for nested dicts.
        """
        if isinstance(item, list):
            return [self._drop_id_prefixes(i) for i in item]
        if isinstance(item, dict):
            return {
                'id' if k.endswith('id') else k: self._drop_id_prefixes(v)
                for k, v in item.items()
            }
        return item

    @classmethod
    def _get_head_id(cls, request):
        """Fetches the request's head query, and validates if present.
        """
        head_id = request.url.query.get('head', None)

        if head_id is not None:
            cls._validate_id(head_id)

        return head_id

    @classmethod
    def _get_filter_ids(cls, request):
        """Parses the `id` filter paramter from the url query.
        """
        id_query = request.url.query.get('id', None)

        if id_query is None:
            return None

        filter_ids = id_query.split(',')
        for filter_id in filter_ids:
            cls._validate_id(filter_id)

        return filter_ids

    @staticmethod
    def _validate_id(resource_id):
        """Confirms a header_signature is 128 hex characters, raising an
        ApiError if not.
        """
        if not re.fullmatch('[0-9a-f]{128}', resource_id):
            raise errors.InvalidResourceId(resource_id)

    @staticmethod
    def _message_to_dict(message):
        """Converts a Protobuf object to a python dict with desired settings.
        """
        return MessageToDict(
            message,
            including_default_value_fields=True,
            preserving_proto_field_name=True)

    @staticmethod
    def _get_type_name(type_enum):
        return Message.MessageType.Name(type_enum)

    @staticmethod
    def _get_status_name(proto, status_enum):
        try:
            return proto.Status.Name(status_enum)
        except ValueError:
            return 'Unknown ({})'.format(status_enum)
