<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%--<%@ page import="org.hibernate.Criteria" %>--%>
<%--<%@ page import="org.hibernate.Transaction" %>--%>
<%--<%@ page import="org.hibernate.Session" %>--%>
<%--<%@ page import="org.hibernate.criterion.Restrictions" %>--%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryResult" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-balance-warn_jsp");

    final Long RC_CLIENT_NOT_FOUND = 110L;
    final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    final Long RC_INVALID_DATA = 150L;
    final Long RC_NO_CONTACT_DATA = 160L;
    final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;

    final String RC_OK_DESC="OK";
    final String RC_CLIENT_NOT_FOUND_DESC="Клиент не найден";
    final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="По условиям найден более одного клиента";
    final String RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC="У клиента нет СНИЛС опекуна";
    final String RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC= "У клиента уже есть данный СНИЛС опекуна";
    final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="Ошибка авторизации клиента";
    final String RC_INTERNAL_ERROR_DESC="Внутренняя ошибка";
    final String RC_NO_CONTACT_DATA_DESC="У лицевого счета нет контактных данных";

    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
    RuntimeContext runtimeContext = null;
    /*Session persistenceSession = null;
    Transaction persistenceTransaction = null;*/
    try {


        runtimeContext = new RuntimeContext();
       /* ClientRoomControllerWSService service = new ClientRoomControllerWSService();

        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client"); */
        ClientRoomController port=clientAuthToken.getPort();
        ClientSummaryResult summaryResult=port.getSummary(clientAuthToken.getContractId());
        if(!RC_OK.equals(summaryResult.getResultCode())){

            throw new Exception(summaryResult.getDescription());
        }

        ClientSummaryExt summaryExt= summaryResult.getClientSummary();





       /* persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();
        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();*/
        if (null != summaryExt) {
            if (-summaryExt.getLimit() > summaryExt.getBalance()) {
%>
<table width="100%">
    <tr>
        <td>
            <div class="low-balance-warn">Ваш баланс превысил кредитный порог (<%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(-summaryExt.getLimit()))%> руб). Просим срочно пополнить
                баланс Вашей карты.
            </div>
        </td>
    </tr>
</table>
<%
            }
        }
       /* persistenceTransaction.commit();
        persistenceTransaction = null;*/
    } catch (RuntimeContext.NotInitializedException e) {
        logger.error("Failed to build page", e);
        throw new UnavailableException(e.getMessage());
    } catch (Exception e) {
        logger.error("Failed to build page", e);
                %>
<div class="error-output-text"> Ошибка при проверке баланса </div>
<%
        //throw new ServletException(e);
    } finally {
       /* HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);*/
    }
%>