package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampReportItem;
import ru.axetta.ecafe.processor.core.persistence.Org;

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
            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            List<GoodItem> allGoods = service.findAllGoods(org.getIdOfOrg());
            List<RegisterStampReportItem> result = new ArrayList<RegisterStampReportItem>();
            calendar.setTime(startTime);
            while (endTime.getTime()>calendar.getTimeInMillis()){
                String date = timeFormat.format(calendar.getTime());
                for (GoodItem goodItem: allGoods){
                    Long val = service.findNotNullGoodsFullNameByOrgByDayAndGoodEq(org.getIdOfOrg(),calendar.getTime(), goodItem.getFullName());
                    RegisterStampReportItem item = new RegisterStampReportItem(goodItem.getPathPart3(),goodItem.getPathPart4(),val,date);
                    RegisterStampReportItem total = new RegisterStampReportItem(goodItem.getPathPart3(),goodItem.getPathPart4(),val,"77777");
                    RegisterStampReportItem allTotal = new RegisterStampReportItem(goodItem.getPathPart3(),goodItem.getPathPart4(),val,"99999");
                    result.add(allTotal);
                    result.add(item);
                    result.add(total);
                }
                calendar.add(Calendar.DATE,1);
            }
            for (GoodItem goodItem: allGoods){
                Long val = service.findNotNullGoodsFullNameByOrgByDailySampleAndGoodEq(org.getIdOfOrg(),startTime, endTime, goodItem.getFullName());
                RegisterStampReportItem dailySampleItem = new RegisterStampReportItem(goodItem.getPathPart3(),goodItem.getPathPart4(),val,"88888");
                RegisterStampReportItem allTotal = new RegisterStampReportItem(goodItem.getPathPart3(),goodItem.getPathPart4(),val,"99999");
                result.add(allTotal);
                result.add(dailySampleItem);
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
