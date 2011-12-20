/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import org.richfaces.component.html.HtmlPanelMenuGroup;
import org.richfaces.component.html.HtmlPanelMenuItem;

import javax.faces.component.UIComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class BasicWorkspacePage extends BasicPage {

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

    private static String buildMenuComponentPath(UIComponent menuComponent) {
        if (null == menuComponent) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        boolean done = false;
        while (!done) {
            String itemTitle = null;
            if (menuComponent instanceof HtmlPanelMenuItem) {
                HtmlPanelMenuItem currentMenuItem = (HtmlPanelMenuItem) menuComponent;
                itemTitle = (String) currentMenuItem.getLabel();
            } else if (menuComponent instanceof HtmlPanelMenuGroup) {
                HtmlPanelMenuGroup currentMenuGroup = (HtmlPanelMenuGroup) menuComponent;
                itemTitle = currentMenuGroup.getLabel();
            } else {
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

    public void showAndExpandMenuGroup() {
        UIComponent menuComponent = getMainMenuComponent();
        if (menuComponent instanceof HtmlPanelMenuGroup) {
            HtmlPanelMenuGroup menuGroup = (HtmlPanelMenuGroup) menuComponent;
            menuGroup.setRendered(true);
            menuGroup.setExpanded(true);
            for (UIComponent uiComponent : menuGroup.getChildren()) {
                uiComponent.setRendered(true);
            }
        }
    }

    public void hideMenuGroup() {
        UIComponent menuComponent = getMainMenuComponent();
        if (menuComponent instanceof HtmlPanelMenuGroup) {
            HtmlPanelMenuGroup menuGroup = (HtmlPanelMenuGroup) menuComponent;
            menuGroup.setRendered(false);
        }
    }
}
