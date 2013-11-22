/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 06.08.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@WebService
public interface DashboardService {

    @WebMethod(operationName = "getInfoForDashboard")
    public DashboardResponse getInfoForDashboard();
}
