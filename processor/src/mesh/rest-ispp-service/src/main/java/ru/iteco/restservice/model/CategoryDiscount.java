/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_categorydiscounts")
public class CategoryDiscount {
    public static final Long RESERVE_DISCOUNT_ID = 50L;
    public static final Long ELEM_DISCOUNT_ID = -90L;
    public static final Long MIDDLE_DISCOUNT_ID = -91L;
    public static final Long HIGH_DISCOUNT_ID = -92L;

    @Id
    @Column(name = "idofcategorydiscount")
    private Long idOfCategoryDiscount;

    @Column(name = "categoryname")
    private String categoryName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cf_clients_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofcategorydiscount"),
            inverseJoinColumns = @JoinColumn(name = "idofclient")
    )
    private Set<Client> clients;

    @OneToOne(mappedBy = "categoryDiscount", fetch = FetchType.LAZY)
    private CategoryDiscountDTSZN categoryDiscountDTSZN;

    @Column(name = "categorytype")
    private Integer categoryType;

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public Long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryDiscountDTSZN getCategoryDiscountDTSZN() {
        return categoryDiscountDTSZN;
    }

    public void setCategoryDiscountDTSZN(CategoryDiscountDTSZN categoryDiscountDTSZN) {
        this.categoryDiscountDTSZN = categoryDiscountDTSZN;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDiscount that = (CategoryDiscount) o;
        return Objects.equals(idOfCategoryDiscount, that.idOfCategoryDiscount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCategoryDiscount);
    }
}
