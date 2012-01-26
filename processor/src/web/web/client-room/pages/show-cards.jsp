<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TimeZone" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-cards_jsp");

    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
%>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="7">
            <div class="output-text">Карты, соответствующие договору <%=clientAuthToken.getContractId()%>
            </div>
        </td>
    </tr>
    <tr class="subheader-tr">
        <td>
            <div class="output-text">Номер карты</div>
        </td>
        <td>
            <div class="output-text">Статус карты</div>
        </td>
        <td>
            <div class="output-text">Состояние</div>
        </td>
        <td>
            <div class="output-text">Дата окончания срока действия</div>
        </td>
        <td>
            <div class="output-text">Дата последних изменений</div>
        </td>
    </tr>
    <%
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();

        Criteria cardsCritieria = persistenceSession.createCriteria(Card.class);
        cardsCritieria.add(Restrictions.eq("client", client));
        cardsCritieria.addOrder(org.hibernate.criterion.Order.asc("createTime"));

        List cardsList = cardsCritieria.list();
        for (Object currCardObject : cardsList) {
            Card currCard = (Card) currCardObject;
    %>
    <tr valign="top">
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CardNoFormat.format(currCard.getCardNo()))%>
            </div>
        </td>
        <td>
            <%int cardState = currCard.getState();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(Card.STATE_NAMES[cardState])%>
                <%
                    if (cardState != Card.ACTIVE_STATE) {
                        String lockReason = currCard.getLockReason();
                        if (StringUtils.isNotEmpty(lockReason)) {
                %>
                (<%=StringEscapeUtils.escapeHtml(lockReason)%>)
                <%
                        }
                    }
                %>
            </div>
        </td>
        <td>
            <%int cardLifeState = currCard.getLifeState();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(Card.LIFE_STATE_NAMES[cardLifeState])%>
                <%
                    Date issueDate = currCard.getIssueTime();
                    if (null != issueDate) {
                %>
                (выдана клиенту <%=StringEscapeUtils.escapeHtml(timeFormat.format(issueDate))%>)
                <%}%>
            </div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(timeFormat.format(currCard.getValidTime()))%>
            </div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(timeFormat.format(currCard.getUpdateTime()))%>
            </div>
        </td>
    </tr>
    <%
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to build page", e);
            throw new ServletException(e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    %>
</table>
