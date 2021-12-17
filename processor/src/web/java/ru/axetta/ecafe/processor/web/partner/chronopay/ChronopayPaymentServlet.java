/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */
 package ru.axetta.ecafe.processor.web.partner.chronopay;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayProtocolRequest;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayProtocolResponse;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.*;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 16:02:24
 * To change this template use File | Settings | File Templates.
 */

/**
 * Принимает callback от Chronopay и увеличивает баланс у клиента
 */
@WebServlet(
        name = "ChronopayPaymentServlet",
        description = "ChronopayPaymentServlet",
        urlPatterns = {"/chronopay/acceptpay"}
)
public class ChronopayPaymentServlet extends HttpServlet {

    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ChronopayPaymentServlet.class);


    /**
     * Вычисляет MD5-хеш строки
     * @param str  строка
     * @return  MD5-хеш строки
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getHash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //String s="f78spx";
        //String s="muffin break";
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        // передаем в MessageDigest байт-код строки
        m.update(str.getBytes("utf-8"));
        // получаем MD5-хеш строки без лидирующих нулей
        String s2 = new BigInteger(1, m.digest()).toString(16);
        StringBuilder sb = new StringBuilder(32);
        // дополняем нулями до 32 символов, в случае необходимости
        //System.out.println(32 - s2.length());
        for (int i = 0, count = 32 - s2.length(); i < count; i++) {
            sb.append("0");
        }
        // возвращаем MD5-хеш
        return sb.append(s2).toString();
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        doPost(request,response);
    }

    /**
     * Принимает уведомление о переводе средств на счет "Новой школы" и зачисляет деньги на баланс клиента
     * @param request  callback от Chronopay
     * @param response  либо сообщение об ошибке либо Сообщение "200 OK"
     * @throws ServletException
     * @throws IOException
     */
     @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            logger.info(String.format("Starting of callback processing  from %s", request.getRemoteAddr()));

          // Enumeration names= request.getParameterNames();
           //while(names.hasMoreElements()){
           //    String parameter=names.nextElement().toString();
             //  logger.info("parameterName = "+parameter+" value = "+request.getParameter(parameter));


          // }



            ChronopayProtocolRequest protocolRequest;
            try {
                protocolRequest = new ChronopayProtocolRequest(request);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Protocol request is %s", protocolRequest.toString()));
                }
            } catch (Exception e) {
                logger.error("Failed to read request", e);
                ChronopayProtocolResponse.badRequest().writeTo(response);
                return;
            }
            ChronopayConfig  chronopayConfig=runtimeContext.getPartnerChronopayConfig();

             if(!StringUtils.equals(chronopayConfig.getIp(),"")){
             if(!StringUtils.equals(chronopayConfig.getIp(),request.getLocalAddr())){

                logger.error(String.format("Invalid ip: %s",request.getLocalAddr()));
                ChronopayProtocolResponse.badRequest().writeTo(response);
                return;
            }
            }

            String customerId=protocolRequest.getCustomerId();
            Long transactionId=protocolRequest.getTransactionId();
            String transactionType=protocolRequest.getTransactionType();
            String total =protocolRequest.getTotal();
            String sign=protocolRequest.getSign();




            String requiredSign=null;


             try{
             requiredSign=getHash(chronopayConfig.getSharedSec()+customerId+transactionId.toString()+transactionType+total)  ;
             }catch (Exception e){
                logger.error("Failed to generate sign");
                 ChronopayProtocolResponse.fail().writeTo(response);
                 return;

             }

           // requiredSign="1";
           // RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
            /// logger.info("before sign check");
           // logger.info("customerId: "+customerId);
            //logger.info("transactionId: "+transactionId.toString());
           // logger.info("transactionType: "+transactionType);
           // logger.info("total: "+total);
            //logger.info("rerquiredSign: "+requiredSign);
            //logger.info("requestSign: "+sign);
            if (!StringUtils.equals(requiredSign, sign)) {
                logger.error(String.format("Invalid sign: %s", protocolRequest));
                ChronopayProtocolResponse.badRequest().writeTo(response);
                return;
            }

            Long idOfContragent;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
                contragentCriteria.add(Restrictions.eq("contragentName", "Chronopay"));
                Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
                idOfContragent = contragent.getIdOfContragent();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                throw new ServletException(e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }

            try {
                runtimeContext.getClientPaymentOrderProcessor()
                        .changePaymentOrderStatus(idOfContragent, protocolRequest.getOrderId(),
                                PaymentStatusConverter.paymentStatusToOrderStatus(ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED),
                                CurrencyConverter.rublesToCopecks(Double.parseDouble(protocolRequest.getTotal())),
                                protocolRequest.getTransactionId().toString(),protocolRequest.getOrderId().toString()+"/"+protocolRequest.getDate());
            } catch (Exception e) {
                logger.error("Failed to change clientPaymentOrder state", e);
                ChronopayProtocolResponse.fail().writeTo(response);
                return;
            }

            ChronopayProtocolResponse.success().writeTo(response);
            logger.info(String.format("End of  callback processing from %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            logger.error("Failed", e);
            throw new UnavailableException(e.getMessage());
        }
    }
}