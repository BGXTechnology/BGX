"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    Image,
    Text,
    TouchableOpacity
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import BulletView from '../components/BulletView';
import NumberPad from '../components/NumberPad';
import Utils from '../common/Utils';
var md5 = require('md5');
import i18n from '../translations/i18n';

export default class PIN extends Component {

    constructor(props) {
        super(props);

        this.state = {
            countBullet: 0
        };

        this.BGTCard = this.props.navigation.getParam("card", null);
        this.pinValue = "";
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    buttonBack_clicked = () => {
        this.props.navigation.goBack();
    }

    onPressNumber = (number) => {
        this.pinValue += number;

        if (this.state.countBullet < 4) {
            this.setState({
                countBullet: this.state.countBullet + 1
            });

            if (this.state.countBullet == 3) {

                if (md5(this.pinValue) == this.BGTCard.PIN) {
                    setTimeout( () => {
                        this.props.navigation.state.params.onInputPinSuccess();
                        this.props.navigation.goBack();
                     }, 300);
                } else {
                    Utils.showAlert(i18n.t('PIN_CURRENT_NOT_MATCH'), true, {
                        done: () => {
                            setTimeout( () => {
                                this.resetBulletView();
                             }, 500);
                        },
                    });
                }
            }
        }
    }

    resetBulletView() {
        this.setState({
            countBullet: 0
        });

        this.pinValue = "";
     }

    render() {
        return (
            <View style={[styles.containView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <TouchableOpacity onPress={this.buttonBack_clicked}>
                    <Image style={styles.iconBack} source={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_back_white.png") : require("../resource/icon_back.png")}/>
                </TouchableOpacity>

                <Text style={[styles.textTitle, {color: CommonStyleSheet.THEME_DARCK ? '#7d7d7d' : '#636363'}]}>{i18n.t('ENTER_YOUR_PIN')}</Text>

                <BulletView ref="numpadView" styleView={styles.bulletView} countSeleted={this.state.countBullet}/>
                <NumberPad styleView={styles.numpadView} onPressNumber={this.onPressNumber}/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    containView: {
        flex: 1
    },
    iconBack: {
        width: 23, 
        height: 14, 
        marginLeft: 20,
        marginTop: 35
    },
    textTitle: {
        fontSize: 24,
        alignSelf: 'center',
        marginTop: Utils.appSize().height < 600 ? 30 : 60,
    },
    bulletView: {
        marginTop: 40,
        alignSelf: 'center'
    },
    numpadView: {
        width: Utils.appSize().width - 60,
        marginTop: Utils.appSize().height < 600 ? 30 : 60,
        alignSelf: 'center'
    }
});