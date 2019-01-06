
import React, { Component } from 'react';
import { TouchableOpacity, StyleSheet, Text, ActivityIndicator, View } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation, } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import RatingView from '../components/RatingView';
import InputText from "../components/InputText";
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupComment extends Component {

    constructor(props) {
        super(props);

        this.state = {
            ratingStar: props.ratingStar,
            comment: props.comment
        }

        this.buttonOK_clicked = props.buttonOK_clicked;
    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                ratingStar: nextProps.ratingStar,
                comment: nextProps.comment
            });
        }
    }

    showSlideAnimationDialog = () => {
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    ratingItemFuction = (star, type) => {        
        this.setState({
            ratingStar: star
        });
    }

    _buttonCancel = () => {
        this.dismissSlideAnimationDialog();
    }

    _buttonOK_clicked = () => {
        this.dismissSlideAnimationDialog();
        this.buttonOK_clicked(this.state.comment, this.state.ratingStar);
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
                <View style={[styles.dialogContentView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c': 'white'}]}>
                    <Text style={[styles.text, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('LIKE_COMMENT')}</Text>

                    <InputText
                        labelStyle={""}
                        style={styles.input}
                        textInputStyle={{color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}}
                        label={""}
                        value={this.state.comment}
                        placeholder={i18n.t('WRITE_YOUR_REVIEW')}
                        placeholderTextColor={"#707070"}
                        returnKeyType='done'
                        secureTextEntry={false}
                        onChangeText={(comment) => this.setState({ comment })}
                    />

                    <RatingView viewStyle={{alignSelf: 'center', marginTop: 15}} ratingCount={this.state.ratingStar} imageSize={42} padding={13} enableRating={true} ratingItemFuction={this.ratingItemFuction} type={RatingView.RATING_TYPE.COMMENT}/>
                    
                    <View style={styles.view_content_button}>
                        {/* Cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel}
                            titleStyle={[styles.title_cancel, {color: CommonStyleSheet.THEME_DARCK ? 'white' : '#111111'}]}
                            style={[styles.button_cancel]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog_1,]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_cancel_black.png") : require("../resource/buttonNew/icon_cancel_2_white.png")}
                        />

                        {/* Ok button */}
                        <BorderButton
                            title={i18n.t('DONE')}
                            onPress={this._buttonOK_clicked}
                            titleStyle={[styles.title_save, {color: CommonStyleSheet.THEME_DARCK ? 'black' : '#fefefe'}]}
                            style={[styles.button_save]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog_1]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_ok_black.png") : require("../resource/buttonNew/icon_ok_1_white.png")}
                            
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
        height: 230
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
        marginBottom: 0,
        height: 25
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
        marginTop: 25
    },
    input: {
        marginLeft: 20,
        marginRight: 20
    }
});