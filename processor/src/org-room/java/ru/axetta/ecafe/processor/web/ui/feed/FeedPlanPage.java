/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.feed;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.08.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class FeedPlanPage extends BasicWorkspacePage {
    private static final long ELEMENTARY_CLASSES_TYPE = Long.MIN_VALUE;
    private static final long MIDDLE_CLASSES_TYPE     = Long.MIN_VALUE + 1;
    private static final long HIGH_CLASSES_TYPE       = Long.MIN_VALUE + 2;
    private static final long ORDER_TYPE              = Long.MIN_VALUE + 3;
    private static final long ALL_TYPE                = Long.MIN_VALUE + 4;
    private static final String ELEMENTARY_CLASSES_TYPE_NAME = "Младшие";
    private static final String MIDDLE_CLASSES_TYPE_NAME     = "Средние";
    private static final String HIGH_CLASSES_TYPE_NAME       = "Старшие";
    private static final String ORDER_TYPE_NAME              = "Заказ";
    private static final String ALL_TYPE_NAME                = "Все";
    private static final Logger logger = LoggerFactory.getLogger(FeedPlanPage.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private Org org;
    private String errorMessages;
    private String infoMessages;
    private Map<Long, Client> clients;
    private List<Complex> complexes;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public Org getOrg() {
        if (org != null) {
            return org;
        }
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return getOrg(session);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Org getOrg(Session session) {
        if (org != null) {
            return org;
        }
        org = (Org) session.get(Org.class, 0L);
        return org;
    }

    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load discounts data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        clients = new HashMap<Long, Client>();
        complexes = new ArrayList<Complex>();

        long prevIdofclient = -1L;
        Client prevClient = null;
        Integer prevGroupNum = null;

        String sql = "select cf_clientgroups.idofclientgroup, cf_clientgroups.groupname, cf_clients.idofclient, cf_persons.firstname, "
                + "       cf_persons.secondname, cf_persons.surname, cf_clientscomplexdiscounts.idofrule, description, idofcomplex, cf_discountrules.priority, "
                + "       CAST(substring(groupname FROM '[0-9]+') AS INTEGER) as groupNum "
                + "from cf_clients "
                + "join cf_clientscomplexdiscounts on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "left join cf_discountrules on cf_discountrules.idofrule=cf_clientscomplexdiscounts.idofrule "
                + "where cf_clients.idoforg=:idoforg "
                + "order by groupNum, groupname, cf_clients.idofclient, cf_discountrules.priority, idofcomplex";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", getOrg(session).getIdOfOrg());
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idofclientgroup = HibernateUtils.getDbLong(o[0]);
            String clientgroupname = HibernateUtils.getDbString(o[1]);
            Long idofclient = HibernateUtils.getDbLong(o[2]);
            String firstName = HibernateUtils.getDbString(o[3]);
            String secondname = HibernateUtils.getDbString(o[4]);
            String surname = HibernateUtils.getDbString(o[5]);
            Long idofrule = HibernateUtils.getDbLong(o[6]);
            String ruleDescription = HibernateUtils.getDbString(o[7]);
            Integer idofcomplex = HibernateUtils.getDbInt(o[8]);
            Integer priority = HibernateUtils.getDbInt(o[9]);
            Integer groupNum = HibernateUtils.getDbInt(o[10]);


            //  Добавляем клиента
            if (prevIdofclient != idofclient.longValue()) {
                Client cl = new Client(idofclientgroup, idofclient,
                        firstName, secondname, surname,
                        idofrule, ruleDescription, idofcomplex, priority);
                clients.put(idofclient, cl);
                prevClient = cl;
            }
            prevClient.addComplex(idofcomplex);


            //  Обновляем комплексы
            boolean complexFound = false;
            long idofsuperclientgroup = -1L;
            String superclientgroupname = "";
            if (groupNum < 4) {
                idofsuperclientgroup = ELEMENTARY_CLASSES_TYPE;
                superclientgroupname = ELEMENTARY_CLASSES_TYPE_NAME;
            } else if (groupNum < 10) {
                idofsuperclientgroup = MIDDLE_CLASSES_TYPE;
                superclientgroupname = MIDDLE_CLASSES_TYPE_NAME;
            } else {
                idofsuperclientgroup = HIGH_CLASSES_TYPE;
                superclientgroupname = HIGH_CLASSES_TYPE_NAME;
            }
            //  Поиск группы клиента
            Complex c = getComplexByComplexIdAndGroup(idofcomplex, idofclientgroup);
            if (c != null) {
                c.increase();
            } else {
                c = new Complex(idofcomplex, idofclientgroup, clientgroupname);
                complexes.add(c);
            }
            //  Поиск супер-группы клиента только в том случае, если школановая
            c = getComplexByComplexIdAndGroup(idofcomplex, idofsuperclientgroup);
            if (c != null) {
                c.increase();
            } else {
                c = new Complex(idofcomplex, idofsuperclientgroup, superclientgroupname);
                complexes.add(c);
            }
            //  Поиск иговой группы клиента
            c = getComplexByComplexIdAndGroup(idofcomplex, ALL_TYPE);
            if (c != null) {
                c.increase();
            } else {
                c = new Complex(idofcomplex, ALL_TYPE, ALL_TYPE_NAME);
                complexes.add(c);
            }

            //  Устанавливаем предыдущий класс от клиента
            prevGroupNum = groupNum;
        }
    int few =2;
    }








    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    private Complex getComplexByComplexIdAndGroup (int idofcomplex, long idofclientgroup) {
        for (Complex complex : complexes) {
            if (complex.getComplex() == idofcomplex &&
                complex.getIdofclientgroup() == idofclientgroup) {
                return complex;
            }
        }
        return null;
    }
    
    public String getPageFilename() {
        return "feed/feed_plan";
    }

    public String getPageTitle() {
        return "План питания";
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }



    public static class Complex {
        private long idofclientgroup;
        private String clientgroupname;
        private int complex;
        private int count;

        public Complex(int complex, long idofclientgroup, String clientgroupname) {
            this.complex = complex;
            this.idofclientgroup = idofclientgroup;
            this.clientgroupname = clientgroupname;
            this.count = 1;
        }

        public int getComplex() {
            return complex;
        }

        public int getCount() {
            return count;
        }

        public long getIdofclientgroup() {
            return idofclientgroup;
        }

        public String getClientgroupname() {
            return clientgroupname;
        }

        public void increase () {
            count++;
        }
    }

    public static class Client {
        private long idoclientgroup;
        private long idofclient;
        private String firstname;
        private String secondname;
        private String surname;
        private long idofrule;
        private String ruleDescription;
        private List<Integer> complexes;
        private int priority;

        public Client(long idoclientgroup, long idofclient, String firstname, String secondname,
                String surname, long idofrule, String ruleDescription, int complex, int priority) {
            this.idoclientgroup = idoclientgroup;
            this.idofclient = idofclient;
            this.firstname = firstname;
            this.secondname = secondname;
            this.surname = surname;
            this.idofrule = idofrule;
            this.ruleDescription = ruleDescription;
            this.complexes = new ArrayList<Integer>();
            this.priority = priority;
        }

        public long getIdoclientgroup() {
            return idoclientgroup;
        }

        public void setIdoclientgroup(long idoclientgroup) {
            this.idoclientgroup = idoclientgroup;
        }

        public long getIdofclient() {
            return idofclient;
        }

        public void setIdofclient(long idofclient) {
            this.idofclient = idofclient;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getSecondname() {
            return secondname;
        }

        public void setSecondname(String secondname) {
            this.secondname = secondname;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public long getIdofrule() {
            return idofrule;
        }

        public void setIdofrule(long idofrule) {
            this.idofrule = idofrule;
        }

        public String getRuleDescription() {
            return ruleDescription;
        }

        public void setRuleDescription(String ruleDescription) {
            this.ruleDescription = ruleDescription;
        }

        public List<Integer> getComplexes() {
            return complexes;
        }

        public void setComplexes(List<Integer> complexes) {
            this.complexes = complexes;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void addComplex (Integer complex) {
            complexes.remove(complex);
            complexes.add(complex);
        }
    }
}
