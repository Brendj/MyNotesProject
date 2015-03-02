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
    private String params=null;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("D");
        element.setAttribute("name", name);
        element.setAttribute("value", value);
        if (params != null){
            element.setAttribute("params", params);
        }
        return element;
    }

    public DirectiveItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DirectiveItem(String name, String value, String params) {
        this.name = name;
        this.value = value;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getParams() {
        return params;
    }

    @Override
    public String toString() {
        if(params == null){
            return String.format("DirectiveItem{name='%s', value='%s'}", name, value);
        }else {
            return String.format("DirectiveItem{name='%s', value='%s', params='%s'}", name, value,params);
        }

    }
}
