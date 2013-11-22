package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

public enum GoodComplaintIterationStatus {

    creation(0, "Создание"),
    consideration(1, "Рассмотрение"),
    investigation(2, "Расследование"),
    conclusion(3, "Заключение");

    private Integer statusNumber;
    private String title;

    GoodComplaintIterationStatus () {

    }

    GoodComplaintIterationStatus(Integer statusNumber, String title) {
        this.statusNumber = statusNumber;
        this.title = title;
    }

    public Integer getStatusNumber() {
        return statusNumber;
    }

    public String getTitle() {
        return title;
    }

    protected void setStatusNumber(Integer statusNumber) {
        // for Hibernate only
    }

    protected void setTitle(String title) {
        // for Hibernate only
    }

    public static GoodComplaintIterationStatus getStatusByNumberNullSafe(Integer statusNumber) {
        if (statusNumber == null) {
            return null;
        }
        for (GoodComplaintIterationStatus status : GoodComplaintIterationStatus.values()) {
            if (statusNumber.equals(status.getStatusNumber())) {
                return status;
            }
        }
        return null;
    }

}
