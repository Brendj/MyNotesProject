<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Панель просмотра списка клиентов --%>
<%--@elvariable id="clientMigrationPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientMigrationPage"--%>
<h:panelGrid id="clientMigrationListPanelGrid" binding="#{clientMigrationPage.pageComponent}" styleClass="borderless-grid">
    <rich:dataTable value="#{clientMigrationPage.clientMigrationItemInfoList}" var="clientMigration">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№"/>
            </f:facet>
            <h:outputText value="#{row+1}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Имя организации"/>
            </f:facet>
            <h:outputText value="#{clientMigration.shortName}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Дата регистрации"/>
            </f:facet>
            <h:outputText value="#{clientMigration.registrationDate}" converter="timeConverter"/>
        </rich:column>
    </rich:dataTable>
</h:panelGrid>