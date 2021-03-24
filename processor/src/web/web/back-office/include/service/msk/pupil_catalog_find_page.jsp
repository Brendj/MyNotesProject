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
            <h:outputText escape="true" value="Mesh GUID" styleClass="output-text" />
                <h:inputText value="#{pupilCatalogFindPage.meshId}"  maxlength="40" size="35"
                             styleClass="input-text" />
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
            <a4j:commandButton value="Найти в реестрах" action="#{pupilCatalogFindPage.updateList}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <h:panelGroup style="text-align: right">
        <h:outputText value="Всего в списке: #{pupilCatalogFindPage.clientTotalCount}" styleClass="output-text" />
        <rich:spacer width="20px" />
    </h:panelGroup>

    <rich:dataTable id="pupilCatalogFindTable" footerClass="data-table-footer"
                    value="#{pupilCatalogFindPage.pupilInfos}" var="responsePersons" rows="50"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column,
                    center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Фамилия" />
            </f:facet>
            <h:outputText value="#{responsePersons.lastname}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Имя" />
            </f:facet>
            <h:outputText value="#{responsePersons.firstname}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Отчество" />
            </f:facet>
            <h:outputText value="#{responsePersons.patronymic}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата рождения" />
            </f:facet>
            <h:outputText value="#{responsePersons.birthdate}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Группа" />
            </f:facet>
            <h:outputText value="#{responsePersons.className}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата окончания обучения" />
            </f:facet>
            <h:outputText value="#{responsePersons.training_end_at}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID" />
            </f:facet>
            <h:outputText value="#{responsePersons.guidNsi}" />
        </rich:column>
        <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Mesh GUID" />
        </f:facet>
        <h:outputText value="#{responsePersons.personId}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="id OO" />
            </f:facet>
            <h:outputText value="#{responsePersons.ooId}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="id ИС ПП" />
            </f:facet>
            <h:outputText value="#{responsePersons.idIsPp}" />
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
            </h:panelGroup>
        </f:facet>
    </rich:dataTable>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
