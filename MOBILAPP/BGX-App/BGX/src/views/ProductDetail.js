"use strict";
import React, { Component } from "react";
import {
    StyleSheet,
    View,
    Image,
    Text,
    TouchableOpacity,
    ImageBackground,
    ScrollView,
    StatusBar
} from "react-native";

import * as CommonStyleSheet from '../common/StyleSheet';
import Utils from "../common/Utils";
import SlideImageView from "../components/SlideImageView";
import RatingView from "../components/RatingView";
import TabView from "../components/TabView";
import BottomBarProductDetail from "../components/BottomBarProductDetail";
import LoadingIndicator from "../components/LoadingIndicator";
import ConfigAPI from "../api/ConfigAPI";
import {BaseService} from "../api/BaseService";
import Item from "../models/Item";
import * as Constants from '../common/Constants';
import Comment from '../models/Comment';
import PopupComment from '../components/PopupComment';
import Share, {ShareSheet, Button} from 'react-native-share';
import i18n from '../translations/i18n';

const BGX_LOGO = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/4QBARXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAA7aADAAQAAAABAAAARQAAAAD/7QA4UGhvdG9zaG9wIDMuMAA4QklNBAQAAAAAAAA4QklNBCUAAAAAABDUHYzZjwCyBOmACZjs+EJ+/+IH6ElDQ19QUk9GSUxFAAEBAAAH2GFwcGwCIAAAbW50clJHQiBYWVogB9kAAgAZAAsAGgALYWNzcEFQUEwAAAAAYXBwbAAAAAAAAAAAAAAAAAAAAAAAAPbWAAEAAAAA0y1hcHBsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALZGVzYwAAAQgAAABvZHNjbQAAAXgAAAWcY3BydAAABxQAAAA4d3RwdAAAB0wAAAAUclhZWgAAB2AAAAAUZ1hZWgAAB3QAAAAUYlhZWgAAB4gAAAAUclRSQwAAB5wAAAAOY2hhZAAAB6wAAAAsYlRSQwAAB5wAAAAOZ1RSQwAAB5wAAAAOZGVzYwAAAAAAAAAUR2VuZXJpYyBSR0IgUHJvZmlsZQAAAAAAAAAAAAAAFEdlbmVyaWMgUkdCIFByb2ZpbGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAG1sdWMAAAAAAAAAHwAAAAxza1NLAAAAKAAAAYRkYURLAAAALgAAAaxjYUVTAAAAJAAAAdp2aVZOAAAAJAAAAf5wdEJSAAAAJgAAAiJ1a1VBAAAAKgAAAkhmckZVAAAAKAAAAnJodUhVAAAAKAAAApp6aFRXAAAAFgAAAsJuYk5PAAAAJgAAAthjc0NaAAAAIgAAAv5oZUlMAAAAHgAAAyBpdElUAAAAKAAAAz5yb1JPAAAAJAAAA2ZkZURFAAAALAAAA4prb0tSAAAAFgAAA7ZzdlNFAAAAJgAAAth6aENOAAAAFgAAA8xqYUpQAAAAGgAAA+JlbEdSAAAAIgAAA/xwdFBPAAAAJgAABB5ubE5MAAAAKAAABERlc0VTAAAAJgAABB50aFRIAAAAJAAABGx0clRSAAAAIgAABJBmaUZJAAAAKAAABLJockhSAAAAKAAABNpwbFBMAAAALAAABQJydVJVAAAAIgAABS5hckVHAAAAJgAABVBlblVTAAAAJgAABXYAVgFhAGUAbwBiAGUAYwBuAP0AIABSAEcAQgAgAHAAcgBvAGYAaQBsAEcAZQBuAGUAcgBlAGwAIABSAEcAQgAtAGIAZQBzAGsAcgBpAHYAZQBsAHMAZQBQAGUAcgBmAGkAbAAgAFIARwBCACAAZwBlAG4A6AByAGkAYwBDHqUAdQAgAGgA7ABuAGgAIABSAEcAQgAgAEMAaAB1AG4AZwBQAGUAcgBmAGkAbAAgAFIARwBCACAARwBlAG4A6QByAGkAYwBvBBcEMAQzBDAEOwRMBD0EOAQ5ACAEPwRABD4ERAQwBDkEOwAgAFIARwBCAFAAcgBvAGYAaQBsACAAZwDpAG4A6QByAGkAcQB1AGUAIABSAFYAQgDBAGwAdABhAGwA4QBuAG8AcwAgAFIARwBCACAAcAByAG8AZgBpAGyQGnUoACAAUgBHAEIAIIJyX2ljz4/wAEcAZQBuAGUAcgBpAHMAawAgAFIARwBCAC0AcAByAG8AZgBpAGwATwBiAGUAYwBuAP0AIABSAEcAQgAgAHAAcgBvAGYAaQBsBeQF6AXVBeQF2QXcACAAUgBHAEIAIAXbBdwF3AXZAFAAcgBvAGYAaQBsAG8AIABSAEcAQgAgAGcAZQBuAGUAcgBpAGMAbwBQAHIAbwBmAGkAbAAgAFIARwBCACAAZwBlAG4AZQByAGkAYwBBAGwAbABnAGUAbQBlAGkAbgBlAHMAIABSAEcAQgAtAFAAcgBvAGYAaQBsx3y8GAAgAFIARwBCACDVBLhc0wzHfGZukBoAIABSAEcAQgAgY8+P8GWHTvZOAIIsACAAUgBHAEIAIDDXMO0w1TChMKQw6wOTA7UDvQO5A7oDzAAgA8ADwQO/A8YDrwO7ACAAUgBHAEIAUABlAHIAZgBpAGwAIABSAEcAQgAgAGcAZQBuAOkAcgBpAGMAbwBBAGwAZwBlAG0AZQBlAG4AIABSAEcAQgAtAHAAcgBvAGYAaQBlAGwOQg4bDiMORA4fDiUOTAAgAFIARwBCACAOFw4xDkgOJw5EDhsARwBlAG4AZQBsACAAUgBHAEIAIABQAHIAbwBmAGkAbABpAFkAbABlAGkAbgBlAG4AIABSAEcAQgAtAHAAcgBvAGYAaQBpAGwAaQBHAGUAbgBlAHIAaQENAGsAaQAg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';

