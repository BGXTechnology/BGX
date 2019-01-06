"use strict";
import React, { Component } from "react";

export default class CategoryItem extends Component {

    constructor(props) {
        super(props);
        if (props == null) {
            return;
        }

        this.categoryID = props["id"];
        this.categoryName = props["name"];
        this.isSelected = false;
    }

}