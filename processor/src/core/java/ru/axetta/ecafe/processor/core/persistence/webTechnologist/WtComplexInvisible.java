package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_complex_invisible")
@IdClass(WtComplexInvisiblePK.class)
public class WtComplexInvisible {
    @Id
    @Column(name = "idofcomplex")
    private Long idOfComplex;

    @Id
    @Column(name = "idoforg")
    private Long idOfOrg;

    @Column(name = "version")
    private Long version;

    @Column(name = "deletestate")
    private Integer deleteState;

    @Column(name = "createdate")
    private Date createDate;

    @Column(name = "create_by_id")
    private Long createdUser;

    @Column(name = "lastupdate")
    private Date lastUpdate;

    @Column(name = "update_by_id")
    private Long updatedUser;

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfOrg() { return idOfOrg; }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
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

    public Long getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(Long createdUser) {
        this.createdUser = createdUser;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(Long updatedUser) {
        this.updatedUser = updatedUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WtComplexInvisible that = (WtComplexInvisible) o;
        return Objects.equals(idOfComplex, that.idOfComplex) &&
                Objects.equals(idOfOrg, that.idOfOrg) &&
                Objects.equals(version, that.version) &&
                Objects.equals(deleteState, that.deleteState) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(createdUser, that.createdUser) &&
                Objects.equals(lastUpdate, that.lastUpdate)&&
                Objects.equals(updatedUser, that.updatedUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplex, idOfOrg, version, deleteState, createDate, createdUser, lastUpdate, updatedUser);
    }
}
