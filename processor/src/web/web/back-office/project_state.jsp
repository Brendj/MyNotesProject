<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>ИС ПП - панель мониторинга</title>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%
String externalURL = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_EXTERNAL_URL);
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
    .ui-tabs-vertical { width: 975px; }
    .ui-tabs-vertical .ui-tabs-nav { padding: .2em .1em .2em .2em; float: left; width: 220px; }
    .ui-tabs-vertical .ui-tabs-nav li { clear: left; width: 100%; border-bottom-width: 1px !important; border-right-width: 0 !important; margin: 0 -1px .2em 0; }
    .ui-tabs-vertical .ui-tabs-nav li a { display:block; }
    .ui-tabs-vertical .ui-tabs-nav li.ui-tabs-active { padding-bottom: 0; padding-right: .1em; border-right-width: 1px; border-right-width: 1px; }
    .ui-tabs-vertical .ui-tabs-panel { padding: 5px; padding-left: 50px; float: right; width: 745px;}
</style>

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
    /**
     * A google.visualization.Query Wrapper. Sends a
     * query and draws the visualization with the returned data or outputs an
     * error message.
     *
     * DISCLAIMER: This is an example code which you can copy and change as
     * required. It is used with the google visualization API which is assumed to
     * be loaded to the page. For more info see:
     * https://developers.google.com/chart/interactive/docs/reference#Query
     */


    /**
     * Constructs a new query wrapper with the given query, visualization,
     * visualization options, and error message container. The visualization
     * should support the draw(dataTable, options) method.
     * @constructor
     */
    var QueryWrapper = function(query, visualization, visOptions, errorContainer) {

        this.query = query;
        this.visualization = visualization;
        this.options = visOptions || {};
        this.errorContainer = errorContainer;
        this.currentDataTable = null;

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
    };


    /** Aborts the sending and drawing. */
    QueryWrapper.prototype.abort = function() {
        this.query.abort();
    };
