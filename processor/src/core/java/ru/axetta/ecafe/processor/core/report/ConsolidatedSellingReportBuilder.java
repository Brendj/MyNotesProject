/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 29.03.16
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class ConsolidatedSellingReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public ConsolidatedSellingReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "ConsolidatedSellingReport.jasper";

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

        return new ConsolidatedSellingReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        String contragent = StringUtils.trimToEmpty(reportProperties.getProperty("contragent"));
        String org_condition = StringUtils.isEmpty(contragent) ? "" : " and org.defaultsupplier = " + contragent;
        org_condition += StringUtils.isEmpty(idOfOrgs) ? "" : String.format(" and org.idoforg in (%s)", idOfOrgs);

        List<ConsolidatedSellingReportItem> result_list = new ArrayList<ConsolidatedSellingReportItem>();

        List<Integer> orderTypes = new ArrayList<Integer>();
        orderTypes.add(OrderTypeEnumType.DEFAULT.ordinal());
        orderTypes.add(OrderTypeEnumType.PAY_PLAN.ordinal());
        orderTypes.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
        Query query = session.createSQLQuery("select org.idoforg, org.shortnameinfoservice, org.district, org.address, "
                + "od.rprice, od.qty, o.ordertype, od.menuorigin "
                + "from cf_orgs org join cf_orders o on org.idoforg = o.idoforg "
                + "join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                + "where o.createddate between :startDate and :endDate and o.ordertype in (:orderTypes) "
                + org_condition
                + "order by org.idoforg");
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("orderTypes", orderTypes);
        List res = query.list();

        int number = 1;
        for (Object o : res ) {
            Object[] row = (Object[]) o;
            Long idOfOrg = ((BigInteger) row[0]).longValue();
            String shortNameInfoService = (String) row[1];
            String district = (String) row[2];
            String address = (String) row[3];
            Long rprice = ((BigInteger) row[4]).longValue();
            Integer qty = (Integer) row[5];
            Integer orderType = (Integer) row[6];
            Integer menuOrigin = (Integer) row[7];
            ConsolidatedSellingReportItem item = getReportItemByIdOfOrg(result_list, idOfOrg);
            if (item == null) {
                item = new ConsolidatedSellingReportItem(idOfOrg, shortNameInfoService, district, address, number);
                number++;
                addValues(item, rprice, qty, orderType, menuOrigin);
                result_list.add(item);
            } else {
                addValues(item, rprice, qty, orderType, menuOrigin);
            }
        }

        return new JRBeanCollectionDataSource(result_list);
    }

    private void addValues(ConsolidatedSellingReportItem item, Long rprice, Integer qty, Integer orderType, Integer menuOrigin) {
        if (orderType.equals(OrderTypeEnumType.PAY_PLAN.ordinal()) || orderType.equals(OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal())) {
            item.addComplexFood(rprice * qty);
        } else if (menuOrigin.equals(OrderDetail.PRODUCT_COMMERCIAL)) {
            item.addPayFood(rprice * qty);
        } else {
            item.addBufferFood(rprice * qty);
        }
    }

    private ConsolidatedSellingReportItem getReportItemByIdOfOrg(List<ConsolidatedSellingReportItem> items, Long idOfOrg) {
        for (ConsolidatedSellingReportItem item : items) {
            if (item.getIdOfOrg().equals(idOfOrg)) return item;
        }
        return null;
    }
}
