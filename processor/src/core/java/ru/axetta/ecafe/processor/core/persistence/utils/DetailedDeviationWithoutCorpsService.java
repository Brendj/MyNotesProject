/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ComplexInfoItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

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
 * Date: 29.01.15
 * Time: 16:42
 */

public class DetailedDeviationWithoutCorpsService {

    private static final Logger logger = LoggerFactory.getLogger(DetailedDeviationWithoutCorpsService.class);

    public DetailedDeviationWithoutCorpsService() {
    }

    //Вернет список клиентов которые были оплачены (по всем дружественным организациям)
    // в зависимости от параметра orderType - строковое, по интервалу от startDate до endTime
    public static List<PlanOrderItem> loadPaidPlanOrderInfo(Session session, String orderType, List<Long> idOfOrgList,
            Date startTime, Date endTime) {
        List<PlanOrderItem> resultPlanOrder = new ArrayList<PlanOrderItem>();

        Query query = session.createSQLQuery(
                "SELECT cfo.idofclient, (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname, (cfod.menutype -50) AS complexid, cfod.idofrule, cfo.createddate, g.groupname, cfod.menudetailname "
                        + "FROM cf_orders cfo "
                        + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                        + "LEFT JOIN cf_clients c ON  cfo.idofclient = c.idofclient and cfod.idoforg = c.idoforg "
                        + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup and cfod.idoforg = g.idoforg "
                        + "LEFT JOIN cf_persons p ON c.idofperson = p.idofperson WHERE cfo.ordertype IN (" + orderType
                        + ") AND cfo.idoforg in (:idOfOrgList) AND cfo.state = 0 "
                        + "AND cfo.createddate >= :startTime AND cfo.createddate < :endTime "
                        + "AND cfod.menutype >= 50 AND cfod.menutype <100 AND cfod.idofrule >= 0");
        query.setParameter("startTime", startTime.getTime());
        query.setParameterList("idOfOrgList", idOfOrgList);
        query.setParameter("endTime", endTime.getTime());

        List result = query.list();

        //Парсим данные
        for (Object o : result) {
            Object[] resultPlanOrderItem = (Object[]) o;

            String clientName;
            String groupName;

            if (resultPlanOrderItem[1] == null) {
                clientName = DetailedDeviationWithoutCorpsService
                        .getClientNameByClientId(session, ((BigInteger) resultPlanOrderItem[0]).longValue());
            } else {
                clientName = (String) resultPlanOrderItem[1];
            }

            if (resultPlanOrderItem[5] == null) {
                groupName = DetailedDeviationWithoutCorpsService
                        .getClientGroupNameByClientId(session, ((BigInteger) resultPlanOrderItem[0]).longValue());
            } else {
                groupName = (String) resultPlanOrderItem[5];
            }

            PlanOrderItem planOrderItem = new PlanOrderItem(((BigInteger) resultPlanOrderItem[0]).longValue(),
                    clientName, (Integer) resultPlanOrderItem[2], ((BigInteger) resultPlanOrderItem[3]).longValue(),
                    CalendarUtils.truncateToDayOfMonth(new Date(((BigInteger) resultPlanOrderItem[4]).longValue())),
                    groupName, (String) resultPlanOrderItem[6]);
            resultPlanOrder.add(planOrderItem);
        }
        return resultPlanOrder;
    }

