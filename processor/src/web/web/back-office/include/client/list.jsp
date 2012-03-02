<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript">
    function setCaretToEnd (e) {
        var control = $((e.target ? e.target : e.srcElement).id);
        if (control.createTextRange) {
            var range = control.createTextRange();
            range.collapse(false);
            range.select();
        }
        else if (control.setSelectionRange) {
            control.focus();
            var length = control.value.length;
            control.setSelectionRange(length, length);
        }
        control.selectionStart = control.selectionEnd = control.value.length;
    }

</script>

<%-- Панель просмотра списка клиентов --%>
<h:panelGrid id="clientListPanelGrid" binding="#{mainPage.clientListPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр (#{mainPage.clientListPage.clientFilter.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.clientListPage.clientFilter.org.shortName}" readonly="true"
                             styleClass="input-text" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
            <h:outputText escape="true" value="Договор" styleClass="output-text" />
            <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Номер договора" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.contractId}" maxlength="10"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.contractPerson.surname}" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Имя" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.contractPerson.firstName}" maxlength="64"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.contractPerson.secondName}" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.contractPerson.idDocument}" maxlength="128"
                             styleClass="input-text" />
            </h:panelGrid>

            <h:outputText escape="true" value="Обслуживается" styleClass="output-text" />
            <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.person.surname}" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Имя" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.person.firstName}" maxlength="64"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.person.secondName}" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
                <h:inputText value="#{mainPage.clientListPage.clientFilter.person.idDocument}" maxlength="128"
                             styleClass="input-text" />
            </h:panelGrid>

            <h:outputText escape="true" value="Наличие карт" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.clientListPage.clientFilter.clientCardOwnCondition}"
                             styleClass="input-text">
                <f:selectItems value="#{mainPage.clientListPage.clientFilter.clientCardOwnMenu.items}" />
            </h:selectOneMenu>
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{mainPage.updateClientListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{mainPage.clearClientListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="clientListTable" value="#{mainPage.clientListPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header" filterMethod="#{mainPage.clientListPage.filterIds}" >
            <f:facet name="header">
                <h:panelGrid columns="1">
                    <h:outputText escape="true" value="Идентификатор" />
                    <h:inputText value="#{mainPage.clientListPage.filterClientId}" id="input">
                        <a4j:support event="onkeyup" reRender="clientListTable"
                            ignoreDupResponses="true" requestDelay="700"
                            oncomplete="setCaretToEnd(event);"
                            />
                    </h:inputText>
                </h:panelGrid>
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfClient}"
                              styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер договора" />
            </f:facet>
            <h:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.contractId}" converter="contractIdConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Договор оформлен" />
            </f:facet>
            <h:outputText escape="true"
                          value="#{item.contractPerson.surname} #{item.contractPerson.firstName} #{item.contractPerson.secondName}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Обслуживается" />
            </f:facet>
            <h:outputText escape="true"
                          value="#{item.person.surname} #{item.person.firstName} #{item.person.secondName}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Текущий баланс" />
            </f:facet>
            <h:outputText escape="true" value="#{item.balance}" converter="copeckSumConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Лимит овердрафта" />
            </f:facet>
            <h:outputText escape="true" value="#{item.limit}" converter="copeckSumConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Лимит расходов" />
            </f:facet>
            <h:outputText escape="true" value="#{item.expenditureLimit}" converter="copeckSumConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <h:commandLink action="#{mainPage.showClientEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Исключить" />
            </f:facet>
            <h:commandLink action="#{mainPage.removeClientFromList}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </h:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="clientListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <c:if test="${!ru.axetta.ecafe.processor.web.ui.MainPage.sessionInstance.eligibleToEditClients}" >
        <rich:simpleTogglePanel switchType="client" label="Изменение лимита овердрафта" opened="false">
            <h:panelGrid columns="2">
                <h:outputText value="Лимит овердрафта" styleClass="output-text"/>
                <h:inputText value="#{mainPage.clientListPage.limit}" maxlength="20" converter="copeckSumConverter" styleClass="input-text" />
                <rich:spacer/>
                <a4j:commandButton value="Применить" action="#{mainPage.setLimit}"
                               reRender="workspaceTogglePanel" styleClass="command-button"/>
            </h:panelGrid>
        </rich:simpleTogglePanel>
        <rich:simpleTogglePanel switchType="client" label="Изменение организации" opened="false">
            <h:panelGrid styleClass="borderless-grid" columns="3">
                <h:outputText escape="true" value="Организация" styleClass="output-text" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                           styleClass="command-link" style="width: 25px;" />
                <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.clientListPage.clientFilter.org.shortName}}" />
                <rich:spacer/>
                <rich:spacer/>
                <a4j:commandButton value="Применить" action="#{mainPage.setOrg}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
        <rich:simpleTogglePanel switchType="client" label="Изменение лимита дневных трат" opened="false">
            <h:panelGrid columns="2">
                <h:outputText value="Лимит дневных трат" styleClass="output-text"/>
                <h:inputText value="#{mainPage.clientListPage.expenditureLimit}" maxlength="20" converter="copeckSumConverter" styleClass="input-text" />
                <rich:spacer/>
                <a4j:commandButton value="Применить" action="#{mainPage.setExpenditureLimit}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>

    </c:if>
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showClientCSVList}" styleClass="command-button" />
</h:panelGrid>