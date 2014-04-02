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

    private final Long idOfClient;
    private final String orgShortName;
    private final PersonItem person;
    private final PersonItem contractPerson;
    private final Long contractId;
    private final Date contractTime;
    private final Integer contractState;

    public String getShortName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ContractIdFormat.format(contractId)).append(" (").append(AbbreviationUtils
                .buildAbbreviation(contractPerson.getFirstName(), contractPerson.getSurname(),
                        contractPerson.getSecondName())).append("): ").append(AbbreviationUtils
                .buildAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName()));
        return stringBuilder.toString();
    }

    public ClientItem() {
        this.idOfClient = null;
        this.orgShortName = null;
        this.person = new PersonItem();
        this.contractPerson = new PersonItem();
        this.contractId = null;
        this.contractTime = null;
        this.contractState = null;
    }

    public ClientItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.orgShortName = client.getOrg().getShortName();
        this.person = new PersonItem(client.getPerson());
        this.contractPerson = new PersonItem(client.getContractPerson());
        this.contractId = client.getContractId();
        this.contractTime = client.getContractTime();
        this.contractState = client.getContractState();
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
}
