import React, { Component } from 'react';
import { createStackNavigator, createDrawerNavigator, createBottomTabNavigator } from 'react-navigation';
import { Dimensions, Image } from 'react-native';
import Root from './src/views/Root';
import Welcome from './src/views/Welcome';
import Login from './src/views/Login';
import Register from './src/views/Register';
import Profile from './src/views/Profile';
import BGXWallet from './src/views/BGXWallet';
import BGXWalletDetail from './src/views/BGXWalletDetail';
import DigitalSpot from './src/views/DigitalSpot';
import DontMissOut from './src/views/DontMissOut';
import ProductDetail from './src/views/ProductDetail';
import QRCode from './src/views/QRCode';
import ChangePassword from './src/views/ChangePassword';
import Menu from './src/views/Menu';
import MyOrders from './src/views/MyOrders';
import ShoppingCart from './src/views/ShoppingCart';
import CheckingOut from './src/views/CheckingOut';
import PIN from './src/views/PIN';
import Receipt from './src/views/Receipt';
import TransactionDetail from './src/views/TransactionDetail';
import Favorites from './src/views/Favorites';
import WebViewComponent from './src/components/WebViewComponent';
import BGXWalletCardEditor from './src/views/BGXWalletCardEditor';
import ForgotPassword from './src/views/ForgotPassword';
import TransferTo from './src/views/TransferTo';

const MyStacKNavigator = createStackNavigator({
  RootView: {
    screen: Root, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },

  ProfileView: {
    screen: Profile, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  BGXWalletView: {
    screen: BGXWallet, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed',
    })
  },
  BGXWalletDetailView: {
    screen: BGXWalletDetail, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  BGXWalletCardEditorView: {
    screen: BGXWalletCardEditor, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  DigitalSpotView: {
    screen: DigitalSpot, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  ProductDetailView: {
    screen: ProductDetail, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  DontMissOutView: {
    screen: DontMissOut, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  QRCodeView: {
    screen: QRCode, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  ChangePasswordView: {
    screen: ChangePassword, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  MyOrdersView: {
    screen: MyOrders, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  ShoppingCartView: {
    screen: ShoppingCart, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  CheckingOutView: {
    screen: CheckingOut, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  PINView: {
    screen: PIN, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  ReceiptView: {
    screen: Receipt, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  FavoritesView: {
    screen: Favorites, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  TransactionDetailView: {
    screen: TransactionDetail, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  TransferToView: {
    screen: TransferTo, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },

},
  {
    // initialRouteName: 'WelcomeView',
    transitionConfig: (toTransitionProps, fromTransitionProps) => {
      if (fromTransitionProps == null) {
        return;
      }
      var fromName = fromTransitionProps.scene.route.routeName;
      var toName = toTransitionProps.scene.route.routeName;
      // // const isBack = fromTransitionProps.navigation.state.index >= toTransitionProps.navigation.state.index;
      // const routeName = isBack ? fromTransitionProps.scene.route.routeName : toTransitionProps.scene.route.routeName;
      if (fromName === 'RootView') {
        return {
          transitionSpec: { duration: 0 }
        }
      }
    }
  });

const WelcomeNavigator = createStackNavigator({
  WelcomeView: {
    screen: Welcome, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  LoginView: {
    screen: Login, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  RegisterView: {
    screen: Register, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  WebViewComponentView: {
    screen: WebViewComponent, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  },
  ForgotPasswordView :{
    screen: ForgotPassword, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed'
    })
  }
},
  {

  });

const MyDrawerNavigator = createDrawerNavigator(
  {
    Main: { screen: MyStacKNavigator },
  },
  {
    contentComponent: Menu,
    drawerWidth: Dimensions.get('window').width > 400 ? 350 : Dimensions.get('window').width * 0.9,
  }
);

const RootNavigator = createStackNavigator({
  Main: {
    screen: MyDrawerNavigator, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed',
      header: null
    })
  },
  Welcome: {
    screen: WelcomeNavigator, navigationOptions: ({ navigation }) => ({
      drawerLockMode: 'locked-closed',
      header: null
    })
  }
},
  {

  });



export default RootNavigator;