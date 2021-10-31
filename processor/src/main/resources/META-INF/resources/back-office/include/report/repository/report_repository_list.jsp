<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToWorkOnlineReport())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%--@elvariable id="reportRepositoryListPage" type="ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryListPage"--%>
<f:verbatim>
<script language="javascript">
function checkReporitoryDate () {
    var startDate = RichFaces.$('startDate').currentDate.getTime();
    var endDate = RichFaces.$('endDate').currentDate.getTime();
    alert (startDate + " :: " + endDate);
    RichFaces.$('startDate').currentDate.setTime(endDate);
    RichFaces.$('startDate').currentDate.setTime(startDate);
}
</script>
</f:verbatim>
<h:panelGrid id="reportRepListPanelGrid" binding="#{reportRepositoryListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр (#{reportRepositoryListPage.filter.status})" switchType="client" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:panelGrid columns="1">
                <h:outputText escape="true" value="Название правила" styleClass="output-text" />
                <h:selectOneListbox value="#{reportRepositoryListPage.filter.ruleName}" style="width:400px; height: 200px" >
                    <f:selectItems value="#{reportRepositoryListPage.ruleNameItems}"/>
                </h:selectOneListbox>
                <%--<rich:comboBox value="#{reportRepositoryListPage.filter.ruleName}" width="400px" styleClass="input-text">
                    <f:selectItems value="#{reportRepositoryListPage.ruleNameItems}" />
                </rich:comboBox>--%>
            </h:panelGrid>
            <h:panelGrid columns="2">
                <h:outputText escape="true" value="Название отчета" styleClass="output-text" />
                <h:inputText value="#{reportRepositoryListPage.filter.reportName}" styleClass="input-text" />
                <h:outputText escape="true" value="Организации" styleClass="output-text" />
                <h:panelGroup styleClass="borderless-div">
                    <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" >
                        <f:setPropertyActionListener value="#{reportRepositoryListPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
                    </a4j:commandButton>
                    <h:outputText styleClass="output-text" escape="true" value=" {#{reportRepositoryListPage.orgsFilter}}" />
                </h:panelGroup>

                <h:outputText escape="true" value="Дата создания" styleClass="output-text" />
                <rich:calendar value="#{reportRepositoryListPage.filter.createdDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
                <rich:calendar id="startDate" value="#{reportRepositoryListPage.filter.startDate}"
                               onchanged="if(#{rich:component('startDate')}.getSelectedDate().getTime() > #{rich:component('endDate')}.getSelectedDate().getTime()) { #{rich:component('startDate')}.selectDate(#{rich:component('endDate')}.getSelectedDate()) }"
                               datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
                <rich:calendar id="endDate" onchanged="if(#{rich:component('startDate')}.getSelectedDate().getTime() > #{rich:component('endDate')}.getSelectedDate().getTime()) { #{rich:component('startDate')}.selectDate(#{rich:component('endDate')}.getSelectedDate()) }"
                               value="#{reportRepositoryListPage.filter.endDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            </h:panelGrid>
        </h:panelGrid>
<%----%>
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{reportRepositoryListPage.reload}"
                               reRender="workspaceTogglePanel" styleClass="command-button" status="repositoryStatus" />
            <a4j:commandButton value="Очистить" action="#{reportRepositoryListPage.resetFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            <a4j:status id="repositoryStatus">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
        </h:panelGrid>
    </rich:simpleTogglePanel>
        <rich:modalPanel id="errorPanel" width="600" height="600" style="overflow: scroll;">
            <f:facet name="header">
                <h:outputText value="Текст ошибки" />
            </f:facet>
            <f:facet name="controls">
                <a4j:commandLink onclick="Richfaces.hideModalPanel('errorPanel')" reRender="this" style="color: white;">
                    <h:outputText value="Закрыть" />
                </a4j:commandLink>
            </f:facet>
            <h:outputText value="#{reportRepositoryListPage.displayedError}" />
        </rich:modalPanel>
    <rich:dataTable id="contractListTable" value="#{reportRepositoryListPage.itemList}" var="item" rows="50"
                    footerClass="data-table-footer"
                    columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. организации" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Правило" />
            </f:facet>
            <h:outputText escape="true" value="#{item.ruleName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Название" />
            </f:facet>
            <h:outputText escape="true" value="#{item.reportName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер организации" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgNum}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.createStateStyle}">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createState}" styleClass="#{item.createStateStyle}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Формат" />
            </f:facet>
            <h:outputText escape="true" value="#{item.documentFormatAsString}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createdDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm:ss" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время формирования" />
            </f:facet>
            <h:outputText escape="true" value="#{item.generationTime} мс." styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата выборки от" />
            </f:facet>
            <h:outputText escape="true" value="#{item.startDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата выборки до" />
            </f:facet>
            <h:outputText escape="true" value="#{item.endDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Загрузить файл" />
            </f:facet>
            <a4j:commandLink value="#{item.reportFile}" action="#{reportRepositoryListPage.downloadReportFile}"
                             styleClass="command-link" reRender="mainMenu, workspaceForm">
                <f:setPropertyActionListener value="#{item}" target="#{reportRepositoryListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ошибка" />
            </f:facet>
            <a4j:commandLink value="Текст ошибки" rendered="#{item.errorButtonRendered}" ajaxSingle="true" oncomplete="Richfaces.showModalPanel('errorPanel');"
                    reRender="errorPanel" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.errorString}" target="#{reportRepositoryListPage.displayedError}" />
            </a4j:commandLink>
            <%--<h:outputText escape="true" value="#{item.errorString}" styleClass="output-text"  />--%>
        </rich:column>

        <rich:column headerClass="column-header" rendered="#{reportRepositoryListPage.canDelete}">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             reRender="uvDeleteConfirmPanel"
                             action="#{uvDeletePage.show}"
                             oncomplete="#{rich:component('uvDeleteConfirmPanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{uvDeletePage.currentEntityItem}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="contractListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>