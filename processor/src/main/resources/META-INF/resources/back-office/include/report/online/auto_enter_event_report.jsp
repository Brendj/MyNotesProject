<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
<h:panelGrid id="autoEnterEventReportPanel" binding="#{mainPage.autoEnterEventReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Месяц" />
        <h:selectOneMenu id="month"
                         value="#{mainPage.autoEnterEventReportPage.monthYearTypeMenu.mounthType}"
                         styleClass="input-text" style="width: 100px;">
            <f:converter converterId="mouthTypeConverter" />
            <f:selectItems value="#{mainPage.autoEnterEventReportPage.monthYearTypeMenu.itemsMonth}" />
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Год" />
        <h:selectOneMenu id="year"
                         value="#{mainPage.autoEnterEventReportPage.monthYearTypeMenu.selectedYear}"
                         styleClass="input-text" style="width: 100px;">
            <f:selectItems value="#{mainPage.autoEnterEventReportPage.monthYearTypeMenu.itemsYears}" />
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.autoEnterEventReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="7" target="#{mainPage.orgSelectPage.filterMode}" />
            </a4j:commandButton>
        </h:panelGroup>

    </h:panelGrid>

    <%--<h:panelGrid styleClass="borderless-grid" columns="2">--%>
        <%--<h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />--%>
        <%--<h:selectBooleanCheckbox value="#{mainPage.autoEnterEventReportPage.allFriendlyOrgs}"--%>
                                 <%--styleClass="output-text">--%>
        <%--</h:selectBooleanCheckbox>--%>
    <%--</h:panelGrid>--%>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.autoEnterEventReportPage.clientFilter.clientGroupId}" styleClass="input-text"
                         style="width: 145px;">
            <f:selectItems value="#{mainPage.autoEnterEventReportPage.clientFilter.clientGroupsCustomItems}" />
            <a4j:support event="onchange" reRender="showDeletedClients" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Клиент" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..."
                               action="#{mainPage.showClientSelectListPage(mainPage.autoEnterEventReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{mainPage.autoEnterEventReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{mainPage.autoEnterEventReportPage.filterClient}}" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.autoEnterEventReportPage.buildReportHTML}"
                           reRender="autoEnterEventReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.autoEnterEventReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty  mainPage.autoEnterEventReportPage.htmlReport}">
            <h:outputText escape="true" value="Сводный отчет по посещению" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.autoEnterEventReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>