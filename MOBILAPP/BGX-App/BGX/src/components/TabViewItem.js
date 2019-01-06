"use strict";
import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, FlatList} from 'react-native';
import * as CommonStyleSheet from '../common/StyleSheet';
import ItemComment from '../cells/ItemComment';

export default class TabViewItem extends Component {

    constructor(props) {
        super(props);
        
        this.state = {
            content: props.content,
            type: props.type
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props !== nextProps) {
            this.setState({
                content: nextProps.content
            });

            this.forceUpdate();
        }
      }

    render() {
        if (this.state.type == 2) {
            return (
                <FlatList
                        style={styles.flatList}
                        data={this.state.content}
                        extraData={this.state}
                        keyExtractor={(item, index) => index.toString()}
                        renderItem={({ item, index }) => (
                            <ItemComment itemIndex={index} 
                                        itemData={item}/>
                        )}
                        refreshing={this.state.refreshing}
                        scrollEnabled={false}
                        onRefresh={this.handleRefresh}
                    />
            )
        }
        return (
            <View style={styles.container} >
                <Text style={[styles.content, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.state.content}</Text>
          </View>
        );
      }
}

const styles = StyleSheet.create({
    container: {
        flex: 1
      },
      content: {
        marginTop: 10,
        marginBottom: 10,
        fontSize: 14
      },
      flatList: {
        flex: 1,
        marginTop: 10
      }
});