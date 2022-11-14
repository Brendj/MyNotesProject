package ru.axetta.ecafe.processor.web.ui.card.items;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */
public class ClientItem {

    private Long idOfClient;
    private long idOfOrg;
    private String orgShortName;
    private final PersonItem person;
    private final PersonItem contractPerson;
    private Long contractId;
    private Date contractTime;
    private Integer contractState;
    private String orgShortAdress;
    private String orgDistrict;
    private String clientGroup;

    public String getShortName() {
        if (contractId == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ContractIdFormat.format(contractId)).append(" (").append(AbbreviationUtils
                .buildAbbreviation(contractPerson.getFirstName(), contractPerson.getSurname(),
                        contractPerson.getSecondName())).append("): ").append(AbbreviationUtils
                .buildAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName()));
        return stringBuilder.toString();
    }

    public String getShortNameContractId() {
        if (contractId == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" (").append(AbbreviationUtils
                .buildAbbreviation(contractPerson.getFirstName(), contractPerson.getSurname(),
                        contractPerson.getSecondName())).append("): ").append(AbbreviationUtils
                .buildAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName())).append(ContractIdFormat.format(contractId));
        return stringBuilder.toString();
    }

    public ClientItem() {
        this.person = new PersonItem();
        this.contractPerson = new PersonItem();
    }

    public ClientItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.idOfOrg = client.getOrg().getIdOfOrg();
        this.orgShortName = client.getOrg().getShortName();
        this.person = new PersonItem(client.getPerson());
        this.contractPerson = new PersonItem(client.getContractPerson());
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
        this.orgShortAdress = client.getOrg().getShortAddress();
        this.orgDistrict = client.getOrg().getDistrict();
        this.clientGroup = client.getClientGroup().getGroupName();
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public PersonItem getPerson() {
        return person;
    }

    public PersonItem getContractPerson() {
        return contractPerson;
    }

    public Long getContractId() {
        return contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public Integer getContractState() {
        return contractState;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public String getOrgShortAdress() { return orgShortAdress;}

    public String getOrgDistrict() { return orgDistrict;}

    public String getClientGroup() { return clientGroup;}
}
