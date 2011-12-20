/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.addpayment.*;
import ru.axetta.ecafe.processor.web.ui.card.*;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountCreatePage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountDeletePage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFileLoadPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountListPage;
import ru.axetta.ecafe.processor.web.ui.client.*;
import ru.axetta.ecafe.processor.web.ui.client.category.*;
import ru.axetta.ecafe.processor.web.ui.client.rule.*;
import ru.axetta.ecafe.processor.web.ui.contragent.*;
import ru.axetta.ecafe.processor.web.ui.event.*;
import ru.axetta.ecafe.processor.web.ui.option.ConfigurationPage;
import ru.axetta.ecafe.processor.web.ui.option.OptionPage;
import ru.axetta.ecafe.processor.web.ui.org.*;
import ru.axetta.ecafe.processor.web.ui.pos.*;
import ru.axetta.ecafe.processor.web.ui.report.job.*;
import ru.axetta.ecafe.processor.web.ui.report.online.*;
import ru.axetta.ecafe.processor.web.ui.report.rule.*;
import ru.axetta.ecafe.processor.web.ui.service.*;
import ru.axetta.ecafe.processor.web.ui.settlement.*;
import ru.axetta.ecafe.processor.web.ui.user.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.richfaces.component.html.HtmlPanelMenu;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 05.06.2009
 * Time: 14:49:47
 * To change this template use File | Settings | File Templates.
 */
public class MainPage {

    private static final Logger logger = LoggerFactory.getLogger(MainPage.class);

    private HtmlPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();
    private Stack<BasicPage> modalPages = new Stack<BasicPage>();

    // User manipulation
    private final BasicWorkspacePage userGroupPage = new BasicWorkspacePage();
    private final UserListPage userListPage = new UserListPage();
    private Long selectedIdOfUser;
    private Long removedIdOfUser;
    private final SelectedUserGroupPage selectedUserGroupPage = new SelectedUserGroupPage();
    private final UserViewPage userViewPage = new UserViewPage();
    private final UserEditPage userEditPage = new UserEditPage();
    private final UserCreatePage userCreatePage = new UserCreatePage();

    // Org manipulation
    private final BasicWorkspacePage orgGroupPage = new BasicWorkspacePage();
    private final OrgListPage orgListPage = new OrgListPage();
    private Long selectedIdOfOrg;
    private final SelectedOrgGroupPage selectedOrgGroupPage = new SelectedOrgGroupPage();
    private final OrgViewPage orgViewPage = new OrgViewPage();
    private final OrgEditPage orgEditPage = new OrgEditPage();
    private final OrgCreatePage orgCreatePage = new OrgCreatePage();
    private final OrgBalanceReportPage orgBalanceReportPage = new OrgBalanceReportPage();
    private final OrgOrderReportPage orgOrderReportPage = new OrgOrderReportPage();

    // Contragent manipulation
    private final BasicWorkspacePage contragentGroupPage = new BasicWorkspacePage();
    private final ContragentListPage contragentListPage = new ContragentListPage();
    private Long selectedIdOfContragent;
    private final SelectedContragentGroupPage selectedContragentGroupPage = new SelectedContragentGroupPage();
    private final ContragentViewPage contragentViewPage = new ContragentViewPage();
    private final ContragentEditPage contragentEditPage = new ContragentEditPage();
    private final ContragentCreatePage contragentCreatePage = new ContragentCreatePage();
    private final ContragentClientPaymentReportPage contragentClientPaymentReportPage = new ContragentClientPaymentReportPage();

    // Contragent client account manipulation
    private final BasicWorkspacePage ccAccountGroupPage = new BasicWorkspacePage();
    private final CCAccountListPage ccAccountListPage = new CCAccountListPage();
    private CompositeIdOfContragentClientAccount removedIdOfCCAccount;
    private final CCAccountDeletePage ccAccountDeletePage = new CCAccountDeletePage();
    private final CCAccountCreatePage ccAccountCreatePage = new CCAccountCreatePage();
    private final CCAccountFileLoadPage ccAccountFileLoadPage = new CCAccountFileLoadPage();

    // Client manipulation
    private final BasicWorkspacePage clientGroupPage = new BasicWorkspacePage();
    private final ClientListPage clientListPage = new ClientListPage();
    private Long selectedIdOfClient;
    private final SelectedClientGroupPage selectedClientGroupPage = new SelectedClientGroupPage();
    private final ClientViewPage clientViewPage = new ClientViewPage();
    private final ClientEditPage clientEditPage = new ClientEditPage();
    private final ClientCreatePage clientCreatePage = new ClientCreatePage();
    private final ClientFileLoadPage clientFileLoadPage = new ClientFileLoadPage();
    private final ClientUpdateFileLoadPage clientUpdateFileLoadPage = new ClientUpdateFileLoadPage();
    private final ContractBuildPage contractBuildPage = new ContractBuildPage();
    private final ClientLimitBatchEditPage clientLimitBatchEditPage = new ClientLimitBatchEditPage();
    private final ClientSmsListPage clientSmsListPage = new ClientSmsListPage();
    private final ClientOperationListPage clientOperationListPage = new ClientOperationListPage();

    // Card manipulation
    private final BasicWorkspacePage cardGroupPage = new BasicWorkspacePage();
    private final CardListPage cardListPage = new CardListPage();
    private Long selectedIdOfCard;
    private final SelectedCardGroupPage selectedCardGroupPage = new SelectedCardGroupPage();
    private final CardViewPage cardViewPage = new CardViewPage();
    private final CardEditPage cardEditPage = new CardEditPage();
    private final CardCreatePage cardCreatePage = new CardCreatePage();
    private final CardOperationListPage cardOperationListPage = new CardOperationListPage();
    private final CardFileLoadPage cardFileLoadPage = new CardFileLoadPage();
    private final CardExpireBatchEditPage cardExpireBatchEditPage = new CardExpireBatchEditPage();

    // Service pages
    private final BasicWorkspacePage serviceGroupPage = new BasicWorkspacePage();
    private final SupportEmailPage supportEmailPage = new SupportEmailPage();
    private final TestLogPage testLogPage = new TestLogPage();
    private final BuildSignKeysPage buildSignKeysPage = new BuildSignKeysPage();
    private final OrderRemovePage orderRemovePage = new OrderRemovePage();
    private final SochiClientsLoadPage sochiClientsLoadPage = new SochiClientsLoadPage();
    private final SochiClientsViewPage sochiClientsViewPage = new SochiClientsViewPage();
    private Long selectedSochiClientContractId;

    // Report job manipulation
    private final BasicWorkspacePage reportJobGroupPage = new BasicWorkspacePage();
    private final ReportJobListPage reportJobListPage = new ReportJobListPage();
    private Long selectedIdOfReportJob;
    private Long removedIdOfReportJob;
    private final SelectedReportJobGroupPage selectedReportJobGroupPage = new SelectedReportJobGroupPage();
    private final ReportJobViewPage reportJobViewPage = new ReportJobViewPage();
    private final ReportJobEditPage reportJobEditPage = new ReportJobEditPage();
    private final ReportJobCreatePage reportJobCreatePage = new ReportJobCreatePage();

    // Report rule manipulation
    private final BasicWorkspacePage reportRuleGroupPage = new BasicWorkspacePage();
    private final ReportRuleListPage reportRuleListPage = new ReportRuleListPage();
    private Long selectedIdOfReportRule;
    private Long removedIdOfReportRule;
    private final SelectedReportRuleGroupPage selectedReportRuleGroupPage = new SelectedReportRuleGroupPage();
    private final ReportRuleViewPage reportRuleViewPage = new ReportRuleViewPage();
    private final ReportRuleEditPage reportRuleEditPage = new ReportRuleEditPage();
    private final ReportRuleCreatePage reportRuleCreatePage = new ReportRuleCreatePage();

    // Report online manipulation (baybikov 05.10.2011)
    private final BasicWorkspacePage reportOnlineGroupPage = new BasicWorkspacePage();

    // baybikov 23.11.2011
    private final FreeComplexReportPage freeComplexReportPage = new FreeComplexReportPage();
    private final PayComplexReportPage payComplexReportPage = new PayComplexReportPage();

    // baybikov 23.11.2011
    private final BasicWorkspacePage complexGroupPage = new BasicWorkspacePage();

    // baybikov (06.10.2011)
    private final SalesReportPage salesReportPage = new SalesReportPage();
    private final SyncReportPage syncReportPage = new SyncReportPage();
    // baybikov (07.10.2011)
    private final StatusSyncReportPage statusSyncReportPage = new StatusSyncReportPage();
    // baybikov (20.10.2011)
    private final ClientReportPage clientReportPage =  new ClientReportPage();

    // baybikov (11.11.2011)
    private final BasicWorkspacePage configurationGroupPage = new BasicWorkspacePage();
    private final ConfigurationPage configurationPage = new ConfigurationPage();
    
    // baybikov (25.11.2011)
    private final BasicWorkspacePage optionGroupPage = new BasicWorkspacePage();
    private final OptionPage optionPage = new OptionPage();

    // baybikov (21.11.2011)
    private final CurrentPositionsReportPage currentPositionsReportPage = new CurrentPositionsReportPage();

    // POS manipulation (baybikov 22.11.2011)
    private final BasicWorkspacePage posGroupPage = new BasicWorkspacePage();
    private final PosListPage posListPage = new PosListPage();
    private Long selectedIdOfPos;
    private final PosDeletePage posDeletePage = new PosDeletePage();
    private final PosCreatePage posCreatePage = new PosCreatePage();
    private final SelectedPosGroupPage selectedPosGroupPage = new SelectedPosGroupPage();
    private final PosEditPage posEditPage = new PosEditPage();

