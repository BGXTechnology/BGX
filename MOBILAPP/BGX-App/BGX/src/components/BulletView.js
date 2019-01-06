"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';

const total = 4;

export default class NumberPad extends Component {

    constructor(props) {
        super(props);

        this.styleView = props.styleView;
        this.state = {
            countSeleted: props.countSeleted == null ? 0 : props.countSeleted
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                countSeleted: nextProps.countSeleted == null ? 0 : nextProps.countSeleted
            });
        }
    }

    _renderChoose = () => {
        let seleted = []
        for(var i = 0; i < this.state.countSeleted; i++) {
            let view = <View key={i} style={[styles.viewBulletSeleted, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#252525'}]}/>  
            seleted.push(view);
        }

        return seleted;
    }

    _renderUnChoose = () => {
        let unSeleted = []
        let count = total - this.state.countSeleted;
        for(var i = 0; i < count; i++) {
            let view = <View key={i} style={[styles.viewBulletUnSeleted, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#3d3d3d' : '#cccccc', borderWidth: CommonStyleSheet.THEME_DARCK ? 1.5 : 0}]}/>  
            unSeleted.push(view);
        }

        return unSeleted;
    }

    render() {
        return (
            <View style={[styles.containView, this.styleView]}>
                {this._renderChoose()}
                {this._renderUnChoose()}
            </View>
        )
    }
}

const styles = StyleSheet.create({
    containView: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: 132, 
        height: 18
    },
    viewBulletSeleted: {
        width: 18, 
        height: 18,
        borderRadius: 9
    },
    viewBulletUnSeleted: {
        width: 18, 
        height: 18,
        borderRadius: 9,
        borderColor: '#525252'
    }
});