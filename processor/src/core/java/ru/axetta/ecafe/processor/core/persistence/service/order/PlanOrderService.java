/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.order;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.ClientsEntereventsService;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * User: shamil
 * Date: 10.10.14
 * Time: 11:33
 */
@Service
public class PlanOrderService {

    // Должен был получить бесплатное питание
    private List<PlanOrderItem> loadPlanOrderItemToPay(Session session, Date payedDate, Long orgId) {
        List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
        // клиенты которые в здании
        List<ClientInfo> clientInfoList = ClientsEntereventsService.loadClientsInfoToPay(session, orgId);
        // правила для организации
        List<DiscountRule> rulesForOrg = ClientsEntereventsService.getDiscountRulesByOrg(session);
        // платные категории
        List<Long> onlyPaydAbleCategories = ClientsEntereventsService.loadAllPaydAbleCategories(session);

        for (ClientInfo clientInfo : clientInfoList) {
            List<Long> categories = getClientBenefits(clientInfo.categoriesDiscounts, clientInfo.groupName);
            categories.removeAll(onlyPaydAbleCategories);
            List<DiscountRule> rules = getClientsRules(rulesForOrg, categories);
            for (DiscountRule rule : rules) {
                addPlanOrderItems(allItems, clientInfo.clientId, rule, payedDate);
            }
        }
        return allItems;
    }
    private void addPlanOrderItems(List<PlanOrderItem> items, Long clientId, DiscountRule rule, Date payedDate) {

        String complexMap = rule.getComplexesMap();

        List<Integer> allComplexesId = new ArrayList<Integer>();

        if ((complexMap != null) ||(complexMap != "")) {
            String[] complexes = complexMap.split(";");
            for (Object complex: complexes) {
                String complexItem = (String) complex;

                String [] complexItemSplited = complexItem.split("=");
                if (Integer.parseInt(complexItemSplited[1]) > 0) {
                    allComplexesId.add(Integer.parseInt(complexItemSplited[0]));
                }
            }
        }

        for (Integer complexId : allComplexesId) {
            PlanOrderItem item = new PlanOrderItem(clientId, complexId, rule.getIdOfRule(), payedDate);
            items.add(item);
            if (rule.getOperationOr()) {
                return;
            }
        }
    }

    // Получает все льготы клиента
    public List<Long> getClientBenefits(String categoriesDiscounts, String groupName) {
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
}
