/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.paypoint.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 16:02:24
 * To change this template use File | Settings | File Templates.
 */
public class PayPointServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PayPointServlet.class);

    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            try {
                if (!authenticate(httpRequest)) {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (Exception e) {
                logger.error("Failed to authenticate request", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            Parser requestParser;
            try {
                requestParser = createRequestParser();
            } catch (Exception e) {
                logger.error("Failed to initialize request parser", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            PayPointRequest request;
            try {
                request = requestParser.parse(httpRequest);
            } catch (Exception e) {
                logger.error("Failed to parse request", e);
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Got request: %s", request.toString()));
            }
            PayPointResponse response;
            try {
                response = process(runtimeContext, request);
            } catch (InvalidRequestException e) {
                logger.error("Failed to parse request", e);
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            } catch (Exception e) {
                logger.error("Failed to process request", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(
                        String.format("PayPointRequest (%s) processed: %s", request.toString(), response.toString()));
            }
            Serializer responseSerializer;
            try {
                responseSerializer = createResponseSerializer();
            } catch (Exception e) {
                logger.error("Failed to initialize response serializer", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            responseSerializer.serialize(response, httpResponse);
        } catch (RuntimeContext.NotInitializedException e) {
            logger.error("Failed", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Parser createRequestParser() throws Exception {
        Map<Integer, Parser.CustomMessageParser> messageParsers = new HashMap<Integer, Parser.CustomMessageParser>(3);
        messageParsers.put(PayPointRequest1.ID, new Request1Parser());
        messageParsers.put(PayPointRequest2.ID, new Request2Parser());
        messageParsers.put(PayPointRequest3.ID, new Request3Parser());
        return new Parser(messageParsers);
    }

    private static Serializer createResponseSerializer() throws Exception {
        Map<Integer, Serializer.CustomMessageSerializer> messageSerializers = new HashMap<Integer, Serializer.CustomMessageSerializer>(
                3);
        messageSerializers.put(PayPointResponse1.ID, new Response1Serializer());
        messageSerializers.put(PayPointResponse2.ID, new Response2Serializer());
        messageSerializers.put(PayPointResponse3.ID, new Response3Serializer());
        return new Serializer(messageSerializers);
    }

    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        //todo: FOR DEBUG ONLY !
        doPost(httpRequest, httpResponse);
    }

    private static boolean authenticate(HttpServletRequest httpRequest) throws Exception {
        String remoteAddress = httpRequest.getRemoteAddr();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("remoteAddress: %s", remoteAddress));
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        PayPointConfig payPointConfig = runtimeContext.getPartnerPayPointConfig();
        List<String> remoteAddressMasks = payPointConfig.getRemoteAddressMasks();
        for (String mask : remoteAddressMasks) {
            if (remoteAddress.matches(mask)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("remoteAddress \"%s\" matches mask \"%s\"", remoteAddress, mask));
                }
                return true;
            }
        }
        logger.error(String.format("Authentication failed for: %s", remoteAddress));
        return false;
    }

    private static PayPointResponse process(RuntimeContext runtimeContext, PayPointRequest request) throws Exception {
        return runtimeContext.getPayPointProcessor().processPartnerPayPointRequest(runtimeContext, request);
    }

}