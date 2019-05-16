/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.kzn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.KznClientsStatistic;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.kzn.KznClientsStatisticReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class KznClientsStatisticViewPage extends OnlineReportPage {

    private Logger logger = LoggerFactory.getLogger(KznClientsStatisticViewPage.class);
    private List<KznClientsStatisticReportItem> items = new ArrayList<KznClientsStatisticReportItem>();
    private KznClientsStatisticReportItem currentItem;
    private KznClientsStatisticReportItem addingItem = new KznClientsStatisticReportItem();

    public void update() {
        items.clear();
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            List<KznClientsStatistic> kznClientsStatisticList = DAOUtils.getKznClientStatisticByOrgs(session, idOfOrgList);
            for (KznClientsStatistic kznClientsStatistic : kznClientsStatisticList) {
                items.add(new KznClientsStatisticReportItem(kznClientsStatistic));
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in reload KznClientsStatisticReportPage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void showOrgListSelectPage () {
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    @Override
    public String getPageFilename() {
        return "service/kzn/statistic/view";
    }

    public List<KznClientsStatisticReportItem> getItems() {
        return items;
    }

    public void setItems(List<KznClientsStatisticReportItem> items) {
        this.items = items;
    }

    public KznClientsStatisticReportItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(KznClientsStatisticReportItem currentItem) {
        this.currentItem = currentItem;
    }

    public KznClientsStatisticReportItem getAddingItem() {
        return addingItem;
    }

    public void setAddingItem(KznClientsStatisticReportItem addingItem) {
        this.addingItem = addingItem;
    }
}
