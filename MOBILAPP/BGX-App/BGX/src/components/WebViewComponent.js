import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, WebView } from 'react-native';
import Utils from '../common/Utils';

const PolicyHTML = require('../resource/web/policy.html');
const TermofServiceHTML = require('../resource/web/TermOfService.html');
import i18n from '../translations/i18n';

export default class WebViewComponent extends Component {

    constructor(props) {
        super(props);
        this.url = this.props.navigation.getParam('url', 'PolicyHTML');
    }

    static navigationOptions = ({ navigation }) => ({
        title: navigation.getParam('url', 'PolicyHTML') == 'PolicyHTML' ? i18n.t('SIGNUP_PRIVACY_POLICY') : i18n.t('SIGNUP_TERM_OF_SERVICE')
    });

    componentDidMount() {
    }

    render() {
        const INJECTEDJAVASCRIPT = `const meta = document.createElement('meta'); meta.setAttribute('content', 'width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0'); meta.setAttribute('name', 'viewport'); document.getElementsByTagName('head')[0].appendChild(meta); `

        return (
            <WebView
                source={this.url == 'PolicyHTML' ?
                    (Utils.isIOS() ? PolicyHTML : { uri: "file:///android_asset/policy.html" })
                    : (Utils.isIOS() ? TermofServiceHTML : { uri: "file:///android_asset/termofservice.html" })}
                style={styles.container}
                scalesPageToFit={Utils.isAndroid() ? false : true}
                javaScriptEnabled={true}
                allowFileAccess={true}
                originWhitelist={['*']}
                useWebKit={true}
                injectedJavaScript={Utils.isAndroid() ? INJECTEDJAVASCRIPT : ''}
                javaScriptEnabledAndroid={true}
                pointerEvents="none"
                onError={(error) => { alert(error) }}
            />
        )
    }

}
const styles = StyleSheet.create({
    container: {
        flex: 1,
    },

});