<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<f:view>

<html>
    <head>
        <title><h:outputText value="Новая школа#{runtimeContext.instanceNameDecorated}"/></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Language" content="ru">
        <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
        <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
        <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
    </head>
    <body>
    <f:subview id="orgListSelectSubView">
        <c:import url="select_orgs.jsp" />
    </f:subview>
    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td style="min-width: 210px; vertical-align: top;" width="215px">
                    <%-- Главное меню --%>
                <f:subview id="directorMenuSubView">
                    <c:import url="/back-office/director/director_main_menu.jsp" />
                </f:subview>
            </td>
            <td style="vertical-align: top;" width="*">
                    <%-- Рабочая область --%>
                <f:subview id="workspaceSubView">
                    <c:import url="/back-office/director/workspace.jsp" />
                </f:subview>
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