/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.10.14
 * Time: 13:40
 */

public class ClientInfo {

      public  Long clientId;
      public  Long groupId;
      public  String groupName;
      public  String categoriesDiscounts;

    public ClientInfo(Long clientId, Long groupId, String groupName, String categoriesDiscounts) {
        this.clientId = clientId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(String categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }
}
