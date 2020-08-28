/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 15.06.2020.
 */
public class FPClient {
    @JsonProperty("ContractId")
    private Long contractId;

    @JsonProperty("Surname")
    private String surname;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("SecondName")
    private String secondName;

    @JsonProperty("Discounts")
    private List<FPClientDiscounts> discounts;

    public FPClient(Client client) {
        this.setContractId(client.getContractId());
        this.setName(client.getPerson().getFirstName());
        this.setSecondName(client.getPerson().getSecondName());
        this.setSurname(client.getPerson().getSurname());
        this.discounts = new ArrayList<>();
        for (CategoryDiscount cd : client.getCategories()) {
            FPClientDiscounts fpClientDiscounts = new FPClientDiscounts(cd);
            this.discounts.add(fpClientDiscounts);
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

    public List<FPClientDiscounts> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<FPClientDiscounts> discounts) {
        this.discounts = discounts;
    }
}
