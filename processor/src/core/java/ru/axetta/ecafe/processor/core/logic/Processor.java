/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import com.google.common.base.Joiner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.file.FileUtils;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodBasicBasketPrice;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.service.CardBlockService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.geoplaner.SmartWatchVendorNotificationManager;
import ru.axetta.ecafe.processor.core.service.meal.MealManager;
import ru.axetta.ecafe.processor.core.service.scud.ScudManager;
import ru.axetta.ecafe.processor.core.sync.*;
import ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.*;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.ResTurnstileSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.TurnstileSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.TurnstileSettingsRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.balance.hold.*;
import ru.axetta.ecafe.processor.core.sync.handlers.card.request.CardRequests;
import ru.axetta.ecafe.processor.core.sync.handlers.card.request.CardRequestsData;
import ru.axetta.ecafe.processor.core.sync.handlers.card.request.CardRequestsProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.CategoriesDiscountsAndRulesRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts.ResCategoriesDiscountsAndRules;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardOperationData;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientGroupManagerRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientgroupManagerData;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ClientgroupManagersProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers.ResClientgroupManagers;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ClientPhotosData;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ClientPhotosProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ClientsPhotos;
import ru.axetta.ecafe.processor.core.sync.handlers.clientphoto.ResClientPhotos;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoleProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoles;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule.ComplexScheduleData;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule.ComplexScheduleProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule.ListComplexSchedules;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule.ResComplexSchedules;
import ru.axetta.ecafe.processor.core.sync.handlers.dtiszn.ClientDiscountDTSZN;
import ru.axetta.ecafe.processor.core.sync.handlers.dtiszn.ClientDiscountDTSZNProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.dtiszn.ClientDiscountsDTSZNRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.emias.*;
import ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request.GoodRequestEZDProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request.GoodRequestEZDRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request.GoodRequestEZDSection;
import ru.axetta.ecafe.processor.core.sync.handlers.groups.*;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.ResHardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.HelpRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.HelpRequestData;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.HelpRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.help.request.ResHelpRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReport;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportData;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportDataProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier.MenuSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier.MenuSupplierProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier.ResMenuSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar.*;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.Migrants;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsData;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.ResMigrants;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSection;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.*;
import ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions.PlanOrdersRestrictions;
import ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions.PlanOrdersRestrictionsProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions.PlanOrdersRestrictionsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrderFeedingProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrdersFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrdersFeedingRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.PreorderFeedingStatusData;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.PreorderFeedingStatusProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.PreorderFeedingStatusRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status.ResPreorderFeedingStatus;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalData;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ResReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ReestrTaloonPreorder;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ReestrTaloonPreorderData;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ReestrTaloonPreorderProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder.ResReestrTaloonPreorder;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts.AccountsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.cards.CardsOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.ResAccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingData;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.RequestFeedingProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.request.feeding.ResRequestFeeding;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplierData;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.RequestsSupplierProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier.ResRequestsSupplier;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.ResSpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesData;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ResSyncSettingsSection;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.SyncSettingProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.SyncSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.SyncSettingsSection;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardOperationProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ResZeroTransactions;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactionData;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactions;
import ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions.ZeroTransactionsProcessor;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.process.ClientGuardianDataProcessor;
import ru.axetta.ecafe.processor.core.sync.request.*;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequest;
import ru.axetta.ecafe.processor.core.sync.response.*;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;
import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:51:14
 * To change this template use File | Settings | File Templates.
 */
