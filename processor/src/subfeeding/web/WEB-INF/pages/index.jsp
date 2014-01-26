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
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/WebContent/css/flick/jquery-ui-1.10.3.custom.min.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/WebContent/css/index.css" />
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-1.10.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/WebContent/js/tools.js"></script>
    <script>
        $(function () {
            $("#submit").button().css({
                'margin-top': '10px'
            });
            $('input:text, input:password').button().css({
                'font': 'inherit',
                'color': 'inherit',
                'text-align': 'left',
                'outline': 'none',
                'cursor': 'text'
            });
        });
    </script>
</head>
<body>
<div id="loginDiv">
    <div id="header">Авторизация</div>
    <div>
        <form method="post" enctype="application/x-www-form-urlencoded"
              action="${pageContext.request.contextPath}/office/login">
            <input type="text" name="contractId" size="25" maxlength="64" placeholder="Номер лицевого счета" />
            <input type="password" name="password" size="25" maxlength="64" placeholder="Пароль" />
            <input id="submit" type="submit" name="authorize" value="Войти" />
            <%if (request.getAttribute("subFeedingError") != null) {%>
            <div class="ui-state-error-text" style="color: red; margin-top: 10px;"><%=request.getAttribute("subFeedingError")%>
            </div>
            <%}%>
        </form>
    </div>
</div>
</body>
</html>