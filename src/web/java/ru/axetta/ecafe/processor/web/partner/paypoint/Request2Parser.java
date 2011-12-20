/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest2;
import ru.axetta.ecafe.util.ParseUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:08:36
 * To change this template use File | Settings | File Templates.
 */
public class Request2Parser implements Parser.CustomMessageParser {

    public static final String CLIENT_ID_PARAM = Request1Parser.CLIENT_ID_PARAM;
    public static final String OPERATION_ID_PARAM = Request1Parser.OPERATION_ID_PARAM;
    public static final String TERMINAL_ID_PARAM = Request1Parser.TERMINAL_ID_PARAM;
    public static final String SUM_PARAM = "SUM";
    public static final String SUMF_PARAM = "SUMF";
    public static final String TIME_PARAM = "TIME";

    private final NumberFormat clientIdFormat;
    private final NumberFormat operationIdFormat;
    private final NumberFormat terminalIdFormat;
    private final NumberFormat sumFormat;
    private final DateFormat timeFormat;

    public Request2Parser() {
        this.clientIdFormat = new DecimalFormat("##################0");
        this.operationIdFormat = new DecimalFormat("##################0");
        this.terminalIdFormat = new DecimalFormat("##################0");
        this.sumFormat = new DecimalFormat("##################0");
        this.timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public PayPointRequest parse(
            Parser.PartialParseResult partialParseResult)
            throws Exception {
        List<MessageToken> messageTokens = partialParseResult.getMessageTokens();
        long clientId = Parser
                .parseRequiredLongParam(CLIENT_ID_PARAM, messageTokens, clientIdFormat);
        long operationId = Parser
                .parseRequiredLongParam(OPERATION_ID_PARAM, messageTokens, operationIdFormat);
        long terminalId = Parser
                .parseRequiredLongParam(TERMINAL_ID_PARAM, messageTokens, terminalIdFormat);
        long sum = Parser
                .parseRequiredLongParam(SUM_PARAM, messageTokens, sumFormat);
        long sumf = Parser
                .parseRequiredLongParam(SUMF_PARAM, messageTokens, sumFormat);
        Date time = parseTime(messageTokens);
        return new PayPointRequest2(partialParseResult.getRequestId(), clientId, operationId, terminalId, sum, sumf,
                time);
    }

    private Date parseTime(List<MessageToken> messageTokens) throws Exception {
        String text = Parser.getSingleParamValue(TIME_PARAM, messageTokens);
        if (null == text) {
            return null;
        }
        return ParseUtils.parseDateTime(this.timeFormat, text);
    }

}
