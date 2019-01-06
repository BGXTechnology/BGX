"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    Text,
    TouchableWithoutFeedback
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';

export default class NumberItem extends Component {

    constructor(props) {
        super(props);

        this.number = props.number;
        this.onPressItem = props.onPressItem;
        this.state = {
            backgroundColor: 'transparent'
        };
    }

    onPressIn = () => {
        this.setState({
            backgroundColor: '#2e2e2e'
        });
    }

    onPressOut = () => {
        this.setState({
            backgroundColor: 'transparent'
        });
    }

    render() {
        return (
            <TouchableWithoutFeedback onPress={() => this.onPressItem(this.number)} onPressIn={this.onPressIn} onPressOut={this.onPressOut}>
                <View style={[styles.containView, {backgroundColor: this.state.backgroundColor}]}>
                    <Text style={[styles.textNumber, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.number}</Text>
                </View>
            </TouchableWithoutFeedback>
        )
    }
}

const styles = StyleSheet.create({
    containView: {
        aspectRatio: 1, 
        alignItems: 'center', 
        borderRadius: 40  
    },
    textNumber: {
        fontSize: 50,
        padding: 10
    }
});