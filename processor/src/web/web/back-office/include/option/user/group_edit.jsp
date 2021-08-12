<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования пользователя --%>
<h:panelGrid id="userGroupEditGrid" binding="#{mainPage.userGroupEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя роли" styleClass="output-text" />
    <h:inputText value="#{mainPage.userGroupEditPage.userName}" maxlength="64" styleClass="input-text" />

    <h:outputText escape="true" value="Организации" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.organizationItems}" var="organizationItems" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{organizationItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.contragentItems}" var="contragentItems" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{contragentItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.clientItems}" var="clientItems" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{clientItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.visitorItems}" var="visitorItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{visitorItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Карты" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.cardItems}" var="cardItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{cardItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.wayBillItems}" var="wayBillItems" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{wayBillItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сервис" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.serviceItems}" var="serviceItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{serviceItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.monitorItems}" var="monitorItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{monitorItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.repositoryItems}" var="repositoryItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{repositoryItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.helpdeskItems}" var="helpdeskItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{helpdeskItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="ЕСП" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.espItems}" var="espdeskItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{espdeskItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{espdeskItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{espdeskItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Настройки" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.optionsItems}" var="optionsItems" rendered="#{mainPage.userGroupEditPage.isDefault}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{optionsItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier || mainPage.userGroupEditPage.isSupplierReport}"/>
    <rich:dataTable value="#{mainPage.userGroupEditPage.functionSelector.onlineReportItems}" var="onlineReportItems" rendered="#{mainPage.userGroupEditPage.isDefault || mainPage.userGroupEditPage.isSupplier || mainPage.userEditPage.isSupplierReport}">
        <rich:column>
            <h:selectBooleanCheckbox value="#{onlineReportItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateUserGroup}" reRender="workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showUserGroupEditPage}"
                       reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>