# Copyright 2016, 2018 NTRlab
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

import logging

LOGGER = logging.getLogger(__name__)


class SignupInfo:
    """Encapsulates authorization data for network enrollment policies

    Attributes:
        bgt_public_key (str): Encoded public key corresponding to private
            key used by BGT to sign wait certificates.
        proof_data (str): Information that can be used internally to verify
            the validity of the signup information.
        anti_sybil_id (str): A string corresponding to the anti-Sybil ID for
            the enclave that generated the signup information.
        sealed_signup_data (str): A base 64 string representing data that can
            be persisted and presented at a later time to restore the BGT
            enclave.
    """

    __MAXIMUM_NONCE_LENGTH__ = 32

    @staticmethod
    def block_id_to_nonce(block_id):
        """
        A convenience method to convert a block ID to an acceptable nonce.

        Args:
            block_id (str): The block ID to convert to a nonce

        Returns:
            An acceptable nonce for calling create_signup_info with
        """
        return block_id[-SignupInfo.__MAXIMUM_NONCE_LENGTH__:]

    @classmethod
    def create_signup_info(cls,
                           bgt_enclave_module,
                           originator_public_key_hash,
                           nonce):
        """
        Creates signup information a BGT 1 validator uses to join the
        validator network.

        Args:
            bgt_enclave_module (module): The module that implements the
                underlying BGT enclave.
            originator_public_key_hash (str): A string representing SHA256
                hash (i.e., hashlib.sha256(OPK).hexdigest()) of the
                originator's public key
            nonce (str): A value that is to be stored in the nonce field of
                the attestation verification report.

                NOTE - the nonce field is limited to __MAXIMUM_NONCE_LENGTH__
                bytes.  If the nonce field is longer than
                __MAXIMUM_NONCE_LENGTH__ bytes then the LAST
                __MAXIMUM_NONCE_LENGTH__ bytes are used.

        Returns:
            SignupInfo: A signup info object.
        """

        # NOTE - because of underlying restrictions on what can be put in the
        # attestation verification report request nonce field, we are limiting
        # to at most the last __MAXIMUM_NONCE_LENGTH__ bytes of the nonce.

        enclave_signup_info = \
            bgt_enclave_module.create_signup_info(
                originator_public_key_hash,
                nonce[-cls.__MAXIMUM_NONCE_LENGTH__:])
        signup_info = cls(enclave_signup_info)

        return signup_info

    @classmethod
    def signup_info_from_serialized(cls, bgt_enclave_module, serialized):
        """
        Converts serialized signup info into an object.

        Args:
            bgt_enclave_module (module): The module that implements the
                underlying BGT enclave.
            serialized (str): The serialized signup info

        Returns:
            SignupInfo: A signup info object.
        """
        enclave_signup_info = \
            bgt_enclave_module.deserialize_signup_info(serialized)

        return cls(enclave_signup_info)

    @classmethod
    def unseal_signup_data(cls,
                           bgt_enclave_module,
                           sealed_signup_data):
        """
        Takes sealed data from a previous call to create_signup_info and
        re-establishes the BGT 1 enclave state.

        Args:
            bgt_enclave_module (module): The module that implements the
                underlying BGT enclave.
            sealed_signup_data: The sealed signup data that was previously
                returned as part of the signup info returned from
                create_signup_info.

        Returns:
            The encoded BGT public key corresponding to private key used by
            BGT to sign wait certificates.
        """
        return bgt_enclave_module.unseal_signup_data(sealed_signup_data)

    @classmethod
    def release_signup_data(cls,
                            bgt_enclave_module,
                            sealed_signup_data):
        """
        Takes sealed data from a previous call to create_signup_info and
        releases enclave resources invalidating this data for future use.

        Args:
            bgt_enclave_module (module): The module that implements the
                underlying BGT enclave.
            sealed_signup_data: The sealed signup data that was previously
                returned as part of the signup info returned from
                create_signup_info.

        Returns:
            The encoded BGT public key corresponding to private key used by
            BGT to sign wait certificates.
        """
        return bgt_enclave_module.release_signup_data(sealed_signup_data)

    def __init__(self, enclave_signup_info):
        self.bgt_public_key = enclave_signup_info.bgt_public_key
        self.proof_data = enclave_signup_info.proof_data
        self.anti_sybil_id = enclave_signup_info.anti_sybil_id
        self.sealed_signup_data = enclave_signup_info.sealed_signup_data

        # We cannot hold the signup info because it cannot be pickled for
        # storage
        self._serialized_signup_info = enclave_signup_info.serialize()

    def __str__(self):
        return \
            'SIGNUP_INFO: PPK={0}, PD={1}, ASID={2}'.format(
                self.bgt_public_key,
                self.proof_data,
                self.anti_sybil_id)

    def serialize(self):
        # Simply return the serialized version of the enclave signup info
        # as we don't have anything to add.
        return self._serialized_signup_info
