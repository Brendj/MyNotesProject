<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="clientBalanceByOrgReportPanelGrid" binding="#{mainPage.clientBalanceByOrgReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Дата" />
        <rich:calendar value="#{mainPage.clientBalanceByOrgReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Поставщик" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.clientBalanceByOrgReportPage.contragent.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.clientBalanceByOrgReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.clientBalanceByOrgReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.clientBalanceByOrgReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientBalanceByOrgReportPage.clientFilter.clientGroupId}"
                         styleClass="input-text">
            <f:selectItems value="#{mainPage.clientBalanceByOrgReportPage.clientFilter.clientGroupItems}" />
            <a4j:support event="onchange" reRender="clientBalanceByOrgReportPanelGrid" />
        </h:selectOneMenu>
    </h:panelGrid>
    <h:panelGroup>
        <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientBalanceByOrgReportPage.clientFilter.clientBalanceCondition}"
                         styleClass="input-text" style="margin-left: 10px; width: 100px;">
            <f:selectItems value="#{mainPage.clientBalanceByOrgReportPage.clientFilter.clientBalanceMenu.items}" />
            <a4j:support event="onchange" reRender="clientBalanceByOrgReportPanelGrid" />
        </h:selectOneMenu>
    </h:panelGroup>


    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.clientBalanceByOrgReportPage.exportToHtmlOnePerUser}"
                           reRender="clientBalanceByOrgReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.clientBalanceByOrgReportPage.exportToXLSOnePerUser}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="clientBalanceByOrgReportTable">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.clientBalanceByOrgReportPage.clientBalanceByOrg && not empty mainPage.clientBalanceByOrgReportPage.clientBalanceByOrg.htmlReport}" >
            <h:outputText escape="true" value="Отчет по остаткам денежных средств на карточках учащихся" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${mainPage.clientBalanceByOrgReportPage.clientBalanceByOrg.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>