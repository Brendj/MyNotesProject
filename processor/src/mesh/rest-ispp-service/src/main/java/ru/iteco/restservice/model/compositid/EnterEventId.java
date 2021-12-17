/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.compositid;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EnterEventId implements Serializable {
    @Column(name = "idofenterevent")
    private Long idOfEnterEvent;

    @Column(name = "idoforg")
    private Long idOfOrg;

    public Long getIdOfEnterEvent() {
        return idOfEnterEvent;
    }

    public void setIdOfEnterEvent(Long idOfEnterEvent) {
        this.idOfEnterEvent = idOfEnterEvent;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnterEventId that = (EnterEventId) o;
        return Objects.equals(idOfEnterEvent, that.idOfEnterEvent) && Objects.equals(idOfOrg, that.idOfOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfEnterEvent, idOfOrg);
    }
}
