/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgShortItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 21.10.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class OrdersMonitoringReportPage extends BasicWorkspacePage {
    private static final long LINK_PARAM_TIMEOUT = 604800000;   //  неделя

    private static final Logger logger = LoggerFactory.getLogger(OrdersMonitoringReportPage.class);
    private int missingCount;
    private int overallCount;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    @Override
    public String getPageFilename() {
        return "monitoring/orders_report";
    }

    @Override
    public void onShow() throws Exception {
        doGenerateReport();
    }

    public void doGenerateReport() {
        RuntimeContext.getAppContext().getBean(OrdersMonitoringReportPage.class).loadCounts();
    }

    @Transactional
    public void loadCounts() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            Query q = session.createSQLQuery(
                      "select count(distinct cf_orders.idoforg) "
                    + "from cf_orders "
                    + "join cf_orgs on cf_orders.idoforg=cf_orgs.idoforg "
                    + "where cf_orgs.state=:state and cf_orders.createddate>EXTRACT(EPOCH FROM now()) * 1000 - :timeout "
                    + "union all "
                    + "select count(distinct cf_orgs.idoforg) "
                    + "from cf_orgs "
                    + "join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                    + "where cf_orgs.state=:state");
            q.setLong("timeout", GoodRequestsReport.REQUESTS_MONITORING_TIMEOUT);
            q.setInteger("state", 1);
            List res = q.list();
            missingCount = ((BigInteger) res.get(0)).intValue();
            overallCount = ((BigInteger) res.get(1)).intValue();
        } catch (Exception e) {
            logger.error("Failed to load counts for orders monitoring report", e);
        }
    }

    public int getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }

    public int getOverallCount() {
        return overallCount;
    }

    public void setOverallCount(int overallCount) {
        this.overallCount = overallCount;
    }

    public Date getMissingStartDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeInMillis(cal.getTimeInMillis() - LINK_PARAM_TIMEOUT);
        return cal.getTime();
    }
}
