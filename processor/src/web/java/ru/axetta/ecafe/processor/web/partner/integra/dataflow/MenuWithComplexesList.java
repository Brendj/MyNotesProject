/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.07.15
 * Time: 10:15
 */

@XmlRootElement(name = "MenuWithComplexesList")
@XmlAccessorType(XmlAccessType.FIELD)
public class MenuWithComplexesList {

    @XmlElement(name = "ComplexInfo")
    private List<MenuWithComplexesExt> list;

    public List<MenuWithComplexesExt> getList() {
        return list;
    }

    public void setList(List<MenuWithComplexesExt> list) {
        this.list = list;
    }
}
