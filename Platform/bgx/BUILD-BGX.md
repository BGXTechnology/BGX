![Sawtooth=BGX](bgx/images/logo-bgx.png)

Hyperledger Sawtooth-BGX
-------------

Hyperledger Sawtooth-BGX is an enterprise solution for building, deploying, and
running distributed ledgers (also called blockchains). It provides an extremely
modular and flexible platform for implementing transaction-based updates to
shared state between untrusted parties coordinated by consensus algorithms.

.
To build the requirements to run a validator network, run this command
$ docker-compose -f bgx/docker/docker-compose-bgx.yaml build

Also provided is a docker-compose file which builds a full set of images
with Sawtooth-BGX installed, and only the run-time dependencies installed.

$ docker-compose -f bgx/docker/docker-compose-installed-bgx.yaml build validator

To run a full validator node from the local source.
$ docker-compose -f bgx/docker/docker-compose-net-bgx.yaml up

For running shell-bgx run next bash cmd .
$ docker exec -it sawtooth-shell-bgx bash
For list created tokens run into shell-bgx. 
$ smart-bgt list  --url http://rest-api:8009
# not in sawtooth shell
$ curl http://localhost:8008/blocks
# smart-bgt init BGX_Token 21fad1db7c1e4f3fb98bb16fcff6942b4b2b9f890196b8754399ebfd74718de1 0xFB2F7C8687F6d86a031D2DE3d51f4c62e83AdA22 20 1 1 --url http://rest-api:8008
# smart-bgt transfer 0236bd0b2f6041338ffe5a2236be89f369ec3094e5247bb40aad3aaa18ff2da395 222 0.1 --url http://rest-api:8008 

# start REST-API 
$ docker-compose -f bgx/docker/docker-compose-rest-api.yaml up 
# make transfer 
$ cd bgs/utils
$ bash transfer.sh 673fcacfb51214e0543b786da79956b541e7d792 4aa37a37b9793a7f3696129d9a367b26fd0b2b1c 1
# create wallet
$ bash create_wallet.sh 673fcacfb51214e0543b786da79956b541e7d792
# get wallet
$ bash get_wallet.sh 673fcacfb51214e0543b786da79956b541e7d792
