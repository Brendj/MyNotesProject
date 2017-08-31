/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampElectronicCollationReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 31.08.2017.
 */
public class RegisterStampNewElectronicCollationReport extends BasicReportForOrgJob{
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
    public static final String REPORT_NAME = "Реестр талонов "
            + "услуг по организации питания и обеспечения питьевого режима обучающихся";
    public static final String[] TEMPLATE_FILE_NAMES = {"RegisterStampElectronicCollationReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5, 38};


    public static final String PARAM_WITH_OUT_ACT_DISCREPANCIES = "includeActDiscrepancies";

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampNewElectronicCollationReport.class);

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
                    createDataSource(session, org, startTime, endTime));
            Date generateEndTime = new Date();
            return new RegisterStampNewElectronicCollationReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);

            String withOutActDiscrepanciesParam = (String) getReportProperties().get(PARAM_WITH_OUT_ACT_DISCREPANCIES);
            boolean withOutActDiscrepancies = false;
            if (withOutActDiscrepanciesParam!=null) {
                withOutActDiscrepancies = withOutActDiscrepanciesParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");

            List<RegisterStampElectronicCollationReportItem> result = service.findAllRegisterStampElectronicCollationItems(
                    org.getIdOfOrg(), startTime, endTime);

            List<Date> dateList = new ArrayList<Date>();

            List<RegisterStampElectronicCollationReportItem> reportItems = new ArrayList<RegisterStampElectronicCollationReportItem>();

            for (RegisterStampElectronicCollationReportItem reg : result) {

                if (!dateList.contains(CalendarUtils.truncateToDayOfMonth(reg.getDateTime()))) {
                    dateList.add(CalendarUtils.truncateToDayOfMonth(reg.getDateTime()));
                }

                RegisterStampElectronicCollationReportItem total = new RegisterStampElectronicCollationReportItem(
                        reg.getQty(), "Итого", "", CalendarUtils.addDays(endTime, 1), reg.getLevel1(), reg.getLevel2(), reg.getLevel3(), reg.getLevel4());
                reportItems.add(total);
            }

            result.addAll(reportItems);

            Date startTimeAddNew = startTime;

            if (result.isEmpty()) {

                while (endTime.getTime() > startTimeAddNew.getTime()) {

                    RegisterStampElectronicCollationReportItem reportItem = new RegisterStampElectronicCollationReportItem(
                            0L, timeFormat.format(startTimeAddNew), "", startTimeAddNew, "", "", "", "");

                    RegisterStampElectronicCollationReportItem total = new RegisterStampElectronicCollationReportItem(
                            0L, "Итого", "", CalendarUtils.addDays(endTime, 1), "", "", "", "");

                    result.add(reportItem);
                    result.add(total);

                    startTimeAddNew = CalendarUtils.addDays(startTimeAddNew, 1);
                }
            }

            Date startTimeAdd = startTime;

            while (endTime.getTime() > startTimeAdd.getTime()) {

                if (!dateList.contains(startTimeAdd)) {
                    RegisterStampElectronicCollationReportItem reportItem = new RegisterStampElectronicCollationReportItem(
                            0L, timeFormat.format(startTimeAdd), "", startTimeAdd, result.get(0).getLevel1(), result.get(0).getLevel2(), result.get(0).getLevel3(), result.get(0).getLevel4());

                    result.add(reportItem);
                }
                startTimeAdd = CalendarUtils.addDays(startTimeAdd, 1);
            }

            return new JRBeanCollectionDataSource(result);
        }

        public boolean confirmMessage(Session session, Date startDate, Date endDate, Long idOfOrg) {

            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);
            boolean b = service.findNotConfirmedTaloons(startDate, endDate, idOfOrg);
            return b;
        }
    }

    public RegisterStampNewElectronicCollationReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,

            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new RegisterStampNewElectronicCollationReport.Builder(templateFilename);
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
