/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class ClientBalanceTransferPage implements ClientSelectPage.CompleteHandler {
    public Client fromClient, toClient;
    public Long sum;
    public String clientSelectType;


    public Client getFromClient() {
        return fromClient;
    }

    public void setFromClient(Client fromClient) {
        this.fromClient = fromClient;
    }

    public Client getToClient() {
        return toClient;
    }

    public void setToClient(Client toClient) {
        this.toClient = toClient;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    @Override
    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        Client cl = null;
        if (idOfClient!=null) cl = (Client)session.get(Client.class, idOfClient);
        if (clientSelectType.equals("from")) {
            fromClient = cl;
        }
        else if (clientSelectType.equals("to")) {
            toClient = cl;
        }
    }
}
