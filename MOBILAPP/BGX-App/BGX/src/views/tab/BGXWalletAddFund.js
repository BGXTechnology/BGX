import React, { Component } from "react";
import { StyleSheet, View } from "react-native";
var moment = require('moment');

export default class BGXWalletAddFund extends Component {

    constructor(props) {
        super(props);
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    componentWillMount() {
        let day = moment(1318781876406).format("DD.MM.YYYY");
        console.log('formatted: ' + day);
    }

    render() {
        return (
            <View style={{ flex: 1, backgroundColor: 'red' }}>
            </View>
        );
    }
}