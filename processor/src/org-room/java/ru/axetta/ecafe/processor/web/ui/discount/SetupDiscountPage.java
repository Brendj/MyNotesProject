/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.discount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.dao.DAOServices;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.ClientListEditPage;

import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SetupDiscountPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(SetupDiscountPage.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private Org org;
    private String errorMessages;
    private String infoMessages;
    private List<String> groups;
    private String group;
    private Map<Long, String> categories;
    private Long category;
    private List<Client> clients;
    private List<DiscountColumn> columns;




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
        loadCategories(session);
        loadGroups(session);
        loadClients(session);
        buildColumns(session);
    }

    public void loadClients (Session session) {
        String groupJoin = "";
        String groupRestr = "";
        String discountRestr = "";
        if (group != null && group.length() > 0) {
            groupJoin = "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg ";
            groupRestr = " and cf_clientgroups.groupname=:groupname ";
        }
        if (category != null && category > 0) {
            discountRestr = " and cf_clients.idofclient in (select cl_cat_disc2.idofclient from cf_clients_categorydiscounts as cl_cat_disc2 where idofcategorydiscount=:idofcategorydiscount) ";
        }

        clients = new ArrayList<Client>();
        Long prevIdoOfClient = null;
        Client cl = null;
        
        String sql = "select cf_clients.idofclient, cf_persons.firstname, cf_persons.secondname, cf_persons.surname, cf_clients_categorydiscounts.idofcategorydiscount "
                + "from cf_clients "
                + "left join cf_persons on cf_persons.idofperson=cf_clients.idofperson "
                + "left join cf_clients_categorydiscounts on cf_clients.idofclient=cf_clients_categorydiscounts.idofclient "
                + groupJoin
                + "where cf_clients.idoforg=:idoforg and surname<>'' " + groupRestr + discountRestr
                + "order by surname, firstname, secondname, cf_clients_categorydiscounts.idofcategorydiscount";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", getOrg(session).getIdOfOrg());
        if (group != null && group.length() > 0) {
            q.setString("groupname", group);
        }
        if (category != null && category > 0) {
            q.setLong("idofcategorydiscount", category);
        }
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClient = HibernateUtils.getDbLong(o[0]);
            String firstName = HibernateUtils.getDbString(o[1]);
            String secondname = HibernateUtils.getDbString(o[2]);
            String surname = HibernateUtils.getDbString(o[3]);
            Long idofcategorydiscount = HibernateUtils.getDbLong(o[4]);
            if (prevIdoOfClient == null || prevIdoOfClient.longValue() != idOfClient.longValue()) {
                cl = new Client(idOfClient, firstName, secondname, surname);
                prevIdoOfClient = idOfClient;
                clients.add(cl);
            }
            if (idofcategorydiscount != null) {
                //  Устанавливаем категорию для клиента 
                cl.addRule(idofcategorydiscount, true);
            }
        }
        recalculateOverall ();
    }

    public void recalculateOverall() {
        OverallClient overall = new OverallClient ("ИТОГО");
        overall.fill(categories);
        for (int i=0; i<clients.size(); i++) {
            Client c = clients.get(i);
            if (!c.getInput()) {
                clients.remove(i);
                i--;
                continue;
            }
            for (Long idofrule : categories.keySet()) {
                Boolean flag = c.getRules().get(idofrule);
                if (flag != null && flag.equals(Boolean.TRUE)) {
                    //  Обновляем итоговое значение
                    overall.addValue(idofrule, 1);
                }
            }
        }
        clients.add(overall);
    }

    @Transactional
    public void loadGroups() throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadGroups(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadGroups(Session session) {
        groups = DAOServices.getInstance().loadGroups(session, getOrg().getIdOfOrg(), true);
        Collections.sort(groups, new ClientListEditPage.ClientComparator ());
    }

    @Transactional
    public void loadCategories () throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadCategories(session);
        } catch (Exception e) {
            logger.error("Failed to load catregories for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadCategories(Session session) {
        if (categories != null) {
            return;
        }
        categories = DAOServices.getInstance().loadDiscountCategories(session);
    }

    public void buildColumns (Session session) {
        if (columns != null) {
            return;
        }
        loadCategories(session);

        columns = new ArrayList<DiscountColumn>();
        for (Long id : categories.keySet()) {
            String title = categories.get(id);
            columns.add(new DiscountColumn(id, title));
        }
    }

    @Transactional
    public void save(long idofclient) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            save(session, idofclient);
            sendInfo("Изменения успешно внесены");
        } catch (Exception e) {
            logger.error("Failed to load discounts data", e);
            sendError("При внесении изменений произошла ошибка: " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void save(Session session, long idofclient) throws Exception {
        //  Изменяем действующее значение
        Client client = null;
        for (Client c : clients) {
            if (c.getIdofclient() == idofclient) {
                //c.getRules().put(idofrule, !c.getRules().get(idofrule));
                client = c;
                break;
            }
        }

        List<Long> idOfCategoryList = new ArrayList<Long>();
        for (Long idofcategorydiscount : client.getRules().keySet()) {
            if (client.getRules().get(idofcategorydiscount).equals(Boolean.FALSE)) {
                continue;
            }
            idOfCategoryList.add(idofcategorydiscount);
        }

        //  Загружаем клиента из БД
        ru.axetta.ecafe.processor.core.persistence.Client cl = (ru.axetta.ecafe.processor.core.persistence.Client) session
                .get(ru.axetta.ecafe.processor.core.persistence.Client.class, client.getIdofclient());
        ClientManager.setCategories(session, cl, idOfCategoryList);
        recalculateOverall();
    }

    @Transactional
    public void saveAll() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            saveAll(session);
            sendInfo("Изменения успешно внесены");
        } catch (Exception e) {
            logger.error("Failed to load discounts data", e);
            sendError("При внесении изменений произошла ошибка: " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void saveAll(Session session) throws Exception {
        for (Client client : clients) {
            if (!client.getInput()) {
                continue;
            }
            //  Составляем список тех льгот, которые отмечены у клиента
            List<Long> idOfCategoryList = new ArrayList<Long>();
            for (Long idofcategorydiscount : client.getRules().keySet()) {
                if (client.getRules().get(idofcategorydiscount).equals(Boolean.FALSE)) {
                    continue;
                }
                idOfCategoryList.add(idofcategorydiscount);
            }
            if (idOfCategoryList.size() < 1) {
                continue;
            }

            //  Загружаем клиента из БД
            ru.axetta.ecafe.processor.core.persistence.Client cl = (ru.axetta.ecafe.processor.core.persistence.Client) session
                    .get(ru.axetta.ecafe.processor.core.persistence.Client.class, client.getIdofclient());
            ClientManager.setCategories(session, cl, idOfCategoryList);
        }
    }








    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();
    }

    public void doApply () {
        /*RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).saveAll();
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();*/
    }

    public void doCancel () {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();
    }

    public void doChangeGroup (javax.faces.event.ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();
    }

    public void doChangeCategory (javax.faces.event.ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).fill();
    }

    public void doChangeDiscount(ActionEvent event) {
        Long idofclient = (Long) event.getComponent().getAttributes().get("idofclient");
        if (idofclient == -1L) {
            return;
        }

        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).save(idofclient);
    }

    public List<SelectItem> getGroups() throws Exception {
        if (groups == null) {
            loadGroups();
        }
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (String group : groups) {
            res.add(new SelectItem(group, group));
        }
        return res;
    }

    public List<SelectItem> getCategories () throws  Exception {
        RuntimeContext.getAppContext().getBean(SetupDiscountPage.class).loadCategories();
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (Long idofcategory : categories.keySet()) {
            res.add(new SelectItem(idofcategory, categories.get(idofcategory)));
        }
        return res;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<DiscountColumn> getColumns() {
        return columns;
    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "discount/setup_discount";
    }

    public String getPageTitle() {
        return "Групповое питание";
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


    public static class Client {
        protected long idofclient;
        protected String firstName;
        protected String secondName;
        protected String surname;
        protected Map<Long, Boolean> rules;
        protected Map<Long, Integer> values;

        public Client () {

        }

        public Client (long idofclient, String firstName, String secondName, String surname) {
            this.idofclient = idofclient;
            this.firstName = firstName;
            this.secondName = secondName;
            this.surname = surname;
            rules = new HashMap<Long, Boolean>();
        }

        public long getIdofclient () {
            return idofclient;
        }

        public String getFullName () {
            String n = surname;
            if (firstName != null && firstName.length() > 0) {
                n += " " + firstName;
            }
            if (secondName != null && secondName.length() > 0) {
                n += " " + secondName;
            }
            return n;
        }

        public Map<Long, Integer> getValues() {
            return values;
        }

        public Map<Long, Boolean> getRules() {
            return rules;
        }

        public void setRules(Map<Long, Boolean> rules) {
            this.rules = rules;
        }

        public void addRule (long idofrule, boolean flag) {
            rules.put(idofrule, flag);
        }

        public boolean getInput() {
            return true;
        }
    }

    public static class OverallClient extends Client{
        protected String title;

        public OverallClient (String title) {
            this.idofclient = -1L;
            this.title = title;
            values = new HashMap<Long, Integer>();
        }

        public void addValue(Long idofcategorydiscount, int val) {
            Integer v = values.get(idofcategorydiscount);
            if (v == null) {
                v = 0;
            }
            v += val;
            values.put(idofcategorydiscount, v);
        }

        @Override
        public String getFullName () {
            return title;
        }
        
        @Override
        public boolean getInput () {
            return false;
        }

        public void fill (Map<Long, String> categories) {
            for (Long k : categories.keySet()) {
                values.put(k, 0);
            }
        }
    }


    public static class DiscountColumn {
        private long id;
        private String title;

        public DiscountColumn (long id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getTitle() {
            String[] chars = title.split("");
            StringBuilder stringBuilder = new StringBuilder();
            for (String c : chars) {
                stringBuilder.append(c).append("<br />");
            }
            return stringBuilder.toString();
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
