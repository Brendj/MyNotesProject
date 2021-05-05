/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 */
public class EMIAS {

    private Long idOfEMIAS;
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
    private Boolean kafka;
    private Boolean archive;
    private Integer hazard_level_id;
    private Boolean processed;
    private Set<EMIASbyDay> daySet;

   public EMIAS(){}


    public Long getIdOfEMIAS() {
        return idOfEMIAS;
    }

    public void setIdOfEMIAS(Long idOfEMIAS) {
        this.idOfEMIAS = idOfEMIAS;
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

    public Boolean getKafka() {
        return kafka;
    }

    public void setKafka(Boolean kafka) {
        this.kafka = kafka;
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

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Set<EMIASbyDay> getDaySet() {
        return daySet;
    }

    public void setDaySet(Set<EMIASbyDay> daySet) {
        this.daySet = daySet;
    }
}
