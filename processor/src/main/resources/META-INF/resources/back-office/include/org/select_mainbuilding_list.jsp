<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<rich:modalPanel id="modalOrgMainbuildingListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalOrgMainbuildingListSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор организации" />
    </f:facet>
    <a4j:form id="modalOrgMainbuildingListSelectorForm" binding="#{mainPage.orgMainBuildingListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalOrgMainbuildingListSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText id="orgMainbuildingListValue" value="#{mainPage.orgMainBuildingListSelectPage.selectedItems}"
                                     readonly="true" size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.orgMainBuildingListSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.showOrgMainBuildingListSelectPage}"
                                         reRender="modalOrgMainbuildingListSelectorTable" />
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.showOrgMainBuildingListSelectPage}"
                                         reRender="modalOrgMainbuildingListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value=""
                                                         target="#{mainPage.orgMainBuildingListSelectPage.filter}" />
                            <a4j:support event="onclick" action="#{mainPage.orgMainBuildingListSelectPage.cancelFilter}"
                                         reRender="modalOrgMainbuildingListSelectorForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.clearOrgMainBuildingListSelectedItemsList}"
                                           reRender="modalOrgMainbuildingListSelectorForm" styleClass="command-link"
                                           value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalOrgMainbuildingListSelectorTable"
                                    value="#{mainPage.orgMainBuildingListSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <%--<a4j:support event="onRowClick" reRender="modalContragentListSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.contragentListSelectPage.selectedItem}" />
                        </a4j:support>--%>
                        <rich:column>

                            <h:selectOneRadio styleClass="output-text" value="#{mainPage.orgMainBuildingListSelectPage.selectedMainOrgId}">
                                <f:selectItem itemValue="#{item.idOfOrg}"/>
                                <%--<a4j:support event="onchange" reRender="modalOrgMainbuildingListSelectorForm">--%>
                                    <%--<c:if test="${item.selected}">--%>
                                        <%--<f:setPropertyActionListener value="#{item.idOfOrg}"--%>
                                                                     <%--target="#{mainPage.orgMainBuildingListSelectPage.selectedMainOrgId}" />--%>
                                    <%--</c:if>--%>
                                    <%--<c:if test="${!item.selected}">--%>
                                        <%--<f:setPropertyActionListener value="#{-1}"--%>
                                                                     <%--target="#{mainPage.orgMainBuildingListSelectPage.selectedMainOrgId}" />--%>
                                    <%--</c:if>--%>
                                <%--</a4j:support>--%>
                                <a4j:support event="onclick" action="#{mainPage.orgMainBuildingListSelectPage.updateSelectedIds(item.idOfOrg, !item.selected)}"
                                             reRender="modalOrgMainbuildingListSelectorForm">
                                    <%--<f:setPropertyActionListener value="#{item.idOfOrg}"--%>
                                                                 <%--target="#{mainPage.orgMainBuildingListSelectPage.selectedMainOrgId}" />--%>
                                </a4j:support>

                            </h:selectOneRadio>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalOrgMainbuildingListSelectorTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                                <a4j:support event="onpagechange" />
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{mainPage.completeOrgMainBuildingListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgMainbuildingListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelOrgMainBuildingListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalOrgMainbuildingListSelectorPanel')}.hide();return false;"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>
