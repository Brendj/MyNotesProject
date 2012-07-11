<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapListPage" type="ru.axetta.ecafe.processor.web.ui.option.technologicalMap.TechnologicalMapListPage"--%>
<%--@elvariable id="technologicalMapEditPage" type="ru.axetta.ecafe.processor.web.ui.option.technologicalMap.TechnologicalMapEditPage"--%>
<h:panelGrid id="technologicalMapListPage" binding="#{technologicalMapListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:column>
        <fieldset>
            <legend><h:outputText value="Фильтры" styleClass="output-text" escape="true"/></legend>
            <h:panelGrid columns="2">
                <h:outputText value="Конфигурации провайдра" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectCurrentConfigurationProvider" value="#{technologicalMapListPage.currentIdOfConfigurationProvider}" styleClass="input-text long-field" >
                    <a4j:support event="onchange" action="#{technologicalMapListPage.onChange}" reRender="technologicalMapListTable"/>
                    <f:selectItem itemLabel="Выберите провайдера" itemValue="-1"/>
                    <f:selectItems value="#{technologicalMapListPage.configurationProviderMenu.items}" />
                    <f:selectItem itemLabel="Выберать без учета провайдера" itemValue="-2"/>
                </h:selectOneMenu>
                <h:outputText value="Группа продукта" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectCurrentProductGroup" value="#{technologicalMapListPage.currentIdOftechnologicalMapGroup}" styleClass="input-text long-field">
                    <a4j:support event="onchange" action="#{technologicalMapListPage.onChange}" reRender="technologicalMapListTable"/>
                    <f:selectItem itemLabel="Выберите провайдера" itemValue="-1"/>
                    <f:selectItems value="#{technologicalMapListPage.technologicalMapGroupMenu.items}" />
                    <f:selectItem itemLabel="Выберать без учета провайдера" itemValue="-2"/>
                </h:selectOneMenu>
                <h:outputText value="Удаленные элементы" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectDeletedStatus" value="#{technologicalMapListPage.deletedStatusSelected}" styleClass="input-text long-field">
                    <a4j:support event="onchange" action="#{technologicalMapListPage.onChange}" reRender="technologicalMapListTable"/>
                    <f:selectItem itemLabel="Показать" itemValue="true"/>
                    <f:selectItem itemLabel="Скрыть" itemValue="false"/>
                </h:selectOneMenu>
            </h:panelGrid>
        </fieldset>
    </h:column>

    <rich:dataTable id="technologicalMapListTable" width="700" var="technologicalMap" value="#{technologicalMapListPage.technologicalMapList}"
                    rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row+1}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наименование технологическая карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{technologicalMap.nameOfTechnologicalMap}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Номер технологической карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{technologicalMap.numberOfTechnologicalMap}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Статус технологической карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{technologicalMap.deletedState}" readonly="true" disabled="true"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" escape="true"/>
            </f:facet>
            <h:commandLink action="#{technologicalMapEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{technologicalMap}" target="#{technologicalMapEditPage.currTechnologicalMap}" />
            </h:commandLink>
        </rich:column>
        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedTechnologicalMapItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{technologicalMap}" target="#{technologicalMapEditPage.currTechnologicalMap}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="technologicalMapListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
