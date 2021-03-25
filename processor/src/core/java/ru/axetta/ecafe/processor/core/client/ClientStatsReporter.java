/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import org.json.JSONException;
import org.json.JSONObject;
import ru.axetta.ecafe.processor.core.client.items.ClientMigrationItemInfo;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

@Component
public class ClientStatsReporter {
    
    @PersistenceContext(unitName = "reportsPU")
    EntityManager entityManager;


    @Transactional
    public String getStatsForClient(Client client, Date from, Date to) throws JSONException {
        from = DateUtils.truncate(from, Calendar.DAY_OF_MONTH);
        to = DateUtils.truncate(to, Calendar.DAY_OF_MONTH);
        Calendar startCal=new GregorianCalendar();
        Calendar endCal=new GregorianCalendar();
        startCal.setTime(from);
        startCal.set(Calendar.HOUR, 12);
        endCal.setTime(to);
        endCal.set(Calendar.HOUR, 12);
        ///
        int nDaysInPeriod = (int)( (endCal.getTime().getTime() - startCal.getTime().getTime()) / (1000 * 60 * 60 * 24));
        ///
        List<Object[]> clientInOrgTimes = DAOUtils.getClientInOrgTimes(entityManager, client.getIdOfClient(), from, to);
        int nDaysWithData=0; Long maxTime=0L, sumTime=0L;
        for (Object[] d : clientInOrgTimes) {
            long timeIn = ((Number)d[1]).longValue();
            if (timeIn>maxTime) maxTime=timeIn;
            sumTime+=timeIn;
            if (timeIn>0) nDaysWithData++;
        }
        JSONObject r = new JSONObject();
        JSONObject s0 = new JSONObject();
        s0.put("id", "0");
        s0.put("name", "Дней в выбранном периоде / из них в школе");
        s0.put("value", String.format("%d / %d", nDaysInPeriod, clientInOrgTimes.size()));
        r.append("stat", s0);
        JSONObject s1 = new JSONObject();
        s1.put("id", "1");
        s1.put("name", "Среднее/максимальное время нахождения в школе в день");
        s1.put("value", nDaysWithData==0?"-":((formatTimeSpan(sumTime/nDaysWithData)+" / "+formatTimeSpan(maxTime))));
        r.append("stat", s1);
        return r.toString();
    }

    private String formatTimeSpan(Long timeSpan) {
        long nMins = (timeSpan%(60*60))/60;
        long nHours = timeSpan/(60*60);
        return String.format("%d ч. %02d м.", nHours, nMins);
    }

    @Transactional
    public List<ClientMigrationItemInfo> reloadMigrationInfoByClient(Long idOfClient){
        TypedQuery<ClientMigration> clientMigrationTypedQuery = entityManager.createQuery("from ClientMigration where client.idOfClient=:idOfClient",ClientMigration.class);
        clientMigrationTypedQuery.setParameter("idOfClient",idOfClient);
        List<ClientMigration> clientMigrationList = clientMigrationTypedQuery.getResultList();
        List<ClientMigrationItemInfo> clientMigrationItemInfoList = new ArrayList<ClientMigrationItemInfo>(clientMigrationList.size());
        for (ClientMigration clientMigration: clientMigrationList){
            clientMigrationItemInfoList.add(new ClientMigrationItemInfo(clientMigration));
        }
        return clientMigrationItemInfoList;
    }
}
