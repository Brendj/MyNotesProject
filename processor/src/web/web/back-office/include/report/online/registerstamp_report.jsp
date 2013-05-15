<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 07.05.13
  Time: 14:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="registerStampPage" type="ru.axetta.ecafe.processor.web.ui.report.online.RegisterStampPage"--%>
<h:panelGrid id="registerStampReportPanelGrid" binding="#{registerStampPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{registerStampPage.org.shortName}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{registerStampPage.start}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />

        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{registerStampPage.end}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />

        <a4j:commandButton value="Применить" action="#{registerStampPage.reload}"
                           reRender="registerStampReportPanelGrid" />
        <a4j:commandButton value="Очистить" action="#{registerStampPage.clear}"
                           reRender="registerStampReportPanelGrid" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>


    <rich:dataTable value="#{registerStampPage.pageItems}" var="item">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="3">
                    <h:outputText value="Дата и номер талона"/>
                </rich:column>
                <rich:column colspan="#{registerStampPage.lastLvlElements}" rowspan="1">
                    <h:outputText value="Количество"/>
                </rich:column>

                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{registerStampPage.lvl1}" var="lvl1" colspan="#{lvl1.value.childCount}" rowspan="#{3-(lvl1.value.childCount>0?2:1)}">
                    <h:outputText value="#{lvl1.key}"/>
                </rich:columns>

                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{registerStampPage.lvl2}" var="lvl2" colspan="#{lvl2.value.childCount}">
                    <h:outputText value="#{lvl2.key}"/>
                </rich:columns>

            </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:outputText value="#{item.date}"/>
        </rich:column>
        <rich:columns value="#{registerStampPage.lvlBottom}" var="lvlBottom" colspan="#{lvlBottom.value.childCount}">
            <h:outputText value="#{item.getValue(lvlBottom.value.fullName)} " />
        </rich:columns>
    </rich:dataTable>

   <%-- <rich:dataTable value="#{registerStampPage.pageItems}" var="item">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="3">
                    <h:outputText value="Дата и номер талона"/>
                </rich:column>
                <rich:column colspan="#{registerStampPage.goodSetCount}" rowspan="1">
                    <h:outputText value="Количество"/>
                </rich:column>
                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{test.lvl1}" var="lvl1" colspan="#{lvl1.value.childCount}" rowspan="#{lvl1.value.childCount>0?1:2}">
                    <h:outputText value="#{lvl1.value.fullName} #{lvl1.key}"/>
                </rich:columns>
                <rich:column breakBefore="true" rendered="false">
                    <rich:spacer />
                </rich:column>
                <rich:columns value="#{test.lvl2}" var="lvl2" colspan="#{lvl2.value.childCount}">
                    <h:outputText value="#{lvl2.value.fullName} #{lvl2.key}"/>
                </rich:columns>
            </rich:columnGroup>
        </f:facet>
      &lt;%&ndash;  <rich:column>
            <h:outputText value="#{item.date}"/>
        </rich:column>
        <rich:columns value="#{test.lvl2}" var="lvl2" colspan="#{lvl2.value.childCount}">
            <h:outputText value="#{item.getValue(lvl2.value.fullName)} " />
        </rich:columns>&ndash;%&gt;
    </rich:dataTable>--%>

        <%--<rich:dataTable value="#{registerStampPage.pageItems}" var="item" rowKeyVar="row">--%>
            <%--<f:facet name="header">--%>
                <%--<rich:columnGroup>--%>
                    <%--<rich:column rowspan="3" colspan="1">--%>
                        <%--<h:outputText value="Дата и номер талона"/>--%>
                    <%--</rich:column>--%>
                    <%--<rich:column colspan="#{registerStampPage.goodSetCount}" rowspan="1">--%>
                        <%--<h:outputText value="Количество" />--%>
                    <%--</rich:column>--%>
                    <%--&lt;%&ndash;<rich:column breakBefore="true" rendered="false">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<rich:spacer />&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</rich:column>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<rich:columns value="#{registerStampPage.lvlSet2}" var="good" colspan="#{good.value}">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<h:outputText value="#{good.key}"/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</rich:columns>&ndash;%&gt;--%>
                    <%--<rich:column breakBefore="true" rendered="false">--%>
                        <%--<rich:spacer />--%>
                    <%--</rich:column>--%>
                    <%--<rich:columns value="#{registerStampPage.allGoods}" var="good">--%>
                        <%--<h:outputText value="#{good.pathPart3}" rendered="#{good.pathPart3!=''}"/>--%>
                        <%--<h:outputText value="#{good.fullName}" rendered="#{good.pathPart3==''}"/>--%>
                    <%--</rich:columns>--%>
                   <%--&lt;%&ndash; <rich:columns value="#{registerStampPage.lvlSet}" var="good" colspan="#{good.value}">--%>
                        <%--<h:outputText value="#{good.key}"/>--%>
                    <%--</rich:columns>&ndash;%&gt;--%>
                    <%--<rich:column breakBefore="true" rendered="false">--%>
                        <%--<rich:spacer />--%>
                    <%--</rich:column>--%>
                    <%--<rich:columns value="#{registerStampPage.allGoods}" var="good">--%>
                        <%--<h:outputText value="#{good.pathPart4}" rendered="#{good.pathPart4!=''}"/>--%>
                        <%--<h:outputText value="#{good.fullName}" rendered="#{good.pathPart4==''}"/>--%>
                    <%--</rich:columns>--%>
                <%--</rich:columnGroup>--%>
            <%--</f:facet>--%>
            <%--<rich:column>--%>
                <%--<h:outputText value="#{item.date}" />--%>
            <%--</rich:column>--%>
            <%--<rich:column rendered="false">--%>
                <%--<rich:spacer />--%>
            <%--</rich:column>--%>
            <%--<rich:columns value="#{registerStampPage.allGoods}" var="good">--%>
                <%--<h:outputText value="#{item.getValue(good.pathPart4)}" />--%>
            <%--</rich:columns>--%>
        <%--</rich:dataTable>--%>


    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>