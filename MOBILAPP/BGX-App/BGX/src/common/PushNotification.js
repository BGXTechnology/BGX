import Utils from './Utils';
import * as Constants  from './Constants';
import FCM, { FCMEvent, RemoteNotificationResult, WillPresentNotificationResult, NotificationType } from "react-native-fcm";
import { Alert, PushNotificationIOS, NativeModules, NativeEventEmitter, AsyncStorage } from 'react-native';

export default class PushNotification {

    static setUp = () => {
        FCM.requestPermissions();
        FCM.getFCMToken().then(token => {
            console.log("TOKEN (getFCMToken)", token);
            if (!Utils.isEmptyString(token)) {
                Utils.saveDataWithKey(Constants.KEY_PUSH_NOTIFICATION, token);
            }
            // this._onChangeToken(token);
        });

        //Get device token from network: receive device token from firebase(only one)
        FCM.on(FCMEvent.RefreshToken, (deviceToken) => {
            // fcm token may not be available on first load, catch it here
            console.log("Device token: refresh", deviceToken);
            if (!Utils.isEmptyString(deviceToken)) {
                Utils.saveDataWithKey(Constants.KEY_PUSH_NOTIFICATION, deviceToken);
            }
        });

        FCM.getInitialNotification().then(notif => {
            console.log("INITIAL NOTIFICATION", notif)
        });

        // this.notificationListener = FCM.on(FCMEvent.Notification, notif => {
        //     console.log("Notification : ", notif);
        //     // if (notif["local_notification"]) {
        //     //     return;
        //     // }
        //     // if (notif.opened_from_tray) {
        //     //     return;
        //     // }

        //     // this.sendLocalNotification(notif);
        // });

        this.refreshTokenListener = FCM.on(FCMEvent.RefreshToken, token => {
            // console.log("TOKEN (refreshUnsubscribe)", token);
            // this._onChangeToken(token);

            if (!Utils.isEmptyString(token)) {
                Utils.saveDataWithKey(Constants.KEY_PUSH_NOTIFICATION, token);
            }
        });
    }


    static sendLocalNotification = (data) => {
        // if (Utils.isIOS()) {
        //     var date = Utils.currentTime();
        //     date = Utils.dateByAddingSeconds(date, 5);
        //     let dateString = Utils.stringFromDate(date, define.DATE_FORMAT_IOS);
        //     var params = {};
        //     params["fireDate"] = dateString;
        //     params["alertBody"] = data.message;
        //     params["userInfo"] = data;
        //     PushNotificationIOS.scheduleLocalNotification(params);
        // } else if (Utils.isAndroid()) {
        //     console.log(`----------------------`);
        //     console.log(data);
        //     console.log(`---------------------- parse`);
        //     let jsonData = data.data;
        //     let body = JSON.parse(jsonData);
        //     console.log(body["body"]);
        //     this._handleNotification(body);

        // }

    }

    static _handleNotification = (notification) => {
        // let data = notification;
        // let message = data["body"];
        // try {
        //     FCM.presentLocalNotification({
        //         title: "",
        //         sound: "1",
        //         body: message,
        //         priority: "high",
        //         click_action: "OPEN_ACTIVITY",
        //         show_in_foreground: true,
        //         local: true
        //     });
        // } catch (error) {
        //     // Error retrieving data
        // }
    }
}