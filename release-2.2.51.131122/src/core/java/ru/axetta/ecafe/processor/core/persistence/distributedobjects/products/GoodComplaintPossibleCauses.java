package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

public enum GoodComplaintPossibleCauses {

    badTaste(0, "Неприятный вкус"),
    badSmell(1, "Неприятный запах"),
    malaise(2, "Недомогание после употребления"),
    badQualityProducts(3, "Подозрение на некачественные продукты в составе блюда"),
    overdue(4, "Просроченность"),
    highPrice(5, "Завышенная цена");

    private Integer causeNumber;
    private String title;

    GoodComplaintPossibleCauses () {

    }

    GoodComplaintPossibleCauses(Integer causeNumber, String title) {
        this.causeNumber = causeNumber;
        this.title = title;
    }

    public Integer getCauseNumber() {
        return causeNumber;
    }

    public String getTitle() {
        return title;
    }

    protected void setCauseNumber(Integer causeNumber) {
        // for Hibernate only
    }

    protected void setTitle(String title) {
        // for Hibernate only
    }

    @Override
    public String toString() {
        return title;
    }

    public static GoodComplaintPossibleCauses getCauseByNumberNullSafe(Integer causeNumber) {
        if (causeNumber == null) {
            return null;
        }
        for (GoodComplaintPossibleCauses cause : GoodComplaintPossibleCauses.values()) {
            if (causeNumber.equals(cause.getCauseNumber())) {
                return cause;
            }
        }
        return null;
    }

}
