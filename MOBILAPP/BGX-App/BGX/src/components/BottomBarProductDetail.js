"use strict";
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, View } from 'react-native';
import * as CommonStyleSheet from '../common/StyleSheet';
import i18n from '../translations/i18n';

export default class BottomBarProductDetail extends Component {

    constructor(props) {
        super(props);

        this.buttonFavorite_clicked = props.buttonFavorite_clicked;
        this.buttonShare_clicked = props.buttonShare_clicked;
        this.buttonToCart_clicked = props.buttonToCart_clicked;
        this.buttonLike_clicked = props.buttonLike_clicked;

        let imgFavorite = null;
        if (props.isFavorite) {
            imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_active.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_active.png");
        } else {
            imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_on.png");
        }

        this.state = {
            imageFavorite: imgFavorite,
            isActiveComment: props.isActiveComment
        };

        this.imageComment = null;
        if (this.isActiveComment) {
            this.imageComment = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_favorite_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_favorite_white_on.png");
        } else {
            this.imageComment = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_favorite_dark_off.png") : require("../resource/tabIcon/tabIcon_product/icon_favorite_white_off.png");
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {

            let imgFavorite = null;
            if (nextProps.isFavorite) {
                imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_active.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_active.png");
            } else {
                imgFavorite = CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_like_dark_on.png") : require("../resource/tabIcon/tabIcon_product/icon_like_white_on.png");
            }

            this.setState({
                imageFavorite: imgFavorite,
                isActiveComment: nextProps.isActiveComment
            });
        }
    }

    render() {
        return (
            <View style={[styles.mainContainer, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#0d0d0d' : '#ffffff' }]}>
                <View style={[styles.viewLine, { backgroundColor: CommonStyleSheet.THEME_DARCK ? 'transparent' : '#b5b5b5' }]} />
                <View style={styles.containView}>
                    <TouchableOpacity style={[{ marginLeft: 30, alignItems: 'center' }]} onPress={this.buttonFavorite_clicked}>
                        <Image style={styles.imageFavorite}
                            source={this.state.imageFavorite} />

                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('FAVORITES')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity style={[{ alignItems: 'center' }]} onPress={this.buttonShare_clicked}>
                        <Image style={styles.imageShare}
                            source={CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_share_dark_on.png")
                                : require("../resource/tabIcon/tabIcon_product/icon_share_white_on.png")} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('SHARE')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity style={[{ alignItems: 'center' }]} onPress={this.buttonToCart_clicked}>
                        <Image style={styles.imageToCart}
                            source={CommonStyleSheet.THEME_DARCK ? require("../resource/tabIcon/tabIcon_product/icon_toCart_dark_on.png")
                                : require("../resource/tabIcon/tabIcon_product/icon_toCart_white_on.png")} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('TO_CART')}</Text>
                    </TouchableOpacity>

                    <TouchableOpacity disabled={!this.state.isActiveComment} style={[{ alignItems: 'center', marginRight: 30 }]} onPress={this.buttonLike_clicked}>
                        <Image style={styles.imageLike}
                            source={this.imageComment} />
                        <Text style={[styles.text, { color: CommonStyleSheet.THEME_DARCK ? '#818181' : '#898989' }]}>{i18n.t('LIKE')}</Text>
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
    imageShare: {
        resizeMode: 'cover',
        width: 29,
        height: 29
    },
    imageToCart: {
        resizeMode: 'cover',
        width: 30.5,
        height: 27
    },
    imageLike: {
        resizeMode: 'cover',
        width: 30,
        height: 27
    },
    text: {
        marginTop: 3,
        marginBottom: 5,
        fontSize: 10
    }
});