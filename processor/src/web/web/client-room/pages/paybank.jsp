<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Bank" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.hibernate.criterion.Order" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.paybank_jsp");
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
    RuntimeContext runtimeContext = null;
    Session persistenceSession = null;
    Transaction persistenceTransaction = null;
    List<Bank> bankList;
    try {
        runtimeContext = new RuntimeContext();
        persistenceSession = runtimeContext.createPersistenceSession();
        persistenceTransaction = persistenceSession.beginTransaction();

        Criteria cr = persistenceSession.createCriteria(Bank.class);
        bankList = (List<Bank>) cr.list();
        Collections.sort(bankList, new Comparator<Bank>() {

            public int compare(Bank o1, Bank o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });

        persistenceTransaction.commit();
        persistenceTransaction = null;
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    } catch (Exception e) {
        logger.error("Failed to build page", e);
        throw new ServletException(e);
    } finally {
        HibernateUtils.rollback(persistenceTransaction, logger);
        HibernateUtils.close(persistenceSession, logger);
    }
%>
<table class="borderless-grid">
    <tr>
        <td>
            <div class="output-text">Оплата через банк</div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Для оплаты через банк Вам необходимо распечатать бланк квитанции. Для формирования
                бланка перейдите по <a class="command-link"
                                       href="<%=StringEscapeUtils.escapeHtml(String.format("/processor/client-room/payform.jsp?contractId=%s", ContractIdFormat.format(clientAuthToken.getContractId())))%>">ссылке
                    - сформировать квитанцию</a></div>
            <%
                if(!(bankList==null || bankList.isEmpty())){
            %>
            <table class="borderless-grid">
                <tr>
                    <td align="center" colspan="2">
                        <div class="output-text">Наименование</div>
                    </td>
                    <td align="center">
                        <div class="output-text">Размер комиссии</div>
                    </td>
                </tr>
                <%
                    for (Bank bank : bankList) {
                %>
                <tr>
                    <td>
                        <img src="<%=StringEscapeUtils.escapeHtml(bank.getLogoUrl())%>"
                             alt="<%=StringEscapeUtils.escapeHtml(bank.getName())%>" />

                        <div class="output-text"></div>
                    </td>
                    <td>
                        <div class="output-text"><b><%=StringEscapeUtils.escapeHtml(bank.getName())%></b><br /><a class="command-link"
                                                                                                                  href="<%=StringEscapeUtils.escapeHtml(bank.getTerminalsUrl())%>">Адреса
                            филиалов и банкоматов</a></div>
                    </td>
                    <td>
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(String.valueOf(bank.getRate()))%>%, но не менее <%=StringEscapeUtils.escapeHtml(String.valueOf(
                                bank.getMinRate()))%> руб. (<font color="red"><%=StringEscapeUtils.escapeHtml(
                                bank.getEnrollmentType())%></font>-зачисление средств)</div>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
            <%
                }
            %>

        </td>
    </tr>
</table>
