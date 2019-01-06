import { StyleSheet } from 'react-native';
import Utils from './Utils';

export let THEME_DARCK = false; // change theme of app
let appSize = Utils.appSize();
export let fontSize = 14;
export let viewCommonStyles = StyleSheet.create({
    common_button: {
        width: appSize.width - 50,
        height: 65,
    },
    common_button_dialog: {
        height: 50,
        width: (appSize.width - 50) / 2 - 40,
    },
    common_button_dialog_1: {
        height: THEME_DARCK ? 50 : 36,
        width: THEME_DARCK ? 132 : 120,
    }
});