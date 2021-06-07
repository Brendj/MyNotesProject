package ru.iteco.restservice.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by nuc on 04.05.2021.
 */
@Entity
@Table(name = "cf_specialdates")
public class SpecialDate {
    @Id
    @Column(name = "IdOfSpecialDate")
    private Long idOfSpecialDate;

    @Column(name = "idOfOrg")
    private Long idOfOrg;

    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    @Column(name = "date")
    private Date date;
    //private Org org;

    @Column(name = "isWeekend")
    private Integer isWeekend;

    @Column(name = "deleted")
    private Integer deleted;
    //private Org orgOwner; // От какой организации создана запись
    //private Long version;
    @Column
    private String comment;

    @Column
    private Long idOfClientGroup;

    public Long getIdOfSpecialDate() {
        return idOfSpecialDate;
    }

    public void setIdOfSpecialDate(Long idOfSpecialDate) {
        this.idOfSpecialDate = idOfSpecialDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getWeekend() {
        return isWeekend;
    }

    public void setWeekend(Integer weekend) {
        isWeekend = weekend;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public boolean isDeleted() {
        return deleted.equals(1);
    }

    public boolean isWeekend() {
        return isWeekend.equals(1);
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }
}
