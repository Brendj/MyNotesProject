<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (request.getHeader("referer")!=null) {
        response.sendRedirect(request.getHeader("referer"));
    }
%>