num=$3
num=${num:=1}
post="{\"data\": {\"payload\" : {\"tx_payload\" : ${num}},\"signed_payload\": \"\"}}"
echo POST  $post
curl -X POST -d "$post" http://localhost:8002/fee
