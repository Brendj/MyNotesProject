/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse3;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:12:57
 * To change this template use File | Settings | File Templates.
 */
public class Response3Serializer implements Serializer.CustomMessageSerializer {

    public static final String OPERATION_ID_PARAM = Request3Parser.OPERATION_ID_PARAM;
    public static final String SUM_PARAM = Request2Parser.SUM_PARAM;

    private final NumberFormat operationIdFormat;
    private final NumberFormat sumFormat;

    public Response3Serializer() {
        this.operationIdFormat = new DecimalFormat("##################0");
        this.sumFormat = new DecimalFormat("##################0");
    }

    public List<MessageToken> serialize(PayPointResponse response) throws Exception {
        PayPointResponse3 response3 = (PayPointResponse3) response;
        List<MessageToken> result = new ArrayList<MessageToken>(1);
        result.add(new MessageToken(OPERATION_ID_PARAM, operationIdFormat.format(response3.getOperationId())));
        Long sum = response3.getSum();
        if (sum != null) {
            result.add(new MessageToken(SUM_PARAM, sumFormat.format(sum)));
        }
        return result;
    }
}
