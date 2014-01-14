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
public class ClientGuardianResponse extends AbstractToElement {

    private final List<ClientGuardianResponseElement> clientGuardianResponseItems;

    public ClientGuardianResponse() {
        this.clientGuardianResponseItems = new LinkedList<ClientGuardianResponseElement>();
    }

    public void addItem(ClientGuardian clientGuardian, Integer resCode, String resultMessage) {
        clientGuardianResponseItems.add(new ClientGuardianResponseElement(clientGuardian, resCode,resultMessage));
    }

    public void addItem(ClientGuardianResponseElement item, Integer resCode, String resultMessage) {
        item.setResult(new ResultOperation(resCode, resultMessage));
        clientGuardianResponseItems.add(item);
    }

    public void addItem(ClientGuardian clientGuardian) {
        final ClientGuardianResponseElement e = new ClientGuardianResponseElement(clientGuardian, 0, null);
        clientGuardianResponseItems.add(e);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResClientGuardian");
        for (ClientGuardianResponseElement item : this.clientGuardianResponseItems) {
            element.appendChild(item.toElement(document, "RCG"));
        }
        return element;
    }

}
