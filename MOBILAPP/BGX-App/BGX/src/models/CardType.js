import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity } from "react-native";

export default class CardType extends Component {

    constructor(props) {
        super(props);
        if (props == null) {
            return;
        }
        this.id = props["id"];
        this.payment = props["payment"];
        this.cardTypeId = props["cardTypeId"];
        this.title = props["title"];
        this.locale = props["locale"];
    }

}