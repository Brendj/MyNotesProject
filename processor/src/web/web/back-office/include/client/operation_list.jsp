<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="clientApplicationForFoodMessagePanel" autosized="true" minWidth="500">
    <f:facet name="header">
        <h:outputText
                value="История статусов по заявлению #{mainPage.clientOperationListPage.currentApplicationForFood.serviceNumber}" />
    </f:facet>
    <rich:dataTable id="clientApplicationForFoodHistoryTable" value="#{mainPage.clientOperationListPage.historyItems}"
                    var="appForFoodHistoryItem" rows="25" footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Статус" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Расшифровка" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата создания" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Отправка в ЕТП" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{appForFoodHistoryItem.applicationForFoodStateString}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{appForFoodHistoryItem.statusTitle}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{appForFoodHistoryItem.createdDate}" styleClass="output-text"
                          converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{appForFoodHistoryItem.sendDate}" styleClass="output-text"
                          converter="timeConverter" />
        </rich:column>
    </rich:dataTable>
    <rich:spacer height="20px" />
    <a4j:commandButton value="Закрыть" onclick="Richfaces.hideModalPanel('clientApplicationForFoodMessagePanel')"
                       style="width: 180px;" ajaxSingle="true" />
</rich:modalPanel>
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
        <a4j:commandButton value="Показать" action="#{mainPage.showClientOperationListPage(true)}"
                           reRender="mainMenu, workspaceTogglePanel" status="clientOperationListGenerateStatus"
                           styleClass="command-button" />
        <a4j:status id="clientOperationListGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText value="Операции по счету:" />
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
                <h:outputText escape="true" value="#{item.sourceBalanceNumberFormat}" styleClass="output-text"
                              rendered="#{item.sourceBalanceNumber!=null}" />
                <h:outputText escape="true" value="#{item.client.contractIdFormat}" styleClass="output-text"
                              rendered="#{item.sourceBalanceNumber==null}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Карта" />
                </f:facet>
                <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link"
                                 reRender="mainMenu, workspaceForm">
                    <h:outputText escape="true" value="#{(item.card==null)?(null):item.card.cardNo}"
                                  converter="cardNoConverter" />
                    <f:setPropertyActionListener value="#{item.card.cardNo}" target="#{mainPage.selectedIdOfCard}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header" sortBy="#{item.transactionTime}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время транзакции" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Входящий баланс" />
                </f:facet>
                <h:outputText escape="true" value="#{item.balanceBeforeTransaction}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Субс. АП" />
                </f:facet>
                <h:outputText escape="true" value="#{item.subBalance1BeforeTransaction}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionSum}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Субс. АП" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionSubBalance1Sum}" converter="copeckSumConverter"
                              styleClass="output-text" />
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
                <rich:datascroller for="clientAccountTransTable" renderIfSinglePage="false" maxPages="5"
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

        <h:outputText value="Зачисления:" />
        <rich:dataTable id="clientPaymentsTable" value="#{mainPage.clientOperationListPage.clientPaymentList.items}"
                        var="item" rows="8"
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
                <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Контрагент" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.contragentName}"
                                 action="#{mainPage.showContragentViewPage}" styleClass="command-link">
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

        <h:outputText value="Переводы:" />
        <rich:dataTable id="clientTransfersTable" value="#{mainPage.clientOperationListPage.accountTransferList}"
                        var="item" rows="8"
                        columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Ид. транзакции списания" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionOnBenefactor.idOfTransaction}"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Ид. транзакции зачисления" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionOnBeneficiary.idOfTransaction}"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время перевода" />
                </f:facet>
                <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Плательщик" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceForm"
                                 value="#{item.clientBenefactor.contractId} (#{item.clientBenefactor.person.fullName})"
                                 action="#{mainPage.showClientViewPage}" styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.clientBenefactor.idOfClient}"
                                                 target="#{mainPage.selectedIdOfClient}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Получатель" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceForm"
                                 value="#{item.clientBeneficiary.contractId} (#{item.clientBeneficiary.person.fullName})"
                                 action="#{mainPage.showClientViewPage}" styleClass="command-link">
                    <f:setPropertyActionListener value="#{item.clientBenefactor.idOfClient}"
                                                 target="#{mainPage.selectedIdOfClient}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transferSum}" converter="copeckSumConverter"
                              styleClass="output-text" />
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
                    <f:setPropertyActionListener value="#{item.createdBy.idOfUser}"
                                                 target="#{mainPage.selectedIdOfUser}" />
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

        <h:outputText value="Покупки:" />
        <rich:dataTable id="clientOrdersTable" value="#{mainPage.clientOperationListPage.clientOrderList.items}"
                        var="item" rows="8"
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
                <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showOrgViewPage}"
                                 styleClass="command-link">
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
                <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showCardViewPage}"
                                 styleClass="command-link">
                    <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter" />
                    <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время покупки" />
                </f:facet>
                <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время транзакции" />
                </f:facet>
                <h:outputText escape="true" value="#{item.transactionTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" sortBy="#{item.orderDate}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Время пробития" />
                </f:facet>
                <h:outputText escape="true" value="#{item.orderDate}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.RSum}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Социальная скидка" />
                </f:facet>
                <h:outputText escape="true" value="#{item.socDiscount}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Скидка поставщика" />
                </f:facet>
                <h:outputText escape="true" value="#{item.tradeDiscount}" converter="copeckSumConverter"
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

        <h:outputText value="Возвраты:" />
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
                <h:outputText escape="true" value="#{item.createTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сумма" />
                </f:facet>
                <h:outputText escape="true" value="#{item.refundSum}" converter="copeckSumConverter"
                              styleClass="output-text" />
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
                    <f:setPropertyActionListener value="#{item.createdBy.idOfUser}"
                                                 target="#{mainPage.selectedIdOfUser}" />
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


        <h:outputText value="Сообщения:" />
        <rich:dataTable id="clientSmsTable" value="#{mainPage.clientOperationListPage.clientSmsList.items}" var="item"
                        rows="8"
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
                    <h:outputText escape="true" value="Время события" />
                </f:facet>
                <h:outputText escape="true" value="#{item.eventTime}" converter="timeConverter"
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
                    <h:outputText escape="true" value="Время доставки" />
                </f:facet>
                <h:outputText escape="true" value="#{item.deliveryTime}" converter="timeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Стоимость" />
                </f:facet>
                <h:outputText escape="true" value="#{item.price}" converter="copeckSumConverter"
                              styleClass="output-text" />
            </rich:column>
            <%--<rich:column headerClass="column-header">--%>
            <%--<f:facet name="header">--%>
            <%--<h:outputText escape="true" value="Тип события" />--%>
            <%--</f:facet>--%>
            <%--<h:outputText escape="true" value="#{item.eventType}" converter="smsContentsTypeConverter"--%>
            <%--styleClass="output-text" />--%>
            <%--</rich:column>--%>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Идентификатор события" />
                </f:facet>
                <h:outputText escape="true" value="#{item.eventId == null ? '-' : item.eventId}"
                              styleClass="output-text" />
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
                        rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer" rows="8"
                        columns="11">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="№" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="ID OO" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Название ОО" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Адрес" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Наименование события" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Дата и время" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Тип карты" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Направление" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Кто отметил" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Группа отметившего" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                        <h:outputText escape="true" value="Л/с отметившего" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>

            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.orgName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.shortAddress}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.enterName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.enterTime}" styleClass="output-text"
                              converter="timeMinuteConverter" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.cardType}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rowspan="#{pass.chekerItemListCount}">
                <h:outputText escape="true" value="#{pass.direction}" styleClass="output-text" />
            </rich:column>

            <rich:subTable value="#{pass.chekerItemList}" var="cheker"
                           columnClasses="center-aligned-column, center-aligned-column, center-aligned-column">
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="#{cheker.cheker}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="#{cheker.groupName}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link"
                                     reRender="mainMenu, workspaceForm">
                        <h:outputText escape="true" value="#{cheker.contractId}" converter="contractIdConverter"
                                      styleClass="output-text" />
                        <f:setPropertyActionListener value="#{cheker.idOfClient}"
                                                     target="#{mainPage.selectedIdOfClient}" />
                    </a4j:commandLink>
                </rich:column>
            </rich:subTable>

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

        <h:outputText value="История уведомлений для поставщиков Smart-часов (для владельцев Smart-часов):" />
        <rich:dataTable id="clientGeoplanerJournal" var="geoplanerJournal"
                        value="#{mainPage.clientOperationListPage.geoplanerNotificationJournalList}" rowKeyVar="row"
                        columnClasses="center-aligned-column" footerClass="data-table-footer" rows="8" columns="10">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="ID OO" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Название ОО" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Поставщик карт" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="ID события прохода" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="ID покупки" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="ID пополнения клиента" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Сервер отправки" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Дата создания записи" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="HTTP-код ответа сервера" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Текст ошибки" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.org.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.org.shortName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.vendor.name}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.idOfEnterEvents}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.idOfOrder}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.idOfClientPayment}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.nodeName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.createDate}" styleClass="output-text"
                              converter="timeMinuteConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.response}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{geoplanerJournal.errorText}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="clientGeoplanerJournal" renderIfSinglePage="false" maxPages="5"
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

        <h:outputText value="Регулярные платежи:" />
        <rich:panel headerClass="workspace-panel-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Автоплатежи" />
            </f:facet>
            <h:outputText value="Подписки на автопополнение баланса:" styleClass="output-text" />
            <rich:dataTable id="clientBankSubscriptions" value="#{mainPage.clientOperationListPage.bankSubscriptions}"
                            var="sub" rows="8" rowKeyVar="row" columnClasses="right-aligned-column, left-aligned-column, center-aligned-column, right-aligned-column, right-aligned-column,
                    left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column"
                            footerClass="data-table-footer">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="№" />
                    </f:facet>
                    <h:outputText escape="true" value="#{row + 1}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Ид подписки" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.idOfSubscription}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Телефон представителя" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.mobile}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Дата подключения" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.activationDate}" styleClass="output-text"
                                  converter="timeConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Дата окончания" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.validToDate}" styleClass="output-text"
                                  converter="dateConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Кол-во месяцев" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.monthsCount}" styleClass="output-text"
                                  rendered="#{sub.showMonthsCount()}" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Дата отключения" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.deactivationDate}" styleClass="output-text"
                                  converter="dateConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Активная" />
                    </f:facet>
                    <h:outputText escape="true" value='#{sub.active ? "Да" : "Нет"}' styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Порог баланса" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.thresholdAmount}" styleClass="output-text"
                                  converter="copeckSumConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Сумма пополнения" />
                    </f:facet>
                    <h:outputText escape="true" value="#{sub.paymentAmount}" styleClass="output-text"
                                  converter="copeckSumConverter" />
                </rich:column>
                <f:facet name="footer">
                    <rich:datascroller for="clientBankSubscriptions" renderIfSinglePage="false" maxPages="5"
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
            <h:outputText value="Регулярные платежи:" styleClass="output-text" />
            <rich:dataTable id="regularPaymentsTable" var="pay"
                            value="#{mainPage.clientOperationListPage.regularPayments}" rowKeyVar="row"
                            footerClass="data-table-footer" rows="8"
                            columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, left-aligned-column">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="№" />
                    </f:facet>
                    <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Ид подписки" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.bankSubscription.idOfSubscription}"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Ид запроса" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.idOfPayment}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Время запроса" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.paymentDate}" styleClass="output-text"
                                  converter="timeMinuteConverter" />
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
                        <h:outputText escape="true" value="Время платежа" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.clientPayment.createTime}" styleClass="output-text"
                                  converter="timeMinuteConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Код ошибки" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.errorCode}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Описание ошибки" />
                    </f:facet>
                    <h:outputText escape="true" value="#{pay.errorDesc}" styleClass="output-text" />
                </rich:column>
                <f:facet name="footer">
                    <rich:datascroller for="regularPaymentsTable" renderIfSinglePage="false" maxPages="5"
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
        </rich:panel>

        <h:outputText value="Перемещения внутри ОО:" />
        <rich:dataTable id="clientGroupMigrationHistoriesTable" var="gMig"
                        value="#{mainPage.clientOperationListPage.clientGroupMigrationHistories}"
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
                <h:outputText escape="true" value="#{gMig.registrationDate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy" />
                </h:outputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Комментарий" />
                </f:facet>
                <h:outputText escape="true" value="#{gMig.comment}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="clientGroupMigrationHistoriesTable" renderIfSinglePage="false" maxPages="5"
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

        <h:outputText value="Перемещения между ОО:" />
        <rich:dataTable id="clientMigrationsTable" var="mig"
                        value="#{mainPage.clientOperationListPage.clientMigrations}" rowKeyVar="row"
                        footerClass="data-table-footer" rows="8"
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


            <rich:column>
                <h:outputText value="#{mig.oldContragent.idOfContragent}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.oldContragent.contragentName}" styleClass="output-text" />
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.oldOrg.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.oldOrg.shortName}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.oldGroupName}" styleClass="output-text" />
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.newContragent.idOfContragent}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.newContragent.contragentName}" styleClass="output-text" />
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.org.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.org.shortName}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{mig.newGroupName}" styleClass="output-text" />
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.registrationDate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy" />
                </h:outputText>
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.balance}" styleClass="output-text" />
            </rich:column>


            <rich:column>
                <h:outputText value="#{mig.comment}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="clientMigrationsTable" renderIfSinglePage="false" maxPages="5"
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

        <h:outputText value="Изменение льгот:" />
        <rich:dataTable id="clientDiscountsChangeTable" var="dis"
                        value="#{mainPage.clientOperationListPage.discountChangeHistories}" rowKeyVar="row"
                        footerClass="data-table-footer" rows="8"
                        columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, left-aligned-column">

            <f:facet name="header">
                <rich:columnGroup columnClasses="gray">
                    <rich:column headerClass="column-header" colspan="2">
                        <h:outputText value="Новые данные" />
                    </rich:column>

                    <rich:column headerClass="column-header" colspan="2">
                        <h:outputText value="Прежние данные" />
                    </rich:column>

                    <rich:column headerClass="column-header" colspan="2">
                        <h:outputText value="Данные ОО" />
                    </rich:column>

                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText value="Дата изменения" />
                    </rich:column>

                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText value="Комментарий" />
                    </rich:column>

                    <rich:column headerClass="column-header" breakBefore="true">
                        <h:outputText value="Тип льготы" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText value="Категории" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="Тип льготы" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText value="Категории" />
                    </rich:column>

                    <rich:column headerClass="column-header">
                        <h:outputText value="ИД" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText value="Наименование" />
                    </rich:column>

                </rich:columnGroup>
            </f:facet>

            <rich:column>
                <h:outputText value="#{dis.getDiscountModeString(dis.discountMode)}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{dis.getCategoriesDiscountsString(dis.categoriesDiscounts)}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column>
                <h:outputText value="#{dis.getDiscountModeString(dis.oldDiscountMode)}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{dis.getCategoriesDiscountsString(dis.oldCategoriesDiscounts)}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column>
                <h:outputText value="#{dis.org.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText value="#{dis.org.shortName}" styleClass="output-text" />
            </rich:column>

            <rich:column>
                <h:outputText value="#{dis.registrationDate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy" />
                </h:outputText>
            </rich:column>

            <rich:column>
                <h:outputText value="#{dis.comment}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="clientDiscountsChangeTable" renderIfSinglePage="false" maxPages="5"
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

        <h:outputText value="Заявления на льготное питание:" />
        <rich:dataTable id="clientApplicationsForFoodTable" var="app"
                        value="#{mainPage.clientOperationListPage.applicationsForFood}" rowKeyVar="row"
                        footerClass="data-table-footer" rows="25">
            <f:facet name="header">
                <rich:columnGroup columnClasses="gray">
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Номер заявления" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Дата заявления" />
                    </rich:column>
                    <rich:column headerClass="column-header" colspan="2">
                        <h:outputText escape="true" value="Период действия льготы" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Статус" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Дата статуса" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Архив" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Дата архивирования" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Льгота" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="ФИО заявителя" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Телефон заявителя" />
                    </rich:column>
                    <rich:column headerClass="column-header" rowspan="2">
                        <h:outputText escape="true" value="Доп. информация" />
                    </rich:column>
                    <rich:column headerClass="column-header" breakBefore="true">
                        <h:outputText escape="true" value="С" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="По" />
                    </rich:column>

                </rich:columnGroup>
            </f:facet>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.serviceNumber}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.createdDate}" styleClass="output-text"
                              converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.startDate}" styleClass="output-text"
                              converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.endDate}" styleClass="output-text" converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.applicationForFoodStateString}" styleClass="output-text"
                              title="#{app.statusTitle}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.lastUpdate}" styleClass="output-text"
                              converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.archieved}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.archiveDate}" styleClass="output-text"
                              converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.benefit}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.applicantFio}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{app.mobile}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <a4j:commandButton value="..." reRender="clientApplicationForFoodMessagePanel" ajaxSingle="true"
                                   oncomplete="Richfaces.showModalPanel('clientApplicationForFoodMessagePanel');">
                    <f:setPropertyActionListener value="#{app}"
                                                 target="#{mainPage.clientOperationListPage.currentApplicationForFood}" />
                </a4j:commandButton>
            </rich:column>
        </rich:dataTable>

        <h:panelGrid columns="2">
            <h:outputText value="Изменение номера телефона" />
            <a4j:commandButton value="Построить" actionListener="#{mainPage.clientOperationListPage.getHistoryMobileChange}"
                               reRender="historyChangeMobile"
                               styleClass="command-button" />
        </h:panelGrid>
        <h:form>
        <rich:dataTable id="historyChangeMobile" value="#{mainPage.clientOperationListPage.clientsMobileHistories}"
                        var="item" rows="8"
                        columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Предыдущий номер телефона" />
                </f:facet>
                <h:outputText escape="true" value="#{item.oldmobile}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Новый номер телефона" />
                </f:facet>
                <h:outputText escape="true" value="#{item.newmobile}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Действие" />
                </f:facet>
                <h:outputText escape="true" value="#{item.action}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" sortBy="#{item.createdate}">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата" />
                </f:facet>
                <h:outputText escape="true" value="#{item.createdate}" converter="dateTimeConverter"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Где изменено" />
                </f:facet>
                <h:outputText escape="true" value="#{item.showing}" styleClass="output-text" />
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
        </h:form>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
