package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class PaymentRegistry implements SectionRequest {
    public final static String SECTION_NAME="PaymentRegistry";

    private final List<Payment> POSPayments;

    public static PaymentRegistry build(Node paymentRegistryNode, LoadContext loadContext) throws Exception {
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

    PaymentRegistry(List<Payment> POSPayments) {
        this.POSPayments = POSPayments;
    }

    public Iterator<Payment> getPayments() {
        return POSPayments.iterator();
    }

    @Override
    public String toString() {
        return "PaymentRegistry{" + "payments=" + POSPayments + '}';
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
