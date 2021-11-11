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
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged() {
        switch (periodTypeMenu.getPeriodType()){
            case ONE_DAY: {
                setEndDate(startDate);
            } break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            } break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            } break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            } break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noOfDays=(int)(diff/(24*60*60*1000));
            switch (noOfDays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
        }
    }

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


