/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class RetirementReason extends LibraryDistributedObject {

    private String retirementReasonName;
    private int hashCode;
    private Set<Ksu2Record> ksu2RecordInternal;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("retirementReasonName"), "retirementReasonName");
        projectionList.add(Projections.property("hashCode"), "hashCode");

        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Criteria criteria = session.createCriteria(RetirementReason.class);
        criteria.add(Restrictions.eq("hashCode",getHashCode()));
        RetirementReason retirementReason = (RetirementReason) criteria.uniqueResult();
        if(!(retirementReason==null || retirementReason.getDeletedState() || guid.equals(retirementReason.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("RetirementReason DATA_EXIST_VALUE");
            distributedObjectException.setData(retirementReason.getGuid());
            throw  distributedObjectException;
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "RetirementReasonName", retirementReasonName);
    }

    @Override
    public RetirementReason parseAttributes(Node node) throws Exception {
        String retirementReasonName = XMLUtils.getStringAttributeValue(node, "RetirementReasonName", 45);
        if (retirementReasonName != null)
            setRetirementReasonName(retirementReasonName);
        setHashCode(hashCode());
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setRetirementReasonName(((RetirementReason) distributedObject).getRetirementReasonName());
        setHashCode(((RetirementReason) distributedObject).getHashCode());
    }

    public String getRetirementReasonName() {
        return retirementReasonName;
    }

    public void setRetirementReasonName(String retirementReasonName) {
        this.retirementReasonName = retirementReasonName;
    }

    @Override
    public String toString() {
        return String.format("RetirementReason{retirementReasonName='%s'}", retirementReasonName);
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

        RetirementReason that = (RetirementReason) o;

        return !(retirementReasonName != null ? !retirementReasonName.equals(that.retirementReasonName)
                : that.retirementReasonName != null);

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
        return 31 * ((retirementReasonName != null ? getStringForHash(retirementReasonName).hashCode() : 0)) + (retirementReasonName != null ? getStringForHash(retirementReasonName).hashCode() : 0);
    }

    public Set<Ksu2Record> getKsu2RecordInternal() {
        return ksu2RecordInternal;
    }

    public void setKsu2RecordInternal(Set<Ksu2Record> ksu2RecordInternal) {
        this.ksu2RecordInternal = ksu2RecordInternal;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
}
