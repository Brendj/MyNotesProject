package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesDataOnOrdersAndPaymentJasperReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:49
 * Отчет "Статистика о расхождении данных по заказам и оплате"
 */

public class DiscrepanciesDataOnOrdersAndPaymentBuilder extends BasicReportForContragentJob.Builder {

    private final String templateFilename;

    public DiscrepanciesDataOnOrdersAndPaymentBuilder() {
        this(null);
    }

    public DiscrepanciesDataOnOrdersAndPaymentBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (StringUtils.isEmpty(this.templateFilename)) {
            throw new Exception("Не найден файл шаблона.");
        }
        Long idOfContragent = Long
                .parseLong(getReportProperties().getProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID));
        Contragent contragent = (Contragent) session.get(Contragent.class, idOfContragent);
        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        Date generateBeginTime = new Date();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        JRDataSource dataSource = buildDataSource(session, contragent, idOfOrgList, startTime, endTime);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new DiscrepanciesDataOnOrdersAndPaymentJasperReport(generateBeginTime, generateDuration, jasperPrint,
                startTime, endTime, contragent.getIdOfContragent());
    }

    @SuppressWarnings("unchecked")
    public JRDataSource buildDataSource(Session session, Contragent contragent, List<Long> idOfOrgList, Date startTime,
            Date endTime) throws Exception {
        List<Item> items = new ArrayList<Item>();
        Criteria catCriteria = session.createCriteria(Org.class)
                .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("defaultSupplier", contragent))
                .setProjection(Projections.projectionList()
                        .add(Projections.property("idOfOrg"))
                        .add(Projections.property("type"))
                        .add(Projections.property("shortName"))
                        .add(Projections.property("address"))
                        .add(Projections.property("cat.idOfCategoryOrg"))
                        .add(Projections.property("cat.categoryName")))
                .addOrder(Order.asc("idOfOrg"));
        if (!idOfOrgList.isEmpty()) {
            catCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        }
        List<Object[]> catRes = (List<Object[]>) catCriteria.list();
        Map<Long, OrgItem> orgItems = new HashMap<Long, OrgItem>();
        for (Object[] row : catRes) {
            Long idOfOrg = (Long) row[0];
            OrganizationType orgType = (OrganizationType) row[1];
            String shortName = StringUtils.defaultString((String) row[2]);
            String address = StringUtils.defaultString((String) row[3]);
            Long idOfCategory = (Long) row[4];
            String categoryName = (String) row[5];
            String category = idOfCategory == null ? (orgType == null ? "" : orgType.toString())
                    : StringUtils.defaultString(categoryName);
            OrgItem orgItem = orgItems.get(idOfOrg);
            if (orgItem == null) {
                orgItems.put(idOfOrg, new OrgItem(idOfOrg, shortName, address, category));
            } else {
                orgItem.setOrgTypeCategory(orgItem.getOrgTypeCategory() + ", " + category);
            }
        }
        Criteria orderCriteria = session.createCriteria(Org.class)
                .createAlias("ordersInternal", "ord")
                .add(Restrictions.eq("defaultSupplier", contragent))
                .add(Restrictions.eq("ord.orderType", OrderTypeEnumType.CORRECTION_TYPE))
                .add(Restrictions.ge("ord.createTime", startTime))
                .add(Restrictions.lt("ord.createTime", endTime))
                .setProjection(Projections.projectionList()
                        .add(Projections.count("ord.compositeIdOfOrder.idOfOrder"))
                        .add(Projections.sum("ord.RSum"))
                        .add(Projections.groupProperty("idOfOrg"))
                        .add(Projections.groupProperty("ord.createTime")))
                .addOrder(Order.asc("idOfOrg"));
        if (!idOfOrgList.isEmpty()) {
            orderCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        }
        List<Object[]> orderRes = (List<Object[]>) orderCriteria.list();
        for (Object[] row : orderRes) {
            Item item = new Item();
            item.setCountActs(row[0] == null ? 0 : ((Long) row[0]).intValue());
            item.setDifferentSum(row[1] == null ? BigDecimal.ZERO :
                    new BigDecimal(CurrencyStringUtils.copecksToRubles((Long) row[1], 0)));
            item.setIdOfOrg((Long) row[2]);
            OrgItem orgItem = orgItems.get(item.getIdOfOrg());
            item.setOrgShortName(orgItem.getOrgShortName());
            item.setAddress(orgItem.getAddress());
            item.setCurrentDate(CalendarUtils.truncateToDayOfMonth((Date) row[3]));
            item.setOrgTypeCategory(orgItem.getOrgTypeCategory());
            items.add(item);
        }
        // Заполняем нулями показатели по тем дням, в которые у организаций не было заказов.
        // Таким образом, создаем в отчете нулевые строки.
        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
        while (beginDate.getTime() <= endDate.getTime()) {
            for (Map.Entry<Long, OrgItem> entry : orgItems.entrySet()) {
                Item item = new Item(entry.getKey(), beginDate);
                if (!items.contains(item)) {
                    item.setOrgShortName(entry.getValue().getOrgShortName());
                    item.setOrgTypeCategory(entry.getValue().getOrgTypeCategory());
                    item.setAddress(entry.getValue().getAddress());
                    item.setCountActs(0);
                    item.setDifferentSum(BigDecimal.ZERO);
                    items.add(item);
                }
            }
            beginDate = CalendarUtils.addDays(beginDate, 1);
        }
        return new JRBeanCollectionDataSource(items);
    }
}
