/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.enterevents;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shamil
 * Date: 14.08.14
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class EnterEventsRepository extends AbstractJpaDao<Org> {
    private final static Logger logger = LoggerFactory.getLogger(EnterEventsRepository.class);

    public static EnterEventsRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(EnterEventsRepository.class);
    }

    public List<DAOEnterEventSummaryModel> getEnterEventsSummary(){
        Calendar begin = Calendar.getInstance();
        CalendarUtils.truncateToDayOfMonth(begin);

         List<Object[]> tempList = (ArrayList)entityManager.createNativeQuery("SELECT  e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup  "
                + "FROM cf_enterevents e "
                + "LEFT JOIN cf_clients c ON e.idofclient = c.idofclient "
                + " WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                + "ORDER BY e.idoforg, e.idofclient, e.evtdatetime DESC\n")
                .setParameter("startDateTime", begin.getTimeInMillis())
                .setParameter("endDateTime", Calendar.getInstance().getTimeInMillis())
                 //.setParameter("startDateTime", 1355097600000L)
                //.setParameter("endDateTime", 1355183999000L)
                .getResultList();
        List<DAOEnterEventSummaryModel> result = new ArrayList<DAOEnterEventSummaryModel>();
        for (Object[] temp:tempList){

            DAOEnterEventSummaryModel entry = new DAOEnterEventSummaryModel();
            entry.setIdOfClient(temp[0]!= null ?((BigInteger) temp[0]).longValue() : null);
            entry.setIdOfOrg(temp[1] != null ? ((BigInteger) temp[1]).longValue() : null);
            entry.setPassDirection((Integer) temp[2]);
            entry.setEventCode((Integer) temp[3]);
            entry.setIdofTempcard(temp[4] != null ? ((BigInteger) temp[4]).longValue() : null);
            entry.setEvtdatetime(temp[5] != null ? ((BigInteger) temp[5]).longValue() : null);
            entry.setIdofvisitor(temp[6] != null ? ((BigInteger) temp[6]).longValue() : null);
            entry.setVisitorFullName((String) temp[7]);
            entry.setIdOfClientGroup(temp[8] != null ? ((BigInteger) temp[8]).longValue() : null);
            result.add(entry);
        }
        return result;

    }
}
