/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
public class Staff extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Criteria criteria = session.createCriteria(Staff.class);
        criteria.add(Restrictions.eq("hashCode", getHashCode()));
        Staff staff = null;
        List list = criteria.list();
        if(list!=null && !list.isEmpty()){
            staff = (Staff) list.get(0);
        }
        if(!(staff==null || staff.getDeletedState() || guid.equals(staff.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Staff DATA_EXIST_VALUE");
            distributedObjectException.setData(staff.getGuid());
            throw distributedObjectException;
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRole", idOfRole);
        XMLUtils.setAttributeIfNotNull(element, "ParentId", parentId);
        XMLUtils.setAttributeIfNotNull(element, "Flags", flags);
        XMLUtils.setAttributeIfNotNull(element, "SurName", surName);
        XMLUtils.setAttributeIfNotNull(element, "FirstName", firstName);
        XMLUtils.setAttributeIfNotNull(element, "SecondName", secondName);
        XMLUtils.setAttributeIfNotNull(element, "StaffPosition", staffPosition);
        XMLUtils.setAttributeIfNotNull(element, "PersonalCode", personalCode);
        XMLUtils.setAttributeIfNotNull(element, "Rights", rights);
    }

    @Override
    protected Staff parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Long longIdOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        if (longIdOfClient != null)
            setIdOfClient(longIdOfClient);
        Long longIdOfRole = XMLUtils.getLongAttributeValue(node, "IdOfRole");
        if (longIdOfRole != null)
            setIdOfRole(longIdOfRole);
        Long longParentId = XMLUtils.getLongAttributeValue(node, "ParentId");
        if (longParentId != null)
            setParentId(longParentId);
        Integer integerFlags = XMLUtils.getIntegerAttributeValue(node, "Flags");
        if (integerFlags != null)
            setFlags(integerFlags);
        String stringSurName = XMLUtils.getStringAttributeValue(node, "SurName", 30);
        if (stringSurName != null)
            setSurName(stringSurName);
        String stringFirstName = XMLUtils.getStringAttributeValue(node, "FirstName", 30);
        if (stringFirstName != null)
            setFirstName(stringFirstName);
        String stringSecondName = XMLUtils.getStringAttributeValue(node, "SecondName", 30);
        if (stringSecondName != null)
            setSecondName(stringSecondName);
        String stringPersonalCode = XMLUtils.getStringAttributeValue(node, "PersonalCode", 128);
        if (stringPersonalCode != null)
            setPersonalCode(stringPersonalCode);
        String stringRights = XMLUtils.getStringAttributeValue(node, "Rights", 256);
        if (stringRights != null)
            setRights(stringRights);
        setHashCode(hashCode());
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
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
    private Integer hashCode;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;
    private Set<WayBill> wayBillInternal;
    private Set<StateChange> stateChangeInternal;
    private Set<InternalDisposingDocument> internalDisposingDocumentInternal;
    private Set<GoodRequest> goodRequestInternal;

    Set<GoodRequest> getGoodRequestInternal() {
        return goodRequestInternal;
    }

    void setGoodRequestInternal(Set<GoodRequest> goodRequestInternal) {
        this.goodRequestInternal = goodRequestInternal;
    }

    Set<InternalDisposingDocument> getInternalDisposingDocumentInternal() {
        return internalDisposingDocumentInternal;
    }

    void setInternalDisposingDocumentInternal(Set<InternalDisposingDocument> internalDisposingDocumentInternal) {
        this.internalDisposingDocumentInternal = internalDisposingDocumentInternal;
    }


    Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
    }

    Set<WayBill> getWayBillInternal() {
        return wayBillInternal;
    }

    void setWayBillInternal(Set<WayBill> wayBillInternal) {
        this.wayBillInternal = wayBillInternal;
    }

    Set<InternalIncomingDocument> getInternalIncomingDocumentInternal() {
        return internalIncomingDocumentInternal;
    }

    void setInternalIncomingDocumentInternal(Set<InternalIncomingDocument> internalIncomingDocumentInternal) {
        this.internalIncomingDocumentInternal = internalIncomingDocumentInternal;
    }

    public Integer getHashCode() {
        return hashCode;
    }

    public void setHashCode(Integer hashCode) {
        this.hashCode = hashCode;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Staff that = (Staff) o;

        return !(firstName != null && orgOwner !=null? !(firstName.equals(that.firstName) && orgOwner.equals(that.orgOwner))
                : (that.firstName != null && that.orgOwner != null));

    }

    private static final String Consonants = "бвгджзклмнпрстфхцчшщbcdfghklmnpqrstuvwxyz1234567890";

    private static boolean isConsonant(char c) {
        for (char consonant : Consonants.toCharArray())
            if (consonant == c)
                return true;
        return false;
    }

    private String getStringForHash(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = sb.length() - 1; i >= 0; --i) {
            if (isConsonant(sb.charAt(i))) continue;
            sb.delete(i, i + 1);
        }
        return sb.toString().toLowerCase();
    }

    @Override
    public int hashCode() {
        int result = 31 * ((firstName != null ? getStringForHash(firstName).hashCode() : 0)) + (firstName != null ? getStringForHash(firstName).hashCode() : 0);
        result = result + (orgOwner != null ? orgOwner.hashCode() : 0);
        return result;
    }
}
