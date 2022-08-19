package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexesItem;

import java.util.Date;
import java.util.Objects;

public class ApplicationForFoodDiscount {
    private Long idOfAppDiscount;
    private ApplicationForFood applicationForFood;
    private Integer dtisznCode;
    private Boolean confirmed;
    private Date startDate;
    private Date endDate;

    public ApplicationForFoodDiscount() {

    }

    public ApplicationForFoodDiscount(Integer dtisznCode) {
        this.dtisznCode = dtisznCode;
        this.confirmed = false;
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

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
