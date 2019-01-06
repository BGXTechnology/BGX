
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, TextInput, View } from 'react-native';
import Utils from '../common/Utils';
import Line from './Line';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });
import BorderButton from './BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import * as Constants from '../common/Constants';
import PopupDialogInput from './PopupDialogInput';
import i18n from '../translations/i18n';

export default class TextComponent extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.style = props.style;
        this.labelStyle = props.labelStyle;
        this.onPress = props.onPress;
        this.label = props.label;
        this.placeholder = props.placeholder;
        this.onChangeText = props.onChangeText;
        this.placeholderTextColor = props.placeholderTextColor;
        this.secureTextEntry = props.secureTextEntry;
        this.disabled = props.disabled;
        this.maxLength = props.maxLength;
        this.numberOfLines = props.numberOfLines;
        this.widthLine = props.widthLine;
        this.onFocus = props.onFocus;
        this.styleContent = props.styleContent;
        this.textInputStyle = props.textInputStyle;
        this.showQRCode = props.showQRCode;
        this.buttonBarcode = props.buttonBarcode;
        this.isPIN = props.isPIN;
        this.state = {
            value: props.value
        };
        this.valueSecure = "";
        this.width = props.width;
    }
    componentDidMount() {
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                value: nextProps.value
            });
        }
    }

    render() {
        return (
            <View style={[styles.container, this.style]}>
                <Text style={[styles.title, this.labelStyle]}>{this.label} </Text>
                <TouchableOpacity onPress={this.onPress} disabled={this.disabled} >
                    <Text style={[styles.text, { width: this.width }, this.styleContent]}
                        numberOfLines={1}>
                        {this.secureTextEntry ? (this._handlerTextSecure(this.state.value)) : this.state.value}
                    </Text>
                </TouchableOpacity>
                <Line style={{ width: this.widthLine }} />
                {this.showQRCode ?
                    <View style={styles.view_barcode}>
                        <TouchableOpacity onPress={this.buttonBarcode}>
                            <Image style={styles.imageBarcode} source={CommonStyleSheet.THEME_DARCK ? require("../resource/barcodeButton_dark.png") : require("../resource/barcodeButton_white.png")} />
                        </TouchableOpacity>
                    </View>
                    : null}
            </View>
        )
    }

    _handlerTextSecure = (text) => {
        if (!Utils.isValidString(text)) return ""
        var valueSecure = "";
        for (var i = 0; i < text.length; i++) {
            valueSecure += "*";
            if (this.isPIN)
                if (valueSecure.length > 3) break;
        }
        return valueSecure;
    }

}
let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        flexDirection: 'column',
        justifyContent: 'center',
    },
    title: {
        fontSize: 16,
    },
    background_dialog: {
        backgroundColor: 'transparent',
        justifyContent: 'center',
    },
    dialogContentView: {
        flexDirection: 'column',
        backgroundColor: '#1c1c1c',
        borderRadius: 20,
    },
    view_content_button: {
        flexDirection: 'row',
        marginTop: 10,
        marginBottom: 10,
    },
    text: {
        width: appSize.width - 50,
        marginTop: 5,
        marginBottom: 5,
        color: 'white',
        height: 20
    },
    view_barcode: {
        position: "absolute",
        flex: 1,
        right: 0,
        bottom: 0,
    },
    imageBarcode: {
        width: 40,
        height: 40,
    },

});