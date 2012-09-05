<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.City" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOService" %>

<%ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
     application.setAttribute("indexResponse",response);

    Cookie[] cks=request.getCookies();
    Long cityIdFromCookie =null;
    if(cks!=null)for(int i=0;i<cks.length;i++){
        if(cks[i].getName().equals("cityId")){
            cityIdFromCookie =Long.parseLong(cks[i].getValue());

        } }

       if(cityIdFromCookie==null){

    Cookie ck=new Cookie("cityId","1");
    ck.setMaxAge(60*60*24*183);
    response.addCookie(ck); }
    /*final Logger logger = LoggerFactory
            .getLogger("index_jsp");*/

    /*RuntimeContext runtimeContext = new RuntimeContext();
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    try {
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();
        Criteria criteria =persistenceSession.createCriteria(City.class);
        List<City> cities =(List<City>)criteria.list();
        for(City city:cities){
            System.out.println(city.getName()+" "+city.getAuthorizationType().getName());
        }

        persistenceSession.flush();
        persistenceTransaction.commit();
        persistenceTransaction = null;
    }catch(Exception e){

        logger.error("error in cities criteria : ",e);
    }  finally {
        HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);
    }*/

   /*List<City> cities = DAOService.getInstance().getTowns();
    for(City city : cities){
        System.out.println(city.getName()+" "+city.getAuthorizationType().getName());
    }*/

%>

<html>
<head>
    <title>ECafe: Личный кабинет клиента<%=null == clientAuthToken ? ""
            : StringEscapeUtils.escapeHtml(String.format(" (договор %s)", ContractIdFormat.format(clientAuthToken.getContractId())))%>
    </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon"
          href="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "images/ecafe-favicon.png"))%>"
          type="image/x-icon">
    <link rel="shortcut icon"
          href="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "images/ecafe-favicon.png"))%>"
          type="image/x-icon">    
</head>
<body>
<jsp:include page="inlinecabinet.jsp"/>
 <%--<a href="../admin-page/index.html">admin page</a>--%>
</body>
</html>