package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.01.14
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventsBuilder implements SectionRequestBuilder {
    private final LoadContext loadContext;

    public EnterEventsBuilder(LoadContext loadContext) {

        this.loadContext = loadContext;
    }

    public EnterEvents build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest!=null?(EnterEvents)sectionRequest:null;
    }

    public EnterEvents build(Node enterEventsNode, LoadContext loadContext) throws Exception {
        return EnterEvents.build(enterEventsNode, loadContext);
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = findFirstChildElement(envelopeNode, EnterEvents.SECTION_NAME);
        if (sectionElement != null) {
            return build(sectionElement,loadContext);
        } else
            return null;
    }
}
