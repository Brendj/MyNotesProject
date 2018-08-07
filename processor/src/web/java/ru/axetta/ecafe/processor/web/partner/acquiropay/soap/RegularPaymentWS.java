/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPData;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPDataHandler;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 22.10.13
 * Time: 17:11
 */

@WebService
public class RegularPaymentWS extends HttpServlet implements IRegularPayment {

    private static final Logger logger = LoggerFactory.getLogger(RegularPaymentWS.class);

    private static final int RC_BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;
    private static final int RC_INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    private static final String RC_CLIENT_NOT_FOUND_DESC = "Client with %s = %s not found.";
    private static final String RC_INTERNAL_SERVER_ERROR_DESC = "Internal server error.";
    private static final String RC_SUBSCRIPTION_NOT_FOUND_DESC = "Subscription with id = %s not found.";
    private static final String RC_SUBSCRIPTION_CLIENT_NOT_VALID = "Subscription client not valid.";
    private static final String RC_INVALID_PARAMETERS_DESC = "Request has invalid parameters.";
    private static final String RC_CLIENT_ALREADY_HAS_SUBSCRIPTION = "Client already has active subscription";

    private static final int CLIENT_ID_TYPE_CONTRACTID = 0;
    private static final int CLIENT_ID_TYPE_SAN = 1;
    private static final int CLIENT_ID_TYPE_MOBILE = 2;

    @Resource
    private WebServiceContext context;

    @Autowired
    @Qualifier("regularPaymentSubscriptionService")
    private RegularPaymentSubscriptionService rpService;

    @Autowired
    private RuntimeContext runtimeContext;

