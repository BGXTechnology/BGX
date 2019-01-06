"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';
import CheckBox from "react-native-check-box";

import { 
    View, 
    Text, 
    StyleSheet
} from "react-native";

export default class ItemProduct extends Component {

    constructor(props) {
        super(props);

        this.itemIndex = props.itemIndex;
        this.itemData = props.itemData;
        this.onPressSelectItem = props.onPressSelectItem;

        this.state = {
            isSelected: this.itemData.isSelected
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.itemData = nextProps.itemData;

            this.setState({
                isSelected: this.itemData.isSelected
            });
        }
    }

    onClickItem = () => {
        this.setState({ isSelected: !this.state.isSelected })
        var isSelect = this.state.isSelected ? false : true;
        this.onPressSelectItem(this.itemIndex, isSelect);
    }

    render() {
        return (
            <CheckBox 
                    style={styles.checkBoxStyle}
                    onClick={this.onClickItem}
                    rightTextView={
                        <Text style={[styles.textCheckBox, {color: CommonStyleSheet.THEME_DARCK ? '#949494' : '#707070'}]}>{this.itemData.categoryName}</Text>
                    }
                    checkBoxColor={CommonStyleSheet.THEME_DARCK ? 'white' : 'black'}
                    isChecked={this.state.isSelected}/>
        );
    };
}

const styles = StyleSheet.create({
    checkBoxStyle: {
        marginTop: 8,
        marginLeft: 13,
        width: Utils.appSize().width - 50,
    },
    textCheckBox: { 
        marginLeft: 10, 
        fontSize: 16
    }
});