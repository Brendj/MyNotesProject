/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.09.12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

public class ClientOrderDetailsByAllOrgReport extends BasicReportForAllOrgJob {

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder implements BasicReportForAllOrgJob.Builder{

        public static HashMap<Integer, String> values = new HashMap<Integer, String>();
        static {
            values.put(0,"Собственное");
            values.put(1,"Централизованное");
            values.put(2, "Централизованное с доготовкой");
            values.put(10,"Закупленное");
        }

        /* КонтракИД, ЗаказИД, Название Блюда, ФИО клиента, Тип производсва, сумма */
        public static class ClientReportItem {
            // ЗаказИД
            private Long idOfOrderDetail;
            // КонтракИД
            private Long contractId;
            //  ФИО клиента
            private String fio;
            //Название Блюда
            private String menuName;
            // Тип производсва
            private String menuOrigin;
            // Cумма
            private Float  price;

            public ClientReportItem(Long idOfOrderDetail, Long contracId, String fio, String menuName,
                    String menuOrigin, Float price) {
                this.idOfOrderDetail = idOfOrderDetail;
                this.contractId = contracId;
                this.fio = fio;
                this.menuName = menuName;
                this.menuOrigin = menuOrigin;
                this.price = price;
            }

            public Long getIdOfOrderDetail() {
                return idOfOrderDetail;
            }

            public void setIdOfOrderDetail(Long idOfOrderDetail) {
                this.idOfOrderDetail = idOfOrderDetail;
            }

            public Long getContractId() {
                return contractId;
            }

            public void setContractId(Long contractId) {
                this.contractId = contractId;
            }

            public String getFio() {
                return fio;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public String getMenuName() {
                return menuName;
            }

            public void setMenuName(String menuName) {
                this.menuName = menuName;
            }

            public String getMenuOrigin() {
                return menuOrigin;
            }

            public void setMenuOrigin(String menuOrigin) {
                this.menuOrigin = menuOrigin;
            }

            public Float getPrice() {
                return price;
            }

            public void setPrice(Float price) {
                this.price = price;
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
            Map<Object, Object> parameterMap = new HashMap<Object, Object>();
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
            return new ClientOrderDetailsByAllOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Calendar calendar, Map<Object, Object> parameterMap) throws Exception {

            Query query = session.createSQLQuery("SELECT cf_orderdetails.idoforderdetail, cf_clients.contractid, cf_persons.firstname || ' ' || cf_persons.secondname || ' ' || cf_persons.surname, "
                    + " cf_orderdetails.menuorigin, cf_orderdetails.menudetailname, cf_orderdetails.rprice"
                    + " FROM  public.cf_clients, public.cf_persons, public.cf_orders, public.cf_orderdetails "
                    + " WHERE (cf_orders.createddate>=:startTime AND cf_orders.createddate<=:endTime AND "
                    + " cf_orders.idoforder = cf_orderdetails.idoforder AND cf_orders.idofclient = cf_clients.idofclient AND cf_persons.idofperson = cf_clients.idofperson );"
                    + " ");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            List list = query.list();
            List<ClientReportItem> menuDetailsItems = new LinkedList<ClientReportItem>();
            for (Object result : list) {
                Object[] sale = (Object[]) result;
                Long idOfOrderDetail = Long.parseLong(sale[0].toString());
                Long contractId = Long.parseLong(sale[1].toString());
                String fullName = sale[2].toString();
                Integer menuOrigin = (Integer) sale[3];
                String menuName = sale[4].toString();
                Float price = Float.parseFloat(sale[5].toString()) / 100;
                menuDetailsItems.add(new ClientReportItem(idOfOrderDetail, contractId, fullName, menuName, values.get(menuOrigin), price));
            }
            return new JRBeanCollectionDataSource(menuDetailsItems);
        }

    }

    private final static Logger logger = LoggerFactory.getLogger(ClientOrderDetailsByAllOrgReport.class);

    public ClientOrderDetailsByAllOrgReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ClientOrderDetailsByAllOrgReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public ClientOrderDetailsByAllOrgReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_DAY;
    }

}
