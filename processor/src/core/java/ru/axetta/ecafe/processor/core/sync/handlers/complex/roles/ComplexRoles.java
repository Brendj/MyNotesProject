/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.roles;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.07.13
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRoles {

    private final List<ComplexRoleItem> complexRoleItemList;

    public ComplexRoles(List<ComplexRoleItem> complexRoleItemList) {
        this.complexRoleItemList = complexRoleItemList;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ComplexesRole");
        for (ComplexRoleItem item : this.complexRoleItemList) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

}
