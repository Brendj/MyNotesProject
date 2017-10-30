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

<h:panelGrid id="directorDiscountFoodGrid" binding="#{directorPage.directorDiscountFoodPage.pageComponent}" styleClass="borderless-grid, director-grid"
             columns="2">
    <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
    <h:selectManyCheckbox value="#{directorPage.directorDiscountFoodPage.selectedOrgs}" id="directorDiscountFoodGridSelectionPanel" styleClass="output-text" layout="pageDirection">
        <f:selectItem itemValue="-1" itemLabel="весь комплекс ОО"/>
        <c:forEach items="#{directorPage.directorDiscountFoodPage.organizations}" var="item">
            <f:selectItem itemValue="#{item.idOfOrg}" itemLabel="#{item.shortName} - #{item.shortAddress}" itemDisabled="#{directorPage.directorDiscountFoodPage.selectedOrgs.contains('-1')}"/>
        </c:forEach>
        <a4j:support event="onchange" reRender="directorDiscountFoodGridSelectionPanel"/>
    </h:selectManyCheckbox>
    <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
    <rich:calendar value="#{directorPage.directorDiscountFoodPage.startDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorDiscountFoodPage.startCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorDiscountFoodGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
    <rich:calendar value="#{directorPage.directorDiscountFoodPage.endDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" dataModel="#{directorPage.directorDiscountFoodPage.endCalendarModel}"
                   mode="ajax" boundaryDatesMode="scroll">
        <a4j:support event="onchanged" reRender="directorDiscountFoodGrid" ajaxSingle="true"/>
    </rich:calendar>
    <h:outputText styleClass="output-text" escape="true" value="Тип отчета"/>
    <h:selectOneMenu styleClass="input-text"
                     value="#{directorPage.directorDiscountFoodPage.reportType}">
        <f:selectItem itemValue="0" itemLabel="графический"/>
        <f:selectItem itemValue="1" itemLabel="табличный"/>
    </h:selectOneMenu>
    <a4j:commandButton value="Генерировать отчет" action="#{directorPage.directorDiscountFoodPage.buildDiscountFoodReport}"
                       reRender="workspaceTogglePanel, directorDiscountFoodGrid" styleClass="command-button"
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
<h:panelGrid id="directorDiscountFoodGraphReportGrid" styleClass="borderless-grid" columns="1"
              rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '0')}">
    <c:forEach items="#{directorPage.directorDiscountFoodPage.chartData}" var="item" varStatus="var">
        <h:panelGroup id="report-holder-${var.index}" styleClass="borderless-grid"
                      rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '0')}">
            <h:graphicImage value="#{item}" width="800" height="400" rendered="#{directorPage.directorDiscountFoodPage.showReport}" />
        </h:panelGroup>
    </c:forEach>
</h:panelGrid>
<c:if test="${directorPage.directorDiscountFoodPage.directorDiscountFoodReport.allOO ne true}">
    <c:forEach items="#{directorPage.directorDiscountFoodPage.directorDiscountFoodReport.items}" var="item" varStatus="var">
        <h:panelGrid id="directorDiscountFoodTableReportGrid-${var.index}" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <c:if test="${item.isSOSH() eq true}">
                            <rich:column headerClass="center-aligned-column" colspan="6">
                                <h:outputText styleClass="column-header" value="Предоставление льготного питания #{item.shortNameInfoService}"/>
                                <h:panelGroup rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
                                    <br />
                                </h:panelGroup>
                                <h:outputText styleClass="column-header" value="(#{item.address})" />
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" breakBefore="true" colspan="2">
                                <h:outputText styleClass="column-header" escape="true" value="Начальные"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" colspan="2">
                                <h:outputText styleClass="column-header" escape="true" value="Средние"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" colspan="2">
                                <h:outputText styleClass="column-header" escape="true" value="Старшие"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" breakBefore="true">
                                <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                            </rich:column>
                        </c:if>
                        <c:if test="${item.isDOU() eq true}">
                            <rich:column headerClass="center-aligned-column" colspan="4">
                                <h:outputText styleClass="column-header" value="Предоставление льготного питания #{item.shortNameInfoService}"/>
                                <h:panelGroup rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
                                    <br />
                                </h:panelGroup>
                                <h:outputText styleClass="column-header" value="(#{item.address})" />
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" breakBefore="true" colspan="2">
                                <h:outputText styleClass="column-header" escape="true" value="1,5 - 3 (ДОУ)"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" colspan="2">
                                <h:outputText styleClass="column-header" escape="true" value="3 - 7 (ДОУ)"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column" breakBefore="true">
                                <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                            </rich:column>
                            <rich:column headerClass="center-aligned-column">
                                <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                            </rich:column>
                        </c:if>
                    </rich:columnGroup>
                </f:facet>
                <c:if test="${item.isSOSH() eq true}">
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.privilegeYoungValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.reserveYoungValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.privilegeMiddleValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.reserveMiddleValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.privilegeElderValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.reserveElderValue}" styleClass="output-text" />
                    </rich:column>
                </c:if>
                <c:if test="${item.isDOU() eq true}">
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.privilegeDOUYoungValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.reserveDOUYoungValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.privilegeDOUElderValue}" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText value="#{item.reserveDOUElderValue}" styleClass="output-text" />
                    </rich:column>
                </c:if>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
<c:if test="${directorPage.directorDiscountFoodPage.directorDiscountFoodReport.allOO eq true}">
    <c:forEach items="#{directorPage.directorDiscountFoodPage.directorDiscountFoodReport.allOOItem}" var="item">
        <h:panelGrid id="directorDiscountFoodTableReportAllGrid" styleClass="borderless-grid" columns="1"
                      rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
            <rich:dataTable value="#{item}" footerClass="data-table-footer">
                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="10">
                            <h:outputText styleClass="column-header" value="Предоставление льготного питания #{item.shortNameInfoService}"/>
                            <h:panelGroup rendered="#{directorPage.directorDiscountFoodPage.showReport && (directorPage.directorDiscountFoodPage.reportType == '1')}">
                                <br />
                            </h:panelGroup>
                            <h:outputText styleClass="column-header" value="весь комплекс" />
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Начальные"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Средние"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="Старшие"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="1,5 - 3 (ДОУ)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText styleClass="column-header" escape="true" value="3 - 7 (ДОУ)"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column" breakBefore="true">
                            <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Льготная категория"/>
                        </rich:column>
                        <rich:column headerClass="center-aligned-column">
                            <h:outputText styleClass="column-header" escape="true" value="Резервная категория"/>
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.privilegeYoungValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.reserveYoungValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.privilegeMiddleValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.reserveMiddleValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.privilegeElderValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.reserveElderValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.privilegeDOUYoungValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.reserveDOUYoungValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.privilegeDOUElderValue}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{item.reserveDOUElderValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
            <h:panelGroup rendered="#{directorPage.directorUseCardsPage.showReport && (directorPage.directorUseCardsPage.reportType == '1')}">
                <br />
            </h:panelGroup>
        </h:panelGrid>
    </c:forEach>
</c:if>
