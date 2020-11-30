<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--<%@ page import="RuntimeContext" %>--%>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>

<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.*" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="javax.xml.datatype.XMLGregorianCalendar" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>

<%
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
    final String RC_CLIENT_NOT_FOUND_DESC="Клиент не найден";
    final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="По условиям найден более одного клиента";
    final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="Ошибка авторизации клиента";
    final String RC_INTERNAL_ERROR_DESC="Внутренняя ошибка";
    final String RC_NO_CONTACT_DATA_DESC="У лицевого счета нет контактных данных";


    final Logger logger = LoggerFactory
            .getLogger("ru.axetta.ecafe.processor.client-room.web.client-room.pages.change-personal-info_jsp");

    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = new RuntimeContext();

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
        Boolean smsNotificationState = null;
        Boolean notifyViaEmail = null;
        Long limit = null;

        if (haveDataToProcess) {
            try {
                currPassword = request.getParameter(CURR_PASSWORD_PARAM);
                address = request.getParameter(ADDRESS_PARAM);
                email = request.getParameter(EMAIL_PARAM);
                phone = PhoneNumberCanonicalizator.canonicalize(request.getParameter(PHONE_PARAM));
                mobilePhone = PhoneNumberCanonicalizator.canonicalize(request.getParameter(MOBILE_PHONE_PARAM));
                smsNotificationState = StringUtils.equals(request.getParameter(NOTIFY_VIA_SMS_PARAM), HTML_TRUE);
                notifyViaEmail = StringUtils.equals(request.getParameter(NOTIFY_VIA_EMAIL_PARAM), HTML_TRUE);
                limit = Long.parseLong(request.getParameter(EXPENDITURE_LIMIT)) * 100;
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse client credentials", e);
                }
                dataToProcessVerified = false;
                errorMessage = "Неверные данные и/или формат данных";
            }
        }


        if (haveDataToProcess && dataToProcessVerified) {
            try {



                Long contractId=clientAuthToken.getContractId();


                ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();



                logger.info("work");
                try {
                    dataProcessSucceed = false;

                    String plainPassword=currPassword;
                    final byte[] plainPasswordBytes = plainPassword.getBytes(CharEncoding.UTF_8);
                    final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                    String sha1HashString = new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);

                   boolean passwordIsRight= port.authorizeClient(contractId,sha1HashString).getResultCode().equals(new Long(0));

                    if (passwordIsRight) {
                        logger.info("Work start");


                        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                        if (mobilePhone==null) {
                            errorMessage = "Неверный формат мобильного телефона, используйте: 7-XXX-XXXXXXX";
                        }
                        else {
                            logger.info("contractId: "+contractId);


                            if(phone==null)phone="";
                            if(address==null)address="";

                           Result r= port.changePersonalInfo(contractId,limit,address,phone,mobilePhone,email,smsNotificationState);

                            if(!r.getResultCode().equals(RC_OK)) {

                                throw new Exception(r.getDescription());
                            }


                            dataProcessSucceed = true;
                        }
                    } else {
                        errorMessage = "Неверно указан текущий пароль";
                    }
                } catch (Exception e) {
                    logger.error("Failed to proceed client credentials check", e);
                    errorMessage = "Внутренняя ошибка";
                }

            } catch (Exception e) {
                logger.error("Failed extract client from persistance layer", e);
                dataProcessSucceed = false;
                errorMessage = "Внутренняя ошибка";
            }
        }

        try {


            Long contractId=clientAuthToken.getContractId();

            ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();

            ru.axetta.ecafe.processor.web.bo.client.ClientSummaryResult summaryResult= port.getSummary(contractId) ;

            if(!summaryResult.getResultCode().equals(RC_OK)) {

                throw new Exception(summaryResult.getDescription());

            }



            ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt summaryExt=summaryResult.getClientSummary();

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
        <% if (summaryExt.getEmail()==null || summaryExt.getEmail().isEmpty()) {%>
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
                        .escapeHtml(ContractIdFormat.format(summaryExt.getContractId()))%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лицо, заключившее договор</div>
            </td>
            <td>
               <%-- <%Person contractPerson = client.getContractPerson();%>--%>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(summaryExt.getOfficialName())%> <%--<%=StringEscapeUtils
                        .escapeHtml(summaryExt.getFirstName())%>--%> <%--<%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(summaryExt.g))%>--%>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Лицо, обслуживаемое по договору</div>
            </td>
            <td>
                <%--<%Person servicePerson = client.getPerson();%>--%>
                <div class="output-text"><%=StringEscapeUtils
                        .escapeHtml(summaryExt.getMiddleName())%> <%=StringEscapeUtils
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
                        .escapeHtml(CurrencyStringUtils.copecksToRubles(summaryExt.getBalance()))%>
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
                <div class="output-text">Лимит расходов (в рублях)</div>
            </td>
            <%
                String exLimitColor = "black";
                if (summaryExt.getExpenditureLimit().equals(0L))
                    exLimitColor = "gray";
            %>
            <td>
                <input type="text" name="<%=EXPENDITURE_LIMIT%>" size="16" maxlength="64" class="input-text"
                       style="color:<%=exLimitColor%>"
                       value="<%=summaryExt.getExpenditureLimit() / 100%>"/>
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Адрес места проживания</div>
            </td>
            <td>
                <input type="text" name="<%=ADDRESS_PARAM%>" size="32" maxlength="128" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getAddress()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Номер телефона</div>
            </td>
            <td>
                <input type="text" name="<%=PHONE_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getPhone()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Номер мобильного телефона</div>
            </td>
            <td>
                <input type="text" name="<%=MOBILE_PHONE_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getMobilePhone()))%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Уведомлять посредством SMS</div>
            </td>
            <td>
                <input type="checkbox" name="<%=NOTIFY_VIA_SMS_PARAM%>" size="16" maxlength="64" class="input-text"
                <%=summaryExt.isNotifyViaSMS() ? HTML_CHECKED : ""%> />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Адрес электронной почты</div>
            </td>
            <td>
                <input type="text" name="<%=EMAIL_PARAM%>" size="16" maxlength="64" class="input-text"
                       value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(summaryExt.getEmail()))%>" />
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

            XMLGregorianCalendar xmlLastFreePayTime = summaryExt.getLastFreePayTime();
            if (null != xmlLastFreePayTime) {
                Date lastFreePayTime=xmlLastFreePayTime.toGregorianCalendar().getTime();
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
        <% 
            // Категории скидок
            /*if (!client.getCategoriesDiscounts().isEmpty()) {
                String[] clientCategories = client.getCategoriesDiscounts().split(",");

                List<Long> idOfCategoryDiscountList = new ArrayList<Long>();

                for (String clientCategory : clientCategories) {
                    Long idOfCategoryDiscount = Long.parseLong(clientCategory);
                    idOfCategoryDiscountList.add(idOfCategoryDiscount);
                }


                Criteria categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryDiscountList));
                List<CategoryDiscount> categoryDiscountList = categoryDiscountCriteria.list();
                for (CategoryDiscount categoryDiscount : categoryDiscountList) {*/
        %>
            <%--<tr>
                <td></td>
                <td>
                     <div class="output-text"><%="- " + StringEscapeUtils
                                .escapeHtml(categoryDiscount.getCategoryName())%>
                     </div>
                </td>
            </tr>--%>
        <%//}
       // }
        %>
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
           /* persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;*/
        } catch (Exception e) {
            logger.error("Failed to build page", e);
            //throw new ServletException(e);
            %>
    <div class="error-output-text"> Не удалось отобразить персональные данные </div>
            <%

           } finally {
            /*HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);*/
        }
    } catch (Exception e) {
               logger.error(e.getMessage(),e);
        throw new UnavailableException(e.getMessage());
    }
%>