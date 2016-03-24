/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.03.16
 * Time: 11:24
 */

public class InteractiveReportDataBuilder {


    private Node mainNode;

    public void createMainNode(Node envelopeNode) {
        mainNode = findFirstChildElement(envelopeNode, "InteractiveReportData");
    }

    public InteractiveReportDataRequest build() throws Exception {
        if (mainNode != null) {
            return InteractiveReportDataRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public InteractiveReportDataRequest build(Node interactiveReportDataNode) throws Exception {
        return InteractiveReportDataRequest.build(interactiveReportDataNode);
    }

    public InteractiveReport buildInteractiveReport(Node interactiveReportDataNode) throws Exception {
        InteractiveReport result = new InteractiveReport(interactiveReportDataNode);
        return result;
    }
}
