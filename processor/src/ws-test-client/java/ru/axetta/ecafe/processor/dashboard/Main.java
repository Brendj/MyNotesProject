/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 06.08.12
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String args[]) {

        DashboardServiceWSService dashboardServiceWSService = new DashboardServiceWSService();
        DashboardService dashboardService = dashboardServiceWSService.getDashboardServiceWSPort();
        DashboardResponse dashboardResponse = dashboardService.getInfoForDashboard();
        for (EduInstItemInfo eduInstItemInfo : dashboardResponse.getEduInstItemInfoList()) {
            XMLGregorianCalendar firstFullSyncTime = eduInstItemInfo.getFirstFullSyncTime();
            XMLGregorianCalendar lastFullSyncTime = eduInstItemInfo.getLastFullSyncTime();
            System.out.print("First full sync time: " + firstFullSyncTime + " | ");
            System.out.println("Last full sync time: " + lastFullSyncTime);
        }

    }

}
