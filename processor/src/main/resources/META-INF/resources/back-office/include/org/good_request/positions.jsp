<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="goodRequestPositionListPage" type="ru.axetta.ecafe.processor.web.ui.org.goodRequest.goodRequestPosition.GoodRequestPositionListPage"--%>
<h:panelGrid id="goodRequestPositionListPage" binding="#{goodRequestPositionListPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:dataTable id="goodRequestPositionListTable" width="700" var="goodRequestPosition" value="#{goodRequestPositionListPage.goodRequestPositionList}" rendered="#{!goodRequestPositionListPage.emptyGoodRequestPositionList}"
                    rows="20" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row+1}" />
        </rich:column>
        <%--<rich:column  headerClass="column-header">--%>
            <%--<f:facet name="header">--%>
                <%--<h:outputText value="Продукт" styleClass="output-text" escape="true"/>--%>
            <%--</f:facet>--%>
            <%--<a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodRequestPosition.product.productName}" action="#{productViewPage.show}" styleClass="command-link">--%>
                <%--<f:setPropertyActionListener value="#{goodRequestPosition.product}" target="#{selectedProductGroupPage.currentProduct}" />--%>
            <%--</a4j:commandLink>--%>
        <%--</rich:column>--%>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Товар | Базовый продукт" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.currentElementValue}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Общее количество" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.totalCount/1000}" rendered="#{goodRequestPosition.floatScale}">
                <f:convertNumber pattern="#0"/>
            </h:outputText>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.totalCount/1000}" rendered="#{!goodRequestPosition.floatScale}">
                <f:convertNumber pattern="#0.000"/>
            </h:outputText>
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Суточная проба" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="-" rendered="#{goodRequestPosition.dailySampleCount==null}"/>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.dailySampleCount/1000}" rendered="#{goodRequestPosition.dailySampleCount!=null && goodRequestPosition.floatScale}">
                <f:convertNumber pattern="#0"/>
            </h:outputText>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.dailySampleCount/1000}" rendered="#{goodRequestPosition.dailySampleCount!=null && !goodRequestPosition.floatScale}">
                <f:convertNumber pattern="#0.000"/>
            </h:outputText>
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Единица измерения" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.unitsScale}" />
        </rich:column>
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Масса нетто (в граммах)" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodRequestPosition.netWeight}" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="goodRequestPositionListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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