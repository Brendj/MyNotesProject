<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script>
    var socket = new WebSocket("ws://localhost:8001");
    socket.onmessage = function(event) {
        var incomingMessage = event.data;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:cardNoByCardReader").value = incomingMessage;
        var button = document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:refreshCardsList");
        if (button != null) {
            button.click();
        }
    };
    function SaveFile(message)
    {
        var mes = message;
        if (mes != '') {
            socket.send(mes);
        }
        return false;
    }
    function disableButtons(value) {
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clientButtonCardShow").disabled=value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:applyButtonCardShow").disabled=value;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clearButtonCardShow").disabled=value;
        document.getElementById("clientSelectSubView:modalClientSelectorForm:orgButtonSelectClient").disabled=value;
        document.getElementById("clientSelectSubView:modalClientSelectorForm:applyButtonSelectClient").disabled=value;
        document.getElementById("clientSelectSubView:modalClientSelectorForm:clearButtonSelectClient").disabled=value;
    }
</script>

<%--@elvariable id="cardOperatorPage" type="ru.axetta.ecafe.processor.web.ui.cardoperator.CardOperatorPage"--%>
<h:panelGrid id="cardOperatorGrid" binding="#{cardOperatorPage.pageComponent}" styleClass="borderless-grid">
    <a4j:commandButton id="refreshCardsList" action="#{cardOperatorPage.onCardRead}"  reRender="cardOperatorTable, cardOperatorPageClient"
                 style="display: none;"/>
    <h:inputText value="#{cardOperatorPage.cardNo}" converter="cardNoConverter" maxlength="10" id="cardNoByCardReader"
                 style="display: none;"  />
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Клиент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..." action="#{mainPage.showEmptyClientSelectPage}" reRender="modalClientSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();disableButtons(false);"
                               styleClass="command-link" style="width: 25px;" id="clientButtonCardShow"
                               onclick="disableButtons(true);"/>
            <h:inputText value="#{cardOperatorPage.client.shortNameContractId}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" id="cardOperatorPageClient" />
        </h:panelGroup>


        <a4j:commandButton value="Применить" action="#{cardOperatorPage.applyClient}" id="applyButtonCardShow"
                           reRender="workspaceTogglePanel" styleClass="command-button"
                           onclick="disableButtons(true);" oncomplete="disableButtons(false)"/>
        <a4j:commandButton value="Очистить" action="#{cardOperatorPage.clearCardOperatorPage}" id="clearButtonCardShow"
                           reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button"
                           onclick="disableButtons(true);" oncomplete="disableButtons(false)"/>

    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <rich:dataTable id="cardOperatorTable" value="#{cardOperatorPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column,
                    left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер, нанесённый на карту" />
            </f:facet>
            <h:outputText escape="true" value="#{item.cardPrintedNo}" converter="cardNoConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <h:outputText escape="true" value="#{item.client.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" converter="cardStateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Печать" />
            </f:facet>

            <a4j:commandButton image="/images/16x16/print.png"
                               oncomplete="SaveFile('#{item.message}')">
            </a4j:commandButton>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Заблокировать" />
            </f:facet>
            <a4j:commandLink action="#{cardOperatorPage.showBlockCardPanel}" styleClass="command-link"
                             reRender="cardOperatorTable"
                             rendered="#{item.canBeBlocked}">
                <h:graphicImage value="/images/16x16/stop.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{cardOperatorPage.selectedItem}" />
            </a4j:commandLink>
            <h:panelGrid styleClass="borderless-grid" columns="4" rendered="#{item.lockingNow}">
                <h:outputText escape="true" value="Причина блокировки: " styleClass="output-text" />
                <%--<h:inputText value="#{item.lockReason}" styleClass="input-text" />--%>
                <h:selectOneMenu value="#{item.lockReasonState}" styleClass="input-text">
                    <f:selectItems value="#{cardOperatorPage.cardLockReasonMenu.items}" />
                </h:selectOneMenu>
                <a4j:commandButton value="Заблокировать" action="#{cardOperatorPage.blockCard}"
                                   reRender="cardOperatorTable" styleClass="command-button">
                    <f:setPropertyActionListener value="#{item}" target="#{cardOperatorPage.selectedItem}" />
                </a4j:commandButton>
                <a4j:commandButton value="Отмена" action="#{cardOperatorPage.hideBlockCardPanel}"
                                   reRender="cardOperatorTable" styleClass="command-button">
                    <f:setPropertyActionListener value="#{item}" target="#{cardOperatorPage.selectedItem}" />
                </a4j:commandButton>
            </h:panelGrid>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardOperatorTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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