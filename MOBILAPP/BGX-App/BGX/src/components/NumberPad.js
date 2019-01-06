"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    Text,
    TouchableWithoutFeedback
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import NumberItem from './NumberItem';

export default class NumberPad extends Component {

    constructor(props) {
        super(props);

        this.styleView = props.styleView;
        this.onPressNumber = props.onPressNumber;
        this.state = {
            backgroundColor: 'transparent'
        };
    }

    
    measureView = (event) => {
        this.height = event.nativeEvent.layout.height;
        this.forceUpdate();
    }

    onPressItem = (number) =>{
        this.onPressNumber(number);
    }

    render() {
        return (
            <View style={this.styleView}>
                <View style={styles.viewLineNumber}>
                    <NumberItem onPressItem={this.onPressItem} number={1}/>
                    <NumberItem onPressItem={this.onPressItem} number={2}/>
                    <NumberItem onPressItem={this.onPressItem} number={3}/>
                </View>
                <View style={[styles.viewLineNumber, {marginTop: 15}]}>
                    <NumberItem onPressItem={this.onPressItem} number={4}/>
                    <NumberItem onPressItem={this.onPressItem} number={5}/>
                    <NumberItem onPressItem={this.onPressItem} number={6}/>
                </View>
                <View style={[styles.viewLineNumber, {marginTop: 15}]}>
                    <NumberItem onPressItem={this.onPressItem} number={7}/>
                    <NumberItem onPressItem={this.onPressItem} number={8}/>
                    <NumberItem onPressItem={this.onPressItem} number={9}/>
                    </View>
                <View style={[styles.viewLineNumber, {marginTop: 15, alignSelf: 'center'}]}>
                    <NumberItem onPressItem={this.onPressItem} number={0}/>
                </View>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    containView: {
        aspectRatio: 1, 
        alignItems: 'center', 
        borderRadius: 40  
    },
    viewLineNumber: {
        flexDirection: 'row', 
        justifyContent: 'space-between', 
        height: 80
    }
});