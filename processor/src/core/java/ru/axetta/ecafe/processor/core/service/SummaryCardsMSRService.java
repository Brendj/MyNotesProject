/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.FTPUploader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 06.06.2017.
 */
@Component
public class SummaryCardsMSRService extends SummaryDownloadBaseService {
    Logger logger = LoggerFactory.getLogger(SummaryCardsMSRService.class);
    public static final String FOLDER_PROPERTY = "ecafe.processor.download.msr.folder";
    public static final String NODE = "ecafe.processor.download.msr.node";
    public static final String USER = "ecafe.processor.download.msr.user"; //?
    public static final String PASSWORD = "ecafe.processor.download.msr.password"; //?
    public static final String FTP_SERVER = "ecafe.processor.download.msr.ftp.server";
    public static final String FTP_PORT = "ecafe.processor.download.msr.ftp.port";
    public static final String FTP_USER = "ecafe.processor.download.msr.ftp.user";
    public static final String FTP_PASSWORD = "ecafe.processor.download.msr.ftp.password";
    public static final String FTP_FOLDER = "ecafe.processor.download.msr.ftp.folder";
    private static final List card_states;
    private static final List before_school_group;
    private static final String FILENAME_PREFIX = "ISPP_CARDS_";
    private static final String FILENAME_PREFIX_CLIENT = "ISPP_CARDS_CLIENTS_";

