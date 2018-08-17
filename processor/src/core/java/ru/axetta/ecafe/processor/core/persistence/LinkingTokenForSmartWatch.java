/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public class LinkingTokenForSmartWatch {
    private Long idofLinkingTokensForSmartWatch;
    private String phoneNumber;
    private String token;

    public Long getIdofLinkingTokensForSmartWatch() {
        return idofLinkingTokensForSmartWatch;
    }

    public void setIdofLinkingTokensForSmartWatch(Long idofLinkingTokensForSmartWatch) {
        this.idofLinkingTokensForSmartWatch = idofLinkingTokensForSmartWatch;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
