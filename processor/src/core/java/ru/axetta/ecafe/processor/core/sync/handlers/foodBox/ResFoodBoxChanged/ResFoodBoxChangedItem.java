package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged;
/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class ResFoodBoxChangedItem {

    private Long id;
    private Integer Res;
    private String error;
    private Long version;

    public ResFoodBoxChangedItem() {
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Id", id);
        XMLUtils.setAttributeIfNotNull(element, "Res", Res);
        XMLUtils.setAttributeIfNotNull(element, "Error", error);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        return element;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRes() {
        return Res;
    }

    public void setRes(Integer res) {
        Res = res;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
