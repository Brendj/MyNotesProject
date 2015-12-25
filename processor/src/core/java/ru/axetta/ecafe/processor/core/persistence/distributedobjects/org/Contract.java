/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.org;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ContractDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.*;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
 * Date: 07.08.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Contract extends ContractDistributedObject {

    //private long idOfContract;
    private String contractNumber;
    private String performer;
    private String customer;
    private Date dateOfConclusion;
    private Date dateOfClosing;
    private boolean contractState;
    private Contragent contragent;

    private final Integer CONTRACT_ACTIVE = 1;
    private final Integer CONTRACT_INACTIVE = 0;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("contractNumber"), "contractNumber");
        projectionList.add(Projections.property("performer"), "performer");
        projectionList.add(Projections.property("customer"), "customer");
        projectionList.add(Projections.property("dateOfConclusion"), "dateOfConclusion");
        projectionList.add(Projections.property("dateOfClosing"), "dateOfClosing");
        projectionList.add(Projections.property("contractState"), "contractState");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Criteria criteria = session.createCriteria(Contract.class);
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        Criterion crByOwner = Restrictions.eq("orgOwner", idOfOrg);
        //Добавим в возвращаемый список контракты, созданные на процессинге

        Query query = session.createQuery("select o.contract.globalId from Org o where o.idOfOrg = :id");
        query.setParameter("id", idOfOrg);
        //query.setResultTransformer(Transformers.aliasToBean(Contract.class));
        List<Long> list = query.list();


        /*Criteria criteriaByOrg = session.createCriteria(Org.class);
        criteriaByOrg.add(Restrictions.eq("contract.idOfContract", this));
        List<Org> list = criteriaByOrg.list();
        Set<Long> set = new HashSet<Long>();
        for (Org o : list) {
            set.add(o.getContract().getIdOfContract());
        }*/
        Criterion crByIds = Restrictions.in("globalId", list);

        criteria.add(Restrictions.or(crByOwner, crByIds));

        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));

        return criteria.list();
    }

    @Override
    protected void appendAttributes(Element element) {
        Org org = DAOService.getInstance().getOrg(getIdOfSyncOrg());
        XMLUtils.setAttributeIfNotNull(element, "Id", contractNumber);
        XMLUtils.setAttributeIfNotNull(element, "Customer", customer);
        XMLUtils.setAttributeIfNotNull(element, "Responsible", performer);
        if (org != null) {
            XMLUtils.setAttributeIfNotNull(element, "Provider", DAOService.getInstance().getDefaultSupplierNameByOrg(org.getIdOfOrg()));
            XMLUtils.setAttributeIfNotNull(element, "OrgSourceMenu", DAOService.getInstance().getMenuSourceOrgName(org.getIdOfOrg()));
            XMLUtils.setAttributeIfNotNull(element, "ProductionConf", DAOService.getInstance().getConfigurationProviderNameByOrg(org.getIdOfOrg()));
        }
        XMLUtils.setAttributeIfNotNull(element, "StDate", getDateOfConclusion());
        XMLUtils.setAttributeIfNotNull(element, "ValidDate", getDateOfClosing());
        Integer state = getContractState() ? CONTRACT_ACTIVE : CONTRACT_INACTIVE;
        Long owner = getOrgOwner();
        if (owner == null) {
            owner = -1L;
        }
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", owner);
        XMLUtils.setAttributeIfNotNull(element, "State", state);
    }

    @Override
    public Contract parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) {
            setOrgOwner(longOrgOwner);
            setContragent(DAOService.getInstance().getOrg(getOrgOwner()).getDefaultSupplier());

        }
        setContractNumber(XMLUtils.getStringAttributeValue(node, "Id", 255));
        setCustomer(XMLUtils.getStringAttributeValue(node, "Customer", 255));
        setPerformer(XMLUtils.getStringAttributeValue(node, "Responsible", 255));
        setDateOfConclusion(XMLUtils.getDateAttributeValue(node, "StDate"));
        setDateOfClosing(XMLUtils.getDateAttributeValue(node, "ValidDate"));
        Integer state = XMLUtils.getIntegerAttributeValue(node, "State");
        Boolean bState = (state == CONTRACT_ACTIVE);
        setContractState(bState);
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    protected void setContract(Session session, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        org.setContract(this);
        session.merge(org);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setContractState(((Contract)distributedObject).getContractState());
        setDateOfClosing(((Contract) distributedObject).getDateOfClosing());
        setDateOfConclusion(((Contract) distributedObject).getDateOfConclusion());
        setCustomer(((Contract) distributedObject).getCustomer());
        setPerformer(((Contract) distributedObject).getPerformer());
        setContractNumber(((Contract) distributedObject).getContractNumber());
        setContragent(((Contract) distributedObject).getContragent());
    }

    public boolean getContractState() {
        return contractState;
    }

    public void setContractState(boolean contractState) {
        this.contractState = contractState;
    }

    public Date getDateOfClosing() {
        return dateOfClosing;
    }

    public void setDateOfClosing(Date dateOfClosing) {
        this.dateOfClosing = dateOfClosing;
    }

    public Date getDateOfConclusion() {
        return dateOfConclusion;
    }

    public void setDateOfConclusion(Date dateOfConclusion) {
        this.dateOfConclusion = dateOfConclusion;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public long getIdOfContract() {
        return globalId;
    }

    /*public void setIdOfContract(long idOfContract) {
        this.idOfContract = idOfContract;
    } */

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }
}
