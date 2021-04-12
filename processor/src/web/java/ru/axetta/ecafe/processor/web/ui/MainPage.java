/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import net.sf.jasperreports.engine.JRException;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.logic.CardManagerProcessor;
import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.UvDeletePage;
import ru.axetta.ecafe.processor.web.ui.addpayment.*;
import ru.axetta.ecafe.processor.web.ui.card.*;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.cardoperator.CardOperatorListPage;
import ru.axetta.ecafe.processor.web.ui.cardoperator.CardRegistrationAndIssuePage;
import ru.axetta.ecafe.processor.web.ui.cardoperator.CardRegistrationConfirm;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountCreatePage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountDeletePage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFileLoadPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountListPage;
import ru.axetta.ecafe.processor.web.ui.client.*;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.*;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.LoadingElementsOfBasicGoodsPage;
import ru.axetta.ecafe.processor.web.ui.contragent.*;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractSelectPage;
import ru.axetta.ecafe.processor.web.ui.event.*;
import ru.axetta.ecafe.processor.web.ui.journal.JournalViewPage;
import ru.axetta.ecafe.processor.web.ui.monitoring.StatusSyncReportPage;
import ru.axetta.ecafe.processor.web.ui.monitoring.SyncMonitorPage;
import ru.axetta.ecafe.processor.web.ui.monitoring.SyncReportPage;
import ru.axetta.ecafe.processor.web.ui.monitoring.SyncStatsPage;
import ru.axetta.ecafe.processor.web.ui.option.ConfigurationPage;
import ru.axetta.ecafe.processor.web.ui.option.ReportTemplateManagerPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategorySelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.security.OptionsSecurityClientPage;
import ru.axetta.ecafe.processor.web.ui.option.security.OptionsSecurityPage;
import ru.axetta.ecafe.processor.web.ui.option.security.OrgsSecurityPage;
import ru.axetta.ecafe.processor.web.ui.option.user.*;
import ru.axetta.ecafe.processor.web.ui.org.*;
import ru.axetta.ecafe.processor.web.ui.org.menu.MenuDetailsPage;
import ru.axetta.ecafe.processor.web.ui.org.menu.MenuExchangePage;
import ru.axetta.ecafe.processor.web.ui.org.menu.MenuViewPage;
import ru.axetta.ecafe.processor.web.ui.org.settings.*;
import ru.axetta.ecafe.processor.web.ui.pos.*;
import ru.axetta.ecafe.processor.web.ui.report.job.*;
import ru.axetta.ecafe.processor.web.ui.report.online.*;
import ru.axetta.ecafe.processor.web.ui.report.rule.*;
import ru.axetta.ecafe.processor.web.ui.report.security.*;
import ru.axetta.ecafe.processor.web.ui.service.*;
import ru.axetta.ecafe.processor.web.ui.service.msk.CancelCategoryBenefitsPage;
import ru.axetta.ecafe.processor.web.ui.service.msk.GroupControlBenefitsPage;
import ru.axetta.ecafe.processor.web.ui.service.msk.GroupControlSubscriptionsPage;
import ru.axetta.ecafe.processor.web.ui.settlement.*;
import ru.axetta.ecafe.processor.web.ui.user.UserListSelectPage;
import ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmLoadPage;
import ru.axetta.ecafe.processor.web.ui.webTechnolog.ComplexListSelectPage;
import ru.axetta.ecafe.processor.web.ui.webTechnolog.DishListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.richfaces.component.html.HtmlPanelMenu;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.PersistenceException;
import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 05.06.2009
 * Time: 14:49:47
 * To change this template use File | Settings | File Templates.
 */
