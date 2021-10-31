<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="latePaymentReportPanelGrid" binding="#{mainPage.latePaymentReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid id="lateParametrsGrid" styleClass="borderless-grid" columns="2">

            <%--<h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.latePaymentReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.latePaymentReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.latePaymentReportPage.filter}}" />
            </h:panelGroup>--%>
            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.latePaymentReportPage.filter}" readonly="true"
                             styleClass="input-text long-field" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.latePaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar"
                             actionListener="#{mainPage.latePaymentReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.latePaymentReportPage.periodTypeMenu.periodType}" styleClass="input-text"
                             style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.latePaymentReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar"
                             actionListener="#{mainPage.latePaymentReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.latePaymentReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                             actionListener="#{mainPage.latePaymentReportPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Количество несвоевременной оплаты за 1 день" />
            <h:panelGroup>
                <h:selectOneMenu value="#{mainPage.latePaymentReportPage.latePaymentByOneDayCountType}"
                                 styleClass="input-text" style="width: 250px;">
                    <f:converter converterId="latePaymentByOneDayCountConverter" />
                    <f:selectItems value="#{mainPage.latePaymentReportPage.latePaymentByOneDayCountTypeMenu.items}" />
                </h:selectOneMenu>
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Количество дней несвоевременной оплаты" />
            <h:panelGroup>
                <h:selectOneMenu value="#{mainPage.latePaymentReportPage.latePaymentDaysCountType}"
                                 styleClass="input-text" style="width: 250px;">
                    <f:converter converterId="latePaymentDaysCountTypeConverter" />
                    <f:selectItems value="#{mainPage.latePaymentReportPage.latePaymentDaysCountTypeMenu.items}" />
                </h:selectOneMenu>
            </h:panelGroup>
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.latePaymentReportPage.buildReportHTML}"
                           reRender="latePaymentReportPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.latePaymentReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{mainPage.latePaymentReportPage.clear}"
                           reRender="latePaymentReportPanelGrid, lateParametrsGrid" styleClass="command-button"
                           status="reportGenerateStatus" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.latePaymentReportPage.htmlReport}">
            <h:outputText escape="true" value="Сводный отчет по несвоевременной оплате питания"
                          styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.latePaymentReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

</h:panelGrid>