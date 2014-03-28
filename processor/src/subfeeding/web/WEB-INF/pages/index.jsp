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
    <jsp:include page="include/header.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/styles/index.css" />
    <script src="${pageContext.request.contextPath}/resources/scripts/tools.js"></script>
    <script>
        $(function () {
            $("#submit").button().css({
                'margin-top': '10px'
            });
            $('input:text, input:password').button().addClass('ui-textfield');
        });
    </script>
    <style type="text/css">
    .sf-error-text {
        color: red;
        margin-top: 10px;
    }
    </style>
</head>
<body>
<div id="loginDiv">
    <div id="header">Авторизация</div>
    <div>
        <form method="post" enctype="application/x-www-form-urlencoded"
              action="${pageContext.request.contextPath}/sub-feeding/login">
            <input type="text" name="contractId" size="25" maxlength="64" placeholder="Номер лицевого счета" />
            <input type="password" name="password" size="25" maxlength="64" placeholder="Пароль" />
            <input id="submit" type="submit" name="authorize" value="Войти" />
            <c:if test="${not empty requestScope.subFeedingError}">
                <div class="ui-state-error-text sf-error-text">${requestScope.subFeedingError}</div>
            </c:if>
        </form>
    </div>
</div>
</body>
</html>