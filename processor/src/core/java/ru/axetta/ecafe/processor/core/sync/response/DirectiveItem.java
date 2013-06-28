/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.06.13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class DirectiveItem {

    private final String name;
    private final String value;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("D");
        element.setAttribute("name", name);
        element.setAttribute("value", value);
        return element;
    }

    public DirectiveItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("DirectiveItem{name='%s', value='%s'}", name, value);
    }
}
