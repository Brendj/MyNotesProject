<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.xml.datatype.XMLGregorianCalendar" %>
<%@ page import="javax.xml.datatype.DatatypeFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.*" %>

<%-- Код для динамической загрузки Yahoo UI Calendar dependancies --%>

<!--Include YUI Loader: -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yuiloader/yuiloader-min.js"></script>

<!--Use YUI Loader to bring in your other dependencies: -->
<script type="text/javascript">
    // Instantiate and configure YUI Loader:
    (function() {
        var loader = new YAHOO.util.YUILoader({
            base: "",
            require: [
                "calendar"],
            loadOptional: false,
            combine: true,
            filter: "MIN",
            allowRollup: true,
            onSuccess: function() {
                //you can make use of all requested YUI modules
                //here.
            }
        });
        // Load the files using the insert() method.
        loader.insert();
    })();
</script>

<%
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
    final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="Ошибка авторизации клиента";
    final String RC_INTERNAL_ERROR_DESC="Внутренняя ошибка";
    final String RC_NO_CONTACT_DATA_DESC="У лицевого счета нет контактных данных";

    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-orders-and-payments_jsp");
    final String PROCESS_PARAM = "submit";
    final String START_DATE_PARAM = "start-date";
    final String END_DATE_PARAM = "end-date";
    final String ID_OF_ORG_PARAM = "org-id";
    final String TIME_OF_ORDER_PARAM="order-time";
    final String ID_OF_ORDER_PARAM = "order-id";
    final String PAGE_PARAM = "page";
    final String SHOW_ORDER_DETAILS_PAGE = "show-order-details";
    final String PARAMS_TO_REMOVE[] = {
            PROCESS_PARAM, START_DATE_PARAM, END_DATE_PARAM, ID_OF_ORG_PARAM, ID_OF_ORDER_PARAM};

    RuntimeContext runtimeContext = null;
    try {
        runtimeContext =new RuntimeContext();
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(session);
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);


        ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();
         Long contractId=clientAuthToken.getContractId();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);


        URI formAction;
        try {
            formAction = UriUtils
                    .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
        } catch (Exception e) {
            logger.error("Failed to build form action", e);
            throw new ServletException(e);
        }

        Date currentTime = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        Date yesterdayDate = DateUtils.addDays(currentTime, -1);
        Date beforeYesterdayDate = DateUtils.addDays(currentTime, -1);

        localCalendar.setTime(currentTime);
        localCalendar.set(Calendar.DAY_OF_WEEK, localCalendar.getFirstDayOfWeek());
        Date thisWeekStartDate = localCalendar.getTime();
        Date thisWeekEndDate = DateUtils.addDays(DateUtils.addWeeks(thisWeekStartDate, 1), -1);
        Date prevWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, -1);
        Date prevWeekEndDate = DateUtils.addWeeks(thisWeekEndDate, -1);
        Date beforePrevWeekStartDate = DateUtils.addWeeks(prevWeekStartDate, -1);
        Date beforePrevWeekEndDate = DateUtils.addWeeks(prevWeekEndDate, -1);

        String todayUri;
        String yesterdayUri;
        String beforeYesterdayUri;
        String thisWeeekUri;
        String prevWeeekUri;
        String beforePrevWeeekUri;
        try {
            todayUri = UriUtils.putParam(
                    UriUtils.putParam(UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(currentTime)),
                            END_DATE_PARAM, dateFormat.format(currentTime)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            yesterdayUri = UriUtils.putParam(
                    UriUtils.putParam(UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(yesterdayDate)),
                            END_DATE_PARAM, dateFormat.format(yesterdayDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            beforeYesterdayUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(beforeYesterdayDate)),
                    END_DATE_PARAM, dateFormat.format(beforeYesterdayDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            thisWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(thisWeekStartDate)),
                    END_DATE_PARAM, dateFormat.format(thisWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            prevWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(prevWeekStartDate)),
                    END_DATE_PARAM, dateFormat.format(prevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            beforePrevWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, dateFormat.format(beforePrevWeekStartDate)),
                    END_DATE_PARAM, dateFormat.format(beforePrevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
        } catch (Exception e) {
            logger.error("Error during URI building", e);
            throw new ServletException(e);
        }

        boolean haveDataToProcess = StringUtils.isNotEmpty(request.getParameter(PROCESS_PARAM));
        boolean dataToProcessVerified = true;
        String startDateParamValue = "";
        String endDateParamValue = "";
        Date startDate = null;
        Date endDate = null;
        String errorMessage = null;

        if (haveDataToProcess) {
            try {
                startDateParamValue = StringUtils.defaultString(request.getParameter(START_DATE_PARAM));
                endDateParamValue = StringUtils.defaultString(request.getParameter(END_DATE_PARAM));
                startDate = dateFormat.parse(startDateParamValue);
                endDate = dateFormat.parse(endDateParamValue);
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to read data", e);
                }
                dataToProcessVerified = false;
                errorMessage = "Неверные данные и/или формат данных";
            }
        } else {
            haveDataToProcess = true;
            startDate = thisWeekStartDate;
            endDate = thisWeekEndDate;
            startDateParamValue = dateFormat.format(startDate);
            endDateParamValue = dateFormat.format(endDate);
        }
%>

<script type="text/javascript">
    var startCalendar = null;
    var endCalendar = null;

    function localizeCalendar(calendar) {
        calendar.cfg.setProperty("DATE_FIELD_DELIMITER", ".");
        calendar.cfg.setProperty("MDY_DAY_POSITION", 1);
        calendar.cfg.setProperty("MDY_MONTH_POSITION", 2);
        calendar.cfg.setProperty("MDY_YEAR_POSITION", 3);
        calendar.cfg.setProperty("MD_DAY_POSITION", 1);
        calendar.cfg.setProperty("MD_MONTH_POSITION", 2);
        calendar.cfg.setProperty("START_WEEKDAY", 1);
        calendar.cfg.setProperty("MONTHS_SHORT", [
            "Янв", "Фев", "Март", "Апр", "Май", "Июнь", "Июль", "Авг", "Сен", "Окт", "Нояб", "Дек"]);
        calendar.cfg.setProperty("MONTHS_LONG", [
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь",
            "Декабрь"]);
        calendar.cfg.setProperty("WEEKDAYS_1CHAR", [
            "В", "П", "В", "С", "Ч", "Т", "С"]);
        calendar.cfg.setProperty("WEEKDAYS_SHORT", [
            "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"]);
        calendar.cfg.setProperty("WEEKDAYS_MEDIUM", [
            "Вос", "Пон", "Втор", "Среда", "Чет", "Пят", "Суб"]);
        calendar.cfg.setProperty("WEEKDAYS_LONG", [
            "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"]);
    }

    startCalendarInit = function() {
        if (null == startCalendar) {
            startCalendar = new YAHOO.widget.Calendar("startCalendar", "startCalendarContainer");
            localizeCalendar(startCalendar);
            startCalendar.selectEvent.subscribe(ripStartDate);
            startCalendar.render();
        }
        var btn = YAHOO.util.Dom.get("toggleStartCalendarBtn");
        btn.onclick = startCalendarHide;
        startCalendar.show();
    }

    startCalendarHide = function() {
        var btn = YAHOO.util.Dom.get("toggleStartCalendarBtn");
        btn.onclick = startCalendarInit;
        if (null != startCalendar) {
            startCalendar.hide();
        }
    }

    endCalendarInit = function() {
        if (null == endCalendar) {
            endCalendar = new YAHOO.widget.Calendar("endCalendar", "endCalendarContainer");
            localizeCalendar(endCalendar);
            endCalendar.selectEvent.subscribe(ripEndDate);
            endCalendar.render();
        }
        var btn = YAHOO.util.Dom.get("toggleEndCalendarBtn");
        btn.onclick = endCalendarHide;
        endCalendar.show();
    }

    endCalendarHide = function() {
        var btn = YAHOO.util.Dom.get("toggleEndCalendarBtn");
        btn.onclick = endCalendarInit;
        if (null != endCalendar) {
            endCalendar.hide();
        }
    }

    ripStartDate = function(type, args) {
        var dates = args[0];
        var date = dates[0];
        var theYear = date[0];
        var theMonth = date[1];
        var theDay = date[2];
        var field = YAHOO.util.Dom.get("startDate");
        field.value = theDay + "." + theMonth + "." + theYear;
        startCalendarHide();
    }

    ripEndDate = function(type, args) {
        var dates = args[0];
        var date = dates[0];
        var theYear = date[0];
        var theMonth = date[1];
        var theDay = date[2];
        var field = YAHOO.util.Dom.get("endDate");
        field.value = theDay + "." + theMonth + "." + theYear;
        endCalendarHide();
    }
</script>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <%if (!dataToProcessVerified) {%>
    <div class="output-text">Ошибка: <%=StringEscapeUtils.escapeHtml(errorMessage)%>
    </div>
    <%}%>
    <table>
        <tr>
            <td>
                <div class="output-text">Начальная дата</div>
            </td>
            <td>
                <input type="text" class="input-text" name="<%=START_DATE_PARAM%>" id="startDate"
                       value="<%=StringEscapeUtils.escapeHtml(startDateParamValue)%>" />
            </td>
            <td>
                <input type="button" class="command-button" value="..." id="toggleStartCalendarBtn"
                       onclick="startCalendarInit();" />
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <div class="yui-skin-sam">
                    <div id="startCalendarContainer" />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Конечная дата</div>
            </td>
            <td>
                <input type="text" class="input-text" name="<%=END_DATE_PARAM%>" id="endDate"
                       value="<%=StringEscapeUtils.escapeHtml(endDateParamValue)%>" />
            </td>
            <td>
                <input type="button" class="command-button" value="..." id="toggleEndCalendarBtn"
                       onclick="endCalendarInit();" />
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <div class="yui-skin-sam">
                    <div id="endCalendarContainer" />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Показать" class="command-button" />
            </td>
        </tr>
    </table>
    <table>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(todayUri))%>"
                   class="command-link">Сегодня</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(thisWeeekUri))%>" class="command-link">На
                    этой
                    неделе</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(yesterdayUri))%>"
                   class="command-link">Вчера</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(prevWeeekUri))%>" class="command-link">На
                    прошлой неделе</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(beforeYesterdayUri))%>"
                   class="command-link">Позавчера</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(beforePrevWeeekUri))%>"
                   class="command-link">На позапрошлой неделе</a>
            </td>
        </tr>
    </table>
