/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.07.16
 * Time: 10:37
 */
public class ClientPhoto {
    private Long idOfClient;
    private Client client;
    private String name;
    private boolean isNew;

    public ClientPhoto() {
    }

    public ClientPhoto(Client client, String name) {
        this.client = client;
        this.name = name;
    }

    public ClientPhoto(Client client, String name, boolean isNew) {
        this.client = client;
        this.name = name;
        this.isNew = isNew;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
