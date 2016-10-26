/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_kgd;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
        /* default value */
        Long sum = 0L;
        Long contractId = 1L;
        String receipt = "CHECK_ONLY";
        Boolean bCheckOnly = true;
        int paymentMethod= ClientPayment.ATM_PAYMENT_METHOD;
        String source = action;
        defaultContragentId = linkConfig.idOfContragent;
        if(action.equals("check")){
            String number = parseResult.getParam("number");
            try {
                contractId = Long.parseLong(number);
            } catch (Exception e) {
                contractId = -1L;
            }
            String amount = parseResult.getParam("amount");
            String replacedString = amount.replaceAll(",", ".");
            Double result = Double.parseDouble(replacedString);
            sum =  result.longValue() * 100;
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true ,defaultContragentId,
                    null, paymentMethod, contractId,
                    receipt, source, sum, false);
        }
        if(action.equals("payment")){
            String number = parseResult.getParam("number");
            try {
                contractId = Long.parseLong(number);
            } catch (Exception e) {
                contractId = -1L;
            }
            String amount = parseResult.getParam("amount");
            String replacedString = amount.replaceAll(",", ".");
            Double result = Double.parseDouble(replacedString);
            sum =  result.longValue() * 100;
            receipt = parseResult.getParam("receipt");
            date = java.net.URLDecoder.decode(parseResult.getParam("date"), "UTF-8");
            source = source + "/" + date;
            final OnlinePaymentProcessor.PayRequest payRequest = new OnlinePaymentProcessor.PayRequest(
                    OnlinePaymentProcessor.PayRequest.V_0, false, defaultContragentId, null, paymentMethod, contractId,
                    receipt, source, sum, false);
            payRequest.setPayDate(timeFormat.parse(date));
            return payRequest;
        }
        if(action.equals("status")){
            receipt = parseResult.getParam("receipt");
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true ,defaultContragentId,
                    null, paymentMethod, contractId,
                    receipt, source, sum, false);
        }
        return null;
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        ParseResult parseResult = getRequestParams();
        String rsp = "";
        StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"windows-1251\"?><response>");
        int resultCode = response.getResultCode();
        message = response.getResultDescription();
        switch ( response.getResultCode()){
            case 0: {
                resultCode=0 ;
                message = action.equals("payment")?"Платеж зачислен.":"Абонент найден.";
                 }  break;
            case 140: resultCode = 140; message = "Неизвестный тип запроса. Оплата уже зарегистрирована."; break;
            case 105: resultCode = 2; message="Абонент не найден." ; break;
        }
        stringBuilder.append(String.format("<code>%d</code>",resultCode));
        if(response.getPaymentId()!=null) stringBuilder.append(String.format("<authcode>%s</authcode>",response.getPaymentId()));
        if(action.equals("payment") || action.equals("status")){
            date = timeFormat.format(new Date());
            stringBuilder.append(String.format("<date>%s</date>",date));
        }
        if(message!=null){
            stringBuilder.append(String.format("<message>%s</message>",message));
        }
        HashMap<String, String> addInfo = response.getAddInfo();
        if(!(addInfo==null || addInfo.isEmpty())){
            stringBuilder.append("<add>");
            for (String key: addInfo.keySet()){
                stringBuilder.append(String.format("%s : %s:",key,addInfo.get(key)));
            }
            stringBuilder.append("</add>");
        }
        stringBuilder.append("</response>");
        rsp = stringBuilder.toString();
        printToStream(rsp, httpResponse);
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        //return parsePostedUrlEncodedParams(httpRequest);
        return  parseGetParams(httpRequest);
    }

    private StdPayConfig.LinkConfig linkConfig;
    public void setLinkConfig(StdPayConfig.LinkConfig linkConfig) {
        this.linkConfig = linkConfig;
    }


}
