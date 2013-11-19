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
 * Date: 17.08.12
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class TypeOfAccompanyingDocument extends LibraryDistributedObject {

    private long idOfTypeOfAccompanyingDocument;
    private String typeOfAccompanyingDocumentName;
    private Integer hashCode;
    private Set<AccompanyingDocument> accompanyingDocumentInternal;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("typeOfAccompanyingDocumentName"), "typeOfAccompanyingDocumentName");
        projectionList.add(Projections.property("hashCode"), "hashCode");

        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Criteria criteria = session.createCriteria(TypeOfAccompanyingDocument.class);
        criteria.add(Restrictions.eq("hashCode", getHashCode()));
        TypeOfAccompanyingDocument typeOfAccompanyingDocument = (TypeOfAccompanyingDocument) criteria.uniqueResult();
        if(!(typeOfAccompanyingDocument==null || typeOfAccompanyingDocument.getDeletedState() || guid.equals(typeOfAccompanyingDocument.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("TypeOfAccompanyingDocument DATA_EXIST_VALUE");
            distributedObjectException.setData(typeOfAccompanyingDocument.getGuid());
            throw  distributedObjectException;
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "TypeOfAccompanyingDocumentName", typeOfAccompanyingDocumentName);
    }

    @Override
    public TypeOfAccompanyingDocument parseAttributes(Node node) throws Exception {
        String typeOfAccompanyingDocumentName = XMLUtils.getStringAttributeValue(node, "TypeOfAccompanyingDocumentName", 36);
        if (typeOfAccompanyingDocumentName != null) {
            setTypeOfAccompanyingDocumentName(typeOfAccompanyingDocumentName);
        }
        setHashCode(hashCode());
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setTypeOfAccompanyingDocumentName(
                ((TypeOfAccompanyingDocument) distributedObject).getTypeOfAccompanyingDocumentName());
        setHashCode(((TypeOfAccompanyingDocument) distributedObject).getHashCode());
    }

    public long getIdOfTypeOfAccompanyingDocument() {
        return idOfTypeOfAccompanyingDocument;
    }

    public void setIdOfTypeOfAccompanyingDocument(long idOfTypeOfAccompanyingDocument) {
        this.idOfTypeOfAccompanyingDocument = idOfTypeOfAccompanyingDocument;
    }

    public String getTypeOfAccompanyingDocumentName() {
        return typeOfAccompanyingDocumentName;
    }

    public void setTypeOfAccompanyingDocumentName(String typeOfAccompanyingDocumentName) {
        this.typeOfAccompanyingDocumentName = typeOfAccompanyingDocumentName;
    }

    @Override
    public String toString() {
        return String
                .format("TypeOfAccompanyingDocument{idOfTypeOfAccompanyingDocument=%d, typeOfAccompanyingDocumentName='%s'}",
                        idOfTypeOfAccompanyingDocument, typeOfAccompanyingDocumentName);
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

        TypeOfAccompanyingDocument that = (TypeOfAccompanyingDocument) o;

        return !(typeOfAccompanyingDocumentName != null ? !typeOfAccompanyingDocumentName.equals(that.typeOfAccompanyingDocumentName)
                : that.typeOfAccompanyingDocumentName != null);

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
        return 31 * ((typeOfAccompanyingDocumentName != null ? getStringForHash(typeOfAccompanyingDocumentName).hashCode() : 0)) + (typeOfAccompanyingDocumentName != null ? getStringForHash(typeOfAccompanyingDocumentName).hashCode() : 0);
    }

    public Set<AccompanyingDocument> getAccompanyingDocumentInternal() {
        return accompanyingDocumentInternal;
    }

    public void setAccompanyingDocumentInternal(Set<AccompanyingDocument> accompanyingDocumentInternal) {
        this.accompanyingDocumentInternal = accompanyingDocumentInternal;
    }

    public Integer getHashCode() {
        return hashCode;
    }

    public void setHashCode(Integer hashCode) {
        this.hashCode = hashCode;
    }
}
