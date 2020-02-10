/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_refresh_token")
public class WtRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash")
    private String hash;

    @Column(name = "last_session")
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
