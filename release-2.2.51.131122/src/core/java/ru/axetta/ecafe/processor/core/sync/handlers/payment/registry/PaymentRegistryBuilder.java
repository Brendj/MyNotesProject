package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.sync.SyncRequest;

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
public class PaymentRegistryBuilder {

    public PaymentRegistry build(Node paymentRegistryNode, SyncRequest.LoadContext loadContext) throws Exception {
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
