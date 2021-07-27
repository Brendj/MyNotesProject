/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgItem;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 29.09.16
 * Time: 10:37
 */
public class OrdersByManufacturerReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Сводный отчет по производителю";
    public static final String[] TEMPLATE_FILE_NAMES = {"OrdersByManufacturerReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};

    final private static Logger logger = LoggerFactory.getLogger(OrdersByManufacturerReport.class);

    public OrdersByManufacturerReport(Date generateTime, long l, JasperPrint jasperPrint, Date startTime, Date endTime,Long idOfContragent) {
        super(generateTime,l,jasperPrint,startTime,endTime,idOfContragent);
    }

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForContragentJob.Builder {
        private final String templateFilename;
        private Long idOfContragent = -1L;
        private List<Long> idOfOrgList;
        private List<String> manufacturers;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);

            if (contragent != null) {
                parameterMap.put("contragentName", contragent.getContragentName());
                idOfContragent = contragent.getIdOfContragent();
            } else {
                throw new IllegalArgumentException("Поставщик не указан.");
            }
            JRDataSource dataSource = createDataSource(session, startTime, endTime, (Calendar) calendar.clone(),
                    parameterMap);

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new OrdersByManufacturerReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                    startTime, endTime, idOfContragent);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {
            String idOfOrgListString = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));

            List<String> idOfOrgListStrings = new ArrayList<String>();
            if (!idOfOrgListString.equals("")) {
                idOfOrgListStrings = Arrays.asList(StringUtils.split(idOfOrgListString, ','));
            }

            idOfOrgList = new ArrayList<Long>();

            if (!idOfOrgListStrings.isEmpty()) {
                for (String idOfOrg : idOfOrgListStrings) {
                    idOfOrgList.add(Long.valueOf(idOfOrg));
                }
            }

            startTime = CalendarUtils.truncateToDayOfMonth(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            List<String> dates = CalendarUtils.datesBetween(startTime, endTime, 1);
            Map<Long, List<ReportItem>> totalListMap = new HashMap<Long, List<ReportItem>>();
            //Получаем список всех школ, заполняем ими список
            List<Long> idOfOrgsList = new LinkedList<Long>();
            retreiveAllOrgs(totalListMap, dates, idOfOrgsList, startTime, endTime);
            if (idOfOrgsList.size() == 0) {
                return new JREmptyDataSource();
            }

            ReportData totalTMP = new ReportData();
            for (List<ReportItem> totalItemList : totalListMap.values()) {
                totalTMP.getItemList().addAll(totalItemList);
            }
            retreiveAllOrders(totalListMap, idOfOrgsList, startTime, endTime);

            //Вывод, разбивка по районам
            Map<String, ReportData> totalResult = new HashMap<String, ReportData>();

            List<TotalReportItem> tot = summaries(totalListMap);

            for (ReportItem o : totalTMP.getItemList()) {
                if (!totalResult.containsKey(o.getDisctrict())) {
                    totalResult.put(o.getDisctrict(), new ReportData(o.getDisctrict(), tot));
                }
                List<ReportItem> itemList = totalResult.get(o.getDisctrict()).getItemList();
                if(itemList == null) {
                    itemList = new ArrayList<ReportItem>();
                }
                itemList.add(o);
            }

            return new JRBeanCollectionDataSource(totalResult.values());
        }

        private void retreiveAllOrgs(Map<Long, List<ReportItem>> totalItemMap, List<String> dates,
                List<Long> idOfOrgsList, Date startTime, Date endTime) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);
            List<OrgItem> allNames;

            if (!idOfOrgList.isEmpty()) {
                allNames = orgRepository.findAllNamesByContragentTSPByListOfOrgIds(idOfContragent, idOfOrgList);
            } else {
                allNames = orgRepository.findAllNamesByContragentTSP(idOfContragent);
                idOfOrgList = new ArrayList<Long>();
                for(OrgItem orgItem : allNames) {
                    idOfOrgList.add(orgItem.getIdOfOrg());
                }
            }

            manufacturers = OrdersRepository.getInstance().findAllManufacturers(idOfOrgList, startTime, endTime);

            List<ReportItem> totalItemList;
            for (OrgItem orgItem : allNames) {
                totalItemList = new ArrayList<ReportItem>();
                for (String date : dates) {
                    for (String manufacturer : manufacturers) {
                        totalItemList.add(new ReportItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, manufacturer));
                    }
                }
                totalItemMap.put(orgItem.getIdOfOrg(), totalItemList);
                idOfOrgsList.add(orgItem.getIdOfOrg());
            }
        }

        private void retreiveAllOrders(Map<Long, List<ReportItem>> totalListMap, List<Long> idOfOrgsList, Date startTime, Date endTime) {
            List<OrderItem> allOrders = OrdersRepository.getInstance().findAllOrdersByManufacturer(idOfOrgsList, startTime, endTime);

            for (OrderItem order : allOrders) {
                for(ReportItem reportItem : totalListMap.get(order.getIdOfOrg())) {
                    String date = CalendarUtils.dateShortToString(order.getOrderDate());
                    if(reportItem.getDate().equals(date) && reportItem.getType().equals(order.getManufacturer())) {
                        reportItem.setSum(reportItem.getSum() + order.getSum());
                    }
                }
            }
        }

        private List<TotalReportItem> summaries(Map<Long, List<ReportItem>> totalListMap) {
            Map<String, Long> map = new HashMap<String, Long>();

            for(List<ReportItem> list : totalListMap.values()) {
                for(ReportItem reportItem : list) {
                    if(map.containsKey(reportItem.getType())) {
                        map.put(reportItem.getType(), map.get(reportItem.getType()) + reportItem.getSum());
                    } else {
                        map.put(reportItem.getType(), reportItem.getSum());
                    }
                }
            }

            List<TotalReportItem> summaries = new ArrayList<TotalReportItem>();

            for(String s : map.keySet()) {
                TotalReportItem totalReportItem = new TotalReportItem(s, Double.parseDouble(map.get(s).toString())/100);
                summaries.add(totalReportItem);
            }

            return summaries;
        }

        public void setIdOfContragent(Long idOfContragent) {
            this.idOfContragent = idOfContragent;
        }
    }


    public OrdersByManufacturerReport() {
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }


    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_LAST_WEEK;
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new OrdersByManufacturerReport();
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public static class ReportData {
        private String name;
        private List<ReportItem> itemList;
        private List<TotalReportItem> totalReportItemList;

        public ReportData() {
            itemList = new ArrayList<ReportItem>();
        }

        public ReportData(String name, List<TotalReportItem> totalReportItemList) {
            this.name = name;
            itemList = new ArrayList<ReportItem>();
            this.totalReportItemList = totalReportItemList;
        }

        public List<ReportItem> getItemList() {
            return itemList;
        }

        public void setItemList(List<ReportItem> itemList) {
            this.itemList = itemList;
        }

        public List<TotalReportItem> getTotalReportItemList() {
            return totalReportItemList;
        }

        public void setTotalReportItemList(List<TotalReportItem> totalReportItemList) {
            this.totalReportItemList = totalReportItemList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ReportItem {
        private String name;
        private String disctrict;
        private String date;
        private long sum;
        private String type;


        public ReportItem(String name, String disctrict, String date, long sum, String type) {
            this.name = name;
            this.disctrict = disctrict;
            this.date = date;
            this.sum = sum;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisctrict() {
            return disctrict;
        }

        public void setDisctrict(String disctrict) {
            this.disctrict = disctrict;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getSum() {
            return sum;
        }

        public String getSummToString() {
            return CurrencyStringUtils.copecksToRubles(sum);
        }

        public void setSum(long sum) {
            this.sum = sum;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class TotalReportItem {

        public String manufacturerTitle;
        public Double sumTotals;


        public TotalReportItem() {
        }

        public TotalReportItem(String manufacturerTitle, Double sumTotals) {
            this.manufacturerTitle = manufacturerTitle;
            this.sumTotals = sumTotals;
        }

        public String getManufacturerTitle() {
            return manufacturerTitle;
        }

        public void setManufacturerTitle(String manufacturerTitle) {
            this.manufacturerTitle = manufacturerTitle;
        }

        public Double getSumTotals() {
            return sumTotals;
        }

        public void setSumTotals(Double sumTotals) {
            this.sumTotals = sumTotals;
        }
    }
}
