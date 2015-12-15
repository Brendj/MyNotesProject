/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.05.13
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class GoodItem implements Comparable<GoodItem>{

    private Long globalId;
    private String pathPart1;
    private String pathPart2;
    private String pathPart3;
    private String pathPart4;
    private String parts[];
    private String fullName;
    private OrderTypeEnumType orderType;

    public GoodItem() {
        this.pathPart1 = "";
        this.pathPart2 = "";
        this.pathPart3 = "";
        this.pathPart4 = "";
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
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

    public String[] getParts() {
        return parts;
    }

    public void setParts(String[] parts) {
        if(parts.length>0) pathPart1 = parts[0];
        if(parts.length>1) pathPart2 = parts[1];
        if(parts.length>2) pathPart3 = parts[2];
        if(parts.length>3) pathPart4 = parts[3];
        this.parts = parts;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoodItem goodItem = (GoodItem) o;

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
    public int compareTo(GoodItem goodItem) {
        return goodItem.getFullName().compareTo(this.fullName);
    }

    public OrderTypeEnumType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderTypeEnumType orderType) {
        this.orderType = orderType;
    }
}
