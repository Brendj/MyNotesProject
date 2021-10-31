<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardGroupPage"--%>
<%--@elvariable id="employeeCardListPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardListPage"--%>
<%--@elvariable id="employeeCardViewPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardViewPage"--%>
<%--@elvariable id="employeeCardEditPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeCardEditPage"--%>
<h:panelGrid id="employeeListGrid" binding="#{employeeCardListPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable id="employeeCardTable" value="#{employeeCardListPage.cards}" var="card" rows="15"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeCardViewPage.show}" styleClass="command-link">
                <h:outputText escape="true" value="#{card.cardNo}" converter="cardNoConverter" styleClass="output-text" />
                <f:setPropertyActionListener value="#{card}" target="#{employeeCardGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Владелец" />
            </f:facet>
            <h:outputText escape="true" value="#{card.visitorItem.fullName}" styleClass="output-text" rendered="#{card.visitorItem!=null}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{card.createDate}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{employeeCardEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{card}" target="#{employeeCardGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="employeeCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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