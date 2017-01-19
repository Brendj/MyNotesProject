<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="deliveredServicesElectronicCollationReportPanelGrid"
             binding="#{mainPage.deliveredServicesElectronicCollationReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesElectronicCollationReportPage.startDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesElectronicCollationReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" />

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText
                    value="#{mainPage.deliveredServicesElectronicCollationReportPage.contragentFilter.contragent.contragentName}"
                    readonly="true" styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2," target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Контракт" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText
                    value="#{mainPage.deliveredServicesElectronicCollationReportPage.contractFilter.contract.contractName}"
                    readonly="true" styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..."
                               action="#{mainPage.deliveredServicesElectronicCollationReportPage.showContractSelectPage}"
                               reRender="modalContractSelectorPanel,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:selectOneMenu id="regionsList" value="#{mainPage.deliveredServicesElectronicCollationReportPage.region}"
                             style="width:325px;">
                <a4j:support event="onchange"
                             reRender="districtOtherCheckBox,orgDeliveredServicesSelectButton,orgDeliveredServicesOrgText"
                             ajaxSingle="true"
                             actionListener="#{mainPage.deliveredServicesElectronicCollationReportPage.resetOrg()}" />
                <f:selectItems value="#{mainPage.deliveredServicesElectronicCollationReportPage.regions}" />
            </h:selectOneMenu>
        </h:panelGroup>
        <h:outputText escape="true" value="Учитывать корпуса других округов" styleClass="output-text" />
        <h:selectBooleanCheckbox id="districtOtherCheckBox"
                                 value="#{mainPage.deliveredServicesElectronicCollationReportPage.otherRegions}"
                                 disabled="#{mainPage.deliveredServicesElectronicCollationReportPage.emptyRegion()}"
                                 title="Учитывать корпуса других округов">
            <a4j:support event="onchange" ajaxSingle="true" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..."
                               action="#{mainPage.deliveredServicesElectronicCollationReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel,withoutFriendlyBuildingggsCheckBox"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" disabled="#{!mainPage.deliveredServicesElectronicCollationReportPage.emptyRegion() || !mainPage.deliveredServicesReportPage.emptyContract()
                               || !mainPage.deliveredServicesElectronicCollationReportPage.emptyContragent()}"
                               id="orgDeliveredServicesSelectButton">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.supplierFilter}" />
                <f:setPropertyActionListener
                        value="#{mainPage.deliveredServicesElectronicCollationReportPage.getStringIdOfOrgList}"
                        target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.deliveredServicesElectronicCollationReportPage.filter
            != mainPage.deliveredServicesElectronicCollationReportPage.FILTER_SUPER ?
            mainPage.deliveredServicesElectronicCollationReportPage.filter : mainPage.deliveredServicesElectronicCollationReportPage.FILTER_INIT}}"
                          id="orgDeliveredServicesOrgText" />
        </h:panelGroup>

        <h:outputText escape="true" value="Формировать только по выбранным корпусам" styleClass="output-text" />
        <h:selectBooleanCheckbox id="withoutFriendlyBuildingggsCheckBox" value="#{mainPage.deliveredServicesElectronicCollationReportPage.withoutFriendly}"
                                 disabled="#{mainPage.deliveredServicesElectronicCollationReportPage.emptyOrgs()}" title="Формировать только по выбранным корпусам">
            <a4j:support event="onchange" ajaxSingle="true" />
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="4">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildDeliveredServicesElectronicCollationReport}"
                           reRender="workspaceTogglePanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.deliveredServicesElectronicCollationReportPage.showCSVList}" styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{mainPage.deliveredServicesElectronicCollationReportPage.clear}"
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
        <c:if test="${not empty mainPage.deliveredServicesElectronicCollationReportPage.deliveredServicesReport && not empty mainPage.deliveredServicesElectronicCollationReportPage.deliveredServicesReport.htmlReport}">
            <h:outputText escape="true" value="Сводный отчет по услугам" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.deliveredServicesElectronicCollationReportPage.deliveredServicesReport.htmlReport} </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>

</h:panelGrid>