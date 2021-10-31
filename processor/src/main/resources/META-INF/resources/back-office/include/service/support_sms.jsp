<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="supportSMSPage" type="ru.axetta.ecafe.processor.web.ui.service.SupportSMSPage"--%>

<rich:panel id="supportSmsPanel" headerClass="workspace-panel-header">
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Кому (номер лицевого счета)" styleClass="output-text" />
            <h:inputText value="#{supportSMSPage.address}" size="11" maxlength="11" styleClass="input-text" />
            <h:outputText escape="true" value="Текст" styleClass="output-text" />
            <h:inputTextarea rows="10" cols="40" value="#{supportSMSPage.text}" styleClass="input-text" />
            <h:outputText escape="true" value="Статус" styleClass="output-text" rendered="#{supportSMSPage.sent}"/>
            <h:inputText readonly="true" size="40" value="#{supportSMSPage.deliveryStatus}" styleClass="input-text" rendered="#{supportSMSPage.sent}"/>
            <a4j:commandButton action="#{supportSMSPage.updateStatus}"
                reRender="supportSmsPanel" value="Обновить статус"
                rendered="#{supportSMSPage.sent}"
                />
            <a4j:commandButton action="#{supportSMSPage.send}"
                reRender="supportSmsPanel" value="Отправить"
                rendered="#{not supportSMSPage.sent}" />

            <a4j:status id="sReportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </h:panelGrid>
</rich:panel>