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
import BorderButtonNew from '../components/BorderButtonNew';
import PopupConfirm from '../components/PopupConfirm';
import Utils from "../common/Utils";
import * as Constants from '../common/Constants';
import LoadingIndicator from "../components/LoadingIndicator";
import ConfigAPI from "../api/ConfigAPI";
import {BaseService} from "../api/BaseService";
import Item from '../models/Item';

import i18n from '../translations/i18n';

export default class ShoppingCart extends Component {

    constructor(props) {
        super(props);

        this.state = {
            dataSource: [],
            totalAmmount: 0,
            alertMessage: i18n.t('MESSAGE_DO_YOU_WANT_TO_CLEAR_CART'),
            okButton: i18n.t('CLEAR'),
            progressView: false
        }

        this.isRequestData = false;
        this.account = null;
        this.currentIndex = -1;

        this.dataShoppingNotFull = null;
        this.publicPrivateKey = null;
    }

    static navigationOptions = ({ navigation, screenProps }) => ({
        header: null
    });

    componentDidMount = async () => {
        this.account = await Utils.getDataWithKey(Constants.KEY_USER);
        this.requestGetShoppingCartList();

        this.publicPrivateKey = await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE);
    }

    onBack_event = () => {
        this.requestGetWishList(this.account.magentoId);
    }

    // Header button
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

    buttonFavoriteHeader_clicked = () => {
        if (Utils.isExistOnBackEvent(this.props.navigation)) {
            this.props.navigation.state.params.onBackEvent();
        }

        this.props.navigation.navigate('FavoritesView', {onBackEvent: this.onBack_event } );
    }

    buttonShoppingCartHeader_clicked = () => {
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

    buttonClearCart_clicked = () => {
        this.setState({
            alertMessage: i18n.t('MESSAGE_DO_YOU_WANT_TO_CLEAR_CART'),
            okButton: i18n.t('CLEAR')
        });
        
        this.popupDialog.showSlideAnimationDialog();
    }

    //Item button
    buttonItemDelete_clicked = (index) => {
        this.setState({
            alertMessage: i18n.t('MESSAGE_DELETE_ITEM_FROM_CART'),
            okButton: i18n.t('OK')
        });

        this.currentIndex = index;
        this.popupDialog.showSlideAnimationDialog();
    }

    buttonItemSaveFavorite_clicked = (index) => {
        if (this.state.dataSource[index].isFavorite) {
            Utils.showAlert(i18n.t('MESSAGE_ITEM_EXIST_IN_FAVORITE_LIST'));
        } else {
            this.requestAddItemToFavorite(this.state.dataSource[index].id);
        } 
    }

    // Bottom button
    buttonCheckout_clicked = () => {
        this.requestGetMyCard();
    }

    //Popup button
    buttonOKPoup_clicked = async () => {
        this.popupDialog.dismissSlideAnimationDialog();
        if (this.state.alertMessage === i18n.t('MESSAGE_DO_YOU_WANT_TO_CLEAR_CART')) {
            this.requestClearCart();
        } else if (this.state.alertMessage === i18n.t('MESSAGE_DELETE_ITEM_FROM_CART')) {
            let data = this.state.dataSource[this.currentIndex];
            this.requestRemoveItemCart(data.id);
        }
    }

    _calculateTotalAmmount = (list) => {
        var ammount = 0;
        for (let item of list) {
            ammount += parseFloat(item.price);
        }

        return Utils.formatPrice(ammount);
    }

    //Request data
    requestGetShoppingCartList = () => {
        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_SHOPPING_CART,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USER_ID]: this.account.id
        };

        let login = new BaseService();
        login.setParam(params);
        login.setCallback(this);
        login.requestData();
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

        // this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestGetListProductFiltered = (listProductID) => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_PRODUCT_FILTERED, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_LIST_PRODUCT_FILTERED.replace('{string_product_id}', listProductID)
        };
  
        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

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

    requestAddItemToFavorite = (productID) => {

        if (this.isRequestData) {
            return;
        }

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_ADD_TO_WISH_LIST, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_ADD_TO_WISH_LIST + productID,
            [ConfigAPI.PARAM_CUSTOMER_ID]: this.account.magentoId
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.POST);
    }

    requestRemoveItemCart = (productID) => {
        if (this.isRequestData) {
            return;
        }

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_DELETE_SHOPPING_CART_ITEM, 
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USER_ID]: this.account.id,
            [ConfigAPI.PARAM_PRODUCT_ID]: productID
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setParam(params);
        service.setCallback(this);
        service.requestData();
    }

    requestClearCart = () => {
        if (this.isRequestData) {
            return;
        }

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_CLEAR_SHOPPING_CART, 
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_USER_ID]: this.account.id,
            [ConfigAPI.PARAM_PRODUCT_ID]: 0
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setParam(params);
        service.setCallback(this);
        service.requestData();
    }

    requestGetMyCard = async () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LIST_CARD,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.account.email,
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: this.account.password,
        };
        this.setState({ progressView: true });
        let listCard = new BaseService();
        listCard.setParam(params);
        listCard.setCallback(this);
        listCard.requestData();
    }

    async onSuccess(code, message, data, method) {
        this.setState({ 
            progressView: false
        });

        this.isRequestData = false;

        if (method === ConfigAPI.METHOD_GET_LIST_SHOPPING_CART) {
            let count = data.length;
            if (count > 0) {
                this.dataShoppingNotFull = data;

                var stringID = "";
                for (let item of data) {
                    stringID += item.productId + ',';
                }
                stringID = stringID.substring(0, stringID.length - 1);

                this.requestGetListProductFiltered(stringID);
            }
        } else if (method === ConfigAPI.METHOD_GET_LIST_PRODUCT_FILTERED) {
            var array = [];

            for (let itemNotFull of this.dataShoppingNotFull) {

                for (let item of data.items) {
                    let itemParse = new Item(item, Item.ITEM_PRODUCT_TYPE.DIGITAL_SPOT);
                    if (itemParse.id == itemNotFull.productId) {
                        array.push(itemParse);
                        continue;
                    }   
                }
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

            this.requestGetWishList(this.account.magentoId);
        } else if (method === ConfigAPI.METHOD_GET_WISH_LIST) {

            let countDataSource = this.state.dataSource.length;
            for (var i = 0; i < countDataSource; i++) {
                this.state.dataSource[i].isFavorite = false;
                this.state.dataSource[i].wishListItemID = "";
            }

            if (data != null) {
                let countFavorite = data.length;
                let countDataSource = this.state.dataSource.length;
                for (var i = 0; i < countFavorite; i++) {
                    for (var j = 0; j < countDataSource; j++) {
                        if (data[i].product_id == this.state.dataSource[j].id) {
                            this.state.dataSource[j].isFavorite = true;
                            this.state.dataSource[j].wishListItemID = data[i].wishlist_item_id;
                            continue;
                        }
                    }
                }
            }

            this.setState({ 
                dataSource: this.state.dataSource
            });
        } else if (method === ConfigAPI.METHOD_ADD_TO_WISH_LIST) {

            Utils.showAlert(i18n.t('MESSAGE_ADD_TO_WISHLIST'), true, null);

            if (data != null) {
                let countFavorite = data.length;
                let countDataSource = this.state.dataSource.length;

                for (var i = 0; i < countFavorite; i++) {
                    for (var j = 0; j < countDataSource; j++) {
                        if (data[i].product_id == this.state.dataSource[j].id) {
                            this.state.dataSource[j].isFavorite = true;
                            this.state.dataSource[j].wishListItemID = data[i].wishlist_item_id;
                            continue;
                        }
                    }
                }
            }
        } else if (method === ConfigAPI.METHOD_DELETE_SHOPPING_CART_ITEM) {
            var shoppingList = this.state.dataSource;
            shoppingList.splice(this.currentIndex, 1);

            let ammount = this._calculateTotalAmmount(shoppingList);
            
            this.setState({ 
                dataSource: [],
                totalAmmount: ammount
            });

            this.setState({ 
                dataSource: shoppingList,
                totalAmmount: ammount
            });

            Utils.showAlert(i18n.t('MESSAGE_ITEM_REMOVE_FROM_SHOPPING_CART'), true, null);
        } else if (method === ConfigAPI.METHOD_CLEAR_SHOPPING_CART) {
            Utils.showAlert(i18n.t('MESSAGE_ALL_ITEMS_REMOVED_SHOPPING_LIST'), true, null); 

            setTimeout(() => {
                this.setState ({
                    dataSource: []
                });
            }, 100);
        } else if (method == ConfigAPI.METHOD_LIST_CARD) {
            
            if (data == null) {
                Utils.showAlert(i18n.t('MESSAGE_NEED_BGT_CARD_TO_CHECK_OUT'), true, null);
                return;
            }

            var bgtCard = null;
            var canPayment = false;
            for (let card of data) {
                if (card.payment == "1") {
                    if (card.active == "1") {
                        canPayment = true;
                        bgtCard = card;
                        break; 
                    }
                }
            }

            if (canPayment) {
                if (this.publicPrivateKey == null) {
                    Utils.showAlert(i18n.t('MESSAGE_NEED_IMPORT_KEY_TO_CHECK_OUT'), true, null);
                    return;
                }
                this.props.navigation.navigate('CheckingOutView', {card: bgtCard});
            } else {
                Utils.showAlert(i18n.t('MESSAGE_NEED_BGT_CARD_TO_CHECK_OUT'), true, null);
            }
        }
    }

    async onFail(code, message, method) {
        this.setState({ progressView: false });
        alert(message)
    }

    // Render function
    _renderClearShoppingCart = () => {
        if (this.state.dataSource.length != 0) {
            return (                
                    <TouchableOpacity onPress={this.buttonClearCart_clicked}>
                        <View style={[styles.viewRightButton, {borderColor: CommonStyleSheet.THEME_DARCK ? '#898989' : '#d0d0d0'}]}>
                            <Image style={styles.iconClearCart} 
                                    source={CommonStyleSheet.THEME_DARCK ? 
                                        require("../resource/icon_clear_white.png") : 
                                        require("../resource/icon_clear_black.png")}/>
                            <Text style={{color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#7d7d7d'}}>{i18n.t('CLEAR_CART')}</Text>
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
                    <Text style={[styles.emptyText, {color: CommonStyleSheet.THEME_DARCK ? '#7d7d7d' : '#959595'}]}>{i18n.t('SHOPPING_CART_EMPTY')}</Text>
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
                            <ItemShoppingCart   itemIndex={index} 
                                                itemData={item}
                                                onPressSelectItem={this.onPressSelectItem}
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
                        <View style={{flexDirection: 'row', justifyContent: 'space-between', alignItems: 'baseline', marginTop: 5}}>
                            <Text style={[styles.textTotal, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{i18n.t('TOTAL_AMOUNT')}</Text>
                            <Text style={styles.textBalance}>{this.state.totalAmmount + ' ' + i18n.t('BETA_SYMBOL')}</Text>
                        </View>
                        {/* , {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#252525'} */}
                        {/* <BorderButton
                            title={i18n.t('CHECKOUT')}
                            onPress={this.buttonCheckout_clicked}
                            titleStyle={[styles.titleButtonCheckout, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                            style={[styles.buttonCheckout]}
                            imageStyle={[{width: Utils.appSize().width - 30, height: 36}]}
                            background={CommonStyleSheet.THEME_DARCK ? require("../resource/icon_button_white.png") : require("../resource/icon_button_drack.png")}/> */}
                            <BorderButtonNew
                                title={i18n.t('CHECKOUT')}
                                onPress={this.buttonCheckout_clicked}
                                titleStyle={[styles.titleButtonCheckout, {color: CommonStyleSheet.THEME_DARCK ? 'black' : 'white'}]}
                                style={{width: Utils.appSize().width - 30, height: 50, marginTop: 10}}
                                imageStyle={[{width: Utils.appSize().width - 30, height: 50}]}
                                buttonType={CommonStyleSheet.THEME_DARCK ? 
                                    BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_DARK : 
                                    BorderButtonNew.BORDER_BUTTON_TYPE.HEIGHT_36_WIDTH_0_WHITE}/>
                    </View>
            );
        }

        return null;
    }

    render() {
        return (
            <View style={[styles.containView, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <HeaderDigitalSpot
                    headerTitle={i18n.t('SHOPPING_CART')}
                    page={HeaderDigitalSpot.VIEW_CONTROLLER.SHOPPING_CART}
                    buttonBackHeader_clicked={this.buttonBackHeader_clicked}
                    buttonMyOrdersHeader_clicked={this.buttonMyOrdersHeader_clicked}
                    buttonFavoriteHeader_clicked={this.buttonFavoriteHeader_clicked}
                    isShowBack={true}
                    typeView={HeaderDigitalSpot.VIEW_CONTROLLER.SHOPPING_CART}/>
                    
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
                        {this._renderClearShoppingCart()}
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
        marginTop: 12
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
    viewRightButton: {
        borderWidth: 1.5, 
        borderRadius: 2, 
        padding: 4, 
        marginRight: 15,
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
        marginTop: 10, 
        flex: 1
    },
    emptyText: {
        fontSize: 18,
        fontWeight: '400',
        textAlign: 'center'
    },
    viewCheckout: {
        height: 80, 
        marginLeft: 15, 
        marginRight: 15, 
        marginBottom: 22, 
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
    buttonCheckout: {
        height: 36,
        flex: 0,
        marginBottom: 0
    },
    titleButtonCheckout: {
        fontWeight: '600'
    }
});