<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 11.02.12
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="orgDiscountReportPanelGrid" binding="#{mainPage.orgDiscountsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.orgDiscountsReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Данные по комплексу" styleClass="output-text" />
        <h:selectOneMenu id="regionsList" value="#{mainPage.orgDiscountsReportPage.orgFilter}" style="width:120px;">
            <f:selectItems value="#{mainPage.orgDiscountsReportPage.orgFilters}" />
        </h:selectOneMenu>

        <h:outputText escape="false" value="Показывать резервников" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgDiscountsReportPage.showReserve}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать платное питание" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgDiscountsReportPage.showPayComplex}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать льготы ДТиСЗН" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgDiscountsReportPage.showDSZN}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Генерировать отчет" action="#{mainPage.orgDiscountsReportPage.buildReportHTML}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="reportGenerateStatus" />
        </h:panelGrid>
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.orgDiscountsReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.orgDiscountsReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по количеству льготников в организации" styleClass="output-text" />
            <f:verbatim>
                <%--<style type="text/css">--%>
                <%--div.htmlReportContent :empty {--%>
                <%--display: none;--%>
                <%--}--%>
                <%--</style>--%>
                <div class="htmlReportContent"> ${mainPage.orgDiscountsReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>