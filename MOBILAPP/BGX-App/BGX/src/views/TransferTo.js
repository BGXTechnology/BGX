
import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity, TouchableHighlight, Text, Image, ScrollView, ActivityIndicator, Picker } from "react-native";
import IconMaterialIcons from "react-native-vector-icons/MaterialIcons";
import Utils from '../common/Utils';
import CardItem from '../components/CardItem';
import { GetTransactionService } from '../api/GetTransactionService';
import { BaseService } from '../api/BaseService';
import ConfigAPI from '../api/ConfigAPI';
import * as Constants from '../common/Constants';
import LoadingIndicator from '../components/LoadingIndicator';
import * as CommonStyleSheet from '../common/StyleSheet';
import PopupDialogInput from '../components/PopupDialogInput';
import TextComponent from '../components/TextComponent';
import BorderButton from '../components/BorderButton';
import CheckCameraAndPhotoHelper from '../nativeFunction/CheckCameraAndPhotoHelper';
import GetPublicPrivateKey from '../nativeFunction/GetPublicPrivateKey';
import i18n from '../translations/i18n';

const ENUM_KEY_INPUT = {
    ADDRESS: 'address',
    AMOUNT: 'amount',
    FEE: 'fee'
};

const BGX_BGT_ACCOUNT = {
    BGX: 1,
    BGT: 2
}
export default class TransferTo extends Component {

    constructor(props) {
        super(props);
        this.state = {
            listCardType: [],
            itemCard: this.props.navigation.getParam('itemCard', null),
            // input content
            address: null,
            amount: null,
            fee: 0,
            progressView: false,
            titlePopupInput: "",
            publicPrivateKeys: null,
            balance: 0,
            reason: "",
        }
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    async componentDidMount() {
        this._getBalance();
        this.setState({
            publicPrivateKeys: await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE)
        });
    }

    renderHeader = () => {
        return (
            <View style={styles.header}>
                <TouchableOpacity style={styles.header_left} onPress={() => {
                    this.props.navigation.state.params.onBack();
                    this.props.navigation.goBack();
                }}>
                    <IconMaterialIcons name="keyboard-backspace" size={30} color="white" />
                </TouchableOpacity>
                <Text style={styles.title}>Transfer To</Text>
            </View>
        );
    }

