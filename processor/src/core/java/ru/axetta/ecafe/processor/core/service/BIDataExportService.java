/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.11.12
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class BIDataExportService {

    private static boolean USE_FTP_AS_STORAGE = false;
    private static final DateFormat FILES_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BIDataExportService.class);
    private static final String FTP_ADDRESS = "";
    private static final String FTP_LOGIN = "";
    private static final String FTP_PASSWORD = "";
    private static final String FTP_WORKDIR = "";
    private String LOCAL_DIRECTORY = null;
    private static final ExportType oldTypes;
    private static final ExportType newTypes;
    private static final long MILLISECONDS_IN_DAY = 86400000L;

    static {
        /* Новый тип */
        List<BIDataExportType> TYPES = new ArrayList<BIDataExportType>();

        //  ------------------------------------------
        //  Категория ОУ (CategoryOrg)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("categoryorg",
                "select idofcategoryorg, categoryname "
                        + "from cf_categoryorg",
                new String[]{"idofcategoryorg", "categoryname"}));

        //  ------------------------------------------
        //  Общеобразовательные учреждения (Orgs)
        //  ------------------------------------------
        String orgTypeCases = ", case ";
        for(OrganizationType ot : OrganizationType.values()) {
            orgTypeCases += " when organizationtype=" + ot.ordinal() + " then '" + ot.toString() + "'";
        }
        orgTypeCases += " else 'Неизвестный тип ОУ' end as orgtype ";
        String orgStatusCases = ", case ";
        for(OrganizationStatus os : OrganizationStatus.values()) {
            orgStatusCases += " when organizationstatus=" + os.ordinal() + " then '" + os.toString() + "'";
        }
        orgStatusCases += " else 'Неизвестный статус ОУ' end as status ";
        TYPES.add(new BIDataExportType("orgs",
                "select cf_orgs.idoforg, cf_orgs.shortname, cf_orgs.address, cf_orgs.district, "
                        + "array_to_string(array_agg(cf_categoryorg_orgs.idofcategoryorg), ',') as orgCategory, cf_orgs.state as isInProm, "
                        + "cf_orgs.latitude as latitude, cf_orgs.longitude as longitude "
                        + orgTypeCases
                        + orgStatusCases
                        + ", cf_orgs.statusDetailing, cf_orgs.btiUnom, cf_orgs.btiUnad, cf_orgs.introductionQueue, cf_orgs.additionalIdBuilding "
                        + "from cf_orgs "
                        + "left join cf_categoryorg_orgs on cf_categoryorg_orgs.idoforg=cf_orgs.idoforg "
                        + "where cf_orgs.state<>0 "
                        + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_orgs.address, cf_orgs.district "
                        + "order by cf_orgs.shortname",
                new String[]{"idoforg", "officialname", "address", "district", "orgCategory", "isInProm",
                             "latitude", "longitude", "orgtype", "status",
                             "statusDetailing", "btiUnom", "btiUnad", "introductionQueue", "additionalIdBuilding"}));

        //  ------------------------------------------
        //  Оффициальные данные ОУ (Orgs_official)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("orgs_official",
                "select o.idoforg, o.guid, o2.officialname "
                + "from cf_orgs o "
                + "join cf_friendly_organization f on o.idoforg=f.friendlyorg "
                + "join cf_orgs o2 on o2.idoforg=f.currentorg "
                + "where o.shortname<>'' and o2.officialname<>'' "
                + "order by o2.officialname",
                new String[]{"idoforg", "guid", "officialname"}));

        //  ------------------------------------------
        //  Поставщики питания (Contragents)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("contragents",
                "select cf_contragents.idofcontragent, cf_contragents.contragentname "
                        + "from cf_orders "
                        + "left join cf_orgs on cf_orders.idoforg=cf_orgs.idoforg "
                        + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent "
                        + "where cf_orgs.state<>0 "
                        + "group by cf_contragents.idofcontragent, cf_contragents.contragentname "
                        + "order by cf_contragents.idofcontragent",
                new String[]{"idofcontragent", "contragentname"}));

        //  ------------------------------------------
        //  Клиенты (Clients)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("clients",
                "select cf_clients.idofclient, cf_clients.idoforg, cf_clients.idofclientgroup, array_to_string(array_agg(cf_clients_categorydiscounts.idofcategorydiscount), ',')  as idofcategorydiscount "
                        + "from cf_clients "
                        + "left join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
                        + "left join cf_clients_categorydiscounts on cf_clients_categorydiscounts.idofclient=cf_clients.idofclient "
                        + "where cf_orgs.state<>0 "
                        + "group by cf_clients.idofclient, cf_clients.idoforg, cf_clients.idofclientgroup "
                        + "order by idoforg",
                new String[]{"idofclient", "idoforg", "idofclientgroup", "socdiscount"}));

        //  ------------------------------------------
        //  Правила социальных скидок (DiscountRules)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("discountrules",
                "select cf_DiscountRules.idofrule as idofdiscountrule, cf_DiscountRules.priority, cf_DiscountRules.description "
                        + "from cf_DiscountRules "
                        + "order by cf_DiscountRules.idofrule",
                new String[]{"idofdiscountrule", "priority", "description"}));

        //  ------------------------------------------
        //  Группы клиентов (ClientGroups)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("clientgroups",
                "select distinct cf_clientgroups.idoforg, cf_clientgroups.idofclientgroup, cf_clientgroups.groupname " +
                        "from cf_clientgroups " +
                        "left join cf_orgs on cf_clientgroups.idoforg=cf_orgs.idoforg "+
                        "where cf_orgs.state<>0 " + //cf_clientgroups.idofclientgroup > 0 and
                        "order by cf_clientgroups.idofclientgroup",
                new String[]{"idoforg", "idofclientgroup", "groupname"}));

        //  ------------------------------------------
        //  Категории клиентов (CategoryDiscounts)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("categorydiscounts", "select idofcategorydiscount, categoryname " +
                "from cf_categorydiscounts " +
                "order by idofcategorydiscount", new String[]{"idofcategorydiscount", "categoryname"}));

        //  ------------------------------------------
        //  Типы карт (CardTypes)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("cardtypes", new String[]{"card_type_id", "categoryname"})
                .setSpecificExporter("cardTypesExporter"));

        //  ------------------------------------------
        //  Детализация заказа (OrderDetails)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("mobilenotify",
                "select idofclient, mobile, email "
                + "from cf_clients as regOrgSrc "
                + "left join cf_cards on regOrgSrc.idofclient=cf_cards.idofclient "
                + "where mobile<>'' or email<>'' and cf_cards.state=0",
                new String[]{"idofclient", "mobile", "email"}));





        //  ------------------------------------------
        //  Проходы (Events)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("events",
                "select cf_enterevents.idofclient, cf_enterevents.evtdatetime, cf_enterevents.idoforg, cf_enterevents.idofenterevent, "
                        + "       case when (cf_enterevents.passdirection=1) then 0 when (cf_enterevents.passdirection=0) then 1 end as action_type "
                        + "from cf_enterevents "
                        + "left join cf_clients on cf_enterevents.idofclient=cf_clients.idofclient and cf_enterevents.idoforg=cf_clients.idoforg "
                        + "left join cf_orgs on cf_enterevents.idoforg=cf_orgs.idoforg "
                        + "where "//cf_enterevents.idofclient<>0 and (cf_enterevents.passdirection=0 or cf_enterevents.passdirection=1) and
                        + "      cf_enterevents.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_enterevents.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + "      and cf_orgs.state<>0 "
                        + "order by cf_enterevents.evtdatetime",
                new String[]{"idofclient", "evtdatetime", "idoforg", "idofenterevent", "action_type"}));

        //  ------------------------------------------
        //  Заказы (Orders)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("orders",
                "select cf_orders.idofclient, cf_orders.orderdate as createddate, cf_orders.idoforg, cf_orders.idoforder, cf_orders.idofcontragent, "
                        + "     cf_cards.cardtype, cf_orders.rsum, cf_orders.socdiscount "
                        + "from cf_orders "
                        + "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient and cf_orders.idoforg=cf_clients.idoforg "
                        + "left join cf_cards on cf_orders.idofcard=cf_cards.idofcard "
                        + "left join cf_orgs on cf_orders.idoforg=cf_orgs.idoforg "
                        + "where cf_orders.orderdate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and " // cf_orders.idofclient<>0 and
                        + "      cf_orders.orderdate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + "      and cf_orgs.state<>0 "
                        + "order by orderdate",
                new String[]{"idofclient", "createddate", "idoforg", "idoforder", "idofcontragent","cardtype", "rsum", "socdiscount"}));

        //  ------------------------------------------
        //  Детализация заказа (OrderDetails)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("orderdetails",
                "select cf_orders.idoforg, cf_orders.idoforder, cf_orderdetails.idoforderdetail, "
                        + "       case when cf_orderdetails.menuType=" + OrderDetail.TYPE_DISH_ITEM + " then 2 "
                        + "            when cf_orderdetails.menuType>=" + OrderDetail.TYPE_COMPLEX_MIN + " and cf_orderdetails.menuType<=" + OrderDetail.TYPE_COMPLEX_LAST + " then 1 "
                        + "            when lower(cf_orderdetails.menugroup)='вендинг' then 3 "
                        + "            else -1 end as foodtype, "
                        + "       cf_orderdetails.menugroup as groupname, CF_ComplexRoles.ExtendRoleName as rationtype, cf_orderdetails.idofrule as idofcategorydiscount, cf_orderdetails.rprice as rsum, cf_orderdetails.socdiscount "
                        + "from cf_orders "
                        + "join cf_orderdetails on cf_orders.idoforg=cf_orderdetails.idoforg and cf_orders.idoforder=cf_orderdetails.idoforder "
                        + "left join CF_ComplexRoles on CF_ComplexRoles.IdOfRole=cf_orderdetails.idofrule "
                        + "left join cf_orgs on cf_orders.idoforg=cf_orgs.idoforg "
                        + "where cf_orders.orderdate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "//cf_orders.idofclient<>0 and
                        + "      cf_orders.orderdate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + "      and cf_orgs.state<>0 "
                        + "order by cf_orders.orderdate",
                new String[]{"idoforg", "idoforder", "idoforderdetail", "foodtype", "groupname", "rationtype", "idofcategorydiscount", "rsum", "socdiscount"}));


        newTypes = new ExportType(TYPES, "new");
    }


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_EXPORT_BI_DATA_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_EXPORT_BI_DATA_ON, "" + (on ? "1" : "0"));
    }


    public static String getLocalDirectory() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_EXPORT_BI_DATA_DIR);
    }


    public static void setLocalDirectory(String dirName) throws IOException {
        File dir = new File(dirName);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to find directory called '" + dirName + "'");
        }
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_EXPORT_BI_DATA_DIR, dir.getAbsolutePath());
    }


    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("BI data export is turned off. You have to activate this tool using common Settings");
            return;
        }
        LOCAL_DIRECTORY = getLocalDirectory();
        if (LOCAL_DIRECTORY == null || LOCAL_DIRECTORY.length() < 1) {
            //logger.error ("You had turn BI data export on but didn't setup local storage directory. Please, check settings.");
            return;
        }


        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());// - MILLISECONDS_IN_DAY * 3);
        clearCalendar(now);

        buildFiles(newTypes, now);
        updateLastDate(now);
    }


    private boolean isTodayFileExists() {
        return false;
    }


    private void getUnfinishedTypes(ExportType exportType, String dir, List<String> typesToUpdate, Calendar now) {
        typesToUpdate.clear();
        File check = null;
        for (BIDataExportType t : exportType.getTypes()) {
            String tName = t.getReportName();
            check = new File(exportType.getRootDirectory(LOCAL_DIRECTORY), parseFileName(now, tName));
            if (!check.exists() && check.length() < 1) {
                typesToUpdate.add(tName);
            }
        }
    }


    private boolean buildFiles(ExportType exportType, Calendar now) throws IOException {
        List<String> typesToUpdate = new ArrayList<String>();
        Calendar last = getStartDate();
        /*Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        clearCalendar(now);*/

        ///last.setTimeInMillis(1377993600000L);


        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session session = runtimeContext.createPersistenceSession();
            for (long ts = last.getTimeInMillis(); ts < now.getTimeInMillis(); ts += MILLISECONDS_IN_DAY) {
                Calendar start = new GregorianCalendar();
                Calendar end = new GregorianCalendar();
                start.setTimeInMillis(ts);
                end.setTimeInMillis(ts + MILLISECONDS_IN_DAY);

                getUnfinishedTypes(exportType, LOCAL_DIRECTORY, typesToUpdate, end);
                for (String t : typesToUpdate) {
                    if (!updateFiles(exportType, session, t, end, start)) // Если файл не удалось создать, то пропускаем
                    // создание всех остальных файлов
                    {
                        //break;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to export BI data", e);
            return false;
        }
    }


    private boolean updateFiles(ExportType exportType, Session session, String t, Calendar now, Calendar last) {
        try {
            // Запись во временный файл
            List<BIDataExportType> types = exportType.getTypes();
            BIDataExportType type = getTypeByName(types, t);
            if (type.useSpecificExporter()) {
                executeExporterMethod(exportType.getRootDirectory (LOCAL_DIRECTORY), t, last, type);
                return true;
            }
            File tempFile = null;
            BufferedWriter output = null;


            org.hibernate.Query q = session.createSQLQuery(applyMacroReplace(type.getSQL(), last, now, last));
            List resultList = q.list();
            StringBuilder builder = new StringBuilder();
            boolean fileCreated = false;
            boolean dataExists = false;
            File rootDir = null;
            try {
                //  Создаем директорию
                rootDir = new File (exportType.getRootDirectory (LOCAL_DIRECTORY));
                rootDir.mkdirs();
            } catch (Exception e) {
                logger.error("Failed to create ашду " + exportType.getRootDirectory (LOCAL_DIRECTORY), e);
                return false;
            }
            try {
                //  Создаем файл
                dataExists = true;
                if (!fileCreated) {
                    tempFile = new File(rootDir, parseFileName(last, t));
                    if (!USE_FTP_AS_STORAGE) {
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                        tempFile.createNewFile();
                    }
                    output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));


                    //  Составляем шапку для CSV
                    for (String col : type.getColumns()) {
                        if (builder.length() > 0) {
                            builder.append(";");
                        }
                        builder.append("\"" + col + "\"");
                    }
                    output.write(builder.toString() + "\n");
                    builder.delete(0, builder.length());

                    fileCreated = true;
                }
            } catch (Exception e) {
                logger.error("Failed to create file " + rootDir + "/" + parseFileName(last, t), e);
                return false;
            }


            //  Составляем таблицу с данными в CSV
            for (Object entry : resultList) {
                //  Обрабатываем данные
                Object e[] = (Object[]) entry;
                for (Object o : e) {
                    if (builder.length() > 0) {
                        builder.append(";");
                    }

                    if (o instanceof String) {
                        builder.append("\"" + ((String) o).trim() + "\"");
                    } else if (o instanceof BigDecimal) {
                        builder.append(((BigDecimal) o).doubleValue());
                    } else if (o instanceof BigInteger) {
                        builder.append(((BigInteger) o).longValue());
                    } else if (o instanceof Double) {
                        builder.append(((Double) o).doubleValue());
                    } else if (o instanceof Integer) {
                        builder.append(((Integer) o).intValue());
                    }
                }
                output.write(builder.toString() + "\n");
                output.flush();
                builder.delete(0, builder.length());
            }
            if (output != null) {
                output.close();
            }
            return dataExists;
        } catch (Exception e) {
            logger.error("Failed to build query for " + t, e);
            return false;
        }
    }


    private Calendar getLastBuildedPeriod(File localDir) throws IOException {
        File content[] = localDir.listFiles();
        Calendar last = null;
        for (File f : content) {
            if (!f.isFile()) {
                continue;
            }

            try {
                //String date = f.getName().substring(0, f.getName().indexOf("_"));
                String date = f.getName().substring(0, f.getName().indexOf(" "));
                Calendar that = new GregorianCalendar();
                that.setTimeInMillis(FILES_FORMAT.parse(date).getTime());

                if (last == null || last.getTimeInMillis() < that.getTimeInMillis()) {
                    last = that;
                }
            } catch (Exception e) {
                logger.error("Wrong file found: " + f.getName(), e);
            }
        }
        if (last == null) {
            last = getStartDate();
        }
        return last;
    }


    private static void clearCalendar(Calendar cal) {
        if (cal == null) {
            return;
        }
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }


    public String applyMacroReplace(String sql) {
        Calendar min = getStartDate();
        Calendar max = getToday();
        return applyMacroReplace(sql, min, max, min);
    }


    public static String applyMacroReplace(String sql, Calendar min, Calendar max, Calendar report) {
        if (sql.indexOf("%MINIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MINIMUM_DATE%", DB_DATE_FORMAT.format(min.getTime()));
        }
        if (sql.indexOf("%MAXIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MAXIMUM_DATE%", DB_DATE_FORMAT.format(max.getTime()));
        }
        if (sql.indexOf("%REPORT_DATE%") > -1) {
            sql = sql.replaceAll("%REPORT_DATE%", DB_DATE_FORMAT.format(report.getTime()));
        }

        return sql;
    }


    public static void updateLastDate (Calendar calendar) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        runtimeContext.setOptionValue(Option.OPTION_EXPORT_BI_DATA_LAST_UPDATE, calendar.getTimeInMillis());
        runtimeContext.saveOptionValues();
    }


    public static Calendar getStartDate() {
        /*Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        clearCalendar(cal);
        return cal;*/
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        long ts = runtimeContext.getOptionValueLong(Option.OPTION_EXPORT_BI_DATA_LAST_UPDATE);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(ts);
        clearCalendar(cal);
        return cal;
    }


    public static Calendar getToday() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        clearCalendar(cal);
        return cal;
    }


    public Calendar[] getUpdatePeriodsViaLocal(List<BIDataExportType> types, List<String> typesToUpdate) throws IOException {
        File dir = new File(LOCAL_DIRECTORY);
        Calendar last = getLastBuildedPeriod(dir);
        Calendar now = new GregorianCalendar();
        clearCalendar(last);
        clearCalendar(now);


        //  Если хотя бы один файл есть, то необзодимо проверить наличие всех файлов
        if (last != null && last.getTimeInMillis() == now.getTimeInMillis()) {
            File files[] = dir.listFiles();

            for (BIDataExportType type : types) {
                String t = type.getReportName();
                String fileName = parseFileName(now, t);
                boolean doAdd = true;
                for (File f : files) {
                    if (f.getName().toLowerCase().equals(fileName) && f.length() > 0) {
                        doAdd = false;
                        break;
                    }
                }
                if (doAdd) {
                    last.setTimeInMillis(
                            last.getTimeInMillis() - MILLISECONDS_IN_DAY);  //  Если необходимо добавлять хотябы одну запись,
                    //  данные выбираем с предыдущего дня по сегодняшний
                    typesToUpdate.add(t);
                }
            }
        } else {
            dir.mkdirs();
            for (BIDataExportType type : types) {
                typesToUpdate.add(type.getReportName());
            }
        }

        return new Calendar[]{last, now};
    }


    public static String parseFileName(Calendar cal, String type) {
        String date = FILES_FORMAT.format(cal.getTime());
        //return date + "_" + type + ".csv";
        return type + "_" + date + ".csv";
    }


    public static BIDataExportType getTypeByName(List<BIDataExportType> types, String reportName) {
        for (BIDataExportType t : types) {
            if (t.getReportName().equals(reportName)) {
                return t;
            }
        }
        return null;
    }


    public static void cardTypesExporter(BIDataExportType type, String LOCAL_DIRECTORY, String t, Calendar last) throws IOException {
        File tempFile = new File(LOCAL_DIRECTORY, parseFileName(last, t));
        if (!USE_FTP_AS_STORAGE) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            tempFile.createNewFile();
        }
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));

        StringBuilder builder = new StringBuilder();
        //  Составляем шапку для CSV
        for (String col : type.getColumns()) {
            if (builder.length() > 0) {
                builder.append(";");
            }
            builder.append("\"" + col + "\"");
        }
        output.write(builder.toString() + "\n");
        builder.delete(0, builder.length());

        output.write("0;\"Неизвестный тип карты\"\n");
        for (int i = 1; i < Card.TYPE_NAMES.length; i++) {
            output.write(i + ";\"" + Card.TYPE_NAMES[i] + "\"\n");
        }
        output.close();
    }


    public static void executeExporterMethod(String dir, String t, Calendar now, BIDataExportType type) {
        if (type == null) {
            return;
        }

        java.lang.reflect.Method meth;
        try {
            meth = BIDataExportService.class
                    .getDeclaredMethod(type.getSpecificExporter(), BIDataExportType.class, String.class, String.class, Calendar.class);
            meth.invoke(null, type, dir, t, now);
        } catch (Exception e) {
            logger.error("Failed to execute exporter method " + type.getSpecificExporter(), e);
        }
    }


    public static class BIDataExportType {

        private String sql;
        private String cols[];
        private String exporterMethod;
        private String reportName;


        public BIDataExportType(String reportName, String cols[]) {
            this.reportName = reportName;
            this.cols = cols;
        }

        public BIDataExportType(String reportName, String sql, String cols[]) {
            this.sql = sql;
            this.cols = cols;
            this.reportName = reportName;
        }

        public String getSQL() {
            return sql;
        }

        public String[] getColumns() {
            return cols;
        }

        public boolean useSpecificExporter() {
            return exporterMethod != null && exporterMethod.length() > 0;
        }

        public BIDataExportType setSpecificExporter(String exporterMethod) {
            this.exporterMethod = exporterMethod;
            return this;
        }

        public String getSpecificExporter() {
            return exporterMethod;
        }

        public String getReportName() {
            return reportName;
        }
    }


    static {

        List<BIDataExportType> TYPES = new ArrayList<BIDataExportType>();
        //  ------------------------------------------
        //  Заказы (Orders)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("orders",
                "select int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, cf_orders.createddate, cf_orders.idoforder, cf_orders.idoforg, cf_orders.idofcontragent, cf_orders.idofclient, "
                        + "       grp1.idofclientgroup, grp2.groupname as grade_class, "
                        + "       array_to_string(array( " + "select cf_clients_categorydiscounts.idofcategorydiscount "
                        + "from cf_clients_categorydiscounts "
                        + "where cf_clients_categorydiscounts.idofclient = cf_clients.idofclient "
                        + "       ), ',') as idofcategorydiscount, cf_cards.cardtype, cf_orders.rsum, cf_orders.socdiscount "
                        + "from cf_orders "
                        + "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient and cf_orders.idoforg=cf_clients.idoforg "
                        + "left join cf_cards on cf_orders.idofcard=cf_cards.idofcard "
                        + "left join cf_clientgroups grp1 on cf_clients.idofclientgroup=grp1.idofclientgroup and cf_clients.idoforg=grp1.idoforg "
                        + "left join cf_clientgroups grp2 on cf_clients.idofclientgroup=grp2.idofclientgroup and cf_clients.idoforg=grp2.idoforg and "
                        + "          CAST(substring(grp2.groupname FROM '[0-9]+') AS INTEGER)<>0 "
                        + "where cf_orders.idofclient<>0 and cf_orders.createddate >= EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000  and "
                        + "cf_orders.createddate < EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " + "order by createddate",
                new String[]{
                        "build_date", "createddate", "idoforder", "idoforg", "idofcontragent", "idofclient",
                        "idofclientgroup", "grade_class", "idofcategorydiscount", "cardtype", "rsum", "socdiscount"}));

        //  ------------------------------------------
        //  Проходы (Events)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("events",
                "select int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, cf_enterevents.evtdatetime, cf_enterevents.idofenterevent, cf_enterevents.idoforg, "
                        +
                        "       cf_enterevents.idofclient, case when (cf_enterevents.passdirection=1) then 0 when (cf_enterevents.passdirection=0) then 1 end as action_type, "
                        +
                        "       cf_clientgroups.idofclientgroup, cf_clientgroups.groupname as grade_class " +
                        "from cf_enterevents " +
                        "left join cf_clients on cf_enterevents.idofclient=cf_clients.idofclient and cf_enterevents.idoforg=cf_clients.idoforg "
                        + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +

                        "where (cf_enterevents.passdirection=0 or cf_enterevents.passdirection=1) and cf_enterevents.idofclient<>0 and "
                        +
                        "      cf_enterevents.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_enterevents.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 "
                        +
                        "order by cf_enterevents.evtdatetime", new String[]{
                "build_date", "evtdatetime", "idofenterevent", "idoforg", "idofclient", "action_type",
                "idofclientgroup", "grade_class"}));

        //  ------------------------------------------
        //  Общеобразовательные учреждения (Orgs)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("orgs", "select cf_orgs.idoforg, cf_orgs.shortname, cf_orgs.address " +
                "from cf_orgs " +
                //"where cf_orgs.shortname<>'' " +
                "order by cf_orgs.shortname", new String[]{"idoforg", "officialname", "address"}));

        //  ------------------------------------------
        //  Поставщики питания (Contragents)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("contagents",
                "select cf_contragents.idofcontragent, cf_contragents.contragentname, cf_contragents.inn "
                        + "from cf_orders "
                        + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent "
                        + "group by cf_contragents.idofcontragent, cf_contragents.contragentname, cf_contragents.inn "
                        + "order by cf_contragents.idofcontragent",
                new String[]{"idofcontragent", "contragentname", "inn"}));

        //  ------------------------------------------
        //  Группы клиентов (clientgroups)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("clientgroups",
                "select distinct cf_clientgroups.idoforg, cf_clientgroups.idofclientgroup, cf_clientgroups.groupname " +
                        "from cf_clientgroups " +
                        "where cf_clientgroups.idofclientgroup > 0 " +
                        "order by cf_clientgroups.idofclientgroup",
                new String[]{"idoforg", "idofclientgroup", "groupname"}));

        //  ------------------------------------------
        //  Социальные скидки для клиентов (clientcomplexdiscounts) - УДАЛЕНО
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("discounts", "select idofcategorydiscount, categoryname " +
                "from cf_categorydiscounts " +
                "order by idofcategorydiscount", new String[]{"idofcategorydiscount", "categoryname"}));

        //  ------------------------------------------
        //  Типы карт (CardTypes)
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("cardtypes", new String[]{"card_type_id", "categoryname"})
                .setSpecificExporter("cardTypesExporter"));

        //  ------------------------------------------
        //  ???
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("clientscount",
                "select cf_clients.idoforg, 1 as supergroup, int8(EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_DATE%') * 1000) as condition_date, int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, cf_clients.idOfClientGroup, count(cf_clients.idofclient) "
                        + "from cf_clients "
                        + "left join cf_clientgroups on cf_clientgroups.idoforg=cf_clients.idoforg and cf_clientgroups.idOfClientGroup=cf_clients.idOfClientGroup "
                        + "where cf_clients.idOfClientGroup>=" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " AND cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + " "//--Сотрудники
                        + "group by cf_clients.idoforg, cf_clients.idOfClientGroup, cf_clientgroups.groupname "

                        + "union all "

                        + "select cf_clients.idoforg, 2 as supergroup, int8(EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_DATE%') * 1000) as condition_date, int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, cf_clients.idOfClientGroup, count(cf_clients.idofclient) "
                        + "from cf_clients "
                        + "left join cf_clientgroups on cf_clientgroups.idoforg=cf_clients.idoforg and cf_clientgroups.idOfClientGroup=cf_clients.idOfClientGroup "
                        + "where cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " " //--Ученики
                        + "group by cf_clients.idoforg, cf_clients.idOfClientGroup, cf_clientgroups.groupname "
                        + "order by idoforg, 2",
                new String[]{
                        "idoforg", "supergroup", "condition_date", "build_date", "idOfClientGroup", "count"}));

        //  ------------------------------------------
        //  ???
        //  ------------------------------------------
        TYPES.add(new BIDataExportType("clientsdiscountcategories",
                "select cf_clients.idofclient, cf_clients.idoforg, int8(EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_DATE%') * 1000) as condition_date, int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, "
                        + "       array_to_string(array_agg(cf_clients_categorydiscounts.idofcategorydiscount), ',')  as idofcategorydiscount, cf_clients.idOfClientGroup as idOfClientGroup "
                        + "from cf_clients "
                        + "left join cf_clients_categorydiscounts on cf_clients_categorydiscounts.idofclient=cf_clients.idofclient "
                        + "left join cf_clientgroups on cf_clientgroups.idoforg=cf_clients.idoforg and cf_clientgroups.idOfClientGroup=cf_clients.idOfClientGroup "
                        + "where cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + " " // Выбывшие
                        + "group by cf_clients.idofclient, cf_clients.idoforg, condition_date, build_date "
                        + "order by cf_clients.idofclient, cf_clients.idoforg",
                /*"select cf_clients.idofclient, cf_clients.idoforg, int8(EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_DATE%') * 1000) as condition_date, int8(EXTRACT(EPOCH FROM now()) * 1000) as build_date, "
                + "       array_to_string(array(select cf_clients_categorydiscounts.idofcategorydiscount "
                + "                             from cf_clients_categorydiscounts "
                + "                             where cf_clients_categorydiscounts.idofclient = cf_clients.idofclient), ',') as idofcategorydiscount "
                + "from cf_clients "
                + "order by cf_clients.idoforg, cf_clients.idofclient", */
                new String[]{
                        "idofclient", "idoforg", "condition_date", "build_date", "categories", "idOfClientGroup"}));

        oldTypes = new ExportType (TYPES, "old");
    }


    public static class ExportType {
        private String dir;
        private final List<BIDataExportType> types;

        public ExportType (List<BIDataExportType> types, String dir) {
            this.types = types;
            this.dir = dir;
        }

        public List<BIDataExportType> getTypes () {
            return types;
        }

        public String getRootDirectory (String rootDir) {
            return rootDir + "/" + dir;
        }
    }
}