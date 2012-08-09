/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.08.12
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class ContractItem extends AbstractEntityItem<Contract> {

    private long idOfContract;
    private String contractNumber;
    private String performer;
    private String customer;
    private boolean contractState;
    private Date dateOfConclusion;
    private Date dateOfClosing;
    private String orgNames;
    private List<Long> idOfOrgList = new ArrayList<Long>(0);

    @Override
    public void fill(Contract contract) {
        idOfContract = contract.getIdOfContract();
        contractNumber = contract.getContractNumber();
        performer = contract.getPerformer();
        customer = contract.getCustomer();
        dateOfClosing = contract.getDateOfClosing();
        dateOfConclusion = contract.getDateOfConclusion();
        contractState = contract.getContractState();
        TypedQuery<Org> query = entityManager.createQuery("from Org where contract=:contract", Org.class);
        query.setParameter("contract", contract);
        List<Org> orgList = query.getResultList();
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
    public Class<Contract> getEntity() {
        return Contract.class;
    }

    public ContractItem(EntityManager entityManager) {
        super(entityManager);
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
}
