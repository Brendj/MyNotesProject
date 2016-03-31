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
            Integer normDiscount = null;
            Integer factDiscount = null;
            String commentDiscount = "";
            Integer normPaydable = null;
            Integer factPaydable = null;
            String commentPaydable = "";
            switch (zt.getCompositeIdOfZeroTransaction().getIdOfCriteria()) {
                case ZT_TYPE_INOUT:
                    normInOut = zt.getTargetLevel();
                    factInOut = zt.getActualLevel() * 100 / zt.getTargetLevel();
                    commentInOut = zt.getComment();
                    break;
                case ZT_TYPE_DISCOUNTPLAN:
                    normDiscount = zt.getTargetLevel();
                    factDiscount = zt.getActualLevel() * 100 / zt.getTargetLevel();
                    commentDiscount = zt.getComment();
                    break;
                case ZT_TYPE_PAYDABLEPLAN:
                    normPaydable = zt.getTargetLevel();
                    factPaydable = zt.getActualLevel() * 100 / zt.getTargetLevel();
                    commentPaydable = zt.getComment();
                    break;
            }
            ZeroTransactionReportItem item = new ZeroTransactionReportItem(num, zt.getOrg().getIdOfOrg(), zt.getOrg().getShortNameInfoService(),
                    zt.getOrg().getDistrict(), zt.getOrg().getAddress(), zt.getCompositeIdOfZeroTransaction().getTransactionDate(),
                    normInOut, factInOut, commentInOut, normDiscount, factDiscount, commentDiscount, normPaydable, factPaydable,
                    commentPaydable);
            zeroTransactionReportItemList.add(item);
                    num++;
        }

        return new JRBeanCollectionDataSource(zeroTransactionReportItemList);
    }
}
