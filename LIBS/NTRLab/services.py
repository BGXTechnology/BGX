
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


# Namespace for general configuration

class BGXConf:

    DEFAULT_STORAGE_PATH = './'
    MAX_RETRY_CREATE_DB = 10

