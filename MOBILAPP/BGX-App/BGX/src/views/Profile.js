"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    ImageBackground,
    TouchableOpacity,
    Text,
    Image,
    KeyboardAvoidingView,
    StatusBar,
    ScrollView,
    TextInput,
    Alert,
    Keyboard,
    TouchableWithoutFeedback
} from "react-native";

import ConfigAPI from "../api/ConfigAPI";
import * as Constants from '../common/Constants';
import { BaseService } from "../api/BaseService";
import { GetAddressService } from '../api/GetAddressService';
import Utils from "../common/Utils";
import { TextField } from 'react-native-material-textfield';
import * as CommonStyleSheet from '../common/StyleSheet';
import AvatarView from '../components/AvatarView';
import BorderButton from '../components/BorderButton';
import InputText from "../components/InputText";
import GetPublicPrivateKey from '../nativeFunction/GetPublicPrivateKey';
import CheckCameraAndPhotoHelper from '../nativeFunction/CheckCameraAndPhotoHelper';
import LoadingIndicator from "../components/LoadingIndicator";
import { CountryNameService } from "../api/CountryNameService";
import PopupDialogInput from '../components/PopupDialogInput';
import TextComponent from '../components/TextComponent';
import FCM, { FCMEvent, RemoteNotificationResult, WillPresentNotificationResult, NotificationType } from "react-native-fcm";
import RNFetchBlob from 'react-native-fetch-blob';
import PopupImagePicker from '../components/PopupImagePicker';

import Permissions from 'react-native-permissions';

import i18n from '../translations/i18n';

var md5 = require('md5');
var ImagePicker = require('react-native-image-picker');

const ENUM_KEY_INPUT = {
    USER_NAME: 'user_name',
    PHONE: 'phone',
    DEC_ACCOUNT: 'dec_account'
};

var options = {
    title: "Choose Avatar",
    cancelButtonTitle: "Cancel",
    takePhotoButtonTitle: "Take Photo",
    chooseFromLibraryButtonTitle: "Choose photo from library",
    maxWidth: 300,
    maxHeight: 300,
    mediaType: 'photo',
    storageOptions: {
        skipBackup: true,
        path: 'images'
    }
};

const DismissKeyboard = ({ children }) => {
    return (
        <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
            {children}
        </TouchableWithoutFeedback>
    );
}

class Profile extends Component {