    // Settlement manipulation (baybikov 22.11.2011)
    private final BasicWorkspacePage settlementGroupPage = new BasicWorkspacePage();
    private final SettlementListPage settlementListPage = new SettlementListPage();
    private Long selectedIdOfSettlement;
    private final SettlementDeletePage settlementDeletePage = new SettlementDeletePage();
    private final SettlementCreatePage settlementCreatePage = new SettlementCreatePage();
    private final SettlementEditPage settlementEditPage = new SettlementEditPage();
    // baybikov (25.11.2011)
    private final SelectedSettlementGroupPage selectedSettlementGroupPage = new SelectedSettlementGroupPage();

    // AddPayment manipulation (baybikov 29.11.2011)
    private final BasicWorkspacePage addPaymentGroupPage = new BasicWorkspacePage();
    private final AddPaymentListPage addPaymentListPage = new AddPaymentListPage();
    private Long selectedIdOfAddPayment;
    private final AddPaymentDeletePage addPaymentDeletePage = new AddPaymentDeletePage();
    private final AddPaymentCreatePage addPaymentCreatePage = new AddPaymentCreatePage();
    private final AddPaymentEditPage addPaymentEditPage = new AddPaymentEditPage();
    private final SelectedAddPaymentGroupPage selectedAddPaymentGroupPage = new SelectedAddPaymentGroupPage();
    
    // Category manipulation (baybikov 05.12.2011)
    private final BasicWorkspacePage categoryGroupPage = new BasicWorkspacePage();
    private final CategoryListPage categoryListPage = new CategoryListPage();
    private Long selectedIdOfCategory;
    private final CategoryDeletePage categoryDeletePage = new CategoryDeletePage();
    private final CategoryCreatePage categoryCreatePage = new CategoryCreatePage();
    private final SelectedCategoryGroupPage selectedCategoryGroupPage = new SelectedCategoryGroupPage();
    private final CategoryEditPage categoryEditPage = new CategoryEditPage();
    
    // Rule manipulation (baybikov 06.12.2011)
    private final BasicWorkspacePage ruleGroupPage = new BasicWorkspacePage();
    private final RuleListPage ruleListPage = new RuleListPage();
    private Long selectedIdOfRule;
    private final RuleDeletePage ruleDeletePage = new RuleDeletePage();
    private final RuleCreatePage ruleCreatePage = new RuleCreatePage();
    private final SelectedRuleGroupPage selectedRuleGroupPage = new SelectedRuleGroupPage();
    private final RuleEditPage ruleEditPage = new RuleEditPage();
    
    // Event notification manipulation
    private final BasicWorkspacePage eventNotificationGroupPage = new BasicWorkspacePage();
    private final EventNotificationListPage eventNotificationListPage = new EventNotificationListPage();
    private Long selectedIdOfEventNotification;
    private Long removedIdOfEventNotification;
    private final SelectedEventNotificationGroupPage selectedEventNotificationGroupPage = new SelectedEventNotificationGroupPage();
    private final EventNotificationViewPage eventNotificationViewPage = new EventNotificationViewPage();
    private final EventNotificationEditPage eventNotificationEditPage = new EventNotificationEditPage();
    private final EventNotificationCreatePage eventNotificationCreatePage = new EventNotificationCreatePage();

    // Modal pages
    private final OrgSelectPage orgSelectPage = new OrgSelectPage();
    private final OrgListSelectPage orgListSelectPage = new OrgListSelectPage();
    private final ContragentSelectPage contragentSelectPage = new ContragentSelectPage();
    private final ClientSelectPage clientSelectPage = new ClientSelectPage();

    // baybikov (06.12.2011)
    private final CategorySelectPage categorySelectPage = new CategorySelectPage();

