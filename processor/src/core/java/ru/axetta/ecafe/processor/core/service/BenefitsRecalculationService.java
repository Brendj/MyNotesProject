/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 30.11.12
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class BenefitsRecalculationService {
    private static final String SELECT_SQL = "select idofclient, idofrule, idofcategoryorg, priority, operationor, "
            + "       sum(complex0) as complex0, sum(complex1) as complex1, "
            + "       sum(complex2) as complex2, sum(complex3) as complex3, "
            + "       sum(complex4) as complex4, sum(complex5) as complex5, "
            + "       sum(complex6) as complex6, sum(complex7) as complex7, "
            + "       sum(complex8) as complex8, sum(complex9) as complex9 " + "from (SELECT "
            + "  cf_clients.idofclient, " + "  cf_discountrules.idofrule, " + "  cf_categoryorg_orgs.idofcategoryorg, "
            + "  cf_discountrules.priority, " + "  cf_discountrules.operationor, " +
            "  cf_discountrules.complex0, "
            + "  cf_discountrules.complex1, cf_discountrules.complex2, cf_discountrules.complex4, "
            + "  cf_discountrules.complex3, cf_discountrules.complex5, cf_discountrules.complex6, "
            + "  cf_discountrules.complex7, cf_discountrules.complex8, cf_discountrules.complex9 "
            + "FROM public.cf_categorydiscounts, " +
            "  public.cf_discountrules, " + "  public.cf_discountrules_categorydiscounts, " +
            "  public.cf_clients, " + "  public.cf_clients_categorydiscounts, " + "public.cf_categoryorg_orgs " +
            "WHERE " + "  cf_discountrules_categorydiscounts.idofrule = cf_discountrules.idofrule AND "
            + "  cf_discountrules_categorydiscounts.idofcategorydiscount = cf_categorydiscounts.idofcategorydiscount AND "
            + "  cf_clients_categorydiscounts.idofcategorydiscount = cf_categorydiscounts.idofcategorydiscount AND "
            + "  cf_clients_categorydiscounts.idofclient = cf_clients.idofclient and "
            + "  cf_categoryorg_orgs.idoforg=cf_clients.idoforg " +
            "order by idofclient) as ooo " + "group by idofclient, idofrule, idofcategoryorg, priority, operationor " +

            "union  all "

            + "select idofclient, idofrule, idofcategoryorg, priority, operationor, "
            + "       sum(complex0) as complex0, sum(complex1) as complex1, "
            + "       sum(complex2) as complex2, sum(complex3) as complex3, "
            + "       sum(complex4) as complex4, sum(complex5) as complex5, "
            + "       sum(complex6) as complex6, sum(complex7) as complex7, "
            + "       sum(complex8) as complex8, sum(complex9) as complex9 " +
            "from public.cf_clients " + "left join public.cf_discountrules on 1=1 "
            + "left join public.cf_categoryorg_orgs on cf_categoryorg_orgs.idoforg=cf_clients.idoforg "
            + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
            + "where cf_discountrules.idofrule=(case when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 then 47 "                                          // Если клиент в группе 1-4 класс, то ставим idofrule=47
            + "                                      when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=8 then 48 "                                          // Если клиент в группе 1-4 класс, то ставим idofrule=48
            + "                                      when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=9 then 49 else -999999 end) and idofcategoryorg<>0 " // Если клиент в группе 1-4 класс, то ставим idofrule=49
            + "group by idofclient, idofrule, idofcategoryorg, priority, operationor "
            + "order by idofclient, priority desc ";
    private static final String UPDATE_SQL = "INSERT INTO cf_clientscomplexdiscounts (createdate, idofclient, idofrule, idofcategoryorg, priority, operationar, idofcomplex) values (:createdate, :idofclient, :idofrule, :idofcategoryorg, :priority, :operationor, :idofcomplex)";
    private static final String DELETE_SQL = "DELETE FROM cf_clientscomplexdiscounts WHERE createdate=:createdate";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BIDataExportService.class);


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_BENEFITS_RECALC_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_BENEFITS_RECALC_ON, "" + (on ? "1" : "0"));
    }


    public void run() {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("BI data export is turned off. You have to activate this tool using common Settings");
            return;
        }


        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session session = runtimeContext.createPersistenceSession();
            loadData(session);
        } catch (Exception e) {
            logger.error("Failed to load data from database");
        }
    }


    @Transactional
    private void loadData(Session session) {
        try {
            List<Integer> complexes = new ArrayList<Integer>();
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR, 0);
            org.hibernate.Query upd = session.createSQLQuery(UPDATE_SQL);
            org.hibernate.Query sel = session.createSQLQuery(SELECT_SQL);
            org.hibernate.Query del = session.createSQLQuery(DELETE_SQL);

            //  Удаляем данные за сегодняшний день, чтобы потом обновить их
            List resultList = sel.list();
            del.setLong("createdate", cal.getTimeInMillis());
            del.executeUpdate();

            //  Считываем ответо от БД и записываем данные в БД
            long prevIdOfClient = 0L;
            for (Object entry : resultList) {
                complexes.clear();
                Object e[] = (Object[]) entry;
                long idofclient = ((BigInteger) e[0]).longValue();
                long idofrule = ((BigInteger) e[1]).longValue();
                long idofcategoryorg = e[2] == null ? 0L : ((BigInteger) e[2]).longValue();
                int priority = ((Integer) e[3]).intValue();
                int operationor = ((Integer) e[4]).intValue();
                for (int i = 5; i < e.length; i++) {
                    if (((BigInteger) e[i]).intValue() > 0) {
                        complexes.add(i - 5);
                    }
                }


                //  С более низким приоритетом будут отсеяны и не записаны в БД, т.к. они отсортированны по приоритету,
                //  а записывается только первый (т.е. с макс приоритетом)
                if (idofclient == prevIdOfClient) {
                    continue;
                }
                prevIdOfClient = idofclient;


                upd.setLong("createdate", cal.getTimeInMillis());
                upd.setLong("idofclient", idofclient);
                upd.setLong("idofrule", idofrule);
                upd.setLong("idofcategoryorg", idofcategoryorg);
                upd.setInteger("priority", priority);
                upd.setInteger("operationor", operationor);
                for (Integer idofcomplex : complexes) {
                    upd.setInteger("idofcomplex", idofcomplex);
                    upd.executeUpdate();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to recalculate benefits", e);
        }
    }
}