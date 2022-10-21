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
import ru.axetta.ecafe.processor.core.utils.PropertyUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Component("ImportMigrantsService")
@Scope("singleton")
public class ImportMigrantsService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportMigrantsService.class);

    public void run() throws Exception {
        if (!isOn()) {
            return;
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Обработка мигрантов из ЕСЗ по расписанию");
        clientsMobileHistory.setShowing("ЕСЗ");
		        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Срабатывание по расписанию");
        clientGuardianHistory.setAction("Обработка мигрантов");
        loadMigrants(clientsMobileHistory, clientGuardianHistory);
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties()
                .getProperty("ecafe.processor.esz.migrants.service.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void loadMigrants(ClientsMobileHistory clientsMobileHistory, ClientGuardianHistory clientGuardianHistory) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            logger.info("Start fill migrants table");
            long begin = System.currentTimeMillis();
            List<ESZMigrantsRequest> eszMigrantsRequestList = MigrantsUtils.getAllESZMigrantsRequests(session);

            Date now = new Date();

            Integer counter = 0;
            Integer size = eszMigrantsRequestList.size();

            Long cycleTime = System.currentTimeMillis();
            for (ESZMigrantsRequest request : eszMigrantsRequestList) {
                if (transaction == null || !transaction.isActive()) {
                    transaction = session.beginTransaction();
                }
                try {
                    // Запись без Мэш-гуида не обрабывается, так как может создаться дубликат клиента
                    // https://yt.iteco.dev/issue/ISPP-752
                    if (StringUtils.isEmpty(request.getClientGuid())) {
                        logger.warn("Empty MESHGUID was received from ESZ for: " +
                                request.getFirstname() + " " + request.getSurname()+ " " + request.getSecondname());
                        cycleTime = loggingInfoAndFlushSession(counter++, size, cycleTime, session);
                        continue;
                    }

                    // Обрабатываем только те записи, у которых дата аннулирования равна нулю или дата
                    // аннулирования максимальная среди остальных повторяющихся записей о клиенте
                    // https://gitlab.iteco.mobi/ispp/processor/-/issues/421
                    List<ESZMigrantsRequest> eszRequestList = MigrantsUtils
                            .getRequestsByExternalIdAndGroupId(session, request.getIdOfESZ(),
                                    request.getIdOfServiceClass());
                    Date lastDateEnd = getLastDateEnd(eszRequestList);
                    Date endDate = null;
                    if (request.getDateEnd() == null) {
                        endDate = request.getDateLearnEnd();
                    } else if (checkLastDate(request.getDateEnd(), lastDateEnd)) {
                        endDate = request.getDateEnd();
                    }
                    if (endDate == null) {
                        cycleTime = loggingInfoAndFlushSession(counter++, size, cycleTime, session);
                        continue;
                    }
                    List<Org> orgVisitList = DAOUtils
                            .getOrgByInnAndUnom(session, request.getVisitOrgInn(), request.getVisitOrgUnom());

                    if (orgVisitList.size() > 1 || orgVisitList.isEmpty()) {
                        logger.warn(String.format(
                                "{%d} organizations were found with unom=%d and inn=%s for client with guid={%s}",
                                orgVisitList.size(), request.getVisitOrgUnom(), request.getVisitOrgInn(), request.getClientGuid()));
                        cycleTime = loggingInfoAndFlushSession(counter++, size, cycleTime, session);
                        continue;
                    }

                    Migrant migrant = null;
                    Client client = DAOUtils.findClientByMeshGuid(session, request.getClientGuid());
                    if (null == client) {
                        // В БД ИСПП могут быть клиенты, у которых отсутсвует Мэш-гуид
                        // Их попробуем найти на ИД ЕСЗ
                        client = DAOUtils.findClientByExternalId(session, request.getIdOfESZ());
                        if(client != null) {
                            // Добавляем клиенту Мэш-гуид
                            client.setMeshGUID(request.getClientGuid());
                            client.setUpdateTime(now);
                            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);
                            client.setClientRegistryVersion(nextClientVersion);
                            session.merge(client);
                        }
                    }
                    if(null == client) {
                        Long idOfClient = ClientManager.createClientFromESZ(session, request.getIdOfESZ(),
                                StringUtils.defaultIfEmpty(request.getSurname(), ""),
                                StringUtils.defaultIfEmpty(request.getFirstname(), ""),
                                StringUtils.defaultIfEmpty(request.getSecondname(), ""),
                                request.getClientGuid(), clientsMobileHistory);
                        client = (Client) session.load(Client.class, idOfClient);
                    }
                    else {
                        migrant = MigrantsUtils.findMigrantByClientAndGroupId(
                                session, request.getIdOfServiceClass(), client.getIdOfClient());
                    }

                    Integer resolution = VisitReqResolutionHist.RES_CONFIRMED;
                    if(request.getDateLearnEnd().getTime() <= now.getTime()) {
                        resolution = VisitReqResolutionHist.RES_CANCELED;
                    }
                    if(request.getDateEnd() != null && resolution.equals(VisitReqResolutionHist.RES_CONFIRMED)) {
                        if(request.getDateEnd().getTime() <= now.getTime()) {
                            resolution = VisitReqResolutionHist.RES_CANCELED;
                        }
                    }

                    if(client.isLeaving() && resolution.equals(VisitReqResolutionHist.RES_CONFIRMED)) {
                        // При создании заявки на миграцию переводить клиента в обучающуюся группу,
                        // если он находится в группе "Выбывшие"
                        // https://gitlab.iteco.mobi/ispp/processor/-/issues/172
                        if (client.isLeaving()) {
                            client.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_OTHER_ORG.getValue());
                            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);
                            client.setClientRegistryVersion(nextClientVersion);
                            session.merge(client);
                            ESZMigrantsUpdateService.addGroupHistory(session, client,
                                    ClientGroup.Predefined.CLIENT_OTHER_ORG.getValue(), clientGuardianHistory);
                        }
                    }
                    if (null == migrant) {
                        if (resolution.equals(VisitReqResolutionHist.RES_CONFIRMED)) {
                            Long idOfProcessorMigrantRequest = MigrantsUtils
                                    .nextIdOfProcessorMigrantRequest(session, client.getOrg().getIdOfOrg());
                            CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(
                                    idOfProcessorMigrantRequest, client.getOrg().getIdOfOrg());
                            String requestNumber = formRequestNumber(client.getOrg().getIdOfOrg(),
                                    orgVisitList.get(0).getIdOfOrg(), idOfProcessorMigrantRequest, now);

                            Migrant migrantNew = new Migrant(compositeIdOfMigrant, client.getOrg().getDefaultSupplier(),
                                    requestNumber, client, orgVisitList.get(0), request.getDateLearnStart(), endDate,
                                    Migrant.NOT_SYNCHRONIZED);
                            migrantNew.setInitiator(MigrantInitiatorEnum.INITIATOR_ESZ);
                            migrantNew.setSection(request.getGroupName());
                            migrantNew.setResolutionCodeGroup(request.getIdOfServiceClass());

                            session.save(migrantNew);

                            session.save(
                                    createResolutionHistoryInternal(session, client, compositeIdOfMigrant.getIdOfRequest(),
                                            VisitReqResolutionHist.RES_CREATED, now));
                            session.flush();

                            session.save(
                                    createResolutionHistoryInternal(session, client, compositeIdOfMigrant.getIdOfRequest(),
                                            VisitReqResolutionHist.RES_CONFIRMED, CalendarUtils.addSeconds(now, 5)));
                        }
                    } else {
                        boolean isMigrantChanged = false;
                        if (!migrant.getOrgRegVendor().equals(client.getOrg().getDefaultSupplier())) {
                            migrant.setOrgRegVendor(client.getOrg().getDefaultSupplier());
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

                        if (isMigrantChanged || resolution.equals(VisitReqResolutionHist.RES_CANCELED)) {
                            if (resolution.equals(VisitReqResolutionHist.RES_CANCELED)) {
                                migrant.setSyncState(Migrant.CLOSED);
                            } else {
                                migrant.setSyncState(Migrant.NOT_SYNCHRONIZED);
                            }
                            session.merge(migrant);

                            session.save(createResolutionHistoryInternal(session, client,
                                    migrant.getCompositeIdOfMigrant().getIdOfRequest(), resolution, now));
                        }
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.error("Error in loadMigrants cycle", e);
                }

                cycleTime = loggingInfoAndFlushSession(counter++, size, cycleTime, session);
                if (transaction.isActive()) {
                    transaction.commit();
                }
                transaction = null;
            }

            logger.info(String.format("End fill migrants table. Time taken %s ms", System.currentTimeMillis() - begin));
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private boolean checkLastDate(Date date, Date lastDateEnd) {
        return (date == null && lastDateEnd == null) || (date != null && date.equals(lastDateEnd));
    }

    private Date getLastDateEnd(List<ESZMigrantsRequest> requestList) {
        // Ищем дату аннулирования: null или максимальную, если все не пусты
        SortedSet<Date> endDates = new TreeSet<>();
        for (ESZMigrantsRequest request : requestList) {
            Date endDate = request.getDateEnd();
            if (endDate == null) {
                return null;
            } else {
                endDates.add(endDate);
            }
        }
        return endDates.last();
    }

    public static String formRequestNumber(Long idOfOrg, Long idOfOrgVisit, Long idOfFirstRequest, Date startDate) {
        return String.format("В-%s-%s/%s-%s", idOfOrg, idOfOrgVisit, (idOfFirstRequest * -1L),
                CalendarUtils.dateShortToString(startDate));
    }

    public static VisitReqResolutionHist createResolutionHistory(Session session, Client client, Long idOfRequest,
            Integer resolution, Date date) {
        Long idOfResol = MigrantsUtils.nextIdOfProcessorMigrantResolutions(session, client.getOrg().getIdOfOrg());
        CompositeIdOfVisitReqResolutionHist comIdOfHist = new CompositeIdOfVisitReqResolutionHist(idOfResol,
                idOfRequest, client.getOrg().getIdOfOrg());

        // создаем новую запись в истории
        return new VisitReqResolutionHist(comIdOfHist, client.getOrg(), resolution, date, MigrantsUtils.getResolutionString(resolution), null, null,
                VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_ESZ);
    }

    public static VisitReqResolutionHist createResolutionHistoryInternal(Session session, Client client,
            Long idOfRequest, Integer resolution, Date date) throws Exception {
        Long idOfESZSchool = PropertyUtils.getIdOfESZOrg();
        Long idOfResol = MigrantsUtils.nextIdOfProcessorMigrantResolutions(session, idOfESZSchool);
        CompositeIdOfVisitReqResolutionHist comIdOfHist = new CompositeIdOfVisitReqResolutionHist(idOfResol,
                idOfRequest, idOfESZSchool);

        // создаем новую запись в истории
        return new VisitReqResolutionHist(comIdOfHist, client.getOrg(), resolution, date, MigrantsUtils.getResolutionString(resolution), null, null,
                VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_ESZ);
    }

    private Long loggingInfoAndFlushSession(Integer counter, Integer size, Long cycleTime, Session session) {
        if (counter % 100 == 0) {
            session.flush();
            session.clear();
            logger.info(String.format("Filling migrants table: %d/%d done. Time taken %s ms", counter, size,
                    System.currentTimeMillis() - cycleTime));
            cycleTime = System.currentTimeMillis();
        }
        return cycleTime;
    }
}
