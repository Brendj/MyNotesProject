/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 15:52
 */

public class MigrantsData implements AbstractToElement {
    private List<ResIncomeMigrationRequestsItem> incomeMigrationRequestsItems;
    private List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems;
    private List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems;
    private List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems;

    @Override
    public Element toElement(Document document) throws Exception {
        Element migrantsData = document.createElement("Migrants");

        Element inMigReq = document.createElement("IncomeMigrationRequests");
        for (ResIncomeMigrationRequestsItem item : this.getIncomeMigrationRequestsItems()) {
            inMigReq.appendChild(item.toElement(document, "IMR"));
        }
        migrantsData.appendChild(inMigReq);

        Element inMigReqHis = document.createElement("IncomeMigrationRequestsHistory");
        for (ResIncomeMigrationRequestsHistoryItem item : this.getIncomeMigrationRequestsHistoryItems()) {
            inMigReqHis.appendChild(item.toElement(document, "IMRH"));
        }
        migrantsData.appendChild(inMigReqHis);

        Element outMigReq = document.createElement("OutcomeMigrationRequests");
        for (ResOutcomeMigrationRequestsItem item : this.getOutcomeMigrationRequestsItems()) {
            outMigReq.appendChild(item.toElement(document, "OMR"));
        }
        migrantsData.appendChild(outMigReq);

        Element outMigReqHis = document.createElement("OutcomeMigrationRequestsHistory");
        for (ResOutcomeMigrationRequestsHistoryItem item : this.getOutcomeMigrationRequestsHistoryItems()) {
            outMigReqHis.appendChild(item.toElement(document, "OMRH"));
        }
        migrantsData.appendChild(outMigReqHis);

        return migrantsData;
    }

    public MigrantsData() {
    }

    public List<ResIncomeMigrationRequestsItem> getIncomeMigrationRequestsItems() {
        return incomeMigrationRequestsItems;
    }

    public void setIncomeMigrationRequestsItems(List<ResIncomeMigrationRequestsItem> incomeMigrationRequestsItems) {
        this.incomeMigrationRequestsItems = incomeMigrationRequestsItems;
    }

    public List<ResIncomeMigrationRequestsHistoryItem> getIncomeMigrationRequestsHistoryItems() {
        return incomeMigrationRequestsHistoryItems;
    }

    public void setIncomeMigrationRequestsHistoryItems(
            List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems) {
        this.incomeMigrationRequestsHistoryItems = incomeMigrationRequestsHistoryItems;
    }

    public List<ResOutcomeMigrationRequestsItem> getOutcomeMigrationRequestsItems() {
        return outcomeMigrationRequestsItems;
    }

    public void setOutcomeMigrationRequestsItems(List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems) {
        this.outcomeMigrationRequestsItems = outcomeMigrationRequestsItems;
    }

    public List<ResOutcomeMigrationRequestsHistoryItem> getOutcomeMigrationRequestsHistoryItems() {
        return outcomeMigrationRequestsHistoryItems;
    }

    public void setOutcomeMigrationRequestsHistoryItems(
            List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems) {
        this.outcomeMigrationRequestsHistoryItems = outcomeMigrationRequestsHistoryItems;
    }
}
