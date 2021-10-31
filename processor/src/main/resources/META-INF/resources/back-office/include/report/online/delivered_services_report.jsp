<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="deliveredServicesReportPanelGrid" binding="#{mainPage.deliveredServicesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.deliveredServicesReportPage.contragentFilter.contragent.contragentName}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2,"
                                             target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Контракт" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.deliveredServicesReportPage.contractFilter.contract.contractName}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.deliveredServicesReportPage.showContractSelectPage}"
                               reRender="modalContractSelectorPanel,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value=""
                                             target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:selectOneMenu id="regionsList" value="#{mainPage.deliveredServicesReportPage.region}" style="width:325px;" >
                <a4j:support event="onchange" reRender="districtOtherCheckBox,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText" ajaxSingle="true"
                             actionListener="#{mainPage.deliveredServicesReportPage.resetOrg()}"/>
                <f:selectItems value="#{mainPage.deliveredServicesReportPage.regions}"/>
            </h:selectOneMenu>
        </h:panelGroup>
        <h:outputText escape="true" value="Учитывать корпуса других округов" styleClass="output-text" />
        <h:selectBooleanCheckbox id="districtOtherCheckBox" value="#{mainPage.deliveredServicesReportPage.otherRegions}"
            disabled="#{mainPage.deliveredServicesReportPage.emptyRegion()}" title="Учитывать корпуса других округов">
            <a4j:support event="onchange" ajaxSingle="true" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.deliveredServicesReportPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel,withoutFriendlyBuildinggsCheckBox"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;"
                               disabled="#{!mainPage.deliveredServicesReportPage.emptyRegion() || !mainPage.deliveredServicesReportPage.emptyContract()
                               || !mainPage.deliveredServicesReportPage.emptyContragent()}"
                               id="orgDeliveredServicesSelectButton">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.deliveredServicesReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.deliveredServicesReportPage.filter != mainPage.deliveredServicesReportPage.FILTER_SUPER ?
            mainPage.deliveredServicesReportPage.filter : mainPage.deliveredServicesReportPage.FILTER_INIT}}"
                          id="orgDeliveredServicesOrgText"/>
        </h:panelGroup>

        <h:outputText escape="true" value="Формировать только по выбранным корпусам" styleClass="output-text" />
        <h:selectBooleanCheckbox id="withoutFriendlyBuildinggsCheckBox" value="#{mainPage.deliveredServicesReportPage.withoutFriendly}"
                                 disabled="#{mainPage.deliveredServicesReportPage.emptyOrgs()}" title="Формировать только по выбранным корпусам">
            <a4j:support event="onchange" ajaxSingle="true" />
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="4">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildDeliveredServicesReport}"
                           reRender="workspaceTogglePanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.deliveredServicesReportPage.showCSVList}" styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{mainPage.deliveredServicesReportPage.clear}"
                           reRender="workspaceTogglePanel" styleClass="command-button"
                           status="reportGenerateStatus" id="clearButton" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.deliveredServicesReportPage.deliveredServicesReport && not empty mainPage.deliveredServicesReportPage.deliveredServicesReport.htmlReport}" >
            <h:outputText escape="true" value="Отчет по оказанным услугам (предварительный)" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${mainPage.deliveredServicesReportPage.deliveredServicesReport.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>

</h:panelGrid>