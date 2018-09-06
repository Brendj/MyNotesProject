<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.card.CardNoFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ui.PaymentTextUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Projections" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.hibernate.sql.JoinType" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>

<%-- Код для динамической загрузки Yahoo UI Calendar dependancies --%>

<!--Include YUI Loader: -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yuiloader/yuiloader-min.js"></script>

<!--Use YUI Loader to bring in your other dependencies: -->
<script type="text/javascript">
    // Instantiate and configure YUI Loader:
    (function () {
        var loader = new YAHOO.util.YUILoader({
            base: "",
            require: [
                "calendar"],
            loadOptional: false,
            combine: true,
            filter: "MIN",
            allowRollup: true,
            onSuccess: function () {
                //you can make use of all requested YUI modules
                //here.
            }
        });
        // Load the files using the insert() method.
        loader.insert();
    })();
</script>

<%  final Logger logger = LoggerFactory
        .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-orders-and-payments_jsp");
    final String PROCESS_PARAM = "submit";
    final String PROCESS_PARAM_VIEW = "submit_view";
    final String START_DATE_PARAM = "start-date";
    final String END_DATE_PARAM = "end-date";
    final String ID_OF_ORG_PARAM = "org-id";
    final String ID_OF_ORDER_PARAM = "order-id";
    final String PAGE_PARAM = "page";
    final String SHOW_ORDER_DETAILS_PAGE = "show-order-details";
    final String PARAMS_TO_REMOVE[] = {
            PROCESS_PARAM, START_DATE_PARAM, END_DATE_PARAM, ID_OF_ORG_PARAM, ID_OF_ORDER_PARAM, PROCESS_PARAM_VIEW};

    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(session);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
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
        }%>

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

    startCalendarInit = function () {
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

    startCalendarHide = function () {
        var btn = YAHOO.util.Dom.get("toggleStartCalendarBtn");
        btn.onclick = startCalendarInit;
        if (null != startCalendar) {
            startCalendar.hide();
        }
    }

    endCalendarInit = function () {
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

    endCalendarHide = function () {
        var btn = YAHOO.util.Dom.get("toggleEndCalendarBtn");
        btn.onclick = endCalendarInit;
        if (null != endCalendar) {
            endCalendar.hide();
        }
    }

    ripStartDate = function (type, args) {
        var dates = args[0];
        var date = dates[0];
        var theYear = date[0];
        var theMonth = date[1];
        var theDay = date[2];
        var field = YAHOO.util.Dom.get("startDate");
        field.value = theDay + "." + theMonth + "." + theYear;
        startCalendarHide();
    }

    ripEndDate = function (type, args) {
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

<table>
<tr>
<td valign="top">
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
<%Session persistenceSession = null;
    org.hibernate.Transaction persistenceTransaction = null;
    try {
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
        Client client = (Client) clientCriteria.uniqueResult();

        Date nextToEndDate = DateUtils.addDays(endDate, 1);

        Criteria ordersCriteria = persistenceSession.createCriteria(Order.class);
        ordersCriteria.add(Restrictions.eq("client", client));
        ordersCriteria.add(Restrictions.ge("createTime", startDate));
        ordersCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        ordersCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        List ordersList = ordersCriteria.list();

        Criteria clientPaymentsCriteria = persistenceSession.createCriteria(ClientPayment.class);
        clientPaymentsCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
        clientPaymentsCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        clientPaymentsCriteria.add(Restrictions.ge("createTime", startDate));
        clientPaymentsCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        clientPaymentsCriteria = clientPaymentsCriteria.createCriteria("transaction");
        clientPaymentsCriteria.add(Restrictions.eq("client", client));
        List clientPaymentsList = clientPaymentsCriteria.list();

        Criteria clientSmsCriteria = persistenceSession.createCriteria(ClientSms.class);
        clientSmsCriteria.add(Restrictions.ge("serviceSendTime", startDate));
        clientSmsCriteria.add(Restrictions.lt("serviceSendTime", nextToEndDate));
        clientSmsCriteria.add(Restrictions.eq("client", client));
        List clientSmsList = clientSmsCriteria.list();

        int orderIndex = 0;
        int ordersCount = ordersList.size();
        int clientPaymentIndex = 0;
        int clientPaymentsCount = clientPaymentsList.size();
        int clientSmsIndex = 0;
        int clientSmsCount = clientSmsList.size();

        boolean done = false;
        while (!done) {
            Date minTime = null;
            if (orderIndex != ordersCount) {
                Order order = (Order) ordersList.get(orderIndex);

                minTime = order.getCreateTime();
            }
            if (clientPaymentIndex != clientPaymentsCount) {
                ClientPayment clientPayment = (ClientPayment) clientPaymentsList.get(clientPaymentIndex);
                Date clientPaymentTime = clientPayment.getCreateTime();
                minTime = minTime == null ? clientPaymentTime
                        : (clientPaymentTime.before(minTime) ? clientPaymentTime : minTime);
            }
            if (clientSmsIndex != clientSmsCount) {
                ClientSms clientSms = (ClientSms) clientSmsList.get(clientSmsIndex);
                Date clientSmsTime = clientSms.getServiceSendTime();
                minTime = minTime == null ? clientSmsTime : (clientSmsTime.before(minTime) ? clientSmsTime : minTime);
            }
            if (minTime == null) {
                done = true;
                continue;
            }
            if (clientPaymentIndex != clientPaymentsCount) {
                ClientPayment clientPayment = (ClientPayment) clientPaymentsList.get(clientPaymentIndex);
                if (clientPayment.getCreateTime().equals(minTime)) {
                    //if (logger.isDebugEnabled()) {
                    //    logger.debug(clientPayment.toString());
                    //}%>
<tr valign="top">
    <td>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(clientPayment.getCreateTime()))%>
        </div>
    </td>
    <td colspan="5">
        <%

            Long clientPaymentSum = clientPayment.getPaySum();
            String transferInfo = PaymentTextUtils.buildTransferInfo(persistenceSession, clientPayment);%>
        <div class="output-text topup-info">Пополнение баланса
            +<%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(clientPaymentSum))%>
            (<%=StringEscapeUtils.escapeHtml(transferInfo)%>)
            идентификатор платежа <%=clientPayment.getIdOfPayment()%>
        </div>
    </td>
    <td>
        <%Card card = clientPayment.getTransaction().getCard();
            if (null != card) {%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCardNo()))%>
        </div>
        <%}%>
    </td>
    <td />
</tr>
<%++clientPaymentIndex;
}
}
    if (orderIndex != ordersCount) {
        Order order = (Order) ordersList.get(orderIndex);
        if (order.getCreateTime().equals(minTime)) {
            //if (logger.isDebugEnabled()) {
            //    logger.debug(order.toString());
            //}%>
<tr valign="top">
    <td>
        <%CompositeIdOfOrder compositeIdOfOrder = order.getCompositeIdOfOrder();
            URI showOrderDetailsUri = UriUtils
                    .putParam(formAction, ID_OF_ORG_PARAM, compositeIdOfOrder.getIdOfOrg().toString());
            showOrderDetailsUri = UriUtils
                    .putParam(showOrderDetailsUri, ID_OF_ORDER_PARAM, compositeIdOfOrder.getIdOfOrder().toString());
            showOrderDetailsUri = UriUtils.putParam(showOrderDetailsUri, PAGE_PARAM, SHOW_ORDER_DETAILS_PAGE);%>
        <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(showOrderDetailsUri.toString()))%>"
           class="command-link">
            <%=StringEscapeUtils.escapeHtml(timeFormat.format(order.getCreateTime()))%>
        </a>
    </td>
    <td align="right">
        <%Long rSum = order.getRSum();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(rSum))%>
        </div>
    </td>
    <td align="right">
        <%
            String socDiscountInfo = "";
            if (order.getSocDiscount() != 0) {
                socDiscountInfo = CurrencyStringUtils.copecksToRubles(order.getSocDiscount());
            }
            if (socDiscountInfo.length() == 0) {
                socDiscountInfo = "-";
            }
        %>
        <div class="output-text"><%=socDiscountInfo%>
        </div>
    </td>
    <td align="right">
        <%
            String trdDiscountInfo = "";
            if (order.getTrdDiscount() != 0) {
                trdDiscountInfo = CurrencyStringUtils.copecksToRubles(order.getTrdDiscount());
            }
            if (trdDiscountInfo.length() == 0) {
                trdDiscountInfo = "-";
            }
        %>
        <div class="output-text"><%=trdDiscountInfo%>
        </div>
    </td>
    <td align="right">
        <%Long grantSum = order.getGrantSum();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(grantSum))%>
        </div>
    </td>
    <td align="right">
        <%Long sumByCash = order.getSumByCash();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCash))%>
        </div>
    </td>
    <td align="right">
        <%Long sumByCard = order.getSumByCard();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sumByCard))%>
        </div>
    </td>
    <td>
        <%Card card = order.getCard();
            if (null != card) {%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCardNo()))%>
        </div>
        <%}%>
    </td>
    <td>
        <table>
            <%Set<OrderDetail> orderDetails = order.getOrderDetails();
                for (OrderDetail currOrderDetail : orderDetails) {%>
            <tr valign="top">
                <td>
                    <% String odStyle = "";
                        if (currOrderDetail.getMenuType() >= OrderDetail.TYPE_COMPLEX_MIN
                                && currOrderDetail.getMenuType() <= OrderDetail.TYPE_COMPLEX_MAX) {
                            odStyle = "od-complex";
                        } else if (currOrderDetail.getMenuType() >= OrderDetail.TYPE_COMPLEX_ITEM_MIN
                                && currOrderDetail.getMenuType() <= OrderDetail.TYPE_COMPLEX_ITEM_MAX) {
                            odStyle = "od-c-item";
                        }%>
                    <div class="output-text <%=odStyle%>"><%=StringEscapeUtils
                            .escapeHtml(currOrderDetail.getMenuDetailName())%>
                        (<%=StringEscapeUtils.escapeHtml(currOrderDetail.getQty().toString())%>)
                    </div>
                </td>
            </tr>
            <%}%>
        </table>
    </td>
