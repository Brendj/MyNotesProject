<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка организаций --%>
<rich:dataTable id="orgListTable" binding="#{mainPage.orgListPage.pageComponent}" value="#{mainPage.orgListPage.items}"
                var="item" rows="20" footerClass="data-table-footer"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Организация" />
        </f:facet>
        <h:commandLink value="#{item.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Статус" />
        </f:facet>
        <h:outputText escape="true" value="#{item.state}" converter="orgStateConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Номер договора" />
        </f:facet>
        <h:outputText escape="true" value="#{item.contractId}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Контактный телефон" />
        </f:facet>
        <h:outputText escape="true" value="#{item.phone}" converter="phoneConverter" styleClass="output-text" />
    </rich:column>

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Контактный телефон" />
        </f:facet>
        <h:outputText escape="true" value="#{item.phone}" converter="phoneConverter" styleClass="output-text" />
    </rich:column>

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <h:commandLink action="#{mainPage.showOrgEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </h:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="orgListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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