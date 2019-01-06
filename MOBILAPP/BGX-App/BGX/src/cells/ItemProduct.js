"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';
import ConfigAPI from "../api/ConfigAPI";
import RatingView from "../components/RatingView";
import i18n from '../translations/i18n';

import { 
    View, 
    Text, 
    StyleSheet, 
    Image,
    TouchableOpacity
} from "react-native";

export default class ItemProduct extends Component {

    constructor(props) {
        super(props);

        this.itemIndex = props.itemIndex;
        this.width = 0;
        this.onPressSelectItem = props.onPressSelectItem;
        this.onLongPressItem = props.onLongPressItem;

        this.state = {
            isSelected: props.indexSelected == this.itemIndex ? true : false,
            rating: 0
        };

        let itemData = props.itemData;
        this.name = itemData.name;
        this.price = itemData.price;
        this.imageURL = itemData.thumbnail;

        this.isRenderItem = true;
        
        if (itemData.id == null) {
            this.isRenderItem = false;
        }
    }

    componentWillReceiveProps(nextProps) {
    
        if (this.props !== nextProps) {
            this.setState({
                isSelected: nextProps.indexSelected == this.itemIndex ? true : false,
                rating: nextProps.itemData.rating
            });
        }
    }

    measureView = (event) => {
        this.width = event.nativeEvent.layout.width;
        this.forceUpdate();
    }

    render() {
        if (this.isRenderItem) {
            return (
            
                <TouchableOpacity style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : '#f5f5f5'}]} 
                                    onLayout={(event) => this.measureView(event)}
                                    onPress={this.onPressSelectItem.bind(this, this.itemIndex)} 
                                    onLongPress={this.onLongPressItem.bind(this, this.itemIndex)}>
                    <Image style={[styles.image, {width: this.width - 20}]} source={this.imageURL == "" ? require("../resource/imageItemDefault.png") : {uri: ConfigAPI.DOMAIN_IMAGE_MAGENTO + this.imageURL}}/>
                    <View style={[styles.view, {width: this.width - 20}]}>
                        <Text style={[styles.text1, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.name}</Text>
                        <Text style={styles.text2}>{this.price  + ' ' + i18n.t('BETA_SYMBOL')}</Text>
                    </View>
    
                    <RatingView viewStyle={styles.viewRating} ratingCount={this.state.rating} imageSize={18}/>
    
                    {
                        this.state.isSelected ?  
                        <View style={[styles.viewBorder, {borderColor: CommonStyleSheet.THEME_DARCK ? '#484848' : '#c4c4c4'}]}/>
                        : null
                    }
                    
                    </TouchableOpacity>
            );
        }

        return (
            <TouchableOpacity disabled={true} style={[styles.container, {backgroundColor: 'transparent'}]}/> 
        );
    };
}

const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        flex: 1,
        marginLeft: 15,
        marginBottom: 15,
        height: 220,
        borderRadius: 10,
        overflow: 'hidden',
    },
    image: {
        marginTop: 10,
        marginLeft: 10,
        marginRight: 10,
        height: 135,
        resizeMode: 'contain'
    },
    view: {
        marginTop: 10,
        marginLeft: 10,
        marginRight: 10,
        flexDirection: 'row', 
        justifyContent: 'space-between',
        alignItems: 'baseline',
    },
    text1: {
        fontSize: 13
    },
    text2: {
        color: '#de113e',
        fontWeight: '500',
        fontSize: 16
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
        left: 10,
        position: 'absolute',
        bottom: 15
    }
});