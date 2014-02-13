/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DailyReferReport;
import ru.axetta.ecafe.processor.core.report.ReferReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.online.items.stamp.RegisterStampPageItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 16.12.13
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ReferReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ReferReportPage.class);

    private Date start;
    private Date end;
    private String htmlReport;
    private ReferReport monthlyReport;
    private DailyReferReport dailyReport;
    private String category;
    private List<String> categories;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<SelectItem> getCategories() {
        if(categories == null || categories.size() < 1) {
            RuntimeContext.getAppContext().getBean(ReferReportPage.class).loadCategories();
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(DailyReferReport.SUBCATEGORY_ALL));
        for (String cat : categories) {
            items.add(new SelectItem(cat));
        }
        return items;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    @Transactional
    public void loadCategories() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            categories = DAOUtils.getDiscountRuleSubcategories(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }


    @Override
    public void onShow() throws Exception {
        category = "Все";
    }

    public void clear(){
    }
    
    private static final int MONTHLY_REPORT = 1;
    private static final int DAILY_REPORT   = 2;

    public void doGenerateMonthly() {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generate(MONTHLY_REPORT);
    }

    public void doGenerateDaily() {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generate(DAILY_REPORT);
    }

    @Transactional
    public void generate(int reportType) {
        Session session = null;
        try {
            monthlyReport = null;
            dailyReport = null;

            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            endDate = updateEndDate(endDate);

            session = (Session) entityManager.getDelegate();
            BasicReportJob.OrgShortItem orgItem = getOrgItem();
            switch (reportType) {
                case MONTHLY_REPORT:
                    generateMonthlyReport(session, orgItem, cal);
                    break;
                case DAILY_REPORT:
                    generateDailyReport(session, orgItem, cal);
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void generateMonthlyReport(Session session, BasicReportJob.OrgShortItem orgItem, Calendar cal) {
        ReferReport.Builder reportBuilder = new ReferReport.Builder();
        reportBuilder.setOrg(orgItem);
        try {
            monthlyReport = reportBuilder.build(session, start, end, cal);
            htmlReport = monthlyReport.getHtmlReport();
        } catch (Exception e) {
            logger.error("Failed to generate monthly report", e);
        }
    }

    public void generateDailyReport(Session session, BasicReportJob.OrgShortItem orgItem, Calendar cal) {
        DailyReferReport.Builder reportBuilder = new DailyReferReport.Builder();
        Properties props = new Properties();
        props.setProperty(DailyReferReport.SUBCATEGORY_PARAMETER, category);
        reportBuilder.setReportProperties(props);
        reportBuilder.setOrg(orgItem);
        try {
            dailyReport = reportBuilder.build(session, start, end, cal);
            htmlReport = dailyReport.getHtmlReport();
        } catch (Exception e) {
            logger.error("Failed to generate daily report", e);
        }
    }

    public Date updateEndDate(Date endDate) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(endDate.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public BasicReportJob.OrgShortItem getOrgItem() {
        if (idOfOrg != null) {
            Org org = null;
            if (idOfOrg != null && idOfOrg > -1) {
                org = DAOService.getInstance().findOrById(idOfOrg);
            }
            return new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName());
        } else {
            return null;
        }

    }

    @Override
    public String getPageFilename() {
        return "report/online/refer_report";
    }

    public String getHtmlReport() {
        return htmlReport;
    }
}
