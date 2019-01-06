"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';

import { 
    View, 
    Text, 
    StyleSheet, 
    Image
} from "react-native";

import BorderButton from '../components/BorderButton';
import BorderButtonNew from '../components/BorderButtonNew';
import * as CommonStyleSheet from '../common/StyleSheet';
import RatingView from "../components/RatingView";
import ConfigAPI from "../api/ConfigAPI";
import i18n from '../translations/i18n';

export default class ItemShoppingCart extends Component {

    constructor(props) {
        super(props);

        this.itemData = props.itemData;

        this.itemIndex = props.itemIndex;
        this.isFavorite = props.isFavorite;
        this.buttonDelete_clicked = props.buttonDelete_clicked;
        this.buttonSaveFavorite_clicked = props.buttonSaveFavorite_clicked;

        this.imageURL = this.itemData.thumbnail;

        this.state = {
            rating: this.itemData.rating
        };
    }

    componentWillReceiveProps(nextProps) {
    
        if (this.props !== nextProps) {
            this.setState({
                isSelected: nextProps.indexSelected == this.itemIndex ? true : false,
                rating: nextProps.itemData.rating
            });
        }
    }

    render() {
        return (
            <View style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : '#f5f5f5'}]}>
                <View style={styles.viewImage}>
                    <View style={styles.viewCoverImage}>
                        <Image style={styles.imageItem} source={this.imageURL == "" ? require("../resource/imageItemDefault.png") : {uri: ConfigAPI.DOMAIN_IMAGE_MAGENTO + this.imageURL}}/>
                    </View>
                    <View style={{flex: 1, marginRight: 10, marginTop: 10}}>
                        <View style={styles.viewTextName}>
                            <Text style={[styles.textName, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.itemData.name}</Text>
                            <Text  style={styles.textBalance}>{this.itemData.price + ' ' + i18n.t('BETA_SYMBOL')}</Text>
                        </View>
                        <RatingView viewStyle={styles.viewRating} ratingCount={this.state.rating} imageSize={18}/>
                        <Text style={[styles.textDescription, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={4}>{this.itemData.shortDescription}
                        </Text>
                    </View>
                </View>
                <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#313131' : '#c9c9c9'}]}/>
                <View style={styles.viewSubjectContain}>
                    <View style={{width: 100}}>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('SUBJECT')}</Text>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('PROVIDER')}</Text>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('AUTHOR')}</Text>
                    </View>
                            
                    <View style={{flex: 1}}>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.itemData.subject}</Text>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.itemData.provider}</Text>
                        <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.itemData.author}</Text>
                    </View>
                </View>
                <View style={styles.viewButton}>

                    <BorderButtonNew
                                    title={i18n.t('DELETE')}
                                    onPress={() => this.buttonDelete_clicked(this.itemIndex)}
                                    titleStyle={[styles.titleButton1, {color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}]}
                                    style={{height: 50, width: 119}}
                                    imageStyle={{height: 50, width: 119}}
                                    buttonType={CommonStyleSheet.THEME_DARCK ? 
                                        BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_DARK_SHORT : 
                                        BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_WHITE_SHORT}/>


                    <BorderButtonNew
                                    title={!this.isFavorite ? i18n.t('SAVE_IN_FAVORITE') : i18n.t('ADD_TO_CART')}
                                    onPress={() => this.buttonSaveFavorite_clicked(this.itemIndex)}
                                    titleStyle={[styles.titleButton2, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                                    style={{height: 50, width: 195}}
                                    imageStyle={{height: 50, width: 195}}
                                    buttonType={CommonStyleSheet.THEME_DARCK ? BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_DARK : BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_WHITE}/>
                </View>
            </View>            
        );
    };
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        marginLeft: 15,
        marginBottom: 15,
        marginRight: 15,
        height: 313,
        borderRadius: 10
    },
    viewImage: {
        width: '100%',
        height: 171,
        flexDirection: 'row'
    },
    viewCoverImage: {
        width: 136, 
        height: '100%', 
        overflow: 'hidden'
    },
    imageItem: {
        margin: 10, 
        width: 116, 
        height: 116, 
        resizeMode: 'contain'
    },
    viewTextName: {
        flexDirection: 'row', 
        justifyContent: 'space-between'
    },
    textName: { 
        fontSize: 20, 
        fontWeight: '500'
    },
    textBalance: {
        color: '#de113e', 
        fontSize: 20, 
        fontWeight: '500'
    },
    textDescription: {
        marginTop: 10, 
        fontSize: 16
    },
    viewRating: {
        marginTop: 10
    },
    viewLine: { 
        height: 1,  
        marginLeft: 10, 
        marginRight: 10,
    },
    viewSubjectContain: {
        marginLeft: 10, 
        marginRight: 10, 
        marginTop: 15,
        marginBottom: 15,
        flexDirection: 'row'
    },
    textSubject: {
        fontSize: 14,
        marginTop: 3
    },
    viewButton: {
        marginLeft: 10, 
        marginRight: 10, 
        marginTop: -10,
        height: 36, 
        flexDirection: 'row', 
        justifyContent: 'space-between'
    },
    button1: {
        // width: '32%',
        width: 108,
        flex: 0
    },
    button2: {
        width: 195,
        flex: 0
    },
    button2Favorite: {
        width: 152,
        flex: 0
    },
    titleButton1: {
        fontWeight: '600'
    },
    titleButton2: {
        fontWeight: '600'
    }
});