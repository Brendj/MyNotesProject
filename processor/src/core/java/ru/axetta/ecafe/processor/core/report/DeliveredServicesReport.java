/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampItem;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 06.05.13
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class DeliveredServicesReport extends BasicReportForAllOrgJob {
    private final static Logger logger = LoggerFactory.getLogger(DeliveredServicesReport.class);

    private List<DeliveredServicesItem> items;
    private Date startDate;
    private Date endDate;

    private static final String ORG_NUM = "Номер ОУ";
    private static final String ORG_NAME = "Наименование ОУ";
    private static final String GOOD_NAME = "Товар";
    private static final List <String> DEFAULT_COLUMNS = new ArrayList<String>();
    static
    {
        DEFAULT_COLUMNS.add(ORG_NUM);
        DEFAULT_COLUMNS.add(ORG_NAME);
        DEFAULT_COLUMNS.add(GOOD_NAME);
    }



    public List<DeliveredServicesItem> getItems () {
        return items;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }


    public static class Builder implements BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            this.templateFilename = null;
        }

        @Override
        public DeliveredServicesReport build(Session session, Date startTime, Date endTime, Calendar calendar)
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
            List<DeliveredServicesItem> items = findNotNullGoodsFullNameByOrg(session, startTime, endTime);
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (templateFilename != null) {
                JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                        createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap,
                                         items));
                return new DeliveredServicesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, null);
            } else {
                return new DeliveredServicesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), startTime,
                        endTime, items);
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap, List<DeliveredServicesItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }


        public List<DeliveredServicesItem> findNotNullGoodsFullNameByOrg(Session session, Date start, Date end){
            String sql =    "select cf_orgs.officialname, "
                                + "split_part(cf_goods.fullname, '/', 1) as level1, "
                                + "split_part(cf_goods.fullname, '/', 2) as level2, "
                                + "split_part(cf_goods.fullname, '/', 3) as level3, "
                                + "split_part(cf_goods.fullname, '/', 4) as level4, "
                                + "count(cf_orders.idoforder) as cnt, "
                                + "cf_orderdetails.rprice price, "
                                + "count(cf_orders.idoforder) * cf_orderdetails.rprice as sum, "
                                + "cf_orgs.address, "
                                + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)') "
                            + "from cf_orgs "
                    + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                    + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                    + "join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                            + "where cf_orderdetails.socdiscount>0 and cf_orders.createddate between :start and :end "
                            + "group by cf_orgs.officialname, level1, level2, level3, level4, price, address "
                            + "order by cf_orgs.officialname, level1, level2, level3, level4";
            Query query = session.createSQLQuery(sql);//.createQuery(sql);
            query.setParameter("start",start.getTime());
            //query.setParameter("start",1357171200000L);
            query.setParameter("end",end.getTime());

            List<DeliveredServicesItem> result = new ArrayList <DeliveredServicesItem> ();
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
                String orgNum = (e[9]==null?"" :(String) e[9]);
                DeliveredServicesItem item = new DeliveredServicesItem ();
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
                result.add(item);
            }
            return result;
        }
    }


    public DeliveredServicesReport() {}


    public DeliveredServicesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<DeliveredServicesItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }


    public DeliveredServicesReport(Date generateTime, long generateDuration, Date startTime,
            Date endTime, List<DeliveredServicesItem> items) {
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
}
