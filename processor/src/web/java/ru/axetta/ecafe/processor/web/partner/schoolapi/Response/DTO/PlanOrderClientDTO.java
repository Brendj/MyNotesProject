/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import org.apache.commons.lang.NullArgumentException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanOrderClientDTO {
    @JsonProperty("ContractId")
    private Long contractId;
    @JsonProperty("Surname")
    private String surname;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("SecondName")
    private String secondName;
    @JsonProperty("Enter")
    private Date enter = null;
    @JsonProperty("Exit")
    private Date exit = null;
    @JsonProperty("Complexes")
    private List<ClientComplexDTO> complexes = new ArrayList<>();
    @JsonIgnore
    private Client client;
    @JsonIgnore
    private EnterEvent enterEvent;
    @JsonIgnore
    private EnterEvent exitEvent;
    @JsonIgnore
    private List<CategoryDiscount> filteredClientCategoryDiscounts = new ArrayList<>();

    public PlanOrderClientDTO(){

    }

    public PlanOrderClientDTO(Client client, EnterEvent enterEvent, EnterEvent exitEvent){
        if(client == null)
            throw new NullArgumentException("Client can not be null");
        this.client = client;
        this.enterEvent = enterEvent;
        this.exitEvent = exitEvent;
        this.contractId = client.getContractId();
        if(client.getPerson() != null){
            this.surname = client.getPerson().getSurname();
            this.name = client.getPerson().getFirstName();
            this.secondName = client.getPerson().getSecondName();
        }
        if(enterEvent != null){
            this.enter = enterEvent.getEvtDateTime();
        }
        if(exitEvent != null){
            this.exit = exitEvent.getEvtDateTime();
        }
    }

    public PlanOrderClientDTO(Client client){
        if(client == null)
            throw new NullArgumentException("Client can not be null");
        this.client = client;
        this.contractId = client.getContractId();
        if(client.getPerson() != null){
            this.surname = client.getPerson().getSurname();
            this.name = client.getPerson().getFirstName();
            this.secondName = client.getPerson().getSecondName();
        }

    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getEnter() {
        return enter;
    }

    public void setEnter(Date enter) {
        this.enter = enter;
    }

    public Date getExit() {
        return exit;
    }

    public void setExit(Date exit) {
        this.exit = exit;
    }

    public List<ClientComplexDTO> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<ClientComplexDTO> complexes) {
        if(complexes != null)
            this.complexes = complexes;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        if(client == null)
            throw new NullArgumentException("Client can not be null");
        this.contractId = client.getContractId();
        if(client.getPerson() != null){
            this.surname = client.getPerson().getSurname();
            this.name = client.getPerson().getFirstName();
            this.secondName = client.getPerson().getSecondName();
        }
        this.client = client;
    }

    public EnterEvent getEnterEvent() {
        return enterEvent;
    }

    public void setEnterEvent(EnterEvent enterEvent) {
        if(enterEvent != null)
            this.enter = enterEvent.getEvtDateTime();
        this.enterEvent = enterEvent;
    }

    public EnterEvent getExitEvent() {
        return exitEvent;
    }

    public void setExitEvent(EnterEvent exitEvent) {
        if(exitEvent != null)
            this.exit = exitEvent.getEvtDateTime();
        this.exitEvent = exitEvent;
    }

    public List<CategoryDiscount> getFilteredClientCategoryDiscounts() {
        return filteredClientCategoryDiscounts;
    }

    public void setFilteredClientCategoryDiscounts(List<CategoryDiscount> filteredClientCategoryDiscounts) {
        if(filteredClientCategoryDiscounts != null)
            this.filteredClientCategoryDiscounts = filteredClientCategoryDiscounts;
    }
}
