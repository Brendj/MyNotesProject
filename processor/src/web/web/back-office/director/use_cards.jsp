<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Akhmetov
  Date: 26.04.16
  Time: 18:35
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
    .director-grid > tbody > tr:first-child > td:first-child {
        vertical-align: top;
        padding-top: 7px;
    }
</style>

<h:panelGrid id="directorUseCardsGrid" binding="#{directorPage.directorUseCardsPage.pageComponent}" styleClass="borderless-grid, director-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций"/>
    <h:selectManyCheckbox value="#{directorPage.directorUseCardsPage.selectedOrgs}" id="directorUseCardsGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorUseCardsPage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName} - #{item.shortAddress}" itemDisabled="#{directorPage.directorUseCardsPage.selectedOrgs.contains('-1')}"/>
        </c:forEach>
        <a4j:support event="onchange" reRender="directorUseCardsGridSelectionPanel"/>
    </h:selectManyCheckbox>

    <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
    <rich:calendar value="#{directorPage.directorUseCardsPage.startDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorUseCardsPage.startCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorUseCardsGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
    <rich:calendar value="#{directorPage.directorUseCardsPage.endDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorUseCardsPage.endCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorUseCardsGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Тип отчета"/>
    <h:selectOneMenu styleClass="input-text"
                   value="#{directorPage.directorUseCardsPage.reportType}">
        <f:selectItem itemValue="0" itemLabel="графический"/>
        <f:selectItem itemValue="1" itemLabel="табличный"/>
    </h:selectOneMenu>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorUseCardsPage.buildUseCardsReport}"
                       reRender="workspaceTogglePanel, directorUseCardsGrid" styleClass="command-button"
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
<h:panelGrid id="directorUseCardsGraphReportGrid" styleClass="borderless-grid" columns="1"
              rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '0')}">
    <c:forEach items="#{directorPage.directorUseCardsPage.chartData}" var="item" varStatus="var">
        <h:panelGroup id="report-holder-${var.index}" styleClass="borderless-grid"
                      rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '0')}">
            <h:graphicImage value="#{item}" width="800" height="400"/>
        </h:panelGroup>
    </c:forEach>
</h:panelGrid>
<c:if test="${directorPage.directorUseCardsPage.directorUseCardsReport.allOO ne true}">
    <c:forEach items="#{directorPage.directorUseCardsPage.directorUseCardsReport.items}" var="item" varStatus="var">
        <h:panelGrid id="directorUseCardsTableReportGrid-${var.index}" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="4">
                            <h:outputText styleClass="column-header" value="Использование электронных носителей при посещении здания #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Сервисная карта, браслет, брелок"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Социальная карта"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Без электронных носителей СОШ"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Без электронных носителей ДОУ"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.serviceValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.socialValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.withoutSOSHValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.withoutDOUValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
<c:if test="${directorPage.directorUseCardsPage.directorUseCardsReport.allOO eq true}">
    <c:forEach items="#{directorPage.directorUseCardsPage.directorUseCardsReport.allOOItem}" var="item">
        <h:panelGrid id="directorUseCardsTableReportAllGrid" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="4">
                            <h:outputText styleClass="column-header" value="Использование электронных носителей при посещении здания #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Сервисная карта, браслет, брелок"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Социальная карта"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Без электронных носителей СОШ"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Без электронных носителей ДОУ"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.serviceValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.socialValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.withoutSOSHValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.withoutDOUValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
