/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.Date;

public class ConsolidationDadas {
    private Long idOrg;
    private String groupName;
    private Date datebefore;
    private Date dateafter;

    ConsolidationDadas ()
    {}

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getDatebefore() {
        return datebefore;
    }

    public void setDatebefore(Date datebefore) {
        this.datebefore = datebefore;
    }

    public Date getDateafter() {
        return dateafter;
    }

    public void setDateafter(Date dateafter) {
        this.dateafter = dateafter;
    }
}
