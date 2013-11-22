/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Menu;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.09.12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

public class MenuDetailsGroupByMenuOriginReport extends BasicReportForAllOrgJob {

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder{

        public static HashMap<Integer, String> values = new HashMap<Integer, String>();
        static {
            values.put(0,"Собственное");
            values.put(1,"Централизованное");
            values.put(2, "Централизованное с доготовкой");
            values.put(10,"Закупленное");
        }

        public static class MenuDetailsItem{
            private String menuOrigin;
            private Float  price;
            private Long count;

            public MenuDetailsItem(String menuOrigin, Float price, Long count) {
                this.menuOrigin = menuOrigin;
                this.price = price;
                this.count = count;
            }

            public String getMenuOrigin() {
                return menuOrigin;
            }

            public Float getPrice() {
                return price;
            }

            public void setPrice(Float price) {
                this.price = price;
            }

            public Long getCount() {
                return count;
            }

            public void setCount(Long count) {
                this.count = count;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new MenuDetailsGroupByMenuOriginReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {

            Query query = session.createSQLQuery("SELECT cf_menudetails.menuorigin,  count(*),"
                    + "  sum(cf_menudetails.price) as price FROM  public.cf_menu, "
                    + "  public.cf_menudetails WHERE (cf_menu.createddate>=:startTime AND cf_menu.createddate<=:endTime AND cf_menu.idofmenu = cf_menudetails.idofmenu) group by cf_menudetails.menuorigin;");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            List list = query.list();
            List<MenuDetailsItem> menuDetailsItems = new LinkedList<MenuDetailsItem>();
            for (Object result : list) {
                Object[] sale = (Object[]) result;
                Integer menuOrigin = (Integer) sale[0];
                Long count = Long.parseLong(sale[1].toString());
                Float price = Float.parseFloat(sale[2].toString()) / 100;
                menuDetailsItems.add(new MenuDetailsItem(values.get(menuOrigin), price, count));
            }
            return new JRBeanCollectionDataSource(menuDetailsItems);
        }

    }

    private final static Logger logger = LoggerFactory.getLogger(MenuDetailsGroupByMenuOriginReport.class);

    public MenuDetailsGroupByMenuOriginReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new MenuDetailsGroupByMenuOriginReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public MenuDetailsGroupByMenuOriginReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_DAY;
    }

}
