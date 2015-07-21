/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 20.07.15
 * Time: 16:59
 */

@XmlRootElement(name = "MenuListWithComplexesResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class MenuListWithComplexesResult extends Result {

    @XmlElement(name = "MenuWithComplexesList")
    private MenuWithComplexesList menuWithComplexesList = new MenuWithComplexesList();

    public MenuWithComplexesList getMenuWithComplexesList() {
        return menuWithComplexesList;
    }

    public void setMenuWithComplexesList(MenuWithComplexesList menuWithComplexesList) {
        this.menuWithComplexesList = menuWithComplexesList;
    }
}