    constructor(props) {
        super(props);

        this.state = {
            avatarSource: null,
            email: "",
            userName: "",
            phone: "",
            password: "",
            location: "",
            ethAddress: "",
            bgtAddress: "",
            bBalance: "0",
            bgxBalance: "0",
            keyboardType: Utils.isAndroid() ? 'email-address' : 'numbers-and-punctuation',
            progressView: false
        };

        this.avatarSource = null;
        this.userName = "";
        this.phone = "";
        this.ethAddress = "";
        this.bgxAccount = "";

        this.isSelectPhoto = false;
        this.isActive = 0;

        this.fcmToken = null;

        this.isGetUserProfile = false;
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    async handleBackEvent() {
        let password = await Utils.getDataWithKey(Constants.KEY_PASSWORD);
        this.setState({
            password: String(password)
        });
    }

    async componentDidMount() {
        let password = await Utils.getDataWithKey(Constants.KEY_PASSWORD);
        let account = await Utils.getDataWithKey(Constants.KEY_USER);
        this.fcmToken = await Utils.getDataWithKey(Constants.KEY_PUSH_NOTIFICATION);

        this._setupFCM();

        if (account != null) {
            this.setState({
                avatarSource: account.avatar == "" ? null : account.avatar,
                email: account.email,
                userName: account.username,
                phone: account.phone,
                password: String(password),
                ethAddress: account.ethereumAddress,
                bgtAddress: account.bgtAddress == null ? "" : account.bgtAddress
            });

            this.avatarSource = account.avatar == "" ? null : account.avatar;
            this.userName = account.username;
            this.phone = account.phone;
            this.ethAddress = account.ethereumAddress;
            this.isActive = account.active;
            this.bgxAccount = account.BGXAccount == null ? "" : account.BGXAccount
        }

        this.requestGetLocation();
        
        //get DEC balance
        if (Utils.isValidString(this.state.ethAddress) && Utils.checkEtherAddressValid(this.state.ethAddress)) {
            
            this.requestGetBGTBalance(true, this.state.ethAddress);
        }

        //get btBalance
        if (Utils.isValidString(this.state.bgtAddress)) {
            this.requestGetBGTBalance(false, this.state.bgtAddress);
        }

        this.isGetUserProfile = true;
        this.requestLogin();
    }

    _setupFCM = async () => {
        var account = await Utils.getDataWithKey(Constants.KEY_USER);

        FCM.on(FCMEvent.Notification, async(notif) => {
            
            account = await Utils.getDataWithKey(Constants.KEY_USER);
            
            if (account == null) {
                return;
            }

            if (notif == null) {
                return;
            }

            let data = notif.data;

            if (data == null) {
                return;
            }

            let dataObject = JSON.parse(data);
            let type = dataObject.type;

            if (type == null) {
                return;
            }

            if (type == 'update_profile') {
                var avatar = dataObject.profile.avatar;
                if (avatar == null || avatar == "") {
                    avatar = null;
                }

                this.setState({
                    avatarSource: avatar,
                    userName: dataObject.profile.username,
                    phone: dataObject.phone,
                    ethAddress: dataObject.profile.ethereumAddress,
                    bgtAddress: dataObject.profile.bgtAddress
                });

                //get DEC balance
                if (Utils.isValidString(this.state.ethAddress) && Utils.checkEtherAddressValid(this.state.ethAddress)) {
                    this.requestGetBGTBalance(true, this.state.ethAddress);
                }

                //get BGT balance
                if (Utils.isValidString(this.state.bgtAddress)) {
                    this.requestGetBGTBalance(false, this.state.bgtAddress);
                }

                Utils.saveDataWithKey(Constants.KEY_USER, dataObject.profile);
            }
        });
    }

    componentWillUnmount() {
        Utils.dismissKeyboard();
    }

    buttonChooseAvatar_clicked = () => {
        this.popupImagePicker.showSlideAnimationDialog();
    }

    onGetAddress = (data) => {
        this.setState({
            ethAddress: data.address
        });

        if (Utils.isValidString(data.address) && Utils.checkEtherAddressValid(this.state.ethAddress)) {
            this.requestGetBGTBalance(true, this.state.ethAddress);
        }
    };

    buttonMenu_clicked = () => {
        this.props.navigation.openDrawer();
    }

    buttonChangePassword_clicked = () => {
        this.props.navigation.navigate('ChangePasswordView', { back: () => this.handleBackEvent() });
    }

    buttonBarcode_clicked = () => {

        Permissions.check('camera').then(response => {
            // Response is one of: 'authorized', 'denied', 'restricted', or 'undetermined'
            if (Utils.isIOS()) {
                if (response === 'authorized' || response === 'undetermined') {
                    this.props.navigation.navigate('QRCodeView', { getAddress: (address) => this.onGetAddress(address) });
                } else {
                    Alert.alert(
                        '',
                        i18n.t('MESSAGE_ALLOW_CAMERA_TO_SCAN_QR'),
                        [
                            { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                            { text: i18n.t('CANCEL') },
                        ],
                        { cancelable: false }
                    );
                }
            } else {
                if (response === 'authorized') {
                    this.props.navigation.navigate('QRCodeView', { getAddress: (address) => this.onGetAddress(address) });
                } else {
                    Alert.alert(
                        '',
                        i18n.t('MESSAGE_ALLOW_CAMERA_TO_SCAN_QR'),
                        [
                            { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                            { text: i18n.t('CANCEL') },
                        ],
                        { cancelable: false }
                    );
                }
            }

        });
    }

    buttonCancel_clicked = () => {
        Utils.dismissKeyboard();
        setTimeout(() => {

            if (this.state.userName == null || this.state.userName == "") {
                Utils.showAlert(i18n.t('MESSAGE_INPUT_USERNAME'), true, null);
                return;
            }

            Alert.alert(
                '',
                i18n.t('MESSAGE_DO_YOU_WANT_CANCEL_EDIT_PROFILE'),
                [
                    { text: i18n.t('OK'), onPress: () => this.requestLogin() },
                    { text: i18n.t('CANCEL') },
                ],
                { cancelable: false }
            );
        }, 100);
    }

    _goBackFuction = () => {
        if (this.isActive == 0) {
            Utils.resetNavigate(this.props.navigation, 'DigitalSpotView');
        } else {
            this.props.navigation.goBack();
        }
    }

    _goToSettingFunction = () => {
        CheckCameraAndPhotoHelper.goToCameraPhotoSetting((status) => {
        });
    }

    buttonSave_clicked = () => {
        Utils.dismissKeyboard();

        var isTrue = true;

        setTimeout(() => {

            if (this.state.userName == null || this.state.userName == "") {
                Utils.showAlert(i18n.t('MESSAGE_INPUT_USERNAME'), true, null);
                return;
            }

            if (this.state.ethAddress != null && this.state.ethAddress != "" && this.state.ethAddress.length >= 2) {
                if (this.state.ethAddress.substring(0, 2) != "0x") {
                    this.setState({
                        ethAddress: "0x" + this.state.ethAddress
                    }, () => {
                        if (!Utils.checkEtherAddressValid(this.state.ethAddress)) {
                            Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                            isTrue = false;
                            return;
                        }
                    })
                } else if (!Utils.checkEtherAddressValid(this.state.ethAddress)) {
                    Utils.showAlert(i18n.t('MESSAGE_ETH_WRONG_FORMAT'), true, null);
                    isTrue = false;
                    return;
                }
            }

            if (!isTrue) {
                return;
            }

            if (this.avatarSource == this.state.avatarSource
                && this.userName == this.state.userName
                && this.phone == this.state.phone
                && this.ethAddress == this.state.ethAddress) {

                Alert.alert(
                    '',
                    i18n.t('MESSAGE_NO_CHANGES_HAVE_BEEN_MADE'),
                    [
                        { text: i18n.t('OK'), onPress: () => this.requestLogin() },
                    ],
                    { cancelable: false }
                );
            } else {
                this.requestUpdateProfile();
            }
        }, 100);
    }

    _showPopupInput = (key, title, text) => {
        if (key == ENUM_KEY_INPUT.USER_NAME || key == ENUM_KEY_INPUT.PHONE || key == ENUM_KEY_INPUT.DEC_ACCOUNT) { // BGT
            if (key === ENUM_KEY_INPUT.PHONE) {
                this.setState({
                    keyboardType: 'phone-pad'
                });
            } else {
                this.setState({
                    keyboardType: Utils.isAndroid() ? 'email-address' : 'numbers-and-punctuation'
                });
            }

            this.setState({ titlePopupInput: title });
            this.popupDialogInput.showSlideAnimationDialog(key, text);
        }
    }

    _handlerInput = (key, text) => {
        switch (key) {
            case ENUM_KEY_INPUT.USER_NAME: {
                this.setState({ userName: text });
                break;
            }
            case ENUM_KEY_INPUT.PHONE: {
                this.setState({ phone: text });
                break;
            }
            case ENUM_KEY_INPUT.DEC_ACCOUNT: {
                this.setState({ ethAddress: text });
                break;
            }
        }
    }

    imagePickerSelectIndex = (index) => {
        if (index == 0) {

            Permissions.check('camera').then(response => {
                // Response is one of: 'authorized', 'denied', 'restricted', or 'undetermined'
                if (Utils.isIOS()) {
                    if (response === 'authorized' || response === 'undetermined') {
                        ImagePicker.launchCamera(options, (response) => {
                            if (response.didCancel) {
                                //   console.log('User cancelled image picker');
                            }
                            else if (response.error) {
                                //   console.log('ImagePicker Error: ', response.error);
                            }
                            else if (response.customButton) {
                                //   console.log('User tapped custom button: ', response.customButton);
                            }
                            else {
                                this.isSelectPhoto = true;
                                let source = { uri: 'data:image/jpeg;base64,' + response.data };
                                this.setState({
                                    avatarSource: source
                                });
                                // this.isSelectPhoto = false;
                            }
                        });
                    } else {
                        Alert.alert(
                            '',
                            i18n.t('MESSAGE_ALLOW_CAMERA_TO_TAKE_PHOTO'),
                            [
                                { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                                { text: i18n.t('CANCEL') },
                            ],
                            { cancelable: false }
                        );
                    }
                } else {
                    setTimeout(() => {
                        if (response === 'authorized') {
                            // alert(1)
                            ImagePicker.launchCamera(options, (response) => {
                                if (response.didCancel) {
                                    //   console.log('User cancelled image picker');
                                }
                                else if (response.error) {
                                    //   console.log('ImagePicker Error: ', response.error);
                                }
                                else if (response.customButton) {
                                    //   console.log('User tapped custom button: ', response.customButton);
                                }
                                else {
                                    this.isSelectPhoto = true;
                                    let source = { uri: 'data:image/jpeg;base64,' + response.data };
                                    this.setState({
                                        avatarSource: source
                                    });
                                    // this.isSelectPhoto = false;
                                }
                            });
                        } else {
                            Alert.alert(
                                '',
                                i18n.t('MESSAGE_ALLOW_CAMERA_TO_TAKE_PHOTO'),
                                [
                                    { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                                    { text: i18n.t('CANCEL') },
                                ],
                                { cancelable: false }
                            );
                        }
                    }, 100);
                }

            });
        } else {
            
            setTimeout(() => {
                Permissions.check('photo').then(response => {
                    // Response is one of: 'authorized', 'denied', 'restricted', or 'undetermined'
                    if (Utils.isIOS()) {
                        if (response === 'authorized' || response === 'undetermined') {
                            ImagePicker.launchImageLibrary(options, (response) => {
                                if (response.didCancel) {
                                    //   console.log('User cancelled image picker');
                                }
                                else if (response.error) {
                                    //   console.log('ImagePicker Error: ', response.error);
                                }
                                else if (response.customButton) {
                                    //   console.log('User tapped custom button: ', response.customButton);
                                }
                                else {
                                    this.isSelectPhoto = true;
                                    let source = { uri: 'data:image/jpeg;base64,' + response.data };
                                    this.setState({
                                        avatarSource: source
                                    });
                                    // this.isSelectPhoto = false;
                                }
                            });
                        } else {
                            Alert.alert(
                                '',
                                i18n.t('MESSAGE_ALLOW_PHOTO_LIBRARY'),
                                [
                                    { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                                    { text: i18n.t('CANCEL') },
                                ],
                                { cancelable: false }
                            );
                        }
                    } else {
                        if (response === 'authorized') {
                            ImagePicker.launchImageLibrary(options, (response) => {
                                if (response.didCancel) {
                                    //   console.log('User cancelled image picker');
                                }
                                else if (response.error) {
                                    //   console.log('ImagePicker Error: ', response.error);
                                }
                                else if (response.customButton) {
                                    //   console.log('User tapped custom button: ', response.customButton);
                                }
                                else {
                                    this.isSelectPhoto = true;
                                    let source = { uri: 'data:image/jpeg;base64,' + response.data };
                                    this.setState({
                                        avatarSource: source
                                    });
                                    // this.isSelectPhoto = false;
                                }
                            });
                        } else {
                            Alert.alert(
                                '',
                                i18n.t('MESSAGE_ALLOW_PHOTO_LIBRARY'),
                                [
                                    { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                                    { text: i18n.t('CANCEL') },
                                ],
                                { cancelable: false }
                            );
                        }
                    }
                });
            }, 100);
        }
    }

    //Request Data
    requestGetLocation = () => {
        let service = new CountryNameService()
        service.setCallback(this);
        service.requestData();
    }

    requestUpdateProfile = () => {
        this.setState({ progressView: true });
        let service = new BaseService();
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_UPDATE_PROFILE,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.email,
            [ConfigAPI.PARAM_PASSWORD]: md5(this.state.password),
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: md5(this.state.password),
            [ConfigAPI.PARAM_USERNAME]: this.state.userName,
            [ConfigAPI.PARAM_PHONE]: this.state.phone,
            [ConfigAPI.PARAM_ETHER_ADDRESS]: this.state.ethAddress,
            [ConfigAPI.PARAM_BGX_ACCOUNT]: this.bgxAccount,
            [ConfigAPI.PARAM_BGT_ADDRESS]: this.state.bgtAddress,
            [ConfigAPI.PARAM_TOKEN]: this.fcmToken
        };

        if (this.state.avatarSource != null) {
            params = { [ConfigAPI.PARAM_AVATAR]: this.state.avatarSource.uri, ...params };
        }

        service.setParam(params);
        service.setCallback(this);
        service.requestData();
    }

    requestLogin = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LOGIN,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.email,
            [ConfigAPI.PARAM_PASSWORD]: md5(this.state.password),
        };
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    requestGetBGTBalance = (isDEC, address) => {
        
        let params = {
            [ConfigAPI.PARAM_METHOD]: isDEC ? ConfigAPI.METHOD_GET_ADDRESS : ConfigAPI.METHOD_BGT_GET_BALANCE,
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', address),
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    //Request Data Success - Fail
    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_COUNTRY_NAME) {
            this.setState({
                location: data.toUpperCase()
            });
            this.setState({ progressView: false });
        } else if (method === ConfigAPI.METHOD_UPDATE_PROFILE) {
            this.setState({ progressView: false });

            Utils.saveDataWithKey(Constants.KEY_USER, data);

            if (data.active == "0") {
                Alert.alert(
                    '',
                    i18n.t('MESSAGE_UPDATE_PROFILE_SUCCESSFULLY'),
                    [
                        { text: i18n.t('OK'), onPress: () => this.buttonMenu_clicked() },
                    ],
                    { cancelable: false }
                );
            } else {
                Alert.alert(
                    '',
                    i18n.t('MESSAGE_UPDATE_PROFILE_SUCCESSFULLY'),
                    [
                        { text: i18n.t('OK'), onPress: () => this._goBackFuction() },
                    ],
                    { cancelable: false }
                );
            }
            this.setState({ progressView: false });
        } else if (method == ConfigAPI.METHOD_LOGIN) {
            Utils.saveDataWithKey(Constants.KEY_USER, data);

            if (this.isGetUserProfile) {
                this.isGetUserProfile = false;
                Utils.saveDataWithKey(Constants.KEY_USER, data);

                if (data != null) {
                    this.setState({
                        avatarSource: data.avatar == "" ? null : data.avatar,
                        email: data.email,
                        userName: data.username,
                        phone: data.phone,
                        ethAddress: data.ethereumAddress,
                        bgtAddress: data.bgtAddress == null ? "" : data.bgtAddress
                    });
        
                    this.avatarSource = data.avatar == "" ? null : data.avatar;
                    this.userName = data.username;
                    this.phone = data.phone;
                    this.ethAddress = data.ethereumAddress;
                    this.isActive = data.active;
                    this.bgxAccount = data.BGXAccount == null ? "" : data.BGXAccount
                }
        
                //get DEC balance
                if (Utils.isValidString(this.state.ethAddress) && Utils.checkEtherAddressValid(this.state.ethAddress)) {
                    this.requestGetBGTBalance(true, this.state.ethAddress);
                }
        
                //get btBalance
                if (Utils.isValidString(this.state.bgtAddress)) {
                    this.requestGetBGTBalance(false, this.state.bgtAddress);
                }

                return;
            } 

            if (data.active == "0") {
                this.buttonMenu_clicked();
            } else {
                this._goBackFuction();
            }
            this.setState({ progressView: false });
        } else if (method === ConfigAPI.METHOD_GET_ADDRESS) {
            if (data == "0") {
                this.setState({
                    bgxBalance: 0
                });
            } else {
                this.setState({
                    bgxBalance: Utils.decimalValue(data / 1000000000000000000, 2)
                });
            }
            this.setState({ progressView: false });
        } else if (method === ConfigAPI.METHOD_BGT_GET_BALANCE) {
            let number = data;
            number = Utils.decimalValue(number, 2);
            if (data % 1 == 0) {
                number = parseInt(data, 10);
            }
            this.setState({ bBalance: number });
            // this.setState({
            //     bBalance: Utils.decimalValue(data, 2)
            // });
            this.setState({ progressView: false });
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        // alert(message)
    }

    render() {
        StatusBar.setBarStyle('light-content', true);
        return (
            // <View style={[{flex: 1}]}>
            <DismissKeyboard>
                <ImageBackground style={[{ flex: 1 }]}
                    source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark_copy.png") : null}>
                    <View style={styles.viewHeader}>
                        <ImageBackground style={styles.imageBackgroudHeader}
                            source={require("../resource/titleBackground.png")}>
                            <View style={styles.viewHeaderTitle}>
                                <TouchableOpacity style={[{ zIndex: 1 }]} onPress={this.buttonMenu_clicked}>
                                    <Image style={styles.iconMenu} source={require("../resource/icon_menu.png")} />
                                </TouchableOpacity>
                                <Text style={styles.textTitleHeader}>{i18n.t('PROFILE')}</Text>
                            </View>
                        </ImageBackground>
                        <View style={styles.viewAvatar}>
                            <AvatarView containStyle={styles.viewAvatarItem} avatarSource={this.state.avatarSource} backgroundColor={CommonStyleSheet.THEME_DARCK ? 'rgba(0, 0, 0, 0.5)' : 'rgba(244, 244, 244, 0.5)'} isSelectPhoto={this.isSelectPhoto} />
                            <View style={styles.viewBalance}>
                                <Text numberOfLines={1} style={styles.textBalance}>{this.state.bBalance}<Text style={[styles.textCurrency, { fontSize: 21, color: '#de113e' }]}>{' ' + i18n.t('BETA_SYMBOL')}</Text></Text>
                                <Text style={[styles.textBalance, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }]}>{this.state.bgxBalance}<Text style={styles.textCurrency}> DEC</Text></Text>
                            </View>
                        </View>

                        <TouchableOpacity style={styles.buttonEdit} onPress={this.buttonChooseAvatar_clicked}>
                            <Image style={styles.iconEdit} source={require("../resource/icon_edit.png")} />
                        </TouchableOpacity>


                    </View>

                    <View style={styles.viewEdit}>

                        <ScrollView style={{ flex: 1 }} showsVerticalScrollIndicator={false}>
                            {/* Email */}
                            <InputText
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={[styles.input, { marginTop: 0, color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }]}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }}
                                label={i18n.t('EMAIL') + ":"}
                                value={this.state.email}
                                secureTextEntry={false}
                                editable={false}
                                selectable
                                selectTextOnFocus={true}
                                onChangeText={(email) => this.setState({ email })}
                            />

                            {/* User Name */}
                            <TextComponent
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={styles.input}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }}
                                label={i18n.t('USER_NAME') + ":"}
                                value={this.state.userName}
                                secureTextEntry={false}
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.USER_NAME, i18n.t('INPUT_USERNAME'), this.state.userName)}
                            />

                            {/* Phone */}
                            <TextComponent
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={styles.input}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }}
                                label={i18n.t('PHONE') + ":"}
                                value={this.state.phone}
                                secureTextEntry={false}
                                keyboardType='phone-pad'
                                onPress={() => this._showPopupInput(ENUM_KEY_INPUT.PHONE, i18n.t('INPUT_PHONE'), this.state.phone)}
                            />

                            {/* Password */}
                            <InputText
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={[styles.input, { color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }]}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }}
                                label={i18n.t('PASSWORD') + ":"}
                                value={this.state.password}
                                secureTextEntry={true}
                                editable={false}
                            />

                            <TouchableOpacity onPress={this.buttonChangePassword_clicked}>
                                <Text style={[styles.textChangePassword, { color: CommonStyleSheet.THEME_DARCK ? '#b7b7b7' : '#111111' }]}>{i18n.t('CHANGE_PASSWORD')}</Text>
                            </TouchableOpacity>

                            {/* Location */}
                            <InputText
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={[styles.input, { color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }]}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }}
                                label={i18n.t('LOCATION') + ":"}
                                value={this.state.location.toUpperCase()}
                                secureTextEntry={false}
                                editable={false}
                            />

                            {/* ETH Address */}
                            <View style={styles.viewETHAddress}>

                                <View style={[{ flex: 1 }]}>

                                    <Text style={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}>{i18n.t('ETH_ADDRESS') + ":"}</Text>
                                    <TouchableOpacity style={{ flex: 1 }}
                                        onPress={() => this._showPopupInput(ENUM_KEY_INPUT.DEC_ACCOUNT, i18n.t('INPUT_DEC_ACCOUNT'), this.state.ethAddress)}>
                                        <Text style={[styles.textInput, { marginTop: 0, color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }]}
                                            numberOfLines={1}>
                                            {this.state.ethAddress}
                                        </Text>
                                    </TouchableOpacity>
                                </View>

                                <TouchableOpacity onPress={this.buttonBarcode_clicked}>
                                    <Image style={styles.imageBarcode} source={CommonStyleSheet.THEME_DARCK ? require("../resource/barcodeButton_dark.png") : require("../resource/barcodeButton_white.png")} />
                                </TouchableOpacity>

                            </View>
                            <View style={styles.viewLine} />

                            {/* BGX Account */}
                            {/* <InputText
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={[styles.input, { color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111' }]}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595', }}
                                label={i18n.t('BGX_ACCOUNT') + ":"}
                                value={this.state.bgxAccount}
                                secureTextEntry={false}
                                editable={false}
                                numberOfLines={2}
                                ellipsizeMode={'tail'}
                                textAlign={'left'}
                            /> */}

                             <TextComponent
                                labelStyle={[styles.label, { color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }]}
                                style={styles.input}
                                label={i18n.t('BGX_ACCOUNT') + ":"}
                                value={this.state.bgtAddress}
                                secureTextEntry={false}
                                styleContent={{ color: CommonStyleSheet.THEME_DARCK ? '#707070' : '#959595' }}
                                disabled={true}
                            // onPress={() => this._showPopupInput(ENUM_KEY_INPUT.PRIVATEKEY, this.state.privateKey)}
                            />

                            {!Utils.isValidString(this.state.bgtAddress) ?
                                <Text style={{ marginTop: 5, color: '#b20000' }}>{i18n.t('CARD_BGT_EMPTY')}</Text>
                                : null
                            }

                        </ScrollView>
                    </View>

                    <View style={styles.viewButton}>
                        {/* Cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this.buttonCancel_clicked}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.buttonCancel, ]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button_dialog_1}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_cancel_black.png") : require("../resource/buttonNew/icon_cancel_2_white.png")}
                        />

                        {/* Save button */}
                        <BorderButton
                            title={i18n.t('SAVE')}
                            onPress={this.buttonSave_clicked}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.buttonSave]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button_dialog_1}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_ok_black.png") : require("../resource/buttonNew/icon_ok_1_white.png")}
                        />
                    </View>

                    <PopupDialogInput
                        ref={(popupDialogInput) => { this.popupDialogInput = popupDialogInput; }}
                        title={this.state.titlePopupInput}
                        keyboardType={this.state.keyboardType}
                        onChangeText={this._handlerInput.bind(this)}
                    />

                    <PopupImagePicker
                        ref={(popupImagePicker) => { this.popupImagePicker = popupImagePicker; }}
                        imagePickerSelectIndex={this.imagePickerSelectIndex}
                    />

                    {/* load progress */}
                    {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
                </ImageBackground>
            </DismissKeyboard>
        );
    }
}

