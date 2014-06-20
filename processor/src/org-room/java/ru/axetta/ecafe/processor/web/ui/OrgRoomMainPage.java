/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.modal.YesNoConfirmPanel;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.ClientFeedActionPanel;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.DisableComplexPanel;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.OrderRegistrationResultPanel;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.ReplaceClientPanel;
import ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreatePanel;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlPanelMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.07.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgRoomMainPage {
    private static final Logger logger = LoggerFactory.getLogger(OrgRoomMainPage.class);

    private HtmlPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();


    public static OrgRoomMainPage getSessionInstance() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (OrgRoomMainPage) context.getApplication().createValueBinding("#{orgRoomMainPage}").getValue(context);
    }

    public BasicWorkspacePage getCurrentWorkspacePage() {
        return currentWorkspacePage;
    }


    public void setCurrentWorkspacePage(BasicWorkspacePage page) {
        this.currentWorkspacePage = page;
        updateSelectedMainMenu();
    }

    public void updateSelectedMainMenu() {
        /* Меню не определено !!!
        /*UIComponent mainMenuComponent = currentWorkspacePage.getMainMenuComponent();
        if (null != mainMenuComponent) {
            mainMenu.setValue(mainMenuComponent.getId());
        }*/
    }

    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext facesExternalContext = facesContext.getExternalContext();
        HttpSession httpSession = (HttpSession) facesExternalContext.getSession(false);
        if (null != httpSession && StringUtils.isNotEmpty(facesExternalContext.getRemoteUser())) {
            httpSession.invalidate();
        }
        return "logout";
    }







    /*******************************************************************************************************************
     *                                     Отображение страниц
     ******************************************************************************************************************/
    public void doShowSelectCreateGroupModal () {
        GroupCreatePanel panel = RuntimeContext.getAppContext().getBean(GroupCreatePanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
    }

    public void doShowYesNoConfirmModal (ActionEvent actionEvent) {
        doShowYesNoConfirmModal();
    }

    public void doShowYesNoConfirmModal () {
        YesNoConfirmPanel panel = RuntimeContext.getAppContext().getBean(YesNoConfirmPanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
    }

    public void doShowClientFeedActionPanel() {
        ClientFeedActionPanel panel = RuntimeContext.getAppContext().getBean(ClientFeedActionPanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
    }

    public void doShowDisableComplexPanel() {
        DisableComplexPanel panel = RuntimeContext.getAppContext().getBean(DisableComplexPanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
    }

    public void doShowReplaceClientPanel() {
        ReplaceClientPanel panel = RuntimeContext.getAppContext().getBean(ReplaceClientPanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
    }

    public void doShowOrderRegistrationResultPanel () {
        doShowOrderRegistrationResultPanel(currentWorkspacePage);
    }

    public void doShowOrderRegistrationResultPanel (BasicWorkspacePage page) {
        OrderRegistrationResultPanel panel = RuntimeContext.getAppContext().getBean(OrderRegistrationResultPanel.class);
        panel.fill();
        panel.addCallbackListener(page);
    }

    public User getCurrentUser() throws Exception {
        return null;
    }

    public boolean isEligibleToEditReports() throws Exception {
        return Boolean.FALSE;
    }
}
