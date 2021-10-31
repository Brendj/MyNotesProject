<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: HBM
  Date: 28.04.2018
  Time: 16:55
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
    function onstartloading(){
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button").attr('disabled', '');
    }
</script>

<rich:modalPanel id="cardRegistrationConfirm" width="250" height="150" resizeable="false" moveable="false" binding="#{mainPage.cardRegistrationConfirm.pageComponent}">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Внимание"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <rich:componentControl for="groupCreatePanel" attachTo="hidelink" operation="hide" event="onclick"/>
        </h:panelGroup>
    </f:facet>

    <a4j:form>
        <a4j:region>
            <h:panelGrid>
                <h:panelGrid>
                    <h:outputText id="MessageOfCRC" styleClass="output-text" value="#{mainPage.cardRegistrationConfirm.message}"/>
                </h:panelGrid>
                <h:panelGrid columns="4">
                    <a4j:commandButton value="Заказать карту" action="#{mainPage.confirmReissueCard}" status="CardRegistrationConfirmStatus"
                                       onclick="if (#{facesContext.maximumSeverity == null}) #{rich:component('cardRegistrationConfirm')}.hide();"
                                       styleClass="command-button" style="width: 120px;" />
                    <a4j:commandButton value="Отменить" action="#{mainPage.cancelReissueCard}" status="CardRegistrationConfirmStatus"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('cardRegistrationConfirm')}.hide();"
                                       styleClass="command-button" style="width: 80px;"/>
                    <a4j:status id="CardRegistrationConfirmStatus" onstart="onstartloading()" onstop="onstoploading()">
                        <f:facet name="start">
                            <h:panelGrid columns="2">
                                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                            </h:panelGrid>
                        </f:facet>
                    </a4j:status>
                </h:panelGrid>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>
