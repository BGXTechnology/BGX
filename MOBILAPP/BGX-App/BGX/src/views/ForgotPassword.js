import React, { Component } from "react";
import { StyleSheet, View, Text, Image, TextInput, KeyboardAvoidingView, TouchableWithoutFeedback, Keyboard, ScrollView, TouchableOpacity } from "react-native";
import { NavigationActions, StackActions } from 'react-navigation';
import Utils from "../common/Utils";
import * as CommonStyleSheet from '../common/StyleSheet';
import CheckBox from "react-native-check-box";
import Account from '../models/Account';
import BorderButton from '../components/BorderButton';
import Line from '../components/Line';
import { BaseService } from '../api/BaseService';
import ConfigAPI from "../api/ConfigAPI";
import * as Constants from '../common/Constants';
import LoadingIndicator from "../components/LoadingIndicator";
import InputText from "../components/InputText";
var md5 = require('md5');
import Icon from "react-native-vector-icons/Ionicons";
import PopupMessage from '../components/PopupMessage';
import i18n from '../translations/i18n';

const DismissKeyboard = ({ children }) => {
    return (
        <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
            {children}
        </TouchableWithoutFeedback>
    );
}

export default class ForgotPassword extends Component {

    constructor(props) {
        super(props);
        this.state = {
            email: "",
            progressView: false
        };
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    renderHeader = () => {
        return (
            <View style={styles.header}>
                <TouchableOpacity style={styles.header_left} onPress={() => this.props.navigation.goBack()}>
                    <Icon name="ios-arrow-round-back" size={30} color={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'} />
                </TouchableOpacity>
                <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('LOGIN_FORGOT_PASSWORD')}</Text>
            </View>
        );
    }

    render() {

        return (
            <DismissKeyboard>
                <View style={styles.container}>
                    <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")} style={styles.background} />
                    <View style={styles.content}>
                        {this.renderHeader()}
                        <Text style={[styles.title_address, {color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }]}>{i18n.t('FORGOT_PASSWORD_TITLE')}</Text>
                        {/* Email */}
                        <InputText
                            labelStyle={styles.label}
                            style={styles.input}
                            label={i18n.t('EMAIL')}
                            value={this.state.email}
                            secureTextEntry={false}
                            textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            onChangeText={(email) => this.setState({ email })}
                        />

                        {/* forgotpassword button */}
                        <BorderButton
                            title={i18n.t('FORGOT_PASSWORD_SEND')}
                            onPress={this._buttonForgotPassword}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_forgotpassword]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}
                        />
                    </View>
                    {/* load progress */}
                    {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}

                    <PopupMessage
                        ref={(popupDialogMessage) => { this.popupDialogMessage = popupDialogMessage; }}
                        onPress={() => this.props.navigation.goBack()}
                        isConfirm={false}
                        titleButtonRight={i18n.t('OK')}
                        title={'about:blank'}
                    />
                </View>
            </DismissKeyboard>
        );
    }

    _buttonForgotPassword = () => {
        Utils.dismissKeyboard();
        if (!Utils.isValidString(this.state.email)) {
            Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL'), true, null);
            return;
        }

        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_RESET_PASSWORD,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.email,
        };
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_RESET_PASSWORD) {
            this.setState({ progressView: false });
            setTimeout(() => {
                this.popupDialogMessage.showSlideAnimationDialog(message);
            }, 100);
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        setTimeout(() => {
            Utils.showAlert(message);
        }, 100);
    }

}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    content: {
        position: "absolute",
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: "center",
        marginTop: Constants.HEIGHT_STATUS_BAR
    },
    background: {
        width: appSize.width,
        height: appSize.height,
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
    title: {
        fontSize: 25,
        textAlign: 'center',
    },
    title_address: {
        fontSize: 16,
        textAlign: 'center',
        marginTop: 30,
        marginBottom: 30
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    },
    input: {
        marginTop: 10,
        width: appSize.width - 50,
        color: '#595959',
    },

    button_forgotpassword: {
        marginTop: 20,
    },
    progress_view: {
        justifyContent: "center",
        position: "absolute",
    },
});