/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.wt;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_complex_exclude_days")
public class WtComplexExcludeDays {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "idofcomplex")
    private WtComplex complex;

    @Column(name = "deleteState")
    private Integer deleteState;

    @Column(name = "version")
    private Long version;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WtComplex getComplex() {
        return complex;
    }

    public void setComplex(WtComplex complex) {
        this.complex = complex;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexExcludeDays that = (WtComplexExcludeDays) o;
        return id.equals(that.id) && date.equals(that.date) && complex.equals(that.complex) && Objects
                .equals(deleteState, that.deleteState) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, complex, deleteState, version);
    }
}
