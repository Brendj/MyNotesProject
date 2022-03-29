package ru.axetta.ecafe.processor.web.partner.meals.models;

public class ClientDataMain {
    private ClientData clientData;

    public ClientData getClientData() {
        if (clientData == null)
            clientData = new ClientData();
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }
}
