<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="cardServicesPage" type="ru.axetta.ecafe.processor.web.ui.service.CardServicesPage"--%>

<h:panelGrid id="cardServicesGrid" binding="#{cardServicesPage.pageComponent}" columns="2">
    <h:panelGrid id="cardServicesPanel">
        <rich:panel>
            <f:facet name="header">
                <h:outputText styleClass="column-header" value="Отправка ЭИ в МЭШ" />
            </f:facet>
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText styleClass="output-text" escape="true" value="Организации" />
                <h:panelGroup>
                    <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" id="cardServicesPanelModalOrgListSelectorPanel"
                                       reRender="modalOrgListSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;">
                        <f:setPropertyActionListener value="#{cardServicesPage.filter}"
                                                     target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    </a4j:commandButton>
                    <h:outputText styleClass="output-text" escape="true" value=" {#{cardServicesPage.filter}}" />
                </h:panelGroup>
                <h:outputText styleClass="output-text" escape="true" value="Учитывать дружественные ОО" />
                <h:selectBooleanCheckbox value="#{cardServicesPage.allFriendlyOrgs}" styleClass="checkboxes" />
                <a4j:commandButton value="Отправить ЭИ в МЭШ" id="CardServicesSentToMeshButton"
                                   action="#{cardServicesPage.sendCardsToMESH()}" styleClass="command-button"
                                   status="cardServicesPanelreportGenerateStatus"
                />
            </h:panelGrid>
        </rich:panel>
        <a4j:commandButton value="Блокировка ЭИ без транзакций" action="#{cardServicesPage.autoBlockCards()}" id="autoBlockCards"
                           styleClass="command-button" reRender="mainMenu, workspaceTogglePanel"
                           status="cardServicesPanelreportGenerateStatus"
        />
        <a4j:status id="cardServicesPanelreportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" id="cardServicesPageInfo"/>
</h:panelGrid>