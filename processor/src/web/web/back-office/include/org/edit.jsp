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
<h:panelGrid id="orgEditGrid" binding="#{mainPage.orgEditPage.pageComponent}" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.orgEditPage.idOfOrg}" styleClass="input-text" />
    <h:outputText escape="true" value="Краткое наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.shortName}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Официальное наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.officialName}" maxlength="128" styleClass="input-text" />

    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.INN}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.OGRN}" maxlength="32" styleClass="input-text" />

    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.address}" maxlength="128" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgEditPage.defaultSupplier.contragentName}" readonly="true"
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
    </h:panelGroup>
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.contractId}" maxlength="50" styleClass="input-text" />
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.orgEditPage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
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
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.orgEditPage.state}" styleClass="input-text">
        <f:selectItems value="#{mainPage.orgEditPage.orgStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Лимит овердрафта по умолчанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.cardLimit}" converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.publicKey}" maxlength="1024" styleClass="input-text long-field" />
    <h:outputText escape="true" value="Текущий номер пакета" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.idOfPacket}" maxlength="10" styleClass="input-text" />
    <h:outputText escape="true" value="Отправитель SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.smsSender}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Стоимость SMS" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.priceOfSms}" converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Размер абонентской платы" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.subscriptionPrice}" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Идентификатор организации - источника меню" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.orgEditPage.menuExchangeSourceOrgName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>

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

    <h:outputText escape="true" value="Список рассылки отчетов по питанию" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReportsOnNutrition}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Список рассылки отчетов по посещению" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReportsOnVisits}" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Список рассылки №1" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReports1}" styleClass="input-text long-field"/>

    <h:outputText escape="true" value="Список рассылки №2" styleClass="output-text" />
    <h:inputText value="#{mainPage.orgEditPage.mailingListReports2}" styleClass="input-text long-field" />

</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateOrg}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showOrgEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>