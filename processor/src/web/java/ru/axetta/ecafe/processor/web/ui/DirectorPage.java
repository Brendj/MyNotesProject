/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.director.*;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.component.UIPanelMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.09.16
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("session")
public class DirectorPage implements Serializable {

    Logger logger = LoggerFactory.getLogger(DirectorPage.class);
    private final DirectorUseCardsPage directorUseCardsPage = new DirectorUseCardsPage();
    private final DirectorDiscountFoodPage directorDiscountFoodPage = new DirectorDiscountFoodPage();
    private final DirectorStaffAttendancePage directorStaffAttendancePage = new DirectorStaffAttendancePage();
    private final DirectorStudentAttendancePage directorStudentAttendancePage = new DirectorStudentAttendancePage();
    private final DirectorFinancePage directorFinancePage = new DirectorFinancePage();

    private UIComponent pageComponent;
    private UIPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();
    private Stack<BasicPage> modalPages = new Stack<BasicPage>();
    private String orgFilterOfSelectOrgListSelectPage = "";
    private final DirectorOrgListSelectPage orgListSelectPage = new DirectorOrgListSelectPage();

    private List<Long> idOfOrgList = new ArrayList<Long>();
    private String filter;
    private String orgFilterPageName = "";


    public UIComponent getPageComponent() {
        return pageComponent;
    }

    public void setPageComponent(UIComponent pageComponent) {
        this.pageComponent = pageComponent;
    }

    public DirectorUseCardsPage getDirectorUseCardsPage() {
        return directorUseCardsPage;
    }

    public DirectorStudentAttendancePage getDirectorStudentAttendancePage() {
        return directorStudentAttendancePage;
    }

    public DirectorFinancePage getDirectorFinancePage() {
        return directorFinancePage;
    }

    public Object showUseCardsPage() {
        directorUseCardsPage.loadOrganizations();
        return showDirectorReportPage(directorUseCardsPage);
    }

    public Object showDirectorDiscountFoodPage() {
        directorDiscountFoodPage.loadOrganizations();
        return showDirectorReportPage(directorDiscountFoodPage);
    }

    public Object showDirectorStaffAttendancePage() {
        directorStaffAttendancePage.loadOrganizations();
        return showDirectorReportPage(directorStaffAttendancePage);
    }

    public Object showDirectorStudentAttendancePage() {
        directorStudentAttendancePage.loadOrganizations();
        return showDirectorReportPage(directorStudentAttendancePage);
    }

    public Object showDirectorFinancePage() {
        directorFinancePage.loadOrganizations();
        return showDirectorReportPage(directorFinancePage);
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

    public BasicPage getTopMostPage() {
        BasicPage currentTopMostPage = currentWorkspacePage;
        if (!modalPages.isEmpty()) {
            currentTopMostPage = modalPages.peek();
        }
        return currentTopMostPage;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            setIdOfOrgList(new ArrayList<Long>());
            if (orgMap.isEmpty())
                setFilter("Не выбрано");
            else {
                setFilter("");
                for(Long idOfOrg : orgMap.keySet()) {
                    getIdOfOrgList().add(idOfOrg);
                    setFilter(getFilter().concat(orgMap.get(idOfOrg) + "; "));
                }
                setFilter(getFilter().substring(0, getFilter().length() - 1));
            }
        }
    }

    public Object showOrgListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof OrgListSelectPage.CompleteHandlerList) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                orgListSelectPage.setFilter("");
                orgListSelectPage.setIdFilter("");
                orgListSelectPage.setRegion("");
                if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
                    orgListSelectPage.fill(persistenceSession, false, null, null);
                } else {
                    getOrgListSelectPage()
                            .fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, false, null,
                                    null, this);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgListSelectPage.pushCompleteHandlerList((DirectorOrgListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(orgListSelectPage);
            } catch (Exception e) {
                logger.error("Failed to fill org selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора организации: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        return null;
    }

    public void setOrgFilterOfSelectOrgListSelectPage(String orgFilterOfSelectOrgListSelectPage) {
        this.orgFilterOfSelectOrgListSelectPage = orgFilterOfSelectOrgListSelectPage;
    }

    public Object updateOrgListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
                orgListSelectPage.fill(persistenceSession, true, null, null);
            } else {
                orgListSelectPage
                        .fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, true, null,
                                null, this);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill org selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object completeOrgListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            orgListSelectPage.completeOrgListSelection(false);
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete orgs selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора организаций: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeOrgListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            orgListSelectPage.completeOrgListSelection(true);
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete orgs selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора организаций: " + e.getMessage(), null));
        }
        return null;
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

    public UIPanelMenu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(UIPanelMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public BasicWorkspacePage getCurrentWorkspacePage() {
        return currentWorkspacePage;
    }

    public DirectorDiscountFoodPage getDirectorDiscountFoodPage() {
        return directorDiscountFoodPage;
    }

    public DirectorOrgListSelectPage getOrgListSelectPage() {
        return orgListSelectPage;
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public void setIdOfOrgList(List<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getOrgFilterPageName() {
        return orgFilterPageName;
    }

    public void setOrgFilterPageName(String orgFilterPageName) {
        this.orgFilterPageName = orgFilterPageName;
    }

    public DirectorStaffAttendancePage getDirectorStaffAttendancePage() {
        return directorStaffAttendancePage;
    }

    public Object clearOrgListSelectedItemsList() {
        orgFilterOfSelectOrgListSelectPage = "";
        orgListSelectPage.deselectAllItems();
        updateOrgListSelectPage();
        return null;
    }

    public Object selectAllOrgListSelectedItemsList() {
        orgListSelectPage.selectAllItems();
        updateOrgListSelectPage();
        return null;
    }
}
