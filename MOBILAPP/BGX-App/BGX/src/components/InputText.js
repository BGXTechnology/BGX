
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, TextInput, View } from 'react-native';
import Utils from '../common/Utils';
import Line from './Line';

export default class InputText extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.style = props.style;
        this.labelStyle = props.labelStyle;
        this.onPress = props.onPress;
        this.label = props.label;
        this.placeholder = props.placeholder;
        this.onChangeText = props.onChangeText;
        this.placeholderTextColor = props.placeholderTextColor;
        this.value = props.value;
        this.secureTextEntry = props.secureTextEntry;
        this.editable = props.editable;
        this.maxLength = props.maxLength;
        this.numberOfLines = props.numberOfLines;
        this.widthLine = props.widthLine;
        this.onFocus = props.onFocus;
        this.textInputStyle = props.textInputStyle;
        this.keyboardType = props.keyboardType;
        this.state = {
            value: props.value
        };
    }
    componentDidMount() {
    }

    _onChangeText(text) {
        if (this.onChangeText != null) {
            this.onChangeText(text);
            this.value = text;
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                value: nextProps.value
            });
        }
    }


    render() {
        return (
            <View style={[styles.container, this.style]}>
                <Text style={[styles.title, this.labelStyle]}>{this.label} </Text>
                <TextInput
                    maxLength={this.maxLength}
                    multiline={false}
                    style={[styles.text_input, this.textInputStyle]}
                    placeholder={this.placeholder}
                    onChangeText={(text) => this._onChangeText(text)}
                    underlineColorAndroid='transparent'
                    placeholderTextColor={this.placeholderTextColor}
                    value={this.state.value}
                    secureTextEntry={this.secureTextEntry}
                    editable={this.editable}
                    numberOfLines={this.numberOfLines}
                    ellipsizeMode='tail'
                    autoCapitalize='none'
                    keyboardType={this.keyboardType}
                    onFocus={() => { if (this.onFocus != null) this.onFocus() }}
                />
                <Line style={{ width: this.widthLine }} />
            </View>
        )
    }

}
const styles = StyleSheet.create({
    container: {
        flexDirection: 'column',
        justifyContent: 'center',
    },
    title: {
        fontSize: 16,
        marginBottom: -10
    },
    text_input: {
        height: 35,
        color: 'white',
        fontSize: 14,
        padding: 0,
        margin: 0,
    },
});