/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.03.16
 * Time: 16:19
 */

public class InteractiveReportDataRequest {
    private final Long maxVersion;

    public InteractiveReportDataRequest(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    static InteractiveReportDataRequest build(Node node) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(node, "V");
        return new InteractiveReportDataRequest(maxVersion);
    }
}