</script>
<script type="text/javascript">
    google.load ("visualization", "1", {packages: ["corechart"]});
    google.setOnLoadCallback (initPeriods);
    google.setOnLoadCallback (drawActiveChart);
    google.setOnLoadCallback (drawUniqueChart);
    var inter;
    var inter2;
    function drawActiveChart ()
    {
        clearInterval (inter);

        var period = document.getElementById('select_period_01');
        var container = document.getElementById('activeChart');
        var chart = new google.visualization.LineChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=ActiveChart&period=' + period.value);
        var options = { title: 'Количество активных ОУ по услугам в день', width: '100%', height: '100%', legend: {position: 'right', alignment: 'end'},
            chartArea: {width: '70%', height: '80%', left: '50'}, fontSize: 11};
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawUniqueChart ()
    {
        clearInterval (inter2);

        var period = document.getElementById('select_period_01');
        var container = document.getElementById('uniqueChart');
        var chart = new google.visualization.LineChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=UniqueChart&period=' + period.value);
        var options = { title: 'Количество уникальных пользователей по услугам в день', width: '100%', height: '100%', legend: {position: 'right', alignment: 'end'},
            chartArea: {width: '70%', height: '80%', left: '50'}, fontSize: 11};
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawContentsChart()
    {
        clearInterval (inter);

        var container = document.getElementById('contentsChart');
        var chart = new google.visualization.PieChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=ContentsChart');
        var options = { title: 'Состав потребления питания в ОУ', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawRefillChart ()
    {
        clearInterval (inter);

        var container = document.getElementById('refillChart');
        var chart = new google.visualization.PieChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=RefillChart');
        var options = { title: 'Обеспечение пополнения лицевых счетов учащихся', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие' };
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawInformingChart()
    {
        clearInterval (inter);

        var container = document.getElementById('informingChart');
        var chart = new google.visualization.PieChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=InformingChart');
        var options = { title: 'Организация информирования родителей', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawBenefitPartChart()
    {
        clearInterval (inter2);

        var container = document.getElementById('benefitPartChart');
        var chart = new google.visualization.PieChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=BenefitPartChart');
        var options = { title: 'Льготные категории по питанию в общем составе учащихся', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawBenefitsChart()
    {
        clearInterval (inter);

        var container = document.getElementById('benefitsChart');
        var chart = new google.visualization.PieChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=BenefitsChart');
        var options = { title: 'Детализация льготных категорий кроме 1-4 класса', sliceVisibilityThreshold: 1/10000000, pieResidueSliceLabel: 'Другие'  };
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawVisitorsChart()
    {
        clearInterval (inter);

        var period = document.getElementById('select_period_02');
        var container = document.getElementById('visitorsChart');
        var chart = new google.visualization.AreaChart(container);
        var query = new google.visualization.Query ('<%= externalURL %>/processor/prj-state?type=VisitorsChart&period=' + period.value);
        var options = { title: 'Процент учащихся, находившихся в школе', vAxis: {maxValue: 100}, width: '100%', height: '100%', legend: {position: 'bottom'},
            chartArea: {width: '90%', height: '70%', left: '50'}, fontSize: 11};
        var queryWrapper = new QueryWrapper(query, chart, options, container);
        queryWrapper.sendAndDraw();
    }

    function drawBenefitsCharts ()
    {
        inter = setInterval(drawBenefitsChart, 10);
        inter2 = setInterval(drawBenefitPartChart, 10);
    }

    function drawActivityCharts ()
    {
        inter = setInterval(drawActiveChart, 10);
        inter2 = setInterval(drawUniqueChart, 10);
    }

    function draw (func)
    {
        inter = setInterval(func, 10);
    }

    function initPeriods ()
    {
        initPeriodsFor (document.getElementById ('select_period_01'));
        initPeriodsFor (document.getElementById ('select_period_02'));
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
        <li><a href="#tabs-1" onclick="drawActivityCharts()">Статус исполнения проекта <br>внедрения ИС ПП</a></li>
        <li><a href="#tabs-2" onclick="draw(drawContentsChart)">Состав потребления <br>питания в ОУ</a></li>
        <li><a href="#tabs-3" onclick="draw(drawRefillChart)">Обеспечение пополнения<br>лицевых счетов учащихся</a></li>
        <li><a href="#tabs-4" onclick="draw(drawInformingChart)">Организация <br>информирования родителей</a></li>
        <li><a href="#tabs-5" onclick="drawBenefitsCharts()">Показатели числа льготников<br/>по питанию в общем составе<br/> учащихся</a></li>
        <li><a href="#tabs-6" onclick="draw(drawVisitorsChart)">Посещаемость</a></li>
    </ul>
    <div id="tabs-1" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_01" name="period" onchange="drawActivityCharts()"></select></div>
        <div id="activeChart" style="width: 100%; height: 310px;"></div><br/>
        <div id="uniqueChart" style="width: 100%; height: 310px;"></div>
    </div>
    <div id="tabs-2" style="padding: 0px; margin: 0px">
        <div id="contentsChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-3" style="padding: 0px; margin: 0px">
        <div id="refillChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-4" style="padding: 0px; margin: 0px">
        <div id="informingChart" style="width: 100%; height: 500px;"></div>
    </div>
    <div id="tabs-5" style="padding: 0px; margin: 0px">
        <div id="benefitPartChart" style="width: 100%; height: 310px;"></div>
        <div id="benefitsChart" style="width: 100%; height: 310px;"></div>
    </div>
    <div id="tabs-6" style="padding: 0px; margin: 0px">
        <div width="100%" style="text-align: right"><select style="font-size: 10pt" id="select_period_02" name="period" onchange="draw(drawVisitorsChart)"></select></div>
        <div id="visitorsChart" style="width: 100%; height: 500px;"></div>
    </div>
</div>
</body>
</html>