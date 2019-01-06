"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    FlatList,
    TouchableOpacity,
    Image,
    Text,
    Alert
} from "react-native";

import { NavigationActions, StackActions } from "react-navigation";

import HeaderDigitalSpot from '../components/HeaderDigitalSpot'; 
import * as CommonStyleSheet from '../common/StyleSheet';
import ItemShoppingCart from '../cells/ItemShoppingCart';
import BorderButton from '../components/BorderButton';
import PopupConfirm from '../components/PopupConfirm';
import ConfigAPI from '../api/ConfigAPI';
import {BaseService} from "../api/BaseService";
import LoadingIndicator from "../components/LoadingIndicator";
import Utils from '../common/Utils';
import * as Constants from '../common/Constants';
import Item from '../models/Item';
import i18n from '../translations/i18n';

export default class Favorites extends Component {

    constructor(props) {
        super(props);

        this.state = {
            dataSource: [],
            alertMessage: i18n.t('MESSAGE_CLEAR_FAVORITE'),
            okButton: i18n.t('CLEAR'),
            totalAmmount: 0,
            progressView: false
        }

        this.account = null;
        this.selectedIndex = -1;

        this.isRequestData = false;
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    async componentDidMount() {
        this.account = await Utils.getDataWithKey(Constants.KEY_USER);
        this.requestGetWishList(this.account.magentoId);
    }

    onBack_event = () => {
        this.requestGetWishList(this.account.magentoId);
    }


    //Header button
    buttonBackHeader_clicked = () => {
        if (Utils.isExistOnBackEvent(this.props.navigation)) {
            this.props.navigation.state.params.onBackEvent();
        }
        this.props.navigation.goBack();
    }

    buttonMyOrdersHeader_clicked = () => {
        if (Utils.isExistOnBackEvent(this.props.navigation)) {
            this.props.navigation.state.params.onBackEvent();
        }
        
        this.props.navigation.navigate('MyOrdersView', {onBackEvent: this.onBack_event } );
    }

    buttonShoppingCartHeader_clicked = () => {
        if (Utils.isExistOnBackEvent(this.props.navigation)) {
            this.props.navigation.state.params.onBackEvent();
        }
        
        this.props.navigation.navigate('ShoppingCartView', {onBackEvent: this.onBack_event } );
    }

    // 2 button header
    buttonBackToShopping_clicked = () => {
        this.props.navigation.dispatch(
            StackActions.reset({
            index: 0,
            actions: [NavigationActions.navigate({ routeName: "DigitalSpotView" })]
          })
        );
    }

    buttonClearFavorite_clicked = () => {
        this.setState({
            alertMessage: i18n.t('MESSAGE_CLEAR_FAVORITE'),
            okButton: i18n.t('CLEAR')
        });

        this.popupDialog.showSlideAnimationDialog();
    }

    //Item button
    buttonItemDelete_clicked = (index) => {
        this.selectedIndex = index;

        this.setState({
            alertMessage: i18n.t('MESSAGE_DELETE_ITEM_FROM_FAVORITE'),
            okButton: i18n.t('OK')
        });
        
        this.popupDialog.showSlideAnimationDialog();
    }

    buttonItemSaveFavorite_clicked = (index) => {
        this.requestAddShoppingCart(this.state.dataSource[index].id);
    }

    // Popup button
    buttonOKPoup_clicked = () => {
        this.popupDialog.dismissSlideAnimationDialog();

        if (this.state.alertMessage === i18n.t('MESSAGE_DELETE_ITEM_FROM_FAVORITE')) {
            this.requestDeleteItemFavorite(this.state.dataSource[this.selectedIndex].wishListItemID);
        } else if (this.state.alertMessage === i18n.t('MESSAGE_CLEAR_FAVORITE')) {
            this.requestClearAllFavorite();
        }
    }

    _calculateTotalAmmount = (list) => {
        var ammount = 0;
        for (let item of list) {
            ammount += parseFloat(item.price);
        }

        return Utils.formatPrice(ammount);
    }

    // Render function
    _renderBackToShopping = () => {
        if (this.state.dataSource.length != 0) {
            return (
                <TouchableOpacity onPress={this.buttonClearFavorite_clicked}>
                    <View style={[styles.viewLeftButton, {marginRight: 15, borderColor: CommonStyleSheet.THEME_DARCK ? '#898989' : '#d0d0d0'}]}>
                        <Image style={styles.iconClearCart} 
                             source={CommonStyleSheet.THEME_DARCK ? 
                             require("../resource/icon_clear_white.png") : 
                             require("../resource/icon_clear_black.png")}/>
                        <Text style={{color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#7d7d7d'}}>{i18n.t('CLEAR_FAVORITE')}</Text>
                    </View>
                </TouchableOpacity>
            );
        }

        return null;
    }

    _renderContent = () => {
        if (this.state.dataSource.length == 0) {
            return (
                <View style={[styles.flatList, {alignItems: 'center', justifyContent: 'center'}]}>
                    <Text style={[styles.emptyText, {color: CommonStyleSheet.THEME_DARCK ? '#7d7d7d' : '#959595'}]}>{i18n.t('FAVORITE_EMPTY')}</Text>
                </View>
            );
        } else {
            return (
                <FlatList
                        style={styles.flatList}
                        data={this.state.dataSource}
                        extraData={this.state}
                        keyExtractor={(item, index) => index.toString()}
                        renderItem={({ item, index }) => (
                            <ItemShoppingCart itemIndex={index} 
                                                itemData={item}
                                                isFavorite={true} 
                                                buttonDelete_clicked={this.buttonItemDelete_clicked} 
                                                buttonSaveFavorite_clicked={this.buttonItemSaveFavorite_clicked}/>
                        )}
                        refreshing={this.state.refreshing}
                        onRefresh={this.handleRefresh}
                    />
            );
        }
    }
    
    _renderCheckout = () => {
        if (this.state.dataSource.length != 0) {
            return (
                <View style={styles.viewCheckout}>
                        <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#121212' : '#d0d0d0'}]}/>
                        <View style={{flexDirection: 'row', justifyContent: 'space-between', alignItems: 'baseline'}}>
                            <Text style={[styles.textTotal, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('TOTAL_AMOUNT')}</Text>
                            <Text style={styles.textBalance}>{this.state.totalAmmount}<Text style={{fontFamily: 'Arial'}}>{' ' + i18n.t('BETA_SYMBOL')}</Text></Text>
                        </View>
                    </View>
            );
        }
        
        return null;
    }

    //request data
    requestGetWishList = (customerID) => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_WISH_LIST, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_WISH_LIST.replace('{customer_id}', customerID)
        };
  
        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestGetRating = () => {
        var array = [];
        for (let item of this.state.dataSource) {
            array.push(item.id);
        }
        
        let string = array.toString();

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LIST_RATING, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_LIST_RATING + string, 
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestDeleteItemFavorite = (wishListItemID) => {
        if (this.isRequestData) {
            return;
        }

        this.isRequestData = true;

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_DELETE_ITEM_FAVORITE, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_DELTE_ITEM_WISH_LIST.replace('{wishlist_item_id}', wishListItemID).replace('{customer_id}', this.account.magentoId)
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.DELTE);
    }

    requestClearAllFavorite = () => {
        if (this.isRequestData) {
            return;
        }

        this.isRequestData = true;

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_CLEAR_ALL_FAVORITE, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_DELTE_ITEM_WISH_LIST.replace('{wishlist_item_id}', 0).replace('{customer_id}', this.account.magentoId)
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.DELTE);
    }

    requestAddShoppingCart = (productID) => {
        if (this.isRequestData) {
            return;
        }

        this.isRequestData = true;

        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_ADD_ITEM_SHOPPING_CART,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USER_ID]: this.account.id,
            [ConfigAPI.PARAM_PRODUCT_ID]: productID,
        };
        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
    }

