<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.output-text-mod {
font-family: Tahoma, Arial, Sans-Serif;
font-size: 10pt;
color: #000;
white-space: nowrap;
padding-right: 10px;
}

.thin-center-aligned-column {
    text-align: center;
    vertical-align: middle;
    width: 1%;
    height: 100px;
    line-height: 0.9;
}

.clientsPanel {
    width: 70%;
}

.groupsPanel {
    width: 30%;
}
</style>

<%--@elvariable id="feedPlanPage" type="ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage"--%>
<a4j:form id="setupFeedPlanForm">
    <h:panelGrid id="setupFeedPlanGrid" binding="#{feedPlanPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">
        <h:panelGrid columns="2">
            <h:outputText styleClass="output-text-mod" value="План питания:"/>
            <rich:calendar value="#{feedPlanPage.planDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false"
                           valueChangeListener="#{feedPlanPage.doChangePlanDate}">
                <a4j:support event="onchanged" reRender="planGrid" bypassUpdates="true" />
            </rich:calendar>
        </h:panelGrid>

        <h:panelGrid id="planGrid" columns="2" style="width: 100%" columnClasses="clientsPanel,groupsPanel">
            <%-- КЛИЕНТЫ --%>
            <rich:panel id="claimsCalendar" style="height: 450px; width: 100%; overflow: auto;">
                <rich:dataTable id="planTable" value="#{feedPlanPage.clients}" var="client" style="width: 100%">
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оплата"/>
                        </f:facet>
                        <a4j:commandButton image="/images/icon/stop.png" styleClass="command-button" />
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Вход"/>
                        </f:facet>
                        <a4j:commandButton image="/images/icon/exit_off.png" styleClass="command-button" />
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Карта"/>
                        </f:facet>
                        <a4j:commandButton image="/images/icon/id_card_off.png" styleClass="command-button" />
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Оператор"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="..."/>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Класс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{feedPlanPage.getGroupName(client.idofclientgroup)}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="ФИО"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" escape="false" value="#{client.fullName}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Замена"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" escape="false" value="-"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Правило"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="#{client.ruleDescription}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Комплекс"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="Комплекс №#{client.complex}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Цена"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="0 р."/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" value="Заказ"/>
                        </f:facet>
                        <h:outputText styleClass="output-text" value="нет"/>
                    </rich:column>
                </rich:dataTable>
            </rich:panel>

            <%-- КЛАССЫ --%>
            <rich:panel id="groupsGrid" style="height: 450px; width: 100%; overflow: auto;">
                <a4j:region>
                <rich:dataTable id="groupsTable" value="#{feedPlanPage.groups}" var="idoclientgroup" style="width: 100%">
                    <rich:column style="text-align: center; line-height: 0.9;">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="К<br/>л<br/>а<br/>с<br/>с"/>
                        </f:facet>
                        <a4j:commandLink styleClass="output-text-mod" value="#{feedPlanPage.getGroupName(idoclientgroup)}" >
                            <a4j:support reRender="planGrid" event="onclick" action="#{feedPlanPage.doChangeGroup(idoclientgroup)}" />
                        </a4j:commandLink>
                    </rich:column>

                    <rich:columns value="#{feedPlanPage.complexes}" var="complex" styleClass="left-aligned-column"
                                  index="ind" headerClass="thin-center-aligned-column"  width="1%" style="width: 1%;">
                        <f:facet name="header">
                            <h:outputText styleClass="output-text-mod" escape="false" value="К<br/>о<br/>м<br/>п<br/>л<br/>е<br/>к<br/>с<br/><br/>№#{complex}" />
                        </f:facet>
                        <h:outputText styleClass="output-text-mod" value="#{feedPlanPage.getComplexCount(idoclientgroup, complex)}" />
                    </rich:columns>
                </rich:dataTable>
                </a4j:region>
            </rich:panel>
        </h:panelGrid>

        <h:panelGrid columns="4">
            <a4j:commandButton value="Оплатить" style="height: 30px; width: 150px;">
                <a4j:support event="onclick" action="#{feedPlanPage.doApply}" />
            </a4j:commandButton>
            <a4j:commandButton value="Очистить план" style="height: 30px; width: 150px;">
                <a4j:support event="onclick" action="#{feedPlanPage.doApply}" />
            </a4j:commandButton>
            <a4j:commandButton value="Автозамена" style="height: 30px; width: 150px;">
                <a4j:support event="onclick" action="#{feedPlanPage.doApply}" />
            </a4j:commandButton>
            <a4j:commandButton value="Исключить комплекс" style="height: 30px; width: 150px;">
                <a4j:support event="onclick" action="#{feedPlanPage.doApply}" />
            </a4j:commandButton>
        </h:panelGrid>
    </h:panelGrid>
</a4j:form>