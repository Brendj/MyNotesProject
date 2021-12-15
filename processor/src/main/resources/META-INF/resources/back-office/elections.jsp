<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<f:view>

<html>
<head>
    <title><h:outputText value="Выборы 2016"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
</head>

<body style="margin: 4px; padding: 0;">

    <rich:simpleTogglePanel rendered="false" />

    <rich:fileUpload rendered="false" />

    <rich:calendar rendered="false" />

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td colspan="2">

            </td>
        </tr>
            <%-- Центральная область --%>
        <tr>
            <td style="min-width: 20px; vertical-align: top;" width="20px">
            </td>
            <td style="vertical-align: top;" width="*">
                    <%-- Рабочая область --%>
                    <a4j:form id="workspaceForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                        <h:outputText styleClass="output-text-big" escape="true" value="Мониторинг проходов" />
                        <c:import url="/back-office/include/report/online/enter_events_report.jsp" />
                    </a4j:form>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                    <%-- Нижний колонтитул --%>
                <h:panelGrid width="100%" cellspacing="4px" cellpadding="0" styleClass="borderless-grid"
                             columnClasses="right-aligned-column">
                </h:panelGrid> <%-- Нижний колонтитул --%>
            </td>
        </tr>
    </table>

</body>

</html>

</f:view>
