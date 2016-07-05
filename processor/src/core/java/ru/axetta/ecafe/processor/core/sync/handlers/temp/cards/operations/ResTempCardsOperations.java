package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;


import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class ResTempCardsOperations implements AbstractToElement {

    private final List<ResTempCardOperation> resTempCardOperationList;

    public ResTempCardsOperations(List<ResTempCardOperation> resTempCardOperationList) {
        this.resTempCardOperationList = resTempCardOperationList;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResTempCardsOperations");
        for (ResTempCardOperation item : this.resTempCardOperationList) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }
}
