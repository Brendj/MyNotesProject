<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToCommodityAccounting())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания организации --%>
<%--@elvariable id="basicGoodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodEditPage"--%>
<%--@elvariable id="basicGoodCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodCreatePage"--%>
<h:panelGrid id="basicGoodEditGrid" binding="#{basicGoodEditPage.ap.pageComponent}" styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
    <h:panelGroup styleClass="borderless-div">
        <h:outputText value="#{basicGoodEditPage.selectedProvidersString}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
        <a4j:commandButton value="..." action="#{basicGoodEditPage.selectConfigurationProviderList}" reRender="configurationProviderSelectListModalPanel, basicGoodEditGrid"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectListModalPanel')}.show();"
                           styleClass="command-link" style="width: 25px; float: right;"
                           disabled="#{basicGoodEditPage.ap.readonly}" rendered="#{!basicGoodEditPage.ap.currentEntity.isGuidEmpty}"/>
        <a4j:commandButton value="..." action="#{basicGoodCreatePage.selectConfigurationProviderList}" reRender="configurationProviderSelectListModalPanel, basicGoodEditGrid"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectListModalPanel')}.show();"
                           styleClass="command-link" style="width: 25px; float: right;"
                           disabled="#{basicGoodEditPage.ap.readonly}" rendered="#{basicGoodEditPage.ap.currentEntity.isGuidEmpty}"/>
    </h:panelGroup>

    <h:outputText escape="true" value="GUID" styleClass="output-text long-field" rendered="#{!basicGoodEditPage.ap.createMode}"/>
    <h:inputText value="#{basicGoodEditPage.ap.currentEntity.guid}" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodEditPage.ap.createMode}"/>

    <h:outputText escape="true" value="Дата регистрации" styleClass="output-text" rendered="#{!basicGoodEditPage.ap.createMode}"/>
    <h:inputText value="#{basicGoodEditPage.ap.currentEntity.createdDate}" converter="timeConverter" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodEditPage.ap.createMode}"/>

    <h:outputText escape="true" value="Дата последнего изменения" styleClass="output-text" rendered="#{!basicGoodEditPage.ap.createMode}"/>
    <h:inputText value="#{basicGoodEditPage.ap.currentEntity.lastUpdate}" converter="timeConverter" styleClass="input-text" readonly="true" disabled="true" rendered="#{!basicGoodEditPage.ap.createMode}"/>

    <h:outputText escape="true" value="Наименование базового товара" styleClass="output-text required-field" />
    <h:inputText value="#{basicGoodEditPage.ap.currentEntity.nameOfGood}" maxlength="128" styleClass="input-text long-field" required="true" requiredMessage="Введите наименование продукта." readonly="#{basicGoodEditPage.ap.readonly}"/>

    <h:outputText escape="true" value="Единица измерения" styleClass="output-text required-field" />
    <h:selectOneListbox value="#{basicGoodEditPage.ap.currentEntity.unitsScale}" readonly="#{basicGoodEditPage.ap.readonly}">
        <f:selectItems value="#{basicGoodEditPage.unitsScaleSelectItemList}"/>
        <f:converter converterId="unitScaleConverter" />
        <a4j:support event="valueChange" reRender="basicGoodEditGrid" />
    </h:selectOneListbox>

    <h:outputText escape="true" value="Масса нетто (грамм)" styleClass="output-text required-field" />
    <h:inputText value="#{basicGoodEditPage.ap.currentEntity.netWeight}" maxlength="50" styleClass="input-text long-field" required="true" requiredMessage="Введите масса нетто (грамм)." readonly="#{basicGoodEditPage.ap.readonly}"/>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton eventsQueue="saveBasicGoodEventsQueue"  value="Создать" action="#{basicGoodEditPage.ap.create}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" rendered="#{basicGoodEditPage.ap.createMode}"/>

        <a4j:commandButton eventsQueue="saveBasicGoodEventsQueue"  value="Редактировать" action="#{basicGoodEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" rendered="#{basicGoodEditPage.ap.readonly}"/>

        <a4j:commandButton eventsQueue="saveBasicGoodEventsQueue"  value="Сохранить" action="#{basicGoodEditPage.ap.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" rendered="#{basicGoodEditPage.ap.editMode}"/>

        <a4j:commandButton value="Восстановить" action="#{basicGoodEditPage.ap.reload}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" rendered="#{basicGoodEditPage.ap.editMode}"/>

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