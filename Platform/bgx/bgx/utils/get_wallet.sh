addr=$1
addr=${addr:="4aa37a37b9793a7f3696129d9a367b26fd0b2b1c"}
echo addr ${addr}
curl http://localhost:8002/wallets/${addr}
