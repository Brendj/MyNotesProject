/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import javax.faces.component.UIComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class BasicPage {

    private UIComponent pageComponent;

    public UIComponent getPageComponent() {
        return pageComponent;
    }

    public void setPageComponent(UIComponent pageComponent) {
        this.pageComponent = pageComponent;
    }
}