/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import org.richfaces.component.UICommandLink;
import org.hibernate.Session;
import org.richfaces.component.UIPanelMenuGroup;
import org.richfaces.component.UIPanelMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class BasicWorkspacePage extends BasicPage {
    Logger logger = LoggerFactory.getLogger(BasicWorkspacePage.class);

    private static final String MENU_PATH_SEPARATOR = " / ";
    private UIComponent mainMenuComponent;


    public BasicWorkspacePage() {
    }

    public String getPageFilename() {
        return "default";
    }

    public UIComponent getMainMenuComponent() {
        return mainMenuComponent;
    }

    public void setMainMenuComponent(UIComponent mainMenuComponent) {
        this.mainMenuComponent = mainMenuComponent;
    }

    public String getPageTitle() {
        return buildMenuComponentPath(mainMenuComponent);
    }

    public void fill(Session session) throws Exception {

    }

    private static String buildMenuComponentPath(UIComponent menuComponent) {
        if (null == menuComponent) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        boolean done = false;
        while (!done) {
            String itemTitle = null;
            if (menuComponent instanceof UIPanelMenuItem) {
                UIPanelMenuItem currentMenuItem = (UIPanelMenuItem) menuComponent;
                itemTitle = (String) currentMenuItem.getLabel();
            } else if (menuComponent instanceof UIPanelMenuGroup) {
                UIPanelMenuGroup currentMenuGroup = (UIPanelMenuGroup) menuComponent;
                itemTitle = currentMenuGroup.getLabel();
            } else if (menuComponent instanceof UICommandLink){ //for complexExtendedReport
                UICommandLink currentCommandLink = (UICommandLink) menuComponent;
                itemTitle = currentCommandLink.getTitle();
            }
            else {
                done = true;
                continue;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.insert(0, MENU_PATH_SEPARATOR);
            }
            if (null != itemTitle) {
                stringBuilder.insert(0, itemTitle);
            }
            menuComponent = menuComponent.getParent();
        }
        return stringBuilder.toString();
    }

    void showAndExpandMenuGroup(UIComponent menuComponent) {
        if (menuComponent !=null && (menuComponent instanceof UIPanelMenuGroup)) {
            UIPanelMenuGroup menuGroup = (UIPanelMenuGroup) menuComponent;
            if (!menuGroup.isRendered()) {
                menuGroup.setRendered(true);
                menuGroup.setExpanded(true);
                for (UIComponent uiComponent : menuGroup.getChildren()) {
                    uiComponent.setRendered(true);
                }
            }
        }
    }
    public void showAndExpandMenuGroup() {
        showAndExpandMenuGroup(getMainMenuComponent());
    }
    public void showAndExpandParentMenuGroup() {
        UIComponent uiComponent = getMainMenuComponent();
        if (uiComponent!=null) showAndExpandMenuGroup(uiComponent.getParent());
    }

    public void hideMenuGroup() {
        UIComponent menuComponent = getMainMenuComponent();
        if (menuComponent instanceof UIPanelMenuGroup) {
            UIPanelMenuGroup menuGroup = (UIPanelMenuGroup) menuComponent;
            menuGroup.setRendered(false);
        }
    }

    public void onShow() throws Exception {
    }

    public Object show() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            this.onShow();
            showAndExpandParentMenuGroup();
            MainPage.getSessionInstance().setCurrentWorkspacePage(this);
            MainPage.getSessionInstance().updateSelectedMainMenu();
        } catch (Exception e) {
            logger.error("Failed to load page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: "+e,
                            null));
        }
        return null;
    }

    public void printMessage(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
    public void printMessageFor(String componentId, String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(componentId,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public void printWarn(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }

    public Logger getLogger() {
        return logger;
    }

    public void logAndPrintMessage(String msg, Exception e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        logger.error(msg, e);
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg+(e==null?"":": "+e.getMessage()), null));
    }
    public void logAndPrintMessageFor(String componentId, String msg, Exception e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        logger.error(msg, e);
        facesContext.addMessage(componentId,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg+(e==null?"":": "+e), null));
    }

}
