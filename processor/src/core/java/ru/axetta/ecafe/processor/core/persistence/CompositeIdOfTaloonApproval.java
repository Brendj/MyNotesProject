/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfTaloonApproval implements Serializable {
    private Long idOfOrg;
    private Date taloonDate;
    private String taloonName;
    private String goodsGuid;

    protected CompositeIdOfTaloonApproval() {
        //for Hibernate only
    }

    public CompositeIdOfTaloonApproval(Long idOfOrg, Date taloonDate, String taloonName, String goodsGuid) {
        this.setIdOfOrg(idOfOrg);
        this.setTaloonDate(taloonDate);
        this.setTaloonName(taloonName);
        this.setGoodsGuid(goodsGuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfTaloonApproval)) {
            return false;
        }
        final CompositeIdOfTaloonApproval that = (CompositeIdOfTaloonApproval) o;
        return idOfOrg.equals(that.getIdOfOrg()) && taloonDate.equals(that.getTaloonDate()) && taloonName.equals(that.getTaloonName());
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public String getTaloonName() {
        return taloonName;
    }

    public void setTaloonName(String taloonName) {
        this.taloonName = taloonName;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
    }
}
