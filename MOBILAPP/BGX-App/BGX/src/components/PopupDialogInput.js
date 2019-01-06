import React, { Component } from 'react';
import { TouchableOpacity, KeyboardAvoidingView, Image, StyleSheet, Text, TextInput, View } from 'react-native';
import Utils from '../common/Utils';
import Line from './Line';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });
import BorderButton from './BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import * as Constants from '../common/Constants';
import i18n from '../translations/i18n';

export default class PopupDialogInput extends Component {

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
        this.editable = props.editable;
        this.key = props.key;
        this.state = {
            title: props.title,
            value: props.value,
            keyboardType: props.keyboardType
        };
    }

    showSlideAnimationDialog = (id, text) => {
        this.setState({
            value: text,
            key: id,
        }, () => {
            this.popupDialog.show();
            this.textInput.focus();
        });
    }

    setKeyboardType = (keyboardType) => {
        this.setState({ keyboardType });
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                title: nextProps.title,
                keyboardType: nextProps.keyboardType
            });
        }
    }

    componentDidMount() {

    }

    dismissSlideAnimationDialog = () => {
        this.popupDialog.dismiss();
    }

    render() {
        return (
            <PopupDialog
                dialogStyle={styles.background_dialog}
                width={Utils.appSize().width - 50}
                // height={Utils.appSize().height}
                ref={(popupDialog) => { this.popupDialog = popupDialog; }}
                dialogAnimation={slideAnimation}
                onDismissed={() => Utils.dismissKeyboard()}>
                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white', }]}>
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? '#949494' : 'black', }]}>{this.state.title}</Text>
                    <View style={styles.content}>
                        <TextInput
                            ref={(textInput) => this.textInput = textInput}
                            {...this.props}
                            maxLength={this.maxLength}
                            multiline={false}
                            style={[styles.text_input, this.textInputStyle, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}
                            placeholder={this.placeholder}
                            onChangeText={(text) => this._onChangeText(text)}
                            underlineColorAndroid='transparent'
                            placeholderTextColor={this.placeholderTextColor}
                            value={this.state.value}
                            secureTextEntry={this.secureTextEntry}
                            editable={this.editable}
                            numberOfLines={this.numberOfLines}
                            keyboardType={this.state.keyboardType}
                            returnKeyType='done'
                            ellipsizeMode='tail'
                            autoCapitalize='none'
                        />

                    </View>
                    <View style={styles.view_content_button}>

                        {/* cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.button_cancel]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog_1,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_cancel_black.png") : require("../resource/buttonNew/icon_cancel_2_white.png")}
                        />

                        {/* export button */}
                        <BorderButton
                            title={i18n.t('OK')}
                            onPress={this._buttonOk}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_ok]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog_1]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_ok_black.png") : require("../resource/buttonNew/icon_ok_1_white.png")}
                        />
                    </View>
                </View>
            </PopupDialog>
        );
    }

    _onChangeText(text) {
        if (this.state.keyboardType == 'phone-pad') {
            this.setState({ value: " " })
            let valueText = Utils.removeEmojis(text);
            if (text.indexOf(',') > -1 || text.indexOf(';') > -1 || text.indexOf('N') > -1 || text.indexOf('n') > -1 || text.indexOf('/') > -1 || text.indexOf('*') > -1) {
                
                setTimeout(() => {
                    this.setState({ value: valueText.replace(/[;,Nn/*]/g, '') })
                }, 1);
            } else {
                setTimeout(() => {
                    this.setState({ value: valueText })
                }, 1);
            }
        } else {
            // alert(valueText)
            // this.setState({ value: " " })
            // let valueText = Utils.removeEmojis(text);
            // setTimeout(() => {
            //     this.setState({ value: valueText })
            // }, 1);

            this.setState({ value: text });
        }
    }

    _buttonCancel = () => {
        Utils.dismissKeyboard();
        this.dismissSlideAnimationDialog();
    }

    _buttonOk = () => {
        Utils.dismissKeyboard();
        this.dismissSlideAnimationDialog();
        if (this.onChangeText != null) {
            this.onChangeText(this.state.key, this.state.value);
        }
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    content: {
        margin: 20,
        borderBottomColor: '#000000',
        borderBottomWidth: 1
    },
    title: {
        marginTop: 10,
        alignSelf: 'center',
        fontWeight: '400',
        fontSize: 16
    },
    text_input: {
        height: 35,
        fontSize: 14,
        padding: 0,
        margin: 0,
        marginTop: Utils.isAndroid() ? -10 : -15
    },
    background_dialog: {
        flex: 1,
        position: "absolute",
        backgroundColor: 'transparent',
        justifyContent: 'center',
    },
    dialogContentView: {
        flexDirection: 'column',
        borderRadius: 20,
    },
    view_content_button: {
        flexDirection: 'row',
        marginTop: 30
    },
    text: {
        width: appSize.width - 50,
        marginTop: 10,
        marginBottom: 10
    },
    button_cancel: {
        margin: 10,
        marginTop: 0,
        flex: 1
    },
    button_ok: {
        margin: 10,
        marginTop: 0,
        flex: 1

    },

});