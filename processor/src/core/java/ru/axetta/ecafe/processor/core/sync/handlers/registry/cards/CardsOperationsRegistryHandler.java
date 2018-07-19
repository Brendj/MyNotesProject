/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.cards;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;

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
                registryItem = cardService.reset(o, idOfOrg);
                break;
            case 5:
                registryItem = cardService.tempblock(o, idOfOrg);
                break;
            case 6:
                registryItem = cardService.block(o, idOfOrg);
                break;
            case 7:
                registryItem = cardService.unblock(o, idOfOrg);
                break;
            default:
                registryItem = null;
        }
        return registryItem;
    }
}
