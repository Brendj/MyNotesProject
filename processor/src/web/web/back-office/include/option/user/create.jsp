<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers()) 
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания пользователя --%>
<h:panelGrid id="userCreateGrid" binding="#{mainPage.userCreatePage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.userName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Клиент" styleClass="output-text"/>
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.userCreatePage.userClientName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.surname}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Имя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.firstName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Отчество" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.secondName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userCreatePage.plainPassword}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userCreatePage.plainPasswordConfirmation}" maxlength="64"
                   styleClass="input-text" />
    <h:outputText escape="true" value="Затребовать смену пароля при первом входе в систему" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.userCreatePage.needChangePassword}" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.userCreatePage.email}" maxlength="128" styleClass="input-text"/>

    <h:outputText escape="true" value="Роль" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.userCreatePage.idOfRole}" styleClass="input-text">
        <a4j:support event="onchange" reRender="userCreateGrid" ajaxSingle="true" />
        <f:selectItems value="#{mainPage.userCreatePage.userRoleEnumTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Имя роли" styleClass="output-text required-field" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <h:inputText value="#{mainPage.userCreatePage.roleName}" maxlength="128" styleClass="input-text required-field" rendered="#{mainPage.userCreatePage.isDefault}"/>

    <%--<h:outputText escape="true" value="Контрагент" styleClass="output-text required-field" rendered="#{mainPage.userCreatePage.isSupplier}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.userCreatePage.isSupplier}">
        <h:inputText value="#{mainPage.userCreatePage.contragentItem.contragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0"
                                         target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2"
                                         target="#{mainPage.classTypes}" />
        </a4j:commandButton>
    </h:panelGroup>--%>

    <h:outputText escape="true" value="Подразделение" styleClass="output-text" rendered="#{mainPage.userCreatePage.isCardOperator}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.userCreatePage.isCardOperator}">
        <h:inputText value="#{mainPage.userCreatePage.department}" maxlength="128" styleClass="input-text" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список контрагентов" styleClass="output-text" rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                           reRender="modalContragentListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.userCreatePage.contragentIds}"
                                         target="#{mainPage.contragentListSelectPage.selectedIds}" />
        </a4j:commandButton>
        <h:outputText value=" {#{mainPage.userCreatePage.contragentFilter}}" escape="true" styleClass="output-text" />
    </h:panelGroup>

    <h:outputText escape="true" value="Организация" styleClass="output-text" rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}"/>
    <h:panelGroup rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}">
       <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                          reRender="modalOrgSelectorPanel"
                          oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                          styleClass="command-link" style="width: 25px;" />
       <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userCreatePage.userOrgName}}" />
    </h:panelGroup>


    <h:outputText escape="true" value="Список организаций рассылки (заявок)" styleClass="output-text" rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}"/>
    <h:panelGroup rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userCreatePage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.userCreatePage.selectOrgType}"/>
            <f:setPropertyActionListener value="#{mainPage.userCreatePage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userCreatePage.orgFilter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список организаций отмены (заказов)" styleClass="output-text" rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}"/>
    <h:panelGroup rendered="#{!mainPage.userCreatePage.isSecurityAdmin && !mainPage.userCreatePage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userCreatePage.showOrgListSelectCancelPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px">
            <f:setPropertyActionListener value="1" target="#{mainPage.userCreatePage.selectOrgType}"/>
            <f:setPropertyActionListener value="#{mainPage.userCreatePage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userCreatePage.orgFilterCanceled}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список организаций" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDirector}" />
    <h:panelGroup rendered="#{mainPage.userCreatePage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userCreatePage.showOrgListPage}" reRender="modalOrgMainbuildingListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgMainbuildingListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px">
            <%--<f:setPropertyActionListener value="#{mainPage.userCreatePage.orgIds}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />--%>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userCreatePage.organizationsFilter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Регион" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.userCreatePage.region}" styleClass="input-text">
        <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
        <f:selectItems value="#{mainPage.userCreatePage.regions}" />
    </h:selectOneMenu>

    <h:outputText escape="true" value="Организации" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.organizationItems}" var="organizationItems" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}">
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
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.contragentItems}" var="contragentItems" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}">
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
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.clientItems}" var="clientItems" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}">
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
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.visitorItems}" var="visitorItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Карты" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.cardItems}" var="cardItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.wayBillItems}" var="wayBillItems" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier}">
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
    <h:outputText escape="true" value="Сервис" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.serviceItems}" var="serviceItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.monitorItems}" var="monitorItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.repositoryItems}" var="repositoryItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.helpdeskItems}" var="helpdeskItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="ЕСП" styleClass="output-text" rendered="#{mainPage.userCreatePage.getIsDefaultOrAdmin}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.espItems}" var="espdeskItems" rendered="#{mainPage.userCreatePage.getIsDefaultOrAdmin}">
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
    <h:outputText escape="true" value="Настройки" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.optionsItems}" var="optionsItems" rendered="#{mainPage.userCreatePage.isDefault}">
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
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier || mainPage.userCreatePage.isSupplierReport}"/>
    <rich:dataTable value="#{mainPage.userCreatePage.functionSelector.onlineReportItemsAll(mainPage.userCreatePage.isSupplierReport)}" var="onlineReportItems" rendered="#{mainPage.userCreatePage.isDefault || mainPage.userCreatePage.isSupplier || mainPage.userCreatePage.isSupplierReport}">
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
    <a4j:commandButton value="Создать нового пользователя" action="#{mainPage.createUser}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>