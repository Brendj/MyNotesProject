<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditContragents()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%--@elvariable id="reconciliationPage" type="ru.axetta.ecafe.processor.web.ui.contragent.ReconciliationPage"--%>
<h:panelGrid id="reconcilePanel" binding="#{reconciliationPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Загрузка реестра для квитирования" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid id="reconcileGrid" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Агент" styleClass="output-text required-field" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{reconciliationPage.caAgentName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="1" target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>
            <h:outputText escape="true" value="Получатель" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{reconciliationPage.caReceiverName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                                   reRender="modalContragentSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
                </a4j:commandButton>
            </h:panelGroup>
            <h:outputText escape="true" value="Дата от" styleClass="output-text required-field" />
            <rich:calendar value="#{reconciliationPage.dtFrom}" datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Дата до (включительно)" styleClass="output-text required-field" />
            <rich:calendar value="#{reconciliationPage.dtTo}" datePattern="dd.MM.yyyy" converter="dateConverter"
                           inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Не включать даты платежа в сравнение" styleClass="output-text required-field" />
            <h:selectBooleanCheckbox value="#{reconciliationPage.dateDependent}" styleClass="output-text" />

            <h:outputText escape="true" value="Файл реестра" styleClass="output-text required-field" />
            <h:panelGrid columns="2">
                <rich:fileUpload id="paymentRegistryFileUploadElement" styleClass="upload"
                                 addButtonClass="upload-command-button"
                                 addButtonClassDisabled="upload-command-button-diasbled"
                                 cleanButtonClass="upload-command-button"
                                 cleanButtonClassDisabled="upload-command-button-diasbled"
                                 stopButtonClass="upload-command-button"
                                 stopButtonClassDisabled="upload-command-button-diasbled"
                                 uploadButtonClass="upload-command-button"
                                 uploadButtonClassDisabled="upload-command-button-diasbled" fileEntryClass="output-text"
                                 fileEntryClassDisabled="output-text" fileEntryControlClass="output-text"
                                 fileEntryControlClassDisabled="output-text" sizeErrorLabel="Недопустимый размер"
                                 stopControlLabel="Остановить" stopEntryControlLabel="Остановить"
                                 addControlLabel="Добавить файл" clearControlLabel="Очистить"
                                 clearAllControlLabel="Очистить все" doneLabel="Готово" cancelEntryControlLabel="Отменить"
                                 transferErrorLabel="Ошибка передачи" uploadControlLabel="Загрузка файлов"
                                 progressLabel="Загрузка" listHeight="150px" maxFilesQuantity="50"
                                 fileUploadListener="#{reconciliationPage.uploadFileListener}">
                    <f:facet name="label">
                        <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
                    </f:facet>
                    <%--<a4j:support event="onuploadcomplete" reRender="fileUploadMessages" />--%>
                    <a4j:support event="onclear" reRender="paymentRegistryFileUploadElement" />
                </rich:fileUpload>

                <h:panelGrid>
<%--                    Вставьте в файл заголовок формата, пример:<br/>
                    !separators=[|@]<br/>
                    !idOfContract=2<br/>
                    !idOfPayment=1<br/>
                    !sum=3<br/>
                    !date=0
                    <pre/>--%>
                    <h:inputTextarea rows="10" cols="30" value="#{reconciliationPage.settings}"
                                     styleClass="input-text" />
                </h:panelGrid>
            </h:panelGrid>
            <a4j:commandButton value="Обработать" action="#{reconciliationPage.processData}"
                               reRender="reconcilePanel" />

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:panel header="Результаты сверки" headerClass="filter-panel-header"
                rendered="#{not empty reconciliationPage.differencesInfo}">
        <h:outputText value="#{reconciliationPage.differencesInfo}" styleClass="output-text"/>
        <rich:spacer height="10" />
        <rich:dataTable id="differencesTable" value="#{reconciliationPage.differencesList}" var="item" rows="20"
                        columnClasses="left-aligned-column" footerClass="data-table-footer"
                        >
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Различия" />
                </f:facet>
                <h:outputText escape="true" value="#{item}" styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="differencesTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
        <h:panelGrid columns="2">
            <h:outputText value="Выгрузка в файл" styleClass="output-text" />
            <h:selectOneMenu value="#{reconciliationPage.exportType}" converter="javax.faces.Integer"
                             styleClass="output-text">
                <f:selectItem itemValue="0" itemLabel="экспорт различий" />
                <f:selectItem itemValue="1" itemLabel="экспорт отсутствующих записей реестра" />
                <f:selectItem itemValue="2" itemLabel="экспорт отсутствующих записей реестра в формате импорта" />
            </h:selectOneMenu>
            <h:commandButton action="#{reconciliationPage.exportToFile}" value="Выгрузить"
                             styleClass="command-button" />
        </h:panelGrid>
    </rich:panel>

</h:panelGrid>
<a4j:status id="reconcileStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
    </f:facet>
</a4j:status>

<h:panelGrid id="reconciliationMessages" styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>