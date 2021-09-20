/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.ArrayList;

/**
 * Created by a.Voinov on 06.06.2019.
 */
public class CultureEnterInfo extends Result {

    private Boolean validityCard;
    private ArrayList<CultureEnterInfo> child;
    private ArrayList<CultureEnterInfo> childrens;
    private String guid;
    private String mesId;
    private Boolean fullAge;
    private String groupName;

    public CultureEnterInfo() {
    }

    public CultureEnterInfo(Long resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public Boolean isValidityCard() {
        return validityCard;
    }

    public void setValidityCard(Boolean validityCard) {
        this.validityCard = validityCard;
    }

    public Boolean isFullAge() {
        return fullAge;
    }

    public void setFullAge(Boolean fullAge) {
        this.fullAge = fullAge;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<CultureEnterInfo> getChild() {
        if (child == null)
            child = new ArrayList<>();
        return child;
    }

    public void setChild(ArrayList<CultureEnterInfo> child) {
        this.child = child;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public ArrayList<CultureEnterInfo> getChildrens() {
        if (childrens == null)
            childrens = new ArrayList<>();
        return childrens;
    }

    public void setChildrens(ArrayList<CultureEnterInfo> childrens) {
        this.childrens = childrens;
    }

    public String getMesId() {
        return mesId;
    }

    public void setMesId(String mesId) {
        this.mesId = mesId;
    }
}
