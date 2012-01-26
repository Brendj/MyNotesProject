<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.AbbreviationUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="windows-1251" %>
<html lang="ru">
<head>
<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.payformgroup_jsp");
    Long idOfOrg=null, idOfClientGroup=null; boolean bOnlyDebtors=false;
    try {
        if (StringUtils.isEmpty(request.getParameter("idOfOrg"))) throw new Exception("Missing parameter: idOfOrg");
        idOfOrg=Long.parseLong(request.getParameter("idOfOrg"));
        if (StringUtils.isEmpty(request.getParameter("idOfClientGroup"))) throw new Exception("Missing parameter: idOfClientGroup");
        idOfClientGroup=Long.parseLong(request.getParameter("idOfClientGroup"));
        if (StringUtils.isNotEmpty(request.getParameter("onlyDebtors"))) {
            bOnlyDebtors=Boolean.parseBoolean(request.getParameter("onlyDebtors"));
        }
    } catch (Exception e) {
        logger.error("Failed to parse parameters", e);
        throw new ServletException(e);
    }


    List clients=null;
    String docTitle=null;
    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        runtimeContext = RuntimeContext.getInstance();
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();
        CompositeIdOfClientGroup cgId=new CompositeIdOfClientGroup(idOfOrg, idOfClientGroup);
        ClientGroup clientGroup = (ClientGroup) persistenceSession.get(ClientGroup.class, cgId);

        Criteria clientsCriteria = persistenceSession.createCriteria(Client.class);
        clientsCriteria.add(Restrictions.eq("clientGroup", clientGroup));
        if (bOnlyDebtors) {
            clientsCriteria.add(Restrictions.lt("balance", 0L));    
        }
        clientsCriteria = clientsCriteria.createCriteria("person");
        HibernateUtils.addAscOrder(clientsCriteria, "surname");
        HibernateUtils.addAscOrder(clientsCriteria, "firstName");
        HibernateUtils.addAscOrder(clientsCriteria, "secondName");
        clients = clientsCriteria.list();
        docTitle="ѕечать квитанции на пополнение счета по классу "+clientGroup.getGroupName()+(bOnlyDebtors?" (должники)":"");

        persistenceTransaction.commit();
        persistenceTransaction = null;
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    } catch (Exception e) {
        throw new ServletException(e);
    } finally {
        HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);
    }
%>
<title>Ќова€ школа - <%=docTitle%>%></title>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
<meta http-equiv="Content-Language" content="ru">
<jsp:include page="payform_css.jsp"/>
<body>
<%
    for (Object o : clients) {
        Client c=(Client)o;
        session.setAttribute("__payform.client", c);
        %>
        <p><%=docTitle%></p>
        <p><%=c.getPerson().getSurname()+" "+c.getPerson().getFirstName()+" "+c.getPerson().getSecondName()+": баланс "+ CurrencyStringUtils.copecksToRubles(c.getBalance())+" руб."%></p>
        <jsp:include page="payform_receipt.jsp">
            <jsp:param name="paySum" value="500"/>
        </jsp:include>
        <p style="page-break-before: always"/>
        <%
    }
%>
</body>
</html>