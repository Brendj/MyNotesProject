package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.ClientInfo;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;

@Service
@DependsOn("runtimeContext")
public class MeshClientProcessorService {
    private static final Logger log = LoggerFactory.getLogger(MeshClientProcessorService.class);

    private MeshClientDAOService service;

    @PostConstruct
    public void init(){
        service = RuntimeContext.getAppContext().getBean(MeshClientDAOService.class);
    }

    public ClientInfo getClientGuardianByMeshGUID(String personGuid) {
        return service.getClientGuardianByMeshGUID(personGuid);
    }

    public void createClient(ClientInfo info) {
       // Client c = new Client();
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

    public void deleteClient(String personGuid) {

    }
}
