
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


import unittest
import services
import ecdsa
import emission
import DAG


# Tests for BGXCrypto

class BGXCryptoTestCase(unittest.TestCase):

    # TODO: add "assertRaises" test for a "sign" function

    def test_digital_signature(self):
        digital_signature_1 = services.BGXCrypto.DigitalSignature()
        verifying_key = digital_signature_1.getVerifyingKey()
        message_1 = "message"
        message_2 = "not a message"

        sign_1 = digital_signature_1.sign(message_1)
        sign_2 = digital_signature_1.sign(message_2)

        self.assertTrue(digital_signature_1.verify(sign_1, message_1))
        self.assertTrue(digital_signature_1.verify(sign_2, message_2))
        self.assertNotEqual(sign_1, sign_2)

        self.assertRaises(ecdsa.keys.BadSignatureError, digital_signature_1.verify, sign_1, message_2)
        self.assertRaises(ecdsa.keys.BadSignatureError, digital_signature_1.verify, sign_2, message_1)

        digital_signature_2 = services.BGXCrypto.DigitalSignature(verifying_key)
        self.assertTrue(digital_signature_2.verify(sign_1, message_1))
        self.assertTrue(digital_signature_2.verify(sign_2, message_2))

        self.assertRaises(ecdsa.keys.BadSignatureError, digital_signature_2.verify, sign_1, message_2)
        self.assertRaises(ecdsa.keys.BadSignatureError, digital_signature_2.verify, sign_2, message_1)


# Tests for EmissionMechanism

class EmissionTestCase(unittest.TestCase):

    # TODO: add "checkEthereum", "checkHashOfClass" tests

    def test_emission(self):
        digital_signature = services.BGXCrypto.DigitalSignature()
        unique_tokens = emission.EmissionMechanism.releaseTokens("BGX Token", "BGT", "id", 1, digital_signature, 2)
        for token in unique_tokens:
            self.assertTrue(token.verifyToken(digital_signature))

        tokens = emission.EmissionMechanism.releaseTokens("BGX Token", "BGT", "id", False, digital_signature, 2)
        for token in tokens:
            self.assertTrue(token.verifyToken(digital_signature))


# Tests for DAG
# TODO: control of exceptions

class DAGTestCase(unittest.TestCase):

    # TODO: control of DB connection

    def test_DAG(self):
        db_name = "Test"
        dag = DAG.Dag(db_name)

        transaction_1 = DAG.Transaction("String1")
        transaction_2 = DAG.Transaction("String2")
        transaction_3 = DAG.Transaction("String3")

        dag.addNode(transaction_1)
        dag.addNode(transaction_2)
        dag.addEdge(transaction_1, transaction_2)

        self.assertTrue(dag.findTransaction(transaction_1))
        self.assertTrue(dag.findTransaction(transaction_2))
        self.assertFalse(dag.findTransaction(transaction_3))
        self.assertFalse(dag.addEdge(transaction_2, transaction_1))

        wrong_node = list()
        self.assertFalse(dag.addNode(wrong_node))
        self.assertFalse(dag.addEdge(wrong_node, transaction_3))
        self.assertFalse(dag.addEdge(transaction_3, wrong_node))

        dag.saveDAG()
        dag_loaded = DAG.Dag(db_name)
        dag_loaded.loadDAG()

        self.assertTrue(dag_loaded.findTransaction(transaction_1))
        self.assertTrue(dag_loaded.findTransaction(transaction_2))
        self.assertFalse(dag_loaded.findTransaction(transaction_3))

        self.assertTrue(dag.findTransaction(transaction_1))
        self.assertTrue(dag.findTransaction(transaction_2))
        self.assertFalse(dag.findTransaction(transaction_3))





#if __name__ == '__main__':
#    unittest.main()