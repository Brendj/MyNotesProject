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
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{registerStampPage.org.shortName}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{registerStampPage.start}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="datePeriodSelect" actionListener="#{registerStampPage.onDateSpecified}"/>
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{registerStampPage.end}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" id="endDateCalendar">
            <a4j:support event="onchanged" reRender="datePeriodSelect" actionListener="#{registerStampPage.onDateSpecified}"/>
        </rich:calendar>

        <h:outputText value="Интервал: " styleClass="output-text"/>
        <h:selectOneMenu id="datePeriodSelect" value="#{registerStampPage.reportPeriod}" converter="javax.faces.Integer"
                         styleClass="output-text" >
            <f:selectItem itemValue="0" itemLabel="1 день"/>
            <f:selectItem itemValue="1" itemLabel="1 неделя"/>
            <f:selectItem itemValue="2" itemLabel="2 недели"/>
            <f:selectItem itemValue="3" itemLabel="1 месяц"/>
            <f:selectItem itemValue="4" itemLabel="Точная дата"/>
            <a4j:support event="onchange" reRender="endDateCalendar" actionListener="#{registerStampPage.onReportPeriodChanged}"/>
        </h:selectOneMenu>

        <a4j:commandButton value="Применить" action="#{registerStampPage.reload}"
                           reRender="registerStampReportPanelGrid" />
        <a4j:commandButton value="Очистить" action="#{registerStampPage.clear}"
                           reRender="registerStampReportPanelGrid" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>


    <rich:dataTable value="#{registerStampPage.pageItems}" var="item">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="3">
                    <h:outputText value="Дата и номер талона"/>
                </rich:column>
                <rich:column colspan="#{registerStampPage.lastLvlElements}" rowspan="1">
                    <h:outputText value="Количество"/>
                </rich:column>

                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{registerStampPage.lvl1}" var="lvl1" colspan="#{lvl1.value.childCount}" rowspan="#{3-(lvl1.value.childCount>0?2:1)}">
                    <h:outputText value="#{lvl1.key}"/>
                </rich:columns>

                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{registerStampPage.lvl2}" var="lvl2" colspan="#{lvl2.value.childCount}">
                    <h:outputText value="#{lvl2.key}"/>
                </rich:columns>

            </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:outputText value="#{item.date}"/>
        </rich:column>
        <rich:columns value="#{registerStampPage.lvlBottom}" var="lvlBottom" colspan="#{lvlBottom.value.childCount}">
            <h:outputText value="#{item.getValue(lvlBottom.value.fullName)} " />
        </rich:columns>
    </rich:dataTable>

    <h:commandButton value="Выгрузить в Excel" actionListener="#{registerStampPage.showCSVList}" styleClass="command-button" />

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>