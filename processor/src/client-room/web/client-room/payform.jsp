<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%--<%@ page import="Person" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.utils.AbbreviationUtils" %>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--<%@ page import="org.hibernate.Criteria" %>--%>
<%--<%@ page import="org.hibernate.Transaction" %>--%>
<%--<%@ page import="org.hibernate.Session" %>--%>
<%--<%@ page import="org.hibernate.criterion.Restrictions" %>--%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryResult" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="windows-1251" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<html lang="ru">
<head>
<%

    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.payform_jsp");

    final Long RC_CLIENT_NOT_FOUND = 110L;
    final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    final Long RC_INVALID_DATA = 150L;
    final Long RC_NO_CONTACT_DATA = 160L;
    final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;

    final String RC_OK_DESC="OK";
    final String RC_CLIENT_NOT_FOUND_DESC="������ �� ������";
    final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="�� �������� ������ ����� ������ �������";
    final String RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC="� ������� ��� ����� �������";
    final String RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC= "� ������� ��� ���� ������ ����� �������";
    final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="������ ����������� �������";
    final String RC_INTERNAL_ERROR_DESC="���������� ������";
    final String RC_NO_CONTACT_DATA_DESC="� �������� ����� ��� ���������� ������";


    Long contractId = null;
    boolean haveDataToShow=true;
    if (StringUtils.isNotEmpty(request.getParameter("contractId"))) {
        try {
            contractId = Long.parseLong(request.getParameter("contractId"));
        } catch (NumberFormatException e) {
            logger.error("Failed to get contractId", e);
        }
    }

    if (null != contractId) {
        RuntimeContext runtimeContext = null;
        /*Session persistenceSession = null;
        Transaction persistenceTransaction = null;*/
        try {
            runtimeContext =new  RuntimeContext();
         /*   persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();*/


             /*ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");*/
             ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

             ClientRoomController port=clientAuthToken.getPort();
             ClientSummaryResult summaryResult= port.getSummary(contractId);

              if(!RC_OK.equals(summaryResult.getResultCode())){
              haveDataToShow=false;
              throw new Exception(summaryResult.getDescription());

              }

             ClientSummaryExt summaryExt=summaryResult.getClientSummary();

            session.setAttribute("__payform.client", summaryExt);
           /* persistenceTransaction.commit();
            persistenceTransaction = null;*/
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
           /* HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);*/
        }
    }

    if(haveDataToShow){
%>
<title>����� ����� - ������ ��������� �� ���������� �����</title>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
<meta http-equiv="Content-Language" content="ru">
<jsp:include page="payform_css.jsp"/>
<body>

<div id="toolbox">
    <p>������ ��� ���������� �������� �� ������, �������������� ��������������� ���������� (<i>Print preview</i>)
        � ��������� � ���������� ����������� ���������.
        ������ ��������� ����� ��&nbsp;��-4� �������� �������������
        ��&nbsp;����� ������� �4 �&nbsp;��&nbsp;������� ������ ��������
        ������. �&nbsp;������ ������� ����� ������������� ���������
        ������� ���� ����� ��&nbsp;10�15&nbsp;�� ���&nbsp;��������
        ���������� �������� ��&nbsp;�������������� (<i>landscape</i>), ����� ��������� ��������� ����������� �&nbsp;��������
        ����.</p>
    <input value="����������" onclick="window.print();" type="button" />
    <input value="�������" onclick="window.close();" type="button" />
    <center><span style="font-size: 80%;">�������������� ���� �� ������ �������� �� ���������� ����� �� ������ �� ���������</span>
    </center>
</div>

<jsp:include page="payform_receipt.jsp"/>

</body>
</html>
<%}else{

 %>
<div class="error-output-text"> �� ������� ���������� ��������� </div>
<%
}%>