</form>

<%if (haveDataToProcess && dataToProcessVerified) {%>
<table class="infotable">
<tr class="header-tr">
    <td colspan="9">
        <div class="output-text">Покупки, совершенные с <%=StringEscapeUtils.escapeHtml(dateFormat.format(startDate))%>
            по <%=StringEscapeUtils.escapeHtml(dateFormat.format(endDate))%>
        </div>
    </td>
</tr>
<tr class="subheader-tr">
    <td>
        <div class="output-text">Дата</div>
    </td>
    <td>
        <div class="output-text">Сумма покупки</div>
    </td>
    <td>
        <div class="output-text">Социальная скидка</div>
    </td>
    <td>
        <div class="output-text">Торговая скидка</div>
    </td>
    <td>
        <div class="output-text">Дотация</div>
    </td>
    <td>
        <div class="output-text">Наличными</div>
    </td>
    <td>
        <div class="output-text">По карте</div>
    </td>
    <td>
        <div class="output-text">Номер карты</div>
    </td>
    <td>
        <div class="output-text">Состав</div>
    </td>
</tr>
<%


    try {
        List<ru.axetta.ecafe.processor.web.bo.client.PurchaseExt> ordersList=null;
        List<ru.axetta.ecafe.processor.web.bo.client.Payment> clientPaymentsList=null;
        List<Sms>clientSmsList=null;
         //port=null;








        Date nextToEndDate = DateUtils.addDays(endDate, 1);

        GregorianCalendar greStartDate = new GregorianCalendar();
        greStartDate.setTime(startDate);
        XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(greStartDate);

        GregorianCalendar greEndDate = new GregorianCalendar();
        greStartDate.setTime(nextToEndDate);
        XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(greEndDate);



        PurchaseListResult ordersResult=port.getPurchaseList(contractId,xmlStartDate,xmlEndDate);

        if(!ordersResult.getResultCode().equals(RC_OK)){

            throw new Exception(ordersResult.getDescription());

        }
        ordersList= ordersResult.getPurchaseList().getP();


        PaymentListResult paymentsResult= port.getPaymentList(contractId,xmlStartDate,xmlEndDate);
        if(!paymentsResult.getResultCode().equals(RC_OK)){

            throw new Exception(paymentsResult.getDescription());

        }

        clientPaymentsList=paymentsResult.getPaymentList().getP();
         ClientSmsListResult smsResult=port.getClientSmsList(contractId,xmlStartDate,xmlEndDate);

        if(!smsResult.getResultCode().equals(RC_OK)){

            throw new Exception(smsResult.getDescription());

        }
       clientSmsList= smsResult.getClientSmsList().getS();


        int orderIndex = 0;
        int ordersCount = ordersList.size();
        int clientPaymentIndex = 0;
        int clientPaymentsCount = clientPaymentsList.size();
        int clientSmsIndex = 0;
        int clientSmsCount = clientSmsList.size();

        boolean done = false;
        try{
        while (!done) {
            Date minTime = null;
            if (orderIndex != ordersCount) {
                ru.axetta.ecafe.processor.web.bo.client.PurchaseExt order =  ordersList.get(orderIndex);

                //minTime = order.getCreateTime();
                minTime=order.getTime().toGregorianCalendar().getTime();
            }
            if (clientPaymentIndex != clientPaymentsCount) {
                ru.axetta.ecafe.processor.web.bo.client.Payment clientPayment =  clientPaymentsList.get(clientPaymentIndex);
                Date clientPaymentTime = clientPayment.getTime().toGregorianCalendar().getTime();
                minTime = minTime == null ? clientPaymentTime
                        : (clientPaymentTime.before(minTime) ? clientPaymentTime : minTime);
            }

            if (minTime == null) {
                done = true;
                continue;
            }
            if (clientPaymentIndex != clientPaymentsCount) {
                ru.axetta.ecafe.processor.web.bo.client.Payment clientPayment =  clientPaymentsList.get(clientPaymentIndex);
                if (clientPayment.getTime().toGregorianCalendar().getTime().equals(minTime)) {

%>

<tr valign="top">
    <td>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(clientPayment.getTime().toGregorianCalendar().getTime()))%>
        </div>
    </td>
    <td colspan="5">
        <%

            Long clientPaymentSum = clientPayment.getSum();
            //String transferInfo = PaymentTextUtils.buildTransferInfo(clientPayment);
        %>
        <div class="output-text topup-info">Пополнение баланса
            +<%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(clientPaymentSum))%>
            (<%=StringEscapeUtils.escapeHtml(clientPayment.getOrigin())%>)
            идентификатор платежа <%--<%=clientPayment.getIdOfPayment()%>--%>
        </div>
    </td>
    <td>
        <%
            /*Card card = clientPayment.getTransaction().getCard();
            if (null != card) {*/
        %>
        <%--<div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCardNo()))%>
        </div>--%>
        <%/*}*/%>
    </td>
    <td />
</tr>
<%
            ++clientPaymentIndex;
        }
    }
    if (orderIndex != ordersCount) {
        ru.axetta.ecafe.processor.web.bo.client.PurchaseExt order =  ordersList.get(orderIndex);
        if (order.getTime().toGregorianCalendar().getTime().equals(minTime)) {
            //if (logger.isDebugEnabled()) {
            //    logger.debug(order.toString());
            //}
%>
<tr valign="top">
    <td>
        <%

              Date orderTime=order.getTime().toGregorianCalendar().getTime();

            URI showOrderDetailsUri = UriUtils
                    .putParam(formAction, TIME_OF_ORDER_PARAM, new Long(orderTime.getTime()).toString());
            showOrderDetailsUri = UriUtils.putParam(showOrderDetailsUri, PAGE_PARAM, SHOW_ORDER_DETAILS_PAGE);
        %>
        <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(showOrderDetailsUri.toString()))%>"
           class="command-link">
            <%=StringEscapeUtils.escapeHtml(timeFormat.format(order.getTime().toGregorianCalendar().getTime()))%>
        </a>
    </td>
    <td align="right">
        <%Long rSum = order.getSum();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rSum))%>
        </div>
    </td>
    <td align="right">
        <%String socDiscountInfo = "";
          if (order.getSocDiscount()!=0) socDiscountInfo=CurrencyStringUtils.copecksToRubles(order.getSocDiscount());
          if (socDiscountInfo.length()==0) socDiscountInfo="-";
      %>
        <div class="output-text"><%=socDiscountInfo%>
        </div>
    </td>
    <td align="right">
        <%String trdDiscountInfo = "";
            if (order.getTrdDiscount()!=0) trdDiscountInfo=CurrencyStringUtils.copecksToRubles(order.getTrdDiscount());
            if (trdDiscountInfo.length()==0) trdDiscountInfo="-";
        %>
        <div class="output-text"><%=trdDiscountInfo%>
        </div>
    </td>
    <td align="right">
        <%Long grantSum = order.getDonation();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(grantSum))%>
        </div>
    </td>
    <td align="right">
        <%Long sumByCash = order.getByCash();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCash))%>
        </div>
    </td>
    <td align="right">
        <%Long sumByCard = order.getByCard();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCard))%>
        </div>
    </td>
    <td>
        <%
            ru.axetta.ecafe.processor.web.bo.client.CardItem card=null;
            List<ru.axetta.ecafe.processor.web.bo.client.CardItem>cards=port.getCardList(clientAuthToken.getContractId()).getCardList().getC();
            for(ru.axetta.ecafe.processor.web.bo.client.CardItem cardItem:cards){if(cardItem.getIdOfCard().equals(order.getIdOfCard())){card = cardItem;}}


               //  Card card = order.getCard();
            if (null != card) {
        %>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCrystalId()))%>
        </div>
        <%}%>
    </td>
    <td>
        <table>
            <% /* Set<OrderDetail> orderDetails = order.getOrderDetails();
                for (OrderDetail currOrderDetail : orderDetails) {*/

                List<ru.axetta.ecafe.processor.web.bo.client.PurchaseElementExt> orderDetails = order.getE();
                for (ru.axetta.ecafe.processor.web.bo.client.PurchaseElementExt currOrderDetail : orderDetails) {
            %>
            <tr valign="top">
                <td>
                    <% String odStyle="";
                       if (currOrderDetail.getType()>= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_0 && currOrderDetail.getType()<= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_9) odStyle="od-complex";
                       else if (currOrderDetail.getType()>= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_ITEM_0 && currOrderDetail.getType()<= ru.axetta.ecafe.processor.core.persistence.OrderDetail.TYPE_COMPLEX_ITEM_9) odStyle="od-c-item";
                    %>
                    <div class="output-text <%=odStyle%>"><%=StringEscapeUtils.escapeHtml(currOrderDetail.getName())%>
                        <%--(<%=StringEscapeUtils.escapeHtml(currOrderDetail.getQty().toString())%>)--%>
                    </div>
                </td>
            </tr>
            <%}%>
        </table>
    </td>
