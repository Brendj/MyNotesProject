/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DTSZNDiscountsReviseService {

    public static final String NODE = "ecafe.processor.revise.dtszn.node";

    public static final String MODE = "ecafe.processor.revise.dtszn.mode.test";
    public static final String DEFAULT_MODE = "true";

    public static final String SERVICE_URL = "ecafe.processor.revise.dtszn.url";
    public static final String DEFAULT_TEST_SERVICE_URL = "https://10.89.95.142:38080/api/public/v2";
    public static final String DEFAULT_PROD_SERVICE_URL = "https://10.89.95.132:38080/api/public/v2";

    public static final String USER = "ecafe.processor.revise.dtszn.user";
    public static final String PASSWORD = "ecafe.processor.revise.dtszzn.password";

    public static final String DEFAULT_USER = "USER_IS_PP";
    public static final String DEFAULT_PASSWORD = "IS_PP#weynb_234%";

    public static final String PAGE_SIZE = "ecafe.processor.revise.dtszn.page.size";
    public static final Long DEFAULT_PAGE_SIZE = 300L;

    public static final String OPERATOR_EQUAL = "=";
    public static final String OPERATOR_IN = "in";
    public static final String OPERATOR_LIKE = "like";
    public static final String OPERATOR_IS_NULL = "is-null";
    public static final String OPERATOR_GT = ">";
    public static final String OPERATOR_LT = "<";

    public static final String DSZN_CODE_FILTER = "24,41,48,52,56,66";

    Logger logger = LoggerFactory.getLogger(DTSZNDiscountsReviseService.class);

    private URL serviceURL;
    private String username;
    private String password;
    private Boolean isTest;

    public boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(NODE);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    @PostConstruct
    public void init() {
        try {
            serviceURL = getServiceUrl();
            username = getUserName();
            password = getPassword();
        } catch (Exception e) {
            logger.error("DTSZNDiscountsReviseService initialization error", e);
        }
    }

    public void run() throws Exception {
        if (!isOn()) {
            return;
        }
        runTask();
    }

    public void runTask() throws Exception {
        if (null == serviceURL) {
            throw new Exception("Unable to run DTSZNDiscountsReviseService - no service url was found");
        }

        Long pageSize = getPageSize();
        List<String> entityIdList = loadEntityIdList(pageSize);

        if (null != entityIdList && entityIdList.isEmpty()) {
            throw new Exception("No benefits was found - revise will be terminating");
        }

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Long currentPage = 1L;
        Long pagesCount = 0L;
        Long clientDTISZNDiscountVersion;
        List<Long> clientIdList = new ArrayList<Long>();

        Session session = null;
        Transaction transaction = null;

        ETPMVService service = RuntimeContext.getAppContext().getBean(ETPMVService.class);

        do {
            try {
                NSIPersonBenefitResponse response = loadPersonBenefits(currentPage, pageSize, entityIdList);

                session = runtimeContext.createPersistenceSession();
                transaction = session.beginTransaction();

                clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);

                for (NSIPersonBenefitResponseItem item : response.getPayLoad()) {

                    Client client = DAOUtils.findClientByGuid(session, item.getPerson().getId());
                    if (null == client) {
                        logger.info(String.format("Client with guid = { %s } not found", item.getPerson().getId()));
                        continue;
                    }

                    if (!client.getOrg().getChangesDSZN()) {
                        logger.info(String.format("Organization has no \"Changes DSZN\" flag. Client with guid = { %s } was skipped", item.getPerson().getId()));
                        continue;
                    }

                    ClientDtisznDiscountInfo discountInfo = DAOUtils
                            .getDTISZNDiscountInfoByClientAndCode(session, client, item.getBenefit().getDsznCode());

                    if (null == discountInfo) {
                        discountInfo = new ClientDtisznDiscountInfo(client, item.getBenefit().getDsznCode(),
                                item.getBenefit().getBenefitForm(),
                                item.getBenefitConfirmed() ? ClientDTISZNDiscountStatus.CONFIRMED : ClientDTISZNDiscountStatus.NOT_CONFIRMED,
                                item.getDsznDateBeginAsDate(), item.getDsznDateEndAsDate(), item.getCreatedAtAsDate(), clientDTISZNDiscountVersion);
                        session.save(discountInfo);
                        if (!clientIdList.contains(client.getIdOfClient())) {
                            clientIdList.add(discountInfo.getIdOfClientDTISZNDiscountInfo());
                        }
                    } else {
                        if (discountInfo.getDtisznCode().equals(item.getBenefit().getDsznCode())) {
                            // Проверяем поля: статус льготы, дата начала действия льготы ДТиСЗН, дата окончания действия льготы ДТиСЗН.
                            // Перезаписываем те поля, которые отличаются в Реестре от ИС ПП (берем из Реестров).
                            boolean wasModified = false;
                            if (!discountInfo.getDateStart().equals(item.getDsznDateBeginAsDate())) {
                                discountInfo.setDateStart(item.getDsznDateBeginAsDate());
                                wasModified = true;
                            }
                            if (!discountInfo.getDateEnd().equals(item.getDsznDateEndAsDate())) {
                                discountInfo.setDateEnd(item.getDsznDateEndAsDate());
                                wasModified = true;
                            }
                            if (wasModified) {
                                discountInfo.setVersion(clientDTISZNDiscountVersion);
                                session.merge(discountInfo);
                                if (!clientIdList.contains(client.getIdOfClient())) {
                                    clientIdList.add(discountInfo.getIdOfClientDTISZNDiscountInfo());
                                }
                            }
                        } else {
                            // "Ставим у такой записи признак Удалена при сверке (дата). Тут можно или признак, или примечание.
                            // Создаем новую запись по тому же клиенту в таблице cf_client_dtiszn_discount_info (данные берем из Реестров)."
                            discountInfo.setArchived(true);
                            session.merge(discountInfo);

                            discountInfo = new ClientDtisznDiscountInfo(client, item.getBenefit().getDsznCode(),
                                    item.getBenefit().getBenefitForm(),
                                    item.getBenefitConfirmed() ? ClientDTISZNDiscountStatus.CONFIRMED : ClientDTISZNDiscountStatus.NOT_CONFIRMED,
                                    item.getDsznDateBeginAsDate(), item.getDsznDateEndAsDate(), item.getCreatedAtAsDate(),
                                    clientDTISZNDiscountVersion);
                            session.save(discountInfo);
                            if (!clientIdList.contains(client.getIdOfClient())) {
                                clientIdList.add(discountInfo.getIdOfClientDTISZNDiscountInfo());
                            }
                        }
                    }

                }
                transaction.commit();
                transaction = null;

                pagesCount = response.getPagesCount();
            } catch (HttpException e) {
                logger.error("HTTP exeption: ", e);
            } catch (Exception e) {
                logger.error("Unable to get person benefits from NSI", e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }

            logger.info(String.format("Revise 2.0: %d/%d pages was processed", currentPage, pagesCount));
        } while (currentPage++ < pagesCount);

        try {
            session = runtimeContext.createPersistenceSession();

            Integer counter = 1;
            for (Long idOfClient : clientIdList) {
                transaction = session.beginTransaction();
                Client client = (Client) session.load(Client.class, idOfClient);
                List<ClientDtisznDiscountInfo> clientInfoList = DAOUtils.getDTISZNDiscountsInfoByClient(session, client);
                processDiscounts(session, client, clientInfoList, service);
                transaction.commit();
                transaction = null;
                logger.info(String.format("Updating discounts for clients: %d/%d done", counter++, clientIdList.size()));
            }

        } catch (Exception e) {
            logger.error("Error in update discounts", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private URL getServiceUrl() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        isTest = Boolean.parseBoolean(runtimeContext.getConfigProperties().getProperty(MODE, DEFAULT_MODE));

        String urlString;
        if (isTest) {
            urlString = runtimeContext.getConfigProperties().getProperty(SERVICE_URL, DEFAULT_TEST_SERVICE_URL);
        } else {
            urlString = runtimeContext.getConfigProperties().getProperty(SERVICE_URL, DEFAULT_PROD_SERVICE_URL);
        }

        return new URL(urlString);
    }

    private Long getPageSize() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String pageSizeString = runtimeContext.getConfigProperties().getProperty(PAGE_SIZE);
        if (null == pageSizeString) {
            return DEFAULT_PAGE_SIZE;
        }
        Long pageSize;
        try {
            pageSize = Long.parseLong(pageSizeString);
        } catch (NumberFormatException e) {
            logger.error(String.format("Unable to parse page size value from config: %s", pageSizeString));
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    private String getUserName() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        return runtimeContext.getConfigProperties().getProperty(USER, DEFAULT_USER);
    }

    private String getPassword() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        return runtimeContext.getConfigProperties().getProperty(PASSWORD, DEFAULT_PASSWORD);
    }

    private NSIRequest buildDictBenefitRequest(Long page, Long pageSize) {
        List<NSIRequestParam> paramList = new ArrayList<NSIRequestParam>();
        paramList.add(new NSIRequestParam("deleted-at", OPERATOR_IS_NULL, false));
        paramList.add(new NSIRequestParam("created-by", OPERATOR_EQUAL, "ou", false));
        return new NSIRequest(paramList, page, "DictBenefit", pageSize);
    }

    private NSIRequest buildPersonBenefitRequest(Long page, Long pageSize, List<String> entityIdList) {
        List<NSIRequestParam> paramList = new ArrayList<NSIRequestParam>();
        paramList.add(new NSIRequestParam("person/student/institution-groups/institution-group/age-group",
                OPERATOR_EQUAL, "ОУ", false));
        paramList.add(new NSIRequestParam("benefit-form/entity-id", OPERATOR_IN,
                StringUtils.join(entityIdList, ","), false));
        paramList.add(new NSIRequestParam("deleted-at", OPERATOR_IS_NULL, false ));
        paramList.add(new NSIRequestParam("dszn-date-begin", OPERATOR_IS_NULL, true));
        paramList.add(new NSIRequestParam("dszn-date-end", OPERATOR_IS_NULL, true));
        paramList.add(new NSIRequestParam("benefit-confirmed", OPERATOR_IS_NULL, true));
        paramList.add(new NSIRequestParam("updated-at", OPERATOR_IS_NULL, true));
        paramList.add(new NSIRequestParam("person/deleted-at", OPERATOR_IS_NULL, false));
        return new NSIRequest(paramList, page, "PersonBenefit", pageSize);
    }

    private NSIBenefitDictionaryResponse loadBenefitsDictionary(Long page, Long pageSize) throws ConnectException {
        HttpClient httpClient = new HttpClient();

        httpClient.getHostConfiguration().setHost(serviceURL.getHost(), serviceURL.getPort(),
                new Protocol("https", new EasySSLProtocolSocketFactory(), serviceURL.getPort()));
        PostMethod method = new PostMethod(serviceURL.getPath());
        try {

            method.addRequestHeader("Content-Type", "application/json; charset=utf-8");
            authenticateHeaders(method);
            prepareMethodData(method, buildDictBenefitRequest(page, pageSize));

            Date date = new Date();
            Integer status = httpClient.executeMethod(method);
            logger.info(
                    String.format("loadBenefitsDictionary(page=%d, pageSize=%d loaded with status %d by %d msec", page,
                            pageSize, status, new Date().getTime() - date.getTime()));
            if (HttpStatus.OK.value() != status) {
                throw new Exception("Unable to get data from NSI");
            }
            return readDataFromResponse(method, NSIBenefitDictionaryResponse.class);
        } catch (ConnectException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in loadBenefitsDictionary", e);
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    private NSIPersonBenefitResponse loadPersonBenefits(Long page, Long pageSize, List<String> entityIdList) throws ConnectException {
        HttpClient httpClient = new HttpClient();

        httpClient.getHostConfiguration().setHost(serviceURL.getHost(), serviceURL.getPort(),
                new Protocol("https", new EasySSLProtocolSocketFactory(), serviceURL.getPort()));
        PostMethod method = new PostMethod(serviceURL.getPath());
        try {

            method.addRequestHeader("Content-Type", "application/json; charset=utf-8");
            authenticateHeaders(method);
            prepareMethodData(method, buildPersonBenefitRequest(page, pageSize, entityIdList));

            Date date = new Date();
            Integer status = httpClient.executeMethod(method);
            logger.info(String.format("loadPersonBenefits(page=%d, pageSize=%d loaded with status %d by %d msec", page,
                    pageSize, status, new Date().getTime() - date.getTime()));
            if (HttpStatus.OK.value() != status) {
                throw new Exception("Unable to get data from NSI");
            }
            return readDataFromResponse(method, NSIPersonBenefitResponse.class);

        } catch (ConnectException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in loadPersonBenefits", e);
        }
        return null;
    }

    private void authenticateHeaders(EntityEnclosingMethod method) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        method.addRequestHeader("Authorization", authHeader);
    }

    private void prepareMethodData(EntityEnclosingMethod method, NSIRequest request) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String serialized = objectMapper.writeValueAsString(request);

        StringRequestEntity requestEntity = new StringRequestEntity(
                serialized,
                "application/json",
                "UTF-8");
        method.setRequestEntity(requestEntity);
    }

    private <T> T readDataFromResponse(EntityEnclosingMethod method, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(method.getResponseBodyAsString(), clazz);
    }

    private List<String> loadEntityIdList(Long pageSize) throws ConnectException {
        Long currentPage = 1L;
        Long pagesCount;

        List<String> entityIds = new ArrayList<String>();
        do {
            try {
                NSIBenefitDictionaryResponse response = loadBenefitsDictionary(currentPage, pageSize);
                String[] dsznCodeArray = StringUtils.split(DSZN_CODE_FILTER, ",");
                List<Long> dsznCodeList = new ArrayList<Long>();
                for (String code : dsznCodeArray) {
                    dsznCodeList.add(Long.parseLong(code));
                }

                for (NSIBenefitDictionaryResponseItem item : response.getPayLoad()) {
                    if (dsznCodeList.contains(item.getDsznCode())) {
                        entityIds.add(item.getEntityId());
                    }
                }

                pagesCount = response.getPagesCount();
            } catch (ConnectException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unable to get benefits from NSI");
                break;
            }
        } while (++currentPage < pagesCount);
        return entityIds;
    }

    public void updateApplicationForFood(Session session, ETPMVService service, Client client, List<ClientDtisznDiscountInfo> infoList/*, ClientDtisznDiscountInfo discountInfo*/) {
        ApplicationForFood application = DAOUtils.findActiveApplicationForFoodByClient(session, client);
        if (null == application ||
                !application.getStatus().equals(new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_SENDED, null)))
            return;

        Date fireTime = new Date();

        try {
            Long applicationVersion = DAOUtils.nextVersionByApplicationForFood(session);
            Long historyVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);
            //7705
            application = DAOUtils.updateApplicationForFoodWithVersion(session, application,
                    new ApplicationForFoodStatus(ApplicationForFoodState.INFORMATION_REQUEST_RECEIVED, null),
                    applicationVersion, historyVersion);
            service.sendStatusAsync(System.currentTimeMillis() - service.getPauseValue(), application.getServiceNumber(),
                    application.getStatus().getApplicationForFoodState(), application.getStatus().getDeclineReason());

            Boolean isOk = false;

            for (ClientDtisznDiscountInfo info : infoList) {
                isOk &= info.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) && CalendarUtils
                        .betweenOrEqualDate(info.getDateStart(), info.getDateEnd(), fireTime);
            }

            if (isOk) {
                //1052
                application = DAOUtils.updateApplicationForFoodWithVersion(session, application,
                        new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, null),
                        applicationVersion, historyVersion);
                service.sendStatusAsync(System.currentTimeMillis() - service.getPauseValue(), application.getServiceNumber(),
                        application.getStatus().getApplicationForFoodState(), application.getStatus().getDeclineReason());

                //1075
                application = DAOUtils.updateApplicationForFoodWithVersion(session, application,
                        new ApplicationForFoodStatus(ApplicationForFoodState.OK, null), applicationVersion,
                        historyVersion);
                service.sendStatusAsync(System.currentTimeMillis() - service.getPauseValue(), application.getServiceNumber(),
                        application.getStatus().getApplicationForFoodState(), application.getStatus().getDeclineReason());
            } else {
                //1080.3
                application = DAOUtils.updateApplicationForFoodWithVersion(session, application,
                        new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.INFORMATION_CONFLICT),
                        applicationVersion, historyVersion);
                service.sendStatusAsync(System.currentTimeMillis() - service.getPauseValue(), application.getServiceNumber(),
                        application.getStatus().getApplicationForFoodState(), application.getStatus().getDeclineReason());
            }
        } catch (Exception e) {
            logger.error(String.format("Error in updateApplicationForFood: " +
                    "unable to update application for food for client with id=%d, idOfClientDTISZNDiscountInfo={%s}",
                    client.getIdOfClient(), StringUtils.join(infoList, ",")));
        }
    }

    public void processDiscounts(Session session, Client client, List<ClientDtisznDiscountInfo> infoList, ETPMVService service) throws Exception {

        Date fireTime = new Date();

        String oldDiscounts = client.getCategoriesDiscounts();
        String newDiscounts = oldDiscounts;

        String[] discounts = StringUtils.split(client.getCategoriesDiscounts(), ',');
        List<Long> categoryDiscountsList = new ArrayList<Long>(discounts.length);
        for (String discount : discounts) {
            try {
                categoryDiscountsList.add(Long.parseLong(discount));
            } catch (NumberFormatException e) {
                logger.warn(String.format("Unable to parse discount code=%s for client with id=%d",
                        discount, client.getIdOfClient()));
            }
        }

        for (ClientDtisznDiscountInfo info : infoList) {
            if (!info.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED) || !CalendarUtils
                    .betweenOrEqualDate(fireTime, info.getDateStart(), info.getDateEnd())) {
                continue;
            }

            CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                    .getCategoryDiscountDSZNByDSZNCode(session, info.getDtisznCode());

            if (null != categoryDiscountDSZN) {
                if (!categoryDiscountsList.contains(categoryDiscountDSZN.getCategoryDiscount().getIdOfCategoryDiscount())) {
                    newDiscounts += oldDiscounts + (oldDiscounts.isEmpty() ? "":",")
                            + categoryDiscountDSZN.getCategoryDiscount().getIdOfCategoryDiscount();
                }
            }
        }
        Integer oldDiscountMode = client.getDiscountMode();
        Integer newDiscountMode = StringUtils.isEmpty(newDiscounts) ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;

        if (!oldDiscountMode.equals(newDiscountMode) || !oldDiscounts.equals(newDiscounts)) {
            client.setCategoriesDiscounts(newDiscounts);
            client.setDiscountMode(newDiscountMode);

            DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, client.getOrg(),
                    newDiscountMode, oldDiscountMode, newDiscounts, oldDiscounts);
            discountChangeHistory.setComment(DiscountChangeHistory.MODIFY_IN_REGISTRY);
            session.save(discountChangeHistory);
            client.setLastDiscountsUpdate(new Date());
            client.setCategories(ClientManager.getCategoriesSet(session, newDiscounts));
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            client.setClientRegistryVersion(clientRegistryVersion);
        }
        updateApplicationForFood(session, service, client, infoList);
    }
}
