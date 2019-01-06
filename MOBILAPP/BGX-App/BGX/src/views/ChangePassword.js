"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    Text,
    Image,
    TextInput,
    KeyboardAvoidingView,
    ProgressBarAndroid,
    TouchableOpacity,
    Alert
} from "react-native";

import ConfigAPI from "../api/ConfigAPI";
import { NavigationActions, StackActions } from 'react-navigation';
import Utils from "../common/Utils";
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import LoadingIndicator from "../components/LoadingIndicator";
import InputText from "../components/InputText";
import { BaseService } from '../api/BaseService';
import * as Constants from '../common/Constants';
var md5 = require('md5');
import i18n from '../translations/i18n';

export default class ChangePassword extends Component {

    constructor(props) {
        super(props);
        this.state = {
            currentPassword: "",
            newPassword: "",
            confirmPassword: "",
            progressView: false
        };

        this.localPassword = "";
        this.getLocalPassword();
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    buttonBack_clicked = () => {
        this.props.navigation.state.params.back();
        this.props.navigation.goBack();
    }

    buttonChangePassword_clicked = () => {
        Utils.dismissKeyboard();

        if (this.state.currentPassword == "") {
            Utils.showAlert(i18n.t('MESSAGE_INPUT_CURRENT_PASSWORD'), true, null);
            return;
        }

        if (!Utils.checkValidPassword(this.state.currentPassword)) {
            Utils.showAlert(i18n.t('MESSAGE_PASSWORD_6_CHARACTERS'), true, null);
            return;
        }

        if (this.state.newPassword == "") {
            Utils.showAlert(i18n.t('MESSAGE_INPUT_NEW_PASSWORD'), true, null);
            return;
        }

        if (!Utils.checkValidPassword(this.state.newPassword)) {
            Utils.showAlert(i18n.t('MESSAGE_PASSWORD_6_CHARACTERS'), true, null);
            return;
        }

        if (this.state.confirmPassword == "") {
            Utils.showAlert(i18n.t('MESSAGE_INPUT_CONFIRM_PASSWORD'), true, null);
            return;
        }

        if (!Utils.checkValidPassword(this.state.confirmPassword)) {
            Utils.showAlert(i18n.t('SIGNUP_PASSWORD_6_SYMBOL'), true, null);
            return;
        }

        if (this.state.currentPassword != this.localPassword) {
            Utils.showAlert(i18n.t('MESSAGE_CURRENT_PASSWORD_DOES_NOT_MATCH'), true, null);
            return;
        }

        if (this.state.newPassword != this.state.confirmPassword) {
            Utils.showAlert(i18n.t('MESSAGE_NEW_PASSWORD_DOES_NOT_MATCH'), true, null);
            return;
        }

        if (this.state.newPassword.trim().length == 0 && this.state.confirmPassword.trim().length == 0) {
            Utils.showAlert(i18n.t('SIGNUP_ONLY_SPACES'), true, null);
            return;
        }

        this.requestUpdatePassword();
    }

    async getLocalPassword() {
        this.localPassword = await Utils.getDataWithKey(Constants.KEY_PASSWORD);
    }

    async requestUpdatePassword() {

        let account = await Utils.getDataWithKey(Constants.KEY_USER);
        let fcmToken = await Utils.getDataWithKey(Constants.KEY_PUSH_NOTIFICATION);

        let service = new BaseService();
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_UPDATE_PROFILE,
            [ConfigAPI.PARAM_EMAIL]: account.email,
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: md5(this.state.currentPassword),
            [ConfigAPI.PARAM_PASSWORD]: md5(this.state.newPassword),
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USERNAME]: account.username,
            [ConfigAPI.PARAM_PHONE]: account.phone,
            [ConfigAPI.PARAM_ETHER_ADDRESS]: account.ethereumAddress,
            [ConfigAPI.PARAM_BGX_ACCOUNT]: account.BGXAccount,
            [ConfigAPI.PARAM_TOKEN]: this.fcmToken
        };

