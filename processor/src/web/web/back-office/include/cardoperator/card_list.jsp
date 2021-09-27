<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<script>
    var socket;

    function SetWebSocket (webSocket) {
        socket = new WebSocket(webSocket);
    }

    function SaveFile(message)
    {
        var mes = message;

        socket.send(mes);
        return false;
    }
    function onstartloading(){
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button").attr('disabled', '');
    }
</script>

<h:panelGrid id="cardOperationListGrid" binding="#{mainPage.cardOperatorListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel id="cardOperatorListFilterPanel"
                            label="Фильтр(#{mainPage.cardOperatorListPage.cardOperatorFilter.status})"
                            switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.cardOperatorListPage.cardOperatorFilter.org.shortName}" readonly="true"
                             styleClass="input-text" style="width: 240px; margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Клиент" />
            <h:panelGroup id="clientFilter">
                <%--<a4j:commandButton value="..."
                                   action="#{mainPage.showClientSelectListPage(mainPage.cardOperatorListPage.cardOperatorFilter.getClientList())}"
                                   reRender="modalClientListSelectorPanel,selectedClientList"
                                   oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                    <f:setPropertyActionListener
                            value="#{mainPage.cardOperatorListPage.cardOperatorFilter.getStringClientList}"
                            target="#{mainPage.clientSelectListPage.clientFilter}" />
                </a4j:commandButton>--%>
                <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel,selectedClientList"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
                <h:outputText styleClass="output-text long-field" escape="true" id="selectedClientList"
                              value=" {#{mainPage.cardOperatorListPage.cardOperatorFilter.client.shortName}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Cтатус" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.cardOperatorListPage.cardOperatorFilter.cardState}"
                             styleClass="input-text" style="width: 240px;">
                <f:selectItems value="#{mainPage.cardOperatorListPage.cardOperatorFilter.cardStateFilterMenu.itemsCarOperator}" />
            </h:selectOneMenu>

            <h:outputText styleClass="output-text" escape="true" value="Дата операции" />
            <rich:calendar value="#{mainPage.cardOperatorListPage.cardOperatorFilter.startDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false" readonly="#{mainPage.cardOperatorListPage.cardOperatorFilter.showOperationsAllPeriod}" />

            <h:outputText escape="false" value="Показать операции за весь период" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.cardOperatorListPage.cardOperatorFilter.showOperationsAllPeriod}"
                                     styleClass="output-text" />

        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{mainPage.updateCardOperatorListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" oncomplete="SetWebSocket('#{mainPage.cardOperatorListPage.serverPath}')"/>
            <a4j:commandButton value="Очистить" action="#{mainPage.clearCardOperatorListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <a4j:status id="cardTableGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="cardTable" value="#{mainPage.cardOperatorListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <h:outputText escape="true" value="#{item.cardNo}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <h:outputText escape="true" value="#{item.personName}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Операция" />
            </f:facet>
            <h:outputText escape="true" value="#{item.operation}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время операции" />
            </f:facet>
            <h:outputText escape="true" value="#{item.date}" converter="timeConverter" styleClass="output-text" />
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
