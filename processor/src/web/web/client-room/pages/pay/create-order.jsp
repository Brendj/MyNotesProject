<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.partner.rbkmoney.CurrencyStringConverter" %>
<%@ page import="ru.axetta.ecafe.processor.core.partner.rbkmoney.PaymentMethodConverter" %>
<%@ page import="ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKConstants" %>
<%@ page import="ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.ClientPayment" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Contragent" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.Inet4Address" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Option" %>
<%!

    public static String getHash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
               //String s="f78spx";
               //String s="muffin break";
              MessageDigest m = MessageDigest.getInstance("MD5");
               m.reset();
                // передаем в MessageDigest байт-код строки
               m.update(str.getBytes("utf-8"));
                // получаем MD5-хеш строки без лидирующих нулей
                String s2 = new BigInteger(1, m.digest()).toString(16);
               StringBuilder sb = new StringBuilder(32);
               // дополняем нулями до 32 символов, в случае необходимости
               //System.out.println(32 - s2.length());
        	        for (int i = 0, count = 32 - s2.length(); i < count; i++) {
            	            sb.append("0");
           	        }
       	        // возвращаем MD5-хеш
                return sb.append(s2).toString();
            }%>
<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.pay.create-order_jsp");

    final String STAGE_PARAM = "stage";
    final String CANCEL_ORDER_STAGE = "cancel";
    final String ORDER_ACCEPTED_STAGE = "accepted";
    final String ORDER_FAILED_STAGE = "failed";

    final String PROCESS_PARAM = "processOrderCreation";
    final String AMOUNT_PARAM = "amount";
    final String PAYMENT_METHOD_PARAM = "paymentMethod";
    final String CONTRAGENT_NAME_PARAM = "contragent";
    final String ID_OF_CLIENT_PAYMENT_ORDER_PARAM = "order-id";


    // final String PRODUCT_ID_CHRONOPAY_PARAM="product_id";
    //final String PRODUCT_PRICE_CHRONOPAY_PARAM="product_price";
   // final String SIGN_CHRONOPAY_PARAM="sign";
   // final String ORDER_ID_CHRONOPAY_PARAM="order_id";


    final long MIN_COPECKS_SUM = 1000L;

    final String PRODUCT_ID_FOR_CHRONOPAY= "006387-0001-0001";
     Boolean chronopaySection;
     Boolean rbkSection;



    RuntimeContext runtimeContext = null;
    try {
        runtimeContext = RuntimeContext.getInstance();

        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        TimeZone localTimeZone = runtimeContext.getDefaultLocalTimeZone(session);
        timeFormat.setTimeZone(localTimeZone);

        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        URI currentUri;
        URI fullSpecifiedlCurrentUri;
        try {
            currentUri = ServletUtils.getHostRelativeUriWithQuery(request);
            fullSpecifiedlCurrentUri = ServletUtils.getFullSpecifiedUriWithQuery(request);
        } catch (Exception e) {
            logger.error("Error during currentUri building", e);
            throw new ServletException(e);
        }

        String rublesAmount = request.getParameter(AMOUNT_PARAM);
        logger.info("rublesAmount: "+rublesAmount);
        boolean haveDataToProcess =
                StringUtils.isNotEmpty(request.getParameter(PROCESS_PARAM)) && StringUtils.isNotEmpty(rublesAmount);
        boolean dataToProcessVerified = false;

        String contragentName = null;
        Long copecksAmount = null;
        int paymentMethod = 0;
        String errorMessage = null;

        if (haveDataToProcess) {
            try {
                contragentName = request.getParameter(CONTRAGENT_NAME_PARAM);
                logger.info("contragentName: "+contragentName);
                paymentMethod = Integer.valueOf(request.getParameter(PAYMENT_METHOD_PARAM));
                copecksAmount = CurrencyStringUtils.rublesToCopecks(rublesAmount);
                if (StringUtils.isEmpty(contragentName) || copecksAmount <= 0) {
                    errorMessage = "Неверные данные и/или формат данных";
                } else if (MIN_COPECKS_SUM > copecksAmount) {
                    errorMessage = String.format("Сумма должна быть не менее %s руб.",
                            CurrencyStringUtils.copecksToRubles(MIN_COPECKS_SUM));
                } else {
                    dataToProcessVerified = true;
                }
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to read data", e);
                }
                errorMessage = "Неверные данные и/или формат данных";
            }
        }

        RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
        ChronopayConfig chronopayConfig=runtimeContext.getPartnerChronopayConfig();

        chronopaySection=runtimeContext.getOptionValueBool(Option.OPTION_CHRONOPAY_SECTION);
        rbkSection=runtimeContext.getOptionValueBool(Option.OPTION_RBK_SECTION);

        if (!haveDataToProcess || !dataToProcessVerified) { %>


  <table border="1">
<%
       if(chronopaySection){
%>

     <tr>
    <td>
    <table class="borderless-grid">
    <tr>
        <td>
            <img src="<%=StringEscapeUtils.escapeHtml(response.encodeURL(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/chronopay/logo-left.png")))%>"
                 alt="Chronopay logo" />

            <div class="output-text">Оплата по банковской карте (Сервис Chronopay)</div>
        </td>
    </tr>
      <tr>
          <td>
        <div class="output-text">Размер комиссии</div>
        <table class="borderless-grid">
            <tr>
                <td>
                    <div class="output-text">
                        Банковские карты:
                    </div>
                </td>
                <td>
                    <div class="output-text">
                        <%=StringEscapeUtils.escapeHtml(chronopayConfig.getRate().toString())%> %
                    </div>
                </td>
            </tr>
           </table>
          </td>
        </tr>
    <tr>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form" name="createOrderForm1">
                <input type="hidden" name="<%=CONTRAGENT_NAME_PARAM%>"
                       value="<%=StringEscapeUtils.escapeHtml("Chronopay")%>" />
                <input type="hidden" name="<%=PAYMENT_METHOD_PARAM%>"
                       value="0" />
                <table class="borderless-grid">
                    <tr>
                        <td>
                            <div class="output-text">Сумма к зачислению (в рублях)</div>
                        </td>
                        <td>
                            <input id="amountEdit1" class="input-text" type="text" name="<%=AMOUNT_PARAM%>"
                                   value="<%=StringUtils.isEmpty(rublesAmount) ? "" : rublesAmount%>" />
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2">
                            <input class="command-button" type="submit" name="<%=PROCESS_PARAM%>" value="Продолжить" />
                        </td>
                    </tr>
                </table>
            </form>
            <script type="text/javascript">
                document.createOrderForm1.<%=AMOUNT_PARAM%>.focus();
            </script>
        </td>
    </tr>
        </table>
     </td>
     </tr>

       <%}if(rbkSection){%>

    <tr>
        <td>
    <table class="borderless-grid">
    <tr>
        <td>
            <img src="<%=StringEscapeUtils.escapeHtml(response.encodeURL(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/rbkmoney/logo.png")))%>"
                 alt="RBK Money logo" />

            <div class="output-text">Платежная система RBK Money</div>
        </td>
    </tr>
    <tr>
        <td>
            <%if (!haveDataToProcess) {%>
            <%--<div class="menuOutput-text">--%>
            <%--Описание схемы оплаты через RBK Money с указанием взимаемых процентов и со ссылкой на отдельное--%>
            <%--побробное описание: надо указать, что обязательная процентая ставка, взимаемая при переводе, составляет <%=rbkMoneyConfig.getRate()%>%.--%>
            <%--К ней добавляется процентая ставка (расчитывается уже с предыдущей суммы, а не с той, что была указана к зачислению), зависящая от указанного способа оплаты.--%>
            <%--И тут надо привести ссылку на страницу сайта RBK Money с указанием таблицы ставок и минимальных значений по каждому способу оплаты.--%>
            <%--</div>--%>
            <div class="output-text">Размер комиссии</div>
            <table class="borderless-grid">
                <tr>
                    <td>
                        <div class="output-text">
                            Банковская карта Visa/MasterCard
                        </div>
                    </td>
                    <td>
                        <div class="output-text">
                            6,09%
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="output-text">
                            Предоплаченная карта RBK Money
                        </div>
                    </td>
                    <td>
                        <div class="output-text">
                            3%
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="output-text">
                            Интернет банкинг
                        </div>
                    </td>
                    <td>
                        <div class="output-text">
                            от 3% до 6,09%
                        </div>
                    </td>
                </tr>
            </table>
            <a class="command-link"
               href="<%=StringEscapeUtils.escapeHtml("https://rbkmoney.ru/common/dpage.aspx?dynamicPageId=rupayrates&RN=dpage.aspx")%>">Тарифы
                на сайте RBK Money</a>
            <%} else {%>
            <div class="output-text">
                Ошибка: <%=StringEscapeUtils.escapeHtml(errorMessage)%>
            </div>
            <%}%>
        </td>
    </tr>
    <tr>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(currentUri.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form" name="createOrderForm">
                <input type="hidden" name="<%=CONTRAGENT_NAME_PARAM%>"
                       value="<%=StringEscapeUtils.escapeHtml(rbkMoneyConfig.getContragentName())%>" />
                <table class="borderless-grid">
                    <tr>
                        <td>
                            <div class="output-text">Сумма к зачислению (в рублях)</div>
                        </td>
                        <td>
                            <input id="amountEdit" class="input-text" type="text" name="<%=AMOUNT_PARAM%>"
                                   value="<%=StringUtils.isEmpty(rublesAmount) ? "" : rublesAmount%>" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="output-text">Способ оплаты</div>
                        </td>
                        <td>
                            <select size="1" class="input-text" name="<%=PAYMENT_METHOD_PARAM%>">
                                <%int[] paymentMethods = RBKConstants.SUPPORTED_PAYMENT_METHODS;
                                    String[] paymentMethodsNames = ClientPayment.PAYMENT_METHOD_NAMES;
                                    for (int i : paymentMethods) {%>
                                <option <%=i == paymentMethod ? "selected" : ""%> value="<%=i%>"><%=StringEscapeUtils
                                        .escapeHtml(paymentMethodsNames[i])%>
                                </option>
                                <%}%>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input class="command-button" type="submit" name="<%=PROCESS_PARAM%>" value="Продолжить" />
                        </td>
                    </tr>
                </table>
            </form>
            <script type="text/javascript">
                document.createOrderForm.<%=AMOUNT_PARAM%>.focus();
            </script>
        </td>
    </tr>
    </table>
        </td>

   </tr>

  <%}%>

