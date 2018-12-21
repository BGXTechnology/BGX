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

from bgx_pbft_engine.oracle import PbftOracle, PbftBlock
from bgx_pbft_engine.pending import PendingForks

from sawtooth_sdk.protobuf.consensus_pb2 import ConsensusPeerMessage
from bgx_pbft_common.protobuf.pbft_consensus_pb2 import PbftMessage,PbftMessageInfo 
from bgx_pbft_engine.config.pbft import PbftConfig

from bgx_pbft.state.settings_view import SettingsView

LOGGER = logging.getLogger(__name__)
PBFT_NAME = 'pbft' 
PBFT_VER  = '1.0'


class PbftEngine(Engine):
    def __init__(self, path_config, component_endpoint,pbft_config):
        # components
        self._path_config = path_config
        self._component_endpoint = component_endpoint
        self._pbft_config = pbft_config 
        self._service = None
        self._oracle = None

        # state variables
        self._exit = False
        self._published = False
        self._building = False
        self._committing = False
        self._check = False
        self._is_peer_connected = False
        self._node = self._pbft_config.node
        self._pending_forks_to_resolve = PendingForks()
        LOGGER.debug('PbftEngine: init done pbft=%s',self._pbft_config)

    def name(self):
        LOGGER.debug('PbftEngine: ask name')
        return PBFT_NAME

    def version(self):
        LOGGER.debug('PbftEngine: ask version')
        return PBFT_VER

    def stop(self):
        self._exit = True

    def _initialize_block(self):
        LOGGER.debug('PbftEngine: _initialize_block')
        chain_head = self._get_chain_head()
        initialize = self._oracle.initialize_block(chain_head,self._node)
        LOGGER.debug('PbftEngine: _initialize_block initialize=%s ID=%s chain_head=%s',initialize,chain_head.block_id.hex(),chain_head)
        if initialize :
            try:
                self._service.initialize_block(previous_id=chain_head.block_id)
                LOGGER.debug('PbftEngine: _initialize_block DONE')
            except exceptions.UnknownBlock:
                LOGGER.debug('BgtEngine: _initialize_block ERROR UnknownBlock')
                #return False
            except exceptions.InvalidState :
                LOGGER.debug('BgtEngine: _initialize_block ERROR InvalidState')
        return True #initialize


    def _check_consensus(self, block):
        #if not self._is_peer_connected :
        #s    return True
        self._start_consensus(block)
        return True

    def _start_consensus(self, block):
        return self._oracle.start_consensus(self._node,block)

    def _peer_message(self, block):
        #LOGGER.info('PbftEngine:handle PEER_MESSAGE: Received %s', type(block))
        return self._oracle.peer_message(self._node,block)

    def _switch_forks(self, current_head, new_head):
        try:
            switch = self._oracle.switch_forks(current_head, new_head)
        # The PBFT fork resolver raises TypeErrors in certain cases,
        # e.g. when it encounters non-PBFT blocks.
        except TypeError as err:
            switch = False
            LOGGER.warning('PBFT fork resolution error: %s', err)

        return switch

    def _check_block(self, block_id):
        # just check there is this block or not 
        self._service.check_blocks([block_id])

    def _fail_block(self, block_id):
        self._service.fail_block(block_id)

    def _get_chain_head(self):
        LOGGER.debug('_get_chain_head ..')
        return PbftBlock(self._service.get_chain_head())

    def _get_block(self, block_id):
        LOGGER.debug('_get_block id=%s',block_id.hex())
        return PbftBlock(self._service.get_blocks([block_id])[block_id])

    def _commit_block(self, block_id):
        self._service.commit_block(block_id)

    def _ignore_block(self, block_id):
        self._service.ignore_block(block_id)

    def _cancel_block(self):
        try:
            self._service.cancel_block()
        except exceptions.InvalidState:
            LOGGER.warning("_cancel_block: ERR=InvalidState")
            pass

    def _summarize_block(self):
        try:
            # Stop adding to the current block and summarize the contents of the block with a digest.
            return self._service.summarize_block()
        except exceptions.InvalidState as err:
            LOGGER.warning("_summarize_block:err=%s",err)
            return None
        except exceptions.BlockNotReady:
            #LOGGER.debug('exceptions.BlockNotReady')
            return None

    def _finalize_block(self):
        summary = self._summarize_block()

        if summary is None:
            #LOGGER.debug('Block not ready to be summarized')
            return None
        LOGGER.debug('Block ready to be summarized  summary=%s',summary.hex())
        consensus = self._oracle.finalize_block(summary)
        
        if consensus is None:
            return None

        try:
            """
            Stop adding batches to the current block and finalize it. Include the given consensus data in the block.
            If this call is successful, a BlockNew update will be received with the new block
            """
            block_id = self._service.finalize_block(consensus)
            #LOGGER.info('Finalized %s with %s',block_id.hex(),json.loads(consensus.decode()))
            LOGGER.info('Finalized id=%s with',block_id.hex())
            return block_id
        except exceptions.BlockNotReady:
            LOGGER.debug('Block not ready to be finalized')
            return None
        except exceptions.InvalidState:
            LOGGER.warning('block cannot be finalized')
            return None
        except Exception as err:
            LOGGER.warning("error=%s",err)
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
            return block_id
        except exceptions.BlockNotReady:
            LOGGER.debug('Block not ready to be finalized')
            return None
        except exceptions.InvalidState:
            LOGGER.warning('block cannot be finalized')
            return None
        except Exception as err:
            LOGGER.warning("error=%s",err)
            return None


    def _check_publish_block(self):
        # Publishing is based solely on wait time, so just give it None.
        #LOGGER.debug('_check_publish_block ')
        #return self._check;
        return self._oracle.check_publish_block(None)

    def start(self, updates, service, startup_state):
        LOGGER.debug('PbftEngine: start service=%s...',service)
        self._service = service
        self._oracle = PbftOracle(
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
            Message.CONSENSUS_BROADCAST_REQUEST  :self._handle_broadcast_request
            #CONSENSUS_NOTIFY_PEER_DISCONNECTED 
        }
        sum_cnt = 0
        LOGGER.debug('PbftEngine: start wait message')
        #self._service.initialize_block()
        while True:
            try:
                try:
                    type_tag, data = updates.get(timeout=0.1)
                except queue.Empty:
                    pass
                else:
                    LOGGER.debug('PbftEngine:Received message: %s',Message.MessageType.Name(type_tag))

                    try:
                        handle_message = handlers[type_tag]
                    except KeyError:
                        LOGGER.error('PbftEngine:Unknown type tag: %s',Message.MessageType.Name(type_tag))
                    else:
                        handle_message(data)

                if self._exit:
                    break

                self._try_to_publish()
                """
                if not self._published:
                    #self._service.initialize_block()
                    if self._initialize_block() :
                        LOGGER.debug('PbftEngine:_initialize_block DONE')
                        self._published = True
                elif not self._building:
                    sum_cnt += 1
                    if sum_cnt > 10:
                        sum_cnt = 0
                        self._my_finalize_block()
                """        
            

            except Exception:  # pylint: disable=broad-except
                LOGGER.exception("PbftEngine:Unhandled exception in message loop")

        LOGGER.debug('PbftEngine: start DONE')

    def _try_to_publish(self):
        if self._published:
            return

        if not self._building:
            if self._initialize_block():
                self._building = True
                LOGGER.debug('PbftEngine: _initialize_block DONE')

        if self._building:
            #LOGGER.debug('PbftEngine: _check_publish_block ..')
            if self._check_publish_block():
                #LOGGER.debug('PbftEngine: _finalize_block ..')
                #self._check = False
                block_id = self._finalize_block()
                if block_id:
                    LOGGER.info("Published block_id=%s", block_id.hex())
                    self._published = True
                    self._building = False
                """
                else:
                    LOGGER.debug('BgtEngine: _cancel_block')
                    self._cancel_block()
                    self._building = False
                """
    def _handle_new_block(self, block):
        LOGGER.info('handle NEW_BLOCK:Received id=%s block_num=%s payload=%s', block.block_id.hex(),block.block_num,block.payload)
        block = PbftBlock(block)
        
        if self._check_consensus(block):
            LOGGER.info('Passed consensus check: %s', block.block_id.hex())
            #self._check = True
            self._check_block(block.block_id)
        else:
            LOGGER.info('Failed consensus check: %s', block.block_id.hex())
            #self._fail_block(block.block_id)
        
    def _handle_valid_block(self, block_id):
        LOGGER.info('handle VALID_BLOCK:Received id=%s', block_id.hex())
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
        LOGGER.info('handle COMMITTED_BLOCK:Chain head updated to %s, abandoning block in progress',block_id.hex())

        self._cancel_block()

        self._building = False
        self._published = False
        self._committing = False

        self._process_pending_forks()

    def _handle_peer_connected(self, block):
        """
        Messages about new peers
        """
        #block = PbftBlock(block)
        LOGGER.info('handle PEER_CONNECTED: Received %s', type(block))
        self._is_peer_connected = True

    def _handle_peer_message(self, block):
        """
        we have got here messages which were sent for consensus via CONSENSUS_BROADCAST_REQUEST 
        """
        #block = PbftBlock(block)
        self._peer_message(block)
       

    def _handle_broadcast_request(self, block):
        LOGGER.info('handle BROADCAST_REQUEST: Received %s', block)
