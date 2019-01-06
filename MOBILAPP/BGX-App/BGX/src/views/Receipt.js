"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    TouchableOpacity,
    ImageBackground,
    Image, 
    Text,
    StatusBar
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import BorderButton from '../components/BorderButton';
import i18n from '../translations/i18n';
import { NavigationActions, StackActions } from "react-navigation";

export default class Receipt extends Component {

    static RECEIPT_TYPE = {
        BONUS_TOKEN: 'BONUS_TOKEN',
        GET_LINK: 'GET_LINK',
        GET_CONTACT: 'GET_CONTACT',
        YOUR_BONUS_TOKEN: 'YOUR_BONUS_TOKEN'
    }

    constructor(props) {
        super(props);

        this.type = Receipt.RECEIPT_TYPE.YOUR_BONUS_TOKEN;
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    //Header button
    buttonBack_clicked = () => {
        // this.props.navigation.goBack();
        this.props.navigation.dispatch(
            StackActions.reset({
            index: 0,
            actions: [NavigationActions.navigate({ routeName: "DigitalSpotView" })]
          })
        );
    }
    
    _renderBottom = () => {
        switch(this.type) {
            case Receipt.RECEIPT_TYPE.BONUS_TOKEN:
                return (
                    <View style={{justifyContent: 'center', height: 79, marginLeft: 20, marginRight: 20}}>
                        <Text style={{color: "#707070" , fontSize: 13}}>{i18n.t('BONUS_TOKEN')}</Text>
                        <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontSize: 15, marginTop: 3}} numberOfLines={1}>12321312312312231123123123123123123</Text>
                    </View>
                );
            case Receipt.RECEIPT_TYPE.GET_LINK:
                return (
                    <BorderButton
                            title={i18n.t('GET_LINK')}
                            onPress={this.buttonOK_clicked}
                            titleStyle={[styles.titleButton, {color: CommonStyleSheet.THEME_DARCK ? '#ffffff' : '#111111'}]}
                            style={[styles.button, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#404040' : '#cccccc', borderColor: CommonStyleSheet.THEME_DARCK ? "#555555" : "transparent"}]}
                        />
                );
            case Receipt.RECEIPT_TYPE.GET_CONTACT:
                return (
                    <BorderButton
                            title={i18n.t('GET_CONTACT_INFO')}
                            onPress={this.buttonOK_clicked}
                            titleStyle={[styles.titleButton, {color: CommonStyleSheet.THEME_DARCK ? '#ffffff' : '#111111'}]}
                            style={[styles.button, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#404040' : '#cccccc', borderColor: CommonStyleSheet.THEME_DARCK ? "#555555" : "transparent"}]}
                        />
                );
            case Receipt.RECEIPT_TYPE.YOUR_BONUS_TOKEN:
                return (
                    <View style={{justifyContent: 'center', alignItems: 'center', height: 79}}>
                        <Text style={{color: CommonStyleSheet.THEME_DARCK ? "white" : "#111111", alignSelf: 'center', fontSize: 18}}>{i18n.t('YOUR_BONUS_TOKEN_ARE_USE')}</Text>
                    </View>
                    
                );
        }
    }

    render() {
        StatusBar.setBarStyle('light-content', true);
        return (
            <View style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <ImageBackground style={styles.imageBackground} source={require("../resource/header_background.png")}>
                    <View style={styles.viewHeaderTitle}>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonBack_clicked}>
                            <Image style={styles.iconBack} source={require("../resource/icon_back_white.png")}/>
                        </TouchableOpacity>
                        <Text style={styles.textTitleHeader}>{i18n.t('RECEIPT')}</Text>
                    </View>
                </ImageBackground>
                
                <View style={[styles.flatList, {alignItems: 'center', justifyContent: 'center'}]}>
                    <Text style={[styles.emptyText, {color: CommonStyleSheet.THEME_DARCK ? '#7d7d7d' : '#959595'}]}>{i18n.t('NO_RECEIPT_AVAILABLE')}</Text>
                </View>

                {/* <View style={[styles.containPadView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1a1a1a' : '#e8e8e8'}]}>
                    <View style={{height: 48, backgroundColor: CommonStyleSheet.THEME_DARCK ? "#121212" : "#e8e8e8", justifyContent: 'center', alignItems: 'center' }}>
                        <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontWeight: '700', fontSize: 23 , textAlign: 'center'}}>{Messages.PAID}</Text>
                    </View>
                    <View style={{backgroundColor: CommonStyleSheet.THEME_DARCK ? "#0d0d0d" : "#c0c0c0", height: 2}}/>
                    <View style={{marginTop: 20, height: 105}}>
                        <View style={{flexDirection: 'row'}}>
                            <View style={{marginLeft: 20, flex: 1, height: 42}}>
                                <Text style={{color: "#707070" , fontSize: 12}}>{Messages.PAYMENT_DATE}</Text>
                                <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontSize: 14}}>11.09.2018</Text>
                            </View>
                            <View style={{marginLeft: 20, marginRight: 20,  flex: 1, height: 42}}>
                                <Text style={{color: "#707070" , fontSize: 12}}>{Messages.TRANSACTION_TIME}</Text>
                                <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontSize: 14}}>11.09.2018</Text>
                            </View>
                        </View>
                        <View style={{backgroundColor: CommonStyleSheet.THEME_DARCK ? '#313131' : '#c9c9c9', height: 1, marginLeft: 20, marginRight: 20}}/>
                        <View style={{flexDirection: 'row'}}>
                            <View style={{ marginTop: 13, marginLeft: 20, flex: 1, height: 42}}>
                                <Text style={{color: "#707070" , fontSize: 12}}>{Messages.PURCHASE_AMOUNT}</Text>
                                <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontSize: 14}}>{30.0 + ' '}<Text style={{color: '#de113e'}}>{Messages.BETA_SYMBOL}</Text> </Text>
                            </View>
                            <View style={{backgroundColor: CommonStyleSheet.THEME_DARCK ? '#313131' : '#c9c9c9', width: 1, height: 42}}/>
                            <View style={{marginTop: 13, marginLeft: 20, marginRight: 20,  flex: 1, height: 42}}>
                                <Text style={{color: "#707070" , fontSize: 12}}>{Messages.TRANSACTION_HASH}</Text>
                                <Text style={{color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111", fontSize: 14}}>{30.0 + ' '}<Text style={{color: '#de113e'}}>{Messages.BETA_SYMBOL}</Text> </Text>
                            </View>
                        </View>
                    </View>
                    <Image style={{width: '100%', height: 6.5, resizeMode: 'repeat'}} source={CommonStyleSheet.THEME_DARCK ? require("../resource/divider_line_dark.png") : require("../resource/divider_line_white.png")}/>
                    {this._renderBottom()}
                </View> */}
            </View>
        );
    }
}
//
//line 0d0d0d c0c0c0
const styles = StyleSheet.create({

    container: {
        flex: 1
    },
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
    iconBack: {
        width: 23, 
        height: 14, 
        marginLeft: 15,
        marginTop: 3
    },
    textTitleHeader: {
        color: 'white', 
        fontSize: 24, 
        fontWeight: '300',
        flex: 1,
        textAlign: 'center',
        marginTop: -3,
        marginLeft: -40
    },
    containPadView: {
        marginLeft: 25, 
        marginRight: 25, 
        marginTop: 20,
        height: 260, 
        overflow: 'hidden',
        borderRadius: 5
    },
    titleButton: {
        fontWeight: '600'
    },
    button: {
        marginRight: 20,
        marginLeft: 20,
        marginTop: 22,
        flex: 0,
        height: 36,
        borderWidth: 1.5
    },
    flatList: {
        marginTop: 15, 
        flex: 1
    },
    emptyText: {
        fontSize: 18,
        fontWeight: '400',
        textAlign: 'center'
    }
});