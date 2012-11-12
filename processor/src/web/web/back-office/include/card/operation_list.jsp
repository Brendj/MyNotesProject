<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра операций по карте --%>
<h:panelGrid id="cardOperationListGrid" binding="#{mainPage.cardOperationListPage.pageComponent}"
             styleClass="borderless-grid">
<h:panelGrid styleClass="borderless-grid" columns="5">
    <h:outputText escape="true" value="Начало периода" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardOperationListPage.startTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Окончание периода" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardOperationListPage.endTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <a4j:commandButton value="Показать" action="#{mainPage.showCardOperationListPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:dataTable id="cardPaymentsTable" value="#{mainPage.cardOperationListPage.cardPaymentListViewer.items}"
                    var="item" rows="8"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время платежа" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Контрагент" />
            </f:facet>
            <a4j:commandLink value="#{item.contragentName}" action="#{mainPage.showContragentViewPage}"
                           styleClass="command-link" reRender="mainMenu, workspaceForm">
                <f:setPropertyActionListener value="#{item.idOfContragent}"
                                             target="#{mainPage.selectedIdOfContragent}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор платежа в системе контрагента" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfPayment}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сумма" />
            </f:facet>
            <h:outputText escape="true" value="#{item.paySum}" converter="copeckSumConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Метод оплаты" />
            </f:facet>
            <h:outputText escape="true" value="#{item.paymentMethod}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Метод оплаты (доп.)" />
            </f:facet>
            <h:outputText escape="true" value="#{item.addPaymentMethod}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор платежа (доп.)" />
            </f:facet>
            <h:outputText escape="true" value="#{item.addIdOfPayment}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardPaymentsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <rich:dataTable id="cardSmsTable" value="#{mainPage.cardOperationListPage.cardSmsList.items}" var="item" rows="8"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfSms}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Телефонный номер" />
            </f:facet>
            <h:outputText escape="true" value="#{item.phone}" converter="phoneConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип содержимого" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contentsType}" converter="smsContentsTypeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус доставки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.deliveryStatus}" converter="smsDeliveryStatusConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время отправки в шлюз" />
            </f:facet>
            <h:outputText escape="true" value="#{item.serviceSendTime}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время отправки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.sendTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время доставки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.deliveryTime}" converter="timeConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Стоимость" />
            </f:facet>
            <h:outputText escape="true" value="#{item.price}" converter="copeckSumConverter" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardSmsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <rich:dataTable id="cardOrdersTable" value="#{mainPage.cardOperationListPage.cardOrderListViewer.items}" var="item"
                    rows="8"
                    columnClasses="left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время покупки" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сумма" />
            </f:facet>
            <h:outputText escape="true" value="#{item.RSum}" converter="copeckSumConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Скидка" />
            </f:facet>
            <h:outputText escape="true" value="#{item.discount}" converter="copeckSumConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дотация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.grantSum}" converter="copeckSumConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Состав" />
            </f:facet>
            <rich:dataTable value="#{item.details}" var="detail"
                            columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column"
                            footerClass="data-table-footer">
                <rich:column>
                    <h:outputText escape="true" value="#{detail.menuDetailName}" styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{detail.RPrice}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{detail.qty}" styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{detail.rootMenu}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardOrdersTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
<rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>