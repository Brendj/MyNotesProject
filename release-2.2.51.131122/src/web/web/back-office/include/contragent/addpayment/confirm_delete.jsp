<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="addPaymentDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('addPaymentDeletePanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText value="Удаление начисленного платежа" styleClass="output-text" />
    </f:facet>
    <a4j:form id="addPaymentDeleteForm" binding="#{mainPage.addPaymentDeletePage.pageComponent}"
              styleClass="borderless-form">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить этот платеж?" styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" ajaxSingle="true" action="#{mainPage.removeAddPayment}"
                                           oncomplete="#{rich:component('addPaymentDeletePanel')}.hide();"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           styleClass="command-button" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('addPaymentDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>