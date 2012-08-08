/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import javax.ejb.EJB;
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

    @EJB
    DashboardServiceBean dashboardServiceBean;

    @Override
    public DashboardResponse getInfoForDashboard() {
        DashboardResponse result = null;
        try {
            result = dashboardServiceBean.getInfoForDashboard();
        } finally {
            return result;
        }
    }

}
