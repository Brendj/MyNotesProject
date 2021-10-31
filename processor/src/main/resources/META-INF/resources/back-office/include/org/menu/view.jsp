<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ComplexInfo" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ComplexInfoDetail" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Menu" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.MenuDetail" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ui.org.menu.MenuViewPage" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.time.DateUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.criterion.Order" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>

<%--Просмотр меню--%>

<%-- Код для динамической загрузки Yahoo UI Calendar dependancies --%>

<!--Include YUI Loader: -->
<!--<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yuiloader/yuiloader-min.js"></script>-->

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
        Date afterTomorrowDate = DateUtils.addDays(tomorrowDate, 1);
        utcCalendar.set(Calendar.DAY_OF_WEEK, utcCalendar.getFirstDayOfWeek());
        Date thisWeekStartDate = utcCalendar.getTime();
        Date thisWeekEndDate = MenuViewPage.getMenuDays() == null ? DateUtils.addDays(DateUtils.addWeeks(thisWeekStartDate, 1), -1)
                : DateUtils.addDays(thisWeekStartDate, MenuViewPage.getMenuDays());
        Date nextWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, 1);
        Date nextWeekEndDate = DateUtils.addDays(DateUtils.addWeeks(nextWeekStartDate, 1), -1);
        Date prevWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, -1);
        Date prevWeekEndDate = DateUtils.addWeeks(thisWeekEndDate, -1);


        //String todayUri;
        //String tomorrowUri;
        //String afterTomorrowUri;
        //String thisWeeekUri;
        //String prevWeeekUri;
        //String nextWeekUri;
        //try {
        //    todayUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(currentTime)), END_DATE_PARAM,
        //            utcDateFormat.format(currentTime)), PROCESS_PARAM, Boolean.toString(true)).toString();
        //    tomorrowUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(tomorrowDate)), END_DATE_PARAM,
        //            utcDateFormat.format(tomorrowDate)), PROCESS_PARAM, Boolean.toString(true)).toString();
        //    afterTomorrowUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(afterTomorrowDate)),
        //            END_DATE_PARAM, utcDateFormat.format(afterTomorrowDate)), PROCESS_PARAM, Boolean.toString(true))
        //            .toString();
        //    thisWeeekUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(thisWeekStartDate)),
        //            END_DATE_PARAM, utcDateFormat.format(thisWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
        //            .toString();
        //    prevWeeekUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(prevWeekStartDate)),
        //            END_DATE_PARAM, utcDateFormat.format(prevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
        //            .toString();
        //    nextWeekUri = UriUtils.putParam(UriUtils.putParam(
        //            UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(nextWeekStartDate)),
        //            END_DATE_PARAM, utcDateFormat.format(nextWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
        //            .toString();
        //} catch (Exception e) {
        //    logger.error("Error during URI building", e);
        //    throw new ServletException(e);
        //}

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

<!--<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>-->
<script>
    // скрипт для схлопывания строк таблицы по дням
    $(document).ready(function(){
        if ($("form").attr("name") === "menu-view") {
            $("table.day th").click(function () {
                $(this).parents('table.day').children('tbody').toggle();
                var $img = $(this).children('div').children(".dayIco");
                var $tmp = $img.attr("src");
                $img.attr("src", $img.attr("src2"));
                $img.attr("src2", $tmp);
            });
            $("table.day th").click(); // для начального схлопывания
        }
    });
