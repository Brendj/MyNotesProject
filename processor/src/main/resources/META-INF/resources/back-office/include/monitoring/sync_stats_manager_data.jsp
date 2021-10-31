<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://java.sun.com/jstl/fmt" %>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="syncStatsPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.SyncStatsPage"--%>
<h:panelGrid id="syncStatsPanelGrid" binding="#{mainPage.syncStatsPage.pageComponent}" styleClass="borderless-grid">
    <%--<h:outputText styleClass="output-text" escape="true" value="Данные по всем синхронизациям собираются и записываются в БД с интервалом 10 минут." />--%>
    <%--<h:outputText styleClass="output-text" escape="true" value="При необходимости можно инициировать сброс данных в БД кнопкой \"Сохранить данные из оперативной памяти в БД\"." />--%>
    <%--<h:outputText styleClass="output-text" escape="true" value="Если не указаны конкретные ОО, то будет показана информация по всем ОО за период." />--%>
    <%--<h:outputText styleClass="output-text" escape="true" value="Период по-умолчанию - текущая неделя." />--%>
    <h:panelGrid columns="2" columnClasses="valign, valign">

        <h:outputText styleClass="output-text" escape="true" value="Организации(я)" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.syncStatsPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               disabled="#{mainPage.syncStatsPage.applyUserSettings}"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.syncStatsPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.syncStatsPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Начало периода выборки" styleClass="output-text" />
        <rich:calendar value="#{mainPage.syncStatsPage.periodStart}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="syncStatsPanelGrid" />
        </rich:calendar>

        <h:outputText escape="true" value="Конец периода выборки" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.syncStatsPage.periodEnd}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="syncStatsPanelGrid" />
        </rich:calendar>
    </h:panelGrid>

    <a4j:commandButton value="Обновить" action="#{mainPage.syncStatsPage.update}"
                       reRender="syncStatsPanelGrid"/>

    <a4j:commandButton value="Сохранить данные из оперативной памяти в БД" action="#{otherActionsPage.runShowShortLog}"
                       styleClass="command-button" />

    <%--<a4j:commandButton value="Суточные данные синхронизации" action="#{otherActionsPage.runShowDailyLog}"--%>
                       <%--styleClass="command-button" />--%>


    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <h:panelGrid columns="2" columnClasses="valign, valign">
        <rich:dataTable id="syncStatsOnPeriodTable" value="#{mainPage.syncStatsPage.syncStatsOnPeriod}" var="item"
                        footerClass="data-table-footer" columnClasses="center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Параметр" />
                </f:facet>
                <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Значение" />
                </f:facet>
                <h:outputText escape="true" value="#{item.value}" styleClass="output-text" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>

</h:panelGrid>

