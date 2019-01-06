import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import CheckBox from "react-native-check-box";
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import InputText from "./InputText";
import RNFileSelector from 'react-native-file-selector';
import Permissions from 'react-native-permissions';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupImportKey extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPressEnterData = props.onPressEnterData;
        this.onPressImportFile = props.onPressImportFile;
        this.onPressQRCode = props.onPressQRCode;
        this.state = {
            enterData: true,
            importFromFile: false,
            generateQRCode: false,
            address: null,
            publicKey: null,
            privateKey: null,
            inputType: null,
            pathFile: "",
        }
    }

    componentDidMount() {

    }

    showSlideAnimationDialog = () => {
        this.setState({
            enterData: true,
            importFromFile: false,
            generateQRCode: false,
            address: null,
            publicKey: null,
            privateKey: null,
            inputType: null,
            pathFile: "",
        }, () => {
            this.slideAnimationDialog.show();
        });

    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    renderEnterData = () => {
        return (
            this.state.enterData ?
                <View style={styles.content_checkbox}>
                    {/* address */}
                    <InputText
                        labelStyle={styles.label}
                        style={styles.input}
                        label={i18n.t('WELLET_CARD_EDITOR_ADDRESS')}
                        value={this.state.address}
                        secureTextEntry={false}
                        textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                        onChangeText={(address) => this.setState({ address })}
                    />

                    {/* public key */}
                    <InputText
                        labelStyle={styles.label}
                        style={styles.input}
                        label={i18n.t('WELLET_CARD_EDITOR_PUBLIC_KEY')}
                        value={this.state.publicKey}
                        secureTextEntry={false}
                        textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                        onChangeText={(publicKey) => this.setState({ publicKey })}
                    />

                    {/* private key */}
                    <InputText
                        labelStyle={styles.label}
                        style={styles.input}
                        label={i18n.t('WELLET_CARD_EDITOR_PRIVATE_KEY')}
                        value={this.state.privateKey}
                        secureTextEntry={false}
                        textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                        onChangeText={(privateKey) => this.setState({ privateKey })}
                    />
                </View>
                : null
        );
    }

    renderImportFromFile = () => {
        return (
            this.state.importFromFile ?
                <View style={styles.content_checkbox}>
                    {/* input type */}
                    <InputText
                        labelStyle={styles.label}
                        style={styles.input}
                        label={i18n.t('IMPORT_KEYS_PLEASE_TYPE_YOUR_SECRET_WORD')}
                        value={this.state.inputType}
                        secureTextEntry={true}
                        textInputStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                        onChangeText={(inputType) => this.setState({ inputType })}
                    />
                    <Text style={styles.select_file} onPress={this._selectFile}>{i18n.t('IMPORT_KEYS_SELECT_FILE')}</Text>
                </View>
                : null
        );
    }

    renderGenerateQRCode = () => {
        return (
            this.state.generateQRCode ?
                <View style={[styles.content_checkbox, { alignItems: 'center', }]}>
                    <TouchableOpacity onPress={this._scanQRCode} >
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
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('TAB_IMPORT_KEY')}</Text>
                    <View style={styles.content}>
                        {/* enter data */}
                        <CheckBox
                            style={styles.checkBoxStyle}
                            onClick={() => {
                                this.setState({
                                    enterData: true,
                                    importFromFile: false,
                                    generateQRCode: false,
                                });
                            }}
                            rightText={i18n.t('IMPORT_KEYS_ENTER_DATA')}
                            rightTextStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', fontSize: 20, }}
                            checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                            isChecked={this.state.enterData}
                        />
                        {this.renderEnterData()}

                        {/* import from file */}
                        {
                            Utils.isAndroid() ?
                                <CheckBox
                                    style={styles.checkBoxStyle}
                                    onClick={() => {
                                        this.setState({
                                            enterData: false,
                                            importFromFile: true,
                                            generateQRCode: false,
                                        });
                                    }}
                                    rightText={i18n.t('IMPORT_KEYS_IMPORT_FROM_FILE')}
                                    rightTextStyle={{ fontSize: 20, color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                    checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                                    isChecked={this.state.importFromFile}
                                /> : null
                        }
                        {
                            Utils.isAndroid() ? this.renderImportFromFile() : null
                        }

                        {/*Generate QR Code */}
                        <CheckBox
                            style={styles.checkBoxStyle}
                            onClick={() => {
                                this.setState({
                                    enterData: false,
                                    importFromFile: false,
                                    generateQRCode: true,
                                });
                            }}
                            rightText={i18n.t('IMPORT_KEYS_GENERATE_QR_CODE')}
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

                        {/* import button */}
                        <BorderButton
                            title={i18n.t('IMPORT_KEYS_IMPORT')}
                            onPress={this._buttonImport}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_import]}
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
        this.setState({
            enterData: true,
            importFromFile: false,
            generateQRCode: false,
        });
        this.dismissSlideAnimationDialog();
    }

    _buttonImport = () => {
        Utils.dismissKeyboard();
        this.dismissSlideAnimationDialog();
        if (this.state.enterData) {
            this.onPressEnterData(this.state.address, this.state.publicKey, this.state.privateKey);
        } else if (this.state.importFromFile) {
            this.onPressImportFile(this.state.inputType, this.state.pathFile);
        } else if (this.state.generateQRCode) {
            this._scanQRCode();
        }
    }

    _selectFile = () => {
        if (Utils.isIOS()) return;
        Permissions.request('storage').then(response => {
            if (response == 'authorized') {
                RNFileSelector.Show(
                    {
                        title: i18n.t('IMPORT_KEYS_SELECT_FILE'),
                        onDone: (path) => {
                            this.setState({ pathFile: path });
                        },
                        onCancel: () => {
                            console.log('cancelled')
                        }
                    }
                )
            }
        })
    }

    _scanQRCode = () => {
        this.dismissSlideAnimationDialog();
        this.onPressQRCode();
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
    button_import: {
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
        marginBottom: 20
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