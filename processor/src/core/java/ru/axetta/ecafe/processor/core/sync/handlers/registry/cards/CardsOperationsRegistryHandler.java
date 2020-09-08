/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.cards;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.VersionUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 9:43
 */
public class CardsOperationsRegistryHandler {
    private static final Logger logger = LoggerFactory.getLogger(CardsOperationsRegistryHandler.class);

    public ResCardsOperationsRegistry handler(SyncRequest request, long idOfOrg) {

        if(request.getCardsOperationsRegistry()== null ||request.getCardsOperationsRegistry().getItems()== null ||request.getCardsOperationsRegistry().getItems().size()  == 0){
            return null;
        }


        ResCardsOperationsRegistry resCardsOperationsRegistry = new ResCardsOperationsRegistry();
        ResCardsOperationsRegistryItem item;
        for (CardsOperationsRegistryItem o : request.getCardsOperationsRegistry().getItems()) {
            try{
                item = handle(o, null == o.getOrgOwner() ? idOfOrg : o.getOrgOwner());
            }catch (Exception e){
                logger.error("CardsOperationsRegistry.handler org:"+idOfOrg + " , idOfOperation:" + o.getIdOfOperation()
                        + " , orgOwner:" + o.getOrgOwner() + " , error:" + e.getMessage(), e);
                item = new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR, ResCardsOperationsRegistryItem.ERROR_MESSAGE);
            }
            resCardsOperationsRegistry.getItemList().add(item);
        }

        return resCardsOperationsRegistry;
    }

    private ResCardsOperationsRegistryItem handle(CardsOperationsRegistryItem o,long idOfOrg) {
        CardService cardService = CardService.getInstance();
        ResCardsOperationsRegistryItem registryItem;
        /*Card byCardNo = CardReadOnlyRepository.getInstance().findByCardNo(o.getCardNo());
        if (byCardNo != null && byCardNo.getState() == CardState.BLOCKED.getValue()){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE );
        }*/
        Boolean isOldArm = Boolean.FALSE;
        RuntimeContext runtimeContext;
        Session session = null;
        Transaction transaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();
            if (VersionUtils.compareClientVersionForRegisterCard(session, idOfOrg) < 0) {
                isOldArm = Boolean.TRUE;
            }
            CardWritableRepository.getInstance().updateCardSync(idOfOrg, DAOUtils.findCardByCardNo(session, o.getCardNo()) , 0L);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error while handling card operation", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        switch (o.getType()){
            case 0:
                registryItem = cardService.registerNew(o, idOfOrg);
                break;
            case 1:
                registryItem = cardService.issueToClient(o, idOfOrg);
                break;
            case 2:
                registryItem = cardService.issueToClientTemp(o, idOfOrg);
                break;
            case 3:
                registryItem = cardService.issueToVisitor(o, idOfOrg);
                break;
            case 4:
                registryItem = cardService.reset(o, idOfOrg, isOldArm);
                break;
            case 5:
                registryItem = cardService.tempblock(o, idOfOrg, isOldArm);
                break;
            case 6:
                registryItem = cardService.block(o, idOfOrg, isOldArm);
                break;
            case 7:
                registryItem = cardService.unblock(o, idOfOrg, isOldArm);
                break;
            default:
                registryItem = null;
        }
        return registryItem;
    }
}
