/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ZeroTransaction;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 29.03.16
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionsReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public ZeroTransactionsReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "ZeroTransactionsReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("startDate", startTime);
        parameterMap.put("endDate", endTime);

        JRDataSource dataSource = buildDataSource(session, startTime, endTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.IS_WRAP_BREAK_WORD, Boolean.TRUE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        exporter.exportReport();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new ZeroTransactionsReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        List<ZeroTransactionReportItem> zeroTransactionReportItemList = new ArrayList<ZeroTransactionReportItem>();

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

        String str = "";
        if (idOfOrgList.isEmpty()) {
            throw new Exception(String.format("Не указана организация '%s'", str));
        }

        Query query = session.createQuery(
                "from ZeroTransaction zt where zt.compositeIdOfZeroTransaction.transactionDate between :startDate AND :endDate "
                        + "AND zt.compositeIdOfZeroTransaction.idOfOrg in :idOfOrgList order by zt.compositeIdOfZeroTransaction.transactionDate, " +
                        "zt.compositeIdOfZeroTransaction.idOfOrg");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameterList("idOfOrgList", idOfOrgList);

        List<ZeroTransaction> list = query.list();

        int num = 1;
        for (ZeroTransaction zt : list) {
            Integer normInOut = null;
            Integer factInOut = null;
            String commentInOut = "";

            Integer normDiscountLowGrade = null;
            Integer factDiscountLowGrade = null;
            String commentDiscountLowGrade = "";

            Integer normDiscountMiddleEightGrade = null;
            Integer factDiscountMiddleEightGrade = null;
            String commentDiscountMiddleEightGrade = "";

            Integer normPaydableChildren = null;
            Integer factPaydableChildren = null;
            String commentPaydableChildren = "";

            Integer normPaydableNotChildren = null;
            Integer factPaydableNotChildren = null;
            String commentPaydableNotChildren = "";

            Integer normBuffet = null;
            Integer factBuffet = null;
            String commentBuffet = "";
            Integer goalSumBuffet = null;

            switch (zt.getCompositeIdOfZeroTransaction().getIdOfCriteria()) {
                case ZT_TYPE_INOUT:
                    normInOut = zt.getCriteriaLevel();
                    factInOut = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentInOut = zt.getComment();
                    break;
                case ZT_TYPE_DISCOUNTPLANLOWGRADE:
                    normDiscountLowGrade = zt.getCriteriaLevel();
                    factDiscountLowGrade = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentDiscountLowGrade = zt.getComment();
                    break;
                case ZT_TYPE_DISCOUNTPLANMIDDLEHIGHTGRADE:
                    normDiscountMiddleEightGrade = zt.getCriteriaLevel();
                    factDiscountMiddleEightGrade = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentDiscountMiddleEightGrade = zt.getComment();
                    break;
                case ZT_TYPE_PAYDABLEPLANCHILDREN:
                    normPaydableChildren = zt.getCriteriaLevel();
                    factPaydableChildren = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentPaydableChildren = zt.getComment();
                    break;
                case ZT_TYPE_PAYDABLEPLANNOTCHILDREN:
                    normPaydableNotChildren = zt.getCriteriaLevel();
                    factPaydableNotChildren = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentPaydableNotChildren = zt.getComment();
                    break;
                case ZT_TYPE_BUFFET:
                    normBuffet = zt.getCriteriaLevel();
                    factBuffet = Math.round(zt.getActualLevel().floatValue() * 100 / zt.getTargetLevel().floatValue());
                    commentBuffet = zt.getComment();
                    break;
            }
            ZeroTransactionReportItem item = new ZeroTransactionReportItem(num, zt.getOrg().getIdOfOrg(), zt.getOrg().getShortNameInfoService(),
                    zt.getOrg().getDistrict(), zt.getOrg().getAddress(), zt.getCompositeIdOfZeroTransaction().getTransactionDate(),
                    normInOut, factInOut, commentInOut, normDiscountLowGrade, factDiscountLowGrade, commentDiscountLowGrade, normDiscountMiddleEightGrade,
                    factDiscountMiddleEightGrade, commentDiscountMiddleEightGrade, normPaydableChildren, factPaydableChildren, commentPaydableChildren,
                    normPaydableNotChildren, factPaydableNotChildren, commentPaydableNotChildren, normBuffet, factBuffet, commentBuffet, goalSumBuffet);
            zeroTransactionReportItemList.add(item);
                    num++;
        }

        return new JRBeanCollectionDataSource(zeroTransactionReportItemList);
    }
}
