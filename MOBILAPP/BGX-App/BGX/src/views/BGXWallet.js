import React, { Component } from "react";
import { StyleSheet, View, TouchableOpacity, Text, Image, FlatList, ActivityIndicator, TouchableHighlight, NetInfo } from "react-native";
import Icon from "react-native-vector-icons/MaterialIcons";
import TabBarComponent from '../components/TabBarComponent';
import Utils from '../common/Utils';
import Line from "../components/Line";
import CardItem from '../components/CardItem';
import ItemBGXWalletAddFund from '../cells/ItemBGXWalletAddFund';
import { GetTransactionService } from '../api/GetTransactionService';
import { BaseService } from '../api/BaseService';
import ConfigAPI from '../api/ConfigAPI';
import * as Constants from '../common/Constants';
var md5 = require('md5');
import PopupAddCard from '../components/PopupAddCard';
import PopupMessage from '../components/PopupMessage';
import LoadingIndicator from "../components/LoadingIndicator";
import PopupDialog, { SlideAnimation } from 'react-native-popup-dialog';
import Account from "../models/Account";
import ModalDropdown from '../components/ModalDropdown';
import IconMaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import IconMaterialIcons from "react-native-vector-icons/MaterialIcons";
import MakeInAppPurchase from '../nativeFunction/MakeInAppPurchase';
import * as CommonStyleSheet from '../common/StyleSheet';
var moment = require('moment');
import i18n from '../translations/i18n';
import GetPublicPrivateKey from '../nativeFunction/GetPublicPrivateKey';

const slideAnimation = new SlideAnimation({
    slideFrom: 'bottom',
});

export default class BGXWallet extends Component {

    constructor(props) {
        super(props);
        this.popupDialog = null;
        this.state = {
            ammount: 0,
            dataSource: [],
            dataCardType: [],
            myCard: [],
            myAllCard: [],
            isHiddenWarning: false,
            refreshing: false,
            marginTopListTransaction: 0,
            marginBottomListTransaction: 0,
            heightListTransaction: 0,
            progressView: false,
            account: "",
            defaultValue: "title",
            itemCard: "",
            balance: 0,
            balanceServer: 0,
            publicPrivateKeys: "", //publicKey , privateKey , 
            isCheckInternet: true
        }

        this.makingAddFun = false;
    }

    static navigationOptions = ({ navigation, screenProps }) => ({

        header: null
    });


    //         // // //TODO test
    // GetPublicPrivateKey.getSignedPayload("0x0f127dec1107acde3c6160c24b5fa8109fa9bc38",
    //     "100",
    //     "bgt",
    //     "any reason to add funds",
    //     "MC4CAQEEIDRJDlE49jMtzF7nnKBJWL73xCn3xCHTf2xVLkboohojoAcGBSuBBAAK",
    //     (signPayload) => {

    //         console.log("getSignedPayload", JSON.stringify(signPayload));
    //     }, (error) => {
    //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
    //     }
    // );

    // GetPublicPrivateKey.getMakeSignedPayload("0x0f127dec1107acde3c6160c24b5fa8109fa9bc38",
    //     "0x7a8b0becac9ab4f1602ebc9646ae8ddb707759b5",
    //     "bgt",
    //     "90",
    //     "MC4CAQEEIDRJDlE49jMtzF7nnKBJWL73xCn3xCHTf2xVLkboohojoAcGBSuBBAAK",
    //     (signPayload) => {
    //         console.log("getMakeSignedPayload", JSON.stringify(signPayload));
    //     }, (error) => {
    //         Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
    //     }
    // );

