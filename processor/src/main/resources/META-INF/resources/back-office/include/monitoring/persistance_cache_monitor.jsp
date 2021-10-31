<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://java.sun.com/jstl/fmt" %>

<%--@elvariable id="monitoringPersistanceCachePage" type="ru.axetta.ecafe.processor.web.ui.monitoring.MonitoringPersistanceCachePage"--%>
<h:panelGrid id="cachePersitentStat" binding="#{monitoringPersistanceCachePage.pageComponent}"
             width="100%" styleClass="cachePersitentStat borderless-grid">

    <h1>Кеш второго уровня</h1>

    <a4j:commandButton value="Обновить" action="#{monitoringPersistanceCachePage.update}" />


    <h:panelGrid columns="2" width="100%" columnClasses="left,right">
        <h:outputText></h:outputText>
        <h:panelGrid columns="2" columnClasses="left,right" width="100%">
            <h:outputText>processorPU</h:outputText>
            <h:outputText>reportPU</h:outputText>
        </h:panelGrid>

        <h:outputText>Количество объектов</h:outputText>
        <h:panelGrid columns="2" columnClasses="left,right"  width="100%">
            <h:outputText value="#{monitoringPersistanceCachePage.procesorStat.secondLevelCachePutCount}" />
            <h:outputText value="#{monitoringPersistanceCachePage.reportStat.secondLevelCachePutCount}" />
        </h:panelGrid>

        <h:outputText>Количество использования</h:outputText>
        <h:panelGrid columns="2" columnClasses="left,right"  width="100%">
            <h:outputText value="#{monitoringPersistanceCachePage.procesorStat.secondLevelCacheHitCount}" />
            <h:outputText value="#{monitoringPersistanceCachePage.reportStat.secondLevelCacheHitCount}" />
        </h:panelGrid>

        <h:outputText>Не найденных объектов</h:outputText>
        <h:panelGrid columns="2" columnClasses="left,right"  width="100%">
            <h:outputText value="#{monitoringPersistanceCachePage.procesorStat.secondLevelCacheMissCount}" />
            <h:outputText value="#{monitoringPersistanceCachePage.reportStat.secondLevelCacheMissCount}" />
        </h:panelGrid>
    </h:panelGrid>

    <br/>

    <rich:simpleTogglePanel switchType="client" opened="false" label="Processor PU полная статистика">
        <h:outputText value="#{monitoringPersistanceCachePage.procesorStat}"/>
    </rich:simpleTogglePanel>

    <rich:simpleTogglePanel switchType="client" opened="false" label="Report PU полная статистика">
        <h:outputText value="#{monitoringPersistanceCachePage.reportStat}"/>
    </rich:simpleTogglePanel>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>