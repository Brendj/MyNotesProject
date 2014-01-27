package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.report.statistics.good.request.AggregateGoodRequestReportItem;
import ru.axetta.ecafe.processor.core.report.statistics.good.request.AggregateGoodRequestReportService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
@Scope("session")
public class AggregateGoodRequestReportPage extends OnlineReportWithContragentPage {

    private static final Logger logger = LoggerFactory.getLogger(AggregateGoodRequestReportPage.class);
    private int daysLimit;
    private List<AggregateGoodRequestReportItem> aggregateGoodRequestReportItems = new ArrayList<AggregateGoodRequestReportItem>();

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    @Autowired
    private AggregateGoodRequestReportService service;

    AggregateGoodRequestReportPage() {
        super();
    }


    public String getPageFilename() {
        return "report/online/aggregate_good_request_report";
    }

    public int getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(int daysLimit) {
        this.daysLimit = daysLimit;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(AggregateGoodRequestReportPage.class).loadPredefinedContragents();
    }

    @Transactional
    public void loadPredefinedContragents () {
        if (idOfContragentOrgList.size() > 0) {
            return;
        }
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            List<OrgShortItem> orgs = OrgListSelectPage.retrieveOrgs(session, "", "", 2);
            Map<Long, String> contragentsMap = new HashMap<Long, String>();
            selectIdOfOrgList = false;
            for (OrgShortItem i : orgs) {
                contragentsMap.put(i.getIdOfOrg(), i.getOfficialName());
            }
            completeOrgListSelection(contragentsMap);
            selectIdOfOrgList = true;
        } catch (Exception e) {
            logger.error("Failed to predefine allowed contragents list", e);
        }
    }

    public Object generateReport(){
        if(idOfContragentOrgList==null || idOfContragentOrgList.isEmpty()){
            printError("Выберите список поставщиков");
            return null;
        }
        //  пределяем на какой лимит дней необходимо увеличить дату
        endDate = new Date(GoodRequestsReportPage.getDaysLimitTS(daysLimit, startDate));

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

    public Object showEducationListSelectPage () {
        setSelectIdOfOrgList(true);
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showSourceListSelectPage () {
        setSelectIdOfOrgList(false);
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }


    //public String getGetStringIdOfOrgList() {
    //    return idOfContragentOrgList.toString().replaceAll("[^0-9,]","");
    //}

}
