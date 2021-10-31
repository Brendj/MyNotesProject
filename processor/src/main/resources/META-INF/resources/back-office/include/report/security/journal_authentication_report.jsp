<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="journalAuthenticationReportPage" type="ru.axetta.ecafe.processor.web.ui.report.security.JournalAuthenticationReportPage"--%>
<h:panelGrid id="journalAuthenticationReportPanelGrid" binding="#{journalAuthenticationReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{journalAuthenticationReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{journalAuthenticationReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Пользователь" />
        <h:panelGroup id="userFilter">
            <a4j:commandButton value="..." action="#{mainPage.showUserSelectPage()}"
                               reRender="modalUserSelectorPanel,userFilter"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalUserSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.userSelectPage.filter}" target="#{mainPage.userSelectPage.filter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedUser"
                          value=" {#{journalAuthenticationReportPage.filter}}" />
        </h:panelGroup>

        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{journalAuthenticationReportPage.doGenerate}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{journalAuthenticationReportPage.doGenerateXLS()}" styleClass="command-button" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="reportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty journalAuthenticationReportPage.htmlReport}" >
            <f:verbatim>
                <div>${journalAuthenticationReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>