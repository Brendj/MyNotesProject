<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--<%@ page import="RuntimeContext" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%--<%@ page import="Client" %>--%>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--<%@ page import="org.hibernate.Criteria" %>--%>
<%--<%@ page import="org.hibernate.Transaction" %>--%>
<%--<%@ page import="org.hibernate.Session" %>--%>
<%--<%@ page import="org.hibernate.criterion.Restrictions" %>--%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.*" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-cards_jsp");
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



    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    /*Session persistenceSession = null;
    Transaction persistenceTransaction = null;*/
    try {
        runtimeContext = new RuntimeContext();
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

         Long contractId=clientAuthToken.getContractId();

        /*ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");*/

        ClientRoomController port=clientAuthToken.getPort();
        //if(true){throw new Exception();}

%>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="7">
            <div class="output-text">Карты, соответствующие лицевому счету <%=clientAuthToken.getContractId()%>
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
        /*persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();

        Criteria cardsCritieria = persistenceSession.createCriteria(Card.class);
        cardsCritieria.add(Restrictions.eq("client", client));
        cardsCritieria.addOrder(org.hibernate.criterion.Order.asc("createTime"));*/
        CardListResult cardResult= port.getCardList(contractId);
        if(!cardResult.getResultCode().equals(RC_OK)){
            throw new Exception(cardResult.getDescription());
        }
        CardList cardList= cardResult.getCardList();
        List<CardItem> cardItemList=cardList.getC();
        //List cardsList = cardsCritieria.list();
        /*for (Object currCardObject : cardsList) {
            Card currCard = (Card) currCardObject;*/
        for(CardItem currCard:cardItemList){
    %>
    <tr valign="top">
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CardNoFormat.format(currCard.getCrystalId()))%>
            </div>
        </td>
        <td>
            <%int cardState = currCard.getState();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(Card.STATE_NAMES[cardState])%>
                <%--<%
                    if (cardState != Card.ACTIVE_STATE) {
                        String lockReason = currCard.getLockReason();
                        if (StringUtils.isNotEmpty(lockReason)) {
                %>
                (<%=StringEscapeUtils.escapeHtml(lockReason)%>)--%>
                <%--<%
                        }
                    }
                %>--%>
            </div>
        </td>
        <td>
            <%int cardLifeState = currCard.getLifeState();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(Card.LIFE_STATE_NAMES[cardLifeState])%>
                <%--<%
                    Date issueDate = currCard.getIssueTime();
                    if (null != issueDate) {
                %>
                (выдана клиенту <%=StringEscapeUtils.escapeHtml(timeFormat.format(issueDate))%>)
                <%}%>--%>
            </div>
        </td>
        <td>
            <div class="output-text">
               <%-- <%=StringEscapeUtils.escapeHtml(timeFormat.format(currCard.getExpiryDate()))%>--%>
                   <%=StringEscapeUtils.escapeHtml(timeFormat.format(currCard.getExpiryDate().toGregorianCalendar().getTime()))%>
            </div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(timeFormat.format(currCard.getChangeDate().toGregorianCalendar().getTime()))%>
            </div>
        </td>
    </tr>
    <%
            }
           /* persistenceTransaction.commit();
            persistenceTransaction = null;*/

            /*throw new UnavailableException(e.getMessage());*/
        } catch (Exception e) {
            logger.error("Failed to build page", e);
              %>
    <div class="error-output-text"> Не удалось отобразить список карт</div>

    <%

            //throw new ServletException(e);
        } finally {
            /*HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);*/
        }
    %>
</table>
