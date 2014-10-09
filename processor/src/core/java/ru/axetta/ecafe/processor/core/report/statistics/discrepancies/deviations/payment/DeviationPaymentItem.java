/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.10.14
 * Time: 11:30
 */

public class DeviationPaymentItem {

    private Long idOfOrg;
    private String orgShortName;
    private String address;
    private String groupName; // группа клиента (класс, сотрудники и т.д.)

    public DeviationPaymentItem() {
    }

    public DeviationPaymentItem(Long idOfOrg, String orgShortName, String address, String groupName) {
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.address = address;
        this.groupName = groupName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
