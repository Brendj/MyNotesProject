<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--<%@ page import="RuntimeContext" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Person" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%--<%@ page import="HibernateUtils" %>--%>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--<%@ page import="org.hibernate.Criteria" %>--%>
<%--<%@ page import="org.hibernate.Transaction" %>--%>
<%--<%@ page import="org.hibernate.Session" %>--%>
<%--<%@ page import="org.hibernate.criterion.Restrictions" %>--%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="javax.xml.datatype.XMLGregorianCalendar" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.change-password_jsp");

    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext =new RuntimeContext();
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);

        final String PROCESS_PARAM = "submit";
        final String CURR_PASSWORD_PARAM = "current-password";
        final String NEW_PASSWORD_PARAM = "new-password";
        final String NEW_PASSWORD_CONFIRM_PARAM = "new-password-confirm";

        /*ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");*/

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

        ClientRoomController port=clientAuthToken.getPort();

        URI formAction;
        try {
            formAction = ServletUtils.getHostRelativeUriWithQuery(request);
        } catch (Exception e) {
            logger.error("Failed to build form action", e);
            throw new ServletException(e);
        }

        boolean haveDataToProcess = StringUtils.isNotEmpty(request.getParameter(PROCESS_PARAM));
        boolean dataToProcessVerified = true;
        boolean dataProcessSucceed = false;
        String errorMessage = null;
        String currPassword = null;
        String newPassword = null;
        String newPasswordConfirmation = null;

        if (haveDataToProcess) {
            try {
                currPassword = request.getParameter(CURR_PASSWORD_PARAM);
                newPassword = request.getParameter(NEW_PASSWORD_PARAM);
                newPasswordConfirmation = request.getParameter(NEW_PASSWORD_CONFIRM_PARAM);
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse client credentials", e);
                }
                errorMessage = "Неверные данные и/или формат данных";
                dataToProcessVerified = false;
            }

            if (StringUtils.isEmpty(newPassword)) {
                dataToProcessVerified = false;
                errorMessage = "Недопустимое значение для нового пароля";
            }
            if (dataToProcessVerified && !StringUtils.equals(newPassword, newPasswordConfirmation)) {
                dataToProcessVerified = false;
                errorMessage = "Новый пароль и подтверждение не совпадают";
            }
        }

        /*Session persistenceSession = null;
        Transaction persistenceTransaction = null;*/
        Long contractId=clientAuthToken.getContractId();
        if (haveDataToProcess && dataToProcessVerified) {
            try {                
                /*persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
                Client client = (Client) clientCriteria.uniqueResult();*/


                 boolean clientHasCurrPassword=port.authorizeClient(clientAuthToken.getContractId(),Client.encryptPassword(currPassword)).getResultCode().equals(new Long(0));
                try {
                    if (clientHasCurrPassword) {
                       /* client.setPassword(newPassword);*/
                          port.changePassword(contractId,Client.encryptPassword(newPassword));
                        /*client.setUpdateTime(new Date());*/
                        /*persistenceSession.update(client);*/
                        dataProcessSucceed = true;
                    } else {
                        dataProcessSucceed = false;
                        errorMessage = "Неверно указан текущий пароль";
                    }
                } catch (Exception e) {
                    logger.error("Failed to proceed client credentials check", e);
                    errorMessage = "Внутренняя ошибка";
                }
                /*persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;*/
            } finally {
                /*HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);*/
            }
        }

        /*persistenceSession = null;
        persistenceTransaction = null;*/
        
        try {
            /*persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();*/
              ClientSummaryExt summaryExt=port.getSummary(contractId).getClientSummary();
%>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
<%
        if (haveDataToProcess) {
            if (dataProcessSucceed) {
%>
<div class="output-text">Данные успешно изменены</div>
<%} else {%>
<div class="error-output-text">Не удалось изменить данные: <%=StringEscapeUtils.escapeHtml(errorMessage)%>
</div>
<%
            }
        }
%>
<table>
    <tr>
        <td>
            <div class="output-text">Номер лицевого счета</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(ContractIdFormat.format(summaryExt.getContractId()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Лицо, заключившее договор</div>
        </td>
        <td>
            <%--<%Person contractPerson = client.getContractPerson();%>--%>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(summaryExt.getOfficialName())%> <%--<%=StringEscapeUtils
                    .escapeHtml(contractPerson.getFirstName())%> <%=StringEscapeUtils
                    .escapeHtml(StringUtils.defaultString(contractPerson.getSecondName()))%>--%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Лицо, обслуживаемое по договору</div>
        </td>
        <td>
            <%--<%Person servicePerson = client.getPerson();%>--%>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(summaryExt.getMiddleName())%> <%=StringEscapeUtils
                    .escapeHtml(summaryExt.getFirstName())%> <%=StringEscapeUtils
                    .escapeHtml(StringUtils.defaultString(summaryExt.getLastName()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Дата заключения договора</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(summaryExt.getDateOfContract().toGregorianCalendar().getTime()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Состояние договора</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(summaryExt.getStateOfContract())%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Текущий баланс счета</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(summaryExt.getBalance().toString())%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Лимит овердрафта</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(summaryExt.getOverdraftLimit()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Адрес места проживания</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getAddress()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Номер телефона</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getPhone()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Номер мобильного телефона</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getMobilePhone()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Адрес электронной почты</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getEmail()))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Уведомлять посредством SMS</div>
        </td>
        <td>
            <div class="output-text"><%=summaryExt.isNotifyViaSMS() ? "да" : "нет"%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Уведомлять по электронной почте</div>
        </td>
        <td>
            <div class="output-text"><%=summaryExt.isNotifyViaEmail() ? "да" : "нет"%>
            </div>
        </td>
    </tr>
    <%
            Integer freePayMaxCount = summaryExt.getFreePayMaxCount();
            if (null != freePayMaxCount) {
    %>
    <tr>
        <td>
            <div class="output-text">Предельное количество покупок без предъявления карты</div>
        </td>
        <td>
            <div class="output-text"><%=freePayMaxCount%>
            </div>
        </td>
    </tr>
    <%}%>
    <tr>
        <td>
            <div class="output-text">Текущее количество покупок без предъявления карты</div>
        </td>
        <td>
            <div class="output-text"><%=summaryExt.getFreePayCount()%>
            </div>
        </td>
    </tr>
    <%

        XMLGregorianCalendar xmlLastFreePayTime=summaryExt.getLastFreePayTime();
        if (null != xmlLastFreePayTime) {
            Date lastFreePayTime = xmlLastFreePayTime.toGregorianCalendar().getTime();

    %>
    <tr>
        <td>
            <div class="output-text">Время последней покупки без предъявления карты</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(lastFreePayTime))%>
            </div>
        </td>
    </tr>
    <%}%>
    <tr>
        <td>
            <div class="output-text">Тип предоставляемой льготы</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(Client.DISCOUNT_MODE_NAMES[summaryExt.getDiscountMode()])%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Текущий пароль</div>
        </td>
        <td>
            <input type="password" name="<%=CURR_PASSWORD_PARAM%>" size="16" maxlength="64" class="input-text" />
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Новый пароль</div>
        </td>
        <td>
            <input type="password" name="<%=NEW_PASSWORD_PARAM%>" size="16" maxlength="64" class="input-text" />
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Подтверждение</div>
        </td>
        <td>
            <input type="password" name="<%=NEW_PASSWORD_CONFIRM_PARAM%>" size="16" maxlength="64" class="input-text" />
        </td>
    </tr>
    <tr>
        <td><input type="submit" name="<%=PROCESS_PARAM%>" value="Изменить пароль" class="command-button" /></td>
    </tr>
</table>
</form>
<%
            /*persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;*/
        } catch (Exception e) {
            logger.error("Failed to build page", e);
            throw new ServletException(e);
        } finally {
            /*HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);*/
        }
    } catch (Exception e) {
        throw new UnavailableException(e.getMessage());                
    }
%>