    async  componentDidMount() {
        NetInfo.isConnected.fetch().then(async isConnected => {
            this.setState({ isCheckInternet: isConnected });
            if (isConnected) {
                this.requestGetListCard();
                let temAccount = await Utils.getDataWithKey(Constants.KEY_USER);
                this.setState({
                    account: new Account(temAccount),
                    publicPrivateKeys: await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE)
                }, () => {
                    this.requestGetMyCard();
                });
            }
        });
        NetInfo.isConnected.addEventListener(
            'connectionChange',
            this.handleFirstConnectivityChange
        );
    }

    async componentWillMount() {

    }

    handleFirstConnectivityChange = async (isConnected) => {
        this.setState({ isCheckInternet: isConnected });
        if (isConnected) {
            this.requestGetListCard();
            let temAccount = await Utils.getDataWithKey(Constants.KEY_USER);
            this.setState({
                account: new Account(temAccount),
                publicPrivateKeys: await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE)
            }, () => {
                this.requestGetMyCard();
            });
        }
    }
    renderHeader = () => {
        return (
            <View style={styles.header}>
                <TouchableOpacity style={styles.header_left} onPress={() => this.props.navigation.openDrawer()}>
                    <Icon name="menu" size={30} color="white" />
                </TouchableOpacity>
                <Text style={styles.title}>{i18n.t('WALLET')}</Text>
                <TouchableOpacity disabled={this.state.myCard.length == 2 ? true : false}
                    style={[styles.header_right,]}
                    onPress={() => this._showAddCard()}>
                    <Text style={[styles.title_right, {
                        color: this.state.myCard.length == 2 ? '#898989' : CommonStyleSheet.THEME_DARCK ? 'white' : 'white'
                    }]}>{i18n.t('WELLET_ADD_NEW')}</Text>
                </TouchableOpacity>
            </View >
        );
    }

    renderBottom = () => {
        return (
            <View style={styles.bottom}>
                <Line style={{ width: '100%', }} />
                <View style={styles.tab_bottom}>
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_add_fund.png')}
                        title={i18n.t('TAB_ADD_FUND')}
                        hide={false}
                        disabled={this.state.itemCard.cardTypeId == 1 || this.state.myCard.length == 0}
                        onPress={this._confirmAddFund}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_transfer_to.png')}
                        title={i18n.t('TAB_TRANSFER')}
                        hide={false}
                        onPress={this._transferTo}
                        disabled={this.state.itemCard.cardTypeId == 1 || this.state.myCard.length == 0}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_exchange.png')}
                        title={i18n.t('TAB_EXCHANGE')}
                        hide={false}
                        disabled={true}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_with_raw.png')}
                        title={i18n.t('TAB_WITHRAW')}
                        hide={false}
                        disabled={true}
                    />
                    <TabBarComponent
                        icon={require('../resource/tabIcon/icon_tab_delete.png')}
                        title={i18n.t('TAB_DELETE')}
                        hide={this.state.myCard.length > 0 ? false : true}
                        onPress={() => this.popupDialogDelete.showSlideAnimationDialog(i18n.t('WELLET_DELETE'))}
                    />

                </View>
            </View>

        );
    }

    handleRefresh = () => {
        this.setState({ refreshing: true },
            () => {
                this.setState({ dataSource: [] });
                this.requestGetTransaction();
            });
    }

    updateView = (event) => {
        let height = event.nativeEvent.layout.height;
        this.setState({ marginTopListTransaction: event.nativeEvent.layout.y },
            () => {
                this.setState({
                    heightListTransaction: Number((Utils.appSize().height - this.state.marginTopListTransaction
                        - height - Constants.HEIGHT_BOTTOM
                        - Constants.HEIGHT_STATUS_BAR
                        - Constants.HEIGHT_PADDING_TOP_STATUS).toFixed(1))
                }, () => {
                    // alert(Utils.appSize().height
                    //     + "\n" + 'marginTopListTransaction :' + this.state.marginTopListTransaction
                    //     + "\n" + 'heightListTransaction :' + this.state.heightListTransaction)
                });
            });
    }

    render() {

        return (
            <View style={[styles.container, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#181818' : 'white', }]}>
                <Image style={styles.background} source={require('../resource/background_dark.jpg')} />
                <View style={styles.content}>
                    {this.renderHeader()}
                    {this.state.myCard.length > 0 ?
                        <View style={styles.view_content_picker}>
                            <ModalDropdown
                                style={styles.dropdown}
                                textStyle={styles.dropdown_text}
                                dropdownStyle={[styles.dropdown_dropdown, { height: this.state.myCard.length == 2 ? 90 : 45 }]}//this.state.heightModalDropdown
                                options={this.state.myCard}
                                defaultValue={this.state.defaultValue}
                                renderButtonText={(rowData) => this._renderButtonText(rowData)}
                                renderRow={this._renderRow.bind(this)}
                                renderSeparator={(sectionID, rowID, adjacentRowHighlighted) => this._renderSeparator(sectionID, rowID, adjacentRowHighlighted)}
                                onSelect={(idx, value) => this._dropdowOnSelect(idx, value)}
                                disabled={this.state.myCard.length < 2}
                            />
                            <IconMaterialCommunityIcons
                                style={styles.icon_down}
                                name="menu-down"
                                size={30}
                                color={this.state.myCard.length < 2 ? "#898989" : 'white'} />
                            <Line style={styles.line} />
                        </View> : null}
                    {this.state.myCard.length > 0 ?
                        <View style={styles.content_card}>
                            {/* <View style={styles.name_card}>
                                <Text style={[styles.text_bgx_erc, { fontSize: 16 }]}>BGX ERC </Text>
                                <Text style={styles.text_bgx_erc_value}>20</Text>
                            </View> */}
                            < CardItem styleView={styles.card_item}
                                cardType={this.state.itemCard.cardTypeId}
                                balance={this.state.balance}
                                cardHolder={this.state.itemCard.cardHolder}
                                publicKey={this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? this.state.itemCard.address : this.state.itemCard.publicKey}
                                onLongPress={this._onLongPressCard}
                            />
                            <View style={styles.content_card_status}>
                                <Text style={[styles.text,]}>{i18n.t('BALANCE')}</Text>
                                <View style={styles.content_bgx_balance}>
                                    <Text style={[styles.text_bgx_erc_value, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{this.state.balance}
                                        <Text style={[styles.text_bgx_erc, { fontSize: 12, }]}>
                                            {this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? ' DEC' :
                                                <Text style={[styles.textCurrency, { fontSize: 12, color: '#de113e' }]}>{' ' + i18n.t('BETA_SYMBOL')}</Text>}
                                        </Text>
                                    </Text>

                                </View>
                                <View style={styles.content_bgx_balance}>
                                    <Text style={styles.text}>{i18n.t('LAST_UPDATE')}</Text>
                                    <Text style={styles.text_bgx_erc}>
                                        {Utils.stringFromDate(this.state.itemCard.created, 'DD.MM.YYYY')}
                                    </Text>
                                </View>
                            </View>
                        </View>
                        : <Text style={styles.wellet_no_card}>{i18n.t('WELLET_NO_ANY_CARD')}</Text>
                    }
                    <Text style={[styles.last_transactions, { color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }]}>{i18n.t('LAST_10_TRANSACTION')}</Text>
                    <View style={[styles.header_list_transaction, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#121212' : '#F5F5F5', }]}
                        onLayout={(event) => { this.updateView(event) }}>
                        <Text style={[{ width: '27%', color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }, styles.transaction_text]}>{i18n.t('DATE')}</Text>
                        <Text style={[{ width: '33%', color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }, styles.transaction_text]}>{i18n.t('FROM_TO')}</Text>
                        <Text style={[{ width: '40%', textAlign: 'right', color: CommonStyleSheet.THEME_DARCK ? 'white' : 'black', }, styles.transaction_text]}>{i18n.t('AMOUNT')}</Text>
                    </View>
                    <View style={[styles.list_transaction, { height: this.state.heightListTransaction }]}>
                        {
                            this.state.dataSource.length > 0 ?
                                <FlatList
                                    showsHorizontalScrollIndicator={false}
                                    showsVerticalScrollIndicator={false}
                                    style={[styles.list_transaction, { height: this.state.heightListTransaction }]}
                                    data={this.state.dataSource}
                                    keyExtractor={(item, index) => index.toString()}
                                    ItemSeparatorComponent={() => <View style={[styles.separator, { backgroundColor: CommonStyleSheet.THEME_DARCK ? '#121212' : 'white' }]} />}
                                    renderItem={({ item, separators }) => (
                                        <ItemBGXWalletAddFund
                                            item={item}
                                            currentAddress={this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT ? this.state.itemCard.address : this.state.account.ethereumAddress}
                                            typeCard={this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT ? Constants.BGX_BGT_ACCOUNT.BGT : Constants.BGX_BGT_ACCOUNT.BGX}
                                            onPress={this._showTransactionsDetail}
                                        />
                                    )}
                                    refreshing={this.state.refreshing}
                                    onRefresh={this.handleRefresh}
                                />
                                : <Text style={[styles.wellet_no_transactions,]}>{i18n.t('WELLET_NO_ITEMS')}</Text>
                        }

                    </View>

                </View>
                {this.renderBottom()}
                <PopupAddCard
                    ref={(popupDialog) => { this.popupDialog = popupDialog; }}
                    onPress={(cardTypeSelect) => this._newCardEditor(cardTypeSelect, false)}
                    cardTypes={this.state.dataCardType}
                />

                <PopupMessage
                    ref={(popupDialogDelete) => { this.popupDialogDelete = popupDialogDelete; }}
                    onPress={this._deleteCard}
                    isConfirm={true}
                    titleButtonLeft={i18n.t('CANCEL')}
                    titleButtonRight={i18n.t('DELETE')}
                    title={'about:blank'}
                />
                <PopupMessage
                    ref={(popupDialogEdit) => { this.popupDialogEdit = popupDialogEdit; }}
                    onPress={this._editCard}
                    isConfirm={true}
                    titleButtonLeft={i18n.t('CANCEL')}
                    titleButtonRight={i18n.t('OK')}
                    title={i18n.t('WELLET_EDIT_CARD')}
                />
                <PopupMessage
                    ref={(popupDialogConfirmAddFund) => { this.popupDialogConfirmAddFund = popupDialogConfirmAddFund; }}
                    onPress={this._addFund}
                    onPressCancel={this._cancelFund}
                    isConfirm={true}
                    titleButtonLeft={i18n.t('CANCEL')}
                    titleButtonRight={i18n.t('OK')}
                    title={i18n.t('CONFIRM_BUY_TOKEN')}
                />

                {this.state.progressView ? <LoadingIndicator style={styles.progress_view} /> : null}
            </View>
        );
    }

    requestGetTransaction = () => {
        this.setState({
            dataSource: [],
        });

        let service = new GetTransactionService()
        service.setCallback(this);
        service.setCardType(this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT ? Constants.BGX_BGT_ACCOUNT.BGT : Constants.BGX_BGT_ACCOUNT.BGX)
        service.setAddress(this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT ? this.state.itemCard.address : this.state.itemCard.address);
        service.requestData();
    }

    requestGetListCard = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LIST_CARD_TYPE,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,

        };
        let listCard = new BaseService();
        listCard.setParam(params);
        listCard.setCallback(this);
        listCard.requestData();
    }

    requestGetMyCard = () => {
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_LIST_CARD,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.account.email,
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: this.state.account.password,
        };
        let listCard = new BaseService();
        listCard.setParam(params);
        listCard.setCallback(this);
        listCard.requestData();
    }

    async onSuccess(code, message, data, method) {
        if (method == ConfigAPI.API_GLOBAL_TRANSACTIONS) {
            this.setState({
                dataSource: [],
                refreshing: false,
            });
            if (this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT) {
                var cards = [];
                for (let item of data) {
                    let isCheckTo = item.address_to === this.state.itemCard.address;
                    let isCheckFrom = item.address_from === this.state.itemCard.address;
                    if (isCheckTo || isCheckFrom) {
                        cards.push(item);
                    }
                }
                cards.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return moment(b.timestamp) - moment(a.timestamp);
                });
                cards = cards.slice(0, 10);
                this.setState({
                    dataSource: cards,
                    refreshing: false,
                });
            } else {
                this.setState({
                    dataSource: data,
                    refreshing: false,
                });
            }
        } else if (method == ConfigAPI.METHOD_LIST_CARD_TYPE) {
            // console.log("METHOD_LIST_CARD_TYPE : ", JSON.stringify(data));
            this.setState({ dataCardType: data });
            Utils.saveDataWithKey(Constants.KEY_DATA_CARD_TYPE, data);
        } else if (method == ConfigAPI.METHOD_LIST_CARD) {
            // console.log("METHOD_LIST_CARD", JSON.stringify(data));
            this.setState({
                myCard: [],
                myAllCard: data
            });
            if (Utils.isValidString(JSON.stringify(data)) && data.length != 0) {
                var cards = [];
                for (var item of data) {
                    if (item.active == '1') {
                        cards.push(item);
                    }
                }
                this.setState({
                    myCard: cards,
                }, () => {
                    if (this.state.myCard.length > 0) {
                        this._defaultValue();
                    }
                });
            }
        } else if (method == ConfigAPI.METHOD_BGX_GET_BALANCE) {
            this.setState({ balance: Utils.decimalValue(data / 1000000000000000000, 2) == 0.00 ? 0 : Utils.decimalValue(data / 1000000000000000000, 2) });
        } else if (method == ConfigAPI.METHOD_BGT_GET_BALANCE) {
            console.log("METHOD_BGT_GET_BALANCE : ", data);
            let number = data;
            number = Utils.decimalValue(number, 2);
            if (data % 1 == 0) {
                number = parseInt(data, 10);
            }
            this.setState({
                balance: number,
                balanceServer: data,
            });
        } else if (method == ConfigAPI.METHOD_UPDATE_CARD) {
            Utils.showAlert(i18n.t('DELETE_CARD_SUCCESSFULLY'), true, null);
            this.setState({ itemCard: "" });
            this.requestGetMyCard();
            Utils.saveDataWithKey(Constants.KEY_PUBLIC_PRIVATE, null);
        } else if (method == ConfigAPI.API_ADD_FUND) {
            if (Utils.isAndroid()) {
                Utils.showAlert(i18n.t('FUND_ADD_SUCCESSFUL'), true, null);
            }

            this._getBalance();
            this.requestGetTransaction();
        }

        setTimeout(() => { this.setState({ progressView: false }) }, 3000);
    }

    async onFail(code, message, method) {
        if (method == ConfigAPI.API_GET_TRANSACTION) {
            this.setState({
                dataSource: [],
            });
        } else if (method == ConfigAPI.API_ADD_FUND) {
            if (Utils.isAndroid()) {
                Utils.showAlert(message, true, null);
            }
        }

        this.setState({
            refreshing: false
        });

        setTimeout(() => { this.setState({ progressView: false }) }, 1000);
    }

    /**
     * show add card 
     */
    _showAddCard = () => {
        if (!this.state.isCheckInternet) {
            Utils.showAlert(i18n.t('NO_INTERNET'), true, null);
            return;
        }
        this.popupDialog.setData(this.state.dataCardType);
        setTimeout(() => {
            this.popupDialog.showSlideAnimationDialog(this.state.itemCard == "" ? 0 : this.state.itemCard.cardTypeId);
        }, 100);
    }

    /**
     * new card
     */
    _newCardEditor = (cardTypeSelect, isEdit) => {
        var cardUpdate = null;
        for (var item of this.state.myAllCard) {
            if (item.cardTypeId == cardTypeSelect.cardTypeId) {
                cardUpdate = item;
                break;
            }
        }
        this.props.navigation.navigate('BGXWalletCardEditorView', {
            cardTypeSelect: cardTypeSelect,
            cardUpdate: cardUpdate,
            balance: this.state.balanceServer,
            onBack: this._onBackScreen,
            isEdit: isEdit
        });
    }

    /**
     * delete card
     */
    _deleteCard = () => {
        if (this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGT) {
            if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.itemCard.publicKey) {
                Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
                return;
            }
        }
        this.setState({ progressView: true });
        let params = {
            [ConfigAPI.PARAM_METHOD]: ConfigAPI.METHOD_UPDATE_CARD,
            [ConfigAPI.PARAM_LOCALE]: i18n.locale,
            [ConfigAPI.PARAM_EMAIL]: this.state.account.email,
            [ConfigAPI.PARAM_CURRENT_PASSWORD]: this.state.account.password,
            [ConfigAPI.PARAM_CARDID]: this.state.itemCard.id,
            [ConfigAPI.PARAM_ACTIVE]: 0,//update card 0 
        };
        let updateCard = new BaseService();
        updateCard.setParam(params);
        updateCard.setCallback(this);
        updateCard.requestData();
    }

    _renderButtonText(rowData) {
        return rowData.title;
    }

    _renderRow(rowData, rowID, highlighted) {
        return (
            <TouchableHighlight underlayColor='black'>
                <View >
                    <Text style={[styles.dropdown_row_text, highlighted && { color: 'black' }]}>
                        {rowData.title}
                    </Text>
                </View>
            </TouchableHighlight>
        );
    }

    _renderSeparator(sectionID, rowID, adjacentRowHighlighted) {
        return (<View style={styles.dropdown_separator} />);
    }

    _defaultValue = () => {
        let title = "";
        let itemCardSelect = null;
        for (let itemCard of this.state.myCard) {
            title = itemCard.title;
            itemCardSelect = itemCard;
            if (itemCard.cardTypeId == 2) {
                title = itemCard.title;
                itemCardSelect = itemCard;
                break;
            }
        }
        this.setState({
            defaultValue: title,
            itemCard: itemCardSelect
        }, () => {
            this.setState({
                dataSource: [],
                refreshing: false,
                progressView: true
            });
            this.requestGetTransaction();
            this._getBalance();
        });
    }

    /** position 
    * 0 : BGX - DEC
    * 1 : BGT  
    * */
    _dropdowOnSelect = (id, card) => {
        this.setState({
            itemCard: card,
            balance: 0
        }, () => {
            this.setState({
                dataSource: [],
                refreshing: false,
                progressView: true
            });
            this.requestGetTransaction();
            this._getBalance();
        });

    }

    _getBalance = () => {
        this.setState({
            balance: 0
        });
        let params = {
            [ConfigAPI.PARAM_METHOD]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? ConfigAPI.METHOD_BGX_GET_BALANCE : ConfigAPI.METHOD_BGT_GET_BALANCE,
            [ConfigAPI.PARAM_API_URL]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ?
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', this.state.itemCard.address) :
                ConfigAPI.API_GET_BALANCE_BGT.replace('{hasher_public_key}', this.state.itemCard.address)
        };

        // alert(JSON.stringify(params))
        console.log("_getBalance : ", JSON.stringify(params));
        let balance = new BaseService();
        balance.setParam(params);
        balance.setCallback(this);
        balance.requestDataPostGet(BaseService.METHOD.GET);
    }

    _onBackScreen = () => {
        setTimeout(() => {
            this.setState({ progressView: true },
                async () => {
                    this.setState({
                        publicPrivateKeys: await Utils.getDataWithKey(Constants.KEY_PUBLIC_PRIVATE)
                    });
                    this.requestGetMyCard();
                });
        }, 500);
    }

    _onLongPressCard = () => {
        this.popupDialogEdit.showSlideAnimationDialog(i18n.t('WELLET_EDIT_CARD'))

    }

    _editCard = () => {
        for (var itemCardSelect of this.state.dataCardType) {
            if (itemCardSelect.cardTypeId == this.state.itemCard.cardTypeId) {
                this._newCardEditor(itemCardSelect, true);
                break;
            }
        }
    }

    _transferTo = () => {
        this.props.navigation.navigate('TransferToView', {
            itemCard: this.state.itemCard,
            onBack: this._onBackScreen
        });
    }

    _confirmAddFund = () => {

        if (Utils.isIOS()) {
            if (this.makingAddFun) {
                return;
            }

            this.makingAddFun = true;
            this._addFund();
        } else {
            this.popupDialogConfirmAddFund.showSlideAnimationDialog(i18n.t('CONFIRM_BUY_TOKEN'));
        }
    }

    _addFund = () => {
        if (!Utils.isValidString(JSON.stringify(this.state.publicPrivateKeys)) || this.state.publicPrivateKeys.publicKey != this.state.itemCard.publicKey) {
            Utils.showAlert(i18n.t('COMMON_PLEASE_INPORT_KEYS'), true, null);
            return;
        }
        console.log("MakeInAppPurchase", "MakeInAppPurchase");
        MakeInAppPurchase.makeTransaction((callback) => {
            console.log("MakeInAppPurchase", "callback");
            if (Utils.isIOS()) {
                this.makingAddFun = false;

                if (callback == "0") {
                    Utils.showAlert(i18n.t('MESSAGE_ADD_FUN_ERROR'), true, null);
                    return;
                }
            }

            GetPublicPrivateKey.getSignedPayload(
                this.state.itemCard.address,
                "9.9",
                this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt',
                "any reason to add funds",
                this.state.publicPrivateKeys.privateKey,
                (signPayload) => {

                    // alert(signPayload)
                    let paramsPlayLoad = {
                        [ConfigAPI.PARAM_ADDRESS_TO]: this.state.itemCard.address,
                        [ConfigAPI.PARAM_TX_PAYLOAD]: 9.9,
                        [ConfigAPI.PARAM_COIN_CODE]: this.state.itemCard.cardTypeId == Constants.BGX_BGT_ACCOUNT.BGX ? 'dec' : 'bgt',
                        [ConfigAPI.PARAM_REASON]: "any reason to add funds"
                    }
                    let params = {
                        [ConfigAPI.PARAM_METHOD]: ConfigAPI.API_ADD_FUND,
                        [ConfigAPI.PARAM_API_URL]: ConfigAPI.API_ADD_FUND,
                        [ConfigAPI.PARAM_PAYLOAD]: paramsPlayLoad,
                        [ConfigAPI.PARAM_SIGNED_PAYLOAD]: signPayload
                    };
                    console.log("API_ADD_FUND", JSON.stringify(params));
                    let balance = new BaseService();
                    balance.setParam(params);
                    balance.setCallback(this);
                    balance.requestDataPostGet(BaseService.METHOD.POST);
                }, (error) => {
                    Utils.showAlert(i18n.t('ERROR_KEY_CHILKAT'), true, null);
                }
            );
        });
    }

    _cancelFund = () => {
        if (Utils.isIOS()) {
            this.makingAddFun = false;
        }
    }

    _showTransactionsDetail = () => {
        this.props.navigation.navigate('TransactionDetailView');
    }
}

