/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 09.02.12
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public class ClientList {
    @XmlElement(name = "Client")
    protected List<ClientItem> clients;

    public List<ClientItem> getClients() {
        if (clients == null)
            clients = new ArrayList<ClientItem>();
        return this.clients;
    }
}
