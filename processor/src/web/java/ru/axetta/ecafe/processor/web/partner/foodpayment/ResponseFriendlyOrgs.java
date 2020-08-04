/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ResponseFriendlyOrgs extends Result {
    @JsonProperty("friendlyOrgs")
    private List<FriendlyOrgDTO> friendlyOrgs;

    public ResponseFriendlyOrgs(){

    }

    public ResponseFriendlyOrgs(List<FriendlyOrgDTO> friendlyOrgs){
        super(0, "OK");
        this.friendlyOrgs = friendlyOrgs;
    }

    public List<FriendlyOrgDTO> getFriendlyOrgs() {
        return friendlyOrgs;
    }

    public void setFriendlyOrgs(List<FriendlyOrgDTO> friendlyOrgs) {
        this.friendlyOrgs = friendlyOrgs;
    }
}
