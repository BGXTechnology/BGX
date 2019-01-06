"use strict";
import ConfigAPI from './ConfigAPI';
import RNFetchBlob from 'react-native-fetch-blob'

export class CountryNameService {
    constructor(props) {

        this.callback = null;
    }

    setCallback = (callback) => {
        this.callback = callback;
    }

    async requestData() {

        RNFetchBlob.fetch('GET', ConfigAPI.API_COUNTRY)
            .then((res) => {
                
                let resJSON = res.json();
                this.callback.onSuccess("", "", resJSON.country, ConfigAPI.METHOD_COUNTRY_NAME);
            })
            .catch((errorMessage) => {
                
                this.callback.onFail('', '', ConfigAPI.METHOD_COUNTRY_NAME);
            })
    }
}