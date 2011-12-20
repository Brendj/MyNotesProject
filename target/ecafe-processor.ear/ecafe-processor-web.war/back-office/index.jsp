<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<html>
<head>
    <title>Новая школа: Бэк-офис</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
</head>

<body style="margin: 4px; padding: 0;">

<f:view>
    <rich:simpleTogglePanel rendered="false" />

    <rich:fileUpload rendered="false" />

    <rich:calendar rendered="false" />

    <f:subview id="orgSelectSubView">
        <c:import url="include/org/select.jsp" />
    </f:subview>

    <f:subview id="contragentSelectSubView">
        <c:import url="include/contragent/select.jsp" />
    </f:subview>

    <f:subview id="clientSelectSubView">
        <c:import url="include/client/select.jsp" />
    </f:subview>

    <f:subview id="ccAccountDeleteSubView">
        <c:import url="include/contragent/ccaccount/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="reportJobDeleteSubView">
        <c:import url="include/report/job/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="reportRuleDeleteSubView">
        <c:import url="include/report/rule/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="eventNotificationDeleteSubView">
        <c:import url="include/event_notification/confirm_delete.jsp" />
    </f:subview>

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td colspan="2">
                    <%-- Заголовок страницы --%>
                <a4j:form id="headerForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <rich:panel styleClass="header-panel" bodyClass="header-panel-body">
                        <h:panelGroup style="text-align: right; float: right;">
                            <h:outputText escape="true" value="ECafe Processor 0.2.2.4"
                                          styleClass="output-text" /><br />
                            <h:outputText escape="true" value="#{request.remoteUser} - " styleClass="output-text" />
                            <h:commandLink value="выход" action="#{mainPage.logout}" styleClass="command-link" />
                        </h:panelGroup>
                        <h:panelGroup style="text-align: left;">
                            <h:graphicImage value="/images/48x48/ecafe.png"
                                            style="border: 0; margin: 0 8px 0 0; vertical-align: middle; " />
                            <h:outputText escape="true" id="headerText" value="Новая школа: Бэк-офис"
                                          styleClass="page-header-text" />
                        </h:panelGroup>
                    </rich:panel>
                </a4j:form>
            </td>
        </tr>
            <%-- Центральная область --%>
        <tr>
            <td style="min-width: 210px; vertical-align: top;" width="215px">
                    <%-- Главное меню --%>
                <f:subview id="mainMenuSubView">
                    <c:import url="/back-office/include/main_menu.jsp" />
                </f:subview>
            </td>
            <td style="vertical-align: top;" width="*">
                    <%-- Рабочая область --%>
                <f:subview id="workspaceSubView">
                    <c:import url="/back-office/include/workspace.jsp" />
                </f:subview>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                    <%-- Нижний колонтитул --%>
                <h:panelGrid width="100%" cellspacing="4px" cellpadding="0" styleClass="borderless-grid"
                             columnClasses="right-aligned-column">
                    <h:outputLink value="http://deepwirelessconcepts.com" styleClass="command-link">
                        <h:outputText escape="true" value="©2009-2010 ООО \"Аксетта\"" styleClass="output-text" />
                    </h:outputLink>
                </h:panelGrid> <%-- Нижний колонтитул --%>
            </td>
        </tr>
    </table>

</f:view>
</body>

</html>