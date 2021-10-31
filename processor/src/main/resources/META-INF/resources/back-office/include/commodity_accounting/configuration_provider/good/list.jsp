<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 10.05.12
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="goodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodEditPage"--%>
<%--@elvariable id="goodViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodViewPage"--%>
<%--@elvariable id="goodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodListPage"--%>
<%--@elvariable id="selectedGoodGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.SelectedGoodGroupPage"--%>
<h:panelGrid id="goodListPage" binding="#{goodListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="goodListPageFilter" styleClass="borderless-grid" columns="1">
        <rich:simpleTogglePanel label="Фильтр" switchType="client"
                                eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">
            <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{goodListPage.selectedConfigurationProvider.name}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{goodListPage.selectConfigurationProvider}" reRender="configurationProviderSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText escape="true" value="Группа товаров" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{goodListPage.selectedGoodGroup.nameOfGoodsGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{goodListPage.selectGoodGroup}" reRender="goodGroupSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('goodGroupSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText value="Удаленные элементы" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectDeletedStatus" value="#{goodListPage.deletedStatusSelected}" styleClass="input-text long-field">
                    <f:selectItem itemLabel="Скрыть" itemValue="false"/>
                    <f:selectItem itemLabel="Показать" itemValue="true"/>
                </h:selectOneMenu>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">

                <a4j:commandButton value="Применить" action="#{goodListPage.onSearch}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />

                <a4j:commandButton value="Очистить" action="#{goodListPage.onClear}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>

    <a4j:status id="goodListTableStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="goodListTable" var="good" value="#{goodListPage.goodList}"
                    rows="10" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Идентификатор" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.globalId}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="GUID" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.guid}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наименование" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{good.nameOfGood}" action="#{goodViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{good}" target="#{selectedGoodGroupPage.currentGood}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Полное наименование товара" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.fullName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Код" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.goodsCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Единица измерения" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.unitsScale}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Масса нетто" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.netWeight}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Срок годности (в минутах)" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.lifeTime}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наценка" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.margin}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Статус товара" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{good.deletedState}" readonly="true" disabled="true"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{goodEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{good}" target="#{selectedGoodGroupPage.currentGood}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             reRender="mainMenu, workspaceForm" rendered="#{good.deletedState}"
                             oncomplete="#{rich:component('removedGoodItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{good}" target="#{goodEditPage.currentGood}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="goodListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

