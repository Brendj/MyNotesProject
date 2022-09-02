package ru.axetta.ecafe.processor.web.internal;

public class ClientGuardianResponse extends ResponseItem{

    private ClientOfAnotherOrgItem clientsData;
    private ClientGuardianItem clientsGuardians;

    public ClientGuardianResponse() {
    }

    public ClientGuardianResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ClientOfAnotherOrgItem getClientsData() {
        return clientsData;
    }

    public void setClientsData(ClientOfAnotherOrgItem clientsData) {
        this.clientsData = clientsData;
    }

    public ClientGuardianItem getClientsGuardians() {
        return clientsGuardians;
    }

    public void setClientsGuardians(ClientGuardianItem clientsGuardians) {
        this.clientsGuardians = clientsGuardians;
    }

}
