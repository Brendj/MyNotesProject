/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_client_dtiszn_discount_info")
public class ClientDTSZNDiscountInfo {
    @Id
    @Column(name = "idofclientdtiszndiscountinfo")
    private Long idOfClientDTISZNDiscountInfo;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "dtiszncode")
    private Long DTISZNCode;

    @Column(name = "datestart")
    private Long dateStart;

    @Column(name = "dateend")
    private Long dateEnd;

    @Column(name = "createddateinternal")
    private Long createdDateInternal;

    @Column(name = "lastupdate")
    private Long lastUpdate;

    @Column(name = "archived")
    private Integer archived;

    @Column(name = "status")
    private Integer status;

    public ClientDTSZNDiscountInfo() {
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

    public Long getDTISZNCode() {
        return DTISZNCode;
    }

    public void setDTISZNCode(Long DTISZNcode) {
        this.DTISZNCode = DTISZNcode;
    }

    public Long getDateStart() {
        return dateStart;
    }

    public void setDateStart(Long dateStart) {
        this.dateStart = dateStart;
    }

    public Long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Long getCreatedDateInternal() {
        return createdDateInternal;
    }

    public void setCreatedDateInternal(Long createdDateInternal) {
        this.createdDateInternal = createdDateInternal;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTSZNDiscountInfo that = (ClientDTSZNDiscountInfo) o;
        return Objects.equals(idOfClientDTISZNDiscountInfo, that.idOfClientDTISZNDiscountInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClientDTISZNDiscountInfo);
    }
}
