/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ExemptionVisitingSyncPOJO {
    private String meshguid;
    private Long idEMIAS;
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;
    private Date createDate;
    private Date updateDate;
    private Boolean accepted;
    private Long version;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("meshguid", meshguid);
        element.setAttribute("idEMIAS",idEMIAS.toString());
        element.setAttribute("dateLiberate", CalendarUtils.dateTimeToString(dateLiberate));
        element.setAttribute("startDateLiberate",CalendarUtils.dateShortToStringFullYear(startDateLiberate));
        element.setAttribute("endDateLiberate",CalendarUtils.dateShortToStringFullYear(endDateLiberate));
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
        element.setAttribute("version", version == null ? "-1" : version.toString());
        return element;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdEMIAS() {
        return idEMIAS;
    }

    public void setIdEMIAS(Long idEMIAS) {
        this.idEMIAS = idEMIAS;
    }

    public String getMeshguid() {
        return meshguid;
    }

    public void setMeshguid(String meshguid) {
        this.meshguid = meshguid;
    }
}
