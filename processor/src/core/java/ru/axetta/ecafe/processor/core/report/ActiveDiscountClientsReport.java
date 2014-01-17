/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
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

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 17.01.14
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class ActiveDiscountClientsReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(ActiveDiscountClientsReport.class);

    private List<ActiveDiscountClientsItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;


    public List<ActiveDiscountClientsItem> getItems() {
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
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + ActiveClientsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public ActiveDiscountClientsReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
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
            List<ActiveDiscountClientsItem> items = findActiveDiscountClients(session, startTime, endTime);
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new ActiveDiscountClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                                                       startTime, endTime, null);
            } else {
                JasperPrint jasperPrint = JasperFillManager
                        .fillReport(templateFilename, parameterMap, createDataSource(items));
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
                return new ActiveDiscountClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        public List<ActiveDiscountClientsItem> findActiveDiscountClients(Session session, Date start, Date end) {
            List<ActiveDiscountClientsItem> result = new ArrayList<ActiveDiscountClientsItem>();
            String orgRestrict = "";
            if (org != null) {
                orgRestrict = " and cf_clients.idoforg=" + org.getIdOfOrg() + " ";
            }
            String sql =
                    "select cf_orgs.district, cf_orgs.shortname, cf_orgs.address, "
                    + "       cf_persons.surname, cf_persons.firstname, cf_persons.secondname, "
                    + "       cf_clientgroups.groupname, cf_categorydiscounts.categoryname, "
                    + "       cf_goods.nameofgood, sum(cf_orders.socdiscount) "
                    + "from cf_clients "
                    + "join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
                    + "join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                    + "join cf_clientgroups on cf_clientgroups.idoforg=cf_clients.idoforg and cf_clientgroups.idofclientgroup=cf_clients.idofclientgroup "
                    + "join cf_clients_categorydiscounts on cf_clients.idofclient=cf_clients_categorydiscounts.idofclient "
                    + "join cf_categorydiscounts on cf_clients_categorydiscounts.idofcategorydiscount=cf_categorydiscounts.idofcategorydiscount "
                    + "left join cf_orders on cf_orders.idofclient=cf_clients.idofclient and cf_orders.idoforg=cf_clients.idoforg and cf_orders.socdiscount<>0 and "
                    + "                       cf_orders.createddate >= :startDate and "
                    + "                       cf_orders.createddate <= :endDate "
                    + "left join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                    + "left join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                    + "where cf_clients.discountmode<>0 " + orgRestrict
                    + "group by cf_orgs.district, cf_orgs.shortname, cf_orgs.address, "
                    + "       cf_persons.surname, cf_persons.firstname, cf_persons.secondname, "
                    + "       cf_clientgroups.groupname, cf_categorydiscounts.categoryname, "
                    + "       cf_goods.nameofgood "
                    + "order by cf_orgs.district, cf_orgs.shortname, cf_clientgroups.groupname, cf_persons.surname, cf_persons.firstname";
            Query query = session.createSQLQuery(sql);
            query.setLong("startDate", start.getTime());
            query.setLong("endDate", end.getTime());
            List res = query.list();
            for (Object entry : res) {
                Object e[]          = (Object[]) entry;
                String district     = (String) e[0];
                String name         = (String) e[1];
                String address      = (String) e[2];
                String surname      = (String) e[3];
                String firstname    = (String) e[4];
                String secondname   = (String) e[5];
                String groupName    = (String) e[6];
                String categoryname = (String) e[7];
                String goodName     = (String) e[8];
                long price          = ((BigInteger) e[9]).longValue();

                ActiveDiscountClientsItem item = new ActiveDiscountClientsItem(district, name, address, surname,
                                                                               firstname, secondname, groupName,
                                                                               categoryname, goodName, price);
                result.add(item);
            }
            return result;
        }

        public boolean isExportToHTML() {
            return exportToHTML;
        }

        public void setExportToHTML(boolean exportToHTML) {
            this.exportToHTML = exportToHTML;
        }

        private JRDataSource createDataSource(List<ActiveDiscountClientsItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }
    }


    public ActiveDiscountClientsReport() {
    }


    public ActiveDiscountClientsReport(Date generateTime, long generateDuration, Date startTime,
                                       Date endTime, List<ActiveDiscountClientsItem> items) {
        this.items = items;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ActiveDiscountClientsReport();  //To change body of implemented methods use File | Settings | File Templates.
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

    public String getHtmlReport() {
        return htmlReport;
    }

    public ActiveDiscountClientsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public static class ActiveDiscountClientsItem {
        protected String district;
        protected String name;
        protected String address;
        protected String surname;
        protected String firstname;
        protected String secondname;
        protected String groupName;
        protected String categoryname;
        protected String goodName;
        protected long price;

        public ActiveDiscountClientsItem(String district, String name, String address, String surname, String firstname,
                String secondname, String groupName, String categoryname, String goodName, long price) {
            this.district = district;
            this.name = name;
            this.address = address;
            this.surname = surname;
            this.firstname = firstname;
            this.secondname = secondname;
            this.groupName = groupName;
            this.categoryname = categoryname;
            this.goodName = goodName;
            this.price = price;
        }
        
        public String getFullName() {
            return surname + " " + firstname + " " + secondname;
        }

        public String getDistrict() {
            return district;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getSurname() {
            return surname;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getSecondname() {
            return secondname;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public String getGoodName() {
            return goodName;
        }

        public long getPrice() {
            return price;
        }
    }
}
