<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    function onstartloading(){
        jQuery(".command-button, .command-link").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button, .command-link").attr('disabled', '');
        updateWidth();
    }
    jQuery(document).ready(function(){
        updateWidth();
    });
</script>

<h:panelGrid id="detailedDeviationsWithoutCorpsNewReportPageGrid"
             binding="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.startDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems
                    value="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar"
                       value="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.filter}}" />
        </h:panelGroup>

        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Генерировать отчет"
                               action="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.buildReportHTML}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="reportGenerateStatus" />
        </h:panelGrid>
        <h:commandButton value="Выгрузить в Excel"
                         actionListener="#{mainPage.detailedDeviationsWithoutCorpsNewReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.detailedDeviationsWithoutCorpsNewReportPage.htmlReport}">
            <h:outputText escape="true" value="Детализированный отчет отклонений оплаты льготного питания"
                          styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.detailedDeviationsWithoutCorpsNewReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>