    //Request Data Success - Fail
    async onSuccess(code, message, data, method) {
        this.setState({ 
            progressView: false 
        });

        this.isRequestData = false;

        if (method === ConfigAPI.METHOD_GET_WISH_LIST) {
            if (data == null) {
                return;
            }

            var array = [];
            for (let item of data) {
                let itemParse = new Item(item, Item.ITEM_PRODUCT_TYPE.FAVORITE);
                array.push(itemParse);
            }
            
            let ammount = this._calculateTotalAmmount(array);

            this.setState({ 
                dataSource: array,
                totalAmmount: ammount
            });

            this.requestGetRating();
        } else if (method === ConfigAPI.METHOD_LIST_RATING) {
            
            let count = data.length;
            for (var i = 0; i < count; i++) {
                this.state.dataSource[i].rating = data[i].value;
            }

            this.setState({ 
                dataSource: this.state.dataSource
            });
        } else if (method === ConfigAPI.METHOD_DELETE_ITEM_FAVORITE) {

            Utils.showAlert(i18n.t('MESSAGE_REMOVE_FROM_FAVORITE'), true, {
                done: () => {
                    var list = this.state.dataSource;
                    list.splice(this.selectedIndex, 1);
                    let ammount = this._calculateTotalAmmount(list);
                    
                    this.setState({ 
                        dataSource: []
                    });

                    this.setState({ 
                        dataSource: list,
                        totalAmmount: ammount
                    });
                }
            });
        } else if (method === ConfigAPI.METHOD_CLEAR_ALL_FAVORITE) {
            Utils.showAlert(i18n.t('MESSAGE_REMOVE_ALL_ITEM_FROM_FAVORITE'), true, {
                done: () => {
                    
                    this.setState({ 
                        dataSource: [],
                        totalAmmount: "0"
                    });
                }
            });
        } else if (method === ConfigAPI.METHOD_ADD_ITEM_SHOPPING_CART) {
    
            this.setState({ 
                progressView: false
            });
            Utils.showAlert(i18n.t('MESSAGE_ITEM_ADD_TO_SHOPPING_CART'), true, null);
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        this.isRequestData = false;
    }

    render() {
        return (
            <View style={[styles.containView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <HeaderDigitalSpot
                    headerTitle={i18n.t('FAVORITES')}
                    page={HeaderDigitalSpot.VIEW_CONTROLLER.FAVORITE}
                    buttonBackHeader_clicked={this.buttonBackHeader_clicked}
                    buttonMyOrdersHeader_clicked={this.buttonMyOrdersHeader_clicked}
                    buttonShoppingCartHeader_clicked={this.buttonShoppingCartHeader_clicked}
                    isShowBack={true}/>
                    
                    <View style={styles.viewButton}>
                        <TouchableOpacity onPress={this.buttonBackToShopping_clicked}>
                            <View style={[styles.viewLeftButton, {borderColor: CommonStyleSheet.THEME_DARCK ? '#898989' : '#d0d0d0'}]}>
                                <Image style={styles.iconLessThan} 
                                        source={CommonStyleSheet.THEME_DARCK ? 
                                            require("../resource/icon_less_than_white.png") : 
                                            require("../resource/icon_less_than_black.png")}/>
                                <Text style={{color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#7d7d7d'}}>{i18n.t('BACK_TO_SHOPPING')}</Text>
                            </View>
                        </TouchableOpacity>
                        {this._renderBackToShopping()}
                    </View>



                    {this._renderContent()}
                    {this._renderCheckout()}

                <PopupConfirm
                    ref={(popupDialog) => { this.popupDialog = popupDialog; }}
                    message={this.state.alertMessage}
                    cancelTitle={i18n.t('CANCEL')}
                    okTitle={this.state.okButton}      
                    buttonOK_clicked={this.buttonOKPoup_clicked}              
                />

                {/* load progress */}
                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }
}

const styles = StyleSheet.create({
    containView: {
        flex: 1
    },
    viewButton: {
        flexDirection: 'row', 
        justifyContent: 'space-between',
        marginTop: 15
    },
    viewLeftButton: {
        borderWidth: 1.5, 
        borderRadius: 2, 
        padding: 4, 
        marginLeft: 15,
        flexDirection: 'row', 
        justifyContent: 'center',
        alignItems: 'center'
    },
    iconLessThan: {
        width: 8, 
        height: 15, 
        marginRight: 5
    },
    iconClearCart: {
        width: 15, 
        height: 15, 
        marginRight: 5
    },
    flatList: {
        marginTop: 15, 
        flex: 1
    },
    emptyText: {
        fontSize: 18,
        fontWeight: '400',
        textAlign: 'center'
    },
    viewCheckout: {
        height: 40, 
        marginLeft: 15, 
        marginRight: 15, 
        marginBottom: 10, 
        justifyContent: 'space-between'
    },
    viewLine: {
        width: '100%', 
        height: 1
    },
    viewText: {
        flexDirection: 'row', 
        justifyContent: 'space-between', 
        alignItems: 'baseline'
    },
    textTotal: {
        fontSize: 20, 
        fontWeight: '500',
    },
    textBalance: {
        color: '#de113e', 
        fontSize: 26, 
        fontWeight: '500'
    },
    titleButtonCheckout: {
        color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white',
        fontWeight: '600'
    }
});