import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity, TouchableHighlight, Text, Image, ScrollView, ActivityIndicator, Picker } from "react-native";
import IconMaterialIcons from "react-native-vector-icons/MaterialIcons";
import TabBarComponent from '../components/TabBarComponent';
import Utils from '../common/Utils';
import Line from "../components/Line";
import CardItem from '../components/CardItem';
import { BaseService } from '../api/BaseService';
import ConfigAPI from '../api/ConfigAPI';
import * as Constants from '../common/Constants';
var md5 = require('md5');
import LoadingIndicator from '../components/LoadingIndicator';
import * as CommonStyleSheet from '../common/StyleSheet';
import PopupDialogInput from '../components/PopupDialogInput';
import PopupImportKey from '../components/PopupImportKey';
import PopupSetChangePin from '../components/PopupSetChangePin';
import PopupExportKey from "../components/PopupExportKey";
import PopupQRCode from "../components/PopupQRCode";
import PopupMessage from "../components/PopupMessage";
import TextComponent from '../components/TextComponent';
import Account from "../models/Account";
import GetPublicPrivateKey from '../nativeFunction/GetPublicPrivateKey';
import Mailer from 'react-native-mail';
import Permissions from 'react-native-permissions';
import CheckCameraAndPhotoHelper from '../nativeFunction/CheckCameraAndPhotoHelper';
import i18n from '../translations/i18n';

const ENUM_KEY_INPUT = {
    CARDHOLDER: 'cardHolder',
    ISSUEDATE: 'issueDate',
    VALIDTILL: 'validTill',
    PIN: 'PIN',
    ADDRESS: 'address',
    PUBLICKEY: 'publicKey',
    PRIVATEKEY: 'privateKey'
};
const BGX_BGT_ACCOUNT = {
    BGX: 1,
    BGT: 2
}
export default class BGXWalletCardEditor extends Component {

    constructor(props) {
        super(props);
        this.state = {
            listCardType: [],
            cardTypeSelect: this.props.navigation.getParam('cardTypeSelect', null),
            cardUpdate: this.props.navigation.getParam('cardUpdate', null),
            balance: this.props.navigation.getParam('balance', 0),
            isEdit: this.props.navigation.getParam('isEdit', true),
            // input content
            cardHolder: null,
            issueDate: null,
            validTill: null,
            PIN: "",
            address: "",
            publicKey: null,
            privateKey: null,
            heightContentInput: 0,
            heightModalDropdown: 0,
            account: "",
            progressView: false,
            publicPrivateKeys: "", //publicKey , privateKey , publicKeyHashed
            titlePopupInput: "",
            isGenerateKey: false,
            newPublicPrivateKeys: "",
            isChange: false,
            fcmToken: "",
            isImportKey: false,
            oldPublicPrivateKeys: "",
        }
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    async componentDidMount() {
        let listCardType = await Utils.getDataWithKey(Constants.KEY_DATA_CARD_TYPE);
        let temAccount = await Utils.getDataWithKey(Constants.KEY_USER);
        let fcmToken = await Utils.getDataWithKey(Constants.KEY_PUSH_NOTIFICATION);
        this.setState({
            listCardType: listCardType,
            issueDate: Utils.stringFromDate(new Date(), 'MMM DD, YYYY HH:mm'),
            account: new Account(temAccount),
            publicPrivateKeys: await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE),
            fcmToken: fcmToken
        }, () => {
            this.setState({
                cardHolder: this.state.account.username,
            });
            if (this.state.cardUpdate != null && this.state.isEdit) {
                this.setState({
                    address: this.state.cardUpdate.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? this.state.cardUpdate.address.trim() : this.state.cardUpdate.address.trim(),
                    publicKey: this.state.cardUpdate.publicKey.trim(),
                    privateKey: this.state.publicPrivateKeys != null ? this.state.publicPrivateKeys.publicKey === this.state.cardUpdate.publicKey.trim() ? this.state.publicPrivateKeys.privateKey.trim() : "" : "",
                    PIN: this.state.cardUpdate.PIN.trim(),
                    cardHolder: this.state.cardUpdate.cardHolder.trim(),
                    issueDate: Utils.stringFromDate(this.state.cardUpdate.issueDate, 'MMM DD, YYYY HH:mm')
                }, () => {
                });
            } else {
                /** public and private key */
                if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) { // BGT
                    // if (Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys))) {
                    //     this.setState({
                    //         address: "",
                    //         publicKey: this.state.publicPrivateKeys.publicKey.trim(),
                    //         privateKey: this.state.publicPrivateKeys.privateKey.trim()
                    //     });
                    // } else {
                    GetPublicPrivateKey.getPublicPrivateKey((privateKey, publicKey, publicKeyHashed) => {
                        let dataKeys = {
                            publicKey: publicKey.trim(),
                            privateKey: privateKey.trim(),
                            publicKeyHashed: publicKeyHashed.trim()
                        };
                        this.setState({
                            address: "",
                            publicKey: publicKey.trim(),
                            privateKey: privateKey.trim(),
                            publicPrivateKeys: dataKeys,
                        });
                        // Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, dataKeys);
                    });
                    // }
                } else {
                    this.setState({
                        cardHolder: this.state.account.username,
                        address: this.state.account.ethereumAddress,
                    });
                }
            }
        });


