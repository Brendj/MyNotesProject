<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: anvarov
  Date: 19.02.2018
  Time: 13:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="acceptanceOfCompletedWorksActPage" type="ru.axetta.ecafe.processor.web.ui.report.online.AcceptanceOfCompletedWorksActPage"--%>
<h:panelGrid id="acceptanceOfCompletedWorksActPanelGrid" binding="#{acceptanceOfCompletedWorksActPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{acceptanceOfCompletedWorksActPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{acceptanceOfCompletedWorksActPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,acceptanceOfCompletedWorksActPanel"
                         actionListener="#{acceptanceOfCompletedWorksActPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect" value="#{acceptanceOfCompletedWorksActPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{acceptanceOfCompletedWorksActPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,acceptanceOfCompletedWorksActPanel"
                         actionListener="#{acceptanceOfCompletedWorksActPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{acceptanceOfCompletedWorksActPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,acceptanceOfCompletedWorksActPanel"
                         actionListener="#{acceptanceOfCompletedWorksActPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Сформировать Акт по юр.лицу (весь образовательный комплекс)" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{acceptanceOfCompletedWorksActPage.showAllOrgs}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Тип питания" styleClass="output-text" />
        <h:selectOneMenu id="typeList" value="#{acceptanceOfCompletedWorksActPage.type}" style="width: 200px">
            <f:selectItems value="#{acceptanceOfCompletedWorksActPage.types}" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать документ" action="#{acceptanceOfCompletedWorksActPage.buildReportHTML}"
                           reRender="acceptanceOfCompletedWorksActPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Word" actionListener="#{acceptanceOfCompletedWorksActPage.showWordList}"
                         styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{acceptanceOfCompletedWorksActPage.clear}"
                           reRender="acceptanceOfCompletedWorksActPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" />
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-grid" id="acceptanceOfCompletedWorksActPanel"
                 columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${acceptanceOfCompletedWorksActPage.htmlReport!=null && not empty acceptanceOfCompletedWorksActPage.htmlReport}">
            <f:verbatim>
                <div>${acceptanceOfCompletedWorksActPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>