/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import sun.awt.AppContext;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private static final String SELECT_ORGS_SQL =
            "select cf_discountrules.idofrule, cf_discountrules_categorydiscounts.idofcategorydiscount, cf_discountrules_categoryorg.idofcategoryorg, "
                    + "       cf_discountrules.priority, cf_discountrules.operationor, complex0, complex1, complex2, complex3, complex4, complex5, complex6, complex7, complex8, complex9 "
                    + "       complex10, complex11, complex12, complex13, complex14, complex15, complex16, complex17, complex18, complex19, "
                    + "       complex20, complex21, complex22, complex23, complex24, complex25, complex26, complex27, complex28, complex29, "
                    + "       complex30, complex31, complex32, complex33, complex34, complex35, complex36, complex37, complex38, complex39, "
                    + "       complex40, complex41, complex42, complex43, complex44, complex45, complex46, complex47, complex48, complex49 "
                    + "from cf_discountrules "
                    + "left join cf_discountrules_categorydiscounts on cf_discountrules.idofrule=cf_discountrules_categorydiscounts.idofrule "
                    + "left join cf_discountrules_categoryorg on cf_discountrules.idofrule=cf_discountrules_categoryorg.idofrule "
                    + "order by cf_discountrules.idofrule, cf_discountrules_categorydiscounts.idofcategorydiscount";
    private static final String SELECT_CLIENTS_SQL =
            "select cf_clients.idofclient, cf_categoryorg_orgs.idofcategoryorg, cf_clients_categorydiscounts.idofcategorydiscount "
                    + "from cf_clients "
                    + "left join cf_clients_categorydiscounts on cf_clients.idofclient=cf_clients_categorydiscounts.idofclient "
                    + "left join cf_categoryorg_orgs on cf_clients.idoforg=cf_categoryorg_orgs.idoforg "
                    + "where cf_clients_categorydiscounts.idofcategorydiscount<>0 and cf_clients.idOfClientGroup<:leavingClientGroup "
                    //+ "and cf_clients.idofclient=1244 "  Для проверок, не удалять
                    + "union all "
                    + "select cf_clients.idofclient, cf_categoryorg_orgs.idofcategoryorg, case when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 then-90  "
                    + "                                                                        when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=8 then -91 "
                    + "                                                                        when CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=9 then -92 else -999999 end "
                    + "from cf_clients "
                    + "left join cf_categoryorg_orgs on cf_clients.idoforg=cf_categoryorg_orgs.idoforg "
                    + "left join cf_clientgroups on cf_clientgroups.idofclientgroup=cf_clients.idofclientgroup AND cf_clientgroups.idoforg=cf_clients.idoforg "
                    + "where CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and cf_clients.idOfClientGroup<:leavingClientGroup "
                    //+ "and cf_clients.idofclient=1244 "  Для проверок, не удалять
                    + "order by idofclient, 3";
    private static final String INSERT_SQL = "INSERT INTO cf_clientscomplexdiscounts (createdate, idofclient, idofrule, idofcategoryorg, priority, operationar, idofcomplex) values (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM cf_clientscomplexdiscounts ";//WHERE createdate=:createdate";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BIDataExportService.class);
    
    @PersistenceContext
    EntityManager em;


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_BENEFITS_RECALC_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_BENEFITS_RECALC_ON, "" + (on ? "1" : "0"));
    }


    @Transactional
    public void runForcibly() {
        logger.info("Started benefits recalculation");
        try {
            loadData();
        } catch (Exception e) {
            logger.error("Failed to load data from database");
        }
        logger.info("Finished benefits recalculation");
    }

    public void run() {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("BI data export is turned off. You have to activate this tool using common Settings");
            return;
        }

        RuntimeContext.getAppContext().getBean(BenefitsRecalculationService.class).runForcibly();
    }


    private void loadData() {
        Session session = (Session)em.getDelegate();
        try {
            List<Integer> complexes = new ArrayList<Integer>();
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR, 0);
            //org.hibernate.Query ins = session.createSQLQuery(INSERT_SQL);
            org.hibernate.Query org = session.createSQLQuery(SELECT_ORGS_SQL);
            org.hibernate.Query sel = session.createSQLQuery(SELECT_CLIENTS_SQL);
            org.hibernate.Query del = session.createSQLQuery(DELETE_SQL);
            sel.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());

            del.executeUpdate();


            //   priority       ruleObj
            TreeMap<Integer, List<DiscountRule>> rules = new TreeMap<Integer, List<DiscountRule>>();
            List resultList = org.list();
            long prevID = 0L;
            DiscountRule rule = null;
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                long ruleID = ((BigInteger) e[0]).longValue();
                long categoryID = e[1] == null ? -1L : ((BigInteger) e[1]).longValue();
                long orgCategoryID = e[2] == null ? -1L : ((BigInteger) e[2]).longValue();
                int priority = ((Integer) e[3]).intValue();
                int operationor = ((Integer) e[4]).intValue();

                if (ruleID != prevID) {
                    List<DiscountRule> these = rules.get(priority);
                    if (these == null) {
                        these = new ArrayList<DiscountRule>();
                        rules.put(priority, these);
                    }
                    rule = new DiscountRule(ruleID, priority, operationor);
                    for (int complexID = 5; complexID < e.length; complexID++) {
                        int complex = ((Integer) e[complexID]).intValue();
                        if (complex != 0) {
                            rule.addComplex(complexID - 5);
                        }
                    }
                    prevID = ruleID;
                    these.add(rule);
                }
                rule.addCategory(orgCategoryID, categoryID);
            }
            //  Необходимо перевернуть правила (поставить приоритет по убыванию)
            Map<Integer, List<DiscountRule>> descRules = rules.descendingMap();


            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            resultList = sel.list();

            session.doWork(new InsertWork(resultList, descRules, calendar));
        } catch (Exception e) {
            logger.error("Failed to recalculate benefits", e);
        }
    }


    public DiscountRule[] getClientRule(CheckoutClient cl, Map<Integer, List<DiscountRule>> rules) {
        //      priority      availableRules
        TreeMap<Integer, List<DiscountRule>> validRules = new TreeMap<Integer, List<DiscountRule>>();
        for (Integer priority : rules.keySet()) {
            List<DiscountRule> list = rules.get(priority);
            for (DiscountRule r : list) {
                for (Long orgCategoryID : r.categories.keySet()) {
                    //  Если типы оргов и проверяемого правила не совпадают, то выходим сразу
                    if (orgCategoryID != -1L && orgCategoryID != cl.orgCategoryID) {
                        continue;
                    }
                    //  Проверяем наличие всех требоуемых категорий у клиента
                    List<Long> requiredCats = r.categories.get(orgCategoryID);
                    if (cl.categories.containsAll(requiredCats)) {
                        //  Если правило полность удовлетворяет требованиям, добавляем его в список доступных клиенту
                        List<DiscountRule> usedRules = validRules.get(priority);
                        if (usedRules == null) {
                            usedRules = new ArrayList<DiscountRule>();
                            validRules.put(priority, usedRules);
                        }
                        usedRules.add(r);
                    }
                }
            }
        }

        //  Сортируем доступные клиенту правила по убыванию, чтобы взять с максимальным приоритетом
        if (validRules.isEmpty()) {
            return null;
        }
        List<DiscountRule> topRules = validRules.get(validRules.descendingMap().keySet().iterator().next());
        return topRules.toArray(new DiscountRule[topRules.size()]);
    }


    public long insertRuleIntoDb(PreparedStatement pstmt, CheckoutClient cl, DiscountRule rules[], Calendar cal, long inserts)
            throws SQLException {
        try {
            for (DiscountRule r : rules) {
                if (r.complexes == null || r.complexes.size() < 1) {
                    continue;
                }

                pstmt.setLong(1, cal.getTimeInMillis());
                pstmt.setLong(2, cl.id);
                pstmt.setLong(3, r.id);
                pstmt.setLong(4, cl.orgCategoryID == -1L ? 0 : cl.orgCategoryID);
                pstmt.setInt(5, r.priority);
                pstmt.setInt(6, r.operationor);
                for (Integer idofcomplex : r.complexes) {
                    pstmt.setInt(7, idofcomplex);
                    pstmt.addBatch();
                    inserts++;
                }
            }
        return inserts;
        } catch (SQLException sqle) {
            throw sqle;
        } catch (Exception e) {
            logger.error("Failed to insert entry into batch", e);
        }
    return inserts;
    }


    public class CheckoutClient {

        private long id;
        private long orgCategoryID;
        private List<Long> categories;

        public CheckoutClient(long id, long orgCategoryID) {
            this.id = id;
            this.orgCategoryID = orgCategoryID;
        }


        public void addCategory(long categoryID) {
            if (categories == null) {
                categories = new ArrayList<Long>();
            }
            if (!categories.contains(categoryID)) {
                categories.add(categoryID);
            }
        }
    }


    public class DiscountRule {

        private long id;
        private int priority;
        private int operationor;
        //         orgType      catID
        private Map<Long, List<Long>> categories;
        private List<Integer> complexes;

        public DiscountRule(long id, int priority, int operationor) {
            this.id = id;
            this.priority = priority;
            this.operationor = operationor;
        }


        public void addComplex(int complexID) {
            if (complexes == null) {
                complexes = new ArrayList<Integer>();
            }
            complexes.add(complexID);
        }


        public void addCategory(long orgCategoryID, long categoryID) {
            if (categories == null) {
                categories = new HashMap<Long, List<Long>>();
            }

            List<Long> cats = categories.get(orgCategoryID);
            if (cats == null) {
                cats = new ArrayList<Long>();
                categories.put(orgCategoryID, cats);
            }
            cats.add(categoryID);
        }
    }

    public class InsertWork implements Work {
        private final List resultList;
        private Map<Integer, List<DiscountRule>> descRules;
        private Calendar calendar;

        public InsertWork (List resultList, Map<Integer, List<DiscountRule>> descRules, Calendar calendar) {
            this.resultList = resultList;
            this.descRules = descRules;
            this.calendar = calendar;
        }

        @Override
        public void execute(Connection connection) throws SQLException {
            long inserts = 0;
            long prevID = 0L;
            CheckoutClient cl = null;
            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                long clientID = ((BigInteger) e[0]).longValue();
                long orgCategoryID = e[1] == null ? -1L : ((BigInteger) e[1]).longValue();
                long categoryID = e[2] == null ? -1L : ((BigInteger) e[2]).longValue();


                if (clientID != prevID) {
                    if (cl != null) {
                        DiscountRule clientRules[] = getClientRule(cl, descRules);
                        if (clientRules != null && clientRules.length > 0) {
                            inserts = insertRuleIntoDb(pstmt, cl, clientRules, calendar, inserts);
                        }
                    }

                    prevID = clientID;
                    cl = new CheckoutClient(clientID, orgCategoryID);
                }
                if (inserts > 1000) {
                    inserts = 0;
                    pstmt.executeBatch();
                }

                cl.addCategory(categoryID);
            }


            if (cl != null) {
                DiscountRule clientRules[] = getClientRule(cl, descRules);
                if (clientRules != null && clientRules.length > 0) {
                    insertRuleIntoDb(pstmt, cl, clientRules, calendar, inserts);
                }
            }
        pstmt.executeBatch();
        }
    }
}