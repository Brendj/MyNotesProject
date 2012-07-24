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

<%--@elvariable id="productEditPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductEditPage"--%>
<%--@elvariable id="productListPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductListPage"--%>
<%--@elvariable id="selectedProductGroupPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.SelectedProductGroupPage"--%>
<h:panelGrid id="productListPage" binding="#{productListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
      <%--TODO: Добавить фильтры: по провайдерам, по группам, по статусам(скрыть или показать удаленные)--%>
    <h:column>

        <rich:simpleTogglePanel label="Фильтр" switchType="client"
                                eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">
            <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{productListPage.selectedConfigurationProvider.name}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{productListPage.selectConfigurationProvider}" reRender="configurationProviderSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText escape="true" value="Группа продуктов" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{productListPage.selectedProductGroup.nameOfGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{productListPage.selectProductGroup}" reRender="productGroupSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('productGroupSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText value="Удаленные элементы" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectDeletedStatus" value="#{productListPage.deletedStatusSelected}" styleClass="input-text long-field">
                    <f:selectItem itemLabel="Скрыть" itemValue="false"/>
                    <f:selectItem itemLabel="Показать" itemValue="true"/>
                </h:selectOneMenu>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">

                <a4j:commandButton value="Применить" action="#{productListPage.onSearch}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />

                <a4j:commandButton value="Очистить" action="#{productListPage.onClear}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:column>

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>


    <rich:dataTable id="productListTable" width="700" var="product" value="#{productListPage.productList}" rendered="#{!productListPage.emptyProductList}"
                    rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row+1}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Товарное наименование" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:commandLink value="#{product.productName}" action="#{productViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{product}" target="#{selectedProductGroupPage.currentProduct}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Полное наименование пищевого продукта" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{product.fullName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Код" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{product.code}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Код (коды) ОКП" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{product.okpCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Статус продукта" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{product.deletedState}" readonly="true" disabled="true"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:commandLink action="#{productEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{product}" target="#{selectedProductGroupPage.currentProduct}" />
            </h:commandLink>
        </rich:column>
        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedProductItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{product}" target="#{productEditPage.currentProduct}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="productListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>
