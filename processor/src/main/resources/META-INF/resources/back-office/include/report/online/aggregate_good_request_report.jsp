<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="aggregateGoodRequestReportPanelGrid" binding="#{mainPage.detailedGoodRequestReportPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid id="goodRequestReportParamPanelGrid" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.detailedGoodRequestReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar"
                             actionListener="#{mainPage.detailedGoodRequestReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect" value="#{mainPage.detailedGoodRequestReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.detailedGoodRequestReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar"
                             actionListener="#{mainPage.detailedGoodRequestReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.detailedGoodRequestReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                             actionListener="#{mainPage.detailedGoodRequestReportPage.onEndDateSpecified}" />
            </rich:calendar>


            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <h:panelGroup>
                <a4j:commandButton value="..."
                                   action="#{mainPage.detailedGoodRequestReportPage.showContragentListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener
                            value="#{mainPage.detailedGoodRequestReportPage.contragentStringIdOfOrgList}"
                            target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    <f:setPropertyActionListener value="Выбор контрагента" target="#{mainPage.orgFilterPageName}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value="{#{mainPage.detailedGoodRequestReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.detailedGoodRequestReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.detailedGoodRequestReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.detailedGoodRequestReportPage.filter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Статус документов" />
            <h:selectOneMenu id="documentStateFilter" value="#{mainPage.detailedGoodRequestReportPage.documentStateFilterMenu.documentStateFilter}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="documentStateFilterConverter" />
                <f:selectItems value="#{mainPage.detailedGoodRequestReportPage.documentStateFilterMenu.items}" />
            </h:selectOneMenu>

            <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildDetailedGoodRequestReport}"
                               reRender="workspaceTogglePanel, detailedGoodRequestTable"
                               styleClass="command-button" status="sReportGenerateStatus" />
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-div">
        <rich:dataTable id="detailedGoodRequestTable" var="items"
                        value="#{mainPage.detailedGoodRequestReportPage.detailedGoodRequestReportItems}"
                        footerClass="data-table-footer" rows="20" width="100%">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column rowspan="2">
                        <h:outputText value="№ заявки" />
                    </rich:column>
                    <rich:column colspan="2">
                        <h:outputText value="Поставщик" />
                    </rich:column>
                    <rich:column colspan="3">
                        <h:outputText value="Получатель" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Товар/продукт" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Количество заказов общее" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Количество суточной пробы" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Количество заказов на детей, временно обучающихся в данной ОО"/>
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Дата к исполнению" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Дата выгрузки" />
                    </rich:column>
                  <%--  <rich:column rowspan="2">
                        <h:outputText value="Дата создания" />
                    </rich:column>
                    <rich:column rowspan="2">
                        <h:outputText value="Дата изменения" />
                    </rich:column>--%>
                    <rich:column breakBefore="true">
                        <h:outputText value="ID" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="ID" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="№ ОУ" />
                    </rich:column>
                    <rich:column>
                        <h:outputText value="Наименование" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.number}" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.idOfSupplier}" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.supplierName}" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.idOfEducation}" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.educationNumber}" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}">
                <h:outputText value="#{items.educationName}" />
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}" styleClass="center-aligned-column">
                <h:outputText value="#{items.doneDate}" converter="dateConverter" />
            </rich:column>
            <rich:column rowspan="#{items.commoditiesCounts}" styleClass="center-aligned-column">
                <h:outputText value="#{items.lastCreateOrUpdateDate}" converter="timeConverter" />
            </rich:column>
            <rich:subTable value="#{items.commodities}" var="commodity"
                           columnClasses="center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
                <rich:column>
                    <h:outputText value="#{commodity.name}" />
                </rich:column>
                <rich:column>
                    <h:outputText value="#{commodity.totalCount}" />
                </rich:column>
                <rich:column>
                    <h:outputText value="#{commodity.dailySampleCount==null?'0':commodity.dailySampleCount}" />
                </rich:column>
                <rich:column>
                    <h:outputText value="#{commodity.tempClientsCount==null?'0':commodity.tempClientsCount}" />
                </rich:column>
            </rich:subTable>
            <f:facet name="footer">
                <rich:datascroller for="detailedGoodRequestTable" renderIfSinglePage="false" maxPages="10"
                                   fastControls="hide" stepControls="auto" boundaryControls="hide"
                                   rendered="#{not empty mainPage.detailedGoodRequestReportPage.detailedGoodRequestReportItems}">
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
