/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest3;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:11:09
 * To change this template use File | Settings | File Templates.
 */
public class Request3Parser implements ru.axetta.ecafe.processor.web.partner.paypoint.Parser.CustomMessageParser {

    public static final String OPERATION_ID_PARAM = Request2Parser.OPERATION_ID_PARAM;

    private final NumberFormat operationIdFormat;

    public Request3Parser() {
        this.operationIdFormat = new DecimalFormat("##################0");
    }

    public PayPointRequest parse(
            ru.axetta.ecafe.processor.web.partner.paypoint.Parser.PartialParseResult partialParseResult)
            throws Exception {
        List<MessageToken> messageTokens = partialParseResult.getMessageTokens();
        long operationId = ru.axetta.ecafe.processor.web.partner.paypoint.Parser
                .parseRequiredLongParam(OPERATION_ID_PARAM, messageTokens, operationIdFormat);
        return new PayPointRequest3(partialParseResult.getRequestId(), operationId);
    }

}
