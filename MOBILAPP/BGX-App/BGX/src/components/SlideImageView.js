"use strict";
import React, { Component } from 'react';
import { Image, StyleSheet, View } from 'react-native';
import Utils from '../common/Utils';
import Swiper from 'react-native-swiper'
import ConfigAPI from '../api/ConfigAPI';

export default class SlideImageView extends Component {

    constructor(props) {
        super(props);

        this.containStyle = props.containStyle;
        this.state = {
            imageList: props.imageList
        };
        
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            if (nextProps.imageList.length > 0) {
                this.setState({
                    imageList: nextProps.imageList
                });
                this.forceUpdate();
            }
        }
    }

    render() {
        return (
            <View style={this.containStyle}>
                <Swiper style={styles.wrapper}
                    dot={<View style={styles.dot} />}
                    activeDot={<View style={styles.dotActive} />}
                    paginationStyle={{
                        bottom: 0
                    }}
                    loop={true}>

                        {this.state.imageList.map((imageURL, key) => {
                            return (
                            <Image
                                key={key}
                                style={styles.imageStyle}
                                source={{uri: ConfigAPI.DOMAIN_IMAGE_MAGENTO + imageURL}}
                                resizeMode='cover'/>
                            );
                        })}
                </Swiper>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    dot: {
        backgroundColor: '#3d3d3d',
        width: 13, 
        height: 13, 
        borderRadius: 7, 
        marginLeft: 7, 
        marginRight: 7,
        borderWidth: 1.5,
        borderColor: '#525252'
    },
    dotActive: {
        backgroundColor: '#ebebeb', 
        width: 13, 
        height: 13, 
        borderRadius: 7, 
        marginLeft: 7, 
        marginRight: 7
    },
    imageStyle: {
        width: '100%',
        height: '90%'
    }
});