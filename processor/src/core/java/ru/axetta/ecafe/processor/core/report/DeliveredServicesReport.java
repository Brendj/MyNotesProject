/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampItem;
import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 06.05.13
 * Time: 13:37
 * Онлайн отчеты -> Льготное питание -> Отчет по предоставленным услугам
 */
public class DeliveredServicesReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(DeliveredServicesReport.class);

    private List<DeliveredServicesItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;

    private static final String ORG_NUM = "Номер ОУ";
    private static final String ORG_NAME = "Наименование ОУ";
    private static final String GOOD_NAME = "Товар";
    private static final List<String> DEFAULT_COLUMNS = new ArrayList<String>();

    static {
        DEFAULT_COLUMNS.add(ORG_NUM);
        DEFAULT_COLUMNS.add(ORG_NAME);
        DEFAULT_COLUMNS.add(GOOD_NAME);
    }


    public List<DeliveredServicesItem> getItems() {
        return items;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }


    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + DeliveredServicesReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public DeliveredServicesReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Long idOfContragent = null;
            Long idOfContract = null;
            if (reportProperties.getProperty("idOfContract") != null &&
                reportProperties.getProperty("idOfContract").length() > 0) {
                try {
                    idOfContract = Long.parseLong(reportProperties.getProperty("idOfContract"));
                } catch (Exception e) {
                    idOfContract = null;
                }
            }
            if (contragent != null) {
                idOfContragent = contragent.getIdOfContragent();
            }

            return build(session, startTime, endTime, calendar, idOfContragent, idOfContract);
        }

        public DeliveredServicesReport build(Session session, Date startTime, Date endTime, Calendar calendar,
                Long contragent, Long contract) throws Exception {
            Date generateTime = new Date();


            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);


            Date generateEndTime = new Date();
            List<DeliveredServicesItem> items = findNotNullGoodsFullNameByOrg(session, startTime, endTime, contragent,
                    contract);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap, items));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new DeliveredServicesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, null);
            } else {
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
                return new DeliveredServicesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<DeliveredServicesItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }


        public List<DeliveredServicesItem> findNotNullGoodsFullNameByOrg(Session session, Date start, Date end,
                Long contragent, Long contract) {
            String contragentCondition = "";
            if (contragent != null) {
                contragentCondition = "(cf_orgs.defaultsupplier=" + contragent + ") AND ";
            }

            String contractOrgsCondition = "";
            if (contract != null) {
                //  Вытаскиваем те оргии, которые привязаны к контракту и устанавливаем их как ограничения. !Будет заменено!
                Query query = session.createSQLQuery("select idoforg from cf_orgs where idofcontract=:contract");//.createQuery(sql);
                query.setParameter("contract", contract);
                List res = query.list();
                for (Object entry : res) {
                    Long org = ((BigInteger) entry).longValue();
                    if (contractOrgsCondition.length() > 0) {
                        contractOrgsCondition = contractOrgsCondition.concat(", ");
                    }
                    contractOrgsCondition = contractOrgsCondition.concat("" + org);
                }

                //  Берем даты начала и окончания контракта, если они выходят за рамки выбранных пользователем дат, то
                //  ограничиваем временные рамки
                Criteria contractCriteria = session.createCriteria(Contract.class);
                contractCriteria.add(Restrictions.eq("idOfContract", contract));
                Contract c = (Contract) contractCriteria.uniqueResult();
                if (c.getDateOfConclusion().getTime() > start.getTime()) {
                    start.setTime(c.getDateOfConclusion().getTime());
                }
                if (c.getDateOfClosing().getTime() < end.getTime()) {
                    end.setTime(c.getDateOfClosing().getTime());
                }
            }
            if (contractOrgsCondition.length() > 0) {
                contractOrgsCondition = " cf_orgs.idoforg in (" + contractOrgsCondition + ") and ";
            }
            String orgCondition = "";
            if (org != null) {
                orgCondition = " cf_orgs.idoforg=" + org.getIdOfOrg() + " and ";
            }


            //String typeCondition = " cf_orders.ordertype<>8 and ";
            String typeCondition = " (cf_orders.ordertype in (0,1,4,5,6)) and " +
                                   " cf_orderdetails.menutype>=:mintype and cf_orderdetails.menutype<=:maxtype and ";
            String sql = "select cf_orgs.officialname, " + "split_part(cf_goods.fullname, '/', 1) as level1, "
                    + "split_part(cf_goods.fullname, '/', 2) as level2, "
                    + "split_part(cf_goods.fullname, '/', 3) as level3, "
                    + "split_part(cf_goods.fullname, '/', 4) as level4, " + "count(cf_orders.idoforder) as cnt, "
                    + "(cf_orderdetails.rprice + cf_orderdetails.socdiscount) price, "
                    + "count(cf_orders.idoforder) * (cf_orderdetails.rprice + cf_orderdetails.socdiscount) as sum, "
                    + "cf_orgs.address, " + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), cf_orgs.idoforg "
                    + "from cf_orgs " + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                    + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                    + "join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                    + "where cf_orderdetails.socdiscount>0 and cf_orders.state=0 and cf_orderdetails.state=0 and "
                    + typeCondition + contragentCondition + contractOrgsCondition + orgCondition
                    + " cf_orders.createddate between :start and :end  "
                    + "group by cf_orgs.idoforg, cf_orgs.officialname, level1, level2, level3, level4, price, address "
                    + "order by cf_orgs.idoforg, cf_orgs.officialname, level1, level2, level3, level4";
            Query query = session.createSQLQuery(sql);//.createQuery(sql);
            query.setParameter("start", start.getTime());
            //query.setParameter("start",1357171200000L);
            query.setParameter("end", end.getTime());
            query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);

            List<DeliveredServicesItem> result = new ArrayList<DeliveredServicesItem>();
            List res = query.list();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                String officialname = (String) e[0];
                String level1 = (String) e[1];
                String level2 = (String) e[2];
                String level3 = (String) e[3];
                String level4 = (String) e[4];
                int count = ((BigInteger) e[5]).intValue();
                long price = ((BigInteger) e[6]).longValue();
                long summary = ((BigInteger) e[7]).longValue();
                String address = (String) e[8];
                String orgNum = (e[9] == null ? "" : (String) e[9]);
                long idoforg = ((BigInteger) e[10]).longValue();
                DeliveredServicesItem item = new DeliveredServicesItem();
                item.setOfficialname(officialname);
                item.setLevel1(level1);
                item.setLevel2(level2);
                item.setLevel3(level3);
                item.setLevel4(level4);
                item.setCount(count);
                item.setPrice(price);
                item.setSummary(summary);
                item.setOrgnum(orgNum);
                item.setAddress(address);
                item.setIdoforg(idoforg);
                result.add(item);
            }
            return result;
        }
    }


    public DeliveredServicesReport() {
    }


    public DeliveredServicesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<DeliveredServicesItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public DeliveredServicesReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public DeliveredServicesReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<DeliveredServicesItem> items) {
        this.items = items;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new DeliveredServicesReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public class JasperStringOutputStream extends OutputStream {

        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b);
        }

        //Netbeans IDE automatically overrides this toString()
        public String toString() {
            return this.string.toString();
        }
    }
}
