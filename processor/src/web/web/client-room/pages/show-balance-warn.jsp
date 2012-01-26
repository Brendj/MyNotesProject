<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-balance-warn_jsp");
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();
        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();
        if (null != client) {
            if (-client.getLimit() > client.getBalance()) {
%>
<table width="100%">
    <tr>
        <td>
            <div class="low-balance-warn">Ваш баланс превысил кредитный порог (<%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(-client.getLimit()))%> руб). Просим срочно пополнить
                баланс Вашей карты.
            </div>
        </td>
    </tr>
</table>
<%
            }
        }
        persistenceTransaction.commit();
        persistenceTransaction = null;
    } catch (RuntimeContext.NotInitializedException e) {
        logger.error("Failed to build page", e);
        throw new UnavailableException(e.getMessage());
    } catch (Exception e) {
        logger.error("Failed to build page", e);
        throw new ServletException(e);
    } finally {
        HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);
    }
%>