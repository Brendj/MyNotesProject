<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="statisticsDiscrepanciesOnOrdersAndAttendanceReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage"--%>
<h:panelGrid id="statisticsDiscrepanciesOnOrdersAndAttendanceReportPageReportPanelGrid"
             binding="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />


        <h:outputText styleClass="output-text required-field" escape="true" value="Поставщик" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.showSourceListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.getContragentStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value="{#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.contragentFilter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.showEducationListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.filter}}" />
        </h:panelGroup>


        <a4j:commandButton value="Генерировать отчет" action="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.buildReport}"
                           reRender="mainMenu, workspaceTogglePanel, statisticsDiscrepanciesOnOrdersAndAttendanceReportPageReportPanelGrid"
                           styleClass="command-button" status="statisticsDiscrepanciesOnOrdersAndAttendanceReportGenerateStatus" />
        <a4j:status id="statisticsDiscrepanciesOnOrdersAndAttendanceReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="itemsReportTable"
            value="#{statisticsDiscrepanciesOnOrdersAndAttendanceReportPage.report.items}"
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
                        <h:outputText value="Заказ факт"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Рекомендуемое кол-во для заказа"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Посещение - факт"/>
                    </rich:column>
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
            <rich:column>
                <h:outputText value="#{item.forecastQty==null?0:item.forecastQty}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{item.enterEventCount==null?0:item.enterEventCount}"/>
            </rich:column>
            <rich:column>
                <h:outputText value="#{(item.requestCount==null || item.enterEventCount==null || item.requestCount==0)?0:(item.requestCount-item.enterEventCount)*100/item.requestCount}">
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
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>