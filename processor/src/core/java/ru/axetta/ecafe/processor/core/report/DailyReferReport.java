/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 16.12.13
 * Time: 17:32
 * РАЗБИВКА ОТЧЕТА ПО БЕСПЛАТНОМУ ПИТАНИЮ
 */
public class DailyReferReport extends BasicReportForAllOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Справка по предоставлению бесплатного питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"DailyReferReport_byAll.jasper", "DailyReferReport_byCat.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{-3, -33};


    private final static Logger logger = LoggerFactory.getLogger(DailyReferReport.class);

    private List<DailyReferReportItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static final String TEMPLATE_FILE_NAME_JASPER_PART = ".jasper";
    public static final String SUBCATEGORY_PARAMETER = "category";
    public static final String SHOW_DAILY_SALES_PARAMETER = "dailySales";
    public static final String OVERALL_SUBCATEGORY_NAME = "Сведения о рационах питания, получеченных в отчетном периоде";
    //  Фильтры категорий
    public static final CategoryFilter SUBCATEGORY_ALL = new CategoryFilter("Все");
    public static final CategoryFilter SUBCATEGORY_SHOOL [] = new CategoryFilter[] {
            new CategoryFilter("Обучающиеся 1-4 кл.", " and cf_discountrules.subcategory like '%1-4 кл.%' "),
            new CategoryFilter("Обучающиеся 5-9 кл.", " and cf_discountrules.subcategory like '%5-9 кл.%' "),
            new CategoryFilter("Обучающиеся 10-11 кл.", " and cf_discountrules.subcategory like '%10-11 кл.%' ") };
    public static final CategoryFilter SUBCATEGORY_KINDERGARTEN [] = new CategoryFilter [] {
            new CategoryFilter("Ясли 1,5-3 лет", " and cf_discountrules.subcategory like '%1,5-3 лет %' "),
            new CategoryFilter("Детский сад 3-7 лет", " and cf_discountrules.subcategory like '%3-7 лет %' ") };
    public static Map<String, String> GROUP1_REPLACEMENTS;
    static {
        GROUP1_REPLACEMENTS = new HashMap<String, String>();
        GROUP1_REPLACEMENTS.put("Обучающиеся 1-4 классов", "Обучающиеся 1-4 классов"/*"Обучающиеся, не получающие питание по льготам"*/);
    }


    public List<DailyReferReportItem> getItems() {
        return items;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;
        private double totalSumm;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String templateName = DailyReferReport.class.getSimpleName();
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName + ".jasper";
            exportToHTML = true;
        }

        @Override
        public DailyReferReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            /*Long idOfContragent = null;
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
            }*/

            return doBuild(session, startTime, endTime, calendar);
        }

        public DailyReferReport doBuild(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            if(startTime == null || endTime == null) {
                throw new IllegalArgumentException("Не задан период");
            }
            totalSumm = 0D;
            Date generateTime = new Date();

            String category = null;
            try {
                category = (String) reportProperties.get(SUBCATEGORY_PARAMETER);
                if (category.equals(SUBCATEGORY_ALL)) {
                    category = "";
                }
            } catch (Exception e) {
                category = null;
            }
            boolean showDailySample = false;
            try {
                showDailySample = Boolean.parseBoolean(reportProperties.get(SHOW_DAILY_SALES_PARAMETER).toString());
            } catch (Exception e) {
                showDailySample = false;
            }
            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("startDate", dailyItemsFormat.format(startTime));
            parameterMap.put("endDate", dailyItemsFormat.format(endTime));
            Object o = reportProperties.getProperty("region");
            if(org == null && o == null) {
                throw new IllegalArgumentException("Не указана организация или регион");
            }
            parameterMap.put("orgName", org == null ? (String) o : org.getShortName());
            CategoryFilter categoryFilter = getCategoryFilter(category);
            if(!categoryFilter.equals(SUBCATEGORY_ALL)) {
                parameterMap.put("category", categoryFilter.getName());
            }
            Org orgObj = null;
            if(org != null) {
                orgObj = DAOReadonlyService.getInstance().findOrById(org.getIdOfOrg());
                String totalCaption = "Общая стоимость за 1-11 классы, руб.";
                if(orgObj.getType().equals(OrganizationType.KINDERGARTEN)) {
                    totalCaption = "Общая стоимость, руб.";
                }
                parameterMap.put("totalCaption", totalCaption);
            }


            Date generateEndTime = new Date();
            List<DailyReferReportItem> items = findDailyReferItems(session, startTime, endTime, categoryFilter,
                    o != null ? (String) o : null, orgObj);//getDailyReferItems(session);//
            if(!categoryFilter.equals(SUBCATEGORY_ALL)) {
                addDailyTotalItem(items);
            }
            if(showDailySample) {
                addSamples(session, org, startTime, endTime, items, categoryFilter, o != null ? (String) o : null, orgObj);
                calculateOverall(items, isOverallReport(categoryFilter), orgObj);
            }
            //  После получения всего списка, передаем итоговую сумму в кач-ве параметра
            parameterMap.put("totalSum", totalSumm);

            String actualTemplateFilename = resolveTemplateFilename(templateFilename, categoryFilter);
            JasperPrint jasperPrint = JasperFillManager.fillReport(actualTemplateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap, items));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new DailyReferReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
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
                return new DailyReferReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        protected String resolveTemplateFilename(String templateFilename, CategoryFilter categoryFilter) {
            String base = templateFilename.substring(0, templateFilename.lastIndexOf(TEMPLATE_FILE_NAME_JASPER_PART));
            if(categoryFilter.equals(SUBCATEGORY_ALL)) {
                return base + "_byAll" + TEMPLATE_FILE_NAME_JASPER_PART;
            } else {
                return base + "_byCat" + TEMPLATE_FILE_NAME_JASPER_PART;
            }
        }

        protected void calculateOverall(List<DailyReferReportItem> items, boolean isOverallReport, Org orgObj) {
            Map<String, Set<String>> groups = new HashMap<String, Set<String>>();
            for(DailyReferReportItem i : items) {
                String g1 = i.getGroup1();
                String g2 = i.getGroup2();

                Set<String> groups2 = groups.get(g1);
                if(groups2 == null) {
                    groups2 = new TreeSet<String>();
                    groups.put(g1, groups2);
                }
                groups2.add(g2);
            }


            List<DailyReferReportItem> newItems = new ArrayList<DailyReferReportItem>();
            for(String g1 : groups.keySet()) {
                Set<String> groups2 = groups.get(g1);
                for(String g2 : groups2) {
                    Set<DailyReferReportItem> targetItems = findItemsByGoups(items, g1, g2);
                    long children = 0L;
                    double summary = 0D;
                    for(DailyReferReportItem i : targetItems) {
                        children += i.getChildren();
                        summary += i.getSummary();
                    }

                    String name = g1 + " (" + g2 + ")";
                    DailyReferReportItem newI = new DailyReferReportItem("ВСЕГО", name, name,
                                                                         children, 0, summary,
                                                                          isOverallReport, orgObj.getType());
                    newI.setGroup1(g1);
                    newI.setGroup2(g2);
                    newI.setIndex(3);
                    newItems.add(newI);
                }
            }
            items.addAll(newItems);
        }

        protected Set<DailyReferReportItem> findItemsByGoups(List<DailyReferReportItem> items, String g1, String g2) {
            Set<DailyReferReportItem> res = null;
            for(DailyReferReportItem i : items) {
                if(i.getGroup1().equals(g1) && i.getGroup2().equals(g2) && i.getIndex() > 0) {
                    if(res == null) {
                        res = new HashSet<DailyReferReportItem>();
                    }
                    res.add(i);
                }
            }

            if(res == null) {
                return Collections.EMPTY_SET;
            } else {
                return res;
            }
        }

        protected boolean isOverallReport(CategoryFilter categoryFilter) {
            if(categoryFilter.equals(SUBCATEGORY_ALL)) {
                return true;
            }
            return false;
        }

        protected DailyReferReportItem[] addSamples(Session session, OrgShortItem org,
                                                    Date startTime, Date endTime,
                                                    List<DailyReferReportItem> items,
                                                    CategoryFilter categoryFilter, String region, Org orgObj) {
            boolean isOverallReport = isOverallReport(categoryFilter);
            Set<String> groups = new HashSet<String>();
            for(DailyReferReportItem i : items) {
                String grp = "";
                if(isOverallReport) {
                    grp = i.getGroup2() + " " + i.getGroup1();
                } else {
                    grp = i.getGroup2();
                }
                groups.add(grp);
            }
            ReferReport.DailyReferReportItem samples [] = ReferReport.Builder.getSampleItems(session, org, startTime, endTime, groups, isOverallReport, region);

            String name = categoryFilter.getName();
            if (categoryFilter.equals(SUBCATEGORY_ALL)) {
                name = OVERALL_SUBCATEGORY_NAME;
            }
            for(ReferReport.DailyReferReportItem it : samples) {
                DailyReferReportItem item = null;
                if(isOverallReport) {
                    item = new DailyReferReportItem("СУТОЧНАЯ ПРОБА", it.getName(), it.getName(),
                            it.getChildren(), it.getPrice(), it.getSummary(), isOverallReport, orgObj.getType());
                    item.setIndex(2);
                } else {
                    item = new DailyReferReportItem("СУТОЧНАЯ ПРОБА", name, it.getGoodName(), it.getChildren(),
                                                    it.getPrice(), it.getSummary(), isOverallReport, orgObj.getType());
                    item.setIndex(2);
                }
                /*if(category == null || category.length() < 1) {
                    if(items != null && items.size() > 0) {
                        item.setGroup1(items.get(0).getGroup1());
                    }
                }*/
                items.add(item);

            }
            return null;
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<DailyReferReportItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

        private List<DailyReferReportItem> getDailyReferItems(Session session) {
            List<List<ReferReportItem>> result = new ArrayList<List<ReferReportItem>>();

            Query q = session.createSQLQuery(
                    "");
            List sqlRes = q.list();

            int i=1;
            List<ReferReportItem> items = new ArrayList<ReferReportItem>();
            if(sqlRes != null && sqlRes.size() > 0) {
                for (Object entry : sqlRes) {
                    Object e[] = (Object[]) entry;
                    long idoforg = ((BigInteger) e[0]).longValue();
                    String category = (String) e[1];
                    long clientsCount = ((BigInteger) e[2]).longValue();
                    double eventsCount = ((BigInteger) e[3]).longValue();
                    double ordersSummary = ((BigDecimal) e[4]).doubleValue();

                    i++;
                }
            }
            result.add(items);
            result.add(new ArrayList<ReferReportItem>());
            return null;
        }

        protected void addDailyTotalItem(List<DailyReferReportItem> items) {
            List<DailyReferReportItem> res = new ArrayList<DailyReferReportItem>();
            for(DailyReferReportItem i : items) {
                int index        = i.getIndex();
                String day       = i.getDay();
                String group0    = i.getGroup0();
                String group1    = "Итого";//i.getGroup1();
                String group2    = i.getGroup2();
                double breakfast = i.getBreakfast();
                double lunch     = i.getLunch();
                long children    = i.getChildren();
                double summary   = i.getSummary();
                long total       = i.getTotal();

                DailyReferReportItem totalI = null;
                for(DailyReferReportItem ii : res) {
                    if (ii.getGroup2() != null) {
                        if (ii.getDay().equals(day) && ii.getGroup0().equals(group0) && ii.getGroup1().equals(group1)
                                && ii.getGroup2().equals(group2)) {
                            totalI = ii;
                            break;
                        }
                    }
                }
                if(totalI == null) {
                    totalI = new DailyReferReportItem();
                    totalI.setIndex(index);
                    totalI.setDay(day);
                    totalI.setGroup0(group0);
                    totalI.setGroup1(group1);
                    totalI.setGroup1Index("ЯЯЯ");
                    totalI.setGroup2(group2);
                    totalI.setBreakfast(breakfast);
                    totalI.setLunch(lunch);
                    res.add(totalI);
                }
                totalI.setChildren(totalI.getChildren() + children);
                totalI.setSummary(totalI.getSummary() + summary);
                totalI.setTotal(totalI.getTotal() + total);
            }
            items.addAll(res);
        }

        private List<DailyReferReportItem> findDailyReferItems(Session session, Date startTime, Date endTime,
                                                               CategoryFilter categoryFilter, String region, Org orgObj) {
            String categoryClause = "";
            if (categoryFilter != null) {
                //categoryClause = " and cf_discountrules.subcategory = '" + category + "' ";
                categoryClause = categoryFilter.getSqlCase();
            } else {
                //categoryClause = " and cf_discountrules.subcategory <> '' ";
            }

            Map<String, DailyReferReportItem> totals = null;
            if(!categoryFilter.equals(SUBCATEGORY_ALL)) {
                totals = new HashMap<String, DailyReferReportItem>();
            }
            List<DailyReferReportItem> result = new ArrayList<DailyReferReportItem>();
            List res = getReportData(session, orgObj == null ? null : orgObj.getIdOfOrg(), startTime.getTime(), endTime.getTime(),
                                     categoryClause, region);
            for (Object entry : res) {
                Object e[]            = (Object[]) entry;
                String name           = (String) e[0];
                String goodname       = (String) e[1];
                long ts               = ((BigInteger) e[2]).longValue();
                BigDecimal priceObj   = e[3] == null ? new BigDecimal(0D) : (BigDecimal) e[3];
                priceObj              = priceObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                long children         = ((BigInteger) e[4]).longValue();
                BigDecimal summaryObj = e[5] == null ? new BigDecimal(0D) : (BigDecimal) e[5];
                summaryObj            = summaryObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                double breakfast      = 50;
                double lunch          = 100;

                boolean isOverallReport = isOverallReport(categoryFilter);
                DailyReferReportItem item = new DailyReferReportItem(ts, name, goodname, children,
                                                                     priceObj.doubleValue(), summaryObj.doubleValue(),
                                                                     isOverallReport, orgObj.getType(), breakfast, lunch);
                totalSumm             += summaryObj.doubleValue();
                result.add(item);

                //  Обновляем тотал объект для питания
                if(totals != null) {
                    DailyReferReportItem total = totals.get(name + " / " + item.getGroup2());
                    if(total == null) {
                        total = new DailyReferReportItem("Итого", name, goodname, 0, 0D, 0D,
                                                         isOverallReport, orgObj.getType());
                        total.setPrice(item.getPrice());
                        totals.put(name + " / " + item.getGroup2(), total);
                        total.setIndex(1);
                    }
                    total.setChildren(total.getChildren() + item.getChildren());
                    total.setTotal(total.getTotal() + item.getTotal());
                    total.setSummary(total.getSummary() + item.getSummary());
                }
            }
            if(totals != null) {
                for(String k : totals.keySet()) {
                    result.add(totals.get(k));
                }
            }


            //  поиск и подстановка стоимости
            double breakfast = 0D;
            double lunch = 0D;
            for(DailyReferReportItem i : result) {
                if(i.getGroup2() != null) {
                    if (i.getGroup2().equals(ReferReport.BREAKFAST)) {
                        breakfast = i.getPrice();
                    } else if (i.getGroup2().equals(ReferReport.LUNCH)) {
                        lunch = i.getPrice();
                    }
                    if (breakfast != 0D && lunch != 0D) {
                        break;
                    }
                }
            }
            for(DailyReferReportItem i : result) {
                i.setBreakfast(breakfast);
                i.setLunch(lunch);
            }

            return result;
        }
    }

    public static final List getReportData(Session session, Long idoforg, long start, long end,
            String categoryClause, String region) {
        return getReportData(session, idoforg, start, end, categoryClause,
                             " and ordertype<>" + OrderTypeEnumType.DAILY_SAMPLE.ordinal(), region);
    }
    
    public static final List getReportData(Session session, Long idoforg, long start, long end,
                                           String categoryClause, String orderTypeClause, String region) {
        if(categoryClause.length() < 1) {
            categoryClause = " and cf_discountrules.subcategory<>'' ";
        }
        String regionClause = "";
        if(region != null && region.trim().length() > 0) {
            regionClause = " and cf_orgs.district='" + region + "' ";
        }
        String orgClause = "";
        if(idoforg != null) {
            orgClause = " cf_orgs.idoforg=" + idoforg + " and ";
        }
        String sql =
                  "select subcategory, nameofgood, "
                + "       int8(EXTRACT(EPOCH FROM d) * 1000) as day, "
                + "       price, "
                + "       count(idoforder) as children, "
                + "       count(idoforder) * (price) as summary "
                + "from ("
                + "      select case when cf_orders.ordertype=6 and position('1-4' in cf_discountrules.subcategory) > 0 then 'Обучающиеся из соц. незащищ. семей 1-4 кл. (завтрак+обед)' "
                + "                  when cf_orders.ordertype=6 and position('5-9' in cf_discountrules.subcategory) > 0 then 'Обучающиеся из соц. незащищ. семей 5-9 кл. (завтрак+обед)' "
                + "                  when cf_orders.ordertype=6 and position('10-11' in cf_discountrules.subcategory) > 0 then 'Обучающиеся из соц. незащищ. семей 10-11 кл. (завтрак+обед)' "
                + "                  else cf_discountrules.subcategory end, "
                + "            cf_goods.nameofgood, "
                + "            cf_orders.idoforder, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d, "
                + "            cast (cf_orderdetails.rprice + cf_orderdetails.socdiscount as decimal) / 100 price "
                + "     from cf_orgs "
                + "     left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                + "     join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                + "     join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                + "     join cf_discountrules on cf_discountrules.idofrule=cf_orderdetails.idofrule "
                + "     where cf_orderdetails.socdiscount<>0 and " + orgClause
                + "           cf_orderdetails.state=0 and cf_orders.state=0 and cf_orders.createddate between :start and :end "
                //+ "           and subcategory='Обучающиеся из многодетных семей 5-9 кл. (завтрак+обед)' "                         //   !! TEST_ONLY
                + "           " + regionClause
                + "           " + orderTypeClause
                + "           " + categoryClause +
                          ") as data "
                + "group by subcategory, nameofgood, d, price "
                + "order by 1, 2";
        /*String sql = "select subcategory, nameofgood, "
                + "       int8(EXTRACT(EPOCH FROM d) * 1000) as day, "
                + "       price, "
                + "       count(idoforder) as children, "
                + "       count(idoforder) * (price) as summary "
                + "from ( "
                + "      select dr.subcategory, g.nameofgood, ord.idoforder, "
                + "             date_trunc('day', to_timestamp(ord.createddate / 1000)) as d, "
                + "             cast (det.rprice + det.socdiscount as decimal) / 100 price "
                + "      from cf_orgs o "
                + "      left join cf_orders ord on o.idoforg=ord.idoforg "
                + "      join cf_orderdetails det on ord.idoforder=det.idoforder and ord.idoforg=det.idoforg "
                + "      join cf_goods g on det.idofgood=g.idofgood "
                + "      join cf_discountrules dr on det.idofrule=dr.idofrule "
                + "      where det.socdiscount<>0 and  o.idoforg=225 and "
                + "            det.state=0 and ord.state=0 and "
                + "            ord.createddate between 1391371200000 and 1391975999000 "
                + "            and ord.ordertype<>5 and dr.subcategory<>'') as data "
                + "group by subcatego   ry, nameofgood, d, price "
                + "order by 1, 2";*/
        Query query = session.createSQLQuery(sql);
        //query.setLong("idoforg", idoforg);
        query.setLong("start", start);
        query.setLong("end", end);
        return query.list();
    }

    public DailyReferReport() {
    }


    public DailyReferReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<DailyReferReportItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public DailyReferReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<DailyReferReportItem> items) {
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public DailyReferReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ReferReport();  //To change body of implemented methods use File | Settings | File Templates.
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

    public static String findGroupRangeInString(String str) {
        Pattern pattern = Pattern.compile("([0-9]{1,2}\\-[0-9]{1,2})");
        Matcher matcher = pattern.matcher(str);
        // check all occurance
        if(matcher.find()) {
            /*System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.println(matcher.group());*/
            return matcher.group();
        }
        return null;
    }

    public static class DailyReferReportItem extends ReferReportItem {
        private int orderType;
        private int index;          //  индекс в таблице
        private String day;         //  день
        private double breakfast;   //  стоимость завтрака, используется только для отчета по категории
        private double lunch;       //  стоимость обеда, используется только для отчета по категории
        private String group0;      //  фикс для вывода шапки таблицы - всегда одинаковое значение
        private String group1Index; //  сортировка для строки
        private String group1;      //  наименование правила
        private String group2;      //  завтрак / обед
        private double price;       //  цена

        public DailyReferReportItem() {

        }

        public DailyReferReportItem(long ts, String name, String goodname, long children,
                                    double price, double summary, OrganizationType orgType) {
            this(dailyItemsFormat.format(new Date(ts)), name,  goodname,
                 children, price, summary, false, orgType);
        }

        public DailyReferReportItem(long ts, String name, String goodname, long children,
                                    double price, double summary, boolean isOverallReport, OrganizationType orgType) {
            this(dailyItemsFormat.format(new Date(ts)), name,  goodname,
                 children, price, summary, isOverallReport, orgType);
        }

        public DailyReferReportItem(String day, String name, String goodname,
                long children, double price, double summary,
                boolean isOverallReport, OrganizationType orgType) throws RuntimeException {
            this(day, name, goodname, children, price, summary, isOverallReport, orgType, 0D, 0D);
        }

        public DailyReferReportItem(long ts, String name, String goodname,
                long children, double price, double summary,
                boolean isOverallReport, OrganizationType orgType,
                double breakfast, double lunch) throws RuntimeException {
            this(dailyItemsFormat.format(new Date(ts)), name,  goodname, children, price,
                 summary, isOverallReport, orgType, breakfast, lunch);
        }

        public DailyReferReportItem(String day, String name, String goodname,
                                    long children, double price, double summary,
                                    boolean isOverallReport, OrganizationType orgType,
                                    double breakfast, double lunch) throws RuntimeException {
            index = 0;
            this.day = day;
            group0 = "_";
            if(isOverallReport) {
                String catName = "";
                if(orgType != null) {
                    if(orgType.equals(OrganizationType.SCHOOL)) {
                        catName = "классов";
                    } else if(orgType.equals(OrganizationType.KINDERGARTEN)) {
                        catName = "лет";
                    }
                } else {
                    catName = "все";
                }
                String groupRange = findGroupRangeInString(name);
                this.group1 = String.format("Обучающиеся %s %s", groupRange, catName);
                if(group1 == null) {
                    throw new RuntimeException("Failed to get group range in string " + name);
                }

                //  добавляем сортировку
                group1Index = getGroup1IndexByGroupRange(groupRange, SUBCATEGORY_SHOOL);
                if(group1Index == null) {
                    group1Index = getGroup1IndexByGroupRange(groupRange, SUBCATEGORY_KINDERGARTEN);
                }
                if(group1Index == null) {
                    group1Index = "" + 0;
                }
            } else {
                if (name.equals(OVERALL_SUBCATEGORY_NAME)) {
                    this.group1 = name;
                } else if (name.length() > 0) {
                    this.group1 = name.substring(0, name.indexOf("("));
                }
                group1Index = this.group1;
            }
            if (goodname.toLowerCase().indexOf("завтрак") > -1) {
                this.group2 = ReferReport.BREAKFAST;
            } else if (goodname.toLowerCase().indexOf("обед") > -1) {
                this.group2 = ReferReport.LUNCH;
            } else if (goodname.toLowerCase().indexOf("полдник") > -1) {
                this.group2 = ReferReport.SNACK;
            }

            //  Если это суббота и обед, то значение ставим в 0!!!
            /*Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);*/
            /*if(this.group2 != null && cal != null) {
                if (this.group2.equals(ReferReport.LUNCH) &&
                        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    children = 0L;
                    summary = 0D;
                }
            }*/

            this.children = children;
            this.price = price;
            this.summary = summary;
            orderType = 1;

            this.breakfast = breakfast;
            this.lunch = lunch;

            applyReplacements();
        }

        protected void applyReplacements() {
            for(String n : GROUP1_REPLACEMENTS.keySet()) {
                if(group1 != null && !StringUtils.isBlank(group1) && group1.equals(n)) {
                    group1 = GROUP1_REPLACEMENTS.get(n);
                    break;
                }
            }
        }

        protected String getGroup1IndexByGroupRange(String groupRange, CategoryFilter[] filters) {
            for(int i=0; i<filters.length; i++) {
                if(filters[i].getName().indexOf(groupRange) >= 0) {
                    return "" + i;
                }
            }
            return null;
        }

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getGroup0() {
            return group0;
        }

        public void setGroup0(String group0) {
            this.group0 = group0;
        }

        public String getGroup1() {
            return group1;
        }

        public void setGroup1(String group1) {
            this.group1 = group1;
        }

        public String getGroup1Index() {
            return group1Index;
        }

        public void setGroup1Index(String group1Index) {
            this.group1Index = group1Index;
        }

        public String getGroup2() {
            return group2;
        }

        public void setGroup2(String group2) {
            this.group2 = group2;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(double breakfast) {
            this.breakfast = breakfast;
        }

        public double getLunch() {
            return lunch;
        }

        public void setLunch(double lunch) {
            this.lunch = lunch;
        }
    }


    public static class ReferReportItem {
        protected String name;            //  наименование правила
        protected long children;          //  количество детей
        protected long total;             //  дето/дни
        protected double summary;         //  сумма
        protected int value;              //  поле для группировки, всегда = 1

        public ReferReportItem() {
            name = "";
            children = 0L;
            total = 0L;
            summary = 0D;
            value = 1;

        }

        public ReferReportItem(String name, long children, long total, double summary) {
            value = 1;
            this.name = name;
            this.children = children;
            this.total = total;
            this.summary = summary;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value =
                    value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getChildren() {
            return children;
        }

        public void setChildren(long children) {
            this.children = children;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public double getSummary() {
            return summary;
        }

        public void setSummary(double summary) {
            this.summary = summary;
        }
    }

    public static CategoryFilter getCategoryFilter(String categoryName) {
        if(SUBCATEGORY_ALL.getName().equals(categoryName)) {
            return SUBCATEGORY_ALL;
        }
        for(CategoryFilter f : SUBCATEGORY_SHOOL) {
            if(f.getName().equals(categoryName)) {
                return f;
            }
        }
        for(CategoryFilter f : SUBCATEGORY_KINDERGARTEN) {
            if(f.getName().equals(categoryName)) {
                return f;
            }
        }
        return null;
    }

    public static class CategoryFilter {
        protected String name;
        protected String sqlCase;

        public CategoryFilter(String name) {
            this.name = name;
            sqlCase = "";
        }

        public CategoryFilter(String name, String sqlCase) {
            this.name = name;
            this.sqlCase = sqlCase;
        }

        public String getName() {
            return name;
        }

        public String getSqlCase() {
            return sqlCase;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CategoryFilter that = (CategoryFilter) o;

            if (name != null ? !name.equals(that.name) : that.name != null) {
                return false;
            }
            if (sqlCase != null ? !sqlCase.equals(that.sqlCase) : that.sqlCase != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (sqlCase != null ? sqlCase.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CategoryFilter{" +
                    "name='" + name + '\'' +
                    ", sqlCase='" + sqlCase + '\'' +
                    '}';
        }
    }
}