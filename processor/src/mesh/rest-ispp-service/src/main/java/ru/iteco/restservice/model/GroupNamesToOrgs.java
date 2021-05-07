package ru.iteco.restservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by nuc on 05.05.2021.
 */
@Entity
@Table(name = "Cf_GroupNames_To_Orgs")
public class GroupNamesToOrgs {
    @Id
    @Column(name = "IdOfGroupNameToOrg")
    private Long idOfGroupNameToOrg;

    @Column(name = "idOfOrg")
    private Long idOfOrg;

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "isSixDaysWorkWeek")
    private Boolean isSixDaysWorkWeek;

    public Long getIdOfGroupNameToOrg() {
        return idOfGroupNameToOrg;
    }

    public void setIdOfGroupNameToOrg(Long idOfGroupNameToOrg) {
        this.idOfGroupNameToOrg = idOfGroupNameToOrg;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getSixDaysWorkWeek() {
        return isSixDaysWorkWeek;
    }

    public void setSixDaysWorkWeek(Boolean sixDaysWorkWeek) {
        isSixDaysWorkWeek = sixDaysWorkWeek;
    }
}
