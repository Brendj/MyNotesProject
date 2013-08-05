package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import ru.axetta.ecafe.processor.core.persistence.CardTempOperation;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardOperation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 01.08.13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class TempCardOperationData {

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
