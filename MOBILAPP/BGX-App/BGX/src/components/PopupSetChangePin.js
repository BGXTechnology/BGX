import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import CheckBox from "react-native-check-box";
import BorderButton from './BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import InputText from "./InputText";
var md5 = require('md5');
import i18n from '../translations/i18n';
const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupSetChangePin extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPress = props.onPress;
        this.state = {
            currentPin: null,
            confirmCurrentPin: null,
            newPin: null,
            confirmPin: null,
            edit: true
        }

        this.styleDialog = null;
    }

    componentDidMount() {

    }

    showSlideAnimationDialog = (edit, pin) => {
        this.setState({
            edit: edit,
            confirmCurrentPin: pin,
            currentPin: "",
            newPin: "",
            confirmPin: "",
        }, () => {
            this.forceUpdate()
            this.slideAnimationDialog.show();

            if (Utils.appSize().height <= 667) {
                if (this.state.edit) {
                    var value = Utils.appSize().height - 667 - 41;
                    if (value <= -140) {
                        value = -140;
                    }

                    this.styleDialog = { marginTop: value };
                } else {
                    var value = Utils.appSize().height - 667 - 41;
                    if (value <= -130) {
                        value = -130;
                    }

                    this.styleDialog = { marginTop: value };
                }
            } else {
                this.styleDialog = null;
            }
        });


    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    render() {
        return (
            <PopupDialog
                dialogStyle={[styles.background_dialog, this.styleDialog]}
                width={Utils.appSize().width - 50}
                height={Utils.appSize().height}
                ref={(popupDialog) => { this.slideAnimationDialog = popupDialog; }}
                dialogAnimation={slideAnimation}
                onDismissed={() => this._onDismissed()}>

                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white' }]}>
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('SET_CHANGE_PIN_TITLE')}</Text>
                    <View style={styles.content}>
                        {/* currentPin */}
                        {
                            this.state.edit ?
                                <InputText
                                    labelStyle={styles.label}
                                    style={styles.input}
                                    label={i18n.t('SET_CHANGE_PIN_CURRENT_PIN')}
                                    value={this.state.currentPin}
                                    secureTextEntry={true}
                                    maxLength={4}
                                    keyboardType={'numeric'}
                                    returnKeyType='done'
                                    textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                    onChangeText={(currentPin) => this.setState({ currentPin })}
                                /> : null
                        }

                        {/* new pin */}
                        <InputText
                            labelStyle={styles.label}
                            style={styles.input}
                            label={i18n.t('SET_CHANGE_PIN_NEW_PIN')}
                            value={this.state.newPin}
                            secureTextEntry={true}
                            maxLength={4}
                            keyboardType={'numeric'}
                            textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            onChangeText={(newPin) => this.setState({ newPin })}
                        />

                        {/* confirm Pin */}
                        <InputText
                            labelStyle={styles.label}
                            style={[styles.input, { marginBottom: 35 }]}
                            label={i18n.t('SET_CHANGE_PIN_CONFIRM_PIN')}
                            value={this.state.confirmPin}
                            secureTextEntry={true}
                            maxLength={4}
                            keyboardType={'numeric'}
                            textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            onChangeText={(confirmPin) => this.setState({ confirmPin })}
                        />
                    </View>

                    <View style={[styles.view_content_button, { marginTop: -40 }]}>
                        {/* cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.button_cancel, { marginBottom: 0 }]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack_opacity.png") : require("../resource/icon_button_dialog_grey.png")}
                        />
                        {/* save button */}
                        <BorderButton
                            title={i18n.t('SAVE')}
                            onPress={this._buttonSave}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_save, { marginBottom: 0 }]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_white.png") : require("../resource/icon_button_dialog_drack.png")}
                        />
                    </View>
                </View>
            </ PopupDialog>
        )
    }

    _onDismissed = () => {
        Utils.dismissKeyboard();
        this.setState({
            currentPin: "",
            confirmCurrentPin: "",
            newPin: "",
            confirmPin: "",
        })
    }
    _buttonCancel = () => {
        this.setState({
            enterData: true,
            importFromFile: false,
            generateQRCode: false,
        });
        Utils.dismissKeyboard();
        this.dismissSlideAnimationDialog();
    }

    _buttonSave = () => {
        if (this.state.edit) {
            if (!Utils.isValidString(this.state.currentPin)) {
                Utils.showAlert(i18n.t('PIN_EMPTY'), true, null);
                return;
            }
            if (this.state.currentPin.trim().length < 4) {
                Utils.showAlert(i18n.t('PIN_4_LENGTH'), true, null);
                return;
            }
            if (md5(this.state.currentPin) != this.state.confirmCurrentPin) {
                Utils.showAlert(i18n.t('PIN_CURRENT_NOT_MATCH'), true, null);
                return;
            }
        }
        if (!Utils.isValidString(this.state.newPin) || !Utils.isValidString(this.state.confirmPin)) {
            Utils.showAlert(i18n.t('PIN_EMPTY'), true, null);
            return;
        }
        if (this.state.newPin.trim().length < 4 || this.state.confirmPin.trim().length < 4) {
            Utils.showAlert(i18n.t('PIN_4_LENGTH'), true, null);
            return;
        }
        if (this.state.edit) {
            if (this.state.newPin.trim() != this.state.confirmPin.trim()) {
                Utils.showAlert(i18n.t('PIN_NEW_CURRENT_NOT_MATCH'), true, null);
                return;
            }
        }
        if (this.state.newPin.trim() != this.state.confirmPin.trim()) {
            Utils.showAlert(i18n.t('PIN_NOT_MATCH'), true, null);
            return;
        }
        Utils.dismissKeyboard();
        this.dismissSlideAnimationDialog();
        this.onPress(md5(this.state.newPin));
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        justifyContent: 'center',
    },
    title: {
        fontSize: 20,
        marginTop: 20,
        width: appSize.width - 50,
        textAlign: 'center',
    },
    background_dialog: {
        backgroundColor: 'transparent',
        justifyContent: 'center',
    },
    dialogContentView: {
        flexDirection: 'column',
        borderRadius: 20,
    },
    text: {
        color: '#6f6f6f',
        fontSize: 16,
        textAlign: 'center',
        width: '100%'
    },
    content: {
        margin: 20
    },
    button_cancel: {
        margin: 10,
        flex: 1
    },
    button_save: {
        margin: 10,
        flex: 1
    },
    view_content_button: {
        flexDirection: 'row',
        marginTop: 10,
        marginBottom: 10,
    },
    right_text_style: {
        color: 'white',
        fontSize: 20,
    },
    content_checkbox: {
        flexDirection: 'column',
    },
    label: {
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
    },
    input: {
        marginTop: 10,
    },
    select_file: {
        textDecorationLine: 'underline',
        color: '#595959',
        fontSize: CommonStyleSheet.fontSize,
        marginLeft: 35,
        marginTop: 10,
    },
    image_barcode: {
        height: 60,
        width: 60,
    },
});