<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="orgListSelectPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage"--%>
<rich:modalPanel id="modalOrgListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalOrgListSelectorPanel')}.hide();return false;"/>
    <rich:hotKey key="ctrl+a" handler="selectAll();return false;"/>
    <rich:hotKey key="ctrl+d" handler="deselectAll();return false;"/>

    <f:facet name="header">
        <h:outputText escape="true" value="#{mainPage.orgFilterPageName}" />
    </f:facet>
    <a4j:form id="modalOrgListSelectorForm" binding="#{mainPage.orgListSelectPage.pageComponent}" styleClass="borderless-form"
              eventsQueue="modalOrgListSelectorFormEventsQueue">
        <a4j:jsFunction name="selectAll" action="#{mainPage.selectAllOrgListSelectedItemsList}" reRender="modalOrgListSelectorForm"/>
        <a4j:jsFunction name="deselectAll" action="#{mainPage.clearOrgListSelectedItemsList}" reRender="modalOrgListSelectorForm"/>

        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgListSelectPage.filter}" size="48" maxlength="128"
                                         styleClass="input-text" />
                            <h:outputText escape="true" value="Фильтр по тэгу: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgListSelectPage.tagFilter}" size="48" maxlength="128"
                                         styleClass="input-text" />
                        </h:panelGrid>
                        <a4j:commandLink action="#{mainPage.updateOrgListSelectPage}" reRender="modalOrgListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{mainPage.updateOrgListSelectPage}" reRender="modalOrgListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:selectOneRadio value="#{mainPage.orgListSelectPage.supplierFilter}" converter="javax.faces.Integer"
                                      styleClass="output-text" >
                        <a4j:support event="onclick" action="#{mainPage.updateOrgListSelectPageWithItemDeselection}" reRender="modalOrgListSelectorForm"/>

                        <f:selectItem itemValue="0" itemLabel="Любые организации" itemDisabled="#{mainPage.orgListSelectPage.allOrgFilterDisabled}"/>
                        <f:selectItem itemValue="1" itemLabel="Только ОУ" itemDisabled="#{mainPage.orgListSelectPage.schoolFilterDisabled}"/>
                        <f:selectItem itemValue="2" itemLabel="Только поставщики" itemDisabled="#{mainPage.orgListSelectPage.supplierFilterDisabled}"/>
                    </h:selectOneRadio>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllOrgListSelectedItemsList}" reRender="modalOrgListSelectorForm"
                                         styleClass="command-link" value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearOrgListSelectedItemsList}" reRender="modalOrgListSelectorForm"
                                         styleClass="command-link" value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalOrgListSelectorOrgTable"
                                    value="#{mainPage.orgListSelectPage.items}" var="item" rows="8"
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
                            <rich:datascroller for="modalOrgListSelectorOrgTable" renderIfSinglePage="false" maxPages="5"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeOrgListSelectionOk}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeOrgListSelectionCancel}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>