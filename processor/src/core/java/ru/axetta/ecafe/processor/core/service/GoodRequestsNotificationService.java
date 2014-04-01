package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOCurrentOrgVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodRequestsNotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodRequestsNotificationService.class);

    public void notifyGoodRequestsReport() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        if (!runtimeContext.isMainNode()) {
            return;
        }
        Long duration = System.currentTimeMillis();
        boolean isHideMissedColumns = runtimeContext
                .getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);
        int maxNumDays = runtimeContext.getOptionValueInt(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        if (runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE)) {
            Map<Long, String> contragentTSPMap = new HashMap<Long, String>();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria criteria = persistenceSession.createCriteria(Contragent.class);
                criteria.add(Restrictions.eq("classId", Contragent.TSP));
                List<Contragent> contragentTSPList = criteria.list();
                for (Contragent contragent : contragentTSPList) {
                    final String requestNotifyEmailAddress = contragent.getRequestNotifyMailList();
                    if (StringUtils.isNotEmpty(requestNotifyEmailAddress)) {
                        contragentTSPMap.put(contragent.getIdOfContragent(), requestNotifyEmailAddress);
                    }
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, LOGGER);
                HibernateUtils.close(persistenceSession, LOGGER);
            }
            for (Long contragent : contragentTSPMap.keySet()) {
                Set<OrgShortItem> shortItems = new HashSet<OrgShortItem>();

                try {
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
                    orgCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", contragent));
                    orgCriteria.setProjection(Projections.projectionList().add(Projections.property("idOfOrg"))
                            .add(Projections.property("shortName")).add(Projections.property("officialName"))
                            .add(Projections.property("address")));
                    List orgList = orgCriteria.list();
                    for (Object obj : orgList) {
                        Object[] row = (Object[]) obj;
                        long idOfOrg = Long.parseLong(row[0].toString());
                        shortItems.add(new OrgShortItem(idOfOrg, row[1].toString(), row[2].toString(),
                                row[3].toString()));
                    }
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, LOGGER);
                    HibernateUtils.close(persistenceSession, LOGGER);
                }

                for (OrgShortItem orgShortItem : shortItems) {
                    boolean doSend = false;
                    StringBuilder newValueHistory = new StringBuilder();
                    final long idOfOrg = orgShortItem.getIdOfOrg();
                    List<DOCurrentOrgVersion> currentOrgVersions = new ArrayList<DOCurrentOrgVersion>();
                    try {
                        persistenceSession = runtimeContext.createReportPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        String sql = "from DOCurrentOrgVersion where idOfOrg=:orgOwner";
                        Query query = persistenceSession.createQuery(sql);
                        query.setParameter("orgOwner", idOfOrg);
                        currentOrgVersions = query.list();
                        List<GoodRequestPosition> positions = new ArrayList<GoodRequestPosition>();
                        Date currentDate = new Date();
                        if (currentOrgVersions.isEmpty()) {
                            sql = "from GoodRequestPosition p where p.globalVersion>:v and p.orgOwner=:orgOwner group by p.goodRequest, p.globalId order by lastUpdate";
                            Query q = persistenceSession.createQuery(sql);
                            q.setParameter("v", 0L);
                            q.setParameter("orgOwner", idOfOrg);
                            List<GoodRequestPosition> goodRequestPositionList = q.list();
                            if (!goodRequestPositionList.isEmpty()) {
                                positions.addAll(goodRequestPositionList);
                            }
                        } else {
                            for (DOCurrentOrgVersion version : currentOrgVersions) {
                                sql = "from GoodRequestPosition p where p.globalVersion>:v and p.orgOwner=:orgOwner group by p.goodRequest, p.globalId order by lastUpdate";
                                Query q = persistenceSession.createQuery(sql);
                                q.setParameter("v", version.getLastVersion());
                                q.setParameter("orgOwner", version.getIdOfOrg());
                                List<GoodRequestPosition> goodRequestPositionList = q.list();
                                if (!goodRequestPositionList.isEmpty()) {
                                    positions.addAll(goodRequestPositionList);
                                }
                            }
                        }

                        if (positions.isEmpty()) {
                            LOGGER.debug("GoodRequest change list is empty " + idOfOrg);
                            continue;
                        }
                        GoodRequestsReport.Builder reportBuilder = new GoodRequestsReport.Builder();
                        currentDate = CalendarUtils.truncateToDayOfMonth(currentDate);
                        GoodRequestsReport goodRequests = reportBuilder
                                .build(persistenceSession, isHideMissedColumns, currentDate,
                                        CalendarUtils.addDays(currentDate, maxNumDays), idOfOrg);
                        if (goodRequests.getGoodRequestItems().size() < 2) {
                            LOGGER.debug("GoodRequests Report is empty " + idOfOrg);
                            continue; // Строка итого всегда присутсвует
                        }

                        // Создадим записи если имеются максимальные значения
                        sql = "select max(globalVersion) from GoodRequestPosition p where p.orgOwner=:orgOwner";
                        query = persistenceSession.createQuery(sql);
                        query.setParameter("orgOwner", idOfOrg);
                        List list = query.list();
                        if (!(list.isEmpty() || list.get(0) == null)) {
                            Object o = list.get(0);
                            Long maxVal = Long.valueOf(o.toString());
                            for (DOCurrentOrgVersion doCurrentOrgVersion : currentOrgVersions) {
                                if (!doCurrentOrgVersion.getObjectId().equals(DOCurrentOrgVersion.GOOD_REQUEST_POSITION)
                                        && !doCurrentOrgVersion.getIdOfOrg().equals(idOfOrg)) {
                                    DOCurrentOrgVersion newDoCurrentOrgVersion = new DOCurrentOrgVersion();
                                    newDoCurrentOrgVersion.setIdOfOrg(idOfOrg);
                                    newDoCurrentOrgVersion.setLastVersion(maxVal);
                                    newDoCurrentOrgVersion.setObjectId(DOCurrentOrgVersion.GOOD_REQUEST_POSITION);
                                    persistenceSession.persist(newDoCurrentOrgVersion);
                                }
                            }
                        }
                        Object[] columnNames = goodRequests.getColumnNames();

                        Criteria syncHistoryCriteria = persistenceSession.createCriteria(SyncHistory.class);
                        syncHistoryCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
                        syncHistoryCriteria.setProjection(Projections.max("syncEndTime"));
                        syncHistoryCriteria.setMaxResults(1);
                        Object maxSyncEndTime = syncHistoryCriteria.uniqueResult();
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy EE HH:mm:ss", new Locale("ru"));
                            newValueHistory
                                    .append("<p>Время последней выгрузки заявок: " + format.format(maxSyncEndTime)
                                            + "</p>");
                            newValueHistory.append("<table border=1 cellpadding=0 cellspacing=0><tr>");
                        } catch (Exception warn) {
                            LOGGER.warn("cannot parse datetime value");
                        }

                        int i = 0;
                        for (Object o : columnNames) {
                            if (++i < 3) {
                                continue;
                            }
                            String colName = o.toString();
                            if (StringUtils.countMatches(colName, "Вс") > 0) {
                                newValueHistory.append("<th align=center style='background-color: #DFDFDF;'>");
                            } else {
                                newValueHistory.append("<th align=center>");
                            }
                            newValueHistory.append(colName).append("</th>");
                        }

                        List<GoodRequestsReport.RequestItem> items = goodRequests.getGoodRequestItems();
                        newValueHistory.append("</tr>");
                        for (GoodRequestsReport.RequestItem item : items) {
                            newValueHistory.append("<tr>");
                            i = 0;
                            for (Object o : columnNames) {
                                final String colName = o.toString();
                                boolean isGood = false;
                                String rowValue = item.getRowValue(colName, 1);
                                if (++i < 3) {
                                    continue;
                                }
                                if (!(StringUtils.isAlphanumeric(colName) || StringUtils.isAlphaSpace(colName))) {
                                    for (GoodRequestPosition position : positions) {
                                        if (position.getCurrentElementId() != null) {
                                            final boolean b = position.getCurrentElementId().equals(item.getIdOfGood());
                                            Calendar calendarDoneDate = Calendar.getInstance();
                                            calendarDoneDate.setTime(position.getGoodRequest().getDoneDate());
                                            CalendarUtils.truncateToDayOfMonth(calendarDoneDate);
                                            Date doneDate = calendarDoneDate.getTime();
                                            Calendar calendar = item.getColumnDate(colName);
                                            CalendarUtils.truncateToDayOfMonth(calendar);
                                            Date day = calendar.getTime();
                                            final boolean c = day.equals(doneDate);
                                            final boolean d = position.getOrgOwner().equals(item.getIdOfOrg());
                                            if (b && c && d) {
                                                isGood = true;
                                                doSend = doSend || isGood;
                                                break;
                                            }
                                        }
                                    }
                                }
                                newValueHistory.append("<td style='text-align: center;");
                                if (StringUtils.countMatches(colName, "Вс") > 0) {
                                    newValueHistory.append("background-color: #DFDFDF;");
                                }
                                if (isGood) {
                                    boolean isCreate = StringUtils.isEmpty(item.getRowLastValue(colName, 1));
                                    if (!isCreate) {
                                        int lastTotal = Integer.parseInt(item.getLastValue(colName));
                                        int lastDailySample = Integer.parseInt(item.getLastDailySample(colName));
                                        int currentTotal = Integer.parseInt(item.getValue(colName));
                                        int currentDailySample = Integer.parseInt(item.getDailySample(colName));
                                        isCreate = !(((lastTotal > 0) && (lastTotal != currentTotal)) || (
                                                (lastDailySample > 0) && (lastDailySample != currentDailySample)));
                                    }
                                    if (isCreate) {
                                        newValueHistory.append("background-color: darkgreen; color: #fff;");
                                    } else {
                                        newValueHistory.append("background-color: palevioletred; color: #fff;");
                                        rowValue = rowValue + " (" + item.getRowLastValue(colName, 1) + ")";
                                    }
                                }
                                newValueHistory.append("'>");
                                newValueHistory.append(rowValue);
                                newValueHistory.append("</td>");
                            }
                            newValueHistory.append("</tr>");
                        }

                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    } finally {
                        HibernateUtils.rollback(persistenceTransaction, LOGGER);
                        HibernateUtils.close(persistenceSession, LOGGER);
                    }
                    boolean sended = false;

                    if (doSend) {
                        try {
                            persistenceSession = runtimeContext.createReportPersistenceSession();
                            persistenceTransaction = persistenceSession.beginTransaction();
                            newValueHistory.append("</table>");
                            String[] values = {
                                    "address", orgShortItem.getAddress(), "shortOrgName", orgShortItem.getShortName(),
                                    "reportValues", newValueHistory.toString()};
                            List<String> strings = Arrays
                                    .asList(StringUtils.split(contragentTSPMap.get(contragent), ";"));
                            List<String> addresses = new ArrayList<String>(strings);

                            DetachedCriteria staffClientQuery = DetachedCriteria.forClass(Staff.class);
                            staffClientQuery.add(Restrictions.eq("orgOwner", idOfOrg));
                            staffClientQuery.add(Restrictions.eq("idOfRole", 0L));
                            staffClientQuery.setProjection(Property.forName("idOfClient"));
                            Criteria subCriteria = staffClientQuery.getExecutableCriteria(persistenceSession);
                            Integer countResult = subCriteria.list().size();
                            if (countResult > 0) {
                                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                                clientCriteria.add(Property.forName("idOfClient").in(staffClientQuery));
                                clientCriteria.setProjection(Property.forName("email"));
                                List<String> address = clientCriteria.list();
                                addresses.addAll(address);
                            }
                            for (String address : addresses) {
                                if (StringUtils.trimToNull(address) != null) {
                                    sended |= eventNotificationService.sendEmail(address, notificationType, values);
                                }
                            }
                            persistenceTransaction.commit();
                            persistenceTransaction = null;
                        } finally {
                            HibernateUtils.rollback(persistenceTransaction, LOGGER);
                            HibernateUtils.close(persistenceSession, LOGGER);
                        }

                    }

                    if (sended || !doSend) {
                        try {
                            persistenceSession = runtimeContext.createPersistenceSession();
                            persistenceTransaction = persistenceSession.beginTransaction();
                            if (currentOrgVersions.isEmpty()) {
                                String sql = "select max(globalVersion) from GoodRequestPosition p where p.orgOwner=:orgOwner";
                                Query query = persistenceSession.createQuery(sql);
                                query.setParameter("orgOwner", idOfOrg);
                                List maxVersions = query.list();
                                if (!(maxVersions.isEmpty() || maxVersions.get(0) == null)) {
                                    Object o = maxVersions.get(0);
                                    Long maxval = Long.valueOf(o.toString());
                                    DOCurrentOrgVersion version = new DOCurrentOrgVersion();
                                    version.setObjectId(DOCurrentOrgVersion.GOOD_REQUEST_POSITION);
                                    version.setIdOfOrg(idOfOrg);
                                    version.setLastVersion(maxval);
                                    persistenceSession.persist(version);
                                }
                            } else {
                                for (DOCurrentOrgVersion version : currentOrgVersions) {
                                    String sql = "select max(globalVersion) from GoodRequestPosition p where p.orgOwner=:orgOwner";
                                    Query query = persistenceSession.createQuery(sql);
                                    query.setParameter("orgOwner", version.getIdOfOrg());
                                    List maxVersions = query.list();
                                    if (!(maxVersions.isEmpty() || maxVersions.get(0) == null)) {
                                        Object o = maxVersions.get(0);
                                        Long maxval = Long.valueOf(o.toString());
                                        version.setLastVersion(maxval);
                                        persistenceSession.update(version);
                                    }
                                }
                            }
                            persistenceTransaction.commit();
                            persistenceTransaction = null;
                        } finally {
                            HibernateUtils.rollback(persistenceTransaction, LOGGER);
                            HibernateUtils.close(persistenceSession, LOGGER);
                        }
                    }

                }
            }
        } else {
            LOGGER.debug("Disable option notification good request change");
        }
        duration = System.currentTimeMillis() - duration;
        LOGGER.info("GoodRequestsNotificationService generateTime: " + duration);
    }

    private final static String notificationType = EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE;

    @Autowired
    private EventNotificationService eventNotificationService;

}
