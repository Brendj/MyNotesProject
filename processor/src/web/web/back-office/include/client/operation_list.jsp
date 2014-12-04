<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра операций по карте --%>
<h:panelGrid id="clientOperationListGrid" binding="#{mainPage.clientOperationListPage.pageComponent}"
             styleClass="borderless-grid">
<h:panelGrid styleClass="borderless-grid" columns="5">
    <h:outputText escape="true" value="Начало периода" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientOperationListPage.startTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Окончание периода" styleClass="output-text" />
    <rich:calendar value="#{mainPage.clientOperationListPage.endTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <a4j:commandButton value="Показать" action="#{mainPage.showClientOperationListPage}"
                       reRender="mainMenu, workspaceTogglePanel" status="clientOperationListGenerateStatus"
                       styleClass="command-button" />
    <a4j:status id="clientOperationListGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
<h:outputText value="Операции по счету:"/>
<rich:dataTable id="clientAccountTransTable" value="#{mainPage.clientOperationListPage.accountTransactionList}"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                var="item" rows="8" footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfTransaction}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Номер счета" />
        </f:facet>
        <h:outputText escape="true" value="#{item.sourceBalanceNumberFormat}" styleClass="output-text" rendered="#{item.sourceBalanceNumber!=null}"/>
        <h:outputText escape="true" value="#{item.client.contractIdFormat}" styleClass="output-text" rendered="#{item.sourceBalanceNumber==null}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Карта" />
        </f:facet>
        <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
            <h:outputText escape="true" value="#{(item.card==null)?(null):item.card.cardNo}" converter="cardNoConverter" />
            <f:setPropertyActionListener value="#{item.card.cardNo}" target="#{mainPage.selectedIdOfCard}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header" sortBy="#{item.transactionTime}">
        <f:facet name="header">
            <h:outputText escape="true" value="Время транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transactionTime}" converter="timeConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Входящий баланс" />
        </f:facet>
        <h:outputText escape="true" value="#{item.balanceBeforeTransaction}" converter="copeckSumConverter" styleClass="output-text"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Субс. АП" />
        </f:facet>
        <h:outputText escape="true" value="#{item.subBalance1BeforeTransaction}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Сумма" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transactionSum}" converter="copeckSumConverter" styleClass="output-text"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Субс. АП" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transactionSubBalance1Sum}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ссылка" />
        </f:facet>
        <h:outputText escape="true" value="#{item.source}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип" />
        </f:facet>
        <h:outputText escape="true" value="#{item.sourceTypeAsString}" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientAccountTransTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Зачисления:"/>
<rich:dataTable id="clientPaymentsTable" value="#{mainPage.clientOperationListPage.clientPaymentList.items}" var="item"
                rows="8"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfTransaction}" styleClass="output-text" />
    </rich:column>
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
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.contragentName}" action="#{mainPage.showContragentViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfContragent}" target="#{mainPage.selectedIdOfContragent}" />
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
        <h:outputText escape="true" value="#{item.paySum}" converter="copeckSumConverter" styleClass="output-text" />
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
        <rich:datascroller for="clientPaymentsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Переводы:"/>
<rich:dataTable id="clientTransfersTable" value="#{mainPage.clientOperationListPage.accountTransferList}" var="item"
                rows="8"
                columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции списания" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transactionOnBenefactor.idOfTransaction}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции зачисления" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transactionOnBeneficiary.idOfTransaction}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Время перевода" />
        </f:facet>
        <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Плательщик" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.clientBenefactor.contractId} (#{item.clientBenefactor.person.fullName})" action="#{mainPage.showClientViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.clientBenefactor.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Получатель" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.clientBeneficiary.contractId} (#{item.clientBeneficiary.person.fullName})" action="#{mainPage.showClientViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.clientBenefactor.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Сумма" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transferSum}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Причина" />
        </f:facet>
        <h:outputText escape="true" value="#{item.reason}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Пользователь" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.createdBy.userName}"
                       action="#{mainPage.showUserViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.createdBy.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientPaymentsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Покупки:"/>
