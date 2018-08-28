/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import java.util.LinkedList;
import java.util.List;

public class JsonChildrenDataInfoItem {
    private String firsName;
    private String secondName;
    private String surName;
    private Long contractID;
    private String groupName;
    private List<JsonSmartWatchInfo> smartWatchInfoList;


    public String getFirsName() {
        return firsName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public Long getContractID() {
        return contractID;
    }

    public void setContractID(Long contractID) {
        this.contractID = contractID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<JsonSmartWatchInfo> getSmartWatchInfoList() {
        if(this.smartWatchInfoList == null){
            this.smartWatchInfoList = new LinkedList<JsonSmartWatchInfo>();
        }
        return smartWatchInfoList;
    }

    public void setSmartWatchInfoList(List<JsonSmartWatchInfo> smartWatchInfoList) {
        this.smartWatchInfoList = smartWatchInfoList;
    }
}