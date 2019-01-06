import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity, Image, WebView, Dimensions } from "react-native";
import Icon from "react-native-vector-icons/MaterialIcons";
import Utils from '../common/Utils';
import ConfigAPI from '../api/ConfigAPI';
import i18n from '../translations/i18n';

export default class DontMissOut extends Component {
    constructor(props) {
        super(props);

        this.state = {
            isLoaded: false,
            isCanGoBack: false,
            isCanGoForward: false
        };

        this.webView = null;
    }
    static navigationOptions = ({ navigation, screenProps }) => ({
        drawerLabel: "asdasd",
        title: i18n.t('NEWS_TITLE'),
        headerTitleStyle: {
            color: '#FFFFFF',
        },
        headerStyle: ({
            backgroundColor: 'black',
        }),
        headerLeft: (
            <View style={{ paddingHorizontal: 10 }}>
                <TouchableOpacity onPress={() => navigation.openDrawer()}>
                    <Icon name="menu" size={30} color="white" />
                </TouchableOpacity>
            </View>
        ),
    });

    componentDidMount() {

    }

    webViewGoBackButton_clicked = () => {
        if (this.state.isLoaded) {
            this.webView.goBack();
        }
    }

    webViewGoForwadButton_clicked = () => {
        if (this.state.isLoaded) {
            this.webView.goForward();
        }
    }

    webViewReloadButton_clicked = () => {
        if (this.state.isLoaded) {
            this.webView.reload();
        }
    }

    webview_onLoadEnd = () => {
        this.setState({
            isLoaded: true
        });
    }

    setWebViewUrlChanged = (webviewState) => {
        this.setState({
            isCanGoBack: webviewState.canGoBack,
            isCanGoForward: webviewState.canGoForward
        });
    };

    render() {
        const INJECTEDJAVASCRIPT = `const meta = document.createElement('meta');
          meta.setAttribute('content', 'width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0');
          meta.setAttribute('name', 'viewport'); 
          document.getElementsByTagName('head')[0].appendChild(meta); `

        return (
            <View style={{ flex: 1, flexDirection: 'column' }}>
                <WebView
                    ref={component => this.webView = component}
                    onLoadEnd={this.webview_onLoadEnd}
                    source={{ uri: ConfigAPI.DOMAIN_NEWS_NOTIFICATIONS }}
                    style={{ flex: 1 }}
                    // scalesPageToFit={true}
                    scalesPageToFit={Utils.isAndroid() ? false : true}
                    javaScriptEnabled={true}
                    allowFileAccess={true}
                    originWhitelist={['*']}
                    useWebKit={true}
                    javaScriptEnabledAndroid={true}
                    onNavigationStateChange={this.setWebViewUrlChanged}
                    injectedJavaScript={Utils.isAndroid() ? INJECTEDJAVASCRIPT : ''}
                    domStorageEnabled={true}
                    pointerEvents="none"
                />
                <View style={{ flexDirection: 'row', width: '100%', height: 50, marginTop: 0, marginBottom: 0, backgroundColor: '#8b8b8b', justifyContent: 'flex-start', alignContent: 'center', alignItems: 'center' }}>
                    <TouchableOpacity onPress={this.webViewGoBackButton_clicked}>
                        <Image style={styles.checkButton} source={this.state.isCanGoBack ? require("../resource/back_web.png") : require("../resource/back_web_grey.png")} />
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.webViewGoForwadButton_clicked}>
                        <Image style={[styles.checkButton, { marginLeft: 25 }]} source={this.state.isCanGoForward ? require("../resource/next_web.png") : require("../resource/next_web_grey.png")} />
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.webViewReloadButton_clicked}>
                        <Image style={[styles.checkButton, { marginLeft: 35 }]} source={this.state.isLoaded ? require("../resource/reload_web.png") : require("../resource/reload_web_grey.png")} />
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    containView: {
        flex: 1,
        backgroundColor: '#f5f5f5'
    },
    flatList: {
        flex: 1,
        marginTop: 15,
        marginLeft: 15,
        marginRight: 15
    },
    activityIndicator: {
        position: 'absolute',
        zIndex: 1,
        marginTop: Dimensions.get('window').height / 2 - 25
    },
    checkButton: {
        marginLeft: 15,
        width: 22,
        height: 22
    }
});