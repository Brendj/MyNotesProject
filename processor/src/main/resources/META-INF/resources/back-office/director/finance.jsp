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
    .director-grid > tbody > tr:first-child > td:first-child {
        vertical-align: top;
        padding-top: 7px;
    }
</style>

<h:panelGrid id="directorFinanceGrid" binding="#{directorPage.directorFinancePage.pageComponent}" styleClass="borderless-grid, director-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
    <h:selectManyCheckbox value="#{directorPage.directorFinancePage.selectedOrgs}" id="directorFinanceGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorFinancePage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName} - #{item.shortAddress}" itemDisabled="#{directorPage.directorFinancePage.selectedOrgs.contains('-1')}"/>
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
    <h:outputText styleClass="output-text" escape="true" value="Тип отчета"/>
    <h:selectOneMenu styleClass="input-text"
                     value="#{directorPage.directorFinancePage.reportType}">
        <f:selectItem itemValue="0" itemLabel="графический"/>
        <f:selectItem itemValue="1" itemLabel="табличный"/>
    </h:selectOneMenu>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorFinancePage.buildFinanceReport}"
                       reRender="workspaceTogglePanel, directorFinanceGrid" styleClass="command-button"
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
<h:panelGrid id="directorFinanceGraphReportGrid" styleClass="borderless-grid" columns="1"
              rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '0')}">
    <c:forEach items="#{directorPage.directorFinancePage.chartData}" var="item" varStatus="var">
        <h:panelGroup id="report-holder-${var.index}" styleClass="borderless-grid"
                      rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '0')}">
            <h:graphicImage value="#{item}" width="800" height="400" rendered="#{directorPage.directorFinancePage.showReport}" />
        </h:panelGroup>
    </c:forEach>
</h:panelGrid>
<c:if test="${directorPage.directorFinancePage.directorFinanceReport.allOO ne true}">
    <c:forEach items="#{directorPage.directorFinancePage.directorFinanceReport.items}" var="item" varStatus="var">
        <h:panelGrid id="directorFinanceTableReportGrid-${var.index}" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Финансовые показатели по обучающимся #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinancePayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStudentsFinanceSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Финансовые показатели по сотрудникам #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinancePayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStaffFinanceSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Количественные показатели по обучающимся #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountPayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStudentsCountSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Количественные показатели по сотрудникам #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="(#{item.shortAddress})" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountPayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStaffCountSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
<c:if test="${directorPage.directorFinancePage.directorFinanceReport.allOO eq true}">
    <c:forEach items="#{directorPage.directorFinancePage.directorFinanceReport.allOOItem}" var="item">
        <h:panelGrid id="directorFinanceTableReportAllGrid" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Финансовые показатели по обучающимся #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinancePayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsFinanceVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStudentsFinanceSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Финансовые показатели по сотрудникам #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinancePayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffFinanceVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStaffFinanceSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Количественные показатели по обучающимся #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountPayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.studentsCountVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStudentsCountSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>

            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="5">
                            <h:outputText styleClass="column-header"
                                          value="Количественные показатели по сотрудникам #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по абонементу"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание по платному плану"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Горячее питание приобретенное в буфете"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Буфетная продукция (в т.ч. Вендинг)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Итого"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountAbonement}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountPayPlan}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountBuffet}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.staffCountVending}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.getStaffCountSum()}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorFinancePage.showReport && (directorPage.directorFinancePage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
