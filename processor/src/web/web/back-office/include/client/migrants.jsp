<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="migrantsPage" type="ru.axetta.ecafe.processor.web.ui.client.MigrantsPage"--%>
<h:panelGrid id="migrantsListPanelGrid" binding="#{migrantsPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid" columnClasses="column-width-250,column-width-500" id="migrantsServisePanelGrid">

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-grid">
                <h:inputText value="#{migrantsPage.orgName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px; width: 300px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                                   reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="6" target="#{mainPage.orgSelectPage.filterMode}" />
                </a4j:commandButton>
            </h:panelGroup>
            <h:outputText styleClass="output-text" escape="true" value="Клиент" />
            <h:panelGroup id="clientFilter">
                <a4j:commandButton value="..."
                                   action="#{mainPage.showClientSelectListPage(migrantsPage.getClientList())}"
                                   reRender="modalClientListSelectorPanel,selectedClientList"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                    #{rich:component('modalClientListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" id="clientFilterButton">
                    <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                    <f:setPropertyActionListener value="#{migrantsPage.getStringClientList}"
                                                 target="#{mainPage.clientSelectListPage.clientFilter}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                              value=" {#{migrantsPage.filterClient}}" />
            </h:panelGroup>
            <h:outputText escape="true" value="Guid клиента" styleClass="output-text" />
            <h:inputText value="#{migrantsPage.guid}" maxlength="36"
                         styleClass="input-text, long-field" />

            <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
            <rich:calendar value="#{migrantsPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" readonly="#{migrantsPage.ignoreDates}">
            </rich:calendar>

            <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{migrantsPage.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" readonly="#{migrantsPage.ignoreDates}">
            </rich:calendar>

            <h:outputText escape="true" value="Строить за всё время" styleClass="output-text" />
            <h:selectBooleanCheckbox id="showAllMigrants"
                                     value="#{migrantsPage.ignoreDates}"
                                     styleClass="output-text" >
                <a4j:support event="onchange" reRender="migrantsServisePanelGrid"/>
            </h:selectBooleanCheckbox>
            <h:outputText escape="true" value="Тип заявок" styleClass="output-text" />
            <h:selectOneMenu value="#{migrantsPage.migrantType}" style="width:180px;">
                <f:selectItems value="#{migrantsPage.migrantTypes}" />
            </h:selectOneMenu>
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{migrantsPage.updateFilter}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{migrantsPage.clearFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="migrantsPageGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <rich:dataTable id="migrantsTable" value="#{migrantsPage.items}" var="item" rows="10" footerClass="data-table-footer">
        <rich:column headerClass="column-header" sortBy="#{item.requestNumber}">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер заявки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.requestNumber}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" sortBy="#{item.lastUpdateDateTime}">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания/редактирования" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lastUpdateDateTime}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Инициатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.initiator}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Guid клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{item.guid}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ФИО клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{item.fio}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Группа" />
            </f:facet>
            <h:outputText escape="true" value="#{item.group}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ОО источник" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgRegistry}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ОО посещения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgVisit}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Кружок/секция" />
            </f:facet>
            <h:outputText escape="true" value="#{item.section}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Начало занятий" />
            </f:facet>
            <h:outputText escape="true" value="#{item.visitStartDate}" styleClass="output-text" converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Окончание занятий" />
            </f:facet>
            <h:outputText escape="true" value="#{item.visitEndDate}" styleClass="output-text" converter="dateConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.resolution}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Действие" />
            </f:facet>
            <a4j:commandLink reRender="migrantsTable" rendered="#{item.isActive}" title="Аннулировать"
                             action="#{migrantsPage.disableMigrantRequest()}">
                <f:setPropertyActionListener value="#{item}" target="#{migrantsPage.currentItem}" />
                <h:graphicImage value="/images/16x16/delete.png" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="migrantsTable" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Сохранить изменения" action="#{migrantsPage.apply}"
                           reRender="migrantsTable" styleClass="command-button"
                           id="applyMigrantsButton" />
    </h:panelGrid>

</h:panelGrid>