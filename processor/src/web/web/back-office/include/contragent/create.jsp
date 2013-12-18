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

<%-- Панель создания контрагента --%>
<h:panelGrid id="contragentCreateGrid" binding="#{mainPage.contragentCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Имя контрагента" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.contragentName}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Класс" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.contragentCreatePage.classId}" styleClass="input-text">
        <f:selectItems value="#{mainPage.contragentCreatePage.contragentClassMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Контактное лицо" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentCreatePage.contactPerson.surname}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Имя" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentCreatePage.contactPerson.firstName}" maxlength="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentCreatePage.contactPerson.secondName}" maxlength="128"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Должность" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentCreatePage.title}" maxlength="30" styleClass="input-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Адрес" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.address}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Офисный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.phone}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Мобильный телефон" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.phone}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Электронная почта" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.email}" maxlength="128" styleClass="input-text" />
    <h:outputText escape="true" value="Факс" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.fax}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="ИНН" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.inn}" maxlength="90" styleClass="input-text" />
    <h:outputText escape="true" value="Банк" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.bank}" maxlength="90" styleClass="input-text" />
    <h:outputText escape="true" value="БИК" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.bic}" maxlength="15" styleClass="input-text" />
    <h:outputText escape="true" value="Коррсчет" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.corrAccount}" maxlength="20" styleClass="input-text" />
    <h:outputText escape="true" value="Счет" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.account}" maxlength="20" styleClass="input-text" />
    <h:outputText escape="true" value="КПП" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.kpp}" maxlength="10" styleClass="input-text" />
    <h:outputText escape="true" value="ОГРН" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.ogrn}" maxlength="15" styleClass="input-text" />
    <h:outputText escape="true" value="ОКАТО" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.okato}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (RSA)" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.publicKey}" maxlength="1024" styleClass="input-text" />
    <h:outputText escape="true" value="Открытый ключ (ГОСТ) - контейнер" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.publicKeyGOSTAlias}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Переводить номер счета" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.contragentCreatePage.needAccountTranslate}" styleClass="output-text" />
    <h:outputText escape="true"  value="Список рассылки сводного отчета по заявкам" styleClass="output-text" />
    <h:inputText value="#{mainPage.contragentCreatePage.requestNotifyMailList}" maxlength="128" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Заметки" styleClass="output-text" />
    <h:inputTextarea rows="5" cols="64" value="#{mainPage.contragentCreatePage.remarks}" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать нового контрагента" action="#{mainPage.createContragent}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>