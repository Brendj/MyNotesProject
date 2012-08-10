package ru.axetta.ecafe.processor.dashboard.client;/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

import ru.axetta.ecafe.processor.dashboard.DashboardResponse;
import ru.axetta.ecafe.processor.dashboard.DashboardService;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceWSService;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 10.08.12
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String args[]) {

        DashboardServiceWSService dashboardServiceWSService = new DashboardServiceWSService();
        DashboardService dashboardService = dashboardServiceWSService.getDashboardServiceWSPort();
        DashboardResponse dashboardResponse = dashboardService.getInfoForDashboard();
        System.out.println(dashboardResponse.getEduInstItemInfoList().size());
        System.out.println(dashboardResponse.getPaymentSystemItemInfoList().size());
    }

}
