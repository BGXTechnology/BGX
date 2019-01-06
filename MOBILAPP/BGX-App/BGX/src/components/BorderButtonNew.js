
import React, { Component } from 'react';
import { TouchableOpacity, Image, StyleSheet, Text, ActivityIndicator, View, ImageBackground } from 'react-native';
import Utils from '../common/Utils';
import * as CommonStyleSheet from '../common/StyleSheet';


export default class BorderButtonNew extends Component {

    static BORDER_BUTTON_TYPE = {
        THEME_DARK_HEIGHT_50_WIDTH_0_WHITE: 'THEME_DARK_HEIGHT_50_WIDTH_0_WHITE',//height 50 width free payment
        THEME_DARK_HEIGHT_50_WIDTH_0_DARK: 'THEME_DARK_HEIGHT_50_WIDTH_0_DARK',
        THEME_WHITE_HEIGHT_50_WIDTH_0_WHITE: 'THEME_WHITE_HEIGHT_50_WIDTH_0_WHITE',//height 50 width free payment
        THEME_WHITE_HEIGHT_50_WIDTH_0_DARK: 'THEME_WHITE_HEIGHT_50_WIDTH_0_DARK',
        HEIGHT_36_WIDTH_0_WHITE: 'HEIGHT_36_WIDTH_0_WHITE',//proceed checkout
        HEIGHT_36_WIDTH_0_DARK: 'HEIGHT_36_WIDTH_0_DARK',
        HEIGHT_36_WIDTH_0_WHITE_SHORT: 'HEIGHT_36_WIDTH_0_WHITE_SHORT',
        HEIGHT_36_WIDTH_0_DARK_SHORT: 'HEIGHT_36_WIDTH_0_DARK_SHORT'
    }

    constructor(props) {
        super(props);

        this.borderColor = props.borderColor;

        this.onPress = props.onPress;

        this.state = {
            title: props.title,
            style: props.style,
            titleStyle: props.titleStyle,
            buttonType: props.buttonType
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
                buttonType: nextProps.buttonType
            });
        }
    }
    
    measureView = (event) => {
        this.width = event.nativeEvent.layout.width;
        
        this.forceUpdate();
    }

    measureText = (event) => {
        this.heigtText = event.nativeEvent.layout.height;
        
        this.forceUpdate();
    }

    _renderItem = () => {
        switch(this.state.buttonType) {
            case BorderButtonNew.BORDER_BUTTON_TYPE.THEME_DARK_HEIGHT_50_WIDTH_0_WHITE:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/dark/100/1/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 68, height: 70}} source={require("../resource/buttonNew/repeat_button/dark/100/1/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/dark/100/1/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (70 - this.heigtText)/2, width: this.width - 68, left: 34}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
            case BorderButtonNew.BORDER_BUTTON_TYPE.THEME_DARK_HEIGHT_50_WIDTH_0_DARK:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/dark/100/2/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 68, height: 70}} source={require("../resource/buttonNew/repeat_button/dark/100/2/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/dark/100/2/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (70 - this.heigtText)/2, width: this.width - 68, left: 34}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
            case BorderButtonNew.BORDER_BUTTON_TYPE.THEME_WHITE_HEIGHT_50_WIDTH_0_WHITE:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/white/100/1/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 68, height: 70}} source={require("../resource/buttonNew/repeat_button/white/100/1/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/white/100/1/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (70 - this.heigtText)/2, width: this.width - 68, left: 34}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
            case BorderButtonNew.BORDER_BUTTON_TYPE.THEME_WHITE_HEIGHT_50_WIDTH_0_DARK:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/white/100/2/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 68, height: 70}} source={require("../resource/buttonNew/repeat_button/white/100/2/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 34, height: 70, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/white/100/2/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (70 - this.heigtText)/2, width: this.width - 68, left: 34}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );

            case BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_WHITE:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/white/72/1/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 50 + 2, height: 50, marginLeft: -1}} source={require("../resource/buttonNew/repeat_button/white/72/1/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/white/72/1/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (50 - this.heigtText)/2, width: this.width - 50, left: 25}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
            case BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_DARK:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/1/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 50 + 2, height: 50, marginLeft: -1}} source={require("../resource/buttonNew/repeat_button/dark/72/1/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/1/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (50 - this.heigtText)/2, width: this.width - 50, left: 25}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
            case BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_WHITE_SHORT:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/white/72/2/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 50 + 2, height: 50, marginLeft: -1}} source={require("../resource/buttonNew/repeat_button/white/72/2/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/white/72/2/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (50 - this.heigtText)/2, width: this.width - 50, left: 25}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );

            case BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_DARK_SHORT:
                return (
                    <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/2/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 50 + 2, height: 50, marginLeft: -1}} source={require("../resource/buttonNew/repeat_button/dark/72/2/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/2/right.png")}/>
                        <Text //ellipsizeMode='tail' numberOfLines={1}
                            style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (50 - this.heigtText)/2, width: this.width - 50, left: 25}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View>
                );
        }
    }

    render() {
        return (
            <View style={[styles.container, this.state.style]} onLayout={(event) => this.measureView(event)}>
                 <TouchableOpacity onPress={this.onPress}>
                    {/* <View style={{width: this.width, flexDirection: 'row'}}>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginLeft: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/left.png")}/>
                        <Image style={{resizeMode: "stretch", width: this.width - 50, height: 50}} source={require("../resource/buttonNew/repeat_button/dark/72/repeat.png")}/>
                        <Image style={{resizeMode: "contain", width: 25, height: 50, marginRight: 0}} source={require("../resource/buttonNew/repeat_button/dark/72/right.png")}/>
                        <Text style={[styles.title, this.state.titleStyle, {position: 'absolute', top: (50 - this.heigtText)/2, width: this.width - 50, left: 25}]} onLayout={(event) => this.measureText(event)}> {this.state.title} </Text>
                    </View> */}
                    {this._renderItem()}
                </TouchableOpacity>
               
            </View>
        )
    }

}
const styles = StyleSheet.create({
    container: {
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
        textAlign: 'center',
        textAlignVertical: "center",
        // backgroundColor: 'red'
    },
    image_background: {
        resizeMode: "contain"
    }
});