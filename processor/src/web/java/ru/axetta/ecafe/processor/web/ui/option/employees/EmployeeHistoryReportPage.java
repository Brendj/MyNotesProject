/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.CardEventOperationItem;
import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.13
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class EmployeeHistoryReportPage extends OnlineReportPage{

    @Autowired
    private EmployeeServiceBean serviceBean;

    private List<VisitorItem> cardEventOperationItems;

    @Override
    public void onShow() throws Exception {
        cardEventOperationItems = new ArrayList<VisitorItem>();
    }

    public Object buildReport(){
        cardEventOperationItems = serviceBean.generateEmployeeHistoryReport(this.startDate, this.endDate);
        return null;
    }

    public List<VisitorItem> getCardEventOperationItems() {
        return cardEventOperationItems;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/employee/history_report";
    }
}
