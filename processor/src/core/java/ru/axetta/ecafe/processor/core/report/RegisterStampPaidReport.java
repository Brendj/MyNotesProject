package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem1;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampPaidReportItem;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: a.anvarov
 */

public class RegisterStampPaidReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Реестр талонов по платному питанию "
            + "услуг по организации питания и обеспечения питьевого режима обучающихся";
    public static final String[] TEMPLATE_FILE_NAMES = {"RegisterStampPaidReport.jasper",
                                                        "RegisterStampPaidReport_summary.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(RegisterStampPaidReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("idOfOrg", org.getIdOfOrg());
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("orgAddress", org.getAddress());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("contractNumber", getReportProperties().getProperty("contractNumber"));
            parameterMap.put("contractDate", getReportProperties().getProperty("contractDate"));

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new RegisterStampPaidReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            boolean withOutActDiscrepancies = false;

            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);

            List<GoodItem1> allGoods = service.findAllGoodsByOrderType(org.getIdOfOrg(), startTime, endTime,
                    OrderTypeEnumType.PAY_PLAN);
            Map<Date, Long> numbers = service.findAllRegistryTalonsPaid(org.getIdOfOrg(), startTime, endTime);

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            List<RegisterStampPaidReportItem> result = new ArrayList<RegisterStampPaidReportItem>();
            calendar.setTime(startTime);
            GoodItem1 emptyGoodItem = new GoodItem1();

            while (endTime.getTime() > calendar.getTimeInMillis()) {
                Date time = calendar.getTime();
                String date = timeFormat.format(time);
                if (allGoods.isEmpty()) {
                    RegisterStampPaidReportItem item = new RegisterStampPaidReportItem(emptyGoodItem,0L,date, time);
                    RegisterStampPaidReportItem total = new RegisterStampPaidReportItem(emptyGoodItem,0L,"Итого", CalendarUtils.addDays(endTime, 1));
                    result.add(item);
                    result.add(total);
                } else {
                    for (GoodItem1 goodItem : allGoods) {
                        String number = numbers.get(time) == null ? "" : Long.toString(numbers.get(time));
                        service.buildRegisterStampPaidReportItem(org.getIdOfOrg(), calendar.getTime(),
                                goodItem.getFullName(), withOutActDiscrepancies, OrderTypeEnumType.PAY_PLAN, result,
                                goodItem, date, number, endTime);
                        /*Long val = service.buildRegisterStampBodyValueByOrderType(org.getIdOfOrg(), calendar.getTime(),
                                goodItem.getFullName(), withOutActDiscrepancies, OrderTypeEnumType.PAY_PLAN);
                        RegisterStampPaidReportItem item = new RegisterStampPaidReportItem(goodItem,val,date,number, time);
                        RegisterStampPaidReportItem total = new RegisterStampPaidReportItem(goodItem,val,"Итого", CalendarUtils.addDays(endTime, 1));
                        result.add(item);
                        result.add(total);*/
                    }
                }
                calendar.add(Calendar.DATE, 1);
            }
            return new JRBeanCollectionDataSource(result);
        }
    }

    public RegisterStampPaidReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    public RegisterStampPaidReport() {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampPaidReport();
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
        return REPORT_PERIOD_PREV_MONTH;
    }
}
