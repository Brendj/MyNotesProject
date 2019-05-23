/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.items.MigrantItem;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by i.semenov on 01.02.2018.
 */
@Component
@Scope("session")
public class MigrantsPage extends OnlineReportPage implements OrgSelectPage.CompleteHandler {
    private static final int TYPE_ALL = 0;
    private static final int TYPE_CREATED = 1;
    private static final int TYPE_ACTIVE = 2;
    private static final int TYPE_INACTIVE = 3;

    Logger logger = LoggerFactory.getLogger(MigrantsPage.class);

    private Date startDate;
    private Date endDate;
    private Long idOfOrg;
    private String orgName;
    private String guid;
    private List<MigrantItem> items = new ArrayList<MigrantItem>();
    private MigrantItem currentItem;
    private Boolean ignoreDates;
    private Integer migrantType = TYPE_ALL;
    private List<SelectItem> migrantTypes = initMigrantType();

    private List<SelectItem> initMigrantType() {
        List<SelectItem> filters = new ArrayList<SelectItem>(4);
        filters.add(new SelectItem(TYPE_ALL, "Все"));
        filters.add(new SelectItem(TYPE_CREATED, "Созданные"));
        filters.add(new SelectItem(TYPE_ACTIVE, "Активные"));
        filters.add(new SelectItem(TYPE_INACTIVE, "Неактивные"));
        return filters;
    }

    protected String filterClient = "Не выбрано";

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
        List<ClientSelectListPage.Item> clientList = getClientList();
        if (StringUtils.isEmpty(guid) && idOfOrg == null && clientList.isEmpty()) {
            printError("Выберите организацию, введите Guid клиента или выберите клиента");
            return;
        }
        startDate = CalendarUtils.startOfDay(startDate);
        endDate = CalendarUtils.endOfDay(endDate);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            List<Long> clientIDList = new ArrayList<Long>();
            for (ClientSelectListPage.Item item : clientList) {
                clientIDList.add(item.getIdOfClient());
            }

            List<Migrant> list = getMigrantItems(session, idOfOrg, guid, startDate, endDate, ignoreDates, clientIDList, migrantType);
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

    public List<Migrant> getMigrantItems(Session session, Long idOfOrg, String guid, Date startDate, Date endDate,
            Boolean ignoreDates, List<Long> clientIDList, Integer migrantType){
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.createAlias("migrant", "m");
        criteria.createAlias("m.clientMigrate", "clientMigrate");
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("migrant"));
        criteria.setProjection(projectionList);

        if(!ignoreDates){
            criteria.add(Restrictions.lt("resolutionDateTime", endDate));
            criteria.add(Restrictions.gt("resolutionDateTime", startDate));
        }

        if(migrantType.equals(TYPE_CREATED)){
            criteria.add(Restrictions.eq("resolution",  VisitReqResolutionHist.RES_CREATED));
        } else if(migrantType.equals(TYPE_ACTIVE)){
            criteria.add(Restrictions.eq("resolution", VisitReqResolutionHist.RES_CONFIRMED));
        } else if(migrantType.equals(TYPE_INACTIVE)){
            List<Integer> inactiveTypes = Arrays.asList(
                    VisitReqResolutionHist.RES_REJECTED,
                    VisitReqResolutionHist.RES_CANCELED,
                    VisitReqResolutionHist.RES_OVERDUE,
                    VisitReqResolutionHist.RES_OVERDUE_SERVER
            );
            criteria.add(Restrictions.in("resolution", inactiveTypes));
        }
        if (idOfOrg != null) {
            Criterion orgVisitCondition = Restrictions.eq("m.orgVisit.idOfOrg", idOfOrg);
            Criterion orgRegistryCondition = Restrictions.eq("m.orgRegistry.idOfOrg", idOfOrg);
            criteria.add(Restrictions.or(orgRegistryCondition, orgVisitCondition));
        }
        if (!StringUtils.isEmpty(guid)) {
            criteria.add(Restrictions.ilike("clientMigrate.clientGUID", guid));
        }
        if (!clientIDList.isEmpty()) {
            criteria.add(Restrictions.in("clientMigrate.idOfClient", clientIDList));
        }

        return criteria.list();
    }

    public void updateFilter() {
        try {
            update();
        } catch (Exception e) {
            printError("Во время обновления данных произошла ошибка: " + e.getMessage());
            logger.error("Error loading migrants: ", e);
        }
    }

    public void clearFilter() {
        this.orgName = "";
        this.idOfOrg = null;
        this.guid = "";
        this.ignoreDates = false;
        this.items.clear();
        this.migrantType = TYPE_ALL;
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

    public MigrantItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(MigrantItem currentItem) {
        this.currentItem = currentItem;
    }

    public String getStringClientList() {
        List<String> val = new ArrayList<String>();
        for (ClientSelectListPage.Item item : getClientList()) {
            val.add(item.getCaption());
        }
        if (val.isEmpty()) {
            return "";
        } else {
            return val.toString();
        }
    }

    public String getFilterClient() {
        return filterClient;
    }

    @Override
    public void completeClientSelection(Session session, List<ClientSelectListPage.Item> items) throws Exception {
        if (items != null) {
            getClientList().clear();
            for (ClientSelectListPage.Item item : items) {
                getClientList().add(item);
            }
        }
        filterClient = getStringClientList();
    }

    public void setFilterClient(String filterClient) {
        this.filterClient = filterClient;
    }

    public Boolean getIgnoreDates() {
        return ignoreDates;
    }

    public void setIgnoreDates(Boolean ignoreDates) {
        this.ignoreDates = ignoreDates;
    }

    public Integer getMigrantType() {
        return migrantType;
    }

    public void setMigrantType(Integer migrantType) {
        this.migrantType = migrantType;
    }

    public List<SelectItem> getMigrantTypes() {
        return migrantTypes;
    }

    public void setMigrantTypes(List<SelectItem> migrantTypes) {
        this.migrantTypes = migrantTypes;
    }
}
