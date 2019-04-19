<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style lang="">
.deleteRow {
background-image: url("/images/16x16/delete.png");
}
</style>

<rich:modalPanel id="modalClientListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalClientListSelectorPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Фильтр по клиентам" />
    </f:facet>
    <a4j:form id="modalClientListSelectorForm" styleClass="borderless-form" eventsQueue="modalClientSelectorFormEventsQueue"
              binding="#{mainPage.clientSelectListPage.pageComponent}">
        <table border="1" cellpadding="5" cellspacing="1" width="850">
            <tr>
                <td width="50%">
            <h:panelGroup>
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <h:outputText escape="true" value="Организация" styleClass="output-text" />
                    <h:panelGroup styleClass="borderless-div">
                        <h:inputText id="modalClientSelectorOrgFilter"
                                     value="#{mainPage.clientSelectListPage.clientFilter.org.shortName}"
                                     readonly="true" styleClass="input-text" style="margin-right: 2px;" />
                        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                                           reRender="modalOrgSelectorPanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                           styleClass="command-link" style="width: 25px;"
                                           disabled="#{mainPage.clientSelectListPage.clientFilter.permanentOrgId != null}" />
                    </h:panelGroup>
                    <h:outputText escape="true" value="Номер лицевого счета" styleClass="output-text" />
                    <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.contractId}" maxlength="16"
                                 styleClass="input-text" />
                </h:panelGrid>
                <rich:simpleTogglePanel label="По контактным данным" switchType="client" opened="false"
                                        styleClass="borderNone" timeout="10"
                                        headerClass="imageNone borderNone linkClass"
                                        bodyClass="imageNone borderNone">
                    <h:panelGrid columns="2" styleClass="borderless-grid">

                        <h:outputText escape="true" value="Мобильный телефон" styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.mobileNumber}" maxlength="10"
                                     styleClass="input-text" />
                        <h:outputText escape="true" value="E-mail" styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.email}" maxlength="128"
                                     styleClass="input-text" />
                    </h:panelGrid>
                </rich:simpleTogglePanel>
                <rich:spacer></rich:spacer>
                <rich:simpleTogglePanel label="По ФИО" switchType="client" opened="false"
                                        styleClass="borderNone" timeout="10"
                                        headerClass="imageNone borderNone linkClass"
                                        bodyClass="imageNone borderNone">
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.person.surname}"
                                     maxlength="128" styleClass="input-text" />
                        <h:outputText escape="true" value="Имя" styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.person.firstName}"
                                     maxlength="64" styleClass="input-text" />
                        <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientSelectListPage.clientFilter.person.secondName}"
                                     maxlength="128" styleClass="input-text" />
                    </h:panelGrid>
                </rich:simpleTogglePanel>
                <rich:spacer></rich:spacer>
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <a4j:commandButton value="Применить" action="#{mainPage.clientSelectListPage.apply()}"
                                       reRender="modalClientListSelectorForm" styleClass="command-button" oncomplete="#{mainPage.clientSelectListPage.resetLimitOffset()}"/>
                    <a4j:commandButton value="Очистить" action="#{mainPage.clearClientSelectListPageFilter}"
                                       reRender="modalClientListSelectorForm" ajaxSingle="true"
                                       styleClass="command-button" />
                </h:panelGrid>
                <h:outputText escape="true" value="Выберите клиентов:" styleClass="output-text"
                              rendered="#{!mainPage.clientSelectListPage.isItemsEmpty()}"/>
                <rich:dataTable width="100%" align="center" id="modalClientSelectorTable"
                                value="#{mainPage.clientSelectListPage.items}" var="item" rows="10" columns="2"
                                footerClass="data-table-footer" columnClasses="left-aligned-column"
                                rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="#{item.caption}" styleClass="output-text" />
                    </rich:column>
                    <rich:column>
                        <a4j:commandButton type="image" image="/images/16x16/play.png"
                                           action="#{mainPage.clientSelectListPage.addToSelectedOne(item)}"
                                           reRender="selectedClientsTable,modalClientSelectorTable,clearSelectedItemsButton"
                                           rendered="#{!item.selected}"/>
                    </rich:column>

                    <f:facet name="footer">
                        <h:panelGroup style="display:block; text-align:center">
                            <a4j:commandButton type="button" image="/images/16x16/left-arrow.png"
                                               action="#{mainPage.clientSelectListPage.pageBack}"
                                               reRender="modalClientSelectorTable,selectedClientsTable"
                                               rendered="#{mainPage.clientSelectListPage.showPager() && mainPage.clientSelectListPage.pageBackEnabled()}" />
                            <rich:spacer/>
                            <a4j:commandButton type="button" image="/images/16x16/right-arrow.png"
                                               action="#{mainPage.clientSelectListPage.pageForward}"
                                               reRender="modalClientSelectorTable,selectedClientsTable"
                                               rendered="#{mainPage.clientSelectListPage.showPager() && mainPage.clientSelectListPage.pageForwardEnabled()}" />
                        </h:panelGroup>
                    </f:facet>
                </rich:dataTable>
                <a4j:commandButton value="Выбрать всех"
                                   action="#{mainPage.clientSelectListPage.addAllToSelected}"
                                   reRender="selectedClientsTable,modalClientSelectorTable,clearSelectedItemsButton"
                                   rendered="#{!mainPage.clientSelectListPage.isItemsEmpty()}"/>
            </h:panelGroup>
                </td>
                <td width="50%" style="vertical-align: top">
            <h:panelGroup>
                <h:panelGrid columns="1" styleClass="borderless-grid">
                    <h:outputText escape="true" value="Выбранные клиенты:" styleClass="output-text" />
                    <rich:dataTable width="100%" align="center" id="selectedClientsTable"
                                    value="#{mainPage.clientSelectListPage.selectedItems}" var="selectedItem" rows="12" columns="2"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{selectedItem.caption}" styleClass="output-text" />
                        </rich:column>
                        <rich:column>
                            <a4j:commandButton type="image" image="/images/16x16/delete.png"
                                    action="#{mainPage.clientSelectListPage.removeFromSelectedOne(selectedItem)}"
                                    reRender="selectedClientsTable,modalClientSelectorTable,clearSelectedItemsButton"/>
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="selectedClientsTable" renderIfSinglePage="false" maxPages="5"
                                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                                <a4j:support event="onpagechange"/>
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                    <a4j:commandButton value="Очистить список" id="clearSelectedItemsButton"
                                    action="#{mainPage.clientSelectListPage.removeAllFromSelected}"
                                    reRender="selectedClientsTable,modalClientSelectorTable,clearSelectedItemsButton"/>
                </h:panelGrid>
            </h:panelGroup>
                </td>
            </tr>
        </table>
        <h:panelGroup layout="block" style="float:right; padding:5px">
            <a4j:commandButton value="Ok" action="#{mainPage.completeClientListSelection}"
                               reRender="#{mainPage.topMostPage.pageComponent.id}"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientListSelectorPanel')}.hide();"
                               styleClass="command-button" style="width: 80px; margin-right: 4px;" />
            <a4j:commandButton value="Отмена" action="#{mainPage.cancelClientListSelection}"
                               reRender="#{mainPage.topMostPage.pageComponent.id}"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientListSelectorPanel')}.hide();"
                               styleClass="command-button" style="width: 80px;">
                <f:setPropertyActionListener value="#{null}"
                                             target="#{mainPage.clientSelectListPage.selectedItem}" />
            </a4j:commandButton>
        </h:panelGroup>
    </a4j:form>
</rich:modalPanel>