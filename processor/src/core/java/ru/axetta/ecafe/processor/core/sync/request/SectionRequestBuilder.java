/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 21.06.2016
 */
public interface SectionRequestBuilder {
    SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception;
}