</tr>
<%++orderIndex;
}
}
    if (clientSmsIndex != clientSmsCount) {
        ClientSms clientSms = (ClientSms) clientSmsList.get(clientSmsIndex);
        if (clientSms.getServiceSendTime().equals(minTime)) {
            //if (logger.isDebugEnabled()) {
            //    logger.debug(clientSms.toString());
            //}%>
<tr valign="top">
    <td>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(clientSms.getServiceSendTime()))%>
        </div>
    </td>
    <td align="right">
        <%AccountTransaction accountTransaction = clientSms.getTransaction();
            Long sum = clientSms.getPrice();%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(sum))%>
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
        <%Long transactionSum = 0L;
            if (null != accountTransaction) {
                transactionSum = accountTransaction.getTransactionSum();
            }%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(transactionSum))%>
        </div>
    </td>
    <td>
        <%if (null != accountTransaction) {
            Card card = accountTransaction.getCard();
            if (null != card) {%>
        <div class="output-text"><%=StringEscapeUtils.escapeHtml(CardNoFormat.format(card.getCardNo()))%>
        </div>
        <%}
        }%>
    </td>
    <td>
        <div class="output-text">
            <%=StringEscapeUtils.escapeHtml(String.format("SMS-уведомление. Тип: %s. Статус: %s.",
                    ClientSms.CONTENTS_TYPE_DESCRIPTION[clientSms.getContentsType()],
                    ClientSms.DELIVERY_STATUS_DESCRIPTION[clientSms.getDeliveryStatus()]))%>
        </div>
    </td>
