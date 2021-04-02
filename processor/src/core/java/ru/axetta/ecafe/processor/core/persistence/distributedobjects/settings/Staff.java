/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalDisposingDocument;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalIncomingDocument;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBill;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:03
 * Направление движения от Потребителя к Поставщику и Поставщика к потребителю в случае востановления данных
 * все пользователи Потребителей отправляются поставщику и все пользователи поставщика отправляются потребителю
 */
public class Staff extends DistributedObject {

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

    public enum Roles {
        ADMIN(0),
        CASHIER(1),
        SECURITY(2),
        LIBRARY_ADMIN(3),
        SELF_SERVICE(4),
        ADMIN_SECURITY(5),
        AUDITOR(6);

        private long idOfRole;

        Roles(long idOfRole) {
            this.idOfRole = idOfRole;
        }

        public long getIdOfRole() {
            return idOfRole;
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("idOfClient"), "idOfClient");
        projectionList.add(Projections.property("idOfRole"), "idOfRole");
        projectionList.add(Projections.property("parentId"), "parentId");
        projectionList.add(Projections.property("flags"), "flags");
        projectionList.add(Projections.property("surName"), "surName");
        projectionList.add(Projections.property("firstName"), "firstName");
        projectionList.add(Projections.property("secondName"), "secondName");
        projectionList.add(Projections.property("staffPosition"), "staffPosition");
        projectionList.add(Projections.property("personalCode"), "personalCode");
        projectionList.add(Projections.property("rights"), "rights");
        projectionList.add(Projections.property("hashCode"), "hashCode");
        criteria.setProjection(projectionList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Boolean isSupplier = DAOUtils.isSupplierByOrg(session, idOfOrg);
        if (isSupplier) {
            return processStaffsToSelfOrg(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        } else {
            return processStaffsToFriendlyOrgs(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    private List<DistributedObject> processStaffsToFriendlyOrgs(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid,
            Integer currentLimit) {
        Org currentOrg = (Org) session.load(Org.class,idOfOrg);
        List<DistributedObject> result = new ArrayList<DistributedObject>();
        Set<Long> friendlyOrgIds = OrgUtils.getFriendlyOrgIds(currentOrg);
        Long supplierOrg = getSourceOrgId(session, idOfOrg);
        friendlyOrgIds.add(supplierOrg);
        List<DistributedObject> adminStaffs = loadAdminStaffsForOrgs(session, friendlyOrgIds);
        List<DistributedObject> currentOrgStaffs = loadAllStaffsForOrg(session, idOfOrg);
        result.addAll(adminStaffs);
        result.addAll(currentOrgStaffs);
        return result;
    }

    private List<DistributedObject> processStaffsToSelfOrg(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid,
            Integer currentLimit) {
        List<DistributedObject> result = new ArrayList<DistributedObject>();
        List<Long> orgOwners = getDestOrgsForSupplier(session, idOfOrg);
        List<DistributedObject> adminStaffs = loadAdminStaffsForOrgs(session, orgOwners);
        List<DistributedObject> currentOrgStaffs = loadAllStaffsForOrg(session, idOfOrg);
        result.addAll(adminStaffs);
        result.addAll(currentOrgStaffs);
        return result;
    }

    private List<Long> getDestOrgsForSupplier(Session session, Long idOfSourceOrg) {
        /* Собираем всех потребителей Организации источника меню */
        Query query = session.createQuery("select rule.idOfDestOrg from MenuExchangeRule rule where rule.idOfSourceOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfSourceOrg);
        return query.list();
    }

    private Long getSourceOrgId(Session session, Long idOfOrg) {
    /* Получаю идентификатор организации источника меню */
        Query query = session.createQuery("select rule.idOfSourceOrg from MenuExchangeRule rule where rule.idOfDestOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return (Long) query.uniqueResult();
    }

    private List<DistributedObject> loadAllStaffsForOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(getClass());
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    private List<DistributedObject> loadAdminStaffsForOrgs(Session session, Collection<Long> orgIds) {
        if (orgIds.size() == 0) return new ArrayList<DistributedObject>();
        Criteria criteria = session.createCriteria(getClass());
        criteria.add(Restrictions.in("orgOwner", orgIds));
        criteria.add(Restrictions.in("idOfRole", Arrays.asList(Roles.ADMIN.getIdOfRole(), Roles.LIBRARY_ADMIN.getIdOfRole())));
        createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }




    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
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

    @Override
    protected Staff parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
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
