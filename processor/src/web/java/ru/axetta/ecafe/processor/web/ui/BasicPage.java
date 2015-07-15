/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.security.Principal;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class BasicPage implements Serializable {

    private UIComponent pageComponent;

    public UIComponent getPageComponent() {
        return pageComponent;
    }

    public void setPageComponent(UIComponent pageComponent) {
        this.pageComponent = pageComponent;
    }

    public String getCurrentUserNot(){
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        String userName = currentInstance.getExternalContext().getRemoteUser();
        return userName;
    }
}