<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardGroupPage"--%>
<%--@elvariable id="visitorDogmCardListPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardListPage"--%>
<%--@elvariable id="visitorDogmCardViewPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardViewPage"--%>
<%--@elvariable id="visitorDogmCardEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardEditPage"--%>
<h:panelGrid id="visitorDogmListGrid" binding="#{visitorDogmCardListPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable id="visitorDogmCardTable" value="#{visitorDogmCardListPage.cards}" var="card" rows="15"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{visitorDogmCardViewPage.show}" styleClass="command-link">
                <h:outputText escape="true" value="#{card.cardNo}" converter="cardNoConverter" styleClass="output-text" />
                <f:setPropertyActionListener value="#{card}" target="#{visitorDogmCardGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Владелец" />
            </f:facet>
            <h:outputText escape="true" value="#{card.visitorItem.fullName}" styleClass="output-text" rendered="#{card.visitorItem!=null}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{card.createDate}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{visitorDogmCardEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{card}" target="#{visitorDogmCardGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="visitorDogmCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>