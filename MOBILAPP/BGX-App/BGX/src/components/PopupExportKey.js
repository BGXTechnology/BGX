import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import CheckBox from "react-native-check-box";
import BorderButton from './BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import InputText from "./InputText";
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupExportKey extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPress = props.onPress;
        this.onPressCancel = props.onPressCancel;
        this.onPressGenerateQRCode = props.onPressGenerateQRCode;
        this.onPressExportFile = props.onPressExportFile;
        this.state = {
            exportToFile: true,
            generateQRCode: false,
            inputType: null,
        }
    }

    componentDidMount() {

    }

    showSlideAnimationDialog = () => {
        this.setState({
            exportToFile: true,
            generateQRCode: false,
            inputType: null,
        }, () => {
            this.slideAnimationDialog.show();
        });
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    renderExportToFile = () => {
        return (
            this.state.exportToFile ?
                <View style={styles.content_checkbox}>
                    {/* input type */}
                    <InputText
                        labelStyle={styles.label}
                        style={styles.input}
                        label={i18n.t('EXPORT_KEYS_TYPE')}
                        value={this.state.inputType}
                        secureTextEntry={true}
                        textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                        onChangeText={(inputType) => this.setState({ inputType })}
                    />
                </View>
                : null
        );
    }

    renderGenerateQRCode = () => {
        return (
            this.state.generateQRCode ?
                <View style={[styles.content_checkbox, { alignItems: 'center', }]}>
                    <TouchableOpacity onPress={this._buttonExport} >
                        <Image style={[styles.image_barcode,]} source={require('../resource/barcodeButton_dark.png')} />
                    </TouchableOpacity>
                </View>
                : null
        );
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

                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white', }]}>
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('TAB_EXPORT_KEY')}</Text>
                    <View style={styles.content}>
                        {/* export to file */}
                        <CheckBox
                            style={styles.checkBoxStyle}
                            onClick={() => {
                                this.setState({
                                    exportToFile: true,
                                    generateQRCode: false,
                                });
                            }}
                            rightText={i18n.t('EXPORT_KEYS_EXPORT_TO_FILE')}
                            rightTextStyle={{ fontSize: 20, color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                            isChecked={this.state.exportToFile}
                        />
                        {this.renderExportToFile()}

                        {/*Generate QR Code */}
                        <CheckBox
                            style={styles.checkBoxStyle}
                            onClick={() => {
                                this.setState({
                                    exportToFile: false,
                                    generateQRCode: true,
                                });
                            }}
                            rightText={i18n.t('EXPORT_KEYS_GENERATE_QR_CODE')}
                            rightTextStyle={{ fontSize: 20, color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                            checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                            isChecked={this.state.generateQRCode}
                        />
                        {this.renderGenerateQRCode()}
                    </View>

                    <View style={styles.view_content_button}>
                        {/* cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.button_cancel]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack_opacity.png") : require("../resource/icon_button_dialog_grey.png")}
                        />

                        {/* export button */}
                        <BorderButton
                            title={i18n.t('EXPORT_KEYS_EXPORT')}
                            onPress={this._buttonExport}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_export]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_white.png") : require("../resource/icon_button_dialog_drack.png")}
                        />
                    </View>
                </View>
            </PopupDialog>
        )
    }


    _buttonCancel = () => {
        Utils.dismissKeyboard();
        this.setState({ inputType: "" });
        this.dismissSlideAnimationDialog();
        if (this.onPressCancel != null) {
            this.onPressCancel();
        }
    }

    _buttonExport = () => {

        this.dismissSlideAnimationDialog();
        Utils.dismissKeyboard();
        if (this.state.exportToFile) {
            if (!Utils.isValidString(this.state.inputType)) {
                Utils.showAlert(i18n.t('SECRET_EMPTY'), true, null);
                return;
            }
            this.onPressExportFile(this.state.inputType);
        } else if (this.state.generateQRCode) {
            this.onPressGenerateQRCode();
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
        marginTop: 20,
        width: appSize.width - 50,
        textAlign: 'center',
        marginBottom: 5
    },
    background_dialog: {
        backgroundColor: 'transparent',
        justifyContent: 'center',
        paddingBottom: 45
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
    },
});