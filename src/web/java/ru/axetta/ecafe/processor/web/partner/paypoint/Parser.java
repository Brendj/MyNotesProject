/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointRequest;
import ru.axetta.ecafe.util.ParseUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:03:38
 * To change this template use File | Settings | File Templates.
 */
public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static class PartialParseResult {

        private final int requestId;
        private final List<MessageToken> messageTokens;

        public PartialParseResult(int requestId, List<MessageToken> messageTokens) {
            this.requestId = requestId;
            this.messageTokens = messageTokens;
        }

        public int getRequestId() {
            return requestId;
        }

        public List<MessageToken> getMessageTokens() {
            return messageTokens;
        }
    }

    public static interface CustomMessageParser {

        PayPointRequest parse(PartialParseResult partialParseResult) throws Exception;

    }

    private static final String REQUEST_PARAM_NAME = "inputmessage";
    public static final String CHARSET_NAME = "windows-1251";
    public static final String PARAM_VALUE_DELIMITER = "=";
    public static final String PARAM_DELIMITER = "\r\n";
    public static final String REQUEST_ID_PARAM = "REQID";

    private final NumberFormat requestIdFormat;
    private final Map<Integer, CustomMessageParser> customMessageParsers;

    public Parser(Map<Integer, CustomMessageParser> messageParsers) throws Exception {
        this.requestIdFormat = new DecimalFormat("##################0");
        this.customMessageParsers = messageParsers;
    }

    public PayPointRequest parse(HttpServletRequest httpRequest) throws Exception {
        final String requestText = httpRequest.getParameter(REQUEST_PARAM_NAME);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got request text: %s", requestText));
        }
        PartialParseResult partialParseResult = parsePartial(requestText);
        CustomMessageParser customMessageParser = this.customMessageParsers.get(partialParseResult.getRequestId());
        if (null == customMessageParser) {
            throw new IllegalArgumentException(String.format("Unknown request: %s", requestText));
        }
        final PayPointRequest request = customMessageParser.parse(partialParseResult);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Text \"%s\" parsed: %s", requestText, request.toString()));
        }
        return request;
    }

    public static int parseRequiredIntParam(String param, List<MessageToken> messageTokens, NumberFormat format)
            throws Exception {
        String textValue = getSingleParamValue(param, messageTokens);
        if (null == textValue) {
            throw new IllegalArgumentException(String.format("Required parameter not found: %s", param));
        }
        return ParseUtils.parseInt(format, textValue);
    }

    public static long parseRequiredLongParam(String param, List<MessageToken> messageTokens, NumberFormat format)
            throws Exception {
        String textValue = getSingleParamValue(param, messageTokens);
        if (null == textValue) {
            throw new IllegalArgumentException(String.format("Required parameter not found: %s", param));
        }
        return ParseUtils.parseLong(format, textValue);
    }

    private PartialParseResult parsePartial(String text) throws Exception {
        List<MessageToken> messageTokens = parseMessageTokens(text);
        int requestId = parseRequiredIntParam(REQUEST_ID_PARAM, messageTokens, requestIdFormat);
        return new PartialParseResult(requestId, messageTokens);
    }

    private List<MessageToken> parseMessageTokens(String text) throws Exception {
        String[] tokenLines = StringUtils.splitByWholeSeparator(StringUtils.defaultString(text), PARAM_DELIMITER);
        List<MessageToken> tokens = new ArrayList<MessageToken>(tokenLines.length);
        for (String line : tokenLines) {
            int valueDelimiter = StringUtils.indexOf(line, PARAM_VALUE_DELIMITER);
            if (valueDelimiter > 0) {
                tokens.add(new MessageToken(StringUtils.substring(line, 0, valueDelimiter),
                        StringUtils.substring(line, valueDelimiter + PARAM_VALUE_DELIMITER.length())));
            } else {
                tokens.add(new MessageToken(line, null));
            }
        }
        return tokens;
    }

    public static String getSingleParamValue(String param, List<MessageToken> messageTokens) {
        String result = null;
        for (MessageToken token : messageTokens) {
            if (StringUtils.equals(token.getParam(), param)) {
                if (result != null) {
                    throw new IllegalArgumentException(
                            String.format("Param \"%s\" has more then one occurrences with values \"%s\" and \"%s\"",
                                    param, result, token.getValue()));
                }
                result = token.getValue();
            }
        }
        return result;
    }
}
