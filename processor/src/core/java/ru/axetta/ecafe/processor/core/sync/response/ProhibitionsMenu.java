/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ProhibitionMenu;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.06.14
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class ProhibitionsMenu implements AbstractToElement{

    private Map<Long, ProhibitionMenuItem> prohibitionItemMap = new HashMap<Long, ProhibitionMenuItem>();
    private final long resultCode;
    private final String resultDescription;

    public ProhibitionsMenu() {
        resultCode = 0;
        resultDescription = "OK";
    }

    public ProhibitionsMenu(long resultCode, String resultDescription) {
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
    }


    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ProhibitionsMenu");
        element.setAttribute("Code", Long.toString(resultCode));
        element.setAttribute("Descr", resultDescription);
        for (ProhibitionMenuItem item : this.prohibitionItemMap.values()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public void addProhibitionMenuInfo(ProhibitionMenu prohibitionMenu){
        final Client client = prohibitionMenu.getClient();
        final Long idOfClient = client.getIdOfClient();
        ProhibitionMenuItem prohibitionMenuItem = prohibitionItemMap.get(idOfClient);
        if(prohibitionMenuItem==null){
            prohibitionMenuItem = new ProhibitionMenuItem(idOfClient);
            prohibitionItemMap.put(idOfClient, prohibitionMenuItem);
        }
        ProhibitionMenuDetail detail = new ProhibitionMenuDetail(prohibitionMenu);
        prohibitionMenuItem.addDetail(detail);
    }

    private static class ProhibitionMenuItem {
        private final long idOfClient; //Идентификатор клиента
        // список запретов клиента
        private List<ProhibitionMenuDetail> prohibitionMenuDetails = new LinkedList<ProhibitionMenuDetail>();

        private ProhibitionMenuItem(long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public void addDetail(ProhibitionMenuDetail detail){
            prohibitionMenuDetails.add(detail);
        }

        public Element toElement(Document document) throws Exception{
            Element element = document.createElement("PM");
            element.setAttribute("IdOfClient", Long.toString(idOfClient));
            for (ProhibitionMenuDetail detail : this.prohibitionMenuDetails) {
                element.appendChild(detail.toElement(document));
            }
            return element;
        }

    }

    private static class ProhibitionMenuDetail {
        private final long idOfProhibition;
        private final long version;
        private final boolean deleteSate;
        private final String text;
        private final int type;

        private ProhibitionMenuDetail(ProhibitionMenu prohibitionMenu) {
            this.idOfProhibition = prohibitionMenu.getIdOfProhibitions();
            this.version = prohibitionMenu.getVersion();
            this.deleteSate = prohibitionMenu.getDeletedState();
            this.text = prohibitionMenu.getFilterText();
            this.type = prohibitionMenu.getProhibitionFilterType().ordinal();
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("PMI");
            element.setAttribute("Id", Long.toString(idOfProhibition));
            element.setAttribute("V", Long.toString(version));
            if(deleteSate) element.setAttribute("D", "1");
            element.setAttribute("Text", text);
            element.setAttribute("Type", Integer.toString(type));
            return element;
        }

    }

}
