<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель создания пользователя --%>
<h:panelGrid id="cityCreateGrid" binding="#{mainPage.cityCreatePage.pageComponent}" styleClass="borderless-grid"
             columns="2">

    <h:outputText value="Название" styleClass="output-text"> </h:outputText>
    <h:inputText value="#{mainPage.cityCreatePage.name}" styleClass="input-text" />

    <h:outputText value="URL сервиса" styleClass="output-text"> </h:outputText>

    <h:inputText value="#{mainPage.cityCreatePage.serviceUrl}" styleClass="input-text" />

    <h:outputText value="Маска лицевого счета" styleClass="output-text"> </h:outputText>

    <h:inputText value="#{mainPage.cityCreatePage.contractIdMask}" styleClass="input-text" />

    <h:outputText value="Активность" styleClass="output-text"> </h:outputText>
    <h:selectBooleanCheckbox value="#{mainPage.cityCreatePage.activity}" styleClass="output-text" />

    <h:outputText value="Имя пользователя" styleClass="output-text"> </h:outputText>

    <h:inputText value="#{mainPage.cityCreatePage.userName}" styleClass="input-text" />

    <h:outputText value="Пароль" styleClass="output-text"> </h:outputText>
    <h:inputText value="#{mainPage.cityCreatePage.password}" styleClass="input-text" />
    <h:outputText value="Тип авторизации " styleClass="output-text"> </h:outputText>

    <h:selectOneMenu value="#{mainPage.cityCreatePage.indexOfAuthType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cityCreatePage.authTypeItems}" />
    </h:selectOneMenu>


</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать новый город" action="#{mainPage.createCity}" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>