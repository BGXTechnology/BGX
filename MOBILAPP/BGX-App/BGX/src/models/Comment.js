import React, { Component } from "react";

export default class Comment extends Component {

    constructor(props) {
        super(props);

        if (props == null) {
            return;
        }

        this.reviewID = props["review_id"];
        this.createdAt = props["created_at"];
        this.title = props["title"];
        this.detail = props["detail"];
        this.nickname = props["nickname"];
        this.customerID = props["customer_id"];
        this.rating = "0";

        let ravitng_votes = props["ravitng_votes"];
        if (ravitng_votes == null || ravitng_votes.length == 0 || ravitng_votes[0] == null) {
            return;
        }

        this.rating = ravitng_votes[0].value;
    }
}