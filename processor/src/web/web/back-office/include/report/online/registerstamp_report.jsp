<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 07.05.13
  Time: 14:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="registerStampPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampPage"--%>
<h:panelGrid id="registerStampReportPanelGrid" binding="#{registerStampPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header" width="800">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{registerStampPage.filter}" readonly="true" styleClass="input-text long-field"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{registerStampPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar"
                             actionListener="#{registerStampPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect" value="#{registerStampPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{registerStampPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar"
                             actionListener="#{registerStampPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{registerStampPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                             actionListener="#{registerStampPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText value="Показывать с расхождениями: " styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{registerStampPage.includeActDiscrepancies}"/>


        </h:panelGrid>

    </rich:simpleTogglePanel>
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{registerStampPage.buildReportHTML}"
                           reRender="registerStampReportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{registerStampPage.showCSVList}" styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{registerStampPage.clear}"
                           reRender="registerStampReportPanelGrid"
                           styleClass="command-button" status="reportGenerateStatus" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="registerStampReportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${registerStampPage.htmlReport!=null && not empty registerStampPage.htmlReport}" >
            <f:verbatim>
                <div>${registerStampPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>


</h:panelGrid>