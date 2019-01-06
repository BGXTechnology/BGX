"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';

import { 
    View, 
    Text, 
    StyleSheet, 
    Image,
    TouchableOpacity
} from "react-native";

import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import ConfigAPI from "../api/ConfigAPI";
import RatingView from "../components/RatingView";
import i18n from '../translations/i18n';

export default class ItemMyOrders extends Component {

    constructor(props) {
        super(props);

        this.itemIndex = props.itemIndex;
        this.itemData = props.itemData;
        this.onPressSelectItem = props.onPressSelectItem;
        this.onLongPressItem = props.onLongPressItem;
        this.buttonBuyItAgain_clicked = props.buttonBuyItAgain_clicked;

        this.state = {
            isSeleted: props.indexSelected == this.itemIndex ? true : false,
            rating: this.itemData.rating,
            imageURL: this.itemData.thumbnail
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                isSelected: nextProps.indexSelected == this.itemIndex ? true : false,
                rating: nextProps.itemData.rating,
                imageURL: this.itemData.thumbnail
            });

            this.forceUpdate();
        }
    }

    measureView = (event) => {
        this.width = event.nativeEvent.layout.width;
        this.forceUpdate();
    }

    render() {
        return (
            <TouchableOpacity style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : '#f5f5f5'}]} 
            onLayout={(event) => this.measureView(event)} 
            onPress={this.props.onPressSelectItem.bind(this, this.itemIndex)}
            onLongPress={this.onLongPressItem.bind(this, this.itemIndex)}>
                <View style={{height: 140, width: 120}}>
                    <Image style={styles.imageItem} source={this.state.imageURL == "" ? require("../resource/imageItemDefault.png") : {uri: ConfigAPI.DOMAIN_IMAGE_MAGENTO + this.state.imageURL}}/>
                    <RatingView viewStyle={styles.viewRating} ratingCount={this.state.rating} imageSize={13}/>
                </View>
                
                {
                    this.state.isSelected ?  
                    <View style={[styles.viewBorder, {borderColor: CommonStyleSheet.THEME_DARCK ? '#484848' : '#c4c4c4'}]}/>
                    : null
                }
                <View style={{flex: 1}}>
                        <View style={styles.itemView}>  
                            <Text style={[styles.text1, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.itemData.name}</Text>
                            <Text style={[styles.text1, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.itemData.price + ' ' + i18n.t('BETA_SYMBOL')}</Text>
                            {/* color: '#de113e',  */}
                        </View>

                        <View style={[styles.itemView, {marginTop: 5}]}>
                            <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7d7d7d'}]}>{Utils.getDayMonthYearFromString(this.itemData.updateAt)}</Text>
                            <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7d7d7d'}]}>{Utils.upperCaseFirstLetter(this.itemData.statusItem)}</Text>
                        </View>
                        <BorderButton
                            title={i18n.t('BUY_IT_AGAIN')}
                            onPress={() => this.buttonBuyItAgain_clicked(this.itemIndex)}
                            titleStyle={[styles.titleButton, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                            style={[styles.button]}
                            imageStyle={{width: 166, height: 50}}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_my_order_dark.png") : require("../resource/buttonNew/icon_my_order_white.png")}
                        />

                        {/* <BorderButton
                            title={i18n.t('OK')}
                            onPress={this._buttonOk}
                            titleStyle={{ color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white' }}
                            style={[styles.button_ok]}
                            imageStyle={[CommonStyleSheet.viewCommonStyles.common_button_dialog_1]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/buttonNew/icon_ok_black.png") : require("../resource/buttonNew/icon_ok_1_white.png")}
                        /> */}
                </View>

                
            </TouchableOpacity>
            
        );
    };
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'row',
        marginLeft: 15,
        marginBottom: 15,
        // marginRight: 0,
        height: 140,
        borderRadius: 10
    },
    imageItem: {
        // marginLeft: 10, 
        marginTop: 10, 
        width: 120, 
        height: 100, 
        resizeMode: 'contain',
        overflow: 'hidden'
    },
    itemView: {
        flexDirection: 'row', 
        justifyContent: 'space-between', 
        marginLeft: 10,
        marginRight: 10, 
        marginTop: 20
    },
    text1: {
        fontSize: 18, 
        fontWeight: '500'
    },
    text2: { 
        fontSize: 14
    },
    button: {
        marginLeft: 10,
        marginTop: 20,
        width: 150,
        marginBottom: 20
    },
    viewBorder: {
        backgroundColor: 'transparent', 
        width: '100%',
        height: '100%',
        borderWidth: 3, 
        borderRadius: 10, 
        position: 'absolute'
    },
    viewRating: {
        marginLeft: 10,
        marginTop: 7,
        alignSelf: 'center'
    }
});