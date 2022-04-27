package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

public class PreOrdersFeedingToPayDetailItem {
    private Long idOfDish;
    private Integer toPay;
    private Integer qty;

    public PreOrdersFeedingToPayDetailItem(Long idOfDish, Integer toPay, Integer qty) {
        this.idOfDish = idOfDish;
        this.toPay = toPay;
        this.qty = qty;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Integer getToPay() {
        return toPay;
    }

    public void setToPay(Integer toPay) {
        this.toPay = toPay;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
