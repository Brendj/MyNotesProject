package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GuardianResponse extends ResponseItem {

    private List<GuardianItem> personsList;

    public GuardianResponse() {
    }

    public GuardianResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GuardianResponse(List<Client> clients){
        this.personsList = fillingGuardianItems(clients);
        this.code = OK;
        this.message = OK_MESSAGE;
    }

    public List<GuardianItem> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(List<GuardianItem> personsList) {
        this.personsList = personsList;
    }

    private List<GuardianItem> fillingGuardianItems(List<Client> clients){
        List<GuardianItem> guardianItems = new ArrayList<>();
        clients.forEach(c -> {
            GuardianItem guardianItem = new GuardianItem();
            guardianItem.setIdOfOrg(c.getOrg().getIdOfOrg());
            guardianItem.setAddressOrg(c.getOrg().getAddress());
            guardianItem.setFirstName(c.getPerson().getFirstName());
            guardianItem.setLastName(c.getPerson().getSurname());
            guardianItem.setPatronymic(c.getPerson().getSecondName());
            guardianItem.setMobile(c.getMobile());
            guardianItem.setSnils(c.getSan());
            guardianItem.setIdOfClient(c.getIdOfClient());
            guardianItem.setGroupName(c.getClientGroup().getGroupName());
            guardianItem.setIdOfClientGroup(c.getIdOfClientGroup());
            guardianItems.add(guardianItem);
        });
        return guardianItems;
    }
}
