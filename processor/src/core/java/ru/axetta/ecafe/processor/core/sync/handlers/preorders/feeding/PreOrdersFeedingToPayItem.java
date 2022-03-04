package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

public class PreOrdersFeedingToPayItem {
    private String guid;
    private Integer toPay;

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
}
