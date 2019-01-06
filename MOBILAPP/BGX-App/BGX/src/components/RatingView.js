"use strict";
import React, { Component } from 'react';
import { Image, StyleSheet, View, Dimensions, TouchableOpacity } from 'react-native';
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';

let countStar = 5;
export default class RatingView extends Component {

    static RATING_TYPE = {
        FROM: 'FROM',
        TO: 'TO',
        COMMENT: 'COMMENT'
    }

    constructor(props) {
        super(props);
        
        this.state = {
            ratingCount: props.ratingCount
        };

        this.imageSize = props.imageSize;
        this.padding = props.padding == null ? 5 : props.padding;
        this.viewStyle = props.viewStyle;
        this.width = this.imageSize * 5 + 20 + this.padding * 2;
        this.type = props.type;

        this.enableRating = props.enableRating;
        this.ratingItemFuction = props.ratingItemFuction;
        
        this.imageSourceOff = require('../resource/icon_star_off_dark.png');
        this.imageSourceOn = CommonStyleSheet.THEME_DARCK ? require('../resource/icon_star_on_dark.png') : require('../resource/icon_star_on_white.png');
        if (this.type == RatingView.RATING_TYPE.COMMENT) {
            this.imageSourceOff = CommonStyleSheet.THEME_DARCK ? require('../resource/icon_star_comment_dark_off.png') : require('../resource/icon_star_comment_white_off.png');
            this.imageSource = CommonStyleSheet.THEME_DARCK ? require('../resource/icon_star_comment_dark_on.png') : require('../resource/icon_star_comment_white_on.png');
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            if(nextProps.ratingCount != null) {
                this.setState({
                    ratingCount: nextProps.ratingCount
                });
                this.forceUpdate();
            }
        }
    }

    _renderRatingStar = () => {
        let rating = []
        for(var i = 0; i < this.state.ratingCount; i++) {
            let index = i + 1;
            let image = 
            <TouchableOpacity key={i} disabled={!this.enableRating} onPress={() => this.ratingItemFuction(index, this.type)}>
                <Image key={i} style={{width: this.imageSize, height: this.imageSize, marginRight: this.padding}}
                source={this.imageSourceOn}/>  
            </TouchableOpacity>
            rating.push(image);
        }

        return rating;
    }

    _renderUnRatingStar = () => {
        let unRating = []
        let count = 5 - this.state.ratingCount
        for(var i = 0; i < count; i++) {
            let index = i + 1;
            let image = 
            <TouchableOpacity key={i+this.state.ratingCount} disabled={!this.enableRating} onPress={() => this.ratingItemFuction(index+this.state.ratingCount, this.type)}>
                <Image key={i+this.state.ratingCount} style={{width: this.imageSize, height: this.imageSize, marginRight: this.padding}}
                source={this.imageSourceOff}/> 
            </TouchableOpacity> 
            unRating.push(image);
        }

        return unRating;
    }

    render() {
        return (
            <View style={[ this.viewStyle, {flexDirection: 'row', width: this.width}]}> 

            {this._renderRatingStar()}
            {this._renderUnRatingStar()}
                        
            </View>
        )
    }
}

const styles = StyleSheet.create({
    viewBackground: {
        overflow: 'hidden',
        opacity: 0.6,
        flex: 1
    },
    image: {
        marginRight: 5
    }
});