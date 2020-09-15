/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
 * Created by voinov on 07.09.20.
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
    final public static String P_CARD_STATUS = "cardStatus";

    private final static Logger logger = LoggerFactory.getLogger(BlockUnblockCardReport.class);


    public BlockUnblockCardReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

    public BlockUnblockCardReport() {

    }

    public enum CardStateType {
        ALL("Все"),
        UNBLOCK("Только разблокированные"),
        BLOCK("Только заблокированные");
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private CardStateType(String description) {
            this.description = description;
        }
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
            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    new JRBeanCollectionDataSource(createDataSource(session)));
            Date generateEndTime = new Date();
            return new BlockUnblockCardReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<BlockUnblockItem> createDataSource(Session session) {

            List<BlockUnblockItem> blockUnblockItemList = new ArrayList<>();
            Org orgLoad;
            try {
                orgLoad = (Org) session.load(Org.class, Long.parseLong(StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG))));
            } catch (Exception e)
            {
                orgLoad = null;
            }

            List<Long> idOfClients = parseStringAsLongList(P_ID_OF_CLIENTS);
            Boolean allFriendlyOrgs = Boolean.parseBoolean(StringUtils.trimToEmpty(reportProperties.getProperty(P_ALL_FRIENDLY_ORGS)));
            String cardState = reportProperties.getProperty(P_CARD_STATUS);


            String filterOrgs = "";
            String filterClients = "";
            String filterStatus = "";
            if (orgLoad != null) {
                if (allFriendlyOrgs) {
                    for (Org org : orgLoad.getFriendlyOrg()) {
                        filterOrgs += "'" + org.getIdOfOrg() + "',";
                    }
                    filterOrgs = filterOrgs.substring(0, filterOrgs.length() - 1);
                } else {
                    filterOrgs = "'" + orgLoad.getIdOfOrg() + "'";
                }
                filterOrgs = " and co.idoforg in (" + filterOrgs + ") ";
            }
            if (idOfClients != null && !idOfClients.isEmpty())
            {
                for (Long idClient : idOfClients) {
                    filterClients += "'" + idClient + "',";
                }
                filterClients = filterClients.substring(0, filterClients.length() - 1);
                filterClients = " and cc.idofclient in (" + filterClients + ") ";
            }
            if (cardState != null)
            {
                if (cardState.equals(CardStateType.BLOCK.getDescription()))
                {
                    filterStatus = "  and ca.state = " + CardState.BLOCKED.getValue() + " ";
                }
                else
                {
                    if (cardState.equals(CardStateType.UNBLOCK.getDescription()))
                    {
                        filterStatus = "  and ca.state = " + CardState.ISSUED.getValue() + " ";
                    }
                }


            }
            //Это разблокирование всего
            Query queryUnblock = session.createSQLQuery("select ccra.idcardactionrequest, ccra.requestid, co.shortname, "
                    + "co.shortnameinfoservice, ccra.firstname,ccra.lastname, ccra.middlename,\n"
                    + "ccg.groupname, null as firp, null as lastp, null as middp, ca.state as cardstate, ca.cardno, ca.cardprintedno,\n"
                    + "ccarold.createdate as blockdate, ccra.createdate as unblockdate,ccra.\"comment\", \n" + "CASE\n"
                    + "    WHEN ccra.contingentid is not null THEN ccra.contingentid\n" + "    ELSE ccra.staffid\n"
                    + "  END idexternal, null as contractp\n" + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idofclient\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "left join cf_cr_cardactionrequests ccarold on ccra.previdcardrequest = ccarold.idcardactionrequest\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is null and ccra.\"action\" = 1\n" + "union\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.shortnameinfoservice, cp.firstname, cp.surname as lastname, cp.secondname as middlename,\n"
                    + "ccg.groupname,cp1.firstname as firp,cp1.surname as lastp , cp1.secondname as middp,\n"
                    + "ca.state as cardstate, ca.cardno, ca.cardprintedno, ccarold.createdate as blockdate, ccra.createdate as unblockdate,\n"
                    + "ccra.\"comment\", \n" + "CASE\n"
                    + "    WHEN ccra.contingentid is not null THEN ccra.contingentid\n" + "    ELSE ccra.staffid\n"
                    + "  END idexternal, null as contractp\n" + "from cf_cr_cardactionrequests ccra \n"
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
                    + "and ccac.idclientchild is not null and ccra.\"action\" = 1;");

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
                String operation = (String) row[16];
                String exId = (String) row[17];
                Long contractId;
                try {
                    contractId = ((BigInteger) row[18]).longValue();
                }
                catch (Exception e)
                {
                    contractId = null;
                }
                BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, blockdate, unblockdate, operation,
                        exId,firstname, lastname, middlename, groupname, contractId, firp, lastp, middp, shortname,
                        address,converterTypeCard(cardstate), cardno, cardprintedno);
                blockUnblockItemList.add(blockUnblockItem);

            }

            //Это блокирование всего исключая то, что успешно разблокировано
            Query query = session.createSQLQuery("select * from (\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.shortnameinfoservice, cp.firstname, cp.surname as lastname, cp.secondname as middlename,\n"
                    + "ccg.groupname, null as firp, null as lastp, null as middp, ca.state as cardstate, ca.cardno, ca.cardprintedno, ccra.createdate as blockdate,\n"
                    + "ccra.\"comment\", \n" + "CASE\n"
                    + "    WHEN ccra.contingentid is not null THEN ccra.contingentid\n" + "    ELSE ccra.staffid\n"
                    + "  END idexternal, null as contractp\n" + "from cf_cr_cardactionrequests ccra \n"
                    + "left join cf_cr_cardactionclient ccac on ccac.idcardactionrequest = ccra.idcardactionrequest\n"
                    + "left join cf_clients cc on cc.idofclient = ccac.idofclient\n"
                    + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                    + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                    + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                    + "left join cf_cards ca on ca.idofcard = ccac.idofcard\n"
                    + "where ccra.processed = true and ca.state is not null \n"
                    + "and ccac.idclientchild is null and ccra.\"action\" = 0\n" + "union\n"
                    + "select ccra.idcardactionrequest, ccra.requestid, co.shortname, co.shortnameinfoservice, cp.firstname, cp.surname as lastname, cp.secondname as middlename,\n"
                    + "ccg.groupname,cp1.firstname as firp,cp1.surname as lastp , cp1.secondname as middp,\n"
                    + "ca.state as cardstate, ca.cardno, ca.cardprintedno, ccra.createdate as blockdate,\n"
                    + "ccra.\"comment\", \n" + "CASE\n"
                    + "    WHEN ccra.contingentid is not null THEN ccra.contingentid\n" + "    ELSE ccra.staffid\n"
                    + "  END idexternal, cc1.contractid as contractp\n" + "from cf_cr_cardactionrequests ccra \n"
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
                String operation = (String) row[15];
                String exId = (String) row[16];
                Long contractId;
                try {
                    contractId = ((BigInteger) row[17]).longValue();
                }
                catch (Exception e)
                {
                    contractId = null;
                }
                BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, blockdate, null, operation,
                        exId,firstname, lastname, middlename, groupname, contractId, firp, lastp, middp, shortname,
                        address,converterTypeCard(cardstate), cardno, cardprintedno);
                blockUnblockItemList.add(blockUnblockItem);
            }

            ////////////////////
    //        //Это блокирование старого всего
    //Date test1 = new Date();
    //        List<BlockUnblockItem> blockUnblockItemListOLD = new ArrayList<>();
    //        Query queryOld = session.createSQLQuery("select ccra.idcardactionrequest, ccra.requestid, ccra.contingentid, ccra.staffid,  \n"
    //                + "ccra.\"comment\", ccg.groupname, cc.contractid, cp.secondname, cp.firstname, cp.surname, co.shortname, co.shortnameinfoservice, \n"
    //                + "ccrab.createdate as blockdate, ccra.createdate as unblockdate\n"
    //                + "from cf_cr_cardactionrequests ccra \n"
    //                + "left join cf_clients cc on cc.idofclient = ccra.idofclient\n"
    //                + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
    //                + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
    //                + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
    //                + "left join cf_cr_cardactionrequests ccrab on ccrab.requestid = ccra.requestid and ccrab.\"action\" = 0\n"
    //                + "where ccra.idofclient is not null and ccra.\"action\" = 1 and cc.contractid is not null\n"
    //                + "union\n"
    //                + "select ccra.idcardactionrequest, ccra.requestid, ccra.contingentid, ccra.staffid,  \n"
    //                + "ccra.\"comment\", ccg.groupname, cc.contractid, cp.secondname, cp.firstname, cp.surname, co.shortname, co.shortnameinfoservice, \n"
    //                + "ccra.createdate as blockdate, null as unblockdate\n" + "from cf_cr_cardactionrequests ccra \n"
    //                + "left join cf_clients cc on cc.idofclient = ccra.idofclient\n"
    //                + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
    //                + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
    //                + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
    //                + "where ccra.idofclient is not null and ccra.\"action\" = 0 and cc.contractid is not null and ccra.requestid not in (select ccra.requestid\n"
    //                + "from cf_cr_cardactionrequests ccra\n"
    //                + "where ccra.idofclient is not null and ccra.\"action\" = 1)");
    //
    //        List rListOld = queryOld.list();
    //
    //        for (Object o : rListOld) {
    //            Object[] row = (Object[]) o;
    //            Long idcardactionrequest = ((BigInteger) row[0]).longValue();
    //            String requestId = (String) row[1];
    //            String contingentId = (String) row[2];
    //            String staffId = (String) row[3];
    //            String operation = (String) row[4];
    //            String groupname = (String) row[5];
    //            Long contractId = ((BigInteger) row[6]).longValue();
    //            String lastname = (String) row[7];
    //            String firstname = (String) row[8];
    //            String middlename = (String) row[9];
    //            String shortname = (String) row[10];
    //            String shortnameinfoservice = (String) row[11];
    //            Date blockdate = new Date(((Timestamp) row[12]).getTime());
    //            Date unblockdate;
    //            try {
    //                unblockdate = new Date(((Timestamp) row[13]).getTime());
    //            } catch (Exception e) {
    //                unblockdate = null;
    //            }
    //
    //            if (staffId != null)
    //            {
    //                Client cl =  DAOUtils.findClientByContractId(session, contractId);
    //                if (cl.getCards() != null) {
    //                    for (Card card : cl.getCards()) {
    //                        if (card.getState() == CardState.BLOCKED.getValue()) {
    //
    //                            BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, blockdate, unblockdate,
    //                                    operation, staffId, firstname, lastname, middlename, groupname, contractId,"","","",
    //                                    shortname, shortnameinfoservice, converterTypeCard(card.getState()), card.getCardNo(),
    //                                    card.getCardPrintedNo());
    //                            blockUnblockItemListOLD.add(blockUnblockItem);
    //                        }
    //                    }
    //                }
    //            }
    //            else {
    //                if (contingentId != null) {
    //                    Client cl = DAOUtils.findClientByMeshGuid(session, contingentId);
    //                    if (cl != null) {
    //                        if (cl.getAgeTypeGroup() != null && StringUtils
    //                                .containsIgnoreCase(cl.getAgeTypeGroup(), "дошкол")) {
    //                            Client guard = DAOUtils.findClientByContractId(session, contractId);
    //                            if (guard != null) {
    //                                if (guard.getCards() != null) {
    //                                    for (Card card : guard.getCards()) {
    //                                        if (card.getState() == CardState.BLOCKED.getValue()) {
    //                                            BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId,
    //                                                    blockdate, unblockdate, operation, contingentId, cl.getPerson().getFirstName(), cl.getPerson().getSurname(),
    //                                                    cl.getPerson().getSecondName(), groupname, contractId,
    //                                                    guard.getPerson().getFirstName(), guard.getPerson().getSurname(),
    //                                                    guard.getPerson().getSecondName(), shortname,
    //                                                    shortnameinfoservice, converterTypeCard(card.getState()), card.getCardNo(), card.getCardPrintedNo());
    //                                            blockUnblockItemListOLD.add(blockUnblockItem);
    //                                        }
    //                                    }
    //                                }
    //                            }
    //                        } else {
    //                            if (cl.getCards() != null) {
    //                                for (Card card : cl.getCards()) {
    //                                    if (card.getState() == CardState.BLOCKED.getValue()) {
    //
    //                                        BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId,
    //                                                blockdate, unblockdate, operation, contingentId, firstname, lastname,
    //                                                middlename, groupname, contractId, "", "", "", shortname,
    //                                                shortnameinfoservice, converterTypeCard(card.getState()), card.getCardNo(),
    //                                                card.getCardPrintedNo());
    //                                        blockUnblockItemListOLD.add(blockUnblockItem);
    //                                    }
    //                                }
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //
    //        }
    //        Date test2 = new Date();
    //
    //        blockUnblockItemList.addAll(blockUnblockItemListOLD);
            ///////////////////////
            Collections.sort(blockUnblockItemList, new Comparator<BlockUnblockItem>() {
                public int compare(BlockUnblockItem o1, BlockUnblockItem o2) {
                    if(o1.getRequestId().equals(o2.getRequestId())){
                        return 0;
                    } else if(Long.valueOf(o1.getRequestId()) < Long.valueOf(o2.getRequestId())){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            return blockUnblockItemList;
        }

        private String converterTypeCard (Integer type)
        {
            if (CardState.ISSUED.getValue() == type)
                return CardState.ISSUED.getDescription();
            if (CardState.BLOCKED.getValue() == type)
                return CardState.BLOCKED.getDescription();
            if (CardState.TEMPBLOCKED.getValue() == type)
                return CardState.TEMPBLOCKED.getDescription();
            if (CardState.TEMPISSUED.getValue() == type)
                return CardState.TEMPISSUED.getDescription();
            if (CardState.FREE.getValue() == type)
                return CardState.FREE.getDescription();
            return CardState.UNKNOWN.getDescription();
        }

        private List<Long> parseStringAsLongList(String propertyName) {
            String propertyValueString = reportProperties.getProperty(propertyName);
            String[] propertyValueArray = StringUtils.split(propertyValueString, ',');
            List<Long> propertyValueList = new ArrayList<Long>();
            for (String propertyValue : propertyValueArray) {
                try {
                    propertyValueList.add(Long.parseLong(propertyValue));
                } catch (NumberFormatException e) {
                    logger.error(String.format("Unable to parse propertyValue: property = %s, value = %s", propertyName,
                            propertyValue), e);
                }
            }
            return propertyValueList;
        }
    }
}
