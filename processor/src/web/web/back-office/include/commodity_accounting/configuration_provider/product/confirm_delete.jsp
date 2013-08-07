<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="productEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductEditPage"--%>
<%--@elvariable id="productListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductListPage"--%>
<rich:modalPanel id="removedProductItemDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('removedProductItemDeletePanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText value="Удаление продукта из справочника" styleClass="output-text" />
    </f:facet>
    <a4j:form styleClass="borderless-form">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить этот продукт?" styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" ajaxSingle="true" action="#{productEditPage.removeProduct}"
                                           oncomplete="#{rich:component('removedProductItemDeletePanel')}.hide();"
                                           reRender="mainMenu, workspaceTogglePanel, #{productListPage.pageComponent.id}"
                                           styleClass="command-button" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('removedProductItemDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>