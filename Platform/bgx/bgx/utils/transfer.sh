from=$1
from=${from:="4aa37a37b9793a7f3696129d9a367b26fd0b2b1c"}
to=$2
to=${to:="673fcacfb51214e0543b786da79956b541e7d792"}
num=$3
num=${num:=1}
post="{\"data\": {\"payload\" : {\"address_from\": \"${from}\",\"address_to\":\"${to}\",\"tx_payload\" : ${num}},\"signed_payload\": \"\"}}"
echo POST  $post
curl -X POST -d "$post" http://localhost:8002/transactions
