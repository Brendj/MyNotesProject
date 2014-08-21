<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра организации --%>
<h:panelGrid id="orgViewGrid" binding="#{mainPage.orgViewPage.pageComponent}" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.idOfOrg}" styleClass="input-text" />
    <h:outputText escape="true" value="GUID" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.guid}" styleClass="input-text" />
    <h:outputText escape="true" value="Краткое наименование" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.shortName}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Официальное наименование" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.officialName}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Тэги" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.tag}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Город" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.city}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Район" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.district}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Локация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.location}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Широта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.latitude}" maxlength="12" styleClass="input-text" />
    <h:outputText escape="true" value="Долгота" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.longitude}" maxlength="12" styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.address}" styleClass="input-text long-field" />


    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.INN}" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.OGRN}" styleClass="input-text" />

    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.phone}" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.defaultSupplierName}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.organizationType}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Тип пищеблока" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgViewPage.refectoryTypeStringRepresentation}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.contractId}" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.contractTime}" styleClass="input-text"
                 converter="timeConverter" />

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
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.state}" converter="orgStateConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Лимит овердрафта по умолчанию" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.cardLimit}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.publicKey}" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Текущий номер пакета" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.idOfPacket}" styleClass="input-text" />
    <h:outputText escape="true" value="Отправитель SMS" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.smsSender}" styleClass="input-text" />
    <h:outputText escape="true" value="Стоимость SMS" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.priceOfSms}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Размер абонентской платы" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.subscriptionPrice}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Идентификатор организации - источника меню" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.menuExchangeSourceOrgName}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Товарный учет" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.commodityAccountingParam?'Включен':'Выключен'}" styleClass="input-text" />

    <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.configurationProviderName}" styleClass="input-text" />

    <h:outputText escape="true" value="Использование плана питания" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.usePlanOrders?'Включен':'Выключен'}" styleClass="input-text" />

    <h:outputText escape="true" value="Главный корпус" styleClass="output-text" />
    <h:selectBooleanCheckbox readonly="true" disabled="true" value="#{mainPage.orgViewPage.mainBuidling}" styleClass="input-text" />

    <h:outputText escape="true" value="Корпуса организации" styleClass="output-text" />
    <h:outputText value="{#{mainPage.orgViewPage.friendlyFilterOrgs}}" styleClass="output-text"/>

    <h:outputText escape="true" value="Список рассылки отчетов по питанию" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReportsOnNutrition}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Список рассылки отчетов по посещению" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReportsOnVisits}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Список рассылки №1" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReports1}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Список рассылки №2" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgViewPage.mailingListReports2}" styleClass="input-text long-field" />

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" columns="2">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showOrgEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Синхронизация балансов" action="#{mainPage.orgViewPage.updateBalance}" styleClass="command-button" />
</h:panelGrid>
<a4j:status id="sOrgViewStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>