/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.08.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Contract {

    private long idOfContract;
    private String contractNumber;
    private String performer;
    private String customer;
    private Date dateOfConclusion;
    private Date dateOfClosing;
    private boolean contractState;
    private Set<Org> organizationInternal = new HashSet<Org>();

    public List<Org> getOrganizationList(){
        return new ArrayList<Org>(getOrganizationInternal());
    }

    public void addOrganization(Org org){
        if(!getOrganizationInternal().contains(org)){
            getOrganizationInternal().add(org);
        }
    }

    public void removeOrganization(Org org){
        getOrganizationInternal().remove(org);
    }

    private Set<Org> getOrganizationInternal() {
        return organizationInternal;
    }

    private void setOrganizationInternal(Set<Org> organizationInternal) {
        this.organizationInternal = organizationInternal;
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
        return idOfContract;
    }

    public void setIdOfContract(long idOfContract) {
        this.idOfContract = idOfContract;
    }
}
