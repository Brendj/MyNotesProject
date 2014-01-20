package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.01.14
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventsBuilder {

    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "EnterEvents");
    }

    public EnterEvents build(LoadContext loadContext) throws Exception {
        if (mainNode != null){
            return build(mainNode, loadContext);
        } else {
            return null;
        }
    }

    public EnterEvents build(Node enterEventsNode, LoadContext loadContext) throws Exception {
        return EnterEvents.build(enterEventsNode, loadContext);
    }

}
