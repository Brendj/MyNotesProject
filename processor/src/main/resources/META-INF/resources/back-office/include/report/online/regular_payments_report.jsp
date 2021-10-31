<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="regularPaymentsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegularPaymentsReportPage"--%>
<h:panelGrid id="regularPaymentsReportGrid" binding="#{mainPage.regularPaymentsReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.regularPaymentsReportPage.startDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.regularPaymentsReportPage.endDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.regularPaymentsReportPage.exportToHTML}"
                           reRender="regularPaymentsReportTable" styleClass="command-button" />
        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.regularPaymentsReportPage.exportToXLS}"
                         styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:spacer height="10" />
    <h:panelGrid styleClass="borderless-grid" id="regularPaymentsReportTable" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.regularPaymentsReportPage.htmlReport}" >
            <f:verbatim>
                <div>${mainPage.regularPaymentsReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>