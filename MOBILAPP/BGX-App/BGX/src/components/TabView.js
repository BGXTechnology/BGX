"use strict";
import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';
import ScrollableTabView, { ScrollableTabBar, } from 'react-native-viewpager-indicator';
import ScrollableViewPager, { ScrollableIndicator, } from 'react-native-viewpager-indicator';
import TabViewItem from './TabViewItem';
import * as CommonStyleSheet from '../common/StyleSheet';

export default class TabView extends Component {

    constructor(props) {
        super(props);
        
        this.state = {
          listItem: props.listItem
        };
    }

    componentWillReceiveProps(nextProps) {
      if (this.props !== nextProps) {
          this.setState({
            listItem: nextProps.listItem
          });
// alert(JSON.stringify(this.state.listItem[3].content.length))
          this.forceUpdate();
      }
    }

    render() {
        return (
          <View style={styles.page}>
              <ScrollableTabView
                    tabBarUnderlineColor="red"
                    tabBarActiveTextColor={CommonStyleSheet.THEME_DARCK ? "#ebebeb" : "#111111"}
                    tabBarInactiveTextColor={CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}
                    tabBarTextStyle = {{paddingBottom:0,fontSize:13,marginTop:0}}
                    tabBarUnderlineStyle = {{backgroundColor: '#ebebeb', height: 1}}
                    renderTabBar={() => <ScrollableTabBar
                                     tabsContainerStyle={[styles.tabbarContainer, {borderColor: CommonStyleSheet.THEME_DARCK ? '#131313' : '#eeeeee'}]}
                                     tabStyle={styles.tab}
                                     tabPadding={tabPadding}
                                     underlineAlignText={true}
                                     />}
                >
                  {this.state.listItem.map(function (item, i) {
                    return (
                        <TabViewItem tabLabel={item.title} content={item.content} key={i} type={item.type}/>
                    );
                  })}
                </ScrollableTabView>
            </View>
        );
      }
}

const tabPadding = 18;
const styles = StyleSheet.create({
    page: {
        flex: 1,
        marginLeft: 10,
        marginRight: 10,
        flexDirection:'column',
      },
      loadingWrapper: {
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f0f0f0'
      },
      tabbarContainer: {
        height: 40,
        borderWidth: 1,
        borderTopWidth: 0,
        borderLeftWidth: 0,
        borderRightWidth: 0
      },
      tab: {
        height: 40,
        alignItems: 'center',
        justifyContent: 'center',
        paddingLeft: tabPadding,
        paddingRight: tabPadding,
      }
});