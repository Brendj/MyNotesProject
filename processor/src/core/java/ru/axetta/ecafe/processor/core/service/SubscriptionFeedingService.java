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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: developer
 * Date: 08.10.13
 * Time: 15:44
 */

@Service("subscriptionFeedingService")
public class SubscriptionFeedingService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionFeedingService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Autowired
    private EventNotificationService enService;

    @Transactional
    public void notifyClientsAboutSubscriptionFeeding() throws Exception {
        final Date currentDate = new Date();
        final Date withdrawDate = CalendarUtils.addDays(currentDate, 7);
        DateFormat df = CalendarUtils.getDateFormatLocal();
        String withdrawDateStr = df.format(withdrawDate);
        String sql = "from CycleDiagram ccd where ccd.stateDiagram=0";
        TypedQuery<CycleDiagram> typedQuery = entityManager.createQuery(sql, CycleDiagram.class);
        List<CycleDiagram> cycleDiagrams = typedQuery.getResultList();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("cycleDiagrams count=" + cycleDiagrams.size());
        }
        if(!cycleDiagrams.isEmpty()){
            for (CycleDiagram cycleDiagram: cycleDiagrams){
                final Client client = cycleDiagram.getClient();
                Long subBalance = client.getSubBalance1();
                Date deactivateDate = null;
                sql = "from SubscriptionFeeding where wasSuspended=false and client=:client and dateDeactivateService>=:currentDate";
                TypedQuery<SubscriptionFeeding> subscriptionFeedingQuery = entityManager.createQuery(sql, SubscriptionFeeding.class);
                subscriptionFeedingQuery.setParameter("client", client);
                subscriptionFeedingQuery.setParameter("currentDate", currentDate);
                subscriptionFeedingQuery.setMaxResults(1);
                List<SubscriptionFeeding> subscriptionFeedings = subscriptionFeedingQuery.getResultList();
                if(subscriptionFeedings!=null && !subscriptionFeedings.isEmpty()){
                    deactivateDate = subscriptionFeedings.get(0).getDateDeactivateService();
                }
                boolean sendNotification = true;
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("SubscriptionFeeding deactivateDate=" + deactivateDate);
                }
                if(deactivateDate!=null){
                    sendNotification = deactivateDate.compareTo(withdrawDate)>0;
                }
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("sendNotification=" + sendNotification);
                }
                final String contractId = String.format("%s01", String.valueOf(client.getContractId()));
                if(sendNotification){
                    if(subBalance==null || subBalance==0L){
                        withdrawDateStr = df.format(CalendarUtils.addDays(currentDate, 1));
                        subBalance = 0L;
                    }
                    if(subBalance - cycleDiagram.getWeekPrice()<0){
                        String[] values = {
                                "contractId", contractId, "withdrawDate", withdrawDateStr,
                                "balance", CurrencyStringUtils.copecksToRubles(subBalance)};
                        enService.sendNotification(client, EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, values);
                    }
                } else {
                    if(deactivateDate!=null){
                        long[] days = new long[7];
                        days[0] = cycleDiagram.getSundayPrice();
                        days[1] = cycleDiagram.getMondayPrice();
                        days[2] = cycleDiagram.getTuesdayPrice();
                        days[3] = cycleDiagram.getWednesdayPrice();
                        days[4] = cycleDiagram.getThursdayPrice();
                        days[5] = cycleDiagram.getFridayPrice();
                        days[6] = cycleDiagram.getSaturdayPrice();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(currentDate);
                        final long currentBalance = subBalance;
                        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("Current Day: "+ dayWeek);
                        }
                        for (int i=dayWeek; i<dayWeek+7; i++){
                            subBalance = subBalance-days[i%7];
                            final Date date = CalendarUtils.addDays(currentDate, i - 5);
                            if(subBalance-days[i%7]<0 && date.compareTo(deactivateDate)<0 ){
                                withdrawDateStr = df.format(date);
                                String[] values = {"contractId", contractId, "withdrawDate", withdrawDateStr,
                                                   "balance", CurrencyStringUtils.copecksToRubles(currentBalance)};
                                enService.sendNotification(client, EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, values);
                                break;
                            }

                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает комплексы, участвующие в АП, для данной орг-ии.
    public List<ComplexInfo> findComplexesWithSubFeeding(Org org) {
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Date tomorrow = CalendarUtils.addDays(today, 1);
        TypedQuery<ComplexInfo> query = entityManager.createQuery("select distinct ci from ComplexInfo ci "
                + "where ci.org = :org and usedSubscriptionFeeding = 1 and menuDate >= :startDate and menuDate < :endDate",
                ComplexInfo.class).setParameter("org", org).setParameter("startDate", today)
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

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает подписку АП, действующую на текущий день.
    public SubscriptionFeeding findClientSubscriptionFeeding(Client c) {
        Session session = entityManager.unwrap(Session.class);
        Date now = new Date();
        DetachedCriteria subQuery = DetachedCriteria.forClass(SubscriptionFeeding.class)
                .add(Restrictions.eq("client", c)).add(Restrictions.eq("deletedState", false)).add(Restrictions
                    .or(Restrictions.isNull("dateDeactivateService"), Restrictions.gt("dateDeactivateService", now)))
                .setProjection(Projections.max("dateActivateService"));
        Criteria criteria = session.createCriteria(SubscriptionFeeding.class).add(Restrictions.eq("client", c))
                .add(Subqueries.propertyEq("dateActivateService", subQuery));
        return (SubscriptionFeeding) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму питания, актуальную на текущий день.
    public CycleDiagram findClientCycleDiagram(Client c) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class).add(Restrictions.eq("client", c))
                .add(Restrictions.eq("deletedState", false)).add(Restrictions.eq("stateDiagram", StateDiagram.ACTIVE));
        return (CycleDiagram) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает циклограмму, действующую в определенный день.
    public CycleDiagram findCycleDiagramOnDate(Client client, Date date) {
        Date dateActivation = CalendarUtils.truncateToDayOfMonth(date);
        Date tomorrow = CalendarUtils.addDays(dateActivation, 1);
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CycleDiagram.class).add(Restrictions.eq("client", client))
                .add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}))
                .add(Restrictions.eq("deletedState", false))
                .add(Restrictions.ge("dateActivationDiagram", dateActivation))
                .add(Restrictions.lt("dateActivationDiagram", tomorrow));
        return (CycleDiagram) criteria.uniqueResult();
    }

    @Transactional(rollbackFor = Exception.class)
    // Приостанавливает подписку АП.
    public void suspendSubscriptionFeeding(Client client) {
        Date date = new Date();
        SubscriptionFeeding sf = findClientSubscriptionFeeding(client);
        sf.setLastDatePauseService(date.before(sf.getDateActivateService()) ? sf.getDateActivateService() : date);
        sf.setWasSuspended(true);
        DAOService daoService = DAOService.getInstance();
        sf.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
        sf.setLastUpdate(date);
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
    // Подключает подписку на АП. Создает также первую циклограмму.
    public SubscriptionFeeding createSubscriptionFeeding(Client client, Org org, String monday, String tuesday,
            String wednesday, String thursday, String friday, String saturday,
            SubscriberFeedingSettingSettingValue parser) {
        DAOService daoService = DAOService.getInstance();
        Date date = new Date();
        Date dayBegin = CalendarUtils.truncateToDayOfMonth(date);
        SubscriptionFeeding sf = new SubscriptionFeeding();
        sf.setCreatedDate(date);
        sf.setClient(client);
        sf.setOrgOwner(org.getIdOfOrg());
        sf.setIdOfClient(client.getIdOfClient());
        sf.setGuid(UUID.randomUUID().toString());
        Date dateActivateService = CalendarUtils.addDays(dayBegin, 1 + parser.getDayForbidChange());
        // Если день активации выпадает на выходной - воскресенье, то берем понедельник.
        if (!CalendarUtils.isWorkingDate(dateActivateService)) {
            dateActivateService = CalendarUtils.addDays(dateActivateService, 1);
        }
        sf.setDateActivateService(dateActivateService);
        sf.setDeletedState(false);
        sf.setSendAll(SendToAssociatedOrgs.SendToSelf);
        sf.setWasSuspended(false);
        Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
        sf.setGlobalVersionOnCreate(version);
        sf.setGlobalVersion(version);
        entityManager.persist(sf);
        CycleDiagram cd = findClientCycleDiagram(client);
        // Если осталась активная циклограмма со старой подписки, то ее необходимо удалить.
        if (cd != null) {
            cd.setDeletedState(true);
            cd.setStateDiagram(StateDiagram.BLOCK);
            cd.setGlobalVersion(daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName()));
            entityManager.merge(cd);
        }
        // Активируем циклограмму сегодняшним днем.
        createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday, dayBegin, true);
        return sf;
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
        cd.setGuid(UUID.randomUUID().toString());
        cd.setDeletedState(false);
        cd.setSendAll(SendToAssociatedOrgs.SendToSelf);
        Long version = daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
        cd.setGlobalVersion(version);
        cd.setGlobalVersionOnCreate(version);
        List<ComplexInfo> availableComplexes = findComplexesWithSubFeeding(org);
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
        cd.setSundayPrice(0L);
        entityManager.persist(cd);
        return cd;
    }

    @Transactional(rollbackFor = Exception.class)
    // Создает новую циклограмму. Если на дату ее активации уже есть цилкограмма, то переводит ее в удаленные.
    public CycleDiagram editCycleDiagram(Client client, Org org, String monday, String tuesday, String wednesday,
            String thursday, String friday, String saturday, Date dateActivationDiagram) {
        DAOService daoService = DAOService.getInstance();
        CycleDiagram cd = findCycleDiagramOnDate(client, dateActivationDiagram);
        if (cd != null) {
            cd.setDeletedState(true);
            cd.setStateDiagram(StateDiagram.BLOCK);
            cd.setGlobalVersion(daoService.updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName()));
            entityManager.merge(cd);
        }
        cd = createCycleDiagram(client, org, monday, tuesday, wednesday, thursday, friday, saturday,
                dateActivationDiagram, false);
        return cd;
    }

    // Возвращает полную стоимость питания на сегодня по заданным комплексам орг-ии.
    private Long getPriceOfDay(String dayComplexes, List<ComplexInfo> availableComplexes) {
        if (StringUtils.isEmpty(dayComplexes)) {
            return 0L;
        }
        String[] complexIds = StringUtils.split(dayComplexes, ';');
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
        return price;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public void activateCycleDiagrams() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Date tomorrow = CalendarUtils.addDays(today, 1);
        Query updateQuery = entityManager.createQuery(
                "update CycleDiagram cd set cd.stateDiagram = :active where cd.stateDiagram = :wait and cd.deletedState = false and "
                        + "cd.dateActivationDiagram >= :today and cd.dateActivationDiagram < :tomorrow and cd.client.idOfClient = :id");
        Query deleteQuery = entityManager.createQuery(
                "update CycleDiagram cd set cd.stateDiagram = :block, cd.deletedState = true where cd.client.idOfClient = :id and cd.dateActivationDiagram < :today");
        Query query = entityManager.createQuery(
                "select cd.client.idOfClient from CycleDiagram cd where cd.stateDiagram = :active and cd.deletedState = false")
                .setParameter("active", StateDiagram.ACTIVE);
        List<Long> clientIds = (List<Long>) query.getResultList();
        int activatedCount = 0;
        int blockedCount = 0;
        for (Long clientId : clientIds) {
            int count = updateQuery.setParameter("active", StateDiagram.ACTIVE).setParameter("wait", StateDiagram.WAIT)
                    .setParameter("today", today).setParameter("tomorrow", tomorrow).setParameter("id", clientId)
                    .executeUpdate();
            if (count != 0) {
                blockedCount += deleteQuery.setParameter("id", clientId).setParameter("today", today).executeUpdate();
            }
        }
        LOGGER.info("Today activated cycle diagrams count: {}", activatedCount);
        LOGGER.info("Today blocked cycle diagrams count: {}", blockedCount);
    }
}
