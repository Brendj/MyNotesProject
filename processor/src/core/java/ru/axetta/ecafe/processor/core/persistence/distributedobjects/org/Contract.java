/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.org;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ContractDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.infinispan.atomic.AtomicHashMap;
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

    private String contractNumber;
    private String performer;
    private String customer;
    private Date dateOfConclusion;
    private Date dateOfClosing;
    private boolean contractState;
    private Contragent contragent;

    private final Integer CONTRACT_ACTIVE = 1;
    private final Integer CONTRACT_INACTIVE = 0;

    private static final AtomicHashMap<Long, HashMap<Long, List<Long>>> cashInludedOrgsForContracts = new AtomicHashMap<Long, HashMap<Long, List<Long>>>();


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
        List resultContracts = loadProcessedContracts(session, idOfOrg, currentMaxVersion, currentLastGuid,
                currentLimit);
        updateCashInludedOrgsForContracts(resultContracts, session, idOfOrg);
        return resultContracts;
    }

    private List loadProcessedContracts(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid,
            Integer currentLimit) {
        Criteria criteria = session.createCriteria(Contract.class);
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);

        List<Long> allFriendlyOrgs = getFriendlyOrgs(session, idOfOrg);
        Junction junction = Restrictions.disjunction();
        List<Long> listContractsIds = loadCurrentContractIdsForOrgs(session, allFriendlyOrgs);
        List<Long> listHistoryContractsIds = loadContractIdsFromHistoryForOrgs(session, currentMaxVersion,
                allFriendlyOrgs);
        if (!listContractsIds.isEmpty()) {
            junction.add(Restrictions.in("globalId", listContractsIds));
        }
        if (!listHistoryContractsIds.isEmpty()) {
            junction.add(Restrictions.in("globalId", listHistoryContractsIds));
        }
        criteria.add(junction);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    private List<Long> loadCurrentContractIdsForOrgs(Session session, List<Long> allFriendlyOrgs) {
        //Добавим в возвращаемый список контракты, созданные на процессинге
        Query queryContractsIds = session.createQuery("select distinct o.contract.globalId from Org o where o.idOfOrg in (:orgs)");
        queryContractsIds.setParameterList("orgs", allFriendlyOrgs);
        return queryContractsIds.list();
    }

    private List<Long> loadContractIdsFromHistoryForOrgs(Session session, Long currentMaxVersion,
            List<Long> allFriendlyOrgs) {
        //и добавим контракты из истории (удаленные контракты или исключенные из контракта организации)
        Query queryHistoryContractsIds = session.createQuery(
                "select distinct h.contract.globalId from ContractOrgHistory h where h.org.idOfOrg in (:orgs) and h.lastVersionOfContract > :version");
        queryHistoryContractsIds.setParameterList("orgs", allFriendlyOrgs);
        queryHistoryContractsIds.setParameter("version", currentMaxVersion);
        return queryHistoryContractsIds.list();
    }

    private void updateCashInludedOrgsForContracts(List resultContracts, Session session, Long currentOrg) {
        if (cashInludedOrgsForContracts.containsKey(currentOrg)) {
            cashInludedOrgsForContracts.get(currentOrg).clear();
        } else {
            cashInludedOrgsForContracts.put(currentOrg, new HashMap<Long, List<Long>>());
        }
        if (resultContracts == null)
            return;
        List<Contract> contracts = (List<Contract>) resultContracts;
        for (Contract contract : contracts) {
            List<Long> includedOrgsInContract = loadIncludedOrgsInContract(session, contract);
            cashInludedOrgsForContracts.get(currentOrg).put(contract.getIdOfContract(), includedOrgsInContract);
        }
    }

    private List<Long> getFriendlyOrgs(Session session, Long idOfOrg) {
        ArrayList<Long> result = new ArrayList<Long>();
        Query query = session.createQuery("select o.friendlyOrg from Org o where o.idOfOrg=:id");
        query.setParameter("id", idOfOrg);
        List<Org> list = query.list();
        for (Org org : list) {
            result.add(org.getIdOfOrg());
        }
        if (!result.contains(idOfOrg)) {
            result.add(idOfOrg);
        }
        return result;
    }

    @Override
    protected void appendAttributes(Element element) {
        Org org = DAOReadonlyService.getInstance().findOrg(getIdOfSyncOrg());
        XMLUtils.setAttributeIfNotNull(element, "Id", contractNumber);
        XMLUtils.setAttributeIfNotNull(element, "Customer", customer);
        XMLUtils.setAttributeIfNotNull(element, "Responsible", performer);
        if (org != null) {
            XMLUtils.setAttributeIfNotNull(element, "Provider", DAOReadonlyService.getInstance().getDefaultSupplierNameByOrg(org.getIdOfOrg()));
            XMLUtils.setAttributeIfNotNull(element, "OrgSourceMenu", DAOReadonlyService.getInstance().getMenuSourceOrgName(org.getIdOfOrg()));
            XMLUtils.setAttributeIfNotNull(element, "ProductionConf", DAOReadonlyService.getInstance().getConfigurationProviderNameByOrg(org.getIdOfOrg()));
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

        XMLUtils.setAttributeIfNotNull(element, "IncludedOrgs", StringUtils.join(loadFromCashIncludedOrgsInContract(), ","));
        /*
        if (!element.hasAttribute("D")) {
            //проверяем, не пришел ли этот контракт из истории контрактов (из истории - это когда организация исключена из контракта, но сам контракт остался)
            //проверка такая: поле idofcontract у организации не равен ид. текущего контракта. Достаточно?
            if ((org.getContract() == null) || DAOService.getInstance()
                    .isContractFromHistory(getIdOfSyncOrg(), getGlobalId())) {
                element.setAttribute("D", "1");
            }
        }
        */
    }

    private List<Long> loadFromCashIncludedOrgsInContract() {
        if (!cashInludedOrgsForContracts.containsKey(getIdOfSyncOrg()))
            return Collections.emptyList();
        HashMap<Long, List<Long>> contractsWithOrgs = cashInludedOrgsForContracts.get(getIdOfSyncOrg());
        if (contractsWithOrgs == null) {
            return Collections.emptyList();
        }
        for (Long idOfContract : contractsWithOrgs.keySet()) {
            if (idOfContract == this.getIdOfContract()) {
                return contractsWithOrgs.get(idOfContract);
            }
        }
        return Collections.emptyList();
    }

    private List<Long> loadIncludedOrgsInContract(Session session, Contract contract) {
        Query query = session.createQuery("select o.idOfOrg from Org o where o.contract.globalId=:id");
        query.setParameter("id", contract.globalId);
        return query.list();
    }

    @Override
    public Contract parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) {
            setOrgOwner(longOrgOwner);
            setContragent(DAOReadonlyService.getInstance().findDefaultSupplier(getOrgOwner()));
        }
        setContractNumber(XMLUtils.getStringAttributeValue(node, "Id", 255));
        setCustomer(XMLUtils.getStringAttributeValue(node, "Customer", 255));
        setPerformer(XMLUtils.getStringAttributeValue(node, "Responsible", 255));
        setDateOfConclusion(XMLUtils.getDateAttributeValue(node, "StDate"));
        setDateOfClosing(XMLUtils.getDateAttributeValue(node, "ValidDate"));
        Integer state = XMLUtils.getIntegerAttributeValue(node, "State");
        Boolean bState = (Objects.equals(state, CONTRACT_ACTIVE));
        setContractState(bState);
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    protected void setContract(Session session, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        org.setContract(this);
        org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
        session.update(org);
    }

    @Override
    public void setContractOrgHistory(Session session, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);

        ContractOrgHistory history = new ContractOrgHistory();
        history.setContract(this);
        history.setOrg(org);
        history.setCreatedDate(new Date());
        history.setLastVersionOfContract(this.globalVersion);
        session.persist(history);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setContractState(((Contract) distributedObject).getContractState());
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
