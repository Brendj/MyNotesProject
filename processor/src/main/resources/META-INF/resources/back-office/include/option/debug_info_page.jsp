<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script>
    var socket = new WebSocket("ws://localhost:8001");
    function SaveFile(message)
    {
        var mes = message;

        socket.send(mes);
        return false;
    }
</script>

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
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Быстрые тесты" styleClass="output-text" />
            <h:commandButton value="Запуск" action="#{debugInfoPage.runQuickTest()}" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Ежедневные итоги для ЕМП" styleClass="output-text" />
            <a4j:commandButton value="Запуск" action="#{debugInfoPage.runEmpSummaryDay()}" reRender="debug_result"
                    status="debugTaskStatus" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Еженедельные итоги для ЕМП" styleClass="output-text" />
            <a4j:commandButton value="Запуск" action="#{debugInfoPage.runEmpSummaryWeek()}" reRender="debug_result"
                               status="debugTaskStatus" />
        </h:panelGrid>

        <a4j:status id="debugTaskStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>

        <h:panelGrid columns="4" styleClass="borderless-grid">
            <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
            <rich:calendar value="#{debugInfoPage.startDate}" datePattern="dd.MM.yyyy"
                       inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
            <rich:calendar value="#{debugInfoPage.endDate}" datePattern="dd.MM.yyyy"
                           inputClass="input-text" showWeeksBar="false" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Результат" styleClass="output-text" />
            <h:inputTextarea value="#{debugInfoPage.result}" cols="50" rows="20" id="debug_result" readonly="true" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Test VFS" styleClass="output-text" />
            <a4j:commandButton value="Запуск" action="#{debugInfoPage.runVFSCollapse()}" reRender="debug_result"
                               status="debugTaskStatus" />
        </h:panelGrid>
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <h:outputText escape="true" value="Socket send" styleClass="output-text" />
            <h:inputText value="#{debugInfoPage.messageSocket}"/>
            <a4j:commandButton value="Отправить в сокет" id="azxcvbnm"
                               oncomplete="SaveFile('#{debugInfoPage.messageSocket}');"
                               />
        </h:panelGrid>
    </rich:simpleTogglePanel>
</h:panelGrid>


<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>