<rich:dataTable id="clientOrdersTable" value="#{mainPage.clientOperationListPage.clientOrderList.items}" var="item"
                rows="8"
                columnClasses="left-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfTransaction}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Организация" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
            <h:outputText escape="true" value="#{item.org.shortName}" />
            <f:setPropertyActionListener value="#{item.org.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. заказа" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfOrder}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Карта" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showCardViewPage}" styleClass="command-link">
            <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter" />
            <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
        </a4j:commandLink>
    </rich:column>
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
            <h:outputText escape="true" value="Социальная скидка" />
        </f:facet>
        <h:outputText escape="true" value="#{item.socDiscount}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Скидка поставщика" />
        </f:facet>
        <h:outputText escape="true" value="#{item.tradeDiscount}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Дотация" />
        </f:facet>
        <h:outputText escape="true" value="#{item.grantSum}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Состав" />
        </f:facet>
        <rich:dataTable value="#{item.details}" var="detail"
                        columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column"
                        footerClass="data-table-footer">
            <rich:column>
                <h:outputText escape="true" value="#{detail.itemCode}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{detail.menuDetailName}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{detail.RPrice}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{detail.menuOutput}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{detail.qty}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{detail.rootMenu}" styleClass="output-text" />
            </rich:column>
        </rich:dataTable>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" styleClass="output-text" />
        </rich:column>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientOrdersTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Возвраты:"/>
<rich:dataTable id="clientRefundTable" value="#{mainPage.clientOperationListPage.accountRefundList}" var="item"
                rows="8"
                columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{item.transaction.idOfTransaction}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Время" />
        </f:facet>
        <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Сумма" />
        </f:facet>
        <h:outputText escape="true" value="#{item.refundSum}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Причина" />
        </f:facet>
        <h:outputText escape="true" value="#{item.reason}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Пользователь" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.createdBy.userName}"
                       action="#{mainPage.showUserViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.createdBy.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientRefundTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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


<h:outputText value="SMS-сообщения:"/>
<rich:dataTable id="clientSmsTable" value="#{mainPage.clientOperationListPage.clientSmsList.items}" var="item" rows="8"
                columnClasses="right-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfTransaction}" styleClass="output-text" />
    </rich:column>
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
        <h:outputText escape="true" value="#{item.deliveryTime}" converter="timeConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Стоимость" />
        </f:facet>
        <h:outputText escape="true" value="#{item.price}" converter="copeckSumConverter" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientSmsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Проходы:" />
<rich:dataTable id="clientPassesTable" var="pass" value="#{mainPage.clientOperationListPage.clientPasses}"
                rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer" rows="8">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="№" />
        </f:facet>
        <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Организация" />
        </f:facet>
        <h:outputText escape="true" value="#{pass.orgName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Наименование входа" />
        </f:facet>
        <h:outputText escape="true" value="#{pass.enterName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Дата и время" />
        </f:facet>
        <h:outputText escape="true" value="#{pass.enterTime}" styleClass="output-text"
                      converter="timeMinuteConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Направление" />
        </f:facet>
        <h:outputText escape="true" value="#{pass.direction}" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="clientPassesTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Регулярные платежи:" />
<rich:dataTable id="regularPaymentsTable" var="pay" value="#{mainPage.clientOperationListPage.regularPayments}"
                rowKeyVar="row" footerClass="data-table-footer" rows="8"
                columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, left-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="№" />
        </f:facet>
        <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <h:outputText escape="true" value="#{pay.idOfPayment}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Время платежа" />
        </f:facet>
        <h:outputText escape="true" value="#{pay.paymentDate}" styleClass="output-text"
                      converter="timeMinuteConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Сумма" />
        </f:facet>
        <h:outputText escape="true" value="#{pay.paymentAmount}" styleClass="output-text"
                      converter="copeckSumConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Баланс до пополнения" />
        </f:facet>
        <h:outputText escape="true" value="#{pay.clientBalance}" styleClass="output-text"
                      converter="copeckSumConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Платеж успешный" />
        </f:facet>
        <h:outputText escape="true" value='#{pay.success ? "Да" : "Нет"}' styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="RRN транзакции" />
        </f:facet>
        <h:outputText escape="true" value="#{pay.rrn}" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="regularPaymentsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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


