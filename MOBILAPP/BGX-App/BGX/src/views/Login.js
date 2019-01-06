import React, { Component } from "react";
import { StyleSheet, View, Text, Image, TextInput, KeyboardAvoidingView, TouchableWithoutFeedback, Keyboard, ScrollView, TouchableOpacity, AppRegistry } from "react-native";
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
import i18n from '../translations/i18n';
import LockTaskService from '../nativeFunction/LockTaskService';

const DismissKeyboard = ({ children }) => {
    return (
        <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
            {children}
        </TouchableWithoutFeedback>
    );
}

export default class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            email: "",
            password: "",
            checkbox: false,
            progressView: false,
            logo: null,
            numberLoginError: 0,
            showLoginError: false
        };

        this.deviceToken = null;
    }

    static navigationOptions = ({ navigation }) => ({
        header: null,
    });

    async componentDidMount() {
        let theme = await Utils.getDataWithKey(Constants.KEY_DATA_THEME);
        if (theme != null) {
            this.setState({
                logo: ConfigAPI.DOMAIN + "/" + theme.logo,
            });
        }
    }

    async  componentWillMount() {
        this.deviceToken = await Utils.getDataWithKey(Constants.KEY_PUSH_NOTIFICATION);
    }

    render() {
        return (
            <DismissKeyboard>
                <View style={styles.container}>
                    <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")} style={styles.background} />
                    <View style={styles.content}>
                        {/* Logo */}
                        {
                            this.state.logo == null ?
                                <Image source={require('../resource/icon_logo_color_dark.png')} style={styles.image_logo} />
                                : <Image source={{ uri: this.state.logo }} style={styles.image_logo} />
                        }

                        <KeyboardAvoidingView style={[{ flex: 1, justifyContent: 'center' }]} behavior="padding" enabled>
                            {/* Email */}
                            <InputText
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('EMAIL')}
                                value={this.state.email}
                                secureTextEntry={false}
                                textInputStyle={[{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}
                                onChangeText={(email) => this.setState({ email })}
                            />

                            {/* password */}
                            <InputText
                                labelStyle={styles.label}
                                style={styles.input}
                                label={i18n.t('PASSWORD')}
                                value={this.state.password}
                                secureTextEntry={true}
                                textInputStyle={[{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}
                                onChangeText={(password) => this.setState({ password })}
                            />

                        </KeyboardAvoidingView>
                        {
                            this.state.showLoginError ?
                                <Text style={[styles.error_login]}>
                                    {i18n.t('YOUR_ACCOUNT_LOCK')}
                                </Text> : null
                        }
                        {/* Login button */}
                        <BorderButton
                            title={i18n.t('LOGIN')}
                            onPress={this._buttonLogin}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_login]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}
                        />

                        {/* forgot password */}
                        <TouchableOpacity onPress={this.forgotPassword}>
                            <Text style={[styles.forgot_password, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>
                                {i18n.t('LOGIN_FORGOT_PASSWORD')}
                            </Text>
                        </TouchableOpacity>

                        {/* Singup button */}
                        <BorderButton
                            title={i18n.t('SIGNUP_FO_FREE')}
                            onPress={this._buttonSigup}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : '#252525', }}
                            style={[styles.button_signup]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_drack_opacity.png") : require("../resource/icon_button_grey.png")}
                        />
                    </View>


                    {/* load progress */}
                    {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
                </View>
            </DismissKeyboard>
        );
    }

    _buttonSigup = () => {
        this.props.navigation.navigate('RegisterView');
    }

    _buttonLogin = async () => {
        Utils.dismissKeyboard();
        if (Utils.isAndroid()) {
            LockTaskService.checkServiceStop((isRun) => {
                if (isRun) {
                    Utils.showAlert(i18n.t('YOUR_PHONE_LOCK'), true, null);
                    this.setState({
                        numberLoginError: 0,
                        showLoginError: false
                    })
                    return;
                }
                if (!Utils.isValidString(this.state.email) && !Utils.isValidString(this.state.password)) {
                    Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL_PASSWORD'), true, null);
                    return;
                } else {
                    if (!Utils.isValidString(this.state.email)) {
                        Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL'), true, null);
                        return;
                    }

                    if (!Utils.isValidString(this.state.password)) {
                        Utils.showAlert(i18n.t('SIGNUP_BLANK_PASSWORD'), true, null);
                        return;
                    }

                    if (!Utils.checkValidPassword(this.state.password)) {
                        // Utils.showAlert(i18n.t('SIGNUP_PASSWORD_6_SYMBOL'), true, null);
                        Utils.showAlert(i18n.t('MESSAGE_EMAIL_PASSWORD_INVALID'), true, null);
                        return;
                    }

                    if (!Utils.checkEmailValid(this.state.email)) {
                        // Utils.showAlert(i18n.t('MESSAGE_EMAIL_INVALID'), true, null);
                        Utils.showAlert(i18n.t('MESSAGE_EMAIL_PASSWORD_INVALID'), true, null);
                        return;
                    }
                }
                this.setState({ progressView: true });
                let params = {
                    [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LOGIN,
                    [ConfigAPI.PARAM_LOCALE]: i18n.locale,
                    [ConfigAPI.PARAM_EMAIL]: this.state.email,
                    [ConfigAPI.PARAM_PASSWORD]: md5(this.state.password),
                    [ConfigAPI.PARAM_TOKEN]: this.deviceToken,
                    [ConfigAPI.PARAM_DEVICE_TYPE]: Utils.isIOS() ? "2" : "1"
                };

                let login = new BaseService();
                login.setParam(params);
                login.setCallback(this);
                login.requestData();
            });
        } else {
            if (!Utils.isValidString(this.state.email) && !Utils.isValidString(this.state.password)) {
                Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL_PASSWORD'), true, null);
                return;
            } else {
                if (!Utils.isValidString(this.state.email)) {
                    Utils.showAlert(i18n.t('SIGNUP_BLANK_EMAIL'), true, null);
                    return;
                }

                if (!Utils.isValidString(this.state.password)) {
                    Utils.showAlert(i18n.t('SIGNUP_BLANK_PASSWORD'), true, null);
                    return;
                }

                if (!Utils.checkValidPassword(this.state.password)) {
                    Utils.showAlert(i18n.t('SIGNUP_PASSWORD_6_SYMBOL'), true, null);
                    // Utils.showAlert(i18n.t('MESSAGE_EMAIL_PASSWORD_INVALID'), true, null);
                    return;
                }

                if (!Utils.checkEmailValid(this.state.email)) {
                    Utils.showAlert(i18n.t('MESSAGE_EMAIL_INVALID'), true, null);
                    // Utils.showAlert(i18n.t('MESSAGE_EMAIL_PASSWORD_INVALID'), true, null);
                    return;
                }
            }
            this.setState({ progressView: true });
            let params = {
                [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LOGIN,
                [ConfigAPI.PARAM_LOCALE]: i18n.locale,
                [ConfigAPI.PARAM_EMAIL]: this.state.email,
                [ConfigAPI.PARAM_PASSWORD]: md5(this.state.password),
                [ConfigAPI.PARAM_TOKEN]: this.deviceToken,
                [ConfigAPI.PARAM_DEVICE_TYPE]: Utils.isIOS() ? "2" : "1"
            };

            let login = new BaseService();
            login.setParam(params);
            login.setCallback(this);
            login.requestData();
        }
    }

    forgotPassword = () => {
        this.props.navigation.navigate('ForgotPasswordView');
    }

    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_LOGIN) {
            setTimeout(() => {
                let routeNameView = 'ProfileView';
                if (data.active == "1") {
                    routeNameView = 'DigitalSpotView';
                }
                Utils.saveDataWithKey(Constants.KEY_USER, data);
                Utils.saveDataWithKey(Constants.KEY_PASSWORD, this.state.password);
                const resetAction = StackActions.reset({
                    index: 0,
                    key: null,
                    routeName: routeNameView,
                    actions: [NavigationActions.navigate({ routeName: 'Main' })],
                });
                this.props.navigation.dispatch(resetAction);
            }, 500);
        }
    }

    async onFail(code, message, method) {
        this.setState({
            progressView: false,
            numberLoginError: this.state.numberLoginError + 1
        }, () => {
            if (Utils.isAndroid()) {
                if (this.state.numberLoginError == 2) {
                    this.setState({
                        showLoginError: true
                    })
                } else if (this.state.numberLoginError == 5) {
                    console.log("checkServiceStop : " + this.state.numberLoginError);
                    LockTaskService.startServiceLockDevice();
                }
            }
        });
        setTimeout(() => {
            Utils.showAlert(message, true, null);
        }, 100);
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        justifyContent: "center",
        alignItems: "center",
        flex: 1,
    },
    content: {
        position: "absolute",
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: "center",
        marginLeft: 25,
        marginRight: 25,
    },
    background: {
        width: appSize.width,
        height: appSize.height,
    },
    logo: {
        fontSize: 80,
        textAlign: 'center',
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
        color: '#5b5b5b', paddingTop: 10
    },
    button_login: {
        marginTop: 30
    },
    button_signup: {
        marginTop: 20,
    },
    progress_view: {
        justifyContent: "center",
        position: "absolute",
    },
    forgot_password: {
        fontSize: 16,
        marginTop: 20,
        fontStyle: 'italic',
        textDecorationLine: 'underline'
    },
    image_logo: {
        height: 90,
        width: appSize.width - 50,
        marginBottom: 5,
        resizeMode: "stretch",
    },
    error_login: {
        fontSize: 16,
        fontStyle: 'italic',
        color: 'red'
    },

});