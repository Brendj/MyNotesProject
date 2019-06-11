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

    public PreorderRequestsReportServiceParam(Date date) {
        idOfOrgList = new ArrayList<>();
        idOfClientList = new ArrayList<>();
        this.date = date;
    }

    public boolean isEmpty() {
        return idOfOrgList.isEmpty() && idOfClientList.isEmpty();
    }

    public String getJPACondition() {
        String result = "";
        if (!idOfOrgList.isEmpty()) result = String.format(" and pc.idOfOrgOnCreate in (%s) ", StringUtils.join(idOfOrgList, ','));
        if (!idOfClientList.isEmpty()) result += String.format(" and pc.client.idOfClient in (%s) ", StringUtils.join(idOfClientList, ','));
        return result;
    }

    public String getOrgJPACondition(String alias) {
        if (!idOfOrgList.isEmpty()) {
            return String.format(" and %s.idOfOrg in (%s) ", alias, StringUtils.join(idOfOrgList, ','));
        }
        if (!idOfClientList.isEmpty()) {
            return String.format(" and %s.idOfOrg in (select distinct c.org.idOfOrg from Client c where c.idOfClient in (%s)) ", alias, StringUtils.join(idOfClientList, ','));
        }
        return "";
    }

    public String getNativeSQLCondition() {
        String result = "";
        if (!idOfOrgList.isEmpty()) result = String.format(" and pc.idOfOrgOnCreate in (%s) ", StringUtils.join(idOfOrgList, ','));
        if (!idOfClientList.isEmpty()) result += String.format(" and pc.idOfClient in (%s) ", StringUtils.join(idOfClientList, ','));
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
}
