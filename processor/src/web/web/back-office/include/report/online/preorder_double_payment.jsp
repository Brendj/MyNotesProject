<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="preorderDoublePaymentReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderDoublePaymentReportPage"--%>
<h:panelGrid id="preorderDoublePaymentReportPanelGrid" binding="#{preorderDoublePaymentReportPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{preorderDoublePaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
        </rich:calendar>

        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{preorderDoublePaymentReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
        </rich:calendar>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{preorderDoublePaymentReportPage.reload}"
                           reRender="preorderDoublePaymentReportTable"
                           styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="preorderDoublePaymentReportTable">
        <rich:dataTable id="preorderDoublePaymentTable" value="#{preorderDoublePaymentReportPage.items}" var="item" rows="50"
                        footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Поставщик питания" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Дата предзаказа" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Номер / Дата заказа" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Ид заказа" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Ид клиента" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="ФИО" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Группа" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Комплекс" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Стоимость" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Оплачено" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.contragentName}" styleClass="output-text"  />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.preorderDate}" styleClass="output-text" converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="false" value="#{item.orderInfo}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.idOfPreorderComplex}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.idOfClient}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.fio}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.groupName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.complexName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.preorderSum}" styleClass="output-text" converter="copeckSumConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.paySum}" styleClass="output-text" converter="copeckSumConverter" />
            </rich:column>
            <rich:datascroller for="preorderDoublePaymentTable" renderIfSinglePage="false" maxPages="5"
                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>