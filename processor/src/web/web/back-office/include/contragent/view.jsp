<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра контрагента --%>
<h:panelGrid id="contragentViewGrid" binding="#{mainPage.contragentViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentViewPage.idOfContragent}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Название" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.contragentName}" styleClass="input-text" />
    <h:outputText escape="true" value="Класс" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.classId}" converter="contragentClassConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Контактное лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentViewPage.contactPerson.surname}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentViewPage.contactPerson.firstName}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentViewPage.contactPerson.secondName}" readonly="true"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.contragentViewPage.title}" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.address}" styleClass="input-text" />
    <h:outputText escape="true" value="Офисный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.phone}" styleClass="input-text" />
    <h:outputText escape="true" value="Мобильный телефон" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.phone}" styleClass="input-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.email}" styleClass="input-text" />
    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.fax}" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.inn}" styleClass="input-text" />
    <h:outputText escape="true" value="Банк" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.bank}" styleClass="input-text" />
    <h:outputText escape="true" value="БИК" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.bic}" styleClass="input-text" />
    <h:outputText escape="true" value="Коррсчет" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.corrAccount}" styleClass="input-text" />
    <h:outputText escape="true" value="Счет" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.account}" styleClass="input-text" />
    <h:outputText escape="true" value="КПП" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentViewPage.kpp}" maxlength="10" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentViewPage.ogrn}" maxlength="15" styleClass="input-text" />
    <h:outputText escape="true" value="ОКАТО" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentViewPage.okato}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="ОКТMО" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentViewPage.oktmo}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (RSA)" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.publicKey}" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (ГОСТ) - контейнер" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.publicKeyGOSTAlias}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Переводить номер счета" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.contragentViewPage.needAccountTranslate}" disabled="true" readonly="true"
                             styleClass="output-text" />
    <h:outputText escape="true" value="Дата регистрации" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.createTime}" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Дата последнего изменения" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.updateTime}" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки сводного отчета по заявкам" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.requestNotifyMailList}" styleClass="input-text" />
    <h:outputText escape="true" value="Список рассылки по отмененным заказам" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.contragentViewPage.orderNotifyMailList}" styleClass="input-text" />

    <h:outputText escape="true" value="Платежный контрагент по умолчанию" styleClass="output-text" rendered="#{mainPage.contragentViewPage.TSP}" />
    <h:inputText value="#{mainPage.contragentViewPage.defaultPayContragent}" readonly="true" styleClass="input-text"  rendered="#{mainPage.contragentViewPage.TSP}" />

    <h:outputText escape="true" value="Пополнение через кассовый терминал" styleClass="output-text" rendered="#{mainPage.contragentViewPage.TSP}" />
    <h:selectBooleanCheckbox value="#{mainPage.contragentViewPage.payByCashier}" disabled="true" styleClass="input-text" rendered="#{mainPage.contragentViewPage.TSP}" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea readonly="true" rows="5" cols="64" value="#{mainPage.contragentViewPage.remarks}"
                     styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" rendered="#{mainPage.contragentViewPage.hasRnip()}">
    <h:outputText escape="true" value="Лог взаимодействия с каталогом РНиП (последние 10 записей)" styleClass="output-text" />
    <h:outputText escape="false" value="#{mainPage.contragentViewPage.rnipLogEdit}" styleClass="output-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" rendered="#{mainPage.contragentViewPage.hasRnip()}">
    <h:outputText escape="true" value="Лог экспорта платежей из РНиП (последние 10 записей)" styleClass="output-text" />
    <h:outputText escape="false" value="#{mainPage.contragentViewPage.rnipLogPayment}" styleClass="output-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showContragentEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>