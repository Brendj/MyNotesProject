package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class ResultClientGuardian extends AbstractToElement {

    private final List<ClientGuardianItem> clientGuardianResponseItems;

    public ResultClientGuardian() {
        this.clientGuardianResponseItems = new LinkedList<ClientGuardianItem>();
    }

    public void addItem(ClientGuardian clientGuardian, Integer resCode, String resultMessage) {
        clientGuardianResponseItems.add(new ClientGuardianItem(clientGuardian, resCode,resultMessage));
    }

    public void addItem(ClientGuardianItem item, Integer resCode, String resultMessage) {
        item.setResult(new ResultOperation(resCode, resultMessage));
        clientGuardianResponseItems.add(item);
    }

    public void addItem(ClientGuardian clientGuardian) {
        final ClientGuardianItem e = new ClientGuardianItem(clientGuardian, 0, null);
        clientGuardianResponseItems.add(e);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResClientGuardian");
        for (ClientGuardianItem item : this.clientGuardianResponseItems) {
            element.appendChild(item.toElement(document, "RCG"));
        }
        return element;
    }

}
