/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

/**
 * Created by i.semenov on 22.05.2018.
 */
public class ClientResult extends Result {
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
