package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

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
public class ClientGuardianData implements AbstractToElement {

    private final List<ClientGuardianItem> clientGuardianResponseItems;
    private final ResultOperation result;

    public ClientGuardianData(ResultOperation result) {
        this.clientGuardianResponseItems = new LinkedList<ClientGuardianItem>();
        this.result = result;
    }

    public void addItem(ClientGuardian clientGuardian) {
        final ClientGuardianItem e = new ClientGuardianItem(clientGuardian);
        if (!clientGuardianResponseItems.contains(e)) {
            clientGuardianResponseItems.add(e);
        }
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ClientsGuardians");
        if(this.result!=null){
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        for (ClientGuardianItem item : this.clientGuardianResponseItems) {
            element.appendChild(item.toElement(document, "CG"));
        }
        return element;
    }

}
