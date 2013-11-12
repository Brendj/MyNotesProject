<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="regularPaymentsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegularPaymentsReportPage"--%>
<h:panelGrid id="regularPaymentsReportGrid" binding="#{regularPaymentsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{regularPaymentsReportPage.startDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{regularPaymentsReportPage.endDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <a4j:commandButton value="Генерировать отчет" action="#{regularPaymentsReportPage.showReport}"
                           reRender="regularPaymentsReportTable" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:spacer height="10" />
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="regularPaymentsReportTable" var="item" value="#{regularPaymentsReportPage.items}"
                        rowKeyVar="row" footerClass="data-table-footer" rows="20"
                        columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column,
                        left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column,
                        center-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время платежа" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentDate}" styleClass="output-text"
                              converter="timeMinuteConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор" />
                </f:facet>
                <h:outputText escape="true" value="#{item.idOfPayment}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Организация" />
                </f:facet>
                <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер л/с" />
                </f:facet>
                <h:outputText escape="true" value="#{item.contractId}" styleClass="output-text"
                              converter="contractIdConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Фамилия" />
                </f:facet>
                <h:outputText escape="true" value="#{item.surname}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Имя" />
                </f:facet>
                <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Отчество" />
                </f:facet>
                <h:outputText escape="true" value="#{item.secondName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentSum}" styleClass="output-text"
                              converter="copeckSumConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Баланс до пополнения" />
                </f:facet>
                <h:outputText escape="true" value="#{item.clientBalance}" styleClass="output-text"
                              converter="copeckSumConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Платеж успешный" />
                </f:facet>
                <h:outputText escape="true" value="#{item.success}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="RRN транзакции" />
                </f:facet>
                <h:outputText escape="true" value="#{item.rrn}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Статус платежа" />
                </f:facet>
                <h:outputText escape="true" value="#{item.status}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сообщение об ошибке" />
                </f:facet>
                <h:outputText escape="true" value="#{item.errorMessage}" styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="regularPaymentsReportTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    </h:panelGrid>
</h:panelGrid>