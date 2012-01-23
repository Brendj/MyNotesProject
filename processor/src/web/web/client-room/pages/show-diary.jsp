<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
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
    final String DAY_OF_WEEK_NAMES[] = {
            "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    final String PROCESS_PARAM = "submit";
    final String START_DATE_PARAM = "start-date";
    final String END_DATE_PARAM = "end-date";
    final String PARAMS_TO_REMOVE[] = {PROCESS_PARAM, START_DATE_PARAM, END_DATE_PARAM};

    final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        
        final Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(session);
        final Calendar utcCalendar = Calendar.getInstance(request.getLocale());
        final DateFormat utcDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        utcCalendar.setTimeZone(utcTimeZone);
        utcDateFormat.setTimeZone(utcTimeZone);

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
        utcCalendar.set(Calendar.DAY_OF_WEEK, utcCalendar.getFirstDayOfWeek());
        Date thisWeekStartDate = utcCalendar.getTime();
        Date yesterdayDate = DateUtils.addDays(currentTime, -1);
        Date beforeYesterdayDate = DateUtils.addDays(currentTime, -1);
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
            todayUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(currentTime)), END_DATE_PARAM,
                    utcDateFormat.format(currentTime)), PROCESS_PARAM, Boolean.toString(true)).toString();
            yesterdayUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(yesterdayDate)),
                    END_DATE_PARAM, utcDateFormat.format(yesterdayDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            beforeYesterdayUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(beforeYesterdayDate)),
                    END_DATE_PARAM, utcDateFormat.format(beforeYesterdayDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            thisWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(thisWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(thisWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            prevWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(prevWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(prevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            beforePrevWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(beforePrevWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(beforePrevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
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
    <table>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(todayUri)%>" class="command-link">Сегодня</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(thisWeeekUri)%>" class="command-link">На этой
                    неделе</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(yesterdayUri)%>" class="command-link">Вчера</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(prevWeeekUri)%>" class="command-link">На прошлой неделе</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(beforeYesterdayUri)%>" class="command-link">Позавчера</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(beforePrevWeeekUri)%>" class="command-link">На позапрошлой
                    неделе</a>
            </td>
        </tr>
    </table>
</form>

<%
    if (haveDataToProcess && dataToProcessVerified) {
%>
<table>
    <tr>
        <td colspan="3">
            <div class="output-text">Дневник с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
                по <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(endDate))%>
            </div>
        </td>
    </tr>
    <%
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();

            Criteria diaryTimesheetCriteria = null;
            ClientGroup clientGroup = client.getClientGroup();
            if (null != clientGroup) {
                diaryTimesheetCriteria = persistenceSession.createCriteria(DiaryTimesheet.class);
                diaryTimesheetCriteria.add(Restrictions.eq("clientGroup", clientGroup));
                diaryTimesheetCriteria.add(Restrictions.ge("recDate", startDate));
                Date nextToEndDate = DateUtils.addDays(endDate, 1);
                diaryTimesheetCriteria.add(Restrictions.lt("recDate", nextToEndDate));
                diaryTimesheetCriteria.addOrder(org.hibernate.criterion.Order.asc("recDate"));
            }

            if (null == diaryTimesheetCriteria) {
                //todo
    %>

    <%
    } else {
        List diaryTimesheets = diaryTimesheetCriteria.list();
        for (Object currObject : diaryTimesheets) {
            DiaryTimesheet currDiaryTimesheet = (DiaryTimesheet) currObject;
            utcCalendar.setTime(currDiaryTimesheet.getRecDate());
            int dayOfWeek = utcCalendar.get(Calendar.DAY_OF_WEEK);
    %>
    <tr>
        <td colspan="3">
            <div class="output-text">
                <%=StringEscapeUtils.escapeHtml(DAY_OF_WEEK_NAMES[dayOfWeek - 1])%>
                <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(currDiaryTimesheet.getRecDate()))%>
            </div>
        </td>
    </tr>
    <%
        final int MAX_NUMBER_OF_CLASSES = 10;
        DiaryClass diaryClasses[] = new DiaryClass[MAX_NUMBER_OF_CLASSES];
        diaryClasses[0] = currDiaryTimesheet.getDiaryClass0();
        diaryClasses[1] = currDiaryTimesheet.getDiaryClass1();
        diaryClasses[2] = currDiaryTimesheet.getDiaryClass2();
        diaryClasses[3] = currDiaryTimesheet.getDiaryClass3();
        diaryClasses[4] = currDiaryTimesheet.getDiaryClass4();
        diaryClasses[5] = currDiaryTimesheet.getDiaryClass5();
        diaryClasses[6] = currDiaryTimesheet.getDiaryClass6();
        diaryClasses[7] = currDiaryTimesheet.getDiaryClass7();
        diaryClasses[8] = currDiaryTimesheet.getDiaryClass8();
        diaryClasses[9] = currDiaryTimesheet.getDiaryClass9();
        for (int i = 0; i < MAX_NUMBER_OF_CLASSES; ++i) {
            DiaryClass currDiaryClass = diaryClasses[i];
            boolean haveMoreClasses = false;
            for (int j = i + 1; j < MAX_NUMBER_OF_CLASSES; ++j) {
                if (null != diaryClasses[j]) {
                    haveMoreClasses = true;
                    break;
                }
            }
            if (null == currDiaryClass && !haveMoreClasses) {
                if (0 == i) {
    %>
    <tr>
        <td colspan="3">
            <div class="output-text">Нет занятий</div>
        </td>
    </tr>
    <%
        }
        break;
    } else {
    %>
    <tr>
        <td>
            <div class="output-text"><%=i + 1%>
            </div>
        </td>
        <%if (null == currDiaryClass) {%>
        <td />
        <td />
        <%
        } else {
            Criteria diaryValueCiteria = persistenceSession.createCriteria(DiaryValue.class);
            diaryValueCiteria.add(Restrictions.eq("org", client.getOrg()));
            diaryValueCiteria.add(Restrictions.eq("client", client));
            diaryValueCiteria.add(Restrictions.eq("diaryClass", currDiaryClass));
            Date diaryValueStartDate = currDiaryTimesheet.getRecDate();
            diaryValueCiteria.add(Restrictions.ge("recDate", diaryValueStartDate));
            diaryValueCiteria.add(Restrictions.lt("recDate", DateUtils.addDays(diaryValueStartDate, 1)));
            diaryValueCiteria.addOrder(org.hibernate.criterion.Order.asc("VType"));
            List diaryValues = diaryValueCiteria.list();

        %>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(currDiaryClass.getClassName())%>
            </div>
        </td>
        <td>
            <table>
                <tr>
                    <%
                        for (Object currDiaryValueObject : diaryValues) {
                            DiaryValue currDiaryValue = (DiaryValue) currDiaryValueObject;
                            int vType = currDiaryValue.getVType();
                            switch (vType) {
                                case DiaryValue.DAY_VALUE1_TYPE:
                                case DiaryValue.DAY_VALUE2_TYPE:
                    %>
                    <td>
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(currDiaryValue.getValue())%>
                        </div>
                    </td>
                    <%
                            break;
                        case DiaryValue.DAY_EXAM_VALUE_TYPE:
                    %>
                    <td bgcolor="lightpink">
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(currDiaryValue.getValue())%>
                        </div>
                    </td>
                    <%
                                    break;
                            }
                        }
                    %>
                </tr>
            </table>
        </td>
        <%}%>
    </tr>
    <%
                        }
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
        }
    %>
</table>
<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    } finally {
        RuntimeContext.release(runtimeContext);
    }
%>