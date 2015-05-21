/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.cards;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

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


}
