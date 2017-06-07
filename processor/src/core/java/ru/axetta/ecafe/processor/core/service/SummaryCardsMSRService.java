/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 06.06.2017.
 */
@Component
public class SummaryCardsMSRService extends SummaryDownloadBaseService {
    Logger logger = LoggerFactory.getLogger(SummaryCardsMSRService.class);
    public static final String FOLDER_PROPERTY = "ecafe.processor.download.msr.summary-cards.folder";
    public static final String NODE = "ecafe.processor.download.msr.summary-cards.node";
    public static final String USER = "ecafe.processor.download.msr.summary-cards.user";
    public static final String PASSWORD = "ecafe.processor.download.msr.summary-cards.password";
    private static final List card_states;

    static {
        card_states = new ArrayList<Integer>();
        card_states.add(CardState.ISSUED.getValue());
        card_states.add(CardState.TEMPISSUED.getValue());
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
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            filename += "/" + df.format(endDate) + ".csv";

            String query_str = "select card.cardNo, client.clientGUID from Card card inner join card.client client "
                    + "where client.clientGroup.compositeIdOfClientGroup.idOfClientGroup NOT between :group_employees and :group_deleted "
                    + "and card.state in (:card_states) and card.validTime > :date";
            Query query = entityManager.createQuery(query_str);
            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_states", card_states);
            query.setParameter("date", startDate);
            //query.setMaxResults(10);

            List list = query.getResultList();

            List<String> result = new ArrayList<String>();
            result.add("Номер карты;GUID");
            for (Object o : list) {
                Object row[] = (Object[]) o;
                StringBuilder b = new StringBuilder();
                b.append(row[0]).append(";");
                b.append(row[1]);
                result.add(b.toString());
            }

            File file = new File(filename);
            FileUtils.writeLines(file, result);
        } catch (Exception e) {
            logger.error("Error build summary clients file for MSR", e);
        }
    }
}
