"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';

import { 
    View, 
    Text, 
    StyleSheet, 
    Image,
} from "react-native";

import BorderButton from '../components/BorderButton';
import * as CommonStyleSheet from '../common/StyleSheet';
import ConfigAPI from "../api/ConfigAPI";
import RatingView from "../components/RatingView";
import i18n from '../translations/i18n';

export default class ItemComment extends Component {

    constructor(props) {
        super(props);

        this.itemData = props.itemData;
    }

    render() {
        return (
            <View style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? 'transparent' : '#f5f5f5'}]}>
                <View style={styles.itemViewAvatar}>
                    <View style={{flexDirection: 'row'}}>
                        <Image style={[styles.imageItem, {borderColor: CommonStyleSheet.THEME_DARCK ? '#1c1919' : '#e6e6e6'}]} source={CommonStyleSheet.THEME_DARCK ? require("../resource/avatar_comment_dark.png") : require("../resource/avatar_comment_white.png")}/>
                        <View style={styles.viewText}>
                            <Text style={[styles.textUserName, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>{this.itemData.nickname}</Text>
                            <Text style={[styles.textDate, {color: CommonStyleSheet.THEME_DARCK ? "#898989" : "#7a7a7a"}]}>{Utils.getDayMonthYearFromString(this.itemData.createdAt)}</Text>
                        </View>
                    </View>
                    <RatingView viewStyle={styles.viewRating} ratingCount={this.itemData.rating} imageSize={16}/>
                </View>
                <Text style={[styles.textUserName, {marginTop: 10, color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>{this.itemData.detail}</Text>

                <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? "#131313" : "#eeeeee"}]}/>
            </View>
        );
    };
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        marginBottom: 15
    },
    imageItem: {
        marginLeft: 0, 
        marginTop: 10, 
        width: 40, 
        height: 40, 
        resizeMode: 'contain',
        borderRadius: 20,
        borderWidth: 2,
        overflow: 'hidden',
    },
    itemViewAvatar: {
        flexDirection: 'row', 
        justifyContent: 'space-between'
    },
    viewText: {
        width: Utils.appSize().width - 190, 
        marginLeft: 10, 
        marginTop: 10
    },
    textUserName: {
        fontSize: 16,
        fontWeight: '500'
    },
    textDate: {
        fontSize: 14,
        marginTop: 3
    },
    viewRating: {
        marginTop: 10,
    },
    viewLine: { 
        height: 1, 
        marginTop: 15
    }
});