    // Те кто должен был получить | Проход по карте зафиксирован
    public static List<PlanOrderItem> loadPlanOrderItemToPayDetected(Session session, Date startTime, Date endTime,
            List<Long> idOfOrgList) {
        List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
        // клиенты которые в здании
        List<ClientInfo> clientInfoList = DetailedDeviationWithoutCorpsService
                .loadClientsInfoToPayDetected(session, startTime, endTime, idOfOrgList);
        if (!clientInfoList.isEmpty()) {
            for (ClientInfo clientInfo : clientInfoList) {
                // правила для организации
                List<DiscountRule> rulesForOrg = DetailedDeviationWithoutCorpsService
                        .getDiscountRulesByOrg(session, clientInfo.getIdOfOrg());
                // платные категории
                List<Long> onlyPaydAbleCategories = DetailedDeviationWithoutCorpsService
                        .loadAllPaydAbleCategories(session);
                List<ComplexInfoItem> complexInfoItemListByPlan = DetailedDeviationWithoutCorpsService
                        .loadComplexNameByPlan(session, clientInfo.getIdOfOrg(), startTime, endTime);

                List<Long> categories = getClientBenefits(clientInfo.categoriesDiscounts, clientInfo.groupName);
                categories.removeAll(onlyPaydAbleCategories);
                List<DiscountRule> rules = getClientsRules(rulesForOrg, categories);
                rules = getRulesByHighPriority(rules);
                for (DiscountRule rule : rules) {
                    addPlanOrderItems(allItems, complexInfoItemListByPlan, clientInfo, rule, startTime);
                }
            }
            return allItems;
        }
        return null;
    }

    // Проход по карте зафиксирован
    public static List<ClientInfo> loadClientsInfoToPayDetected(Session session, Date startTime, Date endTime,
            List<Long> idOfOrgList) {
        List<ClientInfo> clientInfoList = new ArrayList<ClientInfo>();

        Query query = session.createSQLQuery(
                "SELECT DISTINCT cl.idofclient, cl.idoforg, (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname, gr.idofclientgroup, gr.groupName, cl.categoriesDiscounts "
                        + "FROM cf_enterevents e INNER JOIN cf_clients cl ON cl.idOfClient = e.idOfClient "
                        + "LEFT JOIN cf_clientgroups gr "
                        + "ON gr.idofclientgroup = cl.idofclientgroup AND gr.idoforg = cl.idoforg "
                        + "LEFT JOIN cf_persons p ON cl.idofperson = p.idofperson "
                        + "WHERE e.evtdatetime BETWEEN :startTime AND :endTime AND e.idoforg IN ( :idOfOrgList ) AND e.idofclient IS NOT null "
                        + "AND e.passdirection NOT IN (2, 5, 8, 9) AND gr.idOfClientGroup < 1100000030");
        query.setParameter("startTime", startTime.getTime());
        query.setParameterList("idOfOrgList", idOfOrgList);
        query.setParameter("endTime", endTime.getTime());

        List result = query.list();

        for (Object resultClient : result) {
            Object[] resultClientItem = (Object[]) resultClient;
            ClientInfo clientInfo = new ClientInfo(((BigInteger) resultClientItem[0]).longValue(),
                    ((BigInteger) resultClientItem[1]).longValue(), (String) resultClientItem[2],
                    ((BigInteger) resultClientItem[3]).longValue(), (String) resultClientItem[4],
                    (String) resultClientItem[5]);
            clientInfoList.add(clientInfo);
        }
        return clientInfoList;
    }

    //---Функции построения плана---

    // Получает все правила по организации
    public static List<DiscountRule> getDiscountRulesByOrg(Session session, long idOfOrg) {
        return loadRulesForOrg(session, idOfOrg);
    }

