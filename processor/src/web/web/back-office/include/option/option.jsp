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
               <h:outputText escape="true" value="Размер комиссии" styleClass="output-text" />
               <h:inputText value="#{optionPage.chronopayRate}" styleClass="input-text" size="10"/>

               <h:outputText escape="true" value="RBKMoney" styleClass="output-text" />
               <h:selectBooleanCheckbox value="#{optionPage.rbkSection}" styleClass="output-text" />
               <h:outputText escape="true" value="Размер комиссии" styleClass="output-text" />
               <h:inputText value="#{optionPage.rbkRate}" styleClass="input-text" size="10"/>

           </h:panelGrid>


           <%--<h:dataTable value="#{optionPage.banks}" var="bank">


               <h:column headerClass="column-header">
                   <f:facet name="header">Наименование</f:facet>
                   <h:inputText value="#{bank.name}" styleClass="input-text" size="20"/>

               </h:column>
               <h:column headerClass="column-header">
                   <f:facet name="header">URL логотипа</f:facet>
                   <h:inputText value="#{bank.logoUrl}" styleClass="input-text" size="20"/>

               </h:column>
               <h:column headerClass="column-header">
                   <f:facet name="header">URL на адреса</f:facet>
                   <h:inputText value="#{bank.terminalsUrl}" styleClass="input-text" size="20"/>
               </h:column>
               <h:column headerClass="column-header">
                   <f:facet name="header">Комиссия</f:facet>
                   <h:inputText value="#{bank.rate}" styleClass="input-text" size="20"/>
               </h:column>
               <h:column headerClass="column-header">
                   <f:facet name="header">Минимальная комиссия</f:facet>
                   <h:inputText value="#{bank.minRate}" styleClass="input-text" size="20"/>
               </h:column>
               <h:column headerClass="column-header">
                   <f:facet name="header">Тип зачисления</f:facet>
                   <h:inputText value="#{bank.enrollmentType}" styleClass="input-text" size="20"/>
               </h:column>
               <h:column>
                   <a4j:commandButton value="Удалить" action="#{bank.delete}"
                                             reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                                             styleClass="command-button" />
               </h:column>
           </h:dataTable>--%>

           <%--@elvariable id="bankListPage" type="ru.axetta.ecafe.processor.web.ui.option.banks.BankListPage"--%>
           <%--@elvariable id="bankDeletePage" type="ru.axetta.ecafe.processor.web.ui.option.banks.BankDeletePage"--%>
           <rich:dataTable id="bankListTable" width="700" var="bank" value="#{bankListPage.entityList}"
                           rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
               <rich:column  headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="№" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:outputText styleClass="output-text" value="#{row+1}" />
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="Наименование" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.name}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="URL логотипа" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.logoUrl}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="URL на адреса" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.terminalsUrl}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="Комиссия" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.rate}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="Минимальная комиссия" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.minRate}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column headerClass="column-header">
                   <f:facet name="header">
                       <h:outputText value="Тип зачисления" styleClass="output-text" escape="true"/>
                   </f:facet>
                   <h:inputText value="#{bank.enrollmentType}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column style="text-align:center">
                   <f:facet name="header">
                       <h:outputText value="Удалить" escape="true"/>
                   </f:facet>
                   <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                                    oncomplete="#{rich:component('removedBankItemDeletePanel')}.show()">
                       <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                       <f:setPropertyActionListener value="#{bank}" target="#{bankDeletePage.currentEntity}" />
                   </a4j:commandLink>
               </rich:column>
               <f:facet name="footer">
                   <rich:datascroller for="bankListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

           <a4j:commandButton value="Добавить банк" action="#{bankListPage.addBank}"
                              reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                              styleClass="command-button"/>

           <%--<rich:dataTable  value="#{optionPage.banks}"
                           var="bank"
                           >
               <f:facet name="header">
                   <rich:columnGroup>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="Наименование" styleClass="output-text" />
                       </rich:column>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="URL логотипа" styleClass="output-text" />
                       </rich:column>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="URL на адреса" styleClass="output-text" />
                       </rich:column>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="Комиссия" styleClass="output-text" />
                       </rich:column>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="Минимальная комиссия" styleClass="output-text" />
                       </rich:column>
                       <rich:column headerClass="center-aligned-column" rowspan="2">
                           <h:outputText escape="true" value="Тип зачисления" styleClass="output-text"/>
                       </rich:column>

                   </rich:columnGroup>
               </f:facet>

               <rich:column styleClass="center-aligned-column">
                   <h:inputText value="#{bank.name}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <h:inputText value="#{bank.logoUrl}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <h:inputText value="#{bank.terminalsUrl}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <h:inputText value="#{bank.rate}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <h:inputText value="#{bank.minRate}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <h:inputText value="#{bank.enrollmentType}" styleClass="input-text" size="20"/>
               </rich:column>
               <rich:column styleClass="right-aligned-column">
                   <a4j:commandButton value="Удалить" action="#{bank.delete}"
                                      reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                                      styleClass="command-button" />
               </rich:column>


           </rich:dataTable>--%>



       </rich:tab>
    </rich:tabPanel>

    <h:panelGroup style="margin-top: 10px">
        <%--<a4j:commandButton value="Добавить банк" action="#{optionPage.addBank}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                           styleClass="command-button"/>--%>

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