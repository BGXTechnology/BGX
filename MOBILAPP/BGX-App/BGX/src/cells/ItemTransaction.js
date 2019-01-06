"use strict";
import React, { Component } from "react";

import { 
    View, 
    Text, 
    StyleSheet
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import Utils from "../common/Utils";
import i18n from '../translations/i18n';

export default class ItemTransaction extends Component {

    static TRANSACTION_TYPE = {
        CODE: 'CODE',
        LINK: 'LINK',
        EMAIL: 'EMAIL'
    }

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#1c1c1c' : '#f5f5f5'}]}>
                <View style={[styles.viewTitle, {backgroundColor: CommonStyleSheet.THEME_DARCK ? "#121212" : "#e8e8e8"}]}>
                    <Text style={styles.textAmount}>{i18n.t('AMOUNT')}</Text>
                    <Text style={[styles.textBalance, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>{'140.0' + " "}<Text style={{fontSize: 16, color: '#de113e'}}>{i18n.t('BETA_SYMBOL')}</Text></Text>
                </View>
                <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#0d0d0d' : '#c0c0c0'}]}/>

                <View style={styles.viewContainItem}>
                    {/* View 1     */}
                    <View style={[styles.viewItem, {flexDirection: 'row', justifyContent: 'space-between'}]}>
                        <View style={{width: (Utils.appSize().width - 86) / 2}}>
                            <Text style={styles.text1}>{i18n.t('FROM')}</Text>
                            <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>David Lomeli</Text>
                        </View>

                        <View style={{width: (Utils.appSize().width - 86) / 2}}>
                            <Text style={styles.text1}>{i18n.t('TO')}</Text>
                            <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>David Lomeli</Text>
                        </View>
                    </View>
                    <View style={[styles.viewLine1, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#313131' : '#c9c9c9'}]}/>

                    {/* View 2    */}
                    <View style={styles.viewItem}>
                        <Text style={styles.text1}>{i18n.t('DATE')}</Text>
                        <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>11.09.2018</Text>
                    </View>
                    <View style={[styles.viewLine1, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#313131' : '#c9c9c9'}]}/>

                    {/* View 3     */}
                    <View style={styles.viewItem}>
                        <Text style={styles.text1}>{i18n.t('CODE')}</Text>
                        <Text style={[styles.text2, {color: CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}]}>23af32rqasf3r23radfqr23r23fasdasd</Text>
                    </View>
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
        height: 252,
        borderRadius: 5,
        overflow: 'hidden'
    },
    viewTitle: {
        height: 67, 
        justifyContent: 'center', 
        alignItems: 'center'
    },
    textAmount: {
        color: "#707070", 
        fontSize: 18
    },
    textBalance: { 
        fontWeight: '700', 
        fontSize: 25, 
        textAlign: 'center'
    },
    viewLine: {
        height: 1
    },
    viewContainItem: {
        height: 165,
        backgroundColor: 'transparent',
        marginLeft: 18,
        marginRight: 18,
        marginTop: 10
    },
    viewItem: {
        height: 55
    },
    viewLine1: {
        height: 1
    },
    text1: {
        color: "#707070", 
        fontSize: 14,
        marginTop: 8
    },
    text2: {
        fontSize: 16,
        marginTop: 3,
        fontWeight: '400'
    }
});