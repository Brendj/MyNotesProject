/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingItemSyncPOJO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GoodRequestEZDSyncPOJO {
    private Integer idOfOrg;
    private String guid;
    private String groupName;
    private Date date;
    private Integer complexId;
    private String complexName;
    private Integer count;
    private String user;
    private Long version;

    public GoodRequestEZDSyncPOJO() {
    }


    public Element toElement(Document document, DateFormat timeFormat) throws Exception {
        Element element = document.createElement("GR");
        if (idOfOrg != null)
            element.setAttribute("IdOfOrg", idOfOrg.toString());
        if (guid != null)
            element.setAttribute("Guid", guid);
        if (groupName != null)
            element.setAttribute("GroupName", groupName);
        if (date != null)
            element.setAttribute("Date", timeFormat.format(date));
        if (complexId != null)
            element.setAttribute("ComplexId", complexId.toString());
        if (complexName != null)
            element.setAttribute("ComplexName", complexName);
        if (count != null)
            element.setAttribute("Count", count.toString());
        if (user != null)
            element.setAttribute("User", user);
        if (version != null)
            element.setAttribute("V", version.toString());
        return element;
    }


    public Integer getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Integer idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getComplexId() {
        return complexId;
    }

    public void setComplexId(Integer complexId) {
        this.complexId = complexId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
