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

        <table class="borderless-grid" width="750">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="1" styleClass="borderless-grid">
                            <h:panelGrid id="filterTestPanel" columns="1" styleClass="borderless-grid">
                                <h:panelGrid columns="2">
                                    <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                                    <h:panelGrid columns="3" border="0" cellpadding="0" cellspacing="0"
                                                 styleClass="borderless-grid">
                                        <h:inputText style="margin:0;" id="statesinput"
                                                     value="#{mainPage.orgListSelectPage.filter}" size="128"
                                                     maxlength="128" styleClass="input-text" />
                                        <h:graphicImage value="/images/16x16/arrow.png"
                                                        onclick="#{rich:component('suggestion')}.callSuggestion(true)"
                                                        alt="" />
                                    </h:panelGrid>
                                    <h:outputText id="objects" style="font-weight:bold" styleClass="output-text" />
                                    <rich:suggestionbox limitToList="5" height="200" width="790"
                                                        usingSuggestObjects="true"
                                                        onobjectchange="printObjectsSelected(#{rich:element('objects')}, #{rich:component('suggestion')});"
                                                        suggestionAction="#{mainPage.orgListSelectPage.autoComplete}"
                                                        var="orgShortName" for="statesinput" frame="box"
                                                        ajaxSingle="true" fetchValue="#{orgShortName.shortName}"
                                                        id="suggestion" tokens=",">
                                        <h:column>
                                            <h:outputText value="#{orgShortName.shortName}" styleClass="output-text" />
                                        </h:column>
                                    </rich:suggestionbox>
                                </h:panelGrid>
                                <rich:simpleTogglePanel label="Показать фильтры" switchType="client" opened="false"
                                                        styleClass="borderNone" timeout="10"
                                                        headerClass="imageNone borderNone linkClass"
                                                        bodyClass="imageNone borderNone">
                                    <h:panelGrid id="contentDiv" columns="2" styleClass="borderless-grid">
                                        <h:outputText escape="true" value="Фильтр по тэгу: " styleClass="output-text" />
                                        <h:inputText value="#{mainPage.orgListSelectPage.tagFilter}" size="110"
                                                     maxlength="120" styleClass="input-text" />
                                        <h:outputText escape="true" value="Фильтр по ID: " styleClass="output-text" />
                                        <h:inputText value="#{mainPage.orgListSelectPage.idFilter}" size="110"
                                                     maxlength="120" styleClass="input-text" />
                                        <h:outputText escape="true" value="Фильтр по округу: "
                                                      styleClass="output-text" />
                                        <h:selectOneMenu id="regionsList" value="#{mainPage.orgListSelectPage.region}"
                                                         style="width:325px;"
                                                         disabled="#{mainPage.orgListSelectPage.districtFilterDisabled}">
                                            <f:selectItems value="#{mainPage.orgListSelectPage.regions}" />
                                        </h:selectOneMenu>
                                    </h:panelGrid>
                                    <h:panelGrid id="radioButtons" columns="1" styleClass="borderless-grid">
                                        <h:selectOneRadio value="#{mainPage.orgListSelectPage.supplierFilter}"
                                                          converter="javax.faces.Integer" styleClass="output-text">
                                            <a4j:support event="onclick"
                                                         action="#{mainPage.updateOrgListSelectPageWithItemDeselection}"
                                                         reRender="modalOrgListSelectorForm" />

                                            <f:selectItem itemValue="0" itemLabel="Любые организации"
                                                          itemDisabled="#{mainPage.orgListSelectPage.allOrgFilterDisabled}" />
                                            <f:selectItem itemValue="1" itemLabel="Только ОУ"
                                                          itemDisabled="#{mainPage.orgListSelectPage.schoolFilterDisabled}" />
                                            <f:selectItem itemValue="4" itemLabel="Только ДОУ"
                                                          itemDisabled="#{mainPage.orgListSelectPage.primarySchoolFilterDisabled}" />
                                            <f:selectItem itemValue="5" itemLabel="Только СОШ"
                                                          itemDisabled="#{mainPage.orgListSelectPage.secondarySchoolFilterDisabled}" />
                                            <f:selectItem itemValue="2" itemLabel="Только поставщики"
                                                          itemDisabled="#{mainPage.orgListSelectPage.supplierFilterDisabled}" />
                                        </h:selectOneRadio>
                                    </h:panelGrid>
                                </rich:simpleTogglePanel>
                            </h:panelGrid>
                        </h:panelGrid>
                        <a4j:commandLink action="#{mainPage.updateOrgListSelectPage}"
                                         reRender="modalOrgListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{mainPage.updateOrgListSelectPage}"
                                         reRender="modalOrgListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.filter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.tagFilter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.idFilter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.region}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllOrgListSelectedItemsList}"
                                           reRender="modalOrgListSelectorForm" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearOrgListSelectedItemsList}"
                                           reRender="modalOrgListSelectorForm" styleClass="command-link"
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