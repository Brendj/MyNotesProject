package ru.axetta.ecafe.processor.core.sync.request;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.10.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class AccRegistryUpdateRequestBuilder {

    public AccRegistryUpdateRequest build(Node accRegistryUpdateRequest) throws Exception {
        return AccRegistryUpdateRequest.build(accRegistryUpdateRequest);
    }

}
