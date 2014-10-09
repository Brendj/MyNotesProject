<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="detailedDeviationsPaymentOrReducedPriceMealsReportPageGrid"
             binding="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.startDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" />

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener
                        value="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.getStringIdOfOrgList}"
                        target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.filter}}" />
        </h:panelGroup>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Генерировать отчет"
                               action="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.buildReportHTML}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="reportGenerateStatus" />
            <a4j:status id="reportGenerateStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
        <h:commandButton value="Выгрузить в Excel"
                         actionListener="#{mainPage.detailedDeviationsPaymentOrReducedPriceMealsReportPage.generateXLS}"
                         styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>