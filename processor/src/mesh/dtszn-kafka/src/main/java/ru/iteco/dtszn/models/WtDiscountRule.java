/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_discountrules")
public class WtDiscountRule {
    @Id
    @Column(name = "idofrule")
    private Long idOfRule;

    @Column(name = "description")
    private String description;

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtDiscountRule that = (WtDiscountRule) o;
        return Objects.equals(idOfRule, that.idOfRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfRule);
    }
}