    renderBottom = () => {
        return (
            <View style={styles.bottom} >
                {/* Login button */}
                <BorderButton
                    title={i18n.t('SEND')}
                    onPress={this._buttonMakeTranscations}
                    titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                    style={[styles.button_login]}
                    imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                    background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}
                />
            </View>

        );
    }

    updateViewContentInput = (event) => {
        this.setState({
            heightContentInput: Number((Utils.appSize().height - event.nativeEvent.layout.height - Constants.HEIGHT_BOTTOM - Constants.HEIGHT_STATUS_BAR).toFixed(1))
        }, () => {
            // alert(Utils.appSize().height
            //     + "\n" + 'heightContentInput :' + this.state.heightContentInput
            //     + "\n" + 'updateViewContentInput :' + event.nativeEvent.layout.height);
        });
    }

    render() {
        return (
            <View style={[styles.container, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white', }]}>
                <Image style={styles.background} source={require('../resource/background_dark.jpg')} />
                <View style={styles.content}>
                    <View style={styles.content_card}
                        onLayout={(event) => {
                            this.updateViewContentInput(event)
                        }}>
                        {this.renderHeader()}
                        <Text style={styles.title}>{this.state.itemCard.title}</Text>
                        <CardItem
                            styleView={styles.card_item}
                            cardType={this.state.itemCard.cardTypeId}
                            balance={''}
                            cardHolder={this.state.itemCard.cardHolder}
                            publicKey={this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? this.state.itemCard.address : this.state.itemCard.publicKey}
                            disabled={true}
                        />

                    </View>
                    <View style={[styles.content_input, { height: this.state.heightContentInput - Constants.HEIGHT_STATUS_BAR - 18 }]}>
                        <ScrollView>
                            {/* balance */}
                            <View style={styles.content_card_status}>
                                <Text style={[styles.text,]}>{i18n.t('BALANCE')}</Text>
                                <View style={styles.content_bgx_balance}>
                                    <Text style={[styles.text_bgx_erc_value, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{this.state.balance}
                                        <Text style={[styles.text_bgx_erc,]}>
                                            {this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? ' DEC' :
                                                <Text style={[styles.textCurrency, { fontSize: 21, color: '#de113e' }]}>{' ' + i18n.t('BETA_SYMBOL')}</Text>}
                                        </Text>
                                    </Text>

                                </View>
                            </View>

                            {/* Send to */}
                            <Text style={[styles.title_text, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('SEND_TO')}</Text>
                            <TextComponent
                                width={Utils.appSize().width - 100}
                                labelStyle={styles.label}
                                style={[styles.input,]}
                                label={i18n.t('WALLET_ADDRESS')}
                                value={this.state.address}
                                secureTextEntry={false}
                                showQRCode={true}
                                buttonBarcode={this._scanQRcode}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.ADDRESS, i18n.t('WALLET_ADDRESS'), this.state.address)}
                            />
                            {/* Amount */}
                            <Text style={[styles.title_text, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('AMOUNT')}</Text>

                            {/* enter amount */}
                            <TextComponent
                                width={Utils.appSize().width - 100}
                                labelStyle={styles.label}
                                style={[styles.input]}
                                label={i18n.t('ENTER_AMOUNT')}
                                value={this.state.amount}
                                secureTextEntry={false}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.AMOUNT, i18n.t('INPUT_AMOUNT'), this.state.amount)}
                            />

                            {/* fee */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('FEE')}
                                value={this.state.fee}
                                secureTextEntry={false}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.FEE, i18n.t('INPUT_FEE'), this.state.fee)}
                                disabled={true}
                            />

                            <Text style={styles.verity}>{i18n.t('VERIFY')}</Text>
                        </ScrollView>
                    </View>
                </View>

                {this.renderBottom()}

                <PopupDialogInput
                    ref={(popupDialogInputAMOUNT) => { this.popupDialogInputAMOUNT = popupDialogInputAMOUNT; }}
                    title={this.state.titlePopupInput}
                    keyboardType={"numeric"}
                    onChangeText={this._handlerInput.bind(this)}
                />

                <PopupDialogInput
                    ref={(popupDialogInput) => { this.popupDialogInput = popupDialogInput; }}
                    title={this.state.titlePopupInput}
                    onChangeText={this._handlerInput.bind(this)}
                    keyboardType={Utils.isAndroid() ? 'email-address' : 'numbers-and-punctuation'}
                />

                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View >
        );
    }


    _showPopupInput = (key, title, text) => {
        this.setState({ titlePopupInput: title });
        if (key == ENUM_KEY_INPUT.AMOUNT) {
            this.popupDialogInputAMOUNT.showSlideAnimationDialog(key, text);
        } else {
            this.popupDialogInput.showSlideAnimationDialog(key, text);
        }
    }

    _handlerInput = (key, text) => {
        switch (key) {
            case ENUM_KEY_INPUT.ADDRESS: {
                this.setState({ address: text });
                break;
            }
            case ENUM_KEY_INPUT.AMOUNT: {
                this.setState({ amount: text });
                let paramsPlayLoad = {
                    [ConfigAPI.PARAM_TX_PAYLOAD]: text,
                    [ConfigAPI.PARAM_COIN_CODE]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt'
                }
                let params = {
                    [ConfigAPI.PARAM_METHOD]: ConfigAPI.API_GET_TRANSACTION_FEE,
                    [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_TRANSACTION_FEE,
                    [ConfigAPI.PARAM_PAYLOAD]: paramsPlayLoad
                };
                this.setState({ progressView: true });
                let balance = new BaseService();
                balance.setParam(params);
                balance.setCallback(this);
                balance.requestDataPostGet(BaseService.METHOD.POST);
                break;
            }
            case ENUM_KEY_INPUT.FEE: {
                this.setState({ fee: text });
                break;
            }
        }
    }

    _renderButtonText(rowData) {
        return rowData.title;
    }

    _renderRow(rowData, rowID, highlighted) {
        return (
            <TouchableHighlight underlayColor='black'>
                <View onLayout={(event) => { this._updateView(event) }}>
                    <Text style={[styles.dropdown_row_text, highlighted && { color: 'black' }]}>
                        {rowData.title}
                    </Text>
                </View>
            </TouchableHighlight>
        );
    }

    _renderSeparator(sectionID, rowID, adjacentRowHighlighted) {
        return (<View style={styles.dropdown_separator} />);
    }

    _updateView = (event) => {
        this.setState({
            heightModalDropdown: event.nativeEvent.layout.height * 2
        });
    }

    _getBalance = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? ConfigAPI.METHOD_BGX_GET_BALANCE : ConfigAPI.METHOD_BGT_GET_BALANCE,
            [ConfigAPI.PARAM_API_URL]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ?
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', this.state.itemCard.address) :
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', this.state.itemCard.address)
        };
        let balance = new BaseService();
        balance.setParam(params);
        balance.setCallback(this);
        balance.requestDataPostGet(BaseService.METHOD.GET);
    }

    async onSuccess(code, message, data, method) {
        this.setState({ progressView: false });
        if (method == ConfigAPI.METHOD_BGX_GET_BALANCE) {
            this.setState({ balance: Utils.decimalValue(data / 1000000000000000000, 2) == 0.00 ? 0 : Utils.decimalValue(data / 1000000000000000000, 2) });
        } else if (method == ConfigAPI.METHOD_BGT_GET_BALANCE) {
            let number = data;
            number = Utils.decimalValue(number, 2);
            if (data % 1 == 0) {
                number = parseInt(data, 10);
            }
            this.setState({
                balance: number,
            });
        } else if (method == ConfigAPI.API_GET_TRANSACTION_FEE) {
            this.setState({
                fee: data.fee
            });
        } else if (method == ConfigAPI.METHOD_MAKE_TRANSACTION) {
            Utils.showAlert(i18n.t('TRANSACTIONS_SUCCESSFULLY'), true, {
                done: () => {
                    this.props.navigation.state.params.onBack();
                    this.props.navigation.goBack();
                }
            });
        }
    }

    async onFail(code, message, method) {
        this.setState({
            progressView: false
        });
        Utils.showAlert(i18n.t('MAKE_TRANSACTIONS_ERROR'), true, null);
    }

    _scanQRcode = () => {
        if (Utils.isIOS()) {
            CheckCameraAndPhotoHelper.checkIsCameraEnable((status) => {
                if (status == "4" || status == "1") {
                    this.props.navigation.navigate('QRCodeView', { getAddress: (address) => this._onGetAddress(address) });
                } else {
                    Alert.alert(
                        '',
                        i18n.t('MESSAGE_ALLOW_CAMERA_TO_SCAN_QR'),
                        [
                            { text: i18n.t('OK'), onPress: () => { } },
                            { text: i18n.t('CANCEL') },
                        ],
                        { cancelable: false }
                    );
                }
            });
        } else {
            this.props.navigation.navigate('QRCodeView', { getAddress: (address) => this._onGetAddress(address) });
        }
    }

    _onGetAddress = (data) => {
        if (this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX) {
            if (data.address != null && data.address != "" && data.address.length >= 2) {
                if (data.address.substring(0, 2) != "0x") {
                    this.setState({
                        address: "0x" + data.address
                    })
                } else {
                    this.setState({
                        address: data.address
                    });
                }
            }
        } else {
            this.setState({
                address: data.address
            });
        }
    };

    _buttonMakeTranscations = () => {
        if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.itemCard.publicKey) {
            Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
            return;
        }

        if (!Utils.isValidString(this.state.address)) {
            Utils.showAlert(i18n.t('ADDRESS_NOT_BLANK'), true, null);
            return;
        }

        if (!Utils.isValidString(this.state.amount)) {
            Utils.showAlert(i18n.t('AMOUNT_NOT_BLANK'), true, null);
            return;
        }

        if (this.state.amount.split(".").length > 2) {
            Utils.showAlert(i18n.t('FORMAT_AMOUNT'), true, null);
            return;
        }

        if (this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX) {
            if (this.state.address != null && this.state.address != "" && this.state.address.length >= 2) {
                if (this.state.address.substring(0, 2) != "0x") {
                    this.setState({
                        address: "0x" + this.state.address
                    })
                }
            }
            if (!Utils.checkEtherAddressValid(this.state.address)) {
                Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                isTrue = false;
                return;
            }
        }

        GetPublicPrivateKey.getMakeSignedPayload(
            this.state.itemCard.address,
            this.state.address,
            this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt',
            this.state.amount,
            this.state.publicPrivateKeys.privateKey,
            (signPayload) => {
                let paramsPlayLoad = {
                    [ConfigAPI.PARAM_ADDRESS_FROM]: this.state.itemCard.address,
                    [ConfigAPI.PARAM_ADDRESS_TO]: this.state.address,
                    [ConfigAPI.PARAM_TX_PAYLOAD]: parseFloat(this.state.amount),
                    [ConfigAPI.PARAM_COIN_CODE]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt'
                }
                let params = {
                    [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_MAKE_TRANSACTION,
                    [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_MAKE_TRANSACTION,
                    [ConfigAPI.PARAM_PAYLOAD]: paramsPlayLoad,
                    [ConfigAPI.PARAM_SIGNED_PAYLOAD]: signPayload
                };
                console.log("getMakeSignedPayload:  ", JSON.stringify(params));
                this.setState({ progressView: true });
                let balance = new BaseService();
                balance.setParam(params);
                balance.setCallback(this);
                balance.requestDataPostGet(BaseService.METHOD.POST);
            }, (error) => {
                Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
            }
        );
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
        backgroundColor: '#181818',
    },
    content: {
        flex: 1,
        position: "absolute",
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: "center",
        top: Utils.isIOS() ? 18 : 0
    },
    background: {
        width: appSize.width,
        height: 150,
        position: 'absolute',
        resizeMode: 'cover',
        top: 0
    },
    header: {
        width: appSize.width,
        height: 45,
        flexDirection: 'row',
        alignItems: "center",
        justifyContent: 'center',
    },
    header_left: {
        position: 'absolute',
        left: 10,
    },
    header_right: {
        position: 'absolute',
        end: 10,
        flex: 1,
    },
    content_card_status: {
        flexDirection: 'column',
        width: Utils.appSize().width - 20,
        marginTop: Utils.isAndroid() ? 0 : 5,
        marginLeft: 20,
        width: appSize.width - 100,
    },
    text: {
        color: '#898989',
        fontSize: 12,
    },
    content_bgx_balance: {
        flex: 1,
        flexDirection: 'row',
        alignItems: "flex-end",
    },
    text_bgx_erc_value: {
        fontSize: 22,
        color: 'white'
    },
    text_bgx_erc: {
        fontSize: 16,
        color: '#6d6d6d',
        paddingLeft: 5
    },
    title_text: {
        fontSize: 20,
        marginLeft: 20,
        marginTop: 10
    },
    verity: {
        color: '#898989',
        fontSize: 12,
        margin: 20,
    },
    title_login: {

    },
    button_login: {

    },

    title_right: {
        fontSize: 16,
        color: 'white',
        textAlign: 'right',
        paddingTop: 10,
    },
    content_card: {
        flex: 1,
        flexDirection: 'column',
        alignItems: "center",
    },
    title: {
        fontSize: 25,
        textAlign: 'center',
        color: 'white',
    },
    line: {
        width: appSize.width - 100,
        height: 1
    },
    card_item: {
        alignItems: 'center',
        width: appSize.width - 20,
    },
    bottom: {
        width: appSize.width,
        flex: 1,
        position: 'absolute',
        bottom: 10,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
    },
    tab_bottom: {
        flex: 1,
        flexDirection: 'row',
    },
    content_input: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'flex-start',
        // justifyContent: 'space-between',
        marginTop: 10,
        paddingBottom: 10,
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    },
    input: {
        marginTop: 10,
        marginLeft: 20,
        marginRight: 20
    },
    set_change_pin: {
        textDecorationLine: 'underline',
        color: '#474646',
        fontSize: 16,
        marginTop: 10,
        marginLeft: 20
    },
    textCurrency: {
        fontSize: 18,
        fontWeight: '300',
        color: '#7d7d7d'
    },
});