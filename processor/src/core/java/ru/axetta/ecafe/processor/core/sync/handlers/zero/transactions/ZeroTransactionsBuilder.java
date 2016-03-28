/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionsBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "ZeroTransactions");
    }

    public ZeroTransactions build(Node zeroTransactionsNode, Long orgOwner) throws Exception {
        ZeroTransactions result = new ZeroTransactions(zeroTransactionsNode, orgOwner);
        return result;
    }
}
