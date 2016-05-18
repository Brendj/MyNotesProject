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
 * Time: 13:57
 */

public class ResMigrants extends AbstractToElement {
    private List<ResOutcomeMigrationRequestsItem> resOutcomeMigrationRequestsItems;
    private List<ResOutcomeMigrationRequestsHistoryItem> resOutcomeMigrationRequestsHistoryItems;
    private List<ResIncomeMigrationRequestsHistoryItem> resIncomeMigrationRequestsHistoryItems;

    @Override
    public Element toElement(Document document) throws Exception {
        Element resMigrants = document.createElement("ResMigrants");

        Element resOutMigReq = document.createElement("ResOutcomeMigrationRequest");
        for (ResOutcomeMigrationRequestsItem item : this.getResOutcomeMigrationRequestsItems()) {
            resOutMigReq.appendChild(item.toElement(document, "ROMR"));
        }
        resMigrants.appendChild(resOutMigReq);

        Element resOutMigReqHis = document.createElement("ResOutcomeMigrationRequestsHistory");
        for (ResOutcomeMigrationRequestsHistoryItem item : this.getResOutcomeMigrationRequestsHistoryItems()) {
            resOutMigReqHis.appendChild(item.toElement(document, "ROMRH"));
        }
        resMigrants.appendChild(resOutMigReqHis);

        Element resInMigReqHis = document.createElement("ResIncomeMigrationRequestsHistory");
        for (ResIncomeMigrationRequestsHistoryItem item : this.getResIncomeMigrationRequestsHistoryItems()) {
            resInMigReqHis.appendChild(item.toElement(document, "RIMRH"));
        }
        resMigrants.appendChild(resInMigReqHis);

        return resMigrants;
    }

    public ResMigrants() {
    }

    public List<ResOutcomeMigrationRequestsItem> getResOutcomeMigrationRequestsItems() {
        return resOutcomeMigrationRequestsItems;
    }

    public void setResOutcomeMigrationRequestsItems(
            List<ResOutcomeMigrationRequestsItem> resOutcomeMigrationRequestsItems) {
        this.resOutcomeMigrationRequestsItems = resOutcomeMigrationRequestsItems;
    }

    public List<ResOutcomeMigrationRequestsHistoryItem> getResOutcomeMigrationRequestsHistoryItems() {
        return resOutcomeMigrationRequestsHistoryItems;
    }

    public void setResOutcomeMigrationRequestsHistoryItems(
            List<ResOutcomeMigrationRequestsHistoryItem> resOutcomeMigrationRequestsHistoryItems) {
        this.resOutcomeMigrationRequestsHistoryItems = resOutcomeMigrationRequestsHistoryItems;
    }

    public List<ResIncomeMigrationRequestsHistoryItem> getResIncomeMigrationRequestsHistoryItems() {
        return resIncomeMigrationRequestsHistoryItems;
    }

    public void setResIncomeMigrationRequestsHistoryItems(
            List<ResIncomeMigrationRequestsHistoryItem> resIncomeMigrationRequestsHistoryItems) {
        this.resIncomeMigrationRequestsHistoryItems = resIncomeMigrationRequestsHistoryItems;
    }
}
