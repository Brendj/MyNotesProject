package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;
import ru.axetta.ecafe.processor.web.ui.report.online.items.good.request.AggregateGoodRequestReportItem;
import ru.axetta.ecafe.processor.web.ui.report.online.services.AggregateGoodRequestReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class AggregateGoodRequestReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(AggregateGoodRequestReportPage.class);

    @Autowired
    private AggregateGoodRequestReportService service;

    public String getPageFilename() {
        return "monitoring/aggregate_good_request_report";
    }

    private List<AggregateGoodRequestReportItem> aggregateGoodRequestReportItems = new ArrayList<AggregateGoodRequestReportItem>();

    public Object generateReport(){
        aggregateGoodRequestReportItems = service.fetchAggregateGoodRequestReportItems(idOfOrgList, startDate, endDate);
        return null;
    }

    public List<AggregateGoodRequestReportItem> getAggregateGoodRequestReportItems() {
        return aggregateGoodRequestReportItems;
    }

}
