<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ru.axetta.ecafe.processor.core.persistence.EnterEvent" %>
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
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="javax.xml.datatype.XMLGregorianCalendar" %>

<%@ page import="javax.xml.datatype.DatatypeFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>

<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

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
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");

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



    final String DAY_OF_WEEK_NAMES[] = {
            "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    final String PROCESS_PARAM = "submit";
    final String START_DATE_PARAM = "start-date";
    final String END_DATE_PARAM = "end-date";
    final String HTML_CHECKED = "checked";
    final String PARAMS_TO_REMOVE[] = {PROCESS_PARAM, START_DATE_PARAM, END_DATE_PARAM};

    final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = new RuntimeContext();

        final Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(session);
        final Calendar utcCalendar = Calendar.getInstance(request.getLocale());
        final DateFormat utcDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        utcCalendar.setTimeZone(utcTimeZone);
        utcDateFormat.setTimeZone(utcTimeZone);
        utcCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        URI formAction;
        try {
            formAction = UriUtils
                    .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
        } catch (Exception e) {
            logger.error("Failed to build form action", e);
            throw new ServletException(e);
        }

        utcCalendar.set(localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH),
                localCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        utcCalendar.set(Calendar.MILLISECOND, 0);
        Date currentTime = utcCalendar.getTime();
        Date tomorrowDate = DateUtils.addDays(currentTime, 1);
        utcCalendar.set(Calendar.DAY_OF_WEEK, utcCalendar.getFirstDayOfWeek());
        Date thisWeekStartDate = utcCalendar.getTime();
        Date thisWeekEndDate = DateUtils.addDays(DateUtils.addWeeks(thisWeekStartDate, 1), -1);
        Date nextWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, 1);

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
                startDate = utcDateFormat.parse(startDateParamValue);
                endDate = utcDateFormat.parse(endDateParamValue);
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
            startDateParamValue = utcDateFormat.format(startDate);
            endDateParamValue = utcDateFormat.format(endDate);
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
</form>

<%if (haveDataToProcess && dataToProcessVerified) {%>

<table class="infotable">
    <tr class="header-tr">
        <td colspan="5">
            <div class="output-text">Посещения с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
                по <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(endDate))%>
            </div>
        </td>
    </tr>
    <tr class="subheader-tr">
        <td>
            <div class="output-text">Дата и время</div>
        </td>
        <td>
            <div class="output-text">День</div>
        </td>
        <td>
            <div class="output-text">Наименование входа</div>
        </td>
        <td>
            <div class="output-text">Направление</div>
        </td>
        <td>
            <div class="output-text">Временная карта</div>
        </td>
    </tr>
    <%

        try {


            Long contractId=clientAuthToken.getContractId();

            ClientRoomController port=clientAuthToken.getPort();

            GregorianCalendar greStartDate = new GregorianCalendar();
            greStartDate.setTime(startDate);
            XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(greStartDate);

            GregorianCalendar greEndDate = new GregorianCalendar();
            greStartDate.setTime(endDate);
            XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(greEndDate);


            ru.axetta.ecafe.processor.web.bo.client.EnterEventListResult enterEventListResult=port.getEnterEventList(contractId,xmlStartDate,xmlEndDate);

             if(!enterEventListResult.getResultCode().equals(RC_OK)){

                 throw new Exception(enterEventListResult.getDescription());
             }


            ru.axetta.ecafe.processor.web.bo.client.EnterEventList enterEventList=enterEventListResult.getEnterEventList();
            List<ru.axetta.ecafe.processor.web.bo.client.EnterEventItem> enterEvents=enterEventList.getE();

            Map<Integer, String> directionMap = new HashMap<Integer, String>();

            directionMap.put(EnterEvent.ENTRY, "вход");
            directionMap.put(EnterEvent.EXIT, "выход");
            directionMap.put(EnterEvent.PASSAGE_IS_FORBIDDEN, "проход запрещен");
            directionMap.put(EnterEvent.TURNSTILE_IS_BROKEN, "взлом турникета");
            directionMap.put(EnterEvent.EVENT_WITHOUT_PASSAGE, "событие без прохода");
            directionMap.put(EnterEvent.PASSAGE_RUFUSAL, "отказ от прохода");
            directionMap.put(EnterEvent.RE_ENTRY, "повторный вход");
            directionMap.put(EnterEvent.RE_EXIT, "повторный выход");

            String[] daysOfWeek = new String[] {"Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};

            Locale locale = new Locale("ru","RU");;
            Calendar calendar = Calendar.getInstance(locale);

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

           // List<EnterEvent> enterEvents = enterEventCriteria.list();

            for (ru.axetta.ecafe.processor.web.bo.client.EnterEventItem enterEvent : enterEvents) {
                XMLGregorianCalendar dateTime=enterEvent.getDateTime();
                calendar.setTime(new Date(dateTime.getYear(),dateTime.getMonth(),dateTime.getDay()));
    %>
                <tr>
                    <td>
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(enterEvent.getDateTime().toGregorianCalendar().getTime()))%></div>
                    </td>
                    <td>
                        <div class="output-text"><%=daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]%></div>
                    </td>
                    <td>
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(enterEvent.getEnterName())%></div>
                    </td>
                    <td>
                        <div class="output-text">
                            <span class=<%=enterEvent.getDirection() == EnterEvent.DIRECTION_ENTER ? "green" :
                                enterEvent.getDirection() == EnterEvent.DIRECTION_EXIT ? "red" : "" %>>
                                <%=directionMap.get(enterEvent.getDirection())%>
                            </span>
                        </div>
                    </td>
                    <td>
                        <div style="text-align:center">
                            <input type="checkbox" size="16" maxlength="64" class="input-text" style="background:black" disabled="disabled"
                                <%=enterEvent.getTemporaryCard().equals(new Integer(1)) ? HTML_CHECKED : ""%> />
                        </div>
                    </td>
                </tr>
    <%
            }

        } catch (Exception e) {
            logger.error("Failed to build page", e);
            %>
    <div class="error-output-text"> Не удалось отобразить данные </div>
    <%

        }
    %>
</table>
<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>