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

<%-- Панель редактирования организации --%>
<h:outputText escape="true" value="Основные данные:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_basic" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgEditPage.idOfOrg}" styleClass="input-text" />
    <h:outputText escape="true" value="GUID" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.guid}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Официальное наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.officialName}" maxlength="256" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Краткое наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.shortNameInfoService}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.address}" maxlength="256" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Короткий адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.shortAddress}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тэги" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.tag}" maxlength="256" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Реквизиты:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_requisites" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.state}" styleClass="input-text">
        <f:selectItems value="#{mainPage.orgEditPage.orgStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Детализация статуса" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:selectOneMenu value="#{mainPage.orgEditPage.statusDetail}" styleClass="input-text">
            <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
            <f:selectItems value="#{mainPage.orgEditPage.statusDetails}" />
        </h:selectOneMenu>
        <h:inputTextarea rows="2" cols="64" value="#{mainPage.orgEditPage.statusTextArea}" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Очередь внедрения" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.introductionQueue}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Доп. ид. здания" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.uniqueAddressId}" maxlength="9" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНОМ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.btiUnom}" maxlength="8" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНАД" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.btiUnad}" maxlength="1" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.INN}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.OGRN}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Физическое лицо по договору" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgEditPage.officialPosition}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgEditPage.officialPersonSurname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgEditPage.officialPersonFirstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgEditPage.officialPersonSecondName}" maxlength="128" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.contractId}" maxlength="50" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.orgEditPage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Местоположение:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_placement" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Город" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.city}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Район" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.district}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Локация" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.location}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Широта" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.latitude}" maxlength="12" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Долгота" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.longitude}" maxlength="12" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Поставщик питания:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_supplier" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование ОО для поставщика" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.shortName}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgEditPage.defaultSupplier.contragentName}" readonly="true"
                     styleClass="input-text long-field" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.orgEditPage.defaultSupplierMode}" target="#{mainPage.orgEditPage.modeContragentSelect}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Соисполнитель поставщика по умолчанию" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgEditPage.coSupplier.contragentName}" readonly="true"
                     styleClass="input-text long-field" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           rendered="#{mainPage.orgEditPage.defaultSupplier != null}"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.orgEditPage.coSupplierMode}" target="#{mainPage.orgEditPage.modeContragentSelect}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.organizationType}" styleClass="input-text" style="width: 250px;">
        <f:converter converterId="organizationTypeConverter"/>
        <f:selectItems value="#{mainPage.orgEditPage.organizationTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип организации при внедрении" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.organizationTypeInitial}" styleClass="input-text" style="width: 250px;">
        <f:converter converterId="organizationTypeConverter"/>
        <f:selectItems value="#{mainPage.orgEditPage.organizationTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип пищеблока" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.refectoryType}" styleClass="input-text" style="width: 250px;">
        <f:selectItems value="#{mainPage.orgEditPage.refectoryTypeComboMenuItems}" />
    </h:selectOneMenu>

    <%-- Список категорий к которым пренадлежит организация --%>
    <h:outputText escape="true" value="Категории" styleClass="output-text" />
    <h:panelGroup>
        <a4j:commandButton id="categoryOrgAjaxButton1" value="..." action="#{mainPage.showCategoryOrgListSelectPage}" reRender="modalCategoryOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="#{mainPage.orgEditPage.idOfCategoryOrgList}" target="#{mainPage.categoryOrgFilterOfSelectCategoryOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText value="{#{mainPage.orgEditPage.filterCategoryOrg}}" styleClass="output-text"/>
    </h:panelGroup>
    <h:outputText escape="true" value="Включить товарный учет" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.changeCommodityAccounting}" styleClass="output-text">
        <a4j:support event="onclick" reRender="orgEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>

    <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" rendered="#{mainPage.orgEditPage.changeCommodityAccounting}"/>
    <h:panelGroup styleClass="borderless-div" rendered="#{mainPage.orgEditPage.changeCommodityAccounting}">
        <h:inputText value="#{mainPage.orgEditPage.configurationProviderName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.orgEditPage.showConfigurationProviderSelection}" reRender="configurationProviderSelectModalPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Идентификатор организации - источника меню" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgEditPage.menuExchangeSourceOrgName}" readonly="true" styleClass="input-text long-field"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="2" target="#{mainPage.orgSelectPage.filterMode}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Разрешить получение льгот из сверки контингента" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.changesDSZN}"  styleClass="input-text"  />
    <h:outputText id="payPlanParamLabel" escape="true" value="Включить использование плана питания" styleClass="output-text" />
    <h:selectBooleanCheckbox id="payPlanParamCheckbox" value="#{mainPage.orgEditPage.usePlanOrders}"/>
    <h:outputText id="PaydableSubscriptionFeedingLabel" escape="true" value="Включить функционал платного горячего питания" styleClass="output-text" />
    <h:selectBooleanCheckbox id="PaydableSubscriptionFeedingCheckbox" value="#{mainPage.orgEditPage.usePaydableSubscriptionFeeding}"/>
    <h:outputText escape="true" value="Включить вариативное питание" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.variableFeeding}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Пополнение через кассовый терминал" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.payByCashier}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Лимит овердрафта по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.cardLimit}" converter="copeckSumConverter" styleClass="input-text" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Настройка рассылки и уведомлений:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_notifications" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Отправитель SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.smsSender}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Стоимость SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.priceOfSms}" converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Размер абонентской платы" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.subscriptionPrice}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки отчетов по питанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReportsOnNutrition}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Список рассылки отчетов по посещению" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReportsOnVisits}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Список рассылки №1" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReports1}" styleClass="input-text long-field"/>
    <h:outputText escape="true" value="Список рассылки №2" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReports2}" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Параметры синхронизации:" styleClass="output-text-strong" />
