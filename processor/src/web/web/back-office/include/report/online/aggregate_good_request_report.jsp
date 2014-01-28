<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="aggregateGoodRequestReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.AggregateGoodRequestReportPage"--%>
<h:panelGrid id="aggregateGoodRequestReportPanelGrid" binding="#{aggregateGoodRequestReportPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid id="goodRequestReportParamPanelGrid" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
            <rich:calendar value="#{aggregateGoodRequestReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
            <h:selectOneMenu value="#{aggregateGoodRequestReportPage.daysLimit}" converter="javax.faces.Integer"
                             styleClass="output-text" >
                <f:selectItem itemValue="0" itemLabel="1 месяц"/>
                <f:selectItem itemValue="2" itemLabel="2 недели"/>
                <f:selectItem itemValue="1" itemLabel="1 неделя"/>
            </h:selectOneMenu>
            <%--<rich:calendar value="#{aggregateGoodRequestReportPage.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />--%>

            <h:outputText styleClass="output-text required-field" escape="true" value="Поставщик" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{aggregateGoodRequestReportPage.showSourceListSelectPage}" reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{aggregateGoodRequestReportPage.getContragentStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value="{#{aggregateGoodRequestReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{aggregateGoodRequestReportPage.showEducationListSelectPage}" reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{aggregateGoodRequestReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{aggregateGoodRequestReportPage.filter}}" />
            </h:panelGroup>
            <a4j:commandButton value="Генерировать отчет" action="#{aggregateGoodRequestReportPage.generateReport}"
                               reRender="mainMenu, workspaceTogglePanel, aggregateGoodRequestTable"
                               styleClass="command-button" status="sReportGenerateStatus" />
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-div">
        <rich:dataTable id="aggregateGoodRequestTable" var="items"
                        value="#{aggregateGoodRequestReportPage.aggregateGoodRequestReportItems}"
                        footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column rowspan="2">
                        <h:outputText value="№ заявки"/>
                    </rich:column>
                    <rich:column colspan="2">
                        <h:outputText value="Поставщик"/>
                    </rich:column>
                    <rich:column colspan="3">
                        <h:outputText value="Получатель"/>
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Товар/продукт"/>
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Количество заказов"/>
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Количество суточной пробы"/>
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Дата к исполнению"/>
                    </rich:column>
                    <rich:column breakBefore="true">
                        <h:outputText value="ID"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="ID"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="№ ОУ"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.number}"/>
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.idOfSupplier}"/>
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.supplierName}"/>
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.idOfEducation}"/>
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.educationNumber}"/>
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.educationName}"/>
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.doneDate}" converter="dateConverter"/>
            </rich:column>
            <rich:subTable value="#{items.commodities}" var="commodity"
                      columnClasses="center-aligned-column, right-aligned-column">
                <rich:column>
                    <h:outputText value="#{commodity.name}"/>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{commodity.totalCount}"/>
                </rich:column>
                <rich:column>
                    <h:outputText value="#{commodity.dailySampleCount==null?'-':commodity.dailySampleCount}"/>
                </rich:column>
            </rich:subTable>
            <f:facet name="footer">
                <rich:datascroller for="aggregateGoodRequestTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide" rendered="#{not empty aggregateGoodRequestReportPage.aggregateGoodRequestReportItems}">
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
