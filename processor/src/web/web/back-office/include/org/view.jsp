<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Панель просмотра организации --%>
<h:outputText escape="true" value="Основные данные:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_basic" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.idOfOrg}" styleClass="input-text" />
    <h:outputText escape="true" value="ID в НСИ-3" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.orgIdFromNsi}" styleClass="input-text" />
    <h:outputText escape="true" value="GUID" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.guid}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="ЕКИС Id" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.ekisId}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="ЕГИССО Id" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.egissoId}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Официальное наименование" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.officialName}" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Краткое наименование" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.shortNameInfoService}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Учредитель" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.founder}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Подчиненность" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.subordination}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.address}" styleClass="input-text" size="150" />
    <h:outputText escape="true" value="Короткий адрес" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.shortAddress}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тэги" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.tag}" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Реквизиты:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_requisites" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.state}" converter="orgStateConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Детализация статуса" styleClass="output-text" />
    <h:panelGrid  styleClass="borderless-grid" columns="2">
        <h:selectOneMenu readonly="true" value="#{mainPage.orgViewPage.statusDetail}" styleClass="input-text">
            <a4j:support event="onchange" reRender="userEditGrid" ajaxSingle="true" />
            <f:selectItems value="#{mainPage.orgViewPage.statusDetails}" />
        </h:selectOneMenu>
        <h:inputTextarea readonly="true" rows="2" cols="64" value="#{mainPage.orgViewPage.statusTextArea}" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Наличие ГК" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.governmentContract}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Очередь внедрения" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.introductionQueue}" styleClass="input-text" />
    <h:outputText escape="true" value="Доп. ид. здания" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.uniqueAddressId}" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНОМ" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.btiUnom}" styleClass="input-text" />
    <h:outputText escape="true" value="БТИ УНАД" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.btiUnad}" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.INN}" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.OGRN}" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.phone}" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Физическое лицо по договору" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgViewPage.officialPosition}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgViewPage.officialPersonSurname}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgViewPage.officialPersonFirstName}" readonly="true" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.orgViewPage.officialPersonSecondName}" readonly="true" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="В каком межрайонном совете состоит ОО" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.interdistrictCouncil}" styleClass="input-text" />
    <h:outputText escape="true" value="Председателем какого МРС является руководитель ОО" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.interdistrictCouncilChief}" styleClass="input-text" />
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.contractId}" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.contractTime}" styleClass="input-text"
                 converter="timeConverter" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Местоположение:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_placement" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Город" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.city}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Округ" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.district}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Район" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.municipalDistrict}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Локация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.location}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Широта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.latitude}" maxlength="12" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Долгота" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.longitude}" maxlength="12" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Поставщик питания:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_supplier" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование ОО для поставщика" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.shortName}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.defaultSupplierName}" readonly="true" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Соисполнитель поставщика по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.coSupplierName}" readonly="true" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.organizationType}" readonly="true" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тип организации при внедрении" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.organizationTypeInitial}" readonly="true" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тип пищеблока" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.refectoryTypeStringRepresentation}" readonly="true" styleClass="input-text long-field" />

    <%-- Список категорий к которым пренадлежит организация,
    если организация не пренадлежит к категории выводится соответствующее сообщение --%>
    <h:outputText escape="true" value="Категории" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText value="Организация не принадлежит ни к одной категории" escape="true" styleClass="output-text" rendered="#{empty mainPage.orgViewPage.categoryOrg}"/>
        <rich:dataTable value="#{mainPage.orgViewPage.categoryOrg}" var="category"
                        rendered="#{not empty mainPage.orgViewPage.categoryOrg}">
           <rich:column>
               <h:outputText value="#{category.categoryName}" escape="true" styleClass="output-text" />
           </rich:column>
        </rich:dataTable>
    </h:panelGrid>
    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.commodityAccountingParam?'Включен':'Выключен'}" styleClass="input-text" />
    <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.configurationProviderName}" styleClass="input-text" />
    <h:outputText escape="true" value="Идентификатор организации - источника меню" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.menuExchangeSourceOrgName}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Использование плана питания" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.usePlanOrders?'Включен':'Выключен'}" styleClass="input-text" />
    <h:outputText escape="true" value="Использование расписания питания" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.useMealSchedule?'Включен':'Выключен'}" styleClass="input-text" />
    <h:outputText escape="true" value="Включить функционал платного горячего питания" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.usePaydableSubscriptionFeeding}"  styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Включить вариативное питание" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.variableFeeding}"  styleClass="input-text" disabled="true"  />
    <h:outputText escape="true" value="Пополнение через кассовый терминал" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.payByCashier}"  styleClass="input-text" disabled="true"  />
    <h:outputText escape="true" value="Реализация невостребованных порций" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.recyclingEnabled}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Запрет на оплату при расхождении времени" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.denyPayPlanForTimeDifference}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Лимит овердрафта по умолчанию" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.cardLimit}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Включить предзаказы" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.preordersEnabled}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Предварительные заявки по ЛП" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.preorderlp}" styleClass="input-text" disabled="true" />

