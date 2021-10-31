<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="issuedCardsReportPagePanelGrid" binding="#{mainPage.issuedCardsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Пользователь" styleClass="output-text" />
        <h:panelGroup id="userFilter">
            <a4j:commandButton value="..." action="#{mainPage.issuedCardsReportPage.showUserSelectPage()}"
                               reRender="modalUserListSelectorPanel,userFilter"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalUserListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.userListSelectPage.filter}" target="#{mainPage.userListSelectPage.filter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedUser"
                          value=" {#{mainPage.issuedCardsReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.issuedCardsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,issuedCardsReportPagePanel"
                         actionListener="#{mainPage.issuedCardsReportPage.onReportPeriodChanged}" />
        </rich:calendar>
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.issuedCardsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,issuedCardsReportPagePanel"
                         actionListener="#{mainPage.issuedCardsReportPage.onEndDateSpecified}" />
        </rich:calendar>
        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.issuedCardsReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 200px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.issuedCardsReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,issuedCardsReportPagePanelGrid"
                         actionListener="#{mainPage.issuedCardsReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.issuedCardsReportPage.buildReportHTML}"
                           reRender="issuedCardsReportPagePanel" styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.issuedCardsReportPage.exportToXLS}"
                         styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="issuedCardsReportPagePanel">
        <c:if test="${not empty mainPage.issuedCardsReportPage.htmlReport}">
            <h:outputText escape="true" value="#{mainPage.issuedCardsReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.issuedCardsReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>