    private static List<DiscountRule> loadRulesForOrg(Session persistenceSession, long idOfOrg) {
        Criteria criteriaDiscountRule = persistenceSession.createCriteria(DiscountRule.class);
        Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        List<DiscountRule> rules = new ArrayList<DiscountRule>();
        if (!categoryOrgSet.isEmpty()) {
            for (Object object : criteriaDiscountRule.list()) {
                DiscountRule discountRule = (DiscountRule) object;
                boolean bIncludeRule = false;
                if (discountRule.getCategoryOrgs().isEmpty()) {
                    bIncludeRule = true;
                } else if (categoryOrgSet.containsAll(discountRule.getCategoryOrgs())) {
                    bIncludeRule = true;
                }
                if (bIncludeRule) {
                    rules.add(discountRule);
                }
            }
        } else {
            for (Object object : criteriaDiscountRule.list()) {
                DiscountRule discountRule = (DiscountRule) object;
                    /* если правила не установлены категории организаций то отправляем*/
                if (discountRule.getCategoryOrgs().isEmpty()) {
                    rules.add(discountRule);
                }
            }
        }
        return rules;
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
        if (constant.isEmpty()) {
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

    // Загружает имена комплексов по организации клиента
    public static List<ComplexInfoItem> loadComplexNameByPlan(Session session, Long idOfOrg, Date startTime,
            Date endTime) {
        List<ComplexInfoItem> complexInfoItems = new ArrayList<ComplexInfoItem>();

        Query query = session.createSQLQuery(
                "SELECT idofcomplex, menudate, complexname FROM cf_complexinfo WHERE idoforg = :idOfOrg"
                        + " AND menudate BETWEEN :startTime AND :endTime "
                        + "GROUP BY idofcomplex, complexname, menudate ORDER BY menudate, idofcomplex ");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        List result = query.list();

        for (Object o : result) {
            Object[] resultPlanOrderItem = (Object[]) o;
            ComplexInfoItem complexInfoItem = new ComplexInfoItem((Integer) resultPlanOrderItem[0],
                    CalendarUtils.truncateToDayOfMonth(new Date(((BigInteger) resultPlanOrderItem[1]).longValue())),
                    (String) resultPlanOrderItem[2]);
            complexInfoItems.add(complexInfoItem);
        }
        return complexInfoItems;
    }

    // Заполнение плана кто должен был получить питание на основе комплексов
    private static void addPlanOrderItems(List<PlanOrderItem> items, List<ComplexInfoItem> complexInfoItemList,
            ClientInfo clientInfo, DiscountRule rule, Date payedDate) {

        String complexMap = rule.getComplexesMap();

        List<Integer> allComplexesId = new ArrayList<Integer>();

        if ((complexMap != null) && (complexMap != "")) {
            String[] complexes = complexMap.split(";");
            for (String complex : complexes) {
                String[] complexItemSplit = complex.split("=");
                if (Integer.parseInt(complexItemSplit[1]) > 0) {
                    allComplexesId.add(Integer.parseInt(complexItemSplit[0]));
                }
            }
        }

        for (Integer complexId : allComplexesId) {
            String complexName = getComplexName(complexInfoItemList, payedDate, complexId);
            PlanOrderItem item = new PlanOrderItem(clientInfo.getClientId(), clientInfo.getClientName(), complexId,
                    rule.getIdOfRule(), payedDate, clientInfo.getGroupName(), complexName);
            items.add(item);
            if (rule.getOperationOr()) {
                return;
            }
        }
    }

    // Получает имя комплекса
    private static String getComplexName(List<ComplexInfoItem> complexInfoItemList, Date payedDate, Integer complexId) {
        for (ComplexInfoItem complexInfoItem : complexInfoItemList) {
            if (complexInfoItem.getIdOfComplex().equals(complexId) && complexInfoItem.getMenuDate().equals(payedDate)) {
                return complexInfoItem.getComplexName();
            }
        }
        return "";
    }

    //Вспомагательные функции
    public static String getClientNameByClientId(Session session, Long idOfClient) {
        Query query = session.createSQLQuery(
                "SELECT (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname FROM cf_clients cl LEFT JOIN cf_persons p ON cl.idofperson = p.idofperson "
                        + "WHERE idOfClient = :idOfClient ");
        query.setParameter("idOfClient", idOfClient);

        String result = (String) query.uniqueResult();
        return result;
    }

    public static String getClientGroupNameByClientId(Session session, Long idOfClient) {
        Query query = session.createSQLQuery(
                "SELECT g.groupname FROM cf_clients c LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                        + "WHERE c.idofclient = :idOfClient");
        query.setParameter("idOfClient", idOfClient);

        String result = (String) query.uniqueResult();
        return result;
    }
}
