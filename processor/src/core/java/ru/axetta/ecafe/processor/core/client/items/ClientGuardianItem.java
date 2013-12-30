package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.Client;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.12.13
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianItem {
    private Long idOfClient;
    private Long contractId;
    private String personName;

    public ClientGuardianItem(Client client) {
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.personName = client.getPerson().getSurnameAndFirstLetters();
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getPersonName() {
        return personName;
    }
}
