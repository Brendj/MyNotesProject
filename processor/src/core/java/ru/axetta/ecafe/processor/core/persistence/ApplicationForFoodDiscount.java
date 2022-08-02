package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexesItem;

import java.util.Objects;

public class ApplicationForFoodDiscount {
    private Long idOfAppDiscount;
    private ApplicationForFood applicationForFood;
    private Integer dtisznCode;

    public ApplicationForFoodDiscount() {

    }

    public ApplicationForFoodDiscount(Integer dtisznCode) {
        this.dtisznCode = dtisznCode;
    }

    @Override
    public int hashCode() {
        return idOfAppDiscount.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationForFoodDiscount that = (ApplicationForFoodDiscount) o;
        return Objects.equals(idOfAppDiscount, that.idOfAppDiscount);
    }

    public Long getIdOfAppDiscount() {
        return idOfAppDiscount;
    }

    public void setIdOfAppDiscount(Long idOfAppDiscount) {
        this.idOfAppDiscount = idOfAppDiscount;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public Integer getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Integer dtisznCode) {
        this.dtisznCode = dtisznCode;
    }
}
