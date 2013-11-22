/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.09.2009
 * Time: 13:17:39
 * To change this template use File | Settings | File Templates.
 */
public class ClientAuthToken {

    private static final String HTTP_SESSION_ATTRIBUTE = ClientAuthToken.class.getCanonicalName();
    private final Long contractId;
    private final boolean ssoAuth;

    public ClientAuthToken(Long contractId, boolean ssoAuth) {
        this.contractId = contractId;
        this.ssoAuth = ssoAuth;
    }

    public static ClientAuthToken loadFrom(HttpSession httpSession) {
        return (ClientAuthToken) httpSession.getAttribute(HTTP_SESSION_ATTRIBUTE);
    }

    public void storeTo(HttpSession httpSession) {
        httpSession.setAttribute(HTTP_SESSION_ATTRIBUTE, this);
    }

    public Long getContractId() {
        return contractId;
    }

    public boolean isSsoAuth() {
        return ssoAuth;
    }
}
