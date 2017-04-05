/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.model;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by i.semenov on 04.04.2017.
 */
public class OrgDeliveryInfo {
    private Long idOfOrg;
    private String orgShortName;
    private Date sendDate;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(BigInteger idOfOrg) {
        this.idOfOrg = idOfOrg.longValue();
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(BigInteger sendDate) {
        this.sendDate = sendDate == null ? null : new Date(sendDate.longValue());
    }
}
