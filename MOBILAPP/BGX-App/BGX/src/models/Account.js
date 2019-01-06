import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity } from "react-native";

export default class Account extends Component {

    constructor(props) {
        super(props);
        if (props == null) {
            return;
        }
        this.id = props["id"];
        this.email = props["email"];
        this.username = props["username"];
        this.password = props["password"];
        this.phone = props["phone"];
        this.avatar = props["avatar"];
        this.ethereumAddress = props["ethereumAddress"];
        this.BGXAccount = props["BGXAccount"];
        this.hash = props["hash"];
        this.created = props["created"];
        this.active = props["phone"];
        this.createdLink = props["createdLink"];
        this.resetedLink = props["resetedLink"];
        this.resetHash = props["resetHash"];
        this.isReseted = props["isReseted"];
        this.magentoId = props["magentoId"];
    }

}