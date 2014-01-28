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
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

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
    protected List<ActiveDiscountClientsJasperItem> jasperItems;
    private Date startDate;
    private Date endDate;
    private String htmlReport;

    private static final String DISTRICT_NAME = "Район";
    private static final String ORG_NAME = "Организация";
    private static final String ADDRESS_NAME = "Адрес";
    private static final String POS_NAME = "№";
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
        if (items == null || items.size() < 1) {
            return (List) jasperItems;
        }
        return items;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }


    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;
        private boolean exportToHTML = false;
        private boolean exportToObjects = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + ActiveDiscountClientsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        public Builder setExportToObjects(boolean exportToObjects) {
            this.exportToObjects = exportToObjects;
            return this;
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
            if (exportToObjects) {
                return new ActiveDiscountClientsReport(startTime, endTime, items);
            }
            List<ActiveDiscountClientsJasperItem> jasperItems = splitItems(items);
            JasperPrint jasperPrint = JasperFillManager.fillReport
                    (templateFilename, parameterMap, createDataSource(jasperItems));
            if (!exportToHTML) {
                return new ActiveDiscountClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                                                       jasperPrint, startTime, endTime, jasperItems);
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
                return new ActiveDiscountClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, jasperItems).setHtmlReport(os.toString("UTF-8"));
            }
        }

        public List<ActiveDiscountClientsItem> findActiveDiscountClients(Session session, Date start, Date end) {
            List<ActiveDiscountClientsItem> result = new ArrayList<ActiveDiscountClientsItem>();
            String orgRestrict = "";
            if (org != null) {
                orgRestrict = " and cf_clients.idoforg=" + org.getIdOfOrg() + " ";
            }
            String sql =
                    "select cf_orgs.idoforg, cf_clients.idofclient, cf_orgs.district, cf_orgs.shortname, "
                    + "       cf_orgs.address, "
                    + "       cf_persons.surname, cf_persons.firstname, cf_persons.secondname, "
                    + "       cf_clientgroups.groupname, cf_categorydiscounts.categoryname, "
                    + "       cf_complexroles.rolename, sum(cf_orders.socdiscount / 100) "
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
                    + "left join cf_complexroles on cf_complexroles.idofrole=cf_orderdetails.menutype-" + OrderDetail.TYPE_COMPLEX_MIN + " "
                    + "where cf_clients.discountmode<>0 and cf_complexroles.rolename<>'' " + orgRestrict
                    + "group by cf_orgs.idoforg, cf_clients.idofclient, "
                    + "       cf_orgs.district, cf_orgs.shortname, cf_orgs.address, "
                    + "       cf_persons.surname, cf_persons.firstname, cf_persons.secondname, "
                    + "       cf_clientgroups.groupname, cf_categorydiscounts.categoryname, "
                    + "       cf_complexroles.rolename "
                    + "order by cf_orgs.district, cf_orgs.shortname, cf_clientgroups.groupname, "
                    + "         cf_persons.surname, cf_persons.firstname, cf_clients.idofclient";
            Query query = session.createSQLQuery(sql);
            query.setLong("startDate", start.getTime());
            query.setLong("endDate", end.getTime());
            List res = query.list();
            long prevIdOfClient = -1L;
            long prevIdOfOrg = -1L;
            int position = 1;
            long uniqueId = 0;
            String prevDistrict = null;
            ActiveDiscountClientsItem item = null;
            ActiveDiscountOrgItem orgItem = null;
            ActiveDiscountOrgItem districtItem = null;
            ActiveDiscountOrgItem overallItem = new ActiveDiscountOrgItem(Long.MAX_VALUE, null, null);
            long orgCount = 0;
            long districtCount = 0;
            long totalCount = 0;
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
                if (goodName == null || goodName.length() < 1) {
                    goodName = "- Без льготы -";
                }


                if (idoforg != prevIdOfOrg) {
                    if (!district.equals(prevDistrict)) {
                        if (districtItem != null) {
                            districtItem.setUniqueId(uniqueId);
                            districtItem.setPosition(districtCount);
                            result.add(districtItem);
                            uniqueId++;
                        }
                        districtItem = new ActiveDiscountOrgItem(uniqueId, district, null);
                    }

                    if (orgItem != null){
                        orgItem.setUniqueId(uniqueId);
                        orgItem.setPosition(districtCount);
                        result.add(orgItem);
                        uniqueId++;
                    }
                    orgItem = new ActiveDiscountOrgItem(uniqueId, null, name);
                    position = 1;
                }
                if (idofclient != prevIdOfClient) {
                    item = new ActiveDiscountClientsItem
                            (uniqueId, !district.equals(prevDistrict) ? district : "",
                             idoforg != prevIdOfOrg ? name : "",
                             idoforg != prevIdOfOrg ? address : "", surname,
                             firstname, secondname, groupName);
                    item.setPosition(position);
                    result.add(item);
                    uniqueId++;
                    position++;

                    orgCount++;
                    districtCount++;
                    totalCount++;
                }
                item.addCategory(categoryname);
                item.addGood(goodName, price);
                item.addTotal(price);

                orgItem.addCategoryClient(categoryname);
                orgItem.addGoodClient(goodName, price);
                orgItem.addTotal(price);
                districtItem.addCategoryClient(categoryname);
                districtItem.addGoodClient(goodName, price);
                districtItem.addTotal(price);
                overallItem.addCategoryClient(categoryname);
                overallItem.addGoodClient(goodName, price);
                overallItem.addTotal(price);

                prevIdOfClient = idofclient;
                prevIdOfOrg = idoforg;
                prevDistrict = district;
            }
            if (orgItem != null) {
                orgItem.setUniqueId(uniqueId);
                orgItem.setPosition(districtCount);
                result.add(orgItem);
                uniqueId++;
            }
            if (districtItem != null) {
                districtItem.setUniqueId(uniqueId);
                districtItem.setPosition(districtCount);
                result.add(districtItem);
            }
            overallItem.setPosition(totalCount);
            result.add(overallItem);


            //  Beautify items
            List<String> categories = new ArrayList<String>();
            List<String> goods = new ArrayList<String>();
            for (ActiveDiscountClientsItem i : result) {
                if (i instanceof ActiveDiscountOrgItem) {
                    continue;
                }
                for (String cat : i.getCategories().keySet()) {
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }
                for (String good : i.getGoods().keySet()) {
                    if (!goods.contains(good)) {
                        goods.add(good);
                    }
                }
            }
            for (ActiveDiscountClientsItem i : result) {
                i.beautifyCategories(categories);
                i.beautifyGoods(goods);
            }


            return result;
        }

        public boolean isExportToHTML() {
            return exportToHTML;
        }

        public void setExportToHTML(boolean exportToHTML) {
            this.exportToHTML = exportToHTML;
        }

        private JRDataSource createDataSource(List<ActiveDiscountClientsJasperItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

        private List<ActiveDiscountClientsJasperItem> splitItems(List<ActiveDiscountClientsItem> items) {
            List<ActiveDiscountClientsJasperItem> result = new ArrayList<ActiveDiscountClientsJasperItem>();
            for (ActiveDiscountClientsItem i : items) {
                long columnId = 0;

                ActiveDiscountOrgItem orgI = null;
                if (i instanceof ActiveDiscountOrgItem) {
                    orgI = (ActiveDiscountOrgItem) i;
                }
                
                Map cValues = orgI == null ? i.getCategories() : orgI.getCategoriesClients();
                columnId = addActiveDiscountClientsJasperItem(i, cValues, columnId, result);
                Map gValues = orgI == null ? i.getGoods() : orgI.getGoodsClients();
                columnId = addActiveDiscountClientsJasperItem(i, gValues, columnId, result);

                //  total
                ActiveDiscountClientsJasperItem ji = new ActiveDiscountClientsJasperItem
                        (i.getUniqueId(), columnId, i.getDistrict(), i.getName(),
                         i.getAddress(), i.getPosition(), i.getSurname(), i.getFirstname(),
                         i.getSecondname(), i.getGroupName(), "Итого", "" + i.getTotal());
                result.add(ji);
            }
            return result;
        }

        protected long addActiveDiscountClientsJasperItem(ActiveDiscountClientsItem item, Map data, long columnId,
                List<ActiveDiscountClientsJasperItem> result) {
            Set<String> keys = data.keySet();
            for(String k : keys) {
                if (k == null || k.length() < 1 || item.getUniqueId() == null) {
                    continue;
                }

                String val = "";
                try {
                    val = data.get(k).toString();
                } catch (Exception e) {

                }
                ActiveDiscountClientsJasperItem ji = new ActiveDiscountClientsJasperItem
                        (item.getUniqueId(), columnId, item.getDistrict(), item.getName(),
                                item.getAddress(), item.getPosition(), item.getSurname(), item.getFirstname(),
                                item.getSecondname(), item.getGroupName(), k, val == null ? "0" : "" + val);
                result.add(ji);
                columnId++;
            }
            return columnId;
        }
    }


    public ActiveDiscountClientsReport() {
        items = Collections.emptyList();
    }

    public ActiveDiscountClientsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<ActiveDiscountClientsJasperItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.jasperItems = items;
    }

    public ActiveDiscountClientsReport(Date startTime,
            Date endTime, List<ActiveDiscountClientsItem> items) {
        this.items = items;
    }

    public ActiveDiscountClientsReport(Date generateTime, long generateDuration, Date startTime,
                                       Date endTime, List<ActiveDiscountClientsJasperItem> items) {
        this.jasperItems = items;
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
            for (String cat : i.getCategories().keySet()) {
                if (!categoriesSet.contains(cat)) {
                    categoriesSet.add(cat);
                }
            }
            for (String good : i.getGoods().keySet()) {
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

    public static class ActiveDiscountClientsJasperItem extends ActiveDiscountClientsItem {
        protected Long columnId;
        protected String columnName;
        protected String columnValue;


        public ActiveDiscountClientsJasperItem(Long uniqueId, Long columnId, String district, String name, String address, 
                Long position, String surname, String firstname, String secondname, String groupName,
                String columnName, String columnValue) {
            this.uniqueId = uniqueId;
            this.columnId = columnId;
            this.district = district;
            this.name = name;
            this.address = address;
            this.position = position;
            this.surname = surname;
            this.firstname = firstname;
            this.secondname = secondname;
            this.groupName = groupName;
            this.columnName = columnName;
            this.columnValue = columnValue;
            this.categories = new HashMap<String, String>();
            this.goods = new HashMap<String, Long>();
            this.total = 0L;
        }

        public Long getColumnId() {
            return columnId;
        }

        public void setColumnId(Long columnId) {
            this.columnId = columnId;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }

    public static class ActiveDiscountOrgItem extends ActiveDiscountClientsItem {
        protected int totalClients;
        protected Map<String, Integer> categoriesClients;
        protected Map<String, Long> goodsClients;

        public ActiveDiscountOrgItem(Long uniqueId, String district, String name) {
            this.uniqueId = uniqueId;
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

        public Map<String, Integer> getCategoriesClients() {
            return categoriesClients;
        }

        public Map<String, Long> getGoodsClients() {
            return goodsClients;
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
                        return "" + position;
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
        protected Long uniqueId;
        protected String district;
        protected String name;
        protected String address;
        protected long position;
        protected String surname;
        protected String firstname;
        protected String secondname;
        protected String groupName;
        protected Map<String, String> categories;
        protected Map<String, Long> goods;
        protected Long total;


        protected String group = "hello";
        public String getGroup() {
            return group;
        }
        public void setGroup(String group) {
            this.group = group;
        }




        public ActiveDiscountClientsItem() {

        }

        public ActiveDiscountClientsItem(Long uniqueId, String district, String name, String address, String surname, String firstname,
                String secondname, String groupName) {
            this.uniqueId   = uniqueId;
            this.district   = district;
            this.name       = name;
            this.address    = address;
            this.surname    = surname;
            this.firstname  = firstname;
            this.secondname = secondname;
            this.groupName  = groupName;
            categories      = new TreeMap<String,String>();
            goods           = new TreeMap<String,Long>();
            total           = 0L;
        }

        public Long getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(Long uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public void addTotal(long total) {
            this.total += total;
        }

        public long getTotal() {
            return total;
        }

        public void setPosition(long position) {
            this.position = position;
        }

        public long getPosition() {
            return position;
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
            if (categories.containsKey(category)) {
                return;
            }
            categories.put(category, "X");
        }

        public Map<String, String> getCategories() {
            return categories;
        }

        public long getGood(String good) {
            if (goods.size() < 1) {
                return 0L;
            }
            return goods.get(good);
        }
        
        public Map<String, Long> getGoods() {
            return goods;
        }
        
        public List<String> getGoodNamesList() {
            List<String> res = new ArrayList<String>();
            res.addAll(goods.keySet());
            return res;
        }
        
        public Long getGoodValue(String good) {
            return goods.get(good);
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

        public void beautifyCategories(List<String> categories) {
            if (this.categories == null) {
                this.categories = new HashMap<String, String>();
            }
            for (String c : categories) {
                if (!this.categories.containsKey(c)) {
                    this.categories.put(c, "");
                }
            }
        }

        public void beautifyGoods(List<String> goods) {
            if (this.goods == null) {
                this.goods = new HashMap<String, Long>();
            }
            for (String c : goods) {
                if (!this.goods.containsKey(c)) {
                    this.goods.put(c, 0L);
                }
            }
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
                return categories.get(col.getName());
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
