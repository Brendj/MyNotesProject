/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.web.ui.director.DirectorUseCardsPage;
import ru.axetta.ecafe.processor.web.ui.director.DiscountFoodPage;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlPanelMenu;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.09.16
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */

public class DirectorPage extends BasicWorkspacePage {

    private final DirectorUseCardsPage directorUseCardsPage = new DirectorUseCardsPage();
    private final DiscountFoodPage discountFoodPage = new DiscountFoodPage();

    private UIComponent pageComponent;
    private HtmlPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();

    public UIComponent getPageComponent() {
        return pageComponent;
    }

    public void setPageComponent(UIComponent pageComponent) {
        this.pageComponent = pageComponent;
    }

    public DirectorUseCardsPage getDirectorUseCardsPage() {
        return directorUseCardsPage;
    }

    public Object showUseCardsPage() {
        return showDirectorReportPage(directorUseCardsPage);
    }

    public Object showDiscountFoodPage() {
        return showDirectorReportPage(discountFoodPage);
    }

    private Object showDirectorReportPage(BasicWorkspacePage page) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = page;
        } catch (Exception e) {
            logger.error("Failed to set director report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка открытии страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public void updateSelectedMainMenu() {
        UIComponent mainMenuComponent = currentWorkspacePage.getMainMenuComponent();
        if (null != mainMenuComponent) {
            mainMenu.setValue(mainMenuComponent.getId());
        }
    }

    public String logout() throws Exception {
        String outcome = "logout";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext facesExternalContext = facesContext.getExternalContext();
        HttpSession httpSession = (HttpSession) facesExternalContext.getSession(false);
        if (null != httpSession && StringUtils.isNotEmpty(facesExternalContext.getRemoteUser())) {
            httpSession.invalidate();
            ((HttpServletRequest)facesExternalContext.getRequest()).logout();
        }
        return outcome;
    }

    public HtmlPanelMenu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(HtmlPanelMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public BasicWorkspacePage getCurrentWorkspacePage() {
        return currentWorkspacePage;
    }

    public DiscountFoodPage getDiscountFoodPage() {
        return discountFoodPage;
    }
}
