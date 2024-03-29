package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class TempCardsOperationBuilder implements SectionRequestBuilder{

    private final long idOfOrg;

    public TempCardsOperationBuilder(long idOfOrg){
        this.idOfOrg = idOfOrg;
    }

    public TempCardsOperations build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (TempCardsOperations) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, TempCardsOperations.SECTION_NAME);
        if (sectionElement != null) {
            return buildFromCorrectSection(sectionElement, idOfOrg);
        } else
            return null;
    }

    private static TempCardsOperations buildFromCorrectSection(Node enterEventsNode, Long idOfOrg) throws Exception {
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
