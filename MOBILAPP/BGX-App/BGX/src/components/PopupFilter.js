
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, View, ScrollView, TextInput, FlatList } from 'react-native';
import Utils from '../common/Utils';
import PopupDialog, { DialogTitle, DialogButton, SlideAnimation, ScaleAnimation, FadeAnimation } from 'react-native-popup-dialog';
import * as Constants from '../common/Constants';
import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import CheckBox from "react-native-check-box";
import RatingView from "../components/RatingView";
import ConfigAPI from "../api/ConfigAPI";
import {BaseService} from "../api/BaseService";
import LoadingIndicator from "../components/LoadingIndicator";
import CategoryItem from '../models/CategoryItem';
import ItemCheckBox from '../cells/ItemCheckBox';
import i18n from '../translations/i18n';

const slideAnimation = new SlideAnimation({ slideFrom: 'bottom' });

export default class PopupFilter extends Component {

    constructor(props) {
        super(props);

        this.state = {
            priceFrom: props.priceFrom,
            priceTo: props.priceTo,
            ratingStarFrom: props.ratingFrom,
            ratingStarTo: props.ratingTo,
            listCategory: []
        }

        this.onFilter = props.onFilter;

        this.checkBoxHeight = 55;

        this.listCategorySelected = props.listCategorySelected;
    }

