# Copyright 2018 Cargill Incorporated
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

# docker build -f rest_api/Dockerfile -t sawtooth-rest-api-local .

# -------------=== rest_api build ===-------------

FROM ubuntu:xenial
ARG BGX_TOP
RUN echo "deb http://repo.sawtooth.me/ubuntu/ci xenial universe" >> /etc/apt/sources.list \
 && (apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 8AA7AF1F1091A5FD \
 || apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 8AA7AF1F1091A5FD) \
 && apt-get update

RUN apt-get install -y -q --allow-downgrades \
    git \
    python3 \
    python3-stdeb
RUN apt-get install -y -q --allow-downgrades \
    python3-grpcio \
    python3-grpcio-tools \
    python3-protobuf

RUN apt-get install -y -q --allow-downgrades \
    python3-aiodns \
    python3-aiohttp \
    python3-colorlog \
    python3-cchardet\
    python3-pyformance \
    python3-toml \
    python3-yaml \
    python3-zmq

RUN apt-get update && apt-get install -y \
    python3-pip

RUN pip3 install pycryptodome \
    ecdsa \
    cbor

RUN pip3 install aiohttp_cors

RUN mkdir /var/log/sawtooth

ENV PATH=$PATH:/project/$BGX_TOP/bgx/bin:/project/$BGX_TOP/bin

WORKDIR /project/$BGX_TOP

CMD echo "\033[0;32m--- Building rest_api-stub ---\n\033[0m" \
 && bgx/bin/protogen \
 && cd bgx/rest-api-stub \
 && python3 chilkat-linux/installChilkat.py \
 && python3 setup.py clean --all \
 && python3 setup.py build
