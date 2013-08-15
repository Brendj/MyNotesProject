/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientListEditPage;
import ru.axetta.ecafe.processor.web.ui.discount.SetupDiscountPage;
import ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreatePanel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.component.UIModalPanel;
import org.richfaces.component.html.HtmlPanelMenu;
import org.richfaces.function.RichFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.07.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class MainPage {
    private static final Logger logger = LoggerFactory.getLogger(MainPage.class);

    private HtmlPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();
    private Long idoforg = null;

    public void setIdoforg (Long idoforg) {
        this.idoforg = idoforg;
    }
    public Long getIdoforg () {
        return idoforg;
    }


    public static MainPage getSessionInstance() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (MainPage) context.getApplication().createValueBinding("#{mainPage}").getValue(context);
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
     *                                     Список страниц
     ******************************************************************************************************************/
    private final ClientListEditPage clientListEditPage = new ClientListEditPage();
    private final SetupDiscountPage setupDiscountPage = new SetupDiscountPage ();

    public SetupDiscountPage getSetupDiscountPage() {
        return setupDiscountPage;
    }

    public ClientListEditPage getClientListEditPage() {
        return clientListEditPage;
    }



    /*******************************************************************************************************************
     *                                     Отображение страниц
     ******************************************************************************************************************/
    public void doShowSelectCreateGroupModal () {
        GroupCreatePanel panel = RuntimeContext.getAppContext().getBean(GroupCreatePanel.class);
        panel.fill();
        panel.addCallbackListener(currentWorkspacePage);
        /*panel.show();
        UIModalPanel modal = (UIModalPanel) panel.getPageComponent();
        modal.*/
    }

    public Object doShowSetupDiscountPage () {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            setupDiscountPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = setupDiscountPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected user group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы пользователя",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }
}
