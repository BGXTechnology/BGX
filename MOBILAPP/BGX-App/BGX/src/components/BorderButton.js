
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, ImageBackground } from 'react-native';
import Utils from '../common/Utils';

export default class BorderButton extends Component {

    constructor(props) {
        super(props);

        this.borderColor = props.borderColor;

        this.onPress = props.onPress;

        this.state = {
            title: props.title,
            style: props.style,
            titleStyle: props.titleStyle,
            background: props.background,
            imageStyle: props.imageStyle,
        }
    }
    componentDidMount() {
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                title: nextProps.title,
                titleStyle: nextProps.titleStyle,
                style: nextProps.style,
                background: nextProps.background,
                imageStyle: nextProps.imageStyle,
            });
        }
    }

    render() {
        return (
            <View style={[styles.container, this.state.style]}>
                <TouchableOpacity style={[styles.corner]} onPress={this.onPress}>
                    <ImageBackground source={this.state.background}
                        imageStyle={styles.image_background}
                        style={[styles.absoluteView, this.state.imageStyle]}>
                        <Text style={[styles.title, this.state.titleStyle,]}> {this.state.title} </Text>
                    </ImageBackground>
                </TouchableOpacity>
            </View>
        )
    }

}
const styles = StyleSheet.create({
    container: {
        // flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    absoluteView: {
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'transparent'
    },
    title: {
        fontSize: 16,
        // backgroundColor: 'red'
    },
    image_background: {
        resizeMode: "stretch"
    }
});