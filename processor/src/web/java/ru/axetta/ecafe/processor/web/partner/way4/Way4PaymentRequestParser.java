/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.way4;


import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Way4PaymentRequestParser extends OnlinePaymentRequestParser {
    final static String TERMID_PREFIX_INTERNET_ACQUIRING="2";
    
    boolean bPayRequest;
    String rrn;

    @Override
    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest)
            throws Exception {
        ParseResult parseResult = getRequestParams();

        String function = parseResult.getParam("function");
        String opId = rrn = parseResult.getParam("RRN");
        long clientId = parseResult.getReqLongParam("PHONE");
        long sum=CurrencyStringUtils.rublesToCopecks(parseResult.getReqParam("AMOUNT"));
        String currency = parseResult.getParam("CURRENCY");
        if (!currency.equals("RUR") && !currency.equals("RUB")) throw new Exception("Invalid currency: "+currency);
        String termId = parseResult.getParam("TERMINAL");
        int paymentMethod=ClientPayment.ATM_PAYMENT_METHOD;
        if (termId!=null && termId.startsWith(TERMID_PREFIX_INTERNET_ACQUIRING)) {
            paymentMethod=ClientPayment.INTERNET_ACQUIRING_METHOD;
        }
        //linkConfig = new StdPayConfig.LinkConfig();
        //linkConfig.name = "Billing Gateway";
        //linkConfig.remoteAddressMask = ".*";
        //linkConfig.idOfContragent = 72;
        //linkConfig.authType= 0;
        if (function.equals("bank_account")) {
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true,
                    linkConfig.idOfContragent, null, paymentMethod, clientId,
                    ""+opId, null, 0L, false);

        } else if (function.equals("bank_payment")) {
            bPayRequest = true;
            String date=parseResult.getReqParam("DATE")+parseResult.getReqParam("TIME");
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, false,
                    linkConfig.idOfContragent, null, paymentMethod, clientId,
                    opId, date+"/"+termId+"/"+opId, sum, false);
        } else {
            throw new Exception("Invalid function requested: "+function);
        }
    }

    StdPayConfig.LinkConfig linkConfig;
    public void setLinkConfig(StdPayConfig.LinkConfig linkConfig) {
        this.linkConfig = linkConfig;
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        ParseResult parseResult = getRequestParams();
        String stan = parseResult.getParam("STAN");

        String rspCode = translateResultCode(response.getResultCode());
        String infoSection="";
        if (!bPayRequest) {
            if (response.getResultCode()==Processor.PaymentProcessResult.OK.getCode()) {

                Boolean enableSubscriptionFeeding = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
                if(enableSubscriptionFeeding){
                    /*
                      STRRUS1 = ФИО: Петров Петр Иванович
                      STRRUS2 = Баланс 00200485:      PARAM1 =<баланс>
                      STRRUS4 = Баланс 0020048501:     PARAM3 =<баланс АП>|
                      STRRUS3=  (абонемент на питание)
                    * */
                    final Long subBalance1 = response.getSubBalance1()==null?0:response.getSubBalance1();
                    final String fio = "ФИО: "+response.getClientFullName();
                    final String strrus1 = Base64.encodeBytes(fio.getBytes("UTF-8"));

                    final String contractIdStr = String.format("Баланс: %s:", String.valueOf(response.getClientId()));
                    final String strrus2 = Base64.encodeBytes(contractIdStr.getBytes("UTF-8"));

                    final long subBalanceNumber = response.getClientId() * 100 + 1;
                    final String subBalance1Str = String.format("Субсчет: %s:", String.valueOf(subBalanceNumber));
                    //final String strrus3 = Base64.encodeBytes(String.valueOf("(абонемент на питание)").getBytes("UTF-8"));
                    final String strrus3 = Base64.encodeBytes(String.valueOf("Абонемент на питание:").getBytes("UTF-8"));
                    final String strrus4 = Base64.encodeBytes(subBalance1Str.getBytes());

                    final String fullBalance = Base64.encodeBytes(CurrencyStringUtils.copecksToRubles(response.getBalance()).getBytes("UTF-8"));
                    //final String info="STRRUS1="+strrus1+";STRRUS2="+strrus2+";"+"STRRUS3="+strrus3+";STRRUS4="+strrus4+";";
                    final String info="STRRUS1="+strrus3+";";
                    final String fullSubBalance1 = Base64.encodeBytes(CurrencyStringUtils.copecksToRubles(subBalance1).getBytes("UTF-8"));

                    //final String info2 = String.format("PARAM1=%s;PARAM3=%s;", fullBalance, fullSubBalance1);
                    final String info2 = String.format("PARAM3=%s;PARAM4=%s;", Base64.encodeBytes(response.getClientFullName().getBytes("UTF-8")), fullSubBalance1);

                    infoSection=String.format("<AccountInfo><RRN>%s</RRN><Account>%d</Account><Currency>RUR</Currency><Phone>%d</Phone><Info>%s</Info><Info2>%s</Info2></AccountInfo>",
                            rrn,
                            response.getClientId(),
                            response.getClientId(), info, info2);
                } else {
                    infoSection=String.format("<AccountInfo><RRN>%s</RRN><Account>%d</Account><Currency>RUR</Currency><Phone>%d</Phone><Info2>%s</Info2></AccountInfo>",
                            rrn, response.getClientId(), response.getClientId(), "PARAM3="+ Base64.encodeBytes(response.getClientFullName().getBytes("UTF-8"))+";PARAM4="+
                            Base64.encodeBytes(CurrencyStringUtils.copecksToRubles(response.getBalance()).getBytes("UTF-8"))+";");
                }
            }
        } else {

            if (response.getResultCode()==Processor.PaymentProcessResult.OK.getCode()) {
                infoSection=String.format("<Payment><RRN>%s</RRN><Date>%s</Date><Time>%s</Time><Phone>%d</Phone><Account>%d</Account><Amount>%s</Amount><Currency>RUR</Currency></Payment>",
                        rrn, parseResult.getParam("DATE"), parseResult.getParam("TIME"), response.getClientId(),
                        response.getClientId(), parseResult.getParam("AMOUNT"));
            }
        }
        String rsp = String.format("<XML><mBilling Version=\"1.0\"><STAN>%s</STAN><Response>%s</Response>%s</mBilling></XML>", stan, rspCode, infoSection);
        printToStream(rsp, httpResponse);
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return parsePostedUrlEncodedParams(httpRequest);
    }

    private String translateResultCode(int resultCode) {
        if (resultCode== Processor.PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode()) return "ERROR";
        if (resultCode== Processor.PaymentProcessResult.CLIENT_NOT_FOUND.getCode()) return "ERR_PHONE";
        if (resultCode==Processor.PaymentProcessResult.CARD_NOT_FOUND.getCode()) return "ERR_PHONE";
        if (resultCode==Processor.PaymentProcessResult.OK.getCode()) return "OK";
        return "ERROR";
    }

}


