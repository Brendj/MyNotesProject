/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:12:21
 * To change this template use File | Settings | File Templates.
 */
public class Response2Serializer implements Serializer.CustomMessageSerializer {

    public static final String OPERATION_ID_PARAM = Request2Parser.OPERATION_ID_PARAM;
    public static final String CARD_ID_PARAM = "CARDID";

    private final NumberFormat operationIdFormat;
    private final NumberFormat cardPrintedNoFormat;

    public Response2Serializer() {
        this.operationIdFormat = new DecimalFormat("##################0");
        this.cardPrintedNoFormat = new DecimalFormat("##################0");
    }

    public List<MessageToken> serialize(PayPointResponse response) throws Exception {
        PayPointResponse2 response2 = (PayPointResponse2) response;
        List<MessageToken> result = new ArrayList<MessageToken>(1);
        addToken(result, OPERATION_ID_PARAM, operationIdFormat.format(response2.getOperationId()));
        Long cardPrintedNo = response2.getCardPrintedNo();
        addToken(result, CARD_ID_PARAM, cardPrintedNo == null ? "" : cardPrintedNoFormat.format(cardPrintedNo));
        return result;
    }

    private static void addToken(List<MessageToken> tokens, String paramName, String paramValue) throws Exception {
        if (null != paramValue) {
            tokens.add(new MessageToken(paramName, paramValue));
        }
    }
}
