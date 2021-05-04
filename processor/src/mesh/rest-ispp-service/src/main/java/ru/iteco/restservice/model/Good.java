/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_goods")
public class Good {
    @Id
    @Column(name = "idofgood")
    private Long idOfGood;

    @Column(name = "nameofgood")
    private String nameOfGood;

    @OneToOne
    @JoinColumn(name = "idofgood", insertable = false, updatable = false)
    private MenuDetail menuDetail;

    public Long getIdOfGood() {
        return idOfGood;
    }

    public void setIdOfGood(Long idOfGood) {
        this.idOfGood = idOfGood;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public MenuDetail getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(MenuDetail menuDetail) {
        this.menuDetail = menuDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Good good = (Good) o;
        return Objects.equals(idOfGood, good.idOfGood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfGood);
    }
}
