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
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;

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
public class BasicBasketReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public BasicBasketReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "BasicBasketReport.jasper";

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

        return new BasicBasketReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        String idOfBBGoods = StringUtils.trimToEmpty(getReportProperties().getProperty("BBGoods"));
        List<String> stringBBGoodsList = Arrays.asList(StringUtils.split(idOfBBGoods, ','));
        List<Long> idOfBBGoodsList = new ArrayList<Long>(stringBBGoodsList.size());
        for (String idOfOrg : stringBBGoodsList) {
            idOfBBGoodsList.add(Long.parseLong(idOfOrg));
        }

        List<BasicBasketReportItem> result_list = new ArrayList<BasicBasketReportItem>();

        /*Query query = session.createSQLQuery("select b.nameofgood, b.unitsscale, b.netweight, p.nameofconfigurationprovider, od.menudetailname, od.rprice "
                + "from cf_orders o join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                + "join cf_menudetails md on od.idofmenufromsync = md.idofmenufromsync "
                + "join cf_menu m on m.idofmenu = md.idofmenu and m.idoforg = o.idoforg "
                + "join cf_good_basic_basket_price bp on bp.idofmenudetail = md.idofmenudetail "
                + "join cf_goods_basicbasket b on bp.idofbasicgood = b.idofbasicgood "
                + "join cf_provider_configurations p on p.idofconfigurationprovider = bp.idofconfigurationprovider "
                + "where o.CreatedDate between :startDate and :endDate and b.IdOfBasicGood in (:bbGoods) "
                + "order by b.NameOfGood");*/
        Query query = session.createSQLQuery("select b.nameofgood, b.unitsscale, b.netweight, p.nameofconfigurationprovider, bb.menudetailname, bb.price "
                + "from CF_Goods_BasicBasket b join cf_good_bb_menu_price bb on b.IdOfBasicGood = bb.idOfBasicGood "
                + "join cf_provider_configurations p on p.IdOfConfigurationProvider = bb.IdOfConfigurationProvider "
                + "where bb.menuDate between :startDate and :endDate and b.IdOfBasicGood in (:bbGoods) "
                + "order by b.NameOfGood");
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("bbGoods", idOfBBGoodsList);
        List res = query.list();
        for (Object o : res ) {
            Object[] row = (Object[]) o;
            String nameOfGood = (String) row[0];
            String unitsScale = UnitScale.fromInteger((Integer) row[1]).toString();
            Long netWeight = ((BigInteger) row[2]).longValue();
            String nameOfConfigurationProvider = (String) row[3];
            String menuDetailName = (String) row[4];
            Long rprice = ((BigInteger) row[5]).longValue();
            BasicBasketReportItem item = new BasicBasketReportItem(nameOfGood, unitsScale, netWeight,
                    nameOfConfigurationProvider, menuDetailName, rprice);
            result_list.add(item);
        }

        return new JRBeanCollectionDataSource(result_list);
    }
}
