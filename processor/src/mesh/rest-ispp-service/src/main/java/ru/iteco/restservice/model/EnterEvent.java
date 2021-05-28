/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.EnterEventId;
import ru.iteco.restservice.model.enums.PassdirectionType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_enterevents")
public class EnterEvent {
    @EmbeddedId
    private EnterEventId enterEventId;

    @Column(name = "passdirection")
    private PassdirectionType passDirection;

    @ManyToOne
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @ManyToOne
    @JoinColumn(name = "idofclient", insertable = false, updatable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "childpasscheckerid", insertable = false, updatable = false)
    private Client passChecker;

    @ManyToOne
    @JoinColumn(name = "guardianid", insertable = false, updatable = false)
    private Client guardian;

    public EnterEventId getEnterEventId() {
        return enterEventId;
    }

    public void setEnterEventId(EnterEventId enterEventId) {
        this.enterEventId = enterEventId;
    }

    public PassdirectionType getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(PassdirectionType passDirection) {
        this.passDirection = passDirection;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getPassChecker() {
        return passChecker;
    }

    public void setPassChecker(Client passChecker) {
        this.passChecker = passChecker;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enterEventId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnterEvent that = (EnterEvent) o;
        return Objects.equals(enterEventId, that.enterEventId);
    }
}
