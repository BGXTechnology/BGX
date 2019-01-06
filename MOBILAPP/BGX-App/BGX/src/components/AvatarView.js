"use strict";
import React, { Component } from 'react';
import { Image, StyleSheet, View, Dimensions } from 'react-native';
import ConfigAPI from '../api/ConfigAPI';
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';

export default class AvatarView extends Component {

    constructor(props) {
        super(props);
        this.containStyle = props.containStyle;
        this.backgroundColor = props.backgroundColor;
        this.width = 0;

        this.defaultAvatar = CommonStyleSheet.THEME_DARCK ? require("../resource/default_avatar_dark.png") : require("../resource/default_avatar_dark.png");
        
        this.state = {
            avatarSource: props.avatarSource != null ? {uri: (ConfigAPI.DOMAIN_IMAGE + props.avatarSource)} : this.defaultAvatar
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            if (nextProps.isSelectPhoto) {
                this.setState({
                    avatarSource: nextProps.avatarSource != null ? nextProps.avatarSource : this.defaultAvatar
                });
            } else {
                this.setState({
                    avatarSource: nextProps.avatarSource != null ? {uri: (ConfigAPI.DOMAIN_IMAGE + nextProps.avatarSource)} : this.defaultAvatar
                });
            }
        }
    }

    measureView = (event) => {
        this.width = event.nativeEvent.layout.width;
        this.forceUpdate();
    }

    render() {
        return (
            <View style={this.containStyle} onLayout={(event) => this.measureView(event)}>
                <View style={[styles.viewBackground, {backgroundColor: this.backgroundColor, borderRadius: this.width / 2}]}/> 
                <Image style={[styles.imageAvatar, {width: this.width - 10, height: this.width - 10, borderRadius: (this.width - 10) / 2}]}
                        source={this.state.avatarSource} />          
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
    imageAvatar: {
        marginLeft: 5,  
        marginTop: 5,
        position: 'absolute'
    }
});