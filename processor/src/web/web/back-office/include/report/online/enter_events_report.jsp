<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="enterEventsReportPanelGrid" binding="#{electionsPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="false" value="Показать только организации с УИК" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showElectionAreaOnly}" styleClass="output-text">
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:outputText escape="false" value="Фильтры (заполняются через запятую без пробела):" styleClass="output-text" />

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="УИК" styleClass="output-text" />
        <h:inputText value="#{electionsPage.enterEventsMonitoringReportPage.UIKfilter}" maxlength="300"
                     styleClass="input-text" />

        <h:outputText escape="true" value="ИД организации" styleClass="output-text" />
        <h:inputText value="#{electionsPage.enterEventsMonitoringReportPage.idOfOrgFilter}" maxlength="300"
                     styleClass="input-text" />

        <h:outputText escape="true" value="Адрес школы" styleClass="output-text" />
        <h:inputText value="#{electionsPage.enterEventsMonitoringReportPage.addressFilter}" maxlength="500"
                     styleClass="input-text" />

        <h:outputText escape="true" value="Название школы" styleClass="output-text" />
        <h:inputText value="#{electionsPage.enterEventsMonitoringReportPage.orgNameFilter}" maxlength="500"
                     styleClass="input-text" />

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="false" value="Показать данные со статусом 1 (зеленый цвет) - последний вход был в последние 10 минут" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showStatus1}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать данные со статусом 2 (желтый цвет) - последний вход был в последние 30 минут" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showStatus2}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать данные со статусом 3 (розовый цвет) - последний вход был более 30 минут назад" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showStatus3}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать данные со статусом 4 (голубой цвет) - сегодня входов не было, но турникет работает" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showStatus4}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Показать данные со статусом 5 (серый цвет) - сегодня входов не было, турникет не работает" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showStatus5}" styleClass="output-text">
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:outputText escape="false" value="Примечание: для столбцов \"Последняя синхронизация\" и \"Последний вход\" зеленый цвет - в течении 5 минут была активность, " styleClass="output-text" />
    <h:outputText escape="false" value="желтый цвет - в течении 10 минут была активность, розовый цвет - больше 10 минут не было активности." styleClass="output-text" />

    <h:panelGrid styleClass="borderless-grid" columns="4" id="blah-blah">
        <a4j:commandButton value="Обновить" action="#{electionsPage.enterEventsMonitoringReportPage.buildReportHTML}"
                           reRender="enterEventsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{electionsPage.enterEventsMonitoringReportPage.exportToXLS}" styleClass="command-button" />

        <a4j:commandButton value="Очистить фильтры" action="#{electionsPage.enterEventsMonitoringReportPage.clear()}"
                           reRender="enterEventsReportPanelGrid,enterEventsReportTable" ajaxSingle="true" styleClass="command-button" />

        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid" id="enterEventsReportTable">
        <c:if test="${not empty electionsPage.enterEventsMonitoringReportPage.htmlReport}">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${electionsPage.enterEventsMonitoringReportPage.htmlReport} </div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>