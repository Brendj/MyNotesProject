/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_kgd;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.axetta.ecafe.processor.core.logic.Processor.PaymentProcessResult.CLIENT_NOT_FOUND;
import static ru.axetta.ecafe.processor.core.logic.Processor.PaymentProcessResult.OK;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.09.12
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public class SBKGDOnlinePaymentRequestParser extends OnlinePaymentRequestParser {

    private Long code;
    private String message;
    private String date;
    private String action;

    private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest)
            throws Exception {
        ParseResult parseResult = getRequestParams();

        action = parseResult.getParam("action");
        Long sum = 0L;
        Long contractId = 1L;
        String receipt = "CHECK_ONLY";
        Boolean bCheckOnly = true;
        if(action.equals("check") || action.equals("payment")){
            String number = parseResult.getParam("number");
            contractId = Long.parseLong(number);
            Integer type = 0;
            if(!(parseResult.getParam("type")==null || parseResult.getParam("type").equals(""))){
                type = parseResult.getReqIntParam("type");
            }
            String amount = parseResult.getParam("amount");
            String replacedString = amount.replaceAll(",", ".");
            Double result = Double.parseDouble(replacedString);
            sum =  result.longValue() * 100;
        }
        if(action.equals("payment") || action.equals("status")){
            receipt = parseResult.getParam("receipt");
            bCheckOnly = false;
        }
        if(action.equals("status")){
            date = parseResult.getParam("date");
        }
        int paymentMethod= ClientPayment.ATM_PAYMENT_METHOD;
        String source = action +"/" + date;
        return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, bCheckOnly,
                defaultContragentId, null, paymentMethod, contractId,
                receipt, source, sum, false);
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        ParseResult parseResult = getRequestParams();
        String rsp = "";
        StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"windows-1251\"?><response>");
        int resultCode;
        switch ( response.getResultCode()){
            case 0: resultCode=0 ;break;
            case 105: resultCode = 2; break;
            case 100: resultCode = -3; break;
            default:resultCode = -2;
        }
        date = timeFormat.format(new Date());
        //if(action.equals("check") || action.equals("payment")){
        //    message = response.getResultDescription();
        //}
        //if(action.equals("status") || action.equals("payment")){
        //    stringBuilder.append(String.format("<date>%s</date>",date));
        //}
        stringBuilder.append(String.format("<code>%d</code>",resultCode));
        if(response.getPaymentId()!=null) stringBuilder.append(String.format("<authcode>%s</authcode>",response.getPaymentId()));
        stringBuilder.append(String.format("<date>%s</date>",date));
        if(message!=null) stringBuilder.append(String.format("<message>%s</message>",message));
        stringBuilder.append("</response>");
        rsp = stringBuilder.toString();
        printToStream(rsp, httpResponse);
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        //return parsePostedUrlEncodedParams(httpRequest);
        return  parseGetParams(httpRequest);
    }

}
