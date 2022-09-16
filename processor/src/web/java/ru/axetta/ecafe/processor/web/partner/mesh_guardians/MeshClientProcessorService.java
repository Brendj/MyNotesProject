package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.DulDetailService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.ClientInfo;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.DocumentInfo;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.GuardianRelationInfo;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@DependsOn("runtimeContext")
public class MeshClientProcessorService {
    private static final Logger log = LoggerFactory.getLogger(MeshClientProcessorService.class);

    public static final int FULL_UPDATE_OPERATION = 1;
    public static final int DOCUMENTS_UPDATE_OPERATION = 2;
    public static final int CONTACTS_UPDATE_OPERATION = 3;

    private Boolean notifyViaEmail;
    private Boolean notifyViaPUSH;
    private Long expenditureLimit;
    private Long limit;
    private MeshClientDAOService service;

    @PostConstruct
    public void init(){
        service = RuntimeContext.getAppContext().getBean(MeshClientDAOService.class);
        notifyViaEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS);
        notifyViaPUSH = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS);
        expenditureLimit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT);
        limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);
    }


    public Client getGuardianByMeshGUID(Session session, String personGuid) throws Exception {
        Client guardian = null;
        Client client = DAOUtils.findClientByMeshGuid(session, personGuid);
        if(client != null) {
            List<Client> children = ClientManager.findChildsByClient(
                    session, client.getIdOfClient(), false, false);
            if(!children.isEmpty()) {
                guardian = client;
            }
        }
        return guardian;
    }

    private Client createGuardian(ClientInfo info, Org org, Session session) throws Exception {
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Регистрация клиента через внутренний REST-сервис");
        clientsMobileHistory.setShowing("Изменено сервисом Кафки.");

        Client guardian = ClientManager
                .createGuardianTransactionFree(session, info.getFirstname(), info.getPatronymic(),
                        info.getLastname(), info.getMobile(), null,
                        info.getIsppGender(), org, ClientCreatedFromType.DEFAULT, "", null,
                        null, null, null, null, clientsMobileHistory);
        guardian.setEmail(info.getEmail());
        guardian.setMeshGUID(info.getPersonGUID());
        guardian.setSan(info.getSnils());
        guardian.setBirthDate(info.getBirthdate());
        session.update(guardian);

        if (!info.getDocuments().isEmpty()) {
            for (DocumentInfo document : info.getDocuments()) {
                createDul(document, session, guardian.getIdOfClient());
            }
        }
        session.flush();
        return guardian;
    }

    public void updateClient(ClientInfo info) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client guardian = getGuardianByMeshGUID(session, info.getPersonGUID());
            if(guardian == null){
                throw new NotFoundException("Not found guardian by MESH-GUID: " + info.getPersonGUID());
            }

            if(info.getUpdateOperation().equals(FULL_UPDATE_OPERATION)) {
                guardian.getPerson().setFirstName(info.getFirstname());
                guardian.getPerson().setSurname(info.getLastname());
                guardian.getPerson().setSecondName(StringUtils.defaultIfEmpty(info.getPatronymic(), ""));
                guardian.setBirthDate(info.getBirthdate());
                guardian.setSan(info.getSnils());
                guardian.setGender(info.getIsppGender());
            }
            if(info.getUpdateOperation().equals(FULL_UPDATE_OPERATION) ||
                    info.getUpdateOperation().equals(CONTACTS_UPDATE_OPERATION)) {
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("Обновление клиента через внутренний REST-сервис");
                clientsMobileHistory.setShowing("Изменено сервисом Кафки.");
                guardian.initClientMobileHistory(clientsMobileHistory);
                guardian.setMobile(info.getMobile());
                guardian.setEmail(info.getEmail());
            }
            if(info.getUpdateOperation().equals(FULL_UPDATE_OPERATION) ||
                    info.getUpdateOperation().equals(DOCUMENTS_UPDATE_OPERATION)) {
                processDocuments(guardian.getDulDetail(), info.getDocuments(), session, guardian.getIdOfClient());
            }

            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);
            guardian.setClientRegistryVersion(nextClientVersion);
            guardian.setUpdateTime(new Date());
            session.merge(guardian);

            transaction.commit();
            transaction = null;

            session.close();

        } catch (Exception e){
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }


    public void deleteClient(String personGuid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client guardian = getGuardianByMeshGUID(session, personGuid);
            if (guardian == null || (guardian != null && guardian.isDeletedOrLeaving())) {
                throw new NotFoundException("Not found guardian by MESH-GUID: " + personGuid);
            }

            guardian.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            ClientManager.createClientGroupMigrationHistoryLite(
                    session, guardian, guardian.getOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                    ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), ClientGroupMigrationHistory.MODIFY_IN_ISPP
                            .concat(String.format(" (ид. ОО=%s)", guardian.getOrg().getIdOfOrg())));

            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);
            guardian.setClientRegistryVersion(nextClientVersion);
            guardian.setUpdateTime(new Date());
            session.merge(guardian);

            transaction.commit();
            transaction = null;
            session.close();
        } catch (Exception e){
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    public void processRelations(GuardianRelationInfo guardianRelationInfo) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Изменение/Создание через внутренний REST-сервис");
            clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.DEFAULT);

            Client child = DAOUtils.findClientByMeshGuid(session, guardianRelationInfo.getChildrenPersonGuid());
            if(child == null){
                throw new NotFoundException("Not found child by MESH-GUID:" + guardianRelationInfo.getChildrenPersonGuid());
            }
            if (!guardianRelationInfo.getGuardianPersonGuids().isEmpty()) {
                for (ClientInfo meshGuardian : guardianRelationInfo.getGuardianPersonGuids()) {
                    Client guardian = DAOUtils.findClientByMeshGuid(session, meshGuardian.getPersonGUID());

                    if (guardian == null) {
                        guardian = createGuardian(meshGuardian, child.getOrg(), session);
                    }

                    ClientGuardian clientGuardian = DAOUtils
                            .findClientGuardian(session, child.getIdOfClient(), guardian.getIdOfClient());

                    if (clientGuardian == null) {
                        Boolean disabled = meshGuardian.getAgentTypeId().equals(
                                ClientGuardianRoleType.TRUSTED_REPRESENTATIVE.getCode());
                        ClientManager.createClientGuardianInfoTransactionFree(
                                session, guardian, ClientGuardianRelationType.UNDEFINED.getDescription(),
                                ClientGuardianRoleType.fromInteger(meshGuardian.getAgentTypeId()), disabled,
                                child.getIdOfClient(), ClientCreatedFromType.DEFAULT,
                                ClientGuardianRepresentType.UNKNOWN.getCode(), clientGuardianHistory);
                    } else {
                        if (clientGuardian.getDeletedState()) {
                            clientGuardian.initializateClientGuardianHistory(clientGuardianHistory);
                            clientGuardian.restore(ClientManager.generateNewClientGuardianVersion(session),
                                    RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL));

                            // Опекунство активировно(default=false), если:
                            // A. Типы представителей в МК от 1 до 4 типа;
                            // B. Роли представителя в ИСПП от 1 до 2 типа;
                            Boolean disabled = meshGuardian.getAgentTypeId().equals(
                                    ClientGuardianRoleType.TRUSTED_REPRESENTATIVE.getCode());

                            if (clientGuardian.getRepresentType() != null) {
                                disabled = disabled && (!clientGuardian.getRepresentType().equals(ClientGuardianRepresentType.IN_LAW) &&
                                        !clientGuardian.getRepresentType().equals(ClientGuardianRepresentType.GUARDIAN));
                            }
                            clientGuardian.setDisabled(disabled);
                            session.merge(clientGuardian);
                        }

                        if (clientGuardian.getRoleType().getCode() != meshGuardian.getAgentTypeId()) {
                            clientGuardian.initializateClientGuardianHistory(clientGuardianHistory);
                            clientGuardian.setRoleType(ClientGuardianRoleType.fromInteger(meshGuardian.getAgentTypeId()));
                            session.merge(clientGuardian);
                        }
                    }
                }
            }
            List<Client> guardians = DAOReadExternalsService.getInstance()
                    .findGuardiansByClient(child.getIdOfClient(), null);

            // Удаляем те связи, которых нет в МК, но есть в ИСПП
            for(Client guardian : guardians){
                if(guardianRelationInfo.getGuardianPersonGuids().isEmpty() ||
                        guardianRelationInfo.getGuardianPersonGuids().stream().
                                noneMatch(i -> i.getPersonGUID().equals(guardian.getMeshGUID()))){

                    ClientGuardian clientGuardian = DAOReadonlyService.getInstance()
                            .findClientGuardianById(session, child.getIdOfClient(), guardian.getIdOfClient());
                    clientGuardian.initializateClientGuardianHistory(clientGuardianHistory);

                    clientGuardian.delete(ClientManager.generateNewClientGuardianVersion(session));
                    session.merge(clientGuardian);

                    DAOUtils.disableCardRequest(session, guardian.getIdOfClient());
                    MigrantsUtils.disableMigrantRequestIfExists(session, child.getOrg().getIdOfOrg(), guardian.getIdOfClient());
                }
            }

            transaction.commit();
            transaction = null;
            session.close();
        } catch (Exception e){
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    private void processDocuments(Set<DulDetail> dulDetails, List<DocumentInfo> documents, Session session, Long idOfClient) throws Exception{
        for(DocumentInfo di : documents){
            DulDetail d = dulDetails
                    .stream()
                    .filter(dulDetail -> dulDetail.getIdMkDocument().equals(di.getIdMKDocument()))
                    .findFirst()
                    .orElse(null);
            if(d == null){
                createDul(di, session, idOfClient);
            } else {
                d.setSeries(di.getSeries());
                d.setNumber(di.getNumber());
                d.setIssuer(di.getIssuer());
                d.setIssued(di.getIssuedDate());
                d.setLastUpdate(new Date());
                d.setExpiration(di.getExpiration());
                d.setSubdivisionCode(di.getSubdivisionCode());

                session.merge(d);
                session.flush();
            }
        }

        for(DulDetail dulDetailItem : dulDetails){
            boolean exists = documents.stream()
                    .anyMatch(di -> di.getIdMKDocument().equals(dulDetailItem.getIdMkDocument()));
            if(!exists){
                dulDetailItem.setLastUpdate(new Date());
                dulDetailItem.setDeleteState(true);

                session.merge(dulDetailItem);
            }
        }
    }

    private void createDul(DocumentInfo di, Session session, Long idOfClient) throws Exception{
        DulGuide dulGuide = DAOUtils.getDulGuideByType(session, di.getDocumentType());
        if(dulGuide == null){
            return;
        }
        DulDetail newDulDetail = new DulDetail(idOfClient, di.getDocumentType().longValue(), dulGuide);
        newDulDetail.setIdMkDocument(di.getIdMKDocument());
        newDulDetail.setCreateDate(new Date());
        newDulDetail.setLastUpdate(new Date());
        newDulDetail.setNumber(di.getNumber());
        newDulDetail.setSeries(di.getSeries());
        newDulDetail.setIssued(di.getIssuedDate());
        newDulDetail.setIssuer(di.getIssuer());
        newDulDetail.setDeleteState(false);
        newDulDetail.setExpiration(di.getExpiration());
        newDulDetail.setSubdivisionCode(di.getSubdivisionCode());

        session.save(newDulDetail);
    }
}
