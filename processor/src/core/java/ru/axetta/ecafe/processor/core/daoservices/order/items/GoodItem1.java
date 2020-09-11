/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.05.13
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class GoodItem1 implements Comparable<GoodItem1>{

    private Long globalId;
    private String pathPart1;
    private String pathPart2;
    private String pathPart3;
    private String pathPart4;
    private String fullName;
    private Long price;
    private Integer modeOfAdd;

    public GoodItem1() {
        this.pathPart1 = "";
        this.pathPart2 = "";
        this.pathPart3 = "";
        this.pathPart4 = "";
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(BigInteger globalId) {
        this.globalId = globalId.longValue();
    }

    public String getPathPart1() {
        return pathPart1;
    }

    public void setPathPart1(String pathPart1) {
        this.pathPart1 = pathPart1;
    }

    public String getPathPart2() {
        return pathPart2;
    }

    public void setPathPart2(String pathPart2) {
        this.pathPart2 = pathPart2;
    }

    public String getPathPart3() {
        return pathPart3;
    }

    public void setPathPart3(String pathPart3) {
        this.pathPart3 = pathPart3;
    }

    public String getPathPart4() {
        return pathPart4;
    }

    public void setPathPart4(String pathPart4) {
        this.pathPart4 = pathPart4;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoodItem1 goodItem = (GoodItem1) o;

        if (!fullName.equals(goodItem.fullName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    @Override
    public int compareTo(GoodItem1 goodItem) {
        return goodItem.getFullName().compareTo(this.fullName);
    }

    public Integer getModeOfAdd() {
        return modeOfAdd;
    }

    public void setModeOfAdd(Integer modeOfAdd) {
        this.modeOfAdd = modeOfAdd;
    }
}
