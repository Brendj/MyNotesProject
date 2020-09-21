/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BlockUnblockCardReport;
import ru.axetta.ecafe.processor.core.report.BlockUnblockItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.03.16
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
public class CardBlockUnblockService {


    final static String SYNC_BLOCKCARD = "SyncCard";
    public static final String UNBLOCK_COMMENT = "Режим самоизоляции снят";
    public static final String BLOCK_COMMENT = "Для владельца идентификатора действует режим самоизоляции";

    public static class SyncBlockOld implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(CardBlockUnblockService.class)
                        .createOldDateSave(persistenceSession);
                persistenceTransaction.commit();
            } catch (Exception e) {
            }
        }
    }


    public void scheduleSync() throws Exception {
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.card.blocked.cron", "0 0 * ? * *");
        try {
            JobDetail jobDetailSync = new JobDetail(SYNC_BLOCKCARD, Scheduler.DEFAULT_GROUP, SyncBlockOld.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(SYNC_BLOCKCARD, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(SYNC_BLOCKCARD, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(SYNC_BLOCKCARD, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);

            scheduler.start();
        } catch (Exception e) {
        }
    }

    public void createOldDateSave(Session session) {
        if (RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.card.blocked.work", "TRUE").equals("FALSE"))
            return;
        List<BlockUnblockItem> blockUnblockItemListOLD = new ArrayList<>();
        Query queryOld = session.createSQLQuery(
                "select ccra.idcardactionrequest, ccra.requestid, ccra.contingentid, ccra.staffid,  \n"
                        + "ccra.\"comment\", ccg.groupname, cc.contractid, cp.secondname, cp.firstname, cp.surname, co.shortnameinfoservice, co.address, \n"
                        + "ccrab.createdate as blockdate, ccra.createdate as unblockdate, cc.idofclient, co.idoforg \n"
                        + "from cf_cr_cardactionrequests ccra \n"
                        + "left join cf_clients cc on cc.idofclient = ccra.idofclient\n"
                        + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                        + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                        + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                        + "left join cf_cr_cardactionrequests ccrab on ccrab.requestid = ccra.requestid and ccrab.\"action\" = 0\n"
                        + "where ccra.idofclient is not null and ccra.\"action\" = 1 and cc.contractid is not null\n"
                        + "union\n"
                        + "select ccra.idcardactionrequest, ccra.requestid, ccra.contingentid, ccra.staffid,  \n"
                        + "ccra.\"comment\", ccg.groupname, cc.contractid, cp.secondname, cp.firstname, cp.surname, co.shortname, co.shortnameinfoservice, \n"
                        + "ccra.createdate as blockdate, null as unblockdate, cc.idofclient, co.idoforg \n"
                        + "from cf_cr_cardactionrequests ccra \n"
                        + "left join cf_clients cc on cc.idofclient = ccra.idofclient\n"
                        + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                        + "left join cf_orgs co on co.idoforg = cc.idoforg\n"
                        + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = cc.idoforg\n"
                        + "where ccra.idofclient is not null and ccra.\"action\" = 0 and cc.contractid is not null and ccra.requestid not in (select ccra.requestid\n"
                        + "from cf_cr_cardactionrequests ccra\n"
                        + "where ccra.idofclient is not null and ccra.\"action\" = 1)");

        List rListOld = queryOld.list();
        Integer count;
        try {
            count = Integer.valueOf(DAOService.getInstance().getLastCountBlocked());
        } catch (Exception e) {
            count = 0;
        }
        if (count == rListOld.size()) {
            return;
        }
        DAOService.getInstance()
                .setOnlineOptionValue(String.valueOf(rListOld.size()), Option.OPTION_LAST_COUNT_CARD_BLOCK);

        for (Object o : rListOld) {
            Object[] row = (Object[]) o;
            Long idcardactionrequest = ((BigInteger) row[0]).longValue();
            String requestId = (String) row[1];
            String contingentId = (String) row[2];
            String staffId = (String) row[3];
            String operation = (String) row[4];
            String groupname = (String) row[5];
            Long contractId = ((BigInteger) row[6]).longValue();
            String lastname = (String) row[7];
            String firstname = (String) row[8];
            String middlename = (String) row[9];
            String shortname = (String) row[10];
            String adress = (String) row[11];
            Date blockdate = new Date(((Timestamp) row[12]).getTime());
            Date unblockdate;
            try {
                unblockdate = new Date(((Timestamp) row[13]).getTime());
            } catch (Exception e) {
                unblockdate = null;
            }
            Long idofclient = ((BigInteger) row[14]).longValue();
            Long idoforg = ((BigInteger) row[15]).longValue();


            if (staffId != null) {
                Client cl = DAOUtils.findClientByContractId(session, contractId);
                if (cl != null) {
                    if (cl.getCards() != null) {
                        for (Card card : cl.getCards()) {
                            if (card.getLockReason() != null) {
                                if ((card.getState() == CardState.BLOCKED.getValue() && card.getLockReason().equals(BLOCK_COMMENT)) || (card.getState() == CardState.ISSUED.getValue()
                                        && card.getLockReason().equals(UNBLOCK_COMMENT))) {

                                    BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId, blockdate,
                                            unblockdate, operation, staffId, firstname, lastname, middlename, groupname,
                                            contractId, "", "", "", shortname, adress, converterTypeCard(card.getState()), card.getCardNo(),
                                            card.getCardPrintedNo(), idofclient, idoforg);
                                    blockUnblockItemListOLD.add(blockUnblockItem);
                                }
                            }
                        }
                    }
                }
            } else {
                if (contingentId != null) {
                    Client cl = DAOUtils.findClientByMeshGuid(session, contingentId);
                    if (cl != null) {
                        if (cl.getAgeTypeGroup() != null && StringUtils
                                .containsIgnoreCase(cl.getAgeTypeGroup(), "дошкол")) {
                            Client guard = DAOUtils.findClientByContractId(session, contractId);
                            if (guard != null) {
                                if (guard.getCards() != null) {
                                    for (Card card : guard.getCards()) {
                                        if (card.getLockReason() != null) {
                                            if ((card.getState() == CardState.BLOCKED.getValue() && card.getLockReason()
                                                    .equals(BLOCK_COMMENT)) || (card.getState() == CardState.ISSUED.getValue() && card
                                                    .getLockReason().equals(UNBLOCK_COMMENT))) {
                                                BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId,
                                                        blockdate, unblockdate, operation, contingentId, cl.getPerson().getFirstName(), cl.getPerson().getSurname(),
                                                        cl.getPerson().getSecondName(), groupname, contractId,
                                                        guard.getPerson().getFirstName(), guard.getPerson().getSurname(),
                                                        guard.getPerson().getSecondName(), shortname,
                                                        adress, converterTypeCard(card.getState()), card.getCardNo(),
                                                        card.getCardPrintedNo(), idofclient, idoforg);
                                                blockUnblockItemListOLD.add(blockUnblockItem);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (cl.getCards() != null) {
                                for (Card card : cl.getCards()) {
                                    if (card.getLockReason() != null) {
                                        if ((card.getState() == CardState.BLOCKED.getValue() && card.getLockReason().equals(BLOCK_COMMENT)) || (
                                                card.getState() == CardState.ISSUED.getValue() && card.getLockReason().equals(UNBLOCK_COMMENT))) {

                                            BlockUnblockItem blockUnblockItem = new BlockUnblockItem(requestId,
                                                    blockdate, unblockdate, operation, contingentId, firstname, lastname, middlename,
                                                    groupname, contractId, "", "", "", shortname, adress,
                                                    converterTypeCard(card.getState()), card.getCardNo(), card.getCardPrintedNo(),
                                                    idofclient, idoforg);
                                            blockUnblockItemListOLD.add(blockUnblockItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Query query;
        query = session.createSQLQuery("TRUNCATE table cf_cr_cardactionitems");
        query.executeUpdate();
        for (BlockUnblockItem blockUnblockItem : blockUnblockItemListOLD) {
            query = session.createSQLQuery(
                    "insert into cf_cr_cardactionitems(requestId, blockdate, unblockdate, operation, "
                            + "extClientId, firstname, lastname, middlename, groupname, contractIdp, firp, lastp, middp, shortname,"
                            + "address, cardstate, cardno, cardprintedno, idofclient, idoforg) "
                            + "values(:requestId, :blockdate, :unblockdate, :operation, "
                            + ":extClientId, :firstname, :lastname, :middlename, :groupname, :contractIdp, "
                            + ":firp, :lastp, :middp, :shortname, "
                            + " :address, :cardstate, :cardno, :cardprintedno, :idofclient,"
                            + ":idoforg)");
            query.setParameter("requestId", blockUnblockItem.getRequestId());
            query.setParameter("blockdate", blockUnblockItem.getBlockdate().getTime());
            if (blockUnblockItem.getUnblockdate() == null) {
                query.setParameter("unblockdate", 0L);
            } else {
                query.setParameter("unblockdate", blockUnblockItem.getUnblockdate().getTime());
            }
            query.setParameter("operation", blockUnblockItem.getOperation());
            query.setParameter("extClientId", blockUnblockItem.getExtClientId());
            query.setParameter("firstname", blockUnblockItem.getFirstname());
            query.setParameter("lastname", blockUnblockItem.getLastname());
            query.setParameter("middlename", blockUnblockItem.getMiddlename());
            query.setParameter("groupname", blockUnblockItem.getGroupname());
            query.setParameter("contractIdp", blockUnblockItem.getContractIdp());
            query.setParameter("firp", blockUnblockItem.getFirp());
            query.setParameter("lastp", blockUnblockItem.getLastp());
            query.setParameter("middp", blockUnblockItem.getMiddp());
            query.setParameter("shortname", blockUnblockItem.getShortname());
            query.setParameter("address", blockUnblockItem.getAddress());
            query.setParameter("cardstate", blockUnblockItem.getCardstate());
            if (blockUnblockItem.getCardno() == null) {
                query.setParameter("cardno", 0L);
            } else {
                query.setParameter("cardno", blockUnblockItem.getCardno());
            }
            if (blockUnblockItem.getCardprintedno() == null) {
                query.setParameter("cardprintedno", 0L);
            } else {
                query.setParameter("cardprintedno", blockUnblockItem.getCardprintedno());
            }
            query.setParameter("idofclient", blockUnblockItem.getIdofclient());
            query.setParameter("idoforg", blockUnblockItem.getIdoforg());
            query.executeUpdate();
        }
    }

    public static String converterTypeCard(Integer type) {
        if (CardState.ISSUED.getValue() == type) {
            return CardState.ISSUED.getDescription();
        }
        if (CardState.BLOCKED.getValue() == type) {
            return CardState.BLOCKED.getDescription();
        }
        if (CardState.TEMPBLOCKED.getValue() == type) {
            return CardState.TEMPBLOCKED.getDescription();
        }
        if (CardState.TEMPISSUED.getValue() == type) {
            return CardState.TEMPISSUED.getDescription();
        }
        if (CardState.FREE.getValue() == type) {
            return CardState.FREE.getDescription();
        }
        return CardState.UNKNOWN.getDescription();
    }
}
