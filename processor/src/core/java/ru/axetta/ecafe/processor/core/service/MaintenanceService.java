/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("singleton")
public class MaintenanceService {

    private Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

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
        logger.debug("start clean: "+ new Date());
        //Получаем количество дней, старше которых нужно чистить таблицу
        long menuDaysForDeletion =  RuntimeContext.getInstance().getOptionValueInt(
                isSource ? Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION : Option.OPTION_MENU_DAYS_FOR_DELETION);
        if (menuDaysForDeletion < 0) {
            menuDaysForDeletion = 1;
        }

        long timeToClean = System.currentTimeMillis() - menuDaysForDeletion * 24 * 60 * 60 * 1000;

        //Если очиска раз в 30 дней - то только тех записей,что нет в этой таблице
        //Если очистка старше 730 дней - то очистка только тех записей, что есть в данной таблице
        String orgFilter = (isSource ? "" : "not") + " in (select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer)";

        //Получаем список меню
        Query query = entityManager.createNativeQuery(
                "select m.IdOfMenu, m.IdOfOrg from CF_Menu m where m.IdOfOrg " + orgFilter + " and m.MenuDate < :date")
                .setParameter("date", timeToClean);
        List<Object[]> records = query.getResultList();

        Iterator<Object[]> it = records.iterator();
        while (it.hasNext()) {
            Object[] el = it.next();
            Long idOfMenu = ((BigInteger) el[0]).longValue();

            //Находим детали меню для данного заказа
            query = entityManager.createNativeQuery(
                    "select m.MenuPath from CF_MenuDetails m where m.IdOfMenu = :idOfMenu")
                    .setParameter("idOfMenu", idOfMenu);

            List<String> recordsMenuDetail = query.getResultList();

            for (String row : recordsMenuDetail) {
                if (row.indexOf("[Интервальное]") == 0)
                {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        //Получаем дату окончания действия интервального меню
                        Date date = dateFormat.parse(row.substring(29, 39));
                        if (date.getTime() > new Date().getTime()) {
                            //Если дата окончания больше текущей, то удаляем это меню из списка
                            it.remove();
                            break;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.debug("Cannot format row: "+ row);
                    }
                }
            }
        }

        logger.debug("count menu: "+ records.size());
        Set<Long> orgIds = new HashSet<Long>();
        MaintenanceService proxy = getProxy();

        logger.info("Cleaning menu details and menu...");
        int menuDetailDeletedCount = 0;
        int menuDeletedCount = 0;
        int complexInfoDeletedCount = 0;
        int complexDeletedCount = 0;
        int basicBasketDeletedCount = 0;
        //Для каждого меню
        for (Object[] row : records) {
            Long idOfMenu = ((BigInteger) row[0]).longValue();
            orgIds.add(((BigInteger) row[1]).longValue());
            int[] res = cleanMenuInformationInternal(idOfMenu);
            logger.debug(String.format("Successfully delete menu[%d]", idOfMenu));
            complexInfoDeletedCount += res[0];
            complexDeletedCount += res[1];
            basicBasketDeletedCount += res[2];
            menuDetailDeletedCount += res[3];
            menuDeletedCount += res[4];
        }

        logger.info("Cleaning menu exchange...");
        int menuExchangeDeletedCount = 0;
        //Для всех организаций, которых задела чистка меню производим удаление в CF_MenuExchange старше минимальной даты
        for (Long idOfOrg : orgIds) {
            menuExchangeDeletedCount += proxy.cleanMenuExchange(idOfOrg, timeToClean);
        }

        final String format = "Deleted all records before - %s, deleted records count: menu -%d, "
                + "menudetail - %d, menuexchange - %d, "
                + "complex - %d, complexinfo - %d, "
                + "goodbasicbasketprice - %d";
        return String.format(format, new Date(timeToClean), menuDeletedCount,
                menuDetailDeletedCount, menuExchangeDeletedCount,
                complexDeletedCount, complexInfoDeletedCount, basicBasketDeletedCount);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int[] cleanMenuInformationInternal(Long idOfMenu) {
        int[] res = new int[5];
        Session session = entityManager.unwrap(Session.class);

        org.hibernate.Query qComplexInfo = session.createQuery("delete from ComplexInfoDetail where menuDetail.idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
        qComplexInfo.setParameter("idOfMenu", idOfMenu);
        res[0] = qComplexInfo.executeUpdate();

        org.hibernate.Query qComplex = session.createQuery("delete from ComplexInfo where menuDetail.idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
        qComplex.setParameter("idOfMenu", idOfMenu);
        res[1] = qComplex.executeUpdate();

        //org.hibernate.Query qGoodBasicBasketPrice = session.createQuery("update GoodBasicBasketPrice set idofmenudetail = null where idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
        org.hibernate.Query qGoodBasicBasketPrice = session.createQuery("delete from GoodBasicBasketPrice where idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
        qGoodBasicBasketPrice.setParameter("idOfMenu", idOfMenu);
        res[2] = qGoodBasicBasketPrice.executeUpdate();

        org.hibernate.Query qMenuDetail = session.createQuery("delete from MenuDetail where idOfMenuDetail in (select idOfMenuDetail from MenuDetail WHERE menu.idOfMenu = :idOfMenu)");
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
