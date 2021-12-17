<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.hibernate.Query" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>ИС ПП - панель мониторинга</title>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.project_state");

    String externalURL = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_EXTERNAL_URL);
    List<String> regions = new ArrayList<String>();
    regions.add("Все округа");

    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Query q = persistenceSession.createSQLQuery("select distinct cf_orgs.district from cf_orgs where cf_orgs.district<>'' order by cf_orgs.district");
        List res = q.list();
        for (Object reg : res) {
            regions.add((String) reg);
        }

        persistenceTransaction.commit();
        persistenceTransaction = null;
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    } catch (Exception e) {
        throw new ServletException(e);
    } finally {
        HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);
    }


    String regionsStr = "";
    for (String region : regions) {
        regionsStr += "<option value=\"" + region + "\">" + region + "</option>";
    }
%>

<script src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script src="http://code.jquery.com/ui/1.9.1/jquery-ui.js"></script>
<script>
    $(function()
    {
        $( "#tabs" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
        $( "#tabs li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
    });
</script>
<style>
    a { font-size: 12px; }
    .ui-tabs-vertical { width: 1120px; }
    .ui-tabs-vertical .ui-tabs-nav { padding: .2em .1em .2em .2em; float: left; width: 230px; }
    .ui-tabs-vertical .ui-tabs-nav li { clear: left; width: 100%; border-bottom-width: 1px !important; border-right-width: 0 !important; margin: 0 -1px .2em 0; }
    .ui-tabs-vertical .ui-tabs-nav li a { display:block; }
    .ui-tabs-vertical .ui-tabs-nav li.ui-tabs-active { padding-bottom: 0; padding-right: .1em; border-right-width: 1px; border-right-width: 1px; }
    .ui-tabs-vertical .ui-tabs-panel { padding: 5px; padding-left: 50px; float: right; width: 870px;}
</style>

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">


    function colorRows (dataTable) {
        for (var i=0; i<dataTable.getNumberOfRows(); i++) {
            if (dataTable.getValue (i, 4) < 33) {
                dataTable.setProperty(i, 0, 'style', 'background-color: #cf5050;');
                dataTable.setProperty(i, 1, 'style', 'background-color: #cf5050;');
                dataTable.setProperty(i, 2, 'style', 'background-color: #cf5050;');
                dataTable.setProperty(i, 3, 'style', 'background-color: #cf5050;');
                dataTable.setProperty(i, 4, 'style', 'background-color: #cf5050;');
                dataTable.setProperty(i, 5, 'style', 'background-color: #cf5050;');
            }
            else if (dataTable.getValue (i, 4) < 66) {
                dataTable.setProperty(i, 0, 'style', 'background-color: #cbcf50;');
                dataTable.setProperty(i, 1, 'style', 'background-color: #cbcf50;');
                dataTable.setProperty(i, 2, 'style', 'background-color: #cbcf50;');
                dataTable.setProperty(i, 3, 'style', 'background-color: #cbcf50;');
                dataTable.setProperty(i, 4, 'style', 'background-color: #cbcf50;');
                dataTable.setProperty(i, 5, 'style', 'background-color: #cbcf50;');
            }
            else {
                dataTable.setProperty(i, 0, 'style', 'background-color: #90d050;');
                dataTable.setProperty(i, 1, 'style', 'background-color: #90d050;');
                dataTable.setProperty(i, 2, 'style', 'background-color: #90d050;');
                dataTable.setProperty(i, 3, 'style', 'background-color: #90d050;');
                dataTable.setProperty(i, 4, 'style', 'background-color: #90d050;');
                dataTable.setProperty(i, 5, 'style', 'background-color: #90d050;');
            }
        }
    }

    var QueryWrapper = function(query, visualization, visOptions, errorContainer, colorTable) {

        this.query = query;
        this.visualization = visualization;
        this.options = visOptions || {};
        this.errorContainer = errorContainer;
        this.currentDataTable = null;
        this.colorTable = colorTable;

        if (!visualization || !('draw' in visualization) ||
                (typeof(visualization['draw']) != 'function')) {
            throw Error('Visualization must have a draw method.');
        }
    };


    /** Draws the last returned data table, if no data table exists, does nothing.*/
    QueryWrapper.prototype.draw = function() {
        if (!this.currentDataTable) {
            return;
        }
        if (this.colorTable) {
            colorRows (this.currentDataTable);
        }
        this.visualization.draw(this.currentDataTable, this.options);
    };


    /**
     * Sends the query and upon its return draws the visualization.
     * If the query is set to refresh then the visualization will be drawn upon
     * each refresh.
     */
    QueryWrapper.prototype.sendAndDraw = function() {
        var query = this.query;
        var self = this;
        query.send(function(response) {self.handleResponse(response)});
    };


    /** Handles the query response returned by the data source. */
    QueryWrapper.prototype.handleResponse = function(response) {
        this.currentDataTable = null;
        if (response.isError()) {
            this.handleErrorResponse(response);
        } else {
            this.currentDataTable = response.getDataTable();
            this.draw();
        }
        executeNext();
    };


    /** Handles a query response error returned by the data source. */
    QueryWrapper.prototype.handleErrorResponse = function(response) {
        var message = response.getMessage();
        var detailedMessage = response.getDetailedMessage();
        if (this.errorContainer) {
            google.visualization.errors.addError(this.errorContainer,
                    message, detailedMessage, {'showInTooltip': false});
        } else {
            throw Error(message + ' ' + detailedMessage);
        }
        executeNext();
    };


    /** Aborts the sending and drawing. */
    QueryWrapper.prototype.abort = function() {
        this.query.abort();
    };
</script>
<script type="text/javascript">
google.load ("visualization", "1", {packages: ["corechart"]});
google.load('visualization', '1', {packages: ['table']});
google.setOnLoadCallback (drawActivityCharts);
//google.setOnLoadCallback (drawUniqueChart);
/*var inter;
 var inter2;
 var inter3;*/
var methodsStack;

function executeNext () {
    var methodToExecute = methodsStack.pop();
    if (methodToExecute == null || methodToExecute.length < 1) {
        return;
    }
    window[methodToExecute]();
}


function drawActiveChart ()
{
    //clearInterval (inter);

    var container = document.getElementById('activeChart');
    var chart = new google.visualization.LineChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=ActiveChart&period=' + $("#select_period_01").val() + '&region=' + $("#select_region_01").val());
    var options = { title: 'Количество активных ОУ по услугам в день', width: '100%', height: '100%', legend: {position: 'right', alignment: 'end'},
        chartArea: {width: '70%', height: '80%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawUniqueChart ()
{
    //clearInterval (inter2);

    var container = document.getElementById('uniqueChart');
    var chart = new google.visualization.LineChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=UniqueChart&period=' + $("#select_period_011").val() + '&region=' + $("#select_region_011").val());
    var options = { title: 'Количество уникальных пользователей по услугам в день', width: '100%', height: '100%', legend: {position: 'right', alignment: 'end'},
        chartArea: {width: '70%', height: '80%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawContentsChart()
{
    //clearInterval (inter);

    var container = document.getElementById('contentsChart');
    var chart = new google.visualization.PieChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=ContentsChart');
    var options = { title: 'Состав потребления питания в ОУ', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие' };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function toUTF8(str)
{
    return unescape(encodeURIComponent(str));
}

function drawRefillChart ()
{
    //clearInterval (inter);

    var container = document.getElementById('refillChart');
    var chart = new google.visualization.PieChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=RefillChart&region=' + $("#select_region_02").val());
    var options = { title: 'Обеспечение пополнения лицевых счетов учащихся (количество транзакций)', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие' };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawRefillAvgChart ()
{
    //clearInterval (inter2);

    var container = document.getElementById('refillAvgChart');
    var chart = new google.visualization.ColumnChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=RefillAvgChart&region=' + $("#select_region_02").val());
    var options = { title: 'Обеспечение пополнения лицевых счетов учащихся (средняя сумма пополнения)', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие' };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawRefillProgressChart ()
{
    //clearInterval (inter3);

    var container = document.getElementById('refillProgressChart');
    var chart = new google.visualization.SteppedAreaChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=RefillProgressChart&region=' + $("#select_region_02").val());
    var options = { title: 'Обеспечение пополнения лицевых счетов учащихся (динамика пополнений)', isStacked: true, vAxis: {title: 'Динамика пополнений относительно всех пополнений', maxValue: 100, viewWindow: {max: 100}} };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawInformingChart()
{
    //clearInterval (inter);

    var container = document.getElementById('informingChart');
    var chart = new google.visualization.PieChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=InformingChart&region=' + $("#select_region_03").val());
    var options = { title: 'Организация информирования родителей', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawBenefitsChart()
{
    //clearInterval (inter);

    var container = document.getElementById('benefitsChart');
    var chart = new google.visualization.PieChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=BenefitsChart');
    var options = { title: 'Детализация льготных категорий кроме 1-4 класса', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawBenefitPartChart()
{
    //clearInterval (inter2);

    var container = document.getElementById('benefitPartChart');
    var chart = new google.visualization.PieChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=BenefitPartChart');
    var options = { title: 'Льготные категории по питанию в общем составе учащихся', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawVisitorsChart()
{
    //clearInterval (inter);

    var container = document.getElementById('visitorsChart');
    var chart = new google.visualization.AreaChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=VisitorsChart&period=' + $("#select_period_02").val() + '&region=' + $("#select_region_04").val());
    var options = { title: 'Процент учащихся, находившихся в школе', vAxis: {maxValue: 100}, width: '100%', height: '100%', legend: {position: 'bottom'},
        chartArea: {width: '90%', height: '70%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}


function drawRatingDescChart()
{
    //clearInterval (inter);

    var container = document.getElementById('orgsRatingChartDesc');
    var table = new google.visualization.Table(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=OrgsRatingChart');
    var options = { title: 'Рейтинг ОУ', sortColumn: 4, sortAscending: false, allowHtml: true, showRowNumber: true };

    //	Start drawing
    var queryWrapper = new QueryWrapper(query, table, options, container, true);
    queryWrapper.sendAndDraw();
    drawToolbar();
    //executeNext();
}


function drawRatingAscChart()
{
    //clearInterval (inter2);

    var container = document.getElementById('orgsRatingChartAsc');
    var table = new google.visualization.Table(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=OrgsRatingChart');
    var options = { title: 'Рейтинг ОУ', sortColumn: 4, sortAscending: true, allowHtml: true, showRowNumber: true };

    //	Start drawing
    var queryWrapper = new QueryWrapper(query, table, options, container, true);
    queryWrapper.sendAndDraw();
    drawToolbar();
    //executeNext();
}

function drawFiscalChart ()
{
    //clearInterval (inter);

    var container = document.getElementById('fiscalChart');
    var chart = new google.visualization.LineChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=FiscalChart');
    //&period=' + $("#select_period_01").val() + '&region=' + $("#select_region_01").val());
    var options = { title: 'Финансовые показатели', width: '100%', height: '100%', legend: {position: 'right', alignment: 'end'},
        chartArea: {width: '70%', height: '80%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
    //executeNext();
}

function drawContragentsChart ()
{
    var container = document.getElementById('contragentsChart');
    var chart = new google.visualization.AreaChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=ContragentsChart');
    //&period=' + $("#select_period_02").val() + '&region=' + $("#select_region_04").val());
    var options = { title: 'Показатели контрагентов', vAxis: {maxValue: 100}, width: '100%', height: '100%', legend: {position: 'bottom'},
        chartArea: {width: '90%', height: '70%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
}

function drawCardsChart ()
{
    var container = document.getElementById('cardsChart');
    var chart = new google.visualization.AreaChart(container);
    var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=CardsChart');
    //&period=' + $("#select_period_02").val() + '&region=' + $("#select_region_04").val());
    var options = { title: 'Количество активных ОУ по регистрации карт в день', vAxis: {maxValue: 100}, width: '100%', height: '100%', legend: {position: 'bottom'},
        chartArea: {width: '90%', height: '70%', left: '50'}, fontSize: 11};
    var queryWrapper = new QueryWrapper(query, chart, options, container);
    queryWrapper.sendAndDraw();
}



function drawToolbar() {
    var components = [
        {type: 'csv', datasource: '<%= externalURL %>/processor/prj-state?type=OrgsRatingChart&encoding=cyr'}
    ];

    var container = document.getElementById('export_toolbar_01');
    google.visualization.drawToolbar(container, components);
};

function drawRatingCharts ()
{
    /*inter = setInterval(drawRatingDescChart, 10);
     inter2 = setInterval(drawRatingAscChart, 10);*/
    methodsStack = ["drawRatingDescChart", "drawRatingAscChart"];
    executeNext();
}

function drawBenefitsCharts ()
{
    //inter = setInterval(drawBenefitsChart, 10);
    //inter2 = setInterval(drawBenefitPartChart, 10);
    methodsStack = ["drawBenefitsChart"];
    executeNext();
}

function drawActivityCharts ()
{
    /*inter = setInterval(drawActiveChart, 10);*/
    methodsStack = ["drawActiveChart"];
    executeNext();
}

function drawUniqueCharts () {

    /*inter2 = setInterval(drawUniqueChart, 10);*/
    methodsStack = ["drawUniqueChart"];
    executeNext();
}

function drawRefillCharts ()
{
    /*inter = setInterval(drawRefillChart, 10);
     inter2 = setInterval(drawRefillAvgChart, 10);
     inter3 = setInterval(drawRefillProgressChart, 10);*/
    methodsStack = ["drawRefillChart", "drawRefillAvgChart", "drawRefillProgressChart"];
    executeNext();
}

function draw (func)
{
    /*inter = setInterval(func, 10);*/
    methodsStack = [func];
    executeNext();
}

function initPeriods ()
{
    initPeriodsFor (document.getElementById ('select_period_01'));
    initPeriodsFor (document.getElementById ('select_period_011'));
    initPeriodsFor (document.getElementById ('select_period_02'));
    initPeriodsFor (document.getElementById ('select_period_08'));
    initPeriodsFor (document.getElementById ('select_period_09'));
}

function initPeriodsFor (container)
{
    var today = new Date ();
    var mm = today.getMonth () + 1;
    var yyyy = today.getFullYear ();

    var y = 0;
    for (y=2012; y<yyyy; y++)
    {
        if (y > 2012)
        {
            addPeriod (container, "1 полугодие " + y, "1-" + y);
        }
        addPeriod (container, "2 полугодие " + y, "2-" + y);
    }
    if (yyyy > 2012)
    {
        addPeriod (container, "1 полугодие " + y, "1-" + y);
    }
    if (mm > 6)
    {
        addPeriod (container, "2 полугодие " + yyyy, "2-" + yyyy);
    }
}

function addPeriod (container, title, value)
{
    var elOptNew = document.createElement('option');
    elOptNew.text = title;
    elOptNew.value = value;
    elOptNew.selected = true;

    try
    {
        container.add (elOptNew, null); // standards compliant; doesn't work in IE
    }
    catch(ex)
    {
        container.add(elOptNew); // IE only
    }
}
</script>
</head>
<body>
<div id="tabs">
    <ul>
        <li><a href="#tabs-1" onclick="drawActivityCharts()">Количество активных ОУ <br>по услугам в день</a></li>
        <li><a href="#tabs-11" onclick="drawUniqueCharts()">Количество уникальных <br>пользователей по услугам в день</a></li>
        <li><a href="#tabs-2" onclick="draw('drawContentsChart')">Состав потребления <br>питания в ОУ</a></li>
        <li><a href="#tabs-3" onclick="drawRefillCharts()">Обеспечение пополнения<br>лицевых счетов учащихся</a></li>
        <li><a href="#tabs-4" onclick="draw('drawInformingChart')">Организация <br>информирования родителей</a></li>
        <li><a href="#tabs-5" onclick="drawBenefitsCharts()">Показатели числа льготников<br/>по питанию в общем составе<br/> учащихся</a></li>
        <li><a href="#tabs-6" onclick="draw('drawVisitorsChart')">Посещаемость</a></li>
        <li><a href="#tabs-7" onclick="drawRatingCharts()">Рейтинг ОУ</a></li>
        <li><a href="#tabs-8" onclick="drawFiscalChart()">Финансовые показатели</a></li>
        <li><a href="#tabs-9" onclick="drawContragentsChart()">Показатели контрагентов</a></li>
        <li><a href="#tabs-10" onclick="drawCardsChart()">Показатели по картам</a></li>
    </ul>
    <div id="tabs-1" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_01" name="period" onchange="drawActivityCharts()"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_01" name="region" onchange="drawActivityCharts()"><%= regionsStr %></select></div>
        <div id="activeChart" style="width: 100%; height: 310px;"></div><br/>
    </div>
    <div id="tabs-11" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_011" name="period" onchange="drawActivityCharts()"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_011" name="region" onchange="drawActivityCharts()"><%= regionsStr %></select></div>
        <div id="uniqueChart" style="width: 100%; height: 310px;"></div>
    </div>
    <div id="tabs-2" style="padding: 0px; margin: 0px">
        <div id="contentsChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-3" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_02" name="region" onchange="drawRefillCharts()"><%= regionsStr %></select></div>
        <div id="refillChart" style="width: 100%; height: 500px;"></div>
        <div id="refillAvgChart" style="width: 100%; height: 500px;"></div>
        <div id="refillProgressChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-4" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_03" name="region" onchange="drawInformingChart()"><%= regionsStr %></select></div>
        <div id="informingChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-5" style="padding: 0px; margin: 0px">
        <div id="benefitsChart" style="width: 100%; height: 310px;"></div>
        <div id="benefitPartChart" style="width: 100%; height: 310px;"></div>
    </div>
    <div id="tabs-6" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_02" name="period" onchange="draw('drawVisitorsChart')"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_04" name="region" onchange="drawVisitorsChart()"><%= regionsStr %></select></div>
        <div id="visitorsChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-7" style="padding: 0px; margin: 0px">
        <table cellspacing="0" cellpadding="0" border="0" style="width: 100%">
            <tr>
                <td colspan="2" style="text-align: right; padding-bottom: 10px"><a href="<%= externalURL %>/processor/prj-state?type=OrgsRatingChart&encoding=cyr&tqx=out%3Acsv%3B" target="_blank">Загрузить данные в CSV</a></td>
            </tr>
            <tr>
                <td style="width: 50%; padding-right: 5px; padding-bottom: 15px">
                    <div id="orgsRatingChartDesc" style="width: 100%; height: 500px;"></div>
                </td>
                <td style="width: 50%; padding-right: 5px; padding-bottom: 15px">
                    <div id="orgsRatingChartAsc" style="width: 100%; height: 500px;"></div>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="background-color: #eeeeee;">
                    <strong>Проход (%)</strong> - процент уникальных учащихся, осуществивших проход по электронной карте в ОУ за последние 7 дней<br/>
                    <strong>Платное питание (%)</strong> - процент уникальных учащихся, осуществивших оплату горячего питания или буфетной продукции по электронной карте в ОУ за последние 7 дней<br/>
                    <strong>Льготное питание</strong> - наличие проведенного в системе льготного питания хотя бы один раз за последние 7 дней<br/>
                    <strong>Рейтинг (%)</strong> - (Проход (%) + Платное питание (%) + ( Если Льготное питание = ‘Да’  - 100%, иначе – 0 %) ) / 3<br/>
                </td>

            </tr>
        </table>
    </div>
    <div id="tabs-8" style="padding: 0px; margin: 0px">
        <!--<div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_08" name="period" onchange="drawFiscalChart()"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_08" name="region" onchange="drawFiscalChart()"><%= regionsStr %></select></div>-->
        <div id="fiscalChart" style="width: 100%; height: 310px;"></div><br/>
    </div>
    <div id="tabs-9" style="padding: 0px; margin: 0px">
        <!--<div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_09" name="period" onchange="drawContragentsChart()"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_09" name="region" onchange="drawContragentsChart()"><%= regionsStr %></select></div>-->
        <div id="contragentsChart" style="width: 100%; height: 410px;"></div><br/>
    </div>
    <div id="tabs-10" style="padding: 0px; margin: 0px">
        <!--<div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_09" name="period" onchange="drawContragentsChart()"></select></div>
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_region_09" name="region" onchange="drawContragentsChart()"><%= regionsStr %></select></div>-->
        <div id="cardsChart" style="width: 100%; height: 410px;"></div><br/>
    </div>
</div>
<script>
    initPeriods();
</script>
</body>
</html>