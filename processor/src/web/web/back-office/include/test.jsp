<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.EnterEvent" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.CompositeIdOfEnterEvent" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="java.util.Date" %>
<%--
~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
--%>

<%--
  Created by IntelliJ IDEA.
  User: rumil
  Date: 09.09.11
  Time: 11:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>
<%
    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Long idOfClient = 33088L;
        Long idOfEnterEvent = 410L;
        Long idOfOrg = 2L;
        String enterName = "Вход №1";
        String turnstileAddr = "192.168.0.1";
        Integer passDirection = 2;
        Integer eventCode = 13;
        Long idOfCard = 68996922L;
        Long idOfTempCard = null;
        Date evtDateTime = new Date();
        Long idOfVisitor = null;
        String visitorFullName = null;
        Integer docType = null;
        String docSerialNum = null;
        Date issueDocDate = new Date();
        Date visitDateTime = new Date();
        Client client = null;
        if (idOfClient != null)
            client = (Client) persistenceSession.load(Client.class, idOfClient);
        EnterEvent enterEvent = new EnterEvent();
        enterEvent.setCompositeIdOfEnterEvent(
                new CompositeIdOfEnterEvent(idOfEnterEvent, idOfOrg));
        enterEvent.setEnterName(enterName);
        enterEvent.setTurnstileAddr(turnstileAddr);
        enterEvent.setPassDirection(passDirection);
        enterEvent.setEventCode(eventCode);
        enterEvent.setIdOfCard(idOfCard);
        enterEvent.setClient(client);
        enterEvent.setIdOfTempCard(idOfTempCard);
        enterEvent.setEvtDateTime(evtDateTime);
        enterEvent.setIdOfVisitor(idOfVisitor);
        enterEvent.setVisitorFullName(visitorFullName);
        enterEvent.setDocType(docType);
        enterEvent.setDocSerialNum(docSerialNum);
        enterEvent.setIssueDocDate(issueDocDate);
        enterEvent.setVisitDateTime(visitDateTime);
        persistenceSession.save(enterEvent);
        persistenceTransaction.commit();
        persistenceTransaction = null;
%>
<%="Success"%>
<%
    } catch (Exception e) {
%>
<%=e.getMessage()%>
<%
    }
%>
</body>
</html>