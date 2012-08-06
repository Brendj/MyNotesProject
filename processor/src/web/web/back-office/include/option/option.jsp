<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="optionPage" type="ru.axetta.ecafe.processor.web.ui.option.OptionPage"--%>
<h:panelGrid id="optionPanelGrid" binding="#{optionPage.pageComponent}" styleClass="borderless-grid">
   <rich:tabPanel>
        <rich:tab label="Общие">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Использовать схему с оператором" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.withOperator}" styleClass="output-text" />
                <h:outputText escape="true" value="Отпралять СМС-уведомление о событиях входа-выхода" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.notifyBySMSAboutEnterEvent}" styleClass="output-text" />
            </h:panelGrid>
            <h:panelGroup styleClass="borderless-grid">
                <h:outputText escape="true" value="Удалять записи меню в базе" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.cleanMenu}" styleClass="output-text" />
                <h:outputText escape="true" value="Хранить дней от текущей даты " styleClass="output-text" />
                <h:inputText value="#{optionPage.menuDaysForDeletion}" styleClass="input-text" size="3"/>
            </h:panelGroup>
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Лимит овердрафта по-умолчанию для новых клиентов" styleClass="output-text" />
                <h:inputText value="#{optionPage.defaultOverdraftLimit}" styleClass="input-text" size="5"/>
                <h:outputText escape="true" value="Лимит дневных трат по-умолчанию для новых клиентов" styleClass="output-text" />
                <h:inputText value="#{optionPage.defaultExpenditureLimit}" styleClass="input-text" size="5"/>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="Взаимодействие">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Журналировать транзакции в БД" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.journalTransactions}" styleClass="output-text" />
                <h:outputText escape="true" value="Отправлять транзакции в ИС НФП" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.sendJournalTransactionsToNFP}" styleClass="output-text" />
                <h:outputText escape="true" value="URL-адрес сервиса приема ИС НФП" styleClass="output-text" />
                <h:inputText value="#{optionPage.nfpServiceAddress}" styleClass="input-text" size="40"/>
            </h:panelGrid>
        </rich:tab>

       <rich:tab label="Платежные системы">
           <h:outputText escape="true" value="Выводить в личный кабинет" styleClass="output-text" />
           <h:panelGrid styleClass="borderless-grid" columns="4">

               <h:outputText escape="true" value="Chronopay" styleClass="output-text" />
               <h:selectBooleanCheckbox value="#{optionPage.chronopaySection}" styleClass="output-text" />
               <h:outputText escape="true" value="Рамер комиссии" styleClass="output-text" />
               <h:inputText value="#{optionPage.chronopayRate}" styleClass="input-text" size="10"/>

               <h:outputText escape="true" value="RBKMoney" styleClass="output-text" />
               <h:selectBooleanCheckbox value="#{optionPage.rbkSection}" styleClass="output-text" />
               <h:outputText escape="true" value="Рамер комиссии" styleClass="output-text" />
               <h:inputText value="#{optionPage.rbkRate}" styleClass="input-text" size="10"/>

           </h:panelGrid>
       </rich:tab>
    </rich:tabPanel>

    <h:panelGroup style="margin-top: 10px">
        <a4j:commandButton value="Сохранить" action="#{optionPage.save}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                           styleClass="command-button" />
        <a4j:commandButton value="Отмена" action="#{optionPage.cancel}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                           styleClass="command-button" />
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>