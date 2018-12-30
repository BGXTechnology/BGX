key=$1
key=${key:="4aa37a37b9793a7f3696129d9a367b26fd0b2b1c"}
curl -X POST -H "public_key:${key}" http://localhost:8002/wallets
