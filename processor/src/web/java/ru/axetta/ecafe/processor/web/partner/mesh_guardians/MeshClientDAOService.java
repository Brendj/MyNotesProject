package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.ClientInfo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

@Service
@DependsOn("runtimeContext")
public class MeshClientDAOService {
    private static final Logger log = LoggerFactory.getLogger(MeshClientDAOService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;


    @Transactional(readOnly = true)
    public ClientInfo getClientGuardianByMeshGUID(String meshGuid) {
        try {
            List<Long> excludeClientGroupsIds = Arrays.asList(
                    ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                    ClientGroup.Predefined.CLIENT_DELETED.getValue()
            );

            Query q = em.createQuery("select c from Client c join c.person p " +
                    " where c.meshGUID like :meshGUID " +
                    " and c.clientGroup.compositeIdOfClientGroup.idOfClientGroup not in (:excludeClientGroupsIds)");

            q.setParameter("meshGUID", meshGuid)
                    .setParameter("employId", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                    .setParameter("excludeClientGroupsIds", excludeClientGroupsIds)
                    .setMaxResults(1);

            Client c = (Client) q.getSingleResult();
            return ClientInfo.build(c);
        } catch (Exception e){
            log.error("Can't find Guardian by MeshGuid: " + meshGuid);
            throw e;
        }
    }
}
