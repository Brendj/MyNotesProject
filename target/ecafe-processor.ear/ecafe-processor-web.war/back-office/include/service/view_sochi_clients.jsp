<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="sochiClientsLoadPanel" binding="#{mainPage.sochiClientsViewPage.pageComponent}"
             styleClass="borderless-grid">

    <h:outputText escape="true" value="Всего клиентов: #{mainPage.sochiClientsViewPage.sochiClientNumber}"
                  styleClass="output-text" />

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
        <h:inputText value="#{mainPage.selectedSochiClientContractId}"
                     maxlength="#{mainPage.sochiClientsViewPage.contractIdMaxLength}" converter="contractIdConverter"
                     styleClass="input-text" />
        <h:commandButton value="Показать данные по клиенту" action="#{mainPage.showSochiClientsViewPage}"
                         styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" rendered="#{mainPage.sochiClientsViewPage.client != null}">

        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
            <h:inputText value="#{mainPage.sochiClientsViewPage.client.contractId}" readonly="true"
                         styleClass="input-text" converter="contractIdConverter" />

            <h:outputText escape="true" value="ФИО" styleClass="output-text" />
            <h:inputText value="#{mainPage.sochiClientsViewPage.client.fullName}" size="50" readonly="true"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Адрес" styleClass="output-text" />
            <h:inputText value="#{mainPage.sochiClientsViewPage.client.address}" size="50" readonly="true"
                         styleClass="input-text" />

            <h:outputText escape="true" value="Дата/время регистрации" styleClass="output-text" />
            <h:inputText value="#{mainPage.sochiClientsViewPage.client.createTime}" converter="timeConverter"
                         readonly="true" styleClass="input-text" />

            <h:outputText escape="true" value="Дата/время последнего обновления" styleClass="output-text" />
            <h:inputText value="#{mainPage.sochiClientsViewPage.client.updateTime}" converter="timeConverter"
                         readonly="true" styleClass="input-text" />

        </h:panelGrid>

        <rich:dataTable id="sochiClientPaymentsTable" value="#{mainPage.sochiClientsViewPage.payments}" var="item"
                        rows="20"
                        columnClasses="right-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="ID операции" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentId}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма к начислению" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentSum}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Уплаченная сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentSumF}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="ID терминала" />
                </f:facet>
                <h:outputText escape="true" value="#{item.terminalId}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата/время платежа" />
                </f:facet>
                <h:outputText escape="true" value="#{item.paymentTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата/время регистрации платежа" />
                </f:facet>
                <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="sochiClientPaymentsTable" renderIfSinglePage="false" maxPages="5"
                                   fastControls="hide" stepControls="auto" boundaryControls="hide">
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

</h:panelGrid>