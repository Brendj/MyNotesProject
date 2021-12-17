<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка SMS клиентов --%>
<h:panelGrid id="clientSmsListPanel" binding="#{mainPage.clientSmsListPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Отправка" switchType="client" eventsQueue="mainFormEventsQueue" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid id="clientSmsClientFilterPanel" columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.clientSmsListPage.clientFilter.org.shortName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
        </h:panelGrid>

        <a4j:commandButton value="Отправить SMS по отрицательным балансам"
                           action="#{mainPage.sendClientNegativeBalanceSms}" reRender="clientSmsListPanel"
                           styleClass="command-button" ajaxSingle="true" />
        <a4j:commandButton value="Очистить фильтр" action="#{mainPage.clearClientSmsListPageClientFilter}"
                           reRender="clientSmsClientFilterPanel" ajaxSingle="true" styleClass="command-button" />

    </rich:simpleTogglePanel>

    <rich:simpleTogglePanel id="clientSmsListFilterPanel"
                            label="Фильтр (#{mainPage.clientSmsListPage.clientSmsFilter.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
            <h:inputText value="#{mainPage.clientSmsListPage.clientSmsFilter.idOfSms}" maxlength="32"
                         styleClass="input-text" />
            <h:outputText escape="true" value="Клиент" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.clientSmsListPage.clientSmsFilter.client.shortName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}"
                                   reRender="modalClientSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText escape="true" value="Статус доставки" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.clientSmsListPage.clientSmsFilter.deliveryStatus}"
                             styleClass="input-text">
                <f:selectItems value="#{mainPage.clientSmsListPage.clientSmsFilter.smsDeliveryFilterMenu.items}" />
            </h:selectOneMenu>

            <h:outputText escape="true" value="Начальная дата/время отправки в SMS-шлюз" styleClass="output-text" />
            <rich:calendar value="#{mainPage.clientSmsListPage.clientSmsFilter.startTime}"
                           datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter" inputClass="input-text"
                           showWeeksBar="false" />

            <h:outputText escape="true" value="Конечная дата/время отправки в SMS-шлюз" styleClass="output-text" />
            <rich:calendar value="#{mainPage.clientSmsListPage.clientSmsFilter.endTime}" datePattern="dd.MM.yyyy HH:mm"
                           converter="timeMinuteConverter" inputClass="input-text" showWeeksBar="false" />

        </h:panelGrid>

        <h:panelGrid columns="3" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{mainPage.updateClientSmsListPage}"
                               reRender="clientSmsListPanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{mainPage.clearClientSmsListPageClientSmsFilter}"
                               reRender="clientSmsListPanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <a4j:status id="clientSmsTableGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="clientSmsTable" value="#{mainPage.clientSmsListPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column, left-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfSms}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showClientViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.client.shortName}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.client.idOfClient}"
                                             target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер телефона" />
            </f:facet>
            <h:outputText escape="true" value="#{item.phone}" converter="phoneConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип содержимого" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contentsType}" converter="smsContentsTypeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Стоимость" />
            </f:facet>
            <h:outputText escape="true" value="#{item.price}" converter="copeckSumConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус доставки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.deliveryStatus}" converter="smsDeliveryStatusConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата отправки в SMS-шлюз" />
            </f:facet>
            <h:outputText escape="true" value="#{item.serviceSendDate}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата отправки клиенту" />
            </f:facet>
            <h:outputText escape="true" value="#{item.sendDate}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата доставки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.deliveryDate}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="clientSmsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
