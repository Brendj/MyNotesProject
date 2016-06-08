/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.report.budgetMealsShipping.BudgetMealsShippingItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 01.11.13
 * Time: 16:42
 * Отчет по отгрузке бюджетного питания резервной группы
 */

public class BudgetMealsShippingReport extends BasicReportForOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по отгрузке бюджетного питания резервной группы";
    public static final String[] TEMPLATE_FILE_NAMES = {"BudgetMealsShippingReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};


    private final static Logger logger = LoggerFactory.getLogger(BudgetMealsShippingReport.class);

    public BudgetMealsShippingReport() {
    }

    public BudgetMealsShippingReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new BudgetMealsShippingReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportJob.Builder {

        private String templateFilename;

        private Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            Date generateTime = new Date();
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
            parameterMap.put("IS_IGNORE_PAGINATION", true);
            parameterMap.put("orgName", getOrg().getOfficialName());
            JasperPrint jp = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime, endTime));
            Date generateEndTime = new Date();
            return new BudgetMealsShippingReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jp,
                    startTime, endTime, getOrg().getIdOfOrg());
        }

        @SuppressWarnings("unchecked")
        private JRDataSource createDataSource(Session session, Date startTime, Date endTime) {
            String sql =
                    "select ord.client.idOfClient, ord.client.person.surname, ord.client.person.firstName, ord.client.person.secondName, "
                            + " ord.client.clientGroup.groupName, ord.createTime, g.pathPart3, g.pathPart4, sum(det.qty) "
                            + " from Order ord join ord.orderDetailsInternal det join ord.org o join det.good g "
                            + " where ord.state=0 and det.state=0 and ord.createTime between :startTime and :endTime and ord.orderType = :orderType and ord.org.idOfOrg = :idOfOrg "
                            + " and det.menuType >= :minType and det.menuType <= :maxType "
                            + " group by ord.client.idOfClient, ord.client.person.surname, ord.client.person.firstName, "
                            + " ord.client.person.secondName, ord.client.clientGroup.groupName, ord.createTime, g.pathPart3, g.pathPart4";
            Query q = session.createQuery(sql)
                    .setParameter("startTime", startTime)
                    .setParameter("endTime", endTime)
                    .setParameter("orderType", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE)
                    .setParameter("idOfOrg", getOrg().getIdOfOrg())
                    .setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN)
                    .setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);
            List<Object[]> res = (List<Object[]>) q.list();
            Set<BudgetMealsShippingItem> items = new HashSet<BudgetMealsShippingItem>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            for (Object[] record : res) {
                BudgetMealsShippingItem item = new BudgetMealsShippingItem();
                item.setClientid((Long) record[0]);
                item.setClientname(StringUtils.join(new String[]{
                        StringUtils.trimToEmpty((String) record[1]), StringUtils.trimToEmpty((String) record[2]),
                        StringUtils.trimToEmpty((String) record[3])}, ' '));
                item.setClassname(StringUtils.trimToEmpty((String) record[4]));
                item.setDate(dateFormat.format((Date) record[5]));
                item.setLevel3(StringUtils.trimToEmpty((String) record[6]));
                item.setLevel4(StringUtils.trimToEmpty((String) record[7]));
                item.setQty((Long) record[8]);
                items.add(item);
            }
            return new JRBeanCollectionDataSource(items);
        }
    }
}
