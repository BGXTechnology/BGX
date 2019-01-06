import React, { Component } from "react";
import {
    StyleSheet, View, Text, Image, TextInput, KeyboardAvoidingView, ProgressBarAndroid, TouchableWithoutFeedback,
    Keyboard
} from "react-native";
import { NavigationActions, StackActions } from 'react-navigation';
import Utils from "../common/Utils";
import BorderButton from '../components/BorderButton';
import Line from '../components/Line';
import * as CommonStyleSheet from '../common/StyleSheet';
import CheckBox from "react-native-check-box";
import { BaseService } from '../api/BaseService';
import ConfigAPI from "../api/ConfigAPI";
var md5 = require('md5');
import * as Constants from '../common/Constants';
import Account from '../models/Account';
import LoadingIndicator from "../components/LoadingIndicator";
import InputText from "../components/InputText";
import PopupMessage from '../components/PopupMessage';
import i18n from '../translations/i18n';

const DismissKeyboard = ({ children }) => {
    return (
        <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
            {children}
        </TouchableWithoutFeedback>
    );
}

export default class Register extends Component {

    constructor(props) {
        super(props);
        this.state = {
            email: "",
            password: "",
            confirmPassword: "",
            checkbox: false,
            progressView: false,
            logo: null,
        };

        this.deviceToken = null;
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    async  componentWillMount() {
        this.deviceToken = await Utils.getDataWithKey(Constants.KEY_PUSH_NOTIFICATION);
    }

    async componentDidMount() {
        let theme = await Utils.getDataWithKey(Constants.KEY_DATA_THEME);
        if (theme != null) {
            this.setState({
                logo: ConfigAPI.DOMAIN + "/" + theme.logo,
            });
        }
    }

    render() {
        return (
            <DismissKeyboard>
                <View style={styles.container}>
                    {/* Background */}
                    <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")} style={styles.background} />
                    <View style={styles.content}>

                        {/* Logo */}
                        {
                            this.state.logo == null ?
                                <Image source={require('../resource/icon_logo_color_dark.png')} style={styles.image_logo} />
                                : <Image source={{ uri: this.state.logo }} style={styles.image_logo} />
                        }
                        <KeyboardAvoidingView behavior="padding" enabled>
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

                            {/* password */}
                            <InputText
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('PASSWORD')}
                                value={this.state.password}
                                secureTextEntry={true}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onChangeText={(password) => this.setState({ password })}
                            />

                            {/* Confirm password */}
                            <InputText
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('CONFIRM_PASSWORD')}
                                value={this.state.confirmPassword}
                                secureTextEntry={true}
                                textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                onChangeText={(confirmPassword) => this.setState({ confirmPassword })}
                            />
                        </KeyboardAvoidingView>

                        {/* Checkbox */}
                        <CheckBox
                            style={styles.checkBoxStyle}
                            onClick={() => { this.setState({ checkbox: !this.state.checkbox }) }}
                            rightTextView={
                                <View style={styles.content_text_checkbox}>
                                    <Text style={[styles.text_checkbox,]}>{i18n.t('SIGNUP_REGISTRATED_YOU_AGREE_WITH')}
                                        <Text style={[styles.text_checkbox, styles.text_underline]}
                                            onPress={this._buttonTermOfService}>
                                            {i18n.t('SIGNUP_TERM_OF_SERVICE')}
                                        </Text>
                                        <Text style={styles.text_checkbox}>{' ' + i18n.t('AND') + ' '} </Text>
                                        <Text style={[styles.text_checkbox, styles.text_underline]}
                                            onPress={this._buttonPrivacyPolicy}>
                                            {i18n.t('SIGNUP_PRIVACY_POLICY')}
                                        </Text>
                                    </Text>
                                </View>
                            }
                            rightTextStyle={{ color: '#5b5b5b', fontSize: 11 }}
                            checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                            isChecked={this.state.checkbox}
                        />

                        {/* Singup button */}
                        <BorderButton
                            title={i18n.t('SIGNUP_FO_FREE')}
                            onPress={this._buttonSigup}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_signup]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}
                        />

                        {/* Login button */}
                        <BorderButton
                            title={i18n.t('LOGIN')}
                            onPress={this._buttonLogin}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.button_login]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_drack_opacity.png") : require("../resource/icon_button_grey.png")}
                        />
                    </View>

                    {/* load progress */}
                    {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}

                    <PopupMessage
                        ref={(popupDialogMessage) => { this.popupDialogMessage = popupDialogMessage; }}
                        onPress={() => {
                            const resetAction = StackActions.reset({
                                index: 0,
                                key: null,
                                routeName: 'ProfileView',
                                actions: [NavigationActions.navigate({ routeName: 'Main' })],
                            });
                            this.props.navigation.dispatch(resetAction);
                        }}
                        isConfirm={false}
                        titleButtonRight={i18n.t('OK')}
                        title={'about:blank'}
                    />
                </View>
            </DismissKeyboard>
        );
    }

    _buttonSigup = () => {
        Utils.dismissKeyboard();

        if (this.state.email.trim().length == 0 && this.state.password.trim().length == 0
            && this.state.confirmPassword.trim().length == 0 && this.state.checkbox) {
            Utils.showAlert(i18n.t('SIGNUP_EMAIL_PASSWORD_BLANK'), true, null);
            return;
        }
        if (this.state.email.trim().length == 0 && this.state.password.trim().length == 0
            && this.state.confirmPassword.trim().length == 0) {
            Utils.showAlert(i18n.t('SIGNUP_ALL_FIELDS'), true, null);
            return;
        }


        if (!Utils.isValidString(this.state.email)) {
            Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL'), true, null);
            return;
        }

        if (!Utils.isValidString(this.state.password)) {
            Utils.showAlert(i18n.t('SIGNUP_BLANK_PASSWORD'), true, null);
            return;
        }

        if (!Utils.isValidString(this.state.confirmPassword)) {
            Utils.showAlert(i18n.t('SIGNUP_BLANK_CONFIRM_PASSWORD'), true, null);
            return;
        }

        if (!Utils.checkEmailValid(this.state.email)) {
            Utils.showAlert(i18n.t('SIGNUP_INVALID_EMAIL'), true, null);
            return;
        }

        if (this.state.password !== this.state.confirmPassword) {
            Utils.showAlert(i18n.t('SIGNUP_NOT_MATCH_PASSWORD'), true, null);
            return;
        }

        if (!Utils.checkValidPassword(this.state.password)) {
            Utils.showAlert(i18n.t('SIGNUP_PASSWORD_6_SYMBOL'), true, null);
            return;
        }

        if (this.state.password.trim().length == 0) {
            Utils.showAlert(i18n.t('SIGNUP_ONLY_SPACES'), true, null);
            return;
        }

        if (!this.state.checkbox) {
            Utils.showAlert(i18n.t('SIGNUP_ACCEPT_THE_USER_AGREEMENT'));
            return;
        }

        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_REGISTER,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.email,
            [ConfigAPI.PARAM_PASSWORD]: md5(this.state.password),
            [ConfigAPI.PARAM_ETHER_ADDRESS]: '',
            [ConfigAPI.PARAM_NOHASH]: this.state.password,
            [ConfigAPI.PARAM_TOKEN]: this.deviceToken,
            [ConfigAPI.PARAM_DEVICE_TYPE]: Utils.isIOS() ? "2" : "1"
        };
        let register = new BaseService();
        register.setParam(params);
        register.setCallback(this);
        register.requestData();
    }

    _buttonLogin = () => {
        this.props.navigation.navigate('LoginView');
    }

    _buttonTermOfService = () => {
        this.props.navigation.navigate('WebViewComponentView', {
            url: 'TermOfServiceHTML'
        });
    }

    _buttonPrivacyPolicy = () => {
        this.props.navigation.navigate('WebViewComponentView', {
            url: 'PolicyHTML'
        });
    }

    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_REGISTER) {
            this.setState({ progressView: false });
            Utils.saveDataWithKey(Constants.KEY_USER, data);
            Utils.saveDataWithKey(Constants.KEY_PASSWORD, this.state.password);
            this.popupDialogMessage.showSlideAnimationDialog(i18n.t('SIGNUP_SUCCESSFUL').replace('{email_address}', this.state.email))
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
    logo: {
        textAlign: 'center',
        fontSize: 70,

    },
    background: {
        width: appSize.width,
        height: appSize.height,
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    },
    input: {
        marginTop: 10,
        width: appSize.width - 50,
    },
    checkBoxStyle: {
        flex: 1,
        paddingTop: 10,
        width: appSize.width - 50,
    },
    content_text_checkbox: {
        flex: 1,
        paddingRight: 5
    },
    text_checkbox: {
        color: '#474646',
        marginLeft: 5,
        fontSize: 14,
    },
    text_underline: {
        textDecorationLine: 'underline'
    },
    button_login: {
        marginTop: 20,
    },
    button_signup: {
        marginTop: 20
    },
    progress_view: {
        justifyContent: "center",
        position: "absolute",
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    },
    image_logo: {
        height: 90,
        width: appSize.width - 50,
        marginBottom: 5,
        resizeMode: "stretch",
    },
});