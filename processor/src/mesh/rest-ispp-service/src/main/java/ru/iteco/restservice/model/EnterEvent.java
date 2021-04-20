/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.EnterEventId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_enterevents")
public class EnterEvent {
    @EmbeddedId
    private EnterEventId enterEventId;

    public EnterEventId getEnterEventId() {
        return enterEventId;
    }

    public void setEnterEventId(EnterEventId enterEventId) {
        this.enterEventId = enterEventId;
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

    @Override
    public int hashCode() {
        return Objects.hash(enterEventId);
    }
}
