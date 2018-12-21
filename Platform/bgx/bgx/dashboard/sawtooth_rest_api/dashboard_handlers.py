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
import yaml
import base64
import hashlib
import random
import os
from aiohttp import web
import cbor
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
from sawtooth_rest_api.protobuf.batch_pb2 import Batch,BatchHeader,BatchList
from sawtooth_rest_api.protobuf.transaction_pb2 import Transaction,TransactionHeader

from sawtooth_rest_api.route_handlers import RouteHandler,DEFAULT_TIMEOUT

#from sawtooth_signing.secp256k1 import Secp256k1PrivateKey, Secp256k1PublicKey, Secp256k1Context
#from sawtooth_signing import CryptoFactory,create_context


LOGGER = logging.getLogger(__name__)
ROOT = os.path.dirname(__file__)

class DashboardRouteHandler(RouteHandler):
    """Contains a number of aiohttp handlers for endpoints in the Rest Api.

    Args:
        connection (:obj: messaging.Connection): The object that communicates
            with the validator.
        timeout (int, optional): The time in seconds before the Api should
            cancel a request and report that the validator is unavailable.
    """

    def __init__(self, loop, connection,timeout=DEFAULT_TIMEOUT, metrics_registry=None):

        super().__init__(loop,connection,timeout,metrics_registry)
        # Dashboard init
        self._network = {}
        try:
            with open('./network.json') as file:
                self._network = json.load(file)
        except:
            pass

        
        #LOGGER.debug('DashboardRouteHandler: network=%s',self._network)

    async def index(self,request):
        html = request.match_info.get('html', '/')
        LOGGER.debug('DashboardRouteHandler: index=%s html=%s',request.path,html)
        full_path = 'app/html/' +  ('index' if html == '/' else html) + '.html' 
        try:
            content = open(os.path.join(ROOT, full_path), 'r').read()
        except:
            raise errors.FileNotFound()

        return web.Response(content_type='text/html', text=content)

    async def javascript(self,request):
        LOGGER.debug('DashboardRouteHandler: javascript=%s',request.path)
        content = open(os.path.join(ROOT,'app/js/'+request.path[1:]), 'r', encoding='utf-8').read()
        return web.Response(content_type='application/javascript', text=content)

    async def fetch_peers(self, request):
        """Fetches the peers from the validator.
        Request:

        Response:
            data: JSON array of peer endpoints
            link: The link to this exact query
        """
        LOGGER.debug('DashboardRouteHandler: fetch_peers')
        response = await self._query_validator(
            Message.CLIENT_PEERS_GET_REQUEST,
            client_peers_pb2.ClientPeersGetResponse,
            client_peers_pb2.ClientPeersGetRequest())

        return self._wrap_response(
            request,
            data=self._network, #response['peers'],
            metadata=self._get_metadata(request, response))

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

        content = cbor.loads(base64.b64decode(response['value']))
        LOGGER.debug('DashboardRouteHandler: fetch_state=(%s)',content)
        return self._wrap_response(
            request,
            data=content,
            metadata=self._get_metadata(request, response, head=head))


