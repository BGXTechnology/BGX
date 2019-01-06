import React, { Component } from "react";
import { View, Text, StyleSheet, Image, TouchableOpacity } from "react-native";
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';
import * as Constants from '../common/Constants';
import i18n from '../translations/i18n';

export default class ItemBGXWalletAddFund extends Component {

    constructor(props) {
        super(props);
        if (this.props.typeCard == Constants.BGX_BGT_ACCOUNT.BGX) {
            this.image = require('../resource/arrow.png');
            this.address = "";
            if (this.props.currentAddress.toLowerCase() == this.props.item.from) {
                this.image = require('../resource/arrow.png');
                this.address = this.props.item.to;
            } else {
                this.image = require('../resource/arrow_green.png');
                this.address = this.props.item.from;
            }
            this.date = Utils.stringFromTimestamp(this.props.item.timeStamp, 'DD.MM.YYYY');
        } else {
            this.image = require('../resource/arrow.png');
            this.address = "";
            if (this.props.currentAddress.toLowerCase() == this.props.item.address_from) {
                this.image = require('../resource/arrow.png');
                this.address = this.props.item.address_to;
            } else {
                this.image = require('../resource/arrow_green.png');
                this.address = this.props.item.address_from;
            }
            this.date = Utils.stringFromDate(this.props.item.timestamp, 'DD.MM.YYYY');
        }
        this.onPress = props.onPress;
    }

    render() {
        return (
            <TouchableOpacity style={[styles.container, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white', }]}
                onPress={()=>this.onPress()}>
                <Text style={[styles.textDate, { color: CommonStyleSheet.THEME_DARCK ? '#494949' : '#929292', }]}>{this.date}</Text>
                <Text style={[styles.textAddress, { color: CommonStyleSheet.THEME_DARCK ? '#9b9b9b' : '#111111', }]} numberOfLines={1}>{this.address}</Text>
                <Image style={styles.image} source={this.image} />

                <Text style={styles.textAmount} numberOfLines={1}>
                    {
                        (this.props.typeCard == Constants.BGX_BGT_ACCOUNT.BGX) ?
                            (this.props.item.value / 1000000000000000000) + '  Ether' : this.props.item.currency == null ? "" : this.props.item.tx_payload + "  " + i18n.t('BETA_SYMBOL')
                    }
                </Text>
            </TouchableOpacity>
        );
    };

   
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        height: 50
    },
    textDate: {
        marginLeft: 10,
        color: '#494949',
        width: "25%",
    },
    textAddress: {
        width: "30%"
    },
    textAmount: {
        marginLeft: 5,
        marginRight: 5,
        color: '#9b9b9b',
        width: "40%",
        flex: 1,
        textAlign: 'right'
    },
    image: {
        width: 22,
        height: 22,
        resizeMode: 'contain'
    }
});