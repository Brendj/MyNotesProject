/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.compositid;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderDetailCompositeId implements Serializable {
    @Column(name = "idoforderdetail")
    private Long idOfOrderDetail;

    @Column(name = "idoforg")
    private Long idOfOrg;

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
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
        OrderDetailCompositeId that = (OrderDetailCompositeId) o;
        return Objects.equals(idOfOrderDetail, that.idOfOrderDetail) && Objects.equals(idOfOrg, that.idOfOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrderDetail, idOfOrg);
    }
}
