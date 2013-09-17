<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.output-text-mod {
    font-family: Tahoma, Arial, Sans-Serif;
    font-size: 10pt;
    color: #000;
    white-space: nowrap;
}
.topAligned {
    vertical-align: top;
}
</style>

<%--@elvariable id="reportPage" type="ru.axetta.ecafe.processor.web.ui.report.ReportPage"--%>
<h:panelGrid id="reportGrid" binding="#{reportPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">

    <h:panelGrid id="messages">
        <h:outputText value="#{reportPage.errorMessages}" style="color: #FF0000" styleClass="messages" rendered="#{not empty reportPage.errorMessages}"/>
        <h:outputText value="#{reportPage.infoMessages}" styleClass="messages" rendered="#{not empty reportPage.infoMessages}"/>
    </h:panelGrid>

    <h:panelGrid id="params">
        <a4j:form id="reportForm">
        <a4j:region>
        <rich:simpleTogglePanel label="Настройки отчета" switchType="client" style="width: 800px; max-width: 800px"
                                opened="#{reportPage.displaySettings}" headerClass="filter-panel-header">
            <h:panelGrid styleClass="borderless-grid" columns="2" columnClasses="topAligned">
                <a4j:region>
                    <h:panelGrid styleClass="borderless-grid" columnClasses="topAligned">
                        <h:outputText value="Выберите отчет:" styleClass="output-text"/>
                        <h:selectOneListbox id="subscriptions" valueChangeListener="#{reportPage.doChangeReport}"
                                            value="#{reportPage.report}" style="width:300px;" size="10">
                            <f:selectItems value="#{reportPage.reports}"/>
                            <%-- workspaceSubView:workspaceForm:workspacePageSubView:manualMainParams --%>
                            <a4j:support event="onselect" reRender="messages,manualParamHints" status="generateReportStatus"/>
                            <a4j:support event="onchange" reRender="messages,manualParamHints" status="generateReportStatus"/>
                        </h:selectOneListbox>
                    </h:panelGrid>
                </a4j:region>

                <h:panelGrid styleClass="borderless-grid">
                    <h:panelGrid id="manualMainParams" styleClass="borderless-grid" columns="2">
                        <h:outputText value="Дата выборки от:" styleClass="output-text"/>
                        <rich:calendar value="#{reportPage.generateStartDate}" popup="true"/>
                        <h:outputText value="Дата выборки до:" styleClass="output-text"/>
                        <rich:calendar value="#{reportPage.generateEndDate}" popup="true"/>

                        <%--<h:outputText escape="true" value="Формат отчета" styleClass="output-text" />
                        <h:selectOneMenu value="#{manualReportRunnerPage.documentFormat}" styleClass="input-text">
                            <f:selectItems value="#{manualReportRunnerPage.reportFormatMenu.items}" />
                        </h:selectOneMenu>--%>
                    </h:panelGrid>

                    <h:panelGrid id="manualParamHints" styleClass="borderless-grid">
                        <rich:dataTable value="#{reportPage.paramHints}" var="item" rendered="#{reportPage.renderParamHints}"
                                        columnClasses="left-aligned-column, left-aligned-column">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Параметры отчета" styleClass="output-text" style="color: #FFFFFF" />
                            </f:facet>
                            <rich:column>
                                <h:outputText escape="true" value="#{item.hint.paramHint.name}" styleClass="output-text" rendered="#{reportPage.renderElement(item)}" />
                                <h:outputText escape="true" value="*" rendered="#{reportPage.renderElement(item) && item.hint.required}" style="color: #FF0000; font-weight: bold; #{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" />
                            </rich:column>
                            <rich:column>
                                <h:outputText escape="true" value="#{item.hint.paramHint.description}" rendered="#{reportPage.renderElement(item)}" styleClass="output-text" style="#{manualReportRunnerPage.displayElement(item)};" />
                            </rich:column>
                            <rich:column>
                                <h:selectOneMenu rendered="#{reportPage.renderElement(item) && item.type=='combobox'}" style="#{manualReportRunnerPage.displayElement(item)};"
                                                 styleClass="output-text" value="#{item.value}">
                                    <f:selectItems value="#{item.listItems}"/>
                                </h:selectOneMenu>

                                <h:selectOneRadio rendered="#{reportPage.renderElement(item) && item.type=='radio'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" value="#{item.value}">
                                    <f:selectItems value="#{item.listItems}"/>
                                </h:selectOneRadio>

                                <h:selectManyCheckbox rendered="#{reportPage.renderElement(item) && item.type=='checkbox'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" value="#{item.valueItems}">
                                    <f:selectItems value="#{item.listItems}"/>
                                </h:selectManyCheckbox>

                                <h:inputText value="#{item.value}" rendered="#{reportPage.renderElement(item) && item.type=='input'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" />

                                <h:outputText escape="true" value="#{item.value}" style="#{manualReportRunnerPage.displayElement(item)};" rendered="#{reportPage.renderElement(item) && item.type=='output'}" styleClass="output-text" />
                            </rich:column>
                        </rich:dataTable>
                    </h:panelGrid>
                </h:panelGrid>
            </h:panelGrid>

            <h:panelGrid columns="2">
                <a4j:commandButton value="Сформировать" action="#{reportPage.doGenerateReport}" styleClass="command-button" status="generateReportStatus" reRender="messages,params,reportContent"/>
                <a4j:status id="generateReportStatus">
                    <f:facet name="start">
                        <h:panelGrid columns="2">
                            <h:outputText value="Подготовка отчета... " styleClass="output-text-mod"/>
                            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                        </h:panelGrid>
                    </f:facet>
                </a4j:status>
            </h:panelGrid>
        </rich:simpleTogglePanel>
        </a4j:region>
        </a4j:form>

        <h:commandLink value="Сохранить сформированный отчет в Excel" style="color: #0000ff;" action="#{reportPage.doExportToExcel}"
                       styleClass="command-button" rendered="#{not empty reportPage.previousPrint}"/>
    </h:panelGrid>


    <h:panelGrid id="reportContent" styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty reportPage.reportHtml}" >

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${reportPage.reportHtml}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
</h:panelGrid>