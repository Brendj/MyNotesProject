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
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.GuardianRelationInfo;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.findClientGroupByGroupNameAndIdOfOrg;

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

    public ClientInfo getClientGuardianByMeshGUID(String personGuid) {
        return service.getClientGuardianByMeshGUID(personGuid);
    }

    public void createClient(ClientInfo info) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
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

        Person p = new Person(info.getFirstname(), info.getPatronymic(), info.getLastname());
        session.save(p);

        // Copy from ClientCreatePage
        Client client = new Client(org, p, p, 0, notifyViaEmail, true, notifyViaPUSH, contractId,
                new Date(), 0, null, 1, nextVersion, limit, expenditureLimit);

        client.setAddress(info.getAddress());
        client.setPhone(info.getPhone());
        session.save(client);

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Регистрация клиента через внутренний REST-сервис");
        clientsMobileHistory.setShowing("Изменено сервисом Кафки.");
        client.initClientMobileHistory(clientsMobileHistory);

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
            p.setSurname(info.getPatronymic());
            p.setSecondName(info.getLastname());
            session.update(p);

            c.setClientRegistryVersion(nextClientVersion);
            c.setBirthDate(info.getBirthdate());
            c.setGender(info.getGenderId());
            c.setAddress(info.getAddress());
            c.setPhone(info.getPhone());
            c.setMobile(info.getMobile());
            c.setEmail(info.getEmail());
            c.setPassportSeries(info.getPassportSeries());
            c.setPassportNumber(info.getPassportNumber());
            c.setSan(info.getSan());
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

    public void deleteClient(String personGuid) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client c = DAOUtils.findClientByMeshGuid(session, personGuid);
            if (c == null) {
                throw new NotFoundException("Not found client by MESH-GUID: " + personGuid);
            }

            Long nextClientVersion = DAOUtils.updateClientRegistryVersion(session);
            ClientGroup cg = findClientGroupByGroupNameAndIdOfOrg(session,
                    c.getOrg().getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());

            c.setClientRegistryVersion(nextClientVersion);
            c.setClientGroup(cg);

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

            for(String guardianMeshGuid : guardianRelationInfo.getGuardianPersonGuids()) {
                Client guardian = DAOUtils.findClientByMeshGuid(session, guardianMeshGuid);

                if(guardian == null){
                    log.error("Not found guardian by MESH-GUID: " + guardianMeshGuid);
                    continue;
                }

                ClientGuardian clientGuardian = DAOUtils
                        .findClientGuardian(session, c.getIdOfClient(), guardian.getIdOfClient());
                if (clientGuardian == null) {
                    ClientManager.createClientGuardianInfoTransactionFree(session, guardian, null, false,
                                    c.getIdOfClient(), ClientCreatedFromType.DEFAULT, ClientGuardianRepresentType.UNKNOWN.getCode(), clientGuardianHistory);
                } else if (clientGuardian.getDeletedState() || clientGuardian.isDisabled()) {
                    boolean enableSpecialNotification = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL);
                    Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                    clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.DEFAULT);
                    clientGuardian.restore(newGuardiansVersions, enableSpecialNotification);
                    clientGuardian.setCreatedFrom(ClientCreatedFromType.DEFAULT);
                    session.update(clientGuardian);
                }
            }

            List<Client> clientGuardians = DAOReadExternalsService.getInstance()
                    .findGuardiansByClient(c.getIdOfClient(), null);

            for(Client guard : clientGuardians){
                if(!guardianRelationInfo.getGuardianPersonGuids().contains(guard.getMeshGUID())){
                    ClientGuardian cg = DAOReadonlyService.getInstance()
                            .findClientGuardianById(session, c.getIdOfClient(), guard.getIdOfClient());

                    Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                    clientGuardianHistory.setClientGuardian(cg);
                    clientGuardianHistory.setChangeDate(new Date());
                    cg.setDisabled(true);
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
