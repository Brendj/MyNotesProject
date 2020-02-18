<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditOrgs()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%--@elvariable id="orgSyncRequestPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncRequestPage"--%>
<h:panelGrid id="orgSyncRequestPage" binding="#{orgSyncRequestPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <rich:panel>
        <f:facet name="header"><h:outputText styleClass="column-header" value="Запрос синхронизации"/></f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Поставщик" styleClass="output-text required-field"/>
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{orgSyncRequestPage.defaultSupplier.contragentName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;"/>
                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage()}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="#{true}" target="#{orgSyncRequestPage.selectReceiver}"/>
                    <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}"/>
                    <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}"/>
                </a4j:commandButton>
            </h:panelGroup>
            <h:outputText escape="true" value="Организации" styleClass="output-text required-field"/>
            <h:panelGrid columns="2">
                <a4j:commandButton value="..." action="#{orgSyncRequestPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="#{orgSyncRequestPage.idOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" id="orgSyncRequestPageFilter" escape="true"
                              value=" {#{orgSyncRequestPage.filter}}"/>
            </h:panelGrid>
            <h:outputText styleClass="output-text" escape="true" value="Тип синхронизации" />
            <h:selectOneMenu id="contentType" value="#{orgSyncRequestPage.selectedSyncType}"
                             styleClass="input-text" style="width: 250px;">
                <f:selectItems value="#{orgSyncRequestPage.listOfSyncType}" />
            </h:selectOneMenu>
            <rich:spacer />
            <a4j:commandButton value="Запросить" action="#{orgSyncRequestPage.applySyncOperation()}"/>
            <rich:spacer/>
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid">
            <a4j:status id="sFullSyncRequestStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
                </f:facet>
            </a4j:status>
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages"/>
        </h:panelGrid>
    </rich:panel>
</h:panelGrid>
