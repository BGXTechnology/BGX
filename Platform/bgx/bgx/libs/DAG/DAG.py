
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


import matplotlib.pyplot as plt
import networkx as nx
import lmdb
import services
import json


# Prototype for a transection class.
# Note: objects in Dag's nodes must be hashable

class Transaction:

    def __init__(self, someString):
        self.someString = someString
        self.__value = self.someString
        self.__key = services.BGXCrypto.strHash(self.__value)

    def __eq__(self, other):
        return self.__key == other.__key

    def __hash__(self):
        return services.BGXCrypto.intHash(self.__value)

    def __str__(self):
        return self.__value
        #return self.__key + " | " + self.__value

    def key(self):
        return self.__key

    def value(self):
        return self.__value

    # Return (key, value) pair from a tuple with DAG's edge

    def hashFromTuple(pair):
        if not isinstance(pair[0], Transaction) \
            or not isinstance(pair[1], Transaction):
            return False
        key = services.BGXCrypto.strHash(pair[0].value() + pair[1].value())
        data = {'type' : 'edge', 'from' : pair[0].value(), 'to' : pair[1].value()}
        value = json.dumps(data)
        return tuple((key, value))


# BGX wrapper for a work with networkx's Dag

class Dag:

    class __DataBase:

        def __init__(self, db_name):
            self.db = None
            db_path = services.BGXConf.DEFAULT_STORAGE_PATH + 'db_' + db_name
            services.BGXlog.logInfo('Connection to ' + db_path)
            retry_count = 0
            while self.db is None and retry_count \
                < services.BGXConf.MAX_RETRY_CREATE_DB:
                self.db = lmdb.open(db_path, create=True)
                db_path = db_path + str(retry_count)
                retry_count += 1
            if self.db is None:
                services.BGXlog.logError('Fail! Connection to ' + db_path)
                # raise something

        def loadGraph(self):
            services.BGXlog.logInfo('Loading DAG from DB')
            list = []
            with self.db.begin() as lmdb_txn:
                with lmdb_txn.cursor() as lmdb_cursor:
                    for key, value in lmdb_cursor:
                        list.append(tuple((key.decode(), value.decode())))
            return list

        def saveListedGraph(self, list):
            services.BGXlog.logInfo('Saving DAG in DB')
            for pair in list:
                with self.db.begin(write=True) as lmdb_txn:
                    key = pair[0]
                    value = pair[1]
                    lmdb_txn.put(key.encode(), value.encode())

    class __Dag:

        def __init__(self):
            services.BGXlog.logInfo('DAG creation')
            self.graph = nx.DiGraph()

        def __str__(self):
            res = ''
            for node in self.sort():
                res += 'node (' + node.__str__() + '), \n'
            for edge in  self.graph.edges():
                res += 'edge (' + str(edge[0]) + ' -> ' + str(edge[1]) + '), \n'
            return res

        def addNode(self, transaction):
            services.BGXlog.logInfo('Adding node')
            if not isinstance(transaction, Transaction):
                # raise something
                return False
            else:
                self.graph.add_node(transaction)

        def addEdge(self, from_node, to_node):
            services.BGXlog.logInfo('Adding edge')
            if isinstance(from_node, Transaction) == False \
                or isinstance(to_node, Transaction) == False:
                # raise something
                return False
            self.graph.add_edge(from_node, to_node)
            if not nx.algorithms.is_directed_acyclic_graph(self.graph):
                self.graph.remove_edge(from_node, to_node)
                return False

        def findTransaction(self, transaction):
            services.BGXlog.logInfo('Trying to find transaction')
            return self.graph.has_node(transaction)

        def sort(self):
            services.BGXlog.logInfo('Sorting DAG')
            return nx.algorithms.topological_sort(self.graph)

        def toKeyValueList(self):
            services.BGXlog.logInfo('Translating DAG to key-value pairs')
            db_list = []
            for node in self.sort():
                data = {'type': 'node', 'value': node.value()}
                value = json.dumps(data)
                db_list.append(tuple((node.key(), value)))
            for edge in nx.to_edgelist(self.graph):
                db_list.append(Transaction.hashFromTuple(edge))
            return db_list

        def loadFromKeyValueList(self, list):
            services.BGXlog.logInfo('Loading DAG from key-value pairs')
            self.graph.clear()
            for pair in list:
                data = json.loads(pair[1])
                if data['type'] == 'node':
                    content = data['value']
                    transaction = Transaction(content)
                    self.addNode(transaction)
                elif data['type'] == 'edge':
                    from_node = Transaction(data['from'])
                    to_node = Transaction(data["to"])
                    self.addEdge(from_node, to_node)
                else:
                    services.BGXlog.logError('Fail! Something wrong in DB' )
                    # raise something

        def printDAG(self):
            node_pos = dict()
            pos = 3
            eps = -2
            for node in self.sort():
                node_pos[node] = [pos, pos + eps]
                eps *= -1
                pos += 1

            nx.draw_networkx(self.graph, pos=node_pos)
            plt.show()

    __dag = None
    __db = None

    def __init__(self, db_name):
        if not Dag.__dag:
            Dag.__dag = Dag.__Dag()
            Dag.__db = Dag.__DataBase(db_name)

    def __str__(self):
        return Dag.__dag.__str__()

    def addNode(self, transaction):
        return Dag.__dag.addNode(transaction)

    def addEdge(self, from_node, to_node):
        return Dag.__dag.addEdge(from_node, to_node)

    def findTransaction(self, transaction):
        return Dag.__dag.findTransaction(transaction)

    def sort(self):
        return self.__dag.sort()

    def saveDAG(self):
        list = self.__dag.toKeyValueList()
        self.__db.saveListedGraph(list)

    def loadDAG(self):
        list = self.__db.loadGraph()
        self.__dag.loadFromKeyValueList(list)

    def print(self):
        self.__dag.printDAG()

