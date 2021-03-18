/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import ru.iteco.msp.enums.OperationType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_client_discount_history")
public class ClientDiscountHistory {
    @Id
    @Column(name = "idofclientdiscounthistory")
    private Long idOfClientDiscountHistory;

    @Column(name = "registrydate", nullable = false)
    private Long registryDate;

    @Column(name = "comment", length = 128, nullable = false)
    private String comment;

    @Column(name = "operationtype", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private OperationType operationType;

    @ManyToOne
    @JoinColumn(name = "idofcategorydiscount", nullable = false)
    private CategoryDiscount categoryDiscount;

    @ManyToOne
    @JoinColumn(name = "idofclient", nullable = false)
    private Client client;

    public Long getIdOfClientDiscountHistory() {
        return idOfClientDiscountHistory;
    }

    public void setIdOfClientDiscountHistory(Long idOfClientDiscountHistory) {
        this.idOfClientDiscountHistory = idOfClientDiscountHistory;
    }

    public Long getRegistryDate() {
        return registryDate;
    }

    public void setRegistryDate(Long registryDate) {
        this.registryDate = registryDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
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
        ClientDiscountHistory that = (ClientDiscountHistory) o;
        return Objects.equals(idOfClientDiscountHistory, that.idOfClientDiscountHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClientDiscountHistory);
    }
}
