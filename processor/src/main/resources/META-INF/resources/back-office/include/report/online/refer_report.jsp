<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
    .region {
        font-weight: bold;
        background-color: #E3F6FF;
    }
    .overall {
        font-weight: bold;
        background-color: #D5E7F0;
    }
</style>

<%--@elvariable id="referReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ReferReportPage"--%>
<h:panelGrid id="referReportGrid" binding="#{referReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid">
        <h:panelGrid columns="2">
            <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
            <rich:calendar value="#{referReportPage.start}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
            <rich:calendar value="#{referReportPage.end}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
                <h:outputText styleClass="output-text" escape="true" value=" {#{referReportPage.filter}}" />
            </h:panelGroup>

            <h:outputText escape="true" value="Округ" styleClass="output-text" />
            <h:selectOneMenu id="regionsList" value="#{referReportPage.region}" style="width:325px;" >
                <f:selectItems value="#{referReportPage.regions}"/>
            </h:selectOneMenu>
        </h:panelGrid>

        <rich:tabPanel style="width: 500px;" switchType="client">
            <rich:tab label="Месячный отчет">
                <a4j:commandButton value="Генерировать" action="#{referReportPage.doGenerateMonthly}"
                                   reRender="workspaceTogglePanel"
                                   styleClass="command-button" status="reportGenerateStatus" />
                <h:commandButton value="Генерировать в Excel" actionListener="#{referReportPage.doGenerateMonthlyExcel}" styleClass="command-button" />
            </rich:tab>
            <rich:tab label="Дневной отчет">
                <h:panelGrid columns="2">
                    <h:outputText escape="true" value="Категория" styleClass="output-text" />
                    <h:selectOneMenu id="categoriesList" value="#{referReportPage.category}" style="width:325px;" >
                        <f:selectItems value="#{referReportPage.categories}"/>
                    </h:selectOneMenu>

                    <%--<h:outputText escape="true" value="Отображать Суточную пробу" styleClass="output-text" />
                    <h:selectBooleanCheckbox value="#{referReportPage.showDailySales}"
                                             styleClass="output-text" />--%>
                </h:panelGrid>

                <a4j:commandButton value="Генерировать" action="#{referReportPage.doGenerateDaily}"
                                   reRender="workspaceTogglePanel"
                                   styleClass="command-button" status="reportGenerateStatus" />
                <h:commandButton value="Генерировать в Excel" actionListener="#{referReportPage.doGenerateDailyExcel}" styleClass="command-button" />
            </rich:tab>
        </rich:tabPanel>

        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <c:if test="${not empty referReportPage.htmlReport}" >
            <h:outputText escape="true" value="Справка расходованных средств" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${referReportPage.htmlReport}
                </div>
            </f:verbatim>
        </c:if>

        <h:outputText escape="true" value="Не удалось найти данные по указанными параметрами. Попробуйте изменить параметры отчета" style="font-style: italic"
                      styleClass="output-text" rendered="#{referReportPage.showMissReportMessage}"/>
        <%--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />--%>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>