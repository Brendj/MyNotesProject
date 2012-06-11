/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class Product {

    private Long idOfProduct;
    //Наименование продукта
    private String name;
    //Масса брутто, г
    private Float grossMass;
    //Масса нетто, г
    private Float netMass;

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Long getIdOfProduct() {
        return idOfProduct;
    }

    public void setIdOfProduct(Long idOfProduct) {
        this.idOfProduct = idOfProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getGrossMass() {
        return grossMass;
    }

    public void setGrossMass(Float grossMass) {
        this.grossMass = grossMass;
    }

    public Float getNetMass() {
        return netMass;
    }

    public void setNetMass(Float netMass) {
        this.netMass = netMass;
    }
}