<h:panelGrid id="orgEditGrid_sync" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.publicKey}" maxlength="1024" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Текущий номер пакета" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.idOfPacket}" maxlength="10" styleClass="input-text" />
    <h:outputText escape="true" value="Разрешить одну активную карту" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.oneActiveCard}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Уровень безопасности" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.securityLevel}" styleClass="input-text" style="width: 250px;" readonly="true" disabled="true">
        <f:converter converterId="organizationSecurityLevelConverter"/>
        <f:selectItems value="#{mainPage.orgEditPage.securityLevels}" />
    </h:selectOneMenu>
    <h:outputText id="disableEditingClientsFromAISReestrLabel" escape="true" value="Отключить создание, редактирование, удаление клиентов и их атрибутов" styleClass="output-text"/>
    <h:selectBooleanCheckbox id="disableEditingClientsFromAISReestrCheckbox" value="#{mainPage.orgEditPage.disableEditingClientsFromAISReestr}" />
    <h:outputText escape="true" value="Разрешить проведение сверки фотографий" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.photoRegistry}"  styleClass="input-text"  />
    <h:outputText id="fullSyncParamLabel" escape="true" value="Произвести полную синхронизацию" styleClass="output-text" />
    <h:selectBooleanCheckbox id="fullSyncParamCheckbox" value="#{mainPage.orgEditPage.fullSyncParam}" disabled="#{mainPage.orgEditPage.fullSyncParam}"/>
    <h:outputText escape="true" value="Главный корпус" styleClass="output-text" />
    <h:selectBooleanCheckbox disabled="#{mainPage.orgEditPage.mainBuilding}" value="#{mainPage.orgEditPage.mainBuilding}" styleClass="input-text" />

    <h:outputText escape="true" value="Корпуса организации" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-div" columns="2">
        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           rendered="#{mainPage.orgEditPage.mainBuilding}"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="#{mainPage.orgEditPage.idOfFriendlyOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <%--<h:outputText value="{#{mainPage.orgEditPage.friendlyFilterOrgs}}" styleClass="output-text"/>--%>
        <a4j:repeat value="#{mainPage.orgEditPage.friendlyOrganisation}" var="key">
            <a4j:commandLink action="#{mainPage.showOrgEditPage}" reRender="mainMenu, workspaceForm" rendered="#{!mainPage.orgEditPage.isCurrentOrg(key.idOfOrg)}">
                <h:outputText escape="true" value="#{key.shortName}" styleClass="#{mainPage.orgEditPage.getStyleClassLink(key.isMainBuilding())}" />
                <f:setPropertyActionListener value="#{key.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
            <h:outputText escape="true" value="#{key.shortName}" styleClass="#{mainPage.orgEditPage.getStyleClass(key.isMainBuilding())}"
                          rendered="#{mainPage.orgEditPage.isCurrentOrg(key.idOfOrg)}"/>
            <h:outputText escape="false" value="&nbsp;&nbsp;" styleClass="output-text" />
        </a4j:repeat>
    </h:panelGrid>
    <h:outputText escape="true" value="Адрес сервиса проведения сверки" styleClass="output-text" rendered="#{mainPage.spbRegistry}"/>
    <h:inputText value="#{mainPage.orgEditPage.registryUrl}" maxlength="256" styleClass="input-text" rendered="#{mainPage.spbRegistry}"/>
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:panelGrid id="orgEditGrid_password" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Сменить пароль для единого входа" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgEditPage.changeSsoPassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="orgEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль для единого входа" rendered="#{mainPage.orgEditPage.changeSsoPassword}"
                  styleClass="output-text" />
    <h:inputSecret value="#{mainPage.orgEditPage.plainSsoPassword}" maxlength="64"
                   rendered="#{mainPage.orgEditPage.changeSsoPassword}"
                   readonly="#{!mainPage.orgEditPage.changeSsoPassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Подтверждение пароля для единого входа"
                  rendered="#{mainPage.orgEditPage.changeSsoPassword}" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.orgEditPage.plainSsoPasswordConfirmation}" maxlength="64"
                   rendered="#{mainPage.orgEditPage.changeSsoPassword}"
                   readonly="#{!mainPage.orgEditPage.changeSsoPassword}" styleClass="input-text" />
</h:panelGrid>

<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateOrg}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showOrgEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<a4j:status id="sOrgCreateStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>