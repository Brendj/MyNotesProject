<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>


<rich:dataTable id="categoryOrgListTable" binding="#{categoryOrgListPage.pageComponent}"
                    value="#{categoryOrgListPage.items}" var="item" rows="20"
                    columnClasses="center-aligned-column"
                    footerClass="data-table-footer">

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Названия категорий" />
            </f:facet>
            <h:commandLink value="#{item.categoryName}" action="#{categoryOrgViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{categoryOrgViewPage.currCategoryOrg}" />
            </h:commandLink>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <h:commandLink action="#{categoryOrgEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{categoryOrgEditPage.currCategoryOrg}" />
            </h:commandLink>
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

