/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 29.03.16
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class PaymentsBetweenContragentsReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public PaymentsBetweenContragentsReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "PaymentsBetweenContragentsReport.jasper";

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

        return new PaymentsBetweenContragentsReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        List<PaymentsBetweenContragentsReportItem> result_list = new ArrayList<PaymentsBetweenContragentsReportItem>();

        String idOfOrgStr = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrg"));
        Long idOfOrg = Long.valueOf(idOfOrgStr);
        List<Long> orgs = DAOUtils.findFriendlyOrgIds(session, idOfOrg);
        String query_str = "select c.contragentName, org.shortNameInfoService, org.shortAddress, od.menuDetailName, (od.rPrice+od.socdiscount) as rPrice, "
                + "count(od.qty) as qty, sum(od.rPrice*od.qty) as summa, sum(od.SocDiscount*od.qty) as summaDiscount, "
                + "org2.shortnameinfoservice as name2, org2.shortAddress as address2, c2.contragentName as cn2 "
                + "from cf_orders o join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                + "join cf_orgs org on org.idoforg = o.idoforg "
                + "join cf_contragents c on org.defaultsupplier = c.idofcontragent "
                + "join cf_clients cl on o.idofclient = cl.idofclient and cl.idoforg not in (select friendlyorg from cf_friendly_organization where currentorg = org.idoforg) "
                + "join cf_orgs org2 on cl.idoforg = org2.idoforg "
                + "join cf_contragents c2 on org2.defaultsupplier = c2.idofcontragent "
                + "where o.CreatedDate between :startDate and :endDate and org.idoforg in (:orgs) "
                + "and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) "
                + "group by c.contragentName, org.shortNameInfoService, org.shortAddress, od.menuDetailName, od.rPrice, od.socdiscount, "
                + "org2.shortnameinfoservice, org2.shortAddress, c2.contragentName "
                + "order by c.contragentName, org.shortNameInfoService, org.shortAddress, od.menuDetailName";
        Query query = session.createSQLQuery(query_str);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("orgs", orgs);
        query.setParameter("fromMenuType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("toMenuType", OrderDetail.TYPE_COMPLEX_MAX);

        List res = query.list();
        for (Object o : res ) {
            Object[] row = (Object[]) o;
            String contragentName = (String) row[0];
            String shortNameInfoService = (String) row[1];
            String shortAddress = (String) row[2];
            String menuDetailName = (String) row[3];
            Long rprice = ((BigInteger) row[4]).longValue();
            Long qty = ((BigInteger) row[5]).longValue();
            Long sum = ((BigDecimal) row[6]).longValue();
            Long sumDiscount = ((BigDecimal) row[7]).longValue();
            String shortNameInfoServiceOrgClient = (String) row[8];
            String shortAddressOrgClient = (String) row[9];
            String contragentNameOrgClient = (String) row[10];
            PaymentsBetweenContragentsReportItem item = new PaymentsBetweenContragentsReportItem(contragentName, shortNameInfoService,
                    shortAddress, menuDetailName, rprice, qty, sum, sumDiscount, shortNameInfoServiceOrgClient, shortAddressOrgClient, contragentNameOrgClient);
            result_list.add(item);
        }

        return new JRBeanCollectionDataSource(result_list);
    }
}
