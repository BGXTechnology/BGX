
import React, { Component } from 'react';
import { StyleSheet, ActivityIndicator, View } from 'react-native';
import Utils from '../common/Utils';

export default class LoadingIndicator extends Component {

    constructor(props) {
        super(props);
        this.parent = props.parent;
        this.size = props.size;
    }
    componentDidMount() {
    }

    render() {
        return (
            <View style={[styles.container]} >
                {/* <View style={styles.indicator_container}> */}
                    <ActivityIndicator size={"large"} style={styles.indicator} />
                {/* </View> */}
            </View>
        )
    }

}
let size = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        position: "absolute",
        width: size.width,
        height: size.height,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'transparent',
        flex: 1,
    },
    indicator_container: {
        width: 80,
        height: 80,
        backgroundColor: "black",
        alignItems: 'center',
        justifyContent: 'center',
        opacity: 0.8,
        borderRadius: 10,
    },
    indicator: {
        width: 20,
        height: 20,
    },
});