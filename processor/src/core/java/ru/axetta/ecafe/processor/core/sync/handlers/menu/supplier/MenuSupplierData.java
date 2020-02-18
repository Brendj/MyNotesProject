/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplierData implements AbstractToElement {
    private ResMenuSupplier resMenuSupplier;

    public MenuSupplierData(MenuSupplier menuSupplier) {
        resMenuSupplier = new ResMenuSupplier(menuSupplier);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        return resMenuSupplier.toElement(document);
    }

    public ResMenuSupplier getMenuSupplier() {
        return resMenuSupplier;
    }

    public void setMenuSupplier(ResMenuSupplier resMenuSupplier) {
        this.resMenuSupplier = resMenuSupplier;
    }
}
