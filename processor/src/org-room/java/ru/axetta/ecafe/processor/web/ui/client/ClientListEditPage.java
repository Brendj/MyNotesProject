/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.dao.DAOServices;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.CurrentDateChangeEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.axetta.ecafe.processor.web.ui.Constants;
import ru.axetta.ecafe.processor.web.ui.OrgRoomMainPage;
import ru.axetta.ecafe.processor.web.ui.auth.LoginBean;
import ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreateEvent;
import ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreateListener;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClientListEditPage extends BasicWorkspacePage implements GroupCreateListener {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static final String NO_GROUP = "- Без группы -";
    public static final String GROUP_NO_CLIENTS = "- Клиенты отсутствуют -";

    private static final long DAY = 86400000;
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String GROUP_TYPE = "group";
    public static final String CLIENT_TYPE = "client";
    private static final Logger logger = LoggerFactory.getLogger(ClientListEditPage.class);
    private Org org;
    private final SelectedClient selectedClient = new SelectedClient();
    private List<String> groups;
    private boolean isLeafSelected;
    private List<Client> teachers;
    private Map<String, List<Client>> dbTree;
    private TreeNode tree = null;
    private UITree treeComponent;
    private String lookupClientName;
    public static DateFormat df = new SimpleDateFormat(DATE_FORMAT);
    private String errorMessages;
    private String infoMessages;
    private Date enterEventDate;
    private List<CategoryDiscount> categoryDiscounts;
    private boolean allowRemoveGroup;
    private String selectedClientGroup;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public void loadClientsForGroup(List<String> groups) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadClientsForGroup(session, groups);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadClientsForGroup(Session session, List<String> groups) throws Exception {
        if (groups == null || groups.size() < 1) {
            return;
        }

        StringBuilder groupsRestrict = new StringBuilder();
        String noGroup = "";
        for (String group : groups) {
            if (group.equals(NO_GROUP)) {
                noGroup = "cf_clients.idofclientgroup is null";
            } else {
                if (groupsRestrict.length() > 0) {
                    groupsRestrict.append(", ");
                }
                groupsRestrict.append("'").append(group).append("'");
            }
        }
        String groupsClause = " and (" + (groupsRestrict.length() > 0 ? "cf_clientgroups.groupname in (" + groupsRestrict + ")" : "") +
                                         (noGroup.length() > 0 ? (groupsRestrict.length() > 0 ? " or " : " ") + noGroup : "") + ") ";
        
        List<Client> clients = new ArrayList<Client>();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String sql =
                "select idofclient, firstname, secondname, surname, groupname, cf_persons.idofperson, cf_clients.idofclientgroup "
                + "from cf_clients "
                + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                + "left join cf_clientgroups on cf_clients.idoforg=cf_clientgroups.idoforg and cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup "
                + "where cf_clients.idoforg=:idoforg " + groupsClause + " "
                + "order by groupname, surname, firstname, secondname";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", org.getIdOfOrg());
        List resultList = q.list();
        String prevGroupName = "";
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClient = HibernateUtils.getDbLong(o[0]);
            String firstName = HibernateUtils.getDbString(o[1]);
            String secondName = HibernateUtils.getDbString(o[2]);
            String surname = HibernateUtils.getDbString(o[3]);
            String groupName = HibernateUtils.getDbString(o[4]);
            Long idOfPerson = HibernateUtils.getDbLong(o[5]);
            Long idOfClientGroup = HibernateUtils.getDbLong(o[6]);

            if (!groupName.equals(prevGroupName)) {
                if (clients != null) {
                    dbTree.put(prevGroupName, clients);
                }
                clients = new ArrayList<Client>();
                prevGroupName = groupName;
            }

            Client i = new Client(idOfClient, firstName, secondName, surname, idOfPerson);
            clients.add(i);
        }
        if (clients != null) {
            dbTree.put(prevGroupName, clients);
        }

        buildGroupsTree(clients);
    }


    @Transactional
    public void fill(boolean reset) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session, reset);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session, boolean reset) throws Exception {
        categoryDiscounts = loadDiscounts(session);
        if (reset) {
            reset();
        }
        allowRemoveGroup = false;
        selectedClientGroup = null;


        /* Загрузка учителей */
        teachers = new ArrayList<Client>();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String sql =
                "select idofclient, firstname, secondname, surname, groupname, cf_persons.idofperson, cf_clients.idofclientgroup "
                        + "from cf_clients " + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                        + "left join cf_clientgroups on cf_clients.idoforg=cf_clientgroups.idoforg and cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup "
                        + "where cf_clients.idoforg=:idoforg and cf_clients.idofclientgroup=" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " "
                        + "order by groupname, surname, firstname, secondname";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", org.getIdOfOrg());
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClient = HibernateUtils.getDbLong(o[0]);
            String firstName = HibernateUtils.getDbString(o[1]);
            String secondName = HibernateUtils.getDbString(o[2]);
            String surname = HibernateUtils.getDbString(o[3]);
            String groupName = HibernateUtils.getDbString(o[4]);
            Long idOfPerson = HibernateUtils.getDbLong(o[5]);
            Long idOfClientGroup = HibernateUtils.getDbLong(o[6]);

            Client i = new Client(idOfClient, firstName, secondName, surname, idOfPerson);
            teachers.add(i);
        }

        dbTree = new TreeMap<String, List<Client>>();



        //  Добавления в SQL
        /*String lookupClientByNameClause = "";
        if (lookupClientName != null && lookupClientName.length() > 0) {
            String look = lookupClientName.trim().toLowerCase().replaceAll("  ", " ");
            lookupClientByNameClause =
                    " and (lower(firstname || ' ' || secondname || ' ' || surname) like '%" + look + "%' or "
                            + "lower(secondname || ' ' || firstname || ' ' || surname) like '%" + look + "%' or "
                            + "lower(secondname || ' ' || surname || ' ' || firstname) like '%" + look + "%' or "
                            + "lower(firstname || ' ' || surname || ' ' || secondname) like '%" + look + "%' or "
                            + "lower(surname || ' ' || secondname || ' ' || firstname) like '%" + look + "%' or "
                            + "lower(surname || ' ' || firstname || ' ' || secondname) like '%" + look + "%') ";
        }

        //  Загружаем данные и записываем их в map

        teachers = new ArrayList<Client>();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String sql =
                "select idofclient, firstname, secondname, surname, groupname, cf_persons.idofperson, cf_clients.idofclientgroup "
                        + "from cf_clients " + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                        + "left join cf_clientgroups on cf_clients.idoforg=cf_clientgroups.idoforg and cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup "
                        + "where cf_clients.idoforg=:idoforg " + lookupClientByNameClause
                        //+ "  and cf_clientgroups.idofclientgroup=1000000004 "
                        + "order by groupname, surname, firstname, secondname";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", org.getIdOfOrg());
        List resultList = q.list();
        List<Client> clients = new ArrayList<Client>();
        String prevGroupName = "";
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClient = HibernateUtils.getDbLong(o[0]);
            String firstName = HibernateUtils.getDbString(o[1]);
            String secondName = HibernateUtils.getDbString(o[2]);
            String surname = HibernateUtils.getDbString(o[3]);
            String groupName = HibernateUtils.getDbString(o[4]);
            Long idOfPerson = HibernateUtils.getDbLong(o[5]);
            Long idOfClientGroup = HibernateUtils.getDbLong(o[6]);
            if (!groupName.equals(prevGroupName)) {
                if (clients != null) {
                    dbTree.put(prevGroupName, clients);
                }
                clients = new ArrayList<Client>();
                prevGroupName = groupName;
            }
            Client i = new Client(idOfClient, firstName, secondName, surname, idOfPerson);
            clients.add(i);

            //  Если клиент относится к группе Пед. состава, то заносим его в список учителей
            if (idOfClientGroup != null && idOfClientGroup.longValue() == ClientGroup.Predefined.CLIENT_EMPLOYEES
                    .getValue().longValue()) {
                teachers.add(i);
            }
        }
        if (clients != null) {
            dbTree.put(prevGroupName, clients);
        }*/

        loadGroups(session);
        buildGroupsTree(Collections.EMPTY_LIST);
    }

    @Transactional
    public void loadSelectedClientData() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadSelectedClientData(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadSelectedClientData(Session session) {
        if (!isClientSelected()) {
            return;
        }

        //  Вытаскиваем 10 последних событий, по ним можно будет определить находится клиент в школе или нет
        String sql =
                "select groupname, contractid, address, phone, mobile, email, notifyviaemail, notifyviasms, fax, cf_clients.lastupdate, "
                        + "contractdate, cardno, cardtype, cf_cards.createddate, validdate, state, balance, expenditurelimit, limits, lockreason, "
                        + "firstname, secondname, surname, passdirection, evtdatetime, remarks "
                        + "from cf_clients "
                        + "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient and cf_cards.state=" + Card.ACTIVE_STATE + " "
                        + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                        + "left join cf_clientgroups on cf_clients.idoforg=cf_clientgroups.idoforg and cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup "
                        + "left join cf_enterevents on cf_clients.idofclient=cf_enterevents.idofclient "
                        + "where cf_clients.idofclient=:idofclient "
                        + "order by evtdatetime desc "
                        + "limit 1";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idofclient", selectedClient.getIdOfClient());
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            String groupName = HibernateUtils.getDbString(o[0]);
            Long contractId = HibernateUtils.getDbLong(o[1]);
            String address = HibernateUtils.getDbString(o[2]);
            String phone = HibernateUtils.getDbString(o[3]);
            String mobile = HibernateUtils.getDbString(o[4]);
            String email = HibernateUtils.getDbString(o[5]);
            Boolean notifyviaemail = HibernateUtils.getDbBoolean(o[6]);
            Boolean notifyviasms = HibernateUtils.getDbBoolean(o[7]);
            String fax = HibernateUtils.getDbString(o[8]);
            Long lastupdate = HibernateUtils.getDbLong(o[9]);
            Long contractdate = HibernateUtils.getDbLong(o[10]);
            Long cardNo = HibernateUtils.getDbLong(o[11]);
            Integer cardType = HibernateUtils.getDbInt(o[12]);
            Long cardCreatedDate = HibernateUtils.getDbLong(o[13]);
            Long validdate = HibernateUtils.getDbLong(o[14]);
            Integer cardStatus = HibernateUtils.getDbInt(o[15]);
            Long balance = HibernateUtils.getDbLong(o[16]);
            Long overdraftLimit = HibernateUtils.getDbLong(o[17]);
            Long limit = HibernateUtils.getDbLong(o[18]);
            String blockReason = HibernateUtils.getDbString(o[19]);
            String firstName = HibernateUtils.getDbString(o[20]);
            String secondName = HibernateUtils.getDbString(o[21]);
            String surname = HibernateUtils.getDbString(o[22]);
            Integer passDirection = HibernateUtils.getDbInt(o[23]);
            passDirection = passDirection == null ? EnterEvent.EXIT : passDirection;
            String remarks = HibernateUtils.getDbString(o[25]);

            if (phone.length() > 0 && phone.startsWith("7")) {
                phone = phone.substring(1);
            }
            if (mobile.length() > 0 && mobile.startsWith("7")) {
                mobile = mobile.substring(1);
            }
            if (fax.length() > 0 && fax.startsWith("7")) {
                fax = fax.substring(1);
            }

            selectedClient.setFirstName(firstName);
            selectedClient.setSecondName(secondName);
            selectedClient.setSurname(surname);
            selectedClient.setDefaultClientGroup(groupName);
            selectedClient.setClientGroup(groupName);
            selectedClient.setContractId(contractId);
            selectedClient.setAddress(address);
            selectedClient.setPhone(phone);
            selectedClient.setMobile(mobile);
            selectedClient.setFax(fax);
            selectedClient.setEmail(email);
            selectedClient.setNotifyViaEmail(notifyviaemail);
            selectedClient.setNotifyViaSMS(notifyviasms);
            selectedClient.setCreatedDate(df.format(new Date(contractdate)));
            selectedClient.setLastUpdateDate(df.format(new Date(lastupdate)));
            selectedClient.setRemarks(remarks);
            selectedClient.getCard().setCardNo(cardNo);
            selectedClient.getCard().setCardType(cardType);
            selectedClient.getCard().setCreatedDate(cardCreatedDate);
            selectedClient.getCard().setExpiredDate(validdate);
            selectedClient.getCard().setStatus(cardStatus);
            selectedClient.getCard().setBalance(balance);
            selectedClient.getCard().setOverdraftLimit(overdraftLimit);
            selectedClient.getCard().setLimit(limit);
            selectedClient.getCard().setBlockReason(blockReason);
            if (passDirection.intValue() == EnterEvent.EXIT ||
                passDirection.intValue() == EnterEvent.PASSAGE_IS_FORBIDDEN ||
                passDirection.intValue() == EnterEvent.TURNSTILE_IS_BROKEN||
                passDirection.intValue() == EnterEvent.PASSAGE_RUFUSAL  ||
                passDirection.intValue() == EnterEvent.RE_EXIT ||
                passDirection.intValue() == EnterEvent.QUERY_FOR_ENTER) {
                selectedClient.setIsInSchool(false);
            } else if (passDirection.intValue() == EnterEvent.ENTRY ||
                       passDirection.intValue() == EnterEvent.RE_ENTRY ||
                       passDirection.intValue() == EnterEvent.EVENT_WITHOUT_PASSAGE ||
                       passDirection.intValue() == EnterEvent.QUERY_FOR_EXIT) {
                selectedClient.setIsInSchool(true);
            }
        }

        loadEnterEvents(session, new Date());
        loadMigrationHistory(session);
        loadClientDiscounts(session, selectedClient, categoryDiscounts);
    }

    @Transactional
    public void loadClientDiscounts() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadMigrationHistory(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public static void loadClientDiscounts(Session session,
                                           SelectedClient selectedClient,
                                           List<CategoryDiscount> categoryDiscounts) {
        selectedClient.initDiscounts(categoryDiscounts);
        if (selectedClient.getIdOfClient() == null || session == null) {
            return;
        }
        org.hibernate.Query q = session.createSQLQuery("select idofcategorydiscount "
                                                        + "from cf_clients_categorydiscounts "
                                                        + "where idofclient=:idofclient");
        q.setLong("idofclient", selectedClient.getIdOfClient());
        List resultList = q.list();
        for (Object entry : resultList) {
            selectedClient.getDiscounts().put(((BigInteger) entry).longValue(), Boolean.TRUE);
        }
    }


    @Transactional
    public void loadMigrationHistory(Date date) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadMigrationHistory(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadMigrationHistory(Session session) {
        List<SelectedClient.MigrationHistory> migrations = new ArrayList<SelectedClient.MigrationHistory>();
        /*org.hibernate.Query q = session.createSQLQuery(
                "select registrationdate, idofclientmigration " + "from cf_clientmigrationhistory "
                        + "where idofclient=:idofclient order by registrationdate desc");
        q.setLong("idofclient", selectedClient.getIdOfClient());
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long date = getDbLong(o[0]);
            String className = "test";//getDbString(o[1]);

            migrations.add(new SelectedClient.MigrationHistory(new Date(date), className));
        }*/
        selectedClient.setMigrationsHistory(migrations);
    }

    @Transactional
    public void loadEnterEvents(Date date) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            loadEnterEvents(session, date);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void loadEnterEvents(Session session, Date date) {
        List<SelectedClient.EnterEvents> ee = new ArrayList<SelectedClient.EnterEvents>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SEPTEMBER, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        org.hibernate.Query q = session.createSQLQuery(
                "select cf_enterevents.evtdatetime, cf_enterevents.passdirection " + "from cf_enterevents "
                        + "where idofclient=:idofclient and evtdatetime between :startDate and :endDate order by cf_enterevents.evtdatetime");
        q.setLong("idofclient", selectedClient.getIdOfClient());
        q.setLong("startDate", date.getTime());
        q.setLong("endDate", date.getTime() + DAY);
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long eventDate = HibernateUtils.getDbLong(o[0]);
            Integer passDirection = HibernateUtils.getDbInt(o[1]);
            String eventName = "";
            switch (passDirection) {
                case EnterEvent.ENTRY:
                    eventName = "Вход";
                    break;
                case EnterEvent.EXIT:
                    eventName = "Выход";
                    break;
                case EnterEvent.PASSAGE_IS_FORBIDDEN:
                    eventName = "Проход запрещен";
                    break;
                case EnterEvent.TURNSTILE_IS_BROKEN:
                    eventName = "Взлом турникета";
                    break;
                case EnterEvent.EVENT_WITHOUT_PASSAGE:
                    eventName = "Событие без прохода";
                    break;
                case EnterEvent.PASSAGE_RUFUSAL:
                    eventName = "Отказ от прохода";
                    break;
                case EnterEvent.RE_ENTRY:
                    eventName = "Повторный вход";
                    break;
                case EnterEvent.RE_EXIT:
                    eventName = "Повторный выход";
                    break;
                case EnterEvent.QUERY_FOR_ENTER:
                    eventName = "Запрос на вход";
                    break;
                case EnterEvent.QUERY_FOR_EXIT:
                    eventName = "Запрос на выход";
                    break;
            }
            ee.add(new SelectedClient.EnterEvents(new Date(eventDate), eventName));
        }
        selectedClient.setEnterEvents(ee);
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
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        groups = DAOServices.getInstance().loadGroups(session, org.getIdOfOrg());
        Collections.sort(groups, new ClientComparator());
        groups.add(0, "");
    }

    @Transactional
    public void loadDiscounts() throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            categoryDiscounts = loadDiscounts(session);
        } catch (Exception e) {
            logger.error("Failed to load group for org", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public static List<CategoryDiscount> loadDiscounts(Session session) {
        List<CategoryDiscount> categoryDiscounts = new ArrayList<CategoryDiscount>();
        Map<Long, String> categories = DAOServices.getInstance().loadDiscountCategories(session);
        for (Long idofcategorydiscount : categories.keySet()) {
            String name = categories.get(idofcategorydiscount);
            categoryDiscounts.add(new CategoryDiscount(idofcategorydiscount, name));
        }
        return categoryDiscounts;
    }

    @Transactional
    public void loadTeachers() {

    }

    public void loadTeachers(Session session) {

    }

    @Transactional
    public void updateClient(ClientsMobileHistory clientsMobileHistory) throws Exception {
        Session session = null;
        //Transaction transaction = null;
        try {
            session = (Session) entityManager.getDelegate();
            //transaction = session.beginTransaction();
            updateClient(session, clientsMobileHistory);
            //transaction.commit();
            //transaction = null;
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Не удалось обновить данные пользователя");
        } finally {
            //HibernateUtils.rollback(transaction, logger);
            //HibernateUtils.close(session, logger);
        }
    }

    public void updateClient(Session session, ClientsMobileHistory clientsMobileHistory) throws Exception {
        ClientRegisterPage.registerClient(session, selectedClient, org, clientsMobileHistory);
    }


    /**
     * ****************************************************************************************************************
     * Работа со списком
     * ****************************************************************************************************************
     */
    public void buildGroupsTree(List<Client> clients) {
        //  Полученные данные сохраняем в дерево
        /*if (dbTree.isEmpty()) {
            return;
        }*/
        tree = new TreeNodeImpl();
        int groupCounter = 0;
        for (String k : groups) {
            //  Добавляем группу
            TreeNodeImpl groupNode = new TreeNodeImpl();
            groupNode.setData(k.length() < 1 ? NO_GROUP : k);

            //  Заполняем группу учениками
            int clientCounter = 0;
            clients = dbTree.get(k);
            if (clients != null && clients.size() > 0) {
                for (Client cl : clients) {
                    TreeNodeImpl clientNode = new TreeNodeImpl();
                    clientNode.setData(cl);
                    groupNode.addChild(new Integer(clientCounter), clientNode);
                    clientCounter++;
                }
            } else {
                //  Если клиентов нет, то добавляем одного пустого клиента, чтобы дерево отобразило элемент как папку
                if (lookupClientName == null || lookupClientName.length() < 1) {
                    TreeNodeImpl clientNode = new TreeNodeImpl();
                    clientNode.setData(GROUP_NO_CLIENTS);
                    groupNode.addChild(new Integer(clientCounter), clientNode);
                    clientCounter++;
                }
            }

            tree.addChild(new Integer(groupCounter), groupNode);
            groupCounter++;
        }
    }

    public TreeNode getTree() {
        return tree;
    }

    public SelectedClient getSelectedClient() {
        return selectedClient;
    }


    public void groupSelected(String groupName) {
        List<Client> clients = dbTree.get(groupName);
        if ((clients == null || clients.size() < 1) &&
            groupName.matches("[0-9]{1,2}-?[а-яА-Я]")) {
            allowRemoveGroup = true;
            selectedClientGroup = groupName;
        }
    }

    @Transactional
    public void selectClient(Client selectedClient) {
        resetMessages();
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            resetSelectedClient();


            this.selectedClient.copy(selectedClient);
            //  Дозагружаем нужные данные
            loadSelectedClientData(session);
        } catch (Exception e) {
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
            logger.error("Failed to select client", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void applyChanges(ClientsMobileHistory clientsMobileHistory) {
        boolean change = isClientSelected();
        resetMessages();
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();

            //  Вносим изменения в пользователя
            updateClient(session, clientsMobileHistory);
            if (!change) {
                resetSelectedClient();
            }


            //  Обновляем дерево и клиента
            //RuntimeContext.getAppContext().getBean(ClientListEditPage.class).fill(session, false);
            sendInfo(change ? "Данные клиента изменены" : "Клиент успешно зарегистрирован");
        } catch (Exception e) {
            logger.error("Failed to apply client changes", e);
            sendError("Не удалось " + (change ? "изменить данные клиента" : "зарегистрировать клиента") + ": " + e
                    .getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }
    
    @Transactional
    public void removeClientGroup() {
        Session session = null;
        //Transaction transaction = null;
        try {
            session = (Session) entityManager.getDelegate();
            Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
            boolean success = DAOUtils.removeEmptyClientGroupByName(session, org.getIdOfOrg(), selectedClientGroup);
            if (success) {
                sendInfo("Группа " + selectedClientGroup + " удалена");
                dbTree.remove(selectedClientGroup);
                fill(session, false);
                allowRemoveGroup = false;
                selectedClientGroup = null;
            } else {
                sendError("Не удалось удалить группу " + selectedClientGroup);
            }
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Не удалось обновить данные пользователя");
        } finally {
            //HibernateUtils.rollback(transaction, logger);
            //HibernateUtils.close(session, logger);
        }

    }
    
    


    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).fill(true);
    }

    public String getSubmitButtonLabel() {
        return !isClientSelected() ? "Зарегистрировать" : "Сохранить";
    }

    public UITree getTreeComponent() {
        return treeComponent;
    }

    public void setTreeComponent(UITree treeComponent) {
        this.treeComponent = treeComponent;
    }

    public String getClientGroup() {
        return selectedClient == null ? "" : selectedClient.getClientGroup();
    }

    public void setClientGroup(String clientGroup) {
        selectedClient.setClientGroup(clientGroup);
    }

    public Date getEnterEventDate() {
        return enterEventDate;
    }

    public void setEnterEventDate(Date enterEventDate) {
        this.enterEventDate = enterEventDate;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getLookupClientName() {
        return lookupClientName;
    }

    public void setLookupClientName(String lookupClientName) {
        this.lookupClientName = lookupClientName;
    }

    public void doClientsNodeExpand(org.richfaces.event.NodeExpandedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        String group = (String) tree.getRowData();
        List<String> groups = new ArrayList<String>();
        groups.add(group);
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).loadClientsForGroup(groups);
    }

    public void doSelectClient(NodeSelectedEvent event) {
        allowRemoveGroup = false;
        selectedClientGroup = null;
        HtmlTree tree = (HtmlTree) event.getComponent();
        if (tree.isLeaf()) {
            isLeafSelected = true;
            Client selectedClient = (Client) tree.getRowData();
            RuntimeContext.getAppContext().getBean(ClientListEditPage.class).selectClient(selectedClient);
        } else {
            isLeafSelected = false;
            String selectedClient = (String) tree.getRowData();
            RuntimeContext.getAppContext().getBean(ClientListEditPage.class).groupSelected(selectedClient);
        }
    }

    public void doLookupClient() {
        //RuntimeContext.getAppContext().getBean(ClientListEditPage.class).fill(true);
    }

    public void doResetLookupClient() {
        lookupClientName = "";
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).doLookupClient();
    }

    public void doApplyChanges() {
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("изменение клиента через org-room (возможно легаси)");
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).applyChanges(clientsMobileHistory);
        List<String> groups = new ArrayList<String>();
        groups.add(selectedClient.getClientGroup());
        groups.add(selectedClient.getDefaultClientGroup());
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).loadClientsForGroup(groups);
        //RuntimeContext.getAppContext().getBean(ClientListEditPage.class).fill(false);
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).loadSelectedClientData();
    }

    public void doCancelChanges() {
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).resetSelectedClient();
    }

    public void doRemoveClientGroup() {
        if (!allowRemoveGroup || selectedClientGroup == null || selectedClientGroup.length() < 1) {
            return;
        }
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).removeClientGroup();
    }

    public void doRegisterClient() {
        isLeafSelected = true;  //  Показываем панель с настройками клиента
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).resetSelectedClient();
    }

    public void doChangeEnterEventDate(ValueChangeEvent event) {
        enterEventDate = (Date) event.getNewValue();
        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).loadEnterEvents(enterEventDate);
    }

    public void doRemoveClient () {
        selectedClient.setClientGroup(ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
    }

    public List<SelectItem> getGroups() throws Exception {
        if (groups == null) {
            loadGroups();
        }
        List<SelectItem> res = new ArrayList<SelectItem>();
        //res.add(new SelectItem("", ""));
        for (String group : groups) {
            res.add(new SelectItem(group, group));
        }
        return res;
    }

    public List<SelectItem> getTeachers() throws Exception {
        if (this.teachers == null) {
            loadTeachers();
        }
        List<SelectItem> res = new ArrayList<SelectItem>();
        List<String> teachers = new ArrayList<String>();
        for (Client client : this.teachers) {
            teachers.add(client.toString());
        }
        Collections.sort(teachers);
        for (String group : teachers) {
            res.add(new SelectItem(group, group));
        }
        return res;
    }

    public List<CategoryDiscount> getCategoryDiscounts () {
        return categoryDiscounts;
    }

    public boolean getShowClientEditPanel() {
        return isLeafSelected;
    }

    public boolean getShowClassEditPanel() {
        return !isLeafSelected;
    }

    public List<SelectItem> getDiscountModes() {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("Льгота", "Льгота"));
        return res;
    }

    public void onGroupCreateEvent(GroupCreateEvent event) {
        if (!event.isSucceed()) {
            return;
        }

        RuntimeContext.getAppContext().getBean(ClientListEditPage.class).fill(false);
    }

    public boolean getAllowRemoveGroup() {
        return allowRemoveGroup;
    }

    public void setAllowRemoveGroup(boolean allowRemoveGroup) {
        this.allowRemoveGroup = allowRemoveGroup;
    }












    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public boolean isClientSelected() {
        return selectedClient != null && selectedClient.getIdOfClient() != null;
    }

    public String getPageFilename() {
        return "client/client_list";
    }

    public String getPageTitle() {
        return "Управление клиентами";
    }

    protected void reset() {
        resetSelectedClient();
        groups = null;
        dbTree = null;
        tree = null;
    }

    protected void resetSelectedClient() {
        selectedClient.setIdOfClient(null);
        selectedClient.setIdOfPerson(null);
        selectedClient.setClientGroup(null);
        selectedClient.setAddress(null);
        selectedClient.setContractId(null);
        selectedClient.setCreatedDate(null);
        selectedClient.setEmail(null);
        selectedClient.setFax(null);
        selectedClient.setLastUpdateDate(null);
        selectedClient.setMobile(null);
        selectedClient.setNotifyViaEmail(null);
        selectedClient.setNotifyViaSMS(null);
        selectedClient.setPhone(null);
        selectedClient.setRemarks(null);
        selectedClient.setFirstName(null);
        selectedClient.setSecondName(null);
        selectedClient.setSurname(null);

        selectedClient.getCard().setBalance(null);
        selectedClient.getCard().setBlockReason(null);
        selectedClient.getCard().setCardNo(null);
        selectedClient.getCard().setCardType(null);
        selectedClient.getCard().setCreatedDate(null);
        selectedClient.getCard().setExpiredDate(null);
        selectedClient.getCard().setLimit(null);
        selectedClient.getCard().setOverdraftLimit(null);
        selectedClient.getCard().setStatus(null);

        //  Сбрасываем выбор в дереве
        if (treeComponent != null) {
            TreeState state = (TreeState) treeComponent.getComponentState();
            state.setSelected(null);
        }

        ClientListEditPage.loadClientDiscounts (null, selectedClient, categoryDiscounts);
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


    public static class Client {

        protected Long idOfClient;
        protected String firstName;
        protected String secondName;
        protected String surname;
        protected Long idOfPerson;

        public Client(long idOfClient, String firstName, String secondName, String surname, long idOfPerson) {
            this.idOfClient = idOfClient;
            this.firstName = firstName;
            this.secondName = secondName;
            this.surname = surname;
            this.idOfPerson = idOfPerson;
        }

        public long getIdOfPerson() {
            return idOfPerson;
        }

        public Long getIdOfClient() {
            //  Если это человек, установленный по ум., то ничего не отображаем
            if (idOfClient == Long.MIN_VALUE) {
                return null;
            }
            return idOfClient;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getSurname() {
            return surname;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public void setIdOfPerson(Long idOfPerson) {
            this.idOfPerson = idOfPerson;
        }

        public void setIdOfClient(Long idOfClient) {
            if (idOfClient == null) {
                idOfClient = Long.MIN_VALUE;
            }
            this.idOfClient = idOfClient;
        }

        public String toString() {
            return surname + " " + firstName + " " + secondName;
        }
    }

    public static class SelectedClient extends Client {

        private String clientGroup;
        private Long contractId;
        private String address;
        private String phone;
        private String mobile;
        private String fax;
        private String email;
        private Boolean notifyViaEmail;
        private Boolean notifyViaSMS;
        private String createdDate;
        private String lastUpdateDate;
        private ClientCard card;
        private String remarks;
        private boolean isInSchool;
        private List<EnterEvents> enterEvents;
        private List<MigrationHistory> migrations;
        private String discountMode;
        private Map<Long, Boolean> discounts;
        private String defaultClientGroup;


        public SelectedClient() {
            super(Long.MIN_VALUE, "", "", "", Long.MIN_VALUE);
        }

        public SelectedClient(Client client) {
            super(client.getIdOfClient(), client.getFirstName(), client.getSecondName(), client.getSurname(),
                    client.getIdOfPerson());
        }

        public SelectedClient(long idOfClient, String firstName, String secontName, String surname, long idOfPerson) {
            super(idOfClient, firstName, secontName, surname, idOfPerson);
        }

        public void copy(Client client) {
            idOfClient = client.getIdOfClient();
            firstName = client.getFirstName();
            secondName = client.getSecondName();
            surname = client.getSurname();
            idOfPerson = client.getIdOfPerson();
        }

        public String getDefaultClientGroup() {
            return defaultClientGroup;
        }

        public void setDefaultClientGroup(String defaultClientGroup) {
            this.defaultClientGroup = defaultClientGroup;
        }

        public String getClientGroup() {
            return clientGroup;
        }

        public void setClientGroup(String clientGroup) {
            this.clientGroup = clientGroup;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getNotifyViaEmail() {
            return notifyViaEmail;
        }

        public void setNotifyViaEmail(Boolean notifyViaEmail) {
            this.notifyViaEmail = notifyViaEmail;
        }

        public Boolean getNotifyViaSMS() {
            return notifyViaSMS;
        }

        public void setNotifyViaSMS(Boolean notifyViaSMS) {
            this.notifyViaSMS = notifyViaSMS;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getLastUpdateDate() {
            return lastUpdateDate;
        }

        public void setLastUpdateDate(String lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
        }

        public ClientCard getCard() {
            if (card == null) {
                card = new ClientCard();
            }
            return card;
        }

        public void setCard(ClientCard card) {
            this.card = card;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getDiscountCategories() {
            return "Льготные категории не определены";
        }

        public String getDiscountRules() {
            return "Правила не определены";
        }

        public boolean getIsInSchool() {
            return isInSchool;
        }

        public void setIsInSchool (boolean isInSchool) {
            this.isInSchool = isInSchool;
        }

        public void setEnterEvents(List<EnterEvents> enterEvents) {
            this.enterEvents = enterEvents;
        }

        public List<EnterEvents> getEnterEvents() {
            return enterEvents;
        }

        public List<MigrationHistory> getMigrationsHistory() {
            return migrations;
        }

        public void setMigrationsHistory(List<MigrationHistory> migrations) {
            this.migrations = migrations;
        }

        public String getDiscountMode() {
            return discountMode;
        }

        public void setDiscountMode(String discountMode) {
            this.discountMode = discountMode;
        }

        public String getClientGroupDiscount () {
            try {
                int i = Integer.parseInt(clientGroup.replaceAll("[^0-9]", ""));
                return i + " класс";
            } catch (Exception e) {
                return "";
            }
        }

        public String getClientSuperGroupDiscount () {
            try {
                int i = Integer.parseInt(clientGroup.replaceAll("[^0-9]", ""));
                if (i < 5) {
                    return "Младшие классы";
                } else if (i > 3 && i < 10) {
                    return "Средние классы";
                } else if (i > 9) {
                    return "Старшие классы";
                }
                return "";
            } catch (Exception e) {
                return "";
            }
        }

        public Map<Long, Boolean> getDiscounts () {
            return discounts;
        }

        public SelectedClient initDiscounts (List<CategoryDiscount> baseDiscounts) {
            if (discounts != null) {
                discounts.clear();
            } else {
                discounts = new HashMap<Long, Boolean>();
            }

            for (CategoryDiscount discount : baseDiscounts) {
                discounts.put(discount.idofcategorydiscount, Boolean.FALSE);
            }
            return this;
        }






        public static class EnterEvents {

            private Date date;
            private String location;

            public EnterEvents(Date date, String location) {
                this.date = date;
                this.location = location;
            }

            public Date getDate() {
                return date;
            }

            public String getLocation() {
                return location;
            }
        }


        public static class MigrationHistory {

            private Date date;
            private String destination;

            public MigrationHistory(Date date, String destination) {
                this.date = date;
                this.destination = destination;
            }

            public Date getDate() {
                return date;
            }

            public String getDestination() {
                return destination;
            }
        }


        public class ClientCard {

            private Long cardNo;
            private Integer cardType;
            private Long createdDate;
            private Long expiredDate;
            private Integer status;
            private Long balance;
            private Long overdraftLimit;
            private Long limit;
            private String blockReason;

            public ClientCard() {
            }

            public ClientCard(long cardNo, int cardType, long createdDate, long expiredDate, int status, long balance,
                    long overdraftLimit, long limit, String blockReason) {
                this.cardNo = cardNo;
                this.cardType = cardType;
                this.createdDate = createdDate;
                this.expiredDate = expiredDate;
                this.status = status;
                this.balance = balance;
                this.overdraftLimit = overdraftLimit;
                this.limit = limit;
                this.blockReason = blockReason;
            }

            public Long getCardNo() {
                return cardNo == null ? null : cardNo;
            }

            public String getCardType() {
                return cardType == null ? null : Constants.CARD_TYPE_NAMES[cardType];
            }

            public String getCreatedDate() {
                return createdDate == null ? null : df.format(new Date(createdDate));
            }

            public String getExpiredDate() {
                return expiredDate == null ? null : df.format(new Date(expiredDate));
            }

            public String getStatus() {
                return status == null ? null : Constants.CARD_STATE_NAMES[status];
            }

            public String getBalance() {
                return balance == null ? null : "" + balance;
            }

            public String getOverdraftLimit() {
                return overdraftLimit == null ? null : "" + overdraftLimit;
            }

            public String getLimit() {
                return limit == null ? null : "" + limit;
            }

            public String getBlockReason() {
                return blockReason == null ? null : blockReason;
            }

            public void setCardNo(Long cardNo) {
                this.cardNo = cardNo;
            }

            public void setCardType(Integer cardType) {
                this.cardType = cardType;
            }

            public void setCreatedDate(Long createdDate) {
                this.createdDate = createdDate;
            }

            public void setExpiredDate(Long expiredDate) {
                this.expiredDate = expiredDate;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public void setBalance(Long balance) {
                this.balance = balance;
            }

            public void setOverdraftLimit(Long overdraftLimit) {
                this.overdraftLimit = overdraftLimit;
            }

            public void setLimit(Long limit) {
                this.limit = limit;
            }

            public void setBlockReason(String blockReason) {
                this.blockReason = blockReason;
            }
        }
    }


    public static class ClientComparator<String> implements Comparator<String> {
        public int compare(Object o1, Object o2) {
            java.lang.String s1 = (java.lang.String) o1;
            java.lang.String s2 = (java.lang.String) o2;


            try {
                Pattern p = Pattern.compile("([0-9]+)");
                Matcher m1 = p.matcher(s1);
                Matcher m2 = p.matcher(s2);
                boolean m1Found = m1.find();
                boolean m2Found = m2.find();

                if (m1Found && m2Found) {
                    java.lang.String class1 = m1.group();
                    java.lang.String class2 = m2.group();

                    Integer c1 = new Integer(class1);
                    Integer c2 = new Integer(class2);

                    return c1.compareTo(c2);
                } else if (m1Found) {
                    return -1;
                } else if (m2Found) {
                    return 1;
                } else {
                    return s1.compareTo(s2);
                }
            } catch (Exception e) {
                return s1.compareTo(s2);
            }
        }
    }


    public static class CategoryDiscount {
        private long idofcategorydiscount;
        private String name;

        public CategoryDiscount (long idofcategorydiscount, String name) {
            this.idofcategorydiscount = idofcategorydiscount;
            this.name = name;
        }

        public long getIdofcategorydiscount() {
            return idofcategorydiscount;
        }

        public String getName() {
            return name;
        }
    }
}