<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Пронин АВ
  Date: 04.10.2017
  Time: 15:30
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

<h:panelGrid id="directorFinanceGrid" binding="#{directorPage.directorFinancePage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
<%--<h:panelGroup>--%>
<%--<a4j:commandButton value="..." action="#{directorPage.showOrgListSelectPage}"--%>
                   <%--reRender="directorFinanceGrid, modalOrgListSelectorOrgTable"--%>
                   <%--oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"--%>
                   <%--styleClass="command-link" style="width: 25px;">--%>
    <%--<f:setPropertyActionListener value="#{directorPage.directorFinancePage.getStringIdOfOrgList}"--%>
                                 <%--target="#{directorPage.orgFilterOfSelectOrgListSelectPage}" />--%>
<%--</a4j:commandButton>--%>
    <%--<h:outputText styleClass="output-text" escape="true" value=" #{directorPage.directorFinancePage.filter}" />--%>
<%--</h:panelGroup>--%>
    <h:selectManyCheckbox value="#{directorPage.directorFinancePage.selectedOrgs}" id="directorFinanceGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorFinancePage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName}" itemDisabled="#{directorPage.directorFinancePage.selectedOrgs.contains('-1')}"/>
        </c:forEach>
        <a4j:support event="onchange" reRender="directorFinanceGridSelectionPanel"/>
    </h:selectManyCheckbox>
    <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
    <rich:calendar value="#{directorPage.directorFinancePage.startDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorFinancePage.startCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorFinanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
    <rich:calendar value="#{directorPage.directorFinancePage.endDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorFinancePage.endCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorFinanceGrid" ajaxSingle="true"/>
    </rich:calendar>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorFinancePage.buildFinanceReport}"
                       reRender="workspaceTogglePanel, directorFinanceGrid" styleClass="command-button"
                       status="idReportGenerateStatus"/>
<a4j:status id="idReportGenerateStatus">
<f:facet name="start">
    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
</f:facet>
</a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
<h:panelGroup id="directorFinanceReportGrid" styleClass="borderless-grid">
    <c:forEach items="#{directorPage.directorFinancePage.chartData}" var="item">
        <div class="report-holder">
            <h:graphicImage value="#{item}" width="800" height="400" rendered="#{directorPage.directorFinancePage.showReport}" />
        </div>
    </c:forEach>
</h:panelGroup>
