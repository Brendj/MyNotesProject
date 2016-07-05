package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.10.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class AccRegistryUpdateRequestBuilder implements SectionRequestBuilder{

    public AccRegistryUpdateRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (AccRegistryUpdateRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, AccRegistryUpdateRequest.SECTION_NAME);
        if (sectionElement!=null){
            return AccRegistryUpdateRequest.build(sectionElement);
        }
        else return null;
    }
}
