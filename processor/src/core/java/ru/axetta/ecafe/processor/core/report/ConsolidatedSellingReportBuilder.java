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
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
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
    public static final String SHOW_ALL_ORGS = "showAllOrgs";

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
        String org2_condition = StringUtils.isEmpty(contragent) ? "" : " and org2.defaultsupplier = " + contragent;
        org2_condition += StringUtils.isEmpty(idOfOrgs) ? "" : String.format(" and org2.idoforg in (%s)", idOfOrgs);

        List<ConsolidatedSellingReportItem> result_list = new ArrayList<ConsolidatedSellingReportItem>();

        List<Integer> orderTypes = new ArrayList<Integer>();
        orderTypes.add(OrderTypeEnumType.DEFAULT.ordinal());
        orderTypes.add(OrderTypeEnumType.PAY_PLAN.ordinal());
        orderTypes.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
        String query_string = reportProperties.getProperty(ConsolidatedSellingReportBuilder.SHOW_ALL_ORGS).equals("0") ?
                "select org.idoforg, org.shortnameinfoservice, org.district, org.address, "
                + "od.rprice, od.qty, od.menuorigin, od.menutype, pl.preorderguid is not NULL as ispreorder, "
                + "org.organizationtype "
                + "from cf_orgs org join cf_orders o on org.idoforg = o.idoforg "
                + "join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                + "left join cf_preorder_linkod pl on pl.idoforder = o.idoforder and pl.idoforg = o.idoforg "
                + "where o.createddate between :startDate and :endDate and o.ordertype in (:orderTypes) and o.state = 0 "
                + "and (pl.idofpreorderlinkod is null or (pl.idofpreorderlinkod is not NULL and pl.preorderguid is not NULL)) "
                + org_condition
                + "order by org.idoforg"
                :
                "select org.idoforg, org.shortnameinfoservice, org.district, org.address, query.rprice, query.qty, "
                + "query.menuorigin, query.menutype, query.ispreorder, org.organizationtype "
                + " from cf_orgs org left join " + " ("
                + " select org2.idoforg, od.rprice, od.qty, od.menuorigin, od.menutype, pl.preorderguid is not NULL as ispreorder "
                + " from cf_orgs org2 join cf_orders o on org2.idoforg = o.idoforg "
                + " join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                + " left join cf_preorder_linkod pl on pl.idoforder = o.idoforder and pl.idoforg = o.idoforg "
                + " where o.createddate between :startDate and :endDate and o.ordertype in (:orderTypes) and o.state = 0 "
                + "and (pl.idofpreorderlinkod is null or (pl.idofpreorderlinkod is not NULL and pl.preorderguid is not NULL)) "
                + org2_condition
                + " and org2.state = 1 "
                + " ) as query on org.idoforg = query.idoforg"
                + " where org.state = 1 "
                + org_condition
                + " order by org.idoforg\n";
        Query query = session.createSQLQuery(query_string);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("orderTypes", orderTypes);
        List res = query.list();

        int number = 1;
        Long rprice = null;
        Integer qty = null;
        Integer menuOrigin = null;
        Integer menuType = null;
        Boolean isPreorder = null;
        for (Object o : res ) {
            Object[] row = (Object[]) o;
            Long idOfOrg = ((BigInteger) row[0]).longValue();
            String shortNameInfoService = (String) row[1];
            String district = (String) row[2];
            String address = (String) row[3];
            String orgType = OrganizationType.fromInteger((Integer) row[9]).toString();
            boolean data_exists = (row[4] != null);
            if (data_exists) {
                rprice = ((BigInteger) row[4]).longValue();
                qty = (Integer) row[5];
                menuOrigin = (Integer) row[6];
                menuType = (Integer) row[7];
                isPreorder = (Boolean) row[8];
            }
            ConsolidatedSellingReportItem item = getReportItemByIdOfOrg(result_list, idOfOrg);
            if (item == null) {
                item = new ConsolidatedSellingReportItem(idOfOrg, shortNameInfoService, district, address, number, orgType);
                number++;
                if (data_exists) addValues(item, rprice, qty, menuOrigin, menuType, isPreorder);
                result_list.add(item);
            } else {
                if (data_exists) addValues(item, rprice, qty, menuOrigin, menuType, isPreorder);
            }
        }

        return new JRBeanCollectionDataSource(result_list);
    }

    private void addValues(ConsolidatedSellingReportItem item, Long rprice, Integer qty, Integer menuOrigin,
            Integer menuType, Boolean isPreorder) {
        if (isPreorder) {
            item.addPreorderFood(rprice * qty);
        } else if ((menuType >= 50) && (menuType <= 99) && (rprice > 0)) {
            item.addComplexFood(rprice * qty);
        } else if (menuOrigin.equals(OrderDetail.PRODUCT_COMMERCIAL) && menuType.equals(0)) {
            item.addPayFood(rprice * qty);
        } else if (menuType.equals(0) && !menuOrigin.equals(OrderDetail.PRODUCT_VENDING) && !menuOrigin.equals(OrderDetail.PRODUCT_COMMERCIAL)) {
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
