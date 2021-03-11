/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ExemptionVisitingSyncPOJO {
    private Long idExemption;
    private Boolean archive;
    private Integer hazard_level_id;

    private String meshguid;
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;
    private Date createDate;
    private Date updateDate;
    private Long version;
    private Boolean accepted;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idExemption", idExemption.toString());
        element.setAttribute("archive", archive == null ? "false" : archive.toString());
        element.setAttribute("hazard_level_id", hazard_level_id == null ? "0" : hazard_level_id.toString());
        element.setAttribute("meshguid", meshguid);
        element.setAttribute("startDateLiberate",CalendarUtils.dateShortToStringFullYear(startDateLiberate));
        element.setAttribute("endDateLiberate",CalendarUtils.dateShortToStringFullYear(endDateLiberate));
        element.setAttribute("dateLiberate", CalendarUtils.dateTimeToString(dateLiberate));
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

    public Long getIdExemption() {
        return idExemption;
    }

    public void setIdExemption(Long idExemption) {
        this.idExemption = idExemption;
    }

    public String getMeshguid() {
        return meshguid;
    }

    public void setMeshguid(String meshguid) {
        this.meshguid = meshguid;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public Integer getHazard_level_id() {
        return hazard_level_id;
    }

    public void setHazard_level_id(Integer hazard_level_id) {
        this.hazard_level_id = hazard_level_id;
    }
}
