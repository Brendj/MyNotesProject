/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class VisitorDogmHistoryReportPage extends OnlineReportPage{

    @Autowired
    private VisitorDogmServiceBean serviceBean;

    private List<VisitorItem> cardEventOperationItems;

    @Override
    public void onShow() throws Exception {
        cardEventOperationItems = new ArrayList<VisitorItem>();
    }

    public Object buildReport(){
        cardEventOperationItems = serviceBean.generateVisitorDogmHistoryReport(this.startDate, this.endDate);
        //cardEventOperationItems = serviceBean.employeeHistoryReport(this.startDate, this.endDate);
        return null;
    }

    public List<VisitorItem> getCardEventOperationItems() {
        return cardEventOperationItems;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/visitordogm/history_report";
    }
}
