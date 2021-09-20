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

<%-- Панель создания пользователя --%>
<h:panelGrid id="userGroupCreateGrid" binding="#{mainPage.userGroupCreatePage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя роли" styleClass="output-text" />
    <h:inputText value="#{mainPage.userGroupCreatePage.roleName}" maxlength="64" styleClass="input-text" />

    <h:outputText escape="true" value="Организации" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.organizationItems}" var="organizationItems">
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
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.contragentItems}" var="contragentItems">
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
    <h:outputText escape="true" value="Клиенты" styleClass="output-text"/>
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.clientItems}" var="clientItems">
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
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text"/>
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.visitorItems}" var="visitorItems">
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
    <h:outputText escape="true" value="Карты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.cardItems}" var="cardItems">
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
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.wayBillItems}" var="wayBillItems">
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
    <h:outputText escape="true" value="Сервис" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.serviceItems}" var="serviceItems">
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
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.monitorItems}" var="monitorItems">
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
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.repositoryItems}" var="repositoryItems">
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
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.helpdeskItems}" var="helpdeskItems">
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
    <h:outputText escape="true" value="ЕСП" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.espItems}" var="espItems">
        <rich:column>
            <h:selectBooleanCheckbox value="#{espItems.selected}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{espItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{espItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Настройки" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.optionsItems}" var="optionsItems">
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
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userGroupCreatePage.functionSelector.onlineReportItems}" var="onlineReportItems">
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
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать новую роль" action="#{mainPage.userGroupCreatePage.createGroup}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>