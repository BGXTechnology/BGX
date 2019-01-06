"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    ImageBackground,
    TouchableOpacity,
    Text,
    Image
} from "react-native";

import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';
import CardItem from '../components/CardItem';
import BorderButton from '../components/BorderButton';
import * as Constants from '../common/Constants';
import LoadingIndicator from "../components/LoadingIndicator";
import ConfigAPI from "../api/ConfigAPI";
import {BaseService} from "../api/BaseService";
import Item from '../models/Item';
import GetPublicPrivateKey from '../nativeFunction/GetPublicPrivateKey';
import i18n from '../translations/i18n';
import { NavigationActions, StackActions } from "react-navigation";
import BorderButtonNew from '../components/BorderButtonNew';

export default class CheckingOut extends Component {

    constructor(props) {
        super(props);

        this.state = {
            totalAmmount: "0",
            bgtBalance: "0"
        }

        this.BGTCard = this.props.navigation.getParam("card", null);

        this.account = null;
        this.publicPrivateKeys = null;
        this.signPlayload = null;
        this.publicPrivateKey = null;

        this.totalItem = 0;
        this.arrayItem = [];
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    async componentDidMount() {
        this.account = await Utils.getDataWithKey(Constants.KEY_USER);
        this.publicPrivateKeys = await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE);
        this.requestGetShoppingCartList();
        
        this.publicPrivateKey = await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE);

