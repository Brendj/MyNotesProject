package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.10.14
 * Time: 15:07
 */

public class ClientsEntereventsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientsEntereventsService.class);

    private ClientsEntereventsService() {
    }

    // Получаем список учеников внутри здания по организации и по дате
    public static List<Long> getClientsInsideBuilding(Session session, Long idOfOrg, Date startDate, Date endDate)
            throws Exception {
        org.hibernate.Query query = session.createSQLQuery(
                "SELECT c.idofclient FROM cf_enterevents e LEFT JOIN cf_clients c "
                        + "ON c.idofclient = e.idofclient WHERE c.idoforg = :idOfOrg "
                        + "AND e.evtdatetime >= :startDate AND e.evtdatetime <= :endDate "
                        + "AND c.idOfClientGroup < 1100000030 "
                        + "GROUP BY c.idofclient ORDER BY c.idofclient");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        List<Long> idsOfClients = query.list();
        return idsOfClients;
    }

    // Получаем список учеников вне здания по организации и по дате
    public static List<Long> getClientsOutsideBuilding(Session session, Long idOfOrg, Date startDate, Date endDate)
            throws Exception {
        List<Long> idOfClientsInside = getClientsInsideBuilding(session, idOfOrg, startDate, endDate);
        if (!idOfClientsInside.isEmpty()) {
            org.hibernate.Query query = session.createSQLQuery(
                    "SELECT c.idofclient FROM cf_clients c " + "WHERE c.idoforg = :idOfOrg "
                            + "AND c.idOfClientGroup < 1100000030 "
                            + "AND c.idofclient NOT IN (:idOfClientsInside) "
                            + "GROUP BY c.idofclient ORDER BY c.idofclient");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameterList("idOfClientsInside", idOfClientsInside);
            List<Long> idsOfClients = query.list();
            return idsOfClients;
        }
        return new ArrayList<Long>();
    }

    // Выбор правил по льготам
    public static List<DiscountRule> getDiscountRulesByClientBenefits(Session session, List<Long> clientBenefits) {
        List<DiscountRule> discountRulesResult = new ArrayList<DiscountRule>();

        Criteria discountRulesCriteria = session.createCriteria(DiscountRule.class);
        List<DiscountRule> discountRules = discountRulesCriteria.list();

        for (DiscountRule discount : discountRules) {
            String[] categoryDiscounts = discount.getCategoryDiscounts().split(",");
            if (categoryDiscounts.length > 0) {
                List<Long> categoryDiscountsList = new ArrayList<Long>();

                for (Object idsCategoryDiscounts : categoryDiscounts) {
                    categoryDiscountsList.add(Long.parseLong(StringUtils.trim((String) idsCategoryDiscounts)));
                }

                if (clientBenefits.containsAll(categoryDiscountsList)) {
                    discountRulesResult.add(discount);
                }
            }
        }
        return discountRulesResult;
    }

    // Получает все льготы клиента
    public static List<Long> getClientBenefits(Session session, Long idOfClient) {
        Client client = (Client) session.load(Client.class, idOfClient);
        List<Long> clientAllBenefits = new ArrayList<Long>();

        //Ручная загрузка
        String categoriesDiscounts = client.getCategoriesDiscounts();
        if (client.getCategoriesDiscounts().equals("") || client.getCategoriesDiscounts().equals(null)) {
        } else {
            String[] cD = categoriesDiscounts.split(",");
            if (cD.length > 0) {
                for (String idsCd : cD) {
                    clientAllBenefits.add(Long.parseLong(StringUtils.trim(idsCd)));
                }
            }
        }

        //Автоматическая загрузка
        org.hibernate.Query query = session.createSQLQuery("SELECT g.groupname "
                + "FROM cf_clients c LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup  "
                + "WHERE c.idofclientgroup = :idOfClientGroup " + "AND c.idoforg = :idOfOrg "
                + "AND g.idoforg= c.idoforg " + "GROUP BY g.groupname");
        query.setParameter("idOfClientGroup", client.getIdOfClientGroup());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        String groupName = (String) query.uniqueResult();

        Pattern patterNumber = Pattern.compile("\\d+");

        String constant = "";

        Matcher m = patterNumber.matcher(groupName);
        if (m.find()) {
            constant = m.group();
        }

        Long number = Long.parseLong(constant);

        if (number >= 1L && number <= 4L) {
            clientAllBenefits.add(-90L);
        } else if (number > 4L && number <= 9L) {
            clientAllBenefits.add(-91L);
        } else if (number > 9L && number <= 11L) {
            clientAllBenefits.add(-92L);
        }

        if (number == 1L) {
            clientAllBenefits.add(-101L);
        } else if (number == 2L) {
            clientAllBenefits.add(-102L);
        } else if (number == 3L) {
            clientAllBenefits.add(-103L);
        } else if (number == 4L) {
            clientAllBenefits.add(-104L);
        } else if (number == 5L) {
            clientAllBenefits.add(-105L);
        } else if (number == 6L) {
            clientAllBenefits.add(-106L);
        } else if (number == 7L) {
            clientAllBenefits.add(-107L);
        } else if (number == 8L) {
            clientAllBenefits.add(-108L);
        } else if (number == 9L) {
            clientAllBenefits.add(-109L);
        } else if (number == 10L) {
            clientAllBenefits.add(-110L);
        } else if (number == 11L) {
            clientAllBenefits.add(-111L);
        }

        org.hibernate.Query queryDiscount = session.createSQLQuery(
                "SELECT idofcategorydiscount FROM cf_categorydiscounts cd "
                        + "WHERE cd.idofcategorydiscount IN (:idOfDiscountRules) AND categoryType != 1");
        queryDiscount.setParameterList("idOfDiscountRules", clientAllBenefits);

        List<Long> ids = queryDiscount.list();

        List<Long> clientBenefitsNot = new ArrayList<Long>();

        for (Object obj : ids) {
            BigInteger idForLong = (BigInteger) obj;
            clientBenefitsNot.add(idForLong.longValue());
        }

        return clientBenefitsNot;
    }

