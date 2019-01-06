
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View } from 'react-native';
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';


export default class TabBarComponent extends Component {

    constructor(props) {
        super(props);
        this.title = props.title;
        this.borderColor = props.borderColor;
        this.style = props.style;
        this.titleStyle = props.titleStyle;
        this.onPress = props.onPress;
        this.icon = props.icon;
        this.state = {
            isHide: props.hide,
            disabled: props.disabled
        }
    }
    componentDidMount() {
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                isHide: nextProps.hide,
                disabled: nextProps.disabled
            });
        }
    }

    render() {
        return (
            this.state.isHide ? null :
                <TouchableOpacity disabled={this.state.disabled} style={[styles.corner, this.style]} onPress={this.onPress}>
                    <Image source={this.icon} style={[styles.image, { tintColor: this.state.disabled ? CommonStyleSheet.THEME_DARCK ? '#4e4e4e': '#cccccc' : CommonStyleSheet.THEME_DARCK ? 'white' : 'black' }]} />
                    <Text style={[styles.title, this.titleStyle]}> {this.title} </Text>
                </TouchableOpacity>
        )
    }

}
const styles = StyleSheet.create({
    corner: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        paddingVertical: 5
    },
    title: {
        fontSize: 8,
        color: '#4e4e4e'
    },
    image: {
        width: 35,
        height: 35,
        resizeMode: 'contain'
    }
});