package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import java.util.ArrayList;
import java.util.List;

public class PreOrdersFeedingToPayItem {
    private String guid;
    private Integer toPay;
    private List<PreOrdersFeedingToPayDetailItem> details;

    public String getGuid() {
        return guid;
    }

    public PreOrdersFeedingToPayItem(String guid, Integer toPay) {
        this.guid = guid;
        this.toPay = toPay;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getToPay() {
        return toPay;
    }

    public void setToPay(Integer toPay) {
        this.toPay = toPay;
    }

    public List<PreOrdersFeedingToPayDetailItem> getDetails() {
        if (details == null) {
            details = new ArrayList<>();
        }
        return details;
    }
}
