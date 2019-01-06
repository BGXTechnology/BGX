
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View } from 'react-native';
import Utils from '../common/Utils';

export default class Line extends Component {

    constructor(props) {
        super(props);
        this.style = props.style;
    }

    render() {
        return (
            <View style={[styles.corner, this.style]} />
        )
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    corner: {
        alignItems: 'center',
        justifyContent: 'center',
        height: 1,
        backgroundColor: '#b5b5b5',
        opacity: 0.5,
    },
});