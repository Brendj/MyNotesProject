/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;

import java.util.Date;

public class ClientDtisznDiscountInfo implements Comparable {
    private Long idOfClientDTISZNDiscountInfo;
    private Client client;
    private Long dtisznCode;
    private String dtisznDescription;
    private ClientDTISZNDiscountStatus status;
    private Date dateStart;
    private Date dateEnd;
    private Date createdDate;
    private Long version;
    private Boolean archived;
    private Date lastUpdate;
    private Date createdDateInternal;
    private Date lastReceivedDate;
    private String source;
    private Boolean sendnotification;
    private Date archiveDate;
    private Date updatedAt;
    private Boolean appointedMSP;

    public ClientDtisznDiscountInfo(Client client, Long dtisznCode, String dtisznDescription, ClientDTISZNDiscountStatus status,
            Date dateStart, Date dateEnd, Date createdDate, String source, Long version, Date updatedAt) {
        this.client = client;
        this.dtisznCode = dtisznCode;
        this.dtisznDescription = dtisznDescription;
        this.status = status;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.createdDate = createdDate;
        this.version = version;
        this.archived = false;
        this.lastUpdate = new Date();
        this.createdDateInternal = new Date();
        this.lastReceivedDate = new Date();
        this.source = source;
        this.updatedAt = updatedAt;
    }

    public ClientDtisznDiscountInfo() {

    }

    @Override
    public int compareTo(Object o) {
        ClientDtisznDiscountInfo item = (ClientDtisznDiscountInfo) o;
        if (this.isArchivedNullSafe() && !item.isArchivedNullSafe()) return -1;
        if (!this.isArchivedNullSafe() && item.isArchivedNullSafe()) return 1;
        int res = this.dateEnd.compareTo(item.getDateEnd());
        if (res == 0) {
            return DiscountManager.getDiscountPriority(this.dtisznCode).compareTo(item.getDtisznCode().intValue());
        } else {
            return res;
        }
    }

    public boolean isArchivedNullSafe() {
        return this.archived != null && this.archived;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClientDtisznDiscountInfo)) return false;
        return this.idOfClientDTISZNDiscountInfo.equals(((ClientDtisznDiscountInfo)o).getIdOfClientDTISZNDiscountInfo());
    }

    public boolean isInoe() {
        if (dtisznCode == null) return false;
        return dtisznCode.equals(Long.parseLong(RuntimeContext.getAppContext().getBean(ETPMVService.class).BENEFIT_INOE));
    }

    public Long getIdOfClientDTISZNDiscountInfo() {
        return idOfClientDTISZNDiscountInfo;
    }

    public void setIdOfClientDTISZNDiscountInfo(Long idOfClientDTISZNDiscountInfo) {
        this.idOfClientDTISZNDiscountInfo = idOfClientDTISZNDiscountInfo;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Long dtisznCode) {
        this.dtisznCode = dtisznCode;
    }

    public String getDtisznDescription() {
        return dtisznDescription;
    }

    public void setDtisznDescription(String dtisznDescription) {
        this.dtisznDescription = dtisznDescription;
    }

    public ClientDTISZNDiscountStatus getStatus() {
        return status;
    }

    public void setStatus(ClientDTISZNDiscountStatus status) {
        this.status = status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
        this.setArchiveDate(archived ? new Date() : null);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreatedDateInternal() {
        return createdDateInternal;
    }

    public void setCreatedDateInternal(Date createdDateInternal) {
        this.createdDateInternal = createdDateInternal;
    }

    public Date getLastReceivedDate() {
        return lastReceivedDate;
    }

    public void setLastReceivedDate(Date lastReceivedDate) {
        this.lastReceivedDate = lastReceivedDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getSendnotification() {
        if (sendnotification == null)
            return false;
        return sendnotification;
    }

    public void setSendnotification(Boolean sendnotification) {
        this.sendnotification = sendnotification;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAppointedMSP() {
        return appointedMSP;
    }

    public void setAppointedMSP(Boolean appointedMSP) {
        this.appointedMSP = appointedMSP;
    }
}
