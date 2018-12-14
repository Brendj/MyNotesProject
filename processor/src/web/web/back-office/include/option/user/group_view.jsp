<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<h:panelGrid id="userGroupViewPage" binding="#{mainPage.userGroupViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя роли" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.userGroupViewPage.userName}" styleClass="input-text" />
    <h:outputText escape="true" value="Организации" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.organizationItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.organizationItems}" var="organizationItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.organizationItems)}" >
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{organizationItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.contragentItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.contragentItems}" var="contragentItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.contragentItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{contragentItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.clientItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.clientItems}" var="clientItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.clientItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{clientItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.visitorItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.visitorItems}" var="visitorItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.visitorItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{visitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Карты" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.cardItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.cardItems}" var="cardItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.cardItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{cardItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.wayBillItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.wayBillItems}" var="wayBillItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.wayBillItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{wayBillItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Сервис" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.serviceItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.serviceItems}" var="serviceItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.serviceItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{serviceItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.monitorItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.monitorItems}" var="monitorItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.monitorItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{monitorItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.repositoryItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.repositoryItems}" var="repositoryItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.repositoryItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{repositoryItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.helpdeskItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.helpdeskItems}" var="helpdeskItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.helpdeskItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{helpdeskItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Настройки" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.optionsItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.optionsItems}" var="optionsItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.optionsItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{optionsItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.onlineReportItems)}" />
    <rich:dataTable value="#{mainPage.userGroupViewPage.functionViewer.onlineReportItems}" var="onlineReportItems" rendered="#{mainPage.userGroupViewPage.functionViewer.isEmpty(mainPage.userGroupViewPage.functionViewer.onlineReportItems)}">
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{onlineReportItems.functionDesc}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showUserGroupEditPage}"
                       reRender="workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
