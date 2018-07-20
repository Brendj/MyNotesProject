/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;


import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "numberOfRegisteredUsersInOrgByResult", propOrder = {
    "organizationUid",
    "peopleQuantity",
    "timeUpdate"
})
public class PeopleQuantityInOrgResult extends Result {
    @XmlElement(name = "organizationUid")
    private String organizationUid;
    @XmlElement(name = "peopleQuantity")
    private List<PeopleQuantityInGroup> peopleQuantity;
    @XmlElement(name = "timeUpdate")
    private Date timeUpdate;

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public List<PeopleQuantityInGroup> getPeopleQuantity() {
        if(peopleQuantity == null){
            this.peopleQuantity = new ArrayList<PeopleQuantityInGroup>();
        }
        return this.peopleQuantity;
    }

    public void setPeopleQuantity(List<PeopleQuantityInGroup> peopleQuantity) {
        this.peopleQuantity = peopleQuantity;
    }

    public Date getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Date timeUpdate) {
        this.timeUpdate = timeUpdate;
    }
}
