/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
    // Возвращает полную стоимость питания на сегодня по заданным комплексам орг-ии.
    public Long sumComplexesPrice(List<Integer> complexIds, Org org) {
        Session session = entityManager.unwrap(Session.class);
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Date tomorrow = CalendarUtils.addDays(today, 1);
        // Ищем комплексы либо самой орг-ии, либо ее постащиков.
        Set<Org> sourceOrg = entityManager.find(Org.class, org.getIdOfOrg()).getSourceMenuOrgs();
        sourceOrg.add(org);
        Criteria criteria = session.createCriteria(ComplexInfo.class).add(Restrictions.in("org", sourceOrg))
                .add(Restrictions.eq("usedSubscriptionFeeding", 1)).add(Restrictions.in("idOfComplex", complexIds))
                .add(Restrictions.ge("menuDate", today)).add(Restrictions.lt("menuDate", tomorrow))
                .setProjection(Projections.sum("currentPrice"));
        return (Long) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    // Возвращает комплексы, участвующие в АП, для данной орг-ии.
    public List<ComplexInfo> findComplexesWithSubFeeding(Org org) {
        Session session = entityManager.unwrap(Session.class);
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Date tomorrow = CalendarUtils.addDays(today, 1);
        // Ищем комплексы либо самой орг-ии, либо ее постащиков.
        Set<Org> sourceOrg = entityManager.find(Org.class, org.getIdOfOrg()).getSourceMenuOrgs();
        sourceOrg.add(org);
        Criteria criteria = session.createCriteria(ComplexInfo.class).add(Restrictions.in("org", sourceOrg))
                .add(Restrictions.eq("usedSubscriptionFeeding", 1)).add(Restrictions.ge("menuDate", today))
                .add(Restrictions.lt("menuDate", tomorrow));
        return (List<ComplexInfo>) criteria.list();
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
    // Возвращает циклограмму питания, созданную позже всех.
    public CycleDiagram findLastCycleDiagram(Long contractId) {
        Session session = entityManager.unwrap(Session.class);
        Client c = DAOUtils.findClientByContractId(session, contractId);
        DetachedCriteria subQuery = DetachedCriteria.forClass(CycleDiagram.class).add(Restrictions.eq("client", c))
                .add(Restrictions.in("stateDiagram", new Object[]{StateDiagram.WAIT, StateDiagram.ACTIVE}))
                .add(Restrictions.eq("deletedState", false)).setProjection(Projections.max("globalId"));
        Criteria criteria = session.createCriteria(CycleDiagram.class).add(Subqueries.propertyEq("globalId", subQuery));
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
        sf.setLastDatePauseService(CalendarUtils.truncateToDayOfMonth(CalendarUtils.addDays(date, 2)));
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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Long getPriceOfDay(String dayComplexes, Org org) {
        if (StringUtils.isEmpty(dayComplexes)) {
            return 0L;
        }
        String[] complexIds = StringUtils.split(dayComplexes, ';');
        List<Integer> ids = new ArrayList<Integer>();
        for (String id : complexIds) {
            ids.add(Integer.valueOf(id));
        }
        return sumComplexesPrice(ids, org);
    }
}
