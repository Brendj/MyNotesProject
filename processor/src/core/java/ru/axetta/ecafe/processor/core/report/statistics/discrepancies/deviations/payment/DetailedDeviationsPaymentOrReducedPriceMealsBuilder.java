package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.ClientsEntereventsService;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.10.14
 * Time: 18:06
 */

public class DetailedDeviationsPaymentOrReducedPriceMealsBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + DetailedDeviationsPaymentOrReducedPriceMealsJasperReport.class.getSimpleName() + ".jasper";
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (StringUtils.isEmpty(this.templateFilename)) {
            throw new Exception("Не найден файл шаблона.");
        }
        String idOfOrgs = StringUtils
                .trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));

        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        JRDataSource dataSource = buildDataSource(session, idOfOrgList, startTime, endTime);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new DetailedDeviationsPaymentOrReducedPriceMealsJasperReport(generateBeginTime, generateDuration,
                jasperPrint, startTime, endTime);
    }

    @SuppressWarnings("unchecked")
    private JRDataSource buildDataSource(Session session, List<Long> idOfOrgList, Date startTime, Date endTime)
            throws Exception {
/*        List<DeviationPaymentItem> deviationPaymentItems = new ArrayList<>();

        List<Long> idOfCLients = ClientsEntereventsService.getClientsInsideBuilding(session, 10L, startTime, endTime);

        List<Long> idOfClientsOutside = ClientsEntereventsService
                .getClientsOutsideBuilding(session, 10L, startTime, endTime);

        List<DiscountRule> discountRules = ClientsEntereventsService.getDiscountRulesByOrg(session, 10L);

*//*        for (Object obj : idOfCLients) {

            BigInteger idofclient = (BigInteger) obj;

            List<Long> longs = ClientsEntereventsService.getClientBenefits(session, idofclient.longValue());

            List<DiscountRule> discount = ClientsEntereventsService.getDiscountRulesByClientBenefits(session, longs);
        }*//*

        ClientsEntereventsService.loadAllPaydableCategories(session);*/

        loadPlanOrderItemToPay(session, startTime, 8L);

        return null; //new JRBeanCollectionDataSource();
    }



/*    private List<PlanOrderItem> loadAllOrderItemsToPay(Session session, Date startDate, Date endDate, List<Long> allOrgs){

    }*/

    // Должен был получить бесплатное питание
    private List<PlanOrderItem> loadPlanOrderItemToPay(Session session, Date payedDate, Long orgId) {
        List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
        // клиенты которые в здании
        List<ClientInfo> clientInfoList = ClientsEntereventsService.loadClientsInfoToPay(session, payedDate, orgId);
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
                addPlanOrderItems(allItems, clientInfo.clientId, rule, payedDate);
            }
        }
        return allItems;
    }

    private List<DiscountRule> getRulesByHighPriority(List<DiscountRule> rules) {
        List<DiscountRule> resList = new LinkedList<DiscountRule>();
        int maxPriority = 0;
        for(DiscountRule rule : rules){
            if(rule.getPriority() > maxPriority){
                maxPriority = rule.getPriority();
                resList.clear();
                resList.add(rule);
            }else if( rule.getPriority() == maxPriority){
                resList.add(rule);
            }
        }

        return resList;
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
