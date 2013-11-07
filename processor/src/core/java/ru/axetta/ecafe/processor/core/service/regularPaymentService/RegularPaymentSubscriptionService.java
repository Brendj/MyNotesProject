/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 13:17
 */

@Service("regularPaymentSubscriptionService")
public class RegularPaymentSubscriptionService {

    private static Logger logger = LoggerFactory.getLogger(RegularPaymentSubscriptionService.class);
    private RuntimeContext runtimeContext;

    @PostConstruct
    protected void init() {
        runtimeContext = RuntimeContext.getInstance();
    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    private RegularPaymentSubscriptionService getSelfProxy() {
        return RuntimeContext.getAppContext().getBean(RegularPaymentSubscriptionService.class);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MfrRequest createRequestForSubscriptionReg(Long contractId, Long paymentAmount, Long thresholdAmount,
            int period) throws Exception {
        MfrRequest request = new MfrRequest();
        request.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        request.setRequestType(MfrRequest.REQUEST_TYPE_ACTIVATION);
        request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getLinkingUrl());
        Client client = DAOUtils.findClientByContractId(em.unwrap(Session.class), contractId);
        request.setRequestTime(new Date());
        request.setClient(client);
        request.setSan(client.getSan());
        BankSubscription bs = new BankSubscription();
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setMonthsCount(period);
        bs.setClient(client);
        bs.setSan(client.getSan());
        bs.setPaySystem(request.getPaySystem());
        em.persist(bs);
        request.setBs(bs);
        em.persist(request);
        return request;
    }

    @Transactional(propagation = Propagation.NEVER)
    public void checkClientBalances() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        logger.info("RegularPaymentSubscriptionService work started.");
        List<Long> subIds = findSubscriptions(0);
        Date today = new Date();
        for (Long id : subIds) {
            try {
                BankSubscription bs = findBankSubscription(id);
                if (bs.getValidToDate().before(today)) {
                    getSelfProxy().deactivateSubscription(id);
                } else {
                    getSelfProxy().refillOneClientBalance(id);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        logger.info("RegularPaymentSubscriptionService work is over.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void refillOneClientBalance(Long subId) {
        BankSubscription bs = em.find(BankSubscription.class, subId);
        MfrRequest request = new MfrRequest();
        request.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        request.setRequestType(MfrRequest.REQUEST_TYPE_PAYMENT);
        request.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getPaymentUrl());
        request.setRequestTime(new Date());
        request.setClient(bs.getClient());
        request.setSan(bs.getClient().getSan());
        request.setBs(bs);
        em.persist(request);
        RegularPayment payment = new RegularPayment();
        payment.setBankSubscription(bs);
        payment.setMfrRequest(request);
        payment.setPaymentAmount(bs.getPaymentAmount());
        payment.setClient(bs.getClient());
        payment.setClientBalance(bs.getClient().getBalance());
        payment.setThresholdAmount(bs.getThresholdAmount());
        em.persist(payment);
        Map<String, String> params = getParamsForPaymentRequest(payment);
        PaymentResponse pr = sendRequest(request.getRequestUrl(), params);
        processPaymentResponse(request, pr);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean deactivateSubscription(Long bsId) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        MfrRequest mfrRequest = new MfrRequest();
        mfrRequest.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        mfrRequest.setRequestType(MfrRequest.REQUEST_TYPE_DEACTIVATION);
        mfrRequest.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getPaymentUrl());
        mfrRequest.setRequestTime(new Date());
        mfrRequest.setClient(bs.getClient());
        mfrRequest.setSan(bs.getClient().getSan());
        mfrRequest.setBs(bs);
        em.persist(mfrRequest);
        Map<String, String> params = getParamsForDeactivation(mfrRequest);
        PaymentResponse response = sendRequest(mfrRequest.getRequestUrl(), params);
        processPaymentResponse(mfrRequest, response);
        if (MfrRequest.SUBSCRIPTION_DEACTIVATED.equalsIgnoreCase(response.getStatus())) {
            bs.setActive(false);
            bs.setDeactivationDate(response.getDateTime());
            bs.setStatus(MfrRequest.SUBSCRIPTION_DEACTIVATED);
            return true;
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBankSubscription(Long bsId, Long paymentAmount, Long thresholdAmount, int period) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setMonthsCount(period);
        bs.setValidToDate(CalendarUtils.addMonth(new Date(), period));
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkSubscriptionStatus(Long bsId) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        MfrRequest mfrRequest = new MfrRequest();
        mfrRequest.setPaySystem(MfrRequest.ACQUIROPAY_SYSTEM);
        mfrRequest.setRequestType(MfrRequest.REQUEST_TYPE_STATUS_CHECK);
        mfrRequest.setRequestUrl(runtimeContext.getAcquiropaySystemConfig().getPaymentUrl());
        mfrRequest.setRequestTime(new Date());
        mfrRequest.setClient(bs.getClient());
        mfrRequest.setSan(bs.getClient().getSan());
        mfrRequest.setBs(bs);
        em.persist(mfrRequest);
        Map<String, String> params = getParamsForStatusChecking(bs);
        PaymentResponse response = sendRequest(mfrRequest.getRequestUrl(), params);
        processPaymentResponse(mfrRequest, response);
    }

    // Обработка callback'а на запрос активации подписки на автопополнение.
    @Transactional(rollbackFor = Exception.class)
    public void processSubscriptionActivated(Long mfrRequestId, PaymentResponse paymentResponse) throws Exception {
        MfrRequest mfrRequest = em.find(MfrRequest.class, mfrRequestId);
        mfrRequest.setResponseStatus(paymentResponse.getStatus());
        if (MfrRequest.SUBSCRIPTION_ACTIVATED.equalsIgnoreCase(paymentResponse.getStatus())) {
            BankSubscription bs = mfrRequest.getBs();
            bs.setStatus(MfrRequest.SUBSCRIPTION_ACTIVATED);
            bs.setActive(true);
            bs.setPaymentId(paymentResponse.getPaymentId());
            bs.setActivationDate(paymentResponse.getDateTime());
            bs.setMaskedCardNumber(paymentResponse.getPanMask());
            bs.setCardHolder(paymentResponse.getCardHolder());
            Integer expYear = StringUtils.isEmpty(paymentResponse.getExpYear()) ? null
                    : Integer.valueOf(paymentResponse.getExpYear());
            bs.setExpYear(expYear);
            Integer expMonth = StringUtils.isEmpty(paymentResponse.getExpMonth()) ? null
                    : Integer.valueOf(paymentResponse.getExpMonth());
            bs.setExpMonth(expMonth);
            Date validToDate = CalendarUtils.addMonth(paymentResponse.getDateTime(), bs.getMonthsCount());
            if (expMonth != null && expYear != null) {
                Date cardValidityDate = CalendarUtils.getDateOfLastDay(expYear, expMonth);
                bs.setValidToDate(cardValidityDate.before(validToDate) ? cardValidityDate : validToDate);
            } else {
                bs.setValidToDate(validToDate);
            }
        } else if (MfrRequest.ERROR.equalsIgnoreCase(paymentResponse.getStatus())) {
            mfrRequest.setErrorDescription(paymentResponse.getErrorDescription());
        }
    }

    // Обработка callback'а на запрос проведения регулярного пополнения (списания с карты).
    @Transactional(rollbackFor = Exception.class)
    public void processRegularPayment(Long mfrRequestId, PaymentResponse paymentResponse) throws Exception {
        MfrRequest mfrRequest = em.find(MfrRequest.class, mfrRequestId);
        mfrRequest.setResponseStatus(paymentResponse.getStatus());
        RegularPayment rp = findPaymentByMfrRequest(mfrRequest.getIdOfRequest());
        BankSubscription bs = rp.getBankSubscription();
        if (MfrRequest.PAYMENT_SUCCESSFUL.equalsIgnoreCase(paymentResponse.getStatus())) {
            rp.setSuccess(true);
            rp.setStatus(MfrRequest.PAYMENT_SUCCESSFUL);
            rp.setPaymentDate(paymentResponse.getDateTime());
            rp.setAuthCode(paymentResponse.getAuthCode());
            rp.setRrn(Long.valueOf(paymentResponse.getRrn()));
            // Сохраняем дату последнего успешного платежа по подписке
            // и сбрасываем счетчик неуспешных платежей подряд.
            bs.setLastSuccessfulPaymentDate(rp.getPaymentDate());
            bs.setUnsuccessfulPaymentsCount(0);
            bs.setLastPaymentStatus(MfrRequest.PAYMENT_SUCCESSFUL);
        } else if (MfrRequest.ERROR.equalsIgnoreCase(paymentResponse.getStatus())) {
            rp.setStatus(MfrRequest.ERROR);
            // Сохраняем дату неуспешного платежа по подписке
            // и увеличиваем счетчик неуспешных платежей подряд.
            bs.setLastUnsuccessfulPaymentDate(new Date());
            bs.setUnsuccessfulPaymentsCount(bs.getUnsuccessfulPaymentsCount() + 1);
            bs.setLastPaymentStatus(MfrRequest.ERROR);
            mfrRequest.setErrorDescription(paymentResponse.getErrorDescription());
        }
    }

    // Возвращает параметры для запроса по активации подписки (привязка карты).
    public Map<String, String> getParamsForRegRequest(MfrRequest mfrRequest) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", RuntimeContext.getInstance().getAcquiropaySystemConfig().getLinkingUrl());
        String productId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getProductId());
        // ID поставщика питания №1 в системе МФР
        params.put("product_id", productId);
        String account = ContractIdFormat.format(mfrRequest.getClient().getContractId());
        params.put("product_name", "Подписка на оплату Школьного питания л/с " + account);
        params.put("amount", "*");
        // ID запроса ИС ПП
        params.put("cf", mfrRequest.getIdOfRequest().toString());
        // Уникальный идентификатор подписки ИС ПП
        params.put("cf2", mfrRequest.getBs().getIdOfSubscription().toString());
        // Номер лицевого счета учащегося
        params.put("cf3", account);
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + product_id + [amount] + cf +cf2 + cf3 + secret_word)
        String token = CryptoUtils
                .MD5(merchantId + productId + params.get("amount") + params.get("cf") + params.get("cf2") +
                        params.get("cf3") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    // Возвращает параметры для запроса по проведению платежа.
    private Map<String, String> getParamsForPaymentRequest(RegularPayment payment) {
        Map<String, String> params = new HashMap<String, String>();
        // код операции
        params.put("opcode", "rebill");
        // сумма платежа
        params.put("amount", CurrencyStringUtils.copecksToRubles(payment.getPaymentAmount(), 0));
        // ID запроса ИС ПП
        params.put("cf", payment.getMfrRequest().getIdOfRequest().toString());
        String paymentId = payment.getBankSubscription().getPaymentId();
        // ID подписки в системе МФР
        params.put("payment_id", paymentId);
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + payment_id + [amount]  + secret_word)
        String token = CryptoUtils.MD5(merchantId + paymentId + params.get("amount") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    // Возвращает параметры для запроса по деактивации подписки.
    private Map<String, String> getParamsForDeactivation(MfrRequest mfrRequest) {
        Map<String, String> params = new HashMap<String, String>();
        // код операции
        params.put("opcode", "stop_rebilling");
        // ID подписки в системе МФР
        params.put("payment_id", mfrRequest.getBs().getPaymentId());
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + payment_id + secret_word)
        String token = CryptoUtils.MD5(merchantId + params.get("payment_id") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    // Возвращает параметры для запроса по проверке статуса подписки.
    private Map<String, String> getParamsForStatusChecking(BankSubscription bs) {
        Map<String, String> params = new HashMap<String, String>();
        // код операции
        params.put("opcode", "check_status");
        // ID подписки в системе МФР
        params.put("payment_id", bs.getPaymentId());
        String merchantId = String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId());
        String secretWord = runtimeContext.getAcquiropaySystemConfig().getSecretWord();
        // Идентификатор безопасности MD5(merchant_id + payment_id + secret_word)
        String token = CryptoUtils.MD5(merchantId + params.get("payment_id") + secretWord).toLowerCase();
        params.put("token", token);
        return params;
    }

    private PaymentResponse sendRequest(String uri, Map<String, String> params) {
        PostMethod httpMethod = new PostMethod(uri);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpMethod.setParameter(entry.getKey(), entry.getValue());
        }
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            int statusCode = httpClient.executeMethod(httpMethod);
            paymentResponse.setStatusCode(statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                if (httpMethod.getResponseHeader("Content-Type").getValue().contains("text/xml")) {
                    fillFromXMLResponse(paymentResponse, httpMethod.getResponseBodyAsStream());
                }
            } else {
                logger.error("Http request has status {}", statusCode);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            httpMethod.releaseConnection();
        }
        return paymentResponse;
    }

    // MD5(merchant_id + payment_id + status + cf + cf2 + cf3 + secret_word).
    public boolean checkResponseSign(PaymentResponse paymentResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(runtimeContext.getAcquiropaySystemConfig().getMerchantId()));
        if (StringUtils.isNotEmpty(paymentResponse.getPaymentId())) {
            sb.append(paymentResponse.getPaymentId());
        }
        if (StringUtils.isNotEmpty(paymentResponse.getStatus())) {
            sb.append(paymentResponse.getStatus());
        }
        if (StringUtils.isNotEmpty(paymentResponse.getCf())) {
            sb.append(paymentResponse.getCf());
        }
        if (StringUtils.isNotEmpty(paymentResponse.getCf2())) {
            sb.append(paymentResponse.getCf2());
        }
        if (StringUtils.isNotEmpty(paymentResponse.getCf3())) {
            sb.append(paymentResponse.getCf3());
        }
        sb.append(runtimeContext.getAcquiropaySystemConfig().getSecretWord());
        return CryptoUtils.MD5(sb.toString()).equalsIgnoreCase(paymentResponse.getSign());
    }

    private Date parseBankDate(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date result = null;
        try {
            result = sdf.parse(datetime);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Long> findSubscriptions(int rows) {
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Query query = em.createQuery("select distinct bs.idOfSubscription from BankSubscription bs \n" +
                "where bs.active = :active and bs.activationDate is not null and bs.client.balance <= bs.thresholdAmount \n" +
                "and ((bs.lastSuccessfulPaymentDate < :today or bs.lastSuccessfulPaymentDate is null) and " +
                "(bs.lastUnsuccessfulPaymentDate < :today or bs.lastUnsuccessfulPaymentDate is null)) \n" +
                "and (bs.client.idOfClientGroup not in (:cg) or bs.client.idOfClientGroup is null)")
                .setParameter("today", today)
                .setParameter("active", true)
                .setParameter("cg", Arrays.asList(
                        ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                        ClientGroup.Predefined.CLIENT_DELETED.getValue())
                );
        if (rows != 0) {
            query.setMaxResults(rows);
        }
        return (List<Long>) query.getResultList();
    }

    // Список подписок клиента. Не отображает подписки, активация которых не подтверждена.
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public List<BankSubscription> findClientBankSubscriptions(Long contractId) {
        Criteria criteria = em.unwrap(Session.class).createCriteria(BankSubscription.class);
        criteria.createAlias("client", "c").add(Restrictions.eq("c.contractId", contractId))
                .add(Restrictions.isNotNull("activationDate")).addOrder(Order.asc("activationDate"));
        return (List<BankSubscription>) criteria.list();
    }

    // Список всех платежей по подписке.
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<RegularPayment> getSubscriptionPayments(Long subscriptionId, Date beginDate, Date endDate,
            boolean isOnlySuccess) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(RegularPayment.class);
        criteria.createAlias("bankSubscription", "bs").add(Restrictions.eq("bs.idOfSubscription", subscriptionId));
        if (beginDate != null) {
            criteria.add(Restrictions.ge("paymentDate", beginDate));
        }
        if (endDate != null) {
            criteria.add(Restrictions.le("paymentDate", endDate));
        }
        if (isOnlySuccess) {
            criteria.add(Restrictions.eq("success", true));
        }
        return (List<RegularPayment>) criteria.list();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public BankSubscription getSubscriptionWithClient(Long id) {
        TypedQuery<BankSubscription> query = em.createQuery(
                "select distinct bs from BankSubscription bs join fetch bs.client where bs.idOfSubscription = :id",
                BankSubscription.class).setParameter("id", id);
        List<BankSubscription> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public BankSubscription findBankSubscription(Long id) {
        return em.find(BankSubscription.class, id);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public MfrRequest findMfrRequest(Long id) {
        return em.find(MfrRequest.class, id);
    }

    @SuppressWarnings("unchecked")
    private RegularPayment findPaymentByMfrRequest(Long mfrRequestId) {
        Criteria criteria = em.unwrap(Session.class).createCriteria(RegularPayment.class);
        criteria.createAlias("mfrRequest", "mr").add(Restrictions.eq("mr.idOfRequest", mfrRequestId));
        List<RegularPayment> res = (List<RegularPayment>) criteria.list();
        return res.isEmpty() ? null : res.get(0);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Long> findSubscriptionsId(String mobile, String san) {
        Criteria criteria = em.unwrap(Session.class).createCriteria(BankSubscription.class);
        criteria.setProjection(Projections.property("idOfSubscription")).createAlias("client", "c")
                .add(Restrictions.isNotNull("activationDate"));
        if (mobile != null) {
            criteria.add(Restrictions.eq("c.mobile", mobile));
        }
        if (san != null) {
            criteria.add(Restrictions.or(Restrictions.eq("san", san), Restrictions.eq("c.san", san)));
        }
        criteria.addOrder(Order.asc("idOfSubscription"));
        return (List<Long>) criteria.list();
    }

    public void fillFromXMLResponse(PaymentResponse pr, InputStream is) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = XMLUtils.inputStreamToXML(is, dbf);
        Node responseNode = XMLUtils.findFirstChildElement(doc, "response");
        Node workNode = XMLUtils.findFirstChildElement(responseNode, "status");
        if (workNode != null) {
            pr.setStatus(StringUtils.trimToEmpty(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "description");
        if (workNode != null) {
            pr.setErrorDescription(StringUtils.trimToEmpty(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "payment_id");
        if (workNode != null) {
            pr.setPaymentId(StringUtils.trimToEmpty(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "datetime");
        if (workNode != null) {
            pr.setDateTime(parseBankDate(StringUtils.trimToEmpty(workNode.getTextContent())));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "extended_status");
        if (workNode != null) {
            pr.setExtendedStatus(StringUtils.trimToEmpty(workNode.getTextContent()));
        }
        Node additionalNode = XMLUtils.findFirstChildElement(responseNode, "additional");
        if (additionalNode != null) {
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf");
            if (workNode != null) {
                pr.setCf(StringUtils.trimToEmpty(workNode.getTextContent()));
            }
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf2");
            if (workNode != null) {
                pr.setCf2(StringUtils.trimToEmpty(workNode.getTextContent()));
            }
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf3");
            if (workNode != null) {
                pr.setCf3(StringUtils.trimToEmpty(workNode.getTextContent()));
            }
        }
    }

    private void processPaymentResponse(MfrRequest request, PaymentResponse response) {
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            request.setSuccess(true);
        } else {
            request.setErrorDescription(String.valueOf(response.getStatusCode()));
        }
        request.setResponseStatus(response.getStatus());
        if (MfrRequest.ERROR.equalsIgnoreCase(response.getStatus())) {
            request.setErrorDescription(response.getErrorDescription());
        }
    }

    public void fillFromRequest(PaymentResponse pr, HttpServletRequest request) {
        pr.setPaymentId(StringUtils.trimToEmpty(request.getParameter("payment_id")));
        pr.setStatus(StringUtils.trimToEmpty(request.getParameter("status")));
        pr.setCf(StringUtils.trimToEmpty(request.getParameter("cf")));
        pr.setCf2(StringUtils.trimToEmpty(request.getParameter("cf2")));
        pr.setCf3(StringUtils.trimToEmpty(request.getParameter("cf3")));
        pr.setAuthCode(StringUtils.trimToEmpty(request.getParameter("auth_code")));
        pr.setCardHolder(StringUtils.trimToEmpty(request.getParameter("cardholder")));
        pr.setPanMask(StringUtils.trimToEmpty(request.getParameter("pan_mask")));
        pr.setExpMonth(StringUtils.trimToEmpty(request.getParameter("exp_month")));
        pr.setExpYear(StringUtils.trimToEmpty(request.getParameter("exp_year")));
        pr.setExtendedStatus(StringUtils.trimToEmpty(request.getParameter("extended_status")));
        pr.setSign(StringUtils.trimToEmpty(request.getParameter("sign")));
        pr.setDateTime(parseBankDate(StringUtils.trimToEmpty(request.getParameter("datetime"))));
        pr.setErrorDescription(StringUtils.trimToEmpty(request.getParameter("description")));
        pr.setRrn(StringUtils.trimToEmpty(request.getParameter("rrn")));
        pr.setAuthCode(StringUtils.trimToEmpty(request.getParameter("auth_code")));
    }

}
