/** @format */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import PushNotification from './src/common/PushNotification';
import ServiceAndroid from './src/common/ServiceAndroid';

PushNotification.setUp();
AppRegistry.registerComponent(appName, () => App);
AppRegistry.registerHeadlessTask('LockTaskService', () => ServiceAndroid);