</tr>




<%
            ++orderIndex;
        }
    }
    }}catch(Exception e){logger.error("error in payments and orders: ",e);
            throw e;} %>
 </table>

<table class="infotable">
<tr class="header-tr">
    <td colspan="9">
        <div class="output-text">СМС уведомления отправленные с <%=StringEscapeUtils.escapeHtml(dateFormat.format(startDate))%>
            по <%=StringEscapeUtils.escapeHtml(dateFormat.format(endDate))%>
        </div>
    </td>
</tr>
<tr class="subheader-tr">
    <td>
        <div class="output-text">Дата</div>
    </td>
    <td>
        <div class="output-text">Сумма покупки</div>
    </td>
    <td>
        <div class="output-text">Социальная скидка</div>
    </td>
    <td>
        <div class="output-text">Торговая скидка</div>
    </td>
    <td>
        <div class="output-text">Дотация</div>
    </td>
    <td>
        <div class="output-text">Наличными</div>
    </td>
    <td>
        <div class="output-text">По карте</div>
    </td>
    <td>
        <div class="output-text">Номер карты</div>
    </td>
    <td>
        <div class="output-text">Состав</div>
    </td>
</tr>
<%

    done=false;

    try{
    while(!done){
        Date minTime = null;
         if (clientSmsIndex != clientSmsCount) {
            Sms clientSms =  clientSmsList.get(clientSmsIndex);
            Date clientSmsTime = clientSms.getServiceSendTime().toGregorianCalendar().getTime();
            minTime = minTime == null ? clientSmsTime : (clientSmsTime.before(minTime) ? clientSmsTime : minTime);
        }
        if (minTime == null) {
            done = true;
            continue;
        }

    if (clientSmsIndex != clientSmsCount) {
       Sms clientSms =  clientSmsList.get(clientSmsIndex);
        if (clientSms.getServiceSendTime().toGregorianCalendar().getTime().equals(minTime)) {
            //if (logger.isDebugEnabled()) {
            //    logger.debug(clientSms.toString());
            //}
%>



<tr valign="top">
    <td>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(clientSms.getServiceSendTime().toGregorianCalendar().getTime()))%>
        </div>
    </td>
    <td align="right">
        <%
            /*AccountTransaction accountTransaction = clientSms.getTransaction();
            Long sum = clientSms.getPrice();*/
        %>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(clientSms.getPrice()))%>
        </div>
    </td>
    <td align="right">
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(0L))%>
        </div>
    </td>
    <td align="right">
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(0L))%>
        </div>
    </td>
    <td align="right">
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(0L))%>
        </div>
    </td>
    <td align="right">
        <%
           /* Long transactionSum = 0L;
            if (null != accountTransaction) {
                transactionSum = accountTransaction.getTransactionSum();
            }*/
        %>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(clientSms.getTransactionSum()))%>
        </div>
    </td>
    <td>
        <%
           // if (null != accountTransaction) {
               // Card card = accountTransaction.getCard();
                if (null != clientSms.getCardNo()) {
        %>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(clientSms.getCardNo()))%>
        </div>
        <%
                }
           // }
        %>
    </td>
    <td>
        <div class="output-text">
            <%=StringEscapeUtils.escapeHtml(String.format("SMS-уведомление. Тип: %s. Статус: %s.",
                    ru.axetta.ecafe.processor.core.persistence.ClientSms.CONTENTS_TYPE_DESCRIPTION[clientSms.getContentsType()],
                    ru.axetta.ecafe.processor.core.persistence.ClientSms.DELIVERY_STATUS_DESCRIPTION[clientSms.getDeliveryStatus()]))%>
        </div>
    </td>
</tr>

<%
                    ++clientSmsIndex;
                }
            }
        }  }catch(Exception e){logger.error("error in sms: ",e);throw e;}
        %>
    </table>
    <%

    } catch (Exception e) {
        logger.error("Failed to build page", e);

      %>
      <div class="error-output-text"> Не удалось отобразить данные </div>
       <%

    }
%>
<%--</table>--%>
<%
        }
    } catch (Exception e) {
            logger.error(e.getMessage(),e);
        throw new UnavailableException(e.getMessage());
    }
%>