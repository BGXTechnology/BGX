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
# ------------------------------------------------------------------------------

import math
import logging

from bgx_pbft.state.settings_view import SettingsView

LOGGER = logging.getLogger(__name__)


class PbftSettingsView:
    """A class to wrap the retrieval of PBFT configuration settings from the
    configuration view.  For values that are not in the current state view
    or that are invalid, default values are returned.
    """
    _NODE_ = 'plink' 
    _NODES_ = "{\"0281e398fc978e8d36d6b2244c71e140f3ee464cb4c0371a193bb0a5c6574810ba\": \"leader\",\"028c7e06db3af50a9958390e3e29f166b1cf6198586acf37cde46c8ea54e4a79ef\": \"plink\"}"
    _MAX_LOG_SIZE_ = 1000
    _BLOCK_DURATION_ = 200
    _CHECKPOINT_PERIOD_ = 100
    _VIEW_CHANGE_TIMEOUT_ = 4000

    def __init__(self, state_view):
        """Initialize a PbftSettingsView object.
        Args:
            state_view (StateView): The current state view.
        Returns:
            None
        """

        self._settings_view = SettingsView(state_view)
        self._node = None
        self._nodes = None
        self._max_log_size = None
        self._block_duration = None
        self._checkpoint_period = None
        self._view_change_timeout = None
        self._signup_commit_maximum_delay = 2
        self._key_block_claim_limit = 2
        self._block_claim_delay = 2

    def _get_config_setting(self,
                            name,
                            value_type,
                            default_value,
                            validate_function=None):
        """Retrieves a value from the config view, returning the default value
        if does not exist in the current state view or if the value is
        invalid.

        Args:
            name (str): The config setting to return.
            value_type (type): The value type, for example, int, float, etc.,
                of config value.
            default_value (object): The default value to be used if no value
                found or if value in config is invalid, for example, a
                non-integer value for an int config setting.
            validate_function (function): An optional function that can be
                applied to the setting to determine validity.  The function
                should return True if setting is valid, False otherwise.

        Returns:
            The value for the config setting.
        """

        try:
            value = \
                self._settings_view.get_setting(
                    key=name,
                    default_value=default_value,
                    value_type=value_type)

            if validate_function is not None:
                if not validate_function(value):
                    raise \
                        ValueError(
                            'Value ({}) for {} is not valid'.format(
                                value,
                                name))
        except ValueError:
            value = default_value

        return value

    @property
    def pbft_max_log_size(self):
        """Return the max log size if config setting exists and is valid, otherwise return the default.
        """
        if self._max_log_size is None:
            self._max_log_size = self._get_config_setting(
                    name='sawtooth.consensus.pbft.max_log_size',
                    value_type=int,
                    default_value=PbftSettingsView._MAX_LOG_SIZE_,
                    validate_function=lambda value: value >= 0)

        return self._max_log_size

    @property
    def pbft_block_duration(self):
        """Return the block_duration if config setting exists and is valid, otherwise return the default.
        """
        if self._block_duration is None:
            self._block_duration = self._get_config_setting(
                    name='sawtooth.consensus.pbft.block_duration',
                    value_type=int,
                    default_value=PbftSettingsView._BLOCK_DURATION_,
                    validate_function=lambda value: value >= 0)

        return self._block_duration


    @property
    def pbft_checkpoint_period(self):
        """Return the checkpoint_period if config setting exists and is valid, otherwise return the default.
        """
        if self._checkpoint_period is None:
            self._block_duration = self._get_config_setting(
                    name='sawtooth.consensus.pbft.checkpoint_period',
                    value_type=int,
                    default_value=PbftSettingsView._CHECKPOINT_PERIOD_,
                    validate_function=lambda value: value >= 0)

        return self._checkpoint_period
    

    @property
    def pbft_view_change_timeout(self):
        """Return the view_change_timeout if config setting exists and is valid, otherwise return the default.
        """
        if self._view_change_timeout is None:
            self._view_change_timeout = self._get_config_setting(
                    name='sawtooth.consensus.pbft.view_change_timeout',
                    value_type=int,
                    default_value=PbftSettingsView._VIEW_CHANGE_TIMEOUT_,
                    validate_function=lambda value: value >= 0)

        return self._view_change_timeout

    @property
    def pbft_node(self):
        """Return node type.
        """
        if self._node is None:
            self._node = self._get_config_setting(
                    name='sawtooth.consensus.pbft.node',
                    value_type=str,
                    default_value=PbftSettingsView._NODE_,
                    validate_function=lambda value: value)

        return self._node

    @property
    def pbft_nodes(self):
        """Return nodes list.
        """
        if self._nodes is None:
            self._nodes = self._get_config_setting(
                    name='sawtooth.consensus.pbft.nodes',
                    value_type=str,
                    default_value=PbftSettingsView._NODES_,
                    validate_function=lambda value: value)

        return self._nodes

    @property
    def signup_commit_maximum_delay(self):
        return self._signup_commit_maximum_delay

    @property
    def key_block_claim_limit(self):
        return self._key_block_claim_limit

    @property
    def block_claim_delay(self):
        return self._block_claim_delay
