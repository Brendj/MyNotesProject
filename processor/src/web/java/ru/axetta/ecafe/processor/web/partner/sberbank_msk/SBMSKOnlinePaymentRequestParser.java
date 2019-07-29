/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SBMSKOnlinePaymentRequestParser extends OnlinePaymentRequestParser {
    public static final String ACTION_CHECK = "check";
    public static final String ACTION_PAYMENT = "payment";
    private String date;
    private String action;

    private DateFormat timeFormat;

    public SBMSKOnlinePaymentRequestParser(){
        super();
        timeFormat = new SimpleDateFormat("dd.MM.yyyy'_'HH:mm:ss");
        timeFormat.setLenient(false);
    }

    @Override
    protected String getQueryString(HttpServletRequest httpRequest){
        return super.getQueryString(httpRequest);
    }

    @Override
    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest)
            throws Exception {
        ParseResult parseResult = getRequestParams();
        action = parseResult.getParam("ACTION");
        if(action.equals(ACTION_CHECK)){
            return parseForCheckAccount(defaultContragentId, parseResult);
        }
        else if(action.equals(ACTION_PAYMENT)){
            return parseForPayment(defaultContragentId, parseResult);
        }
        return null;
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        String rsp = "";
        StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"windows-1251\"?><response>");

        if(action.equals(ACTION_PAYMENT)){
            date = timeFormat.format(new Date());
            stringBuilder.append(String.format("<REG_DATE>%s</REG_DATE>", date));
        }
        SBMSKPaymentsCodes result = SBMSKPaymentsCodes.getFromPaymentProcessResultCode(response.getResultCode());

        int resultCode = result.getCode();
        stringBuilder.append(String.format("<CODE>%d</CODE>",resultCode));
        stringBuilder.append(String.format("<MESSAGE>%s</MESSAGE>",result.toString()));

        if(action.equals(ACTION_CHECK)) {
            stringBuilder.append(String.format("<INN>%s</INN>", response.getInn()));
            stringBuilder.append(String.format("<NAZN>%s</NAZN>", response.getNazn()));
            stringBuilder.append(String.format("<BIC>%s</BIC>", response.getBic()));
            stringBuilder.append(String.format("<RASCH>%s</RASCH>", response.getRasch()));
            if (response.getBalance() != null) {
                BigDecimal balance = new BigDecimal(response.getBalance() / 100.0); //  rounding error if use double
                stringBuilder.append(String.format(Locale.US, "<BALANCE>%.2f</BALANCE>", balance));
            }
        }

        stringBuilder.append("</response>");
        rsp = new String(stringBuilder.toString().getBytes("UTF-8"), "windows-1251");
        printToStream(rsp, httpResponse);
    }

    public void serializeResponseIfException(HttpServletResponse httpResponse, SBMSKPaymentsCodes error)
    throws Exception {
        String rsp = "";
        StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"windows-1251\"?><response>");

        String message = new String(error.toString().getBytes("UTF-8"), "windows-1251"); // output windows-1251 as UTF-8
        int resultCode = error.getCode();
        stringBuilder.append(String.format("<CODE>%d</CODE>",resultCode));
        stringBuilder.append(String.format("<MESSAGE>%s</MESSAGE>",message));

        stringBuilder.append("</response>");
        rsp = stringBuilder.toString();
        printToStream(rsp, httpResponse);
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return  parseGetParams(httpRequest);
    }

    private OnlinePaymentProcessor.PayRequest parseForCheckAccount(long defaultContragentId, ParseResult parseResult) throws Exception{
        String account = parseResult.getParam("ACCOUNT");
        Long contractId;
        Long sum = 0L;
        String receipt = "CHECK_ONLY";
        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;
        String source = action;
        try {
            contractId = Long.parseLong(account);
        } catch (Exception e) {
            contractId = -1L;
        }
        return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true ,defaultContragentId,
                null, paymentMethod, contractId,
                receipt, source, sum, false);
    }

    private OnlinePaymentProcessor.PayRequest parseForPayment(long defaultContragentId, ParseResult parseResult) throws Exception{
        String account = parseResult.getParam("ACCOUNT");
        Long contractId, sum = 0L;
        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;
        String source = action;
        try {
            contractId = Long.parseLong(account);
        } catch (Exception e) {
            contractId = -1L;
        }
        String amount = parseResult.getParam("AMOUNT");
        if(!amount.matches("\\d+\\.\\d{2}")){
            throw new InvalidPaymentSumException("Invalid format of amount");
        }
        String paymentId = parseResult.getParam("PAY_ID");
        if(!paymentId.matches("\\d*")){
            throw new InvalidPayIdException("PAY_ID contain non num charters");
        }
        sum = Long.parseLong(amount.replace(".", ""));
        if(sum <= 0){
            throw new InvalidPaymentSumException("Amount equal or less zero");
        }
        date = URLDecoder.decode(parseResult.getParam("PAY_DATE"), "UTF-8");
        source = source + "/" + date;
        final OnlinePaymentProcessor.PayRequest payRequest = new OnlinePaymentProcessor.PayRequest(
                OnlinePaymentProcessor.PayRequest.V_0, false, defaultContragentId, null, paymentMethod, contractId,
                paymentId , source, sum, false);
        try {
            payRequest.setPayDate(timeFormat.parse(date));
        }catch (Exception e){
            throw new InvalidDateException("Invalid value in PAY_DATE: " + date);
        }
        return payRequest;
    }
}
