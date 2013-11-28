<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%    
    final String STAGE_PARAM = "stage";
    final String CANCEL_ORDER_STAGE = "cancel";
    final String ORDER_ACCEPTED_STAGE = "accepted";
    final String ORDER_FAILED_STAGE = "failed";

    String stage = request.getParameter(STAGE_PARAM);

    if (StringUtils.equals(stage, CANCEL_ORDER_STAGE)) {
%>
<jsp:include page="pay/cancel-order.jsp" />
<%} else if (StringUtils.equals(stage, ORDER_ACCEPTED_STAGE)) {%>
<jsp:include page="pay/order-accepted.jsp" />
<%} else if (StringUtils.equals(stage, ORDER_FAILED_STAGE)) {%>
<jsp:include page="pay/order-failed.jsp" />
<%} else {%>
<jsp:include page="pay/create-order.jsp" />
<%}%>
