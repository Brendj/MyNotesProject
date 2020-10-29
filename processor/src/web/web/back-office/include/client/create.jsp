<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditClients()) 
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания клиента --%>
<h:panelGrid styleClass="borderless-grid" columns="2" id="clientCreatePanel"
             binding="#{mainPage.clientCreatePage.pageComponent}">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientCreatePage.org.shortName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.clientCreatePage.plainPassword}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Повторите пароль" styleClass="output-text" />
    <h:inputSecret value="#{mainPage.clientCreatePage.plainPasswordConfirmation}" maxlength="64"
                   styleClass="input-text" />
    <h:outputText escape="true" value="Номер лицевого счета" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-div" columns="3">
        <h:inputText value="#{mainPage.clientCreatePage.contractId}" converter="contractIdConverter"
                     maxlength="#{mainPage.clientCreatePage.contractIdMaxLength}" styleClass="input-text" />
        <h:outputText escape="true" value="Генерировать автоматически" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.clientCreatePage.autoContractId}" styleClass="output-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Статус договора" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientCreatePage.contractState}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientCreatePage.clientInitContractStateMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата заключения договора" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientCreatePage.contractTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Физическое лицо, заключившее контракт" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.contractPerson.surname}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.contractPerson.firstName}" maxlength="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.contractPerson.secondName}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.contractPerson.idDocument}" maxlength="128"
                     styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Обслуживаемое физическое лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.person.surname}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.person.firstName}" maxlength="64" styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.person.secondName}" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
        <h:inputText value="#{mainPage.clientCreatePage.person.idDocument}" maxlength="128" styleClass="input-text" />
    </h:panelGrid>

    <h:outputText escape="true" value="Группа" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.clientCreatePage.clientGroupName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientGroupSelectPage}" reRender="modalClientGroupSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" disabled="#{mainPage.clientCreatePage.org.idOfOrg == null}">
            <f:param name="idOfOrg" value="#{mainPage.clientCreatePage.org.idOfOrg}" />
            <f:setPropertyActionListener value="#{mainPage.clientCreatePage.org.idOfOrg}" target="#{mainPage.clientGroupSelectPage.idOfOrg}" />
            <f:setPropertyActionListener value="#{null}" target="#{mainPage.clientGroupSelectPage.filter}" />
        </a4j:commandButton>
    </h:panelGroup>

    <h:outputText escape="true" value="Лимит овердрафта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.limit}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Порог снижения до отправки уведомления" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.balanceToNotify}" maxlength="20" converter="copeckSumConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.address}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Номер мобильного телефона" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.mobile}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.fax}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />

    <h:outputText escape="true" value="Уведомлять с помощью SMS" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientCreatePage.notifyViaSMS}" styleClass="output-text" />
    <h:outputText escape="true" value="Уведомлять с помощью PUSH-уведомлений" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientCreatePage.notifyViaPUSH}" styleClass="output-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.email}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Уведомлять по электронной почте" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientCreatePage.notifyViaEmail}" styleClass="output-text" />
    <h:outputText escape="true" value="Тип SMS-уведомлений (не работает!)" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientCreatePage.payForSMS}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientCreatePage.clientPayForSMSMenu.items}" />
    </h:selectOneMenu>

    <h:outputText escape="true" value="Тип предоставляемой льготы" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientCreatePage.discountMode}">
        <a4j:support event="onchange" reRender="clientCategory" action="#{mainPage.clientCreatePage.changeClientCategory}"/>
        <f:selectItems value="#{mainPage.clientCreatePage.selectItemList}"/>
    </h:selectOneMenu>

    <h:outputText escape="true" value="Категории" styleClass="output-text" />

    <h:panelGroup id="clientCategory">
        <a4j:commandButton value="..." action="#{mainPage.showCategoryListSelectPage}" reRender="modalCategoryListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategoryListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" disabled="#{!mainPage.clientCreatePage.discountModeIsCategory}">
            <f:param name="fullList" value="false" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" id="categoryListFilter" escape="true" value=" {#{mainPage.clientCreatePage.filter}}" />
    </h:panelGroup>

    <h:outputText escape="true" value="СНИЛС" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.san}" maxlength="14" styleClass="input-text" />
    <%--<h:outputText escape="true" value="СНИЛС опекун" styleClass="output-text" />--%>
    <%--<h:inputText value="#{mainPage.clientCreatePage.guardsan}" maxlength="64" styleClass="input-text" />--%>
    <h:outputText escape="true" value="Внешний идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.externalId}" maxlength="64" styleClass="input-text"/>
    <h:outputText escape="true" value="Идентификатор GUID" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.clientGUID}" maxlength="64" styleClass="input-text long-field"/>
    <h:outputText escape="true" value="Пол" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.clientCreatePage.gender}" styleClass="input-text">
        <f:selectItems value="#{mainPage.clientCreatePage.clientGenderMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата рождения" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientCreatePage.birthDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Тип возрастной группы" styleClass="output-text" />
    <h:inputText value="#{mainPage.clientCreatePage.ageTypeGroup}" maxlength="128" styleClass="input-text" />

    <h:outputText escape="true" value="Особенности в питании" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.clientCreatePage.specialMenu}" styleClass="output-text" />

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea rows="5" cols="64" value="#{mainPage.clientCreatePage.remarks}" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать нового клиента" action="#{mainPage.createClient}"
                       reRender="clientCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>