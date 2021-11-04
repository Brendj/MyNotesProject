<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<h:panelGrid id="userViewPage" binding="#{mainPage.userViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.userName}" styleClass="input-text" />
    <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.surname}" styleClass="input-text" />
    <h:outputText value="Имя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.firstName}" styleClass="input-text" />
    <h:outputText value="Отчество" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.secondName}" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.phone}" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.email}" styleClass="input-text"/>
    <h:outputText escape="true" value="Дата последних изменений" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.updateTime}" styleClass="input-text"
                 converter="timeConverter" />
    <h:outputText escape="true" value="Организации" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.organizationItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.organizationItems}" var="organizationItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.organizationItems)}" >
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.contragentItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.contragentItems}" var="contragentItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.contragentItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.clientItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.clientItems}" var="clientItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.clientItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.visitorItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.visitorItems}" var="visitorItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.visitorItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Карты" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.cardItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.cardItems}" var="cardItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.cardItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.wayBillItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.wayBillItems}" var="wayBillItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.wayBillItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сервис" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.serviceItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.serviceItems}" var="serviceItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.serviceItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.monitorItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.monitorItems}" var="monitorItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.monitorItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.repositoryItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.repositoryItems}" var="repositoryItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.repositoryItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.helpdeskItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.helpdeskItems}" var="helpdeskItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.helpdeskItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Настройки" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.optionsItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.optionsItems}" var="optionsItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.optionsItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.onlineReportItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.onlineReportItems}" var="onlineReportItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.onlineReportItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Операции по картам" styleClass="output-text" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.cardOperatorItems)}" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.cardOperatorItems}" var="monitorItems" rendered="#{mainPage.userViewPage.functionViewer.isEmpty(mainPage.userViewPage.functionViewer.cardOperatorItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showUserEditPage}"
                       reRender="workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>