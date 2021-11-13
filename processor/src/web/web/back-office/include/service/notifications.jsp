<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="notificationsPage" type="ru.axetta.ecafe.processor.web.ui.service.NotificationsPage"--%>

<h:panelGrid id="otherActionsGrid" binding="#{notificationsPage.pageComponent}" columns="2">
    <h:panelGrid id="specialDatesFileLoaderPanel">
        <rich:panel>
            <f:facet name="header">
                <h:outputText styleClass="column-header" value="Уведомления" />
            </f:facet>
            <h:panelGrid styleClass="borderless-grid" columns="1">
                <a4j:commandButton value="Итого за день"
                                   action="#{notificationsPage.runEventNotificationServiceForDaily}"
                                   id="runEventNotificationServiceForDaily" styleClass="command-button" />
                <a4j:commandButton value="Итого за неделю"
                                   action="#{notificationsPage.runEventNotificationServiceForWeekly}"
                                   id="runEventNotificationServiceForWeekly" styleClass="command-button" />
                <a4j:commandButton value="Уведомления по льготам (оканчивается срок действия)"
                                   action="#{notificationsPage.endBenefitNotification}" styleClass="command-button" />

                <rich:panel>
                    <h:panelGrid columns="2">
                        <a4j:commandButton value="Уведомления по льготам (отказ)"
                                           action="#{notificationsPage.applicationDenide}"
                                           styleClass="command-button" /><br />
                        <h:outputText value="Номер заявления" styleClass="output-text" />
                        <h:inputText value="#{notificationsPage.serviceNumber}" size="50" />
                    </h:panelGrid>
                </rich:panel>
                <a4j:commandButton value="Запустить Автоплатеж и уведомления"
                                   action="#{notificationsPage.runRegularPayments}" styleClass="command-button" />
                <a4j:commandButton value="Отправка уведомлений об отмене предзаказа" action="#{notificationsPage.cancelPreorder}"
                                   reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
            </h:panelGrid>
        </rich:panel>
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>