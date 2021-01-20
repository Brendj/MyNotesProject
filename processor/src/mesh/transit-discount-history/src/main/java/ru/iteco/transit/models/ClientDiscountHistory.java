/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.models;

import ru.iteco.transit.models.enums.OperationType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_client_discount_history")
public class ClientDiscountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idofclientdiscounthistory")
    private Long idOfClientDiscountHistory;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "operationtype", nullable = false)
    private OperationType operationType;

    @Column(name = "registrydate", nullable = false)
    private Long registryDate;

    @ManyToOne()
    @JoinColumn(name = "idofclient", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "idofcategorydiscount", nullable = false)
    private CategoryDiscount categoryDiscount;

    @Column(name = "comment", length = 128, nullable = false)
    private String comment;

    public Long getIdOfClientDiscountHistory() {
        return idOfClientDiscountHistory;
    }

    public void setIdOfClientDiscountHistory(Long idOfClientDiscountHistory) {
        this.idOfClientDiscountHistory = idOfClientDiscountHistory;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Long getRegistryDate() {
        return registryDate;
    }

    public void setRegistryDate(Long registryDate) {
        this.registryDate = registryDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDiscountHistory that = (ClientDiscountHistory) o;
        return Objects.equals(idOfClientDiscountHistory, that.idOfClientDiscountHistory) &&
                operationType == that.operationType &&
                registryDate.equals(that.registryDate) &&
                client.equals(that.client) &&
                categoryDiscount.equals(that.categoryDiscount) &&
                comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClientDiscountHistory, operationType, registryDate, client, categoryDiscount, comment);
    }
}

