/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardService;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardServiceIml;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MockService;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;

@DependsOn("runtimeContext")
@Service
public class MeshClientCardRefService {
    private final Logger log = LoggerFactory.getLogger(MeshClientCardRefService.class);

    private MeshCardService meshCardService;

    @PostConstruct
    public void initService(){
        RuntimeContext.RegistryType currentType = RuntimeContext.getInstance().getRegistryType();
        if(currentType.equals(RuntimeContext.RegistryType.MSK)){
            this.meshCardService = new MeshCardServiceIml();
        } else {
            this.meshCardService = MockService.getInstance();
        }
    }

    public void createRef(Card c) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            MeshClientCardRef ref = meshCardService.createReferenceBetweenClientAndCard(c);
            session.save(ref);

            transaction.commit();
            transaction = null;
        } catch (Exception e){
            log.error("Can't create Ref", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    public MeshClientCardRef updateRef(MeshClientCardRef ref) {
        try {
            return meshCardService.updateCardForClient(ref);
        } catch (Exception e){
            log.error("Can't create Ref", e);
            return ref;
        }
    }
}
