/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("singleton")
public class MaintenanceService {

    private Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private static int MAXROWS = 50;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private MaintenanceService getProxy() {
        return RuntimeContext.getAppContext().getBean(MaintenanceService.class);
    }

    public void run() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        if (!RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_CLEAN_MENU)) {
            return;
        }

        //Очистка элементов меню старше 30 дней, которых нет в CF_MenuExchangeRules
        logger.info("Starting DB maintanance procedures...");
        try {
            long duration = System.currentTimeMillis();
            String report = getProxy().clean(false);
            duration = System.currentTimeMillis() - duration;
            logger.debug("Total duration = " + duration);
            logger.info("DB maintanance procedures finished successfully. " + report);
        } catch (Exception e) {
            logger.error("Database cleaning failed", e);
        }


        //Очистка элементов меню старше 720 дней, которых есть в CF_MenuExchangeRules
        logger.info("Starting DB maintanance procedures: source organizations...");
        try {
            String report = getProxy().clean(true);
            logger.info("DB maintanance procedures finished successfully. " + report);
        } catch (Exception e) {
            logger.error("Database cleaning failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS, readOnly = true)
    public String clean(boolean isSource) throws Exception {
        logger.debug("start clean: " + new Date());
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
            if (maxDate == null)
            {
                maxDate = new Date(0L);
            }
        } catch (Exception ignore) {
            maxDate = new Date(0L);
        }


        boolean full;
        boolean fullclean = false;
        List<Object[]> result = new ArrayList<>();
        do {
            full = true;
            //Получаем список меню
            Query query = entityManager.createNativeQuery(
                    "select m.IdOfMenu, m.IdOfOrg, m.MenuDate " + "from CF_Menu m where m.IdOfOrg " + orgFilter
                            + " and m.MenuDate < :date and m.MenuDate > :mindate order by m.MenuDate");
            query.setParameter("date", timeToClean);
            query.setParameter("mindate", maxDate.getTime());

            //Максимальное количество записей для очистки - 50000
            List<Object[]> records = query.setMaxResults(MAXROWS / 5).getResultList();

            Object[] el1 = records.get(records.size() - 1);
            maxDate = new Date(((BigInteger) el1[2]).longValue());

            if (records.size() == MAXROWS / 5) {
                DAOService.getInstance().setOnlineOptionValue(CalendarUtils.dateTimeToString(maxDate), Option.OPTION_LAST_DELATED_DATE_MENU);
            } else {
                Long time = maxDate.getTime();
                if (time.equals(0L))
                {
                    //Это сработает в том случае, если все в таблице почищено
                    fullclean = true;
                }
                maxDate.setTime(0L);
                DAOService.getInstance().setOnlineOptionValue(CalendarUtils.dateTimeToString(maxDate), Option.OPTION_LAST_DELATED_DATE_MENU);
            }
            Iterator<Object[]> it = records.iterator();

            while (it.hasNext()) {
                Object[] el = it.next();
                Long idOfMenu = ((BigInteger) el[0]).longValue();

                //Находим детали меню для данного заказа
                query = entityManager
                        .createNativeQuery("SELECT m.MenuPath FROM CF_MenuDetails m WHERE m.IdOfMenu = :idOfMenu")
                        .setParameter("idOfMenu", idOfMenu);

                List<String> recordsMenuDetail = query.getResultList();

                for (String row : recordsMenuDetail) {
                    if (row.indexOf("[Интервальное]") == 0) {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            //Получаем дату окончания действия интервального меню
                            Date date = dateFormat.parse(row.substring(29, 39));
                            if (date.getTime() < new Date().getTime()) {
                                //Если дата окончания больше текущей, то удаляем это меню из списка
                                it.remove();
                                break;
                            }
                        } catch (Exception e) {
                            logger.debug("Cannot format row: " + row);
                        }
                    }
                }
            }
            result.addAll(records);
            if (MAXROWS > result.size()) {
                full = false;
            }


        } while (!full || fullclean);


        logger.debug("count menu: " + result.size());
        Set<Long> orgIds = new HashSet<Long>();
        MaintenanceService proxy = getProxy();

        logger.info("Cleaning menu details and menu...");
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
            logger.debug(String.format("Successfully delete menu[%d]", idOfMenu));
            complexInfoDetailCount += res[0];
            complexInfoCount += res[1];
            goodBasicBasketPriceCount += res[2];
            menuDetailCount += res[3];
            menuCount += res[4];
        }

        logger.info("Cleaning menu exchange...");
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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int[] cleanMenuInformationInternal(Long idOfMenu) {
        int[] res = new int[5];
        Session session = entityManager.unwrap(Session.class);

        org.hibernate.Query qMenuDetailsDelete = session
                .createQuery("select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu");
        qMenuDetailsDelete.setParameter("idOfMenu", idOfMenu);

        List menuDetailsForDelete = qMenuDetailsDelete.list();

        if (menuDetailsForDelete != null && !menuDetailsForDelete.isEmpty()) {
            org.hibernate.Query qComplexInfo = session.createQuery(
                    "delete from ComplexInfoDetail where menuDetail.idOfMenuDetail in (:menuDetailsForDelete)");
            qComplexInfo.setParameterList("menuDetailsForDelete", menuDetailsForDelete);
            res[0] = qComplexInfo.executeUpdate();

            org.hibernate.Query qComplex = session
                    .createQuery("delete from ComplexInfo where menuDetail.idOfMenuDetail in (:menuDetailsForDelete)");
            qComplex.setParameterList("menuDetailsForDelete", menuDetailsForDelete);
            res[1] = qComplex.executeUpdate();

            //org.hibernate.Query qGoodBasicBasketPrice = session.createQuery("update GoodBasicBasketPrice set idofmenudetail = null where idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
            org.hibernate.Query qGoodBasicBasketPrice = session
                    .createQuery("delete from GoodBasicBasketPrice where idOfMenuDetail in (:menuDetailsForDelete)");
            qGoodBasicBasketPrice.setParameterList("menuDetailsForDelete", menuDetailsForDelete);
            res[2] = qGoodBasicBasketPrice.executeUpdate();
        } else {
            res[0] = 0;
            res[1] = 0;
            res[2] = 0;
        }

        org.hibernate.Query qMenuDetail = session.createQuery("delete from MenuDetail where idOfMenu = :idOfMenu");
        qMenuDetail.setParameter("idOfMenu", idOfMenu);
        res[3] = qMenuDetail.executeUpdate();

        org.hibernate.Query qMenu = session.createQuery("delete from Menu WHERE idOfMenu = :idOfMenu");
        qMenu.setParameter("idOfMenu", idOfMenu);
        res[4] = qMenu.executeUpdate();

        return res;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanMenuExchange(Long idOfOrg, long timeToClean) {
        Query query = entityManager.createNativeQuery("DELETE FROM CF_MenuExchange me WHERE me.IdOfOrg = :idOfOrg"
                + " AND me.MenuDate < :date AND me.menuDate <> :nullDate");
        query.setParameter("idOfOrg", idOfOrg).setParameter("date", timeToClean).setParameter("nullDate", 0);
        return query.executeUpdate();
    }
}
