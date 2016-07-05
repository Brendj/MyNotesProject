package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 01.08.13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperationData implements AbstractToElement {

    private final List<TempCardOperationElement> tempCardOperationElementList;

    public TempCardOperationData(List<TempCardOperationElement> tempCardOperationElementList) {
        this.tempCardOperationElementList = tempCardOperationElementList;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("TempCardsOperations");
        for (TempCardOperationElement item : this.tempCardOperationElementList) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
