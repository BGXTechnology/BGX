"use strict";
import React, { Component } from 'react';
import { View, AsyncStorage, StyleSheet, Image, ActivityIndicator, Text, NetInfo } from 'react-native';
import { MyStacKNavigator } from '../../App';
import Utils from "../common/Utils";
import * as Constants from '../common/Constants';
import ConfigAPI from "../api/ConfigAPI";
import { BaseService } from "../api/BaseService";
var md5 = require('md5');
import { NavigationActions, StackActions } from 'react-navigation';
import LoadingIndicator from "../components/LoadingIndicator";
import * as CommonStyleSheet from '../common/StyleSheet';
import Line from '../components/Line';
import RNLanguages from 'react-native-languages';
import i18n from '../translations/i18n';

export default class Root extends Component {

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    constructor(props) {
        super(props);
        this.state = {
            progressView: true
        };
    }

    async componentWillMount() {
        //hard code
        //    this.props.navigation.navigate('TransactionDetailView');
        NetInfo.isConnected.fetch().then(async isConnected => {
            console.log('componentDidMount ' + (isConnected ? 'online' : 'offline'));
            if (isConnected) {
                //remove comment
                let account = await Utils.getDataWithKey(Constants.KEY_USER);
                let password = await Utils.getDataWithKey(Constants.KEY_PASSWORD);
                let theme = await Utils.getDataWithKey(Constants.KEY_DATA_THEME);
                if (theme != null) {
                    CommonStyleSheet.THEME_DARCK = theme.color == "#000000" ? true : false;
                    i18n.locale = theme.language + '';
                } else {
                    i18n.locale = 'en'
                }

                if (!Utils.isValidString(JSON.stringify(account))) {
                    const resetAction = StackActions.reset({
                        index: 0,
                        key: null,
                        routeName: 'WelcomeView',
                        actions: [NavigationActions.navigate({ routeName: 'Welcome' })],
                    });
                    this.props.navigation.dispatch(resetAction);

                } else {
                    let email = account.email;
                    this.requestLogin(email, String(password));
                }
                NetInfo.isConnected.removeEventListener(
                    'connectionChange',
                    this.handleFirstConnectivityChange
                );
            }
        });
        NetInfo.isConnected.addEventListener(
            'connectionChange',
            this.handleFirstConnectivityChange
        );

    }

    handleFirstConnectivityChange = async (isConnected) => {
        console.log('componentDidMount Then, is ' + (isConnected ? 'online' : 'offline'));
        if (isConnected) {
            //remove comment
            let account = await Utils.getDataWithKey(Constants.KEY_USER);
            let password = await Utils.getDataWithKey(Constants.KEY_PASSWORD);
            let theme = await Utils.getDataWithKey(Constants.KEY_DATA_THEME);
            if (theme != null) {
                CommonStyleSheet.THEME_DARCK = theme.color == "#000000" ? true : false;
                i18n.locale = theme.language + '';
            } else {
                i18n.locale = 'en'
            }

            if (!Utils.isValidString(JSON.stringify(account))) {
                const resetAction = StackActions.reset({
                    index: 0,
                    key: null,
                    routeName: 'WelcomeView',
                    actions: [NavigationActions.navigate({ routeName: 'Welcome' })],
                });
                this.props.navigation.dispatch(resetAction);

            } else {
                let email = account.email;
                this.requestLogin(email, String(password));
            }
            NetInfo.isConnected.removeEventListener(
                'connectionChange',
                this.handleFirstConnectivityChange
            );
        }
    }

    //Request Data
    requestLogin = (email, password) => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LOGIN,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: email,
            [ConfigAPI.PARAM_PASSWORD]: md5(password + ''),
        };
        this.setState({ progressView: true });
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    //Response Data
    async onSuccess(code, message, data, method) {
        console.log('METHOD_LOGIN :' + JSON.stringify(data));
        if (method === ConfigAPI.METHOD_LOGIN) {
            setTimeout(() => {
                let routeNameView = 'ProfileView';
                if (data.active == "1") {
                    routeNameView = 'DigitalSpotView';
                }
                Utils.saveDataWithKey(Constants.KEY_USER, data);
                const resetAction = StackActions.reset({
                    index: 0,
                    actions: [NavigationActions.navigate({ routeName: routeNameView })],
                });
                this.props.navigation.dispatch(resetAction);
            }, 1000);
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: true });
        Utils.showAlert(message, true, null);
    }

    render() {

        return (
            <View style={styles.container} >
                {/** background */}
                <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/background_dark.jpg") : require("../resource/background_white.jpg")} style={styles.background} />
                <View style={styles.content}>
                    <Image source={CommonStyleSheet.THEME_DARCK ? require("../resource/logo/icon_logo_color_white.png") : require("../resource/logo/icon_logo_color_dark.png")} style={styles.logo} />
                </View>
                <View style={styles.loading}>
                    <Line style={styles.line} />
                    {/* load progress */}
                    {this.state.progressView ? <ActivityIndicator size={"large"} style={styles.indicator} /> : null}
                    <Text style={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}>{i18n.t('LOADING')}</Text>
                </View>

            </View>
        );
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
        marginLeft: 40,
        marginRight: 40,
    },
    background: {
        width: appSize.width,
        height: appSize.height,
    },
    logo: {
        width: appSize.width - 40,
        height: 100,
        resizeMode: 'contain',
        marginTop: -150,
    },
    loading: {
        position: "absolute",
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: "center",
        bottom: 80
    },
    line: {
        width: appSize.width - 40,
    },
    indicator: {
        width: 20,
        height: 20,
        marginVertical: 20
    },
    text_loading: {
        color: 'white'
    },
});