public class MainPage implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MainPage.class);

    private String smsCode;
    private String newPassword;
    private String newPasswordConfirm;
    private Long selectedIdOfMenu;
    private Long selectedIdOfOrg;
    private CompositeIdOfContragentClientAccount removedIdOfCCAccount;
    private Long selectedIdOfContragent;
    private Long selectedIdOfAddPayment;
    private Long selectedIdOfClient;
    private Long selectedIdOfCard;
    private Long selectedIdOfRule;
    private Long selectedIdOfUser;
    private Long selectedIdOfUserGroup;
    private Long removedIdOfUser;
    private Long removedIdOfUserGroup;
    private Long selectedIdOfReportJob;
    private Long removedIdOfReportJob;
    private Long selectedIdOfPos;
    private Long selectedIdOfSettlement;
    private Long selectedIdOfEventNotification;
    private Long selectedIdOfFeedingSetting;
    private Long removedIdOfEventNotification;
    private Long idOfUser;
    private String removedReportTemplate;
    private String currentConfigurationProvider;
    /* Параметр фильтра по организациям в странице выбора списка оганизаций
     * если строка не пуста и заполнена номерами идентификаторов организаций
     * то отобразится диалоговое окно с организациями где будут помечены галочками
     * те организации идентификаторы которых перечислены черз запятую в этой строке
     * иначе отобразится весь список организаций без проставленных галочек*/
    private String orgFilterOfSelectOrgListSelectPage = "";
    private List<Long> idOfContragentOrgList = null;
    private List<Long> idOfContragentList = null;
    private int multiContrFlag = 0;
    private String classTypes;
    private String selectedMenuDataXML;
    private String categoryOrgFilterOfSelectCategoryOrgListSelectPage;
    private String categoryFilterOfSelectCategoryListSelectPage;
    private User currentUser;
    private Long selectedIdOfReportRule;
    private Long removedIdOfReportRule;
    private String DEFAULT_ORG_FILTER_PAGE_NAME = "Выбор организаций";
    private String orgFilterPageName = DEFAULT_ORG_FILTER_PAGE_NAME;
    private Long contractIdCardOperator;
    private String userFilterOfSelectUserListSelectPage = "";

    private boolean eligibleToViewUsers;
    private Boolean canSendAgain = false;

    private HtmlPanelMenu mainMenu;
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();
    private Stack<BasicPage> modalPages = new Stack<BasicPage>();

    private Boolean webARMppFilter = false;
    //Journals
    private final BasicWorkspacePage journalGroupPage = new BasicWorkspacePage();
    private final JournalBalancesReportPage journalBalancesReportPage = new JournalBalancesReportPage();
    private final JournalAuthenticationReportPage journalAuthenticationReportPage = new JournalAuthenticationReportPage();
    private final JournalProcessesReportPage journalProcessesReportPage = new JournalProcessesReportPage();
    private final JournalReportsReportPage journalReportsReportPage = new JournalReportsReportPage();

    // User manipulation
    private final BasicWorkspacePage userGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage userGroupGroupPage = new BasicWorkspacePage();
    private final UserListPage userListPage = new UserListPage();
    private final UserGroupListPage userGroupListPage = new UserGroupListPage();
    private final SelectedUserGroupPage selectedUserGroupPage = new SelectedUserGroupPage();
    private final SelectedUserGroupPage selectedUserGroupGroupPage = new SelectedUserGroupPage();
    private final UserViewPage userViewPage = new UserViewPage();
    private final UserGroupViewPage userGroupViewPage = new UserGroupViewPage();
    private final UserEditPage userEditPage = new UserEditPage();
    private final UserGroupEditPage userGroupEditPage = new UserGroupEditPage();
    private final UserCreatePage userCreatePage = new UserCreatePage();
    private final UserGroupCreatePage userGroupCreatePage = new UserGroupCreatePage();

    private final OptionsSecurityPage optionsSecurityPage = new OptionsSecurityPage();
    private final OptionsSecurityClientPage optionsSecurityClientPage = new OptionsSecurityClientPage();
    private final OrgsSecurityPage orgsSecurityPage = new OrgsSecurityPage();

    // Org manipulation
    private final BasicWorkspacePage orgGroupPage = new BasicWorkspacePage();
    //categories orgs
    private final BasicWorkspacePage benefitsOptionsPage = new BasicWorkspacePage();
    private final BasicWorkspacePage categoryOrgGroupPage = new BasicWorkspacePage();
    private final FeedingSettingGroupPage feedingSettingsGroupPage = new FeedingSettingGroupPage();
    private final FeedingSettingsListPage feedingSettingsListPage = new FeedingSettingsListPage();
    private final FeedingSettingViewPage feedingSettingViewPage = new FeedingSettingViewPage();
    private final FeedingSettingEditPage feedingSettingEditPage = new FeedingSettingEditPage();
    private final FeedingSettingCreatePage feedingSettingCreatePage = new FeedingSettingCreatePage();
    private final BasicWorkspacePage optionsGroupPage = new BasicWorkspacePage();
    private final OrgListPage orgListPage = new OrgListPage();
    private final SelectedOrgGroupPage selectedOrgGroupPage = new SelectedOrgGroupPage();
    private final OrgViewPage orgViewPage = new OrgViewPage();
    private final OrgEditPage orgEditPage = new OrgEditPage();
    private final OrgCreatePage orgCreatePage = new OrgCreatePage();
    private final OrgBalanceReportPage orgBalanceReportPage = new OrgBalanceReportPage();
    private final OrgOrderReportPage orgOrderReportPage = new OrgOrderReportPage();
    private final MenuViewPage menuViewPage = new MenuViewPage();
    private final MenuDetailsPage menuDetailsPage = new MenuDetailsPage();
    private final MenuExchangePage menuExchangePage = new MenuExchangePage();

    // Contragent manipulation
    private final BasicWorkspacePage contragentGroupPage = new BasicWorkspacePage();
    private final ContragentListPage contragentListPage = new ContragentListPage();
    private final SelectedContragentGroupPage selectedContragentGroupPage = new SelectedContragentGroupPage();
    private final ContragentViewPage contragentViewPage = new ContragentViewPage();
    private final ContragentEditPage contragentEditPage = new ContragentEditPage();
    private final ContragentCreatePage contragentCreatePage = new ContragentCreatePage();
    private final ContragentClientPaymentReportPage contragentClientPaymentReportPage = new ContragentClientPaymentReportPage();
    private final BasicWorkspacePage caOpsGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage clientOpsGroupPage = new BasicWorkspacePage();

    // Contragent client account manipulation
    private final BasicWorkspacePage ccAccountGroupPage = new BasicWorkspacePage();
    private final CCAccountListPage ccAccountListPage = new CCAccountListPage();
    private final CCAccountDeletePage ccAccountDeletePage = new CCAccountDeletePage();
    private final CCAccountCreatePage ccAccountCreatePage = new CCAccountCreatePage();
    private final CCAccountFileLoadPage ccAccountFileLoadPage = new CCAccountFileLoadPage();

    // Client manipulation
    private final BasicWorkspacePage clientGroupPage = new BasicWorkspacePage();
    private final ClientListPage clientListPage = new ClientListPage();
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
    private final BasicWorkspacePage thinClientUserGroupPage = new BasicWorkspacePage();
    private final EmpInfoPage empInfoPage = new EmpInfoPage();
    private final ServiceRNIPPage serviceRNIPPage = new ServiceRNIPPage();
    private final ServiceCheckSumsPage serviceCheckSumsPage = new ServiceCheckSumsPage();

    private final BasicWorkspacePage visitorDogmPage = new BasicWorkspacePage();

    // Card manipulation
    private final BasicWorkspacePage cardGroupPage = new BasicWorkspacePage();
    private final CardListPage cardListPage = new CardListPage();
    private final SelectedCardGroupPage selectedCardGroupPage = new SelectedCardGroupPage();
    private final CardViewPage cardViewPage = new CardViewPage();
    private final CardEditPage cardEditPage = new CardEditPage();
    private final CardCreatePage cardCreatePage = new CardCreatePage();
    private final CardOperationListPage cardOperationListPage = new CardOperationListPage();
    private final CardFileLoadPage cardFileLoadPage = new CardFileLoadPage();
    private final NewCardFileLoadPage newCardFileLoadPage = new NewCardFileLoadPage();
    private final CardExpireBatchEditPage cardExpireBatchEditPage = new CardExpireBatchEditPage();
    private final VisitorDogmLoadPage visitorDogmLoadPage = new VisitorDogmLoadPage();
    private final CreatedAndReissuedCardReportFromCardOperatorPage createdAndReissuedCardReportFromCardOperatorPage =
            new CreatedAndReissuedCardReportFromCardOperatorPage();
    private final IssuedCardsReportPage issuedCardsReportPage = new IssuedCardsReportPage();

    // Service pages
    private final BasicWorkspacePage serviceNewGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage serviceGroupPage = new BasicWorkspacePage();
    private final SupportEmailPage supportEmailPage = new SupportEmailPage();
    private final SupportInfoMailingPage supportInfoMailingPage = new SupportInfoMailingPage();
    private final TestLogPage testLogPage = new TestLogPage();
    private final BuildSignKeysPage buildSignKeysPage = new BuildSignKeysPage();
    private final OrderRemovePage orderRemovePage = new OrderRemovePage();
    private final GroupControlSubscriptionsPage groupControlSubscriptionsPage = new GroupControlSubscriptionsPage();
    private final GroupControlBenefitsPage groupControlBenefitsPage = new GroupControlBenefitsPage();
    private final CancelCategoryBenefitsPage cancelCategoryBenefitsPage = new CancelCategoryBenefitsPage();
    private final SyncStatsPage syncStatsPage = new SyncStatsPage();
    private final RegistryLoadPage registryLoadPage = new RegistryLoadPage();
    private final BasicWorkspacePage infoMessageGroupPage = new BasicWorkspacePage();
    private final InfoMessagePage infoMessagePage = new InfoMessagePage();
    private final InfoMessageCreatePage infoMessageCreatePage = new InfoMessageCreatePage();

    // Report job manipulation
    private final BasicWorkspacePage reportJobGroupPage = new BasicWorkspacePage();
    private final ReportJobListPage reportJobListPage = new ReportJobListPage();
    private final SelectedReportJobGroupPage selectedReportJobGroupPage = new SelectedReportJobGroupPage();
    private final ReportJobViewPage reportJobViewPage = new ReportJobViewPage();
    private final ReportJobEditPage reportJobEditPage = new ReportJobEditPage();
    private final ReportJobCreatePage reportJobCreatePage = new ReportJobCreatePage();
    private final QuartzJobsListPage quartzJobsListPage = new QuartzJobsListPage();

    // Report discountrule manipulation
    private final BasicWorkspacePage reportRuleGroupPage = new BasicWorkspacePage();
    private final ReportRuleListPage reportRuleListPage = new ReportRuleListPage();
    private final SelectedReportRuleGroupPage selectedReportRuleGroupPage = new SelectedReportRuleGroupPage();
    private final ReportRuleViewPage reportRuleViewPage = new ReportRuleViewPage();
    private final ReportRuleEditPage reportRuleEditPage = new ReportRuleEditPage();
    private final ReportRuleCreatePage reportRuleCreatePage = new ReportRuleCreatePage();

    // Report online manipulation
    private final BasicWorkspacePage reportOnlineGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage monitoringGroupPage = new BasicWorkspacePage();
    private final FreeComplexReportPage freeComplexReportPage = new FreeComplexReportPage();
    private final PayComplexReportPage payComplexReportPage = new PayComplexReportPage();
    private final BasicWorkspacePage complexGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage uosGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage autorechargePage = new BasicWorkspacePage();
    private final BasicWorkspacePage benefitPage = new BasicWorkspacePage();
    private final BasicWorkspacePage preorderPage = new BasicWorkspacePage();
    private final BasicWorkspacePage nsiGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage nsiGroupContingentPage = new BasicWorkspacePage();
    private final BasicWorkspacePage nsiGroupOrgPage = new BasicWorkspacePage();
    private final BasicWorkspacePage nsiGroupEmployeePage = new BasicWorkspacePage();
    private final BasicWorkspacePage spbGroupContingentPage = new BasicWorkspacePage();
    private final BasicWorkspacePage spbGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage orgParametersGroup = new BasicWorkspacePage();
    private final BasicWorkspacePage webTechnologistGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage webTechnologistCatalogGroupPage = new BasicWorkspacePage();
    private final SalesReportPage salesReportPage = new SalesReportPage();
    private final SyncReportPage syncReportPage = new SyncReportPage();
    private final StatusSyncReportPage statusSyncReportPage = new StatusSyncReportPage();
    private final ClientReportPage clientReportPage = new ClientReportPage();
    private final ClientBalanceByDayReportPage clientBalanceByDayReportPage = new ClientBalanceByDayReportPage();
    private final ClientBalanceByOrgReportPage clientBalanceByOrgReportPage = new ClientBalanceByOrgReportPage();
    private final BalanceLeavingReportPage balanceLeavingReportPage = new BalanceLeavingReportPage();
    private final ZeroTransactionsReportPage zeroTransactionsReportPage = new ZeroTransactionsReportPage();
    private final SpecialDatesReportPage specialDatesReportPage = new SpecialDatesReportPage();
    private final MigrantsReportPage migrantsReportPage = new MigrantsReportPage();
    private final MonitoringOfReportPage monitoringOfReportPage = new MonitoringOfReportPage();
    private final ClientTransactionsReportPage clientTransactionsReportPage = new ClientTransactionsReportPage();
    //private final BasicBasketReportPage basicBasketReportPage = new BasicBasketReportPage();
    private final SyncMonitorPage syncMonitorPage = new SyncMonitorPage();

    private final DetailedEnterEventReportPage detailedEnterEventReportPage = new DetailedEnterEventReportPage();
    private final BlockUnblockReportPage blockUnblockReportPage = new BlockUnblockReportPage();
    private final EnterEventReportPage enterEventReportPage = new EnterEventReportPage();
    private final BasicWorkspacePage configurationGroupPage = new BasicWorkspacePage();
    private final ConfigurationPage configurationPage = new ConfigurationPage();
    private final BasicWorkspacePage optionGroupPage = new BasicWorkspacePage();
    private final CurrentPositionsReportPage currentPositionsReportPage = new CurrentPositionsReportPage();
    private final AllComplexReportPage allComplexReportPage = new AllComplexReportPage();
    private final TotalSalesPage totalSalesPage = new TotalSalesPage();
    private final OrdersByManufacturerReportPage ordersByManufacturerReportPage = new OrdersByManufacturerReportPage();
    private final DishMenuWebARMPPReportPage dishMenuReportWebArmPP = new DishMenuWebARMPPReportPage();
    private final ComplexMenuReportPage complexMenuReportPage = new ComplexMenuReportPage();
    private final ComplexOrgReportPage complexOrgReportPage = new ComplexOrgReportPage();

    //Charts
    private final BasicWorkspacePage chartsGroupPage = new BasicWorkspacePage();
    private final EnterCardsChartReportPage enterCardsChartReportPage = new EnterCardsChartReportPage();

    // POS manipulation
    private final BasicWorkspacePage posGroupPage = new BasicWorkspacePage();
    private final PosListPage posListPage = new PosListPage();
    private final PosDeletePage posDeletePage = new PosDeletePage();
    private final PosCreatePage posCreatePage = new PosCreatePage();
    private final SelectedPosGroupPage selectedPosGroupPage = new SelectedPosGroupPage();
    private final PosEditPage posEditPage = new PosEditPage();

    // Settlement manipulation
    private final BasicWorkspacePage settlementGroupPage = new BasicWorkspacePage();
    private final SettlementListPage settlementListPage = new SettlementListPage();
    private final SettlementDeletePage settlementDeletePage = new SettlementDeletePage();
    private final SettlementCreatePage settlementCreatePage = new SettlementCreatePage();
    private final SettlementEditPage settlementEditPage = new SettlementEditPage();
    private final SelectedSettlementGroupPage selectedSettlementGroupPage = new SelectedSettlementGroupPage();

    // AddPayment manipulation
    private final BasicWorkspacePage addPaymentGroupPage = new BasicWorkspacePage();
    private final AddPaymentListPage addPaymentListPage = new AddPaymentListPage();
    private final AddPaymentDeletePage addPaymentDeletePage = new AddPaymentDeletePage();
    private final AddPaymentCreatePage addPaymentCreatePage = new AddPaymentCreatePage();
    private final AddPaymentEditPage addPaymentEditPage = new AddPaymentEditPage();
    private final SelectedAddPaymentGroupPage selectedAddPaymentGroupPage = new SelectedAddPaymentGroupPage();

    // Category manipulation
    private final BasicWorkspacePage categoryGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage categoryDSZNGroupPage = new BasicWorkspacePage();
    private final ConfirmDeletePage confirmDeletePage = new ConfirmDeletePage();

    // Rule manipulation
    private final BasicWorkspacePage ruleGroupPage = new BasicWorkspacePage();

    // Code MSP
    private final BasicWorkspacePage codeMSPGroupPage = new BasicWorkspacePage();

    // Event notification manipulation
    private final BasicWorkspacePage eventNotificationGroupPage = new BasicWorkspacePage();
    private final EventNotificationListPage eventNotificationListPage = new EventNotificationListPage();
    private final SelectedEventNotificationGroupPage selectedEventNotificationGroupPage = new SelectedEventNotificationGroupPage();
    private final EventNotificationViewPage eventNotificationViewPage = new EventNotificationViewPage();
    private final EventNotificationEditPage eventNotificationEditPage = new EventNotificationEditPage();
    private final EventNotificationCreatePage eventNotificationCreatePage = new EventNotificationCreatePage();

    // Modal pages
    private final OrgSelectPage orgSelectPage = new OrgSelectPage();
    private final OrgListSelectPage orgListSelectPage = new OrgListSelectPage();
    private final OrgListSelectPage contragentOrgListSelectPage = new OrgListSelectPage();
    private final ContragentSelectPage contragentSelectPage = new ContragentSelectPage();
    private final ContractSelectPage contractSelectPage = new ContractSelectPage();
    private final ContragentListSelectPage contragentListSelectPage = new ContragentListSelectPage();
    private final ComplexListSelectPage complexWebListSelectPage = new ComplexListSelectPage();
    private final DishListSelectPage dishWebListSelectPage = new DishListSelectPage();
    private final ClientSelectPage clientSelectPage = new ClientSelectPage();
    private final ClientSelectListPage clientSelectListPage = new ClientSelectListPage();
    private final ClientGroupSelectPage  clientGroupSelectPage = new ClientGroupSelectPage();
    private final UserSelectPage userSelectPage = new UserSelectPage();
    private final OrgMainBuildingListSelectPage orgMainBuildingListSelectPage = new OrgMainBuildingListSelectPage();
    private final CardRegistrationConfirm cardRegistrationConfirm = new CardRegistrationConfirm();
    private final UserListSelectPage userListSelectPage = new UserListSelectPage();

    private final CategorySelectPage categorySelectPage = new CategorySelectPage();
    private final CategoryListSelectPage categoryListSelectPage = new CategoryListSelectPage();
    private final RuleListSelectPage ruleListSelectPage = new RuleListSelectPage();
    private final CategoryOrgListSelectPage categoryOrgListSelectPage = new CategoryOrgListSelectPage();

    private final BasicWorkspacePage discountGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage goodRequestsGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage budgetFoodGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage paidFoodGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage subscriptionFeedingGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage acceptanceActGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage paymentReportsGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage activityReportsGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage clientReportsGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage informReportsGroupMenu = new BasicWorkspacePage();

    private final BasicWorkspacePage registerStampGroupMenu = new BasicWorkspacePage();
    private final BasicWorkspacePage registerStampElectronicCollationGroupMenu = new BasicWorkspacePage();

    private final OrgDiscountsReportPage orgDiscountsReportPage = new OrgDiscountsReportPage();
    private final AllOrgsDiscountsReportPage allOrgsDiscountsReportPage = new AllOrgsDiscountsReportPage();

    private final ReportTemplateManagerPage reportTemplateManagerPage = new ReportTemplateManagerPage();

    private final BasicWorkspacePage productGuideGroupPage = new BasicWorkspacePage();

    private final ConfigurationProviderCreatePage configurationProviderCreatePage = new ConfigurationProviderCreatePage();
    private final ConfigurationProviderListPage configurationProviderListPage = new ConfigurationProviderListPage();
    private final ConfigurationProviderViewPage configurationProviderViewPage = new ConfigurationProviderViewPage();
    private final ConfigurationProviderEditPage configurationProviderEditPage = new ConfigurationProviderEditPage();
    private final SelectedConfigurationProviderGroupPage selectedConfigurationProviderGroupPage = new SelectedConfigurationProviderGroupPage();
    private final BasicWorkspacePage configurationProviderGroupPage = new BasicWorkspacePage();

    private final BasicWorkspacePage reportGroupPage = new BasicWorkspacePage();

    private BasicWorkspacePage infoGroupMenu = new BasicWorkspacePage();
    private BasicWorkspacePage debugGroupMenu = new BasicWorkspacePage();
    private BasicWorkspacePage cardGroupMenu = new BasicWorkspacePage();

    private final BasicWorkspacePage technologicalMapGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage technologicalMapGroupsGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage productGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage productGroupsGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage goodGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage goodGroupsGroupPage = new BasicWorkspacePage();

    private final ContragentCompletionReportPage contragentCompletionReportPage = new ContragentCompletionReportPage();
    private final ContragentPaymentReportPage contragentPaymentReportPage = new ContragentPaymentReportPage();
    private final ContragentPreordersReportPage contragentPreordersReportPage = new ContragentPreordersReportPage();
    private final ClientPaymentsPage clientPaymentsReportPage = new ClientPaymentsPage();
    private final GoodRequestsNewReportPage goodRequestsNewReportPage = new GoodRequestsNewReportPage();
    private final DeliveredServicesReportPage deliveredServicesReportPage = new DeliveredServicesReportPage();
    private final DeliveredServicesElectronicCollationReportPage deliveredServicesElectronicCollationReportPage = new DeliveredServicesElectronicCollationReportPage();
    private final ClientsBenefitsReportPage clientsBenefitsReportPage = new ClientsBenefitsReportPage();
    private final StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage discrepanciesOnOrdersAndAttendanceReportPage = new StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage();
    private final DetailedGoodRequestReportPage detailedGoodRequestReportPage = new DetailedGoodRequestReportPage();
    private final DiscrepanciesDataOnOrdersAndPaymentReportPage discrepanciesDataOnOrdersAndPaymentReportPage = new DiscrepanciesDataOnOrdersAndPaymentReportPage();
    private final DetailedDeviationsPaymentOrReducedPriceMealsReportPage detailedDeviationsPaymentOrReducedPriceMealsReportPage = new DetailedDeviationsPaymentOrReducedPriceMealsReportPage();
    private final DetailedDeviationsWithoutCorpsReportPage detailedDeviationsWithoutCorpsReportPage = new DetailedDeviationsWithoutCorpsReportPage();
    private final DetailedDeviationsWithoutCorpsNewReportPage detailedDeviationsWithoutCorpsNewReportPage = new DetailedDeviationsWithoutCorpsNewReportPage();
    private final RequestsAndOrdersReportPage requestsAndOrdersReportPage = new RequestsAndOrdersReportPage();
    private final TypesOfCardReportPage typesOfCardReportPage = new TypesOfCardReportPage();
    private final PaymentTotalsReportPage paymentTotalsReportPage = new PaymentTotalsReportPage();
    private final RegularPaymentsReportPage regularPaymentsReportPage = new RegularPaymentsReportPage();
    private final FinancialControlPage financialControlPage = new FinancialControlPage();
    private final LatePaymentReportPage latePaymentReportPage = new LatePaymentReportPage();
    private final LatePaymentDetailedReportPage latePaymentDetailedReportPage = new LatePaymentDetailedReportPage();
    private final AdjustmentPaymentReportPage adjustmentPaymentReportPage = new AdjustmentPaymentReportPage();
    private final SalesReportGroupPage salesReportGroupPage = new SalesReportGroupPage();
    private final TaloonApprovalVerificationPage taloonApprovalVerificationPage = new TaloonApprovalVerificationPage();
    private final TaloonPreorderVerificationPage taloonPreorderVerificationPage = new TaloonPreorderVerificationPage();
    private final ElectronicReconciliationStatisticsPage electronicReconciliationStatisticsPage = new ElectronicReconciliationStatisticsPage();
    private final BasicWorkspacePage electronicReconciliationReportGroupMenu = new BasicWorkspacePage();
    private final CardOperatorListPage cardOperatorListPage = new CardOperatorListPage();
    private final CardRegistrationAndIssuePage cardRegistrationAndIssuePage = new CardRegistrationAndIssuePage();
    private final ClientCreateByCardOperatorPage clientRegistrationByCardOperatorPage = new ClientCreateByCardOperatorPage();
    private final AutoEnterEventReportPage autoEnterEventReportPage = new AutoEnterEventReportPage();
    private final EnterEventJournalReportPage enterEventJournalReportPage = new EnterEventJournalReportPage();

    private final LoadingElementsOfBasicGoodsPage loadingElementsOfBasicGoodsPage = new LoadingElementsOfBasicGoodsPage();

    private final BasicWorkspacePage repositoryUtilityGroupMenu = new BasicWorkspacePage();

    public BasicWorkspacePage getGoodGroupPage() {
        return goodGroupPage;
    }

    public BasicWorkspacePage getGoodGroupsGroupPage() {
        return goodGroupsGroupPage;
    }

    public BasicWorkspacePage getProductGroupsGroupPage() {
        return productGroupsGroupPage;
    }

    public BasicWorkspacePage getTechnologicalMapGroupsGroupPage() {
        return technologicalMapGroupsGroupPage;
    }

    public BasicWorkspacePage getTechnologicalMapGroupPage() {
        return technologicalMapGroupPage;
    }

    public Stack<BasicPage> getModalPages() {
        return modalPages;
    }

    public void setModalPages(Stack<BasicPage> modalPages) {
        this.modalPages = modalPages;
    }

    public BasicWorkspacePage getProductGuideGroupPage() {
        return productGuideGroupPage;
    }

    public String getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(String currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }

    public BasicWorkspacePage getConfigurationProviderGroupPage() {
        return configurationProviderGroupPage;
    }

    public ReportTemplateManagerPage getReportTemplateManagerPage() {
        return reportTemplateManagerPage;
    }

    public AllOrgsDiscountsReportPage getAllOrgsDiscountsReportPage() {
        return allOrgsDiscountsReportPage;
    }

    public BasicWorkspacePage getReportGroupPage() {
        return reportGroupPage;
    }

    public BasicWorkspacePage getOrgParametersGroup() {
        return orgParametersGroup;
    }

    public BasicWorkspacePage getWebTechnologistGroupPage() {
        return webTechnologistGroupPage;
    }

    public BasicWorkspacePage getWebTechnologistCatalogGroupPage() {
        return webTechnologistCatalogGroupPage;
    }

    public Object showAllOrgsDiscountReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = allOrgsDiscountsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set all orgs discount report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по льготам всех организаций: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public OrgDiscountsReportPage getOrgDiscountsReportPage() {
        return orgDiscountsReportPage;
    }

    public Object showOrgDiscountsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = orgDiscountsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set orgs discounts report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по льготам организации: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildAllOrgsDiscountsReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            allOrgsDiscountsReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build all orgs discounts report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object buildOrgDiscountsReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgDiscountsReportPage.buildReport();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build org discounts report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public String getEndOfLine() {
        return "\r\n";
    }

    public String logout() throws Exception {
        String outcome = "logout";
        User user = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext facesExternalContext = facesContext.getExternalContext();
        final String userLogin = facesExternalContext.getRemoteUser();
        if(StringUtils.isNotEmpty(userLogin)) {
            user = getUserByLogin(userLogin);
            if(user != null) {
                Integer idOfRole = user.getIdOfRole();
                if((idOfRole.equals(User.DefaultRole.ADMIN.getIdentification()))||(idOfRole.equals(User.DefaultRole.ADMIN_SECURITY.getIdentification())))
                    outcome = "logoutAdmin";
            }
        }
        HttpSession httpSession = (HttpSession) facesExternalContext.getSession(false);
        if (null != httpSession && StringUtils.isNotEmpty(facesExternalContext.getRemoteUser())) {
            httpSession.invalidate();
            ((HttpServletRequest)facesExternalContext.getRequest()).logout();
        }
        return outcome;
    }

    private User getUserByLogin(String login) throws Exception{
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        User user = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria userCriteria = persistenceSession.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("userName", login));
            userCriteria.add(Restrictions.eq("deletedState", false));
            user = (User) userCriteria.uniqueResult();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return user;
    }

    public void updateSelectedMainMenu() {
        UIComponent mainMenuComponent = currentWorkspacePage.getMainMenuComponent();
        idOfContragentOrgList = null;
        idOfContragentList = null;
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

    public BasicWorkspacePage getUserGroupGroupPage() {
        return userGroupGroupPage;
    }

    public Object showUserGroupPage() {
        currentWorkspacePage = userGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showUserGroupGroupPage() {
        currentWorkspacePage = userGroupGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showJournalGroupPage() {
        currentWorkspacePage = journalGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getBenefitsOptionsPage() {
        return benefitsOptionsPage;
    }

    public BasicWorkspacePage getCategoryOrgGroupPage() {
        return categoryOrgGroupPage;
    }

    public BasicWorkspacePage getOptionsGroupPage() {
        return optionsGroupPage;
    }

    public Object showBenefitsOptionsPage() {
        currentWorkspacePage = benefitsOptionsPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showCategoryOrgGroupPage() {
        currentWorkspacePage = categoryOrgGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showFeedingSettingGroupPage() {
        //currentWorkspacePage = feedingSettingsGroupPage;
        //updateSelectedMainMenu();
        //return null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            feedingSettingsGroupPage.fill(persistenceSession, selectedIdOfFeedingSetting);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = feedingSettingsGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill feeding setting group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы настройки платного питания: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public UserListPage getUserListPage() {
        return userListPage;
    }

    public UserGroupListPage getUserGroupListPage() {
        return userGroupListPage;
    }

    public Object showJournalBalancesReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //journalBalancesReportPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = journalBalancesReportPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы журнала изменений балансов клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showJournalAuthenticationReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = journalAuthenticationReportPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы журнала аутентификации пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showUserGroupListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userGroupListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = userGroupListPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showOptionsSecurityPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            optionsSecurityPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = optionsSecurityPage;
        } catch (Exception e) {
            logger.error("Failed to fill options security page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы настроек ИБ: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showOptionsSecurityClientPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            optionsSecurityClientPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = optionsSecurityClientPage;
        } catch (Exception e) {
            logger.error("Failed to fill options security page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы настроек ИБ: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showOrgsSecurityPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgsSecurityPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = orgsSecurityPage;
        } catch (Exception e) {
            logger.error("Failed to fill options security page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы управления флагами режимов ИБ организаций: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object removeUser(Long idOfUserRemoved, Long idOfUserSelected, SelectedUserGroupPage groupPage, UserListPage listPage) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            listPage.removeUser(persistenceSession, idOfUserRemoved);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (idOfUserRemoved.equals(idOfUserSelected)) {
                idOfUserSelected = null;
                groupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove user", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении пользователя: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object removeUser() {
        return removeUser(removedIdOfUser, selectedIdOfUser, selectedUserGroupPage, userListPage);
    }

    public Object removeUserGroup() {
        return removeUser(removedIdOfUserGroup, selectedIdOfUserGroup, selectedUserGroupGroupPage, userGroupListPage);
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

    public Object showSelectedUserGroupPage(SelectedUserGroupPage page) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            page.fill(persistenceSession, selectedIdOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = selectedUserGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected user group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showSelectedUserGroupPage() {
        return showSelectedUserGroupPage(selectedUserGroupPage);
    }

    public Object showSelectedUserGroupGroupPage() {
        return showSelectedUserGroupPage(selectedUserGroupGroupPage);
    }

    public UserViewPage getUserViewPage() {
        return userViewPage;
    }

    public Object showUserViewPage(SelectedUserGroupPage groupPage, UserViewPage viewPage, Long id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupPage.fill(persistenceSession, id);
            viewPage.fill(persistenceSession, id);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            groupPage.showAndExpandMenuGroup();
            currentWorkspacePage = viewPage;
        } catch (Exception e) {
            logger.error("Failed to fill user view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showUserViewPage() {
        return showUserViewPage(selectedUserGroupPage, userViewPage, selectedIdOfUser);
    }

    public Object showUserGroupViewPage() {
        return showUserViewPage(selectedUserGroupGroupPage, userGroupViewPage, selectedIdOfUserGroup);
    }

    public UserEditPage getUserEditPage() {
        return userEditPage;
    }

    public Object userSendActivationCode(String userName) throws Exception {
        try {
            User.requestSmsCode(userName);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Новый код активации выслан на номер мобильного телефона пользователя.", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    e.getMessage(), null));
        }
        return null;
    }

    public Object showUserEditPage(SelectedUserGroupPage groupPage, UserEditPage editPage, Long id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupPage.fill(persistenceSession, id);
            editPage.fill(persistenceSession, id);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            groupPage.showAndExpandMenuGroup();
            currentWorkspacePage = editPage;
        } catch (Exception e) {
            logger.error("Failed to fill user edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showUserEditPage() {
        return showUserEditPage(selectedUserGroupPage, userEditPage, selectedIdOfUser);
    }

    public Object showUserGroupEditPage() {
        return showUserEditPage(selectedUserGroupGroupPage, userGroupEditPage, selectedIdOfUserGroup);
    }

    public Object updateUser(UserEditPage editPage, SelectedUserGroupPage groupPage, Long idOfUser) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            editPage.updateUser(persistenceSession, idOfUser);
            groupPage.fill(persistenceSession, idOfUser);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные обновлены успешно", null));
        } catch (Exception e) {
            logger.error("Failed to update user", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object updateUser() {
        return updateUser(userEditPage, selectedUserGroupPage, selectedIdOfUser);
    }

    public Object updateUserGroup() {
        return updateUser(userGroupEditPage, selectedUserGroupGroupPage, selectedIdOfUserGroup);
    }

    public UserCreatePage getUserCreatePage() {
        return userCreatePage;
    }

    public UserGroupCreatePage getUserGroupCreatePage() {
        return userGroupCreatePage;
    }

    public Object showUserCreatePage() {
        return showGivenPage(userCreatePage);
    }

    public Object showUserGroupCreatePage() {
        return showGivenPage(userGroupCreatePage);
    }

    private Object showGivenPage(BasicWorkspacePage page) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            page.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = page;
        } catch (Exception e) {
            logger.error("Failed to show user create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при создании пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка организаций: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }


    /* обновление списка организаций */
    public Object updateOrgListPage() {
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
        } catch (Exception e) {
            logger.error("Failed to set filter for org list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка организаций: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }

        return null;
    }

    /* очистка фильтра и списка выводимых организаций */
    public Object clearOrgListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgListPage.getOrgFilter().clear();
            orgListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра данных организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public String getSelectedMenuDataXML() {
        return selectedMenuDataXML;
    }

    public void setSelectedMenuDataXML(String selectedMenuDataXML) {
        this.selectedMenuDataXML = selectedMenuDataXML;
    }

    public String showMenuDataXML() {
        return "showMenuDataXML";
    }

    public MenuExchangePage getMenuExchangePage() {
        return menuExchangePage;
    }

    public Object showMenuExchangePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            menuExchangePage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = menuExchangePage;
        } catch (Exception e) {
            logger.error("Failed to load menu exchange from table", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при выводе данных по мастера меню: " + e.getMessage(), null));
        } finally {

        }
        return null;
    }

    public MenuDetailsPage getMenuDetailsPage() {
        return menuDetailsPage;
    }

    public MenuViewPage getMenuViewPage() {
        return menuViewPage;
    }

    public Object showMenuViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            menuViewPage.setIdOfOrg(selectedIdOfOrg);
            currentWorkspacePage = menuViewPage;
        } catch (Exception e) {
            logger.error("Failed to load menu from table", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при выводе данных по меню: " + e.getMessage(),
                            null));
        }
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
            orgFilterOfSelectOrgListSelectPage = "";
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill org edit page", e);
            final String summary =
                    "Ошибка при подготовке страницы редактирования данных организации: " + e.getMessage();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateOrg() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (orgEditPage.isChangeSsoPassword() && !StringUtils
                .equals(orgEditPage.getPlainSsoPassword(), orgEditPage.getPlainSsoPasswordConfirmation())) {
            final String summary = "Пароль и подтверждение пароля не совпадают";
            final FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
            facesContext.addMessage(null, facesMessage);
            return null;
        }
        if (orgEditPage.getChangeCommodityAccounting()) {
            if (orgEditPage.getConfigurationProvider() == null) {
                final String summary = "Не указана 'Производственная конфигурация'";
                final FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
                facesContext.addMessage(null, facesMessage);
                return null;
            }
            if (orgEditPage.getRefectoryType() != 3 && orgEditPage.getMenuExchangeSourceOrg() == null) {
                final String summary = "Не указана 'Идентификатор организации - источника меню'";
                final FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
                facesContext.addMessage(null, facesMessage);
                return null;
            }

        }
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (orgEditPage.getChangeCommodityAccounting()) {
                orgEditPage.checkCommodityAccountingConfiguration(persistenceSession);
            }
            orgEditPage.updateOrg(persistenceSession, selectedIdOfOrg);
            selectedOrgGroupPage.fill(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            GoodRequestsChangeAsyncNotificationService.getInstance().refreshAllInformation();
            final String summary = "Данные организации обновлены успешно";
            final FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
            facesContext.addMessage(null, facesMessage);
        } catch (Exception e) {
            logger.error("Failed to update org", e);
            final String summary = "Ошибка при изменении данных организации: " + e.getMessage();
            final FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
            facesContext.addMessage(null, facesMessage);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
                    "Ошибка при подготовке страницы регистрации организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                GoodRequestsChangeAsyncNotificationService.getInstance().refreshAllInformation();
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Организация зарегистрирована успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create org", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при регистрации организации: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы отчета по балансу организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            orgBalanceReportPage.buildReport(persistenceSession, selectedIdOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build org balance report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            orgOrderReportPage.fill(persistenceSession, orgOrderReportPage.getIdOfOrg());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedOrgGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = orgOrderReportPage;
        } catch (Exception e) {
            logger.error("Failed to show org order report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по покупкам по организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public OrgSelectPage getOrgSelectPage() {
        return orgSelectPage;
    }

    public OrgListSelectPage getContragentOrgListSelectPage() {
        return contragentOrgListSelectPage;
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

    public ClientGroupSelectPage getClientGroupSelectPage() {
        return clientGroupSelectPage;
    }

    public Object showClientGroupSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ClientGroupSelectPage.CompleteHandler) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                if (params.get("idOfOrg") != null) {
                    Long idOfOrg = Long.parseLong(params.get("idOfOrg"));
                    clientGroupSelectPage.fill(persistenceSession, idOfOrg);
                } else {
                    clientGroupSelectPage.fill(persistenceSession, Long.parseLong("0"));
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                clientGroupSelectPage.pushCompleteHandler((ClientGroupSelectPage.CompleteHandler) currentTopMostPage);
                modalPages.push(clientGroupSelectPage);
            } catch (Exception e) {
                logger.error("Failed to fill client group selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора группы клиента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);

            }
        }
        return null;
    }

    public Object updateClientGroupSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (params.get("idOfOrg") != null) {
                Long idOfOrg = Long.parseLong(params.get("idOfOrg"));
                clientGroupSelectPage.fill(persistenceSession, idOfOrg);
            } else {
                clientGroupSelectPage.fill(persistenceSession, Long.parseLong("0"));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill client group selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора группы клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public Object completeClientGroupSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientGroupSelectPage.completeClientGroupSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == clientGroupSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fill client group selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора группы клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public Object showOrgSelectPage() {
        return showOrgSelectPage(null, null);
    }

    public Object showOrgSelectPage(Long idOfContragent) {
        return showOrgSelectPage(idOfContragent, null);
    }

    public Object showOrgSelectPage(Long idOfContragent, Long idOfContract) {
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
                orgSelectPage.updateOrgTypesItems();
                orgSelectPage.fill(idOfContragent, idOfContract, persistenceSession, idOfContragentOrgList);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgSelectPage.pushCompleteHandler((OrgSelectPage.CompleteHandler) currentTopMostPage);
                modalPages.push(orgSelectPage);
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

    public String getOrgFilterOfSelectOrgListSelectPage() {
        return orgFilterOfSelectOrgListSelectPage;
    }

    public void setOrgFilterOfSelectOrgListSelectPage(String orgFilterOfSelectOrgListSelectPage) {
        this.orgFilterOfSelectOrgListSelectPage = orgFilterOfSelectOrgListSelectPage;
    }

    public void resetOrgFilterPageName() {
        orgFilterPageName = DEFAULT_ORG_FILTER_PAGE_NAME;
    }

    public String getOrgFilterPageName() {
        return orgFilterPageName;
    }

    public void setOrgFilterPageName(String orgFilterPageName) {
        this.orgFilterPageName = orgFilterPageName;
    }

    public Object showOrgListSelectPage(List<Long> idOfContragents) {
        return showOrgListSelectPageWebArm(idOfContragents, null);
    }
    public Object showOrgListSelectPageWebArm(List<Long> idOfContragents, Boolean webARM) {
        webARMppFilter = webARM;
        this.idOfContragentList = new ArrayList<Long>(idOfContragents);
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
                orgListSelectPage.setFilter("");
                orgListSelectPage.setIdFilter("");
                orgListSelectPage.setRegion("");
                orgListSelectPage.updateOrgTypesItems();
                if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
                    orgListSelectPage.fill(persistenceSession, false, idOfContragentOrgList, idOfContragentList, webARMppFilter);
                } else {
                    orgListSelectPage
                            .fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, false, idOfContragentOrgList,
                                    idOfContragentList, this, webARMppFilter);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgListSelectPage.pushCompleteHandlerList((OrgListSelectPage.CompleteHandlerList) currentTopMostPage);
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


    public Object showOrgListSelectPage() {
        return showOrgListSelectPageWebArm(null);
    }

    public Object showOrgListSelectPageWebArm(Boolean webARM) {
        webARMppFilter = webARM;
        idOfContragentList = new ArrayList<>();
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
                orgListSelectPage.setFilter("");
                orgListSelectPage.setIdFilter("");
                orgListSelectPage.setRegion("");
                orgListSelectPage.updateOrgTypesItems();
                if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
                    orgListSelectPage.fill(persistenceSession, false, idOfContragentOrgList, idOfContragentList, webARMppFilter);
                } else {
                    orgListSelectPage
                            .fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, false, idOfContragentOrgList,
                                    idOfContragentList, this, webARMppFilter);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                orgListSelectPage.pushCompleteHandlerList((OrgListSelectPage.CompleteHandlerList) currentTopMostPage);
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

    public void showContragentSelectPageOwn(Boolean isPaymentContragent) {
        BasicPage currentTopMostPage = MainPage.getSessionInstance().getTopMostPage();
        if (currentTopMostPage instanceof ContragentListSelectPage.CompleteHandler
                || currentTopMostPage instanceof ContragentListSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            String classType;
            if(isPaymentContragent){
                classType = "2";
            } else {
                classType = "1";
            }
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                MainPage.getSessionInstance().getContragentListSelectPage().fill(persistenceSession, 0, classType);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof ContragentListSelectPage.CompleteHandler) {
                    MainPage.getSessionInstance().getContragentListSelectPage().pushCompleteHandler(
                            (ContragentListSelectPage.CompleteHandler) currentTopMostPage);
                    MainPage.getSessionInstance().getModalPages().push(
                            MainPage.getSessionInstance().getContragentListSelectPage());
                }
            } catch (Exception e) {
                logger.error("Failed to fill contragent selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора контрагента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
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
            orgSelectPage.updateOrgTypesItems();
            orgSelectPage.fill(persistenceSession, idOfContragentOrgList);
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

    public Object updateUserSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            getUserSelectPage().fill(persistenceSession, getIdOfUser());
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill org selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public void setIdOfContragentOrgList(List<Long> idOfContragentOrgList) {
        this.idOfContragentOrgList = idOfContragentOrgList;
    }

    public List<Long> getIdOfContragentOrgList() {
        return idOfContragentOrgList;
    }

    public List<Long> getIdOfContragentList() {
        return idOfContragentList;
    }

    public void setIdOfContragentList(List<Long> idOfContragentList) {
        this.idOfContragentList = idOfContragentList;
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
            if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
                orgListSelectPage.fill(persistenceSession, true, idOfContragentOrgList, idOfContragentList, webARMppFilter);
            } else {
                orgListSelectPage
                        .fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, true, idOfContragentOrgList,
                                idOfContragentList, this, webARMppFilter);
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

    public Object updateOrgListSelectPageWithItemDeselection() {
        if (orgListSelectPage.getFilterMode() == 2) {
            orgListSelectPage.setDistrictFilterDisabled(true);
        } else {
            orgListSelectPage.setDistrictFilterDisabled(false);
        }
        updateOrgListSelectPage();
        orgListSelectPage.deselectAllItems();
        return null;
    }

    public Object updateOrgSelectPageWithItemDeselection() {
        if (orgSelectPage.getFilterMode() == 2) {
            orgSelectPage.setDistrictFilterDisabled(true);
        } else {
            orgSelectPage.setDistrictFilterDisabled(false);
        }
        updateOrgSelectPage();
        return null;
    }

    public Object clearOrgListSelectedItemsList() {
        orgFilterOfSelectOrgListSelectPage = "";
        orgListSelectPage.deselectAllItems();
        orgListSelectPage.clearSelectedOrgMap();
        updateOrgListSelectPage();
        return null;
    }

    public Object clearContragentListSelectedItemsList() {
        contragentListSelectPage.deselectAllItems();
        return null;
    }

    public Object clearComplexListSelectedItemsList() {
        complexWebListSelectPage.deselectAllItems();
        return null;
    }

    public Object clearDishListSelectedItemsList() {
        dishWebListSelectPage.deselectAllItems();
        return null;
    }

    public Object selectAllOrgListSelectedItemsList() {
        orgListSelectPage.selectAllItems();
        updateOrgListSelectPage();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object completeUserSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userSelectPage.completeUserSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == userSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete user selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object cancelUserSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            userSelectPage.cancelUserSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == userSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete user selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора пользователя: " + e.getMessage(), null));
        }
        return null;
    }

    public Object cancelOrgSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            orgSelectPage.cancelOrgSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete org selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора организации: " + e.getMessage(), null));
        }
        return null;
    }

    public Object cancelClientListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            clientSelectListPage.cancelButtonClick();
            if (!modalPages.empty()) {
                if (modalPages.peek() == clientSelectListPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete client list selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора физ лиц: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeOrgListSelectionOk() {
        webARMppFilter = false;
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

    public Object completeOrgListSelectionCancel() {
        webARMppFilter = false;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра данных контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы редактирования данных контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getRNIPServiceBean();
            String prevRNIPId = rnipLoadPaymentsService.getRNIPIdFromRemarks(persistenceSession, selectedIdOfContragent);
            contragentEditPage.updateContragent(persistenceSession, selectedIdOfContragent);
            selectedContragentGroupPage.fill(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            GoodRequestsChangeAsyncNotificationService.getInstance().updateContragentItems();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные контрагента обновлены успешно", null));
            Boolean upd = contragentEditPage
                    .updateContragentRNIP(persistenceSession, selectedIdOfContragent, prevRNIPId);
            if (upd != null && upd == true) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные контрагента успешно добавлены в очередь обработки РНИП",
                                null));
            } else if (upd != null && upd == false) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не удалось загрузить данные контрагента в РНИП",
                                null));
            }
        } catch (IllegalArgumentException ise) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ise.getMessage(), null));
        } catch (IllegalStateException ise) {
            logger.error("Failed to update contragent catalog in RNIP", ise);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ise.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to update contragent", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы регистрации контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            GoodRequestsChangeAsyncNotificationService.getInstance().updateContragentItems();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Контрагент зарегистрирован успешно", null));
        } catch (ContragentCreatePage.ContragentWithClassExistsException e) {
            logger.error("Failed to create contragent", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации контрагента: для типов \"Оператор\", \"Бюждет\" и \"Клиент\" не может быть создано более одного контрагента",
                    null));
        } catch (Exception e) {
            logger.error("Failed to create contragent", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы отчета по платежам клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentClientPaymentReportPage.buildReport(persistenceSession, selectedIdOfContragent);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build contragent balance report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public ContragentSelectPage getContragentSelectPage() {
        return contragentSelectPage;
    }

    public ContractSelectPage getContractSelectPage() {
        return contractSelectPage;
    }

    public ContragentListSelectPage getContragentListSelectPage() {
        return contragentListSelectPage;
    }

    public void setMultiContrFlag(int multiContrFlag) {
        this.multiContrFlag = multiContrFlag;
    }

    public void setClassTypes(String classTypes) {
        this.classTypes = classTypes;
    }

    public Object selectAllContragentListSelectedItemsList() {
        contragentListSelectPage.selectAllItems();
        return null;
    }

    public Object selectAllComplexListSelectedItemsList() {
        complexWebListSelectPage.selectAllItems();
        return null;
    }


    public Object showContragentListSelectPage() {
        return showContragentListSelectPage(null) ;
    }

    public Object showContragentListSelectPage(List<Long> idOfOrgs) {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ContragentListSelectPage.CompleteHandler
                || currentTopMostPage instanceof ContragentListSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                if(contragentListSelectPage.getClassTypesString() != null){
                    classTypes = contragentListSelectPage.getClassTypesString();
                }
                contragentListSelectPage.fill(persistenceSession, multiContrFlag, classTypes, idOfOrgs);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof ContragentListSelectPage.CompleteHandler) {
                    contragentListSelectPage
                            .pushCompleteHandler((ContragentListSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(contragentListSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill contragents list selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора списка контрагентов: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        return null;
    }

    public Object showComplexListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ComplexListSelectPage.CompleteHandler
                || currentTopMostPage instanceof ComplexListSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                complexWebListSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof ComplexListSelectPage.CompleteHandler) {
                    complexWebListSelectPage
                            .pushCompleteHandler((ComplexListSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(complexWebListSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill complex list selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора списка комплексов меню для Web-технолога: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object showDishListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof DishListSelectPage.CompleteHandler
                || currentTopMostPage instanceof DishListSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                dishWebListSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof DishListSelectPage.CompleteHandler) {
                    dishWebListSelectPage
                            .pushCompleteHandler((DishListSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(dishWebListSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill dish list selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора списка блюд: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        return null;
    }

    public Object showContractSelectPage() {
        return showContractSelectPage(null, null);
    }

    public Object showContractSelectPage(String contragentName) {
        return showContractSelectPage(contragentName, null);
    }

    public Object showContractSelectPage(Long idOfContragent) {
        return showContractSelectPage(null, idOfContragent);
    }

    public Object showContractSelectPage(String contragentName, Long idOfContragent) {

        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ContractSelectPage.CompleteHandler
                || currentTopMostPage instanceof ContractSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                contractSelectPage.fill(persistenceSession, multiContrFlag, contragentName, idOfContragent);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof ContractSelectPage.CompleteHandler) {
                    contractSelectPage.pushCompleteHandler((ContractSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(contractSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill contract selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора контракта: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object showContragentSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof ContragentSelectPage.CompleteHandler
                || currentTopMostPage instanceof ContragentSelectPage) {
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
                        "Ошибка при подготовке страницы выбора контрагента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object completeContragentListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentListSelectPage.completeContragentSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == contragentListSelectPage) {
                    modalPages.pop();
                }
            }
            contragentListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("Failed to complete contragent list selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;

    }

    public Object completeComplexListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            complexWebListSelectPage.completeComplexSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == complexWebListSelectPage) {
                    modalPages.pop();
                }
            }
            complexWebListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("Failed to complete complex Web-технолог list selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка комплексов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;

    }

    public Object completeDishListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            dishWebListSelectPage.completeDishSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == dishWebListSelectPage) {
                    modalPages.pop();
                }
            }
            dishWebListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("Failed to complete dish list selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка блюд: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;

    }

    public Object cancelContragentListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            contragentListSelectPage.cancelContragentListSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == contragentListSelectPage) {
                    modalPages.pop();
                }
            }
            contragentListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("{}", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка контрагентов: " + e.getMessage(), null));
        }
        return null;
    }

    public Object cancelComplexListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            complexWebListSelectPage.cancelContragentListSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == complexWebListSelectPage) {
                    modalPages.pop();
                }
            }
            complexWebListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("{}", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка комплексов: " + e.getMessage(), null));
        }
        return null;
    }

    public Object cancelDishListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            dishWebListSelectPage.cancelContragentListSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == dishWebListSelectPage) {
                    modalPages.pop();
                }
            }
            dishWebListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("{}", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка блюд: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeContractSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contractSelectPage.completeContractSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == contractSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete contract selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора контракта: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;

    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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

    public BasicWorkspacePage getThinClientUserGroupPage() {
        return thinClientUserGroupPage;
    }

    public Object showThinClientUsersGroupPage() {
        currentWorkspacePage = thinClientUserGroupPage;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object updateContragentListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object clearContragentListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            contragentListPage.getContragentFilter().clear();
            contragentListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра данных клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы редактирования данных клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при изменении данных клиента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public ClientOperationListPage getClientOperationListPage() {
        return clientOperationListPage;
    }

    public Object showClientOperationListPage(boolean full) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientOperationListPage.fill(persistenceSession, selectedIdOfClient, full);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientOperationListPage;
        } catch (Exception e) {
            logger.error("Failed to show client operation list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра списка операций по клиенту: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                Client client = clientCreatePage.createClient(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                String.format("Клиент зарегистрирован успешно, ид %d, номер лицевого счета %d",
                                        client.getIdOfClient(), client.getContractId()), null));
            } catch (IllegalArgumentException e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Необходимо выбрать организацию!", null));
            }catch (Exception e) {
                logger.error("Failed to create client", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при регистрации клиента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object createClientByCardOperator() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = clientRegistrationByCardOperatorPage.createClient(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            String.format("Клиент зарегистрирован успешно, ид %d, номер лицевого счета %d",
                                    client.getIdOfClient(), client.getContractId()), null));
        } catch (IllegalArgumentException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Необходимо выбрать организацию!", null));
        }catch (Exception e) {
            logger.error("Failed to create client", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
                    "Ошибка при подготовке страницы загрузки регистрационного списка клиентов: " + e.getMessage(),
                    null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы загрузки списка обновлений клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public synchronized void clientLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (clientFileLoadPage.getOrg() == null || clientFileLoadPage.getOrg().getIdOfOrg() == null) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не указана организация", null));
                return;
            }
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("Загрузка клиентов из файла");
            User user = MainPage.getSessionInstance().getCurrentUser();
            clientsMobileHistory.setUser(user);
            clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
            clientFileLoadPage.loadClients(inputStream, dataSize, clientsMobileHistory);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиенты загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load clients from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по клиентам: : " + e.getMessage(), null));
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
            clientUpdateFileLoadPage.setErrorText("");
        } catch (Exception e) {
            logger.error("Failed to update clients from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по клиентам: " + e.getMessage(), null));
            clientUpdateFileLoadPage.setErrorText(e.getMessage());
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

    public String showGroupControlBenefitsCSVList() {
        return "showGroupControlBenefitsCSVList";
    }

    public String showLoadingElementsOfBasicGoodsCSVList() {
        return "showLoadingElementsOfBasicGoodsCSVList";
    }

    public String showGroupControlSubscriptionsCSVList() {
        return "showGroupControlSubscriptionsCSVList";
    }

    public String showCancelCategoryBenefitsCSVList() {
        return "showCancelCategoryBenefitsCSVList";
    }

    public String showClientLoadResultCSVList() {
        return "showClientLoadResultCSVList";
    }

    public String showClientUpdateLoadResultCSVList() {
        return "showClientUpdateLoadResultCSVList";
    }

    public String showClientUpdateGroupsLoadResultCSVList() {
        return "showClientUpdateGroupsLoadResultCSVList";
    }

    public String showCardLoadResultCSVList() {
        return "showCardLoadResultCSVList";
    }

    public String showNewCardLoadResultCSVList() {
        return "showNewCardLoadResultCSVList";
    }

    public String showRegistryLoadResultCSVList() {
        return "showRegistryLoadResultCSVList";
    }

    public String showNewVisitorLoadResultCSVList() {
        return "showNewVisitorLoadResultCSVList";
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

    public ClientSelectListPage getClientSelectListPage() {
        return clientSelectListPage;
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
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора клиента: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object showClientSelectListPage(List<ClientSelectListPage.Item> clientList) {
        return showClientSelectListPage(clientList, null);
    }

    public Object showClientSelectListPage(List<ClientSelectListPage.Item> clientList, Long idOfOrg) {
        BasicPage currentTopMostPage = getTopMostPage();
        //if (currentTopMostPage instanceof ClientSelectListPage.CompleteHandler) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //clientSelectListPage.updatePermanentOrg(persistenceSession, idOfOrg);
            clientSelectListPage.getClientFilter().setOffset(0);
            clientSelectListPage.fill(persistenceSession, clientList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (currentTopMostPage instanceof ClientSelectListPage.CompleteHandler) {
                clientSelectListPage.pushCompleteHandler((ClientSelectListPage.CompleteHandler) currentTopMostPage);
                modalPages.push(clientSelectListPage);
            }

        } catch (Exception e) {
            logger.error("Failed to fill client selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        //}
        return null;
    }

    public Object showUserSelectPage() {
        showUserSelectPage(null);
        return null;
    }

    public void showUserSelectPage(User.DefaultRole role) {
        BasicPage currentTopMostPage = getTopMostPage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            userSelectPage.fill(persistenceSession, null, role);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (currentTopMostPage instanceof UserSelectPage.CompleteHandler) {
                userSelectPage.pushCompleteHandler((UserSelectPage.CompleteHandler) currentTopMostPage);
                modalPages.push(userSelectPage);
            }

        } catch (Exception e) {
            logger.error("Failed to fill client selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора пользователя: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public Object clearClientSelectListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSelectListPage.clearClientFilter();
            clientSelectListPage.fill(persistenceSession, null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object completeClientListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientSelectListPage.completeClientSelection(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == clientSelectListPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete client selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public BasicPage popModal() {
        return modalPages.pop();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы подготовки договора: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при генерации номера договора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы изменения лимита овердрафта: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы изменения даты валидности карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Операция завершена c ошибками: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Операция завершена c ошибками: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы работы с клиентскими SMS: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Отправка SMS завершена с ошибками: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы работы с клиентскими SMS: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы работы с клиентскими SMS: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы работы с клиентскими SMS: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public BasicWorkspacePage getVisitorDogmPage() {
        return visitorDogmPage;
    }

    public Object showVisitorDogmPage() {
        currentWorkspacePage = visitorDogmPage;
        updateSelectedMainMenu();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public CardOperatorListPage getCardOperatorListPage() {
        return cardOperatorListPage;
    }

    public Object showCardOperatorListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardOperatorListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardOperatorListPage;
        } catch (Exception e) {
            logger.error("Failed to fill card list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object updateCardOperatorListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardOperatorListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for card list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object clearCardOperatorListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardOperatorListPage.getCardOperatorFilter().clear();
            cardOperatorListPage.getCardOperatorFilter().setShowOperationsAllPeriod(false);
            cardOperatorListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for card list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public CardRegistrationAndIssuePage getCardRegistrationAndIssuePage() {
        return cardRegistrationAndIssuePage;
    }

    public Object showCardRegistrationAndIssuePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardRegistrationAndIssuePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardRegistrationAndIssuePage;
        } catch (Exception e) {
            logger.error("Failed to show card create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showCardRegistrationAndIssuePageWithContractId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardRegistrationAndIssuePage.fill(persistenceSession);
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractIdCardOperator);
            cardRegistrationAndIssuePage.setClient(new ClientItem(client));
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = cardRegistrationAndIssuePage;
        } catch (Exception e) {
            logger.error("Failed to show card create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showClientRegistrationByCardOperatorPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientRegistrationByCardOperatorPage.fill(persistenceSession);
            clientRegistrationByCardOperatorPage.setNewContractId(null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = clientRegistrationByCardOperatorPage;
        } catch (Exception e) {
            logger.error("Failed to show card create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра данных карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы редактирования данных карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы регистрации карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации карты: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра списка операций по карте: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы загрузки списка карт на регистрацию: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }


    public synchronized void cardLoadFileListener(UploadEvent event) {
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по картам: " + e.getMessage(), null));
        } finally {
            close(inputStream);
        }
    }

    public NewCardFileLoadPage getNewCardFileLoadPage() {
        return newCardFileLoadPage;
    }

    public Object showNewCardFileLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            newCardFileLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = newCardFileLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show new card file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки новых непривязанных карт на регистрацию: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public synchronized void newCardLoadFileListener(UploadEvent event) {
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
            newCardFileLoadPage.loadCards(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Карты загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load cards from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по картам: " + e.getMessage(), null));
        } finally {
            close(inputStream);
        }
    }

    public VisitorDogmLoadPage getVisitorDogmLoadPage() {
        return visitorDogmLoadPage;
    }

    public Object showVisitorDogmLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            visitorDogmLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = visitorDogmLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show visitor file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки новых сотрудников: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public void newVisitorLoadFileListener(UploadEvent event) {
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
            visitorDogmLoadPage.loadVisitors(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Сотрудники загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load visitors from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных сотрудников: " + e.getMessage(), null));
        } finally {
            close(inputStream);
        }
    }

    public RegistryLoadPage getRegistryLoadPage() {
        return registryLoadPage;
    }

    public Object showRegistryLoadPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            registryLoadPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = registryLoadPage;
        } catch (Exception e) {
            logger.error("Failed to show registry file load page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы загрузки параметров клиентов из файла: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getCcAccountGroupPage() {
        return ccAccountGroupPage;
    }

    public Object showCCAccountGroupPage() {
        currentWorkspacePage = ccAccountGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showContragentOpsGroupPage() {
        currentWorkspacePage = caOpsGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showClientOpsGroupPage() {
        currentWorkspacePage = clientOpsGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getCaOpsGroupPage() {
        return caOpsGroupPage;
    }

    public BasicWorkspacePage getClientOpsGroupPage() {
        return clientOpsGroupPage;
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
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы списка счетов клиентов у контрагентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении счета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы регистрации счета клиента у контрагента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при регистрации счета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы загрузки списка счетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по счетам: " + e.getMessage(), null));
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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public void subscriptionLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        try {
            if (item.isTempFile()) {
                ru.axetta.ecafe.processor.core.mail.File file = new ru.axetta.ecafe.processor.core.mail.File();
                file.setFile(item.getFile());
                file.setFileName(item.getFileName());
                file.setContentType(item.getContentType());
                groupControlSubscriptionsPage.setUploadItem(item);
                groupControlSubscriptionsPage.getFiles().add(file);
            } else {
                throw new Exception("Invalid file");
            }
        } catch (Exception e) {
            logger.error("Failed to load file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public void groupControlGenerate() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        BufferedReader bufferedReader = null;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        try {
            groupControlSubscriptionsPage
                    .groupControlGenerate(groupControlSubscriptionsPage.getUploadItem(), runtimeContext,
                            bufferedReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Файл не был найден" + e.getMessage(), null));
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Файл неверного формата" + ex.getMessage(), null));
        } catch (IOException e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
                }
            }
        }
    }

    public void benefitsLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        try {
            if (item.isTempFile()) {
                ru.axetta.ecafe.processor.core.mail.File file = new ru.axetta.ecafe.processor.core.mail.File();
                file.setFile(item.getFile());
                file.setFileName(item.getFileName());
                file.setContentType(item.getContentType());
                groupControlBenefitsPage.setUploadItem(item);
                groupControlBenefitsPage.getFiles().add(file);
            } else {
                throw new Exception("Invalid file");
            }
        } catch (Exception e) {
            logger.error("Failed to load file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public void basicGoodsLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        try {
            if (item.isTempFile()) {
                ru.axetta.ecafe.processor.core.mail.File file = new ru.axetta.ecafe.processor.core.mail.File();
                file.setFile(item.getFile());
                file.setFileName(item.getFileName());
                file.setContentType(item.getContentType());
                loadingElementsOfBasicGoodsPage.setUploadItem(item);
                loadingElementsOfBasicGoodsPage.getFiles().add(file);
            } else {
                throw new Exception("Invalid file");
            }
        } catch (Exception e) {
            logger.error("Failed to load file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public void groupControlBenefitsGenerate() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        BufferedReader bufferedReader = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        groupControlBenefitsPage.groupBenefitsGenerate(groupControlBenefitsPage.getUploadItem(), runtimeContext);
    }

    public void cancelCategoryBenefitsGenerate() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        cancelCategoryBenefitsPage.cancelCategoryBenefitsGenerate(runtimeContext);
    }

    public void loadingElementsOfBasicGoodsGenerate() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        BufferedReader bufferedReader = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        loadingElementsOfBasicGoodsPage.loadingElementsOfBasicGoodsGenerate(loadingElementsOfBasicGoodsPage.getUploadItem(), runtimeContext);
    }

    public void reportTemplateLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        try {
            if (item.isTempFile()) {
                reportTemplateManagerPage.checkAndSaveFile(item);
            } else {
                throw new Exception("Invalid file");
            }
        } catch (Exception e) {
            logger.error("Failed to load file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public BasicWorkspacePage getServiceNewGroupPage() {
        return serviceNewGroupPage;
    }

    public BasicWorkspacePage getServiceGroupPage() {
        return serviceGroupPage;
    }

    public Object showServiceGroupPage() {
        currentWorkspacePage = serviceGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public SupportInfoMailingPage getSupportInfoMailingPage() {
        return supportInfoMailingPage;
    }

    public Object showSupportInfoMailingPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = supportInfoMailingPage;
        } catch (Exception e) {
            logger.error("Failed to fill support info mailing page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отправки информационной рассылки: " + e.getMessage(), null));
        }
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
                    "Ошибка при подготовке страницы отправки электронного письма: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при отправке электронного письма: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to fill report discountrule list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка правил обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

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
            removeJobRules(persistenceSession, removedIdOfReportRule);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (removedIdOfReportRule.equals(selectedIdOfReportRule)) {
                selectedIdOfReportRule = null;
                selectedReportRuleGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove report discountrule", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при удалении правила обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public void removeJobRules(Session session, Long idOfReportHandleRule) throws Exception {
        session.createQuery("delete from JobRules where reportHandleRule = :idOfReportHandleRule")
                .setLong("idOfReportHandleRule", idOfReportHandleRule).executeUpdate();
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
            logger.error("Failed to fill selected report discountrule group page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы правила обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to fill report discountrule view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра правила обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to fill report discountrule edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования правила обработки отчетов: " + e.getMessage(),
                    null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to update report discountrule", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении правила обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            reportRuleCreatePage.fill(persistenceSession, getCurrentUser());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = reportRuleCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show report discountrule create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания правила обработки отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to create report discountrule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании правила: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы списка правил уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при удалении правила обработки уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке общей страницы правила уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы просмотра правила уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы редактирования правила уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to update report discountrule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении правила: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы создания правила уведомлений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to create report discountrule", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании правила: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to show log ru.axetta.ecafe.processor.core.test page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы тестирования лога: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            logger.error("Failed to ru.axetta.ecafe.processor.core.test log", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при тестировании лога: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы генерации ключей подписи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при генерации ключей: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public OrderRemovePage getOrderRemovePage() {
        return orderRemovePage;
    }

    public GroupControlSubscriptionsPage getGroupControlSubscriptionsPage() {
        return groupControlSubscriptionsPage;
    }

    public GroupControlBenefitsPage getGroupControlBenefitsPage() {
        return groupControlBenefitsPage;
    }

    public CancelCategoryBenefitsPage getCancelCategoryBenefitsPage() {
        return cancelCategoryBenefitsPage;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы удаления покупки: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении покупки: " + e.getMessage(),
                            null));
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
                    "Ошибка при подготовке страницы списка задач по формированию отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении задачи: " + e.getMessage(),
                            null));
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
                    "Ошибка при подготовке общей страницы задач формирования отчетов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
                    "Ошибка при подготовке страницы редактирования задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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


            }
        } catch (Exception e) {
            logger.error("Failed to update report job", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении задачи: " + e.getMessage(),
                            null));
        }
        return null;
    }

    public ReportJobCreatePage getReportJobCreatePage() {
        return reportJobCreatePage;
    }

    public Object showReportJobCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showQuartzJobsListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            quartzJobsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = quartzJobsListPage;
        } catch (Exception e) {
            logger.error("Failed to show quartz jobs list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании задачи: " + e.getMessage(),
                            null));
        }
        return null;
    }

    public List<SelectItem> getAvailableCreateRules() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectItems = reportJobCreatePage.getAvailableCreateRules(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill report job edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return selectItems;
    }

    public List<SelectItem> getAvailableEditRules() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectItems = reportJobEditPage.getAvailableEditRules(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill report job edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования задачи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return selectItems;
    }

    public BasicWorkspacePage getReportOnlineGroupPage() {
        return reportOnlineGroupPage;
    }

    public BasicWorkspacePage getMonitoringGroupPage() {
        return monitoringGroupPage;
    }

    public Object showReportOnlineGroupPage() {
        currentWorkspacePage = reportOnlineGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showMonitoringGroupPage() {
        currentWorkspacePage = monitoringGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getComplexGroupPage() {
        return complexGroupPage;
    }

    public BasicWorkspacePage getNsiGroupPage() {
        return nsiGroupPage;
    }

    public BasicWorkspacePage getNsiGroupContingentPage() {
        return nsiGroupContingentPage;
    }

    public BasicWorkspacePage getNsiGroupOrgPage() {
        return nsiGroupOrgPage;
    }

    public BasicWorkspacePage getSpbGroupContingentPage() {
        return spbGroupContingentPage;
    }

    public BasicWorkspacePage getSpbGroupPage() {
        return spbGroupPage;
    }

    public BasicWorkspacePage getUosGroupPage() {
        return uosGroupPage;
    }

    public BasicWorkspacePage getAutorechargePage() {
        return autorechargePage;
    }

    public BasicWorkspacePage getBenefitPage() {
        return benefitPage;
    }

    public Object showComplexGroupPage() {
        currentWorkspacePage = complexGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showNSIGroupPage() {
        currentWorkspacePage = nsiGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showNSIGroupContingentPage() {
        currentWorkspacePage = nsiGroupContingentPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showNSIGroupOrgPage() {
        currentWorkspacePage = nsiGroupOrgPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showSpbGroupContingentPage() {
        currentWorkspacePage = spbGroupContingentPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showSpbGroupPage() {
        currentWorkspacePage = spbGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showUOSGroupPage() {
        currentWorkspacePage = uosGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showAutorechargeGroupPage() {
        currentWorkspacePage = autorechargePage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showBenefitGroupPage() {
        currentWorkspacePage = benefitPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showPreorderGroupPage() {
        currentWorkspacePage = preorderPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showOrgParametersGroupPage(){
        currentWorkspacePage = orgParametersGroup;
        updateSelectedMainMenu();
        return null;
    }

    public Object showWebTechnologistGroupPage(){
        currentWorkspacePage = webTechnologistGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showWebTechnologistCatalogGroupPage(){
        currentWorkspacePage = webTechnologistCatalogGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getDiscountGroupPage() {
        return discountGroupPage;
    }

    public Object showDiscountGroupPage() {
        currentWorkspacePage = discountGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getGoodRequestsGroupMenu() {
        return goodRequestsGroupMenu;
    }

    public BasicWorkspacePage getBudgetFoodGroupMenu() {
        return budgetFoodGroupMenu;
    }

    public BasicWorkspacePage getElectronicReconciliationReportGroupMenu() {
        return electronicReconciliationReportGroupMenu;
    }

    public LoadingElementsOfBasicGoodsPage getLoadingElementsOfBasicGoodsPage() {
        return loadingElementsOfBasicGoodsPage;
    }

    public BasicWorkspacePage getPaidFoodGroupMenu() {
        return paidFoodGroupMenu;
    }

    public BasicWorkspacePage getSubscriptionFeedingGroupMenu() {
        return subscriptionFeedingGroupMenu;
    }

    public BasicWorkspacePage getAcceptanceActGroupMenu() {
        return acceptanceActGroupMenu;
    }

    public BasicWorkspacePage getRepositoryUtilityGroupMenu() {
        return repositoryUtilityGroupMenu;
    }

    public BasicWorkspacePage getPaymentReportsGroupMenu() {
        return paymentReportsGroupMenu;
    }

    public BasicWorkspacePage getActivityReportsGroupMenu() {
        return activityReportsGroupMenu;
    }

    public BasicWorkspacePage getClientReportsGroupMenu() {
        return clientReportsGroupMenu;
    }

    public BasicWorkspacePage getInformReportsGroupMenu() {
        return informReportsGroupMenu;
    }

    public Object showGoodRequestsGroupMenu() {
        currentWorkspacePage = goodRequestsGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showBudgetFoodGroupMenu() {
        currentWorkspacePage = budgetFoodGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showElectronicReconciliationReportGroupMenu() {
        currentWorkspacePage = electronicReconciliationReportGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showPaidFoodGroupMenu() {
        currentWorkspacePage = paidFoodGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showSubscriptionFeedingGroupMenu() {
        currentWorkspacePage = subscriptionFeedingGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showAcceptanceGroupMenu() {
        currentWorkspacePage = acceptanceActGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showPaymentReportsGroupMenu() {
        currentWorkspacePage = paymentReportsGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showActivityReportsGroupMenu() {
        currentWorkspacePage = activityReportsGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showClientReportsGroupMenu() {
        currentWorkspacePage = clientReportsGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public Object showInformReportsGroupMenu() {
        currentWorkspacePage = informReportsGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getRegisterStampGroupMenu() {
        return registerStampGroupMenu;
    }

    public BasicWorkspacePage getRegisterStampElectronicCollationGroupMenu() {
        return registerStampElectronicCollationGroupMenu;
    }

    public Object showRepositoryUtilityGroupMenu() {
        currentWorkspacePage = repositoryUtilityGroupMenu;
        updateSelectedMainMenu();
        return null;
    }

    /*
        Беслатные комплексы
     */
    public FreeComplexReportPage getFreeComplexReportPage() {
        return freeComplexReportPage;
    }

    public Object showFreeComplexReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = freeComplexReportPage;
        } catch (Exception e) {
            logger.error("Failed to set free complex report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по бесплатным комплексам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildFreeComplexReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            freeComplexReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build free complex report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    /*
        Платные комплексы
     */
    public PayComplexReportPage getPayComplexReportPage() {
        return payComplexReportPage;
    }

    public Object showPayComplexReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = payComplexReportPage;
        } catch (Exception e) {
            logger.error("Failed to set pay complex report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по платным комплексам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildPayComplexReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            payComplexReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build pay complex report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public SalesReportPage getSalesReportPage() {
        return salesReportPage;
    }

    public ContragentCompletionReportPage getContragentCompletionReportPage() {
        return contragentCompletionReportPage;
    }

    public ContragentPaymentReportPage getContragentPaymentReportPage() {
        return contragentPaymentReportPage;
    }

    public ContragentPreordersReportPage getContragentPreordersReportPage() {
        return contragentPreordersReportPage;
    }

    public ClientPaymentsPage getClientPaymentsReportPage() {
        return clientPaymentsReportPage;
    }

    public DeliveredServicesReportPage getDeliveredServicesReportPage() {
        return deliveredServicesReportPage;
    }

    public DeliveredServicesElectronicCollationReportPage getDeliveredServicesElectronicCollationReportPage() {
        return deliveredServicesElectronicCollationReportPage;
    }

    public ClientsBenefitsReportPage getClientsBenefitsReportPage() {
        return clientsBenefitsReportPage;
    }

    public Object showClientsBenefitsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientsBenefitsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по количеству льгот: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildClientsBenefitsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientsBenefitsReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object showDeliveredServicesReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = deliveredServicesReportPage;
        } catch (Exception e) {
            logger.error("Failed to set delivered report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по предоставленным услугам (предварительный): " + e
                            .getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showTaloonApprovalVerificationPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = taloonApprovalVerificationPage;
            currentWorkspacePage.show();
        } catch (Exception e) {
            logger.error("Failed to set taloon approval verification page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы сверки реестров талонов: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showTaloonPreorderVerificationPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = taloonPreorderVerificationPage;
            currentWorkspacePage.show();
        } catch (Exception e) {
            logger.error("Failed to set taloon preorder verification page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы сверки реестров талонов (предзаказ): " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showElectronicReconciliationStatisticsPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = electronicReconciliationStatisticsPage;
            currentWorkspacePage.show();
        } catch (Exception e) {
            logger.error("Failed to set electronic reconciliation statistics verification page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы cтатистикb электронной сверки: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showDeliveredServicesElectronicCollationReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = deliveredServicesElectronicCollationReportPage;
        } catch (Exception e) {
            logger.error("Failed to set delivered report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по предоставленным услугам (электронная сверка): " + e
                            .getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildDeliveredServicesReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            deliveredServicesReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (JRException fnfe) {
            logger.error("Failed to build Delivered report", fnfe);
            String message = (fnfe.getCause() == null ? fnfe.getMessage() : fnfe.getCause().getMessage());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message), null));
        } catch (Exception e) {
            logger.error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object buildDeliveredServicesElectronicCollationReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            deliveredServicesElectronicCollationReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;

            if (deliveredServicesElectronicCollationReportPage.b == true) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Внимание! В выбранном периоде есть несогласованные даты", null));
            }

            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (JRException fnfe) {
            logger.error("Failed to build Delivered report", fnfe);
            String message = (fnfe.getCause() == null ? fnfe.getMessage() : fnfe.getCause().getMessage());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message), null));
        } catch (Exception e) {
            logger.error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public GoodRequestsNewReportPage getGoodRequestsNewReportPage() {
        return goodRequestsNewReportPage;
    }

    public RequestsAndOrdersReportPage getRequestsAndOrdersReportPage() {
        return requestsAndOrdersReportPage;
    }

    public TypesOfCardReportPage getTypesOfCardReportPage() {
        return typesOfCardReportPage;
    }

    public PaymentTotalsReportPage getPaymentTotalsReportPage() {
        return paymentTotalsReportPage;
    }

    public FinancialControlPage getFinancialControlPage() {
        return financialControlPage;
    }

    public LatePaymentReportPage getLatePaymentReportPage() {
        return latePaymentReportPage;
    }

    public LatePaymentDetailedReportPage getLatePaymentDetailedReportPage() {
        return latePaymentDetailedReportPage;
    }

    public AdjustmentPaymentReportPage getAdjustmentPaymentReportPage() {
        return adjustmentPaymentReportPage;
    }

    public StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage getDiscrepanciesOnOrdersAndAttendanceReportPage() {
        return discrepanciesOnOrdersAndAttendanceReportPage;
    }

    public DetailedGoodRequestReportPage getDetailedGoodRequestReportPage() {
        return detailedGoodRequestReportPage;
    }

    public DiscrepanciesDataOnOrdersAndPaymentReportPage getDiscrepanciesDataOnOrdersAndPaymentReportPage() {
        return discrepanciesDataOnOrdersAndPaymentReportPage;
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsReportPage getDetailedDeviationsPaymentOrReducedPriceMealsReportPage() {
        return detailedDeviationsPaymentOrReducedPriceMealsReportPage;
    }

    public DetailedDeviationsWithoutCorpsReportPage getDetailedDeviationsWithoutCorpsReportPage() {
        return detailedDeviationsWithoutCorpsReportPage;
    }

    public DetailedDeviationsWithoutCorpsNewReportPage getDetailedDeviationsWithoutCorpsNewReportPage() {
        return detailedDeviationsWithoutCorpsNewReportPage;
    }

    public SalesReportGroupPage getSalesReportGroupPage() {
        return salesReportGroupPage;
    }

    public AutoEnterEventReportPage getAutoEnterEventReportPage() {
        return autoEnterEventReportPage;
    }

    public EnterEventJournalReportPage getEnterEventJournalReportPage() {
        return enterEventJournalReportPage;
    }

    public Object showGoodRequestNewReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            goodRequestsNewReportPage.fill(persistenceSession, getCurrentUser());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = goodRequestsNewReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            String summary = "Ошибка при подготовке страницы отчета по запрошенным товарам: " + e.getMessage();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showRequestsAndOrdersReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            requestsAndOrdersReportPage.fill(persistenceSession, getCurrentUser());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = requestsAndOrdersReportPage;
            //facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            String summary = "Ошибка при подготовке страницы отчета по запрошенным товарам: " + e.getMessage();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showDiscrepanciesOnOrdersAndAttendanceReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            discrepanciesOnOrdersAndAttendanceReportPage.fill();
            currentWorkspacePage = discrepanciesOnOrdersAndAttendanceReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по запрошенным товарам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showLatePaymentReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            latePaymentReportPage.fill();
            currentWorkspacePage = latePaymentReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showAdjustmentPaymentReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            adjustmentPaymentReportPage.fill();
            currentWorkspacePage = adjustmentPaymentReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showLatePaymentDetailedReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            latePaymentDetailedReportPage.fill();
            currentWorkspacePage = latePaymentDetailedReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildDiscrepanciesOnOrdersAndAttendanceReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            discrepanciesOnOrdersAndAttendanceReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build DiscrepanciesOnOrdersAndAttendanceReport report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public void exportDiscrepanciesOnOrdersAndAttendanceReport(javax.faces.event.ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            discrepanciesOnOrdersAndAttendanceReportPage.export(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build DiscrepanciesOnOrdersAndAttendanceReport report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void exportDiscrepanciesOnOrdersAndAttendanceReportSum(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            discrepanciesOnOrdersAndAttendanceReportPage.exportSum(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build DiscrepanciesOnOrdersAndAttendanceReportSum report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public Object showAggregateGoodRequestReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = detailedGoodRequestReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: " + e.getMessage(),
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildDetailedGoodRequestReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            detailedGoodRequestReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build DiscrepanciesOnOrdersAndAttendanceReport report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object showDiscrepanciesDataOnOrdersAndPaymentReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            discrepanciesDataOnOrdersAndPaymentReportPage.fill();
            currentWorkspacePage = discrepanciesDataOnOrdersAndPaymentReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: " + e.getMessage(),
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showDetailedDeviationsPaymentOrReducedPriceMealsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            detailedDeviationsPaymentOrReducedPriceMealsReportPage.fill();
            currentWorkspacePage = detailedDeviationsPaymentOrReducedPriceMealsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: " + e.getMessage(),
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showDetailedDeviationsWithoutCorpsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            detailedDeviationsWithoutCorpsReportPage.fill();
            currentWorkspacePage = detailedDeviationsWithoutCorpsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: " + e.getMessage(),
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showDetailedDeviationsWithoutCorpsNewReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            detailedDeviationsWithoutCorpsNewReportPage.fill();
            currentWorkspacePage = detailedDeviationsWithoutCorpsNewReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы: " + e.getMessage(),
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showContragentPaymentsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = contragentPaymentReportPage;
        } catch (Exception e) {
            logger.error("Failed to set payment report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по платежам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    /*public Object showContragentPreordersReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = contragentPreordersReportPage;
        } catch (Exception e) {
            logger.error("Failed to set contragent preorders report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по предзаказам поставщиков питания: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }*/

    public Object showClientPaymentsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientPaymentsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по продажам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showSalesReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = salesReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sales report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по продажам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildSalesReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            salesReportPage.buildReport(persistenceSession, facesContext);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public String showFreeComplexCSVList() {
        return "showFreeComplexCSVList";
    }

    public String showPayComplexCSVList() {
        return "showPayComplexCSVList";
    }

    public String showSalesCSVList() {
        return "showSaleCSVList";
    }

    public SyncReportPage getSyncReportPage() {
        return syncReportPage;
    }

    public Object showSyncReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = syncReportPage;
        } catch (Exception e) {
            logger.error("Failed to set sync report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по синхронизации: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildSyncReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            syncReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build sync report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public StatusSyncReportPage getStatusSyncReportPage() {
        return statusSyncReportPage;
    }

    public Object showStatusSyncReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = statusSyncReportPage;
        } catch (Exception e) {
            logger.error("Failed to set status sync report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы статуса синхронизации: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildStatusSyncReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            statusSyncReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build status sync report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public DetailedEnterEventReportPage getDetailedEnterEventReportPage() {
        return detailedEnterEventReportPage;
    }

    public Object showDetailedEnterEventReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = detailedEnterEventReportPage;
        } catch (Exception e) {
            logger.error(" Failed to set enter event report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчет по турникетам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showBlockUnblockReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = blockUnblockReportPage;
        } catch (Exception e) {
            logger.error(" Failed to set block/unblock card page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке отчет: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }


    public EnterEventReportPage getEnterEventReportPage() {
        return enterEventReportPage;
    }

    public Object showEnterEventReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = enterEventReportPage;
        } catch (Exception e) {
            logger.error(" Failed to set enter event report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчет по турникетам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public String showEnterEventCSVList() {
        return "showEnterEventCSVList";
    }

    public ClientBalanceByDayReportPage getClientBalanceByDayReportPage() {
        return clientBalanceByDayReportPage;
    }

    public Object showClientBalanceByDayReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientBalanceByDayReportPage;
        } catch (Exception e) {
            logger.error("Failed to set ClientBalanceByDayreport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientBalanceByOrgReportPage getClientBalanceByOrgReportPage() {
        return clientBalanceByOrgReportPage;
    }

    public Object showClientBalanceByOrgReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientBalanceByOrgReportPage;
        } catch (Exception e) {
            logger.error("Failed to set ClientBalanceByOrgReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public ZeroTransactionsReportPage getZeroTransactionsReportPage() {
        return zeroTransactionsReportPage;
    }

    public Object showZeroTransactionsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = zeroTransactionsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set ZeroTransactionsReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    /*public Object showBasicBasketReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = basicBasketReportPage;
        } catch (Exception e) {
            logger.error("Failed to set BasicBasketReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }*/

    public SpecialDatesReportPage getSpecialDatesReportPage() {
        return specialDatesReportPage;
    }

    public Object showSpecialDatesReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = specialDatesReportPage;
        } catch (Exception e) {
            logger.error("Failed to set SpecialDatesReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public MigrantsReportPage getMigrantsReportPage() {
        return migrantsReportPage;
    }

    public Object showMigrantsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = migrantsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set MigrantsReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public MonitoringOfReportPage getMonitoringOfReportPage() {
        return monitoringOfReportPage;
    }

    public Object showMonitoringOfReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = monitoringOfReportPage;
        } catch (Exception e) {
            logger.error("Failed to set MonitoringOfReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientTransactionsReportPage getClientTransactionsReportPage() {
        return clientTransactionsReportPage;
    }

    public Object showClientTransactionsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientTransactionsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set MonitoringOfReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public BalanceLeavingReportPage getBalanceLeavingReportPage() {
        return balanceLeavingReportPage;
    }

    public Object showBalanceLeavingReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = balanceLeavingReportPage;
        } catch (Exception e) {
            logger.error("Failed to set BalanceLeavingReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public ClientReportPage getClientReportPage() {
        return clientReportPage;
    }

    public Object showClientReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = clientReportPage;
        } catch (Exception e) {
            logger.error("Failed to set client report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчет по учащимся: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildClientReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            if (clientReportPage.getContragentIds() != null) {
                if (!clientReportPage.getContragentIds().isEmpty()) {
                    runtimeContext = RuntimeContext.getInstance();
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    clientReportPage.buildReport(persistenceSession);
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
                } else {
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Выберите постащика : " + "", ""));
                }
            } else {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Выберите постащика : " + "", ""));
            }
        } catch (Exception e) {
            logger.error("Failed to build client report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public TotalSalesPage getTotalSalesPage() {
        return totalSalesPage;
    }

    public Object showTotalSalesPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = totalSalesPage;
        } catch (Exception e) {
            logger.error("Failed to set client report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчет по учащимся: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildTotalSalesReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;

        try {

            totalSalesPage.buildReportHTML();

            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build client report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {

        }
        return null;
    }

    public DishMenuWebARMPPReportPage getDishMenuReportWebArmPP() {
        return dishMenuReportWebArmPP;
    }
    public ComplexMenuReportPage getComplexMenuReportPage() {
        return complexMenuReportPage;
    }

    public ComplexOrgReportPage getComplexOrgReportPage() {
        return complexOrgReportPage;
    }

    public Object showDishMenuWebARMPPReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = dishMenuReportWebArmPP;
        } catch (Exception e) {
            logger.error("Failed to set DishMenuWebARMPPReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по блюдам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showComplexMenuReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = complexMenuReportPage;
        } catch (Exception e) {
            logger.error("Failed to set ComplexMenuReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по комплексам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public OrdersByManufacturerReportPage getOrdersByManufacturerReportPage() {
        return ordersByManufacturerReportPage;
    }

    public Object showOrdersByManufacturerReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = ordersByManufacturerReportPage;
        } catch (Exception e) {
            logger.error("Failed to set OrdersByManufacturerReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы сводного отчета по производителю: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getChartsGroupPage() {
        return chartsGroupPage;
    }

    public Object showChartsGroupPage() {
        currentWorkspacePage = chartsGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public EnterCardsChartReportPage getEnterCardsChartReportPage() {
        return enterCardsChartReportPage;
    }

    public Object showEnterCardsChartReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = enterCardsChartReportPage;
        } catch (Exception e) {
            logger.error("Failed to set EnterCardsChartReport page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public String showClientOrgCSVList() {
        return "showClientOrgCSVList";
    }

    /*
        Configuration
     */
    public ConfigurationPage getConfigurationPage() {
        return configurationPage;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы конфигурации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showReportTemplateManagerPage() {
        reportTemplateManagerPage.load();
        currentWorkspacePage = reportTemplateManagerPage;
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Конфигурация сохранена успешно. Для применения необходим перезапуск", null));
        } catch (Exception e) {
            logger.error("Failed to save configurations", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при сохранении конфигурации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    private final JournalViewPage journalViewPage = new JournalViewPage();

    public JournalViewPage getJournalViewPage() {
        return journalViewPage;
    }

    public Object showJournalViewPage() {
        currentWorkspacePage = journalViewPage;
        return null;
    }

    /*
        Options
         */
    public BasicWorkspacePage getOptionGroupPage() {
        return optionGroupPage;
    }

    public Object showOptionGroupPage() {
        currentWorkspacePage = optionGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public void setCurrentWorkspacePage(BasicWorkspacePage page) {
        this.currentWorkspacePage = page;
        updateSelectedMainMenu();
    }

    /*
        CurrentPositions
         */
    public CurrentPositionsReportPage getCurrentPositionsReportPage() {
        return currentPositionsReportPage;
    }

    public Object showCurrentPositionsReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            buildCurrentPositionsReport();
            currentWorkspacePage = currentPositionsReportPage;
        } catch (Exception e) {
            logger.error("Failed to set current positions report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра текущих позиций: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildCurrentPositionsReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            currentPositionsReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to build current positions report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

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
                currentPositionData = currentPositionsReportPage.prepareCurrentPositionsData(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);

            }
            List<CurrentPositionsManager.CurrentPositionItem> curPositionList = new ArrayList<CurrentPositionsManager.CurrentPositionItem>();
            CurrentPositionsManager currentPositionsManager = new CurrentPositionsManager(
                    currentPositionData.isWithOperator(), currentPositionData.getOperatorContragent(),
                    currentPositionData.getBudgetContragent(), currentPositionData.getClientContragent(),
                    curPositionList);

            // Рассчитать текущие позиции
            currentPositionsReportPage.countCurrentPositions(currentPositionsManager, currentPositionData);

            // Зафиксировать текущие позиции в бд
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                currentPositionsReportPage.fixCurrentPositions(persistenceSession, curPositionList);
                persistenceTransaction.commit();
                persistenceTransaction = null;

                buildCurrentPositionsReport();

                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Расчет текущих позиций завершен успешно", null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            logger.error("Failed to count current positions", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при расчете текущих позиций: " + e.getMessage(), null));
        } finally {

        }

        return null;
    }

    /*
      Справочник точек продаж
     */
    public BasicWorkspacePage getPosGroupPage() {
        return posGroupPage;
    }

    public Object showPosGroupPage() {
        currentWorkspacePage = posGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public PosListPage getPosListPage() {
        return posListPage;
    }

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
                    "Ошибка при подготовке страницы справочника точек продаж: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public PosCreatePage getPosCreatePage() {
        return posCreatePage;
    }

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
                    "Ошибка при подготовке страницы регистрации точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

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
                    "Ошибка при подготовке справочника точек продаж: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public PosDeletePage getPosDeletePage() {
        return posDeletePage;
    }

    public Long getSelectedIdOfPos() {
        return selectedIdOfPos;
    }

    public void setSelectedIdOfPos(Long selectedIdOfPos) {
        this.selectedIdOfPos = selectedIdOfPos;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при удалении точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public String showPosCSVList() {
        return "showPosCSVList";
    }

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
                    "Ошибка при подготовке страницы редактирования точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public PosEditPage getPosEditPage() {
        return posEditPage;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы точки продажи: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    /*
      Платежи между контрагентами
     */
    public BasicWorkspacePage getSettlementGroupPage() {
        return settlementGroupPage;
    }

    public Object showSettlementGroupPage() {
        currentWorkspacePage = settlementGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public SettlementListPage getSettlementListPage() {
        return settlementListPage;
    }

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
                    "Ошибка при подготовке страницы платежей между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public SettlementCreatePage getSettlementCreatePage() {
        return settlementCreatePage;
    }

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
                    "Ошибка при подготовке страницы регистрации платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

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
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Платеж между контрагентами зарегистрирован успешно",
                            null));
        } catch (SettlementCreatePage.WrongContragentsException e) {
            logger.error("Failed to create settlement", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Платеж между указанными контрагентами не может быть осуществлен: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to create settlement", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

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
                    "Ошибка при подготовке справочника платежей между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public SettlementDeletePage getSettlementDeletePage() {
        return settlementDeletePage;
    }

    public Long getSelectedIdOfSettlement() {
        return selectedIdOfSettlement;
    }

    public void setSelectedIdOfSettlement(Long selectedIdOfSettlement) {
        this.selectedIdOfSettlement = selectedIdOfSettlement;
    }

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
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении платежа: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public String showSettlementCSVList() {
        return "showSettlementCSVList";
    }

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
                    "Ошибка при подготовке страницы редактирования платежа: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public SettlementEditPage getSettlementEditPage() {
        return settlementEditPage;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных платежа: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    /*
        Начисление платы за обслуживание
     */
    public BasicWorkspacePage getAddPaymentGroupPage() {
        return addPaymentGroupPage;
    }

    public Object showAddPaymentGroupPage() {
        currentWorkspacePage = addPaymentGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public AddPaymentListPage getAddPaymentListPage() {
        return addPaymentListPage;
    }

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
                    "Ошибка при подготовке страницы платежей между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public AddPaymentCreatePage getAddPaymentCreatePage() {
        return addPaymentCreatePage;
    }

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
                    "Ошибка при подготовке страницы регистрации платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

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
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Платеж между контрагентами зарегистрирован успешно",
                            null));
        } catch (AddPaymentCreatePage.WrongContragentsException e) {
            logger.error("Failed to create addPayment", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Платеж между указанными контрагентами не может быть осуществлен: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to create addPayment", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при регистрации платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

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
                    "Ошибка при подготовке справочника платежей между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public AddPaymentDeletePage getAddPaymentDeletePage() {
        return addPaymentDeletePage;
    }

    public Long getSelectedIdOfAddPayment() {
        return selectedIdOfAddPayment;
    }

    public void setSelectedIdOfAddPayment(Long selectedIdOfAddPayment) {
        this.selectedIdOfAddPayment = selectedIdOfAddPayment;
    }

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
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении платежа: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public String showAddPaymentCSVList() {
        return "showAddPaymentCSVList";
    }

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
                    "Ошибка при подготовке страницы редактирования платежа: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public AddPaymentEditPage getAddPaymentEditPage() {
        return addPaymentEditPage;
    }

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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении данных платежа: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке общей страницы платежа между контрагентами: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    /*
      Категории
     */
    public BasicWorkspacePage getCategoryGroupPage() {
        return categoryGroupPage;
    }

    public Object showCategoryGroupPage() {
        currentWorkspacePage = categoryGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getCategoryDSZNGroupPage() {
        return categoryDSZNGroupPage;
    }

    public Object showCategoryDSZNGroupPage() {
        currentWorkspacePage = categoryDSZNGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public ConfirmDeletePage getConfirmDeletePage() {
        return confirmDeletePage;
    }

    public RuleListSelectPage getRuleListSelectPage() {
        return ruleListSelectPage;
    }

    public Object showRuleListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof RuleListSelectPage.CompleteHandlerList) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                ruleListSelectPage.fill(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                ruleListSelectPage.pushCompleteHandlerList((RuleListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(ruleListSelectPage);
            } catch (Exception e) {
                logger.error("Failed to complete  discountrule selection", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при обработке выбора правила: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object updateRuleListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ruleListSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to complete  discountrule selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора правила: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object completeRuleListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            ruleListSelectPage.completeRuleListSelection(true);
            if (!modalPages.empty()) {
                if (modalPages.peek() == ruleListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  discountrule selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора правила: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeRuleListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            ruleListSelectPage.completeRuleListSelection(false);
            if (!modalPages.empty()) {
                if (modalPages.peek() == ruleListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  discountrule selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора правила: " + e.getMessage(), null));
        }
        return null;
    }

    public CategoryOrgListSelectPage getCategoryOrgListSelectPage() {
        return categoryOrgListSelectPage;
    }

    public String getCategoryOrgFilterOfSelectCategoryOrgListSelectPage() {
        return categoryOrgFilterOfSelectCategoryOrgListSelectPage;
    }

    public void setCategoryOrgFilterOfSelectCategoryOrgListSelectPage(
            String categoryOrgFilterOfSelectCategoryOrgListSelectPage) {
        this.categoryOrgFilterOfSelectCategoryOrgListSelectPage = categoryOrgFilterOfSelectCategoryOrgListSelectPage;
    }

    public Object showCategoryOrgListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof CategoryOrgListSelectPage.CompleteHandlerList) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                if (StringUtils.isEmpty(categoryOrgFilterOfSelectCategoryOrgListSelectPage)) {
                    categoryOrgListSelectPage.fill(persistenceSession);
                } else {
                    categoryOrgListSelectPage
                            .fill(persistenceSession, categoryOrgFilterOfSelectCategoryOrgListSelectPage);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                categoryOrgListSelectPage
                        .pushCompleteHandlerList((CategoryOrgListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(categoryOrgListSelectPage);
            } catch (Exception e) {
                logger.error("Failed to complete  categorydiscount org selection", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при обработке выбора категории организации: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);

            }
        }
        return null;
    }

    public Object updateCategoryOrgListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            categoryOrgListSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to complete categorydiscount org selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории организации: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public Object completeCategoryOrgListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            categoryOrgListSelectPage.completeCategoryOrgListSelection(true);
            if (!modalPages.empty()) {
                if (modalPages.peek() == categoryOrgListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  categorydiscount org selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории организации: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeCategoryOrgListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            categoryOrgListSelectPage.completeCategoryOrgListSelection(false);
            if (!modalPages.empty()) {
                if (modalPages.peek() == categoryOrgListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  categorydiscount org selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории организации: " + e.getMessage(), null));
        }
        return null;
    }

    public CategoryListSelectPage getCategoryListSelectPage() {
        return categoryListSelectPage;
    }

    public String getCategoryFilterOfSelectCategoryListSelectPage() {
        return categoryFilterOfSelectCategoryListSelectPage;
    }

    public void setCategoryFilterOfSelectCategoryListSelectPage(String categoryFilterOfSelectCategoryListSelectPage) {
        this.categoryFilterOfSelectCategoryListSelectPage = categoryFilterOfSelectCategoryListSelectPage;
    }

    public Object showCategoryListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof CategoryListSelectPage.CompleteHandlerList) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                boolean flag = true;
                if (params.get("fullList") != null && params.get("fullList").equalsIgnoreCase("false")) {
                    flag = false;
                }
                if (StringUtils.isEmpty(categoryFilterOfSelectCategoryListSelectPage)) {
                    categoryListSelectPage.fill(persistenceSession, flag);
                } else {
                    categoryListSelectPage.fill(persistenceSession, flag, categoryFilterOfSelectCategoryListSelectPage);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
                categoryListSelectPage
                        .pushCompleteHandlerList((CategoryListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(categoryListSelectPage);
            } catch (Exception e) {
                logger.error("Failed to complete  categorydiscount selection", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при обработке выбора категории: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

    public Object updateCategoryListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            boolean flag = true;
            if (params.get("fullList") != null && params.get("fullList").equalsIgnoreCase("false")) {
                flag = false;
            }
            categoryListSelectPage.fill(persistenceSession, flag);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to complete  categorydiscount selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public Object completeCategoryListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            categoryListSelectPage.completeCategoryListSelection(true);
            if (!modalPages.empty()) {
                if (modalPages.peek() == categoryListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  categorydiscount selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeCategoryListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            categoryListSelectPage.completeCategoryListSelection(false);
            if (!modalPages.empty()) {
                if (modalPages.peek() == categoryListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete  categorydiscount selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории: " + e.getMessage(), null));
        }
        return null;
    }

    public CategorySelectPage getCategorySelectPage() {
        return categorySelectPage;
    }

    public Object showCategorySelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof CategorySelectPage.CompleteHandler
                || currentTopMostPage instanceof CategorySelectPage) {
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
                logger.error("Failed to fill categorydiscount selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора категории: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        return null;
    }

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
            logger.error("Failed to complete categorydiscount selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора категории: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    /*
        Правила
     */
    public BasicWorkspacePage getRuleGroupPage() {
        return ruleGroupPage;
    }

    public BasicWorkspacePage getCodeMSPGroupPage(){
        return codeMSPGroupPage;
    }

    public Object showRuleGroupPage() {
        currentWorkspacePage = ruleGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showReportGroupPage() {
        currentWorkspacePage = reportGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public String showCurrentPositionCSVList() {
        return "showCurrentPositionCSVList";
    }

    public Object showProductGuideGroupPage() {
        currentWorkspacePage = productGuideGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showGoodGroupPage() {
        currentWorkspacePage = goodGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public User getCurrentUser() throws Exception {
        if (currentUser == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            String userName = context.getExternalContext().getRemoteUser();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            RuntimeContext runtimeContext = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                ///
                currentUser = DAOUtils.findUser(persistenceSession, userName);
                /// perform lazy load of function
                currentUser.hasFunction(Function.FUNCD_ORG_VIEW);
                //currentUser.getContragents().size();
                //currentUser.getUserOrgses().size();
                ///
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);


            }
        }
        /////
        return currentUser;
    }

    public static MainPage getSessionInstance() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (MainPage) context.getApplication().createValueBinding("#{mainPage}").getValue(context);
    }

    public String getUserRole() throws Exception {
        return getCurrentUser().getRoleName();
    }

    public boolean isMskRegistry() {
        return RuntimeContext.RegistryType.isMsk();
    }

    public boolean isSpbRegistry() {
        return RuntimeContext.RegistryType.isSpb();
    }

    public boolean isEligibleToViewOrgs() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_ORG_VIEW);
    }

    public boolean isEligibleToViewUsers() throws Exception {
        //return getCurrentUser().hasFunction(Function.FUNC_USER_VIEW);
        return getCurrentUser().isSecurityAdmin();
    }

    public void setEligibleToViewUsers(boolean value) throws Exception {
        eligibleToViewUsers = getCurrentUser().isSecurityAdmin();
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

    public boolean isSMSServiceEMP() {
        return RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl;
    }

    public boolean isInfospamEnabled() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.info.enabled", "0").equals("1");
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

    public boolean isEligibleToCommodityAccounting() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_COMMODITY_ACCOUNTING);
    }

    public boolean isEligibleToEditOrgs() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_ORG_EDIT);
    }

    public boolean isAdmin() throws Exception {
        return getCurrentUser().isAdmin();
    }

    public boolean isSupplier() throws Exception {
        return getCurrentUser().isSupplier() || getCurrentUser().hasFunction(Function.FUNC_SUPPLIER);
    }

    public boolean isCardOperator() throws Exception {
        return getCurrentUser().isCardOperator();
    }

    public boolean isEligibleToEditUsers() throws Exception {
        return getCurrentUser().isSecurityAdmin();
        //return getCurrentUser().hasFunction(Function.FUNC_USER_EDIT);
    }

    public boolean isEligibleToDeleteUsers() throws Exception {
        return getCurrentUser().isSecurityAdmin();
        //return getCurrentUser().hasFunction(Function.FUNC_USER_DELETE);
    }

    public boolean isEligibleToEditOptions() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_OPTION);
    }

    public boolean isEligibleToWorkOnlineReport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_ONLINE_REPORT);
    }

    public boolean isEligibleToWorkOnlineReportDocs() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_ONLINE_REPORT_DOCS);
    }

    public boolean isEligibleToWorkOnlineReportEEReport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_ONLINE_REPORT_EE_REPORT);
    }

    public boolean isEligibleToWorkOnlineReportMenuReport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_WORK_ONLINE_REPORT_MENU_REPORT);
    }

    public boolean isEligibleToMonitor() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_MONITORING);
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

    public boolean isEligibleToProcessPayment() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_PAY_PROCESS);
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

    public boolean isEligibleToViewComplexReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_COMPLEX);
    }

    public boolean isEligibleToViewBenefitReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_BENEFIT);
    }

    public boolean isEligibleToViewRequestReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_REQUEST);
    }

    public boolean isEligibleToViewMessageARM() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTICT_MESSAGE_IN_ARM_OO);
    }

    public boolean isEligibleToViewMealsReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_MEALS);
    }

    public boolean isEligibleToViewRefillReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_REFILL);
    }

    public boolean isEligibleToViewActivityReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY);
    }

    public boolean isEligibleToViewClientsReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ONLINE_REPORT_CLIENTS);
    }

    public boolean isEligibleToShowReportsRepository() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_SHOW_REPORTS_REPOSITORY);
    }

    public boolean isEligibleToEditVisitorDogm() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_VISITORDOGM_EDIT);
    }

    public boolean isEligibleToViewElectronicReconciliationReport() throws  Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT);
    }

    public boolean isEligibleToViewPaidFoodReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_PAID_FOOD_REPORT);
    }

    public boolean isEligibleToViewSubscriptionFeeding() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_SUBSCRIPTION_FEEDING);
    }

    public boolean isEligibleToViewClientReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_CLIENT_REPORTS);
    }

    public boolean isEligibleToViewStatisticDifferences() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_STATISTIC_DIFFERENCES);
    }

    public boolean isEligibleToViewFinancialControl() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_FINANCIAL_CONTROL);
    }

    public boolean isEligibleToViewInformReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_INFORM_REPORTS);
    }

    public boolean isEligibleToViewSalesReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_SALES_REPORTS);
    }

    public boolean isEligibleToViewEnterEventReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_ENTER_EVENT_REPORT);
    }

    public boolean isEligibleToViewCardReports() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_CARD_REPORTS);
    }

    public boolean isEligibleToViewHelpdesk() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_HELPDESK);
    }

    public boolean isEligibleToViewTotalServicesReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_TOTAL_SERVICES_REPORT);
    }

    public boolean isEligibleToViewClientsBenefitsReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_CLIENTS_BENEFITS_REPORT);
    }

    public boolean isEligibleToViewTransactionsReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_TRANSACTIONS_REPORT);
    }

    public boolean isEligibleToViewManualReport() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_MANUAL_REPORT);
    }

    public boolean isEligibleToViewCardSign() throws Exception {
        return !getCurrentUser().hasFunction(Function.FUNC_RESTRICT_CARD_SIGNS);
    }


    public boolean isEligibleToViewCardOperator() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_RESTRICT_CARD_OPERATOR);
    }

    /*public boolean isEligibleToViewFeedingSettings() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_FEEDING_SETTINGS_VIEW);
    }

    public boolean isEligibleToEditFeedingSettings() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_FEEDING_SETTINGS_EDIT);
    }*/

    public boolean isEligibleToViewOrEditFeedingSettings() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_FEEDING_SETTINGS_SUPPLIER) ||
                getCurrentUser().hasFunction(Function.FUNC_FEEDING_SETTINGS_ADMIN);
    }

    public boolean isEligibleToViewCoverageNutritionReport() throws Exception {
        return getCurrentUser().hasFunction(Function.FUNC_COVERAGENUTRITION);
    }

    public Object removeClient() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            if (!isEligibleToRemoveClients()) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Нет прав на удаление клиента", null));
                return null;
            }
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientEditPage.removeClient(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            setSelectedIdOfClient(null);
            selectedClientGroupPage.getMainMenuComponent().setRendered(false);
            showClientListPage();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиент удален", null));
        } catch (Exception e) {
            logger.error("Failed to remove client", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении клиента: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public EmpInfoPage getEmpInfoPage() {
        return empInfoPage;
    }

    public ServiceRNIPPage getServiceRNIPPage() {
        return serviceRNIPPage;
    }

    public ServiceCheckSumsPage getServiceCheckSumsPage() {
        return serviceCheckSumsPage;
    }

    /*
           Все комплексы
        */
    public AllComplexReportPage getAllComplexReportPage() {
        return allComplexReportPage;
    }

    public Object showAllComplexReportPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = allComplexReportPage;
        } catch (Exception e) {
            logger.error("Failed to set all complex report page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по всем комплексам: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object buildAllComplexReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            allComplexReportPage.buildReport(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build all complex report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public String showAllComplexCSVList() {
        return "showAllComplexCSVList";
    }

    public Object removeClientFromList() {
        clientListPage.removeClientFromList(selectedIdOfClient);
        return null;
    }

    public Object setLimit() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.setLimit(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set new limit", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при смене лимита: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public Object setOrg() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.setOrg(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set new org", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при смене организации: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        updateClientListPage();
        return null;
    }

    public Object setExpenditureLimit() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.setExpenditureLimit(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set new expenditure limit", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при смене лимита дневных трат: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object setClientGroupNofifyViaSMS() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.setNotifyViaSMS(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set new expenditure limit", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении параметров уведомления: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object setClientGroupNofifyViaPUSH() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clientListPage.setNotifyViaPUSH(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set new expenditure limit", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при изменении параметров уведомления: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public String getRemovedReportTemplate() {
        return removedReportTemplate;
    }

    public void setRemovedReportTemplate(String removedReportTemplate) {
        this.removedReportTemplate = removedReportTemplate;
    }

    public Object removeReportTemplate() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            reportTemplateManagerPage.removeTemplate(removedReportTemplate);
        } catch (Exception e) {
            logger.error("Error on deleting report template file.", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении шаблона: " + e.getMessage(),
                            null));
        }
        return null;
    }

    public Object showConfigurationProviderGroupPage() {
        currentWorkspacePage = configurationProviderGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showTechnologicalMapGroupPage() {
        currentWorkspacePage = technologicalMapGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showTechnologicalMapGroupsGroupPage() {
        currentWorkspacePage = technologicalMapGroupsGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public void registerModalPageShow(BasicPage modalPage) {
        modalPages.push(modalPage);
    }

    public void registerModalPageHide(BasicPage modalPage) {
        if (modalPages.peek().equals(modalPage)) {
            modalPages.pop();
        }
    }

    public UvDeletePage getOpenedDeletePage() {
        return (UvDeletePage) modalPages.peek();
    }

    public BasicWorkspacePage getInfoGroupMenu() {
        return infoGroupMenu;
    }

    public BasicWorkspacePage getDebugGroupMenu() {
        return debugGroupMenu;
    }

    public BasicWorkspacePage getCardGroupMenu() {
        return cardGroupMenu;
    }

    public String getUserContragentsList() {
        try {
            return ContextDAOServices.getInstance().getContragentsListForTooltip(getCurrentUser().getIdOfUser());
        } catch (Exception e) {
            logger.error("getContragentsListForTooltip Error", e);
            return "";
        }
    }

    public String getUserRegionsList() {
        try {
            return getCurrentUser().getRegion();
        } catch (Exception e) {
            logger.error("getContragentsListForTooltip Error", e);
            return "";
        }
    }

    public Object showSyncStatsPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            currentWorkspacePage = syncStatsPage;
        } catch (Exception e) {
            logger.error("Failed to set sync stats page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы с данными по синхронизациям: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public SyncStatsPage getSyncStatsPage() {
        return syncStatsPage;
    }

    public RegularPaymentsReportPage getRegularPaymentsReportPage() {
        return regularPaymentsReportPage;
    }

    public Boolean isTestMode() {
        return RuntimeContext.getInstance().isTestMode();
    }

    public QuartzJobsListPage getQuartzJobsListPage() {
        return quartzJobsListPage;
    }

    public OptionsSecurityPage getOptionsSecurityPage() {
        return optionsSecurityPage;
    }

    public OptionsSecurityClientPage getOptionsSecurityClientPage() {
        return optionsSecurityClientPage;
    }

    public OrgsSecurityPage getOrgsSecurityPage() {
        return orgsSecurityPage;
    }

    public Object checkUserSmsCode() throws Exception {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        String userName = context.getRemoteUser();
        User user = DAOService.getInstance().findUserByUserName(userName);
        String reqCode = user.getLastSmsCode();
        if (reqCode != null && smsCode != null && reqCode.equals(smsCode)) {
            user.setSmsCodeEnterDate(new Date(System.currentTimeMillis()));
            DAOService.getInstance().setUserInfo(user);
            context.redirect(context.getRequestContextPath() + "/back-office/index.faces");
        } else {
            smsCode = "";
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Введен неверный код активации", null));
        }
        return null;
    }

    public Object sendSMSagain() throws Exception {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        String userName = context.getRemoteUser();
        logger.info(String.format("Start of sending SMS code for the user %s", userName));
        Boolean requstSMS = User.requestSmsCode(userName);
        logger.info(String.format("End of sending SMS code for the user %s", userName));
        if (requstSMS){
            setCanSendAgain(true);
            context.redirect(context.getRequestContextPath() + "/back-office/confirm-sms.faces");
        }
        else {
            context.redirect(context.getRequestContextPath() + "/back-office/emp_server_not_answer.faces");
        }
        setCanSendAgain(true);
        context.redirect(context.getRequestContextPath() + "/back-office/confirm-sms.faces");
        return null;
    }

    public Object doChangeUserPassword() throws Exception {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        String userName = context.getRemoteUser();
        User user = DAOService.getInstance().findUserByUserName(userName);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = SecurityContextAssociationValve.getActiveRequest().getRequest();
        try {
            if (!newPassword.equals(newPasswordConfirm)) {
                newPassword = "";
                newPasswordConfirm = "";
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ОШИБКА: введенные значения не совпадают", null));
                throw new CredentialException("Неверный ввод пароля");
            }
            if (!User.passwordIsEnoughComplex(newPassword)) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Пароль не удовлетворяет требованиям безопасности:", null));
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "- минимальная длина - 6 символов", null));
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "- должны присутствовать прописные и заглавные латинские буквы + хотя бы одна цифра или спецсимвол", null));
                throw new CredentialException("Пароль не удовлетворяет требованиям безопасности");
            }

            user.doChangePassword(newPassword);
            user.setNeedChangePassword(false);
            user.setPasswordDate(new Date(System.currentTimeMillis()));
            DAOService.getInstance().setUserInfo(user);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CHANGE_GRANTS, request.getRemoteAddr(),
                            userName, user, true, null, null);
            DAOService.getInstance().writeAuthJournalRecord(record);
            context.redirect(context.getRequestContextPath() + "/back-office/index.faces");
        } catch (CredentialException e) {
            newPassword = "";
            newPasswordConfirm = "";
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CHANGE_GRANTS, request.getRemoteAddr(),
                            userName, user, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), e.getMessage());
            DAOService.getInstance().writeAuthJournalRecord(record);
        }
        return null;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public BasicWorkspacePage getJournalGroupPage() {
        return journalGroupPage;
    }

    public JournalBalancesReportPage getJournalBalancesReportPage() {
        return journalBalancesReportPage;
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public void setIdOfUser(Long idOfUser) {
        this.idOfUser = idOfUser;
    }

    public UserSelectPage getUserSelectPage() {
        return userSelectPage;
    }

    public JournalProcessesReportPage getJournalProcessesReportPage() {
        return journalProcessesReportPage;
    }

    public JournalReportsReportPage getJournalReportsReportPage() {
        return journalReportsReportPage;
    }

    public TaloonApprovalVerificationPage getTaloonApprovalVerificationPage() {
        return taloonApprovalVerificationPage;
    }

    public TaloonPreorderVerificationPage getTaloonPreorderVerificationPage() {
        return taloonPreorderVerificationPage;
    }

    public ElectronicReconciliationStatisticsPage getElectronicReconciliationStatisticsPage() {
        return electronicReconciliationStatisticsPage;
    }

    public InfoMessagePage getInfoMessagePage() {
        return infoMessagePage;
    }

    public Object showInfoMessagePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            infoMessagePage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = infoMessagePage;
        } catch (Exception e) {
            logger.error("Failed to show info message page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отправки информационных сообщений: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showInfoMessageCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            infoMessageCreatePage.fill();
            currentWorkspacePage = infoMessageCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show info message page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания информационного сообщения: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showInfoMessageGroupPage() {
        currentWorkspacePage = infoMessageGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showFeedingSettingViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            feedingSettingsGroupPage.fill(persistenceSession, selectedIdOfFeedingSetting);
            feedingSettingViewPage.setIdOfSetting(selectedIdOfFeedingSetting);
            feedingSettingViewPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            feedingSettingsGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = feedingSettingViewPage;
        } catch (Exception e) {
            logger.error("Failed to show feeding setting page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы настроек платного питания: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showFeedingSettingEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            feedingSettingsGroupPage.fill(persistenceSession, selectedIdOfFeedingSetting);
            feedingSettingEditPage.setIdOfSetting(selectedIdOfFeedingSetting);
            feedingSettingEditPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            feedingSettingsGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = feedingSettingEditPage;
        } catch (Exception e) {
            logger.error("Failed to show feeding setting edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы настроек платного питания: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showFeedingSettingCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            feedingSettingCreatePage.clear();
            currentWorkspacePage = feedingSettingCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show feeding setting create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания настройки платного питания: " + e.getMessage(), null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showFeedingSettingListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            feedingSettingsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = feedingSettingsListPage;
        } catch (Exception e) {
            logger.error("Failed to show feeding setting edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы настроек платного питания: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object deleteFeedingSetting() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            FeedingSetting setting = (FeedingSetting)persistenceSession.load(FeedingSetting.class, selectedIdOfFeedingSetting);
            persistenceSession.delete(setting);
            feedingSettingsListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            feedingSettingsGroupPage.hideMenuGroup();
            currentWorkspacePage = feedingSettingsListPage;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при попытке удаления настройки платного питания: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getInfoMessageGroupPage() {
        return infoMessageGroupPage;
    }

    public InfoMessageCreatePage getInfoMessageCreatePage() {
        return infoMessageCreatePage;
    }

    /*public BasicBasketReportPage getBasicBasketReportPage() {
        return basicBasketReportPage;
    }*/

    public boolean getIsSpb() {
        return RuntimeContext.RegistryType.isSpb();
    }

    public ClientCreateByCardOperatorPage getClientRegistrationByCardOperatorPage() {
        return clientRegistrationByCardOperatorPage;
    }

    public Long getContractIdCardOperator() {
        return contractIdCardOperator;
    }

    public void setContractIdCardOperator(Long contractIdCardOperator) {
        this.contractIdCardOperator = contractIdCardOperator;
    }

    public BasicWorkspacePage getFeedingSettingsGroupPage() {
        return feedingSettingsGroupPage;
    }

    public Long getSelectedIdOfFeedingSetting() {
        return selectedIdOfFeedingSetting;
    }

    public void setSelectedIdOfFeedingSetting(Long selectedIdOfFeedingSetting) {
        this.selectedIdOfFeedingSetting = selectedIdOfFeedingSetting;
    }

    public FeedingSettingsListPage getFeedingSettingsListPage() {
        return feedingSettingsListPage;
    }

    public FeedingSettingViewPage getFeedingSettingViewPage() {
        return feedingSettingViewPage;
    }

    public FeedingSettingEditPage getFeedingSettingEditPage() {
        return feedingSettingEditPage;
    }

    public FeedingSettingCreatePage getFeedingSettingCreatePage() {
        return feedingSettingCreatePage;
    }

    public OrgMainBuildingListSelectPage getOrgMainBuildingListSelectPage() {
        return orgMainBuildingListSelectPage;
    }

    public Object showOrgMainBuildingListSelectPage() {
        BasicPage currentTopMostPage = getTopMostPage();
        if (currentTopMostPage instanceof OrgMainBuildingListSelectPage.CompleteHandler
                || currentTopMostPage instanceof OrgMainBuildingListSelectPage) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            RuntimeContext runtimeContext;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Long organizationId = null;

                if (currentWorkspacePage instanceof UserCreatePage)
                    organizationId = userCreatePage.getOrganizationId();

                if (currentWorkspacePage instanceof UserEditPage)
                    organizationId = userEditPage.getOrganizationId();


                orgMainBuildingListSelectPage.fill(persistenceSession, organizationId);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                if (currentTopMostPage instanceof OrgMainBuildingListSelectPage.CompleteHandler) {
                    orgMainBuildingListSelectPage
                            .pushCompleteHandler((OrgMainBuildingListSelectPage.CompleteHandler) currentTopMostPage);
                    modalPages.push(orgMainBuildingListSelectPage);
                }
            } catch (Exception e) {
                logger.error("Failed to fill contragents list selection page", e);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ошибка при подготовке страницы выбора списка организаций: " + e.getMessage(), null));
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        return null;
    }

    public Object clearOrgMainBuildingListSelectedItemsList() {
        orgMainBuildingListSelectPage.deselectAllItems();

        if (currentWorkspacePage instanceof UserCreatePage)
            userCreatePage.setOrganizationId(null);

        if (currentWorkspacePage instanceof UserEditPage)
            userEditPage.setOrganizationId(null);

        showOrgMainBuildingListSelectPage();
        return null;
    }

    public Object completeOrgMainBuildingListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long organizationId = null;

            if (currentWorkspacePage instanceof UserCreatePage)
                organizationId = userCreatePage.getOrganizationId();

            if (currentWorkspacePage instanceof UserEditPage)
                organizationId = userEditPage.getOrganizationId();

            orgMainBuildingListSelectPage.completeOrgMainBuildingSelection(persistenceSession, organizationId);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgMainBuildingListSelectPage) {
                    modalPages.pop();
                }
            }
            orgMainBuildingListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("Failed to complete org mainbuilding list selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка организаций: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object cancelOrgMainBuildingListSelection() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            orgMainBuildingListSelectPage.cancelOrgMainBuildingListSelection();
            if (!modalPages.empty()) {
                if (modalPages.peek() == orgMainBuildingListSelectPage) {
                    modalPages.pop();
                }
            }
            orgMainBuildingListSelectPage.setFilter("");
        } catch (Exception e) {
            logger.error("{}", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора списка организаций: " + e.getMessage(), null));
        }
        return null;
    }

    public BasicWorkspacePage getNsiGroupEmployeePage() {
        return nsiGroupEmployeePage;
    }

    public SyncMonitorPage getSyncMonitorPage() {
        return syncMonitorPage;
    }

    public CardRegistrationConfirm getCardRegistrationConfirm() {
        return cardRegistrationConfirm;
    }

    public Object verificationCardData(){
        Long timeStamp = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_VALID_REGISTRY_DATE);
        Date validRegistryDate = CalendarUtils.endOfDay(new Date(timeStamp));
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        boolean doCreateCard = false;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if(cardRegistrationAndIssuePage.isClientHasNotBlockedCard()){
                throw new Exception("Данный клиент имеет незаблокированную(ые) карту(ы).");
            }
            String cardType = Card.TYPE_NAMES[cardRegistrationAndIssuePage.getCardType()];
            if(cardType.equals("Mifare") && !CardManagerProcessor.getPriceOfMifare().equals(0L) ||
                    cardType.equals("Браслет (Mifare)" ) && !CardManagerProcessor.getPriceOfMifareBracelet().equals(0L)){
                Client client = (Client) persistenceSession.load(Client.class, cardRegistrationAndIssuePage.getClient().getIdOfClient());
                Card lastCard = DAOUtils.getLastCardByClient(persistenceSession, client);
                if(lastCard != null) {
                    String cardLockReason = lastCard.getLockReason();
                    Date createDate = lastCard.getCreateTime();
                    String typeOfLastCard = Card.TYPE_NAMES[lastCard.getCardType()];
                    if (cardLockReason != null) {
                        if ((cardLockReason.equals(CardLockReason.REISSUE_BROKEN.getDescription()) || cardLockReason.equals(CardLockReason.REISSUE_LOSS.getDescription()))
                                || createDate.before(validRegistryDate) || (typeOfLastCard.equals("Mifare") && cardType.equals("Браслет (Mifare)"))) {
                            modalPages.push(cardRegistrationConfirm);
                            cardRegistrationConfirm.prepareADialogue(cardType);
                        } else doCreateCard = true;
                    } else if(createDate.before(validRegistryDate) || (typeOfLastCard.equals("Mifare") && cardType.equals("Браслет (Mifare)"))){
                        modalPages.push(cardRegistrationConfirm);
                        cardRegistrationConfirm.prepareADialogue(cardType);
                    } else {
                        doCreateCard = true;
                    }
                } else if(cardType.equals("Браслет (Mifare)" ) && !CardManagerProcessor.getPriceOfMifareBracelet().equals(0L)){
                    modalPages.push(cardRegistrationConfirm);
                    cardRegistrationConfirm.prepareADialogue(cardType);
                } else doCreateCard = true;
            } else doCreateCard = true;
            if(doCreateCard) cardRegistrationAndIssuePage.createCard();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to verifi card", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при проверке данных карты: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object confirmReissueCard() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try{
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            cardRegistrationAndIssuePage.reissueCard(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Карта зарегистрирована успешно", null));
            if (!modalPages.empty()) {
                if (modalPages.peek() == cardRegistrationConfirm) {
                    modalPages.pop();
                }
            }
        } catch(Exception e){
            logger.error("Failed to re-issue card", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при заказе карты: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object cancelReissueCard() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if (!modalPages.empty()) {
                if (modalPages.peek() == cardRegistrationConfirm) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete user selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора пользователя: " + e.getMessage(), null));
        }
        return null;
    }

    public CreatedAndReissuedCardReportFromCardOperatorPage getCreatedAndReissuedCardReportFromCardOperatorPage() {
        return createdAndReissuedCardReportFromCardOperatorPage;
    }

    public Object showCreatedAndReissuedCardReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        User user = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            user = getSessionInstance().getCurrentUser();
            persistenceTransaction = persistenceSession.beginTransaction();
            createdAndReissuedCardReportFromCardOperatorPage.fill(persistenceSession, user);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            currentWorkspacePage = createdAndReissuedCardReportFromCardOperatorPage;
        } catch (Exception e) {
            logger.error("Failed to fill card list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы отчета по картам: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        updateSelectedMainMenu();
        return null;
    }

    public IssuedCardsReportPage getIssuedCardsReportPage() {
        return issuedCardsReportPage;
    }

    public UserListSelectPage getUserListSelectPage() {
        return userListSelectPage;
    }

    public Object clearUserListSelectedItemsList() {
        userListSelectPage.deselectAllItems();
        userFilterOfSelectUserListSelectPage = "";
        updateUserListSelectPage();
        return null;
    }

    public Object selectAllUserListSelectedItemsList() {
        userListSelectPage.selectAllItems();
        updateUserListSelectPage();
        return null;
    }

    public Object updateUserListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            userListSelectPage.fill(persistenceSession, userListSelectPage.getSelectedUsers().keySet(),
                    userListSelectPage.getRoleFilter(), true, this, userFilterOfSelectUserListSelectPage);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill user selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object completeUserListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            userListSelectPage.completeUserListSelection(true);
            if (!modalPages.empty()) {
                if (modalPages.peek() == userListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete users selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора пользователей: " + e.getMessage(), null));
        }
        return null;
    }

    public Object completeUserListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            userListSelectPage.completeUserListSelection(false);
            if (!modalPages.empty()) {
                if (modalPages.peek() == userListSelectPage) {
                    modalPages.pop();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to complete users selection", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при обработке выбора пользователей: " + e.getMessage(), null));
        }
        return null;
    }


    public void showUserListSelectPage() {
        showUserSelectPage(null);
    }

    public void showUserListSelectPage(User.DefaultRole role) {
        BasicPage currentTopMostPage = getTopMostPage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            userListSelectPage.fill(persistenceSession, null, role, false, this, userFilterOfSelectUserListSelectPage);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (currentTopMostPage instanceof UserListSelectPage.CompleteHandlerList) {
                userListSelectPage.pushCompleteHandlerList((UserListSelectPage.CompleteHandlerList) currentTopMostPage);
                modalPages.push(userListSelectPage);
            }

        } catch (Exception e) {
            logger.error("Failed to fill users selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public String getUserFilterOfSelectUserListSelectPage() {
        return userFilterOfSelectUserListSelectPage;
    }

    public void setUserFilterOfSelectUserListSelectPage(String userFilterOfSelectUserListSelectPage) {
        this.userFilterOfSelectUserListSelectPage = userFilterOfSelectUserListSelectPage;
    }

    public SelectedUserGroupPage getSelectedUserGroupGroupPage() {
        return selectedUserGroupGroupPage;
    }

    public UserGroupViewPage getUserGroupViewPage() {
        return userGroupViewPage;
    }

    public UserGroupEditPage getUserGroupEditPage() {
        return userGroupEditPage;
    }

    public Long getSelectedIdOfUserGroup() {
        return selectedIdOfUserGroup;
    }

    public void setSelectedIdOfUserGroup(Long selectedIdOfUserGroup) {
        this.selectedIdOfUserGroup = selectedIdOfUserGroup;
    }

    public Long getRemovedIdOfUserGroup() {
        return removedIdOfUserGroup;
    }

    public void setRemovedIdOfUserGroup(Long removedIdOfUserGroup) {
        this.removedIdOfUserGroup = removedIdOfUserGroup;
    }

    public Boolean getCanSendAgain() {
        return canSendAgain;
    }

    public void setCanSendAgain(Boolean canSendAgain) {
        this.canSendAgain = canSendAgain;
    }

    public Object showClientOperationListPageWithOrgView() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            selectedClientGroupPage.fill(persistenceSession, selectedIdOfClient);
            clientOperationListPage.fill(persistenceSession, selectedIdOfClient, false);
            clientViewPage.fill(persistenceSession, selectedIdOfClient);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            selectedClientGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = clientOperationListPage;
        } catch (Exception e) {
            logger.error("Failed to show client operation list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра списка операций по клиенту: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        updateSelectedMainMenu();
        return null;
    }

    public BasicWorkspacePage getPreorderPage() {
        return preorderPage;
    }

    public BlockUnblockReportPage getBlockUnblockReportPage() {
        return blockUnblockReportPage;
    }

    public ComplexListSelectPage getComplexWebListSelectPage() {
        return complexWebListSelectPage;
    }

    public DishListSelectPage getDishWebListSelectPage() {
        return dishWebListSelectPage;
    }
}