/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
public class Staff extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"IdOfClient", idOfClient);
        setAttribute(element,"IdOfRole", idOfRole);
        setAttribute(element,"ParentId", parentId);
        setAttribute(element,"Flags", flags);
        setAttribute(element,"SurName", surName);
        setAttribute(element,"FirstName", firstName);
        setAttribute(element,"SecondName", secondName);
        setAttribute(element,"StaffPosition", staffPosition);
        setAttribute(element,"PersonalCode", personalCode);
        setAttribute(element,"Rights", rights);
    }

    @Override
    protected Staff parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longIdOfClient = getLongAttributeValue(node,"IdOfClient");
        if(longIdOfClient!=null) setIdOfClient(longIdOfClient);
        Long longIdOfRole = getLongAttributeValue(node, "IdOfRole");
        if(longIdOfRole != null) setIdOfRole(longIdOfRole);
        Long longParentId = getLongAttributeValue(node, "ParentId");
        if(longParentId != null) setParentId(longParentId);
        Integer integerFlags = getIntegerAttributeValue(node, "Flags");
        if(integerFlags != null) setFlags(integerFlags);
        String stringSurName = getStringAttributeValue(node, "SurName",30);
        if(stringSurName != null) setSurName(stringSurName);
        String stringFirstName =getStringAttributeValue(node, "FirstName",30);
        if(stringFirstName != null) setFirstName(stringFirstName);
        String stringSecondName = getStringAttributeValue(node, "SecondName", 30);
        if(stringSecondName != null) setSecondName(stringSecondName);
        String stringPersonalCode = getStringAttributeValue(node, "PersonalCode", 128);
        if(stringPersonalCode != null) setPersonalCode(stringPersonalCode);
        String stringRights = getStringAttributeValue(node, "Rights", 256);
        if(stringRights != null) setRights(stringRights);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Staff) distributedObject).getOrgOwner());
        setIdOfClient(((Staff) distributedObject).getIdOfClient());
        setIdOfRole(((Staff) distributedObject).getIdOfRole());
        setParentId(((Staff) distributedObject).getParentId());
        setFlags(((Staff) distributedObject).getFlags());
        setSurName(((Staff) distributedObject).getSurName());
        setFirstName(((Staff) distributedObject).getFirstName());
        setSecondName(((Staff) distributedObject).getSecondName());
        setStaffPosition(((Staff) distributedObject).getStaffPosition());
        setPersonalCode(((Staff) distributedObject).getPersonalCode());
        setRights(((Staff) distributedObject).getRights());
    }

    private long idOfClient;
    private long idOfRole;
    private long parentId;
    private int flags;
    private String surName;
    private String firstName;
    private String secondName;
    private String staffPosition;
    private String personalCode;
    private String rights;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public String getStaffPosition() {
        return staffPosition;
    }

    public void setStaffPosition(String staffPosition) {
        this.staffPosition = staffPosition;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public long getIdOfRole() {
        return idOfRole;
    }

    public void setIdOfRole(long idOfRole) {
        this.idOfRole = idOfRole;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
