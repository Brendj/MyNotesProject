<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="defaultWorkspacePage" type="ru.axetta.ecafe.processor.web.ui.DefaultWorkspacePage"--%>
<a4j:form>
    <a4j:region>
    <h:panelGrid columns="2">
        <h:outputText value="ОУ" styleClass="output-text" />
        <h:selectOneMenu id="org" value="#{defaultWorkspacePage.org}" style="width:150px;"  >
            <f:selectItems value="#{defaultWorkspacePage.orgs}"/>
        </h:selectOneMenu>
    </h:panelGrid>
    <a4j:commandButton value="Выбрать" action="#{defaultWorkspacePage.doApply}" />
    </a4j:region>
</a4j:form>