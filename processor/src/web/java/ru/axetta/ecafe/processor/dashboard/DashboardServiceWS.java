/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.jws.WebService;
import javax.servlet.http.HttpServlet;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 06.08.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@WebService()
public class DashboardServiceWS extends HttpServlet implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceWS.class);

    @Override
    public DashboardResponse getInfoForDashboard() {
        ApplicationContext applicationContext = RuntimeContext.getAppContext();
        DashboardServiceBean dashboardServiceBean = applicationContext.getBean(DashboardServiceBean.class);
        DashboardResponse result = null;
        try {
            result = dashboardServiceBean.getInfoForDashboard();
        } catch (Exception e) {
            logger.error("error in dashboardServiceBean.getInfoForDashboard", e);
            result = null;
        } finally {
            return result;
        }
    }

}
