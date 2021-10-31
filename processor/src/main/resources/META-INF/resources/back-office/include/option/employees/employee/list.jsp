<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeGroupPage"--%>
<%--@elvariable id="employeeListPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeListPage"--%>
<%--@elvariable id="employeeViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeViewPage"--%>
<%--@elvariable id="employeeEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeEditPage"--%>
<h:panelGrid id="employeeListGrid" binding="#{employeeListPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Показывать" styleClass="output-text" />
        <h:selectOneMenu value="#{employeeListPage.showMode}" styleClass="output-text">
            <f:selectItem itemValue="0" itemLabel="Всех" />
            <f:selectItem itemValue="1" itemLabel="Актуальных" />
            <f:selectItem itemValue="2" itemLabel="Удаленных" />
            <a4j:support event="onchange" action="#{employeeListPage.show}" reRender="employeeListTable" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="4">
        <h:outputText escape="true" value="Фильтр" styleClass="output-text" />
        <h:inputText value="#{employeeListPage.filter}" styleClass="input-text" id="employeeListPageFilterText" />
        <a4j:commandButton value="Применить" action="#{employeeListPage.show}"
                           reRender="employeeListTable" styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{employeeListPage.clearFilter}"
                           reRender="employeeListTable,employeeListPageFilterText" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

    <rich:dataTable id="employeeListTable" value="#{employeeListPage.employees}" var="employee" rows="20" rowKeyVar="row"
                    columnClasses="right-aligned-column,
                    right-aligned-column, right-aligned-column, right-aligned-column,
                     center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                     center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header" rowspan="2">
                    <h:outputText escape="true" value="№ п/п" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="2">
                    <h:outputText escape="true" value="Фамилия" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="2">
                    <h:outputText escape="true" value="Имя" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="2">
                    <h:outputText escape="true" value="Отчество" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="2">
                    <h:outputText escape="true" value="Должность" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="2">
                    <h:outputText escape="true" value="Паспорт" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="2">
                    <h:outputText escape="true" value="Водительское удостоверение" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="2">
                    <h:outputText escape="true" value="Военный билет" />
                </rich:column>

                <rich:column headerClass="column-header" rowspan="2" colspan="1">
                    <h:outputText escape="true" value="Редактировать" />
                </rich:column>

                <rich:column headerClass="column-header" rowspan="2" colspan="1">
                    <h:outputText escape="true" value="Удалить" />
                </rich:column>

                <rich:column breakBefore="true" headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Дата выдачи" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Номер" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Дата выдачи" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Номер" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Дата выдачи" />
                </rich:column>
                <rich:column headerClass="column-header" rowspan="1" colspan="1">
                    <h:outputText escape="true" value="Номер" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandLink value="#{employee.surname}" reRender="mainMenu, workspaceTogglePanel"
                             action="#{employeeViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{employee}" target="#{employeeGroupPage.currentEmployee}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.firstName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.secondName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.position}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.passportDate}" converter="dateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.passportNumber}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.driverLicenceDate}" converter="dateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.driverLicenceNumber}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.warTicketDate}" converter="dateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{employee.warTicketNumber}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header">
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeEditPage.show}"
                             styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{employee}" target="#{employeeGroupPage.currentEmployee}" />
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header">
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeListPage.deleteEmployee}"
                             styleClass="command-link" rendered="#{not employee.deleted}">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{employee}" target="#{employeeGroupPage.currentEmployee}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="employeeListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>