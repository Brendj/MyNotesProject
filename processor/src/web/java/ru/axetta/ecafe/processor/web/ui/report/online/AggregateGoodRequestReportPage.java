package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.items.good.request.AggregateGoodRequestReportItem;
import ru.axetta.ecafe.processor.web.ui.report.online.services.AggregateGoodRequestReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("session")
public class AggregateGoodRequestReportPage extends OnlineReportWithContragentPage {

    private static final Logger logger = LoggerFactory.getLogger(AggregateGoodRequestReportPage.class);

    @Autowired
    private AggregateGoodRequestReportService service;

    AggregateGoodRequestReportPage() {
        super();
    }


    public String getPageFilename() {
        return "report/online/aggregate_good_request_report";
    }

    private List<AggregateGoodRequestReportItem> aggregateGoodRequestReportItems = new ArrayList<AggregateGoodRequestReportItem>();

    public Object generateReport(){
        if(idOfContragentOrgList==null || idOfContragentOrgList.isEmpty()){
            printError("Выберите список контрагентов");
            return null;
        }
        if(idOfOrgList==null || idOfOrgList.isEmpty()){
            aggregateGoodRequestReportItems = service.fetchAggregateGoodRequestReportItems(idOfContragentOrgList, startDate, endDate);
        } else {
            aggregateGoodRequestReportItems = service.fetchAggregateGoodRequestReportItems(idOfContragentOrgList, idOfOrgList, startDate, endDate);
        }
        return null;
    }

    public List<AggregateGoodRequestReportItem> getAggregateGoodRequestReportItems() {
        return aggregateGoodRequestReportItems;
    }

    public void showEducationListSelectPage () {
        setSelectIdOfOrgList(true);
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public void showSourceListSelectPage () {
        setSelectIdOfOrgList(false);
        MainPage.getSessionInstance().showOrgListSelectPage();
    }


}
