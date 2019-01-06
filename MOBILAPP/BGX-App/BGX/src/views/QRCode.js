'use strict';
import React, { Component } from 'react';
import {
  StyleSheet,
  View,
  TouchableOpacity,
  Image,
  Text,
  Alert
} from 'react-native';

import { QRScannerView } from 'ac-qrcode';
import Utils from "../common/Utils";
import Permissions from 'react-native-permissions';

import i18n from '../translations/i18n';

export default class QRCode extends Component {

    constructor(props) {
        super(props);

        this.isGetData = false;
        this.isInView = false;
        this.isShowPermission = false;
        this.permissionInterval = null;
    }

    static navigationOptions = ({ navigation }) => ({
        header: null
      });

    componentDidMount() {

        this.isInView = true;
        this.permissionInterval = setInterval(() => { 
            this.checkPermission(); 
        }, 1000);
    }

    componentWillUnmount = () => {
        this.isInView = false;
        clearInterval(this.permissionInterval);
        this.permissionInterval = null;
    }

    checkPermission = () => {
        Permissions.check('camera').then(response => {
            // Response is one of: 'authorized', 'denied', 'restricted', or 'undetermined'
            if (Utils.isIOS()) {
                
                if (response === 'authorized' || response === 'undetermined') {
                    ImagePicker.launchCamera(options, (response) => {
                        if (response.didCancel) {
                            //   console.log('User cancelled image picker');
                        }
                        else if (response.error) {
                            //   console.log('ImagePicker Error: ', response.error);
                        }
                        else if (response.customButton) {
                            //   console.log('User tapped custom button: ', response.customButton);
                        }
                        else {
                            this.isSelectPhoto = true;
                            let source = { uri: 'data:image/jpeg;base64,' + response.data };
                            this.setState({
                                avatarSource: source
                            });
                            // this.isSelectPhoto = false;
                        }
                    });
                } else {

                    if (this.isShowPermission) {
                        clearInterval(this.permissionInterval);
                        return;
                    }
    
                    this.isShowPermission = true;
                    
                    Alert.alert(
                        '',
                        i18n.t('MESSAGE_CAMERA_NOT_AUTHORIZED'),
                        [
                            { text: i18n.t('OK'), onPress: () => this._cancelAlertPermisson() },
                        ],
                        { cancelable: false }
                    );

                    // Alert.alert(
                    //     '',
                    //     i18n.t('MESSAGE_ALLOW_CAMERA_TO_TAKE_PHOTO'),
                    //     [
                    //         { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                    //         { text: i18n.t('CANCEL') },
                    //     ],
                    //     { cancelable: false }
                    // );
                }
            } else {
                if (response === 'authorized') {
                    ImagePicker.launchCamera(options, (response) => {
                        if (response.didCancel) {
                            //   console.log('User cancelled image picker');
                        }
                        else if (response.error) {
                            //   console.log('ImagePicker Error: ', response.error);
                        }
                        else if (response.customButton) {
                            //   console.log('User tapped custom button: ', response.customButton);
                        }
                        else {
                            this.isSelectPhoto = true;
                            let source = { uri: 'data:image/jpeg;base64,' + response.data };
                            this.setState({
                                avatarSource: source
                            });
                            // this.isSelectPhoto = false;
                        }
                    });
                } else {

                    if (this.isShowPermission) {
                        clearInterval(this.permissionInterval);
                        return;
                    }
    
                    this.isShowPermission = true;

                    Alert.alert(
                        '',
                        i18n.t('MESSAGE_CAMERA_NOT_AUTHORIZED'),
                        [
                            { text: i18n.t('OK'), onPress: () => this._cancelAlertPermisson() },
                        ],
                        { cancelable: false }
                    );

                    // Alert.alert(
                    //     '',
                    //     i18n.t('MESSAGE_ALLOW_CAMERA_TO_SCAN_QR'),
                    //     [
                    //         { text: i18n.t('OK'), onPress: () => this._goToSettingFunction() },
                    //         { text: i18n.t('CANCEL'), onPress: () => this._cancelAlertPermisson() },
                    //     ],
                    //     { cancelable: false }
                    // );
                }
            }
            
        });
    }

    _goToSettingFunction = () => {
        this.isShowPermission = false;
        CheckCameraAndPhotoHelper.goToCameraPhotoSetting((status) => {
        });
    }

    _cancelAlertPermisson = () => {
        this.props.navigation.goBack();
        this.isShowPermission = false;
    }

    backButton_clicked = () => {
        this.props.navigation.goBack();
    }

    barcodeReceived = (e) => {
        if (!this.isGetData) {
            this.props.navigation.state.params.getAddress({ address: e.data });
            this.props.navigation.goBack();
            this.isGetData = true;
        }
    }

    _renderTitleBar(){
        return(
            <View style={styles.containView}>
                <Text style={styles.title}>{i18n.t('SCAN_QR_CODE')}</Text>
                <TouchableOpacity style={styles.button} onPress={this.backButton_clicked}>
                    <Image style={styles.imageButton} source={require("../resource/icon_back_white.png")}/>
                </TouchableOpacity>
            </View>
        );
    }

    _renderMenu() {
        return (
            null
        )
    }
      
    render() {
        return (
            < QRScannerView
                onScanResultReceived={this.barcodeReceived.bind(this)}
                renderTopBarView={() => this._renderTitleBar()}
                renderBottomMenuView={() => this._renderMenu()}
                hintText=''
            />
        )
    }
}

const styles = StyleSheet.create({

    containView: {
        flexDirection: 'row',
        alignItems: 'center',
        marginTop: 20,
        height: 40,
        zIndex: 1,
        // position: 'absolute'
    },
    title: {
        color:'white',
        textAlignVertical:'center', 
        textAlign:'center', 
        fontSize:20, 
        flex: 1, 
        fontWeight: '400'
    },
    button: {
        marginLeft: 10, 
        width: 40, 
        height: 40, 
        justifyContent: 'center', 
        position: 'absolute',
        zIndex: 1
    }, 
    imageButton: {
        width: 22, 
        height: 14, 
        alignSelf: 'center',
        zIndex: 1
    }
});