</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Настройки рассылки и уведомлений:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_notifications" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Отправитель SMS" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.smsSender}" styleClass="input-text" />
    <h:outputText escape="true" value="Стоимость SMS" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.priceOfSms}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Размер абонентской платы" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.subscriptionPrice}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки отчетов по питанию" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReportsOnNutrition}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Список рассылки отчетов по посещению" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReportsOnVisits}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Список рассылки №1" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReports1}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Список рассылки №2" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReports2}" styleClass="input-text long-field" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Параметры синхронизации:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_sync" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.publicKey}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Текущий номер пакета" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.idOfPacket}" styleClass="input-text" />
    <h:outputText escape="true" value="Уровень безопасности" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.securityLevel}" styleClass="input-text long-field" />
    <h:outputText id="disableEditingClientsFromAISReestrLabel" escape="true" value="Разрешить проведение сверки контингента" styleClass="output-text"/>
    <h:selectBooleanCheckbox id="disableEditingClientsFromAISReestrCheckbox" value="#{mainPage.orgViewPage.disableEditingClientsFromAISReestr}" disabled="true" />
    <h:outputText escape="true" value="Разрешить получение льгот из сверки контингента" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.changesDSZN}"  styleClass="input-text" readonly="true" disabled="true"  />
    <h:outputText escape="true" value="Разрешить проведение сверки фотографий" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.photoRegistry}"  styleClass="input-text" disabled="true"  />
    <h:outputText escape="true" value="Разрешить проведение сверки сотрудников" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.allowRegistryChangeEmployee}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Служба помощи" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.helpdeskEnabled}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Главный корпус" styleClass="output-text" />
    <h:selectBooleanCheckbox readonly="true" disabled="true" value="#{mainPage.orgViewPage.mainBuidling}" styleClass="input-text" />
    <h:outputText escape="true" value="Корпуса организации" styleClass="output-text" />
    <a4j:repeat value="#{mainPage.orgViewPage.friendlyOrganisation}" var="key">
        <a4j:commandLink action="#{mainPage.showOrgViewPage}" reRender="mainMenu, workspaceForm" rendered="#{!mainPage.orgViewPage.isCurrentOrg(key.idOfOrg)}">
            <h:outputText escape="true" value="#{key.shortName}" styleClass="#{mainPage.orgViewPage.getStyleClassLink(key.isMainBuilding())}" />
            <f:setPropertyActionListener value="#{key.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </a4j:commandLink>
        <h:outputText escape="true" value="#{key.shortName}" styleClass="#{mainPage.orgViewPage.getStyleClass(key.isMainBuilding())}"
                      rendered="#{mainPage.orgViewPage.isCurrentOrg(key.idOfOrg)}"/>
        <h:outputText escape="false" value="&nbsp;&nbsp;" styleClass="output-text" />
    </a4j:repeat>
    <h:outputText escape="true" value="Использовать Web-АРМ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.useWebArm}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Функционал проверки даты в заявке на питание" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.goodDateCheck}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Адрес сервиса проведения сверки" styleClass="output-text" rendered="#{mainPage.spbRegistry}"/>
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.registryUrl}" maxlength="256" styleClass="input-text" rendered="#{mainPage.spbRegistry}"/>
    <h:outputText escape="true" value="Автоматическое создание карты для клиентов с суидом" styleClass="output-text" rendered="#{mainPage.isSpb}" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.autoCreateCards}" styleClass="input-text" disabled="true" rendered="#{mainPage.isSpb}" />
</h:panelGrid>
<br />
<rich:separator />
<br />
<h:outputText escape="true" value="Параметры контроля доступа в здание:" styleClass="output-text-strong" />
<h:panelGrid id="orgViewGrid_cards" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Разрешить один активный ЭИ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.oneActiveCard}"  styleClass="input-text" disabled="true"  />
    <h:outputText escape="true" value="Цифровая подпись при регистрации ЭИ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.needVirifyCardSign}"  styleClass="input-text" disabled="true"  />
    <h:outputText escape="true" value="Здание работает в летний период" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.workInSummerTime}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Заявки на посещение других ОО" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.requestForVisitsToOtherOrg}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Использование обучающимися нескольких ЭИ в ОО" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.multiCardModeEnabled}" styleClass="input-text" disabled="true" />
    <h:outputText escape="true" value="Использовать длинные идентификаторы ЭИ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.orgViewPage.useLongCardNo}" styleClass="input-text" disabled="true" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" columns="2">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showOrgEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
<a4j:status id="sOrgViewStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>