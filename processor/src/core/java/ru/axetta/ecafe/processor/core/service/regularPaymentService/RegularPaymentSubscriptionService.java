/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
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

    private IRequestOperation getOperationBean(String name) {
        return (IRequestOperation) RuntimeContext.getAppContext().getBean(name + "Request");
    }

    public MfrRequest createRequestForSubscriptionReg(Long contractId, Long paymentAmount, Long thresholdAmount,
            int period) throws Exception {
        return ((SubscriptionRegRequest) getOperationBean("subscriptionReg"))
                .createRequestForSubscriptionReg(contractId, paymentAmount, thresholdAmount, period);
    }

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
                    makeSubscriptionOperation(id, "subscriptionDelete");
                } else {
                    makeSubscriptionOperation(id, "regularPayment");
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        logger.info("RegularPaymentSubscriptionService work is over.");
    }

    private boolean makeSubscriptionOperation(Long subscriptionId, String operationName) {
        IRequestOperation operation = getOperationBean(operationName);
        MfrRequest mfrRequest = operation.createRequest(subscriptionId);
        Map<String, String> params = operation.getRequestParams(mfrRequest);
        PaymentResponse paymentResponse = sendRequest(mfrRequest.getRequestUrl(), params);
        operation.processResponse(mfrRequest.getIdOfRequest(), paymentResponse);
        if (operationName.equals("subscriptionDelete")) {
            return operation.postProcessResponse(mfrRequest.getIdOfRequest(), paymentResponse);
        }
        return true;
    }

    public void refillOneClientBalance(Long bsId) {
        makeSubscriptionOperation(bsId, "regularPayment");
    }

    // Обработка callback'а на запрос активации подписки на автопополнение.
    public void processSubscriptionActivated(Long mfrRequestId, PaymentResponse paymentResponse) {
        getOperationBean("subscriptionReg").postProcessResponse(mfrRequestId, paymentResponse);
    }

    // Обработка callback'а на запрос проведения регулярного пополнения (списания с карты).
    public void processRegularPayment(Long mfrRequestId, PaymentResponse paymentResponse) {
        getOperationBean("regularPayment").postProcessResponse(mfrRequestId, paymentResponse);
    }

    // Возвращает параметры для запроса по активации подписки (привязка карты).
    public Map<String, String> getParamsForRegRequest(MfrRequest mfrRequest) {
        return getOperationBean("subscriptionReg").getRequestParams(mfrRequest);
    }

    public boolean deactivateSubscription(Long subscriptionId) {
        return makeSubscriptionOperation(subscriptionId, "subscriptionDelete");
    }

    private PaymentResponse sendRequest(String uri, Map<String, String> params) {
        PostMethod httpMethod = new PostMethod(uri);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpMethod.setParameter(entry.getKey(), entry.getValue());
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            logger.info("Sending request to MFR: {}", sb.toString());
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
            logger.info("Response from MFR: {}", paymentResponse.toString());
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

    @Transactional(rollbackFor = Exception.class)
    public void updateBankSubscription(Long bsId, Long paymentAmount, Long thresholdAmount, int period) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setMonthsCount(period);
        bs.setValidToDate(CalendarUtils.addMonth(new Date(), period));
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
            pr.setStatus(StringUtils.trim(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "description");
        if (workNode != null) {
            pr.setErrorDescription(StringUtils.trim(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "payment_id");
        if (workNode != null) {
            pr.setPaymentId(StringUtils.trim(workNode.getTextContent()));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "datetime");
        if (workNode != null) {
            pr.setDateTime(parseBankDate(StringUtils.trim(workNode.getTextContent())));
        }
        workNode = XMLUtils.findFirstChildElement(responseNode, "extended_status");
        if (workNode != null) {
            pr.setExtendedStatus(StringUtils.trim(workNode.getTextContent()));
        }
        Node additionalNode = XMLUtils.findFirstChildElement(responseNode, "additional");
        if (additionalNode != null) {
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf");
            if (workNode != null) {
                pr.setCf(StringUtils.trim(workNode.getTextContent()));
            }
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf2");
            if (workNode != null) {
                pr.setCf2(StringUtils.trim(workNode.getTextContent()));
            }
            workNode = XMLUtils.findFirstChildElement(responseNode, "cf3");
            if (workNode != null) {
                pr.setCf3(StringUtils.trim(workNode.getTextContent()));
            }
        }
    }

    public void fillFromRequest(PaymentResponse pr, HttpServletRequest request) {
        pr.setPaymentId(StringUtils.trim(request.getParameter("payment_id")));
        pr.setStatus(StringUtils.trim(request.getParameter("status")));
        pr.setCf(StringUtils.trim(request.getParameter("cf")));
        pr.setCf2(StringUtils.trim(request.getParameter("cf2")));
        pr.setCf3(StringUtils.trim(request.getParameter("cf3")));
        pr.setAuthCode(StringUtils.trim(request.getParameter("auth_code")));
        pr.setCardHolder(StringUtils.trim(request.getParameter("cardholder")));
        pr.setPanMask(StringUtils.trim(request.getParameter("pan_mask")));
        pr.setExpMonth(StringUtils.trim(request.getParameter("exp_month")));
        pr.setExpYear(StringUtils.trim(request.getParameter("exp_year")));
        pr.setExtendedStatus(StringUtils.trim(request.getParameter("extended_status")));
        pr.setSign(StringUtils.trim(request.getParameter("sign")));
        pr.setDateTime(parseBankDate(StringUtils.trim(request.getParameter("datetime"))));
        pr.setErrorDescription(StringUtils.trim(request.getParameter("description")));
        pr.setRrn(StringUtils.trim(request.getParameter("rrn")));
        pr.setAuthCode(StringUtils.trim(request.getParameter("auth_code")));
    }

}
