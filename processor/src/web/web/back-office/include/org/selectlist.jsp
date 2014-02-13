<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="orgSelectListPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgSelectListPage"--%>
<rich:modalPanel id="modalOrgsListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalOrgsListSelectorPanel')}.hide();return false;"/>
    <rich:hotKey key="ctrl+a" handler="selectAll();return false;"/>
    <rich:hotKey key="ctrl+d" handler="deselectAll();return false;"/>

    <f:facet name="header">
        <h:outputText escape="true" value="#{mainPage.orgFilterPageName}" />
    </f:facet>
    <a4j:form id="modalOrgsListSelectorForm" binding="#{orgSelectListPage.pageComponent}" styleClass="borderless-form"
              eventsQueue="modalOrgsListSelectorFormEventsQueue">
        <a4j:jsFunction name="selectAll" action="#{orgSelectListPage.selectAllOrgsListSelectedItemsList}" reRender="modalOrgsListSelectorForm"/>
        <a4j:jsFunction name="deselectAll" action="#{orgSelectListPage.clearOrgsListSelectedItemsList}" reRender="modalOrgsListSelectorForm"/>

        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                            <h:inputText value="#{orgSelectListPage.orgNameFilter}" size="48" maxlength="128"
                                         styleClass="input-text" />
                            <h:outputText escape="true" value="Фильтр по тэгу: " styleClass="output-text" />
                            <h:inputText value="#{orgSelectListPage.tagFilter}" size="48" maxlength="128"
                                         styleClass="input-text" />
                        </h:panelGrid>
                        <a4j:commandLink action="#{orgSelectListPage.updateOrgsListSelectPage}" reRender="modalOrgsListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{orgSelectListPage.updateOrgsListSelectPage}" reRender="modalOrgsListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{orgSelectListPage.orgNameFilter}" />
                        </a4j:commandLink>
                    </h:panelGrid>

                    <h:selectOneListbox value="#{orgSelectListPage.organizationTypeSwitch}">
                        <f:selectItems value="#{orgSelectListPage.organizationTypeSwitchMenu.items}" />
                    </h:selectOneListbox>

                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{orgSelectListPage.selectAllOrgsListSelectedItemsList}" reRender="modalOrgsListSelectorForm"
                                         styleClass="command-link" value="Выбрать все" />
                        <a4j:commandButton action="#{orgSelectListPage.clearOrgsListSelectedItemsList}" reRender="modalOrgsListSelectorForm"
                                         styleClass="command-link" value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalOrgsListSelectorOrgTable"
                                    value="#{orgSelectListPage.items}" var="item" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column headerClass="column-header">
                            <h:selectBooleanCheckbox value="#{item.selected}">
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.shortName} (#{item.officialName})"
                                          styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.address}"
                                          styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalOrgsListSelectorOrgTable" renderIfSinglePage="false" maxPages="5"
                                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                                <a4j:support event="onpagechange" />
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{orgSelectListPage.completeOrgsListSelectionOk}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgsListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{orgSelectListPage.completeOrgsListSelectionCancel}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgsListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>