/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_discountchangehistory")
public class DiscountChangeHistory {
    @Id
    @Column(name = "idofdiscountchange")
    private Long idOfDiscountChange;

    @ManyToOne
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "registrationdate")
    private Long registrationDate;

    @Column(name = "discountmode")
    private Integer discountMode;

    @Column(name = "olddiscountmode")
    private Integer oldDiscountMode;

    @Column(name = "categoriesdiscounts", length = 60)
    private String categoriesDiscounts;

    @Column(name = "oldcategoriesdiscounts", length = 60)
    private String oldCategoriesDiscounts;

    @Column(name = "comment")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getIdOfDiscountChange() {
        return idOfDiscountChange;
    }

    public void setIdOfDiscountChange(Long idOfDiscountChange) {
        this.idOfDiscountChange = idOfDiscountChange;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(Integer discountMode) {
        this.discountMode = discountMode;
    }

    public Integer getOldDiscountMode() {
        return oldDiscountMode;
    }

    public void setOldDiscountMode(Integer oldDiscountMode) {
        this.oldDiscountMode = oldDiscountMode;
    }

    public String getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(String categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public String getOldCategoriesDiscounts() {
        return oldCategoriesDiscounts;
    }

    public void setOldCategoriesDiscounts(String oldCategoriesDiscounts) {
        this.oldCategoriesDiscounts = oldCategoriesDiscounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiscountChangeHistory that = (DiscountChangeHistory) o;
        return Objects.equals(idOfDiscountChange, that.idOfDiscountChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfDiscountChange);
    }
}
