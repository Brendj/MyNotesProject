<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Пронин АВ
  Date: 04.10.2017
  Time: 11:02
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
</style>

<h:panelGrid id="directorStudentAttendanceGrid" binding="#{directorPage.directorStudentAttendancePage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
<%--<h:panelGroup>--%>
<%--<a4j:commandButton value="..." action="#{directorPage.showOrgListSelectPage}"--%>
                   <%--reRender="directorStudentAttendanceGrid, modalOrgListSelectorOrgTable"--%>
                   <%--oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"--%>
                   <%--styleClass="command-link" style="width: 25px;">--%>
    <%--<f:setPropertyActionListener value="#{directorPage.directorStudentAttendancePage.getStringIdOfOrgList}"--%>
                                 <%--target="#{directorPage.orgFilterOfSelectOrgListSelectPage}" />--%>
<%--</a4j:commandButton>--%>
    <%--<h:outputText styleClass="output-text" escape="true" value=" #{directorPage.directorStudentAttendancePage.filter}" />--%>
<%--</h:panelGroup>--%>
    <h:selectManyCheckbox value="#{directorPage.directorStudentAttendancePage.selectedOrgs}" id="directorStudentAttendanceGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorStudentAttendancePage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName}" itemDisabled="#{directorPage.directorStudentAttendancePage.selectedOrgs.contains('-1')}"/>
        </c:forEach>
        <a4j:support event="onchange" reRender="directorStudentAttendanceGridSelectionPanel"/>
    </h:selectManyCheckbox>

    <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
    <rich:calendar value="#{directorPage.directorStudentAttendancePage.startDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorStudentAttendancePage.startCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorStudentAttendanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
    <rich:calendar value="#{directorPage.directorStudentAttendancePage.endDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorStudentAttendancePage.endCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorStudentAttendanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorStudentAttendancePage.buildStudentAttendanceReport}"
                       reRender="workspaceTogglePanel, directorStudentAttendanceGrid" styleClass="command-button"
                       status="idReportGenerateStatus"/>
<a4j:status id="idReportGenerateStatus">
<f:facet name="start">
    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
</f:facet>
</a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
<h:panelGroup id="directorStudentAttendanceReportGrid" styleClass="borderless-grid">
    <c:forEach items="#{directorPage.directorStudentAttendancePage.chartData}" var="item">
        <div class="report-holder">
            <h:graphicImage value="#{item}" width="800" height="400" rendered="#{directorPage.directorStudentAttendancePage.showReport}" />
        </div>
    </c:forEach>
</h:panelGroup>
