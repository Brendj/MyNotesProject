/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 14.05.2019.
 */
public class PreorderRequestsReportServiceParam {
    private final List<Long> idOfOrgList;
    private final List<Long> idOfClientList;
    private final Date date;
    private Integer modBy;
    private Integer serversAmount;

    public PreorderRequestsReportServiceParam(Date date) {
        idOfOrgList = new ArrayList<>();
        idOfClientList = new ArrayList<>();
        this.date = date;
        modBy = null;
        serversAmount = null;
    }

    public boolean isEmpty() {
        return idOfOrgList.isEmpty() && idOfClientList.isEmpty();
    }

    public String getJPACondition() {
        String result = "";
        if (!idOfOrgList.isEmpty()) result = String.format(" and pc.idOfOrgOnCreate in (%s) ", StringUtils.join(idOfOrgList, ','));
        if (!idOfClientList.isEmpty()) result += String.format(" and pc.client.idOfClient in (%s) ", StringUtils.join(idOfClientList, ','));
        if (modBy != null && serversAmount != null) result += String.format(" and mod(pc.idOfOrgOnCreate, %s) = %s", serversAmount, modBy);
        return result;
    }

    public String getOrgJPACondition(String alias) {
        if (!idOfOrgList.isEmpty()) {
            return String.format(" and %s.idOfOrg in (%s) ", alias, StringUtils.join(idOfOrgList, ','));
        }
        if (!idOfClientList.isEmpty()) {
            return String.format(" and %s.idOfOrg in (select distinct c.org.idOfOrg from Client c where c.idOfClient in (%s)) ", alias, StringUtils.join(idOfClientList, ','));
        }
        if (modBy != null && serversAmount != null) return String.format(" and mod(%s.idOfOrg, %s) = %s", alias, serversAmount, modBy);
        return "";
    }

    public String getNativeSQLCondition() {
        String result = "";
        if (!idOfOrgList.isEmpty()) result = String.format(" and pc.idOfOrgOnCreate in (%s) ", StringUtils.join(idOfOrgList, ','));
        if (!idOfClientList.isEmpty()) result += String.format(" and pc.idOfClient in (%s) ", StringUtils.join(idOfClientList, ','));
        if (modBy != null && serversAmount != null) result += String.format(" and mod(pc.idOfOrgOnCreate, %s) = %s", serversAmount, modBy);
        return result;
    }

    public String getRegularPreorderJPACondition() {
        String result = "";
        if (modBy != null && serversAmount != null) result += String.format(" and mod(c.org.idOfOrg, %s) = %s", serversAmount, modBy);
        return result;
    }

    public String getWtMenuJPACondition() {
        String result = "";
        if (modBy != null && serversAmount != null) result += String.format(" and mod(o.idOfOrg, %s) = %s", serversAmount, modBy);
        return result;
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public List<Long> getIdOfClientList() {
        return idOfClientList;
    }

    public Date getDate() {
        return date;
    }

    public Integer getModBy() {
        return modBy;
    }

    public Integer getServersAmount() {
        return serversAmount;
    }

    public void setModBy(Integer modBy) {
        this.modBy = modBy;
    }

    public void setServersAmount(Integer serversAmount) {
        this.serversAmount = serversAmount;
    }
}
