package ru.axetta.ecafe.processor.core.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.util.*;

@Component
@Scope("singleton")
public class CancelledFoodBoxService {
    final static String AUTO_ARCHIVED = "CancelledFoodBoxService";
    public static final String BUFFET_HEALTH = "ecafe.processor.meals.health";
    private static final Logger logger = LoggerFactory.getLogger(CancelledFoodBoxService.class);
    public static Map<Long, Date> currentFoodBoxPreorders = new HashMap<>();
    public static final Integer TIME_ALIVE = getHealthTime();

    public static class CancelledFoodBox implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(CancelledFoodBoxService.class)
                        .start(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed execute cancelledFoodBox", e);
            }
            finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            }
        }

//        public static void manualStart() throws JobExecutionException {
//            RuntimeContext runtimeContext = RuntimeContext.getInstance();
//            Session persistenceSession = null;
//            Transaction persistenceTransaction = null;
//            try {
//                persistenceSession = runtimeContext.createPersistenceSession();
//                persistenceTransaction = persistenceSession.beginTransaction();
//                RuntimeContext.getAppContext().getBean(CancelledFoodBoxService.class)
//                        .start(persistenceSession);
//                persistenceTransaction.commit();
//                persistenceTransaction = null;
//            } catch (Exception e) {
//            }finally {
//                HibernateUtils.rollback(persistenceTransaction, logger);
//                HibernateUtils.close(persistenceSession, logger);
//            }
//        }
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().
                getProperty("ecafe.processor.cancelled.foodbox.node", "199");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
                    && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        logger.info("Сервис CancelledFoodBoxService не запущен instance:" + instance + ", reqInstance:" + reqInstance);
        return false;
    }

    public void scheduleSync() throws Exception {
        if (isOn()) {
            logger.info("Сервис CancelledFoodBoxService успешно запущен");
            //Подготавливаем первоначальный список
            DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
            List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getActiveFoodBoxPreorder();
            logger.info("Первоначальное сканирование обнаружило " + foodBoxPreorders.size() + " заказов фудбокса для удаления");
            for (FoodBoxPreorder foodBoxPreorder : foodBoxPreorders) {
                currentFoodBoxPreorders.put(foodBoxPreorder.getIdFoodBoxPreorder(), foodBoxPreorder.getCreateDate());
            }
        }
        //
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.cancelled.foodbox.time", "0 * * ? * *");
        try {
            JobDetail jobDetailSync = new JobDetail(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP, CancelledFoodBox.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(AUTO_ARCHIVED, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed auto scheduleSync CancelledFoodBoxService", e);
        }
    }


    public void start(Session session) throws Exception {
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        for (Iterator<Map.Entry<Long, Date>> it = currentFoodBoxPreorders.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, Date> entry = it.next();
            Long time = new Date().getTime() - entry.getValue().getTime();
            if (time > TIME_ALIVE) {
                //Удаляем те предзаказы, у которых уже время жизни прошло
                FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorderById(entry.getKey());
                foodBoxPreorder.setCancelReason(2);
                foodBoxPreorder.setState(FoodBoxStateTypeEnum.CANCELED);
                foodBoxPreorder.setPosted(2);
                session.merge(foodBoxPreorder);
                it.remove();
                logger.info("Удаление фудбокс заказа " + foodBoxPreorder.getIdFoodBoxPreorder());
            }
        }
    }

    private static Integer getHealthTime() {
        try {
            String health = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_HEALTH, "7200000");
            return Integer.parseInt(health);
        } catch (Exception e) {
            return 7200000;
        }
    }
}
