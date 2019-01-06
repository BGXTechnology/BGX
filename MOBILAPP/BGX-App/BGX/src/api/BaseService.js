import ConfigAPI from './ConfigAPI';
import RNFetchBlob from 'react-native-fetch-blob'
import i18n from '../translations/i18n';

export class BaseService {

    static METHOD = {
        POST: 'POST',
        GET: 'GET',
        DELTE: 'DELETE'
    }

    constructor(props) {

        this.callback = null;
        this.param = {

        };

        this.task = null;
    }

    setParam = (para) => {
        this.param = { ...para };
    }

    setCallback = (callback) => {
        this.callback = callback;
    }

    async requestData(isPost = true) {

        let bodyString = JSON.stringify({ "data": this.param });
        let method = this.param[ConfigAPI.PARAM_METHOD];

        this.task = RNFetchBlob.config({
            trusty: true
        }).fetch('POST', ConfigAPI.DOMAIN, {
            "Content-Type": "application/json"
        }, bodyString);
        await this.task.then((res) => {

            let resJSON = res.json();
            if (resJSON.status.error == 0) {
                // console.log('requestData data: ' + JSON.stringify(resJSON));
                this.callback.onSuccess(resJSON.status.error, resJSON.status.msg, resJSON.data, method);
            }
            else {
                // console.log(JSON.stringify(resJSON));
                this.callback.onFail(resJSON.status.error, resJSON.status.msg, method);
            }
        }).catch((errorMessage) => {
            // console.log('errorMessage', errorMessage);
            this.callback.onFail('', i18n.t('ERROR_KEY_CHILKAT'), method);
        })
    }

    async requestDataPostGet(methodType) {

        let method = this.param[ConfigAPI.PARAM_METHOD];
        let apiURL = this.param[ConfigAPI.PARAM_API_URL];

        // if (method === ConfigAPI.METHOD_CREATE_ORDER) {
        //     alert(method + ' ' + apiURL)
        // }

        var bodyString = null;
        if (methodType === 'POST' || methodType === 'DELETE') {
            if (method === ConfigAPI.API_GET_TRANSACTION_FEE
                || method === ConfigAPI.API_MAKE_TRANSACTION
                || method === ConfigAPI.API_ADD_FUND
                || method === ConfigAPI.METHOD_MAKE_TRANSACTION) {

                bodyString = JSON.stringify({ [ConfigAPI.PARAM_DATA]: this.param });
                console.log('METHOD_MAKE_TRANSACTION : ' + bodyString);
            } else {
                bodyString = JSON.stringify(this.param);
            }
        }

        this.task = RNFetchBlob.config({
            trusty: true,
            timeout: 30000
        }).fetch(methodType, apiURL, {
            "Content-Type": "application/json",
            "Authorization": ConfigAPI.TOKEN_DEMO
        }, bodyString);

        this.task.then((res) => {

            
            let resJSON = res.json();

            // console.log("resJSON", JSON.stringify(resJSON));
            if (method === ConfigAPI.METHOD_FILTER_MY_ORDER) {
                this.callback.onSuccess(0, "", resJSON[0], method);
            } else if (method === ConfigAPI.METHOD_ADD_COMMENT) {
                this.callback.onSuccess(0, "", resJSON[0], method);
            } else if (method === ConfigAPI.METHOD_FILTER || method === ConfigAPI.METHOD_GET_LIST_COMMENT) {
                this.callback.onSuccess(0, "", resJSON, method);
            } else if (method === ConfigAPI.METHOD_ADD_TO_WISH_LIST) {
                if (resJSON[0].status == true) {
                    this.callback.onSuccess(0, resJSON[0].message, resJSON[0].items, method);
                } else {
                    this.callback.onFail(1, resJSON[0].message, method);
                }
            } else if (method === ConfigAPI.METHOD_DELETE_ITEM_FAVORITE || method === ConfigAPI.METHOD_CLEAR_ALL_FAVORITE) {
                if (resJSON[0].status == true) {
                    this.callback.onSuccess(0, resJSON[0].message, null, method);
                } else {
                    this.callback.onFail(1, resJSON[0].message, method);
                }
            } else if (method === ConfigAPI.METHOD_BGX_GET_BALANCE) {
                if (resJSON.status == 1) {
                    this.callback.onSuccess(0, resJSON.message, resJSON.result, method);
                } else {
                    this.callback.onFail(0, resJSON.message, resJSON.result, method);
                }
            } else if (method === ConfigAPI.METHOD_GET_ADDRESS) {
                if (resJSON.error == null) {
                    if (resJSON.wallet != null && resJSON.wallet.dec != null) {

                        this.callback.onSuccess(0, '', resJSON.wallet.dec, method);
                    } else {
                        this.callback.onSuccess(0, '', '0', method);
                    }
                } else {
                    this.callback.onFail(0, resJSON.error.message, resJSON.error.title, method);
                }
            } else if (method === ConfigAPI.METHOD_BGT_GET_BALANCE) {
                if (resJSON.error == null) {
                    if (resJSON.wallet != null && resJSON.wallet.bgt != null) {

                        this.callback.onSuccess(0, '', resJSON.wallet.bgt, method);
                    } else {
                        this.callback.onSuccess(0, '', '0', method);
                    }
                } else {
                    this.callback.onFail(0, resJSON.error.message, resJSON.error.title, method);
                }
            } else if (method === ConfigAPI.METHOD_MAKE_TRANSACTION) {
                console.log('METHOD_MAKE_TRANSACTION data : ' + resJSON);
                if (resJSON.error == null) {
                    // console.log('getMakeSignedPayload METHOD_MAKE_TRANSACTION : ', resJSON);
                    this.callback.onSuccess(0, '', resJSON, method);
                } else {
                    this.callback.onFail('', resJSON.error.title, method);
                }

            } else {
                this.callback.onSuccess(0, "", resJSON, method);
            }
        })
            .catch((errorMessage) => {
                this.callback.onFail('', i18n.t('ERROR_KEY_CHILKAT'), method);
            })
    }

    async cancelRequest() {

        if (this.task) {
            this.task.cancel((err, taskId) => {
            })
        }
    }
}