</script>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form" name="menu-view">
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
            <%--<td>--%>
                <%--<input type="button" class="command-button" value="..." id="toggleStartCalendarBtn"--%>
                       <%--onclick="startCalendarInit();" />--%>
            <%--</td>--%>
        </tr>
        <%--<tr>--%>
            <%--<td colspan="3">--%>
                <%--<div class="yui-skin-sam">--%>
                    <%--<div id="startCalendarContainer" />--%>
                <%--</div>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td>
                <div class="output-text">Конечная дата</div>
            </td>
            <td>
                <input type="text" class="input-text" name="<%=END_DATE_PARAM%>" id="endDate"
                       value="<%=StringEscapeUtils.escapeHtml(endDateParamValue)%>" />
            </td>
            <%--<td>--%>
                <%--<input type="button" class="command-button" value="..." id="toggleEndCalendarBtn"--%>
                       <%--onclick="endCalendarInit();" />--%>
            <%--</td>--%>
        </tr>
        <%--<tr>--%>
            <%--<td colspan="3">--%>
                <%--<div class="yui-skin-sam">--%>
                    <%--<div id="endCalendarContainer" />--%>
                <%--</div>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td>
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Показать" class="command-button" />
            </td>
        </tr>
    </table>
    <%--<table>--%>
    <%--<tr>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(todayUri))%>"--%>
    <%--class="command-link">Сегодня</a>--%>
    <%--</td>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(thisWeeekUri))%>" class="command-link">На--%>
    <%--этой--%>
    <%--неделе</a>--%>
    <%--</td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(tomorrowUri))%>"--%>
    <%--class="command-link">Завтра</a>--%>
    <%--</td>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(prevWeeekUri))%>" class="command-link">На--%>
    <%--прошлой неделе</a>--%>
    <%--</td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(afterTomorrowUri))%>" class="command-link">Послезавтра</a>--%>
    <%--</td>--%>
    <%--<td>--%>
    <%--<a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(nextWeekUri))%>" class="command-link">На--%>
    <%--следующей неделе</a>--%>
    <%--</td>--%>
    <%--</tr>--%>
    <%--</table>--%>
</form>

<%if (haveDataToProcess && dataToProcessVerified) {%>
<table class="orgMenuView">
<tr>
<td valign="top">
    <%--<div style="margin-top: 0px">--%>
    <table>
        <tr>
            <td colspan="4">
                <div class="output-text">Меню с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
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

                Criteria menuCriteria = persistenceSession.createCriteria(Menu.class);
                menuCriteria.add(Restrictions.eq("org.idOfOrg", MenuViewPage.getIdOfOrg()));
                menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
                menuCriteria.add(Restrictions.ge("menuDate", startDate));
                menuCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));
                menuCriteria.addOrder(Order.asc("menuDate"));

                List menus = menuCriteria.list();
                for (Object currObject : menus) {
                    Menu currMenu = (Menu) currObject;
                    Criteria menuDetailCriteria = persistenceSession.createCriteria(MenuDetail.class);
                    menuDetailCriteria.add(Restrictions.eq("menu", currMenu));
                    menuDetailCriteria.add(Restrictions.ne("menuDetailName", "<Новое блюдо>"));
                    HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
                    HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
                    List menuDetails = menuDetailCriteria.list();
                    if (!menuDetails.isEmpty())
                    //if(false)
                    {
        %>
        <tr>
            <td>   <!--Added row for the nested table-->
                <table class="day">     <!--Start of the nested table-->
                    <thead>
                    <tr>
                        <th>
                            <div class="column-header menu-date">
                                <img class="dayIco" src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/a2.png"))%>"
                                     src2="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/a1.png"))%>"/>
                                <%
                                    utcCalendar.setTime(currMenu.getMenuDate());
                                    int dayOfWeek = utcCalendar.get(Calendar.DAY_OF_WEEK);
                                %>
                                <%=StringEscapeUtils.escapeHtml(DAY_OF_WEEK_NAMES[dayOfWeek - 1])%>
                                <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(currMenu.getMenuDate()))%>
                            </div>
                            </a>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><div class="column-header">Группа</div></td>
                        <td><div class="column-header">Наименование</div></td>
                        <td><div class="column-header">Цена</div></td>
                        <td><div class="column-header">Выход</div></td>
                    </tr>
                    <%
                        //boolean firstGroup = true;
                        String currGroupName = null;
                        for (Object currMenuDetailObject : menuDetails) {
                            MenuDetail currMenuDetail = (MenuDetail) currMenuDetailObject;
                            String groupName = currMenuDetail.getGroupName();
                            boolean firstInCurrGroup = false;
                            //if (firstGroup || !StringUtils.equals(currGroupName, groupName)) {
                            if (!StringUtils.equals(currGroupName, groupName)) {
                                currGroupName = groupName;
                                firstInCurrGroup = true;
                            }
                            //if (firstInCurrGroup && !firstGroup) {
                            if (firstInCurrGroup) {

                    %>  <tr>
                        <td>
                            <div class="menu-group-name">
                                <%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(currGroupName))%>
                            </div>
                        </td>
                            <%
                        } else {
                %>
                    <tr>
                        <td>
                            <div class="menu-group-name">
                            </div>
                        </td>
                        <%
                            }
                        %>
                        <td>
                            <div class="output-text">
                                <%=StringEscapeUtils.escapeHtml(currMenuDetail.getMenuDetailName())%>
                            </div>
                        </td>
                        <td>
                            <% String elementPrice = "-";
                                if (currMenuDetail.getPrice() != null) {
                                    elementPrice = CurrencyStringUtils.copecksToRubles(currMenuDetail.getPrice());
                                }
                            %>
                            <div class="output-text">
                                <%=StringEscapeUtils.escapeHtml(elementPrice)%>
                            </div>
                        </td>
                        <td>
                            <div class="output-text">
                                <%=StringEscapeUtils.escapeHtml(currMenuDetail.getMenuDetailOutput())%>
                            </div>
                        </td>
                    </tr>
                    <%
                            //firstGroup = false;
                        }
                    %>
                    </tbody>
                </table>
            </td>
        </tr>
        <%      }
        }%>

    </table>

