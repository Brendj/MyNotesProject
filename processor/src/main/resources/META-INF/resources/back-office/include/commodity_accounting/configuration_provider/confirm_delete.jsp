<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="configurationProviderDeletePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderDeletePage"--%>
<rich:modalPanel id="removedСonfigurationProviderItemDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText value="Удаление конфигурации поставщика" styleClass="output-text" />
    </f:facet>
    <a4j:form styleClass="borderless-form">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить эту конфигурацию поставищка?" styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" ajaxSingle="true" action="#{configurationProviderDeletePage.remove}"
                                           oncomplete="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.hide();"
                                           reRender="mainMenu, configurationProviderListTable, #{mainPage.topMostPage.pageComponent.id}"
                                           styleClass="command-button" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
                <td>
                    <h:panelGrid styleClass="borderless-grid">
                        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                                       warnClass="warn-messages" />

                    </h:panelGrid>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>