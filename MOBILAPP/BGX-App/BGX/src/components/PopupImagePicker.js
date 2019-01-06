import React, { Component } from 'react';
import { TouchableOpacity, TouchableHighlight, Image, StyleSheet, Text, View, ScrollView, TextInput, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupImagePicker extends Component {

    constructor(props) {
        super(props);

        this.imagePickerSelectIndex = props.imagePickerSelectIndex;
    }

    showSlideAnimationDialog = () => {
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    takePhoto_Selected = () => {
        this.imagePickerSelectIndex(0);
        this.slideAnimationDialog.dismiss();
    }

    photoLibrary_Selected = () => {
        this.imagePickerSelectIndex(1);
        this.slideAnimationDialog.dismiss();
    }

    render() {
        return (
            <PopupDialog
                dialogStyle={styles.background_dialog}
                width={Utils.appSize().width - 20}
                ref={(popupDialog) => {
                    this.slideAnimationDialog = popupDialog;
                }}
                dialogAnimation={slideAnimation}
            >
                <View style={styles.dialogContentView}>
                    <View style={styles.viewContain}>
                        <View style={[ styles.viewItem, {height: 44} ]}>
                            <Text style={styles.textTitle}>{i18n.t('CHOOSE_AVATAR')}</Text>
                        </View>
                        <View style={styles.viewLine}/>

                        <TouchableHighlight underlayColor='#a1a1a1' onPress={this.takePhoto_Selected}>
                            <View style={styles.viewItem}>
                                <Text style={styles.textItem}>{i18n.t('TAKE_PHOTO')}</Text>
                            </View>
                        </TouchableHighlight>
                        <View style={styles.viewLine}/>

                        <TouchableHighlight underlayColor='#a1a1a1' onPress={this.photoLibrary_Selected}>
                            <View style={styles.viewItem}>
                                <Text style={styles.textItem}>{i18n.t('CHOOSE_FROM_LIBRARY')}</Text>
                            </View>
                        </TouchableHighlight>
                    </View>

                    <TouchableHighlight style={styles.touchableHighlightView} underlayColor="#cdcdcd" onPress={this.dismissSlideAnimationDialog}>
                        <View style={styles.viewCancel}>
                            <Text style={[ styles.textItem, {fontSize: 18, fontWeight: '700'} ]}>{i18n.t('CANCEL')}</Text>
                        </View>
                    </TouchableHighlight>
                </View>
            </PopupDialog>
        )
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    title: {
        fontSize: 16,
    },
    background_dialog: {
        backgroundColor: 'transparent',
        height: 234,
        marginTop: Utils.isIOS() ? appSize.height - 234 : appSize.height - 234 - 32
    },
    dialogContentView: {
        flex: 1,
        backgroundColor: 'transparent',
        borderRadius: 15,
        overflow: 'hidden',
        justifyContent: 'space-between',
    },
    viewContain: {
        backgroundColor: '#cdcdcd', 
        height: 160, 
        borderRadius: 15, 
        overflow: 'hidden'
    },
    viewItem: {
        height: 57, 
        alignItems: 'center', 
        justifyContent: 'center'
    },
    viewLine: {
        height: 1, 
        backgroundColor: '#1d1d21'
    },
    textTitle: {
        color: '#8f8f8f', 
        fontSize: 16
    },
    textItem: {
        color: '#007aff', 
        fontSize: 18, 
        fontWeight: '500'
    },
    viewCancel: {
        height: 57, 
        alignItems: 'center', 
        justifyContent: 'center', 
        backgroundColor: 'white', 
        borderRadius: 15, 
    },
    touchableHighlightView: {
        height: 57, 
        borderRadius: 15, 
        overflow: 'hidden', 
        marginBottom: 10
    }
});