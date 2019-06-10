<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="webTechnologistCatalogCreatePage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogCreatePage"--%>
<%--@elvariable id="webTechnologistCatalogViewPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogViewPage"--%>
<h:panelGrid id="webTechnologistCatalogCreatePagePanelGrid" binding="#{webTechnologistCatalogCreatePage.pageComponent}"
             styleClass="borderless-grid">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{webTechnologistCatalogCreatePage.catalogName}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton reRender="mainMenu, workspaceForm" value="Создать" action="#{webTechnologistCatalogCreatePage.createCatalog}"
                               oncomplete="#{webTechnologistCatalogViewPage.show}"
                               styleClass="command-button" >
                <f:setPropertyActionListener value="#{webTechnologistCatalogCreatePage.idOfNewCatalog}"
                                             target="#{webTechnologistCatalogViewPage.targetIdOfCatalog}" />
            </a4j:commandButton>
        </h:panelGrid>
</h:panelGrid>