    componentDidMount() {
        this.requestGetListCategory();
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.listCategorySelected = nextProps.listCategorySelected;

            if (this.listCategorySelected == "-1") {

                var array = [];
                let count = this.state.listCategory.length;
                for (var item of this.state.listCategory) {
                    item.isSelected = true;
                    array.push(item);
                }

                this.setState({
                    priceFrom: "0",
                    priceTo: "10000",
                    ratingStarFrom: 0,
                    ratingStarTo:5,
                    listCategory: array
                });
            }

            this.setState({
                priceFrom: nextProps.priceFrom,
                priceTo: nextProps.priceTo,
                ratingStarFrom: nextProps.ratingFrom,
                ratingStarTo: nextProps.ratingTo
            });
        }
    }

    _buttonCancel_clicked = () => {
        this.dismissSlideAnimationDialog();
    }

    _buttonFilter_clicked = () => {

        let totalCategory = this.state.listCategory.length;
        let priceFrom = this.state.priceFrom;
        let priceTo = this.state.priceTo;
        let starFrom = this.state.ratingStarFrom;
        let starTo = this.state.ratingStarTo;

        if (priceFrom == "" || priceTo == "") {
            Utils.showAlert(i18n.t('MESSAGE_EMPTY_PRICE'), true, null);
            return;
        }

        if (!Utils.isValidPrice(priceFrom) || !Utils.isValidPrice(priceTo) || this._countDotInText(priceFrom) >= 2 || this._countDotInText(priceTo) >= 2) {
            Utils.showAlert(i18n.t('MESSAGE_INVALID_PRICE'), true, null);
            return;
        }
        
        var stringCategory = "";
        for (let item of this.state.listCategory) {
            if (item.isSelected) {
                stringCategory += item.categoryID + ',';
            }
        }
        stringCategory = stringCategory.substring(0, stringCategory.length - 1);

        

        if (stringCategory == "") {
            Utils.showAlert(i18n.t('MESSAGE_CHOOSE_CATEGORY'), true, null);
            return;
        }

        if (parseFloat(priceFrom) > parseFloat(priceTo)) {
            Utils.showAlert(i18n.t('MESSAGE_PRICE_FROM_SMALLER_PRICE_TO'), true, null);
            return;
        }

        if (starFrom > starTo) {
            Utils.showAlert(i18n.t('MESSAGE_STAR_FROM_SMALLER_STAR_TO'), true, null);
            return;
        }

        this.dismissSlideAnimationDialog();
        this.onFilter(this.state.listCategory, totalCategory, priceFrom, priceTo, starFrom, starTo);
    }

    showSlideAnimationDialog = () => {
        this.slideAnimationDialog.show();
    }

    dismissSlideAnimationDialog = () => {
        this.slideAnimationDialog.dismiss();
    }

    ratingItemFuction = (star, type) => {
        if (type === RatingView.RATING_TYPE.FROM) {
            this.setState({
                ratingStarFrom: star
            });
        } else if (type === RatingView.RATING_TYPE.TO) {
            this.setState({
                ratingStarTo: star
            });
        }
    }

    _countDotInText(text) {
    
        var count = 0;
        for (var i = 0; i < text.length; i++) {
            if (text[i] === '.') {
                count += 1;
            }
        }   

        return count;
    }

    _onChangeTextFrom(text) {
        this.setState({ priceFrom: " " })
        let valueText = Utils.removeEmojis(text);
        setTimeout(() => {
            this.setState({ priceFrom: valueText.replace(/[^0-9.]/g, '') })
        }, 1);
    }

    _onChangeTextTo(text) {
        this.setState({ priceTo: " " })
        let valueText = Utils.removeEmojis(text);
        setTimeout(() => {
            this.setState({ priceTo: valueText.replace(/[^0-9.]/g, '') })
        }, 1);
    }

    _itemCheckBoxCheck = (itemIndex, isCheck) => {
        var arrayCheckBoxSelected = this.state.listCategory;
        arrayCheckBoxSelected[itemIndex].isSelected = isCheck

        this.setState({
            listCategory: arrayCheckBoxSelected
        });
    }

    _clearStarFrom = () => {
        this.setState({
            ratingStarFrom: 0
        });
    }

    _clearStarTo = () => {
        this.setState({
            ratingStarTo: 0
        });
    }

    //Request Data
    requestGetListCategory = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_CATEGORY, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_LIST_CATEGORY, 
        };
    
        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    async onSuccess(code, message, data, method) {

        this.setState({ progressView: false });

        if (method === ConfigAPI.METHOD_GET_LIST_CATEGORY) {
            if(data != null && data.items != null) {

                var array = [];

                if (this.listCategorySelected == "-1") {
                    for (let item of data.items) {
                        let object = new CategoryItem(item);
                        object.isSelected = true;
                        array.push(object);
                    }
                } else {
                    let arrayCategorySelected = this.listCategorySelected.split(',');

                    for (let item of data.items) {
                        let object = new CategoryItem(item);
                        for (let idCat of arrayCategorySelected) {
                            if (idCat == item.categoryID) {
                                object.isSelected = true;
                                continue;
                            }
                        }
                        
                        array.push(object);
                    }
                }
 
                this.setState({ 
                    listCategory: array
                });

                this.checkBoxHeight = 25 + 20 + array.length * 26 + (array.length - 1) * 8 + 10;
                
                this.forceUpdate();
            }
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
    }

    measureView = (event) => {
        let height = event.nativeEvent.layout.height;
        // alert(height)
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
                <View style={[styles.dialogContentView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#171717' : '#f5f5f5'}]}>
                    <View style={[styles.viewTextTitlePopup, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : 'white' }]}>
                        <Text style={[styles.textTitlePopup, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('FILTER')}</Text>
                        <View style={[styles.viewLine, {marginTop: -10, backgroundColor: CommonStyleSheet.THEME_DARCK ? '#2e2e2e' : '#c9c9c9' }]}/>
                    </View>

                    <ScrollView>
                        <View style={{height: this.checkBoxHeight}}>
                            <Text style={[styles.textTitle, {color: CommonStyleSheet.THEME_DARCK ? 'white' : '#111111'}]}>{i18n.t('CATEGORIES')}</Text>
                            <FlatList
                                data={this.state.listCategory}
                                extraData={this.state}
                                scrollEnabled={false}
                                keyExtractor={(item, index) => index.toString()}
                                renderItem={({ item, index }) => (
                                    <ItemCheckBox   itemIndex={index} 
                                                        itemData={item}
                                                        onPressSelectItem={this._itemCheckBoxCheck}/>
                                )}/>

                            <View style={[styles.viewLine, {marginTop: 9}]}/>
                        </View>
                        {/* value={this.state.priceFrom} */}
                        <View style={{height: Utils.isIOS() ? 100 : 107}}>
                            <Text style={[styles.textTitle, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('PRICE')}</Text>
                            <View style={styles.viewCheckBox}>
                                <View style={{marginRight: 10, width: 110}}>
                                    <Text style={{color: '#949494'}}>{i18n.t('FROM') + ":"}</Text>
                                    <TextInput style={[styles.textInputCheckBox, {color: CommonStyleSheet.THEME_DARCK ? '#949494' : '#111111'}]}  
                                    onLayout={(event) => this.measureView(event)} 
                                    keyboardType='numeric' 
                                    returnKeyType='done' 
                                    onChangeText={(text) => this._onChangeTextFrom(text)}
                                    // onChangeText={(priceFrom) => this.setState({ priceFrom: priceFrom.replace(/[^0-9.]/g, '') })}
                                    >{this.state.priceFrom}</TextInput>
                                    <View style={styles.viewLineCheckBox}/>
                                </View>
                                <View style={{width: 110}}>
                                    <Text style={{color: '#949494'}}>{i18n.t('TO') + ":"}</Text>
                                    <TextInput style={[styles.textInputCheckBox, {color: CommonStyleSheet.THEME_DARCK ? '#949494' : '#111111'}]} 
                                    keyboardType='numeric' 
                                    returnKeyType='done' 
                                    onChangeText={(text) => this._onChangeTextTo(text)}
                                    // onChangeText={(priceTo) => this.setState({ priceTo: priceTo.replace(/[^0-9.]/g, '') })}
                                    >{this.state.priceTo}</TextInput>
                                    <View style={styles.viewLineCheckBox}/>
                                </View>
                                <Text style={{ color: '#de113e', marginTop: Utils.isIOS() ? 20 : 23}}>{i18n.t('BETA_SYMBOL')}</Text>
                            </View>
                            <View style={[styles.viewLine, {marginTop: Utils.isIOS() ? 7 : 5}]}/>
                        </View>

                        <View style={{height: 112}}>
                            <Text style={[styles.textTitle, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('RATING')}</Text>
                            <View style={{flexDirection: 'row'}}>
                                <View style={{width: 70}}>
                                    <Text style={styles.textSubtitle}>{i18n.t('FROM')}</Text>
                                    <Text style={[styles.textSubtitle, {marginTop: 5}]}>{i18n.t('TO')}</Text>
                                </View>
                                {/* viewStyle={{alignSelf: 'center'}} */}
                                <View style={{flex: 1}}>
                                    <View style={{flexDirection: 'row', alignItems: 'center'}}>
                                        <RatingView ratingCount={this.state.ratingStarFrom} imageSize={27} padding={10} enableRating={true} ratingItemFuction={this.ratingItemFuction} type={RatingView.RATING_TYPE.FROM}/>
                                        <TouchableOpacity onPress={this._clearStarFrom}>
                                            <Image style={styles.iconClose} source={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_clear_white.png") : require("../resource/icon_clear_black.png")}/>
                                        </TouchableOpacity>
                                    </View>
                                    
                                    <View style={{flexDirection: 'row', alignItems: 'center'}}>
                                        <RatingView viewStyle={{marginTop: 5}} ratingCount={this.state.ratingStarTo} imageSize={27} padding={10} enableRating={true} ratingItemFuction={this.ratingItemFuction} type={RatingView.RATING_TYPE.TO}/>
                                        <TouchableOpacity onPress={this._clearStarTo}>
                                            <Image style={[styles.iconClose, {marginTop: 5}]} source={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_clear_white.png") : require("../resource/icon_clear_black.png")}/>
                                        </TouchableOpacity>
                                    </View>
                                </View>
                            </View>
                        </View>
                    </ScrollView>
                    
                    <View style={styles.view_content_button}>
                        <View style={[styles.viewLine, {top: 0, position: 'absolute'}]}/>
                        {/* Cancel button */}
                        <BorderButton
                            title={i18n.t('CANCEL')}
                            onPress={this._buttonCancel_clicked}
                            titleStyle={[styles.title_cancel, {color: CommonStyleSheet.THEME_DARCK ? 'white' : '#111111'}]}
                            style={[styles.button_cancel]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button_dialog_1}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_cancel_black.png") : require("../resource/buttonNew/icon_cancel_2_white.png")}
                        />

                        {/* Ok button */}
                        <BorderButton
                            title={i18n.t('FILTER')}
                            onPress={this._buttonFilter_clicked}
                            titleStyle={[styles.title_save, {color: CommonStyleSheet.THEME_DARCK ? 'black' : '#fefefe'}]}
                            style={[styles.button_save]}
                            imageStyle={CommonStyleSheet.viewCommonStyles.common_button_dialog_1}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_ok_black.png") : require("../resource/buttonNew/icon_ok_1_white.png")}
                        />
                    </View>
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
        height: appSize.height >= 585 ? 495 : appSize.height - 90,
        marginTop: -70
    },
    dialogContentView: {
        flex: 1,
        flexDirection: 'column',
        borderRadius: 20,
        overflow: 'hidden',
        justifyContent: 'space-between',
    },
    viewTextTitlePopup: {
        height: 55
    },
    textTitlePopup: {
        fontSize: 20,
        fontWeight: '500',
        textAlign: 'center',
        margin: 20,
        height: 25
    },
    textSubtitle: {
        marginLeft: 13, 
        marginTop: 5, 
        height: 25, 
        fontSize: 16, 
        fontWeight: '500', 
        color: '#949494'
    },
    viewLine: {
        height: 1, 
        width: '100%'
    },
    textTitle: {
        marginLeft: 13, 
        marginTop: 20, 
        height: 25, 
        fontSize: 18, 
        fontWeight: '500'
    },
    //Checkbox
    viewCheckBox: {
        marginLeft: 13, 
        marginRight: 13, 
        height: Utils.isIOS() ? 45 : 55,
        justifyContent: 'space-between', 
        flexDirection: 'row'
    },
    textInputCheckBox: {
        marginLeft: 3, 
        marginTop: 3, 
        marginTop: Utils.isIOS() ? 0 : -10,
        height: Utils.isIOS() ? 17 : 49
    },
    viewLineCheckBox: {
        height: 1, 
        backgroundColor: '#898989', 
        marginTop: Utils.isIOS() ? 3 : -13
    },
    //Button bottom
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
        height: 78,
        marginBottom: 0,
        alignItems: 'center'
    },
    iconClose: {
        width: 17, 
        height: 17, 
        marginLeft: 20,
    }
});