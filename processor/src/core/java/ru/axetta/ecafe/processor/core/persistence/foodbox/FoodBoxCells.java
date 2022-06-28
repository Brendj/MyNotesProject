/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

public class FoodBoxCells {
    private Long foodboxesid;
    private Integer fbId;
    private Integer totalcellscount;
    private Integer busycells;
    private Org org;
    private Date createDate;
    private Date updateDate;

    public FoodBoxCells()
    {
        updateDate = new Date();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getFoodboxesid() {
        return foodboxesid;
    }

    public void setFoodboxesid(Long foodboxesid) {
        this.foodboxesid = foodboxesid;
    }

    public Integer getFbId() {
        return fbId;
    }

    public void setFbId(Integer fbId) {
        this.fbId = fbId;
    }

    public Integer getTotalcellscount() {
        return totalcellscount;
    }

    public void setTotalcellscount(Integer totalcellscount) {
        this.totalcellscount = totalcellscount;
    }

    public Integer getBusycells() {
        return busycells;
    }

    public void setBusycells(Integer busycells) {
        this.busycells = busycells;
    }
}
