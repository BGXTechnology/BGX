"use strict";
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, View } from 'react-native';
import * as CommonStyleSheet from '../common/StyleSheet';
import i18n from '../translations/i18n';

export default class BottomBarDigitalSpot extends Component {

    constructor(props) {
        super(props);

        this.buttonFavorite_clicked = props.buttonFavorite_clicked;
        this.buttonDetail_clicked = props.buttonDetail_clicked;
        this.buttonToCart_clicked = props.buttonToCart_clicked;
        this.buttonFilter_clicked = props.buttonFilter_clicked;

        let imgFavorite = null;
        let imgDetail = null;
        let imgToCart = null;
        let imgFilter = null;

        if (props.isActive) {
            if (props.isFavorite) {
                imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_active.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_active.png");
            } else {
                imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_on.png");
            }

            imgDetail = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_detail_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_detail_white_on.png");
            imgToCart = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_toCart_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_toCart_white_on.png");
        } else {
            imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_off.png");
            imgDetail = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_detail_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_detail_white_off.png");
            imgToCart = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_toCart_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_toCart_white_off.png");
        }

        if (props.isActiveFilter) {
            imgFilter = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_filter_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_filter_white_on.png")
        } else {
            imgFilter = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_filter_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_filter_white_off.png")
        }

        this.state = {
            isActive: props.isActive,
            isActiveFilter: props.isActiveFilter,
            imageFavorite: imgFavorite,
            imageDetail: imgDetail,
            imageToCart: imgToCart,
            imageFilter: imgFilter,
            isFavorite: props.isFavorite
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            let imgFavorite = null;
            let imgDetail = null;
            let imgToCart = null;
            let imgFilter = null;

            if (nextProps.isActive) {
                if (nextProps.isFavorite) {
                    imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_active.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_active.png");
                } else {
                    imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_on.png");
                }

                imgDetail = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_detail_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_detail_white_on.png");
                imgToCart = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_toCart_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_toCart_white_on.png");
            } else {
                imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_off.png");
                imgDetail = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_detail_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_detail_white_off.png");
                imgToCart = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_toCart_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_toCart_white_off.png");
            }

            if (nextProps.isActiveFilter) {
                imgFilter = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_filter_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_filter_white_on.png")
            } else {
                imgFilter = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_filter_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_filter_white_off.png")
            }

            this.setState({
                isActive: nextProps.isActive,
                isActiveFilter: nextProps.isActiveFilter,
                imageFavorite: imgFavorite,
                imageDetail: imgDetail,
                imageFilter: imgFilter,
                imageToCart: imgToCart,
            });
        }
    }

    render() {
        return (
            <View style={[styles.mainContainer, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#0d0d0d' : '#ffffff' }]}>
                <View style={[styles.viewLine, { backgroundColor: CommonStyleSheet.THEME_DARCK ? 'transparent' : '#b5b5b5' }]}/>
                <View style={styles.containView}>
                    <TouchableOpacity disabled={!this.state.isActive} style={[{ marginLeft: 30, alignItems: 'center' }]} onPress={this.buttonFavorite_clicked}>
                        <Image style={styles.imageFavorite}
                            source={this.state.imageFavorite} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('FAVORITES')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity disabled={!this.state.isActive} style={[{ alignItems: 'center' }]} onPress={this.buttonDetail_clicked}>
                        <Image style={styles.imageDetail}
                            source={this.state.imageDetail} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('DETAILS')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity disabled={!this.state.isActive} style={[{ alignItems: 'center' }]} onPress={this.buttonToCart_clicked}>
                        <Image style={styles.imageToCart}
                            source={this.state.imageToCart} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('TO_CART')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity disabled={!this.state.isActiveFilter} style={[{ alignItems: 'center', marginRight: 30 }]} onPress={this.buttonFilter_clicked}>
                        <Image style={styles.imageFilter}
                            source={this.state.imageFilter} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('FILTER')}</Text>
                    </TouchableOpacity>
                </View>

            </View>
        )
    }
}

const styles = StyleSheet.create({
    mainContainer: {
        width: '100%',
        height: 53,
    },
    containView: {
        width: '100%',
        height: 52,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'flex-end'
    },
    viewLine: {
        height: 1, 
        width: '100%'
    },
    imageFavorite: {
        resizeMode: 'cover',
        width: 30.5,
        height: 29
    },
    imageDetail: {
        resizeMode: 'cover',
        width: 30,
        height: 24
    },
    imageToCart: {
        resizeMode: 'cover',
        width: 30.5,
        height: 27
    },
    imageFilter: {
        resizeMode: 'cover',
        width: 25.5,
        height: 25.5
    },
    text: {
        marginTop: 3,
        marginBottom: 5,
        fontSize: 10
    }
});