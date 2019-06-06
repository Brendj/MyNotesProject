/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.ArrayList;

/**
 * Created by a.Voinov on 06.06.2019.
 */
public class CultureEnterInfo extends Result {

    private String validityCard;
    private ArrayList<String> child = new ArrayList<>();
    private String guid;
    private String fullAge;
    private String groupName;

    public CultureEnterInfo() {
    }

    public CultureEnterInfo(Long resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public String isValidityCard() {
        return validityCard;
    }

    public void setValidityCard(String validityCard) {
        this.validityCard = validityCard;
    }

    public String isFullAge() {
        return fullAge;
    }

    public void setFullAge(String fullAge) {
        this.fullAge = fullAge;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getChild() {
        return child;
    }

    public void setChild(ArrayList<String> child) {
        this.child = child;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
