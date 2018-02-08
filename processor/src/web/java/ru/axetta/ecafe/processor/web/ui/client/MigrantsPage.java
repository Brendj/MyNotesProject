/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.items.MigrantItem;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 01.02.2018.
 */
@Component
@Scope("session")
public class MigrantsPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    Logger logger = LoggerFactory.getLogger(MigrantsPage.class);

    private Date startDate;
    private Date endDate;
    private Long idOfOrg;
    private String orgName;
    private String guid;
    private List<MigrantItem> items = new ArrayList<MigrantItem>();
    private Boolean showAllMigrants;
    private MigrantItem currentItem;

    @PostConstruct
    public void setDates() {
        startDate = CalendarUtils.startOfDay(CalendarUtils.getFirstDayOfMonth(new Date()));
        endDate = CalendarUtils.endOfDay(CalendarUtils.getLastDayOfMonth(new Date()));
    }

    @Override
    public String getPageFilename() {
        return "client/migrants";
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            this.idOfOrg = idOfOrg;
            Org org = (Org)session.load(Org.class, idOfOrg);
            this.orgName = org.getShortNameInfoService();
        }
        else {
            this.idOfOrg = null;
            this.orgName = "";
        }
    }

    private void update() throws Exception {
        if (StringUtils.isEmpty(guid) && idOfOrg == null) {
            printError("Выберите организацию или введите Guid клиента");
            return;
        }
        startDate = CalendarUtils.startOfDay(startDate);
        endDate = CalendarUtils.endOfDay(endDate);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            List orgs = new ArrayList<Long>();
            if (idOfOrg != null) {
                orgs.add(idOfOrg);
            }
            List<Migrant> list = MigrantsUtils.getAllMigrantsForOrgsByDate(session, orgs, guid, startDate, endDate, showAllMigrants);
            items.clear();
            for (Migrant migrant : list) {
                MigrantItem item = new MigrantItem(session, migrant);
                items.add(item);
            }
            Collections.sort(items);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void updateFilter() {
        try {
            update();
        } catch (Exception e) {
            printMessage("Во время обновления данных произошла ошибка: " + e.getMessage());
            logger.error("Error loading migrants: ", e);
        }
    }

    public void clearFilter() {
        this.orgName = "";
        this.idOfOrg = null;
        this.guid = "";
        this.showAllMigrants = false;
        this.items.clear();
    }

    public void disableMigrantRequest() {
        for (MigrantItem item : items) {
            if (item.getCompositeIdOfMigrant().equals(currentItem.getCompositeIdOfMigrant())) {
                item.setAnnulled(true);
                item.setCanceledResolution();
                break;
            }
        }
    }

    public void apply() {
        for (MigrantItem item : items) {
            if (item.isAnnulled()) {
                MigrantsUtils.disableMigrant(item.getCompositeIdOfMigrant());
            }
        }
        printMessage("Изменения сохранены");
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public List<MigrantItem> getItems() {
        return items;
    }

    public void setItems(List<MigrantItem> items) {
        this.items = items;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Boolean getShowAllMigrants() {
        return showAllMigrants;
    }

    public void setShowAllMigrants(Boolean showAllMigrants) {
        this.showAllMigrants = showAllMigrants;
    }

    public MigrantItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(MigrantItem currentItem) {
        this.currentItem = currentItem;
    }
}