export default class ProductDetail extends Component {

    constructor(props) {
        super(props);

        let item = this.props.navigation.getParam("item", null);

        let listMenu = [
            { id: 0,
              title: i18n.t('DESCRIPTION'),
              content: item.description,
              type: 1 },
            { id: 1,
              title: i18n.t('ITEM_INFO'),
              content: item.itemInfo,
              type: 1 },
            { id: 2,
              title: i18n.t('INSTRUCTION'),
              content: item.instruction,
              type: 1 },
            { id: 3,
              title: i18n.t('COMMENTS'),
              content: [],
              type: 2 }
          ];

        this.state = {
            itemSeleted: item,
            menuList: listMenu,
            imageList: [],
            isActiveFavorite: item.isFavorite,
            ratingStar: 1,
            comment: "",
            progressView: false
        }

        this.account = null;
        this.isRequestData = false;

        this.wishListItemID = "";
        if (item.isFavorite) {
            this.wishListItemID = item.wishListItemID;
        }

        this.isFinishRequest = false;
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
    });

    async componentDidMount() {
        this.requestData();
        this.account = await Utils.getDataWithKey(Constants.KEY_USER);
    }

    //Header button
    buttonBack_clicked = () => {
        if (Utils.isExistOnBackEvent(this.props.navigation)) {
            this.props.navigation.state.params.onBackEvent();
        }
        this.props.navigation.goBack();
    }

    // Bottom bar button
    buttonFavorite_clicked = () => {
        if (this.state.itemSeleted.isFavorite) {
            this.requestDeleteItemFavorite(this.state.itemSeleted.wishListItemID);
        } else {
            this.requestAddItemToFavorite(this.state.itemSeleted.id);
        }
    }

    buttonShare_clicked = () => {
        let shareOptions = {
            title: "BGX",
            url: BGX_LOGO,
            message: i18n.t('I_RECOMMEND_PRODUCT') + ' ' + ConfigAPI.DOMAIN_LINK + this.state.itemSeleted.productURL + '\n' + i18n.t('SEND_FROM_MOBILE_APP'),
            subject: "BGX" //  for email
          };
        Share.open(shareOptions);
    }      

    buttonToCart_clicked = async () => {
        this.requestAddShoppingCart(this.state.itemSeleted.id);
    }

    buttonLike_clicked = () => {

        let userName = this.account.username;
        if (userName == null || userName == "") {
            Utils.showAlert(i18n.t('ADD_USER_NAME_BEFORE_COMMENT'), true, null);
            return;
        }
        
        this.popupDialog.showSlideAnimationDialog();
    }

    // Popup button
    buttonOKPoup_clicked = (comment, star) => {
        Utils.dismissKeyboard();

        this.setState({ 
            ratingStar: star 
        });

        if (comment == null || comment == "") {
            Utils.showAlert(i18n.t('MESSAGE_NEED_ADD_COMMENT'), true, {
                done: () => {
                    setTimeout(() => {
                        this.popupDialog.showSlideAnimationDialog();
                    }, 50);
                }
            });

            return;
        }

        setTimeout(() => {
            this.popupDialog.dismissSlideAnimationDialog();
            this.requestAddComment(comment, star);
        }, 50);
    }

    //Request data
    requestData = () => {
        let sku = encodeURIComponent(this.state.itemSeleted.sku);
        
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_ITEM_DETAIL, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_ITEM_DETAIL.replace('{sku}', sku)
        };

        this.isFinishRequest = false;
        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    requestGetListComment = (productId) => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_GET_LIST_COMMENT, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_GET_LIST_COMMENT.replace('{product_id}', productId)
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

    requestDeleteItemFavorite = (wishListItemID) => {
        if (this.isRequestData) {
            return;
        }

        this.isRequestData = false;

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

    requestAddShoppingCart = (productID) => {
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

    requestAddComment = (comment, star) => {
        let nickName = this.account.username == null ? "User" : this.account.username;

        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_ADD_COMMENT, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_ADD_COMMENT,
            [ConfigAPI.PARAM_PRODUCT_ID]: this.state.itemSeleted.id,
            [ConfigAPI.PARAM_NICKNAME]: nickName,
            [ConfigAPI.PARAM_TITLE]: "Comment",
            [ConfigAPI.PARAM_DETAIL]: comment,
            [ConfigAPI.PARAM_RATING_VALUE]: star,
            [ConfigAPI.PARAM_CUSTOMERID]: this.account.magentoId,
            [ConfigAPI.PARAM_STORE_ID]: "1",
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.POST);
    }

    requestCheckProductIsBougth = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_CHECK_PRODUCT_IS_BOUGHT, 
            [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_CHECK_PRODUCT_IS_BOUGHT.replace('{customerId}', this.account.magentoId).replace('{productId}', this.state.itemSeleted.id)
        };

        this.setState({ progressView: true });
        let service = new BaseService();
        service.setCallback(this);
        service.setParam(params);
        service.requestDataPostGet(BaseService.METHOD.GET);
    }

    // Success, fail request data
    async onSuccess(code, message, data, method) {

        if (method === ConfigAPI.METHOD_GET_ITEM_DETAIL) {
            this.isFinishRequest = true;
            let item = new Item(data, Item.ITEM_PRODUCT_TYPE.DIGITAL_SPOT);
            item.isBought = this.state.itemSeleted.isBought;

            item.rating = this.state.itemSeleted.rating;
            if (this.state.isActiveFavorite) {
                item.isFavorite = true;
                item.wishListItemID = this.wishListItemID;
            }
        
            let listMenu = [
                { id: 0,
                    title: i18n.t('DESCRIPTION'),
                    content: item.description,
                    type: 1 },
                { id: 1,
                    title: i18n.t('ITEM_INFO'),
                    content: item.itemInfo,
                    type: 1 },
                { id: 2,
                    title: i18n.t('INSTRUCTION'),
                    content: item.instruction,
                    type: 1 },
                { id: 3,
                    title: i18n.t('COMMENTS'),
                    content: [],
                    type: 2 }
            ];

            this.setState({ 
                menuList: listMenu,
                imageList: item.arrayImage,
                itemSeleted: item
            });

            this.requestCheckProductIsBougth();
            
        } else if (method === ConfigAPI.METHOD_CHECK_PRODUCT_IS_BOUGHT) {
            if (data != null && data[0].status) {
                var item = this.state.itemSeleted;
                item.isBought = true;
                this.setState({ 
                    itemSeleted: item
                });
            }
            
            this.requestGetListComment(this.state.itemSeleted.id);
            this.forceUpdate();
        } else if (method === ConfigAPI.METHOD_GET_LIST_COMMENT) {
          
            if (data == null || data[0] == null || data[0].items == null) {
                this.setState({ 
                    progressView: false
                });
                return;
            }
            
            var array = [];
            let items = data[0].items;

            for (let item of items) {
                let itemParse = new Comment(item);
                array.push(itemParse)
            }

            var listMenu = this.state.menuList;
            
            listMenu[3].content = [];
            this.setState({ 
                menuList: listMenu
            });
            this.forceUpdate();

            listMenu[3].content = array;
            this.setState({ 
                progressView: false,
                menuList: listMenu
            });
            this.forceUpdate();
        } else if (method === ConfigAPI.METHOD_ADD_TO_WISH_LIST) {
            this.isRequestData = false;

            this.setState({ 
                progressView: false,
                isActiveFavorite: true
            });

            Utils.showAlert(i18n.t('MESSAGE_ADD_TO_WISHLIST'), true, null);

            if (data != null) {
                let countFavorite = data.length;

                for (var i = 0; i < countFavorite; i++) {
                    if (data[i].product_id == this.state.itemSeleted.id) {
                        this.state.itemSeleted.isFavorite = true;
                        this.state.itemSeleted.wishListItemID = data[i].wishlist_item_id;
                        break;
                    }
                }
            }
        } else if (method === ConfigAPI.METHOD_DELETE_ITEM_FAVORITE) {
            this.isRequestData = false;

            this.setState({ 
                progressView: false,
                isActiveFavorite: false
            });

            Utils.showAlert(i18n.t('MESSAGE_REMOVE_FROM_FAVORITE'), true, null);
            this.state.itemSeleted.isFavorite = false;
        } else if (method === ConfigAPI.METHOD_ADD_COMMENT) {

            this.setState({ 
                progressView: false,
                ratingStar: 1,
                comment: ""
            });

            if (data.status) {
                Utils.showAlert(i18n.t('MESSAGE_ADD_COMMENT_SUCCESSFULLY'), true, null);

                this.requestGetListComment(this.state.itemSeleted.id);
            } else {
                Utils.showAlert(i18n.t('MESSAGE_ADD_COMMENT_FAILED'), true, null);
            }
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
        alert(message)
    }

    _renderSlideImage = () => {
        if (this.isFinishRequest) {
            return (
                <SlideImageView containStyle={styles.slideView} 
                                    imageList={this.state.imageList}/>
            );
        }

        return null;
    }

    render() {
        StatusBar.setBarStyle('light-content', true);
        return (
            <View style={[styles.container, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white'}]}>
                <ImageBackground style={styles.imageBackground} source={require("../resource/header_background.png")}>
                    <View style={styles.viewHeaderTitle}>
                        <TouchableOpacity style={[{zIndex: 1}]} onPress={this.buttonBack_clicked}>
                            <Image style={styles.iconBack} source={require("../resource/icon_back_white.png")}/>
                        </TouchableOpacity>
                        <Text style={styles.textTitleHeader}>{i18n.t('PRODUCT_DETAIL')}</Text>
                    </View>
                </ImageBackground>

                <ScrollView>
                    {this._renderSlideImage()}
                    <View style={[styles.viewName, {marginTop: this.isFinishRequest ? 0 : 235}]}>
                        <Text style={[styles.textName, {color: CommonStyleSheet.THEME_DARCK ? '#ebebeb' : '#111111'}]}>{this.state.itemSeleted.name}</Text>
                        <Text style={styles.textBalance}>{this.state.itemSeleted.price + ' ' + i18n.t('BETA_SYMBOL')}</Text>
                    </View>
                    <RatingView viewStyle={styles.viewRating} ratingCount={this.state.itemSeleted.rating} imageSize={18}/>
                    <Text style={[styles.textDesciption, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{this.state.itemSeleted.shortDescription}</Text>
                    <View style={[styles.viewContainterItem, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#121212' : '#f5f5f5'}]}>
                        <View style={[styles.viewLine, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#0d0d0d' : '#e8e8e8'}]}/>
                        <View style={[styles.viewSubjectContain, {backgroundColor: CommonStyleSheet.THEME_DARCK ? '#121212' : '#f5f5f5'}]}>
                            <View style={{width: 100}}>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('SUBJECT')}</Text>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('PROVIDER')}</Text>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]}>{i18n.t('AUTHOR')}</Text>
                            </View>
                            
                            <View style={{flex: 1}}>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.state.itemSeleted.subject}</Text>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.state.itemSeleted.provider}</Text>
                                <Text style={[styles.textSubject, {color: CommonStyleSheet.THEME_DARCK ? '#898989' : '#7a7a7a'}]} numberOfLines={1}>{this.state.itemSeleted.author}</Text>
                            </View>
                        </View>
                    </View>
                    <TabView listItem={this.state.menuList}/>
                </ScrollView>
                <BottomBarProductDetail
                        buttonFavorite_clicked={this.buttonFavorite_clicked}
                        buttonShare_clicked={this.buttonShare_clicked}
                        buttonToCart_clicked={this.buttonToCart_clicked}
                        buttonLike_clicked={this.buttonLike_clicked}
                        isFavorite={this.state.isActiveFavorite}
                        isActiveComment={this.state.itemSeleted.isBought}/>

                <PopupComment
                    ref={(popupDialog) => { this.popupDialog = popupDialog; }}   
                    ratingStar={this.state.ratingStar}
                    comment={this.state.comment}
                    buttonOK_clicked={this.buttonOKPoup_clicked}              
                />

                {/* load progress */}
                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }
}

let leftMargin = 15;
const styles = StyleSheet.create({

    container: {
        flex: 1
    },
    imageBackground: {
        width: '100%', 
        height: 64, 
        flexDirection: 'row'
    },
    viewHeaderTitle: {
        width: '100%', 
        flexDirection: 'row', 
        marginTop: 30,
        justifyContent: 'space-between'
    },
    iconBack: {
        width: 23, 
        height: 14, 
        marginLeft: 15,
        marginTop: 3
    },
    textTitleHeader: {
        color: 'white', 
        fontSize: 24, 
        fontWeight: '300',
        flex: 1,
        textAlign: 'center',
        marginTop: -3,
        marginLeft: -40
    },
    slideView: {
        marginTop: 15, 
        width: Utils.appSize().width - 30, 
        height: 220, 
        alignSelf: 'center'
    },
    viewName: {
        marginLeft: leftMargin, 
        marginRight: leftMargin, 
        justifyContent: 'space-between', 
        flexDirection: 'row'
    },
    textName: {
        fontSize: 23, 
        fontWeight: '600'
    },
    textBalance: {
        fontSize: 22, 
        fontWeight: '600', 
        color: '#de113e'
    },
    viewRating: {
        marginLeft: leftMargin, 
        marginTop: 10
    },
    textDesciption: {
        marginLeft: leftMargin, 
        marginRight: leftMargin, 
        marginTop: 20, 
        fontSize: 14
    },
    viewLine: {
        width: '100%', 
        height: 1
    },
    viewContainterItem: {
        marginTop: 20
    },
    viewSubjectContain: {
        marginLeft: leftMargin, 
        marginRight: leftMargin, 
        marginTop: 15,
        marginBottom: 15,
        flexDirection: 'row'
    },
    textSubject: {
        fontSize: 14,
        marginTop: 3
    }
});