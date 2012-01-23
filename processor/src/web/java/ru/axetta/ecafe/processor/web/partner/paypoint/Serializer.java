/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:01:50
 * To change this template use File | Settings | File Templates.
 */
public class Serializer {

    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

    public static interface CustomMessageSerializer {

        List<MessageToken> serialize(PayPointResponse response) throws Exception;
    }

    private static final String CHARSET_NAME = Parser.CHARSET_NAME;
    private static final String PARAM_VALUE_DELIMITER = Parser.PARAM_VALUE_DELIMITER;
    private static final String PARAM_DELIMITER = Parser.PARAM_DELIMITER;
    private static final String REQUEST_ID_PARAM = Parser.REQUEST_ID_PARAM;
    private static final String RESULT_CODE_PARAM = "RES";
    private static final String RESULT_DESCRIPTION_PARAM = "DESC";

    private final NumberFormat requestIdFormat;
    private final NumberFormat resultCodeFormat;
    private final Map<Integer, CustomMessageSerializer> customMessageSerializers;

    public Serializer(Map<Integer, CustomMessageSerializer> messageSerializers) throws Exception {
        this.requestIdFormat = new DecimalFormat();
        this.resultCodeFormat = new DecimalFormat();
        this.customMessageSerializers = messageSerializers;
    }

    public void serialize(PayPointResponse response, HttpServletResponse httpResponse) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream, true, CHARSET_NAME);
        try {
            printStream.print(buildText(response));
        } catch (Exception e) {
            logger.error("Failed to serialize response", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        printStream.close();
        httpResponse.setCharacterEncoding(CHARSET_NAME);
        httpResponse.setContentLength(byteStream.size());
        byteStream.writeTo(httpResponse.getOutputStream());
    }

    private String buildText(PayPointResponse response) throws Exception {
        List<MessageToken> messageTokens = buildMessageTokens(response);
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (MessageToken token : messageTokens) {
            if (!first) {
                stringBuilder.append(PARAM_DELIMITER);
            }
            stringBuilder.append(token.getParam()).append(PARAM_VALUE_DELIMITER).append(token.getValue());
            first = false;
        }
        String result = stringBuilder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("PayPointResponse (%s) serialized: %s", response.toString(), result));
        }
        return result;
    }

    private List<MessageToken> buildMessageTokens(PayPointResponse response) throws Exception {
        List<MessageToken> messageTokens = new ArrayList<MessageToken>(10);
        messageTokens.add(new MessageToken(REQUEST_ID_PARAM, requestIdFormat.format(response.getRequestId())));
        messageTokens.add(new MessageToken(RESULT_CODE_PARAM, resultCodeFormat.format(response.getResultCode())));
        CustomMessageSerializer messageSerializer = customMessageSerializers.get(response.getRequestId());
        if (messageSerializer == null) {
            throw new IllegalArgumentException(String.format("Unknown response: %s", response));
        }
        messageTokens.addAll(messageSerializer.serialize(response));
        messageTokens.add(new MessageToken(RESULT_DESCRIPTION_PARAM, response.getResultDescription()));
        return messageTokens;
    }

}
