<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Circulation" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.criterion.Disjunction" %>
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

    function toggleOnBooks(source) {
        document.getElementsByName('checkStatus0')[0].checked = true;
        document.getElementsByName('checkStatus1')[0].checked = true;
        document.getElementsByName('checkStatus2')[0].checked = true;
        document.getElementsByName('checkStatus3')[0].checked = true;
    }

    function toggleOffBooks(source) {
        document.getElementsByName('checkStatus0')[0].checked = false;
        document.getElementsByName('checkStatus1')[0].checked = false;
        document.getElementsByName('checkStatus2')[0].checked = false;
        document.getElementsByName('checkStatus3')[0].checked = false;
    }

</script>

<%final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");
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
        <td>
            <div class="output-text"><h2>Книги</h2>
            </div>
        </td>
        <td colspan="9">
            <div class="output-text">
                <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>"
                      method="post" enctype="application/x-www-form-urlencoded" class="borderless-form">
                    <input id="checkStatus0" type="checkbox" name="checkStatus0"
                           value="0" <%=request.getSession().getAttribute("isNewSession") != null ? request.getParameter("checkStatus0") != null ? "checked" : "" : "checked"%>>
                    <label for="checkStatus0">На руках</label>
                    <input id="checkStatus1" type="checkbox" name="checkStatus1"
                           value="1" <%=request.getSession().getAttribute("isNewSession") != null ? request.getParameter("checkStatus1") != null ? "checked" : "" : "checked"%>>
                    <label for="checkStatus1">Продлено</label>
                    <input id="checkStatus2" type="checkbox" name="checkStatus2"
                           value="2" <%=request.getSession().getAttribute("isNewSession") != null ? request.getParameter("checkStatus2") != null ? "checked" : "" : "checked"%>>
                    <label for="checkStatus2">Утеряно</label>
                    <input id="checkStatus3" type="checkbox" name="checkStatus3"
                           value="3" <%=request.getSession().getAttribute("isNewSession") != null ? request.getParameter("checkStatus3") != null ? "checked" : "" : "checked"%>>
                    <label for="checkStatus3">Возвращено</label>
                    <input type="button" value="Выделить все" onClick="toggleOnBooks(this)" class="command-button">
                    <input type="button" value="Сбросить все" onClick="toggleOffBooks(this)" class="command-button">
                    <input type="submit" name="<%=PROCESS_PARAM%>" value="Показать" class="command-button" />
                </form>
            </div>
        </td>
    </tr>
    <%// TODO статус (0- на руках, 1 – продлено, 2 – утеряно, 3 – возвращено)
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            //Query query = persistenceSession.createQuery("from Circulation as c inner join Publication as p");
            Criteria circulationCriteria = persistenceSession.createCriteria(Circulation.class);
            circulationCriteria.add(Restrictions.eq("client", client));
            Disjunction objDisjunction = Restrictions.disjunction();
            List<Circulation> circulationList = Collections.emptyList();

            int howManyChecked = 0;
            for (int i = 0; i < 4; i++) {
                String status = request.getParameter("checkStatus" + i);
                if (status != null) {
                    howManyChecked++;
                    objDisjunction.add(Restrictions.eq("status", Integer.valueOf(status)));
                }
            }

            if (howManyChecked > 0 || request.getSession().getAttribute("isNewSession") == null) {
                circulationCriteria.add(objDisjunction);
                circulationList = circulationCriteria.list();
            }

            request.getSession().setAttribute("isNewSession", "false");

            if (circulationList.isEmpty()) {%>
    <tr>
        <td colspan="10">
            <div class="output-text">По данному запросу ничего не найдено!</div>
        </td>
    </tr>
    <%} else {%>
    <tr>
        <td>
            <div class="output-text">Идентификатор записи</div>
        </td>
        <td>
            <div class="output-text">Автор</div>
        </td>
        <td>
            <div class="output-text">Заглавие</div>
        </td>
        <td>
            <div class="output-text">Продолжение заглавия</div>
        </td>
        <td>
            <div class="output-text">Дата издания</div>
        </td>
        <td>
            <div class="output-text">Издатель</div>
        </td>
        <td>
            <div class="output-text">Дата выдачи</div>
        </td>
        <td>
            <div class="output-text">Дата возврата(срок)</div>
        </td>
        <td>
            <div class="output-text">Дата возврата</div>
        </td>
        <td>
            <div class="output-text">Статус</div>
        </td>
    </tr>
    <%for (Circulation circulation : circulationList) {%>
    <tr>
        <td>
            <%=circulation.getGlobalId() + " " +
                    circulation.getOrgOwner()%>
        </td>
        <td>
            <%=circulation.getIssuable().getInstance().getPublication().getAuthor()%>
        </td>
        <td>
            <%=circulation.getIssuable().getInstance().getPublication().getTitle()%>
        </td>
        <td>
            <%=circulation.getIssuable().getInstance().getPublication().getTitle2()%>
        </td>
        <td>
            <%=circulation.getIssuable().getInstance().getPublication().getPublicationdate()%>
        </td>
        <td>
            <%=circulation.getIssuable().getInstance().getPublication().getPublisher()%>
        </td>
        <td>
            <%=StringEscapeUtils.escapeHtml(timeFormat.format(circulation.getIssuanceDate()))%>
        </td>
        <td>
            <%=StringEscapeUtils.escapeHtml(timeFormat.format(circulation.getRefundDate()))%>
        </td>
        <td>
            <%=circulation.getRealRefundDate() != null ? StringEscapeUtils
                    .escapeHtml(timeFormat.format(circulation.getRealRefundDate())) : "NULL"%>
        </td>
        <td>
            <%=circulation.getStatus()%>
        </td>
    </tr>
    <%}
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
<%} catch (RuntimeContext.NotInitializedException e) {
    throw new UnavailableException(e.getMessage());
}%>