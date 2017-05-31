/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import com.google.common.base.Joiner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodBasicBasketPrice;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.meal.MealManager;
import ru.axetta.ecafe.processor.core.sync.*;
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
import ru.axetta.ecafe.processor.core.sync.handlers.groups.*;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReport;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportData;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportDataProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.Migrants;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsData;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.MigrantsProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.migrants.ResMigrants;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.*;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalData;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ReestrTaloonApprovalProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ResReestrTaloonApproval;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts.AccountsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.cards.CardsOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.ResAccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.ResSpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDates;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesData;
import ru.axetta.ecafe.processor.core.sync.handlers.special.dates.SpecialDatesProcessor;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
    private static final long ACC_REGISTRY_TIME_CLIENT_IN_MILLIS = RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.accRegistryUpdate.timeClient", 7) * 24 * 60 * 60 * 1000;
    private static final int MIN_REMAINING_CAPACITY_POOL = 1000;

    private ProcessorUtils processorUtils = RuntimeContext.getAppContext().getBean(ProcessorUtils.class);

    public Processor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
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
                    // обработка синхронизации покупок и прохода клиентов
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
                case TYPE_MIGRANTS:{
                    //обработка временных посетителей (мигрантов)
                    response = buildMigrantsSyncResponse(request);
                    break;
                }
                case TYPE_CONSTRUCTED:{
                    response = buildUnivercalConstructedSectionsSyncResponse(request, syncStartTime, syncResult);
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
                RuntimeContext.getInstance().getCardManager().updateCard(client.getIdOfClient(),
                        card.getIdOfCard(), card.getCardType(), CardState.BLOCKED.getValue(),
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
        ThreadPoolTaskExecutor ex = (ThreadPoolTaskExecutor)RuntimeContext.getAppContext().getBean("executorWithPoolSizeRange");
        //Если размер очереди в пуле приближается к размеру самого пула, то выполнить операцию синхронно, иначе все ок и асинхронно
        if (ex == null || ex.getActiveCount() > ex.getCorePoolSize()*3 || ex.getThreadPoolExecutor().getQueue().remainingCapacity() < MIN_REMAINING_CAPACITY_POOL) {
            processorUtils.saveLastProcessSectionCustomDate(sessionFactory, idOfOrg, sectionType);
            logger.error("queue size of asyncThreadPoolTaskExecutor is near to limit. Run synchronously");
        } else {
            processorUtils.saveLastProcessSectionDate(sessionFactory, idOfOrg, sectionType);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        InteractiveReport interactiveReport = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();


        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        // Register sync history
        syncHistory = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                request.getRemoteAddr());
        addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(),
                request.getRemoteAddr());

        processMigrantsSections(request, syncHistory, responseSections, null);

        processAccountOperationsRegistrySections(request, syncHistory, responseSections, null);


        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry().getPayments().hasNext()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, "no license slots available");
                    throw new Exception("no license slots available");
                }
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.PAYMENT_REGISTRY);
            resPaymentRegistry = processSyncPaymentRegistry(syncHistory.getIdOfSync(), request.getIdOfOrg(),
                    request.getPaymentRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
            bError = true;
        }

        // Process ClientParamRegistry
        try {
            processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), request.getClientParamRegistry(),
                    errorClientIds);
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }


        // Process OrgStructure
        try {
            if (request.getOrgStructure() != null) {
                resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), request.getOrgStructure(), syncHistory);
            }
            if (resOrgStructure != null && resOrgStructure.getResult() > 0) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, resOrgStructure.getError());
            }
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Build client registry
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.CLIENT_REGISTRY);
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(), request.getClientRegistryRequest(),
                    errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process goods basic basket data , IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process menu from Org
        try {
            processSyncMenu(request.getIdOfOrg(), request.getReqMenu());
        } catch (Exception e) {
            String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            resMenuExchange = getMenuExchangeData(request.getIdOfOrg(), syncStartTime,
                    DateUtils.addDays(syncStartTime, RESPONSE_MENU_PERIOD_IN_DAYS));
        } catch (Exception e) {
            String message = String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process prohibitions menu from Org
        try {
            final ProhibitionMenuRequest prohibitionMenuRequest = request.getProhibitionMenuRequest();
            if (prohibitionMenuRequest != null) {
                prohibitionsMenu = getProhibitionsMenuData(request.getOrg(), prohibitionMenuRequest.getMaxVersion());
            }
        } catch (Exception e) {
            String message = String.format("Failed to build prohibitions menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            prohibitionsMenu = new ProhibitionsMenu(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }

        //Process organization structure
        try {
            final OrganizationStructureRequest organizationStructureRequest = request.getOrganizationStructureRequest();
            if (organizationStructureRequest != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ORGANIZATIONS_STRUCTURE);
                organizationStructure = getOrganizationStructureData(request.getOrg(),
                        organizationStructureRequest.getMaxVersion(), organizationStructureRequest.isAllOrgs());
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationStructure = new OrganizationStructure(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }

        try {
            final OrganizationComplexesStructureRequest organizationComplexesStructureRequest = request.getOrganizationComplexesStructureRequest();
            organizationComplexesStructure = getOrganizationComplexesStructureData(request.getOrg(),
                    organizationComplexesStructureRequest.getMaxVersion(), organizationComplexesStructureRequest.getMenuSyncCountDays());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationComplexesStructure = new OrganizationComplexesStructure(100,
                    String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }

        try {
            if (request.getInteractiveReport() != null) {
                interactiveReport = processInteractiveReport(request.getIdOfOrg(), request.getInteractiveReport());
            }
        } catch (Exception e) {
            String message = String.format("processInteractiveReport: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            interactiveReportData = processInteractiveReportData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Build AccRegistry
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);

        }

        try {
            resCardsOperationsRegistry = new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        // Process ReqDiary
        try {
            if (request.getReqDiary() != null) {
                resDiary = processSyncDiary(request.getIdOfOrg(), request.getReqDiary());
            }
        } catch (Exception e) {
            resDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ENTER_EVENTS);
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(), request.getIdOfOrg());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getClientRequests() != null) {
                ClientRequests clientRequests = request.getClientRequests();
                if (clientRequests.getResponseTempCardOperation()) {
                    tempCardOperationData = processClientRequestsOperations(request.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            String message = String.format("processClientRequestsOperations: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process ResCategoriesDiscountsAndRules
        try {
            resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(request.getIdOfOrg(),
                    request.getCategoriesAndDiscountsRequest());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process ComplexRoles
        try {
            complexRoles = processComplexRoles();
        } catch (Exception e) {
            String message = String.format("processComplexRoles: %s", e.getMessage());
            logger.error(message, e);
        }

        // Process CorrectingNumbersOrdersRegistry
        try {
            correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            questionaryData = processQuestionaryData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process questionary data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        /*try {
            goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String
                    .format("Failed to process goods basic basket data , IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }*/

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

        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ACCOUNTS_REGISTRY);
            accountsRegistry = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class)
                    .handlerFull(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            if (request.getReestrTaloonApproval() != null) {
                resReestrTaloonApproval = processReestrTaloonApproval(request.getReestrTaloonApproval());
                reestrTaloonApprovalData = processReestrTaloonApprovalData(request.getReestrTaloonApproval());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonApproval: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getZeroTransactions() != null) {
                zeroTransactionData = processZeroTransactionsData(request.getZeroTransactions());
                resZeroTransactions = processZeroTransactions(request.getZeroTransactions());
            }
        } catch (Exception e) {
            String message = String.format("processZeroTransactions: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getSpecialDates() != null) {
                specialDatesData = processSpecialDatesData(request.getSpecialDates());
                resSpecialDates = processSpecialDates(request.getSpecialDates());
            }
        } catch (Exception e) {
            String message = String.format("processSpecialDates: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        //process groups organization
        fullProcessingGroupsOrganization(request,syncHistory,responseSections);

        //info messages
        processInfoMessageSections(request, responseSections);

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime, "",
                accRegistry, resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry,
                resOrgStructure, resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations,
                tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry,
                manager, orgOwnerData, questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian,
                clientGuardianData, accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions, specialDatesData,
                resSpecialDates, migrantsData, resMigrants, responseSections);
    }

    private SyncResponse buildUnivercalConstructedSectionsSyncResponse(SyncRequest request, Date syncStartTime,
            int syncResult) throws Exception {
        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации
        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();
        List<Long> errorClientIds = new ArrayList<Long>();

        if (request.isFullSync()) {
            idOfPacket = generateIdOfPacket(request.getIdOfOrg());
            // Register sync history
            syncHistory = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                    request.getRemoteAddr());
            addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(),
                    request.getRemoteAddr());
        }

        // мигранты
        processMigrantsSectionsWithClientsData(request, syncHistory, responseSections);

        // операции со счетами
        fullProcessingAccountOperationsRegistry(request, responseSections);

        // Process paymentRegistry
        Boolean wasErrorProcessedPaymentRegistry = false;
        processPaymentRegistrySections(request, syncHistory, responseSections, wasErrorProcessedPaymentRegistry, idOfPacket, errorClientIds);

        //AccIncRegistry or AccIncUpdate
        boolean wasErrorProcessedAccInc = fullProcessingAccIncRegistryOrAccIncUpdate(request, responseSections);

        // Process ClientParamRegistry
        fullProcessingClientParamsRegistry(request, syncHistory, errorClientIds);

        // Process ClientGuardianRequest
        fullProcessingClientGuardians(request, syncHistory, responseSections);

        // Process OrgStructure
        fullProcessingOrgStructure(request, syncHistory, responseSections);

        // Build client registry
        fullProcessingClientsRegistry(request, syncHistory, responseSections, errorClientIds);

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

        // обработка нулевых транзакций
        fullProcessingZeroTransactions(request, syncHistory, responseSections);

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

        // время окончания обработки
        Date syncEndTime = new Date();

        if (request.isFullSync() && syncHistory!=null) {
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
        if (requestSection == null) return;
        try {
            ResProcessGroupsOrganization resGroupsOrganization = processResGroupsOrganization(requestSection);
            addToResponseSections(resGroupsOrganization, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to process ResGroupsOrganization, %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            ProcessGroupsOrganizationData groupsOrganizationData = processGroupsOrganizationData(requestSection);
            addToResponseSections(groupsOrganizationData, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to process GroupsOrganization, %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
                addToResponseSections(manager,responseSections);
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
                AccountsRegistryHandler accountsRegistryHandler = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            }
        } catch (Exception e) {
            String message = String.format("processSpecialDates: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            CategoriesDiscountsAndRulesRequest categoriesDiscountsAndRulesRequest = request.findSection(CategoriesDiscountsAndRulesRequest.class);
            if (categoriesDiscountsAndRulesRequest != null) {
                ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(
                        request.getIdOfOrg(), categoriesDiscountsAndRulesRequest);
                addToResponseSections(resCategoriesDiscountsAndRules, responseSections);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingTempCardsOperationsAndData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        TempCardsOperations cardsOperations = request.getTempCardsOperations();
        if (cardsOperations == null) return;

        try {
            ResTempCardsOperations tempCardsOperations = processTempCardsOperations(cardsOperations, request.getIdOfOrg());
            addToResponseSections(tempCardsOperations, responseSections);
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingEnterEvents(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ENTER_EVENTS);
                SyncResponse.ResEnterEvents resEnterEvents = processSyncEnterEvents(request.getEnterEvents(),
                        request.getOrg());
                addToResponseSections(resEnterEvents, responseSections);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()), e);
        }
    }

    private void fullProcessingResDiary(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.ReqDiary reqDiary = request.findSection(SyncRequest.ReqDiary.class);
        if (reqDiary == null) return;

        SyncResponse.ResDiary resultDiary = null;
        try {
            resultDiary = processSyncDiary(request.getIdOfOrg(), reqDiary);
        } catch (Exception e) {
            resultDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            logger.error(String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }
    }

    private void fullProcessingAccRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncResponse.AccRegistry accRegistry=null;
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg(), null,
                    request.getClientVersion());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        addToResponseSections(accRegistry, responseSections);
    }

    private boolean fullProcessingAccIncRegistryOrAccIncUpdate(SyncRequest request,
            List<AbstractToElement> responseSections) {
        SyncRequest.AccIncRegistryRequest accIncRegistryRequest = request.getAccIncRegistryRequest();
        if (accIncRegistryRequest == null) return false;

        boolean wasError = false;
        try {
            if (request.getProtoVersion() < 6) {
                SyncResponse.AccIncRegistry accIncRegistry = getAccIncRegistry(request.getOrg(), accIncRegistryRequest.dateTime);
                addToResponseSections(accIncRegistry, responseSections);
            } else {
                AccRegistryUpdate accRegistryUpdate = getAccRegistryUpdate(request.getOrg(), accIncRegistryRequest.dateTime);
                addToResponseSections(accRegistryUpdate, responseSections);
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ACC_INC_REGISTRY);
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
        if (interactiveReport == null) return;

        try {
            InteractiveReport resultInteractiveReport = processInteractiveReport(request.getIdOfOrg(), interactiveReport);
            addToResponseSections(resultInteractiveReport, responseSections);
        } catch (Exception e) {
            String message = String.format("processInteractiveReport: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        try {
            InteractiveReportData interactiveReportData = processInteractiveReportData(request.getIdOfOrg());
            addToResponseSections(interactiveReportData, responseSections);
        } catch (Exception e) {
            String message = String
                    .format("Failed to build interactive report data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrganizationComplexesStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SectionRequest sectionRequest = request.findSection(OrganizationComplexesStructureRequest.class);
        if (sectionRequest == null) return;

        OrganizationComplexesStructure organizationComplexesStructure=null;
        try {
            final OrganizationComplexesStructureRequest organizationComplexesStructureRequest = request.getOrganizationComplexesStructureRequest();
            organizationComplexesStructure = getOrganizationComplexesStructureData(request.getOrg(),
                    organizationComplexesStructureRequest.getMaxVersion(), organizationComplexesStructureRequest.getMenuSyncCountDays());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization complexes structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationComplexesStructure = new OrganizationComplexesStructure(100,
                    String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(organizationComplexesStructure, responseSections);
    }

    private void fullProcessingOrganizationStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        final OrganizationStructureRequest organizationStructureRequest = request.findSection(OrganizationStructureRequest.class);
        if (organizationStructureRequest == null) return;

        OrganizationStructure organizationStructureData = null;
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ORGANIZATIONS_STRUCTURE);
            organizationStructureData = getOrganizationStructureData(request.getOrg(),
                    organizationStructureRequest.getMaxVersion(), organizationStructureRequest.isAllOrgs());
        } catch (Exception e) {
            String message = String
                    .format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            organizationStructureData = new OrganizationStructure(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(organizationStructureData, responseSections);
    }

    private void fullProcessingProhibitionsMenu(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        final ProhibitionMenuRequest prohibitionsMenuRequest = request.findSection(ProhibitionMenuRequest.class);
        if (prohibitionsMenuRequest == null) return;

        ProhibitionsMenu prohibitionsMenuData = null;
        try {
            prohibitionsMenuData = getProhibitionsMenuData(request.getOrg(), prohibitionsMenuRequest.getMaxVersion());
        } catch (Exception e) {
            String message = String.format("Failed to build prohibitions menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            prohibitionsMenuData = new ProhibitionsMenu(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }
        addToResponseSections(prohibitionsMenuData, responseSections);
    }

    private void fullProcessingMenuFromOrg(SyncRequest request, Date syncStartTime, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.ReqMenu requestMenu = request.getReqMenu();
        if (requestMenu == null) return;

        try {
            processSyncMenu(request.getIdOfOrg(), requestMenu);
        } catch (Exception e) {
            String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        try {
            SyncResponse.ResMenuExchangeData menuExchangeData = getMenuExchangeData(request.getIdOfOrg(), syncStartTime,
                    DateUtils.addDays(syncStartTime, RESPONSE_MENU_PERIOD_IN_DAYS));
            addToResponseSections(menuExchangeData, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        // Process ComplexRoles
        fullProcessingComplexRoles(responseSections);
    }

    private void fullProcessingGoodsBasicBaskerData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            SectionRequest goodsBasicBasketRequest = request.findSection(GoodsBasicBasketRequest.class);
            if (goodsBasicBasketRequest!=null) {
                GoodsBasicBasketData goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
                addToResponseSections(goodsBasicBasketData, responseSections);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process goods basic basket data , IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingClientsRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, List<Long> errorClientIds) {
        try {
            SyncRequest.ClientRegistryRequest clientRegistryRequest = request.getClientRegistryRequest();
            if (clientRegistryRequest != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.CLIENT_REGISTRY);
                SyncResponse.ClientRegistry clientRegistry = processSyncClientRegistry(request.getIdOfOrg(),
                        clientRegistryRequest, errorClientIds);
                addToResponseSections(clientRegistry, responseSections);
            }
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private boolean fullProcessingPaymentRegistry(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, List<Long> errorClientIds, Long idOfPacket) {
        boolean wasError = false;
        try {
            PaymentRegistry paymentRegistryRequest = request.getPaymentRegistry();
            if (paymentRegistryRequest == null)
                return wasError;

            if (paymentRegistryRequest.getPayments() != null && paymentRegistryRequest.getPayments().hasNext()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    SyncHistory localSyncHistory = syncHistory;
                    if (localSyncHistory == null) {
                        String clientVersion = (request.getClientVersion() == null ? "" : request.getClientVersion());
                        Long packet = (idOfPacket == null ? -1L : idOfPacket);
                        localSyncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(), clientVersion,
                                request.getRemoteAddr());
                    }
                    final String s = String.format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available",
                            request.getIdOfOrg());
                    processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), localSyncHistory, s);
                    throw new Exception("no license slots available");
                }
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.PAYMENT_REGISTRY);
            ResPaymentRegistry resPaymentRegistry = processSyncPaymentRegistry(
                    syncHistory != null ? syncHistory.getIdOfSync() : null, request.getIdOfOrg(), paymentRegistryRequest, errorClientIds);
            addToResponseSections(resPaymentRegistry, responseSections);
        } catch (Exception e) {
            wasError = true;
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
        return wasError;
    }

    private void fullProcessingClientParamsRegistry(SyncRequest request, SyncHistory syncHistory,
            List<Long> errorClientIds) {
        try {
            SyncRequest.ClientParamRegistry clientParamRegistry = request.getClientParamRegistry();
            if (clientParamRegistry != null) {
                processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), clientParamRegistry, errorClientIds);
            }
        } catch (Exception e) {
            String message = String
                    .format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingOrgStructure(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncRequest.OrgStructure orgStructureRequest = request.getOrgStructure();
        if (orgStructureRequest == null) return;
        SyncResponse.ResOrgStructure resOrgStructure=null;
        try {
            resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), orgStructureRequest, syncHistory);
            if (resOrgStructure != null && resOrgStructure.getResult() > 0) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, resOrgStructure.getError());
            }
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void fullProcessingAccountOperationsRegistry(SyncRequest request,
            List<AbstractToElement> responseSections) {
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ACCOUNT_OPERATIONS_REGISTRY);
            if (request.getAccountOperationsRegistry() != null) {
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                ResAccountOperationsRegistry resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
                addToResponseSections(resAccountOperationsRegistry, responseSections);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке AccountOperationsRegistry: ", e);
        }
    }

    private void processMigrantsSectionsWithClientsData(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        Migrants migrantsRequest = request.getMigrants();
        if (migrantsRequest == null) return;
        processMigrantsSections(request, syncHistory, responseSections, null);
        processClientRegistrySectionsForMigrants(request, syncHistory, responseSections);
        processAccRegistrySectionsForMigrants(request, syncHistory, responseSections);
        processAccountRegistrySectionsForMigrants(request, syncHistory, responseSections);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void processAccountRegistrySectionsForMigrants(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        try {
            AccountsRegistry accountsRegistry = RuntimeContext.getAppContext().getBean(AccountsRegistryHandler.class)
                    .handlerMigrants(request.getIdOfOrg());
            addToResponseSections(accountsRegistry, responseSections);
        } catch (Exception e) {
            String message = String
                    .format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }
    }

    private void processAccRegistrySectionsForMigrants(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections) {
        SyncResponse.AccRegistry accRegistryForMigrants;
        try {
            accRegistryForMigrants = getAccRegistryForMigrants(request.getIdOfOrg());
            addToResponseSections(accRegistryForMigrants, responseSections);
        } catch (Exception e) {
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            if(syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            }
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
            if(syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            if(error != null){
                error = true;
            }
            String message = String.format("processMigrants: %s", e.getMessage());
            if(syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            }
            logger.error(message, e);
        }
    }

    private void processAccountOperationsRegistrySections(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, Boolean error) {
        try {
            if (request.getAccountOperationsRegistry() != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ACCOUNT_OPERATIONS_REGISTRY);
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                ResAccountOperationsRegistry resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
                addToResponseSections(resAccountOperationsRegistry, responseSections);
            }
        } catch (Exception e) {
            if(error != null){
                error = true;
            }
            String message = String.format("Ошибка при обработке AccountOperationsRegistry: %s", e.getMessage());
            if(syncHistory != null) {
                processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            }
            logger.error(message, e);
        }
    }

    private void processPaymentRegistrySections(SyncRequest request, SyncHistory syncHistory,
            List<AbstractToElement> responseSections, Boolean error, Long idOfPacket, List<Long> errorClientIds) {
        try {
            if (request.getPaymentRegistry() != null) {
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.PAYMENT_REGISTRY);
                if (request.getPaymentRegistry().getPayments() != null) {
                    if (request.getPaymentRegistry().getPayments().hasNext()) {
                        if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            String clientVersion = (request.getClientVersion() == null ? ""
                                    : request.getClientVersion());
                            Long packet = (idOfPacket == null ? -1L : idOfPacket);
                            if(syncHistory == null) {
                                syncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(),
                                        clientVersion, request.getRemoteAddr());
                            }
                            final String s = String
                                    .format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available",
                                            request.getIdOfOrg());
                            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, s);
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
            if(error != null) {
                error = true;
            }
        }
    }

    private void addToResponseSections(AbstractToElement section,List<AbstractToElement> responseSections) {
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions, specialDatesData,
                resSpecialDates, migrantsData, resMigrants, responseSections);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getReestrTaloonApproval() != null) {
                resReestrTaloonApproval = processReestrTaloonApproval(request.getReestrTaloonApproval());
                reestrTaloonApprovalData = processReestrTaloonApprovalData(request.getReestrTaloonApproval());
            }
        } catch (Exception e) {
            String message = String.format("processReestrTaloonApproval: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                 specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getZeroTransactions() != null) {
                zeroTransactionData = processZeroTransactionsData(request.getZeroTransactions());
                resZeroTransactions = processZeroTransactions(request.getZeroTransactions());
            }
        } catch (Exception e) {
            String message = String.format("processZeroTransactions: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
    }

    private SyncResponse buildSpecialDatesSyncResponse(SyncRequest request) throws Exception {
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getSpecialDates() != null) {
                specialDatesData = processSpecialDatesData(request.getSpecialDates());
                resSpecialDates = processSpecialDates(request.getSpecialDates());
            }
        } catch (Exception e) {
            String message = String.format("processSpecialDates: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        boolean bError = false;

        try {
            if (request.getMigrants() != null) {
                resMigrants = processMigrants(request.getMigrants());
                migrantsData = processMigrantsData(request.getMigrants());

            }
        } catch (Exception e) {
            String message = String.format("processMigrants: %s", e.getMessage());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            clientRegistry = processSyncClientRegistryForMigrants(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            accRegistry = getAccRegistryForMigrants(request.getIdOfOrg());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
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
            processorUtils.createSyncHistoryException(persistenceSessionFactory, request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
    }

    private void processInfoMessageSections(SyncRequest request, List<AbstractToElement> responseSections) {
        InfoMessageRequest infoMessageRequest = request.getInfoMessageRequest();
        if (infoMessageRequest == null) return;

        InfoMessageData infoMessageData = null;
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.INFO_MESSAGE);
            infoMessageData = getInfoMessageData(request.getOrg(), infoMessageRequest.getMaxVersion());
        } catch (Exception e) {
            String message = String.format("Failed to build organization structure, IdOfOrg == %s", request.getIdOfOrg());
            infoMessageData = new InfoMessageData(new ResultOperation(100, String.format("Internal error: %s", e.getMessage())));
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

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

        try {
            processSyncClientParamRegistry(idOfSync, request.getIdOfOrg(), request.getClientParamRegistry(),
                    errorClientIds);
        } catch (Exception e) {
            logger.error(String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }
        // Build client registry
        try {
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.CLIENT_REGISTRY);
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(), request.getClientRegistryRequest(),
                    errorClientIds);
        } catch (Exception e) {
            logger.error(String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(), request.getIdOfOrg());
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


        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

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
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(), request.getIdOfOrg());
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


        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
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
        OrganizationComplexesStructure organizationComplexesStructure = null;
        InteractiveReportData interactiveReportData = null;
        ZeroTransactionData zeroTransactionData = null;
        ResZeroTransactions resZeroTransactions = null;
        SpecialDatesData specialDatesData = null;
        ResSpecialDates resSpecialDates = null;
        MigrantsData migrantsData = null;
        ResMigrants resMigrants = null;

        List<AbstractToElement> responseSections = new ArrayList<AbstractToElement>();

        Boolean bError = false;

        processAccountOperationsRegistrySections(request, null, responseSections, bError);

        processPaymentRegistrySections(request, null, responseSections, bError, idOfPacket, errorClientIds);

        //info messages
        processInfoMessageSections(request, responseSections);

        try {
            if (request.getProtoVersion() < 6) {
                accIncRegistry = getAccIncRegistry(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            } else {
                accRegistryUpdate = getAccRegistryUpdate(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            }
            saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ACC_INC_REGISTRY);

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
                saveLastProcessSectionDateSmart(persistenceSessionFactory, request.getIdOfOrg(), SectionType.ENTER_EVENTS);
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process Enter Events, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        try {
            if (request.getTempCardsOperations() != null) {
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations(), request.getIdOfOrg());
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

        if (RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.sync.emulatorOn", "0").equals("1")) {
            try {
                correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
            } catch (Exception e) {
                String message = String
                        .format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s", request.getIdOfOrg());
                logger.error(message, e);
            }
        }

        updateOrgSyncDate(request.getIdOfOrg());

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure,
                resMenuExchange, resDiary, "", resEnterEvents, resTempCardsOperations, tempCardOperationData,
                resCategoriesDiscountsAndRules, complexRoles, correctingNumbersOrdersRegistry, manager, orgOwnerData,
                questionaryData, goodsBasicBasketData, directiveElement, resultClientGuardian, clientGuardianData,
                accRegistryUpdate, prohibitionsMenu, accountsRegistry, resCardsOperationsRegistry,
                organizationStructure, resReestrTaloonApproval, reestrTaloonApprovalData,
                organizationComplexesStructure, interactiveReportData, zeroTransactionData, resZeroTransactions,
                specialDatesData, resSpecialDates, migrantsData, resMigrants, responseSections);
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
                resAcc = processSyncPaymentRegistryPayment(idOfSync, idOfOrg, Payment, errorClientIds, allocatedClients);
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
            AbstractProcessor processor = new TempCardOperationProcessor(persistenceSession, tempCardsOperations, idOfOrg);
            resTempCardsOperations = (ResTempCardsOperations) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resTempCardsOperations;
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
                persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
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
            persistenceSession = persistenceSessionFactory.openSession();
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

    private ClientGuardianData processClientGuardianDataForMigrants(Long idOfOrg, SyncHistory syncHistory, Long maxVersion) {
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

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            for (ClientGuardianItem item : items) {


                try {
                    Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
                    criteria.add(Restrictions.eq("idOfChildren", item.getIdOfChildren()));
                    criteria.add(Restrictions.eq("idOfGuardian", item.getIdOfGuardian()));
                    ClientGuardian dbClientGuardian = (ClientGuardian) criteria.uniqueResult();
                    if (dbClientGuardian == null) {
                        ClientGuardian clientGuardian = new ClientGuardian(item.getIdOfChildren(), item.getIdOfGuardian());
                        clientGuardian.setDisabled(item.getDisabled());
                        clientGuardian.setVersion(resultClientGuardianVersion);
                        clientGuardian.setDeletedState(item.isDeleted());
                        clientGuardian.setRelation(ClientGuardianRelationType.fromInteger(item.getRelation()));
                        if (item.isDeleted()) {
                            clientGuardian.delete(resultClientGuardianVersion);
                        }
                        persistenceSession.save(clientGuardian);
                        resultClientGuardian.addItem(clientGuardian, 0, null);
                    } else {
                        if (dbClientGuardian.getDeletedState() && !item.isDeleted()) {
                            dbClientGuardian.restore(resultClientGuardianVersion);
                        } else if (item.isDeleted()) {
                            dbClientGuardian.delete(resultClientGuardianVersion);
                        }
                        dbClientGuardian.setRelation(ClientGuardianRelationType.fromInteger(item.getRelation()));
                        dbClientGuardian.setVersion(resultClientGuardianVersion);
                        dbClientGuardian.setDisabled(item.getDisabled());
                        persistenceSession.update(dbClientGuardian);
                        resultClientGuardian.addItem(dbClientGuardian, 0, null);
                    }
                } catch(Exception e) {
                    resultClientGuardian.addItem(item, 100, e.getMessage());
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            logger.error("Error processing ClientsGuardian section: ", ex);
            processorUtils.createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory, "Internal error ClientsGuardian");
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

            Long idOfOrgPayment = payment.getIdOfOrg();
            boolean isFromFriendlyOrg = false;
            if (null != idOfOrgPayment) {
                Long idOfOrganizationPayment = getIdOfOrg(persistenceSession, idOfOrgPayment);

                if (null == idOfOrganizationPayment) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 130,
                            String.format("Organization not found, IdOfOrg == %s, IdOfOrder == %s", idOfOrgPayment,
                                    payment.getIdOfOrder()));
                }

                if(!DAOService.getInstance().isOrgFriendly(idOfOrg, idOfOrgPayment)){
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 150,
                            String.format("Organization is not friendly, IdOfOrg == %s, IdOfOrder == %s", idOfOrgPayment,
                                    payment.getIdOfOrder()));
                }

                idOfOrg = idOfOrgPayment;
                isFromFriendlyOrg = true;
            }

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder());
            Order order = findOrder(persistenceSession, compositeIdOfOrder);

            if (payment.isCommit()) {

                // Check order existence
                //if (DAOUtils.existOrder(persistenceSession, idOfOrg, payment.getIdOfOrder())) {
                if (order != null) {
                    // if order == payment (may be last sync result was not transferred to client)
                    Long orderCardNo = order.getCard() == null ? null : order.getCard().getCardNo();
                    if ((("" + orderCardNo).equals("" + payment.getCardNo())) && (order.getCreateTime()
                            .equals(payment.getTime())) && (order.getSumByCard().equals(payment.getSumByCard()))) {
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
                if (null != cardNo) {
                    card = findCardByCardNo(persistenceSession, cardNo);
                    if (null == card) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 200,
                                String.format("Unknown card, IdOfOrg == %s, IdOfOrder == %s, CardNo == %s", idOfOrg,
                                        payment.getIdOfOrder(), cardNo));
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
                    /*else if (!card.getClient().getIdOfClient().equals(client.getIdOfClient())) {
                        // Specified client isn't the owner of the specified card
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 230, String.format(
                                "Client isn't the owner of the specified card, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                                idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                    }*/
                }
                if (null != client && card != null) {
                    if (Card.ACTIVE_STATE != card.getState()) {
                        Card newCard = client.findActiveCard(persistenceSession, card);
                        if (logger.isWarnEnabled()) {
                            if (!newCard.getIdOfCard().equals(card.getIdOfCard())) {
                                logger.warn(String.format(
                                        "Specified card is inactive. Client: %s, Card: %s. Will use card: %s",
                                        "" + client.getIdOfClient(), card.getIdOfCard(), newCard.getIdOfCard()));
                            }
                        }
                        card = newCard;
                    }
                }
                // If client is specified - check if client is registered for the specified organization
                // or for one of friendly organizations of specified one
                Set<Long> idOfFriendlyOrgSet = getIdOfFriendlyOrg(persistenceSession, idOfOrg);
                if (null != client) {
                    Org clientOrg = client.getOrg();
                    if (!clientOrg.getIdOfOrg().equals(idOfOrg) && !idOfFriendlyOrgSet
                            .contains(clientOrg.getIdOfOrg())) {
                        if(!(MigrantsUtils.getActiveMigrantsByIdOfClient(persistenceSession, client.getIdOfClient()).size() > 0)) {
                            if(!allocatedClients.contains(client.getIdOfClient())) {
                                errorClientIds.add(idOfClient);
                            }
                        }
                        //
                        //return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 220, String.format(
                        //        "Client isn't registered for the specified organization, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                        //        idOfOrg, payment.getIdOfOrder(), idOfClient));
                    }
                }
                // Card check
                if (null != card) {
                    //if (Card.ACTIVE_STATE != card.getState()) {
                    //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 320, String.format(
                    //            "Card is locked, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s", idOfOrg,
                    //            payment.getIdOfOrder(), idOfClient, cardNo));
                    //}
                    //if (null == card.getIssueTime()) {
                    //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 330, String.format(
                    //            "Card wasn't issued, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                    //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                    //}
                    //if (payment.getTime().before(card.getIssueTime())) {
                    //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 340, String.format(
                    //            "Card wasn't issued at payment time, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                    //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                    //}
                    //if (!payment.getTime().before(card.getValidTime())) {
                    //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 350, String.format(
                    //            "Card wasn't valid at payment time, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                    //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                    //}
                }
                // Verify spicified sums to be valid non negative numbers
                if (payment.getSumByCard() < 0 || payment.getSumByCash() < 0 || payment.getSocDiscount() < 0
                        || payment.getRSum() < 0 || payment.getGrant() < 0) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 250,
                            String.format("Negative sum(s) are specified, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
                if (0 != payment.getSumByCard() && card == null) {
                    // Check if card is specified
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 240, String.format(
                            "Payment has card part but doesn't specify CardNo, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                            idOfOrg, payment.getIdOfOrder(), idOfClient));
                }

                SecurityJournalBalance journalBalance = SecurityJournalBalance
                        .getSecurityJournalBalanceDataFromOrder(payment, client, SJBalanceTypeEnum.SJBALANCE_TYPE_ORDER,
                                SJBalanceSourceEnum.SJBALANCE_SOURCE_ORDER, idOfOrg);

                // Create order
                RuntimeContext.getFinancialOpsManager()
                        .createOrderCharge(persistenceSession, payment, idOfOrg, client, card,
                                payment.getConfirmerId(), isFromFriendlyOrg);
                long totalPurchaseDiscount = 0;
                long totalPurchaseRSum = 0;
                long totalLunchRSum = 0;
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
                            purchase.getrPrice() == 0L || payment.getSumByCard() == 0L || payment.getIdOfClient() == null || !MealManager.isSendToExternal);
                    if (purchase.getItemCode() != null) {
                        orderDetail.setItemCode(purchase.getItemCode());
                    }
                    if (purchase.getIdOfRule() != null) {
                        orderDetail.setIdOfRule(purchase.getIdOfRule());
                    }
                    if (purchase.getGuidOfGoods() != null) {
                        Good good = findGoodByGuid(persistenceSession, purchase.getGuidOfGoods());
                        if (good != null) {
                            orderDetail.setGood(good);
                        }
                    }
                    persistenceSession.save(orderDetail);
                    totalPurchaseDiscount += purchase.getDiscount() * Math.abs(purchase.getQty());
                    totalPurchaseRSum += purchase.getrPrice() * Math.abs(purchase.getQty());

                    if (orderDetail.isComplex() || orderDetail.isComplexItem()) {
                        totalLunchRSum += purchase.getrPrice() * Math.abs(purchase.getQty());
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
                if (payment.getRSum() != payment.getSumByCard() + payment.getSumByCash()) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 310,
                            String.format("Invalid sum of order card and cash payments, IdOfOrg == %s, IdOfOrder == %s",
                                    idOfOrg, payment.getIdOfOrder()));
                }

                // Commit data model transaction
                persistenceSession.flush();
                persistenceTransaction.commit();

                SecurityJournalBalance.saveSecurityJournalBalance(journalBalance, true, "OK");

                persistenceTransaction = null;

                // !!!!! ОПОВЕЩЕНИЕ ПО СМС !!!!!!!!
                /* в случае анонимного заказа мы не знаем клиента */
                /* не оповещаем в случае пробития корректировочных заказов */
                if (client != null) {

                    String[] values = generatePaymentNotificationParams(persistenceSession, client, payment);
                    if (payment.getOrderType().equals(OrderTypeEnumType.UNKNOWN) ||
                            payment.getOrderType().equals(OrderTypeEnumType.DEFAULT) ||
                            payment.getOrderType().equals(OrderTypeEnumType.VENDING)) {
                        values = EventNotificationService.attachToValues("isBarOrder", "true", values);
                        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(payment.getTime());
                        values = EventNotificationService.attachToValues("orderEventTime", date, values);
                    } else if (payment.getOrderType().equals(OrderTypeEnumType.PAY_PLAN) || payment.getOrderType()
                            .equals(OrderTypeEnumType.SUBSCRIPTION_FEEDING)) {
                        values = EventNotificationService.attachToValues("isPayOrder", "true", values);
                        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(payment.getTime());
                        values = EventNotificationService.attachToValues("orderEventTime", date, values);
                    } else if (payment.getOrderType().equals(OrderTypeEnumType.REDUCED_PRICE_PLAN) ||
                            payment.getOrderType().equals(OrderTypeEnumType.DAILY_SAMPLE) ||
                            payment.getOrderType().equals(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE) ||
                            payment.getOrderType().equals(OrderTypeEnumType.CORRECTION_TYPE) ||
                            payment.getOrderType().equals(OrderTypeEnumType.WATER_ACCOUNTING)) {
                        values = EventNotificationService.attachToValues("isFreeOrder", "true", values);
                    }
                    values = EventNotificationService.attachTargetIdToValues(payment.getIdOfOrder(), values);
                    values = EventNotificationService
                            .attachSourceOrgIdToValues(idOfOrg, values); //организация из пакета синхронизации
                    long totalBuffetRSum = totalPurchaseRSum - totalLunchRSum;
                    long totalRSum = totalBuffetRSum + totalLunchRSum;
                    values = EventNotificationService.attachToValues("amountPrice",
                            Long.toString(totalBuffetRSum / 100) + ',' + Long.toString(totalBuffetRSum % 100), values);
                    values = EventNotificationService.attachToValues("amountLunch",
                            Long.toString(totalLunchRSum / 100) + ',' + Long.toString(totalLunchRSum % 100), values);
                    values = EventNotificationService.attachToValues("amount",
                            Long.toString(totalRSum / 100) + ',' + Long.toString(totalRSum % 100), values);
                    if (client.getBalance() != null) {
                        values = EventNotificationService.attachToValues("balance",
                                Long.toString(client.getBalance() / 100) + ',' + Long
                                        .toString(Math.abs(client.getBalance()) % 100), values);
                    }
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                            .sendNotificationAsync(client, null, EventNotificationService.MESSAGE_PAYMENT, values,
                                    payment.getOrderDate());

                    List<Client> guardians = findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);

                    if (!(guardians == null || guardians.isEmpty())) {
                        for (Client destGuardian : guardians) {
                            if (DAOReadonlyService.getInstance().allowedGuardianshipNotification(destGuardian.getIdOfClient(),
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
                    Client client = DAOService.getInstance().findClientById(payment.getIdOfClient());
                    SecurityJournalBalance journalBalance = SecurityJournalBalance
                            .getSecurityJournalBalanceDataFromOrder(payment, client, SJBalanceTypeEnum.SJBALANCE_TYPE_PAYMENT,
                                    SJBalanceSourceEnum.SJBALANCE_SOURCE_CANCEL_ORDER, idOfOrg);
                    // Update client balance
                    RuntimeContext.getFinancialOpsManager().cancelOrder(persistenceSession, order);
                    persistenceSession.flush();
                    persistenceTransaction.commit();

                    SecurityJournalBalance.saveSecurityJournalBalance(journalBalance, true, "OK");

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

    private Long getOrderNotificationType(String[] values) throws Exception {
        if(EventNotificationService.findBooleanValueInParams(new String[]{"isBarOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_BAR.getValue();
        } else if(EventNotificationService.findBooleanValueInParams(new String[]{"isPayOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_PAY.getValue();
        } else if(EventNotificationService.findBooleanValueInParams(new String[]{"isFreeOrder"}, values)) {
            return ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_FREE.getValue();
        } else {
            throw new Exception("Не определен тип события");
        }
    }

    private void processSyncClientParamRegistry(SyncHistory syncHistory, Long idOfOrg,
            SyncRequest.ClientParamRegistry clientParamRegistry, List<Long> errorClientIds) throws Exception {
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

                /*ClientGroup clientGroup = orgMap.get(2L).get(clientParamItem.getGroupName());
                *//* если группы нет то создаем *//*
                if(clientGroup == null){
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, idOfOrg, clientParamItem.getGroupName());
                    *//* заносим в хэш - карту*//*
                    nameIdGroupMap.put(clientGroup.getGroupName(),clientGroup);
                }*/
                try {
                    //processSyncClientParamRegistryItem(idOfSync, idOfOrg, clientParamItem, orgMap, version);
                    processSyncClientParamRegistryItem(clientParamItem, orgMap, version, errorClientIds, idOfOrg, allocatedClients);
                } catch (Exception e) {
                    String message = String.format("Failed to process clientParamItem == %s", idOfOrg);
                    if (syncHistory != null) {
                        processorUtils.createSyncHistoryException(persistenceSessionFactory, idOfOrg, syncHistory, message);
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
            HashMap<Long, HashMap<String, ClientGroup>> orgMap, Long version, List<Long> errorClientIds, Long idOfOrg, List<Long> allocatedClients)
            throws Exception {
        boolean ignoreNotifyFlags = RuntimeContext.getInstance().getSmsService().ignoreNotifyFlags();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = findClient(persistenceSession, clientParamItem.getIdOfClient());
            if (clientParamItem.getVersion() != null && clientParamItem.getVersion() < client.getClientRegistryVersion()) {
                return;
            }
            if (!orgMap.keySet().contains(client.getOrg().getIdOfOrg())) {
                if(!(MigrantsUtils.getActiveMigrantsByIdOfClient(persistenceSession, clientParamItem.getIdOfClient()).size() > 0)){
                    if(!allocatedClients.contains(clientParamItem.getIdOfClient())) {
                        errorClientIds.add(client.getIdOfClient());
                        throw new IllegalArgumentException("Client from another organization. idOfClient=" +
                                client.getIdOfClient().toString() + ", idOfOrg=" + idOfOrg.toString() + ", clientParamItem=" +
                                clientParamItem.toString());
                    }
                }
            } else {
            /*if (!client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                throw new IllegalArgumentException("Client from another organization");
            }*/
                client.setFreePayCount(clientParamItem.getFreePayCount());
                client.setFreePayMaxCount(clientParamItem.getFreePayMaxCount());
                client.setLastFreePayTime(clientParamItem.getLastFreePayTime());
                client.setDisablePlanCreationDate(clientParamItem.getDisablePlanCreationDate());
                if (clientParamItem.getExpenditureLimit() != null) {
                    client.setExpenditureLimit(clientParamItem.getExpenditureLimit());
                }

                if (clientParamItem.getAddress() != null) {
                    client.setAddress(clientParamItem.getAddress());
                }
                if (!RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_DISABLE_EMAIL_EDIT) && clientParamItem.getEmail() != null) {
                    String email = clientParamItem.getEmail();
                    //  если у клиента есть емайл и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                    if (client != null && client.getEmail() != null && !client.getEmail().equals(email)) {
                        client.setSsoid("");
                    }
                    client.setEmail(email);
                    if (!StringUtils.isEmpty(clientParamItem.getEmail()) && clientParamItem.getNotifyViaEmail() == null) {
                        client.setNotifyViaEmail(true);
                    }
                }
                if (clientParamItem.getMobilePhone() != null) {
                    String mobile = Client.checkAndConvertMobile(clientParamItem.getMobilePhone());
                    //  если у клиента есть мобильный и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                    if (client != null && client.getMobile() != null && !client.getMobile().equals(mobile)) {
                        client.setSsoid("");
                    }
                    client.setMobile(mobile);
                    logger.info("class : ClientManager, method : modifyClientTransactionFree line : 344, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getPhone());
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
                    logger.info("class : Processor, method : processSyncClientParamRegistryItem line : 3485, idOfClient : " + client.getIdOfClient() + " phone : " + client.getPhone());
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
                if(!ignoreNotifyFlags) {
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

            /* заносим клиента в группу */
                if (clientParamItem.getGroupName() != null) {
                    //TODO УБРАТЬ ВРЕМЕННЫЙ ЗАПРЕТ РЕДАКТИРОВАНИЯ ГРУППЫ В СИНХРЕ
                    if (!(client.getOrg().getDisableEditingClientsFromAISReestr() && ClientGroup.Predefined.parse(clientParamItem.getGroupName()) == null)) {

                    ClientGroup clientGroup = orgMap.get(client.getOrg().getIdOfOrg()).get(clientParamItem.getGroupName());
                    //если группы нет то создаем
                    if (clientGroup == null) {
                        clientGroup = createClientGroup(persistenceSession, client.getOrg().getIdOfOrg(),
                                clientParamItem.getGroupName());
                        // заносим в хэш - карту
                        orgMap.get(client.getOrg().getIdOfOrg()).put(clientGroup.getGroupName(), clientGroup);
                    }

                    if ((clientGroup != null) && (client.getClientGroup() != null) && (clientGroup.getCompositeIdOfClientGroup() != null)) {
                        if (!clientGroup.getCompositeIdOfClientGroup().equals(client.getClientGroup().getCompositeIdOfClientGroup())) {
                            ClientGroupMigrationHistory migrationHistory = new ClientGroupMigrationHistory(client.getOrg(),
                                    client);
                            migrationHistory.setComment(ClientGroupMigrationHistory.MODIFY_IN_ARM.concat(String.format(" (ид. ОО=%s)", idOfOrg)));
                            migrationHistory.setOldGroupId(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup());
                            migrationHistory.setOldGroupName(client.getClientGroup().getGroupName());

                            migrationHistory.setNewGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                            migrationHistory.setNewGroupName(clientGroup.getGroupName());

                            persistenceSession.save(migrationHistory);
                        }

                    }
                    client.setClientGroup(clientGroup);
                    client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    }
                }

                if (clientParamItem.getIsUseLastEEModeForPlan() != null) {
                    client.setUseLastEEModeForPlan(clientParamItem.getIsUseLastEEModeForPlan());
                }
                if (clientParamItem.getBalanceToNotify() != null) {
                    client.setBalanceToNotify((clientParamItem.getBalanceToNotify()));
                }
            }

            String categoriesFromPacket = getCanonicalDiscounts(clientParamItem.getCategoriesDiscounts());
            String categoriesFromClient = getCanonicalDiscounts(client.getCategoriesDiscounts());

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
                    if (!categoriesFromPacket.equals(categoriesFromClient)) {
                        client.setCategoriesDiscounts(categoriesFromPacket);
                        if (!categoriesFromPacket.equals("")) {
                            client.setCategories(categoryDiscountSet);
                        }
                    }
                }
            } else {
                /* Льгота по категориями то очищаем */
                if (!categoriesFromClient.equals("")) {
                    client.setCategoriesDiscounts("");
                    client.setCategories(new HashSet<CategoryDiscount>());
                }
            }

            // Если льготы изменились, то сохраняем историю
            if (!(newClientDiscountMode == oldClientDiscountMode) || !(categoryDiscountSet.equals(categoryDiscountOfClient))) {
                Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
                DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, org, newClientDiscountMode,
                        oldClientDiscountMode, categoriesFromPacket, categoriesFromClient);
                discountChangeHistory.setComment(DiscountChangeHistory.MODIFY_IN_ARM);
                persistenceSession.save(discountChangeHistory);
                client.setLastDiscountsUpdate(new Date());
            }
            client.setDiscountMode(clientParamItem.getDiscountMode());

            client.setClientRegistryVersion(version);

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
            Long idOfOrderMax = (Long) orderMax.get(0),
                    idOfOrderDetail = (Long) orderDetailMax.get(0),
                    idOfEnterEvent = (Long) enterEventMax.get(0),
                    idOfOutcomeMigrRequests = 0L;
            if(migrRequestMax.size() > 0){
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
            return new SyncResponse.CorrectingNumbersOrdersRegistry(idOfOrderMax, idOfOrderDetail, idOfEnterEvent, idOfOutcomeMigrRequests);
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

    private void addClientVersionAndRemoteAddressByOrg(Long idOfOrg, String clientVersion, String remoteAddress) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            updateClientVersionAndRemoteAddressByOrg(persistenceSession, idOfOrg, clientVersion, remoteAddress);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public SyncHistory createSyncHistory(Long idOfOrg, Long idOfPacket, Date startTime, String clientVersion,
            String remoteAddress) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, idOfPacket, clientVersion,
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

    private SyncResponse.AccRegistry getAccRegistryForMigrants(Long idOfOrg)
            throws Exception {
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
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            final Date currentDate = new Date();
            //persistenceSession.refresh(org);
            List<AccountTransactionExtended> accountTransactionList = null;
            try {
                Date dateStartDate = getQueryStartDate(persistenceSession, org.getIdOfOrg(), fromDateTime);
                long time_delta = System.currentTimeMillis();
                accountTransactionList = DAOReadonlyService.getInstance().getAccountTransactionsForOrgSinceTimeV2(org, dateStartDate,
                    currentDate);
                time_delta = System.currentTimeMillis() - time_delta;
                if (time_delta > 10L * 1000L) {
                    logger.error(String.format("Transactions query time = %s ms. IdOfOrg = %s. Period = %3$td.%3$tm.%3$tY %3$tT - %4$td.%4$tm.%4$tY %4$tT (date from packet = %5$td.%5$tm.%5$tY %5$tT)",
                            time_delta, org.getIdOfOrg(), dateStartDate, currentDate, fromDateTime));
                }
                for (AccountTransactionExtended accountTransaction : accountTransactionList) {
                    accRegistryUpdate.addAccountTransactionInfoV2(accountTransaction);
                }
            } catch (Exception e) {
                logger.error("AccRegistryUpdate section failed", e);
                accRegistryUpdate.setResult(new ResultOperation(500, e.getMessage()));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistryUpdate;
    }

    private Date getQueryStartDate(Session session, Long idOfOrg, Date fromDateTime) {
        long difference = System.currentTimeMillis() - fromDateTime.getTime();
        long timeInMillis = ACC_REGISTRY_TIME_CLIENT_IN_MILLIS;
        if (difference > timeInMillis) {
            Date d = processorUtils.getLastProcessSectionDate(session, idOfOrg, SectionType.ACC_INC_REGISTRY);
            return d == null ? fromDateTime : d;
        } else {
            return fromDateTime;
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
            SyncRequest.ClientRegistryRequest clientRegistryRequest, List<Long> errorClientIds) throws Exception {
        SyncResponse.ClientRegistry clientRegistry = new SyncResponse.ClientRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            List<Org> orgList = new ArrayList<Org>(organization.getFriendlyOrg());
            orgList.add(organization);
            List<Client> clients = findNewerClients(persistenceSession, orgList,
                    clientRegistryRequest.getCurrentVersion());
            
            // Добавляем временных посетителей (мигрантов)

            List<Client> migrants = MigrantsUtils.getActiveMigrantsForOrg(persistenceSession, idOfOrg);
            clients.addAll(migrants);

            for (Client client : clients) {
                if (client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
                } else {
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 1));
                }
            }
            List<Long> activeClientsId = findActiveClientsId(persistenceSession, orgList);
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
            for(Client migrant : migrants){
                activeClientsId.add(migrant.getIdOfClient());
            }

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
            persistenceSession = persistenceSessionFactory.openSession();
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

    private OrganizationStructure getOrganizationStructureData(Org org, long version, boolean isAllOrgs) throws Exception {
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
            List<InfoMessage> list = DAOUtils.getInfoMessagesSinceVersion(persistenceSession, org.getIdOfOrg(), version);
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

    private OrganizationComplexesStructure getOrganizationComplexesStructureData(Org org, Long maxVersion, Integer menuSyncCountDays) throws Exception {
        OrganizationComplexesStructure organizationComplexesStructure = new OrganizationComplexesStructure();
        Session session = null;
        Transaction transaction = null;
        try {
            session = persistenceSessionFactory.openSession();
            transaction = session.beginTransaction();
            organizationComplexesStructure.fillComplexesStructureAndApplyChanges(session, org.getIdOfOrg(), maxVersion, menuSyncCountDays);
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
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            persistenceSession.refresh(org);
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
                //MenuDetail menuDetail = DAOUtils.findMenuDetailByLocalId(persistenceSession, menu,
                //        reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
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

            /*if ((bOrgIsMenuExchangeSource && DAOUtils.findMenuDetailByLocalId(persistenceSession, menu, reqMenuDetail.getIdOfMenu()) == null) ||
            (!bOrgIsMenuExchangeSource && DAOUtils.findMenuDetailByPathAndPrice(persistenceSession, menu, reqMenuDetail.getPath(), reqMenuDetail.getPrice()) == null)) {*/
            for (MenuDetail menuDetail : menu.getMenuDetails()) {
                exists = areMenuDetailsEqual(menuDetail, reqMenuDetail);
                if (exists) {
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

                persistenceSession.save(newMenuDetail);
                menu.addMenuDetail(newMenuDetail);

                if (reqMenuDetail.getgBasket() != null) {
                    linkBasket(persistenceSession, newMenuDetail, reqMenuDetail.getgBasket(),
                            menu.getOrg().getIdOfOrg());
                }

                localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), newMenuDetail);
            }
        }
    }

    private void linkBasket(Session session, MenuDetail menuDetail, String guidBasket, Long idOfOrg) {
        GoodsBasicBasket basicBasket = DAOUtils.findBasicGood(session, guidBasket);
        if (basicBasket != null) {
            GoodBasicBasketPrice basicBasketPrice = DAOUtils.findGoodBasicBasketPrice(session, basicBasket, idOfOrg);
            if (basicBasketPrice != null) {
                basicBasketPrice.setMenuDetail(menuDetail);
                GoodBasicBasketPrice.save(session, basicBasketPrice);
            }
        }
    }

    private static boolean areMenuDetailsEqual(MenuDetail menuDetail,
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail) {
        // для организации - источника меню делаем поиска по локальным идентификаторам
        //if (bOrgIsMenuExchangeSource && reqMenuDetail.getIdOfMenu() != null && menuDetail.getLocalIdOfMenu() != null) {
        //    return reqMenuDetail.getIdOfMenu().equals(menuDetail.getLocalIdOfMenu());
        //}
        // для остальных - по путям и ценам, т.к. в клиентах некорректно обновлялось меню, каждый раз перезаписывалось с новыми ид.
        //else {
        return reqMenuDetail.getPath().equals(menuDetail.getMenuPath()) &&
                (reqMenuDetail.getPrice() == null || menuDetail.getPrice() == null
                        || reqMenuDetail.getPrice().longValue() == menuDetail.getPrice().longValue()) && (
                reqMenuDetail.getGroup() == null || menuDetail.getGroupName() == null || reqMenuDetail.getGroup()
                        .equals(menuDetail.getGroupName())) && (reqMenuDetail.getOutput() == null
                || menuDetail.getMenuDetailOutput() == null || reqMenuDetail.getOutput()
                .equals(menuDetail.getMenuDetailOutput()));
        //}
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

    private SyncResponse.ResEnterEvents processSyncEnterEvents(EnterEvents enterEvents, Org org) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResEnterEvents resEnterEvents = new SyncResponse.ResEnterEvents();
        Long idOfOrg;
        Map<String, Long> accessories = new HashMap<String, Long>();
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
                    //Обработали событие от внешней системы

                    EnterEvent enterEvent = new EnterEvent();
                    enterEvent.setCompositeIdOfEnterEvent(new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), idOfOrg));
                    enterEvent.setEnterName(e.getEnterName());
                    enterEvent.setTurnstileAddr(e.getTurnstileAddr());
                    enterEvent.setPassDirection(e.getPassDirection());
                    enterEvent.setEventCode(e.getEventCode());
                    enterEvent.setIdOfCard(e.getIdOfCard());
                    enterEvent.setClient(clientFromEnterEvent);
                    enterEvent.setIdOfTempCard(e.getIdOfTempCard());
                    enterEvent.setEvtDateTime(e.getEvtDateTime());
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
                    //enterEvent.setIdOfClientGroup(e.getIdOfClientGroup());
                    enterEvent.setIdOfClientGroup(
                            clientFromEnterEvent == null ? null : clientFromEnterEvent.getIdOfClientGroup());
                    persistenceSession.save(enterEvent);


                    SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                            0, null);
                    resEnterEvents.addItem(item);

                    if (isDateToday(e.getEvtDateTime()) &&
                            idOfClient != null &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT)) {
                        final EventNotificationService notificationService = RuntimeContext.getAppContext()
                                .getBean(EventNotificationService.class);
                        //final String[] values = generateNotificationParams(persistenceSession, client,
                        //        e.getPassDirection(), e.getEvtDateTime(), guardianId);
                        String[] values = generateNotificationParams(persistenceSession, clientFromEnterEvent, e);
                        values = EventNotificationService.attachTargetIdToValues(e.getIdOfEnterEvent(), values);
                        values = EventNotificationService
                                .attachSourceOrgIdToValues(idOfOrg, values); //организация из пакета синхронизации
                        switch (org.getType()) {
                            case PROFESSIONAL:
                            case SCHOOL: {
                                values = EventNotificationService
                                        .attachEventDirectionToValues(e.getPassDirection(), values);

                                List<Client> guardians = findGuardiansByClient(persistenceSession, idOfClient, null);

                                if (!(guardians == null || guardians.isEmpty())) {
                                    for (Client destGuardian : guardians) {
                                        if (DAOReadonlyService.getInstance().allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                                clientFromEnterEvent.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue())) {
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
                                            if (DAOReadonlyService.getInstance().allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                                    clientFromEnterEvent.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue())) {
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
                    if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS) &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT) && e.getIdOfCard() != null) {
                        Card card = findCardByCardNo(persistenceSession, e.getIdOfCard());
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

    private ResCategoriesDiscountsAndRules processCategoriesDiscountsAndRules(Long idOfOrg,
            CategoriesDiscountsAndRulesRequest categoriesAndDiscountsRequest) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = new ResCategoriesDiscountsAndRules();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            boolean isManyOrgs = categoriesAndDiscountsRequest != null && categoriesAndDiscountsRequest.isManyOrgs();
            resCategoriesDiscountsAndRules.fillData(persistenceSession, idOfOrg, isManyOrgs, categoriesAndDiscountsRequest.getVersionDSZN());
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
            Org organization = getOrgReference(persistenceSession, idOfOrg);
            Long result = organization.getOrgSync().getIdOfPacket();
            organization.getOrgSync().setIdOfPacket(++result);
            //organization.setIdOfPacket(result + 1);
            organization.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
            persistenceSession.update(organization);
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
        //String clientName = client.getPerson().getSurname() + " " + client.getPerson().getFirstName();
        return new String[]{
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                ContractIdFormat.format(client.getContractId()), "surname", client.getPerson().getSurname(),
                "firstName", client.getPerson().getFirstName(), "eventName", eventName, "eventTime", time, "guardian",
                guardianName, "empTime", empTime, "childPassCheckerMark", childPassCheckerMark, "childPassCheckerName",
                childPassCheckerName};
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
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(payment.getTime());
        return new String[]{
                "date", date, "contractId", contractId, "others", CurrencyStringUtils.copecksToRubles(others),
                "complexes", CurrencyStringUtils.copecksToRubles(complexes), "empTime", empTime};
    }

    private static boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (today.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE) &&
                today.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public void runRegularPaymentsIfEnabled(SyncRequest request) {
        if (RuntimeContext.getInstance().isMainNode() && RuntimeContext.getInstance().getSettingsConfig()
                .isEcafeAutopaymentBkEnabled()) {
            processorUtils.runRegularPayments(request);
        }
    }

}