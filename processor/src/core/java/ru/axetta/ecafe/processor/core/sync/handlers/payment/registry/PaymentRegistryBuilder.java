package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class PaymentRegistryBuilder implements SectionRequestBuilder {
    private LoadContext loadContext;

    public PaymentRegistryBuilder(LoadContext loadContext){
        this.loadContext = loadContext;
    }

    public PaymentRegistry build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (PaymentRegistry) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, PaymentRegistry.SECTION_NAME);
        if (sectionElement != null) {
            return buildFromCorrectSection(sectionElement);
        }
        return null;
    }

    private PaymentRegistry buildFromCorrectSection(Node paymentRegistryNode) throws Exception {
        List<Payment> POSPayments = new LinkedList<Payment>();
        Node itemNode = paymentRegistryNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("PT")) {
                POSPayments.add(Payment.build(itemNode, loadContext));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new PaymentRegistry(POSPayments);
    }
}
