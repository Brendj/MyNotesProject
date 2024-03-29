/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.summarySalesToSchools;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 23.09.13
 * Time: 12:23
 * Сводный отчет по продажам в школах
 */

public class SSTSReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Сводный отчет по продажам в школах";
    public static final String[] TEMPLATE_FILE_NAMES = {"SSTSReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{30};


    private static final Logger logger = LoggerFactory.getLogger(SSTSReport.class);

    public SSTSReport() {
    }

    public SSTSReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
            Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new SSTSReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public int getDefaultReportPeriod() {
        return BasicReportJob.REPORT_PERIOD_TODAY;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            String idOfContragent = getReportProperties().getProperty(PARAM_CONTRAGENT_RECEIVER_ID); // ищем контргентов ТСП
            Long validId = null;
            Contragent contragent = null;
            if (idOfContragent != null) {
                try {
                    validId = Long.parseLong(idOfContragent);
                } catch (NumberFormatException e) {
                    throw new Exception("Ошибка парсинга идентификатора контрагента: " + idOfContragent, e);
                }
                contragent = (Contragent) session.get(Contragent.class, validId);
                if (contragent == null) {
                    throw new Exception("Контрагент не найден: " + idOfContragent);
                }
            }
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            Date generateTime = new Date();
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
            parameterMap.put("IS_IGNORE_PAGINATION", true);
            JasperPrint jp = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime, endTime, contragent));
            Date generateEndTime = new Date();
            return new SSTSReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jp, startTime,
                    endTime, validId);
        }

        @SuppressWarnings("unchecked")
        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Contragent contragent) {
            Criteria criteria = session.createCriteria(OrderDetail.class, "details")
                    .createAlias("details.order", "o", JoinType.INNER_JOIN)
                    .createAlias("details.org", "org", JoinType.INNER_JOIN).add(Restrictions.eq("state", 0))
                    .add(Restrictions.eq("o.state", 0)).add(Restrictions.eq("org.defaultSupplier", contragent))
                    .add(Restrictions.between("o.createTime", startTime, endTime)).addOrder(Order.asc("org.shortName"))
                    .setProjection(Projections.projectionList().add(Projections.sqlProjection(
                            "sum(case when {alias}.menuType = 0 and {alias}.menuOrigin not in (11, 20) then {alias}.qty * {alias}.rPrice else 0 end) as sumBuffet",
                            new String[]{"sumBuffet"}, new Type[]{new LongType()})).add(Projections.sqlProjection(
                            "sum(case when {alias}.menuType between 50 and 99 and {alias}.rPrice > 0 then {alias}.qty * {alias}.rPrice else 0 end) as sumComplex",
                            new String[]{"sumComplex"}, new Type[]{new LongType()})).add(Projections.sqlProjection(
                            "sum(case when {alias}.menuType between 50 and 99 and {alias}.rPrice = 0 and {alias}.discount > 0 then {alias}.qty * {alias}.discount else 0 end) as sumComplexBenefit",
                            new String[]{"sumComplexBenefit"}, new Type[]{new LongType()})).add(Projections
                            .sqlProjection(
                                    "sum(CASE WHEN {alias}.menuOrigin = 11 AND {alias}.menuType = 0 THEN {alias}.qty * {alias}.rPrice ELSE 0 END) AS sumProductVending",
                                    new String[]{"sumProductVending"}, new Type[]{new LongType()})).add(Projections
                            .sqlProjection(
                                    "sum(CASE WHEN {alias}.menuOrigin = 20 AND {alias}.menuType = 0 THEN {alias}.qty * {alias}.rPrice ELSE 0 END) AS sumCommercialFood",
                                    new String[]{"sumCommercialFood"}, new Type[]{new LongType()}))
                            .add(Projections.groupProperty("org.idOfOrg"), "orgId")
                            .add(Projections.groupProperty("org.shortName"), "orgName")
                            .add(Projections.groupProperty("org.address"), "orgAddress"))
                    .setResultTransformer(Transformers.aliasToBean(SSTSItem.class));
            List<SSTSItem> res = (List<SSTSItem>) criteria.list();
            return new JRBeanCollectionDataSource(res);
        }
    }
}
