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

import threading
import importlib
import logging

from bgx_pbft.consensus.pbft_settings_view import PbftSettingsView

LOGGER = logging.getLogger(__name__)


class PbftEnclaveFactory:
    """PbftEnclaveFactory provides a mechanism for abstracting the loading of
    a PBFT enclave module.
    """

    _lock = threading.Lock()
    _pbft_enclave_module = None

    @classmethod
    def get_pbft_enclave_module(cls, state_view, config_dir, data_dir):
        """Returns the PBFT enclave module based upon the corresponding value
        set by the sawtooth_settings transaction family.  If no PBFT enclave
        module has been set in the configuration, it defaults to the PBFT
        enclave simulator.

        Args:
            state_view (StateView): The current state view.
            config_dir (str): path to location where configuration for the
                bgt enclave module can be found.
            data_dir (str): path to location where data for the
                bgt enclave module can be found.
        Returns:
            module: The configured PBFT enclave module, or the PBFT enclave
                simulator module if none configured.

        Raises:
            ImportError: Raised if the given module_name does
                not correspond to a consensus implementation.
        """

        with cls._lock:
            # We are only going to load the PBFT enclave if we haven't already
            # done so.  Otherwise, we are just going to return the previously-
            # loaded enclave module.
            if cls._pbft_enclave_module is None:
                # Get the configured PBFT enclave module name.
                pbft_settings_view = PbftSettingsView(state_view)
                module_name = pbft_settings_view.enclave_module_name

                LOGGER.info(
                    'Load PBFT enclave module: %s; '
                    'Max log size: %f; ',
                    module_name,
                    pbft_settings_view.pbft_max_log_size
                  )

                # Load and initialize the module
                module = importlib.import_module(module_name)
                module.initialize(config_dir, data_dir)

                cls._pbft_enclave_module = module

        return cls._pbft_enclave_module
