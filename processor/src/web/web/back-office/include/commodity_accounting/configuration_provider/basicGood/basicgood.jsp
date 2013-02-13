<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" language="JavaScript">
    var previousComponentId;
    function RememberComponentId(id)
    {
        previousComponentId = id;
    }
    function ChangeDisplay(id)
    {
        if (document.getElementById(id).style.display == "block")
        {
            document.getElementById(id).style.display = "none";
        }
        else
        {
            if (previousComponentId) {
                document.getElementById(previousComponentId).style.display = "none";
            }
            previousComponentId = id;
            document.getElementById(id).style.display = "block";
        }
    }
</script>
<%--@elvariable id="basicGoodListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basicGood.BasicGoodListPage"--%>

<h:panelGrid id="basicGoodListPage" binding="#{basicGoodListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <rich:dataTable id="basicGoodListTable" width="700" var="good" value="#{basicGoodListPage.basicGoodList}" rendered="#{!basicGoodListPage.basicGoodListEmpty}"
                    rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row + 1}" />
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
            <h:outputText styleClass="output-text" value="#{good.createdDateFormatted}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Дата последнего изменения" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.lastUpdateFormatted}" />
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
            <h:outputText styleClass="output-text" value="#{good.unitsScaleString}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Масса нетто" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{good.netWeight}" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="basicGoodListPage" styleClass="command-link"
                             onclick="javascript:RememberComponentId('#{basicGoodListPage.cid(basicGoodListPage.editGoodDiv)}')">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{good}" target="#{basicGoodListPage.editBasicGood}" />
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header" width="50px">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" styleClass="output-text"/>
            </f:facet>

            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedBasicGoodItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener target="#{basicGoodListPage.idOfBasicGood}" value="#{good.idOfBasicGood}" />
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

    <a4j:status id="sReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <h:panelGrid rendered="#{basicGoodListPage.editBasicGoodNotNull}" columns="1">
        <a4j:commandLink value="Редактирование базового товара"
                         onclick="javascript:ChangeDisplay('#{basicGoodListPage.cid(basicGoodListPage.editGoodDiv)}')"
                         styleClass="command-link"/>
        <h:panelGroup layout="block" binding="#{basicGoodListPage.editGoodDiv}" id="editBasicGoodGrid"
                      style="display: block; border-style: solid; border-width: 1px; border-color: gray;">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
                <h:inputText value="#{basicGoodListPage.editBasicGood.nameOfGood}" maxlength="128"
                             styleClass="input-text long-field" />

                <h:outputText escape="true" value="Единица измерения" styleClass="output-text required-field" />
                <h:selectOneListbox value="#{basicGoodListPage.editBasicGood.unitsScale}"
                                    size="#{basicGoodListPage.unitsScaleSelectItemListSize}">
                    <f:selectItems value="#{basicGoodListPage.unitsScaleSelectItemList}"/>
                </h:selectOneListbox>

                <h:outputText escape="true" value="Масса нетто (грамм)" styleClass="output-text required-field" />
                <h:inputText value="#{basicGoodListPage.editBasicGood.netWeight}" maxlength="128" styleClass="input-text" />
            </h:panelGrid>
            <h:panelGrid styleClass="borderless-grid">
                <a4j:commandButton value="Сохранить изменнеия" action="#{basicGoodListPage.onEdit}"
                                   reRender="basicGoodListPage" styleClass="command-button" />
            </h:panelGrid>
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid>
        <a4j:commandLink value="Добавление базового товара"
                         onclick="javascript:ChangeDisplay('#{basicGoodListPage.cid(basicGoodListPage.createGoodDiv)}')"
                         styleClass="command-link"/>
        <h:panelGroup layout="block" binding="#{basicGoodListPage.createGoodDiv}" id="createBasicGoodGrid"
                      style="display: none; border-style: solid; border-width: 1px; border-color: gray;">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
                <h:inputText value="#{basicGoodListPage.newBasicGood.nameOfGood}" maxlength="128" styleClass="input-text long-field" />

                <h:outputText escape="true" value="Единица измерения" styleClass="output-text required-field" />
                <h:selectOneListbox value="#{basicGoodListPage.newBasicGood.unitsScale}"
                                    size="#{basicGoodListPage.unitsScaleSelectItemListSize}">
                    <f:selectItems value="#{basicGoodListPage.unitsScaleSelectItemList}"/>
                </h:selectOneListbox>

                <h:outputText escape="true" value="Масса нетто (грамм)" styleClass="output-text required-field" />
                <h:inputText value="#{basicGoodListPage.newBasicGood.netWeight}" maxlength="128" styleClass="input-text" />
            </h:panelGrid>
            <h:panelGrid styleClass="borderless-grid">
                <a4j:commandButton value="Создать базовый товар" action="#{basicGoodListPage.onCreate}"
                                   reRender="basicGoodListPage" styleClass="command-button" />
            </h:panelGrid>
        </h:panelGroup>
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
