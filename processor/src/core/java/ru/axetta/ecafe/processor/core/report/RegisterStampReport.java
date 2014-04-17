package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampReportItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.05.13
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampReport extends BasicReportForOrgJob {

    public static final String PARAM_WITH_OUT_ACT_DISCREPANCIES = "includeActDiscrepancies";

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampReport.class);

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
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new RegisterStampReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);

            String withOutActDiscrepanciesParam = (String) getReportProperties().get(PARAM_WITH_OUT_ACT_DISCREPANCIES);
            boolean withOutActDiscrepancies = false;
            if (withOutActDiscrepanciesParam!=null) {
                withOutActDiscrepancies = withOutActDiscrepanciesParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            List<GoodItem> allGoods = service.findAllGoods(org.getIdOfOrg(), startTime, endTime );
            Map<Date, Long> numbers = service.findAllRegistryTalons(org.getIdOfOrg(), startTime, endTime);
            List<RegisterStampReportItem> result = new ArrayList<RegisterStampReportItem>();
            calendar.setTime(startTime);
            GoodItem emptyGoodItem = new GoodItem();
            while (endTime.getTime()>calendar.getTimeInMillis()){
                Date time = calendar.getTime();
                String date = timeFormat.format(time);
                if(allGoods.isEmpty()){
                    RegisterStampReportItem item = new RegisterStampReportItem(emptyGoodItem,0L,date);
                    RegisterStampReportItem total = new RegisterStampReportItem(emptyGoodItem,0L,"77777");
                    RegisterStampReportItem allTotal = new RegisterStampReportItem(emptyGoodItem,0L,"99999");
                    result.add(allTotal);
                    result.add(item);
                    result.add(total);
                } else {
                    for (GoodItem goodItem: allGoods){
                        String number = numbers.get(time) == null ? "" : Long.toString(numbers.get(time));
                        Long val = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                goodItem.getFullName(), withOutActDiscrepancies);
                        RegisterStampReportItem item = new RegisterStampReportItem(goodItem,val,date,number);
                        RegisterStampReportItem total = new RegisterStampReportItem(goodItem,val,"77777");
                        RegisterStampReportItem allTotal = new RegisterStampReportItem(goodItem,val,"99999");
                        result.add(allTotal);
                        result.add(item);
                        result.add(total);
                    }
                }
                calendar.add(Calendar.DATE,1);
            }
            if(allGoods.isEmpty()){
                RegisterStampReportItem dailySampleItem = new RegisterStampReportItem(emptyGoodItem,0L,"88888");
                RegisterStampReportItem allTotal = new RegisterStampReportItem(emptyGoodItem,0L,"99999");
                result.add(allTotal);
                result.add(dailySampleItem);
            } else {
                for (GoodItem goodItem: allGoods){
                    Long val = service.buildRegisterStampDailySampleValue(org.getIdOfOrg(), startTime, endTime,
                            goodItem.getFullName());
                    RegisterStampReportItem dailySampleItem = new RegisterStampReportItem(goodItem,val,"88888");
                    RegisterStampReportItem allTotal = new RegisterStampReportItem(goodItem,val,"99999");
                    result.add(allTotal);
                    result.add(dailySampleItem);
                }
            }
            return new JRBeanCollectionDataSource(result);
        }
    }

    public RegisterStampReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }

    public RegisterStampReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
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
