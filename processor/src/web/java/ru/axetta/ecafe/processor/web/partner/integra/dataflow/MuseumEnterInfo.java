/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by i.semenov on 01.06.2017.
 */
public class MuseumEnterInfo extends Result {
    public static final Integer MUSEUM_ENTER_TYPE_FREE = 0;
    public static final Integer MUSEUM_ENTER_TYPE_PAY = 1;

    private String orgName;
    private Integer enterType;

    public MuseumEnterInfo() {

    }

    public MuseumEnterInfo(Long resultCode, String description, String orgName, Integer enterType) {
        this.resultCode = resultCode;
        this.description = description;
        this.orgName = orgName;
        this.enterType = enterType;
    }


    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getEnterType() {
        return enterType;
    }

    public void setEnterType(Integer enterType) {
        this.enterType = enterType;
    }
}
