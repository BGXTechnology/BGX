"use strict";
import React, { Component } from "react";
import Utils from '../common/Utils';

export default class Item extends Component {

    static ITEM_PRODUCT_TYPE = {
        DIGITAL_SPOT: 'DIGITAL_SPOT',
        FAVORITE: 'FAVORITE',
        MY_ORDER: 'MY_ORDER'
    }

    constructor(props, type) {
        super(props);

        if (props == null) {
            return;
        }

        this.id                 = "";
        this.sku                = "";
        this.name               = "";
        this.price              = "";
        this.rating             = 0;

        this.thumbnail          = "";
        this.shortDescription   = "";
        this.subject            = "";
        this.provider           = "";
        this.author             = "";
        this.description        = "";
        this.itemInfo           = "";
        this.instruction        = "";
        this.arrayImage         = [];
        this.isFavorite         = false;
        this.wishListItemID     = "";
        this.statusItem         = "";
        this.updateAt           = "";
        this.productURL         = "";

        this.isBought           = false;

        if (type === Item.ITEM_PRODUCT_TYPE.DIGITAL_SPOT) {
            this.id                 = props["id"];
            this.sku                = props["sku"];
            this.name               = props["name"];
            this.price              = Utils.formatPrice(props["price"]);

            let customAttributes = props["custom_attributes"];
            
            for (let item of customAttributes) {
                if (item.attribute_code == "thumbnail") {
                    this.thumbnail = item.value; 
                }

                if (item.attribute_code == "short_description") {
                    this.shortDescription = item.value;    
                }

                if (item.attribute_code == "subject") {
                    this.subject = item.value;
                }

                if (item.attribute_code == "provider") {
                    this.provider = item.value;
                }

                if (item.attribute_code == "author") {
                    this.author = item.value;
                }

                if (item.attribute_code == "description") {
                    this.description = item.value;
                    
                }

                if (item.attribute_code == "item_info") {
                    this.itemInfo = item.value;
                }

                if (item.attribute_code == "instruction") {
                    this.instruction = item.value;
                }

                if (item.attribute_code == "url_key") {
                    this.productURL = item.value + ".html";
                }
            }

            let mediaGalleryEntries = props["media_gallery_entries"];
            if (mediaGalleryEntries != null) {
                for (let media of mediaGalleryEntries) {
                    let file = media["file"];
                    if (file != null && file != "") {
                        this.arrayImage.push(file);
                    }
                }
            }
        } else if (type === Item.ITEM_PRODUCT_TYPE.FAVORITE) {
            this.id                 = props["product_id"];

            let product = props["product"];
                this.sku                = product["sku"];
                this.name               = product["name"];
                this.price              = Utils.formatPrice(product["price"]);

                this.thumbnail          = product["thumbnail"];
                this.shortDescription   = product["short_description"];
                this.subject            = product["subject"];
                this.provider           = product["provider"];
                this.author             = product["author"];

            this.wishListItemID     = props["wishlist_item_id"];
        } else if (type === Item.ITEM_PRODUCT_TYPE.MY_ORDER) {
            this.id                 = props["product_id"];
            this.sku                = props["sku"];
            this.name               = props["name"];
            this.price              = Utils.formatPrice(props["price"]);
        }
    }
}