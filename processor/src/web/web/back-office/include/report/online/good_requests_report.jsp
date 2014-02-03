<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.bordered {
    border-top: 2px solid #000000;
    }
</style>
<script type="text/javascript">
function onstartloading(){
    jQuery(".command-button").attr('disabled', 'disabled');
    }
function onstoploading(){
    jQuery(".command-button").attr('disabled', '');
    updateWidth();
    }
jQuery(document).ready(function(){
updateWidth();
});
</script>

<h:panelGrid id="goodRequestReportPanelGrid" binding="#{mainPage.goodRequestReportPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px;"
                            opened="true" headerClass="filter-panel-header">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.goodRequestReportPage.showContragentListSelectPage}" reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="2" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.goodRequestReportPage.contragentStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                    <f:setPropertyActionListener value="Выбор контрагента" target="#{mainPage.orgFilterPageName}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value="{#{mainPage.goodRequestReportPage.contragentFilter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.goodRequestReportPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" >
                    <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.goodRequestReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.goodRequestReportPage.filter}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Наименование товара"
                          styleClass="output-text" />
            <h:inputText value="#{mainPage.goodRequestReportPage.goodName}" styleClass="input-text" size="50" />

            <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
            <rich:calendar value="#{mainPage.goodRequestReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
            <h:selectOneMenu value="#{mainPage.goodRequestReportPage.daysLimit}" converter="javax.faces.Integer"
                             styleClass="output-text" >
                <f:selectItem itemValue="0" itemLabel="1 месяц"/>
                <f:selectItem itemValue="2" itemLabel="2 недели"/>
                <f:selectItem itemValue="1" itemLabel="1 неделя"/>
            </h:selectOneMenu>
            <rich:calendar value="#{mainPage.goodRequestReportPage.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" rendered="false"/>

            <h:outputText escape="true" value="Скрывать даты с пустыми значениями"
                          styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestReportPage.hideMissedColumns}" styleClass="output-text" />

            <h:outputText escape="true" value="Фильтры по заявкам" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.goodRequestReportPage.orgsFilter}" converter="javax.faces.Integer"
            styleClass="output-text" >
                <f:selectItem itemValue="0" itemLabel="Все"/>
                <f:selectItem itemValue="1" itemLabel="Только с данными"/>
                <f:selectItem itemValue="2" itemLabel="Только пустые"/>
                <f:selectItem itemValue="-1" itemLabel="Отображать организации с отсутствием заявок за последние 2 дня"/>
            </h:selectOneMenu>

            <h:outputText escape="true" value="Суточная проба" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.goodRequestReportPage.showDailySamplesCount}" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.goodRequestReportPage.dailySamplesMode}" rendered="false" styleClass="output-text">
                <f:selectItem itemValue="0" itemLabel="Не выводить"/>
                <f:selectItem itemValue="1" itemLabel="Выводить"/>
            </h:selectOneMenu>



            <rich:hotKey key="return" handler="search();return false;"/>
            <a4j:jsFunction name="search" action="#{mainPage.buildGoodRequestReport}" status="reportGenerateStatus"
                            reRender="mainMenu, workspaceTogglePanel, goodRequestsReportTable"/>

            <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildGoodRequestReport}"
                               reRender="mainMenu, workspaceTogglePanel, goodRequestsReportTable"
                               styleClass="command-button" status="reportGenerateStatus" />

            <h:commandButton value="Генерировать отчет в Excel" actionListener="#{mainPage.exportGoodRequestReport}" styleClass="command-button" />
            <a4j:status id="reportGenerateStatus" onstart="onstartloading()" onstop="onstoploading()">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>



    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.goodRequestReportPage.goodRequestsReport && not empty mainPage.goodRequestReportPage.goodRequestsReport.goodRequestItems}" >
        <h:outputText escape="true" value="Отчет по заявкам организаций" styleClass="output-text" />
        <rich:dataTable id="goodRequestsReportTable" value="#{mainPage.goodRequestReportPage.goodRequestsReport.goodRequestItems}"
                        var="req" rowKeyVar="row" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column , left-aligned-column , right-aligned-column , left-aligned-column , center-aligned-column ">


            <rich:column styleClass="center-aligned-column" style="#{req.getRowBorderStyle()}">
                <f:facet name="header">
                    <h:outputText value="№" />
                </f:facet>
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>

            <rich:columns value="#{mainPage.goodRequestReportPage.goodRequestsReport.columnNames}" var="columnName"
                          styleClass="left-aligned-column" index="ind" headerClass="center-aligned-column"
                          style="#{req.getBackgoundColor(columnName)}; #{req.getRowBorderStyle()}">
                <f:facet name="header">
                    <h:outputText escape="true" value="#{columnName}" />
                </f:facet>
                <h:outputText style="float: left; #{req.getStyle(columnName)}" escape="true"
                              value="#{req.getRowValue(columnName, mainPage.goodRequestReportPage.dailySamplesMode)}"
                              styleClass="output-text" />
            </rich:columns>


           <%-- ECAFE-961
           <f:facet name="footer">
                <rich:datascroller for="goodRequestsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>--%>
        </rich:dataTable>
        </c:if>
        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" rendered="false"/>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>