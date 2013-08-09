<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalGroupCreatorPanel" autosized="true" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText escape="true" value="Название группы" />
    </f:facet>
    <a4j:form id="modalGroupCreatorForm"
              styleClass="borderless-form" eventsQueue="modalGroupCreatorFormEventsQueue">
        test
    </a4j:form>
</rich:modalPanel>