<h:outputText value="Перемещения внутри ОО:" />
<rich:dataTable id="clientGroupMigrationHistoriesTable" var="gMig" value="#{mainPage.clientOperationListPage.clientGroupMigrationHistories}"
               footerClass="data-table-footer" rows="8"
                columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, left-aligned-column">

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид ОО" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.org.idOfOrg}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Наименование ОО" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.org.shortName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Прежняя группа" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.oldGroupName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Новая группа" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.newGroupName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Дата перемещения" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.registrationDate}" styleClass="output-text" >
            <f:convertDateTime pattern="dd.MM.yyyy"/>
        </h:outputText>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Комментарий" />
        </f:facet>
        <h:outputText escape="true" value="#{gMig.comment}" styleClass="output-text" />
    </rich:column>

    <f:facet name="footer">
        <rich:datascroller for="clientGroupMigrationHistoriesTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:outputText value="Перемещения между ОО:" />
<rich:dataTable id="clientMigrationsTable" var="mig" value="#{mainPage.clientOperationListPage.clientMigrations}"
                rowKeyVar="row" footerClass="data-table-footer" rows="8"
                columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, left-aligned-column">

    <f:facet name="header">
        <rich:columnGroup columnClasses="gray">
            <rich:column headerClass="column-header gray" colspan="2">
                <h:outputText value="Данные прежнего поставщика" />
            </rich:column>


            <rich:column headerClass="column-header" colspan="3">
                <h:outputText value="Данные прежней ОО" />
            </rich:column>


            <rich:column headerClass="column-header" colspan="2">
                <h:outputText value="Данные нового поставщика" />
            </rich:column>

            <rich:column headerClass="column-header" colspan="3">
                <h:outputText value="Данные новой ОО" />
            </rich:column>

            <rich:column headerClass="column-header" rowspan="2">
                <h:outputText value="Дата перемещения" />
            </rich:column>

            <rich:column headerClass="column-header" rowspan="2">
                <h:outputText value="Баланс" />
            </rich:column>

            <rich:column headerClass="column-header" rowspan="2">
                <h:outputText value="Комментарий" />
            </rich:column>



            <rich:column headerClass="column-header" breakBefore="true">
                <h:outputText value="Ид" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Наименование" />
            </rich:column>


            <rich:column headerClass="column-header">
                <h:outputText value="Ид" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Наименование" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Группа" />
            </rich:column>



            <rich:column headerClass="column-header">
                <h:outputText value="Ид" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Наименование" />
            </rich:column>


            <rich:column headerClass="column-header">
                <h:outputText value="Ид" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Наименование" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText value="Группа" />
            </rich:column>


        </rich:columnGroup>
    </f:facet>


    <rich:column  >
        <h:outputText value="#{mig.oldContragent.idOfContragent}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.oldContragent.contragentName}" styleClass="output-text" />
    </rich:column>


    <rich:column  >
        <h:outputText value="#{mig.oldOrg.idOfOrg}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.oldOrg.shortName}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.oldGroupName}" styleClass="output-text" />
    </rich:column>


    <rich:column >
        <h:outputText value="#{mig.newContragent.idOfContragent}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.newContragent.contragentName}" styleClass="output-text" />
    </rich:column>


    <rich:column  >
        <h:outputText value="#{mig.org.idOfOrg}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.org.shortName}" styleClass="output-text" />
    </rich:column>
    <rich:column >
        <h:outputText value="#{mig.newGroupName}" styleClass="output-text" />
    </rich:column>


    <rich:column >
        <h:outputText value="#{mig.registrationDate}" styleClass="output-text">
            <f:convertDateTime pattern="dd.MM.yyyy" />
        </h:outputText>
    </rich:column>


    <rich:column >
        <h:outputText value="#{mig.balance}" styleClass="output-text" />
    </rich:column>


    <rich:column >
        <h:outputText value="#{mig.comment}" styleClass="output-text" />
    </rich:column>

    <f:facet name="footer">
        <rich:datascroller for="clientMigrationsTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
