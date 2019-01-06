import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import CheckBox from "react-native-check-box";
import BorderButton from './BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import InputText from "./InputText";
import HTML from 'react-native-render-html';
import QRCode from 'react-native-qrcode';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupQRCode extends Component {

    constructor(props) {
        super(props);
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPress = props.onPress;
        this.isConfirm = props.isConfirm;
        this.titleButtonRight = props.titleButtonRight;
        this.state = {
            privateKey: props.privateKey
        }
    }

    componentDidMount() {

    }

    showSlideAnimationDialog = (privateKey) => {
        this.setState({ privateKey });
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }
    render() {
        return (
            <PopupDialog
                dialogStyle={styles.background_dialog}
                width={Utils.appSize().width - 50}
                height={Utils.appSize().height}
                ref={(popupDialog) => { this.slideAnimationDialog = popupDialog; }}
                dialogAnimation={slideAnimation}
                onDismissed={() => Utils.dismissKeyboard()}>

                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? 'white' : 'white', }]}>
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'black' : 'black', }]}>{i18n.t('EXPORT_KEYS_GENERATE_QR_CODE')}</Text>
                    <QRCode
                        style={styles.qr_code}
                        value={this.state.privateKey}
                        size={200}
                        bgColor='black'
                        fgColor='white' />
                    <View style={styles.view_content_button}>
                        {
                            this.isConfirm ?
                                /* cancel button */
                                <BorderButton
                                    title={i18n.t('CANCEL')}
                                    onPress={this._buttonCancel}
                                    titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                                    style={[styles.button_cancel]}
                                    imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog,]}
                                    background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack_opacity.png") : require("../resource/icon_button_dialog_grey.png")}
                                /> : null
                        }

                        {/* export button */}
                        <BorderButton
                            title={this.titleButtonRight}
                            onPress={this._buttonOK}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'white' }}
                            style={[styles.button_export]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack.png") : require("../resource/icon_button_dialog_drack.png")}//icon_button_dialog_white
                        />
                    </View>
                </View>
            </PopupDialog>
        )
    }


    _buttonCancel = () => {
        this.setState({ inputType: "" });
        this.dismissSlideAnimationDialog();
    }

    _buttonOK = () => {
        this.dismissSlideAnimationDialog();
        if (this.onPress != null) {
            this.onPress();
        }
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
        width: appSize.width - 50,
        textAlign: 'center',
        marginBottom: 15
    },
    title1: {
        fontSize: 16,
        color: 'white',
        margin: 20,
    },
    background_dialog: {
        backgroundColor: 'transparent',
        justifyContent: 'center',
    },
    dialogContentView: {
        flexDirection: 'column',
        borderRadius: 20,
        alignItems: 'center',
        justifyContent: 'center',
        paddingTop: 30
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
    checkBoxStyle: {
        paddingTop: 10,
        paddingBottom: 10,
        width: appSize.width - 50,
        alignItems: 'center',
        justifyContent: 'center',
    },
    text_checkbox: {
        color: 'white',
        fontSize: 20
    },
    separator: {
        height: 1,
        backgroundColor: '#2a2a2a'
    },
    button_cancel: {
        margin: 10,
        flex: 1
    },
    button_export: {
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
        marginLeft: 35,
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
    }

});