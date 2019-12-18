/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class LinkingTokenForSmartWatch {
    private Long idOfLinkingTokensForSmartWatch;
    private String phoneNumber;
    private String token;
    private Date createDate;

    public Long getIdOfLinkingTokensForSmartWatch() {
        return idOfLinkingTokensForSmartWatch;
    }

    public void setIdOfLinkingTokensForSmartWatch(Long idOfLinkingTokensForSmartWatch) {
        this.idOfLinkingTokensForSmartWatch = idOfLinkingTokensForSmartWatch;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
