/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 23.02.11
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ComplexInfoDetail {

    private Long idOfComplexInfoDetail;
    private ComplexInfo complexInfo;
    private MenuDetail menuDetail;
    private Long idOfItem;
    private Integer count;

    protected ComplexInfoDetail() {

    }

    public ComplexInfoDetail(ComplexInfo complexInfo, MenuDetail menuDetail) {
        this.complexInfo = complexInfo;
        this.menuDetail = menuDetail;
    }

    public MenuDetail getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(MenuDetail menuDetail) {
        this.menuDetail = menuDetail;
    }

    public ComplexInfo getComplexInfo() {
        return complexInfo;
    }

    public void setComplexInfo(ComplexInfo complexInfo) {
        this.complexInfo = complexInfo;
    }

    public Long getIdOfComplexInfoDetail() {
        return idOfComplexInfoDetail;
    }

    public void setIdOfComplexInfoDetail(Long idOfComplexInfoDetail) {
        this.idOfComplexInfoDetail = idOfComplexInfoDetail;
    }

    public Long getIdOfItem() {
        return idOfItem;
    }

    public void setIdOfItem(Long idOfItem) {
        this.idOfItem = idOfItem;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComplexInfoDetail that = (ComplexInfoDetail) o;

        if (idOfComplexInfoDetail != that.idOfComplexInfoDetail) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfComplexInfoDetail ^ (idOfComplexInfoDetail >>> 32));
    }

    @Override
    public String toString() {
        return "ComplexInfoDetail{" + "idOfComplexInfoDetail=" + idOfComplexInfoDetail + ", complexInfo=" + complexInfo
                + ", menuDetail=" + menuDetail + ", count=" + count + '}';
    }
}
