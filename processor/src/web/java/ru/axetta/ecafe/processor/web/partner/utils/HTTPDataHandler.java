/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.utils;

import net.bull.javamelody.PayloadNameRequestWrapper;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LogInfoService;
import ru.axetta.ecafe.processor.core.persistence.LogInfoServiceOperationType;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;

import javax.xml.ws.handler.MessageContext;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 04.04.16
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class HTTPDataHandler implements ISetHTTPData {
    private final HTTPData data;

    public HTTPDataHandler(HTTPData data) {
        this.data = data;
    }

    private void setIdOfSystem(String idOfSystem) {
        data.setIdOfSystem(idOfSystem);
    }

    private void setSsoId(String ssoId) {
        data.setSsoId(ssoId);
    }

    private void setOperationType(String operationType) {
        data.setOperationType(operationType);
    }

    public HTTPData getData() {
        return data;
    }

    @Override
    public void setData(MessageContext jaxwsContext) {
        if (jaxwsContext == null) {
            return;
        }
        if (jaxwsContext.containsKey("org.apache.cxf.configuration.security.AuthorizationPolicy")) {
            AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext
                .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (authorizationPolicy != null) {
                setIdOfSystem(authorizationPolicy.getUserName());
            }
        }
        if (jaxwsContext.containsKey("org.apache.cxf.message.Message.PROTOCOL_HEADERS")) {
            Map<String, Object> map = (Map)jaxwsContext.get("org.apache.cxf.message.Message.PROTOCOL_HEADERS");
            if (map.containsKey("USER_SSOID")) {
                List<String> ssoIds = (List)map.get("USER_SSOID");
                String ssoId = ssoIds.get(0);
                setSsoId(ssoId);
            }
        }
        if (jaxwsContext.containsKey("HTTP.REQUEST")) {
            PayloadNameRequestWrapper wrapper = (PayloadNameRequestWrapper)jaxwsContext.get("HTTP.REQUEST");
            String methodName = wrapper.getPayloadRequestName();
            if (methodName != null && methodName.startsWith(".")) {
                methodName = methodName.substring(1, methodName.length());
            }
            setOperationType(methodName);
        }
    }

    public void saveLogInfoService(Logger logger, String idOfSystem, Date createdDate, String ssoId, Long idOfClient, String opType) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String strIsOn = runtimeContext.getConfigProperties().getProperty("ecafe.processor.log.infoservice", "0");
        if (strIsOn.equals("0")) {
            return;
        }
        if (opType != null && opType.trim().equals("")) {
            return;
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(LogInfoServiceOperationType.class);
            criteria.add(Restrictions.eq("nameOfOperationType", opType));
            LogInfoServiceOperationType type = (LogInfoServiceOperationType)criteria.uniqueResult();
            if (type == null) {
                type = new LogInfoServiceOperationType(opType);
                session.persist(type);
            }

            LogInfoService log = new LogInfoService(idOfSystem, createdDate, ssoId, idOfClient, type);
            session.persist(log);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error saving record to LogInfoService: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
