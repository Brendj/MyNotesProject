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
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.phone}" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.email}" styleClass="input-text"/>
    <h:outputText escape="true" value="Дата последних изменений" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userViewPage.updateTime}" styleClass="input-text"
                 converter="timeConverter" />
    <h:outputText escape="true" value="Права пользователя" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.items}" var="item">
        <rich:column>
            <h:outputText escape="true" value="#{item.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Организации" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.organizationItems}" var="organizationItems">
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.contragentItems}" var="contragentItems">
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.clientItems}" var="clientItems">
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.visitorItems}" var="visitorItems">
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Карты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.cardItems}" var="cardItems">
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.wayBillItems}" var="wayBillItems">
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сервис" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.serviceItems}" var="serviceItems">
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.monitorItems}" var="monitorItems">
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.repositoryItems}" var="repositoryItems">
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Настройки" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.optionsItems}" var="optionsItems">
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" />
    <rich:dataTable value="#{mainPage.userViewPage.functionViewer.onlineReportItems}" var="onlineReportItems">
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showUserEditPage}"
                       reRender="workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
