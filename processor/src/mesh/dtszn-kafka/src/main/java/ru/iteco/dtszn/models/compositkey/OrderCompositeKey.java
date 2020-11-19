/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models.compositkey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderCompositeKey implements Serializable {
    @Column(name = "idoforder")
    private Long idOfOrder;

    @Column(name = "idoforg")
    private Long idOfOrg;

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
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
        OrderCompositeKey that = (OrderCompositeKey) o;
        return Objects.equals(idOfOrder, that.idOfOrder) && Objects.equals(idOfOrg, that.idOfOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrder, idOfOrg);
    }
}
