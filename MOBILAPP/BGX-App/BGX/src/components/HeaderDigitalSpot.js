"use strict";
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, View, ImageBackground } from 'react-native';
import Utils from '../common/Utils';
import i18n from '../translations/i18n';

export default class HeaderDigitalSpot extends Component {

    static VIEW_CONTROLLER = {
        MY_ORDER: 'MY_ORDER',
        FAVORITE: 'FAVORITE',
        SHOPPING_CART: 'SHOPPING_CART'
    }

    constructor(props) {
        super(props);

        this.headerTitle = props.headerTitle;
        this.buttonMenuHeader_clicked = props.buttonMenuHeader_clicked;
        this.buttonBackHeader_clicked = props.buttonBackHeader_clicked;
        this.buttonMyOrdersHeader_clicked = props.buttonMyOrdersHeader_clicked;
        this.buttonFavoriteHeader_clicked = props.buttonFavoriteHeader_clicked;
        this.buttonShoppingCartHeader_clicked = props.buttonShoppingCartHeader_clicked;

        this.isShowBack = props.isShowBack;

        this.imageMyOrder = null;
        this.imageFavorite = null;
        this.imageShoppingCart = null;

        this.isShowMyOrder = true;
        
        this.style = {};

        switch(props.page) {
            case HeaderDigitalSpot.VIEW_CONTROLLER.MY_ORDER: 
                this.imageMyOrder = require("../resource/header_icon/icon_myOrder_header_on.png");
                this.imageFavorite = require("../resource/header_icon/icon_like_header_off.png");
                this.imageShoppingCart = require("../resource/header_icon/icon_cart_header_off.png");
                break;
            case HeaderDigitalSpot.VIEW_CONTROLLER.FAVORITE: 
                this.imageMyOrder = require("../resource/header_icon/icon_myOrder_header_off.png");
                this.imageFavorite = require("../resource/header_icon/icon_like_header_on.png");
                this.imageShoppingCart = require("../resource/header_icon/icon_cart_header_off.png");
                break;
            case HeaderDigitalSpot.VIEW_CONTROLLER.SHOPPING_CART: 
                this.imageMyOrder = require("../resource/header_icon/icon_myOrder_header_off.png");
                this.imageFavorite = require("../resource/header_icon/icon_like_header_off.png");
                this.imageShoppingCart = require("../resource/header_icon/icon_cart_header_on.png");
                break;

            default:
                this.imageMyOrder = require("../resource/header_icon/icon_myOrder_header_off.png");
                this.imageFavorite = require("../resource/header_icon/icon_like_header_off.png");
                this.imageShoppingCart = require("../resource/header_icon/icon_cart_header_off.png");
                break;
        }
    }

    measureView = (event) => {
        this.titleWidth = event.nativeEvent.layout.width;
        this.calculate();
        this.forceUpdate();
    }

    calculate = () => {
        let freeSpace = Utils.appSize().width - (22 + 15 + 5 + (24 + 15)*3 + 5);

        if (this.titleWidth == freeSpace) {
            this.style = {
                width: this.titleWidth,
                marginLeft: 5,
                textAlign: 'center'
            }
        } else if (this.titleWidth < freeSpace) {
            this.style = {
                width: this.titleWidth,
                marginLeft: (freeSpace - this.titleWidth) / 2 + 5,
                textAlign: 'center'
            }
        } else {
            this.style = {
                width: freeSpace,
                marginLeft: 5,
                textAlign: 'left'
            }
        }
    }

    render() {
        return (
            <ImageBackground style={styles.imageBackground} source={require("../resource/header_background.png")}>
                <View style={styles.viewHeaderTitle}>
                {
                    !this.isShowBack ? 
                    <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonMenuHeader_clicked}>
                        <Image style={styles.iconMenu} source={require("../resource/icon_menu.png")}/>
                    </TouchableOpacity>
                    :
                    <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonBackHeader_clicked}>
                        <Image style={styles.iconBack} source={require("../resource/icon_back_white.png")}/>
                    </TouchableOpacity>
                }
                    <Text style={[styles.textTitleHeader, this.style]} onLayout={(event) => this.measureView(event)}>{this.headerTitle}</Text>
                    <View style={[{flexDirection: 'row'}]}>
                        
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonMyOrdersHeader_clicked}>
                            <Image style={styles.iconCart} source={this.imageMyOrder}/>
                        </TouchableOpacity>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonFavoriteHeader_clicked}>
                            <Image style={styles.iconCart} source={this.imageFavorite}/>
                        </TouchableOpacity>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonShoppingCartHeader_clicked}>
                            <Image style={styles.iconCart} source={this.imageShoppingCart}/>
                        </TouchableOpacity>
                        </View>                                
                </View>
            </ImageBackground>
        )
    }
}

const styles = StyleSheet.create({
    imageBackground: {
        width: '100%', 
        height: 64, 
        flexDirection: 'row'
    },
    viewHeaderTitle: {
        width: '100%', 
        flexDirection: 'row', 
        marginTop: 30,
        justifyContent: 'space-between'
    },
    iconMenu: {
        width: 22, 
        height: 16, 
        marginLeft: 15,
        marginTop: 3
    },
    iconBack: {
        width: 22, 
        height: 14, 
        marginLeft: 10
    },
    iconCart: {
        width: 24, 
        height: 22, 
        marginRight: 15,
        alignSelf: 'baseline',
    },
    textTitleHeader: {
        color: 'white', 
        fontSize: 24, 
        fontWeight: '300',
        // flex: 1,
        textAlign: 'center',
        marginTop: -3,
        // marginRight: 5,
        // marginLeft: 64,
        // marginLeft: 5,
    }
});