public class Processor implements SyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private static final int RESPONSE_MENU_PERIOD_IN_DAYS = 7;
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;
    private static final long ACC_REGISTRY_TIME_CLIENT_IN_MILLIS =
            RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.accRegistryUpdate.timeClient", 7) * 24 * 60
                    * 60 * 1000;

    private ProcessorUtils processorUtils = RuntimeContext.getAppContext().getBean(ProcessorUtils.class);

    public Processor(SessionFactory persistenceSessionFactory,
            EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    @Override
    public SyncResponse processSyncRequest(SyncRequest request) throws Exception {
        Date syncStartTime = new Date();
        int syncResult = 0;
        SyncResponse response = null;

        try {
            switch (request.getSyncType()) {
                case TYPE_FULL: {
                    // обработка полной синхронизации
                    response = buildFullSyncResponse(request, syncStartTime, syncResult);
                    break;
                }
                case TYPE_GET_ACC_INC: {
                    // обработка синхронизации покупок и прохода клиентов (быстрая синхронизация)
                    response = buildAccIncSyncResponse(request);
                    break;
                }
                case TYPE_GET_CLIENTS_PARAMS: {
                    // обработка синхронизации параметров клиента
                    response = buildClientsParamsSyncResponse(request);
                    break;
                }
                case TYPE_GET_GET_ACC_REGISGTRY_UPDATE: {
                    // обработка синхронизации параметров клиента
                    response = buildAccRegisgtryUpdate(request);
                    break;
                }
                case TYPE_COMMODITY_ACCOUNTING: {
                    // обработка синхронизации параметров клиента
                    response = buildCommodityAccountingSyncResponse(request);
                    break;
                }
                case TYPE_REESTR_TALOONS_APPROVAL: {
                    //обработка синхронизации ручного реестра талонов
                    response = buildReestrTaloonsApprovalSyncResponse(request);
                    break;
                }
                case TYPE_ZERO_TRANSACTIONS: {
                    //обработка нулевых транзакций
                    response = buildZeroTransactionsSyncResponse(request);
                    break;
                }
                case TYPE_MIGRANTS: {
                    //обработка временных посетителей (мигрантов)
                    response = buildMigrantsSyncResponse(request);
                    break;
                }
                case TYPE_HELP_REQUESTS: {
                    //обработка запросов в службу помощи
                    response = buildHelpRequestsSyncResponse(request);
                    break;
                }
                case TYPE_CONSTRUCTED: {
                    response = buildUniversalConstructedSectionsSyncResponse(request, syncStartTime, syncResult);
                    break;
                }
                case TYPE_ORG_SETTINGS: {
                    //обработка настроек ОО
                    response = buildOrgSettingsSectionsResponse(request, syncStartTime, syncResult);
                    break;
                }
                case TYPE_REESTR_TALOONS_PREORDER: {
                    //обработка синхронизации ручного реестра талонов (платное питание)
                    response = buildReestrTaloonsPreorderSyncResponse(request);
                    break;
                }
                case TYPE_MENU_SUPPLIER: {
                    //обработка синхронизации веб-технолога
                    if (request.getOrg().getUseWebArm()) {
                        response = buildMenuSupplierSyncResponse(request);
                    }
                    break;
                }
                case TYPE_REQUEST_SUPPLIER: {
                    //обработка заявок на питание
                    response = buildRequestsSupplierSyncResponse(request);
                    break;
                }
            }

        } catch (Exception e) {
            logger.error(String.format("Failed to perform synchronization, IdOfOrg == %s", request.getIdOfOrg()), e);
            syncResult = 1;
        }

        // Build and return response
        if (request.getSyncType() == SyncType.TYPE_FULL) {
            eventNotificator.fire(new SyncEvent.RawEvent(syncStartTime, request, response));
        }
        return response;
    }

    public void disableClientCardsIfChangeOrg(Client client, Set<Org> oldOrgs, long newIdOfOrg) throws Exception {
        if (client == null) {
            return;
        }
        Boolean isReplaceOrg = !client.getOrg().getIdOfOrg()
                .equals(newIdOfOrg); //сравниваем старую организацию клиента с новой
        for (Org o : oldOrgs) {
            if (o.getIdOfOrg().equals(newIdOfOrg)) {                             //и с дружественными организациями
                isReplaceOrg = false;
                break;
            }
        }
        //Если новая организация не совпадает ни со старой, ни с дружественными старой, то блокируем карты клиента
        if (isReplaceOrg) {
            Set<Card> cards = client.getCards();
            for (Card card : cards) {
                if (card.getState()
                        .equals(CardState.BLOCKED.getValue())) {     //если карта уже заблокирована, ее пропускаем
                    continue;
                }
                RuntimeContext.getInstance().getCardManager()
                        .updateCard(client.getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                                CardState.BLOCKED.getValue(),
                                //статус = Заблокировано
                                card.getValidTime(), card.getLifeState(), card.getLockReason(), card.getIssueTime(),
                                card.getExternalId());
            }
        }
    }

    public Client getClientInfo(long idOfContract) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Client client = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            client = findClientByContractId(persistenceSession, idOfContract);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return client;
    }

    private void saveLastProcessSectionDateSmart(SessionFactory sessionFactory, Long idOfOrg, SectionType sectionType) {
        if (!(sectionType.equals(SectionType.ACC_INC_REGISTRY) || sectionType.equals(SectionType.LAST_TRANSACTION))) {
            return;
        }
        processorUtils.saveLastProcessSectionCustomDate(sessionFactory, idOfOrg, sectionType);
    }

    private Long addPerformanceInfoAndResetDeltaTime(StringBuilder sb, String function, Long delta) {
        if (System.currentTimeMillis() - delta > 100L) {
            sb.append(function + "=" + (System.currentTimeMillis() - delta) + "\n");
        }
        return System.currentTimeMillis();
    }
    private void deleteOldVersionSpecialDate(SyncRequest request)
    {
        if (SyncRequest.versionIsAfter(request.getClientVersion(), "2.7.93.1")
                && !SyncRequest.versionIsAfter(request.getClientVersion(), "2.7.95.1")){
            CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(request.getIdOfOrg(), new Date(1598918400000L));
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                List<SpecialDate> specialDates =
                        DAOUtils.findSpecialDateWithOutGroup(persistenceSession, compositeId);
                for (SpecialDate specialDate: specialDates)
                {
                    specialDate.setDeleted(true);
                    persistenceSession.save(specialDate);
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    /* Do process full synchronization */
    private SyncResponse buildFullSyncResponse(SyncRequest request, Date syncStartTime, int syncResult)
            throws Exception {

        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        PlanOrdersRestrictions planOrdersRestrictionsData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        InteractiveReport interactiveReport = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        ComplexScheduleData complexScheduleData = null;
        ResComplexSchedules resComplexSchedules = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();
        StringBuilder performanceLogger = new StringBuilder();
        Long timeForDelta = System.currentTimeMillis();

        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        // Register sync history
        syncHistory = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                request.getRemoteAddr(), request.getSyncType().getValue());
        addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(), request.getRemoteAddr(),
                request.getSqlServerVersion(), request.getDatabaseSize());
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "Begin sync", timeForDelta);

        processMigrantsSections(request, syncHistory, responseSections, null);
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processMigrantsSections", timeForDelta);

        processAccountOperationsRegistrySections(request, syncHistory, responseSections, null);
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger,
                "processAccountOperationsRegistrySections", timeForDelta);

        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry().getPayments().hasNext()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    processorUtils
                            .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                                    "no license slots available");
                    throw new Exception("no license slots available");
                }
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.PAYMENT_REGISTRY);
            resPaymentRegistry = processSyncPaymentRegistry(syncHistory.getIdOfSync(), request.getIdOfOrg(),
                    request.getPaymentRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
            bError = true;
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncPaymentRegistry",
                timeForDelta);

        // Process ClientParamRegistry
        List<Long> clientsWithWrongVersion = new ArrayList<Long>();
        try {
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("Полная синхронизация");
            clientsMobileHistory.setOrg(getOrgReference(persistenceSessionFactory.openSession(), request.getIdOfOrg()));
            clientsMobileHistory.setShowing("АРМ ОО (ид." + request.getIdOfOrg() + ")");
            processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), request.getClientParamRegistry(),
                    errorClientIds, clientsWithWrongVersion, clientsMobileHistory);
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncClientParamRegistry",
                timeForDelta);

        // Process ClientGuardianRequest
        try {
            ClientGuardianRequest clientGuardianRequest = request.getClientGuardianRequest();
            if (clientGuardianRequest != null) {
                final List<ClientGuardianItem> clientGuardianResponseElement = clientGuardianRequest
                        .getClientGuardianResponseElement();
                if (clientGuardianResponseElement != null) {
                    resultClientGuardian = processClientGuardian(clientGuardianResponseElement, request.getIdOfOrg(),
                            syncHistory);
                }
                final Long responseClientGuardian = clientGuardianRequest.getMaxVersion();
                if (responseClientGuardian != null) {
                    clientGuardianData = processClientGuardianData(request.getIdOfOrg(), syncHistory,
                            responseClientGuardian);
                }
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientGuardianRequest, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processClientGuardian", timeForDelta);


        // Process OrgStructure
        try {
            if (request.getOrgStructure() != null) {
                resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), request.getOrgStructure(), syncHistory);
            }
            if (resOrgStructure != null && resOrgStructure.getResult() > 0) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        resOrgStructure.getError());
            }
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncOrgStructure", timeForDelta);

        // Build client registry
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.CLIENT_REGISTRY);
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(), request.getClientRegistryRequest(),
                    errorClientIds, clientsWithWrongVersion);
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncClientRegistry",
                timeForDelta);

        try {
            goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process goods basic basket data , IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processGoodsBasicBasketData",
                timeForDelta);

        // Process menu from Org
        if (!request.getOrg().getUseWebArm()) {
            try {
                processSyncMenu(request.getIdOfOrg(), request.getReqMenu());
            } catch (Exception e) {
                String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        message);
                logger.error(message, e);
            }
            timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncMenu", timeForDelta);

            try {
                int daysToAdd = request.getOrganizationComplexesStructureRequest() == null
                        || request.getOrganizationComplexesStructureRequest().getMenuSyncCountDays() == null ? getMenuSyncCountDays(request)
                        : request.getOrganizationComplexesStructureRequest().getMenuSyncCountDays();
                resMenuExchange = getMenuExchangeData(request.getIdOfOrg(), syncStartTime,
                        DateUtils.addDays(syncStartTime, daysToAdd));
            } catch (Exception e) {
                String message = String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg());
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        message);
                logger.error(message, e);
            }
            timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "getMenuExchangeData", timeForDelta);
        }

        // Process prohibitions menu from Org
        try {
            final ProhibitionMenuRequest prohibitionMenuRequest = request.getProhibitionMenuRequest();
            if (prohibitionMenuRequest != null) {
                prohibitionsMenu = getProhibitionsMenuData(request.getOrg(), prohibitionMenuRequest.getMaxVersion());
            }
        } catch (Exception e) {
            String message = String.format("Failed to build prohibitions menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            prohibitionsMenu = new ProhibitionsMenu(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "getProhibitionsMenuData", timeForDelta);

        //Process organization structure
        try {
            final OrganizationStructureRequest organizationStructureRequest = request.getOrganizationStructureRequest();
            if (organizationStructureRequest != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.ORGANIZATIONS_STRUCTURE);
                organizationStructure = getOrganizationStructureData(request.getOrg(),
                        organizationStructureRequest.getMaxVersion(), organizationStructureRequest.isAllOrgs());
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationStructure = new OrganizationStructure(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "getOrganizationStructureData",
                timeForDelta);

        try {
            final OrganizationComplexesStructureRequest organizationComplexesStructureRequest = request
                    .getOrganizationComplexesStructureRequest();
            if (organizationComplexesStructureRequest != null) {
                organizationComplexesStructure = getOrganizationComplexesStructureData(request.getOrg(),
                        organizationComplexesStructureRequest.getMaxVersion(),
                        organizationComplexesStructureRequest.getMenuSyncCountDays(),
                        organizationComplexesStructureRequest.getMenuSyncCountDaysInPast());
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationComplexesStructure = new OrganizationComplexesStructure(100,
                    String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "getOrganizationComplexesStructureData",
                timeForDelta);

        try {
            if (request.getInteractiveReport() != null) {
                interactiveReport = processInteractiveReport(request.getIdOfOrg(), request.getInteractiveReport());
            }
        } catch (Exception e) {
            String message = String.format("processInteractiveReport: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processInteractiveReport", timeForDelta);

        try {
            interactiveReportData = processInteractiveReportData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processInteractiveReportData",
                timeForDelta);

        // Build AccRegistry
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "getAccRegistry", timeForDelta);

        try {
            resCardsOperationsRegistry = new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger,
                "CardsOperationsRegistryHandler().handler", timeForDelta);

        // Process ReqDiary
        try {
            if (request.getReqDiary() != null) {
                resDiary = processSyncDiary(request.getIdOfOrg(), request.getReqDiary());
            }
        } catch (Exception e) {
            resDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncDiary", timeForDelta);

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(),
                                syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.ENTER_EVENTS);
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg(),
                        request.getSyncTime());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSyncEnterEvents", timeForDelta);

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(),
                        request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processTempCardsOperations",
                timeForDelta);

        try {
            if (request.getClientRequests() != null) {
                ClientRequests clientRequests = request.getClientRequests();
                if (clientRequests.getResponseTempCardOperation()) {
                    tempCardOperationData = processClientRequestsOperations(request.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            String message = String.format("processClientRequestsOperations: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processClientRequestsOperations",
                timeForDelta);

        // Process ResCategoriesDiscountsAndRules
        try {
            resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(request.getIdOfOrg(),
                    request.getCategoriesAndDiscountsRequest());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processCategoriesDiscountsAndRules",
                timeForDelta);

        // Process ComplexRoles
        try {
            complexRoles = processComplexRoles();
        } catch (Exception e) {
            String message = String.format("processComplexRoles: %s", e.getMessage());
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processComplexRoles", timeForDelta);

        // Process CorrectingNumbersOrdersRegistry
        try {
            correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger,
                "processSyncCorrectingNumbersOrdersRegistry", timeForDelta);

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processOrgOwnerData", timeForDelta);

        try {
            questionaryData = processQuestionaryData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process questionary data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processQuestionaryData", timeForDelta);

        try {
            if (request.getManager() != null) {
                manager = request.getManager();
                manager.setSyncHistory(syncHistory);
                manager.process(persistenceSessionFactory);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process of Distribution Manager, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "RO", timeForDelta);

        if (bError) {
            DAOService.getInstance().updateLastUnsuccessfulBalanceSync(request.getIdOfOrg());
        } else {
            DAOService.getInstance().updateLastSuccessfulBalanceSync(request.getIdOfOrg());
        }

        try {
            if (request.getDirectivesRequest() != null) {
                directiveElement = processFullSyncDirective(request.getDirectivesRequest(), request.getOrg());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        Date syncEndTime = new Date();
        updateSyncHistory(syncHistory.getIdOfSync(), syncResult, syncEndTime);
        updateFullSyncParam(request.getIdOfOrg());

        runRegularPaymentsIfEnabled(request);

        String fullName = DAOService.getInstance().getPersonNameByOrg(request.getOrg());

        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "Service funcs", timeForDelta);

        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.ACCOUNTS_REGISTRY);
            accountsRegistry = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class)
                    .handlerFull(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "AccountsRegistryHandler.handlerFull",
                timeForDelta);

        try {
            if (request.getReestrTaloonApproval() != null) {
                resReestrTaloonApproval = processReestrTaloonApproval(request.getReestrTaloonApproval());
                reestrTaloonApprovalData = processReestrTaloonApprovalData(request.getReestrTaloonApproval());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonApproval: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processReestrTaloonApproval",
                timeForDelta);

        try {
            if (request.getReestrTaloonPreorder() != null) {
                resReestrTaloonPreorder = processReestrTaloonPreorder(request.getReestrTaloonPreorder());
                reestrTaloonPreorderData = processReestrTaloonPreorderData(request.getReestrTaloonPreorder());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonPreorder: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processReestrTaloonPreorder", timeForDelta);

        try {
            if (request.getRequestsSupplier() != null) {
                resRequestsSupplier = processRequestsSupplier(request.getRequestsSupplier());
                requestsSupplierData = processRequestsSupplierData(request.getRequestsSupplier());
            }
        } catch (Exception e) {
            String message = String.format("processRequestsSupplier: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processRequestsSupplier", timeForDelta);

        // Process MenuSupplier
        try {
            if (request.getMenuSupplier() != null && request.getOrg().getUseWebArm()) {
                resMenuSupplier = processMenuSupplier(request.getMenuSupplier());
            }
        } catch (Exception e) {
            String message = String.format("processMenuSupplier: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processMenuSupplier", timeForDelta);

        try {
            if (request.getZeroTransactions() != null) {
                zeroTransactionData = processZeroTransactionsData(request.getZeroTransactions());
                resZeroTransactions = processZeroTransactions(request.getZeroTransactions());
            }
        } catch (Exception e) {
            String message = String.format("processZeroTransactions: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processZeroTransactions", timeForDelta);

        try {
            if (request.getComplexSchedules() != null) {
                complexScheduleData = processComplexScheduleData(request.getComplexSchedules());
                resComplexSchedules = processComplexSchedules(request.getComplexSchedules());
                responseSections.add(resComplexSchedules);
                responseSections.add(complexScheduleData);
            }
        } catch (Exception e) {
            String message = String.format("processComplexSchedules: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processComplexSchedules", timeForDelta);

        try {
            if (request.getSpecialDates() != null) {
                specialDatesData = processSpecialDatesData(request.getSpecialDates());
                resSpecialDates = processSpecialDates(request.getSpecialDates());
                //
                //Удаление всех SpecalDate, у которых группа null
                deleteOldVersionSpecialDate(request);
                //
            }
        } catch (Exception e) {
            String message = String.format("processSpecialDates: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processSpecialDates", timeForDelta);

        //Process GroupManagers
        try {
            ClientGroupManagerRequest clientGroupManagerRequest = request.getClientGroupManagerRequest();
            if (clientGroupManagerRequest != null) {
                ClientgroupManagersProcessor processor = new ClientgroupManagersProcessor(persistenceSessionFactory,
                        clientGroupManagerRequest);
                ResClientgroupManagers resClientgroupManagers = processor.process();
                ClientgroupManagerData clientgroupManagerData = processor.processData(request.getIdOfOrg());
                responseSections.add(resClientgroupManagers);
                responseSections.add(clientgroupManagerData);
            }
        } catch (Exception e) {
            String message = String.format("Failed to process GroupManagers, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "ClientgroupManagersProcessor",
                timeForDelta);

        //process groups organization
        fullProcessingGroupsOrganization(request, syncHistory, responseSections);
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "fullProcessingGroupsOrganization",
                timeForDelta);

        //info messages
        processInfoMessageSections(request, responseSections);

        try {
            HelpRequest helpRequest = request.getHelpRequest();
            if (helpRequest != null) {
                helpRequestData = processHelpRequestData(helpRequest);
                resHelpRequest = processHelpRequest(helpRequest);
            }
        } catch (Exception e) {
            String message = String.format("processHelpRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processHelpRequest", timeForDelta);

        try {
            PreOrdersFeedingRequest preOrdersFeedingRequest = request.getPreOrderFeedingRequest();
            if (preOrdersFeedingRequest != null) {
                preOrdersFeeding = processPreOrderFeedingRequest(preOrdersFeedingRequest);
            }
        } catch (Exception e) {
            String message = String.format("processPreOrderFeedingRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processPreOrderFeedingRequest",
                timeForDelta);

        try {
            ClientBalanceHoldRequest clientBalanceHoldRequest = request.getClientBalanceHoldRequest();
            if (clientBalanceHoldRequest != null) {
                clientBalanceHoldFeeding = processClientBalanceHoldRequest(clientBalanceHoldRequest);
            }
        } catch (Exception e) {
            String message = String.format("processClientBalanceHoldRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processClientBalanceHoldRequest",
                timeForDelta);

        try {
            ClientBalanceHoldData clientBalanceHoldData = request.getClientBalanceHoldData();
            if (clientBalanceHoldData != null) {
                resClientBalanceHoldData = processClientBalanceHoldData(clientBalanceHoldData);
            }
        } catch (Exception e) {
            String message = String.format("processClientBalanceHoldRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        timeForDelta = addPerformanceInfoAndResetDeltaTime(performanceLogger, "processClientBalanceHoldData",
                timeForDelta);

        try {
            if (request.getCardRequests() != null) {
                cardRequestsData = processCardRequestsData(request.getCardRequests());
            }
        } catch (Exception e) {
            String message = String.format("processCardRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        addPerformanceInfoAndResetDeltaTime(performanceLogger, "processCardRequests", timeForDelta);

        try {
            MenusCalendarSupplierRequest menusCalendarSupplierRequest = request.getMenusCalendarSupplierRequest();
            if (menusCalendarSupplierRequest != null) {
                resMenusCalendar = processMenusCalendarSupplier(menusCalendarSupplierRequest);
            }
        } catch (Exception e) {
            String message = String.format("processMenusCalendarSupplier: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            MenusCalendarRequest menusCalendarRequest = request.getMenusCalendarRequest();
            if (menusCalendarRequest != null) {
                menusCalendarData = processMenusCalendarData(menusCalendarRequest);
            }
        } catch (Exception e) {
            String message = String.format("processMenusCalendarSupplier: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            OrgSettingsRequest orgSettingsRequest = request.getOrgSettingsRequest();
            if (orgSettingsRequest != null) {
                orgSettingsRequest.setIdOfOrgSource(request.getIdOfOrg());
                orgSettingSection = processOrgSettings(orgSettingsRequest);
            }
        } catch (Exception e) {
            String message = String.format("Error when process OrgSettings: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            SyncSettingsRequest syncSettingsRequest = request.findSection(SyncSettingsRequest.class);
            if (syncSettingsRequest != null) {
                syncSettingsRequest.setOwner(request.getIdOfOrg());
                SyncSettingProcessor processor = processSyncSettingRequest(syncSettingsRequest);
                if (processor != null) {
                    resSyncSettingsSection = processor.getResSyncSettingsSection();
                    syncSettingsSection = processor.getSyncSettingsSection();
                    processor = null;
                }
            }
        } catch (Exception e) {
            String message = String.format("Error when process SyncSetting: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        //секция отправок заявок от ЭЖД
        try {
            GoodRequestEZDRequest goodRequestEZDRequest = request.getGoodRequestEZDRequest();
            //Если такая секция существует в исходном запросе
            if (goodRequestEZDRequest != null) {
                goodRequestEZDSection = processGoodRequestEZD(goodRequestEZDRequest, request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("Error when process GoodRequestEZD: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        fullProcessingRequestFeeding(request, syncHistory, responseSections);
        fullProcessingClientDiscountDSZN(request, syncHistory, responseSections);
        fullProcessingPreorderFeedingStatus(request, responseSections);
        fullProcessingPlanOrdersRestrictionsData(request, syncHistory, responseSections);

        ProcessingHardwareSettingsRequest(request, syncHistory, responseSections);
        ProcessingTurnstileSettingsRequest(request, syncHistory, responseSections);

        logger.info("Full sync performance info: " + performanceLogger.toString());
        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime, "",
                accRegistry, resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry,
                resOrgStructure, resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations,
                tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry,
                manager, orgOwnerData, questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian,
                clientGuardianData, accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions, specialDatesData,
                resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest, helpRequestData, preOrdersFeeding,
                cardRequestsData, resMenusCalendar, menusCalendarData, clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer, resMenuSupplier,
                resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildUniversalConstructedSectionsSyncResponse(SyncRequest request, Date syncStartTime,
            int syncResult) throws Exception {
        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации
        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();
        List<Long> errorClientIds = new ArrayList<Long>();
        List<Long> clientsWithWrongVersion = new ArrayList<Long>();

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        // Register sync history
        syncHistory = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                request.getRemoteAddr(), request.getSyncType().getValue());
        addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(), request.getRemoteAddr(),
                request.getSqlServerVersion(), request.getDatabaseSize());

        if(CafeteriaExchangeContentType.MENU.equals(request.getContentType())){
            discardMenusSyncParam(request.getIdOfOrg());
        } else if(CafeteriaExchangeContentType.CLIENTS_DATA.equals(request.getContentType())){
            discardClientSyncParam(request.getIdOfOrg());
        }

        // мигранты
        processMigrantsSectionsWithClientsData(request, syncHistory, responseSections);

        // операции со счетами
        fullProcessingAccountOperationsRegistry(request, responseSections);

        // Process paymentRegistry
        Boolean wasErrorProcessedPaymentRegistry = false;
        processPaymentRegistrySections(request, syncHistory, responseSections, wasErrorProcessedPaymentRegistry,
                idOfPacket, errorClientIds);

        //AccIncRegistry or AccIncUpdate
        boolean wasErrorProcessedAccInc = fullProcessingAccIncRegistryOrAccIncUpdate(request, responseSections);

        // Process ClientParamRegistry
        fullProcessingClientParamsRegistry(request, syncHistory, errorClientIds, clientsWithWrongVersion);

        // Process ClientGuardianRequest
        fullProcessingClientGuardians(request, syncHistory, responseSections);

        // Process OrgStructure
        fullProcessingOrgStructure(request, syncHistory, responseSections);

        // Build client registry
        fullProcessingClientsRegistry(request, syncHistory, responseSections, errorClientIds, clientsWithWrongVersion);

        // базовая корзина (товарный учет)
        fullProcessingGoodsBasicBaskerData(request, syncHistory, responseSections);

        // Process menu from Org
        fullProcessingMenuFromOrg(request, syncStartTime, syncHistory, responseSections);

        // Process prohibitions menu from Org
        fullProcessingProhibitionsMenu(request, syncHistory, responseSections);

        //Process organization structure
        fullProcessingOrganizationStructure(request, syncHistory, responseSections);

        // обработка структуры комплексов в организации
        fullProcessingOrganizationComplexesStructure(request, syncHistory, responseSections);

        // обработка интерактивного отчета
        fullProcessingInteractiveReport(request, syncHistory, responseSections);

        // Build AccRegistry
        fullProcessingAccRegistry(request, syncHistory, responseSections);

        // обработка операций по картам
        fullProcessingCardsOperationsRegistry(request, responseSections);

        // Process ReqDiary
        fullProcessingResDiary(request, syncHistory, responseSections);

        // Process enterEvents
        fullProcessingEnterEvents(request, syncHistory, responseSections);

        // обработка временных карт
        fullProcessingTempCardsOperationsAndData(request, syncHistory, responseSections);

        // Process ResCategoriesDiscountsAndRules
        fullProcessingCategoriesDiscountaAndRules(request, syncHistory, responseSections);

        // Process CorrectingNumbersOrdersRegistry
        fullProcessingCorrectingNumbersSection(request, syncHistory, responseSections);

        // обработка OrgOwnerData
        fullProcessingOrgOwnerData(request, syncHistory, responseSections);

        // обработка анкет
        fullProcessingQuestionaryData(request, syncHistory, responseSections);

        // RO (товарный учет)
        fullProcessingRO(request, syncHistory, responseSections);

        if (request.isFullSync() || request.isAccIncSync()) {
            if (wasErrorProcessedPaymentRegistry || wasErrorProcessedAccInc) {
                DAOService.getInstance().updateLastUnsuccessfulBalanceSync(request.getIdOfOrg());
            } else {
                DAOService.getInstance().updateLastSuccessfulBalanceSync(request.getIdOfOrg());
            }
            runRegularPaymentsIfEnabled(request);
        }

        // обработка директив
        fullProcessingDirectives(request, responseSections);

        // AccountRegistry
        fullProcessingAccountsRegistry(request, responseSections);

        // обработка реестра TallonApproval
        fullProcessingReestTaloonApproval(request, syncHistory, responseSections);

        // обработка реестра TaloonPreorder
        fullProcessingReestrTaloonPreorder(request, syncHistory, responseSections);

        // обработка заявок на питание
        fullProcessingRequestsSupplier(request, syncHistory, responseSections);

        //обработка стaтусов предзаказов
        fullProcessingPreorderFeedingStatus(request, responseSections);

        // обработка справочников веб-технолога
        fullProcessingMenuSupplier(request, syncHistory, responseSections);

        // обработка нулевых транзакций
        fullProcessingZeroTransactions(request, syncHistory, responseSections);

        //обработка расписания комплексов
        fullProcessingComplexSchedule(request, syncHistory, responseSections);

        // обработка SpecialDates
        fullProcessingSpecialDates(request, syncHistory, responseSections);

        // обработка ClientPhotos
        fullProcessingClientPhotos(request, syncHistory, responseSections);

        //Process GroupManagers (классных руководителей)
        fullProcessingClientGroupManagers(request, syncHistory, responseSections);

        //GroupsOrganization
        fullProcessingGroupsOrganization(request, syncHistory, responseSections);

        //info messages
        processInfoMessageSections(request, responseSections);

        fullProcessingOrgFiles(request, syncHistory, responseSections);

        fullProcessingHelpRequests(request, syncHistory, responseSections);

        fullProcessingPreOrderFeedingRequest(request, syncHistory, responseSections);

        fullProcessingClientBalanceHoldRequest(request, syncHistory, responseSections);

        fullProcessingClientBalanceHoldData(request, syncHistory, responseSections);

        fullProcessingRequestFeeding(request, syncHistory, responseSections);

        fullProcessingClientDiscountDSZN(request, syncHistory, responseSections);

        fullProcessingOrgSettings(request, syncHistory, responseSections);

        //Секция заявок по ЭЖД
        fullProcessingGoogRequestEZD(request, syncHistory, responseSections);

        fullProcessingCardRequests(request, syncHistory, responseSections);

        fullProcessingMenusCalendar(request, syncHistory, responseSections);

        //process SyncSetting
        fullProcessingSyncSetting(request, syncHistory, responseSections);

        fullProcessingPlanOrdersRestrictionsData(request, syncHistory, responseSections);

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            //Секция обработки заявок от ЕМИАС
            fullProcessingEMIAS(request, responseSections);

            //Секция обработки заявок от ЕМИАС (от Кафки)
            fullProcessingExemptionVisiting(request, responseSections);
        }
        // время окончания обработки
        Date syncEndTime = new Date();

        if (request.isFullSync() && syncHistory != null) {
            updateSyncHistory(syncHistory.getIdOfSync(), syncResult, syncEndTime);
            updateFullSyncParam(request.getIdOfOrg());
        }

        String fullName = DAOService.getInstance().getPersonNameByOrg(request.getOrg());
        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime,
                responseSections);
    }

    private void fullProcessingGroupsOrganization(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        GroupsOrganizationRequest requestSection = request.findSection(GroupsOrganizationRequest.class);
        if (requestSection == null) {
            return;
        }
        try {
            ResProcessGroupsOrganization resGroupsOrganization = processResGroupsOrganization(requestSection);
            addToResponseSections(resGroupsOrganization, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to process ResGroupsOrganization, %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            ProcessGroupsOrganizationData groupsOrganizationData = processGroupsOrganizationData(requestSection);
            addToResponseSections(groupsOrganizationData, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to process GroupsOrganization, %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingRO(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            if (request.getManager() != null) {
                Manager manager = request.getManager();
                manager.setSyncHistory(syncHistory);
                manager.process(persistenceSessionFactory);
                addToResponseSections(manager, responseSections);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process of Distribution Manager, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
    }

    private void fullProcessingAccountsRegistry(SyncRequest request, List<AbstractToElement> responseSections) {
        try {
            AccountsRegistryRequest requestSection = request.findSection(AccountsRegistryRequest.class);
            if (requestSection != null) {
                AccountsRegistryHandler accountsRegistryHandler = RuntimeContext.getAppContext()
                        .getBean(AccountsRegistryHandler.class);
                AccountsRegistry result = null;
                switch (requestSection.getContentType()) {
                    case ForAll: {
                        saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                                SectionType.ACCOUNTS_REGISTRY);
                        result = accountsRegistryHandler.handlerFull(request, request.getIdOfOrg());
                        break;
                    }
                    case ForCardsAndClients: {
                        result = accountsRegistryHandler.accRegistryUpdateHandler(request);
                        break;
                    }
                    case ForMigrants: {
                        result = accountsRegistryHandler.handlerMigrants(request.getIdOfOrg());
                    }
                }
                if (result != null) {
                    addToResponseSections(result, responseSections);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
    }

    private void fullProcessingClientGroupManagers(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientGroupManagerRequest clientGroupManagerRequest = request.getClientGroupManagerRequest();
            if (clientGroupManagerRequest != null) {
                ClientgroupManagersProcessor processor = new ClientgroupManagersProcessor(persistenceSessionFactory,
                        clientGroupManagerRequest);
                ResClientgroupManagers resClientgroupManagers = processor.process();
                addToResponseSections(resClientgroupManagers, responseSections);
                ClientgroupManagerData clientgroupManagerData = processor.processData(request.getIdOfOrg());
                addToResponseSections(clientgroupManagerData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to process GroupManagers, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingSpecialDates(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SpecialDates specialDatesRequest = request.getSpecialDates();
            if (specialDatesRequest != null) {
                SpecialDatesData specialDatesData = processSpecialDatesData(specialDatesRequest);
                addToResponseSections(specialDatesData, responseSections);

                ResSpecialDates resSpecialDates = processSpecialDates(specialDatesRequest);
                addToResponseSections(resSpecialDates, responseSections);
                //
                //Удаление всех SpecalDate, у которых группа null
                deleteOldVersionSpecialDate(request);
                //
            }
        } catch (Exception e) {
            String message = String.format("processSpecialDates: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingClientPhotos(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientsPhotos clientPhotosRequest = request.getClientPhotos();
            if (clientPhotosRequest != null) {
                ClientPhotosData clientPhotosData = processClientPhotosData(clientPhotosRequest);
                addToResponseSections(clientPhotosData, responseSections);

                ResClientPhotos resClientPhotos = processClientPhotos(clientPhotosRequest);
                addToResponseSections(resClientPhotos, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processClientPhotos: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingZeroTransactions(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ZeroTransactions zeroTransactionsRequest = request.findSection(ZeroTransactions.class);
            if (zeroTransactionsRequest != null) {
                ZeroTransactionData zeroTransactionData = processZeroTransactionsData(zeroTransactionsRequest);
                addToResponseSections(zeroTransactionData, responseSections);

                ResZeroTransactions resZeroTransactions = processZeroTransactions(zeroTransactionsRequest);
                addToResponseSections(resZeroTransactions, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processZeroTransactions: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingComplexSchedule(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ListComplexSchedules complexScheduleRequest = request.findSection(ListComplexSchedules.class);
            if (complexScheduleRequest != null) {
                ComplexScheduleData complexScheduleData = processComplexScheduleData(request.getComplexSchedules());
                addToResponseSections(complexScheduleData, responseSections);

                ResComplexSchedules resComplexSchedules = processComplexSchedules(request.getComplexSchedules());
                addToResponseSections(resComplexSchedules, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processComplexSchedules: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingPreorderFeedingStatus(SyncRequest request, List<AbstractToElement> responseSections) {
        PreorderFeedingStatusRequest preorderFeedingStatusRequest = request.getPreorderFeedingStatusRequest();
        if (preorderFeedingStatusRequest != null) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                PreorderFeedingStatusProcessor processor = new PreorderFeedingStatusProcessor(persistenceSession, preorderFeedingStatusRequest);
                ResPreorderFeedingStatus resPreorderFeedingStatus = processor.process();
                addToResponseSections(resPreorderFeedingStatus, responseSections);

                PreorderFeedingStatusData preorderFeedingStatusData = processor.processData(resPreorderFeedingStatus);
                addToResponseSections(preorderFeedingStatusData, responseSections);

                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Error in fullProcessingPreorderFeedingStatus: ", e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    private void fullProcessingReestTaloonApproval(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ReestrTaloonApproval reestrTaloonApprovalRequest = request.getReestrTaloonApproval();
            if (reestrTaloonApprovalRequest != null) {
                ResReestrTaloonApproval resReestrTaloonApproval = processReestrTaloonApproval(
                        reestrTaloonApprovalRequest);
                addToResponseSections(resReestrTaloonApproval, responseSections);

                ReestrTaloonApprovalData reestrTaloonApprovalData = processReestrTaloonApprovalData(
                        reestrTaloonApprovalRequest);
                addToResponseSections(reestrTaloonApprovalData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonApproval: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingReestrTaloonPreorder(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ReestrTaloonPreorder reestrTaloonPreorderRequest = request.getReestrTaloonPreorder();
            if (reestrTaloonPreorderRequest != null) {
                ResReestrTaloonPreorder resReestrTaloonPreorder = processReestrTaloonPreorder(
                        reestrTaloonPreorderRequest);
                addToResponseSections(resReestrTaloonPreorder, responseSections);

                ReestrTaloonPreorderData reestrTaloonPreorderData = processReestrTaloonPreorderData(
                        reestrTaloonPreorderRequest);
                addToResponseSections(reestrTaloonPreorderData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonPreorder: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingRequestsSupplier(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            RequestsSupplier requestsSupplierRequest = request.getRequestsSupplier();
            if (requestsSupplierRequest != null) {
                ResRequestsSupplier resRequestsSupplier = processRequestsSupplier(
                        requestsSupplierRequest);
                addToResponseSections(resRequestsSupplier, responseSections);

                RequestsSupplierData requestsSupplierData = processRequestsSupplierData(
                        requestsSupplierRequest);
                addToResponseSections(requestsSupplierData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processRequestsSupplier: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingMenuSupplier(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            MenuSupplier menuSupplierRequest = request.getMenuSupplier();
            if (menuSupplierRequest != null) {
                ResMenuSupplier resMenuSupplier = processMenuSupplier(menuSupplierRequest);
                addToResponseSections(resMenuSupplier, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("processMenuSupplier: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingDirectives(SyncRequest request, List<AbstractToElement> responseSections) {
        try {
            DirectivesRequest directivesRequest = request.getDirectivesRequest();
            if (directivesRequest != null) {
                DirectiveElement directiveElement = processFullSyncDirective(directivesRequest, request.getOrg());
                addToResponseSections(directiveElement, responseSections);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
    }

    private void fullProcessingQuestionaryData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SectionRequest sectionRequest = request.findSection(QuestionaryClientsRequest.class);
            if (sectionRequest != null) {
                QuestionaryData questionaryData = processQuestionaryData(request.getIdOfOrg());
                addToResponseSections(questionaryData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to process questionary data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrgOwnerData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SectionRequest requestSection = request.findSection(OrgOwnerDataRequest.class);
            if (requestSection != null) {
                OrgOwnerData orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
                addToResponseSections(orgOwnerData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingCorrectingNumbersSection(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SectionRequest section = request.findSection(CorrectingNumbersOrdersRegistryRequest.class);
            if (section != null) {
                SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(
                        request.getIdOfOrg());
                addToResponseSections(correctingNumbersOrdersRegistry, responseSections);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingComplexRoles(List<AbstractToElement> responseSections) {
        try {
            ComplexRoles complexRoles = processComplexRoles();
            addToResponseSections(complexRoles, responseSections);
        } catch (Exception e) {
            String message = String.format("processComplexRoles: %s", e.getMessage());
            logger.error(message, e);
        }
    }

    private void fullProcessingCategoriesDiscountaAndRules(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            CategoriesDiscountsAndRulesRequest categoriesDiscountsAndRulesRequest = request
                    .findSection(CategoriesDiscountsAndRulesRequest.class);
            if (categoriesDiscountsAndRulesRequest != null) {
                ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(
                        request.getIdOfOrg(), categoriesDiscountsAndRulesRequest);
                addToResponseSections(resCategoriesDiscountsAndRules, responseSections);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingTempCardsOperationsAndData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        TempCardsOperations cardsOperations = request.getTempCardsOperations();
        if (cardsOperations == null) {
            return;
        }

        try {
            ResTempCardsOperations tempCardsOperations = processTempCardsOperations(cardsOperations,
                    request.getIdOfOrg());
            addToResponseSections(tempCardsOperations, responseSections);
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getClientRequests() != null) {
                ClientRequests clientRequests = request.getClientRequests();
                if (clientRequests.getResponseTempCardOperation()) {
                    TempCardOperationData tempCardOperationData = processClientRequestsOperations(request.getIdOfOrg());
                    addToResponseSections(tempCardOperationData, responseSections);
                }
            }
        } catch (Exception e) {
            String message = String.format("processClientRequestsOperations: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingEnterEvents(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(),
                                syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.ENTER_EVENTS);
                SyncResponse.ResEnterEvents resEnterEvents = processSyncEnterEvents(request.getEnterEvents(),
                        request.getOrg(), request.getSyncTime());
                addToResponseSections(resEnterEvents, responseSections);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
    }

    private void fullProcessingResDiary(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.ReqDiary reqDiary = request.findSection(SyncRequest.ReqDiary.class);
        if (reqDiary == null) {
            return;
        }

        SyncResponse.ResDiary resultDiary = null;
        try {
            resultDiary = processSyncDiary(request.getIdOfOrg(), reqDiary);
        } catch (Exception e) {
            resultDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        addToResponseSections(resultDiary, responseSections);
    }

    private void fullProcessingCardsOperationsRegistry(SyncRequest request, List<AbstractToElement> responseSections) {
        try {
            SectionRequest sectionRequest = request.findSection(CardsOperationsRegistry.class);
            if (sectionRequest != null) {
                ResCardsOperationsRegistry resCardsOperationsRegistry = new CardsOperationsRegistryHandler()
                        .handler(request, request.getIdOfOrg());
                addToResponseSections(resCardsOperationsRegistry, responseSections);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }
    }

    private void fullProcessingAccRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncResponse.AccRegistry accRegistry = null;
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        addToResponseSections(accRegistry, responseSections);
    }

    private boolean fullProcessingAccIncRegistryOrAccIncUpdate(SyncRequest request,
            List<AbstractToElement> responseSections) {
        SyncRequest.AccIncRegistryRequest accIncRegistryRequest = request.getAccIncRegistryRequest();
        if (accIncRegistryRequest == null) {
            return false;
        }

        boolean wasError = false;
        try {
            if (request.getProtoVersion() < 6) {
                SyncResponse.AccIncRegistry accIncRegistry = getAccIncRegistry(request.getOrg(),
                        accIncRegistryRequest.dateTime);
                addToResponseSections(accIncRegistry, responseSections);
            } else {
                AccRegistryUpdate accRegistryUpdate = getAccRegistryUpdate(request.getOrg(),
                        accIncRegistryRequest.dateTime);
                addToResponseSections(accRegistryUpdate, responseSections);
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.ACC_INC_REGISTRY);
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccIncRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
            if (request.getProtoVersion() < 6) {
                SyncResponse.AccIncRegistry accIncRegistry = new SyncResponse.AccIncRegistry();
                accIncRegistry.setDate(accIncRegistryRequest.dateTime);
                addToResponseSections(accIncRegistry, responseSections);
            } else {
                AccRegistryUpdate accRegistryUpdate = new AccRegistryUpdate();
                addToResponseSections(accRegistryUpdate, responseSections);
            }
            wasError = true;
        }
        return wasError;
    }

    private void fullProcessingInteractiveReport(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        InteractiveReport interactiveReport = request.getInteractiveReport();
        if (interactiveReport == null) {
            return;
        }

        try {
            InteractiveReport resultInteractiveReport = processInteractiveReport(request.getIdOfOrg(),
                    interactiveReport);
            addToResponseSections(resultInteractiveReport, responseSections);
        } catch (Exception e) {
            String message = String.format("processInteractiveReport: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        try {
            InteractiveReportData interactiveReportData = processInteractiveReportData(request.getIdOfOrg());
            addToResponseSections(interactiveReportData, responseSections);
        } catch (Exception e) {
            String message = String
                    .format("Failed to build interactive report data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrganizationComplexesStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SectionRequest sectionRequest = request.findSection(OrganizationComplexesStructureRequest.class);
        if (sectionRequest == null) {
            return;
        }

        OrganizationComplexesStructure organizationComplexesStructure = null;
        try {
            final OrganizationComplexesStructureRequest organizationComplexesStructureRequest = request
                    .getOrganizationComplexesStructureRequest();
            organizationComplexesStructure = getOrganizationComplexesStructureData(request.getOrg(),
                    organizationComplexesStructureRequest.getMaxVersion(),
                    organizationComplexesStructureRequest.getMenuSyncCountDays(),
                    organizationComplexesStructureRequest.getMenuSyncCountDaysInPast());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationComplexesStructure = new OrganizationComplexesStructure(100,
                    String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(organizationComplexesStructure, responseSections);
    }

    private void fullProcessingOrganizationStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        final OrganizationStructureRequest organizationStructureRequest = request
                .findSection(OrganizationStructureRequest.class);
        if (organizationStructureRequest == null) {
            return;
        }

        OrganizationStructure organizationStructureData = null;
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.ORGANIZATIONS_STRUCTURE);
            organizationStructureData = getOrganizationStructureData(request.getOrg(),
                    organizationStructureRequest.getMaxVersion(), organizationStructureRequest.isAllOrgs());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationStructureData = new OrganizationStructure(100,
                    String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(organizationStructureData, responseSections);
    }

    private void fullProcessingProhibitionsMenu(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        final ProhibitionMenuRequest prohibitionsMenuRequest = request.findSection(ProhibitionMenuRequest.class);
        if (prohibitionsMenuRequest == null) {
            return;
        }

        ProhibitionsMenu prohibitionsMenuData = null;
        try {
            prohibitionsMenuData = getProhibitionsMenuData(request.getOrg(), prohibitionsMenuRequest.getMaxVersion());
        } catch (Exception e) {
            String message = String.format("Failed to build prohibitions menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            prohibitionsMenuData = new ProhibitionsMenu(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(prohibitionsMenuData, responseSections);
    }

    private void fullProcessingMenuFromOrg(SyncRequest request, Date syncStartTime, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.ReqMenu requestMenu = request.getReqMenu();
        if (requestMenu == null) {
            return;
        }

        try {
            processSyncMenu(request.getIdOfOrg(), requestMenu);
        } catch (Exception e) {
            String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        try {
            int daysToAdd = getMenuSyncCountDays(request);
            SyncResponse.ResMenuExchangeData menuExchangeData = getMenuExchangeData(request.getIdOfOrg(), syncStartTime,
                    DateUtils.addDays(syncStartTime, daysToAdd));
            addToResponseSections(menuExchangeData, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        // Process ComplexRoles
        fullProcessingComplexRoles(responseSections);
    }

    private int getMenuSyncCountDays(SyncRequest request) {
        int result = RESPONSE_MENU_PERIOD_IN_DAYS;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Query query = persistenceSession.createQuery("select cp.menuSyncCountDays from ConfigurationProvider cp "
                    + "where cp.idOfConfigurationProvider = :id");
            query.setParameter("id", request.getOrg().getConfigurationProvider().getIdOfConfigurationProvider());
            result = (Integer) query.uniqueResult();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ignore) {
        } //если не можем получить значение из конфигурации, берем дефолт
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private void fullProcessingGoodsBasicBaskerData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SectionRequest goodsBasicBasketRequest = request.findSection(GoodsBasicBasketRequest.class);
            if (goodsBasicBasketRequest != null) {
                GoodsBasicBasketData goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
                addToResponseSections(goodsBasicBasketData, responseSections);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process goods basic basket data , IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingClientsRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, List<Long> errorClientIds, List<Long> clientsWithWrongVersion) {
        try {
            SyncRequest.ClientRegistryRequest clientRegistryRequest = request.getClientRegistryRequest();
            if (clientRegistryRequest != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.CLIENT_REGISTRY);
                SyncResponse.ClientRegistry clientRegistry = processSyncClientRegistry(request.getIdOfOrg(),
                        clientRegistryRequest, errorClientIds, clientsWithWrongVersion);
                addToResponseSections(clientRegistry, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private boolean fullProcessingPaymentRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, List<Long> errorClientIds, Long idOfPacket) {
        boolean wasError = false;
        try {
            PaymentRegistry paymentRegistryRequest = request.getPaymentRegistry();
            if (paymentRegistryRequest == null) {
                return wasError;
            }

            if (paymentRegistryRequest.getPayments() != null && paymentRegistryRequest.getPayments().hasNext()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    SyncHistory localSyncHistory = syncHistory;
                    if (localSyncHistory == null) {
                        String clientVersion = (request.getClientVersion() == null ? "" : request.getClientVersion());
                        Long packet = (idOfPacket == null ? -1L : idOfPacket);
                        localSyncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(), clientVersion,
                                request.getRemoteAddr(), request.getSyncType().getValue());
                    }
                    final String s = String
                            .format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available",
                                    request.getIdOfOrg());
                    processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(),
                            localSyncHistory, s);
                    throw new Exception("no license slots available");
                }
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.PAYMENT_REGISTRY);
            ResPaymentRegistry resPaymentRegistry = processSyncPaymentRegistry(
                    syncHistory != null ? syncHistory.getIdOfSync() : null, request.getIdOfOrg(),
                    paymentRegistryRequest, errorClientIds);
            addToResponseSections(resPaymentRegistry, responseSections);
        } catch (Exception e) {
            wasError = true;
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        return wasError;
    }

    private void fullProcessingClientParamsRegistry(SyncRequest request, SyncHistory syncHistory,
            List<Long> errorClientIds, List<Long> clientsWithWrongVersion) {
        try {
            SyncRequest.ClientParamRegistry clientParamRegistry = request.getClientParamRegistry();
            if (clientParamRegistry != null) {
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("Синхронизация по секциям (ConstructedSections)");
                clientsMobileHistory.setOrg(getOrgReference(persistenceSessionFactory.openSession(), request.getIdOfOrg()));
                clientsMobileHistory.setShowing("АРМ ОО (ид." + request.getIdOfOrg() + ")");
                processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), clientParamRegistry, errorClientIds,
                        clientsWithWrongVersion, clientsMobileHistory);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrgStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.OrgStructure orgStructureRequest = request.getOrgStructure();
        if (orgStructureRequest == null) {
            return;
        }
        SyncResponse.ResOrgStructure resOrgStructure = null;
        try {
            resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), orgStructureRequest, syncHistory);
            if (resOrgStructure != null && resOrgStructure.getResult() > 0) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        resOrgStructure.getError());
            }
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        addToResponseSections(resOrgStructure, responseSections);
    }

    private void fullProcessingClientGuardians(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientGuardianRequest clientGuardianRequest = request.getClientGuardianRequest();
            if (clientGuardianRequest != null) {
                final List<ClientGuardianItem> clientGuardianResponseElement = clientGuardianRequest
                        .getClientGuardianResponseElement();
                if (clientGuardianResponseElement != null) {
                    ResultClientGuardian resultClientGuardian = processClientGuardian(clientGuardianResponseElement,
                            request.getIdOfOrg(), syncHistory);
                    addToResponseSections(resultClientGuardian, responseSections);
                }
                final Long responseClientGuardian = clientGuardianRequest.getMaxVersion();
                if (responseClientGuardian != null) {
                    ClientGuardianData clientGuardianData = processClientGuardianData(request.getIdOfOrg(), syncHistory,
                            responseClientGuardian);
                    addToResponseSections(clientGuardianData, responseSections);
                }
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientGuardianRequest, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingAccountOperationsRegistry(SyncRequest request,
            List<AbstractToElement> responseSections) {
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.ACCOUNT_OPERATIONS_REGISTRY);
            if (request.getAccountOperationsRegistry() != null) {
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                ResAccountOperationsRegistry resAccountOperationsRegistry = accountOperationsRegistryHandler
                        .process(request);
                addToResponseSections(resAccountOperationsRegistry, responseSections);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке AccountOperationsRegistry: ", e);
        }
    }

    private void processMigrantsSectionsWithClientsData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        Migrants migrantsRequest = request.getMigrants();
        if (migrantsRequest == null) {
            return;
        }
        processMigrantsSections(request, syncHistory, responseSections, null);
        if (request.getClientRegistryRequest() == null)
            processClientRegistrySectionsForMigrants(request, syncHistory, responseSections);
        processAccountRegistrySectionsForMigrants(request, syncHistory, responseSections);
        if (request.getClientGuardianRequest() == null)
            processClientGuardianDataSectionsForMigrants(request, syncHistory, responseSections);
    }

    private void processClientGuardianDataSectionsForMigrants(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientGuardianData clientGuardianData = processClientGuardianDataForMigrants(request.getIdOfOrg(),
                    syncHistory, null);
            addToResponseSections(clientGuardianData, responseSections);
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientGuardianRequest, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void processAccountRegistrySectionsForMigrants(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            AccountsRegistryRequest requestSection = request.findSection(AccountsRegistryRequest.class);
            if (requestSection == null || AccountsRegistryRequest.ContentType.ForCardsAndClients
                    .equals(requestSection.getContentType())) {
                AccountsRegistry accountsRegistry = RuntimeContext.getAppContext()
                        .getBean(AccountsRegistryHandler.class).handlerMigrants(request.getIdOfOrg());
                addToResponseSections(accountsRegistry, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void processClientRegistrySectionsForMigrants(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SyncResponse.ClientRegistry clientRegistry = processSyncClientRegistryForMigrants(request.getIdOfOrg());
            addToResponseSections(clientRegistry, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            if (syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        message);
            }
            logger.error(message, e);
        }
    }

    private void processMigrantsSections(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, Boolean error) {
        try {
            if (request.getMigrants() != null) {
                ResMigrants resMigrants = processMigrants(request.getMigrants());
                MigrantsData migrantsData = processMigrantsData(request.getMigrants());
                addToResponseSections(resMigrants, responseSections);
                addToResponseSections(migrantsData, responseSections);
            }
        } catch (Exception e) {
            if (error != null) {
                error = true;
            }
            String message = String.format("processMigrants: %s", e.getMessage());
            if (syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        message);
            }
            logger.error(message, e);
        }
    }

    private void processAccountOperationsRegistrySections(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, Boolean error) {
        try {
            if (request.getAccountOperationsRegistry() != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.ACCOUNT_OPERATIONS_REGISTRY);
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                ResAccountOperationsRegistry resAccountOperationsRegistry = accountOperationsRegistryHandler
                        .process(request);
                addToResponseSections(resAccountOperationsRegistry, responseSections);
            }
        } catch (Exception e) {
            if (error != null) {
                error = true;
            }
            String message = String.format("Ошибка при обработке AccountOperationsRegistry: %s", e.getMessage());
            if (syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory,
                        message);
            }
            logger.error(message, e);
        }
    }

    private void processPaymentRegistrySections(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, Boolean error, Long idOfPacket, List<Long> errorClientIds) {
        try {
            if (request.getPaymentRegistry() != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.PAYMENT_REGISTRY);
                if (request.getPaymentRegistry().getPayments() != null) {

                    String clientVersion = (request.getClientVersion() == null ? "" : request.getClientVersion());
                    Long packet = (idOfPacket == null ? -1L : idOfPacket);
                    if (syncHistory == null && !request.getSyncType().equals(SyncType.TYPE_GET_ACC_INC)) {
                        syncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(), clientVersion,
                                request.getRemoteAddr(), request.getSyncType().getValue());
                    }

                    if (request.getPaymentRegistry().getPayments().hasNext()) {
                        if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            final String s = String
                                    .format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available",
                                            request.getIdOfOrg());
                            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(),
                                    syncHistory, s);
                            throw new Exception("no license slots available");
                        }
                    }
                    //saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.PAYMENT_REGISTRY);
                    ResPaymentRegistry resPaymentRegistry = processSyncPaymentRegistry(null, request.getIdOfOrg(),
                            request.getPaymentRegistry(), errorClientIds);
                    addToResponseSections(resPaymentRegistry, responseSections);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process Payment Registry, IdOfOrg == %s", request.getIdOfOrg()), e);
            if (error != null) {
                error = true;
            }
        }
    }

    private void addToResponseSections(AbstractToElement section, List<AbstractToElement> responseSections) {
        if (section != null) {
            responseSections.add(section);
        }
    }

    /* Do process full synchronization */
    private SyncResponse buildCommodityAccountingSyncResponse(SyncRequest request) throws Exception {

        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getManager() != null) {
                manager = request.getManager();
                manager.setSyncHistory(syncHistory);
                manager.process(persistenceSessionFactory);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process of Distribution Manager, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            directiveElement = processFullSyncDirective(request.getDirectivesRequest(), request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }


        Date syncEndTime = new Date();

        String fullName = DAOService.getInstance().getPersonNameByOrg(request.getOrg());

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime, "",
                accRegistry, resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry,
                resOrgStructure, resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations,
                tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry,
                manager, orgOwnerData, questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian,
                clientGuardianData, accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions, specialDatesData,
                resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest, helpRequestData, preOrdersFeeding, cardRequestsData,
				resMenusCalendar, menusCalendarData, clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer, resMenuSupplier,
                resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildReestrTaloonsApprovalSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getReestrTaloonApproval() != null) {
                resReestrTaloonApproval = processReestrTaloonApproval(request.getReestrTaloonApproval());
                reestrTaloonApprovalData = processReestrTaloonApprovalData(request.getReestrTaloonApproval());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonApproval: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                 specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
				clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildReestrTaloonsPreorderSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getReestrTaloonPreorder() != null) {
                resReestrTaloonPreorder = processReestrTaloonPreorder(request.getReestrTaloonPreorder());
                reestrTaloonPreorderData = processReestrTaloonPreorderData(request.getReestrTaloonPreorder());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonPreorder: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer, resMenuSupplier,
                resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildRequestsSupplierSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;


        List<AbstractToElement> responseSections = new ArrayList<>();

        boolean bError = false;

        try {
            if (request.getRequestsSupplier() != null) {
                resRequestsSupplier = processRequestsSupplier(request.getRequestsSupplier());
                requestsSupplierData = processRequestsSupplierData(request.getRequestsSupplier());
            }
        } catch (Exception e) {
            String message = String.format("processRequestsSupplier: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildZeroTransactionsSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getZeroTransactions() != null) {
                zeroTransactionData = processZeroTransactionsData(request.getZeroTransactions());
                resZeroTransactions = processZeroTransactions(request.getZeroTransactions());
            }
        } catch (Exception e) {
            String message = String.format("processZeroTransactions: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildMigrantsSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getMigrants() != null) {
                resMigrants = processMigrants(request.getMigrants());
                migrantsData = processMigrantsData(request.getMigrants());

            }
        } catch (Exception e) {
            String message = String.format("processMigrants: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            clientRegistry = processSyncClientRegistryForMigrants(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            accRegistry = getAccRegistryForMigrants(request.getIdOfOrg());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);

        }

        try {
            accountsRegistry = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class)
                    .handlerMigrants(request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            clientGuardianData = processClientGuardianDataForMigrants(request.getIdOfOrg(), syncHistory, null);
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientGuardianRequest, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();
        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private void processInfoMessageSections(SyncRequest request, List<AbstractToElement> responseSections) {
        InfoMessageRequest infoMessageRequest = request.getInfoMessageRequest();
        if (infoMessageRequest == null) {
            return;
        }

        InfoMessageData infoMessageData = null;
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.INFO_MESSAGE);
            infoMessageData = getInfoMessageData(request.getOrg(), infoMessageRequest.getMaxVersion());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            infoMessageData = new InfoMessageData(
                    new ResultOperation(100, String.format("Internal error: %s", e.getMessage())));
            logger.error(message, e);
        }
        addToResponseSections(infoMessageData, responseSections);
    }

    /* Do process short synchronization for update Client parameters */
    private SyncResponse buildClientsParamsSyncResponse(SyncRequest request) {

        Long idOfPacket = null; // регистируются и заполняются только для полной синхронизации
        SyncHistory idOfSync = null;

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        AccRegistryUpdateRequest accRegistryUpdateRequest = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        Boolean bError = false;

        processAccountOperationsRegistrySections(request, null, responseSections, null);

        processPaymentRegistrySections(request, null, responseSections, bError, idOfPacket, errorClientIds);

        // Build AccRegistryUpdateRequest
        try {
            accRegistryUpdateRequest = request.getAccRegistryUpdateRequest();
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);
        }

        // Build AccRegistry
        try {
            if (accRegistryUpdateRequest != null) {
                accRegistry = getAccRegistry(request.getIdOfOrg(), accRegistryUpdateRequest.getClientIds(),
                        request.getClientVersion());
            } else {
                accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
            }
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }
        List<Long> clientsWithWrongVersion = new ArrayList<Long>();
        try {

            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("Синхронизация типа GetClientParams (синхра клиентов)");
            clientsMobileHistory.setOrg(getOrgReference(persistenceSessionFactory.openSession(), request.getIdOfOrg()));
            clientsMobileHistory.setShowing("АРМ ОО (ид." + request.getIdOfOrg() + ")");
            processSyncClientParamRegistry(idOfSync, request.getIdOfOrg(), request.getClientParamRegistry(),
                    errorClientIds, clientsWithWrongVersion, clientsMobileHistory);
        } catch (Exception e) {
            logger.error(String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }
        // Build client registry
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.CLIENT_REGISTRY);
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(), request.getClientRegistryRequest(),
                    errorClientIds, clientsWithWrongVersion);
        } catch (Exception e) {
            logger.error(String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(),
                        request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            resCardsOperationsRegistry = new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    /* Do process short synchronization for update AccRegisgtryUpdate parameters */
    private SyncResponse buildAccRegisgtryUpdate(SyncRequest request) {

        Long idOfPacket = null; // регистируются и заполняются только для полной синхронизации
        SyncHistory idOfSync = null;

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        AccRegistryUpdateRequest accRegistryUpdateRequest = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        processAccountOperationsRegistrySections(request, null, responseSections, null);


        processPaymentRegistrySections(request, null, responseSections, null, idOfPacket, errorClientIds);

        // Build AccRegistryUpdateRequest
        try {
            accRegistryUpdateRequest = request.getAccRegistryUpdateRequest();
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }
        // Build AccRegistry
        try {
            if (accRegistryUpdateRequest != null) {
                accRegistry = getAccRegistry(request.getIdOfOrg(), accRegistryUpdateRequest.getClientIds(),
                        request.getClientVersion());
            } else {
                accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
            }
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(),
                        request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            resCardsOperationsRegistry = new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            accountsRegistry = new AccountsRegistryHandler().accRegistryUpdateHandler(request);
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            if (request.getCardRequests() != null) {
                cardRequestsData = processCardRequestsData(request.getCardRequests());
            }
        } catch (Exception e) {
            String message = String.format("processCardRequest: %s", e.getMessage());
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer, resMenuSupplier,
                resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    /* Do process short synchronization for update payment register and account inc register */
    private SyncResponse buildAccIncSyncResponse(SyncRequest request) {

        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        Boolean bError = false;

        processAccountOperationsRegistrySections(request, null, responseSections, bError);

        processPaymentRegistrySections(request, null, responseSections, bError, idOfPacket, errorClientIds);

        //info messages
        processInfoMessageSections(request, responseSections);
        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias.accinc", "1").equals("1")) {
                //if (SyncRequest.versionIsAfter(request.getClientVersion(), "2.7.96")) {
                //    try {
                //        EmiasRequest emiasRequest = request.getEmiasRequest();
                //        if (emiasRequest != null) {
                //            FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                //            emiasSection = new EmiasSection();
                //            emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                //            emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                //            emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                //            emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                //        }
                //    } catch (Exception e) {
                //        String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                //        logger.error(message, e);
                //    }
                //}

                try {
                    ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                    if (exemptionVisitingRequest != null) {
                        FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                                exemptionVisitingRequest, request.getIdOfOrg());
                        exemptionVisitingSection = new ExemptionVisitingSection();
                        exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                        exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                        exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                        exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                    }
                } catch (Exception e) {
                    String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                    logger.error(message, e);
                }
            }
        }

        try {
            if (request.getProtoVersion() < 6) {
                accIncRegistry = getAccIncRegistry(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            } else {
                accRegistryUpdate = getAccRegistryUpdate(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                    SectionType.ACC_INC_REGISTRY);

        } catch (Exception e) {
            logger.error(String.format("Failed to build AccIncRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
            if (request.getProtoVersion() < 6) {
                accIncRegistry = new SyncResponse.AccIncRegistry();
                accIncRegistry.setDate(request.getAccIncRegistryRequest().dateTime);
            } else {
                accRegistryUpdate = new AccRegistryUpdate();
            }
            bError = true;
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(),
                        SectionType.ENTER_EVENTS);
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg(),
                        request.getSyncTime());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process Enter Events, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(),
                        request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        if (bError) {
            DAOService.getInstance().updateLastUnsuccessfulBalanceSync(request.getIdOfOrg());
        } else {
            DAOService.getInstance().updateLastSuccessfulBalanceSync(request.getIdOfOrg());
        }

        Date syncEndTime = new Date();

        runRegularPaymentsIfEnabled(request);

        try {
            resCardsOperationsRegistry = new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            accountsRegistry = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class)
                    .accRegistryHandler(request, request.getIdOfOrg());
            //accountsRegistry = new AccountsRegistryHandler().accRegistryHandler(request,request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emulatorOn", "0")
                .equals("1")) {
            try {
                correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
            } catch (Exception e) {
                String message = String.format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s",
                        request.getIdOfOrg());
                logger.error(message, e);
            }
        }

        try {
            ClientBalanceHoldRequest clientBalanceHoldRequest = request.getClientBalanceHoldRequest();
            if (clientBalanceHoldRequest != null) {
                clientBalanceHoldFeeding = processClientBalanceHoldRequest(clientBalanceHoldRequest);
            }
        } catch (Exception e) {
            String message = String.format("processClientBalanceHoldRequest: %s", e.getMessage());
            logger.error(message, e);
        }

        try {
            ClientBalanceHoldData clientBalanceHoldData = request.getClientBalanceHoldData();
            if (clientBalanceHoldData != null) {
                resClientBalanceHoldData = processClientBalanceHoldData(clientBalanceHoldData);
            }
        } catch (Exception e) {
            String message = String.format("processClientBalanceHoldRequest: %s", e.getMessage());
            logger.error(message, e);
        }

        updateOrgSyncDate(request.getIdOfOrg());
        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private SyncResponse buildMenuSupplierSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getMenuSupplier() != null) {
                resMenuSupplier = processMenuSupplier(request.getMenuSupplier());
            }
        } catch (Exception e) {
            String message = String.format("processMenuSupplier(): %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection, exemptionVisitingSectionForARMAnswer, resMenuSupplier,
                resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private void updateOrgSyncDate(long idOfOrg) {
        try {
            OrgSyncWritableRepository orgSyncWritableRepository = OrgSyncWritableRepository.getInstance();
            orgSyncWritableRepository.updateAccRegistryDate(idOfOrg);
        } catch (Exception e) {
            logger.error("Не удалось обновить время синхронизации, idOfOrg: " + idOfOrg, e);
        }
    }

    private void updateFullSyncParam(long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            falseFullSyncByOrg(persistenceSession, idOfOrg);
            setValueForClientsSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            setValueForMenusSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            setValueForOrgSettingsSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void discardClientSyncParam(Long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            setValueForClientsSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void discardMenusSyncParam(Long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            setValueForMenusSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void discardOrgSettingsSyncParam(Long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            setValueForOrgSettingsSyncByOrg(persistenceSession, idOfOrg, Boolean.FALSE);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private DirectiveElement processSyncDirective(Org org) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        DirectiveElement directiveElement = new DirectiveElement();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            directiveElement.process(persistenceSession, org);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return directiveElement;
    }

    private DirectiveElement processFullSyncDirective(DirectivesRequest directivesRequest, Org org) throws Exception {
        DirectiveElement directiveElement = new DirectiveElement();
        directiveElement.processForFullSync(directivesRequest, org);
        return directiveElement;
    }


    private ResPaymentRegistry processSyncPaymentRegistry(Long idOfSync, Long idOfOrg, PaymentRegistry paymentRegistry,
            List<Long> errorClientIds) throws Exception {
        ResPaymentRegistry resPaymentRegistry = new ResPaymentRegistry();
        List<Long> allocatedClients = ClientManager.getAllocatedClientsIds(idOfOrg);
        Iterator<Payment> payments = paymentRegistry.getPayments();
        while (payments.hasNext()) {
            Payment Payment = payments.next();
            ResPaymentRegistryItem resAcc;
            try {
                resAcc = processSyncPaymentRegistryPayment(idOfSync, idOfOrg, Payment, errorClientIds,
                        allocatedClients);
                if (resAcc.getResult() != 0) {
                    logger.error("Failure in response payment registry: " + resAcc);
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to process payment == %s", Payment), e);
                resAcc = new ResPaymentRegistryItem(Payment.getIdOfOrder(), 100, "Internal error");
            }
            // TODO: если resAcc.getResult() != 0 записать в журнал ошибок синхры
            resPaymentRegistry.addItem(resAcc);
        }
        return resPaymentRegistry;
    }


    private OrgOwnerData processOrgOwnerData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        OrgOwnerData orgOwnerData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new OrgOwnerProcessor(persistenceSession, idOfOrg);
            orgOwnerData = (OrgOwnerData) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return orgOwnerData;
    }

    private ResTempCardsOperations processTempCardsOperations(TempCardsOperations tempCardsOperations, Long idOfOrg)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResTempCardsOperations resTempCardsOperations = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new TempCardOperationProcessor(persistenceSession, tempCardsOperations,
                    idOfOrg);
            resTempCardsOperations = (ResTempCardsOperations) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resTempCardsOperations;
    }

    private PlanOrdersRestrictions processPlanOrdersRestrictions(PlanOrdersRestrictionsRequest planOrdersRestrictionsRequest) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        PlanOrdersRestrictions planOrdersRestrictions = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            PlanOrdersRestrictionsProcessor processor = new PlanOrdersRestrictionsProcessor(persistenceSession, planOrdersRestrictionsRequest);
            planOrdersRestrictions = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return planOrdersRestrictions;
    }

    private ResReestrTaloonApproval processReestrTaloonApproval(ReestrTaloonApproval reestrTaloonApproval)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ReestrTaloonApprovalProcessor(persistenceSession, reestrTaloonApproval);
            resReestrTaloonApproval = (ResReestrTaloonApproval) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resReestrTaloonApproval;
    }

    private ReestrTaloonApprovalData processReestrTaloonApprovalData(ReestrTaloonApproval reestrTaloonApproval)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ReestrTaloonApprovalProcessor processor = new ReestrTaloonApprovalProcessor(persistenceSession,
                    reestrTaloonApproval);
            reestrTaloonApprovalData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return reestrTaloonApprovalData;
    }

    private ResReestrTaloonPreorder processReestrTaloonPreorder(ReestrTaloonPreorder reestrTaloonPreorder)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ReestrTaloonPreorderProcessor(persistenceSession, reestrTaloonPreorder);
            resReestrTaloonPreorder = (ResReestrTaloonPreorder) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resReestrTaloonPreorder;
    }

    private ReestrTaloonPreorderData processReestrTaloonPreorderData(ReestrTaloonPreorder reestrTaloonPreorder)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ReestrTaloonPreorderProcessor processor = new ReestrTaloonPreorderProcessor(persistenceSession,
                    reestrTaloonPreorder);
            reestrTaloonPreorderData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return reestrTaloonPreorderData;
    }

    private ZeroTransactionData processZeroTransactionsData(ZeroTransactions zeroTransactions) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ZeroTransactionData zeroTransactionData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ZeroTransactionsProcessor processor = new ZeroTransactionsProcessor(persistenceSession, zeroTransactions);
            zeroTransactionData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return zeroTransactionData;
    }

    private ResZeroTransactions processZeroTransactions(ZeroTransactions zeroTransactions) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResZeroTransactions resZeroTransactions = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ZeroTransactionsProcessor(persistenceSession, zeroTransactions);
            resZeroTransactions = (ResZeroTransactions) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resZeroTransactions;
    }

    private CardRequestsData processCardRequestsData(CardRequests cardRequests) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        CardRequestsData cardRequestsData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            CardRequestsProcessor processor = new CardRequestsProcessor(persistenceSession, cardRequests);
            cardRequestsData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return cardRequestsData;
    }

    private ResComplexSchedules processComplexSchedules(ListComplexSchedules complexSchedules) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResComplexSchedules resComplexSchedules = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ComplexScheduleProcessor(persistenceSession, complexSchedules);
            resComplexSchedules = (ResComplexSchedules) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resComplexSchedules;
    }

    private ComplexScheduleData processComplexScheduleData(ListComplexSchedules complexSchedules) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ComplexScheduleData complexScheduleData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ComplexScheduleProcessor processor = new ComplexScheduleProcessor(persistenceSession, complexSchedules);
            complexScheduleData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return complexScheduleData;
    }

    private SpecialDatesData processSpecialDatesData(SpecialDates specialDates) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SpecialDatesData specialDatesData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            SpecialDatesProcessor processor = new SpecialDatesProcessor(persistenceSession, specialDates);
            specialDatesData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return specialDatesData;
    }

    private ResSpecialDates processSpecialDates(SpecialDates specialDates) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResSpecialDates resSpecialDates = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceSession.setFlushMode(FlushMode.MANUAL);
            persistenceTransaction = persistenceSession.beginTransaction();
            SpecialDatesProcessor processor = new SpecialDatesProcessor(persistenceSession, specialDates);
            resSpecialDates = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resSpecialDates;
    }

    private MigrantsData processMigrantsData(Migrants migrants) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        MigrantsData migrantsData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            MigrantsProcessor processor = new MigrantsProcessor(persistenceSession, migrants);
            migrantsData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return migrantsData;
    }

    private ResMigrants processMigrants(Migrants migrants) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResMigrants resMigrants = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            MigrantsProcessor processor = new MigrantsProcessor(persistenceSession, migrants);
            resMigrants = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMigrants;
    }


    public static List<Client> getMigrants(Long idOfOrg) {
        List<Client> clients = null;
        try {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                clients = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, idOfOrg);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            String message = String.format("processMigrants: %s", e.getMessage());
            logger.error(message, e);
        }
        return clients;
    }

    private ClientPhotosData processClientPhotosData(ClientsPhotos clientPhotos) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientPhotosData clientPhotosData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientPhotosProcessor processor = new ClientPhotosProcessor(persistenceSession, clientPhotos);
            clientPhotosData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientPhotosData;
    }

    private ResClientPhotos processClientPhotos(ClientsPhotos clientPhotos) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResClientPhotos resClientPhotos = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientPhotosProcessor processor = new ClientPhotosProcessor(persistenceSession, clientPhotos);
            resClientPhotos = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resClientPhotos;
    }

    //responce
    private InteractiveReportData processInteractiveReportData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        InteractiveReportData interactiveReportData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            InteractiveReportDataProcessor processor = new InteractiveReportDataProcessor(persistenceSession, idOfOrg);
            interactiveReportData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return interactiveReportData;
    }

    //request
    private InteractiveReport processInteractiveReport(Long idOfOrg, InteractiveReport interactiveReportItem)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        InteractiveReport interactiveReport = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new InteractiveReportProcessor(persistenceSession, idOfOrg,
                    interactiveReportItem);
            interactiveReport = (InteractiveReport) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return interactiveReport;
    }

    private TempCardOperationData processClientRequestsOperations(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        TempCardOperationData tempCardOperationData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new TempCardRequestProcessor(persistenceSession, idOfOrg);
            tempCardOperationData = (TempCardOperationData) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return tempCardOperationData;
    }

    private ClientGuardianData processClientGuardianData(Long idOfOrg, SyncHistory syncHistory, Long maxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientGuardianData clientGuardianData = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientGuardianDataProcessor processor = new ClientGuardianDataProcessor(persistenceSession, idOfOrg,
                    maxVersion);
            clientGuardianData = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            String message = String.format("Load Client Guardian to database error, IdOfOrg == %s :", idOfOrg);
            logger.error(message, ex);
            clientGuardianData = new ClientGuardianData(new ResultOperation(100, ex.getMessage()));
            processorUtils.createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory, message);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGuardianData;
    }

    private ClientGuardianData processClientGuardianDataForMigrants(Long idOfOrg, SyncHistory syncHistory,
            Long maxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientGuardianData clientGuardianData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientGuardianDataProcessor processor = new ClientGuardianDataProcessor(persistenceSession, idOfOrg,
                    maxVersion);
            clientGuardianData = processor.processForMigrants();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            String message = String.format("Load Client Guardian to database error, IdOfOrg == %s :", idOfOrg);
            logger.error(message, ex);
            clientGuardianData = new ClientGuardianData(new ResultOperation(100, ex.getMessage()));
            processorUtils.createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory, message);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGuardianData;
    }

    private ResultClientGuardian processClientGuardian(List<ClientGuardianItem> items, Long idOfOrg,
            SyncHistory syncHistory) {
        ResultClientGuardian resultClientGuardian = new ResultClientGuardian();
        Long resultClientGuardianVersion = 0L;
        if (items.size() > 0) {
            resultClientGuardianVersion = getClientGuardiansResultVersion();
        }

        boolean enableNotificationSpecial = RuntimeContext.getInstance().getOptionValueBool(
                Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL
        );

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceSession.setFlushMode(FlushMode.COMMIT);
            persistenceTransaction = persistenceSession.beginTransaction();
            for (ClientGuardianItem item : items) {
                try {
                    Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
                    criteria.add(Restrictions.eq("idOfChildren", item.getIdOfChildren()));
                    criteria.add(Restrictions.eq("idOfGuardian", item.getIdOfGuardian()));
                    ClientGuardian dbClientGuardian = (ClientGuardian) criteria.uniqueResult();
                    if (dbClientGuardian == null) {
                        ClientGuardian clientGuardian = new ClientGuardian(item.getIdOfChildren(),
                                item.getIdOfGuardian());
                        clientGuardian.setDisabled(item.getDisabled());
                        clientGuardian.setVersion(resultClientGuardianVersion);
                        clientGuardian.setDeletedState(item.isDeleted());
                        clientGuardian.setRepresentType(item.getRepresentType());
                        if (item.getRelation() != null) {
                            clientGuardian.setRelation(ClientGuardianRelationType.fromInteger(item.getRelation()));
                        }
                        if (item.isDeleted()) {
                            clientGuardian.delete(resultClientGuardianVersion);
                        }
                        if (item.getDisabled() || item.isDeleted()) {
                            MigrantsUtils
                                    .disableMigrantRequestIfExists(persistenceSession, idOfOrg, item.getIdOfGuardian());
                        } else if(enableNotificationSpecial) {
                            clientGuardian.getNotificationSettings().add(
                                    new ClientGuardianNotificationSetting(clientGuardian,
                                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())
                            );
                        }
                        clientGuardian.setLastUpdate(new Date());
                        persistenceSession.save(clientGuardian);
                        resultClientGuardian.addItem(clientGuardian, 0, null);
                    } else {
                        if (dbClientGuardian.getDeletedState() && !item.isDeleted()) {
                            dbClientGuardian.restore(resultClientGuardianVersion, enableNotificationSpecial);
                        } else if (item.isDeleted()) {
                            dbClientGuardian.delete(resultClientGuardianVersion);
                        }
                        if (item.getDisabled() || item.isDeleted()) {
                            MigrantsUtils
                                    .disableMigrantRequestIfExists(persistenceSession, idOfOrg, item.getIdOfGuardian());
                        }
                        if (item.getRelation() != null) {
                            dbClientGuardian.setRelation(ClientGuardianRelationType.fromInteger(item.getRelation()));
                        }
                        dbClientGuardian.setVersion(resultClientGuardianVersion);
                        dbClientGuardian.setDisabled(item.getDisabled());
                        dbClientGuardian.setRepresentType(item.getRepresentType());
                        dbClientGuardian.setLastUpdate(new Date());
                        persistenceSession.update(dbClientGuardian);
                        resultClientGuardian.addItem(dbClientGuardian, 0, null);
                    }
                } catch (Exception e) {
                    resultClientGuardian.addItem(item, 100, e.getMessage());
                    logger.error("Error in process ClientGuardian section: ", e);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            logger.error("Error processing ClientsGuardian section: ", ex);
            processorUtils.createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory,
                    "Internal error ClientsGuardian");
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resultClientGuardian;
    }

    private Long getClientGuardiansResultVersion() {
        Long version = 0L;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            version = ClientManager.generateNewClientGuardianVersion(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            logger.error("Failed get max client guardians vesion, ", ex);
            version = 0L;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return version;
    }


    private ComplexRoles processComplexRoles() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ComplexRoles complexRoles = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ComplexRoleProcessor processor = new ComplexRoleProcessor(persistenceSession);
            complexRoles = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return complexRoles;
    }

    private QuestionaryData processQuestionaryData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        QuestionaryData questionaryData = new QuestionaryData();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            questionaryData.process(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return questionaryData;
    }

    private GoodsBasicBasketData processGoodsBasicBasketData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        GoodsBasicBasketData goodsBasicBasketData = new GoodsBasicBasketData();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            goodsBasicBasketData.process(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return goodsBasicBasketData;
    }

    public ResPaymentRegistryItem processSyncPaymentRegistryPayment(Long idOfSync, Long idOfOrg, Payment payment,
            List<Long> errorClientIds, List<Long> allocatedClients) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //  Применяем фильтр оборудования
            idOfOrg = DAOService.getInstance()
                    .receiveIdOfOrgByAccessory(idOfOrg, Accessory.BANK_ACCESSORY_TYPE, payment.getIdOfPOS());

            //SyncHistory syncHistory = (SyncHistory) persistenceSession.load(SyncHistory.class, idOfSync);
            //Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            Long idOfOrganization = getIdOfOrg(persistenceSession, idOfOrg);
            if (null == idOfOrganization) {
                return new ResPaymentRegistryItem(payment.getIdOfOrder(), 130,
                        String.format("Organization not found, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }

            if (payment.getIdOfCashier() == null) {
                return new ResPaymentRegistryItem(payment.getIdOfOrder(), 201,
                        String.format("Cashier attribute not found, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }

            Long idOfOrgPayment = payment.getIdOfOrg();
            boolean isFromFriendlyOrg = false;
            if (null != idOfOrgPayment) {
                Long idOfOrganizationPayment = getIdOfOrg(persistenceSession, idOfOrgPayment);

                if (null == idOfOrganizationPayment) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 130,
                            String.format("Organization not found, IdOfOrg == %s, IdOfOrder == %s", idOfOrgPayment,
                                    payment.getIdOfOrder()));
                }

                if (!DAOService.getInstance().isOrgFriendly(idOfOrg, idOfOrgPayment)) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 150,
                            String.format("Organization is not friendly, IdOfOrg == %s, IdOfOrder == %s",
                                    idOfOrgPayment, payment.getIdOfOrder()));
                }
                isFromFriendlyOrg = true;
                long temp = idOfOrg;
                idOfOrg = idOfOrgPayment;
                idOfOrgPayment = temp;
            }

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder());
            Order order = findOrder(persistenceSession, compositeIdOfOrder);

            if (payment.isCommit()) {

                // Check order existence
                //if (DAOUtils.existOrder(persistenceSession, idOfOrg, payment.getIdOfOrder())) {
                if (order != null) {
                    // if order == payment (may be last sync result was not transferred to client)
                    if ((order.getCreateTime().equals(payment.getTime())) && (order.getSumByCard().equals(payment.getSumByCard()))) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0, "Order is already registered");
                    } else {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 110, String.format(
                                "Order already registered but attributes differ, IdOfOrg == %s, IdOfOrder == %s",
                                idOfOrg, payment.getIdOfOrder()));
                    }
                }
                // If cardNo specified - load card from data model
                Card card = null;
                Long cardNo = payment.getCardNo();
                Long longCardNo = payment.getLongCardNo();
                if (null != cardNo || null != longCardNo) {
                    if (longCardNo != null) {
                        card = DAOUtils.findCardByLongCardNoExtended(persistenceSession, cardNo, payment.getIdOfClient(), null, null);
                    } else {
                        card = findCardByCardNoExtended(persistenceSession, cardNo, payment.getIdOfClient(), null, null);
                    }
                    if (null == card) {
                        logger.info(
                                String.format("Unknown card, IdOfOrg == %s, IdOfOrder == %s, CardNo == %s", idOfOrg,
                                        payment.getIdOfOrder(), cardNo));
                    } else {
                        RuntimeContext.getAppContext().getBean(CardBlockService.class)
                                .saveLastCardActivity(persistenceSession, card.getIdOfCard(), CardActivityType.ORDER);
                    }
                }
                // If client specified - load client from data model
                Client client = null;
                Long idOfClient = payment.getIdOfClient();
                if (null != idOfClient) {
                    client = findClient(persistenceSession, idOfClient);
                    // Check client existance

                    if (null == client) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 210,
                                String.format("Unknown client, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                                        idOfOrg, payment.getIdOfOrder(), idOfClient));
                    }
                }
                if (null != card) {
                    if (null == client) {
                        // Client is specified if card is specified
                        client = card.getClient();
                    }
                }
                // If client is specified - check if client is registered for the specified organization
                // or for one of friendly organizations of specified one
                Set<Long> idOfFriendlyOrgSet = getIdOfFriendlyOrg(persistenceSession, idOfOrg);
                if (null != client) {
                    Org clientOrg = client.getOrg();
                    if (!clientOrg.getIdOfOrg().equals(idOfOrg) && !idOfFriendlyOrgSet
                            .contains(clientOrg.getIdOfOrg())) {
                        if (!(MigrantsUtils.getActiveMigrantsByIdOfClient(persistenceSession, client.getIdOfClient())
                                .size() > 0)) {
                            if (!allocatedClients.contains(client.getIdOfClient())) {
                                errorClientIds.add(idOfClient);
                            }
                        }
                    }
                }
                // Verify spicified sums to be valid non negative numbers
                if (payment.getSumByCard() < 0 || payment.getSumByCash() < 0 || payment.getSocDiscount() < 0
                        || payment.getRSum() < 0 || payment.getGrant() < 0) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 250,
                            String.format("Negative sum(s) are specified, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
                /*if (0 != payment.getSumByCard() && card == null) {
                    // Check if card is specified
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 240, String.format(
                            "Payment has card part but doesn't specify CardNo, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                            idOfOrg, payment.getIdOfOrder(), idOfClient));
                }*/

                SecurityJournalBalance journalBalance = SecurityJournalBalance
                        .getSecurityJournalBalanceDataFromOrder(payment, client, SJBalanceTypeEnum.SJBALANCE_TYPE_ORDER,
                                SJBalanceSourceEnum.SJBALANCE_SOURCE_ORDER, idOfOrg);

                // Create order
                RuntimeContext.getFinancialOpsManager()
                        .createOrderCharge(persistenceSession, payment, idOfOrg, client, card, payment.getConfirmerId(),
                                isFromFriendlyOrg, idOfOrgPayment);
                long totalPurchaseDiscount = 0;
                long totalPurchaseRSum = 0;
                long totalLunchRSum = 0;
                Set<String> rations = new HashSet<>();
                //Проверяем, есть ли среди деталей элемент со ссылкой на предзаказ
                PreorderComplex preorderComplex = findPreorderComplexByPayment(persistenceSession, payment);
                boolean saveAllPreorderDetails = (preorderComplex == null ? false : preorderComplex.getModeOfAdd().equals(PreorderComplex.COMPLEX_MODE_4));

                // Register order details (purchase)
                for (Purchase purchase : payment.getPurchases()) {
                    if (null != findOrderDetail(persistenceSession,
                            new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()))) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 120, String.format(
                                "Order detail is already registered, IdOfOrg == %s, IdOfOrder == %s, IdOfOrderDetail == %s",
                                idOfOrg, payment.getIdOfOrder(), purchase.getIdOfOrderDetail()));
                    }
                    if (purchase.getDiscount() < 0 || purchase.getrPrice() < 0 /*|| purchase.getQty() < 0*/) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 250, String.format(
                                "Negative Discount or rPrice are specified, IdOfOrg == %s, IdOfOrder == %s, Discount == %s, Discount == %s",
                                idOfOrg, payment.getIdOfOrder(), purchase.getDiscount(), purchase.getrPrice()));
                    }
                    OrderDetail orderDetail = new OrderDetail(
                            new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()),
                            payment.getIdOfOrder(), purchase.getQty(), purchase.getDiscount(),
                            purchase.getSocDiscount(), purchase.getrPrice(), purchase.getName(), purchase.getRootMenu(),
                            purchase.getMenuGroup(), purchase.getMenuOrigin(), purchase.getMenuOutput(),
                            purchase.getType(), purchase.getIdOfMenu(), purchase.getManufacturer(),
                            payment.getIdOfClient() == null || !MealManager.isSendToExternal, purchase.getItemCode(),
                            purchase.getIdOfRule(), OrderDetailFRationType.fromInteger(purchase.getfRation()));
                    if (purchase.getGuidOfGoods() != null) {
                        Good good = findGoodByGuid(persistenceSession, purchase.getGuidOfGoods());
                        if (good != null) {
                            orderDetail.setGood(good);
                        }
                    }
                    if (purchase.getIdOfComplex() != null) {
                        WtComplex wtComplex = findWtComplexById(persistenceSession, purchase.getIdOfComplex());
                        if (wtComplex != null) {
                            orderDetail.setWtComplex(wtComplex);
                        }
                    }
                    if (purchase.getIdOfDish() != null) {
                        orderDetail.setIdOfDish(purchase.getIdOfDish());
                    }
                    if (saveAllPreorderDetails || purchase.getGuidPreOrderDetail() != null) {
                        savePreorderGuidFromOrderDetail(persistenceSession, purchase.getGuidPreOrderDetail(),
                                orderDetail, false, preorderComplex, purchase.getGuidOfGoods(), payment.getRSum());
                    }
                    persistenceSession.save(orderDetail);
                    totalPurchaseDiscount += purchase.getDiscount() * Math.abs(purchase.getQty());
                    totalPurchaseRSum += purchase.getrPrice() * Math.abs(purchase.getQty());

                    if (orderDetail.isComplex() || orderDetail.isComplexItem()) {
                        totalLunchRSum += purchase.getrPrice() * Math.abs(purchase.getQty());
                    }
                    if (purchase.getfRation() != null && OrderDetailFRationType.fromInteger(purchase.getfRation()) != OrderDetailFRationType.NOT_SPECIFIED) {
                        rations.add(OrderDetailFRationType.fromInteger(purchase.getfRation()).toString());
                    }
                }
                // Check payment sums
                if (totalPurchaseRSum != payment.getRSum()
                        || totalPurchaseDiscount != payment.getSocDiscount() + payment.getTrdDiscount() + payment
                        .getGrant()) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 300,
                            String.format("Invalid total sum by order, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
                if (payment.getRSum() != payment.getSumByCard() + payment.getSumByCash() + payment.getSummFromCBHR()) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 310,
                            String.format("Invalid sum of order card and cash payments, IdOfOrg == %s, IdOfOrder == %s",
                                    idOfOrg, payment.getIdOfOrder()));
                }

                //Если заказ есть и по нему не было сообщения, то отправляем сообщение
                NotificationOrders notificationOrder =
                        DAOUtils.findNotificationOrder(persistenceSession,payment.getIdOfOrder(), client,false);

                // Commit data model transaction
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;

                SecurityJournalBalance.saveSecurityJournalBalance(journalBalance, true, "OK");

                // !!!!! ОПОВЕЩЕНИЕ ПО СМС !!!!!!!!
                /* в случае анонимного заказа мы не знаем клиента */
                /* не оповещаем в случае пробития корректировочных заказов */
                if (client != null) {
                    if (client.clientHasActiveSmartWatch()) {
                        try {
                            SmartWatchVendorNotificationManager manager = RuntimeContext.getAppContext().getBean(
                                    SmartWatchVendorNotificationManager.class);
                            manager.sendPurchasesInfoToVendor(payment, client);
                        } catch (Exception exc) {
                            logger.error("Can't send to Vendor JSON with Purchases", exc);
                        }
                    }

                    String[] values = generatePaymentNotificationParams(persistenceSession, client, payment);
                    if (payment.getOrderType().equals(OrderTypeEnumType.UNKNOWN) || payment.getOrderType()
                            .equals(OrderTypeEnumType.DEFAULT) || payment.getOrderType()
                            .equals(OrderTypeEnumType.VENDING)) {
                        values = EventNotificationService.attachToValues("isBarOrder", "true", values);
                    } else if (payment.getOrderType().equals(OrderTypeEnumType.PAY_PLAN) || payment.getOrderType()
                            .equals(OrderTypeEnumType.SUBSCRIPTION_FEEDING)) {
                        values = EventNotificationService.attachToValues("isPayOrder", "true", values);
                    } else if (payment.getOrderType().equals(OrderTypeEnumType.REDUCED_PRICE_PLAN) || payment
                            .getOrderType().equals(OrderTypeEnumType.DAILY_SAMPLE) || payment.getOrderType()
                            .equals(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE) || payment.getOrderType()
                            .equals(OrderTypeEnumType.CORRECTION_TYPE) || payment.getOrderType()
                            .equals(OrderTypeEnumType.WATER_ACCOUNTING) || payment.getOrderType()
                            .equals(OrderTypeEnumType.DISCOUNT_PLAN_CHANGE) || payment.getOrderType()
                            .equals(OrderTypeEnumType.RECYCLING_RETIONS)) {
                        values = EventNotificationService.attachToValues("isFreeOrder", "true", values);
                    }
                    if (rations.size() > 0) {
                        values = EventNotificationService
                                .attachToValues(EventNotificationService.PARAM_FRATION, StringUtils.join(rations, ","), values);
                    }
                    String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(payment.getTime());
                    values = EventNotificationService
                            .attachToValues(EventNotificationService.PARAM_ORDER_EVENT_TIME, date, values);
                    values = EventNotificationService
                            .attachToValues(EventNotificationService.PARAM_COMPLEX_NAME, getComplexName(payment),
                                    values);
                    values = EventNotificationService.attachTargetIdToValues(payment.getIdOfOrder(), values);
                    values = EventNotificationService
                            .attachSourceOrgIdToValues(idOfOrg, values); //организация из пакета синхронизации
                    long totalBuffetRSum = totalPurchaseRSum - totalLunchRSum;
                    long totalRSum = totalBuffetRSum + totalLunchRSum;
                    long totalAmountBuyAll = totalBuffetRSum + totalLunchRSum;
                    values = EventNotificationService.attachMoneyToValues(totalBuffetRSum, values, EventNotificationService.PARAM_AMOUNT_PRICE);
                    values = EventNotificationService.attachMoneyToValues(totalLunchRSum, values, EventNotificationService.PARAM_AMOUNT_LUNCH);
                    values = EventNotificationService.attachMoneyToValues(totalRSum, values, EventNotificationService.PARAM_AMOUNT);
                    values = EventNotificationService.attachMoneyToValues(totalAmountBuyAll, values, EventNotificationService.PARAM_AMOUNT_BUY_ALL);
                    if (client.getBalance() != null) {
                        values = EventNotificationService.attachToValues("balance",
                                Long.toString(client.getBalance() / 100) + ',' + Long
                                        .toString(Math.abs(client.getBalance()) % 100), values);
                    }
                    values = EventNotificationService.attachGenderToValues(client.getGender(), values);
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                            .sendNotificationAsync(client, null, EventNotificationService.MESSAGE_PAYMENT, values,
                                    payment.getOrderDate());

                    List<Client> guardians = findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);

                    if (!(guardians == null || guardians.isEmpty())) {
                        for (Client destGuardian : guardians) {
                            if (DAOReadonlyService.getInstance()
                                    .allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                            client.getIdOfClient(), getOrderNotificationType(values))) {
                                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                                        .sendNotificationAsync(destGuardian, client,
                                                EventNotificationService.MESSAGE_PAYMENT, values,
                                                payment.getOrderDate());
                            }
                        }
                    }
                }
            } else {
                // TODO: есть ли необходимость оповещать клиента о сторне?
                // отмена заказа
                if (null != order) {
                    if (payment.getIdOfClient() != null) {
                        Client client = DAOService.getInstance().findClientById(payment.getIdOfClient());
                        SecurityJournalBalance journalBalance = SecurityJournalBalance
                                .getSecurityJournalBalanceDataFromOrder(payment, client,
                                        SJBalanceTypeEnum.SJBALANCE_TYPE_PAYMENT,
                                        SJBalanceSourceEnum.SJBALANCE_SOURCE_CANCEL_ORDER, idOfOrg);
                        SecurityJournalBalance.saveSecurityJournalBalance(journalBalance, true, "OK");
                    }
                    // Update client balance
                    RuntimeContext.getFinancialOpsManager().cancelOrder(persistenceSession, order, payment);
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } else {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0,
                            String.format("Unknown order, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
            }
            // Return no errors
            return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0, null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private ResMenuSupplier processMenuSupplier(MenuSupplier menuSupplier)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResMenuSupplier resMenuSupplier = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new MenuSupplierProcessor(persistenceSession, menuSupplier);
            resMenuSupplier = (ResMenuSupplier) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMenuSupplier;
    }

    private ResRequestsSupplier processRequestsSupplier(RequestsSupplier requestsSupplier)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResRequestsSupplier resRequestsSupplier = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new RequestsSupplierProcessor(persistenceSession, requestsSupplier);
            resRequestsSupplier = (ResRequestsSupplier) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resRequestsSupplier;
    }

    private RequestsSupplierData processRequestsSupplierData(RequestsSupplier requestsSupplier)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        RequestsSupplierData requestsSupplierData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            RequestsSupplierProcessor processor = new RequestsSupplierProcessor(persistenceSession, requestsSupplier);
            requestsSupplierData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return requestsSupplierData;
    }

    private boolean transactionOwnerHaveSmartWatch(AccountTransaction transaction) {
        if (transaction.getClient() != null) {
            return transaction.getClient().clientHasActiveSmartWatch();
        } else if (transaction.getCard() != null) {
            return transaction.getCard().getClient().clientHasActiveSmartWatch();
        }
        return false;
    }

    private String getComplexName(Payment payment) {
        if (payment.getPurchases() == null) {
            return "";
        }
        for (Purchase purchase : payment.getPurchases()) {
            if (purchase.getType() != null && purchase.getType() > 0 && purchase.getType() < 100) {
                return purchase.getName();
            }
        }
        return "";
    }

    private Long getOrderNotificationType(String[] values) throws Exception {
        if (EventNotificationService.findBooleanValueInParams(new String[]{"isBarOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_BAR.getValue();
        } else if (EventNotificationService.findBooleanValueInParams(new String[]{"isPayOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_PAY.getValue();
        } else if (EventNotificationService.findBooleanValueInParams(new String[]{"isFreeOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_FREE.getValue();
        } else {
            throw new Exception("Не определен тип события");
        }
    }

    private void processSyncClientParamRegistry(SyncHistory syncHistory, Long idOfOrg,
            SyncRequest.ClientParamRegistry clientParamRegistry, List<Long> errorClientIds,
            List<Long> clientsWithWrongVersion, ClientsMobileHistory clientsMobileHistory) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Iterator<SyncRequest.ClientParamRegistry.ClientParamItem> clientParamItems = clientParamRegistry
                    .getPayments().iterator();

            List<Long> allocatedClients = ClientManager.getAllocatedClientsIds(persistenceSession, idOfOrg);
            HashMap<Long, HashMap<String, ClientGroup>> orgMap = new HashMap<Long, HashMap<String, ClientGroup>>();
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            Set<Org> orgSet = org.getFriendlyOrg();
            /* совместимость организаций которые не имеют дружественных организаций */
            orgSet.add(org);
            for (Org o : orgSet) {
                List clientGroups = getClientGroupsByIdOfOrg(persistenceSession, o.getIdOfOrg());
                HashMap<String, ClientGroup> nameIdGroupMap = new HashMap<String, ClientGroup>();
                for (Object object : clientGroups) {
                    ClientGroup clientGroup = (ClientGroup) object;
                    nameIdGroupMap.put(clientGroup.getGroupName(), clientGroup);
                    orgMap.put(clientGroup.getCompositeIdOfClientGroup().getIdOfOrg(), nameIdGroupMap);
                }
                orgMap.put(o.getIdOfOrg(), nameIdGroupMap);
            }
            Long version = null;
            if (clientParamItems.hasNext()) {
                version = updateClientRegistryVersion(persistenceSession);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

            while (clientParamItems.hasNext()) {
                SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem = clientParamItems.next();

                //Проверяем, если у клиента меняется организация, то блокируем ему карты в старой организации
                Client client = DAOUtils.findClient(persistenceSession, clientParamItem.getIdOfClient());
                disableClientCardsIfChangeOrg(client, orgSet, idOfOrg);
                Boolean isRemoveDiscount = removeClientDiscountIfChangeOrg(client, persistenceSession, orgSet, idOfOrg);
                if (!isRemoveDiscount) {
                    archiveApplicationForFoodIfChangeOrg(client, persistenceSession, orgSet, idOfOrg);
                }

                /*ClientGroup clientGroup = orgMap.get(2L).get(clientParamItem.getGroupName());
                 *//* если группы нет то создаем *//*
                if(clientGroup == null){
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, idOfOrg, clientParamItem.getGroupName());
                    *//* заносим в хэш - карту*//*
                    nameIdGroupMap.put(clientGroup.getGroupName(),clientGroup);
                }*/
                try {
                    //processSyncClientParamRegistryItem(idOfSync, idOfOrg, clientParamItem, orgMap, version);
                    processSyncClientParamRegistryItem(clientParamItem, orgMap, version, errorClientIds, idOfOrg,
                            allocatedClients, orgSet, clientsWithWrongVersion, clientsMobileHistory);
                } catch (Exception e) {
                    String message = String.format("Failed to process clientParamItem == %s", idOfOrg);
                    if (syncHistory != null) {
                        processorUtils
                                .createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory, message);
                    }
                    logger.error(message, e);
                }
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private String getCanonicalDiscounts(String discounts) {
        String[] arr = discounts.split(",");
        if (arr.length == 0) {
            return "";
        }
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        Arrays.sort(arr);
        return Joiner.on(",").join(arr);
    }

    private void processSyncClientParamRegistryItem(SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem,
            HashMap<Long, HashMap<String, ClientGroup>> orgMap, Long version, List<Long> errorClientIds, Long idOfOrg,
            List<Long> allocatedClients, Set<Org> orgSet, List<Long> clientsWithWrongVersion,
            ClientsMobileHistory clientsMobileHistory) throws Exception {
        boolean ignoreNotifyFlags = RuntimeContext.getInstance().getSmsService().ignoreNotifyFlags();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = findClient(persistenceSession, clientParamItem.getIdOfClient());
            if (clientParamItem.getVersion() != null && clientParamItem.getVersion() < client
                    .getClientRegistryVersion()) {
                clientsWithWrongVersion.add(client.getIdOfClient());
                return;
            }

            if (!orgMap.containsKey(client.getOrg().getIdOfOrg())) {
                if (!(MigrantsUtils.getActiveMigrantsByIdOfClient(persistenceSession, clientParamItem.getIdOfClient())
                        .size() > 0)) {
                    if (!allocatedClients.contains(clientParamItem.getIdOfClient())) {
                        errorClientIds.add(client.getIdOfClient());
                        throw new IllegalArgumentException(
                                "Client from another organization. idOfClient=" + client.getIdOfClient().toString()
                                        + ", idOfOrg=" + idOfOrg.toString() + ", clientParamItem=" + clientParamItem
                                        .toString());
                    }
                }
            } else {
                Long orgOwner = clientParamItem.getOrgOwner();
                boolean changeOrg = false;
                Org oldOrg = client.getOrg();
                if (orgOwner != null) {
                    Org org = (Org) persistenceSession.get(Org.class, orgOwner);
                    changeOrg = !client.getOrg().getIdOfOrg().equals(org.getIdOfOrg());
                    client.setOrg(org);
                }
                client.setFreePayCount(clientParamItem.getFreePayCount());
                client.setFreePayMaxCount(clientParamItem.getFreePayMaxCount());
                client.setLastFreePayTime(clientParamItem.getLastFreePayTime());
                client.setDisablePlanCreationDate(clientParamItem.getDisablePlanCreationDate());
                client.setDisablePlanEndDate(clientParamItem.getDisablePlanEndDate());
                if (clientParamItem.getExpenditureLimit() != null) {
                    client.setExpenditureLimit(clientParamItem.getExpenditureLimit());
                }

                if (clientParamItem.getAddress() != null) {
                    client.setAddress(clientParamItem.getAddress());
                }
                if (!RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_DISABLE_EMAIL_EDIT)
                        && clientParamItem.getEmail() != null) {
                    String email = clientParamItem.getEmail();
                    client.setEmail(email);
                    if (!StringUtils.isEmpty(clientParamItem.getEmail())
                            && clientParamItem.getNotifyViaEmail() == null) {
                        client.setNotifyViaEmail(true);
                    }
                }
                if (clientParamItem.getMobilePhone() != null) {
                    String mobile = Client.checkAndConvertMobile(clientParamItem.getMobilePhone());
                    client.initClientMobileHistory(clientsMobileHistory);
                    client.setMobile(mobile);
                    logger.info("class : ClientManager, method : modifyClientTransactionFree line : 344, idOfClient : "
                            + client.getIdOfClient() + " mobile : " + client.getPhone());
                    if (!StringUtils.isEmpty(mobile)) {
                        if (clientParamItem.getNotifyViaSMS() == null) {
                            client.setNotifyViaSMS(true);
                        }
                        if (clientParamItem.getNotifyViaPUSH() == null) {
                            client.setNotifyViaPUSH(false);
                        }
                    }
                }
                if (clientParamItem.getMiddleGroup() != null) {
                    client.setMiddleGroup(clientParamItem.getMiddleGroup());
                }
                if (clientParamItem.getGender() != null) {
                    client.setGender(clientParamItem.getGender());
                }
                if (clientParamItem.getBirthDate() != null) {
                    client.setBirthDate(clientParamItem.getBirthDate());
                }
                if (clientParamItem.getName() != null) {
                    client.getPerson().setFirstName(clientParamItem.getName());
                }
                if (clientParamItem.getPhone() != null) {
                    client.setPhone(clientParamItem.getPhone());
                    logger.info(
                            "class : Processor, method : processSyncClientParamRegistryItem line : 3485, idOfClient : "
                                    + client.getIdOfClient() + " phone : " + client.getPhone());
                }
                if (clientParamItem.getSecondName() != null) {
                    client.getPerson().setSecondName(clientParamItem.getSecondName());
                }
                if (clientParamItem.getSurname() != null) {
                    client.getPerson().setSurname(clientParamItem.getSurname());
                }
                if (clientParamItem.getRemarks() != null) {
                    client.setRemarks(clientParamItem.getRemarks());
                }
                if (clientParamItem.getSan() != null) {
                    client.setSan(clientParamItem.getSan());
                }
                if (!ignoreNotifyFlags) {
                    if (clientParamItem.getNotifyViaEmail() != null) {
                        client.setNotifyViaEmail(clientParamItem.getNotifyViaEmail());
                    }
                    if (clientParamItem.getNotifyViaSMS() != null) {
                        client.setNotifyViaSMS(clientParamItem.getNotifyViaSMS());
                    }
                    if (clientParamItem.getNotifyViaPUSH() != null) {
                        client.setNotifyViaPUSH(clientParamItem.getNotifyViaPUSH());
                    }
                }
                /* FAX клиента */
                if (clientParamItem.getFax() != null) {
                    client.setFax(clientParamItem.getFax());
                }
                /* разрешает клиенту подтверждать оплату групового питания */
                if (clientParamItem.getCanConfirmGroupPayment() != null) {
                    client.setCanConfirmGroupPayment(clientParamItem.getCanConfirmGroupPayment());
                }

                /* согласие на видеоидентификацию */
                if (clientParamItem.getConfirmVisualRecognition() != null) {
                    client.setConfirmVisualRecognition(clientParamItem.getConfirmVisualRecognition());
                }

                /* заносим клиента в группу */
                if (StringUtils.isNotEmpty(clientParamItem.getGroupName())) {
                    ClientGroup  clientGroup;
                    if (changeOrg) {
                        clientGroup = findClientGroupByGroupNameAndIdOfOrg(persistenceSession,
                                client.getOrg().getIdOfOrg(), clientParamItem.getGroupName());
                    } else {
                        clientGroup = orgMap.get(client.getOrg().getIdOfOrg()).get(clientParamItem.getGroupName());
                    }
                    //если группы нет то создаем
                    if (clientGroup == null) {
                        clientGroup = createClientGroup(persistenceSession, client.getOrg().getIdOfOrg(),
                                clientParamItem.getGroupName());
                        // заносим в хэш - карту
                        orgMap.get(client.getOrg().getIdOfOrg()).put(clientGroup.getGroupName(), clientGroup);
                    }

                    if (changeOrg) {
                        ClientManager.addClientMigrationEntry(persistenceSession, oldOrg, client.getClientGroup(),
                                client.getOrg(), client, ClientGroupMigrationHistory.MODIFY_IN_ARM
                                        .concat(String.format(" (ид. ОО=%s)", idOfOrg)), clientGroup.getGroupName());
                    } else {
                        if (client.getClientGroup() == null || !clientGroup.equals(client.getClientGroup())) {
                            ClientManager.createClientGroupMigrationHistory(persistenceSession, client, client.getOrg(),
                                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                                    ClientGroupMigrationHistory.MODIFY_IN_ARM
                                            .concat(String.format(" (ид. ОО=%s)", idOfOrg)));
                        }
                    }
                    client.setClientGroup(clientGroup);
                    client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                }

                if (clientParamItem.getIsUseLastEEModeForPlan() != null) {
                    client.setUseLastEEModeForPlan(clientParamItem.getIsUseLastEEModeForPlan());
                }
                if (clientParamItem.getBalanceToNotify() != null) {
                    client.setBalanceToNotify((clientParamItem.getBalanceToNotify()));
                }
                if (clientParamItem.getPassportNumber() != null) {
                    client.setPassportNumber(clientParamItem.getPassportNumber());
                }
                if (clientParamItem.getPassportSeries() != null) {
                    client.setPassportSeries(clientParamItem.getPassportSeries());
                }
            }

            String categoriesFromPacket = getCanonicalDiscounts(clientParamItem.getCategoriesDiscounts());

            Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();
            Set<CategoryDiscount> categoryDiscountOfClient = client.getCategories();
            int newClientDiscountMode = clientParamItem.getDiscountMode();
            int oldClientDiscountMode = client.getDiscountMode();
            if (clientParamItem.getDiscountMode() == Client.DISCOUNT_MODE_BY_CATEGORY) {
                /* распарсим строку с категориями */
                if (clientParamItem.getCategoriesDiscounts() != null) {
                    String[] catArray = categoriesFromPacket.split(",");
                    List<Long> idOfCategoryDiscount = new ArrayList<Long>();
                    for (String number : catArray) {
                        idOfCategoryDiscount.add(Long.parseLong(number.trim()));
                    }
                    Criteria categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                    categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryDiscount));
                    for (Object object : categoryDiscountCriteria.list()) {
                        categoryDiscountSet.add((CategoryDiscount) object);
                    }
                    if (!categoryDiscountSet.equals(categoryDiscountOfClient)) {
                        if (!categoriesFromPacket.equals("")) {
                            client.setCategories(categoryDiscountSet);
                        }
                    }
                }
            } else {
                /* Льгота по категориями то очищаем */
                if (!client.getCategories().isEmpty()) {
                    client.setCategories(new HashSet<CategoryDiscount>());
                }
            }

            // Если льготы изменились, то сохраняем историю
            if (!(newClientDiscountMode == oldClientDiscountMode) || !(categoryDiscountSet
                    .equals(categoryDiscountOfClient))) {
                Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
                DiscountManager.saveDiscountHistory(persistenceSession, client, org, categoryDiscountOfClient, categoryDiscountSet,
                        oldClientDiscountMode, newClientDiscountMode, DiscountChangeHistory.MODIFY_IN_ARM);
                client.setLastDiscountsUpdate(new Date());
            }
            client.setDiscountMode(clientParamItem.getDiscountMode());

            DiscountManager.deleteDOUDiscountsIfNeedAfterSetAgeTypeGroup(persistenceSession, client);

            client.setClientRegistryVersion(version);
            client.setUpdateTime(new Date());
            persistenceSession.update(client);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.CorrectingNumbersOrdersRegistry processSyncCorrectingNumbersOrdersRegistry(Long idOfOrg)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria orderCriteria = persistenceSession.createCriteria(Order.class);
            orderCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            orderCriteria.setProjection(Projections.max("compositeIdOfOrder.idOfOrder"));
            List orderMax = orderCriteria.list();
            Criteria orderDetailCriteria = persistenceSession.createCriteria(OrderDetail.class);
            orderDetailCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            orderDetailCriteria.setProjection(Projections.max("compositeIdOfOrderDetail.idOfOrderDetail"));
            List orderDetailMax = orderDetailCriteria.list();

            Criteria enterEventCriteria = persistenceSession.createCriteria(EnterEvent.class);
            enterEventCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            enterEventCriteria.setProjection(Projections.max("compositeIdOfEnterEvent.idOfEnterEvent"));
            List enterEventMax = enterEventCriteria.list();

            Criteria migrRequestCriteria = persistenceSession.createCriteria(Migrant.class);
            migrRequestCriteria.add(Restrictions.eq("orgRegistry.idOfOrg", idOfOrg));
            migrRequestCriteria.setProjection(Projections.max("compositeIdOfMigrant.idOfRequest"));
            List migrRequestMax = migrRequestCriteria.list();

            persistenceTransaction.commit();
            persistenceTransaction = null;
            Long idOfOrderMax = (Long) orderMax.get(0), idOfOrderDetail = (Long) orderDetailMax
                    .get(0), idOfEnterEvent = (Long) enterEventMax.get(0), idOfOutcomeMigrRequests = 0L;
            if (migrRequestMax.size() > 0) {
                idOfOutcomeMigrRequests = (Long) migrRequestMax.get(0);
            }
            if (idOfOrderMax == null) {
                idOfOrderMax = 0L;
            }
            if (idOfOrderDetail == null) {
                idOfOrderDetail = 0L;
            }
            if (idOfEnterEvent == null) {
                idOfEnterEvent = 0L;
            }
            if (idOfOutcomeMigrRequests == null) {
                idOfOutcomeMigrRequests = 0L;
            }
            return new SyncResponse.CorrectingNumbersOrdersRegistry(idOfOrderMax, idOfOrderDetail, idOfEnterEvent,
                    idOfOutcomeMigrRequests);
            //return null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.ResOrgStructure processSyncOrgStructure(Long idOfOrg, SyncRequest.OrgStructure reqStructure,
            SyncHistory syncHistory) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = findOrg(persistenceSession, idOfOrg);
            // Ищем "лишние" группы
            List<ClientGroup> superfluousClientGroups = new ArrayList<ClientGroup>();
            for (ClientGroup clientGroup : organization.getClientGroups()) {
                if (clientGroup.isTemporaryGroup()) {
                    // добавляем временную группу в список для удаления если она уже есть в постоянных
                    if (findClientGroupByName(clientGroup.getGroupName(), reqStructure)) {
                        superfluousClientGroups.add(clientGroup);
                    }
                } else {
                    if (!findClientGroupById(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(),
                            reqStructure)) {
                        superfluousClientGroups.add(clientGroup);
                    }
                }
            }
            // Удаляем "лишние" группы
            for (ClientGroup clientGroup : superfluousClientGroups) {
                // Отсоединяем группу от организации
                organization.removeClientGroup(clientGroup);
                // Удаляем из группы всех ее клиентов
                for (Client client : clientGroup.getClients()) {
                    client.setIdOfClientGroup(null);
                    client.setClientGroup(null);
                    client.setUpdateTime(new Date());
                    persistenceSession.update(client);
                }
                // Удаляем группу из БД
                persistenceSession.delete(clientGroup);
            }
            // Добавляем и обновляем группы согласно запроса
            for (SyncRequest.OrgStructure.Group reqGroup : reqStructure.getGroups()) {
                try {
                    processSyncOrgStructureGroup(persistenceSession, organization, reqGroup);
                } catch (Exception e) {
                    String message = String.format("Failed to process: %s", reqGroup);
                    logger.error(message, e);
                    return new SyncResponse.ResOrgStructure(2, message);
                }
            }

            //persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return new SyncResponse.ResOrgStructure();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static boolean findClientGroupById(long idOfClientGroup, SyncRequest.OrgStructure reqStructure)
            throws Exception {
        for (SyncRequest.OrgStructure.Group group : reqStructure.getGroups()) {
            if (idOfClientGroup == group.getIdOfGroup()) {
                return true;
            }
        }
        return false;
    }

    private static boolean findClientGroupByName(String groupName, SyncRequest.OrgStructure reqStructure)
            throws Exception {
        for (SyncRequest.OrgStructure.Group group : reqStructure.getGroups()) {
            if (groupName.equals(group.getName())) {
                return true;
            }
        }
        return false;
    }

    private void processSyncOrgStructureGroup(Session persistenceSession, Org organization,
            SyncRequest.OrgStructure.Group reqGroup) throws Exception {
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(organization.getIdOfOrg(),
                reqGroup.getIdOfGroup());
        ClientGroup clientGroup = findClientGroup(persistenceSession, compositeIdOfClientGroup);
        if (null != clientGroup) {
            if (!StringUtils.equals(reqGroup.getName(), clientGroup.getGroupName())) {
                clientGroup.setGroupName(reqGroup.getName());
                persistenceSession.update(clientGroup);
            }
        } else {
            clientGroup = new ClientGroup(compositeIdOfClientGroup, reqGroup.getName());
            persistenceSession.save(clientGroup);
        }

        /* не нужный код - т.к. список клиентов всегда полный, из за него при регистрации с клиента сбрасывался класс после синхронизации
        // Ищем "лишних" клиентов
        List<Client> superfluousClients = new ArrayList<Client>();
        for (Client client : clientGroup.getClients()) {
            if (!find(client, reqGroup)) {
                superfluousClients.add(client);
            }
        }
        // Убираем из группы "лишних" клиентов
        for (Client client : superfluousClients) {
            client.setIdOfClientGroup(null);
            client.setUpdateTime(new Date());
            persistenceSession.update(client);
            clientGroup.removeClient(client);
        }*/

        // Добавляем в группу клиентов согласно запросу
        for (Long idOfClient : reqGroup.getClients()) {
            Long idOfClientGroup = getClientGroup(persistenceSession, idOfClient, organization.getIdOfOrg());
            if (idOfClientGroup != null && (idOfClientGroup.longValue() == clientGroup.getCompositeIdOfClientGroup()
                    .getIdOfClientGroup().longValue())) {
                continue;
            }
            ////
            Client client = findClient(persistenceSession, idOfClient);
            Set<Long> idOfFriendlyOrgSet = getIdOfFriendlyOrg(persistenceSession, client.getOrg().getIdOfOrg());
            if (null == client) {
                logger.info(String.format("Client with IdOfClient == %s not found", idOfClient));
            } else if (!client.getOrg().getIdOfOrg().equals(organization.getIdOfOrg()) && !idOfFriendlyOrgSet
                    .contains(client.getOrg().getIdOfOrg())) {
                logger.error(String.format(
                        "Client with IdOfClient == %s belongs to other organization. Client: %s, IdOfOrg by request: %s",
                        idOfClient, client, organization.getIdOfOrg()));
            } else if (client.getOrg().getIdOfOrg().equals(organization.getIdOfOrg())) {
                //ClientGroup curClientGroup = client.getClientGroup();
                //if (null == curClientGroup || !curClientGroup.getCompositeIdOfClientGroup()
                //        .equals(clientGroup.getCompositeIdOfClientGroup())) {
                client.setClientGroup(clientGroup);
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                client.setUpdateTime(new Date());
                //clientGroup.addClient(client);
                persistenceSession.update(client);
                //persistenceSession.update(clientGroup);
                //}
            }
        }
    }

    private static boolean find(Client client, SyncRequest.OrgStructure.Group reqGroup) throws Exception {
        for (Long aLong : reqGroup.getClients()) {
            if (client.getIdOfClient().equals(aLong)) {
                return true;
            }
        }
        return false;
    }

    private void addClientVersionAndRemoteAddressByOrg(Long idOfOrg, String clientVersion, String remoteAddress,
            String sqlServerVersion, Double databaseSize) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            updateClientVersionAndRemoteAddressByOrg(persistenceSession, idOfOrg, clientVersion, remoteAddress,
                    sqlServerVersion, databaseSize);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Can't update ClientVersion, RemoteAddress and sqlServerVersion for ID of Org: " + idOfOrg, e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public SyncHistory createSyncHistory(Long idOfOrg, Long idOfPacket, Date startTime, String clientVersion,
            String remoteAddress, Integer syncType) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, idOfPacket, clientVersion, remoteAddress,
                    syncType);
            persistenceSession.save(syncHistory);
            //Long idOfSync = syncHistory.getIdOfSync();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return syncHistory;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void updateSyncHistory(Long idOfSync, int syncResult, Date endTime) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            SyncHistory syncHistory = getSyncHistoryReference(persistenceSession, idOfSync);
            syncHistory.setSyncEndTime(endTime);
            syncHistory.setSyncResult(syncResult);
            persistenceSession.update(syncHistory);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.AccRegistry getAccRegistry(Long idOfOrg, List<Long> clientIds, String clientVerision)
            throws Exception {
        if (SyncRequest.versionIsAfter(clientVerision, "2.7")) {
            return null;
        }
        SyncResponse.AccRegistry accRegistry = new SyncResponse.AccRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> idOfOrgSet = new HashSet<Long>();

            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            Set<Org> orgSet = org.getFriendlyOrg();
            /* совместимость организаций которые не имеют дружественных организаций */
            for (Org o : orgSet) {
                idOfOrgSet.add(o.getIdOfOrg());
            }
            idOfOrgSet.add(idOfOrg);
            List<Card> cards = getClientsAndCardsForOrgs(persistenceSession, idOfOrgSet, clientIds);
            for (Card card : cards) {
                Client client = card.getClient();
                accRegistry.addItem(new SyncResponse.AccRegistry.Item(card));
            }
            // Добавляем карты перемещенных клиентов.
            if (clientIds == null || clientIds.isEmpty()) {
                List<Client> allocClients = ClientManager.findAllAllocatedClients(persistenceSession, org);
                for (Client client : allocClients) {
                    for (Card card : client.getCards()) {
                        accRegistry.addItem(new SyncResponse.AccRegistry.Item(client, card));
                    }
                }
            }

            // Добавляем карты временных посетителей (мигрантов)
            List<Client> migrantClients = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, org.getIdOfOrg());
            for (Client client : migrantClients) {
                for (Card card : client.getCards()) {
                    accRegistry.addItem(new SyncResponse.AccRegistry.Item(client, card));
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistry;
    }

    private SyncResponse.AccRegistry getAccRegistryForMigrants(Long idOfOrg) throws Exception {
        SyncResponse.AccRegistry accRegistry = new SyncResponse.AccRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);

            // Добавляем карты временных посетителей (мигрантов)
            List<Client> migrantClients = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, org.getIdOfOrg());
            for (Client client : migrantClients) {
                for (Card card : client.getCards()) {
                    accRegistry.addItem(new SyncResponse.AccRegistry.Item(client, card));
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistry;
    }

    private AccRegistryUpdate getAccRegistryUpdate(Org org, Date fromDateTime) throws Exception {
        AccRegistryUpdate accRegistryUpdate = new AccRegistryUpdate();
        final Date currentDate = new Date();
        List<AccountTransactionExtended> accountTransactionList = null;
        try {
            Date dateStartDate = getQueryStartDate(org.getIdOfOrg(), fromDateTime);
            long time_delta = System.currentTimeMillis();
            accountTransactionList = DAOReadonlyService.getInstance()
                    .getAccountTransactionsForOrgSinceTimeV2(org, dateStartDate, currentDate);
            time_delta = System.currentTimeMillis() - time_delta;
            if (time_delta > 10L * 1000L) {
                logger.error(String.format(
                        "Transactions query time = %s ms. IdOfOrg = %s. Period = %3$td.%3$tm.%3$tY %3$tT - %4$td.%4$tm.%4$tY %4$tT (date from packet = %5$td.%5$tm.%5$tY %5$tT)",
                        time_delta, org.getIdOfOrg(), dateStartDate, currentDate, fromDateTime));
            }
            for (AccountTransactionExtended accountTransaction : accountTransactionList) {
                accRegistryUpdate.addAccountTransactionInfoV2(accountTransaction);
            }
        } catch (Exception e) {
            logger.error("AccRegistryUpdate section failed", e);
            accRegistryUpdate.setResult(new ResultOperation(500, e.getMessage()));
        }
        return accRegistryUpdate;
    }

    private Date getQueryStartDate(Long idOfOrg, Date fromDateTime) {
        long difference = System.currentTimeMillis() - fromDateTime.getTime();
        if (difference <= ACC_REGISTRY_TIME_CLIENT_IN_MILLIS) {
            return fromDateTime;
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Date d = processorUtils
                    .getLastProcessSectionDate(persistenceSession, idOfOrg, SectionType.ACC_INC_REGISTRY);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return d == null ? fromDateTime : d;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.AccIncRegistry getAccIncRegistry(Org org, Date fromDateTime) throws Exception {
        SyncResponse.AccIncRegistry accIncRegistry = new SyncResponse.AccIncRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date currentDate = new Date();
            List<Integer> transactionSourceTypes = Arrays
                    .asList(AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE,
                            AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE,
                            AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE,
                            AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE);
            persistenceSession.refresh(org);
            List<AccountTransaction> accountTransactionList = getAccountTransactionsForOrgSinceTime(persistenceSession,
                    org, fromDateTime, currentDate, transactionSourceTypes);
            for (AccountTransaction accountTransaction : accountTransactionList) {
                SyncResponse.AccIncRegistry.Item accIncItem = new SyncResponse.AccIncRegistry.Item(
                        accountTransaction.getIdOfTransaction(), accountTransaction.getClient().getIdOfClient(),
                        accountTransaction.getTransactionTime(), accountTransaction.getTransactionSum(),
                        accountTransaction.getTransactionSubBalance1Sum());
                accIncRegistry.addItem(accIncItem);
            }
            accIncRegistry.setDate(currentDate);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accIncRegistry;
    }

    private SyncResponse.ClientRegistry processSyncClientRegistry(Long idOfOrg,
            SyncRequest.ClientRegistryRequest clientRegistryRequest, List<Long> errorClientIds,
            List<Long> clientsWithWrongVersion) throws Exception {
        SyncResponse.ClientRegistry clientRegistry = new SyncResponse.ClientRegistry();
        List<Client> clients;
        Org organization;
        List<Org> orgList;
        List<Long> activeClientsId;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            organization = getOrgReference(persistenceSession, idOfOrg);
            orgList = new ArrayList<Org>(organization.getFriendlyOrg());
            orgList.add(organization);
            clients = findNewerClients(persistenceSession, orgList, clientRegistryRequest.getCurrentVersion());

            // Добавляем временных посетителей (мигрантов)
            List<Client> migrants = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, idOfOrg);
            clients.addAll(migrants);

            for (Long idOfClientWithWrongVersion : clientsWithWrongVersion) {
                Client c = (Client) persistenceSession.load(Client.class, idOfClientWithWrongVersion);
                if (!clients.contains(c)) {
                    clients.add(c);
                }
            }

            for (Client client : clients) {
                if (client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
                } else {
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 1));
                }
            }
            activeClientsId = findActiveClientsId(persistenceSession, orgList);
            // Получаем чужих клиентов.
            Map<String, Set<Client>> alienClients = ClientManager
                    .findAllocatedClients(persistenceSession, organization);
            for (Map.Entry<String, Set<Client>> entry : alienClients.entrySet()) {
                boolean isTempClient = entry.getKey().equals("TemporaryClients");
                for (Client cl : entry.getValue()) {
                    if (cl.getClientRegistryVersion() > clientRegistryRequest.getCurrentVersion()) {
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(cl, 2, isTempClient));
                    }
                    activeClientsId.add(cl.getIdOfClient());
                }
            }

            // Добавляем временных посетителей (мигрантов)
            for (Client migrant : migrants) {
                activeClientsId.add(migrant.getIdOfClient());
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            // "при отличии количества активных клиентов в базе админки от клиентов, которые должны быть у данной организации
            // с учетом дружественных и правил - выдаем список идентификаторов всех клиентов в отдельном теге"
            if (clientRegistryRequest.getCurrentCount() != null && activeClientsId.size() != clientRegistryRequest
                    .getCurrentCount()) {
                for (Long id : activeClientsId) {
                    clientRegistry.addActiveClientId(id);
                }
            }
            if (!errorClientIds.isEmpty()) {
                List errorClients = fetchErrorClientsWithOutFriendlyOrg(persistenceSession,
                        organization.getFriendlyOrg(), errorClientIds);
                ClientGroup clientGroup = findClientGroupByGroupNameAndIdOfOrg(persistenceSession,
                        organization.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                // Есть возможность отсутсвия даной группы
                if (clientGroup == null) {
                    clientGroup = createClientGroup(persistenceSession, organization.getIdOfOrg(),
                            ClientGroup.Predefined.CLIENT_LEAVING);
                }
                for (Object object : errorClients) {
                    Client client = (Client) object;
                    client.setClientGroup(clientGroup);
                    if (client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
                    } else {
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 1));
                    }
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientRegistry;
    }

    private SyncResponse.ClientRegistry processSyncClientRegistryForMigrants(Long idOfOrg) throws Exception {
        SyncResponse.ClientRegistry clientRegistry = new SyncResponse.ClientRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Client> clients = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, idOfOrg);

            for (Client client : clients) {
                clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientRegistry;
    }

    private OrganizationStructure getOrganizationStructureData(Org org, long version, boolean isAllOrgs)
            throws Exception {
        OrganizationStructure organizationStructure = new OrganizationStructure();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<Org> list;
            if (isAllOrgs) {
                list = DAOUtils.getOrgsSinceVersion(persistenceSession, version);
            } else {
                list = DAOUtils.findAllFriendlyOrgs(persistenceSession, org.getIdOfOrg());
            }
            organizationStructure.addOrganizationStructureInfo(persistenceSession, org, list, isAllOrgs);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return organizationStructure;
    }

    private InfoMessageData getInfoMessageData(Org org, long version) throws Exception {
        InfoMessageData infoMessageData = new InfoMessageData(null);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<InfoMessage> list = DAOUtils
                    .getInfoMessagesSinceVersion(persistenceSession, org.getIdOfOrg(), version);
            List<InfoMessageItem> infoMessageItems = new ArrayList<InfoMessageItem>();
            for (InfoMessage message : list) {
                infoMessageItems.add(new InfoMessageItem(message));
                DAOUtils.setSendDateInfoMessage(persistenceSession, message.getIdOfInfoMessage(), org.getIdOfOrg());
            }
            infoMessageData.setInfoMessageItems(infoMessageItems);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return infoMessageData;
    }

    private OrganizationComplexesStructure getOrganizationComplexesStructureData(Org org, Long maxVersion,
            Integer menuSyncCountDays, Integer menuSyncCountDaysInPast) throws Exception {
        OrganizationComplexesStructure organizationComplexesStructure = new OrganizationComplexesStructure();
        Session session = null;
        Transaction transaction = null;
        try {
            session = persistenceSessionFactory.openSession();
            transaction = session.beginTransaction();
            organizationComplexesStructure
                    .fillComplexesStructureAndApplyChanges(session, org.getIdOfOrg(), maxVersion, menuSyncCountDays,
                            menuSyncCountDaysInPast);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return organizationComplexesStructure;
    }

    private ProcessGroupsOrganizationData processGroupsOrganizationData(GroupsOrganizationRequest sectionRequest)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ProcessGroupsOrganizationData result = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new GroupsOrganizationDataProcessor(persistenceSession, sectionRequest);
            result = (ProcessGroupsOrganizationData) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private ResProcessGroupsOrganization processResGroupsOrganization(GroupsOrganizationRequest sectionRequest)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResProcessGroupsOrganization result = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ResGroupsOrganizationProcessor(persistenceSession, sectionRequest);
            result = (ResProcessGroupsOrganization) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }


    private ProhibitionsMenu getProhibitionsMenuData(Org org, long version) throws Exception {
        ProhibitionsMenu prohibitionsMenu = new ProhibitionsMenu();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<ProhibitionMenu> prohibitionMenuList;
            prohibitionMenuList = getProhibitionMenuForOrgSinceVersion(persistenceSession, org, version);
            for (ProhibitionMenu prohibitionMenu : prohibitionMenuList) {
                prohibitionsMenu.addProhibitionMenuInfo(prohibitionMenu);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return prohibitionsMenu;
    }

    private void processSyncMenu(Long idOfOrg, SyncRequest.ReqMenu reqMenu) throws Exception {
        if (null != reqMenu) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceSession.setFlushMode(FlushMode.COMMIT);

                Org organization = getOrgReference(persistenceSession, idOfOrg);

                boolean bOrgIsMenuExchangeSource = DAOUtils.isOrgMenuExchangeSource(persistenceSession, idOfOrg);

                /// сохраняем секцию Settings
                if (bOrgIsMenuExchangeSource && (reqMenu.getSettingsSectionRawXML() != null)) {
                    persistenceTransaction = persistenceSession.beginTransaction();

                    MenuExchange menuExchangeSettings = new MenuExchange(new Date(0), idOfOrg,
                            reqMenu.getSettingsSectionRawXML(), MenuExchange.FLAG_SETTINGS);
                    persistenceSession.saveOrUpdate(menuExchangeSettings);

                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }

                Iterator<SyncRequest.ReqMenu.Item> menuItems = reqMenu.getItems();
                boolean bFirstMenuItem = true;
                while (menuItems.hasNext()) {
                    //  Открываем тразнакцию для каждого дня
                    persistenceTransaction = persistenceSession.beginTransaction();

                    SyncRequest.ReqMenu.Item item = menuItems.next();
                    /// сохраняем данные меню для распространения
                    if (bOrgIsMenuExchangeSource) {
                        MenuExchange menuExchange = new MenuExchange(item.getDate(), idOfOrg, item.getRawXmlText(),
                                bFirstMenuItem ? MenuExchange.FLAG_ANCHOR_MENU : MenuExchange.FLAG_NONE);
                        //Удаление объекта из сессии с одинаковым идентификатором
                        MenuExchange menuExchangeFromSession = (MenuExchange) persistenceSession.get(MenuExchange.class, menuExchange.getCompositeIdOfMenuExchange());
                        if(menuExchangeFromSession != null) {
                            persistenceSession.evict(menuExchangeFromSession);
                        }
                        persistenceSession.saveOrUpdate(menuExchange);
                    }
                    ///
                    Date menuDate = item.getDate();
                    ////
                    Menu menu = findMenu(persistenceSession, organization, Menu.ORG_MENU_SOURCE, menuDate);
                    Integer detailsHashCode = null;
                    // Подсчитываем хеш-код входных данных
                    final int detailsHashCode1 = item.hashCode();
                    if (null == menu) {
                        // Если меню не найдено то создаем
                        menu = new Menu(organization, menuDate, new Date(), Menu.ORG_MENU_SOURCE,
                                bFirstMenuItem ? Menu.FLAG_ANCHOR_MENU : Menu.FLAG_NONE, detailsHashCode1);
                        persistenceSession.save(menu);
                    } else {
                        // если меню найдено смотрим его хеш-код
                        detailsHashCode = menu.getDetailsHashCode();
                        // обновляем занчение хеша в случае если оно пусто (меню возможно уже есть но не имееет хеша)
                        // или в случае если меню изменилось

                        if (detailsHashCode == null || !detailsHashCode.equals(detailsHashCode1)) {
                            //menu.setDetailsHashCode(detailsHashCode1);
                            //persistenceSession.persist(menu);
                            String sql = "update Menu set detailsHashCode=:detailsHashCode where org=:org and menuSource=:menuSource and menuDate=:menuDate";
                            Query query = persistenceSession.createQuery(sql);
                            query.setParameter("detailsHashCode", detailsHashCode1);
                            query.setParameter("org", organization);
                            query.setParameter("menuSource", Menu.ORG_MENU_SOURCE);
                            query.setParameter("menuDate", menuDate);
                            query.executeUpdate();
                        }
                    }
                    // проверяем спомощью хеш-кода изменилось ли меню, в случае если изменилось то перезаписываем
                    if (detailsHashCode == null || !detailsHashCode.equals(detailsHashCode1)) {
                        /*List<PreorderComplex> preorderComplexes = DAOUtils.getPreorderComplexesForOrgByPeriod(persistenceSession,
                                idOfOrg, CalendarUtils.startOfDay(menuDate), CalendarUtils.endOfDay(menuDate)); */
                        processReqAssortment(persistenceSession, organization, menuDate, item.getReqAssortments());
                        HashMap<Long, MenuDetail> localIdsToMenuDetailMap = new HashMap<Long, MenuDetail>();
                        processReqMenuDetails(persistenceSession, menu, item, item.getReqMenuDetails(),
                                localIdsToMenuDetailMap);
                        processReqComplexInfos(persistenceSession, organization, menuDate, menu,
                                item.getReqComplexInfos(), localIdsToMenuDetailMap);

                    }

                    bFirstMenuItem = false;
                    //  Подтверждаем транзакцию для каждого дня
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }

            } catch (Exception e) {
                logger.error("Error menu.", e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    private void processReqComplexInfos(Session persistenceSession, Org organization, Date menuDate, Menu menu,
            List<SyncRequest.ReqMenu.Item.ReqComplexInfo> reqComplexInfos,
            HashMap<Long, MenuDetail> localIdsToMenuDetailMap) throws Exception {
        deleteComplexInfoForDate(persistenceSession, organization, menuDate);

        for (SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfo : reqComplexInfos) {
            ComplexInfo complexInfo = new ComplexInfo(reqComplexInfo.getComplexId(), organization, menuDate,
                    reqComplexInfo.getModeFree(), reqComplexInfo.getModeGrant(), reqComplexInfo.getModeOfAdd(),
                    reqComplexInfo.getComplexMenuName());
            Integer useTrDiscount = reqComplexInfo.getUseTrDiscount();
            Long currentPrice = reqComplexInfo.getCurrentPrice();
            Integer modeVisible = reqComplexInfo.getModeVisible();
            String goodsGuid = reqComplexInfo.getGoodsGuid();
            if (useTrDiscount != null) {
                complexInfo.setUseTrDiscount(useTrDiscount);
            }
            if (currentPrice != null) {
                complexInfo.setCurrentPrice(currentPrice);
            }
            if (modeVisible != null) {
                complexInfo.setModeVisible(modeVisible);
            }
            if (goodsGuid != null) {
                Good good = findGoodByGuid(persistenceSession, goodsGuid);
                complexInfo.setGood(good);
            }
            Integer usedSubscriptionFeeding = reqComplexInfo.getUsedSubscriptionFeeding();
            if (usedSubscriptionFeeding != null) {
                complexInfo.setUsedSubscriptionFeeding(usedSubscriptionFeeding);
            }
            Integer usedVariableFeeding = reqComplexInfo.getUsedVariableFeeding();
            if (usedVariableFeeding != null) {
                complexInfo.setUsedVariableFeeding(usedVariableFeeding);
            }
            Integer rootComplex = reqComplexInfo.getRootComplex();
            if (rootComplex != null) {
                complexInfo.setRootComplex(rootComplex);
            }
            Integer usedSpecialMenu = reqComplexInfo.getUsedSpecialMenu();
            if (usedSpecialMenu != null) {
                complexInfo.setUsedSpecialMenu(usedSpecialMenu);
            }
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqComplexInfo.getReqMenuDetail();
            if (reqMenuDetail != null) {
                MenuDetail menuDetailOptional = findMenuDetailByLocalId(persistenceSession, menu,
                        reqComplexInfo.getReqMenuDetail().getIdOfMenu());
                if (menuDetailOptional != null) {
                    complexInfo.setMenuDetail(menuDetailOptional);
                }
            }

            SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDiscountDetail reqComplexInfoDiscountDetail = reqComplexInfo
                    .getComplexInfoDiscountDetail();
            if (reqComplexInfoDiscountDetail != null) {
                double size = reqComplexInfoDiscountDetail.getSize();
                int isAllGroups = reqComplexInfoDiscountDetail.getIsAllGroups();
                Integer maxCount = reqComplexInfoDiscountDetail.getMaxCount();
                Long idOfClientGroup = reqComplexInfoDiscountDetail.getIdOfClientGroup();
                ComplexInfoDiscountDetail complexInfoDiscountDetail = new ComplexInfoDiscountDetail(size, isAllGroups);
                if (idOfClientGroup != null) {
                    CompositeIdOfClientGroup compId = new CompositeIdOfClientGroup(organization.getIdOfOrg(),
                            idOfClientGroup);
                    ClientGroup clientGroup = findClientGroup(persistenceSession, compId);
                    complexInfoDiscountDetail.setClientGroup(clientGroup);
                    complexInfoDiscountDetail.setOrg(clientGroup.getOrg());
                }
                if (maxCount != null) {
                    complexInfoDiscountDetail.setMaxCount(maxCount);
                }

                persistenceSession.save(complexInfoDiscountDetail);

                complexInfo.setDiscountDetail(complexInfoDiscountDetail);
            }
            persistenceSession.save(complexInfo);
            for (SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDetail reqComplexInfoDetail : reqComplexInfo
                    .getComplexInfoDetails()) {
                MenuDetail menuDetail = localIdsToMenuDetailMap
                        .get(reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
                if (menuDetail == null) {
                    throw new Exception(
                            "MenuDetail not found for complex detail with localIdOfMenu=" + reqComplexInfoDetail
                                    .getReqMenuDetail().getIdOfMenu());
                }
                menuDetail = (MenuDetail) persistenceSession.get(MenuDetail.class, menuDetail.getIdOfMenuDetail());
                ComplexInfoDetail complexInfoDetail = new ComplexInfoDetail(complexInfo, menuDetail);
                Long idOfItem = reqComplexInfoDetail.getIdOfItem();
                if (idOfItem != null) {
                    complexInfoDetail.setIdOfItem(idOfItem);
                }
                Integer menuItemCount = reqComplexInfoDetail.getCount();
                if (menuItemCount != null) {
                    complexInfoDetail.setCount(menuItemCount);
                }
                persistenceSession.save(complexInfoDetail);
            }
        }
    }

    private SyncRequest.ReqMenu.Item.ReqComplexInfo findReqComplexInfo(SyncRequest.ReqMenu.Item item,
            Integer armComplexId) {
        SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfoMatch = null;
        for (SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfo : item.getReqComplexInfos()) {
            if (armComplexId.equals(reqComplexInfo.getComplexId())) {
                reqComplexInfoMatch = reqComplexInfo;
                break;
            }
        }
        if (reqComplexInfoMatch != null && (reqComplexInfoMatch.getComplexInfoDetails() == null
                || reqComplexInfoMatch.getComplexInfoDetails().size() == 0)) {
            reqComplexInfoMatch = null;
        }
        return reqComplexInfoMatch;
    }

    private boolean equalsNullSafe(String str1, String str2) {
        return ((str1 == null ? "" : str1).equals(str2 == null ? "" : str2));
    }

    private void processReqAssortment(Session persistenceSession, Org organization, Date menuDate,
            List<SyncRequest.ReqMenu.Item.ReqAssortment> reqAssortments) {
        deleteAssortmentForDate(persistenceSession, organization, menuDate);
        for (SyncRequest.ReqMenu.Item.ReqAssortment reqAssortment : reqAssortments) {
            Assortment assortment = new Assortment(organization, menuDate, reqAssortment.getName(),
                    reqAssortment.getFullName(), reqAssortment.getGroup(), reqAssortment.getMenuOrigin(),
                    reqAssortment.getMenuOutput(), reqAssortment.getPrice(), reqAssortment.getFat(),
                    reqAssortment.getCarbohydrates(), reqAssortment.getCalories(), reqAssortment.getVitB1(),
                    reqAssortment.getVitC(), reqAssortment.getVitA(), reqAssortment.getVitE(), reqAssortment.getMinCa(),
                    reqAssortment.getMinP(), reqAssortment.getMinMg(), reqAssortment.getMinFe());
            persistenceSession.save(assortment);
        }
    }

    private void processReqMenuDetails(Session persistenceSession, Menu menu, SyncRequest.ReqMenu.Item item,
            Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails,
            HashMap<Long, MenuDetail> localIdsToMenuDetailMap) throws Exception {
        // Ищем "лишние" элементы меню
        List<MenuDetail> superfluousMenuDetails = new LinkedList<MenuDetail>();
        for (MenuDetail menuDetail : menu.getMenuDetails()) {
            if (!find(menuDetail, item)) {
                superfluousMenuDetails.add(menuDetail);
            }
        }
        // Удаляем "лишние" элементы меню
        for (MenuDetail menuDetail : superfluousMenuDetails) {
            menu.removeMenuDetail(menuDetail);

            String deldetSql = "delete ComplexInfoDetail  where menuDetail=:md";
            Query query = persistenceSession.createQuery(deldetSql);
            query.setParameter("md", menuDetail);
            query.executeUpdate();

            String delSql = "delete ComplexInfo  where menuDetail=:md";
            query = persistenceSession.createQuery(delSql);
            query.setParameter("md", menuDetail);
            query.executeUpdate();

            String updSql = "update GoodBasicBasketPrice b set b.menuDetail=null where b.menuDetail=:md";
            query = persistenceSession.createQuery(updSql);
            query.setParameter("md", menuDetail);
            query.executeUpdate();


            persistenceSession.delete(menuDetail);
        }

        // Добавляем новые элементы из пришедшего меню
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
            boolean exists = false;

            for (MenuDetail menuDetail : menu.getMenuDetails()) {
                exists = areMenuDetailsEqual(menuDetail, reqMenuDetail);
                if (exists) {
                    if (reqMenuDetail.getgBasket() != null) {
                        linkBasket(persistenceSession, menuDetail, reqMenuDetail.getgBasket(),
                                menu.getOrg().getIdOfOrg());
                    }
                    localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), menuDetail);
                    break;
                }
            }

            if (!exists) {
                MenuDetail newMenuDetail = new MenuDetail(menu, reqMenuDetail.getPath(), reqMenuDetail.getName(),
                        reqMenuDetail.getMenuOrigin(), reqMenuDetail.getAvailableNow(), reqMenuDetail.getFlags());
                newMenuDetail.setLocalIdOfMenu(reqMenuDetail.getIdOfMenu());
                newMenuDetail.setGroupName(reqMenuDetail.getGroup() == null ? ""
                        : reqMenuDetail.getGroup()); // в старых версиях клиента могут быть без группы
                newMenuDetail.setMenuDetailOutput(reqMenuDetail.getOutput());
                newMenuDetail.setPrice(reqMenuDetail.getPrice());
                newMenuDetail.setPriority(reqMenuDetail.getPriority());
                newMenuDetail.setProtein(reqMenuDetail.getProtein());
                newMenuDetail.setFat(reqMenuDetail.getFat());
                newMenuDetail.setCarbohydrates(reqMenuDetail.getCarbohydrates());
                newMenuDetail.setCalories(reqMenuDetail.getCalories());
                newMenuDetail.setVitB1(reqMenuDetail.getVitB1());
                newMenuDetail.setVitC(reqMenuDetail.getVitC());
                newMenuDetail.setVitA(reqMenuDetail.getVitA());
                newMenuDetail.setVitE(reqMenuDetail.getVitE());
                newMenuDetail.setMinCa(reqMenuDetail.getMinCa());
                newMenuDetail.setMinP(reqMenuDetail.getMinP());
                newMenuDetail.setMinMg(reqMenuDetail.getMinMg());
                newMenuDetail.setMinFe(reqMenuDetail.getMinFe());
                newMenuDetail.setVitB2(reqMenuDetail.getVitB2());
                newMenuDetail.setVitPp(reqMenuDetail.getVitPp());
                newMenuDetail.setIdOfMenuFromSync(reqMenuDetail.getIdOfMenu());
                newMenuDetail.setShortName(reqMenuDetail.getShortName());
                newMenuDetail.setIdOfGood(reqMenuDetail.getIdOfGood());
                newMenuDetail.setItemCode(reqMenuDetail.getItemCode());

                persistenceSession.save(newMenuDetail);
                menu.addMenuDetail(newMenuDetail);

                if (reqMenuDetail.getgBasket() != null) {
                    linkBasket(persistenceSession, newMenuDetail, reqMenuDetail.getgBasket(),
                            menu.getOrg().getIdOfOrg());
                }

                localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), newMenuDetail);
            }
            //Заполнение новой таблицы cf_good_bb_menu_price
            saveBasicBasketPriceHistoryByMenu(persistenceSession, menu, reqMenuDetail);
        }
    }

    private void linkBasket(Session session, MenuDetail menuDetail, String guidBasket, Long idOfOrg) {
        GoodsBasicBasket basicBasket = DAOUtils.findBasicGood(session, guidBasket);
        if (basicBasket != null) {
            List<GoodBasicBasketPrice> basicBasketPriceList = DAOUtils
                    .findGoodBasicBasketPrice(session, basicBasket, idOfOrg);
            for (GoodBasicBasketPrice basicBasketPrice : basicBasketPriceList) {
                basicBasketPrice.setMenuDetail(menuDetail);
                basicBasketPrice.setPrice(menuDetail.getPrice());
                basicBasketPrice.setLastUpdate(new Date());
                GoodBasicBasketPrice.save(session, basicBasketPrice);
            }
        }
    }

    private void saveBasicBasketPriceHistoryByMenu(Session session, Menu menu,
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail) {
        try {
            if (reqMenuDetail.getgBasket() != null) {
                GoodsBasicBasket basicBasket = DAOUtils.findBasicGood(session, reqMenuDetail.getgBasket());
                if (basicBasket != null) {
                    Long idOfConfigurationProvider = DAOUtils
                            .getOrgConfigurationProvider(session, menu.getOrg().getIdOfOrg());
                    if (idOfConfigurationProvider > 0) {
                        GoodBBMenuPrice goodBBMenuPrice = DAOUtils
                                .findBBMenuPrice(session, basicBasket.getIdOfBasicGood(), idOfConfigurationProvider,
                                        menu.getMenuDate(), reqMenuDetail.getName());
                        if (goodBBMenuPrice == null) {
                            goodBBMenuPrice = new GoodBBMenuPrice();
                            goodBBMenuPrice.setIdOfBasicGood(basicBasket.getIdOfBasicGood());
                            goodBBMenuPrice.setIdOfConfigurationProvider(idOfConfigurationProvider);
                            goodBBMenuPrice.setMenuDate(menu.getMenuDate());
                            goodBBMenuPrice.setPrice(reqMenuDetail.getPrice());
                            goodBBMenuPrice.setMenuDetailName(reqMenuDetail.getName());
                            session.save(goodBBMenuPrice);
                        } else {
                            goodBBMenuPrice.setPrice(reqMenuDetail.getPrice());
                            session.update(goodBBMenuPrice);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in save basic basket price history: ", e);
        }
    }

    private static boolean areMenuDetailsEqual(MenuDetail menuDetail,
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail) {
        return SyncRequest.ReqMenu.Item.ReqMenuDetail.areMenuDetailsEqual(menuDetail, reqMenuDetail);
        /*return reqMenuDetail.getPath().equals(menuDetail.getMenuPath())
                && (reqMenuDetail.getPrice() == null || menuDetail.getPrice() == null
                        || reqMenuDetail.getPrice().longValue() == menuDetail.getPrice().longValue())
                && (reqMenuDetail.getGroup() == null || menuDetail.getGroupName() == null
                        || reqMenuDetail.getGroup().equals(menuDetail.getGroupName()))
                && (reqMenuDetail.getOutput() == null || menuDetail.getMenuDetailOutput() == null
                        || reqMenuDetail.getOutput().equals(menuDetail.getMenuDetailOutput()))
                && (reqMenuDetail.getName().equals(menuDetail.getMenuDetailName()));*/
    }

    private static boolean find(MenuDetail menuDetail, SyncRequest.ReqMenu.Item menuItem) throws Exception {
        Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails = menuItem.getReqMenuDetails();
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
            if (areMenuDetailsEqual(menuDetail, reqMenuDetail)) {
                return true;
            }
        }
        return false;
    }

    private SyncResponse.ResMenuExchangeData getMenuExchangeData(Long idOfOrg, Date startDate, Date endDate)
            throws Exception {
        startDate = CalendarUtils.truncateToDayOfMonth(startDate);
        SyncResponse.ResMenuExchangeData resMenuExData = new SyncResponse.ResMenuExchangeData();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfSourceOrg = findMenuExchangeSourceOrg(persistenceSession, idOfOrg);

            if (idOfSourceOrg != null) {
                List<MenuExchange> menuExchangeList = findMenuExchangeDataBetweenDatesIncludingSettings(
                        persistenceSession, idOfSourceOrg, toDate(startDate), toDate(endDate));
                boolean hasAnchorMenu = false;
                for (MenuExchange menuExchange : menuExchangeList) {
                    if ((menuExchange.getFlags() & MenuExchange.FLAG_ANCHOR_MENU) != 0) {
                        hasAnchorMenu = true;
                        break;
                    }
                }
                /// если в период выборки не попало корневое меню, то ищем его на предыдущие даты
                if (!hasAnchorMenu) {
                    MenuExchange anchorMenu = findMenuExchangeBeforeDateByEqFlag(persistenceSession, idOfSourceOrg,
                            toDate(startDate), MenuExchange.FLAG_ANCHOR_MENU);
                    if (anchorMenu != null) {
                        resMenuExData.addItem(anchorMenu.getMenuDataWithDecompress());
                    }
                }

                for (MenuExchange m : menuExchangeList) {
                    resMenuExData.addItem(m.getMenuDataWithDecompress());
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMenuExData;
    }

    private Date toDate(Date startDate) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(startDate);
        return new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).getTime();
    }

    private SyncResponse.ResDiary processSyncDiary(Long idOfOrg, SyncRequest.ReqDiary reqDiary) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            // Ищем "лишние" предметы
            List<DiaryClass> superfluousDiaryClasses = new LinkedList<DiaryClass>();
            for (DiaryClass diaryClass : organization.getDiaryClasses()) {
                if (!find(diaryClass, reqDiary)) {
                    superfluousDiaryClasses.add(diaryClass);
                }
            }
            // Удаляем "лишние" предметы
            for (DiaryClass diaryClass : superfluousDiaryClasses) {
                organization.removeDiaryClass(diaryClass);
                persistenceSession.delete(diaryClass);
            }
            // Добавляем и обновляем предметы согласно запросу
            Enumeration<SyncRequest.ReqDiary.ReqDiaryClass> reqDiaryClasses = reqDiary.getReqDiaryClasses();
            while (reqDiaryClasses.hasMoreElements()) {
                processSyncDiaryClass(persistenceSession, organization, reqDiaryClasses.nextElement());
            }
            // Добавляем или обновляем расписания и добавляем оценки
            Enumeration<SyncRequest.ReqDiary.ReqDiaryTimesheet> reqDiaryTimesheets = reqDiary.getReqDiaryTimesheets();
            while (reqDiaryTimesheets.hasMoreElements()) {
                processDiaryTimesheet(persistenceSession, organization, reqDiaryTimesheets.nextElement());
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new SyncResponse.ResDiary();
    }

    private SyncResponse.ResEnterEvents processSyncEnterEvents(EnterEvents enterEvents, Org org, Date syncTime) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResEnterEvents resEnterEvents = new SyncResponse.ResEnterEvents();
        Long idOfOrg;
        Map<String, Long> accessories = new HashMap<String, Long>();
        String mod = getStrMod();
        Date now = getTimeWithBuff();

        for (EnterEventItem e : enterEvents.getEvents()) {
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                // Check enter event existence
                final Long idOfClient = e.getIdOfClient();


                //  Применяем фильтр оборудования
                idOfOrg = accessories.get(e.getTurnstileAddr());
                if (idOfOrg == null) {
                    idOfOrg = org.getIdOfOrg();
                    idOfOrg = DAOService.getInstance()
                            .receiveIdOfOrgByAccessory(idOfOrg, Accessory.GATE_ACCESSORY_TYPE, e.getTurnstileAddr());
                    accessories.put(e.getTurnstileAddr(), idOfOrg);
                }


                if (existEnterEvent(persistenceSession, idOfOrg, e.getIdOfEnterEvent())) {
                    EnterEvent ee = findEnterEvent(persistenceSession,
                            new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), idOfOrg));
                    // Если ENTER событие существует (может быть последний результат синхронизации не был передан клиенту)
                    final boolean checkClient =
                            (ee.getClient() == null && idOfClient == null) || (ee.getClient() != null && ee.getClient()
                                    .getIdOfClient().equals(idOfClient));
                    final boolean checkTempCard = (ee.getIdOfTempCard() == null && e.getIdOfTempCard() == null) || (
                            ee.getIdOfTempCard() != null && ee.getIdOfTempCard().equals(e.getIdOfTempCard()));

                    if (checkClient && ee.getEvtDateTime().equals(e.getEvtDateTime()) && checkTempCard) {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_OK,
                                "Enter event already registered");
                        resEnterEvents.addItem(item);
                    } else {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_OK, String.format(
                                "Enter event already registered but attributes differ, idOfOrg == %d, idOfEnterEvent == %d",
                                idOfOrg, e.getIdOfEnterEvent()));
                        resEnterEvents.addItem(item);
                    }
                } else {
                    // find client by id
                    Client clientFromEnterEvent = null;
                    if (idOfClient != null) {
                        clientFromEnterEvent = (Client) persistenceSession.get(Client.class, idOfClient);
                        if (clientFromEnterEvent == null) {
                            SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                    e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_CLIENT_NOT_FOUND,
                                    String.format("Client not found: %d", idOfClient));
                            resEnterEvents.addItem(item);
                            continue;
                        }
                    }

                    //Обработка события от внешней системы
                    if (e.getPassDirection() == EnterEvent.CHECKED_BY_TEACHER_EXT) {
                        String qstr = "delete from EnterEventManual where idOfClient = :idOfClient and evtDateTime <= :evtDateTime";
                        Query query = persistenceSession.createQuery(qstr);
                        query.setParameter("idOfClient", e.getIdOfClient());
                        query.setParameter("evtDateTime", e.getEvtDateTime());
                        query.executeUpdate();
                    }

                    EnterEvent enterEvent = new EnterEvent();
                    enterEvent.setCompositeIdOfEnterEvent(new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), idOfOrg));
                    enterEvent.setEnterName(e.getEnterName());
                    enterEvent.setTurnstileAddr(e.getTurnstileAddr());
                    enterEvent.setPassDirection(e.getPassDirection());
                    enterEvent.setEventCode(e.getEventCode());
                    enterEvent.setIdOfCard(e.getIdOfCard());
                    enterEvent.setLongCardId(e.getLongCardId());
                    enterEvent.setClient(clientFromEnterEvent);
                    enterEvent.setIdOfTempCard(e.getIdOfTempCard());
                    // Проверка корректности времени
                    if(e.getEvtDateTime().after(now)) {
                        enterEvent.setEvtDateTime(changeTimeByMode(e, mod, now, syncTime));
                    } else {
                        enterEvent.setEvtDateTime(e.getEvtDateTime());
                    }
                    enterEvent.setIdOfVisitor(e.getIdOfVisitor());
                    enterEvent.setVisitorFullName(e.getVisitorFullName());
                    enterEvent.setDocType(e.getDocType());
                    enterEvent.setDocSerialNum(e.getDocSerialNum());
                    enterEvent.setIssueDocDate(e.getIssueDocDate());
                    enterEvent.setVisitDateTime(e.getVisitDateTime());
                    final Long guardianId = e.getGuardianId();
                    enterEvent.setGuardianId(guardianId);
                    enterEvent.setChildPassChecker(e.getChildPassChecker());
                    enterEvent.setChildPassCheckerId(e.getChildPassCheckerId());
                    enterEvent.setIdOfClientGroup(
                            clientFromEnterEvent == null ? null : clientFromEnterEvent.getIdOfClientGroup());
                    persistenceSession.save(enterEvent);

                    Card card = DAOUtils.findCardByCardNoExtended(persistenceSession, e.getIdOfCard(), idOfClient, guardianId, e.getIdOfVisitor());
                    if (card != null) {
                        RuntimeContext.getAppContext().getBean(CardBlockService.class)
                                .saveLastCardActivity(persistenceSession, card.getIdOfCard(), CardActivityType.ENTER_EVENT);
                    }

                    if (RuntimeContext.RegistryType.isSpb() && ScudManager.serviceIsWork) {
                        DAOUtils.createEnterEventsSendInfo(enterEvent, persistenceSession);
                    }
                    if (enterEventOwnerHaveSmartWatch(persistenceSession, enterEvent)) {
                        try {
                            SmartWatchVendorNotificationManager manager = RuntimeContext.getAppContext().getBean(
                                    SmartWatchVendorNotificationManager.class);
                            manager.sendEnterEventsToVendor(enterEvent);
                        } catch (Exception exc) {
                            logger.error("Can't send JSON to Vendor with EnterEvents:", exc);
                        }
                    }

                    SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                            0, null);
                    resEnterEvents.addItem(item);

                    if (CalendarUtils.isDateToday(e.getEvtDateTime()) && idOfClient != null && (
                            e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT
                                    || e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT)) {
                        final EventNotificationService notificationService = RuntimeContext.getAppContext()
                                .getBean(EventNotificationService.class);
                        //final String[] values = generateNotificationParams(persistenceSession, client,
                        //        e.getPassDirection(), e.getEvtDateTime(), guardianId);
                        String[] values = generateNotificationParams(persistenceSession, clientFromEnterEvent, e);
                        values = EventNotificationService.attachTargetIdToValues(e.getIdOfEnterEvent(), values);
                        values = EventNotificationService
                                .attachSourceOrgIdToValues(idOfOrg, values); //организация из пакета синхронизации
                        values = EventNotificationService.attachOrgAddressToValues(org.getAddress(), values);
                        values = EventNotificationService
                                .attachOrgShortNameToValues(org.getShortNameInfoService(), values);
                        values = EventNotificationService
                                .attachGenderToValues(clientFromEnterEvent.getGender(), values);
                        switch (org.getType()) {
                            case PROFESSIONAL:
                            case SCHOOL: {
                                values = EventNotificationService
                                        .attachEventDirectionToValues(e.getPassDirection(), values);

                                List<Client> guardians = findGuardiansByClient(persistenceSession, idOfClient, null);

                                if (!(guardians == null || guardians.isEmpty())) {
                                    for (Client destGuardian : guardians) {
                                        if (DAOReadonlyService.getInstance()
                                                .allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                                        clientFromEnterEvent.getIdOfClient(),
                                                        ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS
                                                                .getValue())) {
                                            notificationService
                                                    .sendNotificationAsync(destGuardian, clientFromEnterEvent,
                                                            EventNotificationService.NOTIFICATION_ENTER_EVENT, values,
                                                            e.getPassDirection(), null, e.getEvtDateTime());
                                        }
                                    }
                                }
                                notificationService.sendNotificationAsync(clientFromEnterEvent, null,
                                        EventNotificationService.NOTIFICATION_ENTER_EVENT, values, e.getPassDirection(),
                                        e.getEvtDateTime());
                            }
                            break;
                            case KINDERGARTEN: {
                                if (guardianId != null) {
                                    List<Client> guardians = findGuardiansByClient(persistenceSession, idOfClient,
                                            null);//guardianId);
                                    Client guardianFromEnterEvent = DAOService.getInstance().findClientById(guardianId);
                                    values = EventNotificationService
                                            .attachGuardianIdToValues(guardianFromEnterEvent.getIdOfClient(), values);
                                    values = EventNotificationService
                                            .attachEventDirectionToValues(e.getPassDirection(), values);
                                    if (!(guardians == null || guardians.isEmpty())) {
                                        for (Client destGuardian : guardians) {
                                            if (guardians.size() > 1 && destGuardian.getIdOfClient()
                                                    .equals(guardianFromEnterEvent.getIdOfClient())) {
                                                continue;
                                            }
                                            if (DAOReadonlyService.getInstance()
                                                    .allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                                            clientFromEnterEvent.getIdOfClient(),
                                                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS
                                                                    .getValue())) {
                                                notificationService
                                                        .sendNotificationAsync(destGuardian, clientFromEnterEvent,
                                                                EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN,
                                                                values, e.getPassDirection(), guardianFromEnterEvent,
                                                                e.getEvtDateTime());
                                            }
                                        }
                                    } else {
                                        notificationService.sendNotificationAsync(clientFromEnterEvent, null,
                                                EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, values,
                                                e.getPassDirection(), guardianFromEnterEvent, e.getEvtDateTime());
                                    }
                                }
                            }
                            break;
                        }
                    }

                    /// Формирование журнала транзакции
                    if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS) && (
                            e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT
                                    || e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT) && e.getIdOfCard() != null) {

                        final CompositeIdOfEnterEvent compositeIdOfEnterEvent = enterEvent.getCompositeIdOfEnterEvent();
                        if (card == null) {
                            final String message = "Не найдена карта по событию прохода: idOfOrg=%d, idOfEnterEvent=%d, idOfCard=%d";
                            logger.error(String.format(message, compositeIdOfEnterEvent.getIdOfOrg(),
                                    compositeIdOfEnterEvent.getIdOfEnterEvent(), e.getIdOfCard()));
                        }

                        if (card != null && card.getCardType() == Card.TYPE_UEC) {
                            String OGRN = extraxtORGNFromOrgByIdOfOrg(persistenceSession, idOfOrg);
                            String transCode;
                            switch (e.getPassDirection()) {
                                case EnterEvent.ENTRY:
                                case EnterEvent.RE_ENTRY:
                                    transCode = "IN";
                                    break;
                                case EnterEvent.EXIT:
                                case EnterEvent.RE_EXIT:
                                    transCode = "OUT";
                                    break;
                                default:
                                    transCode = null;
                            }
                            if (transCode != null) {
                                TransactionJournal transactionJournal = new TransactionJournal(
                                        compositeIdOfEnterEvent.getIdOfOrg(),
                                        compositeIdOfEnterEvent.getIdOfEnterEvent(), new Date(), OGRN /*org.getOGRN()*/,
                                        TransactionJournal.SERVICE_CODE_SCHL_ACC, transCode,
                                        TransactionJournal.CARD_TYPE_CODE_UEC,
                                        TransactionJournal.CARD_TYPE_ID_CODE_MUID, Card.TYPE_NAMES[card.getCardType()],
                                        Long.toHexString(card.getCardNo()), clientFromEnterEvent.getSan(),
                                        clientFromEnterEvent.getContractId(),
                                        clientFromEnterEvent.getClientGroupTypeAsString(), e.getEnterName());
                                persistenceSession.save(transactionJournal);
                            }
                        }
                    }

                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception ex) {
                logger.error("Save enter event to database error: ", ex);
                resEnterEvents = new SyncResponse.ResEnterEvents();
                for (EnterEventItem ee : enterEvents.getEvents()) {
                    SyncResponse.ResEnterEvents.Item item;
                    item = new SyncResponse.ResEnterEvents.Item(ee.getIdOfEnterEvent(), 1, "Save to data base error");
                    resEnterEvents.addItem(item);
                }
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }

        return resEnterEvents;
    }

    private Date getTimeWithBuff() {
        String minutesStr;
        try {
            minutesStr = RuntimeContext.getInstance().getConfigProperties()
                    .getProperty("ecafe.processor.enterevents.invalidtimemod.buffertime.minutes", "5");
            int minutes = Integer.parseInt(minutesStr);

            return CalendarUtils.addMinute(new Date(), minutes);
        } catch (Exception e){
            logger.warn("ecafe.processor.enterevents.invalidtimemod.buffertime.minutes has wrong value, set 5 minute");
            return CalendarUtils.addMinute(new Date(), 5);
        }
    }

    private String getStrMod() {
        String mod;
        try{
            mod = RuntimeContext.getInstance().getConfigProperties()
                    .getProperty("ecafe.processor.enterevents.invalidtimemod", "NONE");

            EnterEventsInvalidTimeMod timeMod = EnterEventsInvalidTimeMod.valueOf(mod);
            if(timeMod == null) throw new IllegalArgumentException();

            return mod;
        } catch (IllegalArgumentException e){
            logger.warn("ecafe.processor.enterevents.invalidtimemod has wrong value, use mod \"NONE\"");
            return "NONE";
        }
    }

    private Date changeTimeByMode(EnterEventItem e, String modStr, Date now, Date syncTime) {
        EnterEventsInvalidTimeMod mod = EnterEventsInvalidTimeMod.valueOf(modStr);

        switch (mod){
            case BEGIN_DAY:
                return CalendarUtils.startOfDay(now);
            case END_DAY:
                return CalendarUtils.endOfDay(now);
            case FROM_PACKET:
                return syncTime;
            case NONE:
            default:
                return e.getEvtDateTime();
        }
    }

    private boolean enterEventOwnerHaveSmartWatch(Session session, EnterEvent enterEvent) throws Exception {
        if (enterEvent.getClient() != null) {
            return enterEvent.getClient().clientHasActiveSmartWatch();
        } else if (enterEvent.getIdOfCard() != null) {
            return DAOUtils.findClientByCardNoAndHeHaveActiveSW(session, enterEvent.getIdOfCard(),
                    enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg());
        }
        return false;
    }

    private ResCategoriesDiscountsAndRules processCategoriesDiscountsAndRules(Long idOfOrg,
            CategoriesDiscountsAndRulesRequest categoriesAndDiscountsRequest) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = new ResCategoriesDiscountsAndRules();
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            boolean isManyOrgs = categoriesAndDiscountsRequest != null && categoriesAndDiscountsRequest.isManyOrgs();
            resCategoriesDiscountsAndRules
                    .fillData(persistenceSession, idOfOrg, isManyOrgs, categoriesAndDiscountsRequest.getVersionDSZN());
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resCategoriesDiscountsAndRules;
    }

    private static boolean find(DiaryClass diaryClass, SyncRequest.ReqDiary reqDiary) throws Exception {
        Enumeration<SyncRequest.ReqDiary.ReqDiaryClass> reqDiaryClasses = reqDiary.getReqDiaryClasses();
        while (reqDiaryClasses.hasMoreElements()) {
            SyncRequest.ReqDiary.ReqDiaryClass reqDiaryClass = reqDiaryClasses.nextElement();
            if (diaryClass.getCompositeIdOfDiaryClass().getIdOfClass().equals(reqDiaryClass.getIdOfClass())) {
                return true;
            }
        }
        return false;
    }

    private static void processSyncDiaryClass(Session persistenceSession, Org organization,
            SyncRequest.ReqDiary.ReqDiaryClass reqDiaryClass) throws Exception {
        CompositeIdOfDiaryClass compositeIdOfDiaryClass = new CompositeIdOfDiaryClass(organization.getIdOfOrg(),
                reqDiaryClass.getIdOfClass());
        DiaryClass diaryClass = findDiaryClass(persistenceSession, compositeIdOfDiaryClass);
        if (null == diaryClass) {
            diaryClass = new DiaryClass(compositeIdOfDiaryClass, reqDiaryClass.getName());
            persistenceSession.save(diaryClass);
            organization.addDiaryClass(diaryClass);
        } else {
            diaryClass.setClassName(reqDiaryClass.getName());
            persistenceSession.update(diaryClass);
        }
    }

    private static void processDiaryTimesheet(Session persistenceSession, Org organization,
            SyncRequest.ReqDiary.ReqDiaryTimesheet reqDiaryTimesheet) throws Exception {
        CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet = new CompositeIdOfDiaryTimesheet(
                organization.getIdOfOrg(), reqDiaryTimesheet.getIdOfClientGroup(), reqDiaryTimesheet.getDate());
        DiaryTimesheet diaryTimesheet = findDiaryTimesheet(persistenceSession, compositeIdOfDiaryTimesheet);
        if (null == diaryTimesheet) {
            diaryTimesheet = new DiaryTimesheet(compositeIdOfDiaryTimesheet);
            fill(diaryTimesheet, reqDiaryTimesheet.getClasses());
            persistenceSession.save(diaryTimesheet);
            organization.addDiaryTimesheet(diaryTimesheet);
        } else {
            fill(diaryTimesheet, reqDiaryTimesheet.getClasses());
            persistenceSession.update(diaryTimesheet);
        }

        // Заносим в БД все оценки за этот день
        Enumeration<SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue> reqDiaryValues = reqDiaryTimesheet
                .getReqDiaryValues();
        while (reqDiaryValues.hasMoreElements()) {
            processSyncDiaryTimesheetValue(persistenceSession, organization, reqDiaryTimesheet.getDate(),
                    reqDiaryValues.nextElement());
        }
    }

    private static void fill(DiaryTimesheet diaryTimesheet, Long reqClasses[]) throws Exception {
        diaryTimesheet.setC0(getLongNullSafe(reqClasses, 0));
        diaryTimesheet.setC1(getLongNullSafe(reqClasses, 1));
        diaryTimesheet.setC2(getLongNullSafe(reqClasses, 2));
        diaryTimesheet.setC3(getLongNullSafe(reqClasses, 3));
        diaryTimesheet.setC4(getLongNullSafe(reqClasses, 4));
        diaryTimesheet.setC5(getLongNullSafe(reqClasses, 5));
        diaryTimesheet.setC6(getLongNullSafe(reqClasses, 6));
        diaryTimesheet.setC7(getLongNullSafe(reqClasses, 7));
        diaryTimesheet.setC8(getLongNullSafe(reqClasses, 8));
        diaryTimesheet.setC9(getLongNullSafe(reqClasses, 9));
    }

    private static void processSyncDiaryTimesheetValue(Session session, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        int reqVType = reqDiaryValue.getVType();
        switch (reqVType) {
            case 0:
                StrTokenizer strTokenizer = new StrTokenizer(reqDiaryValue.getValue(), ":");
                while (strTokenizer.hasNext()) {
                    String dayValueType = strTokenizer.nextToken();
                    String dayValue = strTokenizer.nextToken();
                    processSyncDiaryTimesheetDayValue(session, organization, date, reqDiaryValue, dayValueType,
                            dayValue);
                }
                break;

            case 1:
            case 2:
            case 3:
            case 4:
                processQuarterValue(session, organization, date, reqDiaryValue);
                break;

            case 5:
                processYearValue(session, organization, date, reqDiaryValue);
                break;

            default:
                //todo register error here
                break;
        }
    }

    private static void processSyncDiaryTimesheetDayValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue, String parsedDayValueType,
            String parsedDayValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue;
        if (parsedDayValueType.equals("0")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_VALUE1_TYPE);
        } else if (parsedDayValueType.equals("1")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_VALUE2_TYPE);
        } else if (parsedDayValueType.equals("K")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_EXAM_VALUE_TYPE);
        } else if (parsedDayValueType.equals("N")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                    DiaryValue.DAY_PRESENCE_VALUE_TYPE);
        } else if (parsedDayValueType.equals("P")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                    DiaryValue.DAY_BEHAVIOUR_VALUE_TYPE);
        } else {
            throw new IllegalArgumentException("Unkown day value type");
        }
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, parsedDayValue);
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static void processQuarterValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                DiaryValue.QUARTER_VALUE_TYPES[reqDiaryValue.getVType() - 1]);
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, reqDiaryValue.getValue());
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static void processYearValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.YEAR_VALUE_TYPE);
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, reqDiaryValue.getValue());
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static Long getLongNullSafe(Long values[], int index) {
        if (null == values) {
            return null;
        }
        if (index < 0) {
            return null;
        }
        if (index < values.length) {
            return values[index];
        }
        return null;
    }

    private Long generateIdOfPacket(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //Org organization = getOrgReference(persistenceSession, idOfOrg);
            OrgSync orgSync = (OrgSync) persistenceSession.load(OrgSync.class, idOfOrg);
            Long result = orgSync.getIdOfPacket();
            orgSync.setIdOfPacket(++result);
            //organization.setIdOfPacket(result + 1);
            //organization.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
            //persistenceSession.update(organization);
            persistenceSession.update(orgSync);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private String[] generateNotificationParams(Session session, Client client, EnterEventItem event) throws Exception {
        final int passDirection = event.getPassDirection();
        final Long guardianId = event.getGuardianId();
        final Integer childPassChecker = event.getChildPassChecker();
        final Long childPassCheckerId = event.getChildPassCheckerId();
        final Date eventDate = event.getEvtDateTime();
        final String enterEvent = "Вход";
        final String exitEvent = "Выход";
        String eventName = "";
        if (passDirection == EnterEvent.ENTRY) {
            eventName = enterEvent;
        } else if (passDirection == EnterEvent.EXIT) {
            eventName = exitEvent;
        } else if (passDirection == EnterEvent.RE_ENTRY) {
            eventName = enterEvent;
        } else if (passDirection == EnterEvent.RE_EXIT) {
            eventName = exitEvent;
        }
        // Если представитель не пуст, то значит вход/выход в детский сад. Иначе - в школу.
        eventName = eventName + (guardianId == null ? (eventName.equals(enterEvent) ? " в школу"
                : eventName.equals(exitEvent) ? " из школы" : "") : "");
        String guardianName = "";
        if (guardianId != null) {
            Person guardPerson = ((Client) session.load(Client.class, guardianId)).getPerson();
            guardianName = StringUtils.join(new Object[]{guardPerson.getSurname(), guardPerson.getFirstName()}, ' ');
        }
        String childPassCheckerMark = "";
        if (childPassChecker != null) {
            if (childPassChecker.longValue() == 0) {
                childPassCheckerMark = "восп.";
            } else {
                childPassCheckerMark = "охр.";
            }
        }
        String childPassCheckerName = "";
        if (childPassCheckerId != null) {
            Person childPassCheckerPerson = ((Client) session.load(Client.class, childPassCheckerId)).getPerson();
            childPassCheckerName = StringUtils
                    .join(new Object[]{childPassCheckerPerson.getSurname(), childPassCheckerPerson.getFirstName()},
                            ' ');
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(RuntimeContext.getInstance().getDefaultLocalTimeZone(null));
        calendar.setTime(eventDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(eventDate);
        String enterWithChecker = "0";
        if (event.getIdOfClient() != null) {
            if (childPassChecker != null) {
                enterWithChecker = "1";//16,17 событие
            } else {
                if (guardianId != null) {
                    enterWithChecker = "2";//3,4 событие
                } else {
                    enterWithChecker = "3";//1,2 событие
                }
            }
        }
        return new String[]{
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                ContractIdFormat.format(client.getContractId()), "surname", client.getPerson().getSurname(),
                "firstName", client.getPerson().getFirstName(), "eventName", eventName, "eventTime", time, "guardian",
                guardianName, "empTime", empTime, "childPassCheckerMark", childPassCheckerMark, "childPassCheckerName",
                childPassCheckerName, EventNotificationService.ENTER_WITH_CHECKER_VALUES_KEY, enterWithChecker};
    }

    private String[] generatePaymentNotificationParams(Session session, Client client, Payment payment) {
        long complexes = 0L;
        long others = 0L;
        Iterator<Purchase> purchases = payment.getPurchases().iterator();
        while (purchases.hasNext()) {
            Purchase purchase = purchases.next();
            if (purchase.getType() >= OrderDetail.TYPE_COMPLEX_MIN
                    && purchase.getType() <= OrderDetail.TYPE_COMPLEX_MAX) {
                complexes += purchase.getSocDiscount() + purchase.getrPrice();
            } else {
                others += purchase.getSocDiscount() + purchase.getrPrice();
            }
        }

        //String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(System.currentTimeMillis()));
        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(payment.getTime());
        String contractId = String.valueOf(client.getContractId());
        if (payment.getOrderType().equals(OrderTypeEnumType.SUBSCRIPTION_FEEDING)) {
            contractId = contractId + "01";
        }
        String ratation = "";
        for (Purchase purchase : payment.getPurchases()) {
            if (purchase.getType() != null && purchase.getType() > 0 && purchase.getType() < 100) {
                ratation = OrderDetailFRationType.fromInteger(purchase.getfRation()).toString();
                break;
            }
        }

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(payment.getTime());
        String[] values = new String[]{
                "date", date, "contractId", contractId, "others", CurrencyStringUtils.copecksToRubles(others),
                "complexes", CurrencyStringUtils.copecksToRubles(complexes), "empTime", empTime};
        if (!ratation.isEmpty())
            return EventNotificationService.attachToValues(EventNotificationService.PARAM_FRATION, ratation, values);
        return values;
    }

    public void runRegularPaymentsIfEnabled(SyncRequest request) {
        if (RuntimeContext.getInstance().isMainNode() && RuntimeContext.getInstance().getSettingsConfig()
                .isEcafeAutopaymentBkEnabled()) {
            processorUtils.runRegularPayments(request);
        }
    }

    private OrgFiles getOrgFiles(Session session, Org org, OrgFilesRequest.Operation operation) throws Exception {
        return getOrgFiles(session, org, operation, null);
    }

    private OrgFiles getOrgFiles(Session session, Org org, OrgFilesRequest.Operation operation,
            List<OrgFilesItem> orgFilesItems) throws Exception {
        OrgFiles orgFiles = new OrgFiles();
        List<OrgFile> orgFileList = DAOUtils.getOrgFilesForFriendlyOrgs(session, org.getIdOfOrg(), orgFilesItems);
        orgFiles.addOrgFilesInfo(orgFileList, operation);
        return orgFiles;
    }

    private ResOrgFiles getResOrgFiles(Session session, List<OrgFilesItem> orgFilesItemList,
            OrgFilesRequest.Operation operation, Long idOfOrgOwner) throws Exception {
        ResOrgFiles resOrgFiles = new ResOrgFiles(operation);
        List<ResOrgFilesItem> items = new ArrayList<ResOrgFilesItem>();
        List<Long> orgIdList = new ArrayList<Long>();
        for (OrgFilesItem item : orgFilesItemList) {
            orgIdList.add(item.getIdOfOrg());
        }
        List<Org> orgList = DAOUtils.findOrgs(session, orgIdList);
        Map<Long, Org> orgMap = new HashMap<Long, Org>();
        for (Org org : orgList) {
            orgMap.put(org.getIdOfOrg(), org);
        }
        List<OrgFile> orgFilesList = DAOUtils.findOrgFiles(session, orgList);
        Map<Long, OrgFile> orgFileMap = new HashMap<Long, OrgFile>();
        for (OrgFile orgFile : orgFilesList) {
            orgFileMap.put(orgFile.getIdOfOrgFile(), orgFile);
        }

        List<OrgFile> friendlyOrgList = DAOUtils.getOrgFilesForFriendlyOrgs(session, idOfOrgOwner, null);
        Long filesSize = FileUtils.getFilesSizeByOrgList(friendlyOrgList);

        ResOrgFilesItem resItem;
        for (OrgFilesItem item : orgFilesItemList) {
            if (item.getResCode().equals(OrgFilesItem.ERROR_CODE_ALL_OK)) {
                Org org = orgMap.get(item.getIdOfOrg());
                OrgFile orgFile = orgFileMap.get(item.getIdOfOrgFile());

                // add
                if (OrgFilesRequest.Operation.ADD == operation) {
                    if (null == orgFile) {
                        try {
                            byte fileData[] = FileUtils.decodeFromeBase64(item.getFileData());
                            if ((fileData.length + filesSize) >= FileUtils.FILES_SIZE_LIMIT) {
                                throw new FileUtils.NotEnoughFreeSpaceException("not enough free space");
                            }
                            String fileName = FileUtils.saveFile(org.getIdOfOrg(), fileData, item.getFileExt());
                            Long fileSize = FileUtils.fileSize(org.getIdOfOrg(), fileName, item.getFileExt());
                            orgFile = new OrgFile(fileName, item.getFileExt(), item.getDisplayName(), org, new Date(),
                                    fileSize);
                            session.save(orgFile);
                        } catch (IOException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_FILE_NOT_SAVED);
                            item.setErrorMessage("Не удалось сохранить файл");
                        } catch (FileUtils.NotEnoughFreeSpaceException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_OUT_OF_SPACE);
                            item.setErrorMessage("Не удалось сохранить файл: недостаточно свободного места");
                        } catch (FileUtils.FileIsTooBigException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_FILE_IS_TOO_BIG);
                            item.setErrorMessage(
                                    "Не удалось сохранить файл: файл слишком большой (максимальный размер файла - 3MB)");
                        }
                    } else {
                        try {
                            byte fileData[] = FileUtils.decodeFromeBase64(item.getFileData());
                            if ((fileData.length + filesSize) >= FileUtils.FILES_SIZE_LIMIT) {
                                throw new FileUtils.NotEnoughFreeSpaceException("not enough free space");
                            }
                            FileUtils.saveFile(org.getIdOfOrg(), fileData, item.getFileName(), item.getFileExt());
                            Long fileSize = FileUtils.fileSize(org.getIdOfOrg(), item.getFileName(), item.getFileExt());
                            orgFile.setSize(fileSize);
                            orgFile.setDisplayName(item.getDisplayName());
                            orgFile.setExt(item.getFileExt());
                            orgFile.setDate(new Date());
                            session.update(orgFile);
                        } catch (IOException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_FILE_NOT_SAVED);
                            item.setErrorMessage("Не удалось сохранить файл");
                        } catch (FileUtils.NotEnoughFreeSpaceException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_OUT_OF_SPACE);
                            item.setErrorMessage("Не удалось сохранить файл: недостаточно свободного места");
                        } catch (FileUtils.FileIsTooBigException e) {
                            logger.error("Error saving OrgFiles:", e);
                            item.setResCode(OrgFilesItem.ERROR_CODE_FILE_IS_TOO_BIG);
                            item.setErrorMessage(
                                    "Не удалось сохранить файл: файл слишком большой (максимальный размер файла - 3MB)");
                        }
                    }
                } else if (OrgFilesRequest.Operation.DELETE == operation) {     // delete
                    if (null == orgFile) {
                        logger.error("Error removing OrgFiles: file not exist");
                        item.setResCode(OrgFilesItem.ERROR_CODE_FILE_NOT_DELETED);
                        item.setErrorMessage("Не удалось удалить файл: файл не существует");
                    } else {
                        try {
                            if (!FileUtils.removeFile(org.getIdOfOrg(), orgFile.getName(), orgFile.getExt())) {
                                logger.error("Error removing OrgFiles: unexpected error");
                                item.setResCode(OrgFilesItem.ERROR_CODE_FILE_NOT_DELETED);
                                item.setErrorMessage("Не удалось удалить файл");
                            }
                            session.delete(orgFile);
                        } catch (SecurityException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                if (item.getResCode().equals(OrgFilesItem.ERROR_CODE_ALL_OK)) {
                    resItem = new ResOrgFilesItem(orgFile);
                    if (null != item.getIdOfOrgFile()) {
                        resItem.setIdOfOrgFile(item.getIdOfOrgFile());
                    }
                    resItem.setResCode(item.getResCode());
                    if (null != item.getDisplayName() && !item.getDisplayName().isEmpty()) {
                        resItem.setDisplayName(item.getDisplayName());
                    }
                    if (null != item.getFileExt() && !item.getFileExt().isEmpty()) {
                        resItem.setFileExt(item.getFileExt());
                    }
                } else {
                    resItem = new ResOrgFilesItem();
                    resItem.setIdOfOrg(item.getIdOfOrg());
                    resItem.setResCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                    resItem.setIdOfOrgFile(item.getIdOfOrgFile());
                    resItem.setDisplayName(item.getDisplayName());
                    resItem.setFileExt(item.getFileExt());
                }
            } else {
                resItem = new ResOrgFilesItem();
                resItem.setIdOfOrg(item.getIdOfOrg());
                resItem.setIdOfOrgFile(item.getIdOfOrgFile());
                resItem.setResCode(item.getResCode());
                resItem.setErrorMessage(item.getErrorMessage());
                resItem.setDisplayName(item.getDisplayName());
                resItem.setFileExt(item.getFileExt());
            }
            items.add(resItem);
            session.flush();
        }
        resOrgFiles.setItems(items);
        return resOrgFiles;
    }

    private void fullProcessingOrgFiles(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            OrgFilesRequest orgFilesRequest = request.getOrgFilesRequest();
            ResOrgFiles resOrgFiles = null;
            OrgFiles orgFiles = null;

            if (orgFilesRequest != null) {
                switch (orgFilesRequest.getOperation()) {
                    case ADD:
                        resOrgFiles = getResOrgFiles(persistenceSession, orgFilesRequest.getItems(),
                                orgFilesRequest.getOperation(), request.getIdOfOrg());
                        break;
                    case LIST:
                        orgFiles = getOrgFiles(persistenceSession, request.getOrg(), orgFilesRequest.getOperation());
                        break;
                    case DOWNLOAD:
                        orgFiles = getOrgFiles(persistenceSession, request.getOrg(), orgFilesRequest.getOperation(),
                                orgFilesRequest.getItems());
                        break;
                    case DELETE:
                        //idsOfOrgFile = new ArrayList<Long>();
                        //for (OrgFilesItem i : orgFilesRequest.getItems())
                        //    idsOfOrgFile.add(i.getIdOfOrgFile());

                        resOrgFiles = getResOrgFiles(persistenceSession, orgFilesRequest.getItems(),
                                orgFilesRequest.getOperation(), request.getIdOfOrg());
                        break;
                    default:
                        /* nope */
                        break;
                }

                if (null != resOrgFiles) {
                    addToResponseSections(resOrgFiles, responseSections);
                }

                if (null != orgFiles) {
                    addToResponseSections(orgFiles, responseSections);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            String message = String.format("Failed to build organization files, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public SyncHistory createSyncHistoryIntegro(Long idOfOrg, Date startTime, String clientVersion,
            String remoteAddress) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            OrgSync orgSync = (OrgSync) persistenceSession.load(OrgSync.class, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, orgSync.getIdOfPacket(), clientVersion,
                    remoteAddress);
            persistenceSession.save(syncHistory);
            //Long idOfSync = syncHistory.getIdOfSync();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return syncHistory;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private ResHelpRequest processHelpRequest(HelpRequest helpRequest) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResHelpRequest resHelpRequest = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new HelpRequestProcessor(persistenceSession, helpRequest);
            resHelpRequest = (ResHelpRequest) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resHelpRequest;
    }

    private ResMenusCalendar processMenusCalendarSupplier(MenusCalendarSupplierRequest menusCalendarSupplierRequest) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResMenusCalendar resMenusCalendar = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            MenusCalendarProcessor processor = new MenusCalendarProcessor(persistenceSession,
                    menusCalendarSupplierRequest);
            resMenusCalendar = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMenusCalendar;
    }

    private MenusCalendarData processMenusCalendarData(MenusCalendarRequest menusCalendarRequest) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        MenusCalendarData menusCalendarData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            MenusCalendarProcessor processor = new MenusCalendarProcessor(persistenceSession, menusCalendarRequest);
            menusCalendarData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return menusCalendarData;
    }

    private HelpRequestData processHelpRequestData(HelpRequest helpRequest) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        HelpRequestData helpRequestData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            HelpRequestProcessor processor = new HelpRequestProcessor(persistenceSession, helpRequest);
            helpRequestData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return helpRequestData;
    }

    private SyncResponse buildHelpRequestsSyncResponse(SyncRequest request) throws Exception {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSettingSection = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;

        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        try {
            HelpRequest helpRequest = request.getHelpRequest();
            if (helpRequest != null) {
                helpRequestData = processHelpRequestData(helpRequest);
                resHelpRequest = processHelpRequest(helpRequest);
            }
        } catch (Exception e) {
            String message = String.format("processHelpRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSettingSection, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private void fullProcessingHelpRequests(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            HelpRequest helpRequest = request.getHelpRequest();
            if (null != helpRequest) {
                HelpRequestData helpRequestData = processHelpRequestData(helpRequest);
                addToResponseSections(helpRequestData, responseSections);

                ResHelpRequest resHelpRequest = processHelpRequest(helpRequest);
                addToResponseSections(resHelpRequest, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingHelpRequests: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private PreOrdersFeeding processPreOrderFeedingRequest(PreOrdersFeedingRequest preOrdersFeedingRequest)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        PreOrdersFeeding preOrdersFeeding = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            PreOrderFeedingProcessor processor = new PreOrderFeedingProcessor(persistenceSession,
                    preOrdersFeedingRequest);
            preOrdersFeeding = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return preOrdersFeeding;
    }

    private ClientBalanceHoldFeeding processClientBalanceHoldRequest(ClientBalanceHoldRequest clientBalanceHoldRequest)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientBalanceHoldProcessor processor = new ClientBalanceHoldProcessor(persistenceSession,
                    clientBalanceHoldRequest, null);
            clientBalanceHoldFeeding = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientBalanceHoldFeeding;
    }

    private ResClientBalanceHoldData processClientBalanceHoldData(ClientBalanceHoldData clientBalanceHoldData)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientBalanceHoldProcessor processor = new ClientBalanceHoldProcessor(persistenceSession, null,
                    clientBalanceHoldData);
            resClientBalanceHoldData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error in process ClientBalanceHoldData: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resClientBalanceHoldData;
    }

    private void fullProcessingPreOrderFeedingRequest(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            PreOrdersFeedingRequest preOrdersFeedingRequest = request.getPreOrderFeedingRequest();
            if (null != preOrdersFeedingRequest) {
                PreOrdersFeeding preOrdersFeeding = processPreOrderFeedingRequest(preOrdersFeedingRequest);
                addToResponseSections(preOrdersFeeding, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingPreOrderFeedingRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingClientBalanceHoldRequest(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientBalanceHoldRequest clientBalanceHoldRequest = request.getClientBalanceHoldRequest();
            if (null != clientBalanceHoldRequest) {
                ClientBalanceHoldFeeding clientBalanceHoldFeeding = processClientBalanceHoldRequest(
                        clientBalanceHoldRequest);
                addToResponseSections(clientBalanceHoldFeeding, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingClientBalanceHoldRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingClientBalanceHoldData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientBalanceHoldData clientBalanceHoldData = request.getClientBalanceHoldData();
            if (null != clientBalanceHoldData) {
                ResClientBalanceHoldData resClientBalanceHoldData = processClientBalanceHoldData(clientBalanceHoldData);
                addToResponseSections(resClientBalanceHoldData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingClientBalanceHoldData: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingRequestFeeding(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            RequestFeeding requestFeeding = request.getRequestFeeding();
            if (null != requestFeeding) {
                RequestFeedingData requestFeedingData = processRequestFeedingData(requestFeeding);
                addToResponseSections(requestFeedingData, responseSections);

                ResRequestFeeding resRequestFeeding = processRequestFeeding(requestFeeding);
                addToResponseSections(resRequestFeeding, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingRequestFeeding: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingPlanOrdersRestrictionsData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            PlanOrdersRestrictionsRequest planOrdersRestrictionsRequest = request.getPlanOrdersRestrictionsRequest();
            if (null != planOrdersRestrictionsRequest) {
                PlanOrdersRestrictions planOrdersRestrictionsData = processPlanOrdersRestrictions(planOrdersRestrictionsRequest);
                addToResponseSections(planOrdersRestrictionsData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingPlanOrdersRestrictionsData: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingEMIAS(SyncRequest request, List<AbstractToElement> responseSections) {
        try {
            EmiasRequest emiasRequest = request.getEmiasRequest();
            if (emiasRequest != null) {
                FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                EmiasSection emiasSection = new EmiasSection();
                emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                addToResponseSections(emiasSection, responseSections);
                EmiasSectionForARMAnswer emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                addToResponseSections(emiasSectionForARMAnswer, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
            logger.error(message, e);
        }
    }

    private void fullProcessingExemptionVisiting(SyncRequest request, List<AbstractToElement> responseSections) {
        try {
            ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
            if (exemptionVisitingRequest != null) {
                FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(exemptionVisitingRequest, request.getIdOfOrg());
                ExemptionVisitingSection exemptionVisitingSection = new ExemptionVisitingSection();
                exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                addToResponseSections(exemptionVisitingSection, responseSections);
                ExemptionVisitingSectionForARMAnswer exemptionVisitingForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                exemptionVisitingForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                exemptionVisitingForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                addToResponseSections(exemptionVisitingForARMAnswer, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
            logger.error(message, e);
        }
    }

    private ResRequestFeeding processRequestFeeding(RequestFeeding requestFeeding) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResRequestFeeding resRequestFeeding = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new RequestFeedingProcessor(persistenceSession, requestFeeding);
            resRequestFeeding = (ResRequestFeeding) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            ((RequestFeedingProcessor) processor).processETPStatuses(resRequestFeeding.getStatuses());
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resRequestFeeding;
    }

    private RequestFeedingData processRequestFeedingData(RequestFeeding requestFeeding) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        RequestFeedingData requestFeedingData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            RequestFeedingProcessor processor = new RequestFeedingProcessor(persistenceSession, requestFeeding);
            requestFeedingData = processor.processData();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error in process processRequestFeedingData: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return requestFeedingData;
    }

    private void fullProcessingClientDiscountDSZN(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            ClientDiscountsDTSZNRequest dsznRequest = request.getClientDiscountDSZNRequest();
            if (null != dsznRequest) {
                ClientDiscountDTSZN clientDiscountDTSZN = processClientDiscountsDTSZN(dsznRequest);
                addToResponseSections(clientDiscountDTSZN, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("fullProcessingClientDiscountDSZN: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrgSettings(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            OrgSettingsRequest orgSettingsRequest = request.getOrgSettingsRequest();
            if (orgSettingsRequest != null) {
                orgSettingsRequest.setIdOfOrgSource(request.getIdOfOrg());
                OrgSettingSection orgSettingSection = processOrgSettings(orgSettingsRequest);
                addToResponseSections(orgSettingSection, responseSections);
                discardOrgSettingsSyncParam(request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("Error when process OrgSettingSetting: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingGoogRequestEZD(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {

        try {
            GoodRequestEZDRequest goodRequestEZDRequest = request.getGoodRequestEZDRequest();
            //Если такая секция существует в исходном запросе
            if (goodRequestEZDRequest != null) {
                GoodRequestEZDSection goodRequestEZDSection = processGoodRequestEZD(goodRequestEZDRequest,
                        request.getIdOfOrg());
                addToResponseSections(goodRequestEZDSection, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process GoodRequestEZD: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingSyncSetting(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        try {
            SyncSettingsRequest syncSettingsRequest = request.findSection(SyncSettingsRequest.class);
            if (syncSettingsRequest != null) {
                syncSettingsRequest.setOwner(request.getIdOfOrg());
                SyncSettingProcessor processor = processSyncSettingRequest(syncSettingsRequest);
                if (processor != null) {
                    resSyncSettingsSection = processor.getResSyncSettingsSection();
                    syncSettingsSection = processor.getSyncSettingsSection();
                    processor = null;
                    addToResponseSections(resSyncSettingsSection, responseSections);
                    addToResponseSections(syncSettingsSection, responseSections);
                    discardOrgSettingsSyncParam(request.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            String message = String.format("Error when process SyncSettingSetting: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingCardRequests(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            CardRequests cardRequest = request.getCardRequests();
            if (cardRequest != null) {
                CardRequestsData cardRequestsData = processCardRequestsData(request.getCardRequests());
                addToResponseSections(cardRequestsData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process CardRequests: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingMenusCalendar(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            MenusCalendarSupplierRequest menusCalendarSupplierRequest = request.getMenusCalendarSupplierRequest();
            if (menusCalendarSupplierRequest != null) {
                ResMenusCalendar resMenusCalendar = processMenusCalendarSupplier(menusCalendarSupplierRequest);
                addToResponseSections(resMenusCalendar, responseSections);
            }

            MenusCalendarRequest menusCalendarRequest = request.getMenusCalendarRequest();
            if (menusCalendarRequest != null) {
                MenusCalendarData menusCalendarData = processMenusCalendarData(menusCalendarRequest);
                addToResponseSections(menusCalendarData, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process CardRequests: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private ClientDiscountDTSZN processClientDiscountsDTSZN(ClientDiscountsDTSZNRequest dsznRequest) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientDiscountDTSZN clientDiscountDTSZN = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ClientDiscountDTSZNProcessor(persistenceSession, dsznRequest);
            clientDiscountDTSZN = (ClientDiscountDTSZN) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientDiscountDTSZN;
    }

    public boolean removeClientDiscountIfChangeOrg(Client client, Session session, Set<Org> oldOrgs, long newIdOfOrg)
            throws Exception {
        if (client == null) {
            return false;
        }
        //Если новая организация не совпадает ни со старой, ни с дружественными старой, то удаляем льготы
        if (isReplaceOrg(client, oldOrgs, newIdOfOrg)) {
            DiscountManager.deleteDiscount(client, session);
            return true;
        }
        return false;
    }

    public void archiveApplicationForFoodIfChangeOrg(Client client, Session session, Set<Org> oldOrgs, long newIdOforg)
            throws Exception {
        if (client == null) {
            return;
        }

        if (isReplaceOrg(client, oldOrgs, newIdOforg)) {
            ClientManager.archiveApplicationForFoodWithoutDiscount(client, session);
        }

    }

    private boolean isReplaceOrg(Client client, Set<Org> oldOrgs, long newIdOfOrg) {
        Boolean replaceOrg = !client.getOrg().getIdOfOrg()
                .equals(newIdOfOrg); //сравниваем старую организацию клиента с новой
        for (Org o : oldOrgs) {
            if (o.getIdOfOrg().equals(newIdOfOrg)) {                             //и с дружественными организациями
                replaceOrg = false;
                return replaceOrg;
            }
        }
        return replaceOrg;
    }

    private SyncResponse buildOrgSettingsSectionsResponse(SyncRequest request, Date syncStartTime, int syncResult) {
        SyncHistory syncHistory = null;
        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        OrganizationStructure organizationStructure = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;
        ResReestrTaloonApproval resReestrTaloonApproval = null;
        ReestrTaloonApprovalData reestrTaloonApprovalData = null;
        ResReestrTaloonPreorder resReestrTaloonPreorder = null;
        ReestrTaloonPreorderData reestrTaloonPreorderData = null;
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;
        ResHelpRequest resHelpRequest = null;
        HelpRequestData helpRequestData = null;
        PreOrdersFeeding preOrdersFeeding = null;
        CardRequestsData cardRequestsData = null;
        ResMenusCalendar resMenusCalendar = null;
        MenusCalendarData menusCalendarData = null;
        ClientBalanceHoldFeeding clientBalanceHoldFeeding = null;
        ResClientBalanceHoldData resClientBalanceHoldData = null;
        OrgSettingSection orgSetting = null;
        SyncSettingsSection syncSettingsSection = null;
        ResSyncSettingsSection resSyncSettingsSection = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        EmiasSection emiasSection = null;
        EmiasSectionForARMAnswer emiasSectionForARMAnswer = null;
        ExemptionVisitingSection exemptionVisitingSection = null;
        ExemptionVisitingSectionForARMAnswer exemptionVisitingSectionForARMAnswer = null;
        ResMenuSupplier resMenuSupplier = null;
        ResRequestsSupplier resRequestsSupplier = null;
        RequestsSupplierData requestsSupplierData = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        try {
            OrgSettingsRequest orgSettingsRequest = request.getOrgSettingsRequest();
            if (orgSettingsRequest != null) {
                orgSettingsRequest.setIdOfOrgSource(request.getIdOfOrg());
                orgSetting = processOrgSettings(orgSettingsRequest);
            }
        } catch (Exception e) {
            String message = String.format("Error when process OrgSettingSetting: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            SyncSettingsRequest syncSettingsRequest = request.findSection(SyncSettingsRequest.class);
            if (syncSettingsRequest != null) {
                syncSettingsRequest.setOwner(request.getIdOfOrg());
                SyncSettingProcessor processor = processSyncSettingRequest(syncSettingsRequest);
                if (processor != null) {
                    resSyncSettingsSection = processor.getResSyncSettingsSection();
                    syncSettingsSection = processor.getSyncSettingsSection();
                    processor = null;
                }
            }
        } catch (Exception e) {
            String message = String.format("Error when process SyncSettingSetting: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            GoodRequestEZDRequest goodRequestEZDRequest = request.getGoodRequestEZDRequest();
            //Если такая секция существует в исходном запросе
            if (goodRequestEZDRequest != null) {
                goodRequestEZDSection = processGoodRequestEZD(goodRequestEZDRequest, request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("Error when process GoodRequestEZD: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        discardOrgSettingsSyncParam(request.getIdOfOrg());


        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emias", "1").equals("1")) {
            try {
                EmiasRequest emiasRequest = request.getEmiasRequest();
                if (emiasRequest != null) {
                    FullEmiasAnswerForARM fullEmiasAnswerForARM = processEmias(emiasRequest, request.getIdOfOrg());
                    emiasSection = new EmiasSection();
                    emiasSection.setItems(fullEmiasAnswerForARM.getItems());
                    emiasSectionForARMAnswer = new EmiasSectionForARMAnswer();
                    emiasSectionForARMAnswer.setMaxVersion(fullEmiasAnswerForARM.getMaxVersionArm());
                    emiasSectionForARMAnswer.setItems(fullEmiasAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process EmiasRequest: %s", e.getMessage());
                logger.error(message, e);
            }

            try {
                ExemptionVisitingRequest exemptionVisitingRequest = request.getExemptionVisitingRequest();
                if (exemptionVisitingRequest != null) {
                    FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = processExemptionVisiting(
                            exemptionVisitingRequest, request.getIdOfOrg());
                    exemptionVisitingSection = new ExemptionVisitingSection();
                    exemptionVisitingSection.setItems(fullExemptionVisitingAnswerForARM.getItems());
                    exemptionVisitingSectionForARMAnswer = new ExemptionVisitingSectionForARMAnswer();
                    exemptionVisitingSectionForARMAnswer.setMaxVersion(fullExemptionVisitingAnswerForARM.getMaxVersionArm());
                    exemptionVisitingSectionForARMAnswer.setItems(fullExemptionVisitingAnswerForARM.getItemsArm());
                }
            } catch (Exception e) {
                String message = String.format("Error when process ExemptionVisitingRequest: %s", e.getMessage());
                logger.error(message, e);
            }
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData, resReestrTaloonPreorder, reestrTaloonPreorderData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections, resHelpRequest,
                helpRequestData, preOrdersFeeding, cardRequestsData, resMenusCalendar, menusCalendarData,
                clientBalanceHoldFeeding, resClientBalanceHoldData, orgSetting, goodRequestEZDSection,
                resSyncSettingsSection, syncSettingsSection, emiasSection, emiasSectionForARMAnswer, exemptionVisitingSection,
                exemptionVisitingSectionForARMAnswer,
                resMenuSupplier, resRequestsSupplier, requestsSupplierData, resHardwareSettingsRequest, resTurnstileSettingsRequest);
    }

    private OrgSettingSection processOrgSettings(OrgSettingsRequest orgSettingsRequest) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        OrgSettingSection orgSetting = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new OrgSettingsProcessor(persistenceSession, orgSettingsRequest);
            orgSetting = (OrgSettingSection) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return orgSetting;
    }

    private SyncSettingProcessor processSyncSettingRequest(SyncSettingsRequest syncSettingsRequest) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncSettingProcessor processor = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            processor = new SyncSettingProcessor(persistenceSession, syncSettingsRequest);
            processor.process();

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return processor;
    }

    private GoodRequestEZDSection processGoodRequestEZD(GoodRequestEZDRequest goodRequestEZDRequest, Long idOfOrg)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        GoodRequestEZDSection goodRequestEZDSection = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new GoodRequestEZDProcessor(persistenceSession, goodRequestEZDRequest,
                    idOfOrg);
            goodRequestEZDSection = (GoodRequestEZDSection) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return goodRequestEZDSection;
    }

    private FullEmiasAnswerForARM processEmias(EmiasRequest emiasRequest, Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        FullEmiasAnswerForARM fullEmiasAnswerForARM = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new EmiasProcessor(persistenceSession, emiasRequest, idOfOrg);
            fullEmiasAnswerForARM = (FullEmiasAnswerForARM) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return fullEmiasAnswerForARM;
    }

    private FullExemptionVisitingAnswerForARM processExemptionVisiting(ExemptionVisitingRequest exemptionVisitingRequest, Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        FullExemptionVisitingAnswerForARM fullExemptionVisitingAnswerForARM = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ExemptionVisitingProcessor(persistenceSession, exemptionVisitingRequest, idOfOrg);
            fullExemptionVisitingAnswerForARM = (FullExemptionVisitingAnswerForARM) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return fullExemptionVisitingAnswerForARM;
    }

    private ResHardwareSettingsRequest processResHardwareSettingsRequest(HardwareSettingsRequest orgEquipmentRequest) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResHardwareSettingsRequest resHardwareSettingsRequest = null;
        try{
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new HardwareSettingsRequestProcessor(persistenceSession, orgEquipmentRequest);
            resHardwareSettingsRequest = ((HardwareSettingsRequestProcessor) processor).process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resHardwareSettingsRequest;
    }

    private void ProcessingHardwareSettingsRequest(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            HardwareSettingsRequest hardwareSettingsRequest = request.getHardwareSettingsRequest();
            if (hardwareSettingsRequest != null) {
                ResHardwareSettingsRequest resHardwareSettingsRequest = processResHardwareSettingsRequest(hardwareSettingsRequest);
                addToResponseSections(resHardwareSettingsRequest, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process HardwareSettingsRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private ResTurnstileSettingsRequest processResTurnstileSettingsRequest(TurnstileSettingsRequest turnstileSettingsRequest) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResTurnstileSettingsRequest resTurnstileSettingsRequest = null;
        try{
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new TurnstileSettingsRequestProcessor(persistenceSession, turnstileSettingsRequest);
            resTurnstileSettingsRequest = ((TurnstileSettingsRequestProcessor) processor).process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resTurnstileSettingsRequest;
    }

    private void ProcessingTurnstileSettingsRequest(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            TurnstileSettingsRequest turnstileSettingsRequest = request.getTurnstileSettingsRequest();
            if (turnstileSettingsRequest != null) {
                ResTurnstileSettingsRequest resTurnstileSettingsRequest = processResTurnstileSettingsRequest(turnstileSettingsRequest);
                addToResponseSections(resTurnstileSettingsRequest, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Error when process TurnstileSettingsRequest: %s", e.getMessage());
            processorUtils
                    .createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }
}