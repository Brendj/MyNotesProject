/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 19.02.16
 * Time: 13:52
 */

public class DeliveredServicesElectronicCollationReport extends BasicReportForMainBuildingOrgJob {
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
    public static final String REPORT_NAME = "Сводный отчет по услугам (электронная сверка)";
    public static final String[] TEMPLATE_FILE_NAMES = {"DeliveredServicesElectronicCollationReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(DeliveredServicesElectronicCollationReport.class);

    private List<DeliveredServicesItem.DeliveredServicesData> data;
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

    public List<DeliveredServicesItem.DeliveredServicesData> getData() {
        return data;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;
        private String region;
        private Boolean otherRegions = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                    + DeliveredServicesElectronicCollationReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        public void setOrg(Long idOfOrg) {
            org = new OrgShortItem(idOfOrg);
        }

        @Override
        public DeliveredServicesElectronicCollationReport build(Session session, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            Long idOfContragent = null;
            Long idOfContract = null;
            if (reportProperties.getProperty("idOfContract") != null
                    && reportProperties.getProperty("idOfContract").length() > 0) {
                try {
                    idOfContract = Long.parseLong(reportProperties.getProperty("idOfContract"));
                } catch (Exception e) {
                    idOfContract = null;
                }
            }
            if (contragent != null) {
                idOfContragent = contragent.getIdOfContragent();
            }

            return build(session, startTime, endTime, calendar, org.getIdOfOrg(), idOfContragent, idOfContract, region,
                    getOtherRegions(), false);
        }

        public boolean confirmMessage(Session session, Date startTime, Date endTime,
                Calendar calendar, Long orgId, Long contragent, Long contract, String region, Boolean otherRegions)
                throws Exception {

            boolean b = findNotConfirmedTaloons(session, startTime, endTime, contragent,
                    contract);

            return b;
        }


        public DeliveredServicesElectronicCollationReport build(Session session, Date startTime, Date endTime,
                Calendar calendar, Long orgId, Long contragent, Long contract, String region, Boolean otherRegions, Boolean withoutFriendly)
                throws Exception {
            Date generateTime = new Date();
            this.otherRegions = otherRegions;
            this.region = region;

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            String nameOrg = "";
            /////
            if (orgShortItemList == null && orgId != null) {
                OrgShortItem item = new OrgShortItem(orgId);
                orgShortItemList = new ArrayList<OrgShortItem>();
                orgShortItemList.add(item);
            }
            /////
            if (orgShortItemList == null || orgShortItemList.isEmpty()) {
                nameOrg = "                                                                   ";
            } else  {
                StringBuilder stringBuilder = new StringBuilder();
                List<Long> orgs = new ArrayList<Long>();
                for(OrgShortItem orgShortItem : orgShortItemList) {
                    orgs.add(orgShortItem.getIdOfOrg());
                }
                Query query = session.createSQLQuery("select distinct o.officialname from cf_orgs o where o.idoforg in "
                        + "(select f.friendlyorg from cf_friendly_organization f "
                        + "inner join cf_orgs o on o.idoforg = f.friendlyorg "
                        + "where f.currentorg in :orgs and o.mainbuilding = 1)");
                query.setParameterList("orgs", orgs);
                List<String> list = (List<String>) query.list();
                if (list != null && list.size() != 0) {
                    for(String name : list) {
                        stringBuilder.append(name);
                        stringBuilder.append(", ");
                    }
                    nameOrg = stringBuilder.substring(0, stringBuilder.length() - 2);
                }
                if ((nameOrg == null) || nameOrg.isEmpty()) {
                    nameOrg = "                                                                   ";
                }
            }
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("nameOrg", nameOrg);

            Date generateEndTime = new Date();
            DeliveredServicesItem.DeliveredServicesData data = findNotNullGoodsFullNameByOrg(session, startTime, endTime, contragent,
                    contract, parameterMap, withoutFriendly);
            ///Пробегаемся по items и смотрим - если у них всех один и тот же контракт - заполняем параметр contract
            String contractNumber = "______";
            String contractDate = "________";
            Set<Long> orgs = new HashSet<Long>();
            for (DeliveredServicesItem item : data.getList153()) {
                orgs.add(item.getIdoforg());
            }
            for (DeliveredServicesItem item : data.getList37()) {
                orgs.add(item.getIdoforg());
            }
            for (DeliveredServicesItem item : data.getList14()) {
                orgs.add(item.getIdoforg());
            }
            for (DeliveredServicesItem item : data.getList511()) {
                orgs.add(item.getIdoforg());
            }
            if (orgs.size() > 0) {
                Query query = session.createQuery("select distinct contract from Org where idOfOrg in :orgs");
                query.setParameterList("orgs", orgs);
                List list = query.list();
                if (list != null && list.size() == 1) {
                    Contract contr = (Contract) list.get(0);
                    contractNumber = " ".concat(contr.getContractNumber());
                    if (contr.getDateOfConclusion() != null) {
                        contractDate = new SimpleDateFormat("dd.MM.yyyy").format(contr.getDateOfConclusion());
                    } else {
                        contractDate = "________";
                    }
                }
            }
            parameterMap.put("contractNumber", contractNumber);
            parameterMap.put("contractDate", contractDate);
            List<DeliveredServicesItem.DeliveredServicesData> result = new ArrayList<DeliveredServicesItem.DeliveredServicesData>();
            result.add(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap, result));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new DeliveredServicesElectronicCollationReport(generateTime,
                        generateEndTime.getTime() - generateTime.getTime(), jasperPrint, startTime, endTime, null,
                        (org == null ? null : org.getIdOfOrg()));
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
                return new DeliveredServicesElectronicCollationReport(generateTime,
                        generateEndTime.getTime() - generateTime.getTime(), startTime, endTime, result)
                        .setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<DeliveredServicesItem.DeliveredServicesData> data) throws Exception {
            return new JRBeanCollectionDataSource(data);
        }


        public DeliveredServicesItem.DeliveredServicesData findNotNullGoodsFullNameByOrg(Session session, Date start, Date end,
                Long contragent, Long contract, Map<String, Object> parameterMap, Boolean withoutFriendly) {
            String contragentCondition = "";
            if (contragent != null) {
                contragentCondition = "(cf_orgs.defaultsupplier=" + contragent + ") AND ";
            }

            String contractOrgsCondition = "";
            if (contract != null) {
                //  Вытаскиваем те оргии, которые привязаны к контракту и устанавливаем их как ограничения. !Будет заменено!
                Query query = session
                        .createSQLQuery("SELECT idoforg FROM cf_orgs WHERE idofcontract=:contract");//.createQuery(sql);
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
                contractCriteria.add(Restrictions.eq("globalId", contract));
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
            if ((orgShortItemList != null) && (!orgShortItemList.isEmpty())) {
                String in_str = "";
                for (OrgShortItem orgShortItem : orgShortItemList) {
                    if (withoutFriendly) {
                        in_str += orgShortItem.getIdOfOrg().toString() + ",";
                    } else {
                        Org o = (Org) session.load(Org.class, orgShortItem.getIdOfOrg());
                        for (Org fo : o.getFriendlyOrg()) {
                            in_str += fo.getIdOfOrg().toString() + ",";
                        }
                    }
                }
                if (in_str.length() > 0) {
                    in_str = in_str.substring(0, in_str.length() - 1);
                    orgCondition = String.format(" cf_orgs.idoforg in (%s) and ", in_str);
                }
            }

            String districtCondition = "";
            if ((region != null) && !region.isEmpty()) {
                //если выбран регион - надо анализировать флаг otherRegions
                if (otherRegions) {
                    districtCondition = String
                            .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                    + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and ",
                                    region);
                } else {
                    districtCondition = String
                            .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                    + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and cf_orgs.district = '%s' and ",
                                    region, region);
                }
            }

            /*//String typeCondition = " cf_orders.ordertype<>8 and ";
            String typeCondition = " (cf_orders.ordertype in (0,1,4,5,6,8,10)) and "
                    + " cf_orderdetails.menutype>=:mintype and cf_orderdetails.menutype<=:maxtype and ";
            String sql = "select cf_orgs.shortnameinfoservice, split_part(cf_goods.fullname, '/', 1) as level1, "
                    + "split_part(cf_goods.fullname, '/', 2) as level2, "
                    + "split_part(cf_goods.fullname, '/', 3) as level3, "
                    + "split_part(cf_goods.fullname, '/', 4) as level4, sum(cf_orderdetails.qty) as cnt, "
                    + "(cf_orderdetails.rprice + cf_orderdetails.socdiscount) price, "
                    + "sum(cf_orderdetails.qty) * (cf_orderdetails.rprice + cf_orderdetails.socdiscount) as sum, "
                    + "cf_orgs.address, "
                    + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), cf_orgs.idoforg, "
                    + "case cf_orders.orderType when 10 then 1 else 0 end as orderType from cf_orgs "
                    + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                    + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                    + "join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                    + "where cf_orderdetails.socdiscount>0 and cf_orders.state=0 and cf_orderdetails.state=0 and "
                    + typeCondition + contragentCondition + contractOrgsCondition + orgCondition + districtCondition
                    + " cf_orders.createddate between :start and :end  "
                    + "group by cf_orgs.idoforg, cf_orgs.officialname, cf_orders.orderType, level1, level2, level3, level4, price, address "
                    + "order by cf_orgs.idoforg, cf_orgs.officialname, level1, level2, level3, level4";
            Query query = session.createSQLQuery(sql);//.createQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);


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
                Integer orderType = (Integer) e[11];
                DeliveredServicesItem item = new DeliveredServicesItem();
                item.setOfficialname(officialname);
                item.setLevel1(String.format("%02d", orderType).concat("@").concat(level1));
                item.setLevel2(level2);
                item.setLevel3(level3);
                item.setNameOfGood(level4);
                item.setCount(count);
                item.setPrice(price);
                item.setSummary(summary);
                item.setOrgnum(orgNum);
                item.setAddress(address);
                item.setIdoforg(idoforg);
                item.setOrderType(orderType);
                result.add(item);
            }*/

            //Дополнительная инфа из таблицы cf_taloon_approval
            String sqlTaloon = "SELECT cf_orgs.shortnameinfoservice, "
                    + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 1) ELSE split_part(cft.goodsname, '/', 1) END AS level1, "
                    + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 2) ELSE split_part(cft.goodsname, '/', 2) END AS level2, "
                    + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 3) ELSE split_part(cft.goodsname, '/', 3) END AS level3, "
                    + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 4) ELSE split_part(cft.goodsname, '/', 4) END AS level4, "
                    + "sum(cft.soldedqty) AS cnt, "
                    + "cft.price AS price, sum(cft.soldedqty) * cft.price AS sum, cf_orgs.shortaddress, "
                    + "substring(cf_orgs.officialname FROM '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
                    + "cf_orgs.idoforg, CASE WHEN cft.taloonname ILIKE '%вода%' THEN 1 ELSE 0 END AS orderType, cft.taloondate "
                    + "FROM cf_taloon_approval cft JOIN cf_orgs ON cft.idoforg = cf_orgs.idoforg WHERE cft.deletedstate = FALSE  AND "
                    + contragentCondition + contractOrgsCondition + orgCondition + districtCondition
                    + "cft.taloondate BETWEEN :start AND :end"
                    + " GROUP BY cf_orgs.idoforg, cf_orgs.officialname, price, shortaddress, cft.soldedqty, level1, level2, level3, level4, price, shortaddress, cft.taloonname, cft.taloondate";
            Query queryTaloon = session.createSQLQuery(sqlTaloon);
            queryTaloon.setParameter("start", start.getTime());
            queryTaloon.setParameter("end", end.getTime());

            DeliveredServicesItem.DeliveredServicesData result = new DeliveredServicesItem.DeliveredServicesData();
            List<DeliveredServicesItem> waterItems = new ArrayList<DeliveredServicesItem>();
            Map<String, DeliveredServicesItem> headerMap = new TreeMap<String, DeliveredServicesItem>();

            int waterCount = 0;
            Long waterSummary = 0L;
            Long summary37 = 0L;
            Long summary511 = 0L;
            Long summaryAll = 0L;

            List res = queryTaloon.list();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                String officialname = (String) e[0];
                String level1 = (String) e[1];
                String level2 = (String) e[2];
                String level3 = (String) e[3];
                String nameOfGood = (String) e[4];
                int count = ((BigInteger) e[5]).intValue();
                long price = ((BigInteger) e[6]).longValue();
                long summary = ((BigInteger) e[7]).longValue();
                String address = (String) e[8];
                String orgNum = (e[9] == null ? "" : (String) e[9]);
                long idoforg = ((BigInteger) e[10]).longValue();
                Integer orderType = (Integer) e[11];
                Date createdDate = (new Date(((BigInteger)e[12]).longValue()));
                DeliveredServicesItem item = new DeliveredServicesItem();
                item.setOfficialname(officialname);
                item.setLevel1(String.format("%02d", orderType).concat("@").concat(level1));
                item.setLevel2(level2);
                item.setLevel3(level3);
                item.setCount(count);
                item.setPrice(price);
                item.setSummary(summary);
                item.setCreatedDate(createdDate);
                if(orderType.equals(0)) {
                    item.setNameOfGood(nameOfGood);
                } else {
                    waterCount += count;
                    waterSummary += summary;
                }
                item.setOrgnum(orgNum);
                item.setAddress(address);
                item.setIdoforg(idoforg);

                DeliveredServicesItem item1 = new DeliveredServicesItem(level1, level2, level3, nameOfGood,
                        null, null, null, officialname, orgNum, address, idoforg, createdDate);
                if(level3.equals("1,5-3") || level3.equals("1.5-3")) {
                    result.getList153().add(item);
                    summary37 += summary;
                    summaryAll += summary;
                    result.getList37().add(item1);
                    result.getList14().add(item1);
                    result.getList511().add(item1);
                } else if(level3.equals("3-7")) {
                    result.getList37().add(item);
                    summary37 += summary;
                    summaryAll += summary;
                    result.getList153().add(item1);
                    result.getList14().add(item1);
                    result.getList511().add(item1);
                } else if(level3.equals("1-4")) {
                    result.getList14().add(item);
                    summary511 += summary;
                    summaryAll += summary;
                    result.getList153().add(item1);
                    result.getList37().add(item1);
                    result.getList511().add(item1);
                } else if(level3.equals("5-11")) {
                    result.getList511().add(item);
                    summary511 += summary;
                    summaryAll += summary;
                    result.getList153().add(item1);
                    result.getList37().add(item1);
                    result.getList14().add(item1);
                } else if(orderType.equals(1)) {
                    waterItems.add(item);
                }
                if(StringUtils.isNotEmpty(nameOfGood) && !headerMap.keySet().contains(nameOfGood)) {
                    headerMap.put(nameOfGood, item);
                }

            }

            Map<Long, DeliveredServicesItem> mapForWater = new HashMap<Long, DeliveredServicesItem>();
            for(DeliveredServicesItem item : waterItems) {
                if(mapForWater.get(item.getIdoforg()) == null) {
                    for(DeliveredServicesItem itemForWater : result.getList511()) {
                        if(itemForWater.getIdoforg() == item.getIdoforg()) {
                            mapForWater.put(itemForWater.getIdoforg(), itemForWater);
                            break;
                        }
                    }
                }
                DeliveredServicesItem itemForWater = mapForWater.get(item.getIdoforg());
                if(itemForWater != null) {
                    itemForWater.setCountWater(itemForWater.getCountWater() + item.getCount());
                    itemForWater.setSummaryWater(itemForWater.getSummaryWater() + item.getSummary());
                    if (item.getCreatedDate().after(itemForWater.getCreatedDate()) || waterItems.size() < 2) {
                        itemForWater.setPriceWater(item.getPrice());
                        itemForWater.setCreatedDate(item.getCreatedDate());
                    }
                }
            }

            result.setHeaderList(new ArrayList<DeliveredServicesItem>(headerMap.values()));
            result.setListTotal37(result.getHeaderList());
            result.setListTotal511(result.getHeaderList());
            result.setListTotalAll(result.getHeaderList());

            parameterMap.put("waterCount", waterCount);
            parameterMap.put("waterSummary", waterSummary);
            parameterMap.put("summary37", summary37);
            parameterMap.put("summary511", summary511 + waterSummary);
            parameterMap.put("summaryAll", summaryAll + waterSummary);

            return result;
        }

        //Для вывода предупреждения
        public  boolean findNotConfirmedTaloons(Session session, Date start, Date end,
                Long contragent, Long contract) {
            boolean b = false;

            String contragentCondition = "";
            if (contragent != null) {
                contragentCondition = "(cf_orgs.defaultsupplier=" + contragent + ") AND ";
            }

            String contractOrgsCondition = "";
            if (contract != null) {
                //  Вытаскиваем те оргии, которые привязаны к контракту и устанавливаем их как ограничения. !Будет заменено!
                Query query = session
                        .createSQLQuery("SELECT idoforg FROM cf_orgs WHERE idofcontract=:contract");//.createQuery(sql);
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
                contractCriteria.add(Restrictions.eq("globalId", contract));
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
            String in_str = "";
            if (orgShortItemList != null) {
                if(!orgShortItemList.isEmpty()) {
                    for (OrgShortItem orgShortItem : orgShortItemList) {
                        Org o = (Org) session.load(Org.class, orgShortItem.getIdOfOrg());

                        for (Org fo : o.getFriendlyOrg()) {
                            in_str += fo.getIdOfOrg().toString() + ",";
                        }
                    }
                    if (in_str.length() > 0) {
                        in_str = in_str.substring(0, in_str.length() - 1);
                        orgCondition = String.format(" cf_orgs.idoforg in (%s) and ", in_str);
                    }
                }
            }

            String districtCondition = "";
            if ((region != null) && !region.isEmpty()) {
                //если выбран регион - надо анализировать флаг otherRegions
                if (otherRegions) {
                    districtCondition = String
                            .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                    + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and ",
                                    region);
                } else {
                    districtCondition = String
                            .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                    + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and cf_orgs.district = '%s' and ",
                                    region, region);
                }
            }

            String sqlTaloonError = "SELECT cft.ispp_state, cft.pp_state "
                    + "FROM cf_taloon_approval cft JOIN cf_orgs ON cft.idoforg = cf_orgs.idoforg WHERE cft.deletedstate = FALSE  AND "
                    + "(cft.ispp_state in (0) OR cft.pp_state in (0,2)) AND "
                    + contragentCondition + contractOrgsCondition + orgCondition + districtCondition
                    + "cft.taloondate BETWEEN :start AND :end";
            Query queryTaloonError = session.createSQLQuery(sqlTaloonError);
            queryTaloonError.setParameter("start", start.getTime());
            queryTaloonError.setParameter("end", end.getTime());

            List resTaloonError = queryTaloonError.list();

            if (!resTaloonError.isEmpty()) {
                b = true;
            }

            return b;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Boolean getOtherRegions() {
            return otherRegions;
        }

        public void setOtherRegions(Boolean otherRegions) {
            this.otherRegions = otherRegions;
        }
    }

    public DeliveredServicesElectronicCollationReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, List<DeliveredServicesItem.DeliveredServicesData> data, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
        this.data = data;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public DeliveredServicesElectronicCollationReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public DeliveredServicesElectronicCollationReport(Date generateTime, long generateDuration, Date startTime,
            Date endTime, List<DeliveredServicesItem.DeliveredServicesData> data) {
        this.data = data;
    }


    @Override
    public BasicReportForOrgJob createInstance() {
        return new DeliveredServicesReport();
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
        return REPORT_PERIOD_PREV_MONTH;
    }

    public class JasperStringOutputStream extends OutputStream {

        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b);
        }

        public String toString() {
            return this.string.toString();
        }
    }
}
