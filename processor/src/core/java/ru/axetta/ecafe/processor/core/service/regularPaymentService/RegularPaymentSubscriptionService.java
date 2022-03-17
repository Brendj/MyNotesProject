/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalProcess;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;
import ru.axetta.ecafe.processor.core.utils.ssl.EasyX509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

    @Autowired
    private RegularPaymentRequest regularPaymentRequest;
    @Autowired
    private StatusCheckRequest statusCheckRequest;
    @Autowired
    private SubscriptionDeleteRequest subscriptionDeleteRequest;
    @Autowired
    private SubscriptionRegRequest subscriptionRegRequest;

    private static Scheme scheme;

    public MfrRequest createRequestForSubscriptionReg(Long contractId, Long paymentAmount, Long thresholdAmount,
            int period, Date validityDate, String mobile) {
        return subscriptionRegRequest
                .createRequestForSubscriptionReg(contractId, paymentAmount, thresholdAmount, period, validityDate, mobile);
    }

    public void checkClientBalances() {
        checkClientBalances(null);
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.regular.payment.cron", "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            JobDetail job = new JobDetail("RegularPayment", Scheduler.DEFAULT_GROUP, RegularPaymentSubscriptionService.RegularPaymentServiceJob.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("RegularPayment", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("RegularPayment", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("RegularPayment", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule revise 2.0 service job:", e);
        }
    }

    public static class RegularPaymentServiceJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                ((RegularPaymentSubscriptionService)RuntimeContext.getAppContext().getBean("regularPaymentSubscriptionService")).checkClientBalances(null);
            } catch (Exception e) {
                logger.error("Failed to send data to AIS Contingent:", e);
            }
        }
    }

    public void notifyClientsAboutExpiredSubscriptions() {
        if (!RuntimeContext.getInstance().isMainNode() || !notifyAboutExpiredSubscription()) {
            return;
        }
        logger.info("Start sending notifications for expired subscriptions");
        final RegularPaymentNotificationService regularPaymentNotificationService = RuntimeContext.getAppContext().getBean(
                RegularPaymentNotificationService.class);
        List<BankSubscription> list = getExpiredSubscriptionsList();
        for (BankSubscription subscription : list) {
            deactivateSubscription(subscription.getIdOfSubscription()); //отправляем запрос на деактивацию
            regularPaymentNotificationService.sendNotification(subscription);
        }
        logger.info("End sending notifications for expired subscriptions");
    }

    private List<BankSubscription> getExpiredSubscriptionsList() {
        Date date_to = CalendarUtils.startOfDay(new Date());
        //Выбираем активные подписки, где дата окончания меньше начала текущего дня
        String query_str = "select bs from BankSubscription bs where bs.active = true "
                + "and bs.activationDate is not null and bs.validToDate < :date_to "
                + "and (bs.notificationSent is null or bs.notificationSent = false)";
        Query query = em.createQuery(query_str);
        query.setParameter("date_to", date_to);
        return query.getResultList();
    }

    protected boolean notifyAboutExpiredSubscription() {
        return false;
    }

    private boolean isOn() {
        return RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.regularpayments.node");
    }

    public void checkClientBalances(Long idOfOrg ) {
        if (!isOn()) return;
        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.REGULAR_PAYMENTS, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;
        logger.info("RegularPaymentSubscriptionService work started.");
        List<Long> subIds = findSubscriptions(0, idOfOrg);
        Date today = new Date();
        final RegularPaymentNotificationService regularPaymentNotificationService = RuntimeContext.getAppContext().getBean(
                RegularPaymentNotificationService.class);
        for (Long id : subIds) {
            try {
                BankSubscription bs = findBankSubscription(id);
                if (bs.getValidToDate() != null && bs.getValidToDate().before(today)) {
                    deactivateSubscription(id);
                    if (notifyAboutExpiredSubscription()) {
                        regularPaymentNotificationService.sendNotification(bs);
                    }
                } else {
                    refillOneClientBalance(id);
                }
            } catch (Exception ex) {
                isSuccessEnd = false;
                logger.error("Error in regular payment schedule: ", ex);
            }
        }
        logger.info("RegularPaymentSubscriptionService work is over.");
        SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                SecurityJournalProcess.EventType.REGULAR_PAYMENTS, new Date());
        processEnd.saveWithSuccess(isSuccessEnd);
    }

    private PaymentResponse sendSubscriptionRequest(Long subscriptionId, IRequestOperation operation) {
        MfrRequest mfrRequest = operation.createRequest(subscriptionId);
        Map<String, String> params = operation.getRequestParams(mfrRequest);
        int attempt = 1;
        boolean do_break = false;
        PaymentResponse paymentResponse = null;
        while (attempt < 4 && !do_break) {
            logger.info(String.format("Attempt %s sending request to MFR", attempt));
            paymentResponse = sendRequest(mfrRequest.getRequestUrl(), params);
            do_break = (paymentResponse == null) ? false : (paymentResponse.getStatusCode() == HttpStatus.SC_OK);
            attempt++;
        }
        operation.processResponse(mfrRequest.getIdOfRequest(), paymentResponse);
        return paymentResponse;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean subscriptionExistsByClient(Long idOfClient) {
        Query query = em.createQuery("select bs from BankSubscription bs where bs.client.idOfClient = :idOfClient and bs.active = true and bs.activationDate is not null");
        query.setParameter("idOfClient", idOfClient);
        List list = query.getResultList();
        return list != null && list.size() > 0;
    }

    public void refillOneClientBalance(Long bsId) {
        sendSubscriptionRequest(bsId, regularPaymentRequest);
    }

    // Обработка callback'а на запрос активации подписки на автопополнение.
    public void processSubscriptionActivated(Long mfrRequestId, PaymentResponse paymentResponse) {
        subscriptionRegRequest.postProcessResponse(mfrRequestId, null, paymentResponse);
    }

    // Обработка callback'а на запрос проведения регулярного пополнения (списания с карты).
    public void processRegularPayment(Long mfrRequestId, PaymentResponse paymentResponse) {
        regularPaymentRequest.postProcessResponse(mfrRequestId, null, paymentResponse);
    }

    // Возвращает параметры для запроса по активации подписки (привязка карты).
    public Map<String, String> getParamsForRegRequest(MfrRequest mfrRequest) {
        return subscriptionRegRequest.getRequestParams(mfrRequest);
    }

    public boolean deactivateSubscription(Long subscriptionId) {
        logger.info("start deactivation of subscription");
        PaymentResponse paymentResponse = sendSubscriptionRequest(subscriptionId, subscriptionDeleteRequest);
        return subscriptionDeleteRequest.postProcessResponse(null, subscriptionId, paymentResponse);
    }

    public PaymentResponse checkSubscriptionStatus(Long subscriptionId) {
        return sendSubscriptionRequest(subscriptionId, statusCheckRequest);
    }

    protected PaymentResponse sendRequest(String uri, Map<String, String> params) {
        String s = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.acquiropay.mtsOldTeam", "");
        if (StringUtils.isEmpty(s)) {
            return sendRequest_new(uri, params);
        } else {
            return sendRequest_old(uri, params);
        }
    }

    protected PaymentResponse sendRequest_old(String uri, Map<String, String> params) {
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
                logger.info("OK response from MFR");
                if (httpMethod.getResponseHeader("Content-Type").getValue().contains("text/xml")) {
                    fillFromXMLResponse(paymentResponse, httpMethod.getResponseBodyAsStream());
                } else {
                    logger.info("Wrong Content-Type: " + httpMethod.getResponseHeader("Content-Type").getValue());
                }
            } else {
                logger.error("Http request has status {}", statusCode);
            }
        } catch (Exception ex) {
            logger.error("Error in mfr send request: ", ex);
        } finally {
            httpMethod.releaseConnection();
            logger.info("Response from MFR: {}", paymentResponse.toString());
        }
        return paymentResponse;
    }

    private void initScheme() throws Exception {
        if (scheme != null) return;

        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_REGULAR_PAYMENT_CERT_PATH));
        ks.load(fis, RuntimeContext.getInstance().getOptionValueString(Option.OPTION_REGULAR_PAYMENT_CERT_PASSWORD).toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, RuntimeContext.getInstance().getOptionValueString(Option.OPTION_REGULAR_PAYMENT_CERT_PASSWORD).toCharArray());
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        X509TrustManager tm = new EasyX509TrustManager(null) {

            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);

        SSLSocketFactory sf = new SSLSocketFactory(sc);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme = new Scheme("https", sf, 443);
    }

    protected PaymentResponse sendRequest_new(String uri, Map<String, String> params) {
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            initScheme();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);

            HttpPost httpPost = new HttpPost(uri);
            List<org.apache.http.NameValuePair> paramsPost = new ArrayList<org.apache.http.NameValuePair>();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsPost.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramsPost));
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

            logger.info("Sending request to MFR: {}", sb.toString());

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            paymentResponse.setStatusCode(statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                logger.info("OK response from MFR");
            } else {
                logger.error("Http request has status {}", statusCode);
            }
        } catch (Exception ex) {
            logger.error("Error in mfr send request: ", ex);
        } finally {
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
    public void updateBankSubscription(Long bsId, Long paymentAmount, Long thresholdAmount, int period, Date validityDate) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
        bs.setValidToDate((validityDate == null && period > 0) ? CalendarUtils.addMonth(new Date(), period) : validityDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBankSubscriptionWithoutPeriod(Long bsId, Long paymentAmount, Long thresholdAmount) {
        BankSubscription bs = em.find(BankSubscription.class, bsId);
        bs.setPaymentAmount(paymentAmount);
        bs.setThresholdAmount(thresholdAmount);
    }

    @SuppressWarnings("unchecked")
    protected List<Long> findSubscriptions(int rows, Long idOfOrg) {
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());

        String orgCondition = (idOfOrg != null)? " and bs.client.org.idOfOrg = " + idOfOrg+ " ":"";

        Query query = em.createQuery("select distinct bs.idOfSubscription from BankSubscription bs \n" +
                " where bs.active = true and bs.client.balance < bs.thresholdAmount \n" +
                " and ((bs.lastSuccessfulPaymentDate < :today or bs.lastSuccessfulPaymentDate is null) " +
                 orgCondition +
                " and (bs.lastUnsuccessfulPaymentDate < :today or bs.lastUnsuccessfulPaymentDate is null)) \n" +
                " and (bs.client.idOfClientGroup not in (:cg) or bs.client.idOfClientGroup is null)")
                .setParameter("today", today).setParameter("cg",
                        Arrays.asList(ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                                ClientGroup.Predefined.CLIENT_DELETED.getValue()));
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
    public List<Long> findSubscriptionsId(Long contractId, String mobile, String san) throws Exception {
        Criteria criteria = em.unwrap(Session.class).createCriteria(BankSubscription.class);
        criteria.setProjection(Projections.property("idOfSubscription")).createAlias("client", "c")
                .add(Restrictions.isNotNull("activationDate"));
        if (contractId != null) {
            criteria.add(Restrictions.eq("c.contractId", contractId));
        }
        if (mobile != null) {
            criteria.add(Restrictions.eq("c.mobile", mobile));
        }
        if (san != null) {
            criteria.add(Restrictions.or(Restrictions.eq("san", san), Restrictions.eq("c.san", san)));
        }
        criteria.addOrder(Order.asc("idOfSubscription"));
        return (List<Long>) criteria.list();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Long> findSubscriptionsIdWithoutActivationDate(Long contractId) throws Exception {
        Criteria criteria = em.unwrap(Session.class).createCriteria(BankSubscription.class);
        criteria.setProjection(Projections.property("idOfSubscription")).createAlias("client", "c");
        criteria.add(Restrictions.eq("c.contractId", contractId));
        criteria.addOrder(Order.asc("idOfSubscription"));
        return (List<Long>) criteria.list();
    }

    public void fillFromXMLResponse(PaymentResponse pr, InputStream is) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = XMLUtils.inputStreamToXML(is, dbf);
        logger.info("XML response from MFR: " + docToString(doc));
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


        /*if(pr.getExtendedStatus().equalsIgnoreCase("DECLINE")) {
            logDocument(doc);
        }*/
        // Обработка кода ошибки, полученной от Acquiro
        Node responseCode = XMLUtils.findFirstChildElement(responseNode, "response_code");
        if (responseCode != null) {
            pr.setResponseCode(StringUtils.trim(responseCode.getTextContent()));
            pr.setResponseCodeShortDescription(getResponseCodeShortDescription(pr.getResponseCode()));
            pr.setResponseCodeFullDescription(getResponseCodeFullDescription(pr.getResponseCode()));
        } else if(pr.getExtendedStatus() != null && pr.getExtendedStatus().equalsIgnoreCase("DECLINE")) {
            pr.setResponseCodeShortDescription(getResponseCodeShortDescription(DEFAULT_RESPONSE_CODE));
            pr.setResponseCodeFullDescription(getResponseCodeFullDescription(DEFAULT_RESPONSE_CODE));
        }
    }

    private String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            return "Error in document parsing";
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

    protected RegularPaymentRequest getRegularPaymentRequest() {
        return regularPaymentRequest;
    }

    protected StatusCheckRequest getStatusCheckRequest() {
        return statusCheckRequest;
    }

    protected SubscriptionDeleteRequest getSubscriptionDeleteRequest() {
        return subscriptionDeleteRequest;
    }

    protected SubscriptionRegRequest getSubscriptionRegRequest() {
        return subscriptionRegRequest;
    }

    public void logDocument(Document doc) {
        try {
            javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(doc);
            java.io.StringWriter writer = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String log = writer.toString();
            logger.error(String.format("Error message received from EMP service: %s", log));
        } catch (Exception e) {
            logger.error("Failed to receive/parse error message document from EMP", e);
        }
    }

    protected String getResponseCodeShortDescription(String responseCode) {
        String desc = getResponseCodeDescription(responseCode, 0);
        if(desc == null || desc.equals("")) {
            desc = getResponseCodeDescription(DEFAULT_RESPONSE_CODE, 0);
        }
        return desc;
    }

    protected String getResponseCodeFullDescription(String responseCode) {
        String desc = getResponseCodeDescription(responseCode, 1);
        if(desc == null || desc.equals("")) {
            desc = getResponseCodeDescription(DEFAULT_RESPONSE_CODE, 1);
        }
        return desc;
    }

    protected String getResponseCodeDescription(String responseCode, int type) {
        if(responseCode == null || StringUtils.isEmpty(responseCode)) {
            return "";
        }
        String[] descs = MFR_RESPONSE_CODES.get(responseCode);
        if(descs == null || descs.length != 2) {
            return "";
        }
        return descs[type];
    }

    protected static final String DEFAULT_RESPONSE_CODE = "" + Integer.MIN_VALUE;
    protected static Map<String, String[]> MFR_RESPONSE_CODES = new HashMap<String, String[]>();
    static {
        MFR_RESPONSE_CODES.put(DEFAULT_RESPONSE_CODE, new String[] {"Код ошибки не получен", "В сообщении от платежной системы код ошибки не найден. Возможно, произошла ошибка по фроду."});
        MFR_RESPONSE_CODES.put("-17", new String[] {"Внутренняя ошибка банка", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("-18", new String[] {"Внутренняя ошибка банка", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("-19", new String[] {"Ошибка авторизации при 3DS", "«Платеж не выполнен. Не пройдена проверка 3 DSecure. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по 3D Sec."});
        MFR_RESPONSE_CODES.put("-3", new String[] {"Внутренняя ошибка банка ", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("1", new String[] {"Требуется доавторизация голосовым подтверждением", "«Платеж не выполнен. Не пройдена проверка 3 DSecure. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по 3D Sec."});
        MFR_RESPONSE_CODES.put("4", new String[] {"Карта заявлена как потерянная или украденная", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("5", new String[] {"Отказ эмитентом без объяснения причины", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("6", new String[] {"Ошибка на стороне банка, общая, без пояснений", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("7", new String[] {"Карта заявлена как украденная с особым вниманием", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("12", new String[] {"Отклонено эмитентом из-за неверного номера карты или типа запроса«Платеж не выполнен: проверьте данные карты.»", "Неверно указана expiration date или неверно введен cvv/cvc-код."});
        MFR_RESPONSE_CODES.put("14", new String[] {"Неверный номер карты«Платеж не выполнен: проверьте данные карты.»", "Неверно указана expiration date или неверно введен cvv/cvc-код."});
        MFR_RESPONSE_CODES.put("15", new String[] {"Банк-эмитент не определен", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("19", new String[] {"Временная ошибка в транзакции, требуется повтор транзакции", "«Платеж по карте не выполнен. Повторите операцию.»"});
        MFR_RESPONSE_CODES.put("30", new String[] {"Ошибка формата входящего запроса", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("41", new String[] {"Карта была утеряна", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("43", new String[] {"Карта была украдена", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями ЗАО «Международные финансовые решения» | Список ошибок 2 обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("51", new String[] {"Недостаточно средств на карте", "«Платеж по карте не может быть выполнен, недостаточно средств на карте.»"});
        MFR_RESPONSE_CODES.put("54", new String[] {"Неверно указана дата истечения срока действия либо срок действия истек«Платеж не выполнен: проверьте данные карты.»", "Неверно указана expiration date или неверно введен cvv/cvc-код."});
        MFR_RESPONSE_CODES.put("57", new String[] {"Транзакция не разрешена для данного типа платежей", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("58", new String[] {"Транзакция не разрешена для данного типа платежей", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("61", new String[] {"Превышение общего лимита банка на терминал по данной карте", "«Платеж не выполнен: превышены установленные для карты лимиты. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по лимитам."});
        MFR_RESPONSE_CODES.put("62", new String[] {"Для карты сработали установленные ограничения на сумму/частоту операций", "«Платеж не выполнен: превышены установленные для карты лимиты. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по лимитам."});
        MFR_RESPONSE_CODES.put("65", new String[] {"Превышени дневного количества операций по данной карте", "«Платеж не выполнен: превышены установленные для карты лимиты. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по лимитам."});
        MFR_RESPONSE_CODES.put("75", new String[] {"Превышено количество попыток авторизации с неверным вводом cvv/cvc", "«Платеж не выполнен: превышены установленные для карты лимиты. За подробностями обращайтесь в Ваш банк.» - отказ эквайера по лимитам."});
        MFR_RESPONSE_CODES.put("82", new String[] {"Неверный формат cvv/cvc«Платеж не выполнен: проверьте данные карты.»", "Неверно указана expiration date или неверно введен cvv/cvc-код."});
        MFR_RESPONSE_CODES.put("89", new String[] {"НАДО УТОЧНЯТЬ В БАНКЕ", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("91", new String[] {"Привышено время ожидания ответа от эмитента, МПС", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("93", new String[] {"Транзакция не может быть завершена в связи с нарешением закона", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
        MFR_RESPONSE_CODES.put("96", new String[] {"Ошибка на стороне МПС, общая, без комментариев", "«Платеж по карте не может быть выполнен, операция отклонена. За подробностями обращайтесь в Ваш банк.» - отказ эмитента или эквайера по прочим причинам."});
    }
}
