import React, { Component } from "react";
import { StyleSheet, View, Text, Image, TouchableOpacity } from "react-native";
import { NavigationActions, StackActions } from 'react-navigation';
import Utils from "../common/Utils";
import BorderButton from '../components/BorderButton';
import Line from '../components/Line';
import * as CommonStyleSheet from '../common/StyleSheet';
import DialogInput from 'react-native-dialog-input';
import ConfigAPI from "../api/ConfigAPI";
import { BaseService } from '../api/BaseService';
import LoadingIndicator from "../components/LoadingIndicator";
import * as Constants from '../common/Constants';
import i18n from '../translations/i18n';

export default class Welcome extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isDialogVisible: false,
            progressView: false,
            logo: null,
            text: null,
            language: null,
            color: null,
            colorSignup: CommonStyleSheet.THEME_DARCK ? 'black' : 'white',
            colorLogin: CommonStyleSheet.THEME_DARCK ? 'white' : 'black',

            bgSignup: CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png"),
            bgLogin: CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_drack_opacity.png") : require("../resource/icon_button_grey.png"),

            background: CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg"),
            titleSignup: i18n.t('SIGNUP_FO_FREE'),
            titleLogin: i18n.t('LOGIN')
        }
    }

    static navigationOptions = ({ navigation }) => ({
        header: null,
        drawerLockMode: 'locked-closed'
    });

    componentWillMount() {
        // // this.props.navigation.navigate('LoginView');
        // const resetAction = StackActions.reset({
        //     index: 0,
        //     actions: [NavigationActions.navigate({ routeName: 'LoginView' })],
        //   });
        //   this.props.navigation.dispatch(resetAction);
        console.log('componentWillMount');
    }

    async componentDidMount() {
        let theme = await Utils.getDataWithKey(Constants.KEY_DATA_THEME);
        if (theme != null) {
            this.setState({
                logo: ConfigAPI.DOMAIN + "/" + theme.logo,
                language: theme.text
            });
        }
    }

    componentWillUnmount() {
        console.log('componentWillUnmount');
    }

    render() {
        // alert(this.state.THEME_DARCK)
        // alert(CommonStyleSheet.THEME_DARCK)
        return (
            <View style={styles.container}>
                {
                    <Image source={this.state.background}
                        style={styles.background}
                    />
                }
                <View style={styles.content}>

                    <TouchableOpacity onLongPress={this._inputCode}>
                        {
                            this.state.logo == null ?
                                <Image source={require('../resource/icon_logo_color_dark.png')} style={styles.image_logo} />
                                : <Image source={{ uri: this.state.logo }} style={styles.image_logo} />
                        }
                    </TouchableOpacity>

                    <Line style={styles.line} />
                    <Text style={[styles.THE_PLACE_WHERE_PEOPLE, { color: CommonStyleSheet.THEME_DARCK || this.state.THEME_DARCK ? '#b7b7b7' : '#707070', }]}>
                        {this.state.text == null ? i18n.t('THE_PLACE_WHERE_PEOPLE') : this.state.text}
                    </Text>
                    <Line style={styles.line} />

                    <BorderButton
                        title={this.state.titleSignup}
                        onPress={this._buttonSigup}
                        titleStyle={{ color: this.state.colorSignup }}
                        style={[styles.button_signup]}
                        imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                        background={this.state.bgSignup}
                    />
                    <BorderButton
                        title={this.state.titleLogin}
                        onPress={this._buttonLogin}
                        titleStyle={{ color: this.state.colorLogin }}
                        style={[styles.button_login]}
                        imageStyle={CommonStyleSheet.viewCommonStyles.common_button}
                        background={this.state.bgLogin}
                    />

                </View>
                <DialogInput
                    isDialogVisible={this.state.isDialogVisible}
                    title={i18n.t('INTERFACE_CODE_TITLE')}
                    message={i18n.t('INTERFACE_CODE_DESCRIPTION')}
                    hintInput={i18n.t('INTERFACE_CODE')}
                    textInputProps={{ keyboardType: 'numeric' }}
                    dialogStyle={Utils.appSize().height <= 568 ? {marginTop: -90} : null}
                    submitInput={(inputText) => { this._submitInput(inputText) }}
                    closeDialog={() => { this.setState({ isDialogVisible: false }) }}>
                </DialogInput>

                {/* load progress */}
                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }

    /**  handler login */
    _buttonLogin = () => {
        this.props.navigation.navigate('LoginView');
    }
    /**  handler singup */
    _buttonSigup = () => {
        this.props.navigation.navigate('RegisterView');
    }

    _inputCode = () => {
        this.setState({ isDialogVisible: true });
    }

    _submitInput = (inputText) => {
        if (inputText.trim().length != 7) {
            Utils.showAlert(i18n.t('INTERFACE_CODE_INPUT_ERROR'));
            return;
        }
        this.setState({
            progressView: true,
            isDialogVisible: false
        });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_INTERFACE_CODE,
            [ConfigAPI.PARAM_LOCALE]: this.state.language,
            [ConfigAPI.PARAM_CODE]: inputText,
        };
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    async onSuccess(code, message, data, method) {
        if (method === ConfigAPI.METHOD_INTERFACE_CODE) {
            this.setState({
                logo: ConfigAPI.DOMAIN + "/" + data.logo,
                text: data.text,
                language: data.language,
                color: data.color,
                progressView: false,
                colorSignup: data.color == "#000000" ? 'black' : 'white',
                colorLogin: data.color == "#000000" ? 'white' : 'black',
                bgSignup: data.color == "#000000" ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png"),
                bgLogin: data.color == "#000000" ? require("../resource/icon_button_drack_opacity.png") : require("../resource/icon_button_grey.png"),
                background: data.color == "#000000" ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")
            }, () => {
                i18n.locale = data.language;
                CommonStyleSheet.THEME_DARCK = data.color == "#000000" ? true : false;
                CommonStyleSheet.data = data.language;
                this.setState({
                    titleSignup: i18n.t('SIGNUP_FO_FREE'),
                    titleLogin: i18n.t('LOGIN')
                });
            });
            await Utils.saveDataWithKey(Constants.KEY_DATA_THEME, data);
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
        alignItems: "center",
        marginLeft: 25,
        marginRight: 25,
    },
    logo: {
        fontSize: 70,
    },
    image_logo: {
        height: 90,
        width: appSize.width - 50,
        marginBottom: 5,
        resizeMode: "stretch",
    },
    THE_PLACE_WHERE_PEOPLE: {
        fontSize: CommonStyleSheet.fontSize,
        textAlign: 'center',
        marginTop: 20,
        marginBottom: 20,
    },
    background: {
        width: appSize.width,
        height: appSize.height,
    },
    button_signup: {
        marginTop: 40
    },
    button_login: {
        marginTop: 10,
    },
    line: {
        width: appSize.width - 50,
        height: 1,
        opacity: 0.5,
    }
});