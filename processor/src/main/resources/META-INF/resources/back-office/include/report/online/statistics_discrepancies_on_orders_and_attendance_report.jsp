<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<script type="text/javascript">
    function onstartloading(){
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button").attr('disabled', '');
        updateWidth();
    }
    jQuery(document).ready(function(){
        updateWidth();
    });
</script>

<h:panelGrid id="statisticsDiscrepanciesOnOrdersAndAttendanceReportPageReportPanelGrid"
             binding="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.showContragentListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.contragentStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                <f:setPropertyActionListener value="Выбор контрагента" target="#{mainPage.orgFilterPageName}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.contragentFilter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.filter}}" />
        </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildDiscrepanciesOnOrdersAndAttendanceReport}"
                           reRender="workspaceTogglePanel, statisticsDiscrepanciesOnOrdersAndAttendanceReportPageReportPanelGrid"
                           styleClass="command-button"
                           status="statisticsDiscrepanciesOnOrdersAndAttendanceReportGenerateStatus" />

        <h:commandButton value="Выгрузить в Excel"
                         actionListener="#{mainPage.exportDiscrepanciesOnOrdersAndAttendanceReport}"
                         styleClass="command-button" />

        <h:commandButton value="Выгрузить в Excel итоговую таблицу"
                         actionListener="#{mainPage.exportDiscrepanciesOnOrdersAndAttendanceReportSum}"
                         styleClass="command-button" />
    </h:panelGrid>
    <a4j:status id="statisticsDiscrepanciesOnOrdersAndAttendanceReportGenerateStatus" onstart="onstartloading()"
                onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <rich:dataTable id="itemsReportTable" style="width: 940px;"
            value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.report.items}"
                        var="item" rowKeyVar="row" rows="20" footerClass="data-table-footer"
                        columnClasses="left-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column>
                        <h:outputText value="Округ"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Тип ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="№ ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Адрес ОО"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Дата"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Заказано"/>
                    </rich:column>
                    <rich:column rendered="false">
                        <h:outputText value="Рекомендуемое кол-во для заказа"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Продано"/>
                    </rich:column>
                    <rich:column rendered="false">
                        <h:outputText value="Продано, резервникам"/>
                    </rich:column>
                    <%--<rich:column>--%>
                        <%--<h:outputText value="Утилизировано"/>--%>
                    <%--</rich:column>--%>
                    <rich:column>
                        <h:outputText value="% несоответствия"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column>
                <h:outputText value="#{item.district}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.orgTypeCategory}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.shortName}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.number}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.address}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.currentDate}" converter="dateConverter"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.requestCount==null?0:item.requestCount}"/>
            </rich:column>
            <rich:column rendered="false">
                <h:outputText value="#{item.forecastQty==null?0:item.forecastQty}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.orderCount==null?0:item.orderCount}"/>
            </rich:column>
            <%--<rich:column>--%>
                <%--<h:outputText value="#{item.orderReserveCount==null?0:item.orderReserveCount}"/>--%>
            <%--</rich:column>--%>
            <rich:column>
                <h:outputText value="#{item.percent}">
                    <f:convertNumber pattern="#0.00"/>
                </h:outputText>
            </rich:column>
            <rich:column rendered="false">
                <h:outputText value="#{(item.requestCount==null || item.orderCount==null || item.requestCount==0)?0:(item.requestCount-item.orderCount)*100/item.requestCount}">
                    <f:convertNumber pattern="#0.00"/>
                </h:outputText>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="itemsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
        <rich:dataTable id="totalItemsReportTable" style="width: 940px;"
                        value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.report.itemTotals}"
                        var="itemTotals" rowKeyVar="row" rows="5" footerClass="data-table-footer"
                        columnClasses="left-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column colspan="9">
                        <h:outputText value="Итого за период с " styleClass="column-header" />
                        <h:outputText escape="true"
                                      value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.startDate}"
                                      styleClass="column-header">
                            <f:convertDateTime pattern="dd.MM.yyyy" />
                        </h:outputText>
                        <h:outputText value=" по " styleClass="column-header" />
                        <h:outputText escape="true"
                                      value="#{mainPage.discrepanciesOnOrdersAndAttendanceReportPage.endDate}"
                                      styleClass="column-header">
                            <f:convertDateTime pattern="dd.MM.yyyy" />
                        </h:outputText>
                    </rich:column>
                    <rich:column breakBefore="true">
                        <h:outputText value="Округ" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Тип ОО" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование ОО" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="№ ОО" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Адрес ОО" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Заказано" />
                    </rich:column>
                    <rich:column rendered="false">
                        <h:outputText value="Рекомендуемое кол-во для заказа" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Продано" />
                    </rich:column>
                    <rich:column rendered="false">
                        <h:outputText value="Продано, резервникам" />
                    </rich:column>
                    <%--<rich:column>--%>
                        <%--<h:outputText value="Утилизировано" />--%>
                    <%--</rich:column>--%>
                    <rich:column>
                        <h:outputText value="% несоответствия" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column>
                <h:outputText value="#{itemTotals.district}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.orgTypeCategory}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.shortName}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.number}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.address}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.totalRequestCount==null?0:itemTotals.totalRequestCount}" />
            </rich:column>
            <rich:column rendered="false">
                <h:outputText value="#{itemTotals.totalForecastQty==null?0:itemTotals.totalForecastQty}" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{itemTotals.totalOrderCount==null?0:itemTotals.totalOrderCount}" />
            </rich:column>
            <%--<rich:column>--%>
                <%--<h:outputText value="#{itemTotals.totalOrderReserveCount==null?0:itemTotals.totalOrderReserveCount}" />--%>
            <%--</rich:column>--%>
            <rich:column>
                <h:outputText value="#{itemTotals.percent}">
                    <f:convertNumber pattern="#0.00" />
                </h:outputText>
            </rich:column>
            <rich:column rendered="false">
                <h:outputText
                        value="#{(itemTotals.totalRequestCount==null || itemTotals.totalOrderCount==null || itemTotals.totalRequestCount==0)?0:(itemTotals.totalRequestCount-itemTotals.totalOrderCount)*100/itemTotals.totalRequestCount}">
                    <f:convertNumber pattern="#0.00" />
                </h:outputText>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="totalItemsReportTable" renderIfSinglePage="false" maxPages="10"
                                   fastControls="hide" stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
    </h:panelGrid>
    <h:panelGrid>

    </h:panelGrid>
</h:panelGrid>