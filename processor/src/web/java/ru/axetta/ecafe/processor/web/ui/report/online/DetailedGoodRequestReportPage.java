package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.statistics.good.request.DetailedGoodRequestReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.report.statistics.good.request.DetailedGoodRequestReportService;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import java.util.*;

public class DetailedGoodRequestReportPage extends OnlineReportWithContragentPage {

    private static final Logger logger = LoggerFactory.getLogger(DetailedGoodRequestReportPage.class);
    private List<DetailedGoodRequestReportItem> detailedGoodRequestReportItems = new ArrayList<DetailedGoodRequestReportItem>();
    private final DocumentStateFilterMenu documentStateFilterMenu = new DocumentStateFilterMenu();

    public DocumentStateFilterMenu getDocumentStateFilterMenu() {
        return documentStateFilterMenu;
    }

    public String getPageFilename() {
        return "report/online/aggregate_good_request_report";
    }

    public List<DetailedGoodRequestReportItem> getDetailedGoodRequestReportItems() {
        return detailedGoodRequestReportItems;
    }

    public void buildReport(Session persistenceSession) throws Exception{
        if(CollectionUtils.isEmpty(idOfOrgList) && CollectionUtils.isEmpty(idOfContragentOrgList)){
            throw new Exception("Выберите список организаций или поставщиков");
        }
        DetailedGoodRequestReportService service = new DetailedGoodRequestReportService();
        detailedGoodRequestReportItems = service.buildReport(persistenceSession, idOfContragentOrgList, idOfOrgList, startDate, endDate, documentStateFilterMenu.getDocumentStateFilter());
    }
}


