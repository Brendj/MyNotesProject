package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

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

    public ClientInfo getClientByMeshGUID(String personGuid) {
        return service.getClientGuardianByMeshGUID(personGuid);
    }

    public void createClient(ClientInfo info) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Создание клиента через внутренний REST-сервис");

            registryClient(info, session, clientGuardianHistory);

            transaction.commit();
            transaction = null;
        } catch (Exception e){
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    private void registryClient(ClientInfo info, Session session, ClientGuardianHistory clientGuardianHistory) throws Exception {
        Org org = service.getOrgByClientMeshGuid(info.getChildrenPersonGUID());
        Long contractId = RuntimeContext.getInstance()
                .getClientContractIdGenerator().generateTransactionFree(org.getIdOfOrg());

        Long nextVersion = DAOUtils.updateClientRegistryVersion(session);

        Person p = new Person(info.getFirstname(), info.getLastname(), info.getPatronymic());
        session.save(p);

        // Copy from ClientCreatePage
        Client client = new Client(org, p, p, 0, notifyViaEmail, true, notifyViaPUSH, contractId,
                new Date(), 0, contractId.toString(), 1, nextVersion, limit, expenditureLimit);

        client.setAddress("");
        client.setPhone(info.getPhone());
        session.save(client);

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Регистрация клиента через внутренний REST-сервис");
        clientsMobileHistory.setShowing("Изменено сервисом Кафки.");
        client.initClientMobileHistory(clientsMobileHistory);

        client.setMeshGUID(info.getPersonGUID());
        client.setMobile(info.getMobile());
        client.setEmail(info.getEmail());
        client.setBirthDate(info.getBirthdate());
        client.setGender(info.getGenderId());
        client.setDiscountMode(Client.DISCOUNT_MODE_NONE);
        client.setCreatedFrom(ClientCreatedFromType.DEFAULT);

        ClientGroup cg = service.getClientGroupParentByOrg(org);
        if(cg == null) {
            cg = DAOUtils.createClientGroup(session, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_PARENTS);
        }
        client.setClientGroup(cg);
        client.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_PARENTS.getValue());

        session.update(client);

        for(DocumentInfo di : info.getDocuments()){
            createDul(di, session, client.getIdOfClient());
        }

        ClientMigration clientMigration = new ClientMigration(client, org, new Date());
        session.save(clientMigration);

        if(client.getClientGroup() != null) {
            ClientManager.createClientGroupMigrationHistory(session, client, org,
                    client.getIdOfClientGroup(), ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup(),
                    ClientGroupMigrationHistory.MODIFY_IN_ISPP, clientGuardianHistory);
        }
    }

    public void updateClient(ClientInfo info) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client c = DAOUtils.findClientByMeshGuid(session, info.getPersonGUID());
            if(c == null){
                throw new NotFoundException("Not found client by MESH-GUID: " + info.getPersonGUID());
            }
            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);

            Person p = c.getPerson();
            p.setFirstName(info.getFirstname());
            p.setSurname(info.getLastname());
            p.setSecondName(info.getPatronymic());
            session.update(p);

            processDocuments(c.getDulDetail(), info.getDocuments(), session, c.getIdOfClient());

            c.setClientRegistryVersion(nextClientVersion);
            c.setBirthDate(info.getBirthdate());
            c.setGender(info.getGenderId());
            c.setPhone(info.getPhone());
            c.setMobile(info.getMobile());
            c.setEmail(info.getEmail());
            session.update(c);

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

    private void processDocuments(Set<DulDetail> dulDetails, List<DocumentInfo> documents, Session session, Long idOfClient) {
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
            }
        }

        for(DulDetail d : dulDetails){
            boolean exists = documents.stream()
                    .anyMatch(di -> di.getIdMKDocument().equals(d.getIdMkDocument()));
            if(!exists){
                d.setLastUpdate(new Date());
                d.setDeleteState(true);

                session.update(d);
            }
        }
    }

    private void createDul(DocumentInfo di, Session session, Long idOfClient) {
        DulGuide dulGuide = DAOUtils.getDulGuideByType(session, di.getDocumentType());
        if(dulGuide == null){
            throw new NotFoundException("Not found DulGuide by type: " + di.getDocumentType());
        }
        DulDetail d = new DulDetail(idOfClient, di.getDocumentType().longValue(), dulGuide);
        d.setIdMkDocument(di.getIdMKDocument());
        d.setCreateDate(new Date());
        d.setLastUpdate(new Date());
        d.setNumber(di.getNumber());
        d.setSeries(di.getSeries());
        d.setIssued(di.getIssuedDate());
        d.setIssuer(di.getIssuer());
        d.setDeleteState(false);
        d.setExpiration(di.getExpiration());
        d.setSubdivisionCode(di.getSubdivisionCode());

        session.save(d);
    }

    public void deleteClient(String personGuid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client c = DAOUtils.findClientByMeshGuidAsGuardian(session, personGuid);
            if (c == null) {
                throw new NotFoundException("Not found client by MESH-GUID: " + personGuid);
            }

            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);

            c.setClientRegistryVersion(nextClientVersion);
            c.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_LEAVING.getValue());

            session.update(c);

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

    public void processRelation(GuardianRelationInfo guardianRelationInfo) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Изменение/Создание через внутренний REST-сервис");

            Client c = DAOUtils.findClientByMeshGuid(session, guardianRelationInfo.getChildrenPersonGuid());
            if(c == null){
                throw new NotFoundException("Not found client by MESH-GUID:" + guardianRelationInfo.getChildrenPersonGuid());
            }
            if (!guardianRelationInfo.getGuardianPersonGuids().isEmpty()) {
                for (ClientInfo meshGuardian : guardianRelationInfo.getGuardianPersonGuids()) {
                    Client guardian = DAOUtils.findClientByMeshGuid(session, meshGuardian.getPersonGUID());

                    if (guardian == null) {
                        this.registryClient(meshGuardian, session, clientGuardianHistory);
                        continue;
                    }

                    ClientGuardian clientGuardian = DAOUtils
                            .findClientGuardian(session, c.getIdOfClient(), guardian.getIdOfClient());
                    if (clientGuardian == null) {
                        ClientManager.createClientGuardianInfoTransactionFree(
                                session, guardian, ClientGuardianRelationType.UNDEFINED.getDescription(),
                                ClientGuardianRoleType.fromInteger(meshGuardian.getAgentTypeId()), true, c.getIdOfClient(), ClientCreatedFromType.DEFAULT,
                                ClientGuardianRepresentType.UNKNOWN.getCode(), clientGuardianHistory);
                    } else if (clientGuardian.getDeletedState() || clientGuardian.isDisabled()) {
                        boolean enableSpecialNotification = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL);
                        Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                        clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.DEFAULT);
                        clientGuardian.restore(newGuardiansVersions, enableSpecialNotification);
                        clientGuardian.setCreatedFrom(ClientCreatedFromType.DEFAULT);
                        session.update(clientGuardian);
                    }
                }
            }
            List<Client> clientGuardians = DAOReadExternalsService.getInstance()
                    .findGuardiansByClient(c.getIdOfClient(), null);

            for(Client guard : clientGuardians){
                if(guardianRelationInfo.getGuardianPersonGuids().isEmpty() || guardianRelationInfo.getGuardianPersonGuids().stream().noneMatch(i -> i.getPersonGUID().equals(guard.getMeshGUID()))){
                    ClientGuardian cg = DAOReadonlyService.getInstance()
                            .findClientGuardianById(session, c.getIdOfClient(), guard.getIdOfClient());

                    Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                    clientGuardianHistory.setClientGuardian(cg);
                    clientGuardianHistory.setChangeDate(new Date());
                    cg.setDeletedState(true);
                    cg.setVersion(newGuardiansVersions);
                    session.update(cg);

                    DAOUtils.disableCardRequest(session, guard.getIdOfClient());
                    MigrantsUtils.disableMigrantRequestIfExists(session, c.getOrg().getIdOfOrg(), guard.getIdOfClient());
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
}
