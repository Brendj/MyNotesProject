/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.feed;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.*;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.08.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class FeedPlanPage extends BasicWorkspacePage implements ClientFeedActionListener, DisableComplexListener {
    private static final long MILLIS_IN_DAY           = 86400000L;
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
    private List<Client> clients;
    private List<Complex> complexes;
    private Calendar planDate;
    private Long selectedIdOfClientGroup;
    private Client selectedClient;
    private Map<Integer, Boolean> disabledComplexes;




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
        org = (Org) session.get(Org.class, 6L);
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
        resetMessages();
        if (planDate == null) {
            planDate = new GregorianCalendar();
            planDate.setTimeInMillis(System.currentTimeMillis());
            clearDate(planDate);
        }
        if (selectedIdOfClientGroup == null) {
            selectedIdOfClientGroup = ALL_TYPE;
        }
        selectedClient = null;
        clients = new ArrayList<Client>();
        complexes = new ArrayList<Complex>();
        List<Complex> allComplexes = new ArrayList<Complex>();
        List<Complex> superComlexGroups = new ArrayList<Complex>();


        String sql = "select cf_clientgroups.idofclientgroup, cf_clientgroups.groupname, cf_clients.idofclient, cf_persons.firstname, "
                + "       cf_persons.secondname, cf_persons.surname, cf_clientscomplexdiscounts.idofrule, description, "
                + "       cf_clientscomplexdiscounts.idofcomplex, cf_discountrules.priority, CAST(substring(groupname FROM '[0-9]+') AS INTEGER) as groupNum, "
                + "       cf_complexinfo.currentprice, cf_temporary_orders.action, cf_temporary_orders.IdOfOrder "
                + "from cf_clients "
                + "join cf_clientscomplexdiscounts on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "left join cf_discountrules on cf_discountrules.idofrule=cf_clientscomplexdiscounts.idofrule "
                + "left join cf_complexinfo on cf_clients.idoforg=cf_complexinfo.idoforg and cf_complexinfo.idofcomplex=cf_clientscomplexdiscounts.idofcomplex "
                + "                            and menudate between :startMenudate and :endMenudate "
                + "left join cf_temporary_orders on cf_temporary_orders.idofclient=cf_clients.idofclient and "
                + "          cf_temporary_orders.idofcomplex=cf_clientscomplexdiscounts.idofcomplex and "
                + "          cf_temporary_orders.plandate=:plandate "
                + "where cf_clients.idoforg=:idoforg " //+ groupFilter
                + "order by groupNum, groupname, cf_persons.firstname, cf_persons.secondname, cf_persons.surname, cf_clients.idofclient, idofcomplex";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", getOrg(session).getIdOfOrg());
        q.setLong("startMenudate", planDate.getTimeInMillis());
        q.setLong("endMenudate", planDate.getTimeInMillis() + MILLIS_IN_DAY);
        q.setLong("plandate", planDate.getTimeInMillis());
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
            Long price = HibernateUtils.getDbLong(o[11]);
            Integer action = HibernateUtils.getDbInt(o[12]);
            Long idoforder = HibernateUtils.getDbLong(o[13]);
            if (price == null) {
                price = 0L;
            }


            //  Добавляем клиента
            Client cl = new Client(idofclientgroup, idofclient, firstName, secondname, surname,
                    idofrule, ruleDescription, idofcomplex, priority, price, action);
            if (action != null) {
                cl.setTemporarySaved(true);
            }
            cl.setIdoforder(idoforder);
            clients.add(cl);


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
            //  Поиск супер-группы в отдельном массиве
            Complex c = getComplexByComplexIdAndGroup(idofcomplex, idofsuperclientgroup, superComlexGroups);
            if (c != null) {
                c.increase();
            } else {
                //  Если существуют ранее установленные групп и идентификатор у них иной, то значит началась другая супер-группа
                if (superComlexGroups.size() > 0 && idofsuperclientgroup != superComlexGroups.get(0).getIdofclientgroup()) {
                    complexes.addAll(superComlexGroups);
                    superComlexGroups.clear();
                }
                c = new Complex(idofcomplex, idofsuperclientgroup, superclientgroupname);
                superComlexGroups.add(c);
            }
            c.addClient(cl);
            //  Поиск группы клиента
            c = getComplexByComplexIdAndGroup(idofcomplex, idofclientgroup);
            if (c != null) {
                c.increase();
            } else {
                c = new Complex(idofcomplex, idofclientgroup, clientgroupname);
                complexes.add(c);
            }
            c.addClient(cl);
            //  Поиск иговой группы клиента
            c = getComplexByComplexIdAndGroup(idofcomplex, ALL_TYPE, allComplexes);
            if (c != null) {
                c.increase();
            } else {
                c = new Complex(idofcomplex, ALL_TYPE, ALL_TYPE_NAME);
                allComplexes.add(c);
            }
            c.addClient(cl);
        }
        //  Последний комплекс не попадет в список автоматически, добавляем его вручную
        if (superComlexGroups.size() > 0) {
            complexes.addAll(superComlexGroups);
        }
        complexes.addAll(allComplexes);


        // Заполняем выключенные комплексы так, что все остаются
        if (disabledComplexes == null) {
            disabledComplexes = new HashMap<Integer, Boolean>();
        }
        disabledComplexes.clear();
        for (Complex c : complexes) {
            disabledComplexes.put(c.getComplex(), false);
        }
    }

    @Transactional
    public void saveClientFeedAction (List<Client> clients, int actionType) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            saveClientFeedAction(session, clients, actionType);
        } catch (Exception e) {
            logger.error("Failed to save client's into temporary table", e);
            //sendError("Не удалось изменить статус питания клиента " + client.getFullName() + ": " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void saveClientFeedAction(Session session, List<Client> clients, int actionType) {
        for (Client client : clients) {
            if (client.getActionType() == actionType) {
                continue;
            }

            saveClientFeedAction(session, client, actionType);
        }
    }

    public void saveClientFeedAction(Session session, Client client, int actionType) {
        //  Если уже имеется заказ, значит сохранение во временную таблицу запрещено
        if (client.getSaved()) {
            return;
        }


       String sql = "";
        //  Проверяем, сохранен ли клиент во временную таблицу в БД
        if (client.getTemporarySaved()) {
            sql = "update cf_temporary_orders set action=:action, modificationdate=:date, idofuser=:idofuser "
                + "where idofclient=:idofclient and idofcomplex=:idofcomplex and plandate=:plandate";
        } else {
            sql = "insert into cf_temporary_orders (idoforg, idofclient, idofcomplex, plandate, action, creationdate, idofuser) "
                + "values (:idoforg, :idofclient, :idofcomplex, :plandate, :action, :date, :idofuser)";
        }

        clearDate(planDate);
        long currentTS = System.currentTimeMillis();
        org.hibernate.Query query = session.createSQLQuery(sql);
        query.setLong("idofclient", client.getIdofclient());
        query.setInteger("idofcomplex", client.getComplex());
        query.setLong("plandate", planDate.getTimeInMillis());
        query.setInteger("action", actionType);
        query.setLong("date", currentTS);
        query.setLong("idofuser", -1L);
        if (!client.getTemporarySaved()) {
            query.setLong("idoforg", getOrg().getIdOfOrg());
        }
        query.executeUpdate();
        client.setActionType(actionType);
        client.setTemporarySaved(true);
    }

    @Transactional
    public Map<Client, String> saveOrders () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return saveOrders(session);
        } catch (Exception e) {
            logger.error("Failed to save orders", e);
            //sendError("Не создать заказ для " + client.getFullName() + ": " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
        return Collections.emptyMap();
    }

    public Map<Client, String> saveOrders(Session session) {
        Map <Client, String> result = new HashMap<Client, String>();
        boolean hasError = false;
        for (Client client : clients) {
            if (client.getActionType() != ClientFeedActionEvent.PAY_CLIENT || client.getSaved()) {
                continue;
            }
            //  Ошибка, для теста
            if(!hasError) {
                result.put(client, "Не удалось добавить заказ, здесь указывается причина ошибки");
                hasError = true;
                continue;
            }


            Random rand = new Random();
            long idoforder = rand.nextLong();
            org.hibernate.Query query = session.createSQLQuery(
                    "update cf_temporary_orders set idoforder=:idoforder, modificationdate=:date "
                            + "where idofclient=:idofclient and idofcomplex=:idofcomplex and plandate=:plandate");
            query.setLong("idofclient", client.getIdofclient());
            query.setInteger("idofcomplex", client.getComplex());
            query.setLong("plandate", planDate.getTimeInMillis());
            query.setLong("date", System.currentTimeMillis());
            query.setLong("idoforder", idoforder);
            query.executeUpdate();
            client.setIdoforder(idoforder);
            result.put(client, "Заказ успешно составлен");
        }
        return result;
    }

    @Transactional
    public void clear () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            clear(session);
        } catch (Exception e) {
            logger.error("Failed to save orders", e);
            //sendError("Не создать заказ для " + client.getFullName() + ": " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void clear(Session session) {
        //  Удаляем все заказы, имеющиеся за выбранную дату
        org.hibernate.Query query = session.createSQLQuery(
                "delete from cf_temporary_orders where idoforg=:idoforg and plandate=:plandate");
        query.setLong("idoforg", getOrg().getIdOfOrg());
        query.setLong("plandate", planDate.getTimeInMillis());
        query.executeUpdate();
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

    public void doChangePlanDate(ValueChangeEvent event) {
        planDate.setTimeInMillis(((Date) event.getNewValue()).getTime());
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doChangeGroup (long idofclientgroup) {
        resetMessages();
        selectedIdOfClientGroup = idofclientgroup;
        //RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doShowOrderRegistrationResultPanel () {
        resetMessages();
        //  Созраняем заказы
        Map <Client, String> result = RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveOrders();
        //  Передаем полученный массив в модальное окно,
        //  чтобы отобразить ошибки или успехи сохранения заказов
        OrderRegistrationResultPanel panel = RuntimeContext.getAppContext().getBean(OrderRegistrationResultPanel.class);
        panel.setClientSaveMessages(result);
        MainPage.getSessionInstance().doShowOrderRegistrationResultPanel();
    }

    public void doClearPlan () {
        resetMessages();
        for (Client cl : clients) {
            if (cl.getIdoforder() != null) {
                sendError("Присутствуют уже оплаченные заказы, план очистить невозможно");
                return;
            }
        }

        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).clear();
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }
    
    public void doShowClientFeedActionPanel(Client cl) {
        resetMessages();
        selectedClient = cl;
        MainPage.getSessionInstance().doShowClientFeedActionPanel();
    }

    public void doShowDisableComplexPanel() {
        resetMessages();
        DisableComplexPanel panel = RuntimeContext.getAppContext().getBean(DisableComplexPanel.class);
        panel.setComplexes(disabledComplexes);
        MainPage.getSessionInstance().doShowDisableComplexPanel();
    }

    public void doDecreaseDay() {
        planDate.setTimeInMillis(planDate.getTimeInMillis() - MILLIS_IN_DAY);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doIncreaseDay() {
        planDate.setTimeInMillis(planDate.getTimeInMillis() + MILLIS_IN_DAY);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void onClientFeedActionEvent (ClientFeedActionEvent event) {
        //  Если значение было установлено для всех, то выполняем выбранную операцию для всех отображаемых клиентов
        int actionType = event.getActionType();
        List<Client> clients = null;
        if (event.getActionType() > ClientFeedActionEvent.ALL_CLIENTS) {
            clients = getClients();
            actionType = actionType - ClientFeedActionEvent.ALL_CLIENTS;
        } else {
            clients = new ArrayList<Client>();
            clients.add(selectedClient);
        }
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClientFeedAction(clients,actionType);
    }

    public void onDisableComplexEvent (DisableComplexEvent event) {
        disabledComplexes = event.getComplexes();
    }

    public Date getPlanDate() {
        return planDate.getTime();
    }

    public String getSelectedClientGroupName() {
        if (selectedIdOfClientGroup == null) {
            return "";
        }
        for (Complex complex : complexes) {
            if (complex.getIdofclientgroup() == selectedIdOfClientGroup) {
                return complex.getClientgroupname();
            }
        }
        return "";
    }

    public String getComplexesTotalString() {
        Map<Integer, Integer> [] arr = getClientsStatistic(clients);
        Map<Integer, Integer> payed = arr [0];
        Map<Integer, Integer> toPay = arr [1];
        Map<Integer, Integer> total = arr [2];


        //  Составляем строку комплексов
        StringBuilder builder = new StringBuilder();
        for (Integer complex : getComplexes()) {
            Integer p = payed.get(complex);
            Integer n = toPay.get(complex);
            Integer t = total.get(complex);
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("Комплекс №").append(complex).append(" (").
                    append(p == null ? 0 : p).append("/").
                    append(n == null ? 0 : n).append("/").
                    append(t == null ? 0 : t).append(")");
        }
        return builder.toString();
    }

    public String getCurrentTotalString () {
        Map<Integer, Integer> [] arr = getClientsStatistic(getClients());
        Map<Integer, Integer> payed = arr [0];
        Map<Integer, Integer> toPay = arr [1];
        Map<Integer, Integer> total = arr [2];


        //  Суммируем все значения от комплексов
        int payedTotal = 0;
        for (Integer complex : payed.keySet()) {
            payedTotal += payed.get(complex);
        }
        int toPayTotal = 0;
        for (Integer complex : toPay.keySet()) {
            toPayTotal += toPay.get(complex);
        }

        //  Составляем строку
        return "К оплате: " + toPayTotal + ", Оплачено: " + payedTotal;
    }
    
    private Map<Integer, Integer> [] getClientsStatistic(List<Client> clients) {
        Map<Integer, Integer> payed = new HashMap<Integer, Integer>();
        Map<Integer, Integer> toPay = new HashMap<Integer, Integer>();
        Map<Integer, Integer> total = new HashMap<Integer, Integer>();
        for (Client cl : clients) {
            //  Оплаченные
            if (cl.getIdoforder() != null) {
                Integer now = payed.get(cl.getComplex());
                if (now == null) {
                    now = 0;
                }
                now++;
                payed.put(cl.getComplex(), now);
            }
            //  К оплате
            else if (cl.getActionType() == ClientFeedActionEvent.PAY_CLIENT) {
                Integer now = toPay.get(cl.getComplex());
                if (now == null) {
                    now = 0;
                }
                now++;
                toPay.put(cl.getComplex(), now);
            }

            //  Всего
            Integer now = total.get(cl.getComplex());
            if (now == null) {
                now = 0;
            }
            now++;
            total.put(cl.getComplex(), now);
        }
        return new Map[] { payed, toPay, total };
    }

    public void setPlanDate(Date planDate) {
        this.planDate.setTimeInMillis(planDate.getTime());
    }

    public List<Client> getClients() {
        //  Иначе, производим поиск по комплексам и  загружаем их
        List<Client> foundClients = new ArrayList<Client>();
        for (Complex c : complexes) {
            //  Производим поиск по отключенным комплексам
            if (disabledComplexes.get(c.getComplex())) {
                continue;
            }

            //  Если тип "Все", то отображаем всех клиентов; иначе, отображаем только выбранных
            if (c.getIdofclientgroup() == selectedIdOfClientGroup.longValue()) {
                foundClients.addAll(c.getClients());
            }
        }
        return foundClients;
    }
    
    public List<Long> getGroups() {
        List<Long> res = new ArrayList<Long>();
        for (Complex c : complexes) {
            if (res.contains(c.getIdofclientgroup())) {
                continue;
            }
            res.add(c.getIdofclientgroup());
        }
        return res;
    }

    public List<Integer> getComplexes() {
        List<Integer> res = new ArrayList<Integer>();
        for (Complex c : complexes) {
            if (res.contains(c.getComplex())) {
                continue;
            }
            //  Если комплекс отключен, то не отображаем его
            if (disabledComplexes.get(c.getComplex())) {
                continue;
            }
            res.add(c.getComplex());
        }
        return res;
    }
    
    public int getPayedComplexCount(long idoclientgroup, int complex) {
        for (Complex c : complexes) {
            if (c.getIdofclientgroup() == idoclientgroup && c.getComplex() == complex) {
                //  Найдя нужный комплекс, приступаем к подсчету оплаченных комплексов
                int count = 0;
                for (Client cl : c.getClients()) {
                    if (cl.getActionType() == ClientFeedActionEvent.PAY_CLIENT) {
                        count++;
                    }
                }
                return count;
            }
        }
        return 0;
    }
    
    public int getComplexCount(long idoclientgroup, int complex) {
        for (Complex c : complexes) {
            if (c.getIdofclientgroup() == idoclientgroup && c.getComplex() == complex) {
                return c.getCount();
            }
        }
        return 0;
    }

    public String getGroupName(long idofclientgroup) {
        for (Complex c : complexes) {
            if (c.getIdofclientgroup() == idofclientgroup) {
                return c.getClientgroupname();
            }
        }
        return "";
    }
    
    public String getClientGroupStyleClass(long idofclientgroup) {
        if (selectedIdOfClientGroup != null &&
            selectedIdOfClientGroup == idofclientgroup) {
            return "selectClientGroup";
        }
        if (idofclientgroup == ELEMENTARY_CLASSES_TYPE ||
            idofclientgroup == MIDDLE_CLASSES_TYPE ||
            idofclientgroup == HIGH_CLASSES_TYPE ||
            idofclientgroup == ORDER_TYPE ||
            idofclientgroup == ALL_TYPE) {
            return "subcategoryClientGroup";
        }
        return "";
    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    private Complex getComplexByComplexIdAndGroup (int idofcomplex, long idofclientgroup) {
        return getComplexByComplexIdAndGroup (idofcomplex, idofclientgroup, complexes);
    }
    
    private Complex getComplexByComplexIdAndGroup (int idofcomplex, long idofclientgroup, List<Complex> complexes) {
        for (Complex complex : complexes) {
            if (complex.getComplex() == idofcomplex && complex.getIdofclientgroup() == idofclientgroup) {
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
    
    public static void clearDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }



    public static class Complex {
        private long idofclientgroup;
        private String clientgroupname;
        private int complex;
        private int count;
        private List<Client> clients;

        public Complex () {

        }

        public Complex(int complex, long idofclientgroup, String clientgroupname) {
            this.complex = complex;
            this.idofclientgroup = idofclientgroup;
            this.clientgroupname = clientgroupname;
            this.count = 1;
            clients = new ArrayList<Client>();
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

        public void addClient(Client cl) {
            clients.add(cl);
        }
        
        public List<Client> getClients() {
            return clients;
        }

        @Override
        public String toString() {
            return "Complex{" +
                    "idofclientgroup=" + idofclientgroup +
                    ", clientgroupname='" + clientgroupname + '\'' +
                    ", complex=" + complex +
                    ", count=" + count +
                    '}';
        }
    }


    public static class Client {
        private long idofclientgroup;
        private long idofclient;
        private String firstname;
        private String secondname;
        private String surname;
        private long idofrule;
        private String ruleDescription;
        private int complex;
        private int priority;
        private int actionType;
        private long price;
        private boolean temporarySaved;
        private Long idoforder;

        public Client(long idofclientgroup, long idofclient, String firstname, String secondname,
                String surname, long idofrule, String ruleDescription, int complex, int priority,
                long price, Integer action) {
            this.idofclientgroup = idofclientgroup;
            this.idofclient = idofclient;
            this.firstname = firstname;
            this.secondname = secondname;
            this.surname = surname;
            this.idofrule = idofrule;
            this.ruleDescription = ruleDescription;
            this.complex = complex;
            this.priority = priority;
            actionType = action == null ? ClientFeedActionEvent.RELEASE_CLIENT : action;
            this.price = price;
        }

        public long getIdofclientgroup() {
            return idofclientgroup;
        }

        public void setIdofclientgroup(long idofclientgroup) {
            this.idofclientgroup = idofclientgroup;
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

        public int getComplex() {
            return complex;
        }

        public void setComplex(int complex) {
            this.complex = complex;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
        
        public String getFullName () {
            return firstname + "<br/>" + secondname + "<br/>" + surname;
        }

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }
        
        public String getPrice () {
            if (price == 0L) {
                return "0";
            }
            BigDecimal bd = new BigDecimal(price / 100).setScale(1, BigDecimal.ROUND_HALF_DOWN);
            return bd.toString();
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public String getAction() {
            switch (actionType) {
                case ClientFeedActionEvent.BLOCK_CLIENT:
                    return "БЛОК";
                case ClientFeedActionEvent.PAY_CLIENT:
                    return "ОПЛАТА";
                case ClientFeedActionEvent.RELEASE_CLIENT:
                    return "...";
                default:
                    return "...";
            }
        }
        
        public String getActionIcon() {
            switch (actionType) {
                case ClientFeedActionEvent.BLOCK_CLIENT:
                    return "stop";
                case ClientFeedActionEvent.PAY_CLIENT:
                    return "play";
                case ClientFeedActionEvent.RELEASE_CLIENT:
                    return "stop";
                default:
                    return "stop";
            }
        }

        public Long getIdoforder() {
            return idoforder;
        }

        public void setIdoforder(Long idoforder) {
            this.idoforder = idoforder;
        }

        public boolean getTemporarySaved() {
            return temporarySaved;
        }

        public void setTemporarySaved(boolean temporarySaved) {
            this.temporarySaved = temporarySaved;
        }

        public boolean getSaved() {
            return idoforder != null;
        }
        
        public String getLineStyleClass (){
            if (idoforder != null) {
                return "payed";
            }
            return "";
        }

        @Override
        public String toString() {
            return "Client{" +
                    "user='" + firstname + '\'' +
                    " '" + secondname + '\'' +
                    " '" + surname + '\'' +
                    " '" + ruleDescription + '\'' +
                    ", idofclient=" + idofclient +
                    '}';
        }
    }
}
