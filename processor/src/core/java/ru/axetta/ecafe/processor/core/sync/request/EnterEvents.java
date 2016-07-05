package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.01.14
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
public class EnterEvents implements SectionRequest {
    public static final String SECTION_NAME="EnterEvents";

    private final List<EnterEventItem> events;

    static EnterEvents build(Node enterEventsNode, LoadContext loadContext) throws Exception {
        List<EnterEventItem> enterEventList = new ArrayList<EnterEventItem>();
        Node itemNode = enterEventsNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("EE")) {
                EnterEventItem item = EnterEventItem.build(itemNode, loadContext);
                enterEventList.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
        return new EnterEvents(enterEventList);
    }

    EnterEvents(List<EnterEventItem> events) {
        this.events = events;
    }

    public List<EnterEventItem> getEvents() {
        return events;
    }

    @Override
    public String getRequestSectionName() {
         return SECTION_NAME;
    }
}
