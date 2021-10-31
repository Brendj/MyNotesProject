<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="4">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.contragentClientPaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата (не включая)" styleClass="output-text" />
        <rich:calendar value="#{mainPage.contragentClientPaymentReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildContragentClientPaymentReport}"
                           reRender="workspaceTogglePanel, contragentClientPaymentReportTable"
                           styleClass="command-button" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true"
                      value="Отчет по платежам клиентов через контрагента \"#{mainPage.contragentClientPaymentReportPage.shortName}\", выводится не более #{mainPage.contragentClientPaymentReportPage.recordsLimit} записей"
                      styleClass="output-text" />
        <rich:dataTable id="contragentClientPaymentReportTable"
                        value="#{mainPage.contragentClientPaymentReportPage.contragentClientPaymentReport.clientPaymentItems}"
                        var="clientPayment">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Дата платежа" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" styleClass="output-text" value="Идентификатор платежа" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Дата транзакции" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" colspan="4">
                        <h:outputText escape="true" value="Клиент" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Номер карты" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Сумма платежа" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column breakBefore="true" styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Номер договора" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Фамилия" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Имя" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Отчество" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                    <rich:column breakBefore="true" styleClass="left-aligned-column" colspan="8">
                        <h:outputText escape="true" value="Итого" styleClass="output-text" style="color: #ffffff;" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column" >
                        <h:outputText escape="true"
                                      value="#{mainPage.contragentClientPaymentReportPage.contragentClientPaymentReport.totalSum}"
                                      converter="copeckSumConverter" styleClass="output-text" style="color: #ffffff;"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.createTime}" styleClass="output-text"
                              converter="timeConverter" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.idOfPayment}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.transaction.transactionTime}"
                              styleClass="output-text" converter="timeConverter" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showClientViewPage}" styleClass="command-link">
                    <h:outputText escape="true" value="#{clientPayment.client.contractId}"
                                  converter="contractIdConverter" styleClass="output-text" />
                    <f:setPropertyActionListener value="#{clientPayment.client.idOfClient}"
                                                 target="#{mainPage.selectedIdOfClient}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.client.person.surname}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.client.person.firstName}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.client.person.secondName}"
                              styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showCardViewPage}" styleClass="command-link">
                    <h:outputText escape="true" value="#{clientPayment.card.cardNo}" converter="cardNoConverter"
                                  styleClass="output-text" />
                    <f:setPropertyActionListener value="#{clientPayment.card.idOfCard}"
                                                 target="#{mainPage.selectedIdOfCard}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText escape="true" value="#{clientPayment.paySum}" styleClass="output-text"
                              converter="copeckSumConverter" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
