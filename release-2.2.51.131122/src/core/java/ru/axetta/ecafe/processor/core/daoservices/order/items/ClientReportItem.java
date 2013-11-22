/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.05.13
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "idOfOrderDetail",
        "contractId",
        "fio",
        "menuName",
        "menuOrigin",
        "price"
})
public class ClientReportItem {

    // ЗаказИД
    @XmlElement
    protected Long idOfOrderDetail;

    // КонтракИД
    @XmlElement
    protected Long contractId;

    //  ФИО клиента
    @XmlElement
    protected String fio;

    //Название Блюда
    @XmlElement
    protected String menuName;

    // Тип производсва
    @XmlElement
    protected String menuOrigin;

    // Cумма
    @XmlElement
    protected Float price;

    public ClientReportItem() {}

    public ClientReportItem(Long idOfOrderDetail, Long contracId, String fio, String menuName,
            String menuOrigin, Float price) {
        this.idOfOrderDetail = idOfOrderDetail;
        this.contractId = contracId;
        this.fio = fio;
        this.menuName = menuName;
        this.menuOrigin = menuOrigin;
        this.price = price;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(String menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

}
