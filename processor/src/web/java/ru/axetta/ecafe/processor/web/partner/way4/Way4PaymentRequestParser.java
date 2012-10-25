/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.way4;


import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
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
                infoSection=String.format("<AccountInfo><RRN>%s</RRN><Account>%d</Account><Currency>RUR</Currency><Phone>%d</Phone><Info2>%s</Info2></AccountInfo>",
                        rrn, response.getClientId(), response.getClientId(), "PARAM3="+ Base64.encodeBytes(response.getClientFullName().getBytes("UTF-8"))+";PARAM4="+
                                Base64.encodeBytes(CurrencyStringUtils.copecksToRubles(response.getBalance()).getBytes("UTF-8"))+";");
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


