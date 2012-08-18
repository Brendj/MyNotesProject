/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class LibVisit extends DistributedObject {

    private Client client;
    private Date date;
    private int source;

    private Long idOfClient;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public LibVisit parseAttributes(Node node) throws ParseException {

        idOfClient = getLongAttributeValue(node, "idOfClient");

        date = getDateAttributeValue(node, "date");
        source = getIntegerAttributeValue(node, "source");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        if (idOfClient != null) {
            setClient(daoService.findClientById(idOfClient));
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setClient(((LibVisit) distributedObject).getClient());
        setDate(((LibVisit) distributedObject).getDate());
        setSource(((LibVisit) distributedObject).getSource());
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "LibVisit{" +
                "client=" + client +
                ", date=" + date +
                ", source=" + source +
                '}';
    }
}
