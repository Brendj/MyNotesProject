/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SBMSKOnlinePaymentRequestParser extends OnlinePaymentRequestParser {
    public static final String ACTION_CHECK = "check";
    public static final String ACTION_PAYMENT = "payment";
    public static final String ACTION_SUMMARY = "summary";
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
        } else if (action.equals(ACTION_SUMMARY)) {
            return parseForSummary(defaultContragentId, parseResult);
        }
        return null;
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
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
            stringBuilder.append(String.format("<FIO>%s</FIO>", response.processFio()));
            stringBuilder.append(String.format("<INN>%s</INN>", response.getInn()));
            stringBuilder.append(String.format("<NAZN>%s</NAZN>", response.getNazn()));
            stringBuilder.append(String.format("<BIC>%s</BIC>", response.getBic()));
            stringBuilder.append(String.format("<RASCH>%s</RASCH>", response.getRasch()));
            if (response.getBalance() != null) {
                BigDecimal balance = new BigDecimal(response.getBalance() / 100.0); //  rounding error if use double
                stringBuilder.append(String.format(Locale.US, "<BALANCE>%.2f</BALANCE>", balance));
            }
        } else if (action.equals(ACTION_PAYMENT) && response.getIdOfClientPayment() != null) {
            stringBuilder.append(String.format("<EXT_ID>%s</EXT_ID>", response.getIdOfClientPayment()));
        } else if (action.equals(ACTION_SUMMARY)){
            SBMSKSummaryResponse summaryResponse = (SBMSKSummaryResponse) response;
            for(SBMSKClientSummaryBase c : summaryResponse.getClientList()){
                stringBuilder.append("<clientSummary>");
                stringBuilder.append(String.format("<CONTRACTID>%d</CONTRACTID>", c.getContractId()));
                stringBuilder.append(String.format("<BALANCE>%s</BALANCE>", c.getBalance()));
                stringBuilder.append(String.format("<FIO>%s</FIO>", c.getFio()));
                stringBuilder.append(String.format("<NAZN>%s</NAZN>", c.getNazn()));
                stringBuilder.append(String.format("<INN>%s</INN>", c.getInn()));
                stringBuilder.append("</clientSummary>");
            }
        }
        stringBuilder.append("</response>");
        printToStream(stringBuilder.toString(), httpResponse);
    }

    protected void printToStream(String s, HttpServletResponse httpResponse) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteBuffer byteBuffer = Charset.forName("windows-1251").encode(s);
        byte[] tmp = byteBuffer.array();
        outputStream.write(tmp, 0, byteBuffer.limit());
        outputStream.writeTo(httpResponse.getOutputStream());
    }

    private void addSBMSKInfoToResponse(OnlinePaymentProcessor.PayResponse response) {
        try {
            Contragent contragent = DAOReadExternalsService.getInstance().findContragentByClient(response.getClientId());
            response.setInn(getValueNullSafe(contragent.getInn()));
            response.setNazn(getValueNullSafe(contragent.getContragentName()));
            response.setBic(getValueNullSafe(contragent.getBic()));
            response.setRasch(getValueNullSafe(contragent.getAccount()));
        } catch (Exception ignore) {}
    }

    private String getValueNullSafe(String value) {
        return value == null ? "" : value.trim();
    }

    public void serializeResponseIfException(HttpServletResponse httpResponse, SBMSKPaymentsCodes error)
    throws Exception {
        StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"windows-1251\"?><response>");

        int resultCode = error.getCode();
        stringBuilder.append(String.format("<CODE>%d</CODE>",resultCode));
        stringBuilder.append(String.format("<MESSAGE>%s</MESSAGE>",error.toString()));

        stringBuilder.append("</response>");
        printToStream(stringBuilder.toString(), httpResponse);
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return  parseGetParams(httpRequest);
    }

    private OnlinePaymentProcessor.PayRequest parseForCheckAccount(long defaultContragentId, ParseResult parseResult) throws Exception {
        String account = parseResult.getParam("ACCOUNT");
        Long contractId;
        Long sum = 0L;
        String receipt = "CHECK_ONLY";
        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;
        String source = action;
        try {
            contractId = Long.parseLong(account);
        } catch (Exception e) {
            throw new InvalidContractIdFormatException("Invalid contractId format");
        }
        return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true,
                defaultContragentId, null, paymentMethod, contractId, receipt, source, sum, false);
    }

    private OnlinePaymentProcessor.PayRequest parseForPayment(long defaultContragentId, ParseResult parseResult) throws Exception{
        String account = parseResult.getParam("ACCOUNT");
        Long contractId, sum = 0L;
        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;
        String source = action;
        try {
            contractId = Long.parseLong(account);
        } catch (Exception e) {
            throw new InvalidContractIdFormatException("Invalid contractId format");
        }
        String amount = parseResult.getParam("AMOUNT");
        if(!amount.matches("\\d+\\.\\d{2}")){
            throw new InvalidPaymentSumException("Invalid format of amount");
        }
        String paymentId = parseResult.getParam("INFO");
        if (paymentId == null) paymentId = parseResult.getParam("PAY_ID");
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

    private OnlinePaymentProcessor.PayRequest parseForSummary(long defaultContragentId, ParseResult parseResult) throws Exception {
        String mobile = parseResult.getParam("ACCOUNT");
        mobile = Client.checkAndConvertMobile(mobile);
        if (StringUtils.isEmpty(mobile)) {
            throw new InvalidMobileException("Invalid mobile number");
        }
        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;
        Long contractId = 0L;
        Long sum = 0L;
        String receipt = "CHECK_ONLY";
        String source = action;
        return new SBMSKSummaryRequest(OnlinePaymentProcessor.PayRequest.V_0,
                defaultContragentId, null, paymentMethod, contractId, receipt, source, sum, false, mobile);
    }
}
