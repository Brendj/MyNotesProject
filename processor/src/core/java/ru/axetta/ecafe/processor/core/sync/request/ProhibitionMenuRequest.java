/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.06.14
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class ProhibitionMenuRequest implements SectionRequest{
    public static final String SECTION_NAME="ProhibitionsMenuRequest";
    private final Long maxVersion;

    private ProhibitionMenuRequest(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    static ProhibitionMenuRequest build(Node clientGuardianRequestNode) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(clientGuardianRequestNode, "V");
        return new ProhibitionMenuRequest(maxVersion);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
