<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка пользователей --%>
<%--@elvariable id="thinClientUserListPage" type="ru.axetta.ecafe.processor.web.ui.option.user.ThinClientUserListPage"--%>
<rich:dataTable id="thinClientUserListPage" binding="#{thinClientUserListPage.pageComponent}"
                value="#{thinClientUserListPage.items}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Имя пользователя" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.userName}" action="#{thinClientUserViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfClient}" target="#{thinClientUserViewPage.idOfClient}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Клиент" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.clientName}" action="#{thinClientUserViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfClient}" target="#{thinClientUserViewPage.idOfClient}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Организация" />
        </f:facet>
        <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Роль" />
        </f:facet>
        <h:outputText escape="true" value="#{item.roleName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>

        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('removedThinClientUserDeletePanel')}.show()" reRender="removedThinClientUserDeletePanel">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfClient}" target="#{thinClientUserListPage.clientToRemove}" />
        </a4j:commandLink>

    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="thinClientUserListPage" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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


<rich:modalPanel id="removedThinClientUserDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText value="Удаление пользователя" styleClass="output-text" />
    </f:facet>
    <table class="borderless-grid" width="100%">
        <tr>
            <td style="text-align: center;">
                <h:outputText value="Вы уверены в том, что хотите удалить этого пользователя?"
                              styleClass="output-text" />
            </td>
        </tr>
        <tr>
            <td style="text-align: center;">
                <h:panelGroup styleClass="borderless-div">
                    <a4j:commandButton value="Да" action="#{thinClientUserListPage.doRemoveUser(thinClientUserListPage.clientToRemove)}"
                                       oncomplete="#{rich:component('removedThinClientUserDeletePanel')}.hide();"
                                       styleClass="command-button" reRender="thinClientUserListPage" />
                    <a4j:commandButton value="Отмена" styleClass="command-button"
                                       onclick="#{rich:component('removedThinClientUserDeletePanel')}.hide();return false;" />
                </h:panelGroup>
            </td>
        </tr>
    </table>
</rich:modalPanel>