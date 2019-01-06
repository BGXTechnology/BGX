"use strict";
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, View } from 'react-native';
import * as CommonStyleSheet from '../common/StyleSheet';

export const MENU_ICON = {
    LOCATION: 'location',
    DIGITAL_SHOP: 'digitalShop',
    MY_SHOP: 'myShop',
    DONT_MISS_OUT: 'dontMissOut',
}

export default class MenuItem extends Component {

    constructor(props) {
        super(props);

        this.title = props.title;
        this.style = props.style;
        this.icon = props.icon;
        this.onPress = props.onPress;

        this.state = {
            value: props.value,
            isActive: props.isActive != null ? props.isActive : true
        };

        this.iconSource = null;
        if (CommonStyleSheet.THEME_DARCK) {
            switch (props.icon) {
                case MENU_ICON.LOCATION: {
                    this.iconSource = require("../resource/menuIcon/icon_location_dark.png");
                    break;
                }
                case MENU_ICON.DIGITAL_SHOP: {
                    this.iconSource = require("../resource/menuIcon/icon_digitalShop_dark.png");
                    break;
                }
                case MENU_ICON.MY_SHOP: {
                    this.iconSource = require("../resource/menuIcon/icon_myShop_dark.png");
                    break;
                }
                case MENU_ICON.DONT_MISS_OUT: {
                    this.iconSource = require("../resource/menuIcon/icon_dontMissOut_dark.png");
                    break;
                }
            }
        } else {
            switch (props.icon) {
                case MENU_ICON.LOCATION: {
                    this.iconSource = require("../resource/menuIcon/icon_location_white.png");
                    break;
                }
                case MENU_ICON.DIGITAL_SHOP: {
                    this.iconSource = require("../resource/menuIcon/icon_digitalShop_white.png");
                    break;
                }
                case MENU_ICON.MY_SHOP: {
                    this.iconSource = require("../resource/menuIcon/icon_myShop_white.png");
                    break;
                }
                case MENU_ICON.DONT_MISS_OUT: {
                    this.iconSource = require("../resource/menuIcon/icon_dontMissOut_white.png");
                    break;
                }
            }
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                value: nextProps.value,
                isActive: nextProps.isActive != null ? nextProps.isActive : true
            });
        }
    }



    render() {
        return (
            <TouchableOpacity disabled={!this.state.isActive} onPress={this.onPress}>
                <View style={this.style}>
                    <View style={[{flexDirection: 'row', flex: 1, marginLeft: 10, backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                        <Image style={styles.iconImage}
                                source={this.iconSource}/>
                        <View style={styles.viewText}>
                            <Text style={styles.textLabel}>{this.title}</Text>
                            <Text style={[styles.textValue, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : 'black'}]}>{this.state.value}</Text>
                        </View>
                    </View>
                </View>
                <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#131313' : '#e8e8e8'}]}/>
                {
                    !this.state.isActive ? 
                    <View style={[this.style, {width: '100%', position: 'absolute', backgroundColor:CommonStyleSheet.THEME_DARCK ? 'rgba(0, 0, 0, 0.6)' : 'rgba(0, 0, 0, 0.2)'}]}/> :
                    null
                }
                
            </TouchableOpacity>
        )
    }
}

const styles = StyleSheet.create({
    iconImage: {
        width: 52, 
        height: 52, 
        alignSelf: 'center',
        marginLeft: 10
    },
    viewText: {
        height: 45, 
        alignSelf: 'center', 
        marginLeft: 20, 
        flex: 1,
        justifyContent: 'space-between'
    },
    textLabel: {
        fontSize: 15, 
        fontWeight: '300', 
        color: '#898989'
    },
    textValue: {
        fontSize: 20, 
        fontWeight: '500'
    },
    viewLine: {
        height: 1.5
    }
});