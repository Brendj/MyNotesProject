/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResponseToEZDResult", propOrder = {
        "guidOrg",
        "groupName",
        "userName",
        "date",
        "idOfComplex",
        "complexName",
        "count"
})
public class ResponseToEZDResult extends Result {
    private String guidOrg; //Guid организации
    private String groupName; //Наименование группы обучающихся
    private String date; //Дата на которую созданы заявки
    private String userName; //ФИО пользователя
    private Long idOfComplex; //Идентификатор рациона
    private String complexName; //Наименование рациона
    private Integer count; //Количество рационов


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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
