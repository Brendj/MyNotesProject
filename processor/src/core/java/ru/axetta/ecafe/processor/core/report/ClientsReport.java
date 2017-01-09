/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * Отчет - продажи по клиентам
 */
public class ClientsReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет продаж по клиентам";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, 3, 22, 23};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportJob.Builder {

        public static class ClientsReportItem {
            private Integer id = null;
            private String fio = null;
            private List<Long> row;

            public ClientsReportItem() {
                row = new ArrayList<Long>();
                for (float f = 0; f < 32; f++)
                    row.add(0L);
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getFio() {
                return fio;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public List<Long> getRow() {
                return row;
            }

            public void setRow(List<Long> row) {
                this.row = row;
            }

            public void add(int ind, Long val) {
                this.row.set(ind, this.row.get(ind) + val);
                this.row.set(31, this.row.get(31) + val);
            }

            public void add(ClientsReportItem arg) {
                for (int i = 0; i < arg.getRow().size()-1; i++) {
                    this.add(i, arg.getRow().get(i));
                }
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new ClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            HashMap<Integer, ClientsReportItem> mapItems = new HashMap<Integer, ClientsReportItem>(31);
            List<ClientsReportItem> resultRows = new LinkedList<ClientsReportItem>();
            Calendar c = Calendar.getInstance();
            Query query = session.createSQLQuery
                   ("select cf_orders.CreatedDate, sum(cf_orders.rsum/100), trim(both ' ' from cf_persons.firstname) as fn, "
                    + " trim(both ' ' from cf_persons.surname) as sn, trim(both ' ' from cf_persons.secondname) as scn "
                    + "from cf_orders "
                    + "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient "
                    + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                    + "where cf_orders.state=0 and cf_orders.idoforg=:idOfOrg and cf_orders.CreatedDate>=:startTime "
                    + "AND cf_orders.CreatedDate<=:endTime AND (cf_orders.rsum > 0) "
                    + "group by cf_orders.CreatedDate, cf_persons.firstname, cf_persons.surname, cf_persons.secondname "
                    + "order by sn, fn, scn, cf_orders.CreatedDate");
                    /*("SELECT o.CreatedDate, sum(od.rPrice) AS SUM1, p.firstname, p.surname, p.secondname "
                    + "FROM CF_ORDERS o, CF_CLIENTS c, CF_PERSONS p, CF_ORDERDETAILS od "
                    + "WHERE (o.idOfOrg=:idOfOrg) AND (o.idofclient=c.idofclient) AND (p.idofperson=c.idofperson) AND od.idOfOrder=o.idOfOrder "
                    + "AND (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND (od.rPrice > 0) "
                    + "group by o.CreatedDate, p.firstname, p.surname, p.secondname "
                    + "order by p.surname, p.firstname, p.secondname, o.CreatedDate;");*/

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());

            List resultList = query.list();
            ClientsReportItem tmp;
            int i = 1;
            for (Object o : resultList) {
                Object vals[]=(Object[])o;
                String firstname = (String)vals[2];
                String surname = (String)vals[3];
                String secondname = (String)vals[4];
                Long sum = Long.parseLong(vals[1].toString());
                Long time = Long.parseLong(vals[0].toString());
                StringBuilder sb = new StringBuilder();
                sb.append(firstname).append(surname).append(secondname);
                tmp = mapItems.get(sb.toString().hashCode());
                if (tmp == null) {
                    tmp = new ClientsReportItem();
                    tmp.setId(i++);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (surname!= null && !surname.trim().isEmpty())
                    stringBuilder.append(surname).append(' ');
                    if (firstname != null && !firstname.trim().isEmpty())
                        stringBuilder.append(firstname.trim().charAt(0)).append(". ");
                    if (secondname != null && !secondname.trim().isEmpty())
                        stringBuilder.append(secondname.trim().charAt(0)).append('.');
                    tmp.setFio(stringBuilder.toString().trim());
                    mapItems.put(sb.toString().hashCode(), tmp);
                    resultRows.add(tmp);
                }
                c.setTimeInMillis(time);
                int day = c.get(Calendar.DAY_OF_MONTH);
                tmp.add(day, sum);
            }
            // ИТОГО
            ClientsReportItem sum = new ClientsReportItem();
            for (ClientsReportItem clientsReportItem : resultRows) {
                sum.add(clientsReportItem);
            }
            resultRows.add(sum);
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public ClientsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
            Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(ClientsReport.class);

    public ClientsReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ClientsReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }
}


