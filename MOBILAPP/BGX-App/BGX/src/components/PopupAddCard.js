
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import CheckBox from "react-native-check-box";
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupAddCard extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPress = props.onPress;
        this.state = {
            cardTypes: [],
            itemCard: null,
            idCardTypeCheck: 0,
            idCardDisable: 0
        }
    }

    componentDidMount() {

    }

    showSlideAnimationDialog = (id) => {
        this.setState({ idCardDisable: id });
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    setData = (cardTypes) => {
        this.setState({
            cardTypes: cardTypes
        });
    }


    render() {
        return (
            <PopupDialog
                dialogStyle={styles.background_dialog}
                width={Utils.appSize().width - 50}
                ref={(popupDialog) => {
                    this.slideAnimationDialog = popupDialog;
                }}
                dialogAnimation={slideAnimation}
                onDismissed={() => Utils.dismissKeyboard()}
            >
                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white', }]}>
                    <Text style={[styles.title, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('WELLET_TITLE_ADD_FUND')}</Text>
                    <Text style={styles.text}>{i18n.t('WELLET_MESSAGE_ADD_FUND')}</Text>
                    <View style={styles.list_card_type}>
                        <FlatList
                            data={this.state.cardTypes}
                            keyExtractor={(item, index) => index.toString()}
                            ItemSeparatorComponent={() => <View style={styles.separator} />}
                            renderItem={({ item, separators }) => (
                                item.cardTypeId != this.state.idCardDisable ?
                                    <CheckBox
                                        style={styles.checkBoxStyle}
                                        disabled={item.cardTypeId == this.state.idCardDisable ? true : false}
                                        onClick={() => {
                                            this.setState({
                                                idCardTypeCheck: item.id,
                                                itemCard: item
                                            });
                                        }}
                                        rightText={item.title}
                                        rightTextStyle={{ fontSize: 20, color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }}
                                        checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                                        isChecked={item.id === this.state.idCardTypeCheck ? true : false}
                                    />
                                    : null
                            )}
                        />
                    </View>
                    <View style={styles.view_content_button}>
                        {/* Singup button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }}
                            style={[styles.button_cancel]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack_opacity.png") : require("../resource/icon_button_dialog_grey.png")}
                        />

                        {/* Login button */}
                        <BorderButton
                            title={i18n.t('SAVE')}
                            onPress={this._buttonSave}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_save]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_white.png") : require("../resource/icon_button_dialog_drack.png")}
                        />
                    </View>
                </View>
            </PopupDialog>
        )
    }


    _buttonCancel = () => {
        this.setState({ idCardTypeCheck: 0 });
        this.dismissSlideAnimationDialog();
    }

    _buttonSave = () => {
        if (this.state.idCardTypeCheck === 0) {
            Utils.showAlert(i18n.t('PLEASE_SELECT_A_CARD'), true, null);
            return;
        }
        this.setState({ idCardTypeCheck: 0 });
        this.dismissSlideAnimationDialog();
        this.onPress(this.state.itemCard);
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
    list_card_type: {
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


    }
});