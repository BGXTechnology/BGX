import ConfigAPI from './ConfigAPI';
import RNFetchBlob from 'react-native-fetch-blob';
import * as Constants from '../common/Constants';

export class GetTransactionService {

    constructor(props) {
        this.callback = null;
        this.address = "";
        this.task = null;
        this.isCheck = false;
        this.typeCard = Constants.BGX_BGT_ACCOUNT.BGX;
    }

    setAddress = (address) => {
        this.address = address;
        console.log('requestGetTransaction BGT this.address :' +  this.address );
    }

    setISCheckAddress = (isCheck) => {
        this.isCheck = isCheck;
    }

    setCardType = (type) => {
        this.typeCard = type;
        console.log('requestGetTransaction BGT this.typeCard :' +  this.typeCard );
    }

    setCallback = (callback) => {
        this.callback = callback;
    }

    async requestData() {

        let api = ConfigAPI.API_GLOBAL_TRANSACTIONS;
        if (this.typeCard == Constants.BGX_BGT_ACCOUNT.BGX) {
            api = "http://api.etherscan.io/api?module=account&action=txlist&address=" + this.address + "&startblock=0&endblock=99999999&sort=desc&page=1&offset=10&apikey=POsiK1wSTyvVGZh9euVM"
        }
        // console.log("GetTransactionService API : ", api);
        RNFetchBlob.fetch('GET', api).then((res) => {
            let resJSON = res.json();
            // console.log("GetTransactionService : ", JSON.stringify(resJSON));
            if (this.typeCard == Constants.BGX_BGT_ACCOUNT.BGX) {
                if (resJSON.status == 1) {
                    this.callback.onSuccess(resJSON.status, resJSON.msg, resJSON.result, ConfigAPI.API_GLOBAL_TRANSACTIONS);
                }
                else {
                    if (this.isCheck) {
                        this.callback.onSuccess('', '', resJSON.result, ConfigAPI.API_GLOBAL_TRANSACTIONS);
                    } else {
                        this.callback.onFail('', '', ConfigAPI.API_GLOBAL_TRANSACTIONS);
                    }
                }
            } else {
                if (resJSON.transactions != null) {
                    this.callback.onSuccess(resJSON.status, resJSON.msg, resJSON.transactions, ConfigAPI.API_GLOBAL_TRANSACTIONS);
                } else {
                    if (this.isCheck) {
                        this.callback.onSuccess('', '', resJSON.result, ConfigAPI.API_GLOBAL_TRANSACTIONS);
                    } else {
                        this.callback.onFail('', '', ConfigAPI.API_GLOBAL_TRANSACTIONS);
                    }
                }
            }
        })
            .catch((errorMessage) => {
                this.callback.onFail('', '', ConfigAPI.API_GLOBAL_TRANSACTIONS);
            });
    }

    async cancelRequest() {

        if (this.task) {
            this.task.cancel((err, taskId) => {
            })
        }
    }

}