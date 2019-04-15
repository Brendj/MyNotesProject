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

<%--@elvariable id="kznClientsStatisticReportPage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticReportPage"--%>
<h:panelGrid id="kznClientsStatisticPanelGrid" binding="#{kznClientsStatisticReportPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:modalPanel id="kznClientsStatisticPanel" autosized="true" minWidth="500">
        <f:facet name="header">
            <h:outputText value="#{kznClientsStatisticReportPage.currentItem.orgName}" />
        </f:facet>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Общее количество учащихся" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.studentsCountTotal}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 1-4 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.studentsCountYoung}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 5-9 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.studentsCountMiddle}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 10-11 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.studentsCountOld}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 1-4 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.benefitStudentsCountYoung}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 5-9 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.benefitStudentsCountMiddle}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 10-11 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.benefitStudentsCountOld}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников всего" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.benefitStudentsCountTotal}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество сотрудников, администрации и прочих групп" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.currentItem.employeeCount}" rendered="#{kznClientsStatisticReportPage.currentItem != null}" styleClass="input-text" />
        </h:panelGrid>
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('kznClientsStatisticPanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>

    <rich:modalPanel id="kznClientsStatisticAddingPanel" autosized="true" minWidth="500">
        <f:facet name="header">
            <h:outputText value="#{kznClientsStatisticReportPage.currentItem.orgName}" />
        </f:facet>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage()}"
                               reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgSelectPage.filterMode}" />
                <f:setPropertyActionListener value="1" target="#{mainPage.orgSelectPage.supplierFilter}" />
                <f:setPropertyActionListener value="#{kznClientsStatisticReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{kznClientsStatisticReportPage.filter}}" />
            <h:outputText escape="true" value="Общее количество учащихся" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.studentsCountTotal}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 1-4 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.studentsCountYoung}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 5-9 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.studentsCountMiddle}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество учеников 10-11 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.studentsCountOld}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 1-4 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.benefitStudentsCountYoung}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 5-9 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.benefitStudentsCountMiddle}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников 10-11 классов" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.benefitStudentsCountOld}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество льготников всего" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.benefitStudentsCountTotal}" styleClass="input-text" />
            <h:outputText escape="true" value="Количество сотрудников, администрации и прочих групп" styleClass="output-text"/>
            <h:inputSecret value="#{kznClientsStatisticReportPage.addingItem.employeeCount}" styleClass="input-text" />
        </h:panelGrid>
        <rich:spacer height="20px" />
        <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('kznClientsStatisticAddingPanel')" style="width: 180px;" ajaxSingle="true" />
    </rich:modalPanel>

    <h:panelGroup id="orgFilter">
        <a4j:commandButton value="..." action="#{kznClientsStatisticReportPage.showOrgListSelectPage()}"
                           reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
            <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.supplierFilter}" />
            <f:setPropertyActionListener value="#{kznClientsStatisticReportPage.getStringIdOfOrgList}"
                                         target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true"
                      value=" {#{kznClientsStatisticReportPage.filter}}" />
    </h:panelGroup>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сформировать" action="#{kznClientsStatisticReportPage.update()}"
                           reRender="kznClientsStatisticPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="reloadButton" />
        <a4j:commandButton value="Добавить" reRender="kznClientsStatisticAddingPanel" ajaxSingle="true"
                           action="#{kznClientsStatisticReportPage.showAddingModalPage()}"
                           oncomplete="Richfaces.showModalPanel('kznClientsStatisticAddingPanel');"/>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="applicationForFoodTable" value="#{kznClientsStatisticReportPage.items}" var="item" rows="25"
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
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Редактировать" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Удалить" />
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
        <rich:column headerClass="column-header">
            <a4j:commandButton value="..." reRender="kznClientsStatisticPanel" ajaxSingle="true"
                               oncomplete="Richfaces.showModalPanel('kznClientsStatisticPanel');">
                <f:setPropertyActionListener value="#{item}" target="#{kznClientsStatisticReportPage.currentItem}" />
            </a4j:commandButton>
        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandLink reRender="applicationForFoodTable" value="x"
                             action="#{kznClientsStatisticReportPage.delete()}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{kznClientsStatisticReportPage.currentItem}" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>

</h:panelGrid>