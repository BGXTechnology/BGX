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

    _MAX_LOG_SIZE_ = 1000
    _ENCLAVE_MODULE_NAME_ = 'bgx_pbft.enclave.pbft_enclave'

    def __init__(self, state_view):
        """Initialize a PbftSettingsView object.

        Args:
            state_view (StateView): The current state view.

        Returns:
            None
        """

        self._settings_view = SettingsView(state_view)

        self._max_log_size = None
        self._enclave_module_name = None
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
    def enclave_module_name(self):
        """Return the enclave module name if config setting exists and is
        valid, otherwise return the default.

        The enclave module name is the name of the Python module containing the
        implementation of the underlying PBFT enclave.
        """
        if self._enclave_module_name is None:
            self._enclave_module_name = \
                self._get_config_setting(
                    name='sawtooth.consensus.pbft.enclave_module_name',
                    value_type=str,
                    default_value=PbftSettingsView._ENCLAVE_MODULE_NAME_,
                    # function should return true if value is nonempty
                    validate_function=lambda value: value)

        return self._enclave_module_name

    @property
    def signup_commit_maximum_delay(self):
        return self._signup_commit_maximum_delay

    @property
    def key_block_claim_limit(self):
        return self._key_block_claim_limit

    @property
    def block_claim_delay(self):
        return self._block_claim_delay
