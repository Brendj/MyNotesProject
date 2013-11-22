<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="pupilCatalogFindPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.PupilCatalogFindPage"--%>

<h:panelGrid id="pupilCatalogFindPage" styleClass="borderless-grid" binding="#{pupilCatalogFindPage.pageComponent}">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>


    <rich:simpleTogglePanel label="Введите параметры поиска" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{pupilCatalogFindPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

        </h:panelGrid>
        <h:panelGrid columns="6" styleClass="borderless-grid">
            <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
            <h:inputText value="#{pupilCatalogFindPage.familyName}" maxlength="30" size="20"
                         styleClass="input-text" />
            <h:outputText escape="true" value="Имя" styleClass="output-text" />
            <h:inputText value="#{pupilCatalogFindPage.firstName}" maxlength="30" size="20"
                         styleClass="input-text" />
            <h:outputText escape="true" value="Отчество" styleClass="output-text" />
            <h:inputText value="#{pupilCatalogFindPage.secondName}" maxlength="30" size="20"
                         styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Показывать дополнительные поля:" styleClass="output-text"/>
            <h:selectBooleanCheckbox value="#{pupilCatalogFindPage.showExtendedInfo}"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Найти в реестрах" action="#{pupilCatalogFindPage.updateList}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />

        </h:panelGrid>
    </rich:simpleTogglePanel>
    <h:panelGroup>
        <a4j:commandButton value="Исключить найденные по GUID" action="#{pupilCatalogFindPage.removeFoundClientsByGUID}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
        <a4j:commandButton value="Исключить найденные по Ф.И.О."
                           action="#{pupilCatalogFindPage.removeFoundClientsByFullName}" reRender="workspaceTogglePanel"
                           styleClass="command-button" />
    </h:panelGroup>
    <h:panelGroup style="text-align: right">
        <h:outputText value="Всего в списке: #{pupilCatalogFindPage.clientTotalCount}" styleClass="output-text" />
        <rich:spacer width="20px" />
        <a4j:commandLink value="Отметить все записи к регистрации"
                         action="#{pupilCatalogFindPage.markAllForRegistration}" reRender="workspaceTogglePanel"
                         styleClass="command-button" />
        <rich:spacer width="20px" />
        <a4j:commandLink value="Снять все записи c регистрации"
                         action="#{pupilCatalogFindPage.unmarkAllForRegistration}" reRender="workspaceTogglePanel"
                         styleClass="command-button" />
    </h:panelGroup>


    <rich:dataTable id="pupilCatalogFindTable" footerClass="data-table-footer"
                    value="#{pupilCatalogFindPage.pupilInfos}" var="item" rows="50"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Фамилия" />
            </f:facet>
            <h:outputText value="#{item.familyName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Имя" />
            </f:facet>
            <h:outputText value="#{item.firstName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Отчество" />
            </f:facet>
            <h:outputText value="#{item.secondName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="#" />
            </f:facet>
            <h:outputText value="#{item.fullNameOfClientForBind}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="К связи" />
            </f:facet>
            <h:selectBooleanCheckbox readonly="#{item.idOfClientForBind!=null}}" value="#{item.toBind}"
                                     styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата рождения" />
            </f:facet>
            <h:outputText value="#{item.birthDate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Класс" />
            </f:facet>
            <h:outputText value="#{item.group}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{pupilCatalogFindPage.showExtendedInfo}">
            <f:facet name="header">
                <h:outputText escape="true" value="Класс зачисления" />
            </f:facet>
            <h:outputText value="#{item.enterGroup}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{pupilCatalogFindPage.showExtendedInfo}">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата зачисления" />
            </f:facet>
            <h:outputText value="#{item.enterDate}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{pupilCatalogFindPage.showExtendedInfo}">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата отчисления" />
            </f:facet>
            <h:outputText value="#{item.leaveDate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID" />
            </f:facet>
            <h:outputText value="#{item.guid}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{pupilCatalogFindPage.showExtendedInfo}">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID организации" />
            </f:facet>
            <h:outputText value="#{item.guidOfOrg}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{pupilCatalogFindPage.showExtendedInfo}">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText value="#{item.recordState}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. клиента по GUID" />
            </f:facet>
            <h:outputText value="#{item.idOfClient}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="К регистрации" />
            </f:facet>
            <h:selectBooleanCheckbox readonly="#{item.canBeAdded}}" value="#{item.toAdd}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. клиента по Ф.И.О." />
            </f:facet>
            <h:outputText value="#{item.findByFIOResult}" />
        </rich:column>
        <f:facet name="footer">
            <h:panelGroup>
                <rich:datascroller for="pupilCatalogFindTable" renderIfSinglePage="false" maxPages="5"
                                   fastControls="hide" stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
                <a4j:commandButton value="Найти по Ф.И.О." action="#{pupilCatalogFindPage.checkFullNameDuplicates}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Найти по Ф.И.О. (с допуском)"
                                   action="#{pupilCatalogFindPage.checkFullNameDuplicatesFuzzy}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Связать" action="#{pupilCatalogFindPage.bindClients}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <rich:spacer width="20px" />
                <a4j:commandButton value="Зарегистрировать" action="#{pupilCatalogFindPage.registerClients}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <rich:spacer width="20px" />

                <h:commandLink value="Сравнение в файл CSV" style="color: #0000ff;"
                               action="#{pupilCatalogFindPage.buildComparisonCSVFile}" styleClass="command-button" />
            </h:panelGroup>
        </f:facet>

    </rich:dataTable>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
