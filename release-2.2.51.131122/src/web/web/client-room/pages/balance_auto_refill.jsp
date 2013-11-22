<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    final String COMMAND_PARAM = "command";
    final String SHOW_COMMAND = "show";
    final String CREATE_COMMAND = "create";
    final String EDIT_COMMAND = "edit";
    final String DEACTIVATE_COMMAND = "deactivate";
    String command = request.getParameter(COMMAND_PARAM);
    command = StringUtils.isEmpty(command) ? "show" : command;
    if (StringUtils.equals(command, SHOW_COMMAND)) {%>
<jsp:include page="balance-auto-refill/show.jsp" />
<%} else if (StringUtils.equals(command, CREATE_COMMAND)) {%>
<jsp:include page="balance-auto-refill/create.jsp" />
<%} else if (StringUtils.equals(command, EDIT_COMMAND)) {%>
<jsp:include page="balance-auto-refill/edit.jsp" />
<%} else if (StringUtils.equals(command, DEACTIVATE_COMMAND)) {%>
<jsp:include page="balance-auto-refill/deactivate.jsp" />
<%}%>