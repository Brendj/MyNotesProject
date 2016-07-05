/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.cards;

import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;

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
public class CardsOperationsRegistry implements SectionRequest
{
    public static final String SECTION_NAME = "CardsOperationsRegistry";
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

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }


    public static class Builder implements SectionRequestBuilder{
        private final LoadContext loadContext;

        public Builder(LoadContext loadContext){

            this.loadContext = loadContext;
        }

        public CardsOperationsRegistry build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (CardsOperationsRegistry) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = findFirstChildElement(envelopeNode, CardsOperationsRegistry.SECTION_NAME);
            if (sectionElement != null) {
                try {
                    return CardsOperationsRegistry.build(sectionElement, loadContext);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            return null;
        }
    }


}
