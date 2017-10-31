<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToCommodityAccounting())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%--@elvariable id="basicGoodCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodCreatePage"--%>
<h:panelGrid id="basicGoodCreateGrid" binding="#{basicGoodCreatePage.ap.pageComponent}" styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
    <h:panelGroup styleClass="borderless-div">
        <h:outputText value="#{basicGoodCreatePage.selectedProvidersString}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
        <a4j:commandButton value="..." action="#{basicGoodCreatePage.selectConfigurationProviderList}" reRender="configurationProviderSelectListModalPanel, basicGoodCreateGrid"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectListModalPanel')}.show();"
                           styleClass="command-link" style="width: 25px; float: right;"
                           disabled="#{basicGoodCreatePage.ap.readonly}" />
    </h:panelGroup>

    <h:outputText escape="true" value="GUID" styleClass="output-text long-field" rendered="#{!basicGoodCreatePage.ap.createMode}"/>
    <h:inputText value="#{basicGoodCreatePage.ap.currentEntity.guid}" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodCreatePage.ap.createMode}"/>

    <h:outputText escape="true" value="Дата регистрации" styleClass="output-text" rendered="#{!basicGoodCreatePage.ap.createMode}"/>
    <h:inputText value="#{basicGoodCreatePage.ap.currentEntity.createdDate}" converter="timeConverter" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodCreatePage.ap.createMode}"/>

    <h:outputText escape="true" value="Дата последнего изменения" styleClass="output-text" rendered="#{!basicGoodCreatePage.ap.createMode}"/>
    <h:inputText value="#{basicGoodCreatePage.ap.currentEntity.lastUpdate}" converter="timeConverter" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodCreatePage.ap.createMode}"/>

    <h:outputText escape="true" value="Наименование базового товара" styleClass="output-text required-field" />
    <h:inputText value="#{basicGoodCreatePage.ap.currentEntity.nameOfGood}" maxlength="128" styleClass="input-text long-field" required="true" requiredMessage="Введите наименование продукта." readonly="#{basicGoodCreatePage.ap.readonly}"/>

    <h:outputText escape="true" value="Единица измерения" styleClass="output-text required-field" />
    <h:selectOneListbox value="#{basicGoodCreatePage.ap.currentEntity.unitsScale}" readonly="#{basicGoodCreatePage.ap.readonly}">
        <f:selectItems value="#{basicGoodCreatePage.unitsScaleSelectItemList}"/>
        <f:converter converterId="unitScaleConverter" />
        <a4j:support event="valueChange" reRender="basicGoodCreateGrid" />
    </h:selectOneListbox>

    <h:outputText escape="true" value="Масса нетто (грамм)" styleClass="output-text required-field" />
    <h:inputText value="#{basicGoodCreatePage.ap.currentEntity.netWeight}" maxlength="50" styleClass="input-text long-field" required="true" requiredMessage="Введите масса нетто (грамм)." readonly="#{basicGoodCreatePage.ap.readonly}"/>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton eventsQueue="saveBasicGoodEventsQueue"  value="Создать" action="#{basicGoodCreatePage.ap.create}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" rendered="#{basicGoodCreatePage.ap.createMode}"/>

    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>