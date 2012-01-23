/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse1;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:11:41
 * To change this template use File | Settings | File Templates.
 */
public class Response1Serializer implements Serializer.CustomMessageSerializer {

    public static final String CLIENT_ID_PARAM = Request1Parser.CLIENT_ID_PARAM;
    public static final String OPERATION_ID_PARAM = Request1Parser.OPERATION_ID_PARAM;
    public static final String BALANCE_PARAM = "BAL";
    public static final String CLIENT_ABBREVIATION_PARAM = "CLIENTFIO";
    public static final String CLIENT_ADDRESS_PARAM = "CLIENTADDR";
    public static final String CARD_ID_PARAM = "CARDID";

    private final NumberFormat clientIdFormat;
    private final NumberFormat operationIdFormat;
    private final NumberFormat balanceFormat;
    private final NumberFormat cardPrintedNoFormat;

    public Response1Serializer() {
        this.clientIdFormat = new DecimalFormat("##################0");
        this.operationIdFormat = new DecimalFormat("##################0");
        this.balanceFormat = new DecimalFormat("##################0");
        this.cardPrintedNoFormat = new DecimalFormat("##################0");
    }

    public List<MessageToken> serialize(PayPointResponse response) throws Exception {
        PayPointResponse1 response1 = (PayPointResponse1) response;
        List<MessageToken> result = new ArrayList<MessageToken>(3);
        result.add(new MessageToken(CLIENT_ID_PARAM, clientIdFormat.format(response1.getClientId())));
        result.add(new MessageToken(OPERATION_ID_PARAM, operationIdFormat.format(response1.getOperationId())));
        Long balance = response1.getBalance();
        if (null != balance) {
            result.add(new MessageToken(BALANCE_PARAM, balanceFormat.format(balance)));
        }
        addToken(result, CLIENT_ABBREVIATION_PARAM, response1.getClientNameAbbreviation());
        addToken(result, CLIENT_ADDRESS_PARAM, response1.getClientAddress());
        Long cardPrintedNo = response1.getCardPrintedNo();
        addToken(result, CARD_ID_PARAM, cardPrintedNo == null ? "" : cardPrintedNoFormat.format(cardPrintedNo));
        return result;
    }

    private static void addToken(List<MessageToken> tokens, String paramName, String paramValue) throws Exception {
        if (null != paramValue) {
            tokens.add(new MessageToken(paramName, paramValue));
        }
    }
}