let appSize = Utils.appSize();
const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'center',
    },
    content: {
        flex: 1,
        position: "absolute",
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: "center",
        top: 0,
        paddingTop: Constants.HEIGHT_STATUS_BAR
    },
    background: {
        width: appSize.width,
        height: 150,
        position: 'absolute',
        resizeMode: 'cover',
        top: 0
    },
    header: {
        width: appSize.width,
        height: 45,
        flexDirection: 'row',
        alignItems: "center",
        justifyContent: 'center',
    },
    title: {
        fontSize: 25,
        textAlign: 'center',
        color: 'white',
    },
    header_right: {
        position: 'absolute',
        end: 10,
        flex: 1,
    },
    title_right: {
        fontSize: 16,
        color: 'white',
        textAlign: 'right',
        paddingTop: 10,
    },
    header_left: {
        position: 'absolute',
        left: 10,
    },
    name_card: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginBottom: 5
    },
    text_bgx_erc: {
        fontSize: 12,
        color: '#6d6d6d'
    },
    text_bgx_erc_value: {
        fontSize: 20,
    },
    content_card: {
        width: Utils.appSize().width - 20,
        flexDirection: 'column',
    },
    content_card_status: {
        flexDirection: 'column',
        width: Utils.appSize().width - 20,
        marginTop: Utils.isAndroid() ? 0 : 5
    },
    last_transactions: {
        fontSize: 12,
        marginTop: 10,
        marginBottom: 5,
        width: Utils.appSize().width - 20,
    },
    bottom: {
        width: appSize.width,
        flex: 1,
        position: 'absolute',
        bottom: 0,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
    },
    tab_bottom: {
        flex: 1,
        flexDirection: 'row',
    },
    text: {
        color: '#898989',
        fontSize: 12,
    },
    content_bgx_erc: {
        flex: 1,
        flexDirection: 'row',
        alignItems: "center",
        justifyContent: 'center',
    },
    content_bgx_balance: {
        flex: 1,
        flexDirection: 'row',
        alignItems: "flex-end",
    },
    card_item: {
        alignItems: 'center',
        width: appSize.width - 20,
    },
    list_transaction: {
        width: appSize.width - 20,
    },
    header_list_transaction: {
        flexDirection: 'row',
        width: Utils.appSize().width - 20,
        height: 45,
        alignItems: "center",
        paddingLeft: 10,
        paddingRight: 10
    },
    transaction_text: {
        fontSize: 14,
    },
    separator: {
        marginLeft: 10,
        marginRight: 10,
        height: 1,

    },
    wellet_no_card: {
        color: '#7d7d7d',
        fontSize: 25,
        paddingTop: Utils.isAndroid() ? 110 : 100,
        alignItems: "center",
        textAlign: 'center',
        justifyContent: 'center',
    },
    wellet_no_transactions: {
        color: '#7d7d7d',
        fontSize: 25,
        paddingTop: 30,
        alignItems: "center",
        textAlign: 'center',
        justifyContent: 'center',
    },
    view_content_picker: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: 10
    },
    dropdown: {
        alignSelf: 'flex-end',
        width: appSize.width - 100,
    },
    dropdown_text: {
        fontSize: 20,
        color: 'white',
        textAlign: 'center',
        textAlignVertical: 'center',
    },
    dropdown_dropdown: {
        borderColor: '#181818',
        borderWidth: 2,
        borderRadius: 10,
        width: appSize.width - 100,
        // height: 100,
    },
    dropdown_separator: {
        height: 1,
        backgroundColor: '#181818',
    },
    dropdown_row_text: {
        marginHorizontal: 4,
        fontSize: 20,
        color: 'black',
        textAlignVertical: 'center',
        textAlign: 'center',
        textAlignVertical: 'center',
        marginTop: 10,
        marginBottom: 10
    },
    icon_down: {
        position: 'absolute',
        right: 0,
    },
    line: {
        width: appSize.width - 100,
        height: 1
    },
    textCurrency: {
        fontSize: 18,
        fontWeight: '300',
        color: '#7d7d7d'
    },
});