        if (this.BGTCard.address != null) {
            this.requestGetBGTBalance(this.BGTCard.address);
        }
        
    }

    buttonBack_clicked = () => {
        this.props.navigation.goBack();
    }

    onInputPinSuccess = () => {
        this.requestMakeTransaction();
    }

    buttonPay_clicked = () => {
        this.props.navigation.navigate('PINView', {card: this.BGTCard, onInputPinSuccess: this.onInputPinSuccess } );
    }

    buttonCancel_clicked = () => {
        this.props.navigation.goBack();
    }

    _calculateTotalAmmount = (list) => {
        var ammount = 0;
        for (let item of list) {
            ammount += parseFloat(item.price);
        }

        return Utils.formatPrice(ammount);
    }

    _getSignPayload = () => {
        if (this.publicPrivateKey == null) {
            return;
        }

        // GetPublicPrivateKey.getSignedPayload(this.publicPrivateKey.publicKeyHashed,
        //     '100', 
        //     'bgt', 
        //     this.publicPrivateKey.privateKey, (signPlayload) => {
        //     console.debug('getSignedPayload ' + signPlayload)
        //     }, (error) => {
        //         Utils.showAlert('error', true, null);
        //     }
        // );
//5886a181868068c63f737074ae58936e646ed23a
//
//,
        // GetPublicPrivateKey.getSignedPayload(this.BGTCard.address,
        //     100 + "",
        //     'bgt',
        //     "any reason to add funds",
        //     this.publicPrivateKey.privateKey,
        //     (signPayload) => {
        //         console.debug('signPayload ' + signPayload);
        //         console.debug('signPayload ' + this.state.totalAmmount);
        //         console.debug('signPayload ' + this.BGTCard.address);
        //         console.debug('signPayload ' + this.publicPrivateKey.privateKey);
        //         console.debug('signPayload ' + this.publicPrivateKey.privateKey);
        //     }, (error) => {
        //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
        //     }
        // );

        let amount = parseFloat(this.state.totalAmmount) + "";

        GetPublicPrivateKey.getMakeSignedPayload(this.BGTCard.address,
            "5886a181868068c63f737074ae58936e646ed23a",
            this.BGTCard.cardTypeId == 1 ? 'dec' : 'bgt',
            amount,
            this.publicPrivateKey.privateKey,
            (signPayload) => {
                console.log("getMakeSignedPayload", amount);
                console.log("getMakeSignedPayload", signPayload);
                this.signPlayload = signPayload.replace('"', "");
            }, (error) => {
                Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
            }
        );

        // make transaction - not remove - example
        
        // GetPublicPrivateKey.getMakeSignedPayload("61c7e042b04c4f39654280457f6c9acad58bf5d5",//from
        //     "5886a181868068c63f737074ae58936e646ed23a",
        //     "bgt",
        //     "15.0",
        //     "MC4CAQEEIEzyOrRMoRkRy1/4/sbuhTZ8yiTeRbQE3Ok20af4MZCtoAcGBSuBBAAK",
        //     (signPayload) => {
        //         console.log("getMakeSignedPayload", JSON.stringify(signPayload));
        //     }, (error) => {
        //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
        //     }
        // );
    }

    //Request data
    requestGetShoppingCartList = () => {
        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_SHOPPING_CART,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USER_ID]: this.account.id
        };
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    requestGetListProductFiltered = (listProductID) => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_PRODUCT_FILTERED, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_LIST_PRODUCT_FILTERED.replace('{string_product_id}', listProductID)
        };
  
        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestMakeTransaction = () => {
    
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_MAKE_TRANSACTION,
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_MAKE_TRANSACTION,
            [ConfigAPI.PARAM_PAYLOAD]: {
                    [ConfigAPI.PARAM_ADDRESS_FROM]: this.BGTCard.address,
                    [ConfigAPI.PARAM_ADDRESS_TO]: "5886a181868068c63f737074ae58936e646ed23a",
                    [ConfigAPI.PARAM_TX_PAYLOAD]: parseFloat(this.state.totalAmmount),
                    [ConfigAPI.PARAM_COIN_CODE]: this.BGTCard.cardTypeId == 1 ? 'dec' : 'bgt'
                },
                [ConfigAPI.PARAM_SIGNED_PAYLOAD]: this.signPlayload
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.POST);
    }

    requestSendEmailReceipt = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_SEND_EMAIL_RECEIPT,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.account.email
        };

        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    requestGetBGTBalance = (address) => {

        let params = {
            [ConfigAPI.PARAM_METHOD]: this.BGTCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? ConfigAPI.METHOD_BGX_GET_BALANCE : ConfigAPI.METHOD_BGT_GET_BALANCE,
            [ConfigAPI.PARAM_API_URL]: this.BGTCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ?
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', address) :
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', address)
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestCreateOther = () => {

        var itemArrayDict = [];
        for (let item of this.arrayItem) {
            let price = parseFloat(item.price);
            let param = {
                    "base_original_price": item.price,
                  "base_price": item.price,
                  "base_price_incl_tax": item.price,
                  "base_row_total": item.price,
                  "base_row_total_incl_tax": item.price,
                  "name": item.name,
                  "original_price": item.price,
                  "price": item.price,
                  "price_incl_tax": item.price,
                  "product_id": item.id,
                  "product_type": "simple",
                  "qty_ordered": 1,
                  "row_total": item.price,
                  "row_total_incl_tax": item.price,
                  "sku": item.sku,
                  "store_id": 1,
                  "weight": 0
            }
            itemArrayDict.push(param);
        }


        let total = parseFloat(this.state.totalAmmount);

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_CREATE_ORDER,
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_CREATE_ORDER,
            "entity": {
              "base_currency_code": "β",
              "base_discount_amount": 0,
              "base_grand_total": total,
              "base_shipping_amount": 0,
              "base_shipping_incl_tax": 0,
              "base_shipping_tax_amount": 0,
              "base_shipping_discount_amount": 0,
              "base_subtotal": total,
              "base_subtotal_incl_tax": total,
              "base_total_due": 0,
              "base_total_paid": total,
              "base_to_global_rate": 1,
              "base_to_order_rate": 1,
              "discount_tax_compensation_amount": 0,
              "base_discount_tax_compensation_amount": 0,
              "shipping_discount_tax_compensation_amount": 0,
              "customer_is_guest": 0,
              "customer_email": this.account.email,
              "customer_firstname": this.account.username,
              "customer_group_id": 1,
              "customer_id": this.account.magentoId,
              "customer_lastname": this.account.username,
              "customer_note_notify": 0,
              "discount_amount": 0,
              "email_sent": 0,
              "global_currency_code": "β",
              "grand_total": total,
              "order_currency_code": "β",
              "shipping_amount": 0,
              "shipping_tax_amount": 0,
              "shipping_description": "Free Shipped",
              "shipping_discount_amount": 0,
              "shipping_incl_tax": 0,
              "state": "complete",
              "status": "complete",
              "store_currency_code": "β",
              "store_to_base_rate": 0,
              "store_to_order_rate": 0,
              "store_id": 1,
              "subtotal": total,
              "subtotal_incl_tax": total,
              "total_due": 0,
              "total_paid": total,
              "total_item_count": this.totalItem,
              "total_qty_ordered": this.totalItem,
              "tax_amount": 0,
              "weight": 0,
              "items": itemArrayDict,
              "billing_address": {
                "address_type": "billing",
                "city": "VN",
                "company": "",
                "country_id": "VN",
                "customer_address_id": 1,
                "email": this.account.email,
                "firstname": this.account.username,
                "lastname": this.account.username,
                "postcode": "70000",
                "region": "VN",
                "street": [
                  "tenduong"
                ],
                "telephone": this.account.phone
              },
              "payment": {
                "amount_ordered": total,
                "amount_paid": total,
                "base_amount_ordered": total,
                "base_amount_paid": total,
                "base_shipping_amount": 0,
                "method": "checkmo",
                "shipping_amount": 0
              },
              "status_histories": [
                {
                  "comment": "new order",
                  "status": "complete"
                }
              ],
              "extension_attributes": {
                "shipping_assignments": [
                  {
                    "shipping": {
                      "address": {
                        "address_type": "shipping",
                        "city": "VN",
                        "company": "",
                        "country_id": "VN",
                        "customer_address_id": 1,
                        "email": this.account.email,
                        "firstname": this.account.username,
                        "lastname": this.account.username,
                        "postcode": "70000",
                        "region": "VN",
                        "street": [
                          "tenduong"
                        ],
                        "telephone": this.account.phone
                      },
                      "method": "freeshipping_freeshipping",
                      "total": {
                        "base_shipping_amount": 0,
                        "base_shipping_incl_tax": 0,
                        "shipping_amount": 0,
                        "shipping_incl_tax": 0
                      },
                      "extension_attributes": [
                        
                      ]
                    },
                    "items": itemArrayDict,
                    "extension_attributes": [
                      
                    ]
                  }
                ],
                "applied_taxes": [
                  
                ],
                "item_applied_taxes": [
                  
                ],
                "converting_from_quote": false
              }
            }
          }

          console.debug('11223344 ' + JSON.stringify(params))

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.POST);
    }

    async onSuccess(code, message, data, method) {
        this.setState({ 
            progressView: false
        });

        this.isRequestData = false;

        if (method === ConfigAPI.METHOD_GET_LIST_SHOPPING_CART) {
            
            let count = data.length;
            this.totalItem = count;
            if (count > 0) {
                this.dataShoppingNotFull = data;

                var stringID = "";
                for (let item of data) {
                    stringID += item.productId + ',';
                }
                stringID = stringID.substring(0, stringID.length - 1);

                this.requestGetListProductFiltered(stringID);
            }
        } else if (method === ConfigAPI.METHOD_GET_LIST_PRODUCT_FILTERED) {
            var array = [];

            for (let itemNotFull of this.dataShoppingNotFull) {

                for (let item of data.items) {
                    let itemParse = new Item(item, Item.ITEM_PRODUCT_TYPE.DIGITAL_SPOT);
                    if (itemParse.id == itemNotFull.productId) {
                        array.push(itemParse);
                        continue;
                    }   
                }
            }

            this.arrayItem = array;

            let ammount = this._calculateTotalAmmount(array);

            this.setState({ 
                totalAmmount: ammount
            });

            this._getSignPayload();
        } else if (method === ConfigAPI.METHOD_MAKE_TRANSACTION) {
            let error = data.error;
           
            if (error != null) {
                let message = error.title;
                Utils.showAlert(message, true, null);
            } else {
                this.requestSendEmailReceipt();
                this.requestCreateOther();

                Utils.showAlert(i18n.t('MESSAGE_PAID_SUCCESSFUL'), false, {
                    done: () => {
                        this.props.navigation.navigate("ReceiptView");
                    },
                    cancel: () => {
                        this.props.navigation.dispatch(
                            StackActions.reset({
                            index: 0,
                            actions: [NavigationActions.navigate({ routeName: "DigitalSpotView" })]
                          })
                        );
                    }
                });
            }
        } else if (method == ConfigAPI.METHOD_BGX_GET_BALANCE) {
            this.setState({ balance: Utils.decimalValue(data / 1000000000000000000, 2) == 0.00 ? 0 : Utils.decimalValue(data / 1000000000000000000, 2) });
        } else if (method === ConfigAPI.METHOD_BGT_GET_BALANCE) {
            this.setState({
                bgtBalance: data
            });
        } else if (method === ConfigAPI.METHOD_CREATE_ORDER) {
            
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        if (method === ConfigAPI.METHOD_MAKE_TRANSACTION) {
            Utils.showAlert(message, true, null);
        }
    }

    render() {
        return (
            <View style={[styles.viewContain, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <ImageBackground style={styles.imageBackgroudHeader}
                    source={require("../resource/titleBackground.png")}>
                    <View style={styles.viewHeaderTitle}>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonBack_clicked}>
                            <Image style={styles.iconBack} source={require("../resource/icon_back_white.png")}/>
                        </TouchableOpacity>
                        <Text style={styles.textTitleHeader}>{i18n.t('CHECKING_OUT')}</Text>
                    </View>
                </ImageBackground>

                <View style={styles.viewContainOther}>
                    <CardItem styleView={styles.card_item}
                                cardType={this.BGTCard.cardTypeId}
                                balance={this.state.bgtBalance}
                                cardHolder={this.BGTCard.cardHolder}
                                publicKey={this.BGTCard.publicKey}
                                disabled={true}
                                isShowBalance={true}/>

                    <View style={{marginTop: 30, width: Utils.appSize().width - 60}}>
                        <View style={styles.viewText}>
                            <Text style={[styles.textAmmount, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('AMOUNT')}</Text>
                            <Text style={[styles.textBalance, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.state.totalAmmount + ' '}<Text style={{color: '#de113e', fontSize: 18, fontWeight: '600'}}>{i18n.t('BETA_SYMBOL')}</Text></Text>
                        </View>
                        <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#2f2f2f' : '#d0d0d0'}]}/>
                        <View style={[styles.viewText, {marginTop: 20}]}>
                            <Text style={{color: CommonStyleSheet.THEME_DARCK ? '#959595' : '#636363', fontSize: 16, fontWeight: '500'}}>{i18n.t('COMMISSION')}</Text>
                            <Text style={{color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111', fontSize: 16, fontWeight: '600'}}>{0  + ' '}<Text style={{color: '#de113e'}}>{i18n.t('BETA_SYMBOL')}</Text></Text>
                        </View>
                        <View style={[styles.viewText, {marginTop: 20}]}>
                            <Text style={[styles.textLabel, {color: CommonStyleSheet.THEME_DARCK ? '#959595' : '#636363'}]}>{i18n.t('BONUS_TOKEN')}</Text>
                            <Text style={[styles.textValue, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{0  + ' '}<Text style={{color: '#de113e'}}>{i18n.t('BETA_SYMBOL')}</Text></Text>
                        </View>
                    </View>

                    <View style={styles.viewButton}>

                        <BorderButtonNew
                                title={i18n.t('PAY')}
                                onPress={this.buttonPay_clicked}
                                titleStyle={[styles.titleButtonPay, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                                style={styles.buttonPay}
                                buttonType={CommonStyleSheet.THEME_DARCK ? BorderButtonNew.BORDER_BUTTON_TYPE.THEME_DARK_HEIGHT_50_WIDTH_0_WHITE : BorderButtonNew.BORDER_BUTTON_TYPE.THEME_WHITE_HEIGHT_50_WIDTH_0_WHITE}/>      

                        <BorderButtonNew
                                title={i18n.t('CANCEL')}
                                onPress={this.buttonCancel_clicked}
                                titleStyle={[styles.titleButtonCancel, {color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}]}
                                style={styles.buttonCancel}
                                buttonType={CommonStyleSheet.THEME_DARCK ? BorderButtonNew.BORDER_BUTTON_TYPE.THEME_DARK_HEIGHT_50_WIDTH_0_DARK : BorderButtonNew.BORDER_BUTTON_TYPE.THEME_WHITE_HEIGHT_50_WIDTH_0_DARK}/>  
                    </View>
                </View>

                
            </View>
        );
    }
}

const styles = StyleSheet.create({
    viewContain: {
        flex: 1, 
        alignItems: 'center'
    },
    imageBackgroudHeader: {
        width: '100%', 
        height: 150
    },
    viewHeaderTitle: {
        width: '100%', 
        flexDirection: 'row', 
        marginTop: 30,
        justifyContent: 'space-between'
    },
    iconBack: {
        width: 23, 
        height: 14, 
        marginLeft: 15,
        marginTop: 3
    },
    textTitleHeader: {
        color: 'white', 
        fontSize: 24, 
        fontWeight: '300',
        flex: 1,
        textAlign: 'center',
        marginTop: -3,
        marginLeft: -40
    },
    viewContainOther: {
        flex: 1, 
        marginTop: -80, 
        alignItems: 'center'
    }, 
    card_item: {
        alignItems: 'center',
        width: Utils.appSize().width - 20,
        zIndex: 1
    },
    viewText: {
        flexDirection: 'row', 
        justifyContent: 'space-between', 
        alignItems: 'baseline'
    },
    viewLine: {
        height: 1, 
        width: '100%', 
        marginTop: 20
    },
    textAmmount: {
        fontSize: 20, 
        fontWeight: '500'
    },
    textBalance: {
        fontSize: 26, 
        fontWeight: '600'
    },
    textLabel: {
        fontSize: 16, 
        fontWeight: '500'
    },
    textValue: {
        fontSize: 16, 
        fontWeight: '600'
    },
    viewButton: {
        width: '75%', 
        justifyContent: 'space-between', 
        height: 140,
        bottom: 10,
        position: 'absolute'
    },
    titleButtonPay: {
        fontWeight: '600',
        fontSize: 20
    }, 
    buttonPay: {
        width: '100%',
        height: 70,
        flex: 0
    },
    titleButtonCancel: {
        fontWeight: '600',
        fontSize: 20
    }, 
    buttonCancel: {
        width: '100%',
        height: 70,
        flex: 0
    }
});