"use strict";
import ConfigAPI from './ConfigAPI';
import RNFetchBlob from 'react-native-fetch-blob'

export class GetAddressService {

    constructor(props) {

        this.callback = null;
        this.address = "";
        this.task = null;
    }

    setAddress = (address) => {
        this.address = address;
    }

    setCallback = (callback) => {
        this.callback = callback;
    }

    async requestData() {
        let api = "http://api.etherscan.io/api?module=account&action=balance&address=" + this.address + "&tag=latest&apikey=NZKQGW4NSXT1PP5XM4TJQKV4K676S3BT5X";
        
        RNFetchBlob.fetch('GET', api)
        .then((res) => {
                let resJSON = res.json();
                this.callback.onSuccess("", "", resJSON, ConfigAPI.METHOD_GET_ADDRESS);
            })
            .catch((errorMessage) => {
                this.callback.onFail('', '', ConfigAPI.METHOD_GET_ADDRESS);
            });
    }

    async cancelRequest() {

        if (this.task) {
            this.task.cancel((err, taskId) => {
            })
        }
    }

}