<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>


<rich:dataTable id="categoryOrgListTable" binding="#{categoryOrgListPage.pageComponent}"
                    value="#{categoryOrgListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Названия категорий" />
        </f:facet>
        <h:outputText escape="true" styleClass="output-text" value="#{item.categoryName}" />
    </rich:column>

    <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditCategory}">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{categoryOrgEditPage.show}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item}" target="#{categoryOrgEditPage.entity}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditCategory}"
                 style="text-align:center">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('confirmDeletePanel')}.show();">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{categoryOrgListPage}"
                                         target="#{confirmDeletePage.listener}" />
            <f:setPropertyActionListener value="#{item}"
                                         target="#{confirmDeletePage.entity}" />
        </a4j:commandLink>
    </rich:column>

    <f:facet name="footer">
        <rich:datascroller for="categoryOrgListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

