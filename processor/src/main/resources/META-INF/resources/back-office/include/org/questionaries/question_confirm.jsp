<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 26.12.12
  Time: 13:45
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="confirmDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText value="Подтвердите удаление" styleClass="output-text" />
    </f:facet>
    <a4j:form id="confirmDeleteForm" binding="#{confirmDeletePage.pageComponent}"
              styleClass="borderless-form">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить выбранный элемент?" styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" ajaxSingle="true" action="#{confirmDeletePage.delete}"
                                           oncomplete="#{rich:component('confirmDeletePanel')}.hide();"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           styleClass="command-button" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('confirmDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>