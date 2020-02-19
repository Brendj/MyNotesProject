/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_wt_complexes_items")
public class WtComplexesItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfComplexItem")
    private Long idOfComplexItem;

    @ManyToOne
    @JoinColumn(name = "idOfComplex")
    private WtComplex wtComplex;

    @Column(name = "cycle_day")
    private Integer cycleDay;

    @Column(name = "count_dishes")
    private Integer countDishes;

    @ManyToMany
    @JoinTable(name = "cf_wt_complex_items_dish",
            joinColumns = @JoinColumn(name = "idOfComplexItem"),
            inverseJoinColumns = @JoinColumn(name = "idOfDish"))
    private Set<WtDish> dishes = new HashSet<>();

    public Long getIdOfComplexItem() {
        return idOfComplexItem;
    }

    public void setIdOfComplexItem(Long idOfComplexItem) {
        this.idOfComplexItem = idOfComplexItem;
    }

    public Integer getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(Integer cycleDay) {
        this.cycleDay = cycleDay;
    }

    public Integer getCountDishes() {
        return countDishes;
    }

    public void setCountDishes(Integer countDishes) {
        this.countDishes = countDishes;
    }

    public WtComplex getWtComplex() {
        return wtComplex;
    }

    public void setWtComplex(WtComplex wtComplex) {
        this.wtComplex = wtComplex;
    }

    public Set<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<WtDish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtComplexesItem that = (WtComplexesItem) o;
        return Objects.equals(idOfComplexItem, that.idOfComplexItem) && Objects.equals(cycleDay, that.cycleDay)
                && Objects.equals(countDishes, that.countDishes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplexItem, cycleDay, countDishes);
    }
}
