/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.items.MigrantItem;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
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

            items = getMigrantItems(session, idOfOrg, guid, startDate, endDate, ignoreDates, clientIDList, migrantType);

            Collections.sort(items);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<MigrantItem> getMigrantItems(Session session, Long idOfOrg, String guid, Date startDate, Date endDate,
            Boolean ignoreDates, List<Long> clientIDList, Integer migrantType){
        String inactiveTypes = StringUtils.join(Arrays.asList(
                VisitReqResolutionHist.RES_REJECTED,
                VisitReqResolutionHist.RES_CANCELED,
                VisitReqResolutionHist.RES_OVERDUE,
                VisitReqResolutionHist.RES_OVERDUE_SERVER
        ), ", ");

        String strQuery = "SELECT m.requestNumber AS requestNumber,"
                + " TO_TIMESTAMP(firstResol.resolutiondatetime/1000) AS createDateTime,"
                + " TO_TIMESTAMP(lastResol.resolutiondatetime/1000) AS lastUpdateDateTime, "
                + "CASE WHEN m.initiator = 0 THEN 'ОУ' "
                + "     WHEN m.initiator = 1 THEN 'ЕСЗ' "
                + "     WHEN m.initiator = 2 THEN 'НСИ' "
                + "     WHEN m.initiator = 3 THEN 'Процессинг' "
                + "     ELSE 'Неизвестно' "
                + "END AS initiator, "
                + "(p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fio, ('(' || regOrg.idoforg || ') ' || regOrg.shortnameinfoservice) AS orgRegistry, "
                + "('(' || visitOrg.idoforg || ') ' || visitOrg.shortnameinfoservice) AS orgVisit, "
                + "TO_TIMESTAMP(m.visitstartdate/1000) AS visitStartDate, TO_TIMESTAMP(m.visitenddate/1000) AS visitEndDate, "
                + "(CASE WHEN lastResol.resolution = 0 THEN 'Создана' "
                + "     WHEN lastResol.resolution = 1 THEN 'Подтверждена' "
                + "     WHEN lastResol.resolution = 2 THEN 'Отклонена и сдана в архив' "
                + "     WHEN lastResol.resolution = 3 THEN 'Аннулирована и сдана в архив' "
                + "     WHEN lastResol.resolution = 4 THEN 'Сдана в архив по истечению срока действия' "
                + "     WHEN lastResol.resolution = 5 THEN 'Сдана в архив по истечению срока действия' "
                + "     ELSE 'Неизвестно' "
                + "END || "
                + "CASE WHEN lastResol.initiator IS NULL THEN '' "
                + "     ELSE ' (' || CASE WHEN lastResol.initiator = 0 THEN 'ОУ' "
                + "                       WHEN lastResol.initiator = 1 THEN 'ЕСЗ' "
                + "                       WHEN lastResol.initiator = 2 THEN 'НСИ' "
                + "                       WHEN lastResol.initiator = 3 THEN 'Процессинг' "
                + "                       ELSE 'Неизвестно' "
                + "                  END || ')' "
                + "END) AS resolution, "
                + "cc.clientguid AS guid, m.section AS section, lastResol.resolution AS resolutionValue, cg.groupname AS group, visitOrg.shortname AS shortNameVisit, "
                + "visitOrg.idoforg AS idOfOrgVisit, visitOrg.shortAddress AS shortAddressVisit, m.idofrequest AS idOfRequest, m.idoforgregistry AS idOfOrgRegistry "
                + "FROM cf_migrants AS m "
                + "JOIN cf_clients cc ON m.idofclientmigrate = cc.idofclient "
                + "JOIN cf_persons p ON cc.idofperson = p.idofperson "
                + "JOIN cf_orgs AS regOrg ON m.idoforgregistry = regOrg.idoforg "
                + "JOIN cf_orgs AS visitOrg ON m.idoforgvisit = visitOrg.idoforg "
                + "LEFT JOIN cf_clientgroups cg ON cc.idoforg = cg.idoforg AND cc.idofclientgroup = cg.idofclientgroup "
                + "JOIN (SELECT DISTINCT ON(idofrequest, idoforgregistry) * FROM cf_visitreqresolutionhist ORDER BY idofrequest, idoforgregistry, resolutiondatetime ASC) AS firstResol ON m.idofrequest = firstResol.idofrequest and m.idoforgregistry = firstResol.idoforgregistry "
                + "JOIN (SELECT DISTINCT ON(idofrequest, idoforgregistry) * FROM cf_visitreqresolutionhist ORDER BY idofrequest, idoforgregistry, resolutiondatetime DESC) AS lastResol ON m.idofrequest = lastResol.idofrequest and m.idoforgregistry = lastResol.idoforgregistry "
                + "WHERE ";
        boolean isFirstStatement = true;
        if (idOfOrg != null) {
            strQuery += " (m.idoforgregistry = :idOfOrg OR m.idoforgvisit = :idOfOrg) ";
            isFirstStatement = false;
        }
        if (!StringUtils.isEmpty(guid)) {
            strQuery += isFirstStatement ? "" : " AND ";
            strQuery += " cc.clientGUID LIKE " + guid;

            isFirstStatement = false;
        }
        if (!CollectionUtils.isEmpty(clientIDList)) {
            strQuery += isFirstStatement ? "" : " AND ";
            strQuery += " cc.idOfClient IN (:clientIDList)";
        }

        if(!ignoreDates){
            strQuery += " AND lastResol.resolutionDateTime between :startDate AND :endDate ";
        }

        if (migrantType.equals(TYPE_CREATED)) {
            strQuery += " AND lastResol.resolution = " + VisitReqResolutionHist.RES_CREATED;
        } else if(migrantType.equals(TYPE_ACTIVE)){
            strQuery += " AND lastResol.resolution = " + VisitReqResolutionHist.RES_CONFIRMED;
        } else if(migrantType.equals(TYPE_INACTIVE)){
            strQuery += " AND lastResol.resolution IN (" + inactiveTypes + ")";
        }

        SQLQuery query = session.createSQLQuery(strQuery);
        if(idOfOrg != null){
            query.setParameter("idOfOrg", idOfOrg);
        }
        if(!CollectionUtils.isEmpty(clientIDList)){
            query.setParameterList("clientIDList", clientIDList);
        }
        if(!ignoreDates){
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
        }
        query
                .addScalar("requestNumber", StringType.INSTANCE)
                .addScalar("createDateTime", TimestampType.INSTANCE)
                .addScalar("lastUpdateDateTime", TimestampType.INSTANCE)
                .addScalar("initiator", StringType.INSTANCE)
                .addScalar("fio", StringType.INSTANCE)
                .addScalar("orgRegistry", StringType.INSTANCE)
                .addScalar("orgVisit", StringType.INSTANCE)
                .addScalar("visitStartDate", TimestampType.INSTANCE)
                .addScalar("visitEndDate", TimestampType.INSTANCE)
                .addScalar("resolution", StringType.INSTANCE)
                .addScalar("guid", StringType.INSTANCE)
                .addScalar("section", StringType.INSTANCE)
                .addScalar("resolutionValue", IntegerType.INSTANCE)
                .addScalar("group", StringType.INSTANCE)
                .addScalar("shortNameVisit", StringType.INSTANCE)
                .addScalar("idOfOrgVisit", LongType.INSTANCE)
                .addScalar("shortAddressVisit", StringType.INSTANCE)
                .addScalar("idOfRequest", LongType.INSTANCE)
                .addScalar("idOfOrgRegistry", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(MigrantItem.class));

        return query.list();
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
