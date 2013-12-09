package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.DistributedObjectCurrentVersionRepository;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestPositionRepository;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOCurrentOrgVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodRequestsNotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodRequestsNotificationService.class);

    @Autowired
    private GoodRequestPositionRepository requestPositionRepository;

    @Autowired
    private DistributedObjectCurrentVersionRepository versionRepository;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Transactional
    public void checkModification() {
        Map<Long, DOCurrentOrgVersion> currentOrgVersion = versionRepository.findAllByGoodRequestPosition();
        Map<Long, Long> versionMap = requestPositionRepository.extractOrgOwnerAndVersion();
        for (Long orgOwner : versionMap.keySet()) {
            Long maxVersion = versionMap.get(orgOwner);
            DOCurrentOrgVersion version = currentOrgVersion.get(orgOwner);
            if (version == null) {
                DOCurrentOrgVersion doCurrentOrgVersion = new DOCurrentOrgVersion();
                doCurrentOrgVersion.setIdOfOrg(orgOwner);
                doCurrentOrgVersion.setLastVersion(maxVersion);
                doCurrentOrgVersion.setObjectId(DOCurrentOrgVersion.GOOD_REQUEST_POSITION);
                versionRepository.persist(doCurrentOrgVersion);
            } else {
                DOCurrentOrgVersion orgVersion = currentOrgVersion.get(orgOwner);
                if (maxVersion > version.getLastVersion()) {
                    HashMap.SimpleEntry<String, String> entity = extractOrgInfo(orgOwner);
                    Map<GoodRequest, List<GoodRequestPosition>> positions = requestPositionRepository
                            .findByIsNotNullLastUpdateAndGtVersionAndOrgOwner(orgVersion.getLastVersion(), orgOwner);
                    if (StringUtils.isNotEmpty(entity.getValue())) {
                        for (GoodRequest goodRequest : positions.keySet()) {
                            StringBuilder newValueHistory = new StringBuilder(
                                    "<table border=1 cellpadding=0 cellspacing=0><tr>");
                            newValueHistory.append("<th align=center>Наименование</th>");
                            newValueHistory.append("<th align=center>Дата изменения</th>");
                            newValueHistory.append("<th align=center>Общее количество</th>");
                            newValueHistory.append("<th align=center>Суточная проба</th>");
                            newValueHistory.append("<th align=center>История изменений</th>");
                            newValueHistory.append("</tr>");
                            for (GoodRequestPosition position : positions.get(goodRequest)) {
                                Date date = position.getLastUpdate() != null ? position.getLastUpdate()
                                        : position.getCreatedDate();
                                newValueHistory.append("<tr><td align=center>");
                                newValueHistory.append(position.getCurrentElementValue());
                                newValueHistory.append("</td><td align=center>");
                                newValueHistory.append(CalendarUtils.dateToString(date));
                                newValueHistory.append("</td><td align=center>");
                                final Long totalCount = position.getTotalCount();
                                newValueHistory.append(totalCount / 1000);
                                newValueHistory.append("</td><td align=center>");
                                final Long dailySampleCount = position.getDailySampleCount();
                                if (dailySampleCount != null) {
                                    newValueHistory.append(dailySampleCount / 1000);
                                } else {
                                    newValueHistory.append("-");
                                }
                                newValueHistory.append("</td><td align=center>");
                                final String updateHistory = position.getUpdateHistory();
                                if (StringUtils.isEmpty(updateHistory)) {
                                    newValueHistory.append("-");
                                } else {
                                    String[] vals = updateHistory.split(";");
                                    StringBuilder lastHistory = new StringBuilder("<table border=0 cellpadding=0 cellspacing=0><tr>");
                                    lastHistory.append("<td align=center>Дата изменения</td>");
                                    lastHistory.append("<td align=center>Общее количество</td>");
                                    lastHistory.append("<td align=center>Суточная проба</td>");
                                    lastHistory.append("</tr>");
                                    for (String str: vals){
                                        String[] lastHist = str.split(" ");
                                        lastHistory.append("<tr><td align=center>");
                                        lastHistory.append(lastHist[0]);
                                        lastHistory.append("</td><td align=center>");
                                        lastHistory.append(lastHist[1]);
                                        lastHistory.append("</td><td align=center>");
                                        if(lastHist.length>2){
                                            lastHistory.append(lastHist[2]);
                                        } else {
                                            lastHistory.append("-");
                                        }
                                        lastHistory.append("</td>");
                                    }
                                    newValueHistory.append(lastHistory.toString());
                                }
                                newValueHistory.append("</td></tr>");
                            }
                            newValueHistory.append("</table>");
                            String[] values = {
                                    "number", goodRequest.getNumber(), "shortName", entity.getKey(), "newValueHistory",
                                    newValueHistory.toString()};
                            eventNotificationService.sendEmail(entity.getValue(),
                                    EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                            LOGGER.debug("send");
                            orgVersion.setLastVersion(maxVersion);
                            versionRepository.persist(orgVersion);
                        }
                    }
                }
            }
        }

    }

    private AbstractMap.SimpleEntry<String, String> extractOrgInfo(Long orgOwner) {
        return versionRepository.extractShortNameAndEmailFromOrg(orgOwner);
    }

}
