/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.cards;

import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * 3.11.	Реестр операций по постоянным картам
 *
 * User: shamil
 * Date: 30.04.15
 * Time: 10:55
 */
public class CardsOperationsRegistry {
    public static final String SYNC_NAME = "CardsOperationsRegistry";
    private static final Logger logger = LoggerFactory.getLogger(CardsOperationsRegistry.class);

    private List<CardsOperationsRegistryItem> items = new LinkedList<CardsOperationsRegistryItem>();

    public CardsOperationsRegistry(List<CardsOperationsRegistryItem> items) {
        this.items = items;
    }

    public static CardsOperationsRegistry build(Node cardsOperationsRegistryNode, LoadContext loadContext) throws Exception {
        List<CardsOperationsRegistryItem> operationItemList = new LinkedList<CardsOperationsRegistryItem>();
        Node itemNode = cardsOperationsRegistryNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals(CardsOperationsRegistryItem.SYNC_NAME)) {
                operationItemList.add(CardsOperationsRegistryItem.build(itemNode, loadContext));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new CardsOperationsRegistry(operationItemList);
    }

    public List<CardsOperationsRegistryItem> getItems() {
        return items;
    }

    public static CardsOperationsRegistry find(Node envelopeNode, LoadContext loadContext) {
        CardsOperationsRegistry cardsOperationsRegistry = null;
        Node CardsOperationsRegistryNode = findFirstChildElement(envelopeNode, SYNC_NAME);
        if (CardsOperationsRegistryNode != null) {
            try {
                cardsOperationsRegistry = build(CardsOperationsRegistryNode, loadContext);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return cardsOperationsRegistry;
    }

    public ResCardsOperationsRegistry handler(SyncRequest request, long idOfOrg) {

        if(request.getCardsOperationsRegistry()== null ||request.getCardsOperationsRegistry().getItems()== null ||request.getCardsOperationsRegistry().getItems().size()  == 0){
            return null;
        }


        ResCardsOperationsRegistry resCardsOperationsRegistry = new ResCardsOperationsRegistry();
        ResCardsOperationsRegistryItem item;
        for (CardsOperationsRegistryItem o : request.getCardsOperationsRegistry().getItems()) {
            try{
                item = handle(o, idOfOrg);
            }catch (Exception e){
                logger.error("CardsOperationsRegistry.handler org:"+idOfOrg + " , idOfOperation:" + o.getIdOfOperation()+ " , error:" + e.getMessage(), e);
                item = new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR, ResCardsOperationsRegistryItem.ERROR_MESSAGE);
            }
            resCardsOperationsRegistry.getItemList().add(item);
        }

        return resCardsOperationsRegistry;
    }

    private ResCardsOperationsRegistryItem handle(CardsOperationsRegistryItem o,long idOfOrg) {
        CardService cardService = CardService.getInstance();
        ResCardsOperationsRegistryItem registryItem;
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
                registryItem = cardService.block(o, idOfOrg);
                break;
            case 6:
                registryItem = cardService.blockAndReset(o, idOfOrg);
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
