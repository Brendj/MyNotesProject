/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.OrganizationType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_orgs")
public class Org {
    @Id
    @Column(name = "idoforg")
    private Long idOfOrg;

    @Column(name = "officialname")
    private String officialName;

    @Column(name = "organizationtype")
    @Enumerated(EnumType.ORDINAL)
    private OrganizationType type;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "shortaddress")
    private String shortAddress;

    @Column(name = "shortnameinfoservice")
    private String shortNameInfoService;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Org org = (Org) o;
        return Objects.equals(idOfOrg, org.idOfOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrg);
    }
}
