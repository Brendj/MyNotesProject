/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic;

import org.springframework.context.annotation.DependsOn;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardService;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardServiceIml;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MockService;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ResponsePersons;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    public MeshClientCardRef createRef(Card c) {
        MeshClientCardRef ref = null;
        try {
            ref = meshCardService.createReferenceBetweenClientAndCard(c);
        } catch (Exception e){
            log.error("Can't create Ref", e);
        }
        return ref;
    }

    public MeshClientCardRef updateRef(MeshClientCardRef ref) {
        try {
            return meshCardService.updateCardForClient(ref);
        } catch (Exception e){
            log.error("Can't update Ref", e);
            return ref;
        }
    }

    public MeshClientCardRef deleteRef(MeshClientCardRef ref) {
        try {
            return meshCardService.deleteReferenceBetweenClientAndCard(ref);
        } catch (Exception e){
            log.error("Can't delete Ref", e);
            return ref;
        }
    }

    public void deleteRef(Integer id, String meshGUID) {
        try {
            meshCardService.deleteReferenceBetweenClientAndCardById(id, meshGUID);
        } catch (Exception e){
            log.error("Can't delete Ref (Error Correction)", e);
        }
    }

    public void changeRef(Card c) throws Exception {
        try {
            meshCardService.deleteReferenceBetweenClientAndCard(c.getMeshCardClientRef());

            MeshClientCardRef newRef = meshCardService.createReferenceBetweenClientAndCard(c);
            c.getMeshCardClientRef().setClient(newRef.getClient());
            c.getMeshCardClientRef().setIdOfRefInExternalSystem(newRef.getIdOfRefInExternalSystem());
            c.getMeshCardClientRef().setLastUpdate(newRef.getLastUpdate());
            c.getMeshCardClientRef().setSend(newRef.getSend());
        } catch (Exception e){
            log.error("Can't change Ref", e);
            throw e;
        }
    }

    @Transactional
    public void registryCardInMESHByOrgs(List<Org> orgs) throws Exception {
        if(CollectionUtils.isEmpty(orgs)){
            return;
        }
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();

            for(Org o : orgs){
                try {
                    transaction = session.beginTransaction();
                    log.info(String.format("Begin sending cards for OO ID %d", o.getIdOfOrg()));

                    List<Card> cards = DAOUtils.getNotRegistryInMeshCardsByOrg(session, o);
                    if(CollectionUtils.isEmpty(cards)){
                        log.info("No cards, skipped");
                        continue;
                    }

                    log.info(String.format("Process %d cards", cards.size()));
                    for (Card c : cards) {
                        MeshClientCardRef ref = this.createRef(c);
                        c.setMeshCardClientRef(ref);

                        session.update(c);
                    }
                    transaction.commit();
                    transaction = null;

                    log.info(String.format("OO ID %d is processed", o.getIdOfOrg()));
                } catch (Exception e){
                    log.error(
                            String.format("For OO ID %d catch exception. Further processing of this organization is skipped",
                                    o.getIdOfOrg()), e);
                } finally {
                    HibernateUtils.rollback(transaction, log);
                }
            }
            transaction = null;
            session.close();

        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    public List<Category> getCardCategoryByClient(Client client) {
        ResponsePersons person = meshCardService.findPersonById(client.getMeshGUID());
        if(person == null){
            return Collections.emptyList();
        }

        List<Category> res = new LinkedList<>();

        for(Category c : person.getCategories()){
            if(c.getCategoryId().equals(Category.CARD_CATEGORY_ID)){
                res.add(c);
            }
        }
        return res;
    }
}