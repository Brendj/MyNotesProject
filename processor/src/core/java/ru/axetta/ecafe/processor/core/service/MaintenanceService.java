/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("singleton")
public class MaintenanceService {

    private final static ThreadLocal<Logger> logger = new ThreadLocal<Logger>(){
        @Override protected Logger initialValue() { return LoggerFactory.getLogger(MaintenanceService.class); }
    };

    private int MAXROWS;
    private static final AtomicLong threadCounter = new AtomicLong();
    private static final int daysMinus = 45;
    private static final String CLEAR_MENU_THREAD_COUNT_PROPERTY = "ecafe.processor.clearmenu.maintenanceservice.thread.count";
    private static final String CLEAR_MENU_PRIORITY_ORGS = "ecafe.processor.clearmenu.maintenanceservice.orgs";
    private static final Set<Long> orgsInProgress = Collections.synchronizedSet(new HashSet<Long>());
    private static final Object sync = new Object();

    @Resource(name = "clearMenuExecutor")
    protected ThreadPoolTaskExecutor taskExecutor;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private MaintenanceService getProxy() {
        return RuntimeContext.getAppContext().getBean(MaintenanceService.class);
    }

    public static class ClearingMenu implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(MaintenanceService.class).run();
        }
    }

    public void scheduleSync() throws Exception {
        String syncScheduleClear = RuntimeContext.getInstance().getConfigProperties()
                .getProperty("ecafe.processor.clearmenu.maintenanceservice.cron", "");

        try {
            MAXROWS = Integer.valueOf(RuntimeContext.getInstance().getConfigProperties()
                    .getProperty("ecafe.processor.clearmenu.maintenanceservice.size", ""));
        } catch (Exception e) {
            MAXROWS = 10;
        }
        try {
            JobDetail jobDetailEndBenefit = new JobDetail("ClearMenu", Scheduler.DEFAULT_GROUP,
                    MaintenanceService.ClearingMenu.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncScheduleClear.equals("")) {
                CronTrigger triggerEndBenefit = new CronTrigger("ClearMenu", Scheduler.DEFAULT_GROUP);
                triggerEndBenefit.setCronExpression(syncScheduleClear);
                if (scheduler.getTrigger("ClearMenu", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("ClearMenu", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(jobDetailEndBenefit, triggerEndBenefit);
            }
            scheduler.start();
        } catch (Exception e) {
            logger.get().error("Failed to schedule cleaning menu service job:", e);
        }
    }

    public void wrapRun() throws Exception {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        int tCount = RuntimeContext.getInstance().getPropertiesValue(CLEAR_MENU_THREAD_COUNT_PROPERTY, 1);
        if (tCount == 1)
            runVersion2();
        else
            runVersion2MultiThread(tCount);
    }

    public void runVersion2MultiThread(int coreSize) throws Exception {
        taskExecutor.setCorePoolSize(coreSize);
        for (int i = 0; i < coreSize; i++) {
            ClearMenuThreadWrapper wrapper = new ClearMenuThreadWrapper();
            taskExecutor.execute(wrapper);
            Thread.sleep(10000L); //запуск с разницей в 10 секунд
        }
    }

    private boolean getOrgInProgressUsed(Long id) {
        for (Long org : orgsInProgress) {
            if (org.equals(id)) return true;
        }
        return false;
    }

    public void runVersion2() {
        Session session = null;
        Transaction transaction = null;
        Date dateTime = CalendarUtils.addDays(new Date(), -daysMinus);
        dateTime = CalendarUtils.startOfDay(dateTime);
        try {
            DAOService.getInstance().createStatTable();
            //Проверяем само значение ключа
            String orgs = RuntimeContext.getInstance().getPropertiesValue(CLEAR_MENU_PRIORITY_ORGS, "");
            //Список разрешенных ip
            String[] orgsId = orgs.split(";");
            List<Long> orgList = new ArrayList<>();
            for (String orgId: orgsId)
            {
                try {
                    orgList.add(Long.valueOf(orgId));
                } catch (Exception e){
                }
            }
            orgList.addAll(DAOService.getInstance().getOrgIdsForClearMenu());
            logger.get().info(String.format("Found %s orgs to clear menu ", orgList.size()));
            for (Long id : orgList) {
                boolean idUsed;
                synchronized (sync) {
                    idUsed = getOrgInProgressUsed(id);
                    if (!idUsed) orgsInProgress.add(id);
                }
                if (idUsed) continue;
                Thread.currentThread().setName("ClearMenu_ORG_ID-" + id + "-n" + threadCounter.addAndGet(1));
                logger.get().info(String.format("Start clear menu for org id = %s", id));
                try {
                    Date startDate = new Date();
                    session = RuntimeContext.getInstance().createPersistenceSession();
                    session.setFlushMode(FlushMode.COMMIT);
                    transaction = session.beginTransaction();
                    org.hibernate.Query query = session.createSQLQuery("CREATE TEMP TABLE temp_menudetails(idofmenudetail BIGINT PRIMARY KEY) ON COMMIT DROP");
                    query.executeUpdate();
                    query = session.createSQLQuery("INSERT INTO temp_menudetails \n"
                            + "  SELECT idofmenudetail \n"
                            + "  FROM cf_menudetails md JOIN cf_menu m ON m.idofmenu = md.idofmenu \n"
                            + "  WHERE m.menudate < :datetime AND m.idoforg = :idOfOrg");
                    query.setParameter("idOfOrg", id);
                    query.setParameter("datetime", dateTime.getTime());
                    int rows = query.executeUpdate();
                    if (rows == 0) {
                        query = session.createSQLQuery("insert into srv_clear_menu_stat(idoforg) "
                                + "values(:idOfOrg)");
                        query.setParameter("idOfOrg", id);
                        query.executeUpdate();
                        transaction.commit();
                        transaction = null;
                        logger.get().info("No records to delete");
                        continue;
                    }
                    query = session.createSQLQuery("ANALYZE temp_menudetails");
                    query.executeUpdate();
                    logger.get().info("Temp table filled");

                    query = session.createSQLQuery("DELETE FROM cf_complexinfodetail WHERE idofmenudetail IN (SELECT idofmenudetail FROM temp_menudetails)");
                    query.executeUpdate();
                    logger.get().info("Deleted from complexinfodetail");

                    query = session.createSQLQuery("DELETE FROM cf_complexinfo WHERE idofmenudetail IN (SELECT idofmenudetail FROM temp_menudetails)");
                    query.executeUpdate();
                    logger.get().info("Deleted from complexinfo");

                    query = session.createSQLQuery("DELETE FROM cf_good_basic_basket_price WHERE idofmenudetail IN (SELECT idofmenudetail FROM temp_menudetails)");
                    query.executeUpdate();
                    logger.get().info("Deleted from basic_basket");

                    query = session.createSQLQuery("DELETE FROM cf_menudetails WHERE idofmenudetail IN (SELECT idofmenudetail FROM temp_menudetails)");
                    query.executeUpdate();
                    logger.get().info("Deleted from menudetails");

                    query = session.createSQLQuery("DELETE FROM cf_menu WHERE menudate < :datetime AND idoforg = :idOfOrg");
                    query.setParameter("idOfOrg", id);
                    query.setParameter("datetime", dateTime.getTime());
                    query.executeUpdate();
                    logger.get().info("Deleted from menu");

                    Date endDate = new Date();
                    query = session.createSQLQuery("insert into srv_clear_menu_stat(idoforg, startdate, enddate, datefrom, amount) "
                            + "values(:idOfOrg, :startDate, :endDate, :dateFrom, :amount)");
                    query.setParameter("idOfOrg", id);
                    query.setParameter("startDate", startDate.getTime());
                    query.setParameter("endDate", endDate.getTime());
                    query.setParameter("dateFrom", dateTime.getTime());
                    query.setParameter("amount", rows);
                    query.executeUpdate();

                    transaction.commit();
                    transaction = null;
                    logger.get().info(String.format("End clear menu for org id = %s", id));
                } catch (Exception e) {
                    logger.get().error(String.format("Error in clear menu version 2 for orgId=%s", id), e);
                } finally {
                    HibernateUtils.rollback(transaction, logger.get());
                    HibernateUtils.close(session, logger.get());
                }
            }
        } catch (Exception e) {
            logger.get().error("Error in clear menu version 2", e);
        } finally {
            Thread.currentThread().setName("ClearMenu_OFF" + "-n" + threadCounter.addAndGet(1));
        }
    }

    public void run() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        if (!RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_CLEAN_MENU)) {
            return;
        }

        //Очистка элементов меню старше 30 дней, которых нет в CF_MenuExchangeRules
        logger.get().info("Starting DB maintanance procedures...");
        try {
            long duration = System.currentTimeMillis();
            String report = getProxy().clean(false);
            duration = System.currentTimeMillis() - duration;
            logger.get().debug("Total duration = " + duration);
            logger.get().info("DB maintanance procedures finished successfully. " + report);
        } catch (Exception e) {
            logger.get().error("Database cleaning failed", e);
        }


        //Очистка элементов меню старше 720 дней, которых есть в CF_MenuExchangeRules
        logger.get().info("Starting DB maintanance procedures: source organizations...");
        try {
            String report = getProxy().clean(true);
            logger.get().info("DB maintanance procedures finished successfully. " + report);
        } catch (Exception e) {
            logger.get().error("Database cleaning failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS, readOnly = true)
    public String clean(boolean isSource) throws Exception {
        logger.get().debug("start clean: " + new Date());
        //Получаем количество дней, старше которых нужно чистить таблицу
        long menuDaysForDeletion = RuntimeContext.getInstance().getOptionValueInt(
                isSource ? Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION : Option.OPTION_MENU_DAYS_FOR_DELETION);
        if (menuDaysForDeletion < 0) {
            menuDaysForDeletion = 1;
        }

        long timeToClean = System.currentTimeMillis() - menuDaysForDeletion * 24 * 60 * 60 * 1000;

        //Если очиска раз в 30 дней - то только тех записей,что нет в этой таблице
        //Если очистка старше 730 дней - то очистка только тех записей, что есть в данной таблице
        String orgFilter =
                (isSource ? "" : "not") + " in (select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer)";

        Date maxDate = null;
        try {
            maxDate = CalendarUtils.parseDateWithDayTime(DAOService.getInstance().getDeletedLastedDateMenu());
            if (maxDate == null) {
                maxDate = new Date(0L);
            }
        } catch (Exception ignore) {
            maxDate = new Date(0L);
        }


        boolean full;
        boolean fullclean = false;
        List<Object[]> result = new ArrayList<>();
        do {
            full = false;
            //Получаем список меню
            Query query = entityManager.createNativeQuery(
                    "select m.IdOfMenu, m.IdOfOrg, m.MenuDate " + "from CF_Menu m where m.IdOfOrg " + orgFilter
                            + " and m.MenuDate < :date and m.MenuDate > :mindate order by m.MenuDate");
            query.setParameter("date", timeToClean);
            query.setParameter("mindate", maxDate.getTime());

            //Максимальное количество записей для очистки - 50000
            List<Object[]> records = query.setMaxResults(MAXROWS / 5).getResultList();

            if (!records.isEmpty()) {
                Object[] el1 = records.get(records.size() - 1);
                maxDate = new Date(((BigInteger) el1[2]).longValue());

                if (records.size() == MAXROWS / 5) {
                    DAOService.getInstance().setOnlineOptionValue(CalendarUtils.dateTimeToString(maxDate), Option.OPTION_LAST_DELATED_DATE_MENU);
                } else {
                    //Long time = maxDate.getTime();
                    //if (time.equals(0L))
                    //{
                    //    //Это сработает в том случае, если все в таблице почищено
                    //    fullclean = true;
                    //}
                    fullclean = true;
                    maxDate.setTime(0L);
                    DAOService.getInstance().setOnlineOptionValue(CalendarUtils.dateTimeToString(maxDate), Option.OPTION_LAST_DELATED_DATE_MENU);
                }
                Iterator<Object[]> it = records.iterator();

                while (it.hasNext()) {
                    Object[] el = it.next();
                    Long idOfMenu = ((BigInteger) el[0]).longValue();

                    //Находим детали меню для данного заказа
                    query = entityManager.createNativeQuery("SELECT m.MenuPath FROM CF_MenuDetails m WHERE m.IdOfMenu = :idOfMenu")
                            .setParameter("idOfMenu", idOfMenu);

                    List<String> recordsMenuDetail = query.getResultList();

                    for (String row : recordsMenuDetail) {
                        if (row.indexOf("[Интервальное]") == 0) {
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                //Получаем дату окончания действия интервального меню
                                Date date = dateFormat.parse(row.substring(29, 39));
                                if (date.getTime() > new Date().getTime()) {
                                    //Если дата окончания больше текущей, то удаляем это меню из списка
                                   // it.remove();
                                    break;
                                }
                            } catch (Exception e) {
                                logger.get().debug("Cannot format row: " + row);
                            }
                        }
                    }
                }
                result.addAll(records);
                if (MAXROWS <= result.size()) {
                    full = true;
                }
            }
            else
            {
                fullclean = true;
            }
        } while (!full && !fullclean);


        logger.get().debug("count menu: " + result.size());
        Set<Long> orgIds = new HashSet<Long>();
        MaintenanceService proxy = getProxy();

        logger.get().info("Cleaning menu details and menu...");
        int complexInfoDetailCount = 0;
        int complexInfoCount = 0;
        int goodBasicBasketPriceCount = 0;
        int menuDetailCount = 0;
        int menuCount = 0;
        //Для каждого меню
        for (Object[] row : result) {
            Long idOfMenu = ((BigInteger) row[0]).longValue();
            orgIds.add(((BigInteger) row[1]).longValue());
            int[] res = cleanMenuInformationInternal(idOfMenu);
            logger.get().debug(String.format("Successfully delete menu[%d]", idOfMenu));
            complexInfoDetailCount += res[0];
            complexInfoCount += res[1];
            goodBasicBasketPriceCount += res[2];
            menuDetailCount += res[3];
            menuCount += res[4];
        }

        logger.get().info("Cleaning menu exchange...");
        int menuExchangeDeletedCount = 0;
        //Для всех организаций, которых задела чистка меню производим удаление в CF_MenuExchange старше минимальной даты
        for (Long idOfOrg : orgIds) {
            menuExchangeDeletedCount += proxy.cleanMenuExchange(idOfOrg, timeToClean);
        }

        final String format = "Deleted all records before - %s, deleted records count: Menu - %d, "
                + "MenuDetail - %d, GoodBasicBasketPrice - %d, " + "ComplexInfo - %d, ComplexInfoDetail - %d, "
                + "MenuExchange - %d";
        return String.format(format, new Date(timeToClean), menuCount, menuDetailCount, goodBasicBasketPriceCount,
                complexInfoCount, complexInfoDetailCount, menuExchangeDeletedCount);
    }

    public int[] cleanMenuInformationInternal(Long idOfMenu) {
        int[] res = new int[5];
        Query qMenuDetailsDelete = entityManager
                .createNativeQuery("SELECT idOfMenuDetail FROM CF_MenuDetails cmd WHERE cmd.idOfMenu = :idOfMenu");
        qMenuDetailsDelete.setParameter("idOfMenu", idOfMenu);

        List menuDetailsForDeleteMass = qMenuDetailsDelete.getResultList();
        if (menuDetailsForDeleteMass != null && !menuDetailsForDeleteMass.isEmpty()) {
            res[0] = getProxy().cleanComplexInfoDetail(menuDetailsForDeleteMass);
            res[1] = getProxy().cleanComplexInfo(menuDetailsForDeleteMass);
            res[2] = getProxy().cleanGoodBasicBasket(menuDetailsForDeleteMass);
        } else {
            res[0] = 0;
            res[1] = 0;
            res[2] = 0;
        }
        res[3] = getProxy().cleanMenuDetail(idOfMenu);
        res[4] = getProxy().cleanMenu(idOfMenu);

        return res;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanMenuExchange(Long idOfOrg, long timeToClean) {
        Query query = entityManager.createNativeQuery("DELETE FROM CF_MenuExchange me WHERE me.IdOfOrg = :idOfOrg"
                + " AND me.MenuDate < :date AND me.menuDate <> :nullDate");
        query.setParameter("idOfOrg", idOfOrg).setParameter("date", timeToClean).setParameter("nullDate", 0);
        return query.executeUpdate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanComplexInfoDetail(List menuDetailsForDelete) {
        Query qComplexInfo = entityManager.createNativeQuery(
                "DELETE FROM CF_ComplexInfoDetail cid WHERE cid.idOfMenuDetail IN (:menuDetailsForDelete)");
        qComplexInfo.setParameter("menuDetailsForDelete", menuDetailsForDelete);
        return qComplexInfo.executeUpdate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanComplexInfo(List menuDetailsForDelete) {
        Query qComplex = entityManager.createNativeQuery(
                "DELETE FROM CF_ComplexInfo cc WHERE cc.idOfMenuDetail IN (:menuDetailsForDelete)");
        qComplex.setParameter("menuDetailsForDelete", menuDetailsForDelete);
        return qComplex.executeUpdate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanGoodBasicBasket(List menuDetailsForDelete) {
        Query qGoodBasicBasketPrice = entityManager.createNativeQuery(
                "DELETE FROM Cf_Good_Basic_Basket_Price cg WHERE cg.idofmenudetail IN (:menuDetailsForDelete)");
        qGoodBasicBasketPrice.setParameter("menuDetailsForDelete", menuDetailsForDelete);
        return qGoodBasicBasketPrice.executeUpdate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanMenuDetail(Long idOfMenu) {
        Query qMenuDetail = entityManager.createNativeQuery("DELETE FROM CF_MenuDetails WHERE idOfMenu = :idOfMenu");
        qMenuDetail.setParameter("idOfMenu", idOfMenu);
        return qMenuDetail.executeUpdate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanMenu(Long idOfMenu) {
        Query qMenu = entityManager.createNativeQuery("DELETE FROM CF_Menu WHERE idOfMenu = :idOfMenu");
        qMenu.setParameter("idOfMenu", idOfMenu);
        return qMenu.executeUpdate();
    }

    public static final class ClearMenuThreadWrapper implements Runnable {

        @Override
        public void run() {
            RuntimeContext.getAppContext().getBean(MaintenanceService.class).runVersion2();
        }
    }
}