</tr>
<%++clientSmsIndex;
}
}
}
    persistenceTransaction.commit();
    persistenceTransaction = null;
} catch (Exception e) {
    logger.error("Failed to build page", e);
    throw new ServletException(e);
} finally {
    HibernateUtils.rollback(persistenceTransaction, logger);
    HibernateUtils.close(persistenceSession, logger);
}%>
</table>
</td>
<td valign="top" align="center">
    <%
        boolean haveDataToProcessView = StringUtils.isNotEmpty(request.getParameter(PROCESS_PARAM_VIEW));
        try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Client teacher = DAOUtils.findTeacherPaymentForStudents(persistenceSession, clientAuthToken.getContractId());

                if (teacher !=null) {
                    List students = new ArrayList(0);
                    if(haveDataToProcessView){
                        students = DAOUtils.fetchStudentsByCanNotConfirmPayment(persistenceSession, teacher.getIdOfClient());
                    }

    %>
    <p class="output-text">Список заказов по учащимся с превышением лимита овердрафта</p>
    <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
          enctype="application/x-www-form-urlencoded" class="borderless-form">
        <input type="submit" name="<%=PROCESS_PARAM_VIEW%>" value="Показать" class="command-button" />
    </form>
   <br/>
    <table border="1" cellpadding="0" cellspacing="0">
        <tr>
            <td>
                                   <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                       ФИО
                                   </span>
            </td>
            <td>
                                   <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                       Текущий баланс
                                   </span>
            </td>
            <td>
                                    <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                    Дата заказа
                                   </span>
            </td>
            <td>
                                    <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                    Сумма заказа
                                   </span>
            </td>
        </tr>
        <%if (students.isEmpty() && haveDataToProcessView) {
        %>
        <tr>
            <td colspan="4" align="center" valign="center">
                <span style="margin: 5px;text-align: center;font-size: 8pt; color: #90ee90">
                  Должников нет
                </span>
            </td>
        </tr>
        <%
        } else {
            Long idOfClient = null;
            Long sum = 0L;
            for (Object object : students) {
                Object[] student = (Object[]) object;
                String fio = String.valueOf(student[1]) + " "+String.valueOf(student[0]) + " "+String.valueOf(student[2]) ;
                Long balance = Long.valueOf(String.valueOf(student[3]));
                String stringDate = timeFormat.format((Date)student[5]);
                Long paySum = Long.valueOf(String.valueOf(student[4]));
                Long currentIdOfClient = Long.valueOf(String.valueOf(student[6]));
                if (idOfClient == null || (idOfClient != null && !idOfClient.equals(currentIdOfClient))) {
                    idOfClient = currentIdOfClient;
                    sum = 0L;
                }
                if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                    sum += paySum;%>
        <tr>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                            <%= fio%>
                                                        </span>
            </td>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                <%=
                                                StringEscapeUtils
                                                        .escapeHtml(CurrencyStringUtils.copecksToRubles(balance))
                                                %>
                                               </span>

            </td>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                <%=StringEscapeUtils.escapeHtml(stringDate)%>
                                               </span>

            </td>
            <td>
                                                       <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                           <%=
                                                           StringEscapeUtils.escapeHtml(
                                                                   CurrencyStringUtils.copecksToRubles(paySum))
                                                           %>
                                                       </span>

            </td>
        </tr>
        <%}
        }
        }

        %>
    </table>
    <%} else {
                       /* TODO: students logic  */
                       %>
    <p class="output-text">Список заказов по учащимся с превышением лимита овердрафта</p>
    <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
          enctype="application/x-www-form-urlencoded" class="borderless-form">
        <input type="submit" name="<%=PROCESS_PARAM_VIEW%>" value="Показать" class="command-button" />
    </form>
    <br/>
    <table border="1" cellpadding="0" cellspacing="0">
        <tr>
            <td>
                                   <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                       ФИО
                                   </span>
            </td>
            <td>
                                   <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                       Текущий баланс
                                   </span>
            </td>
            <td>
                                    <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                    Дата заказа
                                   </span>
            </td>
            <td>
                                    <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                    Сумма заказа
                                   </span>
            </td>
        </tr>
        <%
        if(haveDataToProcessView){
            Criteria criteria = persistenceSession.createCriteria(Order.class);
            criteria.add(Restrictions.isNotNull("confirmerId"));
            criteria.createCriteria("client","student", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.sqlRestriction("{alias}.balance - {alias}.limits < 0"));
           criteria.add(Restrictions.eq("student.contractId",clientAuthToken.getContractId()));
           // teacher
           // criteria.createAlias("student.person","person", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("student.person","person", JoinType.LEFT_OUTER_JOIN);
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property("person.firstName"), "firstName")
                    .add(Projections.property("person.surname"), "surname")
                    .add(Projections.property("person.secondName"), "secondName")
                    .add(Projections.property("student.balance"), "balance")
                    .add(Projections.property("RSum"), "rSum")
                    .add(Projections.property("createTime"), "createTime")
                    .add(Projections.property("student.idOfClient"), "idOfClient")
            );
            List students = criteria.list();
        if (students.isEmpty() && haveDataToProcessView) {
        %>
        <tr>
            <td colspan="4" align="center" valign="center">
                <span style="margin: 5px;text-align: center;font-size: 8pt; color: #90ee90">
                  Долгов нет
                </span>
            </td>
        </tr>
        <%
        } else {
            Long idOfClient = null;
            Long sum = 0L;
            for (Object object : students) {
                Object[] stud = (Object[]) object;
                String fio = String.valueOf(stud[1]) + " "+String.valueOf(stud[0]) + " "+String.valueOf(stud[2]) ;
                Long balance = Long.valueOf(String.valueOf(stud[3]));
                String stringDate = timeFormat.format((Date)stud[5]);
                Long paySum = Long.valueOf(String.valueOf(stud[4]));
                Long currentIdOfClient = Long.valueOf(String.valueOf(stud[6]));
                if (idOfClient == null || (idOfClient != null && !idOfClient.equals(currentIdOfClient))) {
                    idOfClient = currentIdOfClient;
                    sum = 0L;
                }
                if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                    sum += paySum;%>
        <tr>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                            <%= fio%>
                                                        </span>
            </td>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                <%=
                                                StringEscapeUtils
                                                        .escapeHtml(CurrencyStringUtils.copecksToRubles(balance))
                                                %>
                                               </span>

            </td>
            <td>
                                                        <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                <%=StringEscapeUtils.escapeHtml(stringDate)%>
                                               </span>

            </td>
            <td>
                                                       <span style="margin: 5px;text-align: center;font-size: 8pt;">
                                                           <%=
                                                           StringEscapeUtils.escapeHtml(
                                                                   CurrencyStringUtils.copecksToRubles(paySum))
                                                           %>
                                                       </span>

            </td>
        </tr>
        <%}
        }
        }

        %>
    </table>
          <%
        }
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
    %>

</td>
</tr>
</table>

<%}
} catch (RuntimeContext.NotInitializedException e) {
    throw new UnavailableException(e.getMessage());
}%>