<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ClientGroup" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.CompositeIdOfClientGroup" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Org" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
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
        session.setAttribute("__payform.org", (Org) persistenceSession.get(Org.class,idOfOrg));
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
        docTitle="Печать квитанции на пополнение счета по классу "+clientGroup.getGroupName()+(bOnlyDebtors?" (должники)":"");

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
    //URI formAction;
    //try {
    //    formAction = ServletUtils.getHostRelativeUriWithQuery(request);
    //} catch (Exception e) {
    //    logger.error("Failed to build form action", e);
    //    throw new ServletException(e);
    //}
    //String fieldReceiver="ООО &quot;АйкьюТек&quot;";
    //String fieldBank = "Отделении «Банк Татарстан» № 8610  г.Казань";
    //String fieldAccount="40702810662260004883";
	//String fieldINN="1656057429";
	//String fieldBIK="049205603";
	//String fieldCorrAcc="30101810600000000603";

    //String fieldReceiver=(request.getParameter("fieldReceiver")!=null?request.getParameter("fieldReceiver"):"");
	//String fieldAccount=(request.getParameter("fieldAccount")!=null?request.getParameter("fieldAccount"):"");
	//String fieldINN=(request.getParameter("fieldINN")!=null?request.getParameter("fieldINN"):"");
	//String fieldBank=(request.getParameter("fieldBank")!=null?request.getParameter("fieldBank"):"");
	//String fieldBIK=(request.getParameter("fieldBIK")!=null?request.getParameter("fieldBIK"):"");
	//String fieldCorrAcc=(request.getParameter("fieldCorrAcc")!=null?request.getParameter("fieldCorrAcc"):"");

%>
<title>Новая школа - <%=docTitle%>%></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="ru">
<jsp:include page="payform_css.jsp"/>
<body>
<div id="toolbox">
    <p>Прежде чем отправлять документ на печать, воспользуйтесь предварительным просмотром (<i>Print preview</i>)
        и убедитесь в корректном отображении документа.
        Обычно квитанция формы «№&nbsp;ПД-4» свободно располагается
        на&nbsp;листе формата А4 и&nbsp;не&nbsp;требует особых настроек
        печати. В&nbsp;редких случаях может потребоваться уменьшить
        боковые поля листа до&nbsp;10–15&nbsp;мм или&nbsp;изменить
        ориентацию страницы на&nbsp;горизонтальную (<i>landscape</i>), чтобы квитанция полностью поместилась в&nbsp;печатное
        поле.</p>
        <input value="Напечатать" onclick="window.print();" type="button" />
        <input value="Закрыть" onclick="window.close();" type="button" />
        <center><span style="font-size: 80%;">информационный блок от начала страницы до пунктирной линии на печать не выводится</span>
        </center>
</div>
<%
    for (Object o : clients) {
        Client c=(Client)o;
        session.setAttribute("__payform.client", c);
        %>
        <p><%=docTitle%></p>
        <p><%=c.getPerson().getSurname()+" "+c.getPerson().getFirstName()+" "+c.getPerson().getSecondName()+": баланс "+ CurrencyStringUtils.copecksToRubles(c.getBalance())+" руб."%></p>
        <jsp:include page="payform_receipt.jsp"/>
        <p style="page-break-before: always"/>
        <%
    }
%>
</body>
</html>