/*    public static void fillList(List<PlanOrderItem> items, Long idOfClient, DiscountRule rule, ) {

    }*/


    // Должен был получить бесплатное питание, выбор клиентов для составления плана питания
    public static List<ClientInfo> loadClientsInfoToPay(Session session, Long orgId) {
        List<ClientInfo> clientInfoList = new ArrayList<ClientInfo>();
        org.hibernate.Query clientInfoQuery = session.createSQLQuery(
                "SELECT DISTINCT cl.idOfClient, (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname, gr.idofclientgroup,  gr.groupName, cl.categoriesDiscounts  "
                        + "FROM cf_clients cl LEFT JOIN cf_enterevents ce ON cl.idOfClient = ce.idOfClient "
                        + "LEFT JOIN cf_cards cr ON cr.IdOfClient = cl.IdOfClient "
                        + "LEFT JOIN cf_persons p ON cl.idofperson = p.idofperson "
                        + "LEFT JOIN cf_clientgroups gr ON gr.idofclientgroup = cl.idofclientgroup AND gr.idoforg = cl.idoforg "
                        + "WHERE cr.State = 0 AND gr.idOfClientGroup < 1100000030 "
                        //+ "AND ce.evtdatetime >= :payedDate AND ce.evtdatetime < :payedDateAddOneDay "
                        + " AND cl.idoforg = :orgId ");
        clientInfoQuery.setParameter("orgId", orgId);
        List<Object> result = clientInfoQuery.list();

        for (Object resultClient : result) {
            Object[] resultClientItem = (Object[]) resultClient;
            ClientInfo clientInfo = new ClientInfo(((BigInteger) resultClientItem[0]).longValue(),
                    (String) resultClientItem[1], ((BigInteger) resultClientItem[2]).longValue(),
                    (String) resultClientItem[3], (String) resultClientItem[4]);
            clientInfoList.add(clientInfo);
        }
        return clientInfoList;
    }

    public static final String orderType_Lgotniki = "6";//План льготного питания, резерв

    //Вернет список заказов которые были оплачены
    // в зависимости от параметра
    public static List<PlanOrderItem> loadPaidPlanOrders(Session session, String orderType, String idOfOrgList,
            Date startTime, Date endTime) {
        Query query = session.createSQLQuery(
                "SELECT cfo.idofclient,(cfod.menutype -50) AS complexId, cfod.idofrule, cfo.orderdate,  cfod.menudetailname, g.groupname  "
                        + "FROM cf_orders cfo "
                        + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                        + " LEFT JOIN cf_clients c ON  cfo.idofclient = c.idofclient and cfod.idoforg = c.idoforg "
                        + " LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup and cfod.idoforg = g.idoforg "
                        + "WHERE cfo.ordertype IN (" + orderType + ") AND cfo.idoforg IN (" + idOfOrgList + ") "
                        + " AND cfo.orderdate between :startTime AND :endTime "
                        + "AND cfod.menutype > 50 AND cfod.menutype <100 AND cfod.idofrule >= 0");

        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());


        List<Object> result = query.list();
        List<PlanOrderItem> planOrderItemList = new ArrayList<PlanOrderItem>();
        for (Object resultClient : result) {
            Object[] resultClientItem = (Object[]) resultClient;
            PlanOrderItem planOrderItem = new PlanOrderItem(((BigInteger) resultClientItem[0]).longValue(),
                    (Integer) resultClientItem[1], ((BigInteger) resultClientItem[2]).longValue(),
                    new Date(((BigInteger) resultClientItem[3]).longValue()), (String) resultClientItem[4], (String) resultClientItem[5]);
            planOrderItemList.add(planOrderItem);
        }

        return planOrderItemList;
    }

    //Вернет список клиентов которые были оплачены
    // в зависимости от параметра orderType - строковое, по интервалу от startDate до endTime
    public static List<PlanOrderItem> loadPaidPlanOrderInfo(Session session, String orderType, List<Long> idOfOrgList,
            Date startTime, Date endTime) {


        List<PlanOrderItem> resultPlanOrder = new ArrayList<PlanOrderItem>();

        String[] orderTypeArray = orderType.split(",");
        List<Integer> orderTypeList = new ArrayList<Integer>();

        for (String ordType : orderTypeArray) {
            orderTypeList.add(Integer.parseInt(ordType));
        }

        Query query = session.createSQLQuery(
                "SELECT idofclient, (cfod.menutype -50) AS complexid, idofrule, orderdate  " + "FROM cf_orders cfo "
                        + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                        + "WHERE cfo.ordertype IN (:orderTypeList) " + "AND cfo.idoforg IN (:idOfOrgList) "
                        + "AND cfo.orderdate >= :startTime AND cfo.orderdate <= :endTime "
                        + "AND cfod.menutype > 50 AND cfod.menutype <100 AND cfod.idofrule >= 0");
        query.setParameterList("orderTypeList", orderTypeList);
        query.setParameterList("idOfOrgList", idOfOrgList);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        List result = query.list();

        //Парсим данные
        for (Object o : result) {
            Object[] resultPlanOrderItem = (Object[]) o;
            PlanOrderItem planOrderItem = new PlanOrderItem(((BigInteger) resultPlanOrderItem[0]).longValue(),
                    (Integer) resultPlanOrderItem[1], ((BigInteger) resultPlanOrderItem[2]).longValue(),
                    new Date(((BigInteger) resultPlanOrderItem[3]).longValue()));
            resultPlanOrder.add(planOrderItem);
        }
        return resultPlanOrder;
    }


    // Должен был получить бесплатное питание
    public static List<PlanOrderItem> loadPlanOrderItemToPay(Session session, Date payedDate, Long orgId) {
        List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
        // клиенты которые в здании
        List<ClientInfo> clientInfoList = ClientsEntereventsService.loadClientsInfoToPay(session, orgId);
        if (!clientInfoList.isEmpty()) {
            // правила для организации
            List<DiscountRule> rulesForOrg = ClientsEntereventsService.getDiscountRulesByOrg(session, orgId);
            // платные категории
            List<Long> onlyPaydAbleCategories = ClientsEntereventsService.loadAllPaydAbleCategories(session);

            for (ClientInfo clientInfo : clientInfoList) {
                List<Long> categories = getClientBenefits(clientInfo.categoriesDiscounts, clientInfo.groupName);
                categories.removeAll(onlyPaydAbleCategories);
                List<DiscountRule> rules = getClientsRules(rulesForOrg, categories);
                rules = getRulesByHighPriority(rules);
                for (DiscountRule rule : rules) {
                    addPlanOrderItems(allItems, clientInfo, rule, payedDate);
                }
            }
            return allItems;
        }
        return null;
    }

    // Отбирает все правила с высоким приорететом
    private static List<DiscountRule> getRulesByHighPriority(List<DiscountRule> rules) {
        List<DiscountRule> resList = new LinkedList<DiscountRule>();
        int maxPriority = 0;
        for (DiscountRule rule : rules) {
            if (rule.getPriority() > maxPriority) {
                maxPriority = rule.getPriority();
                resList.clear();
                resList.add(rule);
            } else if (rule.getPriority() == maxPriority) {
                resList.add(rule);
            }
        }
        return resList;
    }

    // Заполнение плана кто должен был получить питание на основе комплексов
    private static void addPlanOrderItems(List<PlanOrderItem> items, ClientInfo clientId, DiscountRule rule,
            Date payedDate) {

        String complexMap = rule.getComplexesMap();

        List<Integer> allComplexesId = new ArrayList<Integer>();

        if ((complexMap != null) || (complexMap != "")) {
            String[] complexes = complexMap.split(";");
            for (String complex : complexes) {
                String[] complexItemSplit = complex.split("=");
                if (Integer.parseInt(complexItemSplit[1]) > 0) {
                    allComplexesId.add(Integer.parseInt(complexItemSplit[0]));
                }
            }
        }

        for (Integer complexId : allComplexesId) {
            PlanOrderItem item = new PlanOrderItem(clientId.getClientId(), clientId.getClientName(), complexId,
                    rule.getIdOfRule(), payedDate, clientId.getGroupName());
            items.add(item);
            if (rule.getOperationOr()) {
                return;
            }
        }
    }

    // Получает все льготы клиента
    public static List<Long> getClientBenefits(String categoriesDiscounts, String groupName) {
        List<Long> clientAllBenefits = new ArrayList<Long>();
        //Ручная загрузка
        if (categoriesDiscounts.equals("") || categoriesDiscounts.equals(null)) {
        } else {
            String[] cD = categoriesDiscounts.split(",");
            if (cD.length > 0) {
                for (Object idsCd : cD) {
                    clientAllBenefits.add(Long.parseLong(StringUtils.trim((String) idsCd)));
                }
            }
        }

        //Автоматическая загрузка
        Pattern patterNumber = Pattern.compile("\\d+");
        String constant = "";
        Matcher m = patterNumber.matcher(groupName);
        if (m.find()) {
            constant = m.group();
        }
        if(constant.isEmpty()){
            return clientAllBenefits;
        }
        Long number = Long.parseLong(constant);

        if (number >= 1L && number <= 4L) {
            clientAllBenefits.add(-90L);
        } else if (number > 4L && number <= 9L) {
            clientAllBenefits.add(-91L);
        } else if (number > 9L && number <= 11L) {
            clientAllBenefits.add(-92L);
        }

        if (number == 1L) {
            clientAllBenefits.add(-101L);
        } else if (number == 2L) {
            clientAllBenefits.add(-102L);
        } else if (number == 3L) {
            clientAllBenefits.add(-103L);
        } else if (number == 4L) {
            clientAllBenefits.add(-104L);
        } else if (number == 5L) {
            clientAllBenefits.add(-105L);
        } else if (number == 6L) {
            clientAllBenefits.add(-106L);
        } else if (number == 7L) {
            clientAllBenefits.add(-107L);
        } else if (number == 8L) {
            clientAllBenefits.add(-108L);
        } else if (number == 9L) {
            clientAllBenefits.add(-109L);
        } else if (number == 10L) {
            clientAllBenefits.add(-110L);
        } else if (number == 11L) {
            clientAllBenefits.add(-111L);
        }

        return clientAllBenefits;
    }

    // Выбор правил по льготам
    public static List<DiscountRule> getClientsRules(List<DiscountRule> discountRules, List<Long> clientBenefits) {
        List<DiscountRule> discountRulesResult = new ArrayList<DiscountRule>();
        for (DiscountRule discount : discountRules) {
            String[] categoryDiscounts = discount.getCategoryDiscounts().split(",");
            if (categoryDiscounts.length > 0) {
                List<Long> categoryDiscountsList = new ArrayList<Long>();

                for (Object idsCategoryDiscounts : categoryDiscounts) {
                    categoryDiscountsList.add(Long.parseLong(StringUtils.trim((String) idsCategoryDiscounts)));
                }

                if (clientBenefits.containsAll(categoryDiscountsList)) {
                    discountRulesResult.add(discount);
                }
            }
        }
        return discountRulesResult;
    }

    //Получает все категории платные для исключения из категорий
    public static List<Long> loadAllPaydAbleCategories(Session session) {
        List<Long> idOfCategoryDiscounts = new ArrayList<Long>();

        org.hibernate.Query queryDiscount = session
                .createSQLQuery("SELECT idofcategorydiscount FROM cf_categorydiscounts cd WHERE categoryType = 1");

        List<BigInteger> result = queryDiscount.list();

        for (BigInteger idDiscount : result) {
            idOfCategoryDiscounts.add(idDiscount.longValue());
        }

        return idOfCategoryDiscounts;
    }

    // Получает все правила по организации
    public static List<DiscountRule> getDiscountRulesByOrg(Session session, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        List<DiscountRule> discountRules = new ArrayList<DiscountRule>();
        if (!categoryOrgSet.isEmpty()) {
            Object[] arrayCategory = categoryOrgSet.toArray();
            for (Object obj : arrayCategory) {
                CategoryOrg categoryOrg = (CategoryOrg) obj;
                discountRules.addAll(categoryOrg.getDiscountRules());
            }
            return discountRules;
        }
        return null;
    }

}
