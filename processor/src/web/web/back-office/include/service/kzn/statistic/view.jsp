<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    function onstartloading() {
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading() {
        jQuery(".command-button").attr('disabled', '');
        updateWidth();
    }
    jQuery(document).ready(function () {
        updateWidth();
    });
</script>

<%--@elvariable id="kznClientsStatisticViewPage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticViewPage"--%>
<h:panelGrid id="kznClientsStatisticPanelGrid" binding="#{kznClientsStatisticViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGroup id="orgFilter">
        <a4j:commandButton value="..." action="#{kznClientsStatisticViewPage.showOrgListSelectPage()}"
                           reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
            <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.supplierFilter}" />
            <f:setPropertyActionListener value="#{kznClientsStatisticViewPage.getStringIdOfOrgList}"
                                         target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true"
                      value=" {#{kznClientsStatisticViewPage.filter}}" />
    </h:panelGroup>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сформировать" action="#{kznClientsStatisticViewPage.update()}"
                           reRender="kznClientsStatisticPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton" />
        <a4j:commandButton value="Добавить" reRender="kznClientsStatisticAddingPanel" ajaxSingle="true"
                           action="#{kznClientsStatisticViewPage.showAddingModalPage()}"
                           oncomplete="Richfaces.showModalPanel('kznClientsStatisticAddingPanel');"/>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="applicationForFoodTable" value="#{kznClientsStatisticViewPage.items}" var="item" rows="25"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Школа" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Общее количество учащихся" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество учащихся 1-4 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество учащихся 5-9 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество учащихся 10-11 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество льготников 1-4 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество льготников 5-9 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество льготников 10-11 классов" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество льготников всего" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество сотрудников, администрации и прочих групп" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.studentsCountTotal}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.studentsCountYoung}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.studentsCountMiddle}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.studentsCountOld}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.benefitStudentsCountYoung}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.benefitStudentsCountMiddle}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.benefitStudentsCountOld}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.benefitStudentsCountTotal}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.employeeCount}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>