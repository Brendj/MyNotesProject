/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_preorder_flags")
public class PreorderFlag {
    @Id
    @Column(name = "idofpreorderflag")
    private Long idOfPreorderFlag;

    @Column(name = "allowedpreorder")
    private Integer allowedPreorder;

    @OneToOne()
    @JoinColumn(name = "idofclient")
    private Client client;

    public Long getIdOfPreorderFlag() {
        return idOfPreorderFlag;
    }

    public void setIdOfPreorderFlag(Long idOfPreorderFlag) {
        this.idOfPreorderFlag = idOfPreorderFlag;
    }

    public Integer getAllowedPreorder() {
        return allowedPreorder;
    }

    public void setAllowedPreorder(Integer allowedPreorder) {
        this.allowedPreorder = allowedPreorder;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreorderFlag that = (PreorderFlag) o;
        return Objects.equals(idOfPreorderFlag, that.idOfPreorderFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfPreorderFlag);
    }
}
