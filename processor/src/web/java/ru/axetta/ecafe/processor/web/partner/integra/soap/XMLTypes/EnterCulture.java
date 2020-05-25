/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap.XMLTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnterCulture")
public class EnterCulture {
    @XmlElement(required = true, nillable = true)
    protected String guid;
    @XmlElement(required = true)
    protected String orgCode;
    @XmlElement(required = true)
    protected String cultureName;
    @XmlElement(required = true)
    protected String cultureShortName;
    @XmlElement(required = true)
    protected String cultureAddress;
    @XmlElement(required = true)
    protected Date accessTime;
    @XmlElement(required = true)
    protected Long eventsStatus;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCultureName() {
        return cultureName;
    }

    public void setCultureName(String cultureName) {
        this.cultureName = cultureName;
    }

    public String getCultureShortName() {
        return cultureShortName;
    }

    public void setCultureShortName(String cultureShortName) {
        this.cultureShortName = cultureShortName;
    }

    public String getCultureAddress() {
        return cultureAddress;
    }

    public void setCultureAddress(String cultureAddress) {
        this.cultureAddress = cultureAddress;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public Long getEventsStatus() {
        return eventsStatus;
    }

    public void setEventsStatus(Long eventsStatus) {
        this.eventsStatus = eventsStatus;
    }
}
