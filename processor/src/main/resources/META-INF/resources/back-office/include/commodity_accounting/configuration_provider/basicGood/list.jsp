<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="basicGoodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodListPage"--%>
<%--@elvariable id="basicGoodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodEditPage"--%>
<h:panelGrid id="basicGoodListPage" binding="#{basicGoodListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="basicGoodListPageFilter" styleClass="borderless-grid" columns="1">
        <rich:simpleTogglePanel label="Фильтр (#{basicGoodListPage.filter.status})" switchType="client"
                                opened="false" headerClass="filter-panel-header">

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Наименование продукта" styleClass="output-text" />
                <h:inputText value="#{basicGoodListPage.filter.nameOfGood}" styleClass="input-text" />
                <%--<h:outputText escape="true" value="Масса нетто (грамм)" styleClass="output-text" />
                <h:inputText value="#{basicGoodListPage.filter.netWeight}" styleClass="input-text" />--%>
                <h:outputText escape="true" value="Единица измерения" styleClass="output-text"/>
                <h:selectOneListbox id="unitsScaleFilterValue" value="#{basicGoodListPage.filter.unitsScale}">
                    <f:selectItems value="#{basicGoodListPage.filter.unitsScaleSelectItemList}"/>
                    <f:converter converterId="unitScaleConverter" />
                    <a4j:support event="valueChange" reRender="unitsScaleFilterValue" />
                </h:selectOneListbox>
                <h:outputText styleClass="output-text" escape="true" value="Начальная дата создания" />
                <rich:calendar value="#{basicGoodListPage.filter.createdDateBegin}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText styleClass="output-text" escape="true" value="Конечная дата создания" />
                <rich:calendar value="#{basicGoodListPage.filter.createdDateEnd}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{basicGoodListPage.reload}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{basicGoodListPage.resetFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>


    <a4j:status id="basicGoodListPageStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="basicGoodListTable" var="good" value="#{basicGoodListPage.itemList}"
                    rows="10" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row + 1}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наименование" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.nameOfGood}" escape="true"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Единица измерения" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.unitsScale}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Масса нетто" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.netWeight}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="GUID" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.guid}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата создания" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.createdDate}" converter="timeConverter"/>
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата последнего изменения" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.lastUpdate}" converter="timeConverter"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" action="#{basicGoodEditPage.show}" styleClass="command-link"
                             reRender="mainMenu, workspaceTogglePanel">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{good}" target="#{basicGoodEditPage.selectedEntityGroupPage.currentEntityItem}" />
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header" width="50px">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" styleClass="output-text"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             reRender="uvDeleteConfirmPanel"
                             action="#{uvDeletePage.show}"
                             oncomplete="#{rich:component('uvDeleteConfirmPanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{good}" target="#{uvDeletePage.currentEntityItem}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="basicGoodListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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