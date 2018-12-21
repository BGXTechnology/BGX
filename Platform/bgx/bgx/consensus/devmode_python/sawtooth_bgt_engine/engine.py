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
import queue

import json

from sawtooth_sdk.consensus.engine import Engine
from sawtooth_sdk.consensus import exceptions
from sawtooth_sdk.protobuf.validator_pb2 import Message

from sawtooth_bgt_engine.oracle import BgtOracle, BgtBlock
from sawtooth_bgt_engine.pending import PendingForks


LOGGER = logging.getLogger(__name__)


class BgtEngine(Engine):
    def __init__(self, path_config, component_endpoint):
        # components
        self._path_config = path_config
        self._component_endpoint = component_endpoint
        self._service = None
        self._oracle = None

        # state variables
        self._exit = False
        self._published = False
        self._building = False
        self._committing = False

        self._pending_forks_to_resolve = PendingForks()
        LOGGER.debug('BgtEngine: init done')

    def name(self):
        LOGGER.debug('BgtEngine: ask name')
        return 'Devmode'

    def version(self):
        LOGGER.debug('BgtEngine: ask version')
        return '0.1'

    def stop(self):
        self._exit = True

    def _initialize_block(self):
        LOGGER.debug('BgtEngine: _initialize_block')
        chain_head = self._get_chain_head()
        LOGGER.debug('BgtEngine: _initialize_block ID=%s chain_head=%s',chain_head.block_id,chain_head)
        #initialize = True #self._oracle.initialize_block(chain_head)

        #if initialize:
        try:
            self._service.initialize_block(previous_id=chain_head.block_id)
            LOGGER.debug('BgtEngine: _initialize_block DONE')
        except exceptions.UnknownBlock:
            LOGGER.debug('BgtEngine: _initialize_block ERROR UnknownBlock')
            #return False
        except exceptions.InvalidState :
            LOGGER.debug('BgtEngine: _initialize_block ERROR InvalidState')
            #return False
        return True

    def _check_consensus(self, block):
        return True
        #return self._oracle.verify_block(block)

    def _switch_forks(self, current_head, new_head):
        try:
            switch = self._oracle.switch_forks(current_head, new_head)
        # The BGT fork resolver raises TypeErrors in certain cases,
        # e.g. when it encounters non-BGT blocks.
        except TypeError as err:
            switch = False
            LOGGER.warning('BGT fork resolution error: %s', err)

        return switch

    def _check_block(self, block_id):
        self._service.check_blocks([block_id])

    def _fail_block(self, block_id):
        self._service.fail_block(block_id)

    def _get_chain_head(self):
        return BgtBlock(self._service.get_chain_head())

    def _get_block(self, block_id):
        return BgtBlock(self._service.get_blocks([block_id])[block_id])

    def _commit_block(self, block_id):
        self._service.commit_block(block_id)

    def _ignore_block(self, block_id):
        self._service.ignore_block(block_id)

    def _cancel_block(self):
        try:
            self._service.cancel_block()
        except exceptions.InvalidState:
            pass

    def _summarize_block(self):
        try:
            return self._service.summarize_block()
        except exceptions.InvalidState as err:
            LOGGER.warning(err)
            return None
        except exceptions.BlockNotReady:
            #LOGGER.debug('exceptions.BlockNotReady')
            return None

    def _finalize_block(self):
        summary = self._summarize_block()

        if summary is None:
            #LOGGER.debug('Block not ready to be summarized')
            return None

        consensus = self._oracle.finalize_block(summary)

        if consensus is None:
            return None

        try:
            block_id = self._service.finalize_block(consensus)
            LOGGER.info(
                'Finalized %s with %s',
                block_id.hex(),
                json.loads(consensus.decode()))
            return block_id
        except exceptions.BlockNotReady:
            LOGGER.debug('Block not ready to be finalized')
            return None
        except exceptions.InvalidState:
            LOGGER.warning('block cannot be finalized')
            return None


    def _my_finalize_block(self):
        summary = self._summarize_block()

        if summary is None:
            #LOGGER.debug('Block not ready to be summarized')
            return None
        LOGGER.debug('Can FINALIZE NOW')
        consensus = b'devmode' #self._oracle.finalize_block(summary)

        if consensus is None:
            return None

        try:
            block_id = self._service.finalize_block(consensus)
            LOGGER.info('Finalized %s with ',block_id.hex()) #json.loads(consensus.decode())
            self._building = True
            # broadcast 
            #LOGGER.debug('broadcast ...')
            #self._service.broadcast('message_type',b'payload')
            return block_id
        except exceptions.BlockNotReady:
            LOGGER.debug('Block not ready to be finalized')
            return None
        except exceptions.InvalidState:
            LOGGER.warning('block cannot be finalized')
            return None


    def _check_publish_block(self):
        # Publishing is based solely on wait time, so just give it None.
        LOGGER.debug('_check_publish_block ')
        return self._oracle.check_publish_block(None)

    def start(self, updates, service, startup_state):
        LOGGER.debug('BgtEngine: start service=%s...',service)
        self._service = service
        self._oracle = BgtOracle(
            service=service,
            component_endpoint=self._component_endpoint,
            config_dir=self._path_config.config_dir,
            data_dir=self._path_config.data_dir,
            key_dir=self._path_config.key_dir)

        # 1. Wait for an incoming message.
        # 2. Check for exit.
        # 3. Handle the message.
        # 4. Check for publishing.
        
        handlers = {
            Message.CONSENSUS_NOTIFY_BLOCK_NEW: self._handle_new_block,
            Message.CONSENSUS_NOTIFY_BLOCK_VALID: self._handle_valid_block,
            Message.CONSENSUS_NOTIFY_BLOCK_COMMIT:self._handle_committed_block,
            Message.CONSENSUS_NOTIFY_PEER_CONNECTED:self._handle_peer_connected,
            Message.CONSENSUS_NOTIFY_PEER_MESSAGE:self._handle_peer_message,
            #CONSENSUS_NOTIFY_PEER_DISCONNECTED 
        }
        sum_cnt = 0
        LOGGER.debug('BgtEngine: start wait message')
        #self._service.initialize_block()
        while True:
            try:
                try:
                    type_tag, data = updates.get(timeout=0.1)
                except queue.Empty:
                    pass
                else:
                    LOGGER.debug('BgtEngine:Received message: %s',Message.MessageType.Name(type_tag))

                    try:
                        handle_message = handlers[type_tag]
                    except KeyError:
                        LOGGER.error('BgtEngine:Unknown type tag: %s',Message.MessageType.Name(type_tag))
                    else:
                        handle_message(data)

                if self._exit:
                    break

                #self._try_to_publish()
                if not self._published:
                    #self._service.initialize_block()
                    self._initialize_block() 
                    self._published = True
                elif not self._building:
                    sum_cnt += 1
                    if sum_cnt > 10:
                        sum_cnt = 0
                        self._my_finalize_block()
                        
            

            except Exception:  # pylint: disable=broad-except
                LOGGER.exception("BgtEngine:Unhandled exception in message loop")

        LOGGER.debug('BgtEngine: start DONE')

    def _try_to_publish(self):
        if self._published:
            return

        if not self._building:
            if self._initialize_block():
                self._building = True
                LOGGER.debug('BgtEngine: _initialize_block DONE')

        if self._building:
            LOGGER.debug('BgtEngine: _check_publish_block ..')
            if self._check_publish_block():
                LOGGER.debug('BgtEngine: _finalize_block ..')
                block_id = self._finalize_block()
                if block_id:
                    LOGGER.info("Published block %s", block_id.hex())
                    self._published = True
                    self._building = False
                else:
                    LOGGER.debug('BgtEngine: _cancel_block')
                    self._cancel_block()
                    self._building = False

    def _handle_new_block(self, block):
        block = BgtBlock(block)
        LOGGER.info('handle_new_block:Received %s', block)

        if self._check_consensus(block):
            LOGGER.info('Passed consensus check: %s', block.block_id.hex())
            self._check_block(block.block_id)
        else:
            LOGGER.info('Failed consensus check: %s', block.block_id.hex())
            self._fail_block(block.block_id)

    def _handle_valid_block(self, block_id):
        block = self._get_block(block_id)

        self._pending_forks_to_resolve.push(block)

        self._process_pending_forks()

    def _process_pending_forks(self):
        LOGGER.info('_process_pending_forks ..')
        while not self._committing:
            block = self._pending_forks_to_resolve.pop()
            if block is None:
                break

            self._resolve_fork(block)

    def _resolve_fork(self, block):
        chain_head = self._get_chain_head()

        LOGGER.info(
            'Choosing between chain heads -- current: %s -- new: %s',
            chain_head.block_id.hex(),
            block.block_id.hex())

        if self._switch_forks(chain_head, block):
            LOGGER.info('Committing %s', block.block_id.hex())
            self._commit_block(block.block_id)
            self._committing = True
        else:
            LOGGER.info('Ignoring %s', block.block_id.hex())
            self._ignore_block(block.block_id)

    def _handle_committed_block(self, block_id):
        LOGGER.info('Chain head updated to %s, abandoning block in progress',block_id.hex())

        self._cancel_block()

        self._building = False
        self._published = False
        self._committing = False

        self._process_pending_forks()

    def _handle_peer_connected(self, block):
        #block = BgtBlock(block)
        LOGGER.info('_handle_peer_connected:Received %s', block)

    def _handle_peer_message(self, block):
        #block = BgtBlock(block)
        LOGGER.info('_handle_peer_message:Received %s', block)

