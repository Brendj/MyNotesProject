/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContractItem extends AbstractEntityItem<Contract> {
    
    public static class Filter extends AbstractFilter {
        String contractNum, performer, customer;
        
        @Override
        public boolean isEmpty() {
            return (StringUtils.isEmpty(contractNum) && StringUtils.isEmpty(performer) && StringUtils.isEmpty(customer));
        }

        @Override
        public void clear() {
            contractNum = performer = customer = "";
        }

        @Override
        protected void apply(EntityManager entityManager, Criteria crit) {
            if (!StringUtils.isEmpty(contractNum)) crit.add(Restrictions.like("contractNumber", contractNum, MatchMode.ANYWHERE));
            if (!StringUtils.isEmpty(performer)) crit.add(Restrictions.like("performer", performer, MatchMode.ANYWHERE));
            if (!StringUtils.isEmpty(customer)) crit.add(Restrictions.like("customer", customer, MatchMode.ANYWHERE));
        }

        public String getContractNum() {
            return contractNum;
        }

        public void setContractNum(String contractNum) {
            this.contractNum = contractNum;
        }

        public String getPerformer() {
            return performer;
        }

        public void setPerformer(String performer) {
            this.performer = performer;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }
    }
    

    private long idOfContract;
    private Contragent contragent;
    private String contractNumber;
    private String performer;
    private String customer;
    private boolean contractState;
    private Date dateOfConclusion;
    private Date dateOfClosing;
    private String orgNames;
    private List<Long> idOfOrgList = new ArrayList<Long>(0);



    @Override
    public void fillForList(EntityManager entityManager, Contract contract) {
        idOfContract = contract.getIdOfContract();
        contractNumber = contract.getContractNumber();
        performer = contract.getPerformer();
        customer = contract.getCustomer();
        dateOfClosing = contract.getDateOfClosing();
        dateOfConclusion = contract.getDateOfConclusion();
        contractState = contract.getContractState();
        try {
            contragent = DAOService.getInstance().getContragentById(contract.getContragent().getIdOfContragent());
        } catch (Exception e) {
            contragent = contract.getContragent();
        }
    }
    
    @Override
    public void fill(EntityManager entityManager, Contract contract) {
        fillForList(entityManager, contract);
        ////
        List<Org> orgList = DAOUtils.findOrgsWithContract(entityManager, contract);
        if (orgList.isEmpty())
            orgNames = "Не выбрано";
        else {
            orgNames = "";
            for(Org org : orgList) {
                idOfOrgList.add(org.getIdOfOrg());
                orgNames = orgNames.concat(org.getShortName() + "; ");
            }
            orgNames = orgNames.substring(0, orgNames.length() - 1);
        }
    }

    @Override
    protected void saveTo(EntityManager entityManager, Contract contract) {
        contract.setContractNumber(getContractNumber());
        contract.setCustomer(getCustomer());
        contract.setPerformer(getPerformer());
        contract.setContractState(isContractState());
        contract.setDateOfClosing(getDateOfClosing());
        contract.setDateOfConclusion(getDateOfConclusion());
        contract.setContragent(getContragent());
        if(!getIdOfOrgList().isEmpty()){
            for (Org org : DAOUtils.findOrgs(entityManager, getIdOfOrgList())){
                org.setContract(contract);
            }
        }
    }

    @Override
    protected void prepareForEntityRemove(EntityManager entityManager, Contract entity) {
        DAOUtils.removeContractLinkFromOrgs(entityManager, entity);
    }

    @Override
    public Contract getEntity(EntityManager entityManager) {
        return entityManager.find(Contract.class, getIdOfContract());
    }
    @Override
    public Contract createEmptyEntity() {
        return new Contract();
    }

    public long getIdOfContract() {
        return idOfContract;
    }

    public void setIdOfContract(long idOfContract) {
        this.idOfContract = idOfContract;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Date getDateOfConclusion() {
        return dateOfConclusion;
    }

    public void setDateOfConclusion(Date dateOfConclusion) {
        this.dateOfConclusion = dateOfConclusion;
    }

    public Date getDateOfClosing() {
        return dateOfClosing;
    }

    public void setDateOfClosing(Date dateOfClosing) {
        this.dateOfClosing = dateOfClosing;
    }

    public boolean isContractState() {
        return contractState;
    }
    public String getContractStateAsString() {
        return contractState?"Активен":"Не активен";
    }

    public void setContractState(boolean contractState) {
        this.contractState = contractState;
    }

    public String getOrgNames() {
        return orgNames;
    }

    public void setOrgNames(String orgNames) {
        this.orgNames = orgNames;
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public void setIdOfOrgList(List<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    @Override
    public String toString() {
        return "Контракт (номер: "+contractNumber+")";
    }
}
