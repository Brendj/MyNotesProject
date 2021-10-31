<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--@elvariable id="NSIOrgsRegistrySynchSettingsPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgsRegistrySynchSettingsPage"--%>
<h:panelGrid id="idNSIOrgsRegistrySynchSettingsPage" styleClass="borderless-grid" binding="#{NSIOrgsRegistrySynchSettingsPage.pageComponent}">

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Список округов" styleClass="output-text" />
        <h:inputText value="#{NSIOrgsRegistrySynchSettingsPage.regionsString}" size="150" styleClass="input-text" />

        <h:outputText escape="true" value="Учредитель ОО" styleClass="output-text" />
        <h:inputTextarea value="#{NSIOrgsRegistrySynchSettingsPage.founder}" cols="150" rows="10" styleClass="input-text" />

        <h:outputText escape="true" value="Отраслевое подчинение" styleClass="output-text" />
        <h:inputTextarea value="#{NSIOrgsRegistrySynchSettingsPage.industry}" cols="150" rows="10" styleClass="input-text" />

        <h:outputText escape="true" value="" styleClass="output-text" />
        <h:outputText escape="true" value="#{NSIOrgsRegistrySynchSettingsPage.who}" style="font-size: 10pt;" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />

    <a4j:status id="updateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <a4j:commandButton value="Сохранить" action="#{NSIOrgsRegistrySynchSettingsPage.saveSettings}" status="updateStatus"
                       reRender="idNSIOrgsRegistrySynchSettingsPage"/>

</h:panelGrid>