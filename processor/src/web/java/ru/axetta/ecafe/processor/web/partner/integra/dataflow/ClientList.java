/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.12.11
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
public class ClientList {
    private List<ClientItem> clients;

    public List<ClientItem> getClients() {
        if (clients == null) {
            clients = new ArrayList<ClientItem>();
        }
        return this.clients;
    }
}
