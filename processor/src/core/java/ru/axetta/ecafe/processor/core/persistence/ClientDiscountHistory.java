/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Objects;

public class ClientDiscountHistory {
    private Long idOfClientDiscountHistory;
    private ClientDiscountHistoryOperationTypeEnum operationType;
    private Date registryDate;
    private Client client;
    private CategoryDiscount categoryDiscount;
    private String comment;

    public static ClientDiscountHistory build(Client client, String comment, CategoryDiscount discount,
            ClientDiscountHistoryOperationTypeEnum type){
        ClientDiscountHistory history = new ClientDiscountHistory();
        history.setClient(client);
        history.setComment(comment);
        history.setRegistryDate(new Date());
        history.setCategoryDiscount(discount);
        history.setOperationType(type);

        return history;
    }

    public Long getIdOfClientDiscountHistory() {
        return idOfClientDiscountHistory;
    }

    public void setIdOfClientDiscountHistory(Long idOfClientDiscountHistory) {
        this.idOfClientDiscountHistory = idOfClientDiscountHistory;
    }

    public ClientDiscountHistoryOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(ClientDiscountHistoryOperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public Date getRegistryDate() {
        return registryDate;
    }

    public void setRegistryDate(Date registryDate) {
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
        return Objects.equals(idOfClientDiscountHistory, that.idOfClientDiscountHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClientDiscountHistory);
    }
}
