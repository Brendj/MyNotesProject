<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="repositoryReportsRenamePage" type="ru.axetta.ecafe.processor.web.ui.report.repository.RepositoryReportsRenamePage"--%>
<h:panelGrid id="reportRepListPanelGrid" binding="#{repositoryReportsRenamePage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:panelGrid columns="1">
            <h:outputText escape="true" value="Предыдущее название" styleClass="output-text" />
            <h:selectOneListbox value="#{repositoryReportsRenamePage.currentRuleName}" style="width:400px; height: 260px" >
                <f:selectItems value="#{repositoryReportsRenamePage.ruleNameItems}"/>
            </h:selectOneListbox>
        </h:panelGrid>
        <h:panelGrid columns="1">
            <h:outputText escape="true" value="Новое название" styleClass="output-text" />
            <h:selectOneListbox value="#{repositoryReportsRenamePage.newRuleName}" style="width:400px; height: 200px" >
                <f:selectItems value="#{repositoryReportsRenamePage.allowedRuleNameItems}"/>
            </h:selectOneListbox>
            <rich:spacer height="20px"/>
            <h:outputText escape="true" value="(произвольное название)" styleClass="output-text" />
            <h:inputText value="#{repositoryReportsRenamePage.customNewRuleName}" style="width:400px;"/>
        </h:panelGrid>
    </h:panelGrid>
    <%----%>
    <h:panelGrid columns="3" styleClass="borderless-grid">
        <a4j:commandButton value="Применить" action="#{repositoryReportsRenamePage.doApply}"
                           reRender="workspaceTogglePanel" styleClass="command-button" status="repositoryStatus" />
        <a4j:commandButton value="Очистить" action="#{repositoryReportsRenamePage.doReset}"
                           reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        <a4j:status id="repositoryStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>