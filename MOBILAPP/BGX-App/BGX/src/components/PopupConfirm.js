
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupConfirm extends Component {

    constructor(props) {
        super(props);

        this.state = {
            message: props.message,
            okTitle: props.okTitle
        }

        this.cancelTitle = props.cancelTitle;
        this.buttonOK_clicked = props.buttonOK_clicked;
    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                message: nextProps.message,
                okTitle: nextProps.okTitle
            });
        }
    }

    showSlideAnimationDialog = () => {
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    render() {
        return (
            <PopupDialog
                dialogStyle={styles.background_dialog}
                width={300}
                ref={(popupDialog) => {
                    this.slideAnimationDialog = popupDialog;
                }}
                dialogAnimation={slideAnimation}
                onDismissed={() => Utils.dismissKeyboard()}
            >
                <View style={[styles.dialogContentView, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white', }]}>
                    <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111', }]}>{this.state.message}</Text>

                    <View style={styles.view_content_button}>
                        {/* Cancel button */}
                        <BorderButton
                            title={this.cancelTitle}
                            onPress={this._buttonCancel}
                            titleStyle={[styles.title_cancel, { color: CommonStyleSheet.THEME_DARCK ? 'white' : '#111111', }]}
                            style={[styles.button_cancel]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_dialog_drack_opacity.png") : require("../resource/icon_button_dialog_grey.png")}
                        />
                        {/* Ok button */}
                        <BorderButton
                            title={this.state.okTitle}
                            onPress={this.buttonOK_clicked}
                            titleStyle={[styles.title_save, { color: CommonStyleSheet.THEME_DARCK ? 'black' : '#fefefe', }]}
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
        this.dismissSlideAnimationDialog();
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    title: {
        fontSize: 16,
    },
    background_dialog: {
        backgroundColor: 'transparent',
        height: 170
    },
    dialogContentView: {
        flex: 1,
        flexDirection: 'column',

        borderRadius: 20,
    },
    text: {

        fontSize: 16,
        fontWeight: '500',
        textAlign: 'center',
        margin: 20,
        height: 50
    },
    title_cancel: {
        fontWeight: '600'
    },
    button_cancel: {

        marginLeft: 15,
        width: 120,
        flex: 0,
        height: 36,

        borderColor: '#555555'
    },
    title_save: {
        fontWeight: '600'
    },
    button_save: {
        marginRight: 15,
        width: 120,
        flex: 0,
        height: 36
    },
    view_content_button: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 20
    }
});