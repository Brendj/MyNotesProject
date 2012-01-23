/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest1;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:06:36
 * To change this template use File | Settings | File Templates.
 */
public class Request1Parser implements Parser.CustomMessageParser {

    public static final String CLIENT_ID_PARAM = "CLIENTID";
    public static final String OPERATION_ID_PARAM = "OPID";
    public static final String TERMINAL_ID_PARAM = "TERMID";

    private final NumberFormat clientIdFormat;
    private final NumberFormat operationIdFormat;
    private final NumberFormat terminalIdFormat;

    public Request1Parser() {
        this.clientIdFormat = new DecimalFormat("##################0");
        this.operationIdFormat = new DecimalFormat("##################0");
        this.terminalIdFormat = new DecimalFormat("##################0");
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
        return new PayPointRequest1(partialParseResult.getRequestId(), clientId, operationId, terminalId);
    }

}
