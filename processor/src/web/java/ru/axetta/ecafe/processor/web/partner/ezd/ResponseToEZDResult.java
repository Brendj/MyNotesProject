/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

public class ResponseToEZDResult extends Result {
    private String guidOrg; //Guid организации
    private String groupName; //Наименование группы обучающихся
    private String date; //Дата на которую созданы заявки
    private Long idOfComplex; //Идентификатор рациона

    public String getGuidOrg() {
        return guidOrg;
    }

    public void setGuidOrg(String guidOrg) {
        this.guidOrg = guidOrg;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }
}
