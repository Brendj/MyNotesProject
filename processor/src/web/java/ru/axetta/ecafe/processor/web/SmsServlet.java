/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.SMSService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.11.16
 * Time: 10:37
 */
@WebServlet(
        name = "SmsServlet",
        description = "SmsServlet",
        urlPatterns = {"/sms-state/"}
)
public class SmsServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(SmsServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SMSService smsService = RuntimeContext.getAppContext().getBean(SMSService.class);
            SMSService.QueueState queueState = smsService.getQueueState();

            long time = 0;
            long size = 0;
            for(Object o : EMPSmsServiceImpl.getBuffer()) {
                long i = (Long) o;
                time += i;
                size++;
            }
            long responseTime = 0;
            if(size > 0) {
                responseTime = time / size;
            }

            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            out.println("По состоянию на " + CalendarUtils.dateTimeToString(new Date()));
            out.println("<br/>");
            out.println("Размер очереди: " + queueState.getQueue());
            out.println("<br/>");
            out.println("Время ответа: " + responseTime + " мс.");
            out.println("<br/>");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
