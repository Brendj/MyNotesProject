<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="uvDeletePage" type="ru.axetta.ecafe.processor.web.ui.abstractpage.UvDeletePage"--%>
<rich:modalPanel id="uvDeleteConfirmPanel" autosized="true" width="300" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('uvDeleteConfirmPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText value="Удаление объекта" styleClass="output-text" />
    </f:facet>
    <a4j:form id="removedContractItemDeletePanelForm" styleClass="borderless-form">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить #{uvDeletePage.currentEntityItem}?" styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" action="#{uvDeletePage.delete}"
                                           oncomplete="#{rich:component('uvDeleteConfirmPanel')}.hide(); return false;"
                                           reRender="mainMenu, workspaceTogglePanel"
                                           styleClass="command-button" />
                        <a4j:commandButton value="Отмена" styleClass="command-button" action="#{uvDeletePage.hide}"
                                           onclick="#{rich:component('uvDeleteConfirmPanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>