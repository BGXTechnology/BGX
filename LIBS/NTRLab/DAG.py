
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


import networkx as nx
import lmdb
import services


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
        return self.__key + " | " + self.__value

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
        value = pair[0].key() + "->" + pair[1].key()
        return tuple((key, value))


# BGX wrapper for a work with networkx's Dag

class Dag:

    class __DataBase:

        def __init__(self, dbName):
            self.db = None
            dbPath = services.BGXConf.DEFAULT_STORAGE_PATH + 'db_' + dbName
            services.BGXlog.logInfo('Connection to ' + dbPath)
            retry_count = 0
            while self.db is None and retry_count \
                < services.BGXConf.MAX_RETRY_CREATE_DB:
                self.db = lmdb.open(dbPath, create=True)
                dbPath = dbPath + str(retry_count)
                retry_count += 1
            if self.db is None:
                services.BGXlog.logError('Fail! Connection to ' + dbPath)
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
                res += 'node (' + node.__str__() + '), '
            res += 'end'
            return res

        def addNode(self, transaction):
            services.BGXlog.logInfo('Adding node')
            if not isinstance(transaction, Transaction):
                return False
            else:
                self.graph.add_node(transaction)

        def addEdge(self, fromNode, toNode):
            services.BGXlog.logInfo('Adding edge')
            if isinstance(fromNode, Transaction) == False \
                or isinstance(toNode, Transaction) == False:
                return False
            self.graph.add_edge(fromNode, toNode)
            if not nx.algorithms.is_directed_acyclic_graph(self.graph):
                self.graph.remove_edge(fromNode, toNode)
                return False

        def findTransaction(self, transaction):
            services.BGXlog.logInfo('Trying to find transaction')
            return self.graph.has_node(transaction)

        def sort(self):
            services.BGXlog.logInfo('Sorting DAG')
            return nx.algorithms.topological_sort(self.graph)

        # TODO: сгружать ребра в БД

        def toKeyValueList(self):
            services.BGXlog.logInfo('Translating DAG to key-value pairs')
            list = []
            for node in self.sort():
                list.append(tuple((node.key(), node.value())))
            #for edge in nx.to_edgelist(self.graph):
            #    list.append(Transaction.hashFromTuple(edge))
            return list

        # TODO: добавить проверку хэша у нод, загрузку ребер

        def loadFromKeyValueList(self, list):
            services.BGXlog.logInfo('Loading DAG from key-value pairs')
            self.graph.clear()
            for pair in list:
                transaction = Transaction(pair[1])
                self.graph.add_node(transaction)

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

    def addEdge(self, fromNode, toNode):
        return Dag.__dag.addEdge(fromNode, toNode)

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
        for node in self.sort():
            print(node)




print('-----------------------------------------------------------------------')
d = Dag("someUsualDBB")

t1 = Transaction("String9")
t2 = Transaction("String10")

d.addNode(t1)
d.addNode(t2)
d.addEdge(t1, t2)

t3 = Transaction("String11")
t4 = Transaction("String12")
d.addEdge(t3, t4)

d.print()
print('-----------------------------------------------------------------------')
d.saveDAG()
d.loadDAG()
d.print()
print('-----------------------------------------------------------------------')

