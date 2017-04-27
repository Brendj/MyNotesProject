/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeedingType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.truncateToDayOfMonth;

/**
 * Created with IntelliJ IDEA.
 * User: developer
 * Date: 08.10.13
 * Time: 15:44
 */

@Service("subscriptionFeedingService")
public class SubscriptionFeedingService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionFeedingService.class);

    public static SubscriptionFeedingService getInstance() {
        return RuntimeContext.getAppContext().getBean(SubscriptionFeedingService.class);
    }

    //private Calendar localCalendar;
    private DateFormat dateFormat;

    @PostConstruct
    public void init(){
        try {
            dateFormat = CalendarUtils.getDateFormatLocal();
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        }
        //try {
        //    localCalendar = RuntimeContext.getInstance().getLocalCalendar(null);
        //} catch (Exception e) {
        //    localCalendar = Calendar.getInstance();
        //}
    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Autowired
    private EventNotificationService enService;

    /**
     * Метод оповещает об отсутсвии средств на субсчете АП
     */
    public void notifyDeactivateClients(){
        SubscriptionFeedingService instance = SubscriptionFeedingService.getInstance();
        final Date currentDay = new Date();
        List list = instance.findNotifyClientsDeactivate(currentDay);
        if(list.isEmpty()) LOGGER.debug("clients empty");
        for (Object obj: list){
            Client client = (Client) obj;
            final String contractId = String.valueOf(client.getContractId());
            String[] values = new String[]{"contractId", contractId};
            enService.sendNotification(client, null, EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS, values, currentDay);
        }
    }

    /**
     * Метод оповещает о не достатке средств оплаты АП на ближайшие недели
     * При условии что дата отключения или паузы бедет более чем через неделю
     * TODO: добавить взятие даты паузы или отключения последней циклограммы для уточнения даты
     */
    public void notifyClients(){
        //  отключение
        if(1 == 1) {
            return;
        }

        SubscriptionFeedingService instance = SubscriptionFeedingService.getInstance();
        final Date currentDay = new Date();
        List list = instance.findNotifyClients(currentDay);
        if(list.isEmpty()) LOGGER.debug("clients empty");
        //Date lastWeekDay = new Date();
        //lastWeekDay = CalendarUtils.addDays(lastWeekDay, 7);
        for (Object obj: list){
            Object[] row = (Object[]) obj;
            Client client = (Client) row[0];
            CycleDiagram diagram = (CycleDiagram) row[1];
            final String contractId = String.format("%s01", String.valueOf(client.getContractId()));
            final Long subBalance = client.getSubBalance1();
            String[] weeks = new String[7];
            weeks[0] = diagram.getSundayPrice();
            weeks[1] = diagram.getMondayPrice();
            weeks[2] = diagram.getTuesdayPrice();
            weeks[3] = diagram.getWednesdayPrice();
            weeks[4] = diagram.getThursdayPrice();
            weeks[5] = diagram.getFridayPrice();
            weeks[6] = diagram.getSaturdayPrice();

            String[] val = null;
            long[] weeksPriseSummary = new long[7];

            for (int j = 0; j < weeks.length; j++) {
                if (weeks[j] != null) {
                    if (weeks[j].contains("|")) {
                        val = weeks[j].split("\\|");
                    } else {
                        val[0] = weeks[j];
                    }
                    for (String value : val) {
                        weeksPriseSummary[j] = weeksPriseSummary[j] + Long.parseLong(value);
                    }
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDay);
            long currentBalance = subBalance;
            int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] values = null;
            for (int i=dayWeek; i<dayWeek+7; i++){
                currentBalance = currentBalance-weeksPriseSummary[i%7];
                final Date date = CalendarUtils.addDays(currentDay, i - dayWeek+1);
                if(currentBalance<=0){
                    values = new String[]{"contractId", contractId, "withdrawDate", dateFormat.format(date),
                                          "balance", CurrencyStringUtils.copecksToRubles(subBalance)};
                    break;
                }
            }
            if(values!=null){
                enService.sendNotification(client, null, EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, values, currentDay);
            }
        }
    }


    /**
     * Метод возвращает список активных циклограмм на указанную дату
     * @param date дата
     * @return список подписок
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public List findNotifyClients(final Date date) {
        Date nextWeekDay = new Date();
        nextWeekDay = CalendarUtils.addDays(nextWeekDay, 7);
        final String sql = "select cl, cd from CycleDiagram cd left join cd.client cl where cd.stateDiagram=0 and "
                + " exists (from SubscriptionFeeding sf "
                + "  where sf.dateCreateService<:currentDate and sf.dateActivateSubscription<:currentDate  "
                + "  and (sf.lastDatePauseSubscription is null or sf.lastDatePauseSubscription>:nextWeekDay) "
                + "  and (sf.dateDeactivateService is null or sf.dateDeactivateService>:nextWeekDay) and sf.client=cl and sf.deletedState=false) "
                + " and not (cl.subBalance1 is null) and cl.subBalance1>0 and"
                //+ " cl.subBalance1-cd.mondayPrice-cd.tuesdayPrice-cd.wednesdayPrice-cd.thursdayPrice-cd.fridayPrice-cd.saturdayPrice-cd.sundayPrice<=0 and "
                + " cd.dateActivationDiagram = ( select max(incd.dateActivationDiagram) from CycleDiagram incd where incd.client=cl "
                + "and incd.dateActivationDiagram<:currentDate and incd.deletedState=false)";
        Query cycleDiagramTypedQuery = entityManager.createQuery(sql);
        cycleDiagramTypedQuery.setParameter("currentDate", date);
        cycleDiagramTypedQuery.setParameter("nextWeekDay", nextWeekDay);
        return cycleDiagramTypedQuery.getResultList();
    }


    /**
     * Метод возвращает список активных циклограмм на указанную дату
     * @param date дата
     * @return список подписок
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public List findNotifyClientsDeactivate(final Date date) {
        final String sql = "select cl from CycleDiagram cd left join cd.client cl where cd.stateDiagram=0 and "
                + " exists (from SubscriptionFeeding sf "
                + "  where sf.dateCreateService<:currentDate and sf.dateActivateSubscription<:currentDate  "
                + "  and (sf.lastDatePauseSubscription is null or sf.lastDatePauseSubscription>:currentDate) "
                + "  and (sf.dateDeactivateService is null or sf.dateDeactivateService>:currentDate) and sf.client=cl and sf.deletedState=false) "
                + " and not (cl.subBalance1 is null) and "
                + " cl.balance<=0 and "
                + " cd.dateActivationDiagram = ( select max(incd.dateActivationDiagram) from CycleDiagram incd where incd.client=cl "
                + "and incd.dateActivationDiagram<:currentDate and incd.deletedState=false)";
        Query cycleDiagramTypedQuery = entityManager.createQuery(sql);
        cycleDiagramTypedQuery.setParameter("currentDate", date);
        return cycleDiagramTypedQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает комплексы, участвующие в АП или ВП, для данной орг-ии.
    public List<ComplexInfo> findComplexesWithSubFeeding(Org org, boolean vp) {
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Date tomorrow = CalendarUtils.addOneDay(today);
        String sql;
        if (vp) {
            sql = "select distinct ci from ComplexInfo ci "
                    + "where ci.org = :org and usedVariableFeeding = 1 and menuDate >= :startDate and menuDate < :endDate";
        } else {
            sql = "select distinct ci from ComplexInfo ci "
                    + "where ci.org = :org and usedSubscriptionFeeding = 1 and menuDate >= :startDate and menuDate < :endDate";
        }
        TypedQuery<ComplexInfo> query = entityManager.createQuery(sql, ComplexInfo.class)
                .setParameter("org", org)
                .setParameter("startDate", today)
                .setParameter("endDate", tomorrow);
        List<ComplexInfo> res = query.getResultList();
        // Если комплексов на сегодня нет, то ищем их на каждый день в течение недели.
        int dayCount = 1;
        Date beginDate;
        Date endDate = tomorrow;
        while (res.isEmpty() && dayCount < 8) {
            beginDate = endDate;
            endDate = CalendarUtils.addDays(endDate, 1);
            res = query.setParameter("org", org).setParameter("startDate", beginDate).setParameter("endDate", endDate)
                    .getResultList();
            dayCount++;
        }
        return res;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean allowCreateByRootComplexes(Org org, List<Integer> complexIds, Date date) {
        Query query = entityManager.createQuery("select ci.rootComplex, count(distinct ci.idOfComplex) from ComplexInfo ci "
                + "where ci.usedVariableFeeding = 1 and ci.org = :org "
                + "and ci.idOfComplex in :complexIds and ci.menuDate >= :date group by ci.rootComplex")
                .setParameter("org", org)
                .setParameter("complexIds", complexIds)
                .setParameter("date", date);
        List res = query.getResultList();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            if ((Long)row[1] > 1) return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает подписку АП, действующую на текущий день.
    public SubscriptionFeeding findClientSubscriptionFeeding(Client c) {
        Session session = entityManager.unwrap(Session.class);
        Date now = new Date();
        DetachedCriteria subQuery = DetachedCriteria.forClass(SubscriptionFeeding.class);
        subQuery.add(Restrictions.eq("client", c));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.or(
                Restrictions.isNull("dateDeactivateService"),
                Restrictions.gt("dateDeactivateService", now))
        );
        subQuery.setProjection(Projections.max("dateActivateService"));
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Subqueries.propertyEq("dateActivateService", subQuery));
        return (SubscriptionFeeding) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает подписку АП, действующую на текущий день.
    public List<SubscriptionFeeding> findSubscriptionFeedingByClient(Client c) {
        Session session = entityManager.unwrap(Session.class);
        Date now = new Date();
        DetachedCriteria subQuery = DetachedCriteria.forClass(SubscriptionFeeding.class);
        subQuery.add(Restrictions.eq("client", c));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.isNotNull("dateActivateService"));
        subQuery.add(Restrictions.le("dateActivateService", now));
        subQuery.add(Restrictions.or(
                Restrictions.isNull("dateDeactivateService"),
                Restrictions.gt("dateDeactivateService", now)
        ));
        subQuery.setProjection(Projections.max("dateCreateService"));
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Subqueries.propertyEq("dateActivateService", subQuery));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SubscriptionFeeding> findSubscriptionFeedingByClient(Client c, Date currentDate) {
        Session session = entityManager.unwrap(Session.class);
        DetachedCriteria subQuery = DetachedCriteria.forClass(SubscriptionFeeding.class);
        subQuery.add(Restrictions.eq("client", c));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.le("dateCreateService", currentDate));
        subQuery.add(Restrictions.or(
                Restrictions.isNull("dateDeactivateService"),
                Restrictions.gt("dateDeactivateService", currentDate)
        ));
        subQuery.setProjection(Projections.max("dateCreateService"));
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Subqueries.propertyEq("dateCreateService", subQuery));
        return criteria.list();
    }

    public static SubscriptionFeeding getCurrentSubscriptionFeedingByClientToDay(Session session, Client c, Date currentDate, Integer type) {
        DetachedCriteria subQuery = DetachedCriteria.forClass(SubscriptionFeeding.class);
        subQuery.add(Restrictions.eq("client", c));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.le("dateCreateService", currentDate));
        if (type != null) {
            subQuery.add(Restrictions.eq("feedingType", SubscriptionFeedingType.fromInteger(type)));
        }
        subQuery.add(Restrictions.or(
              Restrictions.isNull("dateDeactivateService"),
              Restrictions.gt("dateDeactivateService", currentDate)
        ));
        subQuery.setProjection(Projections.max("dateCreateService"));
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Restrictions.eq("deletedState", false));
        if (type != null) {
            criteria.add(Restrictions.eq("feedingType", SubscriptionFeedingType.fromInteger(type)));
        }
        criteria.add(Subqueries.propertyEq("dateCreateService", subQuery));
        List list = criteria.list();
        ArrayList<SubscriptionFeeding> subscriptionFeedings = new ArrayList<SubscriptionFeeding>(list.size());
        for (Object obj: list){
            SubscriptionFeeding subscriptionFeeding = (SubscriptionFeeding) obj;
            subscriptionFeedings.add(subscriptionFeeding);
        }
        Collections.sort(subscriptionFeedings, new Comparator<SubscriptionFeeding>() {
            @Override public int compare(SubscriptionFeeding o1, SubscriptionFeeding o2) {
                return o2.getGlobalId().compareTo(o1.getGlobalId());
            }
        });
        SubscriptionFeeding subscriptionFeeding = null;
        for (SubscriptionFeeding sf: subscriptionFeedings){
            if(sf.getLastDatePauseSubscription()!=null){
                break;
            }
            subscriptionFeeding = sf;
        }
        if(subscriptionFeeding==null && !subscriptionFeedings.isEmpty()){
            subscriptionFeeding = subscriptionFeedings.get(subscriptionFeedings.size()-1);
        }
        return subscriptionFeeding;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SubscriptionFeeding> findSubscriptionFeedingByClient(Client c, Date startDate, Date endDate) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.or(
                Restrictions.between("createdDate", startDate, endDate),
                Restrictions.between("lastUpdate", startDate, endDate)
        ));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму питания, актуальную на текущий день.
    public CycleDiagram findClientCycleDiagram(Client c) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("stateDiagram", StateDiagram.ACTIVE));
        return (CycleDiagram) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму питания, актуальную на текущий день.
    public CycleDiagram findActiveCycleDiagram(Client c, Date day) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", c));
        criteria.add(Restrictions.eq("dateActivationDiagram", day));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("stateDiagram", StateDiagram.ACTIVE));
        return (CycleDiagram) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public CycleDiagram findCycleDiagramOnDate(Client client, Date date) {
        Date dateActivation = CalendarUtils.truncateToDayOfMonth(date);
        Date tomorrow = CalendarUtils.addDays(dateActivation, 1);
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.ge("dateActivationDiagram", dateActivation));
        criteria.add(Restrictions.lt("dateActivationDiagram", tomorrow));
        return (CycleDiagram) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public CycleDiagram findCycleDiagramByClient(Client client) {
        Date currentDay = CalendarUtils.truncateToDayOfMonth(new Date());
        Session session = entityManager.unwrap(Session.class);
        DetachedCriteria subQuery = DetachedCriteria.forClass(CycleDiagram.class);
        subQuery.add(Restrictions.eq("client", client));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}));
        subQuery.add(Restrictions.le("dateActivationDiagram", currentDay));
        subQuery.setProjection(Projections.max("dateActivationDiagram"));
        if(subQuery.getExecutableCriteria(session).uniqueResult()!=null){
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("deletedState", false));
            criteria.add(Subqueries.propertyEq("dateActivationDiagram", subQuery));
            return (CycleDiagram) criteria.uniqueResult();
        } else return null;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public List<CycleDiagram> findCycleDiagramsByClient(Client client, Integer type) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}));
        if (type != null) {
            criteria.add(Restrictions.eq("feedingType", SubscriptionFeedingType.fromInteger(type)));
        }
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public List<CycleDiagram> findCycleDiagramsByClient(Client client, Date startDate, Date endDate, Integer type) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class);
        criteria.add(Restrictions.eq("client", client));
        if (type != null) {
            criteria.add(Restrictions.eq("feedingType", SubscriptionFeedingType.fromInteger(type)));
        }
        criteria.add(Restrictions.or(
                Restrictions.between("createdDate", startDate, endDate),
                Restrictions.between("lastUpdate", startDate, endDate)
        ));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public CycleDiagram findCycleDiagramByClient(Client client, Date currentDay) {
        currentDay = CalendarUtils.truncateToDayOfMonth(currentDay);
        Session session = entityManager.unwrap(Session.class);
        DetachedCriteria subQuery = DetachedCriteria.forClass(CycleDiagram.class);
        subQuery.add(Restrictions.eq("client", client));
        subQuery.add(Restrictions.eq("deletedState", false));
        subQuery.add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}));
        subQuery.add(Restrictions.ge("dateActivationDiagram", currentDay));
        subQuery.setProjection(Projections.min("dateActivationDiagram"));
        if(subQuery.getExecutableCriteria(session).uniqueResult()!=null){
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("deletedState", false));
            criteria.add(Subqueries.propertyEq("dateActivationDiagram", subQuery));
            return (CycleDiagram) criteria.uniqueResult();
        } else return null;
    }


    @Transactional(rollbackFor = Exception.class)
    // Приостанавливает подписку АП.
    public void suspendSubscriptionFeeding(Client client) throws Exception {
        //Date date = new Date();
        DAOService daoService = DAOService.getInstance();
        List<ECafeSettings> settings = daoService
                .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
        ECafeSettings cafeSettings = settings.get(0);
        SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
        //Date today = truncateToDayOfMonth(new Date());
        //Date date = CalendarUtils.addDays(today, 1 + parser.getDayForbidChange());
        Date currentDay = new Date();

        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        final int hoursForbidChange = parser.getHoursForbidChange();
        int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
        Date dayForbid = CalendarUtils.addDays(currentDay, dayForbidChange);
        if(dayForbid.getHours()>=12){
            dayForbid = CalendarUtils.addOneDay(currentDay);
        }
        sf.setLastDatePauseSubscription(dayForbid);
        sf.setWasSuspended(true);
        //DAOService daoService = DAOService.getInstance();
        sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        sf.setLastUpdate(currentDay);
        //sf.setReasonWasSuspended(reasonWasSuspended);
        entityManager.merge(sf);
    }

    @Transactional(rollbackFor = Exception.class)
    // Приостанавливает подписку АП.
    public void suspendSubscriptionFeeding(Client client, Date endPauseDate) throws Exception {
        DAOService daoService = DAOService.getInstance();
        List<ECafeSettings> settings = daoService
                .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
        ECafeSettings cafeSettings = settings.get(0);
        SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
        Date today = truncateToDayOfMonth(endPauseDate);

        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        sf.setLastDatePauseSubscription(today);
        sf.setWasSuspended(true);
        sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        sf.setLastUpdate(new Date());
        entityManager.merge(sf);
    }


    @Transactional(rollbackFor = Exception.class)
    // Возобновляет подписку АП.
    public void reopenSubscriptionFeeding(Client client) {
        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        sf.setWasSuspended(false);
        DAOService daoService = DAOService.getInstance();
        sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        sf.setLastUpdate(new Date());
        entityManager.merge(sf);
    }

    @Transactional(rollbackFor = Exception.class)
    // Возобновляет подписку АП.
    public void reopenSubscriptionFeeding(Client client, Date endReopenDate) {
        SubscriptionFeeding lassf = findClientSubscriptionFeeding(client);
        DAOService daoService = DAOService.getInstance();
        SubscriptionFeeding sf = new SubscriptionFeeding();
        sf.setClient(client);
        sf.setOrgOwner(lassf.getOrgOwner());
        sf.setIdOfClient(client.getIdOfClient());
        sf.setDateActivateSubscription(endReopenDate);
        sf.setDateCreateService(lassf.getDateCreateService());
        sf.setDateDeactivateService(lassf.getDateDeactivateService());
        sf.setDeletedState(false);
        sf.setSendAll(SendToAssociatedOrgs.SendToSelf);
        sf.setWasSuspended(false);
        sf.setCreatedDate(new Date());
        Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
        sf.setGlobalVersionOnCreate(version);
        sf.setGlobalVersion(version);
        entityManager.persist(sf);
        //sf.setWasSuspended(false);
        //sf.setLastDatePauseService(null);
        //DAOService daoService = DAOService.getInstance();
        //sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        //sf.setLastUpdate(new Date());
        //entityManager.merge(sf);
    }


    @Transactional(rollbackFor = Exception.class)
    // Возобновляет подписку АП.
    public void cancelSubscriptionFeeding(Client client) {
        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        sf.setWasSuspended(false);
        sf.setLastDatePauseSubscription(null);
        DAOService daoService = DAOService.getInstance();
        sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        sf.setLastUpdate(new Date());
        entityManager.merge(sf);
    }

    @Transactional(rollbackFor = Exception.class)
    // Подключает подписку на АП. Создает также первую циклограмму.
    public SubscriptionFeeding createSubscriptionFeeding(Client client, Org org, String monday, String tuesday,
            String wednesday, String thursday, String friday, String saturday, Date newDateActivateService,
            Date dateCreateService) {
        DAOService daoService = DAOService.getInstance();
        Date date = new Date();
        Date dayBegin = CalendarUtils.truncateToDayOfMonth(date);
        SubscriptionFeeding sf = new SubscriptionFeeding();
        sf.setCreatedDate(date);
        sf.setClient(client);
        sf.setOrgOwner(org.getIdOfOrg());
        sf.setIdOfClient(client.getIdOfClient());
        sf.setDateActivateSubscription(newDateActivateService);
        sf.setDateCreateService(dateCreateService);
        sf.setDeletedState(false);
        sf.setSendAll(SendToAssociatedOrgs.SendToSelf);
        sf.setWasSuspended(false);
        Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
        sf.setGlobalVersionOnCreate(version);
        sf.setGlobalVersion(version);
        sf.setStaff(null);
        entityManager.persist(sf);
        CycleDiagram cd = findClientCycleDiagram(client);
        // Если осталась активная циклограмма со старой подписки, то ее необходимо удалить.
        // Данная опираеция проводится на стороне АРМ
        // Активируем циклограмму сегодняшним днем.
        // Создаем циклограмму если есть на клиента циклограммы то создаем с ожидаем если нет то активную
        createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday, dayBegin, cd==null);

        return sf;
    }

    @Transactional(rollbackFor = Exception.class)
    // Подключает подписку на АП. Создает также первую циклограмму.
    public CycleDiagram editSubscriptionFeeding(Client client, Org org, String monday, String tuesday, String wednesday,
            String thursday, String friday, String saturday, Date dateActivationDiagram) {
        DAOService daoService = DAOService.getInstance();
        Date date = new Date();
        Date dayBegin = CalendarUtils.truncateToDayOfMonth(date);
        List<SubscriptionFeeding> list = findSubscriptionFeedingByClient(client, dayBegin);
        SubscriptionFeeding sf = list.get(0);
        sf.setDateActivateSubscription(dateActivationDiagram);
        Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
        sf.setGlobalVersionOnCreate(version);
        sf.setGlobalVersion(version);
        sf.setStaff(null);
        entityManager.persist(sf);
        return createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday, dateActivationDiagram, true);

        //return sf;
    }

    @Transactional(rollbackFor = Exception.class)
    // Создает новую циклограмму.
    public CycleDiagram createCycleDiagram(Client client, Org org, String monday, String tuesday, String wednesday,
            String thursday, String friday, String saturday, Date dateActivationDiagram, boolean active) {
        DAOService daoService = DAOService.getInstance();
        CycleDiagram cd = new CycleDiagram();
        cd.setCreatedDate(new Date());
        cd.setClient(client);
        cd.setOrgOwner(org.getIdOfOrg());
        cd.setIdOfClient(client.getIdOfClient());
        cd.setDateActivationDiagram(dateActivationDiagram);
        if (active) {
            cd.setStateDiagram(StateDiagram.ACTIVE);
        } else {
            cd.setStateDiagram(StateDiagram.WAIT);
        }
        cd.setDeletedState(false);
        cd.setSendAll(SendToAssociatedOrgs.SendToSelf);
        Long version = daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
        cd.setGlobalVersionOnCreate(version);
        cd.setGlobalVersion(version);
        List<ComplexInfo> availableComplexes = findComplexesWithSubFeeding(org, false);
        cd.setMonday(monday);
        cd.setMondayPrice(getPriceOfDay(monday, availableComplexes));
        cd.setTuesday(tuesday);
        cd.setTuesdayPrice(getPriceOfDay(tuesday, availableComplexes));
        cd.setWednesday(wednesday);
        cd.setWednesdayPrice(getPriceOfDay(wednesday, availableComplexes));
        cd.setThursday(thursday);
        cd.setThursdayPrice(getPriceOfDay(thursday, availableComplexes));
        cd.setFriday(friday);
        cd.setFridayPrice(getPriceOfDay(friday, availableComplexes));
        cd.setSaturday(saturday);
        cd.setSaturdayPrice(getPriceOfDay(saturday, availableComplexes));
        cd.setSunday("");
        cd.setSundayPrice("0");
        cd.setStaff(null);
        entityManager.persist(cd);
        return cd;
    }



    @Transactional(rollbackFor = Exception.class)
    // Создает новую циклограмму. Если на дату ее активации уже есть цилкограмма, то переводит ее в удаленные.
    // Если есть циклограмма на эту дату то ее редактируем
    public CycleDiagram editCycleDiagram(Client client, Org org, String monday, String tuesday, String wednesday,
            String thursday, String friday, String saturday, Date dateActivationDiagram) {
        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        CycleDiagram cd = findCycleDiagramOnDate(client, dateActivationDiagram);
        Date currentDate = new Date();
        //  if(!( new Date() between  dateActivateService and dateDeActivateService)) {
        //     редактируем
        //  }
        if(!(currentDate.after(sf.getDateActivateSubscription()) && sf.isActual())){
            if(cd==null){
                cd = createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday,
                        dateActivationDiagram, false);
            } else {
                editCycleDiagram(org, cd, monday, tuesday, wednesday, thursday, friday, saturday);
                entityManager.merge(cd);
            }
        } else {
            //  else {
            //    создаем копию с отложеной деактивацией. new cd.setStateDiagram(StateDiagram.WAIT);
            //    с вычислением даты активации
            // }
            if(cd==null || cd.isActual()){
                cd = createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday,
                        dateActivationDiagram, false);
            } else {
                editCycleDiagram(org, cd, monday, tuesday, wednesday, thursday, friday, saturday);
                entityManager.merge(cd);
            }
        }

        cd.setStaff(null);

        //if (cd != null) {
        //    cd.setDeletedState(true);
        //    cd.setStateDiagram(StateDiagram.BLOCK);
        //    cd.setGlobalVersion(daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName()));
        //    entityManager.merge(cd);
        //}
        //cd = createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday,
        //        dateActivationDiagram, false);
        return cd;
    }

    private void editCycleDiagram(Org org, CycleDiagram cd, String monday, String tuesday, String wednesday,
            String thursday, String friday, String saturday) {
        DAOService daoService = DAOService.getInstance();
        cd.setGlobalVersion(daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName()));
        List<ComplexInfo> availableComplexes = findComplexesWithSubFeeding(org, false);
        cd.setMonday(monday);
        cd.setMondayPrice(getPriceOfDay(monday, availableComplexes));
        cd.setTuesday(tuesday);
        cd.setTuesdayPrice(getPriceOfDay(tuesday, availableComplexes));
        cd.setWednesday(wednesday);
        cd.setWednesdayPrice(getPriceOfDay(wednesday, availableComplexes));
        cd.setThursday(thursday);
        cd.setThursdayPrice(getPriceOfDay(thursday, availableComplexes));
        cd.setFriday(friday);
        cd.setFridayPrice(getPriceOfDay(friday, availableComplexes));
        cd.setSaturday(saturday);
        cd.setSaturdayPrice(getPriceOfDay(saturday, availableComplexes));
        cd.setSunday("");
        cd.setSundayPrice("0");
        cd.setStaff(null);
    }

    // Возвращает полную стоимость питания на сегодня по заданным комплексам орг-ии.
    public String getPriceOfDay(String dayComplexes, List<ComplexInfo> availableComplexes) {
        if (StringUtils.isEmpty(dayComplexes)) {
            return "0";
        }
        String[] weekComplexIds = StringUtils.split(dayComplexes, "\\|");
        String realPrice = "";
        for (int i = 0; i < weekComplexIds.length; i++) {

            String[] complexIds = StringUtils.split(weekComplexIds[i], ';');
            List<Integer> ids = new ArrayList<Integer>();
            for (String id : complexIds) {
                ids.add(Integer.valueOf(id));
            }
            long price = 0L;
            for (ComplexInfo ci : availableComplexes) {
                if (ids.contains(ci.getIdOfComplex())) {
                    price += ci.getCurrentPrice();
                }
            }
            if (i == weekComplexIds.length - 1)  {
            realPrice = realPrice.concat(String.valueOf(price));
            } else {
                realPrice = realPrice.concat(String.valueOf(price) + "|");
            }
        }
        return realPrice;
    }

}
