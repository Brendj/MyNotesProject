/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public class LinkingToken {
    Long idOfLinkingToken;
    Long idOfClient;
    String token;

    public Long getIdOfLinkingToken() {
        return idOfLinkingToken;
    }

    public void setIdOfLinkingToken(Long idOfLinkingToken) {
        this.idOfLinkingToken = idOfLinkingToken;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
