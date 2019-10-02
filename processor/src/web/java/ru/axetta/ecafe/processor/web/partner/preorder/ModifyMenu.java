/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

/**
 * Created by nuc on 23.09.2019.
 */
public class ModifyMenu {
    private Long newIdOfMenu;
    private Long idOfPreorderMenuDetail;
    private Long idOfPreorderComplex;

    public ModifyMenu(Long newIdOfMenu, Long idOfPreorderMenuDetail, Long idOfPreorderComplex) {
        this.newIdOfMenu = newIdOfMenu;
        this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
        this.idOfPreorderComplex = idOfPreorderComplex;
    }

    public Long getNewIdOfMenu() {
        return newIdOfMenu;
    }

    public void setNewIdOfMenu(Long newIdOfMenu) {
        this.newIdOfMenu = newIdOfMenu;
    }

    public Long getIdOfPreorderMenuDetail() {
        return idOfPreorderMenuDetail;
    }

    public void setIdOfPreorderMenuDetail(Long idOfPreorderMenuDetail) {
        this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }
}
