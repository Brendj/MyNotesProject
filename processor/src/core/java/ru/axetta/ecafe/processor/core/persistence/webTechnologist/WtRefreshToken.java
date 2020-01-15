/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Date;
import java.util.Objects;

public class WtRefreshToken {

    private String hash;
    private Date lastSession;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getLastSession() {
        return lastSession;
    }

    public void setLastSession(Date lastSession) {
        this.lastSession = lastSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtRefreshToken that = (WtRefreshToken) o;
        return Objects.equals(hash, that.hash) && Objects.equals(lastSession, that.lastSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, lastSession);
    }
}
