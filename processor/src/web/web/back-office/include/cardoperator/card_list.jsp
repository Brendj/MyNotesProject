<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="cardOperationListPage" type="ru.axetta.ecafe.processor.web.ui.cardoperator.CardOperationListPage"--%>
<h:panelGrid id="cardOperationListGrid" binding="#{cardOperationListPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel id="cardOperationListFilterPanel" label="Фильтр(#{cardOperationListPage.cardFilter.status})"
                            switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{cardOperationListPage.cardFilter.org.shortName}" readonly="true"
                             styleClass="input-text" style="width: 240px; margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Клиент" />
            <h:panelGroup id="clientFilter">
                <a4j:commandButton value="..."
                                   action="#{mainPage.showClientSelectListPage(cardOperationListPage.getClientList())}"
                                   reRender="modalClientListSelectorPanel,selectedClientList"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                    <f:setPropertyActionListener value="#{cardOperationListPage.getStringClientList}"
                                                 target="#{mainPage.clientSelectListPage.clientFilter}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                              value=" {#{mainPage.clientTransactionsReportPage.filterClient}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Cтатус" styleClass="output-text" />
            <h:selectOneMenu value="#{cardOperationListPage.cardFilter.cardState}" styleClass="input-text"
                             style="width: 240px;">
                <f:selectItems value="#{cardOperationListPage.cardFilter.cardStateFilterMenu.items}" />
            </h:selectOneMenu>

            <h:outputText styleClass="output-text" escape="true" value="Дата операции" />
            <rich:calendar value="#{cardOperationListPage.startDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false" />

            <h:outputText escape="false" value="Показать операции за весь период" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{cardOperationListPage.showOperationsAllPeriod}"
                                     styleClass="output-text" />

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{cardOperationListPage.updateCardOperationsListPage}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{cardOperationListPage.clearCardOperationsListPageFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="cardTableGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="cardTable" value="#{cardOperationListPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" converter="cardNoConverter"
                          styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Лицевой счет" />
            </f:facet>
            <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <h:outputText escape="true" value="#{item.personName}" converter="cardNoConverter"
                          styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.status}" converter="cardNoConverter" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.date}" converter="cardNoConverter" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" converter="cardNoConverter"
                          styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Печать" />
            </f:facet>
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="cardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
