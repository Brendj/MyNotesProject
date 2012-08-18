/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public class Reader extends DistributedObject {

    private long idOfClient;
    private Client client;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Reader parseAttributes(Node node) {

        Long idOfClient = getLongAttributeValue(node, "idOfClient");
        if (idOfClient != null) {
            setIdOfClient(idOfClient);
        }
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setClient(daoService.findClientById(idOfClient));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIdOfClient(((Reader) distributedObject).getIdOfClient());
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Reader{" +
                "idOfClient=" + idOfClient +
                ", client=" + client +
                '}';
    }
}
