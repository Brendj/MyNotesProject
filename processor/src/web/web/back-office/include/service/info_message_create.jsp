<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="infoMessagePageCreateControls" binding="#{mainPage.infoMessageCreatePage.pageComponent}" >
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Тип сообщения" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:selectOneMenu id="regionsList" value="#{mainPage.infoMessageCreatePage.type}" style="width:250px;" >
                <f:selectItems value="#{mainPage.infoMessageCreatePage.types}"/>
            </h:selectOneMenu>
        </h:panelGroup>

        <h:outputText escape="true" value="Заголовок сообщения" styleClass="output-text" />
        <h:inputText value="#{mainPage.infoMessageCreatePage.header}" style="width:350px;" styleClass="input-text" />

        <h:outputText escape="true" value="Текст сообщения" styleClass="output-text" />
        <h:inputTextarea value="#{mainPage.infoMessageCreatePage.content}" style="width:350px;height:100px;" styleClass="input-text" />

        <h:outputText escape="true" value="Список организаций рассылки" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.infoMessageCreatePage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.infoMessageCreatePage.filter}}"/>
        </h:panelGroup>

        <a4j:status id="updateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />

        <a4j:commandButton value="Сохранить" action="#{mainPage.infoMessageCreatePage.doSave}" status="updateStatus"
                       onclick="this.disabled = true;" style="width: 180px;" reRender="infoMessagePageCreateControls" />
    </h:panelGrid>
</h:panelGrid>
