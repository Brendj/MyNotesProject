/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 03.02.2020
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

public class ResMenuSupplierItem {

    private Long version;
    private Boolean deletedState;
    private Integer resultCode;
    private String errorMessage;

    public ResMenuSupplierItem() {

    }

    //public ResMenuSupplierItem(ReestrMenuSupplier menuSupplier) {
    //    this.version = menuSupplier.getVersion();
    //    this.deletedState = menuSupplier.getDeletedState();
    //}

    //public ResMenuSupplierItem(ReestrMenuSupplier menuSupplier, Integer resCode) {
    //    this.version = menuSupplier.getVersion();
    //    this.deletedState = menuSupplier.getDeletedState();
    //    this.resultCode = resCode;
    //}

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);

        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);

        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

}
