<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://java.sun.com/jstl/fmt" %>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="synchErrorsPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.SynchErrorsPage"--%>
<h:panelGrid id="synchErrorsPageGrid" binding="#{synchErrorsPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid columns="2">
        <a4j:commandButton value="Обновить" action="#{synchErrorsPage.update}"
                           reRender="synchErrorsPageGrid"/>
        <a4j:commandButton value="Очистить" action="#{synchErrorsPage.doClear}"
                           reRender="synchErrorsPageGrid"/>
    </h:panelGrid>

    <h:panelGrid columns="2" rendered="#{synchErrorsPage.count > 0}">
        <rich:dataTable id="synchErrorsPageTable" value="#{synchErrorsPage.items}" var="item"
                        footerClass="data-table-footer" rows="20" columnClasses="center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата синхронизации" />
                </f:facet>
                <h:outputText escape="true" value="#{item.date}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Сообщение" />
                </f:facet>
                <h:outputText escape="false" value="#{item.message}" styleClass="output-text"/>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="synchErrorsPageTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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

    <h:outputText styleClass="output-text" value="Для данной организации ошибок не найдено" rendered="#{synchErrorsPage.count < 1}"/>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>