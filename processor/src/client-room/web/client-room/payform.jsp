<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%--<%@ page import="Person" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.utils.AbbreviationUtils" %>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="windows-1251" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<html lang="ru">
<head>
<%

    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.payform_jsp");
    Long contractId = null;
    if (StringUtils.isNotEmpty(request.getParameter("contractId"))) {
        try {
            contractId = Long.parseLong(request.getParameter("contractId"));
        } catch (NumberFormatException e) {
            logger.error("Failed to get contractId", e);
        }
    }

    if (null != contractId) {
        RuntimeContext runtimeContext = null;
        /*Session persistenceSession = null;
        Transaction persistenceTransaction = null;*/
        try {
            runtimeContext =new  RuntimeContext();
         /*   persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();*/
             ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");

             ClientSummaryExt summaryExt=port.getSummary(contractId).getClientSummary();

            session.setAttribute("__payform.client", summaryExt);
           /* persistenceTransaction.commit();
            persistenceTransaction = null;*/
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
           /* HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);*/
        }
    }
%>
<title>Новая школа - Печать квитанции на пополнение счета</title>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
<meta http-equiv="Content-Language" content="ru">
<jsp:include page="payform_css.jsp"/>
<body>

<div id="toolbox">
    <p>Прежде чем отправлять документ на печать, воспользуйтесь предварительным просмотром (<i>Print preview</i>)
        и убедитесь в корректном отображении документа.
        Обычно квитанция формы «№&nbsp;ПД-4» свободно располагается
        на&nbsp;листе формата А4 и&nbsp;не&nbsp;требует особых настроек
        печати. В&nbsp;редких случаях может потребоваться уменьшить
        боковые поля листа до&nbsp;10–15&nbsp;мм или&nbsp;изменить
        ориентацию страницы на&nbsp;горизонтальную (<i>landscape</i>), чтобы квитанция полностью поместилась в&nbsp;печатное
        поле.</p>
    <input value="Напечатать" onclick="window.print();" type="button" />
    <input value="Закрыть" onclick="window.close();" type="button" />
    <center><span style="font-size: 80%;">информационный блок от начала страницы до пунктирной линии на печать не выводится</span>
    </center>
</div>

<jsp:include page="payform_receipt.jsp"/>

</body>
</html>