    public String getEndOfLine() {
        return "\r\n";
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

    void updateSelectedMainMenu() {
        UIComponent mainMenuComponent = currentWorkspacePage.getMainMenuComponent();
        if (null != mainMenuComponent) {
            mainMenu.setValue(mainMenuComponent.getId());
        }
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

    public BasicWorkspacePage getUserGroupPage() {
        return userGroupPage;
    }

    public Object showUserGroupPage() {
        currentWorkspacePage = userGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public UserListPage getUserListPage() {
        return userListPage;
    }

    public Object showUserListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = userListPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка пользователей",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userListPage.removeUser(persistenceSession, removedIdOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (removedIdOfUser.equals(selectedIdOfUser)) {
                selectedIdOfUser = null;
                selectedUserGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove user", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении пользователя", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Long getSelectedIdOfUser() {
        return selectedIdOfUser;
    }

    public void setSelectedIdOfUser(Long selectedIdOfUser) {
        this.selectedIdOfUser = selectedIdOfUser;
    }

    public Long getRemovedIdOfUser() {
        return removedIdOfUser;
    }

    public void setRemovedIdOfUser(Long removedIdOfUser) {
        this.removedIdOfUser = removedIdOfUser;
    }

    public SelectedUserGroupPage getSelectedUserGroupPage() {
        return selectedUserGroupPage;
    }

    public Object showSelectedUserGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedUserGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected user group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы пользователя",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public UserViewPage getUserViewPage() {
        return userViewPage;
    }

    public Object showUserViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
            userViewPage.fill(persistenceSession, selectedIdOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedUserGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = userViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill user view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных пользователя", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public UserEditPage getUserEditPage() {
        return userEditPage;
    }

    public Object showUserEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
            userEditPage.fill(persistenceSession, selectedIdOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedUserGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = userEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill user edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных пользователя", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (userEditPage.isChangePassword() && !StringUtils
                .equals(userEditPage.getPlainPassword(), userEditPage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                userEditPage.updateUser(persistenceSession, selectedIdOfUser);
                selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные пользователя обновлены успешно", null));
            } catch (Exception e) {
                logger.error("Failed to update user", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных пользователя",
                                null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public UserCreatePage getUserCreatePage() {
        return userCreatePage;
    }

    public Object showUserCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = userCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show user create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания пользователя", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!StringUtils.equals(userCreatePage.getPlainPassword(), userCreatePage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                userCreatePage.createUser(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Пользователь создан успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create user", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании пользователя", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public BasicWorkspacePage getOrgGroupPage() {
        return orgGroupPage;
    }

    public Object showOrgGroupPage() {
        currentWorkspacePage = orgGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public OrgListPage getOrgListPage() {
        return orgListPage;
    }

    public Object showOrgListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = orgListPage;
        } catch (Exception e) {
            logger.error("Failed to fill org list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка организаций",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Long getSelectedIdOfOrg() {
        return selectedIdOfOrg;
    }

    public void setSelectedIdOfOrg(Long selectedIdOfOrg) {
        this.selectedIdOfOrg = selectedIdOfOrg;
    }

    public SelectedOrgGroupPage getSelectedOrgGroupPage() {
        return selectedOrgGroupPage;
    }

    public Object showSelectedOrgGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedOrgGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected org group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы организации",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public OrgViewPage getOrgViewPage() {
        return orgViewPage;
    }

    public Object showOrgViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            orgViewPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill org view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public OrgEditPage getOrgEditPage() {
        return orgEditPage;
    }

    public Object showOrgEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            orgEditPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill org edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateOrg() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (orgEditPage.isChangeSsoPassword() && !StringUtils
                .equals(orgEditPage.getPlainSsoPassword(), orgEditPage.getPlainSsoPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                orgEditPage.updateOrg(persistenceSession, selectedIdOfOrg);
                selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные организации обновлены успешно", null));
            } catch (Exception e) {
                logger.error("Failed to update org", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных организации", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public OrgCreatePage getOrgCreatePage() {
        return orgCreatePage;
    }

    public Object showOrgCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = orgCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show org create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createOrg() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!StringUtils.equals(orgCreatePage.getPlainSsoPassword(), orgCreatePage.getPlainSsoPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                orgCreatePage.createOrg(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Организация зарегистрирована успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create org", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации организации", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public OrgBalanceReportPage getOrgBalanceReportPage() {
        return orgBalanceReportPage;
    }

    public Object showOrgBalanceReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            orgBalanceReportPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgBalanceReportPage;
        } catch (Exception e) {
            logger.error("Failed to show org balance report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по балансу организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildOrgBalanceReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgBalanceReportPage.buildReport(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build org balance report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public OrgOrderReportPage getOrgOrderReportPage() {
        return orgOrderReportPage;
    }

    public Object showOrgOrderReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            orgOrderReportPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgOrderReportPage;
        } catch (Exception e) {
            logger.error("Failed to show org order report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по покупкам по организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildOrgOrderReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgOrderReportPage.buildReport(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build org order report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public OrgSelectPage getOrgSelectPage() {
        return orgSelectPage;
    }

    public OrgListSelectPage getOrgListSelectPage() {
        return orgListSelectPage;
    }

    public BasicPage getTopMostPage() {
        BasicPage currentTopMostPage = currentWorkspacePage;
        if (!modalPages.isEmpty()) {
            currentTopMostPage = modalPages.peek();
        }
        return currentTopMostPage;
    }

    public Object showOrgSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof OrgSelectPage.CompleteHandler) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                orgSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgSelectPage.pushCompleteHandler((OrgSelectPage.CompleteHandler) currentTopMostPage);
                modalPages.push(orgSelectPage);
            } catch (Exception e) {
                logger.error("Failed to fill org selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора организации", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    //01.11.2011
    public Object showOrgListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof OrgListSelectPage.CompleteHandlerList) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                orgListSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgListSelectPage.pushCompleteHandlerList((OrgListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(orgListSelectPage);
            } catch (Exception e) {
                logger.error("Failed to fill org selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора организации", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public Object updateOrgSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill org selection page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора организации",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object updateOrgListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgListSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill org selection page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора организации",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object completeOrgSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgSelectPage.completeOrgSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete org selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора организации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    //01.11.2011
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
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора организаций", null));
        }
        return null;
    }

    //01.11.2011
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
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора организаций", null));
        }
        return null;
    }

    public BasicWorkspacePage getContragentGroupPage() {
        return contragentGroupPage;
    }

    public Object showContragentGroupPage() {
        currentWorkspacePage = contragentGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public ContragentListPage getContragentListPage() {
        return contragentListPage;
    }

    public Object showContragentListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = contragentListPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка контрагентов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Long getSelectedIdOfContragent() {
        return selectedIdOfContragent;
    }

    public void setSelectedIdOfContragent(Long selectedIdOfContragent) {
        this.selectedIdOfContragent = selectedIdOfContragent;
    }

    public SelectedContragentGroupPage getSelectedContragentGroupPage() {
        return selectedContragentGroupPage;
    }

    public Object showSelectedContragentGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedContragentGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected contragent group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы контрагента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ContragentViewPage getContragentViewPage() {
        return contragentViewPage;
    }

    public Object showContragentViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            contragentViewPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedContragentGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = contragentViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ContragentEditPage getContragentEditPage() {
        return contragentEditPage;
    }

    public Object showContragentEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            contragentEditPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedContragentGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = contragentEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateContragent() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentEditPage.updateContragent(persistenceSession, selectedIdOfContragent);
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные контрагента обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update contragent", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ContragentCreatePage getContragentCreatePage() {
        return contragentCreatePage;
    }

    public Object showContragentCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = contragentCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show contragent create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createContragent() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentCreatePage.createContragent(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Контрагент зарегистрирован успешно", null));
        } catch (ContragentCreatePage.ContragentWithClassExistsException e) {
            logger.error("Failed to create contragent", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации контрагента: для типов \"Оператор\", \"Бюждет\" и \"Клиент\" не может быть создано более одного контрагента", null));
        } catch (Exception e) {
            logger.error("Failed to create contragent", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ContragentClientPaymentReportPage getContragentClientPaymentReportPage() {
        return contragentClientPaymentReportPage;
    }

    public Object showContragentClientPaymentReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            contragentClientPaymentReportPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedContragentGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = contragentClientPaymentReportPage;
        } catch (Exception e) {
            logger.error("Failed to show contragent client payment report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по платежам клиентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildContragentClientPaymentReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentClientPaymentReportPage.buildReport(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build contragent balance report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ContragentSelectPage getContragentSelectPage() {
        return contragentSelectPage;
    }

    private int multiContrFlag = 0;

    public void setMultiContrFlag(int multiContrFlag) {
        this.multiContrFlag = multiContrFlag;
    }

    private String classTypes;

    public void setClassTypes(String classTypes) {
        this.classTypes = classTypes;
    }

    public Object showContragentSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ContragentSelectPage.CompleteHandler ||
            currentTopMostPage instanceof ContragentSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                contragentSelectPage.fill(persistenceSession, multiContrFlag, classTypes);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof ContragentSelectPage.CompleteHandler) {
                    contragentSelectPage.pushCompleteHandler((ContragentSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(contragentSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill contragent selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора контрагента", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    /*public void updateContragentSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentSelectPage.fill(persistenceSession, 0, "");
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill contragent selection page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора контрагента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
    }*/

    public Object completeContragentSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentSelectPage.completeContragentSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == contragentSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete contragent selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    private boolean isModalPageVisible(BasicPage basicPage) {
        return modalPages.contains(basicPage);
    }

    public boolean isOrgSelectPageVisible() {
        return isModalPageVisible(orgSelectPage);
    }

    public boolean isContragentSelectPageVisible() {
        return isModalPageVisible(contragentSelectPage);
    }

    public BasicWorkspacePage getClientGroupPage() {
        return clientGroupPage;
    }

    public Object showClientGroupPage() {
        currentWorkspacePage = clientGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public ClientListPage getClientListPage() {
        return clientListPage;
    }

    public Object showClientListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientListPage;
        } catch (Exception e) {
            logger.error("Failed to fill client list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка клиентов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateClientListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for client list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка клиентов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearClientListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.getClientFilter().clear();
            clientListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка клиентов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Long getSelectedIdOfClient() {
        return selectedIdOfClient;
    }

    public void setSelectedIdOfClient(Long selectedIdOfClient) {
        this.selectedIdOfClient = selectedIdOfClient;
    }

    public SelectedClientGroupPage getSelectedClientGroupPage() {
        return selectedClientGroupPage;
    }

    public Object showSelectedClientGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedClientGroupPage.fill(persistenceSession, selectedIdOfClient);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedClientGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected client group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientViewPage getClientViewPage() {
        return clientViewPage;
    }

    public Object showClientViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedClientGroupPage.fill(persistenceSession, selectedIdOfClient);
            clientViewPage.fill(persistenceSession, selectedIdOfClient);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedClientGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = clientViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill client view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных клиента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientEditPage getClientEditPage() {
        return clientEditPage;
    }

    public Object showClientEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedClientGroupPage.fill(persistenceSession, selectedIdOfClient);
            clientEditPage.fill(persistenceSession, selectedIdOfClient);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedClientGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = clientEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill client edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных клиента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateClient() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (clientEditPage.isChangePassword() && !StringUtils
                .equals(clientEditPage.getPlainPassword(), clientEditPage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                clientEditPage.updateClient(persistenceSession, selectedIdOfClient);
                selectedClientGroupPage.fill(persistenceSession, selectedIdOfClient);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные клиента обновлены успешно", null));
            } catch (Exception e) {
                logger.error("Failed to update client", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных клиента", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public ClientOperationListPage getClientOperationListPage() {
        return clientOperationListPage;
    }

    public Object showClientOperationListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientOperationListPage.fill(persistenceSession, selectedIdOfClient);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientOperationListPage;
        } catch (Exception e) {
            logger.error("Failed to show client operation list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра списка операций по клиенту", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientCreatePage getClientCreatePage() {
        return clientCreatePage;
    }

    public Object showClientCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show client create page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы регистрации клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createClient() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!StringUtils.equals(clientCreatePage.getPlainPassword(), clientCreatePage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                clientCreatePage.createClient(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиент зарегистрирован успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create client", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации клиента", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientFileLoadPage getClientFileLoadPage() {
        return clientFileLoadPage;
    }

    public ClientUpdateFileLoadPage getClientUpdateFileLoadPage() {
        return clientUpdateFileLoadPage;
    }

    public Object showClientFileLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientFileLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientFileLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show client file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки регистрационного списка клиентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showClientUpdateFileLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientUpdateFileLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientUpdateFileLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show client update file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки списка обновлений клиентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public void clientLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            clientFileLoadPage.loadClients(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиенты загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load clients from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по клиентам",
                            null));
        } finally {
            close(inputStream);
        }
    }

    public void clientUpdateLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            clientUpdateFileLoadPage.updateClients(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиенты загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load clients from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по клиентам",
                            null));
        } finally {
            close(inputStream);
        }
    }

    public void sochiClientsLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            sochiClientsLoadPage.loadClients(inputStream, dataSize);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(
                    "Клиенты загружены и зарегистрированы успешно. Всего обработано: %d. Обработано успешно: %d",
                    sochiClientsLoadPage.getLineResultSize(), sochiClientsLoadPage.getSuccessLineNumber()), null));
        } catch (Exception e) {
            logger.error("Failed to load sochi clients from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по клиентам",
                            null));
        } finally {
            close(inputStream);
        }
    }

    private static void close(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
                logger.error("failed to close input stream", e);
            }
        }
    }

    public String showClientCSVList() {
        return "showClientCSVList";
    }

    public String showClientLoadResultCSVList() {
        return "showClientLoadResultCSVList";
    }

    public String showClientUpdateLoadResultCSVList() {
        return "showClientUpdateLoadResultCSVList";
    }

    public String showCardLoadResultCSVList() {
        return "showCardLoadResultCSVList";
    }

    public Long getSelectedIdOfCard() {
        return selectedIdOfCard;
    }

    public void setSelectedIdOfCard(Long selectedIdOfCard) {
        this.selectedIdOfCard = selectedIdOfCard;
    }

    public ClientSelectPage getClientSelectPage() {
        return clientSelectPage;
    }

    public Object showClientSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ClientSelectPage.CompleteHandler) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                clientSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                clientSelectPage.pushCompleteHandler((ClientSelectPage.CompleteHandler) currentTopMostPage);
                modalPages.push(clientSelectPage);
            } catch (Exception e) {
                logger.error("Failed to fill client selection page", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора клиента",
                                null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    public Object updateClientSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill client selection page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object completeClientSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSelectPage.completeClientSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == clientSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete client selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора клиента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearClientSelectPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSelectPage.getClientFilter().clear();
            clientSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client select page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ContractBuildPage getContractBuildPage() {
        return contractBuildPage;
    }

    public Object showContractBuildPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contractBuildPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = contractBuildPage;
        } catch (Exception e) {
            logger.error("Failed to show contract build page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы подготовки договора",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object generateClientContractNumber() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contractBuildPage.generateContractNumber();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Номер договора клиента сгенерирован успешно", null));
        } catch (Exception e) {
            logger.error("Failed to generate client contract number", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при генерации номера договора клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ClientLimitBatchEditPage getClientLimitBatchEditPage() {
        return clientLimitBatchEditPage;
    }

    public CardExpireBatchEditPage getCardExpireBatchEditPage() {
        return cardExpireBatchEditPage;
    }

    public Object showClientLimitBatchEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientLimitBatchEditPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientLimitBatchEditPage;
        } catch (Exception e) {
            logger.error("Failed to show client limit batch edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы изменения лимита овердрафта", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showCardExpireBatchEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardExpireBatchEditPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardExpireBatchEditPage;
        } catch (Exception e) {
            logger.error("Failed to show card expire batch edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы изменения даты валидности карты", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object batchUpdateClientLimit() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientLimitBatchEditPage.updateClientLimit(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Операция завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to batch update client limit", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Операция завершена c ошибками", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object batchUpdateCardExpire() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardExpireBatchEditPage.updateExpireDate(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Операция завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to batch update card expire date", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Операция завершена c ошибками", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ClientSmsListPage getClientSmsListPage() {
        return clientSmsListPage;
    }

    public Object showClientSmsListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSmsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientSmsListPage;
        } catch (Exception e) {
            logger.error("Failed to show client sms page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы работы с клиентскими SMS", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object sendClientNegativeBalanceSms() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSmsListPage.sendNegativeBalanceSms(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Отправка SMS завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to send client negative balance sms", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Отправка SMS завершена с ошибками", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object updateClientSmsListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSmsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to show client sms page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы работы с клиентскими SMS", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearClientSmsListPageClientFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSmsListPage.getClientFilter().clear();
            clientSmsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to show client sms page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы работы с клиентскими SMS", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearClientSmsListPageClientSmsFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSmsListPage.getClientSmsFilter().clear();
            clientSmsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to show client sms page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы работы с клиентскими SMS", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public BasicWorkspacePage getCardGroupPage() {
        return cardGroupPage;
    }

    public Object showCardGroupPage() {
        currentWorkspacePage = cardGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public CardListPage getCardListPage() {
        return cardListPage;
    }

    public Object showCardListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardListPage;
        } catch (Exception e) {
            logger.error("Failed to fill card list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка карт", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateCardListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for card list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка карт", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearCardListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardListPage.getCardFilter().clear();
            cardListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for card list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка карт", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public String showCardCSVList() {
        return "showCardCSVList";
    }

    public SelectedCardGroupPage getSelectedCardGroupPage() {
        return selectedCardGroupPage;
    }

    public Object showSelectedCardGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedCardGroupPage.fill(persistenceSession, selectedIdOfCard);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedCardGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected card group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы клиента",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public CardViewPage getCardViewPage() {
        return cardViewPage;
    }

    public Object showCardViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedCardGroupPage.fill(persistenceSession, selectedIdOfCard);
            cardViewPage.fill(persistenceSession, selectedIdOfCard);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedCardGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = cardViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill card view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных карты", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public CardEditPage getCardEditPage() {
        return cardEditPage;
    }

    public Object showCardEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedCardGroupPage.fill(persistenceSession, selectedIdOfCard);
            cardEditPage.fill(persistenceSession, selectedIdOfCard);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedCardGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = cardEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill card edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных карты", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateCard() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardEditPage.updateCard(persistenceSession, selectedIdOfCard);
            selectedCardGroupPage.fill(persistenceSession, selectedIdOfCard);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные карты обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update card", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных карты", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public CardCreatePage getCardCreatePage() {
        return cardCreatePage;
    }

    public Object showCardCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show card create page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы регистрации карты",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createCard() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardCreatePage.createCard(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Карта зарегистрирована успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create card", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации карты", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public CardOperationListPage getCardOperationListPage() {
        return cardOperationListPage;
    }

    public Object showCardOperationListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardOperationListPage.fill(persistenceSession, selectedIdOfCard);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardOperationListPage;
        } catch (Exception e) {
            logger.error("Failed to show card operation list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра списка операций по карте", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public CardFileLoadPage getCardFileLoadPage() {
        return cardFileLoadPage;
    }

    public Object showCardFileLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardFileLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardFileLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show card file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки списка карт на регистрацию", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public void cardLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            cardFileLoadPage.loadCards(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Карты загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load cards from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по картам",
                            null));
        } finally {
            close(inputStream);
        }
    }

    public BasicWorkspacePage getCcAccountGroupPage() {
        return ccAccountGroupPage;
    }

    public Object showCCAccountGroupPage() {
        currentWorkspacePage = ccAccountGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public CCAccountListPage getCcAccountListPage() {
        return ccAccountListPage;
    }

    public Object showCCAccountListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = ccAccountListPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateCCAccountListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Object clearCCAccountListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountListPage.getFilter().clear();
            ccAccountListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public CompositeIdOfContragentClientAccount getRemovedIdOfCCAccount() {
        return removedIdOfCCAccount;
    }

    public void setRemovedIdOfCCAccount(CompositeIdOfContragentClientAccount removedIdOfCCAccount) {
        this.removedIdOfCCAccount = removedIdOfCCAccount;
    }

    public CCAccountDeletePage getCcAccountDeletePage() {
        return ccAccountDeletePage;
    }

    public Object removeCCAccount() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountDeletePage.removeCCAccount(persistenceSession, removedIdOfCCAccount);
            ccAccountListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove contragent client account", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении счета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public String showCCAccountCSVList() {
        return "showCCAccountCSVList";
    }

    public CCAccountCreatePage getCCAccountCreatePage() {
        return ccAccountCreatePage;
    }

    public Object showCCAccountCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = ccAccountCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show contragent client account create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации счета клиента у контрагента", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createCCAccount() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountCreatePage.createCCAccount(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Счет зарегистрирован успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create ccAccount", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации счета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public CCAccountFileLoadPage getCCAccountFileLoadPage() {
        return ccAccountFileLoadPage;
    }

    public Object showCCAccountFileLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ccAccountFileLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = ccAccountFileLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show contragent client account file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки списка счетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public void ccAccountLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        try {
            if (item.isTempFile()) {
                inputStream = new FileInputStream(item.getFile());
            } else {
                inputStream = new ByteArrayInputStream(item.getData());
            }
            ccAccountFileLoadPage.loadCCAccounts(inputStream);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Счета загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load ccAccounts from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по счетам",
                            null));
        } finally {
            close(inputStream);
        }
    }

    public void mailLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        try {
            if (item.isTempFile()) {
                ru.axetta.ecafe.processor.core.mail.File file = new ru.axetta.ecafe.processor.core.mail.File();
                file.setFile(item.getFile());
                file.setFileName(item.getFileName());
                file.setContentType(item.getContentType());
                supportEmailPage.loadFiles(file);
            } else {
                throw new Exception("Invalid file");
            }
        } catch (Exception e) {
            logger.error("Failed to load file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла", null));
        }
    }

    public BasicWorkspacePage getServiceGroupPage() {
        return serviceGroupPage;
    }

    public Object showServiceGroupPage() {
        currentWorkspacePage = serviceGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public SupportEmailPage getSupportEmailPage() {
        return supportEmailPage;
    }

    public Object showSupportEmailPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            supportEmailPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = supportEmailPage;
        } catch (Exception e) {
            logger.error("Failed to fill support email page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отправки электронного письма", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object sendSupportEmail() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            supportEmailPage.sendSupportEmail(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Электронное письмо отправлено успешно", null));
        } catch (Exception e) {
            logger.error("Failed to send support email", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при отправке электронного письма", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public BasicWorkspacePage getReportRuleGroupPage() {
        return reportRuleGroupPage;
    }

    public Object showReportRuleGroupPage() {
        currentWorkspacePage = reportRuleGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public ReportRuleListPage getReportRuleListPage() {
        return reportRuleListPage;
    }

    public Object showReportRuleListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportRuleListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = reportRuleListPage;
        } catch (Exception e) {
            logger.error("Failed to fill report rule list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка правил обработки отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeReportRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportRuleListPage.removeReportRule(persistenceSession, removedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (removedIdOfReportRule.equals(selectedIdOfReportRule)) {
                selectedIdOfReportRule = null;
                selectedReportRuleGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove report rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении правила обработки отчетов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Long getSelectedIdOfReportRule() {
        return selectedIdOfReportRule;
    }

    public void setSelectedIdOfReportRule(Long selectedIdOfReportRule) {
        this.selectedIdOfReportRule = selectedIdOfReportRule;
    }

    public Long getRemovedIdOfReportRule() {
        return removedIdOfReportRule;
    }

    public void setRemovedIdOfReportRule(Long removedIdOfReportRule) {
        this.removedIdOfReportRule = removedIdOfReportRule;
    }

    public SelectedReportRuleGroupPage getSelectedReportRuleGroupPage() {
        return selectedReportRuleGroupPage;
    }

    public Object showSelectedReportRuleGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportRuleGroupPage.fill(persistenceSession, selectedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedReportRuleGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected report rule group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы правила обработки отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ReportRuleViewPage getReportRuleViewPage() {
        return reportRuleViewPage;
    }

    public Object showReportRuleViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportRuleGroupPage.fill(persistenceSession, selectedIdOfReportRule);
            reportRuleViewPage.fill(persistenceSession, selectedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedReportRuleGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = reportRuleViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill report rule view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра правила обработки отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ReportRuleEditPage getReportRuleEditPage() {
        return reportRuleEditPage;
    }

    public Object showReportRuleEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportRuleGroupPage.fill(persistenceSession, selectedIdOfReportRule);
            reportRuleEditPage.fill(persistenceSession, selectedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedReportRuleGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = reportRuleEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill report rule edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования правила обработки отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateReportRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportRuleEditPage.updateReportRule(persistenceSession, selectedIdOfReportRule);
            selectedReportRuleGroupPage.fill(persistenceSession, selectedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            runtimeContext.getAutoReportProcessor().loadAutoReportRules();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Правило обработки отчетов обновлено успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update report rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении правила обработки отчетов",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public ReportRuleCreatePage getReportRuleCreatePage() {
        return reportRuleCreatePage;
    }

    public Object showReportRuleCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportRuleCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = reportRuleCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show report rule create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания правила обработки отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createReportRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportRuleCreatePage.createReportRule(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            runtimeContext.getAutoReportProcessor().loadAutoReportRules();
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Правило создано успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create report rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public BasicWorkspacePage getEventNotificationGroupPage() {
        return eventNotificationGroupPage;
    }

    public Object showEventNotificationGroupPage() {
        currentWorkspacePage = eventNotificationGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public EventNotificationListPage getEventNotificationListPage() {
        return eventNotificationListPage;
    }

    public Object showEventNotificationListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            eventNotificationListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = eventNotificationListPage;
        } catch (Exception e) {
            logger.error("Failed to fill event notification list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка правил уведомлений", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeEventNotification() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            eventNotificationListPage.removeEventNotification(persistenceSession, removedIdOfEventNotification);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (removedIdOfEventNotification.equals(selectedIdOfEventNotification)) {
                selectedIdOfEventNotification = null;
                selectedEventNotificationGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove event notification", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении правила обработки уведомлений",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public Long getSelectedIdOfEventNotification() {
        return selectedIdOfEventNotification;
    }

    public void setSelectedIdOfEventNotification(Long selectedIdOfEventNotification) {
        this.selectedIdOfEventNotification = selectedIdOfEventNotification;
    }

    public Long getRemovedIdOfEventNotification() {
        return removedIdOfEventNotification;
    }

    public void setRemovedIdOfEventNotification(Long removedIdOfEventNotification) {
        this.removedIdOfEventNotification = removedIdOfEventNotification;
    }

    public SelectedEventNotificationGroupPage getSelectedEventNotificationGroupPage() {
        return selectedEventNotificationGroupPage;
    }

    public Object showSelectedEventNotificationGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedEventNotificationGroupPage.fill(persistenceSession, selectedIdOfEventNotification);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedEventNotificationGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected event notification group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы правила уведомлений", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public EventNotificationViewPage getEventNotificationViewPage() {
        return eventNotificationViewPage;
    }

    public Object showEventNotificationViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedEventNotificationGroupPage.fill(persistenceSession, selectedIdOfEventNotification);
            eventNotificationViewPage.fill(persistenceSession, selectedIdOfEventNotification);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedEventNotificationGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = eventNotificationViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill event notification view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра правила уведомлений", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public EventNotificationEditPage getEventNotificationEditPage() {
        return eventNotificationEditPage;
    }

    public Object showEventNotificationEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedEventNotificationGroupPage.fill(persistenceSession, selectedIdOfEventNotification);
            eventNotificationEditPage.fill(persistenceSession, selectedIdOfEventNotification);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedEventNotificationGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = eventNotificationEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill event notification edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования правила уведомлений", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateEventNotification() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            eventNotificationEditPage.updateEventNotification(persistenceSession, selectedIdOfEventNotification);
            selectedEventNotificationGroupPage.fill(persistenceSession, selectedIdOfEventNotification);
            persistenceTransaction.commit();
            runtimeContext.getEventProcessor().loadEventNotificationRules();
            persistenceTransaction = null;
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Правило обновлено успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update report rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public EventNotificationCreatePage getEventNotificationCreatePage() {
        return eventNotificationCreatePage;
    }

    public Object showEventNotificationCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            eventNotificationCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = eventNotificationCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show event notification create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания правила уведомлений", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createEventNotification() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            eventNotificationCreatePage.createEventNotification(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            runtimeContext.getEventProcessor().loadEventNotificationRules();
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Правило создано успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create report rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public TestLogPage getTestLogPage() {
        return testLogPage;
    }

    public Object showTestLogPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            testLogPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = testLogPage;
        } catch (Exception e) {
            logger.error("Failed to show log test page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы тестирования лога",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object doLogTest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            testLogPage.writeTextToLog(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
        } catch (Exception e) {
            logger.error("Failed to test log", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при тестировании лога", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public BuildSignKeysPage getBuildSignKeysPage() {
        return buildSignKeysPage;
    }

    public Object showBuildSignKeysPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            buildSignKeysPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = buildSignKeysPage;
        } catch (Exception e) {
            logger.error("Failed to show sign keys build page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы генерации ключей подписи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildSignKeys() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            buildSignKeysPage.buildSignKes(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
        } catch (Exception e) {
            logger.error("Failed to build sign keys", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при генерации ключей", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public OrderRemovePage getOrderRemovePage() {
        return orderRemovePage;
    }

    public SochiClientsLoadPage getSochiClientsLoadPage() {
        return sochiClientsLoadPage;
    }

    public SochiClientsViewPage getSochiClientsViewPage() {
        return sochiClientsViewPage;
    }

    public Object showOrderRemovePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orderRemovePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = orderRemovePage;
        } catch (Exception e) {
            logger.error("Failed to show order remove page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы удаления покупки",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showSochiClientsLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        currentWorkspacePage = sochiClientsLoadPage;
        updateSelectedMainMenu();
        return null;
    }

    public Long getSelectedSochiClientContractId() {
        return selectedSochiClientContractId;
    }

    public void setSelectedSochiClientContractId(Long selectedSochiClientContractId) {
        this.selectedSochiClientContractId = selectedSochiClientContractId;
    }

    public Object showSochiClientsViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            sochiClientsViewPage.fill(runtimeContext, persistenceSession, selectedSochiClientContractId);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = sochiClientsViewPage;
        } catch (Exception e) {
            logger.error("Failed to show sochi clients view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра информации по клиентам по Сочи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeOrder() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            orderRemovePage.removeOrder();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
        } catch (Exception e) {
            logger.error("Failed to remove order", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении покупки", null));
        }
        return null;
    }

    public BasicWorkspacePage getReportJobGroupPage() {
        return reportJobGroupPage;
    }

    public Object showReportJobGroupPage() {
        currentWorkspacePage = reportJobGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public ReportJobListPage getReportJobListPage() {
        return reportJobListPage;
    }

    public Object showReportJobListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportJobListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = reportJobListPage;
        } catch (Exception e) {
            logger.error("Failed to fill report job list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка задач по формированию отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeReportJob() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            reportJobListPage.removeReportJob(removedIdOfReportJob);
        } catch (Exception e) {
            logger.error("Failed to remove report job", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении задачи", null));
        }
        return null;
    }

    public Long getSelectedIdOfReportJob() {
        return selectedIdOfReportJob;
    }

    public void setSelectedIdOfReportJob(Long selectedIdOfReportJob) {
        this.selectedIdOfReportJob = selectedIdOfReportJob;
    }

    public Long getRemovedIdOfReportJob() {
        return removedIdOfReportJob;
    }

    public void setRemovedIdOfReportJob(Long removedIdOfReportJob) {
        this.removedIdOfReportJob = removedIdOfReportJob;
    }

    public SelectedReportJobGroupPage getSelectedReportJobGroupPage() {
        return selectedReportJobGroupPage;
    }

    public Object showSelectedReportJobGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportJobGroupPage.fill(persistenceSession, selectedIdOfReportJob);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedReportJobGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected report job group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы задач формирования отчетов", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ReportJobViewPage getReportJobViewPage() {
        return reportJobViewPage;
    }

    public Object showReportJobViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportJobGroupPage.fill(persistenceSession, selectedIdOfReportJob);
            reportJobViewPage.fill(persistenceSession, selectedIdOfReportJob);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedReportJobGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = reportJobViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill report job view page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы просмотра задачи",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public ReportJobEditPage getReportJobEditPage() {
        return reportJobEditPage;
    }

    public Object showReportJobEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedReportJobGroupPage.fill(persistenceSession, selectedIdOfReportJob);
            reportJobEditPage.fill(persistenceSession, selectedIdOfReportJob);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedReportJobGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = reportJobEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill report job edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования задачи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateReportJob() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            reportJobEditPage.updateReportJob(selectedIdOfReportJob);
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                selectedReportJobGroupPage.fill(persistenceSession, selectedIdOfReportJob);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Задача обновлена успешно", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        } catch (Exception e) {
            logger.error("Failed to update report job", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении задачи", null));
        }
        return null;
    }

    public ReportJobCreatePage getReportJobCreatePage() {
        return reportJobCreatePage;
    }

    public Object showReportJobCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            reportJobCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = reportJobCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show report job create page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы создания задачи",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createReportJob() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            reportJobCreatePage.createReportJob();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Задача создана успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create report job", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании задачи", null));
        }
        return null;
    }

    // baybikov (05.10.2011)
    public BasicWorkspacePage getReportOnlineGroupPage() {
        return reportOnlineGroupPage;
    }

    // baybikov (05.10.2011)
    public Object showReportOnlineGroupPage() {
        currentWorkspacePage = reportOnlineGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public BasicWorkspacePage getComplexGroupPage() {
        return complexGroupPage;
    }

    // baybikov (22.11.2011)
    public Object showComplexGroupPage() {
        currentWorkspacePage = complexGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    /*
        Беслатные комплексы
     */
    // baybikov (05.10.2011)
    public FreeComplexReportPage getFreeComplexReportPage() {
        return freeComplexReportPage;
    }

    // baybikov (05.10.2011)
    public Object showFreeComplexReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = freeComplexReportPage;
        } catch (Exception e) {
            logger.error("Failed to set free complex report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по бесплатным комплексам", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.10.2011)
    public Object buildFreeComplexReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            freeComplexReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build free complex report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    /*
        Платные комплексы
     */
    // baybikov (05.10.2011)
    public PayComplexReportPage getPayComplexReportPage() {
        return payComplexReportPage;
    }

    // baybikov (05.10.2011)
    public Object showPayComplexReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = payComplexReportPage;
        } catch (Exception e) {
            logger.error("Failed to set pay complex report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по платным комплексам", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.10.2011)
    public Object buildPayComplexReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            payComplexReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build pay complex report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (06.10.2011)
    public SalesReportPage getSalesReportPage() {
        return salesReportPage;
    }

    // baybikov (06.10.2011)
    public Object showSalesReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = salesReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по продажам", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.10.2011)
    public Object buildSalesReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            salesReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (23.11.2011)
    public String showFreeComplexCSVList() {
        return "showFreeComplexCSVList";
    }

    // baybikov (23.11.2011)
    public String showPayComplexCSVList() {
        return "showPayComplexCSVList";
    }

    // baybikov (06.10.2011)
    public String showSalesCSVList() {
        return "showSaleCSVList";
    }

    // baybikov (06.10.2011)
    public SyncReportPage getSyncReportPage() {
        return syncReportPage;
    }

    // baybikov (06.10.2011)
    public Object showSyncReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = syncReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sync report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по синхронизации", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.10.2011)
    public Object buildSyncReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            syncReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build sync report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (07.10.2011)
    public StatusSyncReportPage getStatusSyncReportPage() {
        return statusSyncReportPage;
    }

    // baybikov (07.10.2011)
    public Object showStatusSyncReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = statusSyncReportPage;
        } catch (Exception e) {
            logger.error("Failed to set status sync report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы статуса синхронизации", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (07.10.2011)
    public Object buildStatusSyncReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            statusSyncReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build status sync report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (20.10.2011)
    public ClientReportPage getClientReportPage() {
        return clientReportPage;
    }

    // baybikov (20.10.2011)
    public Object showClientReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientReportPage;
        } catch (Exception e) {
            logger.error("Failed to set client report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчет по учащимся", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (20.10.2011)
    public Object buildClientReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build client report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (20.10.2011)
    public String showClientOrgCSVList() {
        return "showClientOrgCSVList";
    }

    /*
        Configuration
     */
    // baybikov (11.11.2011)
    public BasicWorkspacePage getConfigurationGroupPage() {
        return configurationGroupPage;
    }

    // baybikov (11.11.2011)
    public Object showConfigurationGroupPage() {
        currentWorkspacePage = configurationGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (11.11.2011)
    public ConfigurationPage getConfigurationPage() {
        return configurationPage;
    }

    // baybikov (11.11.2011)
    public Object showConfigurationPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            configurationPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = configurationPage;
        } catch (Exception e) {
            logger.error("Failed to fill configuration page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы конфигурации",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object saveConfiguration() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            configurationPage.save(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Конфигурация сохранена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to save configurations", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при сохранении конфигурации", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }
    
    /*
        Options
         */
    // baybikov (11.11.2011)
    public BasicWorkspacePage getOptionGroupPage() {
        return optionGroupPage;
    }

    // baybikov (11.11.2011)
    public Object showOptionGroupPage() {
        currentWorkspacePage = optionGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (11.11.2011)
    public OptionPage getOptionPage() {
        return optionPage;
    }

    // baybikov (11.11.2011)
    public Object showOptionPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            optionPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = optionPage;
        } catch (Exception e) {
            logger.error("Failed to fill option page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы с настройками",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object saveOption() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            optionPage.save(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Настройки сохранены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to save options", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при сохранении настроек", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    /*
        CurrentPositions
         */
    // baybikov (21.11.2011)
    public CurrentPositionsReportPage getCurrentPositionsReportPage() {
        return currentPositionsReportPage;
    }

    // baybikov (21.11.2011)
    public Object showCurrentPositionsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = currentPositionsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set current positions report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра текущих позиций", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (21.11.2011)
    public Object buildCurrentPositionsReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            currentPositionsReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build current positions report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (25.11.2011)
    public Object countCurrentPositions() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        CurrentPositionsReportPage.CurrentPositionData currentPositionData = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            // Получить текущие заказы, clientPayments, платежи м/у контрагентами и начисленные платежи
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                currentPositionData =
                        currentPositionsReportPage.prepareCurrentPositionsData(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);

            }

            // Рассчитать текущие позиции
            List<CurrentPositionsManager.CurrentPositionItem> currentPositionList =
                    currentPositionsReportPage.countCurrentPositions(currentPositionData);

            // Зафиксировать текущие позиции в бд
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                currentPositionsReportPage.fixCurrentPositions(persistenceSession, currentPositionList);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Расчет текущих позиций завершен успешно", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        catch (Exception e) {
            logger.error("Failed to count current positions", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при расчете текущих позиций", null));
        }
        finally {
            RuntimeContext.release(runtimeContext);
        }

        return null;
    }

    /*
      Справочник точек продаж
     */
    // baybikov (22.11.2011)
    public BasicWorkspacePage getPosGroupPage() {
        return posGroupPage;
    }

    // baybikov (22.11.2011)
    public Object showPosGroupPage() {
        currentWorkspacePage = posGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public PosListPage getPosListPage() {
        return posListPage;
    }

    // baybikov (22.11.2011)
    public Object showPosListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = posListPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent POS list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы справочника точек продаж", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public PosCreatePage getPosCreatePage() {
        return posCreatePage;
    }

    // baybikov (22.11.2011)
    public Object showPosCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = posCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show contragent POS create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации точки продажи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public Object createPos() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posCreatePage.createPos(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Точка продажи зарегистрирована успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create ccAccount", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации точки продажи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public Object clearPosListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posListPage.getFilter().clear();
            posListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке справочника точек продаж", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public PosDeletePage getPosDeletePage() {
        return posDeletePage;
    }

    // baybikov (22.11.2011)
    public Long getSelectedIdOfPos() {
        return selectedIdOfPos;
    }

    // baybikov (22.11.2011)
    public void setSelectedIdOfPos(Long selectedIdOfPos) {
        this.selectedIdOfPos = selectedIdOfPos;
    }

    // baybikov (22.11.2011)
    public Object removePos() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posDeletePage.removePos(persistenceSession, selectedIdOfPos);
            posListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove POS", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении точки продажи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public String showPosCSVList() {
        return "showPosCSVList";
    }

    // baybikov (22.11.2011)
    public Object showPosEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedPosGroupPage.fill(persistenceSession, selectedIdOfPos);
            posEditPage.fill(persistenceSession, selectedIdOfPos);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedPosGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = posEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill POS edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования точки продажи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public PosEditPage getPosEditPage() {
        return posEditPage;
    }

    // baybikov (22.11.2011)
    public Object updatePos() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            posEditPage.updatePos(persistenceSession, selectedIdOfPos);
            selectedPosGroupPage.fill(persistenceSession, selectedIdOfPos);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные точки продажи обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update POS", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных точки продажи", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (25.11.2011)
    public SelectedPosGroupPage getSelectedPosGroupPage() {
        return selectedPosGroupPage;
    }

    public Object showSelectedPosGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedPosGroupPage.fill(persistenceSession, selectedIdOfPos);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedPosGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected POS group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы точки продажи",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }
    
    /*
      Платежи между контрагентами
     */
    // baybikov (22.11.2011)
    public BasicWorkspacePage getSettlementGroupPage() {
        return settlementGroupPage;
    }

    // baybikov (22.11.2011)
    public Object showSettlementGroupPage() {
        currentWorkspacePage = settlementGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public SettlementListPage getSettlementListPage() {
        return settlementListPage;
    }

    // baybikov (22.11.2011)
    public Object showSettlementListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = settlementListPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent settlement list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы платежей между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public SettlementCreatePage getSettlementCreatePage() {
        return settlementCreatePage;
    }

    // baybikov (22.11.2011)
    public Object showSettlementCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = settlementCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show contragent settlement create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации платежа между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public Object createSettlement() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementCreatePage.createSettlement(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Платеж между контрагентами зарегистрирован успешно", null));
        } catch (SettlementCreatePage.WrongContragentsException e) {
            logger.error("Failed to create settlement", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Платеж между указанными контрагентами не может быть осуществлен", null));
        } catch (Exception e) {
            logger.error("Failed to create settlement", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации платежа между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public Object clearSettlementListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementListPage.getFilter().clear();
            settlementListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке справочника платежей между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public SettlementDeletePage getSettlementDeletePage() {
        return settlementDeletePage;
    }

    // baybikov (22.11.2011)
    public Long getSelectedIdOfSettlement() {
        return selectedIdOfSettlement;
    }

    // baybikov (22.11.2011)
    public void setSelectedIdOfSettlement(Long selectedIdOfSettlement) {
        this.selectedIdOfSettlement = selectedIdOfSettlement;
    }

    // baybikov (22.11.2011)
    public Object removeSettlement() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementDeletePage.removeSettlement(persistenceSession, selectedIdOfSettlement);
            settlementListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove settlement", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (22.11.2011)
    public String showSettlementCSVList() {
        return "showSettlementCSVList";
    }

    // baybikov (22.11.2011)
    public Object showSettlementEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedSettlementGroupPage.fill(persistenceSession, selectedIdOfSettlement);
            settlementEditPage.fill(persistenceSession, selectedIdOfSettlement);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedSettlementGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = settlementEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill settlement edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (22.11.2011)
    public SettlementEditPage getSettlementEditPage() {
        return settlementEditPage;
    }

    // baybikov (22.11.2011)
    public Object updateSettlement() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            settlementEditPage.updateSettlement(persistenceSession, selectedIdOfSettlement);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные платежа обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update settlement", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public SelectedSettlementGroupPage getSelectedSettlementGroupPage() {
        return selectedSettlementGroupPage;
    }

    public Object showSelectedSettlementGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedSettlementGroupPage.fill(persistenceSession, selectedIdOfSettlement);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedSettlementGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected settlement group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы платежа между контрагентами",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }
    
    /*
        Начисление платы за обслуживание
     */
    // baybikov (29.11.2011)
    public BasicWorkspacePage getAddPaymentGroupPage() {
        return addPaymentGroupPage;
    }

    // baybikov (29.11.2011)
    public Object showAddPaymentGroupPage() {
        currentWorkspacePage = addPaymentGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (29.11.2011)
    public AddPaymentListPage getAddPaymentListPage() {
        return addPaymentListPage;
    }

    // baybikov (29.11.2011)
    public Object showAddPaymentListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = addPaymentListPage;
        } catch (Exception e) {
            logger.error("Failed to fill contragent addPayment list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы платежей между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (29.11.2011)
    public AddPaymentCreatePage getAddPaymentCreatePage() {
        return addPaymentCreatePage;
    }

    // baybikov (29.11.2011)
    public Object showAddPaymentCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = addPaymentCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show contragent addPayment create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации платежа между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (29.11.2011)
    public Object createAddPayment() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentCreatePage.createAddPayment(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Платеж между контрагентами зарегистрирован успешно", null));
        } catch (AddPaymentCreatePage.WrongContragentsException e) {
            logger.error("Failed to create addPayment", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Платеж между указанными контрагентами не может быть осуществлен", null));
        } catch (Exception e) {
            logger.error("Failed to create addPayment", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации платежа между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (29.11.2011)
    public Object clearAddPaymentListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentListPage.getFilter().clear();
            addPaymentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for contragent client account list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке справочника платежей между контрагентами", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (29.11.2011)
    public AddPaymentDeletePage getAddPaymentDeletePage() {
        return addPaymentDeletePage;
    }

    // baybikov (29.11.2011)
    public Long getSelectedIdOfAddPayment() {
        return selectedIdOfAddPayment;
    }

    // baybikov (29.11.2011)
    public void setSelectedIdOfAddPayment(Long selectedIdOfAddPayment) {
        this.selectedIdOfAddPayment = selectedIdOfAddPayment;
    }

    // baybikov (29.11.2011)
    public Object removeAddPayment() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentDeletePage.removeAddPayment(persistenceSession, selectedIdOfAddPayment);
            addPaymentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove addPayment", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (29.11.2011)
    public String showAddPaymentCSVList() {
        return "showAddPaymentCSVList";
    }

    // baybikov (29.11.2011)
    public Object showAddPaymentEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedAddPaymentGroupPage.fill(persistenceSession, selectedIdOfAddPayment);
            addPaymentEditPage.fill(persistenceSession, selectedIdOfAddPayment);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedAddPaymentGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = addPaymentEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill addPayment edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (29.11.2011)
    public AddPaymentEditPage getAddPaymentEditPage() {
        return addPaymentEditPage;
    }

    // baybikov (29.11.2011)
    public Object updateAddPayment() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            addPaymentEditPage.updateAddPayment(persistenceSession, selectedIdOfAddPayment);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные платежа обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update addPayment", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных платежа", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    public SelectedAddPaymentGroupPage getSelectedAddPaymentGroupPage() {
        return selectedAddPaymentGroupPage;
    }

    public Object showSelectedAddPaymentGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedAddPaymentGroupPage.fill(persistenceSession, selectedIdOfAddPayment);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedAddPaymentGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected addPayment group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы платежа между контрагентами",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }
    
    /*
      Категории
     */
    // baybikov (05.12.2011)
    public BasicWorkspacePage getCategoryGroupPage() {
        return categoryGroupPage;
    }

    // baybikov (05.12.2011)
    public Object showCategoryGroupPage() {
        currentWorkspacePage = categoryGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.12.2011)
    public CategoryListPage getCategoryListPage() {
        return categoryListPage;
    }

    // baybikov (05.12.2011)
    public Object showCategoryListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = categoryListPage;
        } catch (Exception e) {
            logger.error("Failed to fill category list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы категорий", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.12.2011)
    public CategoryCreatePage getCategoryCreatePage() {
        return categoryCreatePage;
    }

    // baybikov (05.12.2011)
    public Object showCategoryCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = categoryCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show category create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы категорий", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.12.2011)
    public Object createCategory() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryCreatePage.createCategory(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Категория зарегистрирована успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create category", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации категории", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (05.12.2011)
    public CategoryDeletePage getCategoryDeletePage() {
        return categoryDeletePage;
    }

    // baybikov (05.12.2011)
    public Long getSelectedIdOfCategory() {
        return selectedIdOfCategory;
    }

    // baybikov (05.12.2011)
    public void setSelectedIdOfCategory(Long selectedIdOfCategory) {
        this.selectedIdOfCategory = selectedIdOfCategory;
    }

    // baybikov (05.12.2011)
    public Object removeCategory() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryDeletePage.removeCategory(persistenceSession, selectedIdOfCategory);
            categoryListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove category", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении категории", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (05.12.2011)
    public Object showCategoryEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedCategoryGroupPage.fill(persistenceSession, selectedIdOfCategory);
            categoryEditPage.fill(persistenceSession, selectedIdOfCategory);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedCategoryGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = categoryEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill category edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования категорий", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (05.12.2011)
    public CategoryEditPage getCategoryEditPage() {
        return categoryEditPage;
    }

    // baybikov (05.12.2011)
    public Object updateCategory() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryEditPage.updateCategory(persistenceSession, selectedIdOfCategory);
            selectedCategoryGroupPage.fill(persistenceSession, selectedIdOfCategory);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные категории обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update category", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных категории", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (05.12.2011)
    public SelectedCategoryGroupPage getSelectedCategoryGroupPage() {
        return selectedCategoryGroupPage;
    }

    public Object showSelectedCategoryGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedCategoryGroupPage.fill(persistenceSession, selectedIdOfCategory);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedCategoryGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected category group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы категорий",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.12.2011)
    public CategorySelectPage getCategorySelectPage() {
        return categorySelectPage;
    }

    // baybikov (06.12.2011)
    public Object showCategorySelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof CategorySelectPage.CompleteHandler ||
            currentTopMostPage instanceof CategorySelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                categorySelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof CategorySelectPage.CompleteHandler) {
                    categorySelectPage.pushCompleteHandler((CategorySelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(categorySelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill category selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора категории", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        return null;
    }

    // baybikov (06.12.2011)
    public Object completeCategorySelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categorySelectPage.completeCategorySelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == categorySelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete category selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора категории", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }
    
    /*
        Правила
     */
    // baybikov (06.12.2011)
    public BasicWorkspacePage getRuleGroupPage() {
        return ruleGroupPage;
    }

    // baybikov (06.12.2011)
    public Object showRuleGroupPage() {
        currentWorkspacePage = ruleGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.12.2011)
    public RuleListPage getRuleListPage() {
        return ruleListPage;
    }

    // baybikov (06.12.2011)
    public Object showRuleListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = ruleListPage;
        } catch (Exception e) {
            logger.error("Failed to fill rule list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы правил", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.12.2011)
    public RuleCreatePage getRuleCreatePage() {
        return ruleCreatePage;
    }

    // baybikov (06.12.2011)
    public Object showRuleCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleCreatePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = ruleCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show rule create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы правил", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.12.2011)
    public Object createRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleCreatePage.createRule(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Правило зарегистрировано успешно", null));
        } catch (Exception e) {
            logger.error("Failed to create rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (06.12.2011)
    public RuleDeletePage getRuleDeletePage() {
        return ruleDeletePage;
    }

    // baybikov (06.12.2011)
    public Long getSelectedIdOfRule() {
        return selectedIdOfRule;
    }

    // baybikov (06.12.2011)
    public void setSelectedIdOfRule(Long selectedIdOfRule) {
        this.selectedIdOfRule = selectedIdOfRule;
    }

    // baybikov (06.12.2011)
    public Object removeRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleDeletePage.removeRule(persistenceSession, selectedIdOfRule);
            ruleListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove rule", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (06.12.2011)
    public Object showRuleEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedRuleGroupPage.fill(persistenceSession, selectedIdOfRule);
            ruleEditPage.fill(persistenceSession, selectedIdOfRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedRuleGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = ruleEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill rule edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования правил", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    // baybikov (06.12.2011)
    public RuleEditPage getRuleEditPage() {
        return ruleEditPage;
    }

    // baybikov (06.12.2011)
    public Object updateRule() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleEditPage.updateRule(persistenceSession, selectedIdOfRule);
            selectedRuleGroupPage.fill(persistenceSession, selectedIdOfRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные правила обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update rule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных правила", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }

    // baybikov (06.12.2011)
    public SelectedRuleGroupPage getSelectedRuleGroupPage() {
        return selectedRuleGroupPage;
    }

    public Object showSelectedRuleGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedRuleGroupPage.fill(persistenceSession, selectedIdOfRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedRuleGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected rule group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы правил",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        updateSelectedMainMenu();
        return null;
    }

    public String showCurrentPositionCSVList() {
        return "showCurrentPositionCSVList";
    }

    //private int workspaceState = WorkspaceConstants.DEFAULT_PAGE_INDEX;
    ///* For test only */    
    //private String smsMessageId;
    //private String smsMessageText;
    //private String smsPhoneNumber;
    //private String currentSmsMessageId;            
    //
    //public String testSmsSend() {
    //    FacesContext facesContext = FacesContext.getCurrentInstance();
    //    try {
    //        SendResponse response = RuntimeContext.getInstance().getSmsService()
    //                .sendTextMessage(smsMessageId, null, smsPhoneNumber, smsMessageText);
    //        if (response.isSuccess()) {
    //            facesContext.addMessage(null,
    //                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Запрос на отправку отправлен успешно.", null));
    //        } else {
    //            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
    //                    String.format("Запрос на отправку отправлен успешно, но служба ответила отказом. Результат: %s",
    //                            response.getStatusMessage()), null));
    //        }
    //    } catch (Exception e) {
    //        if (logger.isDebugEnabled()) {
    //            logger.debug("Failed working with SMS service", e);
    //        }
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
    //                String.format("Ошибка при работе с SMS службой: %s", StringUtils.defaultString(e.getMessage())),
    //                null));
    //        updateView();
    //        return null;
    //    }
    //    updateView();
    //    return null;
    //}
    //
    //public String testSmsDeliveryCheck() {
    //    FacesContext facesContext = FacesContext.getCurrentInstance();
    //    try {
    //        DeliveryResponse response = RuntimeContext.getInstance().getSmsService().getDeliveryStatus(smsMessageId);
    //        if (response.isDelivered()) {
    //            facesContext.addMessage(null,
    //                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщение доставлено успешно.", null));
    //        } else {
    //            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
    //                    String.format("Сообщение недоставлено. Результат: %s",
    //                            StringUtils.defaultString(response.getStatusMessage())), null));
    //        }
    //    } catch (Exception e) {
    //        if (logger.isDebugEnabled()) {
    //            logger.debug("Failed working with SMS service", e);
    //        }
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
    //                String.format("Ошибка при работе с SMS службой: %s", StringUtils.defaultString(e.getMessage())),
    //                null));
    //        updateView();
    //        return null;
    //    }
    //    updateView();
    //    return null;
    //}
    //
    //public String testSmsMessageIdGeneration2() {
    //    RuntimeContext runtimeContext = RuntimeContext.getInstance();
    //    FacesContext facesContext = FacesContext.getCurrentInstance();
    //    try {
    //        currentSmsMessageId = runtimeContext.getMessageIdGenerator().generate();
    //        if (logger.isDebugEnabled()) {
    //            logger.debug("Ok");
    //        }
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
    //        if (logger.isDebugEnabled()) {
    //            logger.debug("Ok");
    //        }
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
    //    } catch (Exception e) {
    //        logger.error("Failed", e);
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", null));
    //        updateView();
    //        return null;
    //    }
    //    updateView();
    //    return null;
    //}    
    //
    //public String showOrderDeleter() {
    //    workspaceState = WorkspaceConstants.SERVICE_ORDER_DELETE_STATE;
    //    updateView();
    //    return null;
    //}
    //
    //public String deleteOrder() {
    //    FacesContext facesContext = FacesContext.getCurrentInstance();
    //    try {
    //        orderDeleter.delete();
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ok", null));
    //    } catch (Exception e) {
    //        logger.error("Failed", e);
    //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", null));
    //    }
    //    updateView();
    //    return null;
    //}
    //
    //public String changePasswords() {
    //    Session session = null;
    //    AccountTransaction transaction = null;
    //    try {
    //        session = RuntimeContext.getInstance().getSessionFactory().openSession();
    //        transaction = session.beginTransaction();
    //        Org org = (Org) session.load(Org.class, 6L);
    //        Criteria clientCriteria = session.createCriteria(Client.class);
    //        clientCriteria.add(Restrictions.eq("org", org));
    //        List clients = clientCriteria.list();
    //        for (Object object : clients) {
    //            Client client = (Client) object;
    //            long contractId = client.getContractId();
    //            NumberFormat formatter = new DecimalFormat("0000");
    //            String newPassword = formatter.format(contractId % 10000);
    //            client.setPassword(newPassword);
    //            session.update(client);
    //        }
    //        transaction.commit();
    //        transaction = null;
    //    } catch (Exception e) {
    //        logger.error("Failed", e);
    //    } finally {
    //        HibernateUtils.rollback(transaction, logger);
    //        HibernateUtils.close(session, logger);
    //    }
    //    updateView();
    //    return null;
    //}
    //

    User currentUser;
    User getCurrentUser() throws Exception {
        if (currentUser==null) {
            FacesContext context = FacesContext.getCurrentInstance();
            String userName = context.getExternalContext().getRemoteUser();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            RuntimeContext runtimeContext=null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                ///
                currentUser = DAOUtils.findUser(persistenceSession, userName);
                /// perform lazy load of function
                currentUser.hasFunction(Function.FUNCD_ORG_VIEW);
                ///
                persistenceTransaction.commit();
                persistenceTransaction = null;
                currentWorkspacePage = reportJobCreatePage;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
                RuntimeContext.release(runtimeContext);
            }
        }
        /////
        return currentUser;
    }

    public static MainPage getSessionInstance() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        return (MainPage)context.getApplication().createValueBinding("#{mainPage}").getValue(context);
    }

    public boolean isEligibleToViewOrgs() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_ORG_VIEW);
    }

    public boolean isEligibleToViewUsers() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_USER_VIEW);
    }

    public boolean isEligibleToViewContragents() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CONTRAGENT_VIEW);
    }

    public boolean isEligibleToViewCards() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CARD_VIEW);
    }

    public boolean isEligibleToEditCards() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CARD_EDIT);
    }

    public boolean isEligibleToViewClients() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CLIENT_VIEW);
    }

    public boolean isEligibleToRemoveClients() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CLIENT_REMOVE);
    }

    public boolean isEligibleToViewReports() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_REPORT_VIEW);
    }

    public boolean isEligibleToServiceClients() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_SERVICE_CLIENTS);
    }

    public boolean isEligibleToServiceSupport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_SERVICE_SUPPORT);
    }

    public boolean isEligibleToServiceAdmin() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_SERVICE_ADMIN);
    }

    public boolean isEligibleToEditClients() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CLIENT_EDIT);
    }

    public boolean isEligibleToEditContragents() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CONTRAGENT_EDIT);
    }

    public boolean isEligibleToEditReports() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_REPORT_EDIT);
    }

    public boolean isEligibleToEditOrgs() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_ORG_EDIT);
    }

    public boolean isEligibleToEditUsers() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_USER_EDIT);
    }

    public boolean isEligibleToDeleteUsers() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_USER_DELETE);
    }

    public boolean isEligibleToEditOptions() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_OPTION);
    }

    public boolean isEligibleToWorkOnlineReport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_ONLINE_REPORT);
    }

    public boolean isEligibleToCountCurrentPositions() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_COUNT_CURRENT_POSITIONS);
    }

    public boolean isEligibleToViewPos() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_POS_VIEW);
    }

    public boolean isEligibleToEditPos() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_POS_EDIT);
    }

    public boolean isEligibleToViewPayment() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_PAYMENT_VIEW);
    }

    public boolean isEligibleToEditPayment() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_PAYMENT_EDIT);
    }

    public boolean isEligibleToViewCategory() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CATEGORY_VIEW);
    }

    public boolean isEligibleToEditCategory() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_CATEGORY_EDIT);
    }

    public boolean isEligibleToViewRule() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_RULE_VIEW);
    }

    public boolean isEligibleToEditRule() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_RULE_EDIT);
    }

    public Object removeClient() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            if (!isEligibleToRemoveClients()) {
                facesContext
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Нет прав на удаление клиента", null));
                return null;
            }
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientEditPage.removeClient(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            setSelectedIdOfClient(null); selectedClientGroupPage.getMainMenuComponent().setRendered(false);
            showClientListPage();
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиент удален", null));
        } catch (Exception e) {
            logger.error("Failed to remove client", e);
            facesContext
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении клиента: "+e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            RuntimeContext.release(runtimeContext);
        }
        return null;
    }
}