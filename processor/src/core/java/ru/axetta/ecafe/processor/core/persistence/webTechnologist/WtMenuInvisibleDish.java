package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_menu_invisible_dish")
@IdClass(WtMenuInvisibleDishPK.class)
public class WtMenuInvisibleDish {
    @Id
    @Column(name = "idofmenu")
    private Long idOfMenu;

    @Id
    @Column(name = "idoforg")
    private Long idOfOrg;

    @Id
    @Column(name = "idofdish")
    private Long idOfDish;

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

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
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

    public void setCreatedUser(Long createdUser) { this.createdUser = createdUser; }

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
        WtMenuInvisibleDish that = (WtMenuInvisibleDish) o;
        return Objects.equals(idOfMenu, that.idOfMenu) &&
                Objects.equals(idOfOrg, that.idOfOrg) &&
                Objects.equals(idOfDish, that.idOfDish) &&
                Objects.equals(version, that.version) &&
                Objects.equals(deleteState, that.deleteState) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(createdUser, that.createdUser) &&
                Objects.equals(lastUpdate, that.lastUpdate) &&
                Objects.equals(updatedUser, that.updatedUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                idOfMenu, idOfOrg, idOfDish, version, deleteState, createDate, createdUser, lastUpdate, updatedUser);
    }
}
