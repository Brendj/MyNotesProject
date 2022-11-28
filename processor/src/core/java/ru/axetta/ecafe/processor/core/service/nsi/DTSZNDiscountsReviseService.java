/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientDiscountHistoryService;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVScheduledStatus;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.partner.revise.ReviseDAOService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.BenefitService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;

@Component
public class DTSZNDiscountsReviseService {

    public static final String NODE = "ecafe.processor.revise.dtszn.node";
    public static final String FIELD_PROPERTY = "ecafe.processor.revise.dtszn.useLastReceiveDate";

    public static final String MODE = "ecafe.processor.revise.dtszn.mode.test";
    public static final String DEFAULT_MODE = "true";

    public static final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.revise.dtszn.cronExpression";

    public static final String MAX_RECORDS_PER_TRANSACTION = "ecafe.processor.revise.dtszn.records";
    public static final Long DEFAULT_MAX_RECORDS_PER_TRANSACTION = 20L;

    public static final String DISABLE_OU_FILTER_PROPERTY = "ecafe.processor.revise.dtszn.disableOUFilter";
    public static final Boolean DEFAULT_DISABLE_OU_FILTER = true;

    public static final String DISABLE_UPDATED_AT_FILTER_PROPERTY = "ecafe.processor.revise.dtszn.disableUpdatedAtFilter";
    public static final Boolean DEFAULT_DISABLE_UPDATED_AT_FILTER = true;

    public static final String OPERATOR_EQUAL = "=";
    public static final String OPERATOR_IN = "in";
    public static final String OPERATOR_LIKE = "like";
    public static final String OPERATOR_IS_NULL = "is-null";
    public static final String OPERATOR_GT = ">";
    public static final String OPERATOR_LT = "<";

    public static final String DSZN_CODE_FILTER = "24,41,48,52,56,66";

    public static final Integer DATA_SOURCE_TYPE_NSI = 1;
    public static final Integer DATA_SOURCE_TYPE_DB = 2;

    public static final String DATA_SOURCE_TYPE_MARKER_NSI = "nsiir";
    public static final String DATA_SOURCE_TYPE_MARKER_OU = "ou";
    public static final String DATA_SOURCE_TYPE_MARKER_ARM = "arm";

    public static final String OTHER_DISCOUNT_DESCRIPTION = "Иное";
    public static final Long OTHER_DISCOUNT_CODE = 0L;

    private static Logger logger = LoggerFactory.getLogger(DTSZNDiscountsReviseService.class);
    private static ReviseLogger reviseLogger = RuntimeContext.getAppContext().getBean(ReviseLogger.class);

    private Boolean isTest;
    private Long maxRecords;

    public boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(NODE);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void run() throws Exception {
        if (!isOn()) {
            return;
        }
        runTask();
    }

    public void runTask() throws Exception {
        runTaskDB();
    }

    public void updateApplicationsForFoodTask(boolean forTest, String guid) throws Exception {
        updateApplicationsForFoodTaskService(forTest, null, guid);
    }

    @PostConstruct
    public void init() {
        try {
            maxRecords = getMaxRecords();
        } catch (Exception e) {
            logger.error("DTSZNDiscountsReviseService initialization error", e);
        }
    }

