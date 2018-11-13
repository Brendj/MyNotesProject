/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.service.CommonTaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nuc on 08.11.2018.
 */
public class CommonTaskServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CommonTaskServlet.class);


    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        if (httpRequest.getParameter(CommonTaskService.OPERATION_PARAM) == null) return;
        final String operation = httpRequest.getParameter(CommonTaskService.OPERATION_PARAM);
        if (operation.equals(CommonTaskService.OPERATION_LOGGING)) {
            write_to_log(httpRequest);
        }
        httpResponse.sendError(HttpServletResponse.SC_OK);
    }

    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        doPost(httpRequest, httpResponse);
    }

    private void write_to_log(HttpServletRequest httpRequest) {
        if (httpRequest.getParameter(CommonTaskService.NODE_PARAM) == null) return;
        String node = httpRequest.getParameter(CommonTaskService.NODE_PARAM);
        if (httpRequest.getParameter(CommonTaskService.INFO_PARAM) != null)
            logger.info(String.format("Node: %s, info: %s", node, httpRequest.getParameter(CommonTaskService.INFO_PARAM)));
        if (httpRequest.getParameter(CommonTaskService.ERROR_PARAM) != null)
            logger.error(String.format("Node: %s, error: %s", node, httpRequest.getParameter(CommonTaskService.ERROR_PARAM)));
    }
}