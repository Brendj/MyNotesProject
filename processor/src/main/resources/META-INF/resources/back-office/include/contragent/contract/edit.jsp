<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditOrgs())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания/редактирования/просмотра котрактов --%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractEditPage"--%>
<h:panelGrid id="contractEditGrid" binding="#{contractEditPage.ap.pageComponent}" styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
    <h:outputText escape="true" value="#{contractEditPage.ap.contragentFilter.contragent.contragentName}" styleClass="output-text" rendered="#{contractEditPage.ap.readonly}" />
    <h:panelGroup styleClass="borderless-div" rendered="#{!contractEditPage.ap.readonly}">
        <h:inputText value="#{contractEditPage.ap.contragentFilter.contragent.contragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
        </a4j:commandButton>
    </h:panelGroup>

    <h:outputText escape="true" value="Исполнитель" styleClass="output-text required-field" />
    <h:inputText value="#{contractEditPage.ap.currentEntity.performer}" maxlength="128" styleClass="input-text long-field" readonly="#{contractEditPage.ap.readonly}"/>
    <h:outputText escape="true" value="Заказчик" styleClass="output-text required-field" />
    <h:inputText value="#{contractEditPage.ap.currentEntity.customer}" maxlength="128" styleClass="input-text long-field" readonly="#{contractEditPage.ap.readonly}"/>
    <h:outputText escape="true" value="Номер" styleClass="output-text required-field" />
    <h:inputText value="#{contractEditPage.ap.currentEntity.contractNumber}" maxlength="50" styleClass="input-text long-field" readonly="#{contractEditPage.ap.readonly}"/>

    <h:outputText escape="true" value="Статус" styleClass="output-text required-field" />
    <h:selectOneMenu value="#{contractEditPage.ap.currentEntity.contractState}" styleClass="input-text" readonly="#{contractEditPage.ap.readonly}">
        <f:selectItem itemLabel="Не активен" itemValue="false"/>
        <f:selectItem itemLabel="Активен" itemValue="true"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата заключения" styleClass="output-text required-field" />
    <rich:calendar value="#{contractEditPage.ap.currentEntity.dateOfConclusion}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false"
                   readonly="#{contractEditPage.ap.readonly}"/>
    <h:outputText escape="true" value="Срок действия" styleClass="output-text required-field"/>
    <rich:calendar value="#{contractEditPage.ap.currentEntity.dateOfClosing}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false"
                   readonly="#{contractEditPage.ap.readonly}"/>

    <h:outputText escape="true" value="Организации" styleClass="output-text" />

    <h:panelGrid columns="2">
        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" eventsQueue="modalOrgListSelectorPanelEventsQueue"
                           rendered="#{!contractEditPage.ap.readonly}">
            <f:setPropertyActionListener value="#{contractEditPage.ap.stringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" id="contractEditFilter" escape="true" value=" {#{contractEditPage.ap.currentEntity.orgNames}}" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton eventsQueue="saveContractEventsQueue"  value="Создать" action="#{contractEditPage.ap.create}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button"
                           rendered="#{contractEditPage.ap.createMode}"/>

        <a4j:commandButton eventsQueue="saveContractEventsQueue"  value="Редактировать" action="#{contractEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button"
                           rendered="#{contractEditPage.ap.readonly}"/>

        <a4j:commandButton eventsQueue="saveContractEventsQueue"  value="Сохранить" action="#{contractEditPage.ap.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button"
                           rendered="#{contractEditPage.ap.editMode}"/>

        <a4j:commandButton value="Восстановить" action="#{contractEditPage.ap.reload}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button"
                           rendered="#{contractEditPage.ap.editMode}"/>

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
