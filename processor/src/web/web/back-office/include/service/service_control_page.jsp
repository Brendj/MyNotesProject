<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="serviceControlPage" type="ru.axetta.ecafe.processor.web.ui.service.ServiceControlPage"--%>
<h:panelGrid id="cardServicesGrid" binding="#{serviceControlPage.pageComponent}" columns="2">
    <h:panelGrid id="cardServicesPanel">
        <rich:panel>
            <f:facet name="header">
                <h:outputText styleClass="column-header" value="Запуск задачи отправки факта предаставления МСП" />
            </f:facet>
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
                <rich:calendar value="#{serviceControlPage.startDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
                <rich:calendar value="#{serviceControlPage.endDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <a4j:commandButton value="Отправить запрос" id="CardServicesSentToMeshButton"
                                   action="#{serviceControlPage.sendTask()}" styleClass="command-button"
                                   status="cardServicesPanelreportGenerateStatus"
                />
            </h:panelGrid>
        </rich:panel>
        <a4j:status id="cardServicesPanelreportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" id="serviceControlPageInfo"/>
    </h:panelGrid>
</h:panelGrid>