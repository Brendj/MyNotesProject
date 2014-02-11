/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.06.13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class DirectiveElement {

    private List<DirectiveItem> directiveItemList;

    public void process(Session session, Org org) throws Exception{

        directiveItemList = new ArrayList<DirectiveItem>();
        Boolean fullSync = org.getFullSyncParam();
        if(fullSync) {
            directiveItemList.add(new DirectiveItem("FullSync","1"));
            DAOUtils.falseFullSyncByOrg(session, org.getIdOfOrg());
        }
        Boolean commodityAccounting = org.getCommodityAccounting();
        directiveItemList.add(new DirectiveItem("CommodityAccounting",commodityAccounting?"1":"0"));
        Boolean usePlanOrders = org.getUsePlanOrders();
        directiveItemList.add(new DirectiveItem("UsePlanOrders",usePlanOrders?"1":"0"));
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Directives");
        for (DirectiveItem directiveItem : this.directiveItemList) {
            element.appendChild(directiveItem.toElement(document));
        }
        return element;
    }

}