        // // //TODO test
        // GetPublicPrivateKey.getSignedPayload("0x4223b0f5e3c58b37d03df872e80e1b210c1340e1",
        //     "100",
        //     "bgt",
        //     "any reason to add funds",
        //     "MC4CAQEEIJNnzRV2+3kD3/XRZWMJh2GHEwVLb2GLM1JNY+RxNNGkoAcGBSuBBAAK",
        //     (signPayload) => {

        //         console.log("getSignedPayload fuck", JSON.stringify(signPayload));
        //     }, (error) => {
        //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
        //     }
        // );
        // GetPublicPrivateKey.getMakeSignedPayload("3083d47393546f8be497050ef4c2b4bc1c4d4f9f",
        //     "61c7e042b04c4f39654280457f6c9acad58bf5d5",
        //     "bgt",
        //     "100",
        //     "MC4CAQEEIFGOjnuymhLV39ZmzQcy9btuYxHXJ/ppzo4URmWRMT5foAcGBSuBBAAK",
        //     (signPayload) => {
        //         console.log("getMakeSignedPayload", JSON.stringify(signPayload));
        //     }, (error) => {
        //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
        //     }
        // );
    }

    renderHeader = () => {
        return (
            <View style={styles.header}>
                <TouchableOpacity style={styles.header_left} onPress={this._handlerBack}>
                    <IconMaterialIcons name="keyboard-backspace" size={30} color="white" />
                </TouchableOpacity>
                <Text style={styles.title}>{i18n.t('WALLET')}</Text>
                <TouchableOpacity style={styles.header_right} onPress={this._save}>
                    <Text style={styles.title_right}>{i18n.t('SAVE')}</Text>
                </TouchableOpacity>
            </View>
        );
    }

    renderBottom = () => {
        return (
            <View style={styles.bottom} >
                <Line style={{ width: '100%', }} />
                <View style={styles.tab_bottom}>
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_import_key.png')}
                        title={i18n.t('TAB_IMPORT_KEY')}
                        hide={false}
                        disabled={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? !this.state.isEdit : true}  // BGT
                        onPress={this._showImportKey}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_export_key.png')}
                        title={i18n.t('TAB_EXPORT_KEY')}
                        hide={false}
                        disabled={!this.state.isEdit}  // BGT
                        onPress={this._showExportKey}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_generate_key.png')}
                        title={i18n.t('TAB_GENERATE_KEY')}
                        hide={false}
                        disabled={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? false : true}  // BGT
                        onPress={this._GenerateExportKey}
                    />
                </View>
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
                        onLayout={(event) => { this.updateViewContentInput(event) }}>
                        {this.renderHeader()}
                        <Text style={styles.title}>{this.state.cardTypeSelect.title}</Text>
                        <CardItem
                            styleView={styles.card_item}
                            cardType={this.state.cardTypeSelect.cardTypeId}
                            balance={''}
                            cardHolder={this.state.cardHolder}
                            publicKey={this.state.cardTypeSelect.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? this.state.address : this.state.publicKey}
                            disabled={true}
                        />
                    </View>
                    <View style={[styles.content_input, { height: this.state.heightContentInput - Constants.HEIGHT_STATUS_BAR - 18 }]}>
                        <ScrollView>
                            {/* Card holder */}
                            <TextComponent
                                width={Utils.appSize().width - 100}
                                labelStyle={styles.label}
                                style={[styles.input, { marginTop: 0 }]}
                                label={i18n.t('WELLET_CARD_EDITOR_CARD_HOLDER')}
                                value={this.state.cardHolder}
                                secureTextEntry={false}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.CARDHOLDER, i18n.t('INPUT_CARD_HOLDER'), this.state.cardHolder)}
                            />

                            {/* issua date */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('WELLET_CARD_EDITOR_ISSUE_DATE')}
                                styleContent={{ color: '#6d6d6d' }}
                                value={this.state.issueDate}// 
                                secureTextEntry={false}
                                disabled={true}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.ISSUEDATE, this.state.issueDate)}
                            />
                            {/* valid till */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('WELLET_CARD_EDITOR_VALID_TILL')}
                                value={this.state.validTill}
                                secureTextEntry={true}
                                disabled={true}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.VALIDTILL, this.state.validTill)}
                            />
                            {/* pin */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('WELLET_CARD_EDITOR_PIN')}
                                value={this.state.PIN}
                                secureTextEntry={true}
                                disabled={true}
                                isPIN={true}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.PIN, this.state.PIN)}
                            />

                            <Text style={[styles.set_change_pin, { color: CommonStyleSheet.THEME_DARCK ? this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? '#474646' : "#6d6d6d" : this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? 'black' : "#6d6d6d" }]}
                                onPress={this._setChangePin}>{i18n.t('WELLET_CARD_EDITOR_SET_CHANGE_PIN')}
                            </Text>
                            {/* address */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={[styles.input, { width: this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGX ? Utils.appSize().width - 60 : Utils.appSize().width - 100 }]}
                                label={i18n.t('WELLET_CARD_EDITOR_ADDRESS')}
                                value={this.state.address}
                                secureTextEntry={false}
                                width={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGX ? Utils.appSize().width - 100 : null}
                                showQRCode={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGX}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onPress={() => { if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGX) this._showPopupInput(ENUM_KEY_INPUT.ADDRESS, i18n.t('INPUT_ADDRESS'), this.state.address) }}
                                buttonBarcode={this._scanQRcode}
                                disabled={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT}
                            />

                            {/* public key */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('WELLET_CARD_EDITOR_PUBLIC_KEY')}
                                value={this.state.publicKey}
                                secureTextEntry={false}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                disabled={true}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.PUBLICKEY, this.state.publicKey)}
                            />

                            {/* private key */}
                            <TextComponent
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('WELLET_CARD_EDITOR_PRIVATE_KEY')}
                                value={this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? this.state.privateKey : ""}//this.state.privateKey
                                secureTextEntry={true}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                disabled={true}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.PRIVATEKEY, this.state.privateKey)}
                            />
                        </ScrollView>
                    </View>
                </View>

                {this.renderBottom()}
                <PopupImportKey
                    ref={(popupImportKey) => { this.popupImportKey = popupImportKey; }}
                    onPressQRCode={this._scanQRcode}
                    onPressEnterData={this._enterData}
                    onPressImportFile={this._importFile}
                />
                <PopupExportKey
                    ref={(popupExportKey) => { this.popupExportKey = popupExportKey; }}
                    onPressExportFile={this._handlerExportKey}
                    onPressCancel={this._handlerExportKeyCancel}
                    onPressGenerateQRCode={this._showGenerateQRCode}
                />
                <PopupSetChangePin
                    ref={(popupSetChangePin) => { this.popupSetChangePin = popupSetChangePin; }}
                    onPress={this._handlerPin}
                />
                <PopupDialogInput
                    ref={(popupDialogInput) => { this.popupDialogInput = popupDialogInput; }}
                    title={this.state.titlePopupInput}
                    onChangeText={this._handlerInput.bind(this)}
                    keyboardType={Utils.isAndroid() ? 'email-address' : 'numbers-and-punctuation'}
                />
                <PopupQRCode
                    titleButtonRight={i18n.t('OK')}
                    ref={(popupDialogQRCode) => { this.popupDialogQRCode = popupDialogQRCode; }}
                    onPress={this._handlerCancelQRCode}
                />
                <PopupMessage
                    ref={(popupDialogCreateCard) => { this.popupDialogCreateCard = popupDialogCreateCard; }}
                    onPress={this._createCardYes}
                    onPressCancel={this._createCardNo}
                    isConfirm={true}
                    titleButtonRight={i18n.t('YES')}
                    titleButtonLeft={i18n.t('NO')}
                    title={'about:blank'}
                />
                <PopupMessage
                    ref={(popupDialogBack) => { this.popupDialogBack = popupDialogBack; }}
                    onPress={() => {
                        this.props.navigation.state.params.onBack();
                        this.props.navigation.goBack();
                    }}
                    isConfirm={true}
                    titleButtonRight={i18n.t('OK')}
                    titleButtonLeft={i18n.t('CANCEL')}
                    title={'about:blank'}
                />

                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View >
        );
    }

    _handlerBack = () => {
        if (this.state.cardUpdate == null) {
            this.popupDialogBack.showSlideAnimationDialog(i18n.t('CONFIRM_CANCEL_CREATE_CARD'));
        } else {
            this.popupDialogBack.showSlideAnimationDialog(i18n.t('CONFIRM_CANCEL_EDIT_CARD'));
        }
    }

    _showPopupInput = (key, title, text) => {
        if (key == ENUM_KEY_INPUT.CARDHOLDER || key == ENUM_KEY_INPUT.ADDRESS || this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) { // BGT
            this.setState({ titlePopupInput: title });
            this.popupDialogInput.showSlideAnimationDialog(key, text);
        }
    }

    _handlerInput = (key, text) => {
        this.setState({
            isChange: true
        })
        switch (key) {
            case ENUM_KEY_INPUT.CARDHOLDER: {
                this.setState({ cardHolder: text });
                break;
            }
            case ENUM_KEY_INPUT.ISSUEDATE: {
                this.setState({ issueDate: text });
                break;
            }
            case ENUM_KEY_INPUT.VALIDTILL: {
                this.setState({ validTill: text });
                break;
            }
            case ENUM_KEY_INPUT.PIN: {
                this.setState({ PIN: text });
                break;
            }
            case ENUM_KEY_INPUT.ADDRESS: {
                this.setState({ address: text });
                break;
            }
            case ENUM_KEY_INPUT.PUBLICKEY: {
                this.setState({ publicKey: text });
                break;
            }
            case ENUM_KEY_INPUT.PRIVATEKEY: {
                this.setState({ privateKey: text });
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

    _showImportKey = () => {
        this.popupImportKey.showSlideAnimationDialog();
    }

    _showExportKey = () => {
        if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
            this.popupExportKey.showSlideAnimationDialog();
        } else {
            this.popupDialogQRCode.showSlideAnimationDialog(this.state.address);
        }
    }

    _setChangePin = () => {
        if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) { // BGT
            this.popupSetChangePin.showSlideAnimationDialog(this.state.isEdit, this.state.cardUpdate == null ? "" : this.state.cardUpdate.PIN.trim());
        }
    }

    _GenerateExportKey = () => {
        if (!this.state.isGenerateKey) {
            if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.publicKey) {
                Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
                return;
            }
        }
        GetPublicPrivateKey.getPublicPrivateKey((privateKey, publicKey, publicKeyHashed) => {
            let dataKeys = {
                publicKey: publicKey.trim(),
                privateKey: privateKey.trim(),
                publicKeyHashed: publicKeyHashed.trim()
            };
            this.setState({
                newPublicPrivateKeys: dataKeys
            }, () => {
                this.setState({
                    address: "",
                    publicKey: publicKey.trim(),
                    privateKey: privateKey.trim(),
                    isGenerateKey: true,
                    isChange: true
                });
                Utils.showAlert(i18n.t('GENERATED_SUCCESSFULLY'), true, null);
            });
        });
    }

    _updateView = (event) => {
        this.setState({
            heightModalDropdown: event.nativeEvent.layout.height * 2
        });
    }

    _handlerPin = (pin) => {
        this.setState({
            PIN: pin,
            isChange: true
        });
    }

    _handlerExportKey = async (passKey) => {
        await Utils.dismissKeyboard();
        if (Utils.isAndroid()) {
            if (Utils.isAndroid()) {
                Permissions.request('contacts').then(response => {
                    if (response == 'authorized') {
                        Permissions.request('storage').then(response => {
                            if (response == 'authorized') {
                                GetPublicPrivateKey.checkEmailDevice((callback) => {
                                    if (callback) {
                                        this._saveFile(passKey);
                                    } else {
                                        Utils.showAlert(i18n.t('DEVICE_NOT_EMAIL'), true, null);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        } else {
            GetPublicPrivateKey.checkEmailDevice((callback) => {
                if (callback == 'true') {
                    this._saveFile(passKey);
                } else {
                    Utils.showAlert(i18n.t('DEVICE_NOT_EMAIL'), true, null);
                }
            });
        }

        if (!this.state.isEdit || this.state.isGenerateKey) {
            this.props.navigation.state.params.onBack();
            this.props.navigation.goBack();
        }
    }

    _handlerExportKeyCancel = () => {
        if (!this.state.isEdit) {
            this.props.navigation.state.params.onBack();
            this.props.navigation.goBack();
        }
    }

    _saveFile = (passKey) => {
        if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.publicKey) {
            Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
            return;
        }
        let nameFile = Utils.stringFromDate(new Date(), 'DDMMYYYY-HHmmss') + '-' + this.state.cardHolder + ".p12";
        GetPublicPrivateKey.saveFile(passKey, nameFile, this.state.publicPrivateKeys.privateKey, (pathFile, error) => {
            Mailer.mail({
                subject: i18n.t('EMAIL_SUBJECT'),
                // recipients: ['tu.nguyenv@titancorpvn.com'],
                // ccRecipients: [],
                // bccRecipients: [],
                body: i18n.t('EMAIL_BODY') + "<br/><br />" + " <br /><br />" + "privateKey: " + this.state.privateKey +
                    "<br/><br /> publicKey: " + this.state.publicKey + "<br/><br /> address: " + this.state.address,
                isHTML: true,
                attachment: {
                    path: pathFile,  // The absolute path of the file from which to read data.
                    type: 'csv',   // Mime Type: jpg, png, doc, ppt, html, pdf, csv
                    name: nameFile,   // Optional: Custom filename for attachment
                }
            }, (error, event) => {
                if (Utils.isAndroid()) {
                    Utils.showAlert(i18n.t('DEVICE_NOT_EMAIL'), true, null);
                }
            });
        });
    }

    _save = async () => {
        if (this.state.isGenerateKey) {
            this.setState({
                oldPublicPrivateKeys: this.state.publicPrivateKeys,
            }, () => {
                this.setState({
                    publicPrivateKeys: this.state.newPublicPrivateKeys,
                })
            });
        }
        if (this.state.isImportKey) {
            this.setState({
                publicPrivateKeys: this.state.newPublicPrivateKeys,
            }, () => {
                Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, this.state.newPublicPrivateKeys);
                Utils.showAlert(i18n.t('IMPORT_KEY_SUCCESSFULLY'), true, {
                    done: () => {
                        this.props.navigation.state.params.onBack();
                        this.props.navigation.goBack();
                    }
                });
            });
            return;
        }
        if (!Utils.isNull(this.state.cardUpdate) && this.state.isEdit) {
            if (!this.state.isChange) {
                Utils.showAlert(i18n.t('MESSAGE_NO_CHANGES_HAVE_BEEN_MADE'), true,
                    {
                        done: () => {
                            this.props.navigation.state.params.onBack();
                            this.props.navigation.goBack();
                        }
                    });
                return;
            }
        }

        if (!Utils.isValidString(this.state.cardHolder)) {
            Utils.showAlert(i18n.t('WELLET_CARD_HOLDER_NOT_BLANK'), true, null);
            return;
        }

        if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
            if (!Utils.isValidString(this.state.PIN)) {
                Utils.showAlert(i18n.t('WELLET_CARD_PIN_NOT_BLANK'), true, null);
                return;
            }
            if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys))) {
                Utils.showAlert(i18n.t('PLEASE_IMPORT_KEYS'), true, null);
                return;
            }
        } else {
            if (!Utils.isValidString(this.state.address)) {
                Utils.showAlert(i18n.t('MESSAGE_ETH_BLANK'), true, null);
                return;
            }
            if (this.state.address != null && this.state.address != "" && this.state.address.length >= 2) {
                if (this.state.address.substring(0, 2) != "0x") {
                    let tempAddress = "0x" + this.state.address;
                    this.setState({
                        address: tempAddress
                    });
                    if (!Utils.checkEtherAddressValid(tempAddress)) {
                        Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                        return;
                    }
                } else if (!Utils.checkEtherAddressValid(this.state.address)) {
                    Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                    return;
                }
            } else {
                Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                return;
            }
        }
        this.setState({ progressView: true });

        let params = {
            [ConfigAPI.PARAM_METHOD]: Utils.isNull(this.state.cardUpdate) ? ConfigAPI.METHOD_ADD_CARD : ConfigAPI.METHOD_UPDATE_CARD,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.account.email,
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: this.state.account.password,
            [ConfigAPI.PARAM_CARDTYPEID]: this.state.cardTypeSelect.cardTypeId,
            [ConfigAPI.PARAM_TEMPLATE]: this.state.cardHolder,
            [ConfigAPI.PARAM_CARDHOLDER]: this.state.cardHolder,
            [ConfigAPI.PARAM_ISSUEDATE]: Utils.stringFromDate(new Date(this.state.issueDate), 'YYYY-MM-DD HH:mm:ss'),
            [ConfigAPI.PARAM_VALIDTILL]: this.state.validTill,
            [ConfigAPI.PARAM_PIN]: this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? this.state.PIN : "",
            [ConfigAPI.PARAM_ADDRESS]: this.state.address,
            [ConfigAPI.PARAM_PUBLICKEY]: this.state.publicKey,
            [ConfigAPI.PARAM_PUBLIC_KEY_HASHED]: this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT ? this.state.publicPrivateKeys.publicKeyHashed.trim() : "",
            [ConfigAPI.PARAM_CARDID]: Utils.isNull(this.state.cardUpdate) ? null : this.state.cardUpdate.id,
            [ConfigAPI.PARAM_ACTIVE]: 1,//update card 0 
            // [ConfigAPI.PARAM_TOKEN]: this.state.fcmToken,
            [ConfigAPI.PARAM_DEVICE_TYPE]: Utils.isIOS() ? "2" : "1"
        };
        console.log("_save", JSON.stringify(params));
        // return;
        let saveCard = new BaseService();
        saveCard.setParam(params);
        saveCard.setCallback(this);
        saveCard.requestData();
    }

    async onSuccess(code, message, data, method) {
        if (method == ConfigAPI.METHOD_ADD_CARD) {
            this.setState({ progressView: false });
            if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
                // console.log('add card: ' + JSON.stringify(data));
                this.setState({
                    address: data.address,
                    cardUpdate: data,
                    isChange: false
                });
                Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, this.state.publicPrivateKeys);
                this.popupDialogCreateCard.showSlideAnimationDialog(i18n.t('WELLET_ADD_SUCCESSFUL_BGT'));
            } else {
                Utils.showAlert(i18n.t('WELLET_ADD_SUCCESSFUL'), true, {
                    done: () => {
                        this.props.navigation.state.params.onBack();
                        this.props.navigation.goBack();
                    }
                });
            }
        } else if (method == ConfigAPI.METHOD_UPDATE_CARD) {
            this.setState({ progressView: false });

            if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGX) {
                if (this.state.cardUpdate != null && this.state.isEdit) {
                    Utils.showAlert(i18n.t('UPDATE_CARD_SUCCESSFULLY'), true, {
                        done: () => {
                            this.props.navigation.state.params.onBack();
                            this.props.navigation.goBack();
                        }
                    });
                } else {
                    Utils.showAlert(i18n.t('CREATE_CARD_SUCCESSFULLY'), true, {
                        done: () => {
                            this.props.navigation.state.params.onBack();
                            this.props.navigation.goBack();
                        }
                    });
                }
                return;
            }
            if (this.state.isGenerateKey) {
                for (var item of data) {
                    if (item.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
                        this.setState({
                            address: item.address,
                            isChange: false
                        }, () => {
                            this.popupDialogCreateCard.showSlideAnimationDialog(i18n.t('UPDATE_CARD_SUCCESSFULLY_BGT'));
                        });
                        Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, this.state.newPublicPrivateKeys);
                        //make transactions
                        // console.log('maketransactions item : ', JSON.stringify(item));
                        // console.log('oldPublicPrivateKeys : ', JSON.stringify(this.state.oldPublicPrivateKeys));
                        if (this.state.balance != 0) {
                            GetPublicPrivateKey.getMakeSignedPayload(
                                this.state.cardUpdate.address,
                                item.address,
                                this.state.cardUpdate.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt',
                                String(this.state.balance),
                                this.state.oldPublicPrivateKeys.privateKey,
                                (signPayload) => {
                                    let paramsPlayLoad = {
                                        [ConfigAPI.PARAM_ADDRESS_FROM]: this.state.cardUpdate.address,
                                        [ConfigAPI.PARAM_ADDRESS_TO]: item.address,
                                        [ConfigAPI.PARAM_TX_PAYLOAD]: parseFloat(this.state.balance),
                                        [ConfigAPI.PARAM_COIN_CODE]: this.state.cardUpdate.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt',
                                    }
                                    let params = {
                                        [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_MAKE_TRANSACTION,
                                        [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_MAKE_TRANSACTION,
                                        [ConfigAPI.PARAM_PAYLOAD]: paramsPlayLoad,
                                        [ConfigAPI.PARAM_SIGNED_PAYLOAD]: signPayload
                                    };
                                    console.log('getMakeSignedPayload params: ', JSON.stringify(params));
                                    let balance = new BaseService();
                                    balance.setParam(params);
                                    balance.setCallback(this);
                                    balance.requestDataPostGet(BaseService.METHOD.POST);
                                }, (error) => {
                                    Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
                                }
                            );
                        }
                        break;
                    }
                }
            } else {
                if (this.state.isEdit) {
                    Utils.showAlert(i18n.t('UPDATE_CARD_SUCCESSFULLY'), true, {
                        done: () => {
                            this.props.navigation.state.params.onBack();
                            this.props.navigation.goBack();
                        }
                    });
                } else {
                    // console.log('maketransactionsisGenerateKey : ', JSON.stringify(data));
                    for (var item of data) {
                        if (item.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
                            this.setState({
                                address: item.address,
                                cardUpdate: item,
                                isChange: false
                            });
                            Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, this.state.publicPrivateKeys);
                            this.popupDialogCreateCard.showSlideAnimationDialog(i18n.t('WELLET_ADD_SUCCESSFUL_BGT'));
                            break;
                        }
                    }
                }
            }
        } else if (method == ConfigAPI.METHOD_MAKE_TRANSACTION) {
            if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
                this.popupDialogCreateCard.showSlideAnimationDialog(i18n.t('UPDATE_CARD_SUCCESSFULLY_BGT'));
            } else {
                Utils.showAlert(i18n.t('UPDATE_CARD_SUCCESSFULLY'), true, {
                    done: () => {
                        this.props.navigation.state.params.onBack();
                        this.props.navigation.goBack();
                    }
                });
            }
        }
    }

    async onFail(code, message, method) {
        if (method == ConfigAPI.METHOD_ADD_CARD) {
            this.setState({
                dataSource: [],
                progressView: false
            });
        }
        this.setState({ progressView: false });
        Utils.showAlert(method + "\n" + message, true, null);
    }

    _enterData = (address, publicKey, privateKey) => {
        GetPublicPrivateKey.loadStringPrivateKey(privateKey, (priKey, publicKey, publicKeyHashed) => {
            if (this.state.publicKey != publicKey) {
                Utils.showAlert(i18n.t('IMPORT_KEY_ERROR'), true, null);
                return;
            }
            let dataKeys = {
                publicKey: publicKey.trim(),
                privateKey: priKey.trim(),
                publicKeyHashed: publicKeyHashed.trim(),
                isChange: true
            };

            this.setState({
                address: address,
                publicKey: publicKey.trim(),
                privateKey: priKey.trim(),
                newPublicPrivateKeys: dataKeys,
                isImportKey: true
                // publicPrivateKeys: dataKeys,
            });
            // Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, dataKeys);
        }, (error) => {
            Utils.showAlert(i18n.t('IMPORT_KEY_ERROR'), true, null);
        });
    }

    _importFile = (key, pathFile) => {
        GetPublicPrivateKey.loadFilePrivateKey(key, pathFile, (priKey, publicKey, publicKeyHashed) => {
            // console.log('loadFilePrivateKey : ' + publicKey);
            // console.log('loadFilePrivateKey old : ' + this.state.publicPrivateKeys.publicKey);

            if (this.state.publicKey != publicKey) {
                Utils.showAlert(i18n.t('IMPORT_KEY_ERROR'), true, null);
                return;
            }
            let dataKeys = {
                publicKey: publicKey.trim(),
                privateKey: priKey.trim(),
                publicKeyHashed: publicKeyHashed.trim()
            };
            this.setState({
                publicKey: publicKey.trim(),
                privateKey: priKey.trim(),
                newPublicPrivateKeys: dataKeys,
                isImportKey: true
            });
            // Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, dataKeys);
        }, (error) => {
            Utils.showAlert(i18n.t('IMPORT_KEY_ERROR'), true, null);
        });
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
        if (this.state.cardTypeSelect.cardTypeId == BGX_BGT_ACCOUNT.BGT) {
            GetPublicPrivateKey.loadStringPrivateKey(data.address, (priKey, publicKey, publicKeyHashed) => {
                if (this.state.publicKey != publicKey) {
                    Utils.showAlert(i18n.t('IMPORT_QRCODE_KEY_ERROR'), true, null);
                    return;
                }
                let dataKeys = {
                    publicKey: publicKey.trim(),
                    privateKey: priKey.trim(),
                    publicKeyHashed: publicKeyHashed.trim()
                };
                this.setState({
                    publicKey: publicKey.trim(),
                    privateKey: priKey.trim(),
                    newPublicPrivateKeys: dataKeys,
                    isImportKey: true
                });
                // Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, dataKeys);
            }, (error) => {
                Utils.showAlert(i18n.t('IMPORT_KEY_ERROR'), true, null);
            });
        } else {
            this.setState({
                address: data.address,
                isChange: true
            });
        }
    };

    _showGenerateQRCode = () => {
        if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.publicKey) {
            Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
            return;
        }
        this.popupDialogQRCode.showSlideAnimationDialog(this.state.publicPrivateKeys.privateKey);
    }

    _createCardYes = () => {
        this.popupExportKey.showSlideAnimationDialog();
        // this.props.navigation.state.params.onBack();
        // this.props.navigation.goBack();
    }

    _createCardNo = () => {
        this.props.navigation.state.params.onBack();
        this.props.navigation.goBack();
    }

    _handlerCancelQRCode = () => {
        if (this.state.isGenerateKey || !this.state.isEdit) {
            this.props.navigation.state.params.onBack();
            this.props.navigation.goBack();
        }
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
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
    view_content_picker: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    dropdown: {
        alignSelf: 'flex-end',
        width: appSize.width - 100,
    },
    dropdown_text: {
        fontSize: 20,
        color: 'white',
        textAlign: 'center',
        textAlignVertical: 'center',
    },
    dropdown_dropdown: {
        borderColor: '#181818',
        borderWidth: 2,
        borderRadius: 10,
        width: appSize.width - 100,
    },
    dropdown_separator: {
        height: 1,
        backgroundColor: '#181818',
    },
    dropdown_row_text: {
        marginHorizontal: 4,
        fontSize: 20,
        color: 'black',
        textAlignVertical: 'center',
        textAlign: 'center',
        textAlignVertical: 'center',
        marginTop: 10,
        marginBottom: 10
    },
    icon_down: {
        position: 'absolute',
        right: 0,
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
        bottom: 0,
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
        width: appSize.width - 100,
    },
    set_change_pin: {
        textDecorationLine: 'underline',
        fontSize: 16,
        marginTop: 10,
        marginLeft: 20
    },
});