        this.setState({ progressView: true });
        service.setParam(params);
        service.setCallback(this);
        service.requestData();
    }

    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_UPDATE_PROFILE) {
            Utils.saveDataWithKey(Constants.KEY_PASSWORD, this.state.newPassword);
            this.getLocalPassword();
            this.setState({ progressView: false });
            Alert.alert(
                '',
                'Change password successfully',
                [
                    { text: 'OK', onPress: () => this.buttonBack_clicked() },
                ],
                { cancelable: false }
            )
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        setTimeout(() => {
            Utils.showAlert(message, true, null);
        }, 100);
    }

    render() {
        return (

            <View style={styles.container}>
                {/* Background */}
                <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")} style={styles.background} />
                <View style={styles.viewTitle}>
                    <TouchableOpacity style={styles.backButton} onPress={this.buttonBack_clicked}>
                        <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_back_white.png") : require("../resource/icon_back.png")} style={styles.imageBack} />
                    </TouchableOpacity>
                    <View style={[{ flex: 1 }]}>
                        <Text style={[styles.textTitle, {color: CommonStyleSheet.THEME_DARCK ? 'white' : '#111111'}]}>{i18n.t('CHANGE_PASSWORD')}</Text>
                    </View>
                </View>
                <View style={styles.content}>

                    {/* Logo */}
                    <Text style={[styles.logo, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#686868'}]}>{i18n.t('TO_FILL_THE_PASSWORD')}</Text>
                    <KeyboardAvoidingView behavior="padding" enabled>
                        {/* Current password */}
                        <InputText
                            labelStyle={styles.label}
                            style={styles.input}
                            textInputStyle={{color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}}
                            label={i18n.t('CURRENT_PASSWORD')}
                            value={this.state.currentPassword}
                            secureTextEntry={true}
                            onChangeText={(currentPassword) => this.setState({ currentPassword })}
                        />

                        {/* password */}
                        <InputText
                            labelStyle={styles.label}
                            style={styles.input}
                            textInputStyle={{color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}}
                            label={i18n.t('NEW_PASSWORD')}
                            value={this.state.newPassword}
                            secureTextEntry={true}
                            onChangeText={(newPassword) => this.setState({ newPassword })}
                        />

                        {/* Confirm password */}
                        <InputText
                            labelStyle={styles.label}
                            style={styles.input}
                            textInputStyle={{color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}}
                            label={i18n.t('CONFIRM_NEW_PASSWORD')}
                            value={this.state.confirmPassword}
                            secureTextEntry={true}
                            onChangeText={(confirmPassword) => this.setState({ confirmPassword })}
                        />
                    </KeyboardAvoidingView>

                    {/* Singup button {backgroundColor: CommonStyleSheet.THEME_DARCK ? 'white' : '#252525'}*/}
                    <BorderButton
                        title={i18n.t('CHANGE_PASSWORD')}
                        onPress={this.buttonChangePassword_clicked}
                        titleStyle={[styles.titleChangePassword, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                        style={[styles.buttonChangePassword]}
                        imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                        background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}
                    />

                </View>

                {/* load progress */}
                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        justifyContent: "center",
        alignItems: 'center',
    },
    content: {
        position: "absolute",
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
        marginLeft: 25,
        marginRight: 25,
        alignItems: 'center'
    },
    viewTitle: {
        width: '100%',
        height: 50,
        flexDirection: 'row',
        alignItems: 'center',
        top: 20,
        position: 'absolute'
    },
    backButton: {
        height: '100%',
        justifyContent: 'center'
    },
    imageBack: {
        width: 22,
        height: 14,
        marginLeft: 10
    },
    textTitle: {
        fontSize: 24,
        textAlign: 'center'
    },
    logo: {
        textAlign: 'center',
        fontSize: 22,
        width: appSize.width - 50,
        marginBottom: 40
    },
    background: {
        width: appSize.width,
        height: appSize.height,
    },
    // label: {
    //     color: '#595959',
    //     fontSize: CommonStyleSheet.fontSize,
    // },
    input: {
        marginTop: 10,
        width: appSize.width - 80,
    },
    titleChangePassword: {
        fontSize: 18,
        fontWeight: '600'
    },
    buttonChangePassword: {
        marginTop: 40,
        width: appSize.width - 80,
        height: 50
    },
    progress_view: {
        justifyContent: "center",
        position: "absolute",
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    }
});