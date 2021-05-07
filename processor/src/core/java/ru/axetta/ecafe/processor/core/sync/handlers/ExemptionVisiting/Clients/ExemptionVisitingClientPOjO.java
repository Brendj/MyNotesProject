/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.Clients;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ResSyncSettingsItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ExemptionVisitingClientPOjO {
    private Long idOfClient;
    private List<ExemptionVisitingClientDates> itemList = new LinkedList<>();


    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idOfClient", idOfClient.toString());
        for(ExemptionVisitingClientDates item : itemList){
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public List<ExemptionVisitingClientDates> getItemList() {
        if (itemList == null)
            return  new LinkedList<>();
        return itemList;
    }

    public void setItemList(List<ExemptionVisitingClientDates> itemList) {
        this.itemList = itemList;
    }
}
