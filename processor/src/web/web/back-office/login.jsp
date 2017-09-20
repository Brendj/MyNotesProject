<%@ page language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.login.JBossLoginModule" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    if (StringUtils.isNotEmpty(request.getRemoteUser())) {
        String mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/index.faces");
        response.sendRedirect(mainPage);
        return;
    }
    String requestedResource = (String)request.getAttribute("javax.servlet.forward.request_uri");
    String userRoleParam = "";
    if ((requestedResource.endsWith("/admin/index.faces")) || (requestedResource.endsWith("/admin/j_security_check"))) userRoleParam = JBossLoginModule.ROLENAME_ADMIN;
    if ((requestedResource.endsWith("/director/index.faces")) || (requestedResource.endsWith("/director/j_security_check"))) userRoleParam = JBossLoginModule.ROLENAME_DIRECTOR;
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<html>
<head>
    <title>Новая школа: Авторизация</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
</head>
<body>
<f:view>
    <table style="width: 100%; height: 100%">
        <tr valign="middle">
            <td align="center">
                <h:panelGrid cellpadding="0" cellspacing="0">
                    <rich:panel header="Необходима авторизация" styleClass="login-panel"
                                headerClass="login-panel-header" bodyClass="login-panel-body">
                        <form id="loginForm" method="post" enctype="application/x-www-form-urlencoded"
                              action="j_security_check" class="borderless-form">
                            <input type="hidden" name="ecafeUserRole" value="<%= userRoleParam %>" />
                            <div align="center">
                                <%if (null != request.getParameter("error")) {%>
                                <h:outputText styleClass="error-output-text"
                                              value="#{not empty requestScope['errorMessage'] ? requestScope['errorMessage'] : 'Ошибка аутентификации'}" />
                                <%}%>
                                <h:panelGrid columns="2">
                                    <h:outputText value="Пользователь" styleClass="output-text" />
                                    <h:inputText id="j_username" size="16" maxlength="64" styleClass="input-text" />
                                    <h:outputText value="Пароль" styleClass="output-text" />
                                    <h:inputSecret id="j_password" size="16" maxlength="64" styleClass="input-text" />
                                </h:panelGrid>
                                <h:commandButton id="submitBtn" value="Войти" type="submit"
                                                 styleClass="command-button" />
                            </div>
                        </form>
                    </rich:panel>
                </h:panelGrid>
            </td>
        </tr>
    </table>
</f:view>
</body>
</html>