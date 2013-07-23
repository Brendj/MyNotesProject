/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.roles;

import ru.axetta.ecafe.processor.core.persistence.ComplexRole;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.07.13
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRoleItem {

    private final Long id;
    private final String name;

    public ComplexRoleItem(ComplexRole complexRole) {
        this.id = complexRole.getIdOfRole();
        this.name = complexRole.getRoleName();
    }

    public ComplexRoleItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("CMR");
        element.setAttribute("IdOfComplex", Long.toString(this.id));
        element.setAttribute("NameRole", this.name);
        return element;
    }
}
