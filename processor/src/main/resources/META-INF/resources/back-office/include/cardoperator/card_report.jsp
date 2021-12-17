<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="reportPanelGrid" binding="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,reportPanel"
                         actionListener="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.onReportPeriodChanged}" />
        </rich:calendar>
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,reportPanel"
                         actionListener="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.onEndDateSpecified}" />
        </rich:calendar>
        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 200px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,reportPanel"
                         actionListener="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.generateReport}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.createdAndReissuedCardReportFromCardOperatorPage.exportToXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty mainPage.createdAndReissuedCardReportFromCardOperatorPage.report && not empty mainPage.createdAndReissuedCardReportFromCardOperatorPage.htmlReport}" >
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${mainPage.createdAndReissuedCardReportFromCardOperatorPage.htmlReport}
                </div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
