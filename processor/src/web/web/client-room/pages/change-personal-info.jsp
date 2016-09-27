<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.CategoryDiscount" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Person" %>
<%@ page import="ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.util.ClientRoomNotificationSettingsUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TimeZone" %>

<%
    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.web.client-room.pages.change-personal-info_jsp");

    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();

        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);

        final String PROCESS_PARAM = "submit";
        final String CURR_PASSWORD_PARAM = "current-password";
        final String EMAIL_PARAM = "email";
        final String ADDRESS_PARAM = "address";
        final String PHONE_PARAM = "phone";
        final String MOBILE_PHONE_PARAM = "mobile-phone";
        final String HTML_TRUE = "on";
        final String HTML_CHECKED = "checked";
        final String NOTIFY_VIA_SMS_PARAM = "notify-via-sms";
        final String NOTIFY_RULE_PARAM = "notify-rule";
        final String NOTIFY_VIA_EMAIL_PARAM = "notify-via-mail";
        final String EXPENDITURE_LIMIT = "expenditure-limit";

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
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
        String address = null;
        String email = null;
        String phone = null;
        String mobilePhone = null;
        Boolean notifyViaSms = null;
        Boolean notifyViaEmail = null;
        Long expenditureLimit = null;

        if (haveDataToProcess) {
            try {
                currPassword = request.getParameter(CURR_PASSWORD_PARAM);
                address = request.getParameter(ADDRESS_PARAM);
                email = request.getParameter(EMAIL_PARAM);
                phone = PhoneNumberCanonicalizator.canonicalize(request.getParameter(PHONE_PARAM));
                mobilePhone = PhoneNumberCanonicalizator.canonicalize(request.getParameter(MOBILE_PHONE_PARAM));
                notifyViaSms = StringUtils.equals(request.getParameter(NOTIFY_VIA_SMS_PARAM), HTML_TRUE);
                notifyViaEmail = StringUtils.equals(request.getParameter(NOTIFY_VIA_EMAIL_PARAM), HTML_TRUE);
                expenditureLimit = Long.parseLong(request.getParameter(EXPENDITURE_LIMIT)) * 100;
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse client credentials", e);
                }
                dataToProcessVerified = false;
                errorMessage = "Неверные данные и/или формат данных";
            }
        }
        
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        if (haveDataToProcess && dataToProcessVerified) {
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
                Client client = (Client) clientCriteria.uniqueResult();
                try {
                    dataProcessSucceed = false;
                    if (client.hasPassword(currPassword)) {
                        client.setAddress(address);
                        client.setPhone(phone);
                        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                        if (mobilePhone==null) {
                            errorMessage = "Неверный формат мобильного телефона, используйте: 7-XXX-XXXXXXX";
                        }
                        else {
                            client.setMobile(mobilePhone);
                            client.setEmail(email);
                            if (!RuntimeContext.getInstance().getOptionValueBool(
                                                    Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM)) {
                                client.setNotifyViaSMS(notifyViaSms);
                            }
                            client.setNotifyViaEmail(notifyViaEmail);
                            client.setUpdateTime(new Date());
                            client.setExpenditureLimit(expenditureLimit);
                            persistenceSession.update(client);
                            ClientRoomNotificationSettingsUtils.setNotificationSettings(client, request, NOTIFY_RULE_PARAM, HTML_TRUE);
                            dataProcessSucceed = true;
                        }
                    } else {
                        errorMessage = "Неверно указан текущий пароль";
                    }
                } catch (Exception e) {
                    logger.error("Failed to proceed client credentials check", e);
                    errorMessage = "Внутренняя ошибка";
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed extract client from persistance layer", e);
                dataProcessSucceed = false;
                errorMessage = "Внутренняя ошибка";
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        persistenceSession = null;
        persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();
            List <NotificationSettingItem> notifications = ClientRoomNotificationSettingsUtils.getNotificationSettings(client);
            Long subBalance1 = client.getSubBalance1()==null?0L:client.getSubBalance1();
            Long subBalance0 = client.getBalance() - subBalance1;
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
        <% if (client.getEmail()==null || client.getEmail().isEmpty()) {%>
            <tr>
                <td colspan="2">
                    <div class="error-output-text">Пожалуйста, сохраните свой адрес электронной почты для возможности восстановления пароля</div>
                </td>
            </tr>
        <%}%>
        <tr>
            <td>
                <div class="output-text">Номер лицевого счета</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(ContractIdFormat.format(client.getContractId()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лицо, заключившее договор</div>
            </td>
            <td>
                <%Person contractPerson = client.getContractPerson();%>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(contractPerson.getSurname())%> <%=StringEscapeUtils
                        .escapeHtml(contractPerson.getFirstName())%> <%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(contractPerson.getSecondName()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лицо, обслуживаемое по договору</div>
            </td>
            <td>
                <%Person servicePerson = client.getPerson();%>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(servicePerson.getSurname())%> <%=StringEscapeUtils
                        .escapeHtml(servicePerson.getFirstName())%> <%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(servicePerson.getSecondName()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Дата заключения договора</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils.escapeHtml(timeFormat.format(client.getContractTime()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Состояние договора</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(Client.CONTRACT_STATE_NAMES[client.getContractState()])%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Текущий баланс счета</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(CurrencyStringUtils.copecksToRubles(client.getBalance()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Основнй счет</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(CurrencyStringUtils.copecksToRubles(subBalance0))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Субсчет АП</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(CurrencyStringUtils.copecksToRubles(subBalance1))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лимит овердрафта</div>
            </td>
            <td>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(CurrencyStringUtils.copecksToRubles(client.getLimit()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лимит расходов (в рублях)</div>
            </td>
            <%
                String exLimitColor = "black";
                if (client.getExpenditureLimit().equals(0L))
                    exLimitColor = "gray";
            %>
            <td>
                <input type="text" name="<%=EXPENDITURE_LIMIT%>" size="16" maxlength="64" class="input-text"
                       style="color:<%=exLimitColor%>"
                       value="<%=client.getExpenditureLimit() / 100%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Адрес места проживания</div>
            </td>
            <td>
                <input type="text" name="<%=ADDRESS_PARAM%>" size="32" maxlength="128" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(client.getAddress()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Номер телефона</div>
            </td>
            <td>
                <input type="text" name="<%=PHONE_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(client.getPhone()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Номер мобильного телефона</div>
            </td>
            <td>
                <input type="text" name="<%=MOBILE_PHONE_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(client.getMobile()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Уведомлять посредством SMS</div>
            </td>
            <td>
                <input type="checkbox" name="<%=NOTIFY_VIA_SMS_PARAM%>" size="16" maxlength="64" class="input-text"
                <%=client.isNotifyViaSMS() ? HTML_CHECKED : ""%>
                <%=RuntimeContext.getInstance().getOptionValueBool(
                        Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM) ? "disabled=\"disabled\" onclick=\"return false\" onkeydown=\"return false\"" : ""%>/>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Адрес электронной почты</div>
            </td>
            <td>
                <input type="text" name="<%=EMAIL_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(client.getEmail()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Правила оповещений</div>
            </td>
            <td>
                <table cellpadding="0" cellspacing="0" border="0">
                    <%
                    for (NotificationSettingItem it : notifications) {
                        %>
                        <tr>
                            <td>
                                <div class="output-text"><%=it.getNotifyName()%></div>
                            </td>
                            <td>
                                <input type="checkbox" name="<%=NOTIFY_RULE_PARAM%>-<%= it.getNotifyType() %>" size="16" maxlength="64" class="input-text"
                                       <%=it.isEnabled() ? HTML_CHECKED : ""%>
                                        <%=RuntimeContext.getInstance().getOptionValueBool(
                                                Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM) ? "disabled=\"disabled\" onclick=\"return false\" onkeydown=\"return false\"" : ""%>/>
                            </td>
                        </tr>
                        <%
                    }
                    %>
                </table>
            </td>
        </tr>
        <!--
        <tr>
            <td>
                <div class="output-text">Уведомлять по электронной почте</div>
            </td>
            <td>
                <input type="checkbox" name="<=NOTIFY_VIA_EMAIL_PARAM%>" size="16" maxlength="64"
                       class="input-text" <=
                client.isNotifyViaEmail() ? HTML_CHECKED : ""%> />
            </td>
        </tr>-->
        <%
            Integer freePayMaxCount = client.getFreePayMaxCount();
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
                <div class="output-text"><%=client.getFreePayCount()%>
                </div>
            </td>
        </tr>
        <%
            Date lastFreePayTime = client.getLastFreePayTime();
            if (null != lastFreePayTime) {
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
                        .escapeHtml(Client.DISCOUNT_MODE_NAMES[client.getDiscountMode()])%>
                </div>
            </td>
        </tr>
        <% 
            // Категории скидок
            if (!client.getCategoriesDiscounts().isEmpty()) {
                String[] clientCategories = client.getCategoriesDiscounts().split(",");

                List<Long> idOfCategoryDiscountList = new ArrayList<Long>();

                for (String clientCategory : clientCategories) {
                    Long idOfCategoryDiscount = Long.parseLong(clientCategory);
                    idOfCategoryDiscountList.add(idOfCategoryDiscount);
                }


                Criteria categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryDiscountList));
                List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
                for (CategoryDiscount categoryDiscount : categoryDiscountList) {
        %>
            <tr>
                <td></td>
                <td>
                     <div class="output-text"><%="- " + StringEscapeUtils
                                .escapeHtml(categoryDiscount.getCategoryName())%>
                     </div>
                </td>
            </tr>
        <%}}%>
        <tr>
            <td>
                <div class="output-text">Текущий пароль</div>
            </td>
            <td>
                <input type="password" name="<%=CURR_PASSWORD_PARAM%>" size="32" maxlength="64" class="input-text" />
            </td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" name="<%=PROCESS_PARAM%>" value="Изменить личные данные" class="command-button" />
            </td>
        </tr>
    </table>
</form>
<%
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to build page", e);
            throw new ServletException(e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>