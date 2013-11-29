<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>
<!DOCTYPE html>
<html>
<head>
    <title>Абонементное питание</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/subfeeding/styles.css"/>
</head>
<body>
<form method="post" enctype="application/x-www-form-urlencoded" action="${pageContext.request.contextPath}/sub-feeding/login"
      class="borderless-form-login">
    <table id="login-form">
        <tr class="login-form-input-tr">
            <td align="center">
                <div align="center">
                    <table>
                        <tr>
                            <td>
                                <div class="output-text">Номер лицевого счета</div>
                            </td>
                            <td>
                                <input type="text" name="contractId" size="16" maxlength="64" class="input-text" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="output-text">Пароль</div>
                            </td>
                            <td>
                                <input type="password" name="password" size="16" maxlength="64" class="input-text" />
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
        <tr class="login-form-button-tr">
            <td align="center">
                <input type="submit" name="authorize" value="Войти" class="command-button" />
            </td>
        </tr>
<%
    if (request.getAttribute("subFeedingError") != null) {
%>
        <tr valign="middle">
            <td align="center">
                <div class="output-text" style="color: red;"><%=request.getAttribute("subFeedingError")%></div>
            </td>
        </tr>
<%
    }
%>
    </table>
</form>
</body>
</html>