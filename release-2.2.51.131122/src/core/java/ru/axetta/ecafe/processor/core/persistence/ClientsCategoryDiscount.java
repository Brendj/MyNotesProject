/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.02.12
 * Time: 0:38
 * To change this template use File | Settings | File Templates.
 */
public class ClientsCategoryDiscount {

    public Long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    private Long idOfCategoryDiscount;

    private Long idOfClient;

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfClientsCategoryDiscount() {
        return idOfClientsCategoryDiscount;
    }

    public void setIdOfClientsCategoryDiscount(Long idOfClientsCategoryDiscount) {
        this.idOfClientsCategoryDiscount = idOfClientsCategoryDiscount;
    }

    private Long idOfClientsCategoryDiscount;
}
