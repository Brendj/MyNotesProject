/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.05.13
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class GoodItem implements Comparable<GoodItem>{

    private Long globalId;
    private String pathPart2;
    private String pathPart3;
    private String pathPart4;
    private String fullName;

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
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
}
