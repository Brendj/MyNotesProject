<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="debugInfoPage" type="ru.axetta.ecafe.processor.web.ui.option.DebugInfoPage"--%>
<h:panelGrid id="debugInfoGrid" binding="#{debugInfoPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Отладочные задачи" switchType="client"
                            opened="true" headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Тест приема платежей РНИП. Путь к файлу на сервере: /home/jbosser/processor/Debugs/rnip.txt" styleClass="output-text" />
                <h:commandButton value="Запуск" action="#{debugInfoPage.runTestRNIP()}" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Test2" styleClass="output-text" />
            <h:commandButton value="Запуск" action="#{debugInfoPage.runTest2()}" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Тест обращения к АИС Реестр" styleClass="output-text" />
            <h:commandButton value="Запуск" action="#{debugInfoPage.runTestAISReestr()}" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
</h:panelGrid>


<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>