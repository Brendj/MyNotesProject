/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public class Products {

    private Long idOfProducts;
    private Long idOfTechnologicalMap;
    private Integer index;
    private Product product1;
    private Product product2;
    private Product product3;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getIdOfProducts() {
        return idOfProducts;
    }

    public void setIdOfProducts(Long idOfProducts) {
        this.idOfProducts = idOfProducts;
    }

    public Long getIdOfTechnologicalMap() {
        return idOfTechnologicalMap;
    }

    public void setIdOfTechnologicalMap(Long idOfTechnologicalMap) {
        this.idOfTechnologicalMap = idOfTechnologicalMap;
    }

    public Product getProduct1() {
        return product1;
    }

    public void setProduct1(Product product1) {
        this.product1 = product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public void setProduct2(Product product2) {
        this.product2 = product2;
    }

    public Product getProduct3() {
        return product3;
    }

    public void setProduct3(Product product3) {
        this.product3 = product3;
    }
}
