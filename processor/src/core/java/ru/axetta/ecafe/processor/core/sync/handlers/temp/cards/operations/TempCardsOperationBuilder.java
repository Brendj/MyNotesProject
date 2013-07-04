package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class TempCardsOperationBuilder {

    public TempCardsOperations build(Node enterEventsNode, Long idOfOrg) throws Exception {
        List<TempCardOperation> tempCardOperations = new ArrayList<TempCardOperation>();
        Node itemNode = enterEventsNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("TCO")) {
                TempCardOperation tempCardOperation = TempCardOperation.build(itemNode, idOfOrg);
                tempCardOperations.add(tempCardOperation);
            }
            itemNode = itemNode.getNextSibling();
        }
        return new TempCardsOperations(tempCardOperations);
    }
}
