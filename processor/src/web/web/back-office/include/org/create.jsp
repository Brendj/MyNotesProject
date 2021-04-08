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

<%-- Панель создания организации --%>
<h:panelGrid id="orgCreateGrid" binding="#{mainPage.orgCreatePage.pageComponent}" styleClass="borderless-grid" columns="1">
<h:outputText escape="true" value="Основные данные:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_basic" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="ID в НСИ-3" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.orgIdFromNsi}" styleClass="input-text" />
    <h:outputText escape="true" value="GUID" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.guid}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="ЕКИС Id" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.ekisId}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="ЕГИССО Id" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.egissoId}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Официальное наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.officialName}" maxlength="256" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Краткое наименование" styleClass="output-text required-field" />
    <h:inputText value="#{mainPage.orgCreatePage.shortNameInfoService}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Учредитель" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.founder}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Подчиненность" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.subordination}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.address}" maxlength="256" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Короткий адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.shortAddress}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Тэги" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.tag}" maxlength="256" styleClass="input-text" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Реквизиты:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_requisites" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgCreatePage.state}" styleClass="input-text">
        <f:selectItems value="#{mainPage.orgCreatePage.orgStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Детализация статуса" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:selectOneMenu value="#{mainPage.orgCreatePage.statusDetail}" styleClass="input-text">
            <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
            <f:selectItems value="#{mainPage.orgCreatePage.statusDetails}" />
        </h:selectOneMenu>
        <h:inputTextarea rows="2" cols="64" value="#{mainPage.orgCreatePage.statusTextArea}" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Наличие ГК" styleClass="output-text"/>
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.governmentContract}" styleClass="input-text" />
    <h:outputText escape="true" value="Очередь внедрения" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.introductionQueue}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Доп. ид. здания" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.uniqueAddressId}" maxlength="9" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНОМ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.btiUnom}" maxlength="8" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНАД" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.btiUnad}" maxlength="1" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.INN}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.OGRN}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Физическое лицо по договору" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgCreatePage.officialPosition}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgCreatePage.officialPersonSurname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgCreatePage.officialPersonFirstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgCreatePage.officialPersonSecondName}" maxlength="128"
                     styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.contractId}" maxlength="50" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.orgCreatePage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Местоположение:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_placement" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Город" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.city}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Округ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.district}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Район" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.municipalDistrict}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Локация" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.location}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Широта" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.latitude}" maxlength="12" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Долгота" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.longitude}" maxlength="12" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Поставщик питания:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_supplier" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование ОО для поставщика" styleClass="output-text required-field" />
    <h:inputText value="#{mainPage.orgCreatePage.shortName}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text required-field" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgCreatePage.defaultSupplier.contragentName}" readonly="true"
                     styleClass="input-text long-field" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0"
                                         target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2"
                                         target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.orgCreatePage.defaultSupplierMode}" target="#{mainPage.orgCreatePage.modeContragentSelect}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Соисполнитель поставщика по умолчанию" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgCreatePage.coSupplier.contragentName}" readonly="true"
                     styleClass="input-text long-field" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           rendered="#{mainPage.orgCreatePage.defaultSupplier != null}"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            <f:setPropertyActionListener value="#{mainPage.orgCreatePage.coSupplierMode}" target="#{mainPage.orgCreatePage.modeContragentSelect}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgCreatePage.organizationType}" styleClass="input-text" style="width: 250px;">
        <f:converter converterId="organizationTypeConverter"/>
        <f:selectItems value="#{mainPage.orgCreatePage.organizationTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип организации при внедрении" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgCreatePage.organizationTypeInitial}" styleClass="input-text" style="width: 250px;">
        <f:converter converterId="organizationTypeConverter"/>
        <f:selectItems value="#{mainPage.orgCreatePage.organizationTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип пищеблока" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgCreatePage.refectoryType}" styleClass="input-text" style="width: 250px;">
        <f:selectItem itemValue="-1" itemLabel="-- не выставлено --" />
        <f:selectItems value="#{mainPage.orgCreatePage.refectoryTypeComboMenuItems}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Идентификатор организации - источника меню" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgCreatePage.menuExchangeSourceOrgName}" readonly="true" styleClass="input-text long-field"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="2" target="#{mainPage.orgSelectPage.filterMode}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Разрешить получение льгот из сверки контингента" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.changesDSZN}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Пополнение через кассовый терминал" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.payByCashier}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Запрет на оплату при расхождении времени" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.denyPayPlanForTimeDifference}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Лимит овердрафта по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.cardLimit}" converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Включить предзаказы" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.preordersEnabled}" styleClass="input-text" />
    <h:outputText escape="true" value="Предварительные заявки по ЛП" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.preorderlp}" styleClass="input-text"/>
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Настройки рассылки и уведомлений:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_notifications" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Отправитель SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.smsSender}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Стоимость SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.priceOfSms}" converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Размер абонентской платы" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.subscriptionPrice}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки отчетов по питанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.mailingListReportsOnNutrition}" styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки отчетов по посещению" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.mailingListReportsOnVisits}" styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки №1" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.mailingListReports1}" styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки №2" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.mailingListReports2}" styleClass="input-text" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Параметры синхронизации:" styleClass="output-text-strong" />
<h:panelGrid id="orgCreateGrid_sync" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgCreatePage.publicKey}" maxlength="1024" styleClass="input-text" />
    <h:outputText escape="true" value="Уровень безопасности" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgCreatePage.securityLevel}" styleClass="input-text" style="width: 250px;">
        <f:converter converterId="organizationSecurityLevelConverter"/>
        <f:selectItems value="#{mainPage.orgCreatePage.securityLevels}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Разрешить проведение сверки фотографий" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.photoRegistry}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Разрешить проведение сверки сотрудников" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.allowRegistryChangeEmployee}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.helpdeskEnabled}"  styleClass="input-text"  />
    <h:outputText escape="true" value="Автоматическое создание карты для клиентов с суидом" styleClass="output-text" rendered="#{mainPage.isSpb}" />
    <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.autoCreateCards}" styleClass="input-text" rendered="#{mainPage.isSpb}" />
</h:panelGrid>
    <br />
    <rich:separator />
    <br />
    <h:outputText escape="true" value="Параметры контроля доступа в здание:" styleClass="output-text-strong" />
    <h:panelGrid id="orgCreateGrid_cards" styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Разрешить один активный ЭИ" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.oneActiveCard}"  styleClass="input-text"  />
        <h:outputText escape="true" value="Цифровая подпись при регистрации ЭИ" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.needVerifyCardSign}"  styleClass="input-text"  />
        <h:outputText escape="true" value="Использовать длинные идентификаторы ЭИ" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgCreatePage.useLongCardId}" styleClass="input-text" />
    </h:panelGrid>
<br />
<rich:separator />
<br />
<h:panelGrid id="orgCreateGrid_password" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Пароль для единого входа" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.orgCreatePage.plainSsoPassword}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Подтверждение пароля для единого входа" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.orgCreatePage.plainSsoPasswordConfirmation}" maxlength="64"
                   styleClass="input-text" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать новую организацию" action="#{mainPage.createOrg}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
</h:panelGrid>