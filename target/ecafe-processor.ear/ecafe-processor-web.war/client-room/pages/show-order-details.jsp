<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.classic.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TimeZone" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-order-details_jsp");

    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);

        final String ID_OF_ORG_PARAM = "org-id";
        final String ID_OF_ORDER_PARAM = "order-id";

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        boolean haveDataToProcess = false;
        boolean dataProcessSucceed = false;
        CompositeIdOfOrder compositeIdOfOrder = null;
        String errorMessage = null;

        try {
            long idOfOrg = Long.parseLong(request.getParameter(ID_OF_ORG_PARAM));
            long idOfOrder = Long.parseLong(request.getParameter(ID_OF_ORDER_PARAM));
            compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, idOfOrder);
            haveDataToProcess = true;
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read data", e);
            }
            errorMessage = "Неверные данные и/или формат данных";
        }

        if (!haveDataToProcess) {
%>
<div class="output-text">Ошибка: <%=StringEscapeUtils.escapeHtml(errorMessage)%>
</div>
<%
} else {
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();

        Order order = (Order) persistenceSession.get(Order.class, compositeIdOfOrder);
        dataProcessSucceed = null != order && client.equals(order.getClient());
        if (!dataProcessSucceed) {
            errorMessage = "Покупка с указанными параметрами не найдена";
        }

        if (!dataProcessSucceed) {
%>
<div class="output-text">Ошибка: <%=StringEscapeUtils.escapeHtml(errorMessage)%>
</div>
<%} else {%>
<table>
    <tr>
        <td>
            <div class="output-text">Дата и время совершения покупки:</div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(timeFormat.format(order.getCreateTime()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Общая сумма покупки с учетом скидки и дотации:</div>
        </td>
        <td align="right">
            <%Long rSum = order.getRSum();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rSum))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Скидка:</div>
        </td>
        <td align="right">
            <%Long discount = order.getDiscount();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(discount))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Дотация:</div>
        </td>
        <td align="right">
            <%Long grantSum = order.getGrantSum();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(grantSum))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Cумма, уплаченная по карте:</div>
        </td>
        <td align="right">
            <%Long sumByCard = order.getSumByCard();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCard))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Cумма, уплаченная наличными:</div>
        </td>
        <td align="right">
            <%Long sumByCash = order.getSumByCash();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCash))%>
            </div>
        </td>
    </tr>
    <%
        Card card = order.getCard();
        if (null != card) {
    %>
    <tr>
        <td>
            <div class="output-text">Номер карты:</div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCardNo()))%>
            </div>
        </td>
    </tr>
    <%}%>
</table>

<table>
    <tr>
        <td colspan="4">
            <div class="output-text">Состав покупки</div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Наименование</div>
        </td>
        <td>
            <div class="output-text">Количество</div>
        </td>
        <td>
            <div class="output-text">Цена с учетом скидки</div>
        </td>
        <td>
            <div class="output-text">Скидка</div>
        </td>
    </tr>
    <%
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail currOrderDetail : orderDetails) {
    %>
    <tr>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(currOrderDetail.getMenuDetailName())%>
            </div>
        </td>
        <td align="right">
            <div class="output-text">
                <%=currOrderDetail.getQty()%>
            </div>
        </td>
        <td align="right">
            <%Long rPrice = currOrderDetail.getRPrice(); %>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rPrice))%>
            </div>
        </td>
        <td align="right">
            <%Long detailDiscount = currOrderDetail.getDiscount(); %>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(detailDiscount))%>
            </div>
        </td>
    </tr>
    <%}%>
</table>
<%
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to build page", e);
                throw new ServletException(e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    } finally {
        RuntimeContext.release(runtimeContext);
    }
%>
<a href="javascript:history.go(-1)" class="command-link">Назад</a>