</table>
<%
} else {
    if (StringUtils.equals(contragentName, rbkMoneyConfig.getContragentName())) {
        Long contragentSum = Math.round(copecksAmount * 100.0d / (100 - rbkMoneyConfig.getRate()));
        String recipientAmount;
        try {
            recipientAmount = CurrencyStringConverter.copecksToRubles(contragentSum);
        } catch (Exception e) {
            logger.error("Failed to convert amount", e);
            throw new ServletException(e);
        }

        Long idOfClient = null;
        Long idOfContragent = null;
        String clientEmail = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();
            idOfClient = client.getIdOfClient();
            clientEmail = client.getEmail();

            Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
            contragentCriteria.add(Restrictions.eq("contragentName", contragentName));
            Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
            idOfContragent = contragent.getIdOfContragent();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        Long idOfClientPaymentOrder;
        try {
            idOfClientPaymentOrder = runtimeContext.getClientPaymentOrderProcessor()
                    .createPaymentOrder(idOfClient, idOfContragent, paymentMethod, copecksAmount, contragentSum);
        } catch (Exception e) {
            logger.error("Failed to add clientPaymentOrder", e);
            throw new ServletException(e);
        }

        String paymenMethodCodeName = PaymentMethodConverter.getPaymentMethodCodeName(paymentMethod);
        URI confirmUri = rbkMoneyConfig.getPurchaseUri();
        URI cancelUri;
        URI successUri;
        URI failUri;
        try {
            cancelUri = currentUri;

            successUri = UriUtils.putParam(fullSpecifiedlCurrentUri, STAGE_PARAM, ORDER_ACCEPTED_STAGE);
            successUri = UriUtils
                    .putParam(successUri, ID_OF_CLIENT_PAYMENT_ORDER_PARAM, idOfClientPaymentOrder.toString());

            failUri = UriUtils.putParam(fullSpecifiedlCurrentUri, STAGE_PARAM, ORDER_FAILED_STAGE);
            failUri = UriUtils.putParam(failUri, ID_OF_CLIENT_PAYMENT_ORDER_PARAM, idOfClientPaymentOrder.toString());
        } catch (Exception e) {
            throw new ServletException(e);
        }
%>
<table>
    <tr>
        <td colspan="2">
            <div class="output-text">Подтвердите параметры запроса на перечисление средств</div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Платежная система</div>
        </td>
        <td>
            <div class="output-text"><%=contragentName%></div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Сумма к зачислению (в рублях)</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(copecksAmount))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Сумма к оплате с учетом внутренней комиссии платежной системы (в рублях)</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(contragentSum))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Способ оплаты</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(ClientPayment.PAYMENT_METHOD_NAMES[paymentMethod])%>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table>
                <tr>
                    <td>
                        <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(confirmUri.toString()))%>"
                              method="post" enctype="application/x-www-form-urlencoded" class="borderless-form">
                            <input type="hidden" name="eshopId"
                                   value="<%=StringEscapeUtils.escapeHtml(rbkMoneyConfig.getEshopId())%>" />
                            <input type="hidden" name="orderId" value="<%=idOfClientPaymentOrder%>" />
                            <input type="hidden" name="serviceName"
                                   value="<%=StringEscapeUtils.escapeHtml(rbkMoneyConfig.getServiceName())%>" />
                            <input type="hidden" name="recipientAmount"
                                   value="<%=StringEscapeUtils.escapeHtml(recipientAmount)%>" />
                            <input type="hidden" name="recipientCurrency" value="RUR" />
                            <%if (StringUtils.isNotEmpty(clientEmail)) {%>
                            <input type="hidden" name="user_email"
                                   value="<%=StringEscapeUtils.escapeHtml(clientEmail)%>" />
                            <%}%>
                            <input type="hidden" name="version" value="2" />
                            <input type="hidden" name="preference"
                                   value="<%=StringEscapeUtils.escapeHtml(paymenMethodCodeName)%>" />
                            <%--<input type="hidden" name="successUrl" value="<%=successUri.toString()%>" />--%>
                            <%--<input type="hidden" name="failUrl" value="<%=failUri.toString()%>" />--%>
                            <input type="hidden" name="successUrl"
                                   value="<%=StringEscapeUtils.escapeHtml(response.encodeURL(successUri.toString()))%>" />
                            <input type="hidden" name="failUrl"
                                   value="<%=StringEscapeUtils.escapeHtml(response.encodeURL(failUri.toString()))%>" />
                            <input class="command-button" type="submit"
                                   value="Подтвердить и продолжить через RBK Money" />
                        </form>
                    </td>
                    <td>
                        <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(cancelUri.toString()))%>"
                              method="post" enctype="application/x-www-form-urlencoded" class="borderless-form">
                            <input type="hidden" name="<%=ID_OF_CLIENT_PAYMENT_ORDER_PARAM%>"
                                   value="<%=idOfClientPaymentOrder%>" />
                            <input type="hidden" name="<%=STAGE_PARAM%>" value="<%=CANCEL_ORDER_STAGE%>" />
                            <input class="command-button" type="submit" value="Отменить" />
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<%} else {
    if (StringUtils.equals(contragentName, chronopayConfig.getContragentName())) {
        Long contragentSum = Math.round(copecksAmount * 100.0d / (100 - chronopayConfig.getRate()));
        String recipientAmount;
        try {
            recipientAmount = CurrencyStringConverter.copecksToRubles(contragentSum);
        } catch (Exception e) {
            logger.error("Failed to convert amount", e);
            throw new ServletException(e);
        }

        Long idOfClient = null;
        Long idOfContragent = null;
        String clientEmail = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", clientAuthToken.getContractId()));
            Client client = (Client) clientCriteria.uniqueResult();
            idOfClient = client.getIdOfClient();
            clientEmail = client.getEmail();

            Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
            contragentCriteria.add(Restrictions.eq("contragentName", contragentName));
            Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
            idOfContragent = contragent.getIdOfContragent();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        Long idOfClientPaymentOrder;
        try {
            idOfClientPaymentOrder = runtimeContext.getClientPaymentOrderProcessor()
                    .createPaymentOrder(idOfClient, idOfContragent, paymentMethod, copecksAmount, contragentSum);
        } catch (Exception e) {
            logger.error("Failed to add clientPaymentOrder", e);
            throw new ServletException(e);
        }
        //logger.info("idOfClientPaymentOrder: "+idOfClientPaymentOrder);



        String productPrice=recipientAmount;

        String sign=PRODUCT_ID_FOR_CHRONOPAY+"-"+productPrice+"-"+idOfClientPaymentOrder+"-"+chronopayConfig.getSharedSec();
         try{

        sign=getHash(sign);

         }catch(Exception e){logger.error(e.toString());
            throw e;
         }


        String paymenMethodCodeName = PaymentMethodConverter.getPaymentMethodCodeName(paymentMethod);
        URI confirmUri =new URI( chronopayConfig.getPurchaseUri());
        URI cancelUri;
        URI successUri;
        URI failUri;

        try {
            cancelUri = currentUri;

            successUri = UriUtils.putParam(fullSpecifiedlCurrentUri, STAGE_PARAM, ORDER_ACCEPTED_STAGE);
            successUri = UriUtils
                    .putParam(successUri, ID_OF_CLIENT_PAYMENT_ORDER_PARAM, idOfClientPaymentOrder.toString());

            failUri = UriUtils.putParam(fullSpecifiedlCurrentUri, STAGE_PARAM, ORDER_FAILED_STAGE);
            failUri = UriUtils.putParam(failUri, ID_OF_CLIENT_PAYMENT_ORDER_PARAM, idOfClientPaymentOrder.toString());







        } catch (Exception e) {
            throw new ServletException(e);
        }



%>
<table>
    <tr>
        <td colspan="2">
            <div class="output-text">Подтвердите параметры запроса на перечисление средств</div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Платежная система</div>
        </td>
        <td>
            <div class="output-text"><%=contragentName%></div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Сумма к зачислению (в рублях)</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(copecksAmount))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Сумма к оплате с учетом внутренней комиссии платежной системы (в рублях)</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(CurrencyStringUtils.copecksToRubles(contragentSum))%>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Способ оплаты</div>
        </td>
        <td>
            <div class="output-text"><%=StringEscapeUtils
                    .escapeHtml(ClientPayment.PAYMENT_METHOD_NAMES[paymentMethod])%>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table>
                <tr>
                    <td>
                        <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(confirmUri.toString()))%>"
                              method="post" enctype="application/x-www-form-urlencoded" class="borderless-form">
                            <input type="hidden" name="product_id"
                                   value="<%=StringEscapeUtils.escapeHtml(PRODUCT_ID_FOR_CHRONOPAY)%>" />
                            <input type="hidden" name="product_price"
                                   value="<%=StringEscapeUtils.escapeHtml(productPrice)%>" />

                            <input type="hidden" name="order_id"
                                   value="<%=StringEscapeUtils.escapeHtml(idOfClientPaymentOrder.toString())%>" />

                            <input type="hidden" name="cb_type" value="<%=StringEscapeUtils.escapeHtml("P")%>" />
                            <input type="hidden" name="cb_url" value="<%=StringEscapeUtils.escapeHtml(response.encodeURL(chronopayConfig.getCallbackUrl()))%>" />
                            <input type="hidden" name="success_url" value="<%=StringEscapeUtils.escapeHtml(response.encodeURL(successUri.toString()))%>" />
                            <input type="hidden" name="decline_url" value="<%=StringEscapeUtils.escapeHtml(response.encodeURL(failUri.toString()))%>" />
                            <input type="hidden" name="sign"
                                   value="<%=StringEscapeUtils.escapeHtml(sign)%>" />
                            <input class="command-button" type="submit"
                                   value="Оплатить через Chronopay" />
                        </form>
                    </td>
                    <td>
                        <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(cancelUri.toString()))%>"
                              method="post" enctype="application/x-www-form-urlencoded" class="borderless-form">
                            <input type="hidden" name="<%=ID_OF_CLIENT_PAYMENT_ORDER_PARAM%>"
                                   value="<%=idOfClientPaymentOrder%>" />
                            <input type="hidden" name="<%=STAGE_PARAM%>" value="<%=CANCEL_ORDER_STAGE%>" />
                            <input class="command-button" type="submit" value="Отменить" />
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

   <% }else{%>
<div class="output-text">Указанная система приема платежей не поддерживается.</div>

<%}}%>
<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>