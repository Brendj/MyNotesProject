
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClientRepresentative complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClientRepresentative">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientRepresentative")
public class ClientRepresentative {

    @XmlAttribute(name = "Id")
    protected Long id;

    @XmlAttribute(name = "Name")
    protected String name;

    @XmlAttribute(name = "Email")
    protected String email;

    @XmlAttribute(name = "Mobile")
    protected String mobile;

    @XmlAttribute(name = "NotifyViaPush")
    protected Boolean notifyviapush;

    @XmlAttribute(name = "NotifyViaEmail")
    protected Boolean notifyviaemail;

    @XmlAttribute(name = "CreatedWhere")
    protected Integer createdWhere;

    @XmlAttribute(name = "IdOfOrg")
    private Long idOfOrg;

    @XmlAttribute(name = "OrgShortName")
    private String orgShortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getNotifyviapush() {
        return notifyviapush;
    }

    public void setNotifyviapush(Boolean notifyviapush) {
        this.notifyviapush = notifyviapush;
    }

    public Boolean getNotifyviaemail() {
        return notifyviaemail;
    }

    public void setNotifyviaemail(Boolean notifyviaemail) {
        this.notifyviaemail = notifyviaemail;
    }

    public Integer getCreatedWhere() {
        return createdWhere;
    }

    public void setCreatedWhere(Integer createdWhere) {
        this.createdWhere = createdWhere;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }
}
