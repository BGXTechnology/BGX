"use strict";
import React, { Component } from 'react';
import { StyleSheet, View, Text, ImageBackground, Image, TouchableOpacity, TouchableHighlight } from 'react-native';
import Utils from '../common/Utils';
import QRCode from 'react-native-qrcode';


const BGX_BGT_ACCOUNT = {
    BGX: 1,
    BGT: 2
}
export default class CardItem extends Component {

    constructor(props) {
        super(props);

        this.styleView = props.styleView;
        this.onLongPress = props.onLongPress;
        this.state = {
            balance: props.balance,
            cardHolder: props.cardHolder,
            publicKey: props.publicKey == null ? "" : props.publicKey,
            cardType: props.cardType,
            disabled: props.disabled,
            isShowBalance: props.isShowBalance
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                balance: nextProps.balance,
                cardHolder: nextProps.cardHolder,
                publicKey: nextProps.publicKey == null ? "" : nextProps.publicKey,
                cardType: nextProps.cardType,
                isShowBalance: nextProps.isShowBalance
            });
        }
    }

    render() {
        return (
            <TouchableHighlight
                disabled={this.state.disabled}
                onLongPress={() => { if (this.onLongPress != null) this.onLongPress() }}>
                <View style={this.styleView} >
                    <Image
                        style={styles.imageCard}
                        source={this.state.cardType == BGX_BGT_ACCOUNT.BGX ? require("../resource/image_card_bgx.png") : require("../resource/image_card_bgt.png")} />
                    <View style={styles.content}>
                        {/** card type -- balance */}
                        {
                            this.state.isShowBalance ? <Text style={styles.cardType}
                                numberOfLines={1}>
                                {this.state.cardType == 1 ? 'DEC' : 'BGT'}

                                <Text style={styles.cardBalance}
                                    numberOfLines={1}>
                                    {' ' + Utils.decimalValue(parseFloat(this.state.balance), 2)}
                                </Text>
                            </Text> : null
                        }

                        {/** card holder -- user name */}
                        <Text style={[styles.card_holder, { marginTop: this.state.isShowBalance ? 0 : 15 }]}
                            numberOfLines={1}>
                            {this.state.cardHolder}
                        </Text>
                        {/** public key */}
                        <Text style={styles.public_key}
                            numberOfLines={1}>
                            {this.state.publicKey}
                        </Text>
                    </View>
                    {/* {
                        this.state.cardType == BGX_BGT_ACCOUNT.BGX ?
                            <View style={styles.qr_code}>
                                <QRCode
                                    style={styles.qr_code}
                                    value={this.state.publicKey}
                                    size={100}
                                    bgColor='black'
                                    fgColor='white' />
                            </View> : null
                    } */}
                </View>
            </TouchableHighlight >
        )
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    containView: {
        flex: 1,
        backgroundColor: 'blue',
    },
    imageCard: {
        width: appSize.width - 20,
        height: 150,
        resizeMode: 'stretch',
    },
    cardType: {
        color: '#898989',
        fontSize: 20,
    },
    cardBalance: {
        color: 'white',
        fontWeight: '500',
        fontSize: 23,
    },
    card_holder: {
        color: 'white',
        fontWeight: '500',
        marginBottom: 4,
        fontSize: 20
    },
    content: {
        width: appSize.width - 50,
        height: 150,
        position: "absolute",
        flexDirection: 'column',
        alignItems: "flex-start",
        paddingTop: 65//150 / 2
    },
    qr_code: {
        position: "absolute",
        right: 20,
        top: (150 - 100) / 2 - 12
    },
    public_key: {
        color: '#898989',
        marginBottom: 4,
        fontSize: 16,
    },
});