/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.Data;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.MapKeyModel;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.ShortBuilding;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.StClass;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 06.04.18.
 */
public class BlockUnblockCardReport extends BasicReportForMainBuildingOrgJob {

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
    public static final String REPORT_NAME = "Детализированный отчет по посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"BlockUnblockCard.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};
    final public static String P_ID_OF_CLIENTS = "idOfClients";
    final public static String P_ALL_FRIENDLY_ORGS = "friendsOrg";

    private final static Logger logger = LoggerFactory.getLogger(BlockUnblockCardReport.class);


    public BlockUnblockCardReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

    public BlockUnblockCardReport() {

    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new BlockUnblockCardReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.roundToBeginOfDay(startTime);

            String idOfOrgString = StringUtils
                    .trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            Long idOfOrg = Long.parseLong(idOfOrgString);

            Org orgLoad = (Org) session.load(Org.class, idOfOrg);

            Set<Org> orgs = orgLoad.getFriendlyOrg();

            for (Org orgM : orgs) {
                if (orgM.isMainBuilding()) {
                    parameterMap.put("shortNameInfoService", orgM.getShortNameInfoService());
                    break;
                } else {
                    parameterMap.put("shortNameInfoService", orgLoad.getShortNameInfoService());
                }
            }

            parameterMap.put("beginDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));


            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, orgLoad, parameterMap));
            Date generateEndTime = new Date();
            return new BlockUnblockCardReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, orgLoad.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org,
                Map<String, Object> parameterMap) throws Exception {

            List<BlockUnblockItem> blockUnblockItemList = new ArrayList<>();

            //Это разблокирование всего
            Query queryUnblock = session.createSQLQuery("select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.address, ccra.firstname,ccra.lastname, ccra.middlename,\n"
                    + "ccg.groupname, null as firp, null as lastp, null as middp, ca.state as cardstate, ca.cardno, ca.cardprintedno,\n"
                    + "ccarold.createdate as blockdate, ccra.createdate as unblockdate\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idofclient\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "left join cf_cr_cardactionrequests ccarold on ccra.previdcardrequest = ccarold.idcardactionrequest\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is null and ccra.\"action\" = 1\n" + "union\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.address, cp.firstname, cp.surname as lastname, cp.secondname as middlename,\n"
                    + "ccg.groupname,cp1.firstname as firp,cp1.surname as lastp , cp1.secondname as middp,\n"
                    + "ca.state as cardstate, ca.cardno, ca.cardprintedno, ccarold.createdate as blockdate, ccra.createdate as unblockdate\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idclientchild\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                    + "left join cf_clients cc1 on cc1.idofclient = ccac.idofclient\n"
                    + "left join cf_persons cp1 on cp1.idofperson = cc1.idofperson\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "left join cf_cr_cardactionrequests ccarold on ccra.previdcardrequest = ccarold.idcardactionrequest\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is not null and ccra.\"action\" = 1");

            List rListUnblock = queryUnblock.list();
            for (Object o : rListUnblock) {
                Object[] row = (Object[]) o;
                Long idcardactionrequest = ((BigInteger) row[0]).longValue();
                String requestId = (String) row[1];
                String shortname = (String) row[2];
                String address = (String) row[3];
                String firstname = (String) row[4];
                String lastname = (String) row[5];
                String middlename = (String) row[6];
                String groupname = (String) row[7];
                String firp = (String) row[8];
                String lastp = (String) row[9];
                String middp = (String) row[10];
                Integer cardstate = (Integer) row[11];
                Long cardno = ((BigInteger) row[12]).longValue();
                Long cardprintedno = ((BigInteger) row[13]).longValue();
                Date blockdate = new Date(((Timestamp) row[14]).getTime());
                Date unblockdate = new Date(((Timestamp) row[15]).getTime());
                BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, shortname, address, firstname, lastname,
                        middlename, groupname, firp, lastp, middp, cardstate, cardno, cardprintedno, blockdate, unblockdate);
                blockUnblockItemList.add(blockUnblockItem);
            }

            //Это блокирование всего исключая то, что успешно разблокировано
            Query query = session.createSQLQuery("select * from (\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.address, ccra.firstname,ccra.lastname, ccra.middlename,\n"
                    + "ccg.groupname, null as firp, null as lastp, null as middp, ca.state as cardstate, ca.cardno, ca.cardprintedno, ccra.createdate as blockdate\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idofclient\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is null and ccra.\"action\" = 0\n" + "union\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.address, cp.firstname, cp.surname as lastname, cp.secondname as middlename,\n"
                    + "ccg.groupname,cp1.firstname as firp,cp1.surname as lastp , cp1.secondname as middp,\n"
                    + "ca.state as cardstate, ca.cardno, ca.cardprintedno, ccra.createdate as blockdate\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idclientchild\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                    + "left join cf_clients cc1 on cc1.idofclient = ccac.idofclient\n"
                    + "left join cf_persons cp1 on cp1.idofperson = cc1.idofperson\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is not null and ccra.\"action\" = 0) as vlocked\n"
                    + "where vlocked.requestid not in \n" + "(select ccra.requestid\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idofclient\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is null and ccra.\"action\" = 1\n" + "union\n" + "select ccra.requestid\n"
                    + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idclientchild\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                    + "left join cf_clients cc1 on cc1.idofclient = ccac.idofclient\n"
                    + "left join cf_persons cp1 on cp1.idofperson = cc1.idofperson\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is not null and ccra.\"action\" = 1)");

            List rList = query.list();

            for (Object o : rList) {
                Object[] row = (Object[]) o;
                Long idcardactionrequest = ((BigInteger) row[0]).longValue();
                String requestId = (String) row[1];
                String shortname = (String) row[2];
                String address = (String) row[3];
                String firstname = (String) row[4];
                String lastname = (String) row[5];
                String middlename = (String) row[6];
                String groupname = (String) row[7];
                String firp = (String) row[8];
                String lastp = (String) row[9];
                String middp = (String) row[10];
                Integer cardstate = (Integer) row[11];
                Long cardno = ((BigInteger) row[12]).longValue();
                Long cardprintedno = ((BigInteger) row[13]).longValue();
                Date blockdate = new Date(((Timestamp) row[14]).getTime());
                BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, shortname, address, firstname, lastname,
                        middlename, groupname, firp, lastp, middp, cardstate, cardno, cardprintedno, blockdate, null);
                blockUnblockItemList.add(blockUnblockItem);
            }
            return new JRBeanCollectionDataSource(blockUnblockItemList);
        }
    }
}
