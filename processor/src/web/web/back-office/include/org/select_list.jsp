<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<script type="text/javascript">
    function printObjectsSelected(output, sgcomponent) {
        output.innerHTML = sgcomponent.getSelectedItems().pluck('state');
    }
</script>

<style type="text/css">
    .imageNone {
        background-image: none;
        background-color: #f3f2f2;
    }

    .borderNone {
        border: 0;
    }

    .linkClass {
        text-decoration: underline;
        color: #0000ff;
    }

</style>

<rich:modalPanel id="modalOrgListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalOrgListSelectorPanel')}.hide();return false;" />
    <rich:hotKey key="ctrl+a" handler="selectAll();return false;" />
    <rich:hotKey key="ctrl+d" handler="deselectAll();return false;" />

    <f:facet name="header">
        <h:outputText escape="true" value="#{mainPage.orgFilterPageName}" />
    </f:facet>
    <a4j:form id="modalOrgListSelectorForm" binding="#{mainPage.orgListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalOrgListSelectorFormEventsQueue">
        <a4j:jsFunction name="selectAll" action="#{mainPage.selectAllOrgListSelectedItemsList}"
                        reRender="modalOrgListSelectorForm" />
        <a4j:jsFunction name="deselectAll" action="#{mainPage.clearOrgListSelectedItemsList}"
                        reRender="modalOrgListSelectorForm" />

        <table class="borderless-grid" width="850">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="1" styleClass="borderless-grid">
                            <h:panelGrid id="filterTestPanel" columns="1" styleClass="borderless-grid">
                                <h:panelGrid columns="1" styleClass="borderless-grid">
                                    <a4j:outputPanel id="myPanel" ajaxRendered="true">
                                        <h:inputText id="selectedOrgsString"
                                                     value="#{mainPage.orgListSelectPage.selectedOrgsString}"
                                                     readonly="true" size="100" styleClass="input-text" />
                                    </a4j:outputPanel>
                                </h:panelGrid>
                                <h:panelGrid id="contentDiv" columns="2" styleClass="borderless-grid">
                                    <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                                    <h:inputText id="inputFilter" value="#{mainPage.orgListSelectPage.filter}" size="60"
                                                 maxlength="120" styleClass="input-text">
                                        <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgListSelectPage}"
                                                     reRender="modalOrgListSelectorOrgTable" />
                                    </h:inputText>
                                    <h:outputText escape="true" value="Фильтр по ID: " styleClass="output-text" />
                                    <h:inputText id="inputIdFilter" value="#{mainPage.orgListSelectPage.idFilter}"
                                                 size="60" maxlength="120" styleClass="input-text">
                                        <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgListSelectPage}"
                                                     reRender="modalOrgListSelectorOrgTable" />
                                    </h:inputText>
                                    <h:outputText escape="true" value="ID в НСИ-3: " styleClass="output-text" />
                                    <h:inputText value="#{mainPage.orgSelectPage.orgIdFromNsi}" size="48" maxlength="10"
                                                 styleClass="input-text">
                                        <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                                     reRender="modalOrgSelectorOrgTable" />
                                    </h:inputText>
                                    <h:outputText escape="true" value="ЕКИС Id: " styleClass="output-text" />
                                    <h:inputText value="#{mainPage.orgSelectPage.ekisId}" size="48" maxlength="10"
                                                 styleClass="input-text">
                                        <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                                     reRender="modalOrgSelectorOrgTable" />
                                    </h:inputText>
                                    <h:outputText escape="true" value="Округ: " styleClass="output-text" />
                                    <h:selectOneMenu id="regionsList" value="#{mainPage.orgListSelectPage.region}"
                                                     style="width:386px;"
                                                     disabled="#{mainPage.orgListSelectPage.districtFilterDisabled}">
                                        <f:selectItems value="#{mainPage.orgListSelectPage.regions}" />
                                        <a4j:support event="onchange" action="#{mainPage.updateOrgListSelectPage}"
                                                     reRender="modalOrgListSelectorOrgTable" />
                                    </h:selectOneMenu>
                                    <h:outputText escape="true" value="Поставщик питания: " styleClass="output-text" />
                                    <h:selectOneMenu id="contragentList" disabled="#{mainPage.orgListSelectPage.disableContragentFilter()}"
                                                     value="#{mainPage.orgSelectPage.idOfSelectedContragent}"
                                                     styleClass="output-text" style="width:386px;">
                                        <f:selectItems value="#{mainPage.orgSelectPage.contragentsList}" />
                                        <a4j:support event="onchange" action="#{mainPage.updateOrgSelectPage}"
                                                     reRender="modalOrgSelectorOrgTable"/>
                                    </h:selectOneMenu>
                                </h:panelGrid>
                                    <rich:simpleTogglePanel label="Показать доп. фильтры" switchType="client" opened="false"
                                                            styleClass="borderNone" timeout="10"
                                                            headerClass="imageNone borderNone linkClass"
                                                            bodyClass="imageNone borderNone">
                                        <h:panelGrid columns="3">
                                            <a4j:repeat id="OrganizationTypesForSelectMany"
                                                        value="#{mainPage.orgListSelectPage.availableOrganizationTypes}" var="item" >
                                                <h:panelGrid columns="2">
                                                    <h:selectBooleanCheckbox value="#{item.selected}" disabled="#{item.disabled}">
                                                        <a4j:support event="onchange" action="#{mainPage.updateOrgListSelectPageWithItemDeselection}"
                                                                     reRender="modalOrgListSelectorForm" requestDelay="1000"/>
                                                    </h:selectBooleanCheckbox>
                                                    <h:outputText styleClass="output-text" value="#{item.typeName}"/>
                                                </h:panelGrid>
                                            </a4j:repeat>
                                        </h:panelGrid>
                                    </rich:simpleTogglePanel>
                            </h:panelGrid>
                        </h:panelGrid>
                        <a4j:commandLink action="#{mainPage.updateOrgListSelectPage}"
                                         reRender="modalOrgListSelectorOrgTable, inputFilter, inputTagFilter,inputIdFilter, regionsList, myPanel"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.filter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.idFilter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.region}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllOrgListSelectedItemsList}"
                                           reRender="modalOrgListSelectorOrgTable, myPanel" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearOrgListSelectedItemsList}"
                                           reRender="modalOrgListSelectorOrgTable, myPanel" styleClass="command-link"
                                           value="Очистить выбор" />
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
                                <a4j:support event="onchange" action="#{mainPage.updateOrgListSelectPage}"
                                             reRender="selectedOrgsString" />
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
                            <h:outputText escape="true" value="#{item.address}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalOrgListSelectorOrgTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
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