    private Long getMaxRecords() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String recordsString = runtimeContext.getConfigProperties().getProperty(MAX_RECORDS_PER_TRANSACTION);
        if (null == recordsString) {
            return DEFAULT_MAX_RECORDS_PER_TRANSACTION;
        }
        Long records;
        try {
            records = Long.parseLong(recordsString);
        } catch (NumberFormatException e) {
            logger.error(String.format("Unable to parse max records value from config: %s", recordsString));
            return DEFAULT_MAX_RECORDS_PER_TRANSACTION;
        }
        return records;
    }

    public void updateApplicationsForFoodTaskService(boolean forTest, String serviceNumber, String guid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            transaction = session.beginTransaction();

            List<ApplicationForFood> applicationForFoodList;
            if (serviceNumber != null && !serviceNumber.isEmpty()) {
                applicationForFoodList = DAOUtils.getApplicationForFoodListByStatusAndServiceNumber(session,
                        new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_SENDED), false,
                        serviceNumber);
            } else {
                applicationForFoodList = DAOUtils.getApplicationForFoodListByStatus(session,
                        new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_SENDED), false, guid);
            }

            ETPMVService service = RuntimeContext.getAppContext().getBean(ETPMVService.class);
            Date fireTime = new Date();

            logger.info(String.format("%d applications was find for update", applicationForFoodList.size()));
            Integer counter = 1;
            for (ApplicationForFood applicationForFood : applicationForFoodList) {
                ClientDtisznDiscountInfo info = null;
                try {
                    if (null == transaction || !transaction.isActive()) {
                        transaction = session.beginTransaction();
                    }

                    if (applicationForFood.isNewFormat()) {
                        //todo не обрабатываем заявления в новом формате??
                        logger.info(String.format("Application with number = %s has new format and skipped",
                                applicationForFood.getServiceNumber()));
                        continue;
                    }
                    Long dtsznCode = (null == applicationForFood.getApplicationDiscountOldFormat().getDtisznCode()) ? OTHER_DISCOUNT_CODE
                            : applicationForFood.getApplicationDiscountOldFormat().getDtisznCode();

                    info = DAOUtils
                            .getDTISZNDiscountInfoByClientAndCode(session, applicationForFood.getClient(), dtsznCode);
                    if (null == info) {
                        logger.info(String.format("Application with number = %s skipped for waiting discount",
                                applicationForFood.getServiceNumber()));
                        continue;
                    }
                    Boolean isDiscountOk;
                    Boolean isDateOk = true;

                    if (applicationForFood.getLastUpdate().getTime() >= info.getLastReceivedDate().getTime()) {
                        isDateOk = false;
                    }
                    isDiscountOk = info.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) && CalendarUtils
                            .betweenOrEqualDate(fireTime, info.getDateStart(), info.getDateEnd()) && !info
                            .getArchived();

                    if (!isDateOk) {
                        logger.info(String.format("Application with number = %s skipped for waiting discount",
                                applicationForFood.getServiceNumber()));
                        logger.info(String.format("Updating applications: %d/%d", counter++,
                                applicationForFoodList.size()));
                        continue;
                    }

                    Long applicationVersion = DAOUtils.nextVersionByApplicationForFood(session);
                    Long historyVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);

                    LinkedList<ETPMVScheduledStatus> statusList = new LinkedList<ETPMVScheduledStatus>();
                    //7705
                    ApplicationForFoodStatus status = new ApplicationForFoodStatus(
                            ApplicationForFoodState.INFORMATION_REQUEST_RECEIVED);
                    applicationForFood = DAOUtils
                            .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                                    applicationVersion, historyVersion, false);
                    statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                            status.getApplicationForFoodState()));
                    if (isDiscountOk) {
                        //1052
                        status = new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING);
                        applicationForFood = DAOUtils
                                .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                                        applicationVersion, historyVersion, false);
                        statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                                status.getApplicationForFoodState()));

                        //1075
                        status = new ApplicationForFoodStatus(ApplicationForFoodState.OK);
                        applicationForFood = DAOUtils
                                .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                                        applicationVersion, historyVersion, true);
                        statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                                status.getApplicationForFoodState()));
                        applicationForFood.setDiscountDateStart(info.getDateStart());
                        applicationForFood.setDiscountDateEnd(info.getDateEnd());
                        session.update(applicationForFood);
                    } else {
                        //1080.3
                        status = new ApplicationForFoodStatus(ApplicationForFoodState.DENIED_PASSPORT);
                        applicationForFood = DAOUtils
                                .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                                        applicationVersion, historyVersion, true);
                        statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                                status.getApplicationForFoodState()));
                        //Отправка уведомления клиенту
                        Client client = applicationForFood.getClient();
                        //ClientDtisznDiscountInfo clientDtisznDiscountInfo = DAOUtils
                        //        .getDTISZNDiscountInfoByClientAndCode(session, client,
                        //                applicationForFood.getDtisznCode());
                        String[] values = new String[]{
                                BenefitService.SERVICE_NUMBER, applicationForFood.getServiceNumber(),
                                BenefitService.DATE, CalendarUtils.dateToString(applicationForFood.getCreatedDate()),
                                BenefitService.DTISZN_CODE, info.getDtisznCode().toString(),
                                BenefitService.DTISZN_DESCRIPTION, info.getDtisznDescription()};
                        values = EventNotificationService.attachGenderToValues(client.getGender(), values);
                        if (forTest) {
                            values = attachValue(values, "TEST", "true");
                        }

                        List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);
                        if (!(guardians == null || guardians.isEmpty())) {
                            //Оправка всем представителям
                            for (Client destGuardian : guardians) {
                                if (DAOReadonlyService.getInstance()
                                        .allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                                client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())) {
                                    RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                                            .sendNotification(destGuardian, client, EventNotificationService.NOTIFICATION_PREFERENTIAL_FOOD, values,
                                                    new Date());
                                }
                            }
                        } else {
                            //Отправка только клиенту
                            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                                    .sendNotification(client, null,
                                            EventNotificationService.NOTIFICATION_PREFERENTIAL_FOOD, values,
                                            new Date());
                        }
                    }
                    logger.info(String.format(
                            "Application with number updated to %s ClientDtisznDiscountInfo{status = %s, dateStart = %s, dateEnd = %s}",
                            isDiscountOk ? "ok" : "denied", info.getStatus().toString(), info.getDateStart().toString(),
                            info.getDateEnd().toString()));
                    service.sendStatusesAsync(statusList);
                    logger.info(
                            String.format("Updating applications: %d/%d", counter++, applicationForFoodList.size()));

                } catch (Exception e) {
                    logger.error(String.format("Error in updateApplicationsForFoodTask: "
                                    + "unable to update application for food for client with id=%d, idOfClientDTISZNDiscountInfo={%s}",
                            applicationForFood.getClient().getIdOfClient(), info), e);
                } finally {
                    if (null != transaction && transaction.isActive()) {
                        transaction.commit();
                        transaction = null;
                    }
                }
            }

            if (null != transaction && transaction.isActive()) {
                transaction.commit();
                transaction = null;
            }
        } catch (Exception e) {
            logger.error("Error in update discounts", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void updateApplicationsForFoodKafkaService(ApplicationForFood applicationForFood) {
        //Обновление заявления
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            LinkedList<ETPMVScheduledStatus> statusList = new LinkedList<ETPMVScheduledStatus>();
            //1052
            ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING);
            Long applicationVersion = DAOUtils.nextVersionByApplicationForFood(session);
            Long historyVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);
            applicationForFood = DAOUtils
                    .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status,
                            applicationVersion, historyVersion, true);
            statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                    status.getApplicationForFoodState()));

            //Назначение новых ЛК
            Client client = session.load(Client.class, applicationForFood.getClient().getIdOfClient());

            ApplicationForFoodDiscount infomax = applicationForFood.getAppointedDiscount();
            for (ApplicationForFoodDiscount discount : applicationForFood.getDtisznCodes()) {
                if (!discount.getConfirmed()) continue;
                if (discount.equals(infomax)) continue;
                DiscountManager.addDtisznDiscount(session, client, discount.getDtisznCode(), discount.getStartDate(),
                        discount.getEndDate(), false, DiscountChangeHistory.MODIFY_IN_SERVICE);
            }
            if (infomax != null) {
                DiscountManager.addDtisznDiscount(session, client, infomax.getDtisznCode(), infomax.getStartDate(),
                        infomax.getEndDate(), true, DiscountChangeHistory.MODIFY_IN_SERVICE);
                applicationForFood.setDiscountDateStart(infomax.getStartDate());
                applicationForFood.setDiscountDateEnd(infomax.getEndDate());
            } else {
                logger.info("Not found appointed discount for " + applicationForFood.getServiceNumber());
            }

            //1075
            ApplicationForFoodStatus status2 = new ApplicationForFoodStatus(ApplicationForFoodState.OK);
            applicationForFood = DAOUtils
                    .updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status2,
                            applicationVersion, historyVersion, true);
            statusList.add(new ETPMVScheduledStatus(applicationForFood.getServiceNumber(),
                    status2.getApplicationForFoodState()));
            session.update(applicationForFood);
            transaction.commit();
            transaction = null;
            ETPMVService service = RuntimeContext.getAppContext().getBean(ETPMVService.class);
            service.sendStatusesAsync(statusList);
        } catch (Exception e) {
            logger.error("Error in update ApplicationsForFoodKafkaService", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public ApplicationForFoodDiscount getMaxPriorityDiscount(Session session, ApplicationForFoodDiscount applicationForFoodDiscount, ApplicationForFoodDiscount applicationForFoodDiscountActive)
    {
        CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, applicationForFoodDiscount.getDtisznCode().longValue());
        Integer prior = categoryDiscountDSZN.getPriority();
        if (applicationForFoodDiscountActive == null)
        {
            applicationForFoodDiscountActive = applicationForFoodDiscount;
        } else
        {
            if (applicationForFoodDiscountActive.getEndDate().equals(applicationForFoodDiscount.getEndDate()))
            {
                //Сравниваем приоритеты
                CategoryDiscountDSZN categoryDiscountDSZN1 =
                        DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, applicationForFoodDiscountActive.getDtisznCode().longValue());
                if (categoryDiscountDSZN1 != null) {
                    if (prior > categoryDiscountDSZN1.getPriority()) {
                        applicationForFoodDiscountActive = applicationForFoodDiscount;
                    }
                }
            }
            else {
                if (applicationForFoodDiscountActive.getEndDate().before(applicationForFoodDiscount.getEndDate())) {
                    applicationForFoodDiscountActive = applicationForFoodDiscount;
                }
            }
        }
        return applicationForFoodDiscountActive;
    }

    public void updateApplicationsForFoodTaskServiceNotification(String serviceNumber)
            throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            transaction = session.beginTransaction();

            ApplicationForFood applicationForFood = DAOUtils
                    .getApplicationForFood(session, serviceNumber);
            //todo не обрабатываем заявления в новом формате??
            if (applicationForFood.isNewFormat()) return;

            Long dtsznCode = (null == applicationForFood.getApplicationDiscountOldFormat().getDtisznCode()) ? OTHER_DISCOUNT_CODE
                    : applicationForFood.getApplicationDiscountOldFormat().getDtisznCode();

            ClientDtisznDiscountInfo info = DAOUtils
                    .getDTISZNDiscountInfoByClientAndCode(session, applicationForFood.getClient(), dtsznCode);
            //Отправка уведомления клиенту
            Client client = applicationForFood.getClient();
            String[] values = new String[]{
                    BenefitService.SERVICE_NUMBER, applicationForFood.getServiceNumber(), BenefitService.DATE,
                    CalendarUtils.dateToString(applicationForFood.getCreatedDate()), BenefitService.DTISZN_CODE,
                    info.getDtisznCode().toString(), BenefitService.DTISZN_DESCRIPTION, info.getDtisznDescription()};
            values = EventNotificationService.attachGenderToValues(client.getGender(), values);
            values = attachValue(values, "TEST", "true");


            List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);
            if (!(guardians == null || guardians.isEmpty())) {
                //Оправка всем представителям
                for (Client destGuardian : guardians) {
                    if (DAOReadonlyService.getInstance()
                            .allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                                    client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())) {
                        RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                                .sendNotification(destGuardian, client, EventNotificationService.NOTIFICATION_PREFERENTIAL_FOOD, values, new Date());
                    }
                }
            } else {
                //Отправка только клиенту
                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                        .sendNotification(client, null, EventNotificationService.NOTIFICATION_PREFERENTIAL_FOOD, values,
                                new Date());
            }
         } catch(Exception e)
        {
            logger.error("Error in update discounts for one Application", e);
            throw e;
        } finally
        {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
}

    private String[] attachValue(String[] values, String name, String value) {
        String[] newValues = new String[values.length + 2];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 2] = name;
        newValues[newValues.length - 1] = value;
        return newValues;
    }

    private ApplicationForFoodDiscount getApplicationForFoodDiscountByInfo(ApplicationForFood application,
                                                                           ClientDtisznDiscountInfo info) {
        for (ApplicationForFoodDiscount discount : application.getDtisznCodes()) {
            if ((discount.getDtisznCode() == null && info.getDtisznCode().equals(0L))
            || discount.getDtisznCode().equals(info.getDtisznCode().intValue())) {
                return discount;
            }
        }
        return null;
    }

    public void updateApplicationForFood(Session session, Client client, List<ClientDtisznDiscountInfo> infoList) {
        ApplicationForFood application = DAOUtils.findActiveApplicationForFoodByClient(session, client);
        if (null == application
                || !application.getStatus().equals(new ApplicationForFoodStatus(ApplicationForFoodState.OK))) {
            return;
        }

        Date fireTime = new Date();

        try {
            Boolean isOk = false;

            ApplicationForFoodDiscount applicationForFoodDiscount = null;
            for (ClientDtisznDiscountInfo info : infoList) {
                applicationForFoodDiscount = getApplicationForFoodDiscountByInfo(application, info);
                if (applicationForFoodDiscount == null) continue;
                if (((applicationForFoodDiscount.getDtisznCode() == null && info.getDtisznCode().equals(0L))
                        || applicationForFoodDiscount.getDtisznCode().equals(info.getDtisznCode().intValue())) && info.getStatus()
                        .equals(ClientDTISZNDiscountStatus.CONFIRMED) && fireTime.before(info.getDateEnd())
                        && !info.getArchived()) {
                    isOk = true;
                    break;
                }
            }
            if (applicationForFoodDiscount == null) return;

            if (application.getStatus().equals(new ApplicationForFoodStatus(ApplicationForFoodState.OK))) {
                if (!isOk) {
                    applicationForFoodDiscount.removeConfirmed();
                    session.update(applicationForFoodDiscount);
                    if (!application.allDiscountsConfirmed()) {
                        Long applicationVersion = DAOUtils.nextVersionByApplicationForFood(session);
                        application.setArchived(true);
                        application.setVersion(applicationVersion);
                        session.update(application);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Error in updateApplicationForFood: "
                            + "unable to update application for food for client with id=%d, idOfClientDTISZNDiscountInfo={%s}",
                    client.getIdOfClient(), StringUtils.join(infoList, ",")), e);
        }
    }

    public void processDiscounts(Session session, Client client, List<ClientDtisznDiscountInfo> infoList,
            Long otherDiscountCode) throws Exception {

        Date fireTime = new Date();

        Set<CategoryDiscount> oldDiscounts = client.getCategories();
        Set<CategoryDiscount> newDiscounts;

        //String[] discounts = StringUtils.split(client.getCategoriesDiscounts(), ',');
        List<Long> categoryDiscountsList = new ArrayList<Long>(oldDiscounts.size());
        for (CategoryDiscount categoryDiscount : oldDiscounts) {
            Long discountCode = categoryDiscount.getIdOfCategoryDiscount();

            List<CategoryDiscountDSZN> categoryDiscountDSZNList = DAOUtils
                    .getCategoryDiscountDSZNByCategoryDiscountCode(session, discountCode);
            if (categoryDiscountDSZNList.isEmpty() || discountCode.equals(otherDiscountCode)) {
                categoryDiscountsList.add(discountCode);
                continue;
            }
            List<Long> discountCodeList = new ArrayList<Long>();
            for (CategoryDiscountDSZN categoryDiscountDSZN : categoryDiscountDSZNList) {
                discountCodeList.add(categoryDiscountDSZN.getCode().longValue());
            }

            List<ClientDtisznDiscountInfo> clientDtisznDiscountInfoList = DAOUtils
                    .getDTISZNDiscountInfoByClientAndCode(session, client, discountCodeList);

            Boolean isOk = true;

            for (ClientDtisznDiscountInfo info : clientDtisznDiscountInfoList) {
                isOk &= info.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) && CalendarUtils
                        .betweenOrEqualDate(fireTime, info.getDateStart(), info.getDateEnd()) && !info.getArchived();
            }

            if (!clientDtisznDiscountInfoList.isEmpty() && isOk) {
                categoryDiscountsList.add(discountCode);
            }
        }

        List<Long> discountCodes = new ArrayList<Long>(categoryDiscountsList);
        for (ClientDtisznDiscountInfo info : infoList) {
            if (!info.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) || !CalendarUtils
                    .betweenOrEqualDate(fireTime, info.getDateStart(), info.getDateEnd()) || info.getArchived()) {
                continue;
            }

            CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                    .getCategoryDiscountDSZNByDSZNCode(session, info.getDtisznCode());

            if (null != categoryDiscountDSZN && null != categoryDiscountDSZN.getCategoryDiscount()) {
                if (!discountCodes.contains(categoryDiscountDSZN.getCategoryDiscount().getIdOfCategoryDiscount())) {
                    discountCodes.add(categoryDiscountDSZN.getCategoryDiscount().getIdOfCategoryDiscount());
                }
            }
        }
        newDiscounts = ClientManager.getCategoriesSet(session, StringUtils.join(discountCodes, ","));
        Integer oldDiscountMode = client.getDiscountMode();
        Integer newDiscountMode =
                newDiscounts.size() == 0 ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;

        if (!oldDiscountMode.equals(newDiscountMode) || !oldDiscounts.equals(newDiscounts)) {
            try {
                DiscountManager.renewDiscounts(session, client, newDiscounts, oldDiscounts,
                        DiscountChangeHistory.MODIFY_IN_REGISTRY);
            } catch (Exception e) {
                logger.error(String.format("Unexpected discount code for client with id=%d", client.getIdOfClient()),
                        e);
            }
        }
        updateApplicationForFood(session, client, infoList);
    }

    public void runTaskPart2() throws Exception {
        runTaskPart2(null, null);
    }

    private void runOneClient(Session session, Long idOfClient, Long otherDiscountCode)
            throws Exception {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Client client = (Client) session.load(Client.class, idOfClient);
            List<ClientDtisznDiscountInfo> clientInfoList = DAOUtils.getDTISZNDiscountsInfoByClient(session, client);
            if (!clientInfoList.isEmpty()) {
                processDiscounts(session, client, clientInfoList, otherDiscountCode);
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
        }
    }

    public void runTaskPart2(Date startDate, String guid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            //session.setFlushMode(FlushMode.MANUAL);
            transaction = session.beginTransaction();

            Long otherDiscountCode = DAOUtils.getOtherDiscountCode(session);
            List<Long> clientList;
            if (null == startDate) {
                clientList = DAOUtils.getUniqueClientIdFromClientDTISZNDiscountInfo(session);
            } else {
                clientList = DAOUtils.getUniqueClientIdFromClientDTISZNDiscountInfoSinceDate(session, startDate, guid);
            }
            transaction.commit();
            transaction = null;
            Integer clientCounter = 1;

            List<Long> finalDopProcessing = new LinkedList<>();
            List<Long> currentProcessing = new LinkedList<>();
            for (Long idOfClient : clientList) {
                try {
                    currentProcessing.add(idOfClient);
                    runOneClient(session, idOfClient, otherDiscountCode);
                    if (0 == clientCounter % maxRecords) {
                        session.clear();
                        currentProcessing.clear();
                    }
                    logger.info(String.format("Updating discounts for clients: client %d/%d", clientCounter++,
                            clientList.size()));
                } catch (StaleObjectStateException e) {
                    finalDopProcessing.addAll(currentProcessing);
                    session.clear();
                } catch (Exception e) {
                    logger.error(String.format("Error in update discounts for client %s", idOfClient), e);
                }
            }

            if (finalDopProcessing.size() > 0) {
                for (Long idOfClient : finalDopProcessing) {
                    runOneClient(session, idOfClient, otherDiscountCode);
                }
                session.clear();
            }

        } catch (Exception e) {
            logger.error("Error in update discounts", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void updateArchivedFlagForDiscounts() throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Date fireTime = new Date();

            Long nextVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);

            Query query = session.createSQLQuery(
                    "update cf_client_dtiszn_discount_info set archived = 1, sendnotification = false, version = :version, lastupdate = :lastUpdate "
                            + "where (lastreceiveddate not between :start and :end or lastreceiveddate is null) and dtiszncode <> :otherDiscountCode");
            query.setParameter("start", CalendarUtils.startOfDay(fireTime).getTime());
            query.setParameter("end", CalendarUtils.endOfDay(fireTime).getTime());
            query.setParameter("version", nextVersion);
            query.setParameter("lastUpdate", fireTime.getTime());
            query.setParameter("otherDiscountCode", OTHER_DISCOUNT_CODE);
            int rows = query.executeUpdate();
            if (0 != rows) {
                logger.info(String.format("%d discounts marked as archived", rows));
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in update archived flag", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void runTaskDB() throws Exception {
        runTaskDB(null);
    }

    private boolean isStudent(Client client) {
        if (client == null) {
            return false;
        }
        try {
            return client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                    < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() || client.getClientGroup()
                    .getCompositeIdOfClientGroup().getIdOfClientGroup()
                    .equals(ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
        } catch (Exception e) {
            logger.error("Error in isStudent method: ", e);
            return false;
        }
    }

    /*private ClientDtisznDiscountInfo getDTISZNDiscountInfoByClientAndCode(List<ClientDtisznDiscountInfo> discountInfoList,
                                                                          Client client, Long dtisznCode) {
        return discountInfoList.stream()
                .filter(i -> i.getClient().equals(client) && i.getDtisznCode().equals(dtisznCode)).findFirst().orElse(null);
    }

    private List<ClientDtisznDiscountInfo> getDTISZNDiscountInfoByClient(List<ClientDtisznDiscountInfo> discountInfoList,
                                                                          Client client) {
        return discountInfoList.stream()
                .filter(i -> i.getClient().equals(client)).collect(Collectors.toList());
    }*/

    //todo Нужно добавить "Процессинг проверяет наличие признака "отменена" у ЛК
    // 3.1. если признак установлен - далее п. 4."

    public void runTaskDB(String guid) throws Exception {
        ReviseDAOService.DiscountItemsWithTimestamp discountItemList;
        ClientDiscountHistoryService service = RuntimeContext.getAppContext().getBean(ClientDiscountHistoryService.class);

        if (StringUtils.isEmpty(guid)) {
            Date deltaDate = null;
            try {
                deltaDate = CalendarUtils.parseDateWithDayTime(DAOReadonlyService.getInstance().getReviseLastDate());
            } catch (Exception ignore) {
            }
            if (deltaDate == null) {
                deltaDate = CalendarUtils.addHours(new Date(), -24);
            }
            deltaDate = CalendarUtils.addSeconds(deltaDate,1);

            discountItemList = RuntimeContext.getAppContext().getBean(ReviseDAOService.class)
                    .getDiscountsUpdatedSinceDate(deltaDate);
        } else {
            discountItemList = RuntimeContext.getAppContext().getBean(ReviseDAOService.class).getDiscountsByGUID(guid);
            if (discountItemList.getItems().isEmpty()) {
                throw new Exception(String.format("По гуиду \"%s\" ничего не найдено", guid));
            }
        }

        if (!StringUtils.isEmpty(guid)) {
            final Set<Integer> dtsznCodes = new HashSet<>();

            for (ReviseDAOService.DiscountItem item : discountItemList.getItems()) {
                dtsznCodes.add(item.getDsznCode());
            }

            List<ReviseDAOService.DiscountItem> discountItemListTemp
                    = new ArrayList<ReviseDAOService.DiscountItem>(discountItemList.getItems());

            for (Integer dtsznCode : dtsznCodes) {
                ReviseDAOService.DiscountItem prevMax = null;
                for (ReviseDAOService.DiscountItem item : discountItemList.getItems()) {
                    if (item.getDsznCode().equals(dtsznCode)) {
                        if (prevMax == null) {
                            prevMax = item;
                            continue;
                        }
                        if (item.getUpdatedAt().after(prevMax.getUpdatedAt())) {
                            discountItemListTemp.remove(prevMax);
                            prevMax = item;
                        } else {
                            discountItemListTemp.remove(item);
                        }
                    }
                }
            }

            discountItemList.setItems(discountItemListTemp);
        }

        Date fireTime = new Date();
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);

            transaction = session.beginTransaction();
            Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);

            Integer counter = 0;
            Map<Long, Boolean> orgData = new HashMap<>();
            for (ReviseDAOService.DiscountItem item : discountItemList.getItems()) {

                if (null == transaction || !transaction.isActive()) {
                    transaction = session.beginTransaction();
                }
                Client client = null;
                if (!StringUtils.isEmpty(item.getMeshGUID())) {
                    client = DAOUtils.findClientByMeshGuid(session, item.getMeshGUID());
                }
                if (client == null) {
                    client = DAOUtils.findClientByGuid(session, item.getRegistryGUID());
                }
                logger.info(String.format("Processing record %s from %s. IdOfClient=%s", counter, discountItemList.getItems().size(),
                        client == null ? "null" : client.getIdOfClient()));
                if (!isStudent(client)) {
                    //logger.info(String.format("Client with guid = { %s } not found", item.getPerson().getId()));
                    if (0 == counter++ % maxRecords) {
                        transaction.commit();
                        transaction = null;
                    }
                    logger.info("Client is null or !student");
                    continue;
                }

                Boolean changesDSZN = orgData.get(client.getOrg().getIdOfOrg());
                if (changesDSZN == null) {
                    orgData.put(client.getOrg().getIdOfOrg(), client.getOrg().getChangesDSZN());
                    changesDSZN = orgData.get(client.getOrg().getIdOfOrg());
                }
                if (!changesDSZN) {
                    logger.info(String.format(
                            "Organization has no \"Changes DSZN\" flag. Client with guid = { %s } was skipped",
                            item.getRegistryGUID()));
                    if (0 == counter++ % maxRecords) {
                        transaction.commit();
                        transaction = null;
                    }
                    continue;
                }

                ClientDtisznDiscountInfo discountInfo = DAOUtils.getDTISZNDiscountInfoByClientAndCode(session, client,
                        item.getDsznCode().longValue());

                if (null == discountInfo) {
                    discountInfo = new ClientDtisznDiscountInfo(client, item.getDsznCode().longValue(), item.getTitle(),
                            item.getBenefitConfirm() ? ClientDTISZNDiscountStatus.CONFIRMED
                                    : ClientDTISZNDiscountStatus.NOT_CONFIRMED, item.getSdDszn(), item.getFdDszn(),
                            item.getUpdatedAt(), DATA_SOURCE_TYPE_MARKER_OU, clientDTISZNDiscountVersion, item.getUpdatedAt(), true);
                    discountInfo.setArchived(item.getDeleted() || item.getFd().getTime() <= fireTime.getTime()
                            || item.getFdDszn().getTime() <= fireTime.getTime() || !item.getBenefitConfirm());
                    session.save(discountInfo);
                    logger.info("Created ClientDtisznDiscountInfo");
                } else {
                    if (discountInfo.getDtisznCode().equals(item.getDsznCode().longValue())) {
                        // Проверяем поля: статус льготы, дата начала действия льготы ДТиСЗН, дата окончания действия льготы ДТиСЗН.
                        // Перезаписываем те поля, которые отличаются в Реестре от ИС ПП (берем из Реестров).
                        boolean wasModified = false;

                        if (discountInfo.getUpdatedAt() != null) {
                            if (discountInfo.getUpdatedAt().before(item.getUpdatedAt())) {
                                discountInfo.setUpdatedAt(item.getUpdatedAt());
                                wasModified = true;
                            }
                        }
                        if (!discountInfo.getDateStart().equals(item.getSdDszn())) {
                            discountInfo.setDateStart(item.getSdDszn());
                            wasModified = true;
                        }
                        if (!discountInfo.getDateEnd().equals(item.getFdDszn())) {
                            discountInfo.setDateEnd(item.getFdDszn());
                            wasModified = true;
                        }
                        if (item.getBenefitConfirm() && (
                                discountInfo.getStatus().equals(ClientDTISZNDiscountStatus.NOT_CONFIRMED)
                                        || discountInfo.getArchived())) {
                            discountInfo.setStatus(ClientDTISZNDiscountStatus.CONFIRMED);
                            discountInfo.setArchived(false);
                            discountInfo.setActive(true);
                            wasModified = true;
                        }
                        if (!item.getBenefitConfirm() && (
                                discountInfo.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) || !discountInfo
                                        .getArchived())) {
                            discountInfo.setStatus(ClientDTISZNDiscountStatus.NOT_CONFIRMED);
                            discountInfo.setArchived(true);
                            wasModified = true;
                        }
                        if (item.getDeleted() || item.getFd().getTime() <= fireTime.getTime()
                                || item.getFdDszn().getTime() <= fireTime.getTime()) {
                            discountInfo.setArchived(true);
                            wasModified = true;
                        } else if (item.getBenefitConfirm() && discountInfo.getArchived()) {
                            discountInfo.setArchived(false);
                            discountInfo.setActive(true);
                            wasModified = true;
                        }
                        discountInfo.setLastReceivedDate(new Date());
                        if (wasModified) {
                            discountInfo.setVersion(clientDTISZNDiscountVersion);
                            discountInfo.setLastUpdate(new Date());
                            if (discountInfo.getUpdatedAt() == null) {
                                discountInfo.setUpdatedAt(item.getUpdatedAt());
                            }
                        }
                        discountInfo.setSource(DATA_SOURCE_TYPE_MARKER_OU);

                        session.merge(discountInfo);

                        logger.info(String.format("ClientDtisznDiscountInfo OK. wasModified: %s", wasModified ? "true" : "false"));
                        if(wasModified){
                            service.saveChangeHistoryByDiscountInfo(session, discountInfo,
                                    DiscountChangeHistory.MODIFY_IN_REGISTRY);
                        }
                    } else {
                        // "Ставим у такой записи признак Удалена при сверке (дата). Тут можно или признак, или примечание.
                        // Создаем новую запись по тому же клиенту в таблице cf_client_dtiszn_discount_info (данные берем из Реестров)."
                        discountInfo.setArchived(true);
                        discountInfo.setLastUpdate(new Date());
                        session.merge(discountInfo);

                        discountInfo = new ClientDtisznDiscountInfo(client, item.getDsznCode().longValue(),
                                item.getTitle(), item.getBenefitConfirm() ? ClientDTISZNDiscountStatus.CONFIRMED
                                : ClientDTISZNDiscountStatus.NOT_CONFIRMED, item.getSdDszn(), item.getFdDszn(),
                                item.getUpdatedAt(), DATA_SOURCE_TYPE_MARKER_OU, clientDTISZNDiscountVersion, item.getUpdatedAt(), true);
                        discountInfo.setArchived(item.getDeleted() || item.getFd().getTime() <= fireTime.getTime()
                                || item.getFdDszn().getTime() <= fireTime.getTime() || !item.getBenefitConfirm());
                        session.save(discountInfo);
                        logger.info("Archived old and created new ClientDtisznDiscountInfo");
                    }
                }
                session.flush();
                DiscountManager.rebuildAppointedMSPByClient(session, discountInfo.getClient());
                if (0 == counter++ % maxRecords) {
                    transaction.commit();
                    transaction = null;
                }
            }

            if (null != transaction && transaction.isActive()) {
                transaction.commit();
                transaction = null;
            }
        } catch (Exception e) {
            logger.error("Unable to get person benefits from DB", e);
            return;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        updateArchivedFlagForDiscountsDB(guid);
        runTaskPart2(fireTime, guid);
        updateApplicationsForFoodTask(false, guid);
        if (StringUtils.isEmpty(guid) && discountItemList != null && discountItemList.getDate() != null) {
            DAOService.getInstance().setOnlineOptionValue(CalendarUtils.dateTimeToString(discountItemList.getDate()),
                    Option.OPTION_REVISE_LAST_DATE);
        }
    }

    public void updateArchivedFlagForDiscountsDB(String guid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();

            Date fireTime = new Date();
            Long nextVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
            Query query;
            if (StringUtils.isEmpty(guid)) {
                query = session.createQuery("select i from ClientDtisznDiscountInfo i where i.dateEnd <= :now "
                        + "and i.status = :confirmed and i.archived = false");
            } else {
                query = session.createQuery("select i from ClientDtisznDiscountInfo i where i.dateEnd <= :now "
                        + "and (i.client.clientGUID = :guid or i.client.meshGUID = :guid) "
                        + "and i.status = :confirmed and i.archived = false");
                query.setParameter("guid", guid);
            }

            query.setParameter("now", CalendarUtils.addDays(fireTime, -1));
            query.setParameter("confirmed", ClientDTISZNDiscountStatus.CONFIRMED);
            List<ClientDtisznDiscountInfo> list = query.list();

            for (ClientDtisznDiscountInfo info : list) {
                try {
                    transaction = session.beginTransaction();
                    DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(
                            info);
                    builder.withArchived(true);
                    builder.save(session, nextVersion);
                    DiscountManager.rebuildAppointedMSPByClient(session, info.getClient());

                    if (!info.getDtisznCode().equals(0L)) {
                        transaction.commit();
                        transaction = null;
                        continue; //если не Иное - пропускаем обновление льгот клиента
                    }
                    Client client = info.getClient();
                    List<ClientDtisznDiscountInfo> infoList = new ArrayList<>();
                    infoList.add(info);
                    updateApplicationForFood(session, client, infoList);
                    if (client.getCategories().size() == 0) {
                        transaction.commit();
                        transaction = null;
                        continue;
                    }

                    CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                            .getCategoryDiscountDSZNByDSZNCode(session, info.getDtisznCode());
                    Long isppCode = categoryDiscountDSZN.getCategoryDiscount()
                            .getIdOfCategoryDiscount(); //код льготы ИСПП для льготы из Инфо
                    Set<CategoryDiscount> discounts = client.getCategories();
                    Set<CategoryDiscount> oldDiscounts = client.getCategories();
                    Integer oldDiscountMode = client.getDiscountMode();

                    discounts.removeIf(s -> s.getIdOfCategoryDiscount() == isppCode);
                    Integer newDiscountMode =
                            discounts.size() == 0 ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;

                    if (!oldDiscountMode.equals(newDiscountMode) || !oldDiscounts.equals(discounts)) {
                        try {
                            DiscountManager.renewDiscounts(session, client, discounts, oldDiscounts,
                                    DiscountChangeHistory.MODIFY_IN_REGISTRY);
                        } catch (Exception e) {
                            transaction.rollback();
                            transaction = null;
                            logger.error(String.format("Unexpected discount code for client with id=%d",
                                    client.getIdOfClient()));
                        }
                    }

                    if (transaction != null) {
                        transaction.commit();
                        transaction = null;
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    transaction = null;
                    logger.error("Error in update archived flag", e);
                }
            }
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void updateDiscountsForGUID(String guid) throws Exception {
        if (!guid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new IllegalArgumentException("GUID wrong format");
        }

        runTaskDB(guid);
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling revise 2.0 service job: " + syncSchedule);
            JobDetail job = new JobDetail("DTSZNDiscountsReviseService", Scheduler.DEFAULT_GROUP,
                    DTSZNDiscountsReviseServiceJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("DTSZNDiscountsReviseService", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("DTSZNDiscountsReviseService", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("DTSZNDiscountsReviseService", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed to schedule revise 2.0 service job:", e);
        }
    }

public static class DTSZNDiscountsReviseServiceJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).run();
        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to run revise 2.0 service job:", e);
        }
    }
}
}
