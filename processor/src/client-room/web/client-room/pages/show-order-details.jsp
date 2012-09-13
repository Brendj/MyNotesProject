<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.*" %>
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
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="javax.xml.datatype.DatatypeFactory" %>
<%@ page import="javax.xml.datatype.XMLGregorianCalendar" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.OrderDetail" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.PurchaseExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.CardItem" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.PurchaseElementExt" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-order-details_jsp");

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
    try {

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        /*ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService service = new ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService();
        ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");*/

        ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port= clientAuthToken.getPort();

        runtimeContext = new RuntimeContext();
        
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);

        final String ID_OF_ORG_PARAM = "org-id";
        final String ID_OF_ORDER_PARAM = "order-id";
        final String TIME_OF_ORDER_PARAM="order-time";


        boolean haveDataToProcess = false;
        boolean dataProcessSucceed = false;
        //CompositeIdOfOrder compositeIdOfOrder = null;
        String errorMessage = null;
        Date orderTime=new Date();
        try {
           // long idOfOrg = Long.parseLong(request.getParameter(ID_OF_ORG_PARAM));
           // long idOfOrder = Long.parseLong(request.getParameter(ID_OF_ORDER_PARAM));
           // compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, idOfOrder);
            logger.info("TIME_OF_ORDER_PARAM "+request.getParameter(TIME_OF_ORDER_PARAM));
            orderTime.setTime(Long.parseLong(request.getParameter(TIME_OF_ORDER_PARAM)));
            logger.info("orderTime: "+timeFormat.format(orderTime));
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
    /*Session persistenceSession = null;
    Transaction persistenceTransaction = null;*/
    try {
       /* persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();

        Order order = (Order) persistenceSession.get(Order.class, compositeIdOfOrder);*/

        //Date endDate = DateUtils.addDays(orderTime, 1);

        GregorianCalendar greOrderTime = new GregorianCalendar();
        greOrderTime.setTime(orderTime);
        XMLGregorianCalendar xmlOrderTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(greOrderTime);

        /*GregorianCalendar greEndDate = new GregorianCalendar();
        greEndDate.setTime(orderTime);
        XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(greEndDate);*/
        ru.axetta.ecafe.processor.web.bo.client.PurchaseExt order=null;
        PurchaseListResult purchsesResult=port.getPurchaseList(clientAuthToken.getContractId(),xmlOrderTime,xmlOrderTime);

        if(!RC_OK.equals(purchsesResult.getResultCode())){

            throw new Exception(purchsesResult.getDescription());
        }

        List<ru.axetta.ecafe.processor.web.bo.client.PurchaseExt> orders= purchsesResult.getPurchaseList().getP();
         order = orders.get(0);
       // for(PurchaseExt purchaseExt:orders){if(purchaseExt.getTime().toGregorianCalendar().getTime().equals(orderTime)){order = purchaseExt;}  }

        dataProcessSucceed = null != order && order.getTime().toGregorianCalendar().getTime().equals(orderTime);
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
                <%=StringEscapeUtils.escapeHtml(timeFormat.format(order.getTime().toGregorianCalendar().getTime()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Общая сумма покупки с учетом скидки и дотации:</div>
        </td>
        <td align="right">
            <%Long rSum = order.getSum();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rSum))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Скидка (льгота):</div>
        </td>
        <td align="right">
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(order.getSocDiscount()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Скидка (торговая):</div>
        </td>
        <td align="right">
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(order.getTrdDiscount()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Дотация:</div>
        </td>
        <td align="right">
            <%Long grantSum = order.getDonation();%>
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
            <%Long sumByCard = order.getByCard();%>
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
            <%Long sumByCash = order.getByCash();%>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCash))%>
            </div>
        </td>
    </tr>
    <%
       // Card card = order.getCard();
        //Long idOfCard=order.getIdOfCard();
        ru.axetta.ecafe.processor.web.bo.client.CardItem card=null;

         CardListResult cardsResult=port.getCardList(clientAuthToken.getContractId());

          if(!RC_OK.equals(cardsResult.getResultCode())){
              throw new Exception(cardsResult.getDescription());
          }
        List<ru.axetta.ecafe.processor.web.bo.client.CardItem>cards=cardsResult.getCardList().getC();

        logger.info("requiredId: "+order.getIdOfCard());
        logger.info("cardsCount: "+cards.size());
        for(ru.axetta.ecafe.processor.web.bo.client.CardItem cardItem:cards){

            if(cardItem.getIdOfCard().equals(order.getIdOfCard())){card = cardItem;}}


        if (null != card) {

    %>
    <tr>
        <td>
            <div class="output-text">Номер карты:</div>
        </td>
        <td>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCrystalId()))%>
            </div>
        </td>
    </tr>
    <%}%>
</table>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="4">
            <div class="output-text">Состав покупки</div>
        </td>
    </tr>
    <tr class="subheader-tr">
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
       /* Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail currOrderDetail : orderDetails) {*/

        List<ru.axetta.ecafe.processor.web.bo.client.PurchaseElementExt> orderDetails = order.getE();
        for (ru.axetta.ecafe.processor.web.bo.client.PurchaseElementExt currOrderDetail : orderDetails) {
    %>
    <tr>
        <td>
	<%
	    String odStyle="";
            if (currOrderDetail.getType()>= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_0 && currOrderDetail.getType()<= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_9) odStyle="od-complex";
            else if (currOrderDetail.getType()>= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_ITEM_0 && currOrderDetail.getType()<= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_ITEM_9) odStyle="od-c-item";
	%>
            <div class="output-text <%=odStyle%>">
                <%=StringEscapeUtils.escapeHtml(currOrderDetail.getName())%>
            </div>
        </td>
        <td align="center">
            <div class="output-text">
                <%=currOrderDetail.getAmount()%>
            </div>
        </td>
        <td align="center">
            <%Long rPrice = currOrderDetail.getSum(); %>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rPrice))%>
            </div>
        </td>
        <td align="center">
            <%Long detailDiscount = order.getTrdDiscount(); %>
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(detailDiscount))%>
            </div>
        </td>
    </tr>
    <%}%>
</table>
<%
                }
               /* persistenceTransaction.commit();
                persistenceTransaction = null;*/
            } catch (Exception e) {
                logger.error("Failed to build page", e);

                    %>
        <div class="error-output-text"> Не удалось отобразить данные заказа </div>
<%
               // throw new ServletException(e);
            } finally {
               /* HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);*/
            }
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>
<a href="javascript:history.go(-1)" class="command-link">Назад</a>
