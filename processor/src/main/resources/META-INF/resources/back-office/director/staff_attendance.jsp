<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Пронин АВ
  Date: 28.09.2017
  Time: 12:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style type="text/css">
    .out-of-date {
        color: gray;
    }
    .rich-table tr + tr {
        background-position: 0 80% !important;
    }
    .director-grid > tbody > tr:first-child > td:first-child {
        vertical-align: top;
        padding-top: 7px;
    }
</style>

<h:panelGrid id="directorStaffAttendanceGrid" binding="#{directorPage.directorStaffAttendancePage.pageComponent}" styleClass="borderless-grid, director-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
    <h:selectManyCheckbox value="#{directorPage.directorStaffAttendancePage.selectedOrgs}" id="directorStaffAttendanceGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorStaffAttendancePage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName} - #{item.shortAddress}" itemDisabled="#{directorPage.directorStaffAttendancePage.selectedOrgs.contains('-1')}"/>
        </c:forEach>
        <a4j:support event="onchange" reRender="directorStaffAttendanceGridSelectionPanel"/>
    </h:selectManyCheckbox>

    <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
    <rich:calendar value="#{directorPage.directorStaffAttendancePage.startDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorStaffAttendancePage.startCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorStaffAttendanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
    <rich:calendar value="#{directorPage.directorStaffAttendancePage.endDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorStaffAttendancePage.endCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorStaffAttendanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Тип отчета"/>
    <h:selectOneMenu styleClass="input-text"
                     value="#{directorPage.directorStaffAttendancePage.reportType}">
        <f:selectItem itemValue="0" itemLabel="графический"/>
        <f:selectItem itemValue="1" itemLabel="табличный"/>
    </h:selectOneMenu>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorStaffAttendancePage.buildStaffAttendanceReport}"
                       reRender="workspaceTogglePanel, directorStaffAttendanceGrid" styleClass="command-button"
                       status="idReportGenerateStatus"/>
    <a4j:status id="idReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:outputText styleClass="output-text" escape="true" value=""/>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
<h:panelGrid id="directorStaffAttendanceGraphReportGrid" styleClass="borderless-grid" columns="1"
              rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '0')}">
    <c:forEach items="#{directorPage.directorStaffAttendancePage.chartData}" var="item" varStatus="var">
        <h:panelGroup id="report-holder-${var.index}" styleClass="borderless-grid"
                      rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '0')}">
            <h:graphicImage value="#{item}" width="800" height="400" rendered="#{directorPage.directorStaffAttendancePage.showReport}"/>
        </h:panelGroup>
    </c:forEach>
</h:panelGrid>
<c:if test="${directorPage.directorStaffAttendancePage.directorStaffAttendanceReport.allOO ne true}">
    <c:forEach items="#{directorPage.directorStaffAttendancePage.directorStaffAttendanceReport.items}" var="item" varStatus="var">
        <h:panelGrid id="directorStaffAttendanceTableReportGrid-${var.index}" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="6">
                            <h:outputText styleClass="column-header" value="Посещаемость сотрудников  #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Педагогический состав и администрация"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Тех.персонал, сотрудники и другие"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" rowspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Посетители"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" rowspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Родители"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Не вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" >
                            <h:outputText styleClass="column-header" escape="true" value="Не вошли"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.administrationValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.exAdministrationValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.techpersonalValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.exTechpersonalValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.parentValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.visitorValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
<c:if test="${directorPage.directorStaffAttendancePage.directorStaffAttendanceReport.allOO eq true}">
    <c:forEach items="#{directorPage.directorStaffAttendancePage.directorStaffAttendanceReport.allOOItem}" var="item">
        <h:panelGrid id="directorStaffAttendanceTableReportAllGrid" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="6">
                            <h:outputText styleClass="column-header" value="Посещаемость сотрудников  #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Педагогический состав и администрация"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Тех.персонал, сотрудники и другие"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" rowspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Посетители"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" rowspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Родители"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Не вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Вошли"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" >
                            <h:outputText styleClass="column-header" escape="true" value="Не вошли"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.administrationValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.exAdministrationValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.techpersonalValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.exTechpersonalValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.parentValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.visitorValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorStaffAttendancePage.showReport && (directorPage.directorStaffAttendancePage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
