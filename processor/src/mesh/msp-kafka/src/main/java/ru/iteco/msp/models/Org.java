/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_orgs")
public class Org {
    @Id
    @Column(name = "idoforg")
    private Long idOfOrg;

    @Column(name = "organizationidfromnsi")
    private Long organizationIdFromNSI;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getOrganizationIdFromNSI() {
        return organizationIdFromNSI;
    }

    public void setOrganizationIdFromNSI(Long organizationIdFromNSI) {
        this.organizationIdFromNSI = organizationIdFromNSI;
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
