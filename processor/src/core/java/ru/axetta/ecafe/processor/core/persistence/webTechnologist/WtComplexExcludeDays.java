/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_complex_exclude_days")
public class WtComplexExcludeDays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "idofcomplex")
    private WtComplex complex;

    @Column(name = "version")
    private Long version;

    //@Column(name = "deleteState")
    //private Integer deleteState;

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

    public ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex getComplex() {
        return complex;
    }

    public void setComplex(ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex complex) {
        this.complex = complex;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    //public Integer getDeleteState() {
    //    return deleteState;
    //}
    //
    //public void setDeleteState(Integer deleteState) {
    //    this.deleteState = deleteState;
    //}


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexExcludeDays that = (WtComplexExcludeDays) o;
        return id.equals(that.id) && date.equals(that.date) && complex.equals(that.complex) && version
                .equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, complex, version);
    }

    @Override
    public String toString() {
        return "WtComplexExcludeDays{" + "id=" + id + ", date=" + date + ", complex=" + complex + ", version=" + version
                + '}';
    }
}
