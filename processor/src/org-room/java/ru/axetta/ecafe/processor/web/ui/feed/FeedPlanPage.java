/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.feed;

//import generated.payments.processing.POSPaymentController;
//import generated.payments.processing.POSPaymentControllerWSService;
//import generated.payments.processing.PosPayment;

import generated.pos.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestRepository;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.OrgRoomMainPage;
import ru.axetta.ecafe.processor.web.ui.auth.LoginBean;
import ru.axetta.ecafe.processor.web.ui.modal.YesNoEvent;
import ru.axetta.ecafe.processor.web.ui.modal.YesNoListener;
import ru.axetta.ecafe.processor.web.ui.modal.feed_plan.*;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.net.URL;
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
public class FeedPlanPage extends BasicWorkspacePage implements /*ClientFeedActionListener, */DisableComplexListener, ReplaceClientListener,
        YesNoListener {
    public static final int PAY_CLIENT     = 1;
    public static final int BLOCK_CLIENT   = 2;
    public static final int INSIDE_CLIENT  = 1;
    public static final int OUTSIDE_CLIENT = 2;
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
    public static final String DISCOUNT_START = "Платное питание";
    public static final String DISCOUNT_END = "%]";
    private static final Logger logger = LoggerFactory.getLogger(FeedPlanPage.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private GoodRequestRepository goodRequestRepository;
    private Org org;
    private String errorMessages;
    private String infoMessages;
    private List<Client> clients;
    private List<ReplaceClient> replaceClients;
    private List<Complex> complexes;
    private Calendar planDate;
    private Long selectedIdOfClientGroup;
    private Client selectedClient;
    private List<Complex> orderedComplexes;
    private Map<Integer, Boolean> disabledComplexes;
    private boolean clearPlan;
    private boolean orderRegistrationResult;
    private boolean displayDiscountClients = true;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        //  Сбрасываем все значения, необходимые для GUI
        resetMessages();
        if (planDate == null) {
            planDate = new GregorianCalendar();
            planDate.setTimeInMillis(System.currentTimeMillis());
        }
        if (selectedIdOfClientGroup == null) {
            selectedIdOfClientGroup = ALL_TYPE;
        }
        clearDate(planDate);
        selectedClient = null;

        //  Загружаем клиентов и комплексы одним запросом
        loadClaims(session);
        loadReplaceClients(session);
        loadClientsAndComplexes(session);
    }

    public void loadClaims (Session session) {
        if (orderedComplexes == null) {
            orderedComplexes = new ArrayList<Complex>();
        }
        orderedComplexes.clear();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        org.hibernate.Query q = session.createSQLQuery(
                "select cf_complexinfo.idofcomplex, count(cf_complexinfo.idofcomplex), cf_goods_requests_positions.totalcount / 1000 "
                        + "from cf_goods_requests "
                        + "left join cf_goods_requests_positions on cf_goods_requests.idofgoodsrequest=cf_goods_requests_positions.idofgoodsrequest "
                        + "left join cf_complexinfo on cf_complexinfo.idofcomplex=cf_goods_requests_positions.idofgood and "
                        + "                            cf_complexinfo.idoforg=cf_goods_requests.orgowner and "
                        + "                            cf_complexinfo.menudate between :startMenudate and :endMenudate "
                        + "where donedate between :startMenudate and :endMenudate and cf_goods_requests.orgowner=:idoforg "
                        + "group by idofcomplex, cf_goods_requests_positions.totalcount "
                        + "order by idofcomplex");
        q.setLong("idoforg", org.getIdOfOrg());
        q.setLong("startMenudate", planDate.getTimeInMillis());
        q.setLong("endMenudate", planDate.getTimeInMillis() + MILLIS_IN_DAY);
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Integer idofcomplex = HibernateUtils.getDbInt(o[0]);
            Long complesCount = HibernateUtils.getDbLong(o[1]);
            Long goodsCount = HibernateUtils.getDbLong(o[2]);
            complesCount = complesCount == null ? 0L : complesCount;
            goodsCount = goodsCount == null ? 0L : goodsCount;

            if (idofcomplex == null) {
                continue;
            }
            OrderedComplex c = new OrderedComplex(idofcomplex, ORDER_TYPE, ORDER_TYPE_NAME);
            c.setCount(complesCount.intValue() * goodsCount.intValue());
            orderedComplexes.add(c);
        }
    }

    public void loadReplaceClients(Session session) {
        replaceClients = new ArrayList<ReplaceClient>();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String sql = "";
        org.hibernate.Query q = session.createSQLQuery(
                "select cf_clients.idofclient, cf_persons.firstname, cf_persons.secondname, cf_persons.surname "
                        + "from cf_clients "
                        + "left join cf_clients_categorydiscounts on cf_clients.idofclient=cf_clients_categorydiscounts.idofclient "
                        + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                        + "where cf_clients_categorydiscounts.idofcategorydiscount=50 and cf_clients.idoforg=:idoforg");
        q.setLong("idoforg", org.getIdOfOrg());
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idofclient = HibernateUtils.getDbLong(o[0]);
            String firstname = HibernateUtils.getDbString(o[1]);
            String secondname = HibernateUtils.getDbString(o[2]);
            String surname = HibernateUtils.getDbString(o[3]);
            replaceClients.add(new ReplaceClient(idofclient, firstname, secondname, surname));
        }
    }

    public void loadClientsAndComplexes(Session session) throws Exception {
        clients = new ArrayList<Client>();
        complexes = new ArrayList<Complex>();
        List<Complex> allComplexes = new ArrayList<Complex>();
        List<Complex> superComlexGroups = new ArrayList<Complex>();
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String feedTypeRestrict = " and position('" + DISCOUNT_START + "' in cf_discountrules.description)";
        if(displayDiscountClients) {
            feedTypeRestrict += "=0 ";
        } else {
            feedTypeRestrict += ">0 ";
        }


        String sql = "select cf_clientgroups.idofclientgroup, cf_clientgroups.groupname, cf_clients.idofclient, cf_persons.firstname, "
                + "       cf_persons.secondname, cf_persons.surname, cf_clientscomplexdiscounts.idofrule, description, "
                + "       cf_clientscomplexdiscounts.idofcomplex, cf_discountrules.priority, CAST(substring(groupname FROM '[0-9]+') AS INTEGER) as groupNum, "
                + "       cf_complexinfo.currentprice, cf_temporary_orders.action, cf_temporary_orders.IdOfOrder, cf_temporary_orders.idofreplaceclient, "
                + "       cf_temporary_orders.inBuilding, cf_clients.balance "
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
                + "where cf_clients.idoforg=:idoforg and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 "
                +        feedTypeRestrict
                //+ " and cf_clients.idofclient=129962 "
                + "order by groupNum, groupname, cf_persons.firstname, cf_persons.secondname, cf_persons.surname, cf_clients.idofclient, idofcomplex";
        org.hibernate.Query q = session.createSQLQuery(sql);
        q.setLong("idoforg", org.getIdOfOrg());
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
            Long idofreplaceclient = HibernateUtils.getDbLong(o[14]);
            Integer inBuilding = HibernateUtils.getDbInt(o[15]);
            Long balance = HibernateUtils.getDbLong(o[16]);
            if (price == null) {
                price = 0L;
            }
            double discountRate = 100D;
            if(!displayDiscountClients) {
                if(ruleDescription.indexOf(DISCOUNT_START) == 0) {
                    String discount = ruleDescription.substring(
                            ruleDescription.indexOf(DISCOUNT_START) + DISCOUNT_START.length(),
                            ruleDescription.indexOf(DISCOUNT_END));
                    discount = discount.replaceAll("\\[", "");
                    discount = discount.replaceAll("\\]", "");
                    discountRate = Integer.parseInt(discount.trim());
                    //ruleDescription = "";
                } else {
                    discountRate = 100;
                }
            }


            //  Добавляем клиента
            Client cl = new Client(idofclientgroup, idofclient, firstName, secondname, surname,
                    idofrule, ruleDescription, idofcomplex, priority, price, action, inBuilding);
            cl.setGroupNum(groupNum);
            cl.setDiscountRate(discountRate);
            cl.setBalance(((double) balance) / 100);
            if (action != null) {
                cl.setTemporarySaved(true);
            }
            cl.setIdoforder(idoforder);
            //  Проверяем замененного клиента, если установлен, то загруженному клиенту, осуществляемому
            //  замены, необходимо поставить id текущего клиента
            cl.setIdofReplaceClient(idofreplaceclient);
            if (idofreplaceclient != null) {
                for (ReplaceClient replaceCl : replaceClients) {
                    if (replaceCl.getIdofclient() == idofreplaceclient.longValue()) {
                        replaceCl.setIdOfTargetClient(cl.getIdofclient());
                        replaceCl.setNameOfTargetClient(cl.getFullNameWithoutBreaks());
                        break;
                    }
                }
            }
            clients.add(cl);


            //  Обновляем комплексы
            boolean complexFound = false;
            long idofsuperclientgroup = getIdOfSuperClientGroup(groupNum);
            String superclientgroupname = getNameOfSuperClientGroup(groupNum);
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


        //  Перед добавлением строки с Заказом, необходимо проверить ниличие в нем всех комплексов
        for (Integer idofcomplex : getComplexes()) {
            if (!orderedComplexes.contains(idofcomplex)) {
                OrderedComplex c = new OrderedComplex(idofcomplex, ORDER_TYPE, ORDER_TYPE_NAME);
                c.setCount(0);
                orderedComplexes.add(c);
            }
        }
        //  Вставляем заказанные комплексы из ранее загруженного массива, а так же составленные все комплексы
        complexes.addAll(orderedComplexes);
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

    private static final Long getIdOfSuperClientGroup (Integer groupNum) {
        if (groupNum < 5) {
            return ELEMENTARY_CLASSES_TYPE;
        } else if (groupNum < 10) {
            return MIDDLE_CLASSES_TYPE;
        } else {
            return HIGH_CLASSES_TYPE;
        }
    }

    private static final String getNameOfSuperClientGroup (Integer groupNum) {
        if (groupNum < 4) {
            return ELEMENTARY_CLASSES_TYPE_NAME;
        } else if (groupNum < 10) {
            return MIDDLE_CLASSES_TYPE_NAME;
        } else {
            return HIGH_CLASSES_TYPE_NAME;
        }
    }

    @Transactional
    public void saveClient (List<Client> clients) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            saveClient(session, clients);
        } catch (Exception e) {
            logger.error("Failed to save client's into temporary table", e);
            //sendError("Не удалось изменить статус питания клиента " + client.getFullName() + ": " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void saveClient(Session session, List<Client> clients) {
        for (Client client : clients) {
            saveClient(session, client);
            }
        }

    public void saveClient(Session session, Client client) {
        //  Если уже имеется заказ, значит сохранение во временную таблицу запрещено
        if (client.getSaved()) {
            return;
        }

        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        String sql = "";
        //  Проверяем, сохранен ли клиент во временную таблицу в БД
        if (client.getTemporarySaved()) {
            sql = "update cf_temporary_orders set action=:action, "
                  + "idofreplaceclient=" + (client.getIdofReplaceClient() == null ? "null" : ":idofreplaceclient") + ", "
                  + "modificationdate=:date, idofuser=:idofuser, inBuilding=:inBuilding "
                  + "where idofclient=:idofclient and idofcomplex=:idofcomplex and plandate=:plandate and idofrule=:idofrule";
        } else {
            sql = "insert into cf_temporary_orders (idoforg, idofclient, idofcomplex, plandate, action, creationdate, idofuser, idofreplaceclient, inBuilding, idofrule) "
                + "values (:idoforg, :idofclient, :idofcomplex, :plandate, :action, :date, :idofuser, "
                + (client.getIdofReplaceClient() == null ? "null" : ":idofreplaceclient") + ", :inBuilding, :idofrule)";
        }

        clearDate(planDate);
        long currentTS = System.currentTimeMillis();
        org.hibernate.Query query = session.createSQLQuery(sql);
        query.setLong("idofclient", client.getIdofclient());
        query.setInteger("idofcomplex", client.getComplex());
        query.setLong("plandate", planDate.getTimeInMillis());
        query.setInteger("action", client.getActionType());
        query.setLong("date", currentTS);
        query.setLong("idofrule", client.getIdofrule());
        query.setLong("idofuser", RuntimeContext.getAppContext().getBean(LoginBean.class).getUser().getIdOfClient());
        query.setInteger("inBuilding", client.getInBuilding());
        if (!client.getTemporarySaved()) {
            query.setLong("idoforg", org.getIdOfOrg());
        }
        if (client.getIdofReplaceClient() != null) {
            query.setLong("idofreplaceclient", client.getIdofReplaceClient());
        }
        query.executeUpdate();
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
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);
        POSPaymentController service = createController(logger);
        if (service == null) {
            for (Client client : clients) {
                if (client.getActionType() != PAY_CLIENT || client.getSaved()) {
                    continue;
                }
                result.put(client, "Не удалось осуществить оплату: Не удалось подключиться к веб-службе");
            }
            return result;
        }
        List<PosPayment> payments = new ArrayList<PosPayment>();
        for (Client client : clients) {
            if (client.getActionType() != PAY_CLIENT || client.getSaved()) {
                continue;
            }
            //  Вызов веб-службы и добавление заказа
            client.getIdofrule();


            ru.axetta.ecafe.processor.core.persistence.Client dbClient = DAOReadonlyService.getInstance().findClientById(client.getIdofclient());
            long fullPrice = DAOReadonlyService.getInstance().getComplexPrice(org.getIdOfOrg(), client.getComplex());
            long discountPrice = 0L;
            long rsumPrice = 0L;
            if(client.getDiscountRate() != 100) {
                rsumPrice = client.getDiscountRate() == 0 ? fullPrice : (long)
                          (fullPrice - (fullPrice * client.getDiscountRate() / 100));
                discountPrice = fullPrice - rsumPrice;
            } else {
                rsumPrice = 0L;
                discountPrice = fullPrice;
            }
            /*if(client.getBalance() * 100 < rsumPrice) {
                result.put(client, "Не удалось осуществить оплату: у клиента недостаточно средств на счете");
                continue;
            }*/
            OrderPurchaseItem opi = getOrderPurchaseItem(client, org.getIdOfOrg(), session);
            if (opi == null) {
                result.put(client, "Не удалось осуществить оплату: отсутствует информация о стоимости комплекса");
                continue;
            }
            XMLGregorianCalendar paymentDate = getPaymentDate();
            PosPayment payment = new PosPayment();
            payment.setIdOfClient(client.getIdofclient());
            if (dbClient.getCards() != null && dbClient.getCards().size() > 0) {
                payment.setCardNo(dbClient.getCards().iterator().next().getCardNo());
            } else {
                payment.setCardNo(0L);
            }
            payment.setIdOfOrder(DAOService.getInstance().getNextIdOfOrder(org));
            //payment.setIdOfPOS(0L);
            payment.setConfirmerId(0L);
            payment.setIdOfCashier(RuntimeContext.getAppContext().getBean(LoginBean.class).getUser().getIdOfClient());
            payment.setTime(paymentDate);
            payment.setOrderDate(paymentDate);
            payment.setTrdDiscount(0L);
            payment.setOrderType(client.getDiscountRate() != 100 ? 3 : 4);
            payment.setRSum(rsumPrice);
            payment.setSumByCard(0L);
            payment.setSumByCash(0L);
            payment.setSocDiscount(discountPrice);
            payment.setGrant(0L);
            payment.setComments("- Оплачено из ТК -");
            PosPurchase purchase = new PosPurchase();
            purchase.setIdOfOrderDetail(DAOService.getInstance().getNextIdOfOrderDetail(org));
            purchase.setQty(1L);
            purchase.setRPrice(rsumPrice);
            purchase.setDiscount(discountPrice);
            purchase.setSocDiscount(discountPrice);
            purchase.setName(opi.getName());
            purchase.setMenuGroup(opi.getMenuGroup());
            purchase.setMenuOrigin(opi.getMenuOrigin());
            purchase.setRootMenu(opi.getRootMenu());
            purchase.setType(opi.getType());
            purchase.setIdOfRule(opi.getIdOfRule());
            purchase.setGuidOfGoods(opi.getGoodGuid());
            purchase.setItemCode("");
            purchase.setMenuOutput("");
            payment.getPurchases().add(purchase);
            payments.clear();
            payments.add(payment);


            PosResPaymentRegistry res = service.createOrder(org.getIdOfOrg(), payments);
            if (res.getResultCode() == 100L) {
                result.put(client, "Не удалось осуществить оплату: Произошла внутренняя ошибка");
                continue;
            }
            PosResPaymentRegistryItemList resList = res.getProhibitionsList();
            if (resList == null || resList.getI().size() < 1) {
                try {
                    org.hibernate.Query query = session.createSQLQuery(
                            "update cf_temporary_orders set idoforder=:idoforder, modificationdate=:date "
                            + "where idofclient=:idofclient and idofcomplex=:idofcomplex and plandate=:plandate");
                    query.setLong("idofclient", client.getIdofclient());
                    query.setInteger("idofcomplex", client.getComplex());
                    query.setLong("plandate", planDate.getTimeInMillis());
                    query.setLong("date", System.currentTimeMillis());
                    query.setLong("idoforder", payment.getIdOfOrder());
                    query.executeUpdate();
                    client.setIdoforder(payment.getIdOfOrder());
                    result.put(client, "Заказ успешно составлен");
                } catch (Exception e) {
                    logger.error("Failed to update order in database", e);
                    result.put(client, "Не удалось осуществить оплату: Произошла внутренняя ошибка");
                }
            } else {
                StringBuilder str = new StringBuilder();
                for (PosResPaymentRegistryItem item : resList.getI()) {
                    if (item.getError() != null && item.getError().length() > 0) {
                        if (str.length() > 0) {
                            str.append("; ");
                        }
                        str.append(item.getError());
                    }
                }
                result.put(client, "Не удалось осуществить оплату: " + str);
            }
        }

        return result;
    }
    
    protected OrderPurchaseItem getOrderPurchaseItem(Client client, long idOfOrg, Session session) {
        int complex = client.getComplex();
        org.hibernate.Query query = session.createSQLQuery(
                "select distinct(cf_goods.idofgood), cf_goods.nameofgood, cf_goods_groups.nameofgoodsgroup, cf_goods.guid, menuorigin "
                + "from cf_complexinfo "
                + "join cf_goods on cf_complexinfo.idofgood=cf_goods.idofgood "
                + "join cf_goods_groups on cf_goods.idofgoodsgroup=cf_goods_groups.idofgoodsgroup "
                + "left join cf_complexinfodetail on cf_complexinfo.idofcomplexinfo=cf_complexinfodetail.idofcomplexinfo "
                + "join cf_menudetails on cf_complexinfodetail.idofmenudetail=cf_menudetails.idofmenudetail "
                + "where idoforg=" + idOfOrg + " and idofcomplex=" + complex);
        List data = query.list();
        for (Object entry : data) {
            Object o[] = (Object[]) entry;
            Long idofgood = HibernateUtils.getDbLong(o[0]);
            String good = HibernateUtils.getDbString(o[1]);
            String groupName = HibernateUtils.getDbString(o[2]);
            String guid = HibernateUtils.getDbString(o[3]);
            int menuorigin = HibernateUtils.getDbInt(o[4]);
            return new OrderPurchaseItem(good, groupName, "", client.getIdofrule(),
                                         guid, complex + OrderDetail.TYPE_COMPLEX_MIN, menuorigin);
        }
        return null;
    }

    protected XMLGregorianCalendar getPaymentDate() {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(calendar);
            return xmlCalendar;
        } catch (Exception e) {
            return null;
        }
    }

    public static POSPaymentController createController(Logger logger) {
        POSPaymentController controller = null;
        try {
            POSPaymentControllerWSService service = new POSPaymentControllerWSService(new URL("http://localhost:8080/processor/soap/pos?wsdl"),
                    new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "POSPaymentControllerWSService"));
            controller = service.getPOSPaymentControllerWSPort();

            org.apache.cxf.endpoint.Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(10 * 60 * 1000);
            policy.setConnectionTimeout(10 * 60 * 1000);
            return controller;
        } catch (Exception e) {
            logger.error("Failed to intialize FrontControllerService", e);
            return null;
        }
    }

    public void clear() {
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).clear(Collections.EMPTY_LIST);
    }

    @Transactional
    public void clear (List<Integer> complexes) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            clear(session, complexes);
        } catch (Exception e) {
            logger.error("Failed to save orders", e);
            //sendError("Не создать заказ для " + client.getFullName() + ": " + e.getMessage());
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void clear(Session session, List<Integer> complexes) {
        String complexRestrict = "";
        for (Integer complexId : complexes) {
            if (complexRestrict.length() > 0) {
                complexRestrict = complexRestrict + " or ";
            }
            complexRestrict = complexRestrict + "cf_temporary_orders.idofcomplex=" + complexId;
        }
        if (complexRestrict.length() > 0) {
            complexRestrict = " and (" + complexRestrict + ") ";
        }
        Org org = RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg(session);  //  Получаем Org от авторизованного клиента
        //  Удаляем все заказы, имеющиеся за выбранную дату
        org.hibernate.Query query = session.createSQLQuery(
                "delete from cf_temporary_orders where idoforg=:idoforg and plandate=:plandate " + complexRestrict);
        query.setLong("idoforg", org.getIdOfOrg());
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
        orderRegistrationResult = true;
        clearPlan = false;
    }
    
    public void onYesNoEvent (YesNoEvent event) {
        if (!event.isYes()) {
            return;
        }

        if (orderRegistrationResult) {
            resetMessages();
            //  Созраняем заказы
            Map <Client, String> result = RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveOrders();
            //  Передаем полученный массив в модальное окно,
            //  чтобы отобразить ошибки или успехи сохранения заказов
            OrderRegistrationResultPanel panel = RuntimeContext.getAppContext().getBean(OrderRegistrationResultPanel.class);
            panel.setClientSaveMessages(result);
            OrgRoomMainPage.getSessionInstance().doShowOrderRegistrationResultPanel(this);
        } else if (clearPlan) {
            resetMessages();
            if (hasSavedData()) {
                sendError("Присутствуют уже оплаченные заказы, план очистить невозможно");
                return;
            }

            RuntimeContext.getAppContext().getBean(FeedPlanPage.class).clear();
            RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
        }
    }

    public void doClearPlan () {
        clearPlan = true;
        orderRegistrationResult = false;
    }
    
    /*public void doShowClientFeedActionPanel(Client cl) {
        resetMessages();
        selectedClient = cl;
        MainPage.getSessionInstance().doShowClientFeedActionPanel();
    }*/

    public void doChangeClientInBuilding(Client cl) {
        cl.setInBuilding(cl.getInBuilding() == INSIDE_CLIENT ? OUTSIDE_CLIENT : INSIDE_CLIENT);
        if (cl.getInBuilding() == OUTSIDE_CLIENT) {
            cl.setActionType(BLOCK_CLIENT);
    }
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(cl);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClient(clients);
    }
    
    public void doChangeClientAction(Client cl) {
        cl.setActionType(cl.getActionType() == PAY_CLIENT ? BLOCK_CLIENT : PAY_CLIENT);
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(cl);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClient(clients);
    }

    public void doChangeAllClientsPayAction() {
        doChangeAllClientsAction (PAY_CLIENT);
    }

    public void doChangeAllClientsBlockAction() {
        doChangeAllClientsAction (BLOCK_CLIENT);
    }

    private void doChangeAllClientsAction(int action) {
        List<Client> clients = getClients();
        for (Client cl : clients) {
            if (cl.getSaved()){
                continue;
            }
            cl.setActionType(action);
        }
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClient(clients);
    }
    
    public void doShowReplaceClientPanel(Client cl) {
        resetMessages();
        ReplaceClientPanel panel = RuntimeContext.getAppContext().getBean(ReplaceClientPanel.class);
        panel.setClients(replaceClients, cl);
        OrgRoomMainPage.getSessionInstance().doShowReplaceClientPanel();
    }

    public void doShowDisableComplexPanel() {
        resetMessages();
        DisableComplexPanel panel = RuntimeContext.getAppContext().getBean(DisableComplexPanel.class);
        panel.setComplexes(disabledComplexes);
        OrgRoomMainPage.getSessionInstance().doShowDisableComplexPanel();
    }

    public void doDecreaseDay() {
        planDate.setTimeInMillis(planDate.getTimeInMillis() - MILLIS_IN_DAY);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doIncreaseDay() {
        planDate.setTimeInMillis(planDate.getTimeInMillis() + MILLIS_IN_DAY);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doSwitchToDiscountClients() {
        displayDiscountClients = true;
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    public void doSwitchToPayPlan() {
        displayDiscountClients = false;
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }

    /*public void onClientFeedActionEvent (ClientFeedActionEvent event) {
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
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClient(clients, actionType);
    }*/

    public void onDisableComplexEvent (DisableComplexEvent event) {
        //disabledComplexes = event.getComplexes();
        //  Удаляем данные выбранных комплексов
        for (Client cl : clients) {
            if (cl.getIdoforder() != null) {
                sendError("Присутствуют уже оплаченные заказы, план очистить невозможно");
                return;
            }
        }

        Map<Integer, Boolean> res = event.getComplexes();
        List<Integer> complexesToDelete = Collections.EMPTY_LIST;
        if (res.size() > 0) {
            complexesToDelete = new ArrayList<Integer>();
            for (Integer complexId : res.keySet()) {
                if (res.get(complexId).booleanValue() == true) {
                    complexesToDelete.add(complexId);
                }
            }
        }
        if (hasSavedData()) {
            RuntimeContext.getAppContext().getBean(FeedPlanPage.class).clear(complexesToDelete);
            RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
        } else {
            for (Client cl : clients) {
                for (int c : complexesToDelete) {
                    if (cl.getComplex() == c) {
                        cl.setActionType(BLOCK_CLIENT);
                    }
                }
            }
        }
    }

    private boolean hasSavedData() {
        for (Client cl : clients) {
            if (cl.getIdoforder() != null) {
                return true;
            }
        }
        return false;
    }

    public void onReplaceClientEvent(ReplaceClientEvent event) {
        Client target = event.getClient();
        ReplaceClient replaceClient = event.getReplaceClient();
        if (replaceClient != null) {
            target.setIdofReplaceClient(replaceClient.getIdofclient());
            replaceClient.setIdOfTargetClient(target.getIdofclient());
            replaceClient.setNameOfTargetClient(target.getFullNameWithoutBreaks());
        } else {
            target.setIdofReplaceClient(null);
        }
        List<Client> updateClients = new ArrayList<Client>();
        updateClients.add(target);
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).saveClient(updateClients);
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
            if (selectedIdOfClientGroup >= 0) {
                if (cl.getIdofclientgroup() != selectedIdOfClientGroup) {
                    continue;
                }
            } else if (selectedIdOfClientGroup != ALL_TYPE) {
                if (getIdOfSuperClientGroup(cl.getGroupNum()).longValue() != selectedIdOfClientGroup.longValue()) {
                    continue;
                }
            }
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
            else if (cl.getActionType() == PAY_CLIENT) {
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
    
    public String getReplaceClient(Client client) {
        if (client.getIdofReplaceClient() == null) {
            if (client.getSaved()) {
                return "[Без замены]";
            } else {
                return "[Выбрать]";
            }
        }
        for (ReplaceClient replaceCl : replaceClients) {
            if (replaceCl.getIdofclient() == client.getIdofReplaceClient().longValue()) {
                return replaceCl.getFullName();
            }
        }
        return "[Выбрать]";
    }

    public List<Client> getClients() {
        //  Иначе, производим поиск по комплексам и  загружаем их
        List<Client> foundClients = new ArrayList<Client>();
        for (Complex c : complexes) {
            //  Производим поиск по отключенным комплексам
            /*if (disabledComplexes.get(c.getComplex())) {
                continue;
            }*/

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

    public boolean isOrderedComplex (long idoclientgroup) {
        if (idoclientgroup == ORDER_TYPE) {
            return true;
        } else {
            return false;
        }
    }

    public List<Integer> getComplexes() {
        List<Integer> res = new ArrayList<Integer>();
        for (Complex c : complexes) {
            if (res.contains(c.getComplex())) {
                continue;
            }
            //  Если комплекс отключен, то не отображаем его
            /*if (disabledComplexes.get(c.getComplex())) {
                continue;
            }*/
            res.add(c.getComplex());
        }
        return res;
    }
    
    public int getPayedComplexCount(long idoclientgroup, int complex) {
        if (isOrderedComplex(idoclientgroup)) {
            return 0;
        }
        for (Complex c : complexes) {
            if (c.getIdofclientgroup() == idoclientgroup && c.getComplex() == complex) {
                //  Найдя нужный комплекс, приступаем к подсчету оплаченных комплексов
                int count = 0;
                for (Client cl : c.getClients()) {
                    if (cl.getActionType() == PAY_CLIENT) {
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

    public String getReplaceClientById (long idofclient) {
        //replaceClients.get
        return "Замена будет здесь";
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
        protected long idofclientgroup;
        protected String clientgroupname;
        protected int complex;
        protected int count;
        protected List<Client> clients;

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


    public static class OrderedComplex extends Complex{
        protected int count;

        public OrderedComplex () {

        }

        public OrderedComplex(int complex, long idofclientgroup, String clientgroupname) {
            super(complex, idofclientgroup, clientgroupname);
        }

        @Override
        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }


    public static class Client {
        protected long idofclientgroup;
        protected long idofclient;
        protected String firstname;
        protected String secondname;
        protected String surname;
        protected long idofrule;
        protected String ruleDescription;
        protected int complex;
        protected int priority;
        protected int actionType;
        protected int inBuilding;
        protected long price;
        protected boolean temporarySaved;
        protected Long idoforder;
        protected Long idofReplaceClient;
        protected Integer groupNum;
        protected double discountRate;
        protected double balance;

        public Client() {

        }

        public Client(long idofclientgroup, long idofclient, String firstname, String secondname,
                String surname, long idofrule, String ruleDescription, int complex, int priority,
                long price, Integer action, Integer inBuilding) {
            this.idofclientgroup = idofclientgroup;
            this.idofclient = idofclient;
            this.firstname = firstname;
            this.secondname = secondname;
            this.surname = surname;
            this.idofrule = idofrule;
            this.ruleDescription = ruleDescription;
            this.complex = complex;
            this.priority = priority;
            actionType = action == null ? BLOCK_CLIENT : action;
            this.inBuilding = inBuilding == null ? OUTSIDE_CLIENT : inBuilding;
            this.price = price;
            this.discountRate = 100D;
            this.balance = 0D;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getDiscountRate() {
            return discountRate;
        }

        public void setDiscountRate(double discountRate) {
            this.discountRate = discountRate;
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
        
        public String getFullNameWithoutBreaks () {
            return firstname + " " + secondname + " " + surname;
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
                case BLOCK_CLIENT:
                    return "БЛОК";
                case PAY_CLIENT:
                    return "ОПЛАТА";
                default:
                    return "...";
            }
        }
        
        public String getActionIcon() {
            switch (actionType) {
                case BLOCK_CLIENT:
                    return "stop";
                case PAY_CLIENT:
                    return "play";
                default:
                    return "stop";
            }
        }

        public void setInBuilding(int inBuilding) {
            this.inBuilding = inBuilding;
        }
        
        public int getInBuilding() {
            return inBuilding;
        }
        
        public String getInBuildingIcon() {
            switch (inBuilding) {
                case INSIDE_CLIENT:
                    return "inside";
                case OUTSIDE_CLIENT:
                    return "outside";
                default: 
                    return "outside";
            }
        }

        public Long getIdofReplaceClient() {
            return idofReplaceClient;
        }

        public void setIdofReplaceClient(Long idofReplaceClient) {
            this.idofReplaceClient = idofReplaceClient;
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

        public Integer getGroupNum() {
            return groupNum;
        }

        public void setGroupNum(Integer groupNum) {
            this.groupNum = groupNum;
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


    public class ReplaceClient {
        private long idofclient;
        private String firstname;
        private String secondname;
        private String surname;
        private Long idOfTargetClient;
        private String nameOfTargetClient;

        public ReplaceClient(long idofclient, String firstname, String secondname, String surname) {
            this.idofclient = idofclient;
            this.firstname = firstname;
            this.secondname = secondname;
            this.surname = surname;
            idOfTargetClient = null;
        }

        public long getIdofclient() {
            return idofclient;
        }

        public String getFullName() {
            return firstname + " " + secondname + " " + surname;
        }

        public Long getIdOfTargetClient() {
            return idOfTargetClient;
        }

        public void setIdOfTargetClient(Long idOfTargetClient) {
            this.idOfTargetClient = idOfTargetClient;
        }

        public void setNameOfTargetClient(String nameOfTargetClient) {
            this.nameOfTargetClient = nameOfTargetClient;
        }

        public String getNameOfTargetClient() {
            return nameOfTargetClient;
        }
    }

    public class OrderPurchaseItem {

        protected String name;
        protected String menuGroup;
        protected String rootMenu;
        protected long idOfRule;
        protected String goodGuid;
        protected int type;
        protected int menuOrigin;

        public OrderPurchaseItem(String name, String menuGroup, String rootMenu,
                long idOfRule, String goodGuid, int type, int menuOrigin) {
            this.name = name;
            this.menuGroup = menuGroup;
            this.rootMenu = rootMenu;
            this.idOfRule = idOfRule;
            this.goodGuid = goodGuid;
            this.type = type;
            this.menuOrigin = menuOrigin;
        }

        public String getName() {
            return name;
        }

        public String getMenuGroup() {
            return menuGroup;
        }

        public String getRootMenu() {
            return rootMenu;
        }

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getGoodGuid() {
            return goodGuid;
        }

        public int getType() {
            return type;
        }

        public int getMenuOrigin() {
            return menuOrigin;
        }
    }
}