</td>
<td valign="top" class="right">
    <%--<div style="margin-top: 0px">--%>
    <table>
        <tr>
            <td colspan="4">
                <div class="output-text">Комплексы с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
                    по <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(endDate))%>
                </div>
            </td>
        </tr>
        <%

            Criteria complexInfoCriteria = persistenceSession.createCriteria(ComplexInfo.class);
            complexInfoCriteria.add(Restrictions.eq("org.idOfOrg", MenuViewPage.getIdOfOrg()));
            complexInfoCriteria.add(Restrictions.ge("menuDate", startDate));
            complexInfoCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 2)));
            complexInfoCriteria.addOrder(Order.asc("menuDate"));
            //  complexInfoCriteria.add(Restrictions.lt("menuDate", endDate));

            // HibernateUtils.addAscOrder(complexInfoCriteria,"menuDate");

            List complexes=complexInfoCriteria.list();

            ArrayList<ArrayList<ComplexInfo>> sortedComplexes=new ArrayList<ArrayList<ComplexInfo>>();

            Date currDate=null;
            ArrayList<ComplexInfo>currComplexListWithSameDate=new ArrayList<ComplexInfo>();

            for(Object complexObject:complexes){

                ComplexInfo currComplex=(ComplexInfo)complexObject;

                if(currDate==null){
                    currComplexListWithSameDate.add(currComplex);
                    currDate=currComplex.getMenuDate();
                    continue;
                }

                if(currComplex.getMenuDate().equals(currDate)){
                    currComplexListWithSameDate.add(currComplex);

                }else{

                    ArrayList<ComplexInfo>newComplexes=new ArrayList<ComplexInfo>();
                    newComplexes.addAll(currComplexListWithSameDate);

                    sortedComplexes.add(newComplexes);

                    currComplexListWithSameDate=new ArrayList<ComplexInfo>();
                    currComplexListWithSameDate.add(currComplex);
                    currDate=currComplex.getMenuDate();

                }


            }

            currDate=null;

            for(ArrayList<ComplexInfo> complexesWithSameDate:sortedComplexes){
                boolean emptyComplexes=true;
                ComplexInfo currComplex=complexesWithSameDate.get(0);

                currDate=currComplex.getMenuDate();


                // for(Object complexObject:complexes){
                ArrayList<ArrayList<ComplexInfoDetail>> complexDetailsWithSameDate =new ArrayList<ArrayList<ComplexInfoDetail>>();

                for(ComplexInfo complex:complexesWithSameDate){
                    Criteria complexDetailsCriteria=persistenceSession.createCriteria(ComplexInfoDetail.class);
                    complexDetailsCriteria.add(Restrictions.eq("complexInfo",complex));


                    List<ComplexInfoDetail> complexDetails=complexDetailsCriteria.list();
                    if(!complexDetails.isEmpty()) {emptyComplexes=false;
                        logger.info("complexName: "+complex.getComplexName());

                    }

                    ArrayList<ComplexInfoDetail>complexDetailList=new ArrayList<ComplexInfoDetail>();
                    complexDetailList.addAll(complexDetails);
                    complexDetailsWithSameDate.add(complexDetailList);


                }

                //boolean emptyDetailList;
                //for(ArrayList){}


                /*Criteria complexDetailsCriteria=persistenceSession.createCriteria(ComplexInfoDetail.class);
               complexDetailsCriteria.add(Restrictions.eq("complexInfo",currComplex));

               List<ComplexInfoDetail> complexDetails=complexDetailsCriteria.list();*/

                //if(true)
                if(!emptyComplexes)
                {


        %>
        <tr>
            <td>   <!--Added row for the nested table-->
                <table class="day">     <!--Start of the nested table-->
                    <thead>
                    <tr>
                        <th>
                            <div class="column-header menu-date">
                                <img class="dayIco" src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/a2.png"))%>"
                                     src2="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/a1.png"))%>"/>
                                <%

                                    // logger.info("currDate: "+currDate);

                                    utcCalendar.setTime(currDate);

                                    // logger.info("utcCalendar.setTime(): "+);
                                    int dayOfWeek = utcCalendar.get(Calendar.DAY_OF_WEEK);
                                %>
                                <%=StringEscapeUtils.escapeHtml(DAY_OF_WEEK_NAMES[dayOfWeek - 1])%>
                                <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(currDate))%>
                            </div>
                        </th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr>
                        <td><div class="column-header">Комплекс</div></td>
                        <td><div class="column-header">Цена комплекса</div></td>
                        <td><div class="column-header">Содержание</div></td>
                        <td><div class="column-header">Цена</div></td>
                        <td><div class="column-header">Льготный</div></td>
                    </tr>
                    <%
                        //boolean firstGroup = true;
                        for(ArrayList<ComplexInfoDetail>complexDetails:complexDetailsWithSameDate) {

                            String currComplexName = null;
                            if(!complexDetails.isEmpty()){
                                for (int index=0;index<complexDetails.size();index++) {

                                    ComplexInfoDetail currComplexDetail=complexDetails.get(index);
                                    logger.info("currComplexDetail: "+currComplexDetail.getComplexInfo().getComplexName());

                                    if (index==0) {

                    %>  <tr>
                        <td>
                            <div class="menu-group-name">
                                <%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(currComplexDetail.getComplexInfo().getComplexName()))%>
                            </div>
                        </td>
                        <td>
                            <% String complexPrice = "-";
                                if (currComplexDetail.getComplexInfo().getCurrentPrice() != null) {
                                    complexPrice = CurrencyStringUtils.copecksToRubles(currComplexDetail.getComplexInfo().getCurrentPrice());
                                }
                            %>
                            <div class="menu-group-name">
                                <%=StringEscapeUtils.escapeHtml(complexPrice)%>
                            </div>
                        </td>
                            <%
                       } else {
                %>
                    <tr>
                        <td>
                            <div class="menu-group-name">
                            </div>
                        </td>
                        <td>
                            <div class="menu-group-name">
                            </div>
                        </td>
                        <%
                            }
                        %>
                        <td>
                            <div class="output-text">
                                <%
                                    Integer count = currComplexDetail.getCount();
                                    if (count == null) {
                                        count = 1;
                                    }
                                    String menuDetailName = currComplexDetail.getMenuDetail().getMenuDetailName();
                                    menuDetailName += " (" + String.valueOf(count) + ")";
                                %>
                                <%=StringEscapeUtils.escapeHtml(menuDetailName)%>
                            </div>
                        </td>
                        <td>
                            <% String elementPrice = "-";
                                if (currComplexDetail.getMenuDetail().getPrice() != null) {
                                    elementPrice = CurrencyStringUtils.copecksToRubles(currComplexDetail.getMenuDetail().getPrice());
                                }
                            %>
                            <div class="output-text">
                                <%=StringEscapeUtils.escapeHtml(elementPrice)%>
                            </div>
                        </td>
                        <td>
                            <%
                                String modeFree = "";
                                if(currComplexDetail.getComplexInfo().getModeFree() == 1 )
                                    modeFree = "л";
                                String subscription = "";
                                if(currComplexDetail.getComplexInfo().getUsedSubscriptionFeeding() == 1 )
                                    subscription = "ап";
                            %>
                            <div class="output-text" style="text-align:center">
                                <%=StringEscapeUtils.escapeHtml(modeFree)%>
                                <%=StringEscapeUtils.escapeHtml(subscription)%>
                            </div>
                        </td>
                    </tr>
                    <%
                                    //firstGroup = false;
                                } } }
                    %>
                    </tbody>
                </table>

            </td>
        </tr>





        <%



                }



            }
        %>
    </table>
</td>
</tr>
</table>

<%
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

<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>