<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра пользователя --%>
<h:panelGrid id="cityViewPage" binding="#{mainPage.cityViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText value="Название" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.name}" styleClass="output-text" />


    <h:outputText value="URL сервиса" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.serviceUrl}" styleClass="output-text" />


    <h:outputText value="Маска лицевого счета" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.contractIdMask}" styleClass="output-text" />


    <h:outputText value="Активность" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.activity}" styleClass="output-text" />


    <h:outputText value="Имя пользователя" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.userName}" styleClass="output-text" />


    <h:outputText value="Пароль" styleClass="output-text"> </h:outputText>

    <h:outputText value="#{mainPage.cityViewPage.password}" styleClass="output-text" />


    <h:outputText value="Тип авторизации " styleClass="output-text"> </h:outputText>


    <h:outputText value="#{mainPage.cityViewPage.authTypeName}" styleClass="output-text" />


</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showCityEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
