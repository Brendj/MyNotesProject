package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class ClientRequestBuilder implements SectionRequestBuilder{

    public ClientRequests build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest!=null?(ClientRequests)sectionRequest:null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ClientRequests.SECTION_NAME);
        if (sectionElement != null) {
            return new ClientRequests(sectionElement);
        } else
            return null;
    }
}
