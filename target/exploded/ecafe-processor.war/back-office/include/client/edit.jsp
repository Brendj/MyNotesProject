<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditClients())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<f:subview id="clientDeleteSubView">
    <c:import url="include/client/confirm_client_delete.jsp" />
</f:subview>

<%-- Панель редактирования клиента --%>
<h:panelGrid id="clientEditGrid" styleClass="borderless-grid" binding="#{mainPage.clientEditPage.pageComponent}"
             columns="2">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientEditPage.org.shortName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.changePassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="clientEditGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Пароль" styleClass="output-text"
                  rendered="#{mainPage.clientEditPage.changePassword}" />
    <h:inputSecret value="#{mainPage.clientEditPage.plainPassword}" maxlength="64"
                   rendered="#{mainPage.clientEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text"
                  rendered="#{mainPage.clientEditPage.changePassword}" />
    <h:inputSecret value="#{mainPage.clientEditPage.plainPasswordConfirmation}" maxlength="64"
                   rendered="#{mainPage.clientEditPage.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.contractId}" converter="contractIdConverter"
                 maxlength="#{mainPage.clientEditPage.contractIdMaxLength}" styleClass="input-text" />
    <h:outputText escape="true" value="Статус договора" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientEditPage.contractState}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientEditPage.clientContractStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientEditPage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Физическое лицо, заключившее контракт" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.surname}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.firstName}" maxlength="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.secondName}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.contractPerson.idDocument}" maxlength="128"
                     styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Обслуживаемое физическое лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.surname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.firstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.secondName}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientEditPage.person.idDocument}" maxlength="128" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.clientEditPage.balance}" maxlength="20"
                 converter="copeckSumConverter" styleClass="input-text" />
    <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.limit}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Лимит расходов" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.expenditureLimit}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.address}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Номер мобильного телефона" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.mobile}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Уведомлять с помощью SMS" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.notifyViaSMS}" styleClass="output-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.email}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Уведомлять по электронной почте" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientEditPage.notifyViaEmail}" styleClass="output-text" />
    <h:outputText escape="true" value="Тип SMS-уведомлений (не работает!)" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientEditPage.payForSMS}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientEditPage.clientPayForSMSMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Предельное количество покупок без предъявления карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientEditPage.freePayMaxCount}" styleClass="input-text" />
    <h:outputText escape="true" value="Категории скидок" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Нет категорий" styleClass="output-text"
                      rendered="#{mainPage.clientEditPage.categoriesEmpty}" />
        <a4j:repeat value="#{mainPage.clientEditPage.categoryDiscounts}" var="categoryDiscount">
            <h:selectBooleanCheckbox value="#{categoryDiscount.selected}" styleClass="output-text" />
            <h:outputText escape="true" value="#{categoryDiscount.categoryName}" styleClass="output-text" /><br/>
        </a4j:repeat>
    </h:panelGrid>
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateClient}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showClientEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    <rich:spacer height="1" width="15"/>
    <a4j:commandButton value="Удалить"
                       onclick="#{rich:component('clientDeletePanel')}.show();"
                       styleClass="command-button" >
    </a4j:commandButton>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>