export default Profile;

const styles = StyleSheet.create({
    viewHeader: {
        width: '100%',
        height: 225,
        zIndex: 1
    },
    imageBackgroudHeader: {
        width: '100%',
        height: 150
    },
    viewHeaderTitle: {
        width: '100%',
        flexDirection: 'row',
        marginTop: 30
    },
    iconMenu: {
        width: 22,
        height: 16,
        marginLeft: 20
    },
    textTitleHeader: {
        color: 'white',
        fontSize: 26,
        fontWeight: '300',
        alignSelf: 'baseline',
        flex: 1,
        marginLeft: -42,
        textAlign: 'center',
        marginTop: -5
    },
    viewAvatar: {
        width: '100%',
        position: 'absolute',
        flexDirection: 'row',
        marginTop: 75
    },
    viewAvatarItem: {
        marginLeft: 30,
        width: 150,
        height: 150
    },
    viewBalance: {
        width: '100%',
        height: 60,
        marginLeft: 25,
        alignSelf: 'center',
        justifyContent: 'space-between',
    },
    textBalance: {
        color: 'white',
        fontSize: 22,
        fontWeight: '600'
    },
    textCurrency: {
        fontSize: 18,
        fontWeight: '300',
        color: '#7d7d7d'
    },
    buttonEdit: {
        position: 'absolute',
        marginTop: 90,
        marginLeft: 145
    },
    iconEdit: {
        width: 34,
        height: 34
    },
    viewEdit: {
        flex: 1,
        marginLeft: 25,
        width: '80%',
        marginTop: 10
    },
    input: {
        marginTop: 15
    },
    buttonCancel: {
        marginTop: 20,
        width: 120,
        height: 40,
        borderRadius: 20,
        marginRight: 30
    },
    buttonSave: {
        marginTop: 20,
        width: 120,
        height: 40,
        borderRadius: 20
    },
    textInput: {
        height: 25,
        marginRight: 10,
        fontSize: 15,
        marginTop: -5,
        flex: 1,
        padding: 0
    },
    viewLine: {
        backgroundColor: '#cfcfd2',
        width: '100%',
        height: 1,
        marginBottom: 3
    },
    label: {
        fontSize: CommonStyleSheet.fontSize,
    },
    textChangePassword: {
        textDecorationLine: 'underline',
        marginTop: 10
    },
    viewETHAddress: {
        flexDirection: 'row',
        flex: 1,
        justifyContent: 'space-between',
        height: Utils.isIOS() ? 40 : 50,
        marginTop: 15
    },
    imageBarcode: {
        width: 40,
        height: 40,
        marginTop: Utils.isIOS() ? 0 : 10
    },
    viewButton: {
        marginLeft: 25,
        height: 60,
        width: '80%',
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 10
    },
    progress_view: {
        justifyContent: "center",
        position: "absolute",
    },
});