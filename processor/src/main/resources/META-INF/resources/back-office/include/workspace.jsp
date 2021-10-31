<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Рабочая область --%>
<a4j:form id="workspaceForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
    <rich:panel id="workspaceTogglePanel" headerClass="workspace-panel-header">
        <f:facet name="header">
            <h:outputText escape="true" value="#{mainPage.currentWorkspacePage.pageTitle}" />
        </f:facet>
        <f:subview id="workspacePageSubView">
            <a4j:outputPanel>
                <c:import url="/back-office/include/${mainPage.currentWorkspacePage.pageFilename}.jsp" />
            </a4j:outputPanel>
        </f:subview>
    </rich:panel>
</a4j:form>