package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
                        + "AND c.idOfClientGroup NOT IN (1100001000, 1100001001, 1100000010, 1100000020, 1100000030, 1100000040, 1100000050, 1100000060, 1100000070, 1100000080) "
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
                            + "AND c.idOfClientGroup NOT IN (1100001000, 1100001001, 1100000010, 1100000020, 1100000030, 1100000040, 1100000050, 1100000060, 1100000070, 1100000080) "
                            + "AND c.idofclient NOT IN (:idOfClientsInside) "
                            + "GROUP BY c.idofclient ORDER BY c.idofclient");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameterList("idOfClientsInside", idOfClientsInside);
            List<Long> idsOfClients = query.list();
            return idsOfClients;
        }
        return new ArrayList<>();
    }

    public static List<Long> getClientsOrders(Session session, List<Long> idOfClients, Long idOfOrg, Date startDate,
            Date endDate) throws Exception {
        org.hibernate.Query query = session.createSQLQuery("");
        List<Long> arr = new ArrayList<>();
        return arr;
    }

    // Выбор правил по льготам
    public static List<DiscountRule> getDiscountRulesByClientBenefits(Session session, List<Long> clientBenefits) {
        List<DiscountRule> discountRulesResult = new ArrayList<>();

        Criteria discountRulesCriteria = session.createCriteria(DiscountRule.class);
        List<DiscountRule> discountRules = discountRulesCriteria.list();

        for (DiscountRule discount : discountRules) {
            String[] categoryDiscounts = discount.getCategoryDiscounts().split(",");
            if (categoryDiscounts.length > 0) {
                List<Long> categoryDiscountsList = new ArrayList<>();

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
        List<Long> clientAllBenefits = new ArrayList<>();

        //Ручная загрузка
        String categoriesDiscounts = client.getCategoriesDiscounts();
        if (client.getCategoriesDiscounts().equals("") || client.getCategoriesDiscounts().equals(null)) {
        } else {
            String[] cD = categoriesDiscounts.split(",");
            if (cD.length > 0) {
                for (Object idsCd : cD) {
                    clientAllBenefits.add(Long.parseLong(StringUtils.trim((String) idsCd)));
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

        List<Long> clientBenefitsNot = new ArrayList<>();

        for (Object obj : ids) {
            BigInteger idForLong = (BigInteger) obj;
            clientBenefitsNot.add(idForLong.longValue());
        }

        return clientBenefitsNot;
    }

    // Получает все правила по организации
    public static List<DiscountRule> getDiscountRulesByOrg(Session session, Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        Set<CategoryOrg> categoryOrgSet = org.getCategories();
        List<DiscountRule> discountRules = new ArrayList<>();
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

/*    public static void fillList(List<PlanOrderItem> items, Long idOfClient, DiscountRule rule, ) {

    }*/


    public static List<ClientInfo> loadClientsInfoToPay(Session session, Date payedDate, Long orgId) {

        Date payedDateAddOneDay = CalendarUtils.addOneDay(payedDate);

        org.hibernate.Query clientInfoQuery = session.createSQLQuery(
                "SELECT DISTINCT cl.idOfClient, cl.categoriesDiscounts, gr.idofclientgroup, gr.groupName "
                        + "FROM cf_clients cl LEFT JOIN cf_enterevents ce "
                        + "ON cl.idOfClient = ce.idOfClient "
                        + "LEFT JOIN cf_cards ON cr.IdOfClient = cl.IdOfClient "
                        + "LEFT JOIN cf_clientgroups gr ON gr.idofclientgroup = cl.IdOfClientGroup "
                        + "WHERE cr.State = 0 AND gr.idOfClientGroup > 1100000000 "
                        + "AND ce.evtdatetime >= :payedDate AND ce.evtdatetime < :payedDateAddOneDay "
                        + "AND gr.idOfOrg = cl.idOfOrg and cl.idOfOrg = :orgId "
                        + "GROUP BY cl.idOfClient, gr.idofClientGroup, gr.groupName");
        clientInfoQuery.setParameter("payedDate", payedDate.getTime());
        clientInfoQuery.setParameter("payedDateAddOneDay", payedDateAddOneDay.getTime());
        clientInfoQuery.setParameter("orgId", orgId);

        List<ClientInfo> clientInfoList = clientInfoQuery.list();
        return clientInfoList;
    }

    //Получает все категории платные для исключения из плана
    public static List<Long> loadAllPaydAbleCategories(Session session) {
        org.hibernate.Query queryDiscount = session
                .createSQLQuery("SELECT idofcategorydiscount FROM cf_categorydiscounts cd WHERE categoryType = 1");
        List<BigInteger> result = queryDiscount.list();

        List<Long> idOfCategoryDiscounts = new ArrayList<>();
        for (Object ids : result) {
            BigInteger idses = (BigInteger) ids;
            idOfCategoryDiscounts.add(idses.longValue());
        }

        return idOfCategoryDiscounts;
    }
}
