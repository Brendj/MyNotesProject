/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalsales;

import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: regal
 * Date: 25.01.15
 * Time: 0:03
 */
public class TotalSalesItem {
    private String name;
    private String disctrict;
    private String date;
    private long sum;
    private String type;
    private Boolean count;
    private String ageGroup;
    private List<Long> idOfClientList;
    private int uniqueClientCount;


    public TotalSalesItem(String name,String disctrict , String date, long sum, String type, String ageGroup, Boolean count) {
        this.name = name;
        this.disctrict = disctrict;
        this.date = date;
        this.sum = sum;
        this.type = type;
        this.ageGroup = ageGroup;
        this.idOfClientList = new ArrayList<Long>();
        this.uniqueClientCount = 0;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisctrict() {
        return disctrict;
    }

    public void setDisctrict(String disctrict) {
        this.disctrict = disctrict;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSum() {
        return sum;
    }

    public String getSummToString() {
        return CurrencyStringUtils.copecksToRubles(sum);
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public List<Long> getIdOfClientList() {
        return idOfClientList;
    }

    public void setIdOfClientList(List<Long> idOfClientList) {
        this.idOfClientList = idOfClientList;
    }

    public int getUniqueClientCount() {
        return uniqueClientCount;
    }

    public void setUniqueClientCount(int uniqueClientCount) {
        this.uniqueClientCount = uniqueClientCount;
    }

    public Boolean getCount() {
        return count;
    }

    public void setCount(Boolean count) {
        this.count = count;
    }
}
