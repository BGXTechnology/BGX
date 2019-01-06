"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    ImageBackground,
    Image,
    Text,
    TouchableOpacity,
    FlatList
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import Utils from "../common/Utils";
import * as Constants from '../common/Constants';
import LoadingIndicator from "../components/LoadingIndicator";
import ConfigAPI from "../api/ConfigAPI";
import {BaseService} from "../api/BaseService";
import ItemTransaction from "../cells/ItemTransaction";
import BorderButton from '../components/BorderButton';

import i18n from '../translations/i18n';

export default class TransactionDetail extends Component {

    constructor(props) {
        super(props);

        this.state = {
            dataSource: ["1"],
            progressView: false
        }
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    buttonBack_clicked = () => {
        this.props.navigation.goBack();
    }

    render() {
        return (
            <View style={[styles.containView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <ImageBackground style={styles.imageBackground} source={require("../resource/header_background.png")}>
                    <View style={styles.viewHeaderTitle}>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonBack_clicked}>
                            <Image style={styles.iconBack} source={require("../resource/icon_back_white.png")}/>
                        </TouchableOpacity>
                        <Text style={styles.textTitleHeader}>{i18n.t('TRANSACTION_DETAIL')}</Text>
                    </View>
                </ImageBackground>

                <View style={[styles.flatList, {alignItems: 'center', justifyContent: 'center'}]}>
                    <Text style={[styles.emptyText, {color: CommonStyleSheet.THEME_DARCK ? '#7d7d7d' : '#959595'}]}>{i18n.t('FEARUTE_IS_NOT_IMPLEMENTED')}</Text>
                </View>

                {/* <FlatList
                        style={styles.flatList}
                        data={this.state.dataSource}
                        extraData={this.state}
                        keyExtractor={(item, index) => index.toString()}
                        renderItem={({ item, index }) => (
                            <ItemTransaction />
                        )}
                        refreshing={this.state.refreshing}
                        onRefresh={this.handleRefresh}
                    />
                    
                    <BorderButton
                        title={i18n.t('OK')}
                        titleStyle={[styles.titleButtonOK, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                        style={[styles.buttonOK, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#252525'}]}/> */}
                {/* load progress */}
                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }
}

const styles = StyleSheet.create({
    containView: {
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
    flatList: {
        marginTop: 10, 
        flex: 1
    },
    buttonOK: {
        width: 288,
        height: 50,
        flex: 0,
        marginBottom: 0,
        alignSelf: 'center'
    },
    titleButtonOK: {
        fontWeight: '600',
        fontSize: 18
    },
    emptyText: {
        fontSize: 18,
        fontWeight: '400',
        textAlign: 'center'
    }
});