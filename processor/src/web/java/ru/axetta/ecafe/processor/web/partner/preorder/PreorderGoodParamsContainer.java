/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

public class PreorderGoodParamsContainer {
    private Integer goodType;
    private Integer ageGroup;

    public PreorderGoodParamsContainer(Integer goodType, Integer ageGroup){
        this.ageGroup = ageGroup;
        this.goodType = goodType;
    }

    public Integer getGoodType() {
        return goodType;
    }

    public void setGoodType(Integer goodType) {
        this.goodType = goodType;
    }

    public Integer getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(Integer ageGroup) {
        this.ageGroup = ageGroup;
    }
}
