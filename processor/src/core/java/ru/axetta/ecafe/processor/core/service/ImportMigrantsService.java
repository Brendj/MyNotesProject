/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("ImportMigrantsService")
@Scope("singleton")
public class ImportMigrantsService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportMigrantsFileService.class);

    public void run() throws Exception {
        if (!isOn())
            return;
        loadMigrants();
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.esz.migrants.service.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void loadMigrants() throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            logger.info("Start fill migrants table");
            long begin = System.currentTimeMillis();
            List<ESZMigrantsRequest> eszMigrantsRequestList = MigrantsUtils.getAllESZMigrantsRequests(session);

            Date date = new Date();

            for (ESZMigrantsRequest request : eszMigrantsRequestList) {
                Migrant migrant = MigrantsUtils.getMigrantRequestByGuidAndGroupId(session, request.getClientGuid(),
                        request.getIdOfServiceClass());

                if (null == migrant) {
                    List<Migrant> migrants = MigrantsUtils.getMigrantRequestsByExternalIdAndGroupId(session, request.getIdOfESZ(),
                            request.getIdOfServiceClass());
                    if (migrants.isEmpty() || migrants.size() > 1) {
                        migrant = null;
                    } else {
                        migrant = migrants.get(0);
                    }
                }

                Client client = null;

                if (null != request.getClientGuid() && !request.getClientGuid().isEmpty())
                    client = DAOUtils.findClientByGuid(session, request.getClientGuid());

                if (null == client) {
                    Long idOfClient = ClientManager.forceGetClientESZ(session, request.getIdOfESZ(),
                            (request.getSurname() == null) ? "" : request.getSurname(),
                            (request.getFirstname() == null) ? "" : request.getFirstname(),
                            (request.getSecondname() == null) ? "" : request.getSecondname());
                    client = (Client) session.load(Client.class, idOfClient);
                }

                List<Org> orgVisitList = DAOUtils.getOrgByInnAndUnom(session, request.getVisitOrgInn(), request.getVisitOrgUnom());

                if (orgVisitList.size() > 1) {
                    logger.warn(String.format("More then one organization was found with unom=%d and inn=%s for client with guid={%s}",
                            request.getVisitOrgUnom(), request.getVisitOrgInn(), request.getClientGuid()));
                    continue;
                }

                if (orgVisitList.isEmpty()) {
                    logger.warn(String.format("No organization was found with unom=%d and inn=%s for client with guid={%s}",
                            request.getVisitOrgUnom(), request.getVisitOrgInn(), request.getClientGuid()));
                    continue;
                }

                Integer resolution;

                if (null == request.getDateEnd()) { // Всегда ставим 1 – подтверждена, если поле DateEnd = null.
                    resolution = VisitReqResolutionHist.RES_CONFIRMED;
                } else {  // Всегда ставим 3 — аннулирована и сдана в архив, если поле DateEnd = не null.
                    if (request.getDateEnd().getTime() <= date.getTime()) {
                        resolution = VisitReqResolutionHist.RES_CANCELED;
                    } else {
                        resolution = VisitReqResolutionHist.RES_CONFIRMED;
                    }
                }

                if (request.getDateLearnEnd().getTime() <= date.getTime()) {
                    resolution = VisitReqResolutionHist.RES_CANCELED;
                }

                Date endDate = (null == request.getDateEnd()) ? request.getDateLearnEnd() : request.getDateEnd();

                // флаг, показывающий наличие изменений в заявке
                boolean isMigrantChanged = false;

                // нет связки - добавляем в мигрантов и историю
                if (null == migrant) {
                    Long idOfProcessorMigrantRequest = MigrantsUtils
                            .nextIdOfProcessorMigrantRequest(session, client.getOrg().getIdOfOrg());
                    CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(idOfProcessorMigrantRequest,
                            client.getOrg().getIdOfOrg());
                    String requestNumber = formRequestNumber(client.getOrg().getIdOfOrg(), orgVisitList.get(0).getIdOfOrg(),
                            idOfProcessorMigrantRequest, date);

                    // создаем нового мигранта
                    Migrant migrantNew = new Migrant(compositeIdOfMigrant, client.getOrg().getDefaultSupplier(),
                            requestNumber, client, orgVisitList.get(0), request.getDateLearnStart(), endDate, Migrant.NOT_SYNCHRONIZED);
                    migrantNew.setInitiator(MigrantInitiatorEnum.INITIATOR_ESZ);
                    migrantNew.setSection(request.getGroupName());
                    migrantNew.setResolutionCodeGroup(request.getIdOfServiceClass());
                    session.save(migrantNew);

                    session.save(createResolutionHistory(session, client, compositeIdOfMigrant.getIdOfRequest(),
                            VisitReqResolutionHist.RES_CREATED, date));
                    session.flush();
                    session.save(createResolutionHistory(session, client, compositeIdOfMigrant.getIdOfRequest(), resolution,
                            CalendarUtils.addSeconds(date, 1)));
                } else {    // сравниваем, если надо - обновляем
                    if (!migrant.getOrgRegVendor().equals(client.getOrg().getDefaultSupplier())) {
                        migrant.setOrgRegVendor(client.getOrg().getDefaultSupplier());
                        isMigrantChanged = true;
                    }

                    if (!migrant.getClientMigrate().equals(client)) {
                        migrant.setClientMigrate(client);
                        isMigrantChanged = true;
                    }

                    if (!migrant.getOrgVisit().equals(orgVisitList.get(0))) {
                        migrant.setOrgVisit(orgVisitList.get(0));
                        isMigrantChanged = true;
                    }

                    if (!migrant.getVisitStartDate().equals(request.getDateLearnStart())) {
                        migrant.setVisitStartDate(request.getDateLearnStart());
                        isMigrantChanged = true;
                    }

                    if (!migrant.getVisitEndDate().equals(endDate)) {
                        migrant.setVisitEndDate(endDate);
                        isMigrantChanged = true;
                    }

                    if (!migrant.getSection().equals(request.getGroupName())) {
                        migrant.setSection(request.getGroupName());
                        isMigrantChanged = true;
                    }

                    if (!migrant.getResolutionCodeGroup().equals(request.getIdOfServiceClass())) {
                        migrant.setResolutionCodeGroup(request.getIdOfServiceClass());
                    }

                    if (isMigrantChanged) {
                        migrant.setSyncState(Migrant.NOT_SYNCHRONIZED);
                        session.saveOrUpdate(migrant);
                    }

                    VisitReqResolutionHist hist = MigrantsUtils.getLastResolutionForMigrant(session, migrant);
                    if (!resolution.equals(hist.getResolution())) {
                        session.save(createResolutionHistory(session, client, migrant.getCompositeIdOfMigrant().getIdOfRequest(),
                                resolution, date));
                    }
                }
                session.flush();
            }

            Query query = session.createSQLQuery("truncate table cf_esz_migrants_requests");
            query.executeUpdate();

            transaction.commit();
            transaction = null;

            logger.info(String.format("End fill migrants table. Time taken %s ms", System.currentTimeMillis() - begin));
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private static String formRequestNumber(Long idOfOrg, Long idOfOrgVisit, Long idOfFirstRequest, Date startDate){
        return String.format("В-%s-%s/%s-%s", idOfOrg, idOfOrgVisit, (idOfFirstRequest * -1L),
                CalendarUtils.dateShortToString(startDate));
    }

    private VisitReqResolutionHist createResolutionHistory(Session session, Client client, Long idOfRequest,
            Integer resolution, Date date) {
        Long idOfResol = MigrantsUtils.nextIdOfProcessorMigrantResolutions(session, client.getOrg().getIdOfOrg());
        CompositeIdOfVisitReqResolutionHist comIdOfHist = new CompositeIdOfVisitReqResolutionHist(idOfResol,
                idOfRequest, client.getOrg().getIdOfOrg());

        // создаем новую запись в истории
        return new VisitReqResolutionHist(comIdOfHist, client.getOrg(),
                resolution, date, null, null, null,
                VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_ESZ);
    }
}
