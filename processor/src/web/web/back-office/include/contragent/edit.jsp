<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditContragents())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования контрагента --%>
<h:panelGrid id="contragentEditGrid" binding="#{mainPage.contragentEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.idOfContragent}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Название" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.contragentName}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Класс" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.contragentEditPage.classId}" styleClass="input-text">
        <f:selectItems value="#{mainPage.contragentEditPage.contragentClassMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Контактное лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentEditPage.contactPerson.surname}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentEditPage.contactPerson.firstName}" maxlength="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentEditPage.contactPerson.secondName}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentEditPage.title}" maxlength="30" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.address}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Офисный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.phone}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Мобильный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.phone}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.email}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.fax}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.inn}" maxlength="90" styleClass="input-text" />
    <h:outputText escape="true" value="Банк" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.bank}" maxlength="90" styleClass="input-text" />
    <h:outputText escape="true" value="БИК" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.bic}" maxlength="15" styleClass="input-text" />
    <h:outputText escape="true" value="Коррсчет" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.corrAccount}" maxlength="20" styleClass="input-text" />
    <h:outputText escape="true" value="Счет" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.account}" maxlength="20" styleClass="input-text" />
    <h:outputText escape="true" value="КПП" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.kpp}" maxlength="10" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.ogrn}" maxlength="15" styleClass="input-text" />
    <h:outputText escape="true" value="ОКАТО" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.okato}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="ОКТMО" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.oktmo}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (RSA)" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.publicKey}" maxlength="1024" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (ГОСТ) - контейнер" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.publicKeyGOSTAlias}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Переводить номер счета" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.contragentEditPage.needAccountTranslate}" styleClass="output-text" />
    <h:outputText escape="true" value="Список рассылки сводного отчета по заявкам" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.requestNotifyMailList}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки по отмененным заказам" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentEditPage.orderNotifyMailList}" styleClass="input-text" />


    <h:outputText escape="true" value="Платежный контрагент по умолчанию" styleClass="output-text" rendered="#{mainPage.contragentEditPage.TSP}" />

    <h:panelGroup styleClass="borderless-div"   rendered="#{mainPage.contragentEditPage.TSP}" >
        <h:inputText value="#{mainPage.contragentEditPage.defaultPayContragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
            <f:setPropertyActionListener value="1" target="#{mainPage.classTypes}" />
        </a4j:commandButton>
        <a4j:commandButton value="X" action="#{mainPage.contragentEditPage.cancelContragentSelection}"
                           reRender="contragentEditGrid" rendered="#{mainPage.contragentEditPage.defaultPayContragentName!=null}"/>
    </h:panelGroup>

    <h:outputText escape="true" value="Пополнение через кассовый терминал" styleClass="output-text" rendered="#{mainPage.contragentEditPage.TSP}" />
    <h:selectBooleanCheckbox value="#{mainPage.contragentEditPage.payByCashier}"  styleClass="input-text" rendered="#{mainPage.contragentEditPage.TSP}" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea rows="5" cols="64" value="#{mainPage.contragentEditPage.remarks}" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateContragent}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" status="reportGenerateStatus" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showContragentEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<a4j:status id="reportGenerateStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>