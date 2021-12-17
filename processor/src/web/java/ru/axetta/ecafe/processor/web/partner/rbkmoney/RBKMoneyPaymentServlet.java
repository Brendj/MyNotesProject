/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.rbkmoney;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.*;
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

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 16:02:24
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "RBKMoneyPaymentServlet",
        description = "RBKMoneyPaymentServlet",
        urlPatterns = {"/rbkmoney/acceptpay"}
)
public class RBKMoneyPaymentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RBKMoneyPaymentServlet.class);

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            logger.info(String.format("Starting synchronization with %s", request.getRemoteAddr()));
            ProtocolRequest protocolRequest;
            try {
                protocolRequest = new ProtocolRequest(request);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Protocol request is %s", protocolRequest.toString()));
                }
            } catch (Exception e) {
                logger.error("Failed to read request", e);
                ProtocolResponse.badRequest().writeTo(response);
                return;
            }

            RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
            if (!StringUtils.equals(rbkMoneyConfig.getSecretKey(), protocolRequest.getSecretKey())) {
                logger.error(String.format("Inalid secretKey: %s", protocolRequest));
                ProtocolResponse.badRequest().writeTo(response);
                return;
            }

            Long idOfContragent;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
                contragentCriteria.add(Restrictions.eq("contragentName", rbkMoneyConfig.getContragentName()));
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
                                PaymentStatusConverter.paymentStatusToOrderStatus(protocolRequest.getPaymentStatus()),
                                CurrencyConverter.rublesToCopecks(protocolRequest.getRecipientAmount()),
                                protocolRequest.getPaymentId(), null);
            } catch (Exception e) {
                logger.error("Failed to change clientPaymentOrder state", e);
                ProtocolResponse.fail().writeTo(response);
                return;
            }

            ProtocolResponse.success().writeTo(response);
            logger.info(String.format("End of synchronization with %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            logger.error("Failed", e);
            throw new UnavailableException(e.getMessage());
        }
    }
}