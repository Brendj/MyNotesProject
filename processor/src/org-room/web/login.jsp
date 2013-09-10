<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Новая школа: Авторизация</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/icon/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/icon/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/styles.css"/>" type="text/css">
</head>
<%--@elvariable id="loginBean" type="ru.axetta.ecafe.processor.web.ui.auth.LoginBean"--%>
<body>
<f:view>
    <table style="width: 100%; height: 100%">
        <tr valign="middle">
            <td align="center">
                <h:panelGrid cellpadding="0" cellspacing="0">
                    <rich:panel header="Необходима авторизация" styleClass="login-panel"
                                headerClass="login-panel-header" bodyClass="login-panel-body">
                        <form method="post">
                            <h:outputText escape="true" value="#{loginBean.errorMessage}" rendered="#{not empty loginBean.errorMessage}" styleClass="error-messages" />
                            <h:panelGrid columns="2">
                                <h:outputText value="Пользователь" styleClass="output-text" />
                                <input type="text" name="username" size="16" maxlength="64" class="input-text">
                                <h:outputText value="Пароль" styleClass="output-text" />
                                <input type="password" name="password" size="16" maxlength="64" class="input-text">
                                <input type="hidden" name="doLogin" value="1">
                            </h:panelGrid>
                            <input type="submit" value="Войти" class="command-button">
                        </form>
                    </rich:panel>
                </h:panelGrid>
            </td>
        </tr>
    </table>
</f:view>
</body>
</html>