    @PostConstruct
    public void initServices() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentCreateSubscription(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType, @WebParam(name = "contractID") String contractID,
            @WebParam(name = "accountRegion") int accountRegion,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency,
            @WebParam(name = "subscriptionPeriodOfValidity") int period) {
        Session session = null;
        Transaction tr = null;
        RequestResult requestResult = new RequestResult();
        try {
            session = runtimeContext.createPersistenceSession();
            tr = session.beginTransaction();
            Long contractId = ContractIdFormat.parse(contractID);
            Client c = DAOUtils.findClientByContractId(session, contractId);
            if (c == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_CLIENT_NOT_FOUND_DESC,
                        clientIDType == CLIENT_ID_TYPE_SAN ? "san" : "mobile phone number", clientID));
                return requestResult;
            }
            if (!(lowerLimitAmount > 0 && paymentAmount > 0 && period >= 1 && period <= 12)) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(RC_INVALID_PARAMETERS_DESC);
                return requestResult;
            }
            if (rpService.subscriptionExistsByClient(c.getIdOfClient())) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(RC_CLIENT_ALREADY_HAS_SUBSCRIPTION);
                return requestResult;
            }
            HTTPData data = new HTTPData();
            HTTPDataHandler handler = new HTTPDataHandler(data);
            Date date = new Date(System.currentTimeMillis());
            logRequest(handler);
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                    c.getIdOfClient(), handler.getData().getOperationType());
            MfrRequest mfrRequest = rpService
                    .createRequestForSubscriptionReg(contractId, paymentAmount, lowerLimitAmount, period);
            Map<String, String> params = rpService.getParamsForRegRequest(mfrRequest);
            requestResult.setParametersList(new ParametersList());
            requestResult.getParametersList().setList(new ArrayList<ParametersList.Parameter>());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                requestResult.getParametersList().getList()
                        .add(new ParametersList.Parameter(entry.getKey(), entry.getValue()));
            }
            requestResult.setErrorCode(0);
            tr.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(tr, logger);
            logger.error(ex.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        } finally {
            HibernateUtils.close(session, logger);
        }
        return requestResult;
    }

    // метод с облегченной проверкой
    @Override
    @WebMethod
    public RequestResult regularPaymentEasyCheckCreateSubscription(@WebParam(name = "contractID") Long contractID,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency) {
        Session session = null;
        Transaction tr = null;
        RequestResult requestResult = new RequestResult();
        try {
            session = runtimeContext.createPersistenceSession();
            tr = session.beginTransaction();
            Long contractId = contractID;
            Client c = DAOUtils.findClientByContractId(session, contractId);
            int period = 12;
            MfrRequest mfrRequest = rpService.createRequestForSubscriptionReg(contractId, paymentAmount, lowerLimitAmount, period);
            Map<String, String> params = rpService.getParamsForRegRequest(mfrRequest);
            requestResult.setParametersList(new ParametersList());
            requestResult.getParametersList().setList(new ArrayList<ParametersList.Parameter>());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                requestResult.getParametersList().getList().add(new ParametersList.Parameter(entry.getKey(), entry.getValue()));
            }
            requestResult.setErrorCode(0);
            tr.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(tr, logger);
            logger.error(ex.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        } finally {
            HibernateUtils.close(session, logger);
        }
        return requestResult;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentDeleteSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID,
            @WebParam(name = "contractId") Long contractId) {
        boolean result = false;
        RequestResult requestResult = new RequestResult();
        try {
            BankSubscription bs = rpService.findBankSubscription(regularPaymentSubscriptionID);
            if (bs == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, regularPaymentSubscriptionID));
                return requestResult;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, requestResult)) {
                return requestResult;
            }
            HTTPData data = new HTTPData();
            HTTPDataHandler handler = new HTTPDataHandler(data);
            Date date = new Date(System.currentTimeMillis());
            logRequest(handler);
            Long idOfClient = null;
            if (contractId != null) {
                idOfClient = DAOService.getInstance().getClientByContractId(contractId).getIdOfClient();
            } else if (bs.getClient() != null) {
                idOfClient = bs.getClient().getIdOfClient();
            }
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                    idOfClient, handler.getData().getOperationType());
            result = !bs.isActive() || rpService.deactivateSubscription(regularPaymentSubscriptionID);
        } catch (Exception ex) {
            logger.error("Error in delete regular payment subscription: ", ex);
        }
        if (result) {
            requestResult.setErrorCode(0);
        } else {
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    private void logRequest(HTTPDataHandler handler) {
        MessageContext jaxwsContext = context.getMessageContext();
        if (handler != null) {
            handler.setData(jaxwsContext);
        }
        /*AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext
                .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
        if (handler != null) {
            handler.setIdOfSystem(authorizationPolicy.getUserName());
            if (jaxwsContext.containsKey("org.apache.cxf.message.Message.PROTOCOL_HEADERS")) {
                Map<String, Object> map = (Map)jaxwsContext.get("org.apache.cxf.message.Message.PROTOCOL_HEADERS");
                if (map.containsKey("USER_SSOID")) {
                    List<String> ssoIds = (List)map.get("USER_SSOID");
                    String ssoId = ssoIds.get(0);
                    handler.setSsoId(ssoId);
                }
            }
            if (jaxwsContext.containsKey("HTTP.REQUEST")) {
                PayloadNameRequestWrapper wrapper = (PayloadNameRequestWrapper)jaxwsContext.get("HTTP.REQUEST");
                String methodName = wrapper.getPayloadRequestName();
                if (methodName != null && methodName.startsWith(".")) {
                    methodName = methodName.substring(1, methodName.length());
                }
                handler.setOperationType(methodName);
            }
        }*/
    }

    private boolean checkSubscriptionContractId(BankSubscription bs, Long contractId, RequestResult requestResult) {
        if (contractId != null) {
            Client c = DAOService.getInstance().getClientByContractId(contractId);
            if (c == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_CLIENT_NOT_FOUND_DESC, "contractId", contractId));
                return false;
            }
            if (c.getIdOfClient().longValue() != bs.getClient().getIdOfClient().longValue()) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(RC_SUBSCRIPTION_CLIENT_NOT_VALID);
                return false;
            }
        }
        return true;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentEditSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency,
            @WebParam(name = "subscriptionPeriodOfValidity") int period,
            @WebParam(name = "contractId") Long contractId) {
        RequestResult requestResult = new RequestResult();
        try {
            BankSubscription bs = rpService.findBankSubscription(regularPaymentSubscriptionID);
            if (bs == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, regularPaymentSubscriptionID));
                return requestResult;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, requestResult)) {
                return requestResult;
            }

            if (!(lowerLimitAmount > 0 && paymentAmount > 0 && period >= 1 && period <= 12)) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(RC_INVALID_PARAMETERS_DESC);
                return requestResult;
            }
            rpService.updateBankSubscription(regularPaymentSubscriptionID, paymentAmount, lowerLimitAmount, period);
            requestResult.setErrorCode(0);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentEasyCheckEditSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID,
            @WebParam(name = "contractId") Long contractId,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency) {
        RequestResult requestResult = new RequestResult();
        try {
            BankSubscription bs = rpService.findBankSubscription(regularPaymentSubscriptionID);
            if (bs == null) {
                requestResult.setErrorCode(RC_BAD_REQUEST);
                requestResult.setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, regularPaymentSubscriptionID));
                return requestResult;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, requestResult)) {
                return requestResult;
            }
            rpService.updateBankSubscriptionWithoutPeriod(regularPaymentSubscriptionID, paymentAmount, lowerLimitAmount);
            requestResult.setErrorCode(0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentReadSubscriptionList(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType) {
        RequestResult requestResult = new RequestResult();
        List<Long> subscriptionList;
        try {
            if (clientIDType == CLIENT_ID_TYPE_CONTRACTID) {
                Long contractId = Long.parseLong(clientID);
                subscriptionList = rpService.findSubscriptionsId(contractId, null, null);
            } else if (clientIDType == CLIENT_ID_TYPE_SAN) {
                subscriptionList = rpService.findSubscriptionsId(null, null, clientID);
            } else if (clientIDType == CLIENT_ID_TYPE_MOBILE) {
                subscriptionList = rpService
                        .findSubscriptionsId(null, PhoneNumberCanonicalizator.canonicalize(clientID), null);
            } else {
                subscriptionList = new ArrayList<Long>();
            }
            requestResult.setSubscriptionList(new SubscriptionList());
            requestResult.getSubscriptionList().setIdList(subscriptionList);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    //Выбирает все подписки без учета даты activationDate - даты активации
    @Override
    @WebMethod
    public RequestResult regularPaymentEasyCheckReadSubscriptionList(@WebParam(name = "contractId") Long contractId) {
        RequestResult requestResult = new RequestResult();
        List<Long> subscriptionList;
        try {
            if (contractId != null) {
                subscriptionList = rpService.findSubscriptionsIdWithoutActivationDate(contractId);
            } else {
                subscriptionList = new ArrayList<Long>();
            }
            requestResult.setSubscriptionList(new SubscriptionList());
            requestResult.getSubscriptionList().setIdList(subscriptionList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            requestResult.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            requestResult.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return requestResult;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentReadSubscriptionListWithInfo(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType) {
        RequestResult result = new RequestResult();
        List<Long> subscriptionList;
        try {
            if (clientIDType == CLIENT_ID_TYPE_CONTRACTID) {
                Long contractId = Long.parseLong(clientID);
                subscriptionList = rpService.findSubscriptionsId(contractId, null, null);
            } else if (clientIDType == CLIENT_ID_TYPE_SAN) {
                subscriptionList = rpService.findSubscriptionsId(null, null, clientID);
            } else if (clientIDType == CLIENT_ID_TYPE_MOBILE) {
                subscriptionList = rpService
                        .findSubscriptionsId(null, PhoneNumberCanonicalizator.canonicalize(clientID), null);
            } else {
                subscriptionList = new ArrayList<Long>();
            }
            result.setSubscriptionList(new SubscriptionList());
            result.getSubscriptionList().setInfoList(new ArrayList<SubscriptionInfo>());
            for (Long id : subscriptionList) {
                BankSubscription bs = rpService.getSubscriptionWithClient(id);
                SubscriptionInfo si = createSubscriptionInfo(bs);
                result.getSubscriptionList().getInfoList().add(si);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            result.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return result;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentReadSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID,
            @WebParam(name = "contractId") Long contractId) {
        RequestResult result = new RequestResult();
        try {
            BankSubscription bs = rpService.getSubscriptionWithClient(regularPaymentSubscriptionID);
            if (bs == null) {
                result.setErrorCode(RC_BAD_REQUEST);
                result.setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, regularPaymentSubscriptionID));
                return result;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, result)) {
                return result;
            }

            SubscriptionInfo si = createSubscriptionInfo(bs);
            result.setSubscriptionInfo(si);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            result.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return result;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentReadPayments(
            @WebParam(name = "regularPaymentSubscriptionID") Long subscriptionID,
            @WebParam(name = "beginDate") Date beginDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "contractId") Long contractId) {
        RequestResult result = new RequestResult();
        try {
            BankSubscription bs = rpService.findBankSubscription(subscriptionID);
            if (bs == null) {
                result.setErrorCode(RC_BAD_REQUEST);
                result.setErrorDesc(String.format(RC_SUBSCRIPTION_NOT_FOUND_DESC, subscriptionID));
                return result;
            }
            if (contractId != null && !checkSubscriptionContractId(bs, contractId, result)) {
                return result;
            }

            List<RegularPayment> rpList = rpService.getSubscriptionPayments(subscriptionID, beginDate, endDate, false);
            result.setPaymentList(new RegularPaymentList());
            result.getPaymentList().setList(new ArrayList<RegularPaymentList.RegularPaymentInfo>());
            for (RegularPayment rp : rpList) {
                RegularPaymentList.RegularPaymentInfo rpi = new RegularPaymentList.RegularPaymentInfo(
                        rp.getPaymentAmount(), rp.getPaymentDate(), rp.getClientBalance(), rp.getThresholdAmount(),
                        rp.getStatus());
                result.getPaymentList().getList().add(rpi);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.setErrorCode(RC_INTERNAL_SERVER_ERROR);
            result.setErrorDesc(RC_INTERNAL_SERVER_ERROR_DESC);
        }
        return result;
    }

    private SubscriptionInfo createSubscriptionInfo(BankSubscription bs) {
        SubscriptionInfo si = new SubscriptionInfo();
        si.setIdOfSubscription(bs.getIdOfSubscription());
        si.setPaymentAmount(bs.getPaymentAmount());
        si.setContractID(ContractIdFormat.format(bs.getClient().getContractId()));
        Integer clientIDType = StringUtils.isEmpty(bs.getSan()) ? CLIENT_ID_TYPE_MOBILE : CLIENT_ID_TYPE_SAN;
        si.setClientIDType(clientIDType);
        si.setClientID(clientIDType == CLIENT_ID_TYPE_SAN ? bs.getSan() : bs.getClient().getMobile());
        si.setCurrency(643);
        si.setLowerLimitAmount(bs.getThresholdAmount());
        si.setSubscriptionPeriodOfValidity(bs.getMonthsCount());
        si.setRegistrationDate(bs.getActivationDate());
        si.setValidityDate(bs.getValidToDate());
        si.setDeactivationDate(bs.getDeactivationDate());
        si.setStatus(bs.getStatus());
        si.setLastPaymentStatus(bs.getLastPaymentStatus());
        si.setLastPaymentDate(
                MfrRequest.PAYMENT_SUCCESSFUL.equals(bs.getLastPaymentStatus()) ? bs.getLastSuccessfulPaymentDate()
                        : bs.getLastUnsuccessfulPaymentDate());
        si.setCardNumber(bs.getMaskedCardNumber());
        si.setCardHolder(bs.getCardHolder());
        si.setExpMonth(bs.getExpMonth());
        si.setExpYear(bs.getExpYear());
        return si;
    }

    @Override
    @WebMethod
    public RequestResult regularPaymentReadSettings() {
        RequestResult result = new RequestResult();
        LowerLimitAmountList limitAmountList = new LowerLimitAmountList();
        limitAmountList.setList(new ArrayList<Long>());
        result.setLowerLimitAmountList(limitAmountList);
        String lowerLimitAmounts = runtimeContext.getOptionValueString(Option.OPTION_THRESHOLD_VALUES);
        String[] lowerLimitAmountArray = StringUtils.split(lowerLimitAmounts, ";");
        for (String value : lowerLimitAmountArray) {
            limitAmountList.getList().add(Long.valueOf(value));
        }
        PaymentAmountList paymentAmountList = new PaymentAmountList();
        paymentAmountList.setList(new ArrayList<Long>());
        result.setPaymentAmountList(paymentAmountList);
        String paymentAmounts = runtimeContext.getOptionValueString(Option.OPTION_AUTOREFILL_VALUES);
        String[] paymentAmountArray = StringUtils.split(paymentAmounts, ";");
        for (String value : paymentAmountArray) {
            paymentAmountList.getList().add(Long.valueOf(value));
        }
        return result;
    }
}
