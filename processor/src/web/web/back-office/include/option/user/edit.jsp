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

<%-- Панель редактирования пользователя --%>
<h:panelGrid id="userEditGrid" binding="#{mainPage.userEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.userName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Клиент" styleClass="output-text"/>
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.userEditPage.userClientName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.surname}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Имя" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.firstName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Отчество" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.secondName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.userEditPage.changePassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="userEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль" rendered="#{mainPage.userEditPage.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret value="#{mainPage.userEditPage.plainPassword}" maxlength="64"
                   rendered="#{mainPage.userEditPage.changePassword}"
                   readonly="#{!mainPage.userEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" rendered="#{mainPage.userEditPage.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret id="userEditorPasswordConfirmation" value="#{mainPage.userEditPage.plainPasswordConfirmation}"
                   maxlength="64" rendered="#{mainPage.userEditPage.changePassword}"
                   readonly="#{!mainPage.userEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Затребовать смену пароля при следующем входе в систему" styleClass="output-text"
                  rendered="#{mainPage.userEditPage.changePassword}"/>
    <h:selectBooleanCheckbox value="#{mainPage.userEditPage.needChangePassword}" rendered="#{mainPage.userEditPage.changePassword}" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.userEditPage.email}" maxlength="128" styleClass="input-text"/>

    <h:outputText escape="true" value="Заблокировать пользователя" styleClass="output-text" />
    <h:selectBooleanCheckbox id="blockedFlag" value="#{mainPage.userEditPage.blocked}" valueChangeListener="#{mainPage.userEditPage.blockedToChange}" >
       <a4j:support ajaxSingle="true" event="onchange" reRender="blockedUntilId"/>
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Дата окончания блокировки" styleClass="output-text" />
    <rich:calendar id="blockedUntilId" value="#{mainPage.userEditPage.blockedUntilDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" valueChangeListener="#{mainPage.userEditPage.blockedDateChange}" >
        <a4j:support ajaxSingle="true" event="onchanged" reRender="blockedFlag"/>
    </rich:calendar>
    <h:outputText escape="true" value="Роль" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.userEditPage.idOfRole}" styleClass="input-text">
        <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
        <f:selectItems value="#{mainPage.userEditPage.userRoleEnumTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Имя роли" styleClass="output-text required-field" rendered="#{mainPage.userEditPage.isDefault}"/>
    <h:inputText value="#{mainPage.userEditPage.roleName}" maxlength="128" styleClass="input-text" rendered="#{mainPage.userEditPage.isDefault}"/>

    <%--<h:outputText escape="true" value="Контрагент" styleClass="output-text required-field" rendered="#{mainPage.userEditPage.isSupplier}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.userEditPage.isSupplier}">
        <h:inputText value="#{mainPage.userEditPage.contragentItem.contragentName}" readonly="true"
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

    <h:outputText escape="true" value="Подразделение" styleClass="output-text" rendered="#{mainPage.userEditPage.isCardOperator}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.userEditPage.isCardOperator}">
        <h:inputText value="#{mainPage.userEditPage.department}" maxlength="128" styleClass="input-text" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список контрагентов" styleClass="output-text" rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                           reRender="modalContragentListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.userEditPage.contragentIds}"
                                         target="#{mainPage.contragentListSelectPage.selectedIds}" />
        </a4j:commandButton>
        <h:outputText value=" {#{mainPage.userEditPage.contragentFilter}}" styleClass="output-text" escape="true" />
    </h:panelGroup>
    <h:outputText escape="true" value="Организация" styleClass="output-text"/>
    <h:panelGroup>
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                           reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                           styleClass="command-link" style="width: 25px;" />
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userEditPage.userOrgName}}" />
    </h:panelGroup>
    <h:outputText escape="true" value="Список организаций рассылки (заявок)" styleClass="output-text" rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}" />
    <h:panelGroup rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userEditPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.userEditPage.selectOrgType}"/>
            <f:setPropertyActionListener value="#{mainPage.userEditPage.orgIds}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userEditPage.orgFilter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список организаций отмены (заказов)" styleClass="output-text" rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}" />
    <h:panelGroup rendered="#{!mainPage.userEditPage.isSecurityAdmin && !mainPage.userEditPage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userEditPage.showOrgListSelectCancelPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px">
            <f:setPropertyActionListener value="1" target="#{mainPage.userEditPage.selectOrgType}"/>
            <%--<f:setPropertyActionListener value="#{mainPage.userEditPage.orgIdsCanceled}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />--%>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userEditPage.orgFilterCanceled}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Список организаций" styleClass="output-text" rendered="#{mainPage.userEditPage.isDirector}" />
    <h:panelGroup rendered="#{mainPage.userEditPage.isDirector}">
        <a4j:commandButton value="..." action="#{mainPage.userEditPage.showOrgListPage}" reRender="modalOrgMainbuildingListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgMainbuildingListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px">
            <f:setPropertyActionListener value="#{mainPage.userEditPage.orgIds}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.userEditPage.organizationsFilter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="Регион" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.userEditPage.region}" styleClass="input-text">
        <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
        <f:selectItems value="#{mainPage.userEditPage.regions}" />
    </h:selectOneMenu>

    <h:outputText escape="true" value="Организации" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.organizationItems}" var="organizationItems" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}">
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
    <h:outputText escape="true" value="Контрагенты" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.contragentItems}" var="contragentItems" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}">
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
    <h:outputText escape="true" value="Клиенты" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.clientItems}" var="clientItems" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}">
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
    <h:outputText escape="true" value="Сотрудники" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.visitorItems}" var="visitorItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Карты" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.cardItems}" var="cardItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.wayBillItems}" var="wayBillItems" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier}">
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
    <h:outputText escape="true" value="Сервис" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.serviceItems}" var="serviceItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Мониторинг" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.monitorItems}" var="monitorItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Репозиторий отчетов" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.repositoryItems}" var="repositoryItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.helpdeskItems}" var="helpdeskItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Настройки" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.optionsItems}" var="optionsItems" rendered="#{mainPage.userEditPage.isDefault}">
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
    <h:outputText escape="true" value="Онлайн отчеты" styleClass="output-text" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier || mainPage.userEditPage.isSupplierReport}"/>
    <rich:dataTable value="#{mainPage.userEditPage.functionSelector.onlineReportItemsAll(mainPage.userEditPage.isSupplierReport)}" var="onlineReportItems" rendered="#{mainPage.userEditPage.isDefault || mainPage.userEditPage.isSupplier || mainPage.userEditPage.isSupplierReport}">
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
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateUser}" reRender="workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showUserEditPage}"
                       reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>