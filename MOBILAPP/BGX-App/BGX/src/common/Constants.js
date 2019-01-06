import Utils from './Utils';

/******************* Data ***********************/
export let KEY_USER = "BGX_key_user";
export let KEY_PASSWORD = "BGX_password";
export let KEY_DATA_CARD_TYPE = "BGX_KEY_DATA_CARD_TYPE";
export let KEY_DATA_THEME = "BGX_KEY_DATA_THEME";
export let KEY_PUBLIC_PRIVATE = "BGX_KEY_PUBLIC_PRIVATE";
export let KEY_PUSH_NOTIFICATION = "BGX_KEY_PUSH_NOTIFICATION";


/******************* interface code ***********************/
export let WHITE = "#ffffff";
export let BLACK = "#000000";

export let INTERFACE_CODE_COLOR = "#ffffff";
export let INTERFACE_CODE_LANGUAGE = "en";
export let INTERFACE_CODE_LOGO = "uploads/interface/BGX_Logo_Black.png";
export let INTERFACE_CODE_TEXT = "The place where people can come to find and discover anything they might want to buy online";
export let OPPOSITE_TEXT_COLOR = "#000000";
export let OPACITY = 0.5;


/******************* value ***********************/
export const HEIGHT_STATUS_BAR = Utils.isIOS() ? 18 : 0;
export const HEIGHT_BOTTOM = Utils.isIOS() ? 50 : 70;
export const HEIGHT_PADDING_TOP_STATUS = Utils.isAndroid() ? 18 : 0;

export const BGX_BGT_ACCOUNT = {
    BGX: 1,
    BGT: 2
}