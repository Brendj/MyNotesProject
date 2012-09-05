<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Person" %>
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
<%@ page import="java.net.URI" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.Base64" %>
<%@ page import="java.net.URLDecoder" %>
<html lang="ru">
<head>
<%

    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.payform_jsp");
    if (StringUtils.isEmpty(request.getCharacterEncoding())) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            logger.error("Can\'t assign character set to request", e);
            throw new ServletException(e);
        }
    }
    Long contractId = null;
    if (StringUtils.isNotEmpty(request.getParameter("contractId"))) {
        try {
            contractId = Long.parseLong(request.getParameter("contractId"));
        } catch (NumberFormatException e) {
            logger.error("Failed to get contractId", e);
        }
    }

    if (null != contractId) {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            session.setAttribute("__payform.client", client);
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
    }
    URI formAction;
    try {
        formAction = ServletUtils.getHostRelativeUriWithQuery(request);
    } catch (Exception e) {
        logger.error("Failed to build form action", e);
        throw new ServletException(e);
    }
    //String fieldReceiver="ООО &quot;АйкьюТек&quot;";
    //String fieldBank = "Отделении «Банк Татарстан» № 8610  г.Казань";
    //String fieldAccount="40702810662260004883";
	//String fieldINN="1656057429";
	//String fieldBIK="049205603";
	//String fieldCorrAcc="30101810600000000603";

    String fieldReceiver=(request.getParameter("fieldReceiver")!=null?request.getParameter("fieldReceiver"):"ООО &quot;АйкьюТек&quot;");
	String fieldAccount=(request.getParameter("fieldAccount")!=null?request.getParameter("fieldAccount"):"40702810662260004883");
	String fieldINN=(request.getParameter("fieldINN")!=null?request.getParameter("fieldINN"):"1656057429");
	String fieldBank=(request.getParameter("fieldBank")!=null?request.getParameter("fieldBank"):"Отделении «Банк Татарстан» № 8610  г.Казань");
	String fieldBIK=(request.getParameter("fieldBIK")!=null?request.getParameter("fieldBIK"):"049205603");
	String fieldCorrAcc=(request.getParameter("fieldCorrAcc")!=null?request.getParameter("fieldCorrAcc"):"30101810600000000603");


%>
<title>Новая школа - Печать квитанции на пополнение счета</title>
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
    <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
          enctype="application/x-www-form-urlencoded" class="borderless-form">
        <table id="login-form" align="center" width="100%">
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>Наименование получателя платежа</label>
                </td><td>
                    <input type="text" id="fieldReceiver" value="<%=fieldReceiver%>" name="fieldReceiver" size="64">
                </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>ИНН получателя платежа</label>
                </td><td>
                <input type="text" id="fieldINN" value="<%=fieldINN%>" name="fieldINN" size="64">
            </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>Номер счета получателя платежа</label>
                </td><td>
                    <input type="text" id="fieldAccount" value="<%=fieldAccount%>" name="fieldAccount" size="64">
                </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>Наименование банка получателя платежа</label>
                </td><td>
                    <input type="text" id="fieldBank" value="<%=fieldBank%>" name="fieldBank" size="64">
                </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>БИК</label>
                </td><td>
                    <input type="text" id="fieldBIK" value="<%=fieldBIK%>" name="fieldBIK" size="64">
                </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td>
                    <label>Номер кор./сч. банка получателя платежа</label>
                </td><td>
                    <input type="text" id="fieldCorrAcc" value="<%=fieldCorrAcc%>" name="fieldCorrAcc" size="64">
                </td>
            </tr>
            <tr valign="middle" class="login-form-input-tr">
                <td align="center" colspan="2">
                    <input type="submit" id="submit" name="submit" value="Изменить"/>
                </td>
            </tr>
        </table>
    <input value="Напечатать" onclick="window.print();" type="button" />
    <input value="Закрыть" onclick="window.close();" type="button" />
    <center><span style="font-size: 80%;">информационный блок от начала страницы до пунктирной линии на печать не выводится</span>
    </center>
</div>

<jsp:include page="payform_receipt.jsp"/>

</body>
</html>