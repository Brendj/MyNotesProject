/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingItemSyncPOJO;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EMIASSyncPOJO {
    private String guid;
    private Long idEventEMIAS;
    private Long typeEventEMIAS;
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;
    private Date createDate;
    private Date updateDate;
    private Boolean accepted;
    private Long deletedemiasid;
    private Long version;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("guid", guid);
        element.setAttribute("idEventEMIAS",idEventEMIAS.toString());
        element.setAttribute("typeEventEMIAS",typeEventEMIAS.toString());
        element.setAttribute("dateLiberate", CalendarUtils.dateShortToStringFullYear(dateLiberate));
        element.setAttribute("startDateLiberate",CalendarUtils.dateShortToStringFullYear(startDateLiberate));
        element.setAttribute("endDateLiberate",CalendarUtils.dateShortToStringFullYear(endDateLiberate));
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
        element.setAttribute("deletedemiasid", deletedemiasid == null ? "" : deletedemiasid.toString());
        element.setAttribute("version", version == null ? "-1" : version.toString());
        return element;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdEventEMIAS() {
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(Long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }

    public Long getTypeEventEMIAS() {
        return typeEventEMIAS;
    }

    public void setTypeEventEMIAS(Long typeEventEMIAS) {
        this.typeEventEMIAS = typeEventEMIAS;
    }

    public Date getDateLiberate() {
        return dateLiberate;
    }

    public void setDateLiberate(Date dateLiberate) {
        this.dateLiberate = dateLiberate;
    }

    public Date getStartDateLiberate() {
        return startDateLiberate;
    }

    public void setStartDateLiberate(Date startDateLiberate) {
        this.startDateLiberate = startDateLiberate;
    }

    public Date getEndDateLiberate() {
        return endDateLiberate;
    }

    public void setEndDateLiberate(Date endDateLiberate) {
        this.endDateLiberate = endDateLiberate;
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

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Long getDeletedemiasid() {
        return deletedemiasid;
    }

    public void setDeletedemiasid(Long deletedemiasid) {
        this.deletedemiasid = deletedemiasid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
