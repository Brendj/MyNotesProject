<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
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
<%@ page import="javax.xml.datatype.DatatypeConfigurationException" %>
<%@ page import="javax.xml.datatype.DatatypeConstants" %>

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
<%!
    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        if (date==null) return null;
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc = DatatypeFactory.newInstance()
                .newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }

%>
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
        runtimeContext =new  RuntimeContext();

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
        Date thisWeekEndDate = DateUtils.addDays(DateUtils.addWeeks(thisWeekStartDate, 1), -1);
        Date nextWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, 1);
        Date nextWeekEndDate = DateUtils.addDays(DateUtils.addWeeks(nextWeekStartDate, 1), -1);
        Date prevWeekStartDate = DateUtils.addWeeks(thisWeekStartDate, -1);
        Date prevWeekEndDate = DateUtils.addWeeks(thisWeekEndDate, -1);


        String todayUri;
        String tomorrowUri;
        String afterTomorrowUri;
        String thisWeeekUri;
        String prevWeeekUri;
        String nextWeekUri;
        try {
            todayUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(currentTime)), END_DATE_PARAM,
                    utcDateFormat.format(currentTime)), PROCESS_PARAM, Boolean.toString(true)).toString();
            tomorrowUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(tomorrowDate)), END_DATE_PARAM,
                    utcDateFormat.format(tomorrowDate)), PROCESS_PARAM, Boolean.toString(true)).toString();
            afterTomorrowUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(afterTomorrowDate)),
                    END_DATE_PARAM, utcDateFormat.format(afterTomorrowDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            thisWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(thisWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(thisWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            prevWeeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(prevWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(prevWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
                    .toString();
            nextWeekUri = UriUtils.putParam(UriUtils.putParam(
                    UriUtils.putParam(formAction, START_DATE_PARAM, utcDateFormat.format(nextWeekStartDate)),
                    END_DATE_PARAM, utcDateFormat.format(nextWeekEndDate)), PROCESS_PARAM, Boolean.toString(true))
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

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
<script>
    // скрипт для схлопывания строк таблицы по дням
    $(document).ready(function(){
        $("table.day th").click(function () {
            $(this).parents('table.day').children('tbody').toggle();
            var $img = $(this).children('div').children(".dayIco");
            var $tmp = $img.attr("src");
            $img.attr("src", $img.attr("src2"));
            $img.attr("src2", $tmp);
        });
        $("table.day th").click(); // для начального схлопывания
    });
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
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(tomorrowUri))%>"
                   class="command-link">Завтра</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(prevWeeekUri))%>" class="command-link">На
                    прошлой неделе</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(afterTomorrowUri))%>" class="command-link">Послезавтра</a>
            </td>
            <td>
                <a href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(nextWeekUri))%>" class="command-link">На
                    следующей неделе</a>
            </td>
        </tr>
    </table>
</form>

<%if (haveDataToProcess && dataToProcessVerified) {%>
<table>
 <tr>
 <td valign="top">
<table>
    <tr>
        <td colspan="4">
            <div class="output-text">Меню с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
                по <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(endDate))%>
            </div>
        </td>
    </tr>

    <%

        try {


            ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();





            XMLGregorianCalendar xmlStartDate = toXmlDateTime(startDate);



            XMLGregorianCalendar xmlEndDate = toXmlDateTime(DateUtils.addDays(endDate,1));





            ru.axetta.ecafe.processor.web.bo.client.MenuListResult menuListResult=port.getMenuList(clientAuthToken.getContractId(), xmlStartDate, xmlEndDate);

            if(!RC_OK.equals(menuListResult.getResultCode())){

                throw new Exception(menuListResult.getDescription());
            }

            ru.axetta.ecafe.processor.web.bo.client.MenuListExt menuListExt=menuListResult.getMenuList();
            List<ru.axetta.ecafe.processor.web.bo.client.MenuDateItemExt> menus=menuListExt.getM();




            for (ru.axetta.ecafe.processor.web.bo.client.MenuDateItemExt currMenu : menus) {

                List<ru.axetta.ecafe.processor.web.bo.client.MenuItemExt>menuDetails=currMenu.getE();
                if (!menuDetails.isEmpty()) {
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
                                    XMLGregorianCalendar xmlDate=currMenu.getDate();
                                     GregorianCalendar greDate= xmlDate.toGregorianCalendar();
                                     Date date=greDate.getTime();
                                   // logger.info("date: "+date);
                                    //logger.info("utcDateFormat.format(date): "+utcDateFormat.format(date));
                                    date=DateUtils.addDays(date,1);
                                    //logger.info("utcDateFormat.format(date+1): "+utcDateFormat.format(date));
                                    utcCalendar.setTime(date);
                                    int dayOfWeek = utcCalendar.get(Calendar.DAY_OF_WEEK);
                                %>
                                <%=StringEscapeUtils.escapeHtml(DAY_OF_WEEK_NAMES[dayOfWeek - 1])%>
                                <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(date))%>
                            </div>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><div class="column-header">Группа</div></td>
                        <td><div class="column-header">Наименование</div></td>
                        <td><div class="column-header">Цена</div></td>
                    </tr>
                <%
                        //boolean firstGroup = true;
                        String currGroupName = null;
                        for (ru.axetta.ecafe.processor.web.bo.client.MenuItemExt currMenuDetail : menuDetails) {
                            //MenuDetail currMenuDetail = (MenuDetail) currMenuDetailObject;
                            String groupName = currMenuDetail.getGroup();
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
                                <%=StringEscapeUtils.escapeHtml(currMenuDetail.getName())%>
                            </div>
                        </td>
                        <td>
                            <div class="output-text">
                                <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(currMenuDetail.getPrice()))%>
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
            }

        %>

  </table>
     </td>
 <td valign="top">
   <table>
    <tr>
        <td colspan="4">
            <div class="output-text">Комплексы с <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(startDate))%>
                по <%=StringEscapeUtils.escapeHtml(utcDateFormat.format(endDate))%>
            </div>
        </td>
    </tr>
    <%
           endDate=DateUtils.addDays(endDate,2);
          xmlEndDate=toXmlDateTime(endDate);
          xmlStartDate=toXmlDateTime(startDate);


          ComplexListResult complexListResult=port.getComplexList(clientAuthToken.getContractId(),xmlStartDate,xmlEndDate);
          ComplexDateList complexDateList=complexListResult.getComplexDateList();

          List<ComplexDate> complexDates=complexDateList.getE();






        for(ComplexDate complexDate:complexDates){

          Date currDate=  complexDate.getDate().toGregorianCalendar().getTime();

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
                                   currDate=DateUtils.addDays(currDate,1);
                                  // logger.info("currDate="+currDate);


                                   utcCalendar.setTime(currDate);
                                  // logger.info("tcDateFormat.format(currDate)"+utcDateFormat.format(currDate));
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
                       <td><div class="column-header">Содержание</div></td>
                       <%--<td><div class="column-header">Цена</div></td>--%>
                   </tr>


           <%

           List<Complex> complexes = complexDate.getE();

           for(Complex complex:complexes){

              List<ComplexDetail> complexDetails= complex.getE();

              for(int i=0;i<complexDetails.size();i++){
                ComplexDetail complexDetail=complexDetails.get(i);
                if(i==0){
                %>
                   <tr>
                       <td>
                           <div class="menu-group-name">
                               <%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(complex.getName()))%>
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
                               <%=StringEscapeUtils.escapeHtml(complexDetail.getName())%>
                           </div>
                       </td>
                       <%--<td>
                           <div class="output-text">
                               <%=StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(currComplexDetail.getMenuDetail().getPrice()))%>
                           </div>
                       </td>--%>
                   </tr>




                       <%


              }
           } %>
                   </tbody>
                   </table>
               </td>
           </tr>
                       <%

            }  %>
           </table>
 </td>
   </tr>
</table>
           <%

        } catch (Exception e) {
            logger.error("Failed to build page", e);
        %>
        <div class="error-output-text"> Не удалось отобразить меню </div>
         <%


        }
    %>
<%--</table>--%>
<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>