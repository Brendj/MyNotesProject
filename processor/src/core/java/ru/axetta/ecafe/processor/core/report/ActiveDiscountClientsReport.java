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
import java.math.BigDecimal;
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

    private static final String DISTRICT_NAME = "Район";
    private static final String ORG_NAME = "Организация";
    private static final String ADDRESS_NAME = "Адрес";
    private static final String POS_NAME = "Порядковый номер (№ п.п.)";
    private static final String FIO_NAME = "ФИО";
    private static final String CLASS_NAME = "Класс";
    private static final String TOTAL_NAME = "Итого";
    private static final int DISTRICT_COL = 0;
    private static final int ORG_COL = 1;
    private static final int ADDRESS_COL = 2;
    private static final int POS_COL = 3;
    private static final int FIO_COL = 4;
    private static final int CLASS_COL = 5;
    private static final int TOTAL_COL = 6;
    private static final int CATEGORY_COL = 100;
    private static final int GOOD_COL = 101;
    private static final List <ReportColumn> DEFAULT_COLUMNS = new ArrayList <ReportColumn> ();
    static
    {
        DEFAULT_COLUMNS.add(new ReportColumn (DISTRICT_COL, DISTRICT_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (ORG_COL, ORG_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (ADDRESS_COL, ADDRESS_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (POS_COL, POS_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (FIO_COL, FIO_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (CLASS_COL, CLASS_NAME));
    }


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
            if (exportToHTML) {
                return new ActiveDiscountClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                                                       startTime, endTime, items);
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
                    "select cf_orgs.idoforg, cf_clients.idofclient, cf_orgs.district, cf_orgs.shortname,"
                    + "       cf_orgs.address, "
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
                    + "group by cf_orgs.idoforg, cf_clients.idofclient, "
                    + "       cf_orgs.district, cf_orgs.shortname, cf_orgs.address, "
                    + "       cf_persons.surname, cf_persons.firstname, cf_persons.secondname, "
                    + "       cf_clientgroups.groupname, cf_categorydiscounts.categoryname, "
                    + "       cf_goods.nameofgood "
                    + "order by cf_orgs.district, cf_orgs.shortname, cf_clientgroups.groupname, "
                    + "         cf_persons.surname, cf_persons.firstname, cf_clients.idofclient";
            Query query = session.createSQLQuery(sql);
            query.setLong("startDate", start.getTime());
            query.setLong("endDate", end.getTime());
            List res = query.list();
            long prevIdOfClient = -1L;
            long prevIdOfOrg = -1L;
            int position = 1;
            String prevDistrict = null;
            ActiveDiscountClientsItem item = null;
            ActiveDiscountOrgItem orgItem = null;
            ActiveDiscountOrgItem districtItem = null;
            ActiveDiscountOrgItem overallItem = new ActiveDiscountOrgItem(null, null);
            for (Object entry : res) {
                Object e[]          = (Object[]) entry;
                long idoforg        = ((BigInteger) e[0]).longValue();
                long idofclient     = ((BigInteger) e[1]).longValue();
                String district     = (String) e[2];
                String name         = (String) e[3];
                String address      = (String) e[4];
                String surname      = (String) e[5];
                String firstname    = (String) e[6];
                String secondname   = (String) e[7];
                String groupName    = (String) e[8];
                String categoryname = (String) e[9];
                String goodName     = (String) e[10];
                long price          = 0L;
                if (e[11] != null) {
                    price = ((BigDecimal) e[11]).longValue();
                }


                if (idoforg != prevIdOfOrg) {
                    if (!district.equals(prevDistrict)) {
                        if (districtItem != null) {
                            result.add(districtItem);
                        }
                        districtItem = new ActiveDiscountOrgItem(district, null);
                    }

                    if (orgItem != null){
                        result.add(orgItem);
                    }
                    orgItem = new ActiveDiscountOrgItem(null, name);
                    position = 1;
                }
                if (idofclient != prevIdOfClient) {
                    item = new ActiveDiscountClientsItem
                            (district, name, address, surname,
                            firstname, secondname, groupName);
                    item.setPosition(position);
                    result.add(item);
                    position++;
                }
                item.addCategory(categoryname);
                item.addGood(goodName, price);
                item.addTotal(price);

                orgItem.addCategoryClient(categoryname);
                orgItem.addGoodClient(categoryname, price);
                orgItem.addTotal(price);
                districtItem.addCategoryClient(categoryname);
                districtItem.addGoodClient(categoryname, price);
                districtItem.addTotal(price);
                overallItem.addCategoryClient(categoryname);
                overallItem.addGoodClient(categoryname, price);
                overallItem.addTotal(price);

                prevIdOfOrg = idoforg;
                prevDistrict = district;
            }
            if (orgItem != null) {
                result.add(orgItem);
            }
            if (districtItem != null) {
                result.add(districtItem);
            }
            result.add(overallItem);
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
        items = Collections.emptyList();
    }


    public ActiveDiscountClientsReport(Date generateTime, long generateDuration, Date startTime,
                                       Date endTime, List<ActiveDiscountClientsItem> items) {
        this.items = items;
    }

    private List <ReportColumn> cols;
    public Object[] getColumnNames () {
        if (cols != null) {
            cols.clear();
        } else {
            cols = new ArrayList<ReportColumn>();
        }
        for (ReportColumn c : DEFAULT_COLUMNS) {
            cols.add(c);
        }

        if (items == null || items.size() < 1) {
            return cols.toArray();
        }
        Set<String> categoriesSet = new TreeSet<String>();
        Set<String> goodsSet = new TreeSet<String>();
        for (ActiveDiscountClientsItem i : items) {
            if (i instanceof ActiveDiscountOrgItem) {
                continue;
            }
            for (String cat : i.getCategories()) {
                if (!categoriesSet.contains(cat)) {
                    categoriesSet.add(cat);
                }
            }
            for (String good : i.getGoods()) {
                if (!goodsSet.contains(good)) {
                    goodsSet.add(good);
                }
            }
        }
        for (String cat : categoriesSet) {
            cols.add(new ReportColumn(CATEGORY_COL, cat));
        }
        for (String good : goodsSet) {
            cols.add(new ReportColumn(GOOD_COL, good));
        }
        cols.add(new ReportColumn(TOTAL_COL, TOTAL_NAME));
        return cols.toArray();
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

    public static class ActiveDiscountOrgItem extends ActiveDiscountClientsItem {
        protected int totalClients;
        protected Map<String, Integer> categoriesClients;
        protected Map<String, Long> goodsClients;

        public ActiveDiscountOrgItem(String district, String name) {
            if (district != null && district.length() > 0) {
                this.district = "Итого по " + district;
                this.name = "";
            } else if (name != null && name.length() > 0) {
                this.district = "";
                this.name = "Итого по " + name;
            } else {
                this.district = "ИТОГО";
                this.name = "";
            }
            this.address    = "";
            this.surname    = "";
            this.firstname  = "";
            this.secondname = "";
            this.groupName  = "";
            categoriesClients      = new HashMap<String, Integer>();
            goodsClients           = new HashMap<String, Long>();
            total           = 0L;
        }

        public void addCategoryClient(String category) {
            Integer cc = categoriesClients.get(category);
            if (cc == null) {
                cc = 0;
            }
            categoriesClients.put(category, cc + 1);
        }

        public void addGoodClient(String category, long price) {
            Long gc = goodsClients.get(category);
            if (gc == null) {
                gc = 0L;
            }
            goodsClients.put(category, gc + price);
        }

        @Override
        public String getRowValue(Object columnObj) {
            ReportColumn col = (ReportColumn) columnObj;
            if (col.getType() != CATEGORY_COL && col.getType() != GOOD_COL) {
                switch (col.getType()) {
                    case DISTRICT_COL:
                        return district;
                    case ORG_COL:
                        return name;
                    case ADDRESS_COL:
                        return "";
                    case POS_COL:
                        return "";
                    case FIO_COL:
                        return "";
                    case CLASS_COL:
                        return "";
                    case TOTAL_COL:
                        return "" + total;
                }
            } else if (col.getType() == CATEGORY_COL) {
                Integer val = categoriesClients.get(col.getName());
                if (val == null) {
                    return "0";
                }
                return "" + val;
            } else if (col.getType() == GOOD_COL) {
                Long val = goodsClients.get(col.getName());
                if (val == null) {
                    return "0";
                }
                return "" + val;
            }
            return "";
        }
    }

    public static class ActiveDiscountClientsItem {
        protected String district;
        protected String name;
        protected String address;
        protected int position;
        protected String surname;
        protected String firstname;
        protected String secondname;
        protected String groupName;
        protected List<String> categories;
        protected Map<String, Long> goods;
        protected Long total;

        public ActiveDiscountClientsItem() {

        }

        public ActiveDiscountClientsItem(String district, String name, String address, String surname, String firstname,
                String secondname, String groupName) {
            this.district   = district;
            this.name       = name;
            this.address    = address;
            this.surname    = surname;
            this.firstname  = firstname;
            this.secondname = secondname;
            this.groupName  = groupName;
            categories      = new ArrayList<String>();
            goods           = new HashMap<String,Long>();
            total           = 0L;
        }

        public void addTotal(long total) {
            this.total += total;
        }

        public long getTotal() {
            return total;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void addGood(String good, long price) {
            if (good == null || price < 0) {
                return;
            }
            Long current = goods.get(good);
            if (current == null) {
                current = 0L;
            }
            goods.put(good, current + price);
        }

        public void addCategory(String category) {
            if (categories.contains(category)) {
                return;
            }
            categories.add(category);
        }

        public List<String> getCategories() {
            return categories;
        }

        public long getGood(String good) {
            if (goods.size() < 1) {
                return 0L;
            }
            return goods.get(good);
        }
        
        public Set<String> getGoods() {
            return goods.keySet();
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

        public String getRowValue(Object columnObj) {
            ReportColumn col = (ReportColumn) columnObj;
            if (col.getType() != CATEGORY_COL && col.getType() != GOOD_COL) {
                switch (col.getType()) {
                    case DISTRICT_COL:
                        return district;
                    case ORG_COL:
                        return name;
                    case ADDRESS_COL:
                        return address;
                    case POS_COL:
                        return "" + position;
                    case FIO_COL:
                        return surname + " " + firstname + " " + secondname;
                    case CLASS_COL:
                        return groupName;
                    case TOTAL_COL:
                        return "" + total;
                }
            } else if (col.getType() == CATEGORY_COL) {
                if (categories.contains(col.getName())) {
                    return "X";
                }
                return "";
            } else if (col.getType() == GOOD_COL) {
                Long val = goods.get(col.getName());
                if (val == null) {
                    return "";
                }
                return "" + val;
            }
            return "";
        }
    }

    public static class ReportColumn {
        private String name;
        private int type;

        public ReportColumn (int type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getName () {
            return name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
