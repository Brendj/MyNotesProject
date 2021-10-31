<%--
  Created by IntelliJ IDEA.
  User: Akhmetov
  Date: 24.03.16
  Time: 15:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="istkKioskOptionsPage" type="ru.axetta.ecafe.processor.web.ui.option.IstkKioskOptionsPage"--%>

<h:panelGrid id="istkKioskOptionPanel" binding="#{istkKioskOptionsPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Адрес веб-сервиса"
                      styleClass="output-text" />
        <h:inputText value="#{istkKioskOptionsPage.wsAddress}" styleClass="input-text" size="35" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="4">
        <a4j:commandButton value="Получить список школ" styleClass="command-button" action="#{istkKioskOptionsPage.connect}" reRender="istkKioskOptionPanel" />
        <a4j:commandButton value="Разрешить всем" styleClass="command-button" reRender="istkKioskOptionPanel" disabled="#{empty istkKioskOptionsPage.schoolItemList}"
                action="#{istkKioskOptionsPage.setAllFlags(true)}" />
        <a4j:commandButton value="Запретить всем" styleClass="command-button" reRender="istkKioskOptionPanel" disabled="#{empty istkKioskOptionsPage.schoolItemList}"
                action="#{istkKioskOptionsPage.setAllFlags(false)}" />
        <a4j:commandButton value="Сохранить изменения" styleClass="command-button" action="#{istkKioskOptionsPage.saveChangedParameters}" reRender="istkKioskOptionPanel"
                           disabled="#{empty istkKioskOptionsPage.changedPermissions}" />
    </h:panelGrid>

<rich:dataTable id="schoolKioskTable"
                footerClass="data-table-footer" value="#{istkKioskOptionsPage.schoolItemList}" var="item" rows="20"
                columnClasses="center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column"
                rendered="#{istkKioskOptionsPage.tableVisible}">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид." />
        </f:facet>
        <h:outputText value="#{item.schoolId}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название школы" />
        </f:facet>
        <h:outputText value="#{item.schoolName}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Округ" />
        </f:facet>
        <h:outputText value="#{item.district}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Район" />
        </f:facet>
        <h:outputText value="#{item.area}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор ИС ПП" />
        </f:facet>
        <h:outputText value="#{item.isppId}"/>
    </rich:column>
    <rich:column headerClass="column-header" styleClass="#{item.style}">
        <f:facet name="header">
            <h:outputText escape="true" value="Игра разрешена" />
        </f:facet>
        <h:selectBooleanCheckbox value="#{item.permitGame}">
            <a4j:support event="onchange" action="#{istkKioskOptionsPage.processGameFlag}" reRender="istkKioskOptionPanel">
                <f:setPropertyActionListener value="#{item}" target="#{istkKioskOptionsPage.currentItem}" />
            </a4j:support>
        </h:selectBooleanCheckbox>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="schoolKioskTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                           stepControls="auto" boundaryControls="hide">
            <f:facet name="previous">
                <h:graphicImage value="/images/16x16/left-arrow.png" />
            </f:facet>
            <f:facet name="next">
                <h:graphicImage value="/images/16x16/right-arrow.png" />
            </f:facet>
        </rich:datascroller>
    </f:facet>
</rich:dataTable>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

</h:panelGrid>