    static {
        card_states = new ArrayList<Integer>();
        card_states.add(CardState.ISSUED.getValue());
        card_states.add(CardState.TEMPISSUED.getValue());
    }
    static {
        before_school_group = new ArrayList<String>();
        before_school_group.add(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL]);
        before_school_group.add(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_OUT]);
        before_school_group.add(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_STEP]);
    }

    protected String getNode() {
        return NODE;
    }

    @Override
    public void run() {
        if (!isOn()) {
            return;
        }
        Date endDate = CalendarUtils.endOfDay(new Date());
        Date startDate = CalendarUtils.truncateToDayOfMonth(new Date());
        run(startDate, endDate);
    }

    public void run(Date startDate, Date endDate) throws RuntimeException {
        logger.info("Start make summary clients file for MSR");
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(FOLDER_PROPERTY, null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", FOLDER_PROPERTY));
            }
            SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
            filename += "/" + FILENAME_PREFIX + df.format(endDate) + ".csv";

            String query_str = "select card.cardNo, client.clientGUID from Card card inner join card.client client "
                    + "where client.clientGroup.compositeIdOfClientGroup.idOfClientGroup NOT between :group_employees and :group_deleted "
                    + "and card.state in (:card_states) and card.validTime > :date and client.ageTypeGroup not in :ageTypeGroupList";
            Query query = entityManager.createQuery(query_str);
            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_states", card_states);
            query.setParameter("date", startDate);
            String[] beforeSchoolTypeGroups = {Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_OUT],
                                               Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_STEP],
                                               Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL]};
            query.setParameter("ageTypeGroupList", Arrays.asList(beforeSchoolTypeGroups));
            //query.setMaxResults(10);

            List list = query.getResultList();

            List<String> result = new ArrayList<String>();
            result.add("id\tuid\tguid");
            long counter = 1;
            for (Object o : list) {
                Object row[] = (Object[]) o;
                if (!StringUtils.isEmpty((String)row[1])) {
                    Long cardId = (Long)row[0];
                    cardId = convertCardId(cardId);
                    StringBuilder b = new StringBuilder();
                    b.append(counter).append("\t").append(cardId).append("\t").append(row[1]);
                    result.add(b.toString());
                    counter++;
                }
            }
            uploadFile (filename, result);
            culture (startDate, endDate);
        } catch (Exception e) {
            logger.error("Error build and upload summary clients file for MSR", e);
        }
    }

    public void culture(Date startDate, Date endDate) throws RuntimeException {
        logger.info("Start culture summary task");
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(FOLDER_PROPERTY, null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", FOLDER_PROPERTY));
            }
            SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
            filename += "/" + FILENAME_PREFIX_CLIENT + df.format(endDate) + ".csv";

            String query_str = "select card.cardNo, client.clientGUID from Card card inner join card.client client "
                    + "where client.clientGroup.compositeIdOfClientGroup.idOfClientGroup NOT between :group_employees and :group_deleted "
                    + "and card.state in (:card_states) and card.validTime > :date "
                    + "and client.ageTypeGroup in :schoolchild and client.clientGUID is not null";

            Query query = entityManager.createQuery(query_str);

            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_states", card_states);
            query.setParameter("schoolchild", Arrays.asList(Client.GROUP_NAME_SCHOOL));
            query.setParameter("date", startDate);

            List<String> result = new ArrayList<String>();

            result = formatedList (query.getResultList(), result, false, null);


            query_str = " select distinct cf_cards.cardno, cf_clients.clientguid, cf_cards.ValidDate\n"
                    + " from cf_clients\n"
                    + " inner join cf_client_guardian on cf_client_guardian.idofchildren = cf_clients.idofclient\n"
                    + " inner join cf_cards on cf_client_guardian.idofguardian = cf_cards.idofclient\n"
                    + " inner join cf_clientgroups on cf_clientgroups.idofclientgroup = cf_clients.idofclientgroup and cf_clientgroups.idoforg = cf_clients.idoforg\n"
                    + " where cf_clients.idofclientgroup not between :group_employees and :group_deleted\n"
                    + " and cf_clients.clientguid is not null  \n"
                    + " and cf_cards.state in (:card_states)  \n"
                    + " and cf_client_guardian.deletedstate is false and cf_client_guardian.disabled = 0 \n"
                    + " and cf_clients.agetypegroup in (:schoolchild)";

            query = entityManager.createNativeQuery(query_str);

            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_states", card_states);
            query.setParameter("schoolchild", before_school_group);

            result = formatedList (query.getResultList(), result, true, startDate);
            uploadFile (filename, result);

        } catch (Exception e) {
            logger.error("Error build and upload summary clients file for MSR", e);
        }
    }

    private void uploadFile (String filename, List<String>  result) throws IOException {
        logger.info("Start upload file to ftp task");
        File file = new File(filename);
        FileUtils.writeLines(file, result);

        String server = RuntimeContext.getInstance().getPropertiesValue(FTP_SERVER, null);
        int port = RuntimeContext.getInstance().getPropertiesValue(FTP_PORT, 0);
        String user = RuntimeContext.getInstance().getPropertiesValue(FTP_USER, null);
        String password = RuntimeContext.getInstance().getPropertiesValue(FTP_PASSWORD, null);
        String remoteFolder = RuntimeContext.getInstance().getPropertiesValue(FTP_FOLDER, null);
        if (server != null && port != 0 && user != null && password != null && remoteFolder != null) {
            FTPUploader ftpUploader = new FTPUploader(server, port, user, password);
            boolean success = ftpUploader.uploadTextFile(filename, remoteFolder);
            logger.info(success ? "MSR upload file successful" : "MSR upload file error");
        }
        if (file.exists()) {
            file.delete();
        }
    }

    private List<String> formatedList (List list, List<String>  result, boolean parent, Date startDate)
    {
        Long time = 0L;
        if (startDate != null)
            time = startDate.getTime();
        for (Object o : list) {
            Object row[] = (Object[]) o;
            //guid не пустой
            if (!StringUtils.isEmpty((String)row[1])) {
                Long cardId;
                if (parent)
                    cardId = ((BigInteger) row[0]).longValue();
                else
                    cardId = (Long) row[0];
                cardId = convertCardId(cardId);
                StringBuilder b = new StringBuilder();
                if (parent) {
                    if ((((BigInteger) row[2]).longValue() > time)) {
                        b.append(row[1]).append("\t").append(cardId).append("\t").append("1");
                        result.add(b.toString());
                    }
                }
                else
                {
                    b.append(row[1]).append("\t").append(cardId).append("\t").append("0");
                    result.add(b.toString());
                }
            }
        }
        return result;
    }

    public static Long convertCardId(Long cardId) {
        String hex = Long.toHexString(cardId);
        if (cardId > 0xFFFFFFFFL) {     // 7 byte cards
            return cardId >> 24;
        } else {                        // 4 byte cards
            while (hex.length() < 14) {
                hex = "0".concat(hex);
            }
            StringBuilder sb = new StringBuilder(hex).reverse();
            hex = "0x" + sb.charAt(1) + sb.charAt(0) + sb.charAt(3) + sb.charAt(2) +
                    sb.charAt(5) + sb.charAt(4) + sb.charAt(7) + sb.charAt(6);
            return Long.decode(hex);
        }
    }
}
