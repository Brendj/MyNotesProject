/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.utils.TypesOfCardService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.11.14
 * Time: 14:08
 */
public class TypesOfCardReport extends BasicReportForAllOrgJob {

    public static final String PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS = "includeSummaryByDistrict";
    public static final String PARAM_CLIENT_GROUP = "clientGroup";
    public static final String PARAM_GROUP_NAME = "groupName";

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReport.class);

    public TypesOfCardReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, null);
    }

    public TypesOfCardReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new TypesOfCardReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        String subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        return new Builder(templateFilename, subReportDir);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private final String subReportDir;

        public Builder(String templateFilename, String subReportDir) {
            this.templateFilename = templateFilename;
            this.subReportDir = subReportDir;
        }

        public BasicReportJob build(Session session, Date startTime, Calendar calendar) throws Exception {
            String clientGroupName = getReportProperties().getProperty(PARAM_GROUP_NAME);

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("groupName", clientGroupName);
            parameterMap.put("SUBREPORT_DIR", subReportDir);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime));
            long generateDuration = generateTime.getTime();
            return new TypesOfCardReport(generateTime, generateDuration, jasperPrint, startTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime) throws Exception {
            List<TypesOfCardReportItem> result = new ArrayList<TypesOfCardReportItem>();

            int ac = Card.ACTIVE_STATE; // активная карта
            int lc = Card.LOCKED_STATE; // заблокированная карта

            TypesOfCardService service = new TypesOfCardService();
            service.setSession(session);

            String withOutSummaryByDistrictParam = getReportProperties()
                    .getProperty(PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS);
            boolean withOutSummaryByDistrict = false;
            if (withOutSummaryByDistrictParam != null) {
                withOutSummaryByDistrict = withOutSummaryByDistrictParam.trim().equalsIgnoreCase("true");
            }

            String clientGroup = getReportProperties().getProperty(PARAM_CLIENT_GROUP);
            Long clientGroupId = Long.parseLong(clientGroup);

            String groupRestrict = "";
            String groupIds = "";

            if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
                if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS)) {
                    List<Long> clientGroupIdsList = ClientGroupMenu.getNotStudent();
                    groupRestrict = " and cfcl.idofclientgroup not in (";

                    for (int i = 0; i < clientGroupIdsList.size() - 1; i++) {
                        groupIds = groupIds + clientGroupIdsList.get(i) + ", ";
                    }
                    groupIds = groupIds + clientGroupIdsList.get(clientGroupIdsList.size() - 1);
                    groupRestrict = groupRestrict + groupIds + ")";
                } else {
                    groupRestrict = " and cfcl.idofclientgroup in (" + clientGroupId + ")";
                }
            }

            List<String> districtNames = service.loadDistrictNames();

            for (String district : districtNames) {
                Long stateServiceAct = service.getStatByDistrictName(district, "1", ac, startTime, groupRestrict);
                Long stateServiceActNot = service.getStatByDistrictName(district, "1", lc, startTime, groupRestrict);

                Long stateScuAct = service.getStatByDistrictName(district, "3", ac, startTime, groupRestrict);
                Long stateScuActNot = service.getStatByDistrictName(district, "3", lc, startTime, groupRestrict);

                Long stateOthAct = service.getStatByDistrictName(district, "0,2,4", ac, startTime, groupRestrict);
                Long stateOthActNot = service.getStatByDistrictName(district, "0,2,4", lc, startTime, groupRestrict);

                Long sumStateAct = stateServiceAct + stateScuAct + stateOthAct;
                Long sumStateNot = stateServiceActNot + stateScuActNot + stateOthActNot;

                TypesOfCardReportItem typesOfCardReportItem = new TypesOfCardReportItem(district, stateServiceAct,
                        stateServiceActNot, stateScuAct, stateOthActNot, stateOthAct, stateOthActNot, sumStateAct,
                        sumStateNot);

                if (!withOutSummaryByDistrict) {
                    //Лист по орг.
                    List<TypesOfCardSubreportItem> typesOfCardSubreportItemList = new ArrayList<TypesOfCardSubreportItem>();

                    List<TypesOfCardOrgItem> idListByDistrict = service.getAllOrgsByDistrictName(district);

                    for (TypesOfCardOrgItem typesOfCardOrgItem : idListByDistrict) {

                        Long stateServiceActSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "1", ac, startTime, groupRestrict);
                        Long stateServiceActNotSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "1", lc, startTime, groupRestrict);

                        Long stateScuActSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "3", ac, startTime, groupRestrict);
                        Long stateScuActNotSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "3", lc, startTime, groupRestrict);

                        Long stateOthActSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "0,2,4", ac, startTime, groupRestrict);
                        Long stateOthActNotSub = service
                                .getStatByOrgId(typesOfCardOrgItem.getIdOfOrg(), "0,2,4", lc, startTime, groupRestrict);

                        Long sumStateActSub = stateServiceActSub + stateScuActSub + stateOthActSub;
                        Long sumStateNotSub = stateServiceActNotSub + stateScuActNotSub + stateOthActNotSub;

                        TypesOfCardSubreportItem typesOfCardSubreportItem = new TypesOfCardSubreportItem(
                                typesOfCardOrgItem.getShortName(), typesOfCardOrgItem.getAddress(), stateServiceActSub,
                                stateServiceActNotSub, stateScuActSub, stateOthActNotSub, stateOthActSub,
                                stateOthActNotSub, sumStateActSub, sumStateNotSub);
                        typesOfCardSubreportItemList.add(typesOfCardSubreportItem);
                    }
                    //установка листа по округу
                    typesOfCardReportItem.setTypesOfCardSubeportItems(typesOfCardSubreportItemList);
                }
                result.add(typesOfCardReportItem);
            }

            return new JRBeanCollectionDataSource(result);
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return build(session